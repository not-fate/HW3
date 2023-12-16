package DBConnectors;

import java.io.IOException;

public class PostgresHandler extends DatabaseHandler {
    public PostgresHandler() throws IOException {
        super("/postgres_hibernate.properties");
    }
}
