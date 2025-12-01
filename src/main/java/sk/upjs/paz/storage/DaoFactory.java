package sk.upjs.paz.storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public enum DaoFactory {
    INSTANCE;
    private JdbcTemplate jdbcTemplate;
    private UserDao userDao;
    private AnimalDao animalDao;
    private EnclosureDao enclosureDao;
    private TaskDao taskDao;
    private TicketDao ticketDao;


    public UserDao getUserDao() {
        if (userDao == null) {
            userDao = new MysqlUserDao(getJdbcTemplate());
        }
        return userDao;
    }

    public AnimalDao getAnimalDao() {
        if (animalDao == null) {
            animalDao = new MysqlAnimalDao(
                    getJdbcTemplate(),
                    getUserDao()
            );
        }
        return animalDao;
    }

    public EnclosureDao getEnclosureDao() {
        if (enclosureDao == null) {
            enclosureDao = new MysqlEnclosureDao(
                    getJdbcTemplate(),
                    getAnimalDao()
            );
        }
        return enclosureDao;
    }

    public TaskDao getTaskDao() {
        if (taskDao == null) {
            taskDao = new MysqlTasklDao(
                    getJdbcTemplate(),
                    getUserDao()
            );
        }
        return taskDao;
    }

    public TicketDao getTicketDao() {
        if (ticketDao == null) {
            ticketDao = new MysqlTicketDao(
                    getJdbcTemplate(),
                    getUserDao()
            );
        }
        return ticketDao;
    }

    private JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            MysqlDataSource datasource = new MysqlDataSource();
            datasource.setUrl("jdbc:mysql://localhost:3306/zoopervisor");
            datasource.setUser("zoopervisor");
            datasource.setPassword("zoopervisor");
            jdbcTemplate = new JdbcTemplate(datasource);
        }
        return jdbcTemplate;
    }
}
