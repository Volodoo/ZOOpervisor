package sk.upjs.paz.ticket;

import lombok.Data;
import sk.upjs.paz.user.User;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class Ticket {
    private Long id;
    private String type;
    private LocalDateTime purchaseDateTime;
    private BigDecimal price;
    private User cashier;

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String formattedPurchaseDateTime = purchaseDateTime.format(formatter);
        String formattedCashier = cashier.getFirstName() + " " + cashier.getLastName();

        String typeSlovak = "";
        switch (type) {
            case "Child":
                typeSlovak = "Dieťa";
                break;
            case "Student_Senior":
                typeSlovak = "Študent/Senior";
                break;
            case "Adult":
                typeSlovak = "Dospelý";
                break;

            default:
                typeSlovak = type;
        }

        return String.format("Typ: %s\nDátum a Čas predaja: %s\nCena: %s €\nPredavač(ka): %s",
                typeSlovak, formattedPurchaseDateTime, price, formattedCashier);
    }

    public static Ticket fromResultSet(ResultSet rs) throws SQLException {
        return fromResultSet(rs, "");
    }

    public static Ticket fromResultSet(ResultSet rs, String aliasPrefix) throws SQLException {
        var id = rs.getLong(aliasPrefix + "id");
        if (rs.wasNull()) {
            return null;
        }

        var ticket = new Ticket();
        ticket.setId(id);
        ticket.setType(rs.getString(aliasPrefix + "type"));

        Timestamp ts = rs.getTimestamp(aliasPrefix + "purchase_date_time");
        LocalDateTime purchaseTimestamp = null;
        if (ts != null) {
            purchaseTimestamp = ts.toLocalDateTime();
        }

        ticket.setPurchaseDateTime(purchaseTimestamp);
        ticket.setPrice(rs.getBigDecimal(aliasPrefix + "price"));
        ticket.setCashier(null);
        return ticket;
    }


}

