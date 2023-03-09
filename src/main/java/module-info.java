open module parrhesia1000 {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires net.rgielen.fxweaver.core;
    requires net.rgielen.fxweaver.spring;
    requires org.apache.commons.lang3;
    requires org.bitcoinj.core;
    requires org.slf4j;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.core;
    requires spring.web;
    requires lombok;
    requires org.apache.commons.io;
    requires org.threeten.extra;
    requires spring.websocket;
    requires org.apache.tomcat.embed.core;
    requires org.apache.tomcat.embed.el;
    requires org.apache.tomcat.embed.websocket;
    exports parrhesia1000;
    exports parrhesia1000.dto;
    exports parrhesia1000.ui;

}