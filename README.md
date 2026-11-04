NELSON CHOONYA 2410058
MULENGA CHIKONDE 2410210
ENELIKO MWANZA 2410382
KENNEDY MWALE 2410207
EUNICE NKONDE 2410330

PROBLEM STATEMENT
Shop owners in Zambia record customer debts (kaloba) in exercise books, which can easily be lost, damaged or become difficult to manage. This often leads to inaccurate records, disputes over outstanding debts and loss of income.

Classes
Customer-Parent class containing customer details and a credit-limit method. RegularCustomer and WholesaleCustomer will extend it.

CreditSale-Stores details of a credit sale, including customer, description/items, amount and date.

Repayment-Stores a repayment amount, customer and date.

Ledger-Keeps the customer, sale and repayment collections and calculates outstanding balances.

LedgerService-Applies the business rules, validates sales and repayments, checks credit limits and identifies customers who owe money.

 HOW TO RUN 
 To run our project, the lecturer should open the project folder in a terminal that has Java 17 and Maven installed, run mvn clean compile to build the project using the instructions in the pom.xml, then run mvn exec:java to start the application through the exec-maven-plugin which launches our main class com.kaloba.ledger.LedgerApplication, and finally open a browser to http://localhost:7070 to view the Kaloba Ledger website.
