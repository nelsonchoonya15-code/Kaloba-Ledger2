
package kaloba;

import io.javalin.Javalin;
import io.javalin.http.Context;
import kaloba.model.Ledger;
import kaloba.service.LedgerService;
import kaloba.storage.Database;
import kaloba.web.ApiRoutes;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    private static final String PUBLIC_RESOURCE_PATH = "/kaloba/model/resources/public/";

    public static void main(String[] args) {
        LedgerService service = new LedgerService(new Ledger(), new Database("kaloba.db"));
        Javalin app = Javalin.create();
        new ApiRoutes(service).register(app);
        app.get("/", ctx -> servePublicFile(ctx, "index.html", "text/html; charset=utf-8"));
        app.get("/app.js", ctx -> servePublicFile(ctx, "app.js", "text/javascript; charset=utf-8"));
        app.get("/style.css", ctx -> servePublicFile(ctx, "style.css", "text/css; charset=utf-8"));
        app.start(7000);
    }

    private static void servePublicFile(Context ctx, String fileName, String contentType) throws IOException {
        try (InputStream resource = Main.class.getResourceAsStream(PUBLIC_RESOURCE_PATH + fileName)) {
            if (resource == null) {
                throw new IllegalStateException("Public resource not found: " + fileName);
            }
            ctx.contentType(contentType).result(resource.readAllBytes());
        }
    }
}
