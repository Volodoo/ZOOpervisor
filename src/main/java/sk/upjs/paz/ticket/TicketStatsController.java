package sk.upjs.paz.ticket;

import javafx.fxml.FXML;
import javafx.embed.swing.SwingNode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import sk.upjs.paz.Factory;
import sk.upjs.paz.SceneManager;
import sk.upjs.paz.user.User;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;

import javax.swing.SwingUtilities;

public class TicketStatsController {

    @FXML
    private SwingNode chartContainer;

    @FXML
    private Button btnDailySales;

    @FXML
    private Button btnTypeSales;

    @FXML
    private Label statsLabel;

    private TicketDao ticketDao = Factory.INSTANCE.getTicketDao();
    private List<Ticket> tickets;

    @FXML
    public void initialize() {
        tickets = ticketDao.getAll();

        // Default graf: denné predaje
        showDailySalesChart();

        // Nastavenie tlačidiel
        btnDailySales.setOnAction(e -> showDailySalesChart());
        btnTypeSales.setOnAction(e -> showTypeSalesChart());
    }

    /** Graf: počet predaných lístkov každý deň podľa predavačov */
    private void showDailySalesChart() {
        Set<LocalDate> days = tickets.stream()
                .map(t -> t.getPurchaseDateTime().toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new));

        Map<User, Map<LocalDate, Long>> stats = tickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getCashier,
                        Collectors.groupingBy(
                                t -> t.getPurchaseDateTime().toLocalDate(),
                                Collectors.counting()
                        )
                ));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        StringBuilder statsText = new StringBuilder(translate("statistic.option1")+ ":\n");

        for (User cashier : stats.keySet()) {
            Map<LocalDate, Long> byDay = stats.get(cashier);
            long total = 0;
            for (LocalDate day : days) {
                long count = byDay.getOrDefault(day, 0L);
                dataset.addValue(count, cashier.getFirstName() + " " + cashier.getLastName(), day.toString());
                total += count;
            }
            statsText.append(cashier.getFirstName())
                    .append(" ")
                    .append(cashier.getLastName())
                    .append(": ")
                    .append(total+" ")
                    .append(translate("tickets"))
                    .append(":\n");
        }

        statsLabel.setText(statsText.toString());

        JFreeChart lineChart = ChartFactory.createLineChart(
                translate("statistic.option1"),
                translate("date"),
                translate("ticket.count"),
                dataset
        );

        setChart(lineChart);
    }

    /** Graf: počet predaných lístkov podľa typu lístka (histogram) */
    private void showTypeSalesChart() {
        List<String> allTypes = Arrays.asList("Child", "Student_Senior", "Adult");

        Map<String, Long> typeCounts = tickets.stream()
                .collect(Collectors.groupingBy(Ticket::getType, Collectors.counting()));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        StringBuilder statsText = new StringBuilder(translate("statistic.option2")+ ":\n");

        for (String type : allTypes) {
            long count = typeCounts.getOrDefault(type, 0L);
            dataset.addValue(count, translate("menu.tickets"), getTypeInSlovak(type));
            statsText.append(getTypeInSlovak(type)).append(": ").append(count+" ").append("tickets").append(" \n");
        }

        statsLabel.setText(statsText.toString());

        JFreeChart barChart = ChartFactory.createBarChart(
                translate("statistic.option2"),
                translate("ticket.type"),
                translate("ticket.count"),
                dataset
        );

        setChart(barChart);
    }

    /** Pomocná metóda na aktualizáciu SwingNode */
    private void setChart(JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        javafx.application.Platform.runLater(() ->
                SwingUtilities.invokeLater(() -> chartContainer.setContent(chartPanel))
        );
    }

    private String translate(String key) {
        try {
            return SceneManager.getBundle().getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    private String getTypeInSlovak(String type) {
        switch (type) {
            case "child":
                return "Dieťa";
            case "studentSenior":
                return "Študent/Senior";
            case "adult":
                return "Dospelý";
            default:
                return type;
        }
    }
}
