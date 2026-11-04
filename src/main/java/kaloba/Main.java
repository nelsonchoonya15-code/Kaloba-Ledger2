package kaloba;

import io.javalin.Javalin;
import kaloba.model.Ledger;
import kaloba.service.LedgerService;
import kaloba.storage.Database;
import kaloba.web.ApiRoutes;

public class Main {

    public static void main(String[] args) {
        Database database = new Database("kaloba.db");
        Ledger ledger = new Ledger();
        LedgerService service = new LedgerService(ledger, database);
        Javalin app = Javalin.create(config -> config.staticFiles.add("/public"));
        new ApiRoutes(service).register(app);
        app.start(7070);

        System.out.println("Kaloba Ledger is running: http://localhost:7070");
    }
}
