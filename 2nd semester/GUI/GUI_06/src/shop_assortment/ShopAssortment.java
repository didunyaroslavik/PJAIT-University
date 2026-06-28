package shop_assortment;

import java.io.*;
import java.util.*;

public class ShopAssortment {

    public static void main(String[] args) {
        String inputFileName = "src/shop_assortment/shopAssortment.txt";
        String outputFileName = "src/shop_assortment/summary.txt";

        // Using a TreeMap so that the customers are automatically sorted alphabetically
        Map<String, List<Purchase>> customerPurchases = new TreeMap<>();

        // 1. Read data from the text file
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Split by spaces
                String[] parts = line.split("\\s+");
                if (parts.length >= 3) {
                    String customerName = parts[0];
                    String productName = parts[1];
                    int price = Integer.parseInt(parts[2]);

                    Purchase purchase = new Purchase(productName, price);

                    // Add purchase to the customer's list
                    customerPurchases.putIfAbsent(customerName, new ArrayList<>());
                    customerPurchases.get(customerName).add(purchase);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + inputFileName);
            return;
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        // 2. Process data and write the summary to the output file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName))) {

            for (Map.Entry<String, List<Purchase>> entry : customerPurchases.entrySet()) {
                String customerName = entry.getKey();
                List<Purchase> purchases = entry.getValue();

                int totalPurchases = purchases.size();
                int totalExpenses = 0;
                Set<String> uniqueProducts = new HashSet<>();

                // Calculate total expenses and gather unique products
                for (Purchase p : purchases) {
                    totalExpenses += p.getPrice();
                    uniqueProducts.add(p.getName());
                }

                int uniqueProductsCount = uniqueProducts.size();

                // Format: [name] [number of purchases] [number of unique products] [total expenses]
                String summaryLine = String.format("%s %d %d %d",
                        customerName, totalPurchases, uniqueProductsCount, totalExpenses);

                bw.write(summaryLine);
                bw.newLine();

                // Also printing to console to verify
                System.out.println(summaryLine);
            }

            System.out.println("\nSummary successfully written to " + outputFileName);

        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}