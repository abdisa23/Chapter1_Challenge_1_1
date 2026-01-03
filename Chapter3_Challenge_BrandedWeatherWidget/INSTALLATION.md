# Installation and Setup Guide

## Prerequisites

1. **Java Development Kit (JDK) 11 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Apache Maven 3.6+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -v`

3. **JavaFX SDK 17+** (Optional - included via Maven)
   - Download from: https://gluonhq.com/products/javafx/
   - Only needed if not using Maven dependencies

## Project Setup

### Option 1: Using IDE (Recommended)

1. **IntelliJ IDEA:**
   - File → Open → Select project folder
   - Maven will automatically download dependencies
   - Run configuration is auto-created

2. **Eclipse:**
   - File → Import → Maven → Existing Maven Projects
   - Select project folder
   - Right-click project → Run As → Java Application

3. **VS Code:**
   - Open project folder
   - Install "Extension Pack for Java" and "Maven for Java"
   - Run from the Run tab

### Option 2: Command Line

```bash
# Clone or extract project
cd BrandedWeatherWidget

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run

# Or create executable JAR
mvn clean package
java -jar target/BrandedWeatherWidget-1.0.0.jar
```

## Project Structure Explanation

```
src/main/java/com/ecolife/weatherwidget/
├── MainApp.java                 # Entry point, window setup
├── controller/                  # MVC Controllers
│   └── WeatherController.java   # Main controller logic
├── model/                       # Data models
│   ├── Weather.java            # Current weather data
│   └── Forecast.java           # Forecast data
├── service/                     # Business logic
│   └── WeatherService.java     # API/mock data service
└── util/                       # Utility classes
    ├── AnimationUtil.java      # Animation helper
    ├── ConfigManager.java      # Configuration
    └── ThemeManager.java       # Theme management

src/main/resources/
├── fxml/                       # UI layouts
│   └── WeatherView.fxml
├── css/                        # Stylesheets
│   ├── style.css              # Main theme
│   ├── theme-blue.css         # Blue theme
│   └── theme-dark.css         # Dark theme
├── images/                     # Images and icons
│   └── leaf_icon.png
└── config.properties          # Application configuration
```

## Adding Real Weather Data

To use real weather data instead of mock data:

1. **Get an API Key:**
   - Sign up at https://openweathermap.org/api
   - Get your free API key

2. **Update WeatherService.java:**
   ```java
   private static final String API_KEY = "your_actual_api_key_here";
   ```

3. **Uncomment API calls in WeatherController:**
   ```java
   // Replace mock calls with real API calls
   currentWeather = weatherService.getCurrentWeather(currentCity);
   forecastList = weatherService.get5DayForecast(currentCity);
   ```

## Customization

### Changing Colors
Edit `style.css` to modify colors:
- Primary: `#2E7D32`
- Secondary: `#4CAF50`
- Accent: `#FFC107`

### Adding Features
1. **New Weather Source:**
   - Create new service class implementing `WeatherProvider` interface
   - Update controller to use new service

2. **Additional Metrics:**
   - Add fields to `Weather` model
   - Update FXML with new UI elements
   - Update controller logic

### Building Distribution

```bash
# Create fat JAR with dependencies
mvn clean compile assembly:single

# Create native installer (requires additional plugins)
mvn jpackage:jpackage

# For Windows:
# Creates .exe installer in target/ directory

# For macOS:
# Creates .dmg or .pkg installer

# For Linux:
# Creates .deb or .rpm package
```

## Troubleshooting

### Common Issues:

1. **JavaFX not found:**
   ```
   Error: JavaFX runtime components are missing
   ```
   Solution: Ensure you're using Maven command or add VM options:
   ```
   --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml
   ```

2. **Missing dependencies:**
   ```
   Could not resolve dependencies
   ```
   Solution: Run `mvn clean install -U`

3. **FXML loading error:**
   ```
   Location is not set
   ```
   Solution: Check resource paths and ensure files are in `resources/` folder

4. **UI not showing:**
   Check that FXML file path is correct in `MainApp.java`

### Debug Mode:
```bash
mvn clean compile javafx:run -Djavafx.verbose=true
```

## Support

For issues or questions:
1. Check the troubleshooting section
2. Review JavaFX documentation: https://openjfx.io/
3. Check Maven logs in `target/` directory

## License

Educational project - Free to use and modify.