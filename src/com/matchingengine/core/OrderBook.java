package com.matchingengine.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class OrderBook {
    //seperate queue for each prodcut, and we have seperate ones for buy and sell
    private Map<String, PriorityQueue<Order>> buyOrdersByProduct;
    private Map<String, PriorityQueue<Order>> sellOrdersByProduct;

    public OrderBook() {
        buyOrdersByProduct = new HashMap<>();
        sellOrdersByProduct = new HashMap<>();
    }

    public void addOrder(Order order){
        if (order.getOrderType() == OrderType.BUY) {
            //if doesnt exist we need to make a new priority queue
            buyOrdersByProduct.computeIfAbsent(order.getProduct(), k -> new PriorityQueue<Order>(
                    (o1, o2) -> Double.compare(o2.getPrice(), o2.getPrice())
            ));
            buyOrdersByProduct.get(order.getProduct()).add(order);
        }
        else if (order.getOrderType() == OrderType.SELL) {
            sellOrdersByProduct.computeIfAbsent(order.getProduct(), k -> new PriorityQueue<Order>(
                    (o1, o2) -> Double.compare(o1.getPrice(), o2.getPrice())
            ));
        }
    }

    public Order getTopBuyOrder(String product){
        return buyOrdersByProduct.getOrDefault(product, new PriorityQueue<>()).peek();
    }

    public Order getTopSellOrder(String product){
        return sellOrdersByProduct.getOrDefault(product, new PriorityQueue<>()).peek();
    }

    public Order removeTopBuyOrder(String product){
        return buyOrdersByProduct.get(product) != null ? buyOrdersByProduct.get(product).poll() : null;
    }

    public Order removeTopSellOrder(String product){
        return sellOrdersByProduct.get(product) != null ? sellOrdersByProduct.get(product).poll() : null;
    }
}
