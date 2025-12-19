package sk.upjs.paz.ticket;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.SwingUtilities;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sk.upjs.paz.Factory;
import sk.upjs.paz.user.User;
import sk.upjs.paz.user.UserDao;

import static sk.upjs.paz.SceneManager.getBundle;

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
    private final UserDao userDao;

    private boolean showCashiers = true;

    // Cache ID -> meno
    private final Map<Long, String> cashierLabelCache = new HashMap<>();

    public TicketStatsController() {
        ticketService = new TicketService(Factory.INSTANCE.getTicketDao());
        userDao = Factory.INSTANCE.getUserDao();
    }

    @FXML
    public void initialize() {
        aggregationComboBox.getItems().addAll("period.day", "period.week", "period.month");

        aggregationComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String key) {
                if (key == null) return "";
                try {
                    return getBundle().getString(key);
                } catch (Exception e) {
                    return key;
                }
            }

            @Override
            public String fromString(String string) {
                return null;
            }
        });

        aggregationComboBox.getSelectionModel().select("period.day");

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

    /**
     * 1 = DAY, 2 = WEEK, 3 = MONTH
     */
    private int getSelectedPeriod() {
        String value = aggregationComboBox.getValue();
        return switch (value) {
            case "period.week" -> 2;
            case "period.month" -> 3;
            default -> 1;
        };
    }

    private void updateChart() {
        LocalDate start = fromDatePicker.getValue();
        LocalDate end = toDatePicker.getValue();
        int period = getSelectedPeriod();

        Map<String, Long> flatData = showCashiers
                ? ticketService.getTicketCountByCashier(start, end, period)
                : ticketService.getTicketCountByType(start, end, period);

        Map<String, Map<String, Long>> groupedData = groupFlatData(flatData);
        DefaultCategoryDataset dataset = createDataset(groupedData);

        String chartTitle = showCashiers
                ? getBundle().getString("byCashiers")
                : getBundle().getString("statistic.option2");

        JFreeChart chart = ChartFactory.createBarChart(
                chartTitle,
                getBundle().getString("period.graph"),
                getBundle().getString("ticket.count"),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        setChart(chart);
    }

    /**
     * Zoskupí flat map do datasetu pre JFreeChart
     * periodKey -> (series -> value)
     */
    private Map<String, Map<String, Long>> groupFlatData(Map<String, Long> flatData) {
        Map<String, Map<String, Long>> grouped = new LinkedHashMap<>();

        for (Map.Entry<String, Long> entry : flatData.entrySet()) {
            String key = entry.getKey();

            int startIdx = key.indexOf('(');
            int endIdx = key.indexOf(')');

            String rawSeries = key.substring(0, startIdx).trim(); // ID alebo typ
            String periodKey = key.substring(startIdx + 1, endIdx).trim();

            String seriesName = rawSeries;

            if (showCashiers) {
                Long userId = Long.valueOf(rawSeries);
                seriesName = getCashierLabel(userId);
            }

            grouped.computeIfAbsent(periodKey, k -> new LinkedHashMap<>())
                    .put(seriesName, entry.getValue());
        }

        return grouped;
    }

    private String getCashierLabel(Long userId) {
        return cashierLabelCache.computeIfAbsent(userId, id -> {
            User user = userDao.getById(id);
            if (user == null) {
                return "User ID " + id;
            }
            return user.getFirstName() + " " + user.getLastName();
        });
    }

    private DefaultCategoryDataset createDataset(Map<String, Map<String, Long>> groupedData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        groupedData.forEach((periodKey, seriesMap) ->
                seriesMap.forEach((series, value) ->
                        dataset.addValue(value, series, periodKey))
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
