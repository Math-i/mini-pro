package pf;


import java.sql.*;
import java.util.*;
import java.sql.Date;

abstract class Transaction {
    int id;
    String name;
    double amount;
    Date date;

    public Transaction(int id, String name, double amount, Date date) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.date = date;
    }

    public abstract void insertDetails(Connection con) throws SQLException;
    public abstract void displayDetails();
    public abstract void updateDetails(Connection con) throws SQLException;
}

class Expense extends Transaction {
    String category;

    public Expense(int id, String name, double amount, Date date, String category) {
        super(id, name, amount, date);
        this.category = category;
    }

    @Override
    public void insertDetails(Connection con) throws SQLException {
        String query = "INSERT INTO expenses VALUES(?,?,?,?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);
        st.setString(2, name);
        st.setDouble(3, amount);
        st.setDate(4, date);
        st.setString(5, category);
        st.execute();
    }

    @Override
    public void displayDetails() {
        System.out.println("Expense Id : " + id);
        System.out.println("Expense Name : " + name);
        System.out.println("Expense Amount : " + amount);
        System.out.println("Expense Date : " + date);
        System.out.println("Expense Category : " + category);
    }

    @Override
    public void updateDetails(Connection con) throws SQLException {
        String query = "UPDATE expenses SET name=?, amount=?, date=?, category=? WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, name);
        st.setDouble(2, amount);
        st.setDate(3, date);
        st.setString(4, category);
        st.setInt(5, id);
        st.executeUpdate();
    }
}

class Income extends Transaction {
    String source;

    public Income(int id, String name, double amount, Date date, String source) {
        super(id, name, amount, date);
        this.source = source;
    }

    @Override
    public void insertDetails(Connection con) throws SQLException {
        String query = "INSERT INTO incomes VALUES(?,?,?,?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setInt(1, id);
        st.setString(2, name);
        st.setDouble(3, amount);
        st.setDate(4, date);
        st.setString(5, source);
        st.execute();
    }

    @Override
    public void displayDetails() {
        System.out.println("Income Id : " + id);
        System.out.println("Income Name : " + name);
        System.out.println("Income Amount : " + amount);
        System.out.println("Income Date : " + date);
        System.out.println("Income Source : " + source);
    }

    @Override
    public void updateDetails(Connection con) throws SQLException {
        String query = "UPDATE incomes SET name=?, amount=?, date=?, source=? WHERE id=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, name);
        st.setDouble(2, amount);
        st.setDate(3, date);
        st.setString(4, source);
        st.setInt(5, id);
        st.executeUpdate();
    }
}

public class PersonalFinanceManager {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost/finance_manager";
        String user = "root";
        String pass = "root@123456";
        Connection con = DriverManager.getConnection(url, user, pass);
        Scanner sc = new Scanner(System.in);
        System.out.println("===========================================");
        System.out.println("CHOOSE THE OPTION :");
        System.out.println("===========================================");
        System.out.println("1 => ADD EXPENSE");
        System.out.println("2 => ADD INCOME");
        System.out.println("3 => DISPLAY TRANSACTION DETAILS");
        System.out.println("4 => UPDATE TRANSACTION DETAILS");
      
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                // Add Expense
                System.out.println("Enter the expense id:");
                int expenseId = sc.nextInt();
                System.out.println("Enter the expense name:");
                String expenseName = sc.next();
                System.out.println("Enter the expense amount:");
                double expenseAmount = sc.nextDouble();
                System.out.println("Enter the expense date (YYYY-MM-DD):");
                String expenseDateStr = sc.next();
                Date expenseDate = Date.valueOf(expenseDateStr);
                System.out.println("Enter the expense category:");
                String expenseCategory = sc.next();

                Expense expense = new Expense(expenseId, expenseName, expenseAmount, expenseDate, expenseCategory);
                expense.insertDetails(con);

                System.out.println("Expense added successfully!");
                break;

            case 2:
                // Add Income
                System.out.println("Enter the income id:");
                int incomeId = sc.nextInt();
                System.out.println("Enter the income name:");
                String incomeName = sc.next();
                System.out.println("Enter the income amount:");
                double incomeAmount = sc.nextDouble();
                System.out.println("Enter the income date (YYYY-MM-DD):");
                String incomeDateStr = sc.next();
                Date incomeDate = Date.valueOf(incomeDateStr);
                System.out.println("Enter the income source:");
                String incomeSource = sc.next();

                Income income = new Income(incomeId, incomeName, incomeAmount, incomeDate, incomeSource);
                income.insertDetails(con);

                System.out.println("Income added successfully!");
                break;

