package com.matchingengine.core;

public class Order {
    private String ID;
    private double price;
    private int quantity;
    private OrderType orderType;
    private OrderStrat orderStrat;
    private String product;

    public Order(String ID, double price, int quantity, OrderType orderType, OrderStrat orderStrat, String product) {
        this.ID = ID;
        this.price = price;
        this.quantity = quantity;
        this.orderType = orderType;
        this.orderStrat = orderStrat;
    }

    public String getID() {
        return ID;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public OrderStrat getOrderStrat() {
        return orderStrat;
    }

    public String getProduct() {
        return product;
    }

}
