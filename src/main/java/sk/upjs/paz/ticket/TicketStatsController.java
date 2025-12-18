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
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import sk.upjs.paz.Factory;

public class TicketStatsController {

    @FXML
    public DatePicker fromDatePicker;
    @FXML
    public DatePicker toDatePicker;
    @FXML
    public ComboBox<String> aggregationComboBox;
    @FXML
    private SwingNode chartContainer;

    @FXML
    private Button cashierSalesButton;
    @FXML
    private Button typeSalesButton;

    private final TicketService ticketService;

    // stav: true = cashier, false = type
    private boolean showCashiers = true;

    public TicketStatsController() {
        ticketService = new TicketService(Factory.INSTANCE.getTicketDao());
    }

    @FXML
    public void initialize() {
        aggregationComboBox.getItems().addAll("DAY", "WEEK", "MONTH");
        aggregationComboBox.getSelectionModel().select("DAY");

        fromDatePicker.setValue(LocalDate.of(2025, 12, 6));
        toDatePicker.setValue(LocalDate.of(2025, 12, 8));

        ChangeListener<Object> updateListener = (obs, oldVal, newVal) -> updateChart();
        fromDatePicker.valueProperty().addListener(updateListener);
        toDatePicker.valueProperty().addListener(updateListener);
        aggregationComboBox.valueProperty().addListener(updateListener);

        // tlačidlá pre prepínanie
        cashierSalesButton.setOnAction(e -> {
            showCashiers = true;
            updateChart();
        });

        typeSalesButton.setOnAction(e -> {
            showCashiers = false;
            updateChart();
        });

        // vykreslíme defaultne
        updateChart();
    }

    private void updateChart() {
        LocalDate start = fromDatePicker.getValue();
        LocalDate end = toDatePicker.getValue();
        String period = aggregationComboBox.getValue();

        if (showCashiers) {
            Map<String, Map<String, Long>> groupedData =
                    getGroupedDataWithPeriod(ticketService.getTicketCountByCashier(start, end, period), period);
            DefaultCategoryDataset dataset = createDataset(groupedData);
            JFreeChart chart = ChartFactory.createBarChart(
                    "Predaj lístkov podľa predavačov",
                    "Obdobie",
                    "Počet lístkov",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            setChart(chart);
        } else {
            Map<String, Map<String, Long>> groupedData =
                    getGroupedDataWithPeriod(ticketService.getTicketCountByType(start, end, period), period);
            DefaultCategoryDataset dataset = createDataset(groupedData);
            JFreeChart chart = ChartFactory.createBarChart(
                    "Predaj lístkov podľa typu",
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
    }

    /** Zozbiera data do mapy: periodKey -> (séria -> počet) */
    private Map<String, Map<String, Long>> getGroupedDataWithPeriod(Map<String, Long> flatData, String period) {
        Map<String, Map<String, Long>> result = new TreeMap<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (String key : flatData.keySet()) {
            String[] parts = key.split("\\(");
            String seriesName = parts[0].trim();
            LocalDate date = LocalDate.now(); // default

            if (parts.length > 1) {
                try {
                    date = LocalDate.parse(parts[1].replace(")", "").trim(), dayFormatter);
                } catch (Exception ignored) {}
            }

            String periodKey;
            switch (period.toUpperCase()) {
                case "WEEK":
                    int weekNumber = date.get(weekFields.weekOfWeekBasedYear());
                    int year = date.getYear();
                    periodKey = "Week " + weekNumber + " " + year;
                    break;
                case "MONTH":
                    int monthNumber = date.getMonthValue();
                    periodKey = "Month " + monthNumber + " " + date.getYear();
                    break;
                default:
                    DateTimeFormatter dayWithWeekFormatter = DateTimeFormatter.ofPattern("EEEE, dd.MM.yyyy");
                    periodKey = date.format(dayWithWeekFormatter);
                    break;
            }

            result.putIfAbsent(periodKey, new LinkedHashMap<>());
            result.get(periodKey).put(seriesName, flatData.get(key));
        }

        return result;
    }

    /** Vytvorí DefaultCategoryDataset z mapy */
    private DefaultCategoryDataset createDataset(Map<String, Map<String, Long>> groupedData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (String periodKey : groupedData.keySet()) {
            Map<String, Long> seriesMap = groupedData.get(periodKey);
            for (String series : seriesMap.keySet()) {
                dataset.addValue(seriesMap.get(series), series, periodKey);
            }
        }
        return dataset;
    }

    /** Vloží graf do SwingNode */
    private void setChart(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(false);

        javafx.application.Platform.runLater(() ->
                SwingUtilities.invokeLater(() -> chartContainer.setContent(chartPanel))
        );
    }
}
