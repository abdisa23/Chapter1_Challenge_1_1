package com.ecolife.weatherwidget.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

public class AnimationUtil {
    
    // Fade in animation
    public static void fadeIn(Node node, int durationMillis) {
        node.setOpacity(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(durationMillis), node);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
    
    // Fade out animation
    public static void fadeOut(Node node, int durationMillis) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(durationMillis), node);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.play();
    }
    
    // Scale animation on hover
    public static void addHoverAnimation(Node node, double scaleFactor, int durationMillis) {
        node.setOnMouseEntered(e -> {
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(durationMillis), node);
            scaleIn.setToX(scaleFactor);
            scaleIn.setToY(scaleFactor);
            scaleIn.play();
        });
        
        node.setOnMouseExited(e -> {
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(durationMillis), node);
            scaleOut.setToX(1);
            scaleOut.setToY(1);
            scaleOut.play();
        });
    }
    
    // Pulse animation
    public static void addPulseAnimation(Node node, double scaleFactor, int durationMillis) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(durationMillis), node);
        pulse.setFromX(1);
        pulse.setFromY(1);
        pulse.setToX(scaleFactor);
        pulse.setToY(scaleFactor);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }
    
    // Rotation animation
    public static void rotateAnimation(Node node, double degrees, int durationMillis) {
        RotateTransition rotate = new RotateTransition(Duration.millis(durationMillis), node);
        rotate.setByAngle(degrees);
        rotate.setCycleCount(1);
        rotate.play();
    }
    
    // Sequential fade animation for multiple nodes
    public static void sequentialFadeIn(Node[] nodes, int delayBetween, int durationEach) {
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            node.setOpacity(0);
            
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(i * delayBetween),
                new KeyValue(node.opacityProperty(), 1, 
                Interpolator.EASE_IN))
            );
            timeline.play();
        }
    }
    
    // Shake animation for error/warning
    public static void shakeAnimation(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(100), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}