            case 3:
                // Display Transaction Details
                System.out.println("Enter the transaction id:");
                int transactionId = sc.nextInt();
                System.out.println("Choose the transaction type (1 for expense, 2 for income):");
                int transactionType = sc.nextInt();

                String tableName = (transactionType == 1) ? "expenses" : "incomes";
                String queryDisplay = "SELECT * FROM " + tableName + " WHERE id=?";
                PreparedStatement stDisplay = con.prepareStatement(queryDisplay);
                stDisplay.setInt(1, transactionId);
                ResultSet rsDisplay = stDisplay.executeQuery();

                if (rsDisplay.next()) {
                    if (transactionType == 1) {
                        Expense displayedExpense = new Expense(
                                rsDisplay.getInt("id"),
                                rsDisplay.getString("name"),
                                rsDisplay.getDouble("amount"),
                                rsDisplay.getDate("date"),
                                rsDisplay.getString("category")
                        );
                        displayedExpense.displayDetails();
                    } else if (transactionType == 2) {
                        Income displayedIncome = new Income(
                                rsDisplay.getInt("id"),
                                rsDisplay.getString("name"),
                                rsDisplay.getDouble("amount"),
                                rsDisplay.getDate("date"),
                                rsDisplay.getString("source")
                        );
                        displayedIncome.displayDetails();
                    }
                } else {
                    System.out.println("No transaction found with the given ID.");
                }
                rsDisplay.close();
                stDisplay.close();
                break;

            case 4:
                // Update Transaction Details
                System.out.println("Enter the transaction id you want to update:");
                int updateId = sc.nextInt();
                System.out.println("Choose the transaction type (1 for expense, 2 for income):");
                int updateType = sc.nextInt();

                String updateTableName = (updateType == 1) ? "expenses" : "incomes";
                String querySelect = "SELECT * FROM " + updateTableName + " WHERE id=?";
                PreparedStatement stSelect = con.prepareStatement(querySelect,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                stSelect.setInt(1, updateId);
                ResultSet rs = stSelect.executeQuery();

                if (rs.next()) {
                    if (updateType == 1) {
                        Expense updatedExpense = new Expense(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("amount"),
                                rs.getDate("date"),
                                rs.getString("category")
                        );

                        System.out.println("CURRENT DETAILS:");
                        updatedExpense.displayDetails();

                        System.out.println("Enter the new name (or enter '-' to keep the current value):");
                        String newName = sc.next();
                        if (!newName.equals("-")) {
                            updatedExpense.name = newName;
                        }

                        System.out.println("Enter the new amount (or enter -1 to keep the current value):");
                        double newAmount = sc.nextDouble();
                        if (newAmount != -1) {
                            updatedExpense.amount = newAmount;
                        }

                        System.out.println("Enter the new date (YYYY-MM-DD) (or enter '-' to keep the current value):");
                        String newDateStr = sc.next();
                        if (!newDateStr.equals("-")) {
                            Date newDate = Date.valueOf(newDateStr);
                            updatedExpense.date = newDate;
                        }

                        System.out.println("Enter the new category (or enter '-' to keep the current value):");
                        String newCategory = sc.next();
                        if (!newCategory.equals("-")) {
                            updatedExpense.category = newCategory;
                        }

                        updatedExpense.updateDetails(con);
                        System.out.println("Data updated!");
                    } else if (updateType == 2) {
                        Income updatedIncome = new Income(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getDouble("amount"),
                                rs.getDate("date"),
                                rs.getString("source")
                        );

                        System.out.println("CURRENT DETAILS:");
                        updatedIncome.displayDetails();

                        System.out.println("Enter the new name (or enter '-' to keep the current value):");
                        String newName = sc.next();
                        if (!newName.equals("-")) {
                            updatedIncome.name = newName;
                        }

                        System.out.println("Enter the new amount (or enter -1 to keep the current value):");
                        double newAmount = sc.nextDouble();
                        if (newAmount != -1) {
                            updatedIncome.amount = newAmount;
                        }

                        System.out.println("Enter the new date (YYYY-MM-DD) (or enter '-' to keep the current value):");
                        String newDateStr = sc.next();
                        if (!newDateStr.equals("-")) {
                            Date newDate = Date.valueOf(newDateStr);
                            updatedIncome.date = newDate;
                        }

                        System.out.println("Enter the new source (or enter '-' to keep the current value):");
                        String newSource = sc.next();
                        if (!newSource.equals("-")) {
                            updatedIncome.source = newSource;
                        }

                        updatedIncome.updateDetails(con);
                        System.out.println("Data updated!");
                    }
                } else {
                    System.out.println("Invalid ID. No records found for the given ID.");
                }
                rs.close();
                stSelect.close();
                break;

            
            default:
                System.out.println("INVALID OPTION!");
        }

        sc.close();
        con.close();
    }
}