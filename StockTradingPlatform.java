import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class StockTradingPlatform {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<String, Stock> market = new LinkedHashMap<>();
    private static final Map<String, Integer> portfolio = new LinkedHashMap<>();
    private static final List<String> transactions = new ArrayList<>();
    private static double cash = 100_000.00;

    static class Stock {
        final String symbol;
        final String company;
        double price;

        Stock(String symbol, String company, double price) {
            this.symbol = symbol;
            this.company = company;
            this.price = price;
        }
    }

    public static void main(String[] args) {
        market.put("TCS", new Stock("TCS", "Tata Consultancy Services", 3850.00));
        market.put("INFY", new Stock("INFY", "Infosys", 1780.00));
        market.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries", 2950.00));
        market.put("HDFCBANK", new Stock("HDFCBANK", "HDFC Bank", 1680.00));

        while (true) {
            System.out.println("\\n=== Stock Trading Platform (Simulation) ===");
            System.out.println("1. View market  2. Buy shares  3. Sell shares");
            System.out.println("4. View portfolio  5. View transaction history  6. Simulate price change  7. Exit");
            int choice = readInt("Choose: ", 1, 7);
            switch (choice) {
                case 1 -> showMarket();
                case 2 -> trade(true);
                case 3 -> trade(false);
                case 4 -> showPortfolio();
                case 5 -> showTransactions();
                case 6 -> simulateMarketChange();
                case 7 -> {
                    System.out.println("Goodbye! This was a simulated trading session.");
                    scanner.close();
                    return;
                }
            }
        }
    }

    private static void showMarket() {
        System.out.printf("%-12s %-32s %12s%n", "Symbol", "Company", "Price (INR)");
        for (Stock s : market.values())
            System.out.printf("%-12s %-32s %,12.2f%n", s.symbol, s.company, s.price);
    }

    private static void trade(boolean buying) {
        showMarket();
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Unknown stock symbol.");
            return;
        }
        int quantity = readInt("Quantity: ", 1, 1_000_000);
        double total = stock.price * quantity;

        if (buying) {
            if (total > cash) {
                System.out.printf("Insufficient cash. Available: INR %.2f%n", cash);
                return;
            }
            cash -= total;
            portfolio.merge(symbol, quantity, Integer::sum);
            transactions.add(String.format("BUY  %d %s @ INR %.2f (total INR %.2f)",
                    quantity, symbol, stock.price, total));
        } else {
            int owned = portfolio.getOrDefault(symbol, 0);
            if (quantity > owned) {
                System.out.printf("You own only %d shares of %s.%n", owned, symbol);
                return;
            }
            cash += total;
            int remaining = owned - quantity;
            if (remaining == 0) portfolio.remove(symbol);
            else portfolio.put(symbol, remaining);
            transactions.add(String.format("SELL %d %s @ INR %.2f (total INR %.2f)",
                    quantity, symbol, stock.price, total));
        }
        System.out.printf("%s successful. Cash balance: INR %.2f%n",
                buying ? "Purchase" : "Sale", cash);
    }

    private static void showPortfolio() {
        double holdingsValue = 0;
        System.out.println("\\n=== Portfolio ===");
        System.out.printf("Cash balance: INR %.2f%n", cash);
        if (portfolio.isEmpty()) System.out.println("No shares currently owned.");
        for (Map.Entry<String, Integer> entry : portfolio.entrySet()) {
            Stock stock = market.get(entry.getKey());
            double value = stock.price * entry.getValue();
            holdingsValue += value;
            System.out.printf("%-12s Qty: %5d  Price: INR %10.2f  Value: INR %12.2f%n",
                    entry.getKey(), entry.getValue(), stock.price, value);
        }
        System.out.printf("Holdings value: INR %.2f%n", holdingsValue);
        System.out.printf("Total portfolio value (cash + holdings): INR %.2f%n", cash + holdingsValue);
    }

    private static void showTransactions() {
        System.out.println("\\n=== Transaction History ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }
        for (int i = 0; i < transactions.size(); i++)
            System.out.println((i + 1) + ". " + transactions.get(i));
    }

    private static void simulateMarketChange() {
        System.out.println("Enter percentage change for a stock (e.g. 2.5 or -1.0).");
        System.out.print("Stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("Unknown stock symbol.");
            return;
        }
        double percent = readDouble("Percentage (-50 to 50): ", -50, 50);
        stock.price *= 1 + percent / 100.0;
        System.out.printf("%s new simulated price: INR %.2f%n", symbol, stock.price);
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.printf("Enter a whole number between %d and %d.%n", min, max);
        }
    }

    private static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (Double.isFinite(value) && value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.printf("Enter a number between %.1f and %.1f.%n", min, max);
        }
    }
}
