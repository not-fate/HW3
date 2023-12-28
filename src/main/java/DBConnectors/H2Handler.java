package DBConnectors;

import java.io.IOException;

public class H2Handler extends DatabaseHandler{
    public H2Handler() throws IOException {
        super("/h2.properties");
    }
}
