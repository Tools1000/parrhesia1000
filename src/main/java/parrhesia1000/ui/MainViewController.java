package parrhesia1000.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import fx1000.FXUtil;
import fx1000.UiUtil;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.*;
import parrhesia1000.config.AppConfig;
import parrhesia1000.nostr.event.handler.AuthorCacheHandler;
import parrhesia1000.nostr.event.handler.GenericFeedHandler;
import parrhesia1000.nostr.event.handler.PersonalFeedHandler;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;
import parrhesia1000.ui.control.AppStats;
import parrhesia1000.ui.control.FeedContentBox;
import tools1000.ToolsUtil;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Slf4j
@Component
@FxmlView("/fxml/MainView.fxml")
public class MainViewController extends DebuggableController implements Initializable, ConnectHandler {

    private final ApplicationContext applicationContext;

    private final ObjectMapper mapper;

    private final Executor executor;

    private final AppConfig appConfig;

    private final UiConfig uiConfig;

    private final AuthorCache authorCache;

    private final ParrhesiaClient client;

    private final SessionCallbackHandler sessionCallbackHandler;

    private final RequestSender requestSender;

    public VBox root;

    public TabPane tabPane;

    @FXML
    MenuBar menuBar;

   private final TimeRangeMap timeRangeMap;

    public MainViewController(AppConfig appConfig, ApplicationContext applicationContext, ObjectMapper mapper, Executor executor, AppConfig appConfig1, UiConfig uiConfig, AuthorCache authorCache, ParrhesiaClient client, SessionCallbackHandler sessionCallbackHandler, RequestSender requestSender, TimeRangeMap timeRangeMap) {
        super(appConfig);
        this.applicationContext = applicationContext;
        this.mapper = mapper;
        this.executor = executor;
        this.appConfig = appConfig1;
        this.uiConfig = uiConfig;
        this.authorCache = authorCache;
        this.client = client;
        this.sessionCallbackHandler = sessionCallbackHandler;
        this.requestSender = requestSender;
        this.timeRangeMap = timeRangeMap;
        sessionCallbackHandler.getEventHandlerList().add(new AuthorCacheHandler(mapper,appConfig, authorCache, applicationContext, executor));
    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[]{root, tabPane};
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        super.initialize(url, resourceBundle);

        FXUtil.initAppMenu(menuBar);

        tabPane.getTabs().add(buildGlobalFeedTab("Global Feed"));

        Tab tab = buildButton();
        tabPane.getTabs().add(tab);

        root.getStyleClass().add("nostr-background");

        tabPane.getStyleClass().add("floating");
        tabPane.getStyleClass().add("custom-tab-pane");



//        sessionCallbackHandler.getEventHandlerList().add(globalFeedHandler);

        sessionCallbackHandler.getConnectHandlerList().add(this);



        connect();

    }

    private void connect(){
        log.debug("Initialization done, connecting");
        client.connect(sessionCallbackHandler);
    }

