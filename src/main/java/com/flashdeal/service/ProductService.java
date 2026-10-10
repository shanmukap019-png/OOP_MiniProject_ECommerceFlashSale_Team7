package com.flashdeal.service;

import com.flashdeal.inventory.InventoryService;
import com.flashdeal.model.Product;
import com.flashdeal.persistence.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service managing product catalog, searching, updates, and stock synchronization.
 */
public class ProductService {
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public ProductService(ProductRepository productRepository, InventoryService inventoryService) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        initializeInventoryStock();
    }

    /**
     * Synchronizes thread-safe Inventory with persisted products from repository.
     */
    public void initializeInventoryStock() {
        for (Product product : productRepository.getAllProducts()) {
            inventoryService.setStock(product.getProductId(), product.getAvailableStock());
        }
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllProducts();
        // Sync live available stock from inventory
        for (Product p : products) {
            int liveStock = inventoryService.getAvailableStock(p.getProductId());
            p.setAvailableStock(liveStock);
        }
        return products;
    }

    public Product getProductById(String productId) {
        Product p = productRepository.findById(productId);
        if (p != null) {
            p.setAvailableStock(inventoryService.getAvailableStock(p.getProductId()));
        }
        return p;
    }

    public void updateStock(String productId, int newStock) {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        inventoryService.setStock(productId, newStock);
        productRepository.updateStock(productId, newStock);
    }

    public void addProduct(String id, String name, String desc, String category,
                           BigDecimal origPrice, BigDecimal salePrice, int stock) {
        Product product = new Product(id, name, desc, category, origPrice, salePrice, stock);
        productRepository.saveOrUpdate(product);
        inventoryService.setStock(id, stock);
    }

    public List<Product> searchProducts(String query, String category) {
        List<Product> products = productRepository.search(query, category);
        for (Product p : products) {
            p.setAvailableStock(inventoryService.getAvailableStock(p.getProductId()));
        }
        return products;
    }

    public ProductRepository getRepository() {
        return productRepository;
    }
}
