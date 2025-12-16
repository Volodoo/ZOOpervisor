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

        // 1. Zoznam všetkých dní (usporiadaný)
        Set<LocalDate> days = new TreeSet<>();

        for (Ticket ticket : tickets) {
            LocalDate day = ticket.getPurchaseDateTime().toLocalDate();
            days.add(day);
        }

        // 2. Štatistiky: pokladník -> (deň -> počet lístkov)
        Map<User, Map<LocalDate, Long>> stats = new HashMap<>();

        for (Ticket ticket : tickets) {
            User cashier = ticket.getCashier();
            LocalDate day = ticket.getPurchaseDateTime().toLocalDate();

            // ak pokladník ešte nemá mapu dní, vytvor ju
            if (!stats.containsKey(cashier)) {
                stats.put(cashier, new HashMap<>());
            }

            Map<LocalDate, Long> byDay = stats.get(cashier);

            // zvýš počet lístkov v daný deň
            long count = byDay.getOrDefault(day, 0L);
            byDay.put(day, count + 1);
        }

        // 3. Dataset pre graf
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 4. Textové štatistiky
        StringBuilder statsText =
                new StringBuilder(translate("statistic.option1") + ":\n");

        for (User cashier : stats.keySet()) {
            Map<LocalDate, Long> byDay = stats.get(cashier);
            long total = 0;

            for (LocalDate day : days) {
                long count = byDay.getOrDefault(day, 0L);

                dataset.addValue(
                        count,
                        cashier.getFirstName() + " " + cashier.getLastName(),
                        day.toString()
                );

                total += count;
            }

            statsText.append(cashier.getFirstName())
                    .append(" ")
                    .append(cashier.getLastName())
                    .append(": ")
                    .append(total)
                    .append(" ")
                    .append(translate("tickets"))
                    .append("\n");
        }

        statsLabel.setText(statsText.toString());

        // 5. Vytvorenie grafu
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

        // 1. Všetky typy lístkov (poradie bude vždy rovnaké)
        List<String> allTypes = Arrays.asList("Child", "Student_Senior", "Adult");

        // 2. Počty lístkov podľa typu
        Map<String, Long> typeCounts = new HashMap<>();

        for (Ticket ticket : tickets) {
            String type = ticket.getType();

            long count = 0;
            if (typeCounts.containsKey(type)) {
                count = typeCounts.get(type);
            }

            typeCounts.put(type, count + 1);
        }

        // 3. Dataset pre graf
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // 4. Textové štatistiky
        StringBuilder statsText =
                new StringBuilder(translate("statistic.option2") + ":\n");

        for (String type : allTypes) {
            long count = 0;

            if (typeCounts.containsKey(type)) {
                count = typeCounts.get(type);
            }

            dataset.addValue(
                    count,
                    translate("menu.tickets"),
                    getTypeInSlovak(type)
            );

            statsText.append(getTypeInSlovak(type))
                    .append(": ")
                    .append(count)
                    .append(" ")
                    .append("tickets")
                    .append("\n");
        }

        statsLabel.setText(statsText.toString());

        // 5. Vytvorenie stĺpcového grafu
        JFreeChart barChart = ChartFactory.createBarChart(
                translate("statistic.option2"),
                translate("ticket.type"),
                translate("ticket.count"),
                dataset
        );

        setChart(barChart);
    }

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
