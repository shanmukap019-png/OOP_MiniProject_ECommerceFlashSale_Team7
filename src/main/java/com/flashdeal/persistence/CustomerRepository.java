package com.flashdeal.persistence;

import com.flashdeal.model.Customer;

import java.io.IOException;
import java.util.*;

/**
 * Repository providing customer entities for flash sale simulation.
 * Persists customers to data/customers.csv and pre-populates realistic Indian names.
 */
public class CustomerRepository {
    private static final String FILE_NAME = "customers.csv";
    private static final String CSV_HEADER = "customerId,name,email,phone";

    private final FileStorageService storageService;
    private final List<Customer> customerList;

    public CustomerRepository(FileStorageService storageService) {
        this.storageService = storageService;
        this.customerList = new ArrayList<>();
        loadOrCreateCustomers();
    }

    private void loadOrCreateCustomers() {
        customerList.clear();
        try {
            List<String> lines = storageService.readLines(FILE_NAME);
            if (lines.isEmpty() || (lines.size() == 1 && lines.get(0).startsWith("customerId"))) {
                seedCustomers();
                saveAll();
            } else {
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (i == 0 && line.toLowerCase().startsWith("customerid")) continue;
                    List<String> tokens = FileStorageService.parseCsvLine(line);
                    if (tokens.size() >= 4) {
                        customerList.add(new Customer(tokens.get(0), tokens.get(1), tokens.get(2), tokens.get(3)));
                    }
                }
                if (customerList.isEmpty()) {
                    seedCustomers();
                    saveAll();
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading customers.csv: " + e.getMessage());
            seedCustomers();
        }
    }

    private void seedCustomers() {
        String[] firstNames = {
                "Aarav", "Diya", "Rohan", "Ananya", "Vikram", "Priya", "Rajesh", "Sneha",
                "Arjun", "Kavya", "Aditya", "Neha", "Karthik", "Pooja", "Rahul", "Meera",
                "Suresh", "Ishita", "Manoj", "Divya", "Gaurav", "Swati", "Nikhil", "Tanvi",
                "Abhishek", "Ritu", "Varun", "Simran", "Deepak", "Shalini", "Pranav", "Nisha",
                "Alok", "Shruti", "Siddharth", "Aishwarya", "Vishal", "Pallavi", "Akash", "Preeti",
                "Manish", "Sonali", "Harish", "Anjali", "Sanjay", "Komal", "Amit", "Bhavna", "Sunil", "Rupa"
        };
        String[] lastNames = {
                "Sharma", "Patel", "Verma", "Iyer", "Singh", "Nair", "Kumar", "Reddy",
                "Gupta", "Deshmukh", "Chopra", "Rao", "Joshi", "Bose", "Menon", "Bhat"
        };

        for (int i = 0; i < 50; i++) {
            String fName = firstNames[i % firstNames.length];
            String lName = lastNames[(i * 3 + 1) % lastNames.length];
            String fullName = fName + " " + lName;
            String cid = String.format("CUST-%03d", i + 1);
            String email = fName.toLowerCase() + "." + lName.toLowerCase() + (i + 1) + "@gmail.com";
            String phone = String.format("+91 98%02d%05d", (i * 7) % 90 + 10, (i * 12345) % 90000 + 10000);

            customerList.add(new Customer(cid, fullName, email, phone));
        }
    }

    public synchronized void saveAll() {
        List<String> lines = new ArrayList<>();
        for (Customer c : customerList) {
            lines.add(String.format("%s,%s,%s,%s",
                    FileStorageService.escapeCsvField(c.getCustomerId()),
                    FileStorageService.escapeCsvField(c.getName()),
                    FileStorageService.escapeCsvField(c.getEmail()),
                    FileStorageService.escapeCsvField(c.getPhone())
            ));
        }
        try {
            storageService.writeLines(FILE_NAME, CSV_HEADER, lines);
        } catch (IOException e) {
            System.err.println("Failed to save customers.csv: " + e.getMessage());
        }
    }

    public synchronized List<Customer> getAllCustomers() {
        return new ArrayList<>(customerList);
    }

    public synchronized List<Customer> getBatch(int count) {
        List<Customer> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (i < customerList.size()) {
                batch.add(customerList.get(i));
            } else {
                int index = i + 1;
                batch.add(new Customer("CUST-" + index, "Buyer #" + index, "buyer" + index + "@mail.com", "+91 98000" + String.format("%05d", index)));
            }
        }
        return batch;
    }
}