    private Tab buildButton() {
        Tab addTab = new Tab("Add feed.."); // You can replace the text with an icon
        addTab.setClosable(false);
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == addTab) {
                tabPane.getTabs().add(tabPane.getTabs().size() - 1, buildNewTab("New Feed")); // Adding new tab before the "button" tab
                tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2); // Selecting the tab before the button, which is the newly created one
            }
        });
        return addTab;
    }

    private Tab buildGlobalFeedTab(String tabName) {
        return buildFeedTab(tabName, RequestFactory.GLOBAL_FEED, Collections.emptyList());
    }

    private Tab buildFeedTab(String tabName, String subscriptionId, List<String> authors) {
        Feed feed = new Feed();
        EventCache eventCache = new EventCache();
        Tab tab = new Tab(tabName);
        tab.setContent(buildGlobalFeedContent(subscriptionId, authors, feed, eventCache));
        sessionCallbackHandler.getEventHandlerList().add(new GenericFeedHandler(mapper, appConfig,subscriptionId, authorCache, requestSender, eventCache));
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                requestSender.sendRequest(client.getSession(), RequestFactory.stopRequestRequest(subscriptionId));
            }
        });
        return tab;
    }

    private Tab buildNewTab(String tabName) {
        Feed feed = new Feed();
        EventCache eventCache = new EventCache();
        Tab tab = new Tab(tabName);
        VBox content = new VBox(4);
        Label label = new Label("Enter pub key");
        TextField textField = new TextField();
        HBox hBox = new HBox(4);
        label.setAlignment(Pos.CENTER);
        textField.setAlignment(Pos.CENTER);
        Button addTabButton = new Button("Add");
        addTabButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String pubKey = textField.getText().trim();
                pubKey = StringUtils.abbreviateMiddle(pubKey, "..", 14);
                String subId = "feed-for-" + pubKey;
                log.debug("Adding tab for {}", pubKey);
                Node node = buildPersonalFeedContent(subId, List.of(Bech32ToHexString.decode(textField.getText().trim())), feed, eventCache);
                tab.setContent(node);
                tab.setText(pubKey);
                timeRangeMap.getMap().put(subId, new TimeRange(LocalDateTime.now(), LocalDateTime.now().minusHours(1)));
                sessionCallbackHandler.getEventHandlerList().add(new PersonalFeedHandler(mapper, appConfig,subId,authorCache, requestSender, eventCache));
//                TimeRange timeRange = timeRangeMap.getMap().getOrDefault(subId, new TimeRange());
//                if(client.getSession().isOpen()){
//                    requestSender.sendRequest(client.getSession(), RequestFactory.buildFeedRequest(subId ,timeRange).addAuthorsFilter(List.of(Bech32ToHexString.decode(textField.getText().trim()))).addKindsFilter(List.of(1)));
//                } else {
//                    log.debug("Not connected");
//                }

            }
        });
        addTabButton.disableProperty().bind(textField.textProperty().isEmpty());
        hBox.getChildren().addAll(UiUtil.applyDebug(label, appConfig.isDebug()), UiUtil.applyDebug(textField, appConfig.isDebug()), UiUtil.applyDebug(addTabButton, appConfig.isDebug()));
        hBox.setAlignment(Pos.CENTER);
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(UiUtil.applyDebug(hBox, appConfig.isDebug()));
        tab.setContent(UiUtil.applyDebug(content, appConfig.isDebug()));
        return tab;
    }





    private Node buildPersonalFeedContent(String subscriptionId, List<String> authors, Feed feed, EventCache eventCache) {
        return buildFeedContent(subscriptionId, authors, feed, eventCache);
    }

    private Node buildGlobalFeedContent(String subscriptionId, List<String> authors, Feed feed, EventCache eventCache) {
        return buildFeedContent(subscriptionId, authors,  feed, eventCache);
    }

    public void handleLoadOlderPosts(String subscriptionId, List<String> authors) {
        boolean shortDuration = authors == null || authors.isEmpty();
        Duration duration;
        if(shortDuration){
            duration = Duration.of(19, ChronoUnit.SECONDS);
        } else {
            duration = Duration.of(1, ChronoUnit.DAYS);
        }
        TimeRange timeRange = timeRangeMap.getMap().getOrDefault(subscriptionId, new TimeRange(duration));
        requestSender.sendRequest(client.getSession(), RequestFactory.buildFeedRequest(subscriptionId, timeRange).addKindsFilter(List.of(1)).addAuthorsFilter(authors));
        TimeRange timeRangeNew = timeRange.updateSince(Duration.of(1, ChronoUnit.DAYS));
        timeRangeMap.getMap().put(subscriptionId, timeRangeNew);
        log.debug("Loading older posts, old time range: {}, new time range {}", timeRange, timeRangeNew);
    }

    public void loadMoreFeedPosts(Feed feed, EventCache eventCache) {
        List<FeedContentElement> list = ToolsUtil.getSubList(eventCache.getElements(), 20);
        log.debug("Adding {} new posts", list.size());
        for (FeedContentElement e : list) {
            feed.addElement(new FeedContentBox(executor, appConfig, uiConfig, applicationContext.getBean(HostServices.class), e, authorCache));
        }
        eventCache.elementsProperty().removeAll(list);
        log.debug("Feed size now {}", feed.getFeedContent().size());
    }

    private Node buildFeedContent(String subscriptionId, List<String> authors, Feed feed, EventCache eventCache) {
        Pane stackPane = new StackPane();
        stackPane.getStyleClass().add("feed-content-pane");
        ListView<FeedContentBox> listView = configureNewFeedListView(buildNewFeedListView());
        listView.getStyleClass().add("list-view");
//        listView.setPadding(new Insets(20,0,20,0));
        Bindings.bindContent(listView.getItems(), feed.getFeedContent());
        Button loadNewerButton = new Button("Load newer");
        Button loadOlderButton = new Button("Load older");
        Button loadMoreButton = new Button("Load more");
        Button loadMoreButton2 = new Button("Load more");
        eventCache.elementsProperty().sizeProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(appConfig.isDebug()) {
                    loadMoreButton.setText("Load more (" + newValue + ")");
                    loadMoreButton2.setText("Load more (" + newValue + ")");
                }
            }
        });
        loadMoreButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadMoreFeedPosts(feed, eventCache);
            }
        });

        loadMoreButton2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                loadMoreFeedPosts(feed, eventCache);
            }
        });

        loadOlderButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleLoadOlderPosts(subscriptionId, authors);
            }
        });


