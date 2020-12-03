package com.kruskal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        new Kruska();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {

            }
        };
        timer.start();
    }
}
