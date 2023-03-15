package parrhesia1000.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import net.rgielen.fxweaver.core.FxmlView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import parrhesia1000.*;
import parrhesia1000.config.AppConfig;
import fx1000.FXUtil;
import parrhesia1000.nostr.event.handler.AuthorCacheHandler;
import parrhesia1000.nostr.event.handler.GlobalFeedHandler;
import parrhesia1000.nostr.event.handler.PersonalFeedHandler;
import parrhesia1000.request.RequestFactory;
import parrhesia1000.request.RequestSender;
import parrhesia1000.ui.control.AppStats;
import parrhesia1000.ui.control.FeedContentBox;
import tools1000.ToolsUtil;

import java.net.URL;
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

    @FXML
    public Tab globalFeedTab;

    @FXML
    public Button loadNewButton;

    public TabPane tabPane;

    @FXML
    public Pane stack;

    @FXML
    public AppStats appStats;

    @FXML
    public Pane stack1;

    @FXML
    MenuBar menuBar;

    @FXML
    ListView<FeedContentBox> listView;

    public MainViewController(AppConfig appConfig, ApplicationContext applicationContext, ObjectMapper mapper, Executor executor, AppConfig appConfig1, UiConfig uiConfig, AuthorCache authorCache, ParrhesiaClient client, SessionCallbackHandler sessionCallbackHandler, RequestSender requestSender) {
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
        sessionCallbackHandler.getEventHandlerList().add(new AuthorCacheHandler(mapper,appConfig, authorCache, applicationContext, executor));
    }

    @Override
    protected Parent[] getUiElementsForRandomColor() {
        return new Parent[0];
    }

    public void handleMenuItemSettings(ActionEvent actionEvent) {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        FXUtil.initAppMenu(menuBar);
        applyRandomColors(root, tabPane);

        tabPane.getTabs().add(buildGlobalFeedTab("Global Feed"));

        tabPane.getTabs().add(buildButton());



//        sessionCallbackHandler.getEventHandlerList().add(globalFeedHandler);

        sessionCallbackHandler.getConnectHandlerList().add(this);



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
        Feed feed = new Feed();
        EventCache eventCache = new EventCache();
        Tab tab = new Tab(tabName);
        tab.setContent(buildGlobalFeedContent(feed, eventCache));
        sessionCallbackHandler.getEventHandlerList().add(new GlobalFeedHandler(mapper, appConfig,applicationContext, eventCache, authorCache, feed, requestSender));
        tab.setOnClosed(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                requestSender.sendRequest(client.getSession(), RequestFactory.stopRequestRequest(RequestFactory.GLOBAL_FEED));
            }
        });
        return tab;
    }

    private Tab buildNewTab(String tabName) {
        Feed personalFeed = new Feed();
        EventCache personalEventCache = new EventCache();
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
                log.debug("Adding tab for {}", pubKey);
                tab.setContent(buildPersonalFeedContent(personalFeed, personalEventCache));
                tab.setText(pubKey);
                String subId = "feed-for-" + pubKey;
                sessionCallbackHandler.getEventHandlerList().add(new PersonalFeedHandler(mapper, appConfig,subId,authorCache, requestSender, personalEventCache));
                requestSender.sendRequest(client.getSession(), RequestFactory.buildPersonalFeedRequest(subId, List.of(Bech32ToHexString.decode(textField.getText().trim())),appConfig.getFeed().getPersonal().getLookBehindSeconds()));

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

    private Node buildGlobalFeedContent(Feed feed, EventCache eventCache) {
        StackPane stackPane = new StackPane();
        ListView<FeedContentBox> listView = configureNewFeedListView(buildNewFeedListView());
        Bindings.bindContent(listView.getItems(), feed.getFeedContent());
        Button loadButton = new Button("Load more");
        eventCache.elementsProperty().sizeProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(appConfig.isDebug())
                    loadButton.setText("Load more (" + newValue + ")");
            }
        });
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleLoadNewFeedPosts(feed, eventCache);
            }
        });
        loadButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty().not());
        stackPane.getChildren().add(UiUtil.applyDebug(listView, appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(loadButton, true));
        StackPane.setAlignment(loadButton, Pos.TOP_CENTER);
        return stackPane;
    }

    private Node buildPersonalFeedContent(Feed feed, EventCache eventCache) {
        StackPane stackPane = new StackPane();
        ListView<FeedContentBox> listView = configureNewFeedListView(buildNewFeedListView());
        Bindings.bindContent(listView.getItems(), feed.getFeedContent());
        Button loadButton = new Button("Load more");
        eventCache.elementsProperty().sizeProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(appConfig.isDebug())
                    loadButton.setText("Load more (" + newValue + ")");
            }
        });
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleLoadNewFeedPosts(feed, eventCache);
            }
        });
        loadButton.visibleProperty().bind(eventCache.elementsProperty().emptyProperty().not());
        stackPane.getChildren().add(UiUtil.applyDebug(listView, appConfig.isDebug()));
        stackPane.getChildren().add(UiUtil.applyDebug(loadButton, true));
        StackPane.setAlignment(loadButton, Pos.TOP_CENTER);
        return stackPane;
    }

    private ListView<FeedContentBox> buildNewFeedListView() {
        return new ListView<>();
    }

    private ListView<FeedContentBox> configureNewFeedListView(ListView<FeedContentBox> listView){
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
                            item.prefWidthProperty().bind(listView.widthProperty().subtract(50));
                        }
                    }
                };
                return lc;
            }
        });
        return listView;
    }

    public void handleLoadNewFeedPosts(Feed feed, EventCache eventCache) {
        List<FeedContentElement> list = ToolsUtil.getSubList(eventCache.getElements(), 20);
        log.debug("Adding {} new posts", list.size());
        for (FeedContentElement e : list) {
            feed.addElement(new FeedContentBox(executor, appConfig, uiConfig, applicationContext.getBean(HostServices.class), e, authorCache));
        }
        eventCache.elementsProperty().removeAll(list);
        log.debug("Feed size now {}", feed.getFeedContent().size());
    }

    @Override
    public void connected(WebSocketSession session) {
        // global feed always active
        requestSender.sendRequest(session, RequestFactory.buildPublicFeedRequest());
    }

    @Override
    public void disconnected(WebSocketSession session) {

    }
}