//        stackPane.getChildren().add(UiUtil.applyDebug(buildHorizontalButtonsBox(loadNewerButton, loadMoreButton, eventCache), appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(listView, appConfig.isDebug()));
//        stackPane.getChildren().add(UiUtil.applyDebug(buildHorizontalButtonsBox(loadOlderButton, loadMoreButton2, eventCache), appConfig.isDebug()));

        stackPane.getChildren().add(UiUtil.applyDebug(loadNewerButton, appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(loadMoreButton, appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(loadOlderButton, appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(loadMoreButton2, appConfig.isDebug()));

        VBox.setVgrow(listView, Priority.ALWAYS);
        loadNewerButton.setAlignment(Pos.CENTER);
        loadOlderButton.setAlignment(Pos.CENTER);
        loadMoreButton.setAlignment(Pos.CENTER);
        loadMoreButton2.setAlignment(Pos.CENTER);

        StackPane.setAlignment(loadNewerButton, Pos.TOP_CENTER);
        StackPane.setAlignment(loadMoreButton, Pos.TOP_CENTER);
        StackPane.setAlignment(loadOlderButton, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(loadMoreButton2, Pos.BOTTOM_CENTER);

        loadMoreButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty().not());
        loadMoreButton.managedProperty().bind(loadMoreButton.visibleProperty());
        loadNewerButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty());
        loadNewerButton.managedProperty().bind(loadNewerButton.visibleProperty());

        loadMoreButton2.visibleProperty().bind(eventCache.elementsProperty().emptyProperty().not());
        loadMoreButton2.managedProperty().bind(loadMoreButton2.visibleProperty());
        loadOlderButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty());
        loadOlderButton.managedProperty().bind(loadOlderButton.visibleProperty());

        return stackPane;
    }

    private HBox buildHorizontalButtonsBox(Button loadNewerButton, Button loadMoreButton, EventCache eventCache) {
        HBox box = horizontalSpacerHox(List.of(loadNewerButton, loadMoreButton));
        loadMoreButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty().not());
        loadMoreButton.managedProperty().bind(loadMoreButton.visibleProperty());
        loadNewerButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty());
        loadNewerButton.managedProperty().bind(loadNewerButton.visibleProperty());
        return box;
    }

    private HBox horizontalSpacerHox(List<Node> nodes) {
        HBox topButtonsBox = new HBox();
        Pane spacer1 = new Pane();
        Pane spacer2 = new Pane();
        topButtonsBox.getChildren().add(UiUtil.applyDebug(spacer1, appConfig.isDebug()));
        for(Node node : nodes){
            topButtonsBox.getChildren().add(UiUtil.applyDebug(node, appConfig.isDebug()));
        }
        topButtonsBox.getChildren().add(UiUtil.applyDebug(spacer2, appConfig.isDebug()));
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        return topButtonsBox;
    }

    private ListView<FeedContentBox> configureNewFeedListView(ListView<FeedContentBox> listView){
        listView.getStyleClass().add("transparent-background");
        listView.prefWidthProperty().bind(tabPane.prefWidthProperty());
        listView.prefHeightProperty().bind(tabPane.prefHeightProperty());
        listView.setCellFactory(new Callback<ListView<FeedContentBox>, ListCell<FeedContentBox>>() {
            @Override
            public ListCell<FeedContentBox> call(ListView<FeedContentBox> param) {
                ListCell<FeedContentBox> lc = new ListCell<>() {

                    @Override
                    protected void updateItem(FeedContentBox item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(item);
                            item.prefWidthProperty().bind(listView.widthProperty().subtract(40));
                        }
                    }
                };
                return lc;
            }
        });
        return listView;
    }

    private ListView<FeedContentBox> buildNewFeedListView() {
        return new ListView<>();
    }

    @Override
    public void connected(WebSocketSession session) {
        log.debug("Connected");

    }

    @Override
    public void disconnected(WebSocketSession session) {
        log.debug("Disconnected");
    }
}
