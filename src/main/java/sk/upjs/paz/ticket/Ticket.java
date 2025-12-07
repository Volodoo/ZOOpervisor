package sk.upjs.paz.ticket;

import lombok.Data;
import sk.upjs.paz.animal.Animal;
import sk.upjs.paz.animal.Sex;
import sk.upjs.paz.user.User;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Ticket {
    private Long id;
    private String type;
    private LocalDateTime purchaseTimestamp;
    private BigDecimal price;
    private User cashier;

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

        Timestamp ts = rs.getTimestamp(aliasPrefix + "purchase_timestamp");
        LocalDateTime purchaseTimestamp = null;
        if (ts != null) {
            purchaseTimestamp = ts.toLocalDateTime();
        }

        ticket.setPurchaseTimestamp(purchaseTimestamp);
        ticket.setPrice(rs.getBigDecimal(aliasPrefix + "price"));
        ticket.setCashier(null);
        return ticket;
    }


}

