/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stockpriceapplet;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.*;

public class StockPriceApplet extends Applet implements Runnable, ActionListener, MouseListener, MouseMotionListener {

    // Stock data
    private Vector<StockData> stockData;
    private Vector<Double> priceHistory;
    private double currentPrice;
    private Random random;

    // Chart properties
    private int chartWidth, chartHeight;
    private int chartX, chartY;
    private int selectedPoint = -1;
    private boolean dragging = false;

    // UI components
    private Button startButton, stopButton;
    private Choice stockSelector;
    private Label priceLabel, timeLabel;
    private Thread animationThread;
    private boolean running = false;

    // Colors
    private Color bgColor = new Color(240, 240, 240);
    private Color gridColor = new Color(200, 200, 200);
    private Color lineColor = new Color(0, 100, 200);
    private Color pointColor = new Color(200, 50, 50);

    public void init() {
        setBackground(bgColor);
        setLayout(new BorderLayout());

        initializeStocks();
        setupUI();
        setupChart();

        addMouseListener(this);
        addMouseMotionListener(this);

        // Start with random data
        generateInitialData();
    }

    private void initializeStocks() {
        stockData = new Vector<StockData>();
        stockData.add(new StockData("AAPL", "Apple Inc.", 150.0));
        stockData.add(new StockData("GOOGL", "Google LLC", 2800.0));
        stockData.add(new StockData("MSFT", "Microsoft Corp", 300.0));
        stockData.add(new StockData("AMZN", "Amazon.com Inc", 3300.0));
        stockData.add(new StockData("TSLA", "Tesla Inc", 700.0));

        priceHistory = new Vector<Double>();
        random = new Random();
        currentPrice = stockData.get(0).basePrice;
    }

