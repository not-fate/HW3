package DBConnectors;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public abstract class DatabaseHandler {
    protected Properties properties = new Properties();
    protected SessionFactory sessionFactory;

    public DatabaseHandler(String path) throws IOException {
        InputStream input = getClass().getResourceAsStream(path);
        properties.load(input);
        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Row.class)
                .buildSessionFactory();
    }

    public List<Row> load() {
        try (var session = sessionFactory.openSession()) {
            Query<Row> query = session.createQuery("from DBConnectors.Row", DBConnectors.Row.class);
            return query.list();
        }
    }

    public void upload(List<Row> rows) {
        try (var session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.createSQLQuery("truncate table Trees").executeUpdate();
                rows.forEach(session::saveOrUpdate);
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }
}
