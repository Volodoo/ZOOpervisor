package sk.upjs.paz;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import sk.upjs.paz.animal.AnimalDao;
import sk.upjs.paz.animal.MysqlAnimalDao;
import sk.upjs.paz.enclosure.EnclosureDao;
import sk.upjs.paz.enclosure.MysqlEnclosureDao;
import sk.upjs.paz.security.AuthDao;
import sk.upjs.paz.security.MysqlAuthDao;
import sk.upjs.paz.task.MysqlTaskDao;
import sk.upjs.paz.task.TaskDao;
import sk.upjs.paz.ticket.MysqlTicketDao;
import sk.upjs.paz.ticket.TicketDao;
import sk.upjs.paz.user.MysqlUserDao;
import sk.upjs.paz.user.UserDao;


public enum Factory {
    INSTANCE;

    private volatile JdbcOperations jdbcOperations;
    private volatile UserDao userDao;
    private volatile AnimalDao animalDao;
    private volatile TaskDao taskDao;
    private volatile EnclosureDao enclosureDao;
    private volatile TicketDao ticketDao;
    private volatile AuthDao authDao;

    private final Object lock = new Object();

    public JdbcOperations getMysqlJdbcOperations() {
        if (jdbcOperations == null) {
            synchronized (lock) {
                if (jdbcOperations == null) {
                    var dataSource = new MysqlDataSource();
                    dataSource.setUrl(System.getProperty("DB_JDBC", "jdbc:mysql://localhost:3307/zoopervisor"));
                    dataSource.setUser(System.getProperty("DB_USER", "zoopervisor"));
                    dataSource.setPassword(System.getProperty("DB_PASSWORD", "zoopervisor"));
                    jdbcOperations = new JdbcTemplate(dataSource);
                }
            }
        }
        return jdbcOperations;
    }

    public UserDao getUserDao() {
        if (userDao == null) {
            synchronized (lock) {
                if (userDao == null) {
                    userDao = new MysqlUserDao(getMysqlJdbcOperations());
                }
            }
        }
        return userDao;
    }

    public AnimalDao getAnimalDao() {
        if (animalDao == null) {
            synchronized (lock) {
                if (animalDao == null) {
                    animalDao = new MysqlAnimalDao(getMysqlJdbcOperations());
                }
            }
        }
        return animalDao;
    }

    public TaskDao getTaskDao() {
        if (taskDao == null) {
            synchronized (lock) {
                if (taskDao == null) {
                    taskDao = new MysqlTaskDao(getMysqlJdbcOperations());
                }
            }
        }
        return taskDao;
    }

    public EnclosureDao getEnclosureDao() {
        if (enclosureDao == null) {
            synchronized (lock) {
                if (enclosureDao == null) {
                    enclosureDao = new MysqlEnclosureDao(getMysqlJdbcOperations());
                }
            }
        }
        return enclosureDao;
    }

    public TicketDao getTicketDao() {
        if (ticketDao == null) {
            synchronized (lock) {
                if (ticketDao == null) {
                    ticketDao = new MysqlTicketDao(getMysqlJdbcOperations());
                }
            }
        }
        return ticketDao;
    }

    public AuthDao getAuthDao() {
        if (authDao == null) {
            synchronized (lock) {
                if (authDao == null) {
                    authDao = new MysqlAuthDao(getMysqlJdbcOperations());
                }
            }
        }
        return authDao;
    }

}
