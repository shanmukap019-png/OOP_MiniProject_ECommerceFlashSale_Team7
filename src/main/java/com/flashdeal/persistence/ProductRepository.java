package com.flashdeal.persistence;

import com.flashdeal.model.Product;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Repository responsible for loading, storing, and persisting Product inventory data in products.csv.
 * Maintains an in-memory HashMap and ArrayList for rapid search and retrieval.
 */
public class ProductRepository {
    private static final String FILE_NAME = "products.csv";
    private static final String CSV_HEADER = "productId,name,description,category,originalPrice,flashSalePrice,initialStock,availableStock,status";

    private final FileStorageService storageService;
    private final Map<String, Product> productMap;
    private final List<Product> productList;
    private final Object repoLock = new Object();

    public ProductRepository(FileStorageService storageService) {
        this.storageService = storageService;
        this.productMap = new LinkedHashMap<>();
        this.productList = new ArrayList<>();
        loadOrCreateProducts();
    }

    private void loadOrCreateProducts() {
        synchronized (repoLock) {
            productMap.clear();
            productList.clear();

            try {
                List<String> lines = storageService.readLines(FILE_NAME);
                if (lines.isEmpty() || (lines.size() == 1 && lines.get(0).startsWith("productId"))) {
                    // File is missing or only contains headers -> seed with sample products
                    seedInitialProducts();
                    saveAll();
                } else {
                    // Parse existing lines
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (i == 0 && line.toLowerCase().startsWith("productid")) {
                            continue; // skip header
                        }
                        Product p = parseProductFromCsv(line);
                        if (p != null) {
                            productMap.put(p.getProductId(), p);
                            productList.add(p);
                        }
                    }
                    if (productList.isEmpty()) {
                        seedInitialProducts();
                        saveAll();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error reading products.csv, falling back to default seed: " + e.getMessage());
                seedInitialProducts();
            }
        }
    }

    private void seedInitialProducts() {
        List<Product> sample = Arrays.asList(
                new Product("PROD-101", "OnePlus Nord Buds 2 TWS Earbuds", "Active Noise Cancellation 25dB, 12.4mm Dynamic Drivers", "Electronics",
                        new BigDecimal("2999.00"), new BigDecimal("1499.00"), 25),
                new Product("PROD-102", "boAt Rockerz 450 Bluetooth Headphones", "40mm drivers, 15 hours playtime, lightweight ergonomic design", "Electronics",
                        new BigDecimal("3990.00"), new BigDecimal("1299.00"), 15),
                new Product("PROD-103", "Noise ColorFit Pulse 3 Smartwatch", "1.96-inch TFT display, Bluetooth calling, 100 sports modes", "Wearables",
                        new BigDecimal("4999.00"), new BigDecimal("1799.00"), 20),
                new Product("PROD-104", "Xiaomi 20000mAh Power Bank 3i", "18W fast charging, triple output ports, dual input micro-USB/Type-C", "Accessories",
                        new BigDecimal("2199.00"), new BigDecimal("1199.00"), 30),
                new Product("PROD-105", "SanDisk Ultra 128GB MicroSDXC Card", "Up to 140MB/s read speed, Class 10, A1 App Performance", "Storage",
                        new BigDecimal("1800.00"), new BigDecimal("749.00"), 40),
                new Product("PROD-106", "Logitech B170 Wireless Optical Mouse", "Reliable 2.4GHz wireless connection, 12-month battery life", "Computer Accessories",
                        new BigDecimal("895.00"), new BigDecimal("499.00"), 50),
                new Product("PROD-107", "Portronics Ruffpad 8.5E LCD Notepad", "Environment-friendly re-writable digital notepad with stylus", "Gadgets",
                        new BigDecimal("999.00"), new BigDecimal("299.00"), 35),
                new Product("PROD-108", "Realme 65W SuperDart Fast Charger", "Type-C flash charging adapter, multi-layer safety protection", "Mobile Accessories",
                        new BigDecimal("2499.00"), new BigDecimal("1099.00"), 18)
        );

        for (Product p : sample) {
            productMap.put(p.getProductId(), p);
            productList.add(p);
        }
    }

    private Product parseProductFromCsv(String line) {
        try {
            List<String> tokens = FileStorageService.parseCsvLine(line);
            if (tokens.size() < 8) return null;

            String id = tokens.get(0);
            String name = tokens.get(1);
            String desc = tokens.get(2);
            String category = tokens.get(3);
            BigDecimal origPrice = new BigDecimal(tokens.get(4));
            BigDecimal salePrice = new BigDecimal(tokens.get(5));
            int initialStock = Integer.parseInt(tokens.get(6));
            int availableStock = Integer.parseInt(tokens.get(7));

            Product p = new Product(id, name, desc, category, origPrice, salePrice, initialStock);
            p.setAvailableStock(availableStock);
            return p;
        } catch (Exception e) {
            System.err.println("Skipping malformed product line: " + line + " (" + e.getMessage() + ")");
            return null;
        }
    }

    public List<Product> getAllProducts() {
        synchronized (repoLock) {
            return new ArrayList<>(productList);
        }
    }

    public Product findById(String productId) {
        synchronized (repoLock) {
            return productMap.get(productId);
        }
    }

    public void updateStock(String productId, int newStock) {
        synchronized (repoLock) {
            Product p = productMap.get(productId);
            if (p != null) {
                p.setAvailableStock(newStock);
                saveAll();
            }
        }
    }

    public void saveOrUpdate(Product product) {
        synchronized (repoLock) {
            if (product == null) return;
            if (productMap.containsKey(product.getProductId())) {
                // Update
                for (int i = 0; i < productList.size(); i++) {
                    if (productList.get(i).getProductId().equals(product.getProductId())) {
                        productList.set(i, product);
                        break;
                    }
                }
            } else {
                // Insert
                productList.add(product);
            }
            productMap.put(product.getProductId(), product);
            saveAll();
        }
    }

    public void saveAll() {
        synchronized (repoLock) {
            List<String> lines = new ArrayList<>();
            for (Product p : productList) {
                String line = String.format("%s,%s,%s,%s,%.2f,%.2f,%d,%d,%s",
                        FileStorageService.escapeCsvField(p.getProductId()),
                        FileStorageService.escapeCsvField(p.getName()),
                        FileStorageService.escapeCsvField(p.getDescription()),
                        FileStorageService.escapeCsvField(p.getCategory()),
                        p.getOriginalPrice(),
                        p.getFlashSalePrice(),
                        p.getInitialStock(),
                        p.getAvailableStock(),
                        p.getStatus()
                );
                lines.add(line);
            }
            try {
                storageService.writeLines(FILE_NAME, CSV_HEADER, lines);
            } catch (IOException e) {
                System.err.println("Failed to persist products.csv: " + e.getMessage());
            }
        }
    }

    public List<Product> search(String query, String category) {
        synchronized (repoLock) {
            List<Product> results = new ArrayList<>();
            String q = (query != null) ? query.trim().toLowerCase() : "";
            String cat = (category != null && !category.equalsIgnoreCase("ALL")) ? category.trim() : null;

            for (Product p : productList) {
                boolean matchesCategory = (cat == null || p.getCategory().equalsIgnoreCase(cat));
                boolean matchesQuery = q.isEmpty() ||
                        p.getName().toLowerCase().contains(q) ||
                        p.getProductId().toLowerCase().contains(q) ||
                        p.getDescription().toLowerCase().contains(q);

                if (matchesCategory && matchesQuery) {
                    results.add(p);
                }
            }
            return results;
        }
    }
}
