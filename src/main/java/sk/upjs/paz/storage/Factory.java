package sk.upjs.paz.storage;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;


public enum Factory {
    INSTANCE;

    // The JdbcOperations object should be created only once and reused.
    // `volatile` means that the compiler will limit some CPU cache optimizations
    // that are not safe in the context of multithreading.
    // We will not use multithreading in the scope of this exercise,
    // but it is a general practice to use volatile for instance variables of a `factory` class/enum.
    private volatile JdbcOperations jdbcOperations;
    private volatile UserDao userDao;
    private volatile AnimalDao animalDao;
    private volatile TaskDao taskDao;
    private volatile EnclosureDao enclosureDao;

    private final Object lock = new Object();

    public JdbcOperations getMysqlJdbcOperations() {
        if (jdbcOperations == null) {
            // Again, we are not using multithreading in this exercise, so technically using `synchronized` is not
            // necessary, but it is a general practice to use it in `factory` classes/enums.

            // Explanation of the following line:
            // The `synchronized` block is used to ensure that only one thread can create the JdbcOperations object.
            // This is necessary because the JdbcOperations object should be created only once and reused.
            // If we did not use `synchronized`, two threads could create two different JdbcOperations objects and save it to the same variable.
            // This would create a space-time paradox where a microscopic black hole would spawn and swallow your computer.
            // Or, more likely, it would just cause a crash of the application.
            // Also, it in many tutorials and examples you may see `synchronized (this)` but it can sometimes lead to
            // subtle and hard-to-find bugs, hence we are using some dummy instance variable.
            synchronized (lock) {
                // The reason why we use double-checking (i.e. 2x `if (jdbcOperations == null)`), both inside and outside of
                // the `synchronized`, is a common performance optimization, because synchronization is costly.
                if (jdbcOperations == null) {
                    var dataSource = new MysqlDataSource();
                    dataSource.setUrl(System.getProperty("DB_JDBC", "jdbc:mysql://localhost:3306/zoopervisor"));
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
                    taskDao = new MysqlTasklDao(getMysqlJdbcOperations());
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

}
