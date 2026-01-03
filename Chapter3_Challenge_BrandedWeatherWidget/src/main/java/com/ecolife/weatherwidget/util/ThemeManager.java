package com.ecolife.weatherwidget.util;

import javafx.scene.Parent;
import javafx.scene.Scene;

public class ThemeManager {
    public enum Theme {
        GREEN("Green", "/css/style.css"),
        BLUE("Blue", "/css/theme-blue.css"),
        DARK("Dark", "/css/theme-dark.css");
        
        private final String name;
        private final String cssPath;
        
        Theme(String name, String cssPath) {
            this.name = name;
            this.cssPath = cssPath;
        }
        
        public String getName() { return name; }
        public String getCssPath() { return cssPath; }
    }
    
    private static Theme currentTheme = Theme.GREEN;
    
    public static void applyTheme(Scene scene, Theme theme) {
        if (scene == null) return;
        
        // Clear existing theme stylesheets
        scene.getStylesheets().clear();
        
        // Apply new theme
        String cssPath = theme.getCssPath();
        String cssUrl = ThemeManager.class.getResource(cssPath).toExternalForm();
        
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl);
            currentTheme = theme;
            System.out.println("Applied theme: " + theme.getName());
        } else {
            System.err.println("Theme CSS not found: " + cssPath);
        }
    }
    
    public static void applyTheme(Parent root, Theme theme) {
        if (root == null || root.getScene() == null) return;
        applyTheme(root.getScene(), theme);
    }
    
    public static Theme getCurrentTheme() {
        return currentTheme;
    }
    
    public static void cycleTheme(Scene scene) {
        Theme[] themes = Theme.values();
        int nextIndex = (currentTheme.ordinal() + 1) % themes.length;
        applyTheme(scene, themes[nextIndex]);
    }
}