    private void setupUI() {
        // Control panel
        Panel controlPanel = new Panel();
        controlPanel.setLayout(new FlowLayout());

        // Stock selector
        controlPanel.add(new Label("Select Stock:"));
        stockSelector = new Choice();
        for (StockData stock : stockData) {
            stockSelector.add(stock.symbol + " - " + stock.name);
        }
        stockSelector.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                switchStock(stockSelector.getSelectedIndex());
            }
        });
        controlPanel.add(stockSelector);

        // Control buttons
        startButton = new Button("Start");
        stopButton = new Button("Stop");
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        controlPanel.add(startButton);
        controlPanel.add(stopButton);

        // Price display
        priceLabel = new Label("Current Price: $--.--");
        timeLabel = new Label("Last Update: --:--:--");
        controlPanel.add(priceLabel);
        controlPanel.add(timeLabel);

        add(controlPanel, BorderLayout.NORTH);
    }

    private void setupChart() {
        chartWidth = 600;
        chartHeight = 300;
        chartX = 50;
        chartY = 100;
    }

    private void generateInitialData() {
        priceHistory.clear();
        double price = currentPrice;
        for (int i = 0; i < 50; i++) {
            priceHistory.add(price);
            price += (random.nextDouble() - 0.5) * 10;
        }
        currentPrice = price;
    }

    private void switchStock(int index) {
        if (index >= 0 && index < stockData.size()) {
            currentPrice = stockData.get(index).basePrice;
            generateInitialData();
            repaint();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        drawChart(g);
        drawInfoPanel(g);
    }

    private void drawChart(Graphics g) {
        // Draw chart background
        g.setColor(Color.WHITE);
        g.fillRect(chartX, chartY, chartWidth, chartHeight);

        // Draw grid
        g.setColor(gridColor);
        for (int i = 1; i < 5; i++) {
            int y = chartY + (chartHeight / 5) * i;
            g.drawLine(chartX, y, chartX + chartWidth, y);
        }
        for (int i = 1; i < 10; i++) {
            int x = chartX + (chartWidth / 10) * i;
            g.drawLine(x, chartY, x, chartY + chartHeight);
        }

        // Draw border
        g.setColor(Color.BLACK);
        g.drawRect(chartX, chartY, chartWidth, chartHeight);

        // Draw price line
        if (priceHistory.size() > 1) {
            drawPriceLine(g);
        }

        // Draw title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        int selectedIndex = stockSelector.getSelectedIndex();
        String title = stockData.get(selectedIndex).symbol + " - " + stockData.get(selectedIndex).name;
        g.drawString(title, chartX + 10, chartY - 10);

        // Draw Y-axis labels
        drawYAxisLabels(g);
    }

    private void drawPriceLine(Graphics g) {
        double minPrice = Collections.min(priceHistory);
        double maxPrice = Collections.max(priceHistory);
        double priceRange = maxPrice - minPrice;

        if (priceRange == 0) priceRange = 1;

        g.setColor(lineColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2));

        int pointSpacing = chartWidth / (priceHistory.size() - 1);

        for (int i = 0; i < priceHistory.size() - 1; i++) {
            double price1 = priceHistory.get(i);
            double price2 = priceHistory.get(i + 1);

            int x1 = chartX + i * pointSpacing;
            int y1 = chartY + chartHeight - (int)(((price1 - minPrice) / priceRange) * chartHeight);

            int x2 = chartX + (i + 1) * pointSpacing;
            int y2 = chartY + chartHeight - (int)(((price2 - minPrice) / priceRange) * chartHeight);

            g.drawLine(x1, y1, x2, y2);
        }

        // Draw points
        g.setColor(pointColor);
        for (int i = 0; i < priceHistory.size(); i++) {
            double price = priceHistory.get(i);
            int x = chartX + i * pointSpacing;
            int y = chartY + chartHeight - (int)(((price - minPrice) / priceRange) * chartHeight);

            if (i == selectedPoint) {
                g.setColor(Color.RED);
                g.fillOval(x - 4, y - 4, 8, 8);
                g.setColor(pointColor);
            } else {
                g.fillOval(x - 3, y - 3, 6, 6);
            }
        }
    }

    private void drawYAxisLabels(Graphics g) {
        double minPrice = Collections.min(priceHistory);
        double maxPrice = Collections.max(priceHistory);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 10));

        DecimalFormat df = new DecimalFormat("$#.##");
        g.drawString(df.format(maxPrice), chartX - 45, chartY + 10);
        g.drawString(df.format(minPrice), chartX - 45, chartY + chartHeight - 5);
        g.drawString(df.format((maxPrice + minPrice) / 2), chartX - 45, chartY + chartHeight / 2);
    }

    private void drawInfoPanel(Graphics g) {
        if (selectedPoint >= 0 && selectedPoint < priceHistory.size()) {
            double price = priceHistory.get(selectedPoint);
            int x = chartX + selectedPoint * (chartWidth / (priceHistory.size() - 1));
            int y = chartY + chartHeight - (int)(((price - Collections.min(priceHistory)) /
                    (Collections.max(priceHistory) - Collections.min(priceHistory))) * chartHeight);

            // Draw tooltip
            g.setColor(new Color(255, 255, 200));
            g.fillRect(x + 10, y - 40, 100, 30);
            g.setColor(Color.BLACK);
            g.drawRect(x + 10, y - 40, 100, 30);

            DecimalFormat df = new DecimalFormat("$#.##");
            g.drawString("Price: " + df.format(price), x + 15, y - 25);
            g.drawString("Point: " + selectedPoint, x + 15, y - 15);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startAnimation();
        } else if (e.getSource() == stopButton) {
            stopAnimation();
        }
    }

    private void startAnimation() {
        if (!running) {
            running = true;
            animationThread = new Thread(this);
            animationThread.start();
        }
    }

    private void stopAnimation() {
        running = false;
        if (animationThread != null) {
            animationThread = null;
        }
    }

    public void run() {
        while (running) {
            // Simulate price change
            double change = (random.nextDouble() - 0.5) * 20;
            currentPrice += change;

            // Add to history and remove oldest if too many
            priceHistory.add(currentPrice);
            if (priceHistory.size() > 100) {
                priceHistory.remove(0);
            }

            // Update display
            updateDisplay();

            try {
                Thread.sleep(1000); // Update every second
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void updateDisplay() {
        DecimalFormat df = new DecimalFormat("$#.##");
        priceLabel.setText("Current Price: " + df.format(currentPrice));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        timeLabel.setText("Last Update: " + timeFormat.format(new Date()));

        repaint();
    }

    // Mouse event handlers for interactive chart
    public void mouseClicked(MouseEvent e) {
        checkPointSelection(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {
        if (checkPointSelection(e.getX(), e.getY())) {
            dragging = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (dragging && selectedPoint >= 0) {
            // Calculate new price based on Y position
            double minPrice = Collections.min(priceHistory);
            double maxPrice = Collections.max(priceHistory);
            double priceRange = maxPrice - minPrice;

            if (priceRange == 0) priceRange = 1;

            int mouseY = Math.max(chartY, Math.min(chartY + chartHeight, e.getY()));
            double newPrice = minPrice + ((chartY + chartHeight - mouseY) / (double)chartHeight) * priceRange;

            priceHistory.set(selectedPoint, newPrice);
            currentPrice = priceHistory.lastElement();
            updateDisplay();
        }
    }

    private boolean checkPointSelection(int mouseX, int mouseY) {
        if (priceHistory.isEmpty()) return false;

        double minPrice = Collections.min(priceHistory);
        double maxPrice = Collections.max(priceHistory);
        double priceRange = maxPrice - minPrice;
        if (priceRange == 0) priceRange = 1;

        int pointSpacing = chartWidth / (priceHistory.size() - 1);

        for (int i = 0; i < priceHistory.size(); i++) {
            double price = priceHistory.get(i);
            int x = chartX + i * pointSpacing;
            int y = chartY + chartHeight - (int)(((price - minPrice) / priceRange) * chartHeight);

            if (Math.abs(mouseX - x) <= 5 && Math.abs(mouseY - y) <= 5) {
                selectedPoint = i;
                repaint();
                return true;
            }
        }

        selectedPoint = -1;
        repaint();
        return false;
    }

    // Unused mouse event methods
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    public void destroy() {
        stopAnimation();
    }

    // Stock data class
    class StockData {
        String symbol;
        String name;
        double basePrice;

        StockData(String symbol, String name, double basePrice) {
            this.symbol = symbol;
            this.name = name;
            this.basePrice = basePrice;
        }
    }
}