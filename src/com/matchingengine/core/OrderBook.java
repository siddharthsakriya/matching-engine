package com.matchingengine.core;

import java.util.*;

public class OrderBook {

    private PriorityQueue<Double> buyOrders;
    private HashMap<Double, Queue<Order>> buyPriceLevels;
    private PriorityQueue<Double> sellOrders;
    private HashMap<Double, Queue<Order>> sellPriceLevels;

    public OrderBook() {
        sellOrders = new PriorityQueue<>();
        buyOrders = new PriorityQueue<>(Collections.reverseOrder());
        buyPriceLevels = new HashMap<>();
        sellPriceLevels = new HashMap<>();
    }

    public void addOrder(Order order){
        if (order.getOrderType() == OrderType.BUY) {
            if(buyPriceLevels.containsKey(order.getPrice())) {
                buyPriceLevels.get(order.getPrice()).add(order);
            }
            else{
                buyOrders.add(order.getPrice());
                buyPriceLevels.put(order.getPrice(), new LinkedList<>());
                buyPriceLevels.get(order.getPrice()).add(order);
            }
        }

        else if(order.getOrderType() == OrderType.SELL) {
            if(sellPriceLevels.containsKey(order.getPrice())) {
                sellPriceLevels.get(order.getPrice()).add(order);
            }
            else{
                sellOrders.add(order.getPrice());
                sellPriceLevels.put(order.getPrice(), new LinkedList<>());
                sellPriceLevels.get(order.getPrice()).add(order);
            }
        }
    }

    public boolean isSellEmpty(){
        return sellOrders.isEmpty();
    }

    public boolean isBuyEmpty(){
        return buyOrders.isEmpty();
    }

    public double getTopSellPrice(){
        if (!sellOrders.isEmpty()){
            return sellOrders.peek();
        }
        return -1;
    }

    public double getTopBuyPrice(){
        if (!buyOrders.isEmpty()){
            return buyOrders.peek();
        }
        return -1;
    }

    public Order getTopSellOrder(double price){
        if (!sellOrders.isEmpty()){
            return sellPriceLevels.get(price).peek();
        }
        return null;
    }

    public Order getTopBuyOrder(double price){
        if (!buyOrders.isEmpty()){
            return buyPriceLevels.get(price).peek();
        }
        return null;
    }

    public void removeTopSellOrder(double price){
        sellPriceLevels.get(price).poll();
        if(sellPriceLevels.get(price).isEmpty()){
            sellOrders.poll();
            sellPriceLevels.remove(price);
        }
    }

    public void removeTopBuyOrder(double price){
        buyPriceLevels.get(price).poll();
        if(buyPriceLevels.get(price).isEmpty()){
            buyOrders.poll();
            buyPriceLevels.remove(price);
        }
    }

    public void cancelOrder(String orderId, OrderType orderType, double price){
        if (orderType == OrderType.BUY && buyPriceLevels.containsKey(price)) {
            buyPriceLevels.get(price).removeIf(order -> Objects.equals(order.getID(), orderId));
        }

        if (orderType == OrderType.SELL && sellPriceLevels.containsKey(price)) {
            sellPriceLevels.get(price).removeIf(order -> Objects.equals(order.getID(), orderId));
        }
    }

    public PriorityQueue<Double> getSellBook(){
        return sellOrders;
    }

    public PriorityQueue<Double> getBuyBook(){
        return buyOrders;
    }

    public HashMap<Double, Queue<Order>> getBuyPriceLevels(){
        return buyPriceLevels;
    }

    public HashMap<Double, Queue<Order>> getSellPriceLevels(){
        return sellPriceLevels;
    }


}
