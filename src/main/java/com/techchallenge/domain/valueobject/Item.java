package com.techchallenge.domain.valueobject;

import java.math.BigDecimal;

public class Item {
    private String skuNumber;
    private String category;
    private String title;
    private String description;
    private BigDecimal unitPrice;
    private Integer quantity;
    private String unitMeasure;
    private BigDecimal totalAmount;

    public Item(String skuNumber, String category, String title, String description, BigDecimal unitPrice, Integer quantity) {
        this.skuNumber = Validation.validateSkuNumber(skuNumber);
        this.category = Validation.validateCategory(category);
        this.title = Validation.validateTitle(title);
        this.description = Validation.validateDescription(description);
        this.unitPrice = Validation.validateUnitPrice(unitPrice);
        this.quantity = Validation.validateQuantity(quantity);
        this.unitMeasure = "unit";
        this.totalAmount = unitPrice;
    }

    public String getSkuNumber() {
        return skuNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getUnitMeasure() {
        return unitMeasure;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
