package kaloba.web;

import io.javalin.Javalin;
import io.javalin.http.Context;
import kaloba.e.LedgerService;
import kaloba.exception.CreditLimitExceededException;
import kaloba.exception.CustomerNotFoundException;
import kaloba.model.CreditSale;
import kaloba.model.Customer;
import kaloba.model.Repayment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiRoutes {

    public record CustomerRequest(String name, String type) { }
    public record SaleRequest(int customerId, String description, double amount) { }
    public record RepaymentRequest(int customerId, double amount) { }

    private final LedgerService service;

    public ApiRoutes(LedgerService service) {
        this.service = service;
    }

    public void register(Javalin app) {
        app.get("/api/customers", this::listCustomers);
        app.post("/api/customers", this::registerCustomer);
        app.put("/api/customers/{id}", this::updateCustomer);
        app.delete("/api/customers/{id}", this::deleteCustomer);
        app.post("/api/sales", this::recordSale);
        app.post("/api/repayments", this::recordRepayment);

        app.exception(CreditLimitExceededException.class, (e, ctx) -> ctx.status(400).result(e.getMessage()));
        app.exception(CustomerNotFoundException.class, (e, ctx) -> ctx.status(404).result(e.getMessage()));
        app.exception(IllegalArgumentException.class, (e, ctx) -> ctx.status(400).result(e.getMessage()));
    }

    private void listCustomers(Context ctx) {
        boolean onlyOwing = "true".equals(ctx.queryParam("owing"));
        List<Customer> customers = onlyOwing ? service.getCustomersWhoOwe() : service.getAllCustomers();

        List<Map<String, Object>> result = new ArrayList<>();
        for (Customer customer : customers) {
            result.add(customerToMap(customer));
        }
        ctx.json(result);
    }

    private void registerCustomer(Context ctx) {
        CustomerRequest request = ctx.bodyAsClass(CustomerRequest.class);
        Customer customer = service.registerCustomer(request.name(), request.type());
        ctx.status(201).json(customerToMap(customer));
    }

    private void updateCustomer(Context ctx) throws CustomerNotFoundException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        CustomerRequest request = ctx.bodyAsClass(CustomerRequest.class);
        Customer customer = service.updateCustomer(id, request.name(), request.type());
        ctx.json(customerToMap(customer));
    }

    private void deleteCustomer(Context ctx) throws CustomerNotFoundException {
        int id = ctx.pathParamAsClass("id", Integer.class).get();
        service.deleteCustomer(id);
        ctx.status(204);   // 204 = done, nothing to send back
    }

    private void recordSale(Context ctx) throws CustomerNotFoundException, CreditLimitExceededException {
        SaleRequest request = ctx.bodyAsClass(SaleRequest.class);
        CreditSale sale = service.recordSale(request.customerId(), request.description(), request.amount());

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", sale.getId());
        json.put("customerId", sale.getCustomerId());
        json.put("description", sale.getDescription());
        json.put("amount", sale.getAmount());
        json.put("date", sale.getDate().toString());
        json.put("newBalance", service.getBalance(sale.getCustomerId()));
        ctx.status(201).json(json);
    }

    private void recordRepayment(Context ctx) throws CustomerNotFoundException {
        RepaymentRequest request = ctx.bodyAsClass(RepaymentRequest.class);
        Repayment repayment = service.recordRepayment(request.customerId(), request.amount());

        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", repayment.getId());
        json.put("customerId", repayment.getCustomerId());
        json.put("amount", repayment.getAmount());
        json.put("date", repayment.getDate().toString());
        json.put("newBalance", service.getBalance(repayment.getCustomerId()));
        ctx.status(201).json(json);
    }

    /** Builds the JSON for one customer: what the page needs to show in its table. */
    private Map<String, Object> customerToMap(Customer customer) {
        Map<String, Object> json = new LinkedHashMap<>();
        json.put("id", customer.getId());
        json.put("name", customer.getName());
        json.put("type", customer.getType());
        json.put("creditLimit", customer.getCreditLimit());
        json.put("balance", service.getBalance(customer.getId()));
        return json;
    }
}
