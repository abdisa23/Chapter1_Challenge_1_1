# EcoLife Branded Weather Widget

A stylish, eco-themed weather widget application built with JavaFX that displays current weather conditions and forecasts with smooth animations.

## Features

- **Current Weather Display**: Shows temperature, humidity, wind speed, and conditions
- **5-Day Forecast**: Visual forecast with day-by-day predictions
- **Eco-Friendly Design**: Green color palette with leaf icon branding
- **Smooth Animations**: Fade transitions and hover effects
- **Responsive Layout**: Adapts to different screen sizes
- **Mock Weather API**: Simulated weather data with realistic values

## Project Structure

```
BrandedWeatherWidget/
├── src/main/java/com/ecolife/weatherwidget/
│   ├── MainApp.java              # Application entry point
│   ├── controller/               # MVC Controller
│   ├── model/                    # Data models
│   └── util/                     # Utility classes
├── src/main/resources/
│   ├── fxml/                     # FXML layout files
│   ├── css/                      # Stylesheets
│   └── images/                   # Application images
└── pom.xml                       # Maven configuration
```

## Requirements

- Java 11 or higher
- Maven 3.6+
- JavaFX 17

## Running the Application

### Using Maven:
```bash
mvn clean javafx:run
```

### Or compile and run manually:
```bash
mvn clean compile
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/BrandedWeatherWidget-1.0.0.jar
```

## Usage

1. Launch the application
2. The widget will display mock weather data for "EcoCity"
3. Hover over forecast cards to see details
4. Click the refresh button to update weather data
5. Close the window to exit

## Design Philosophy

This widget follows eco-friendly design principles:
- Green color scheme promoting environmental awareness
- Clean, minimalist interface
- Smooth animations for pleasant user experience
- Leaf icon representing nature and sustainability

## License

This project is for educational purposes as part of the JavaFX learning challenge.