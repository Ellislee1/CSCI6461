package com.csci6461;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class runs the main application as well as contains the UI.
 * @author Ellis Thompson
 * @author imanuelportalatin
 */
public class ComputerSimulation extends Application {
    /**
     * This method contains the window that the program runs in and loads it.
     * @param stage The window on which the application runs
     * @throws IOException IO exception for any crash
     */
    @Override
    public void start(final Stage stage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(ComputerSimulation.class.getResource("Simulator-view.fxml"));
        final Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("Computer Simulation (16-bit)");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main call to run the program.
     * @param args Any arguments passed to the program.
     */
    public static void main(final String[] args) {
        Application.launch();
    }
}