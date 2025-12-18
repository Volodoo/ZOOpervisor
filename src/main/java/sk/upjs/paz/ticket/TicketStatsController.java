package sk.upjs.paz.ticket;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import sk.upjs.paz.Factory;

public class TicketStatsController {

    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ComboBox<String> aggregationComboBox;
    @FXML
    private SwingNode chartContainer;

    @FXML
    private Button cashierSalesButton;
    @FXML
    private Button typeSalesButton;

    private final TicketService ticketService;
    private boolean showCashiers = true;

    public TicketStatsController() {
        ticketService = new TicketService(Factory.INSTANCE.getTicketDao());
    }

    @FXML
    public void initialize() {
        aggregationComboBox.getItems().addAll("DAY", "WEEK", "MONTH");
        aggregationComboBox.getSelectionModel().select("DAY");

        fromDatePicker.setValue(LocalDate.of(2025, 12, 6));
        toDatePicker.setValue(LocalDate.now());

        ChangeListener<Object> updateListener = (obs, oldVal, newVal) -> updateChart();
        fromDatePicker.valueProperty().addListener(updateListener);
        toDatePicker.valueProperty().addListener(updateListener);
        aggregationComboBox.valueProperty().addListener(updateListener);

        cashierSalesButton.setOnAction(e -> switchMode(true));
        typeSalesButton.setOnAction(e -> switchMode(false));

        updateChart();
    }

    private void switchMode(boolean showCashiers) {
        this.showCashiers = showCashiers;
        updateChart();
    }

    private void updateChart() {
        LocalDate start = fromDatePicker.getValue();
        LocalDate end = toDatePicker.getValue();
        String period = aggregationComboBox.getValue();

        // Získame flat data podľa vybraného režimu
        Map<String, Long> flatData;
        if (showCashiers) {
            flatData = ticketService.getTicketCountByCashier(start, end, period);
        } else {
            flatData = ticketService.getTicketCountByType(start, end, period);
        }

        // Skupíme flat data do formátu: periodKey -> (séria -> počet)
        Map<String, Map<String, Long>> groupedData = groupFlatData(flatData);

        // Vytvoríme dataset a graf
        DefaultCategoryDataset dataset = createDataset(groupedData);

        String chartTitle;
        if (showCashiers) {
            chartTitle = "Predaj lístkov podľa predavačov";
        } else {
            chartTitle = "Predaj lístkov podľa typu";
        }

        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle,
                "Obdobie",
                "Počet lístkov",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        setChart(chart);
    }

    /**
     * Skupí flatData do formátu: periodKey -> (séria -> počet), poradie zachované
     */
    private Map<String, Map<String, Long>> groupFlatData(Map<String, Long> flatData) {
        Map<String, Map<String, Long>> grouped = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : flatData.entrySet()) {
            String key = entry.getKey();
            int startIdx = key.indexOf('(');
            int endIdx = key.indexOf(')');
            String seriesName = key.substring(0, startIdx).trim();
            String periodKey = key.substring(startIdx + 1, endIdx).trim();

            grouped.computeIfAbsent(periodKey, k -> new LinkedHashMap<>())
                    .put(seriesName, entry.getValue());
        }
        return grouped;
    }

    private DefaultCategoryDataset createDataset(Map<String, Map<String, Long>> groupedData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        groupedData.forEach((periodKey, seriesMap) ->
                seriesMap.forEach((series, value) -> dataset.addValue(value, series, periodKey))
        );
        return dataset;
    }

    private void setChart(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(false);

        BarRenderer renderer = (BarRenderer) chart.getCategoryPlot().getRenderer();
        if (showCashiers) {
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, Color.GREEN);
            renderer.setSeriesPaint(2, Color.BLUE);
            renderer.setSeriesPaint(3, Color.ORANGE);
            renderer.setSeriesPaint(4, Color.PINK);
        } else {
            renderer.setSeriesPaint(0, Color.CYAN);
            renderer.setSeriesPaint(1, Color.MAGENTA);
            renderer.setSeriesPaint(2, Color.YELLOW);
        }

        javafx.application.Platform.runLater(() ->
                SwingUtilities.invokeLater(() -> chartContainer.setContent(chartPanel))
        );
    }
}
