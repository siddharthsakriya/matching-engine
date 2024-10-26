package com.matchingengine.matching;

import com.matchingengine.core.Order;
import com.matchingengine.core.OrderBook;
import com.matchingengine.core.OrderStrat;
import com.matchingengine.core.OrderType;

public class MatchingEngine {
    private final OrderBook orderBook;

    public MatchingEngine(){
        orderBook = new OrderBook();
    }

    public void processOrder(Order order){
        if (order.getOrderStrat() == OrderStrat.Limit){
            processLimitOrder(order);
        }
        else if (order.getOrderStrat() == OrderStrat.Market) {
            processMarketOrder(order);
        }
    }

    //matching algorithm for limit orders
    private void processLimitOrder(Order order){
        if (order.getOrderType() == OrderType.BUY){

            double topSellPrice = orderBook.getTopSellPrice();
            while (order.getQuantity() > 0 && !orderBook.isSellEmpty()){

                if (order.getPrice() >= topSellPrice){
                    Order orderBeingMatched = orderBook.getTopSellOrder(topSellPrice);
                    int quantityRemaining = orderBeingMatched.getQuantity() - order.getQuantity();

                    if (quantityRemaining >= 0){
                        order.setQuantity(0);
                        //if exactly matched
                        if (quantityRemaining == 0){
                            orderBook.removeTopSellOrder(topSellPrice);
                            System.out.println("Order has been filled, and fully matched");
                        }

                        //if partially matched
                        else{
                            orderBeingMatched.setQuantity(quantityRemaining);
                            System.out.println("Order has been filled, but partially matched");
                        }

                    }
                    else{
                        orderBook.removeTopSellOrder(topSellPrice);
                        order.setQuantity(Math.abs(quantityRemaining));
                    }
                }
                else {
                    break;
                }
                topSellPrice = orderBook.getTopSellPrice();
            }
                if (order.getQuantity() > 0){
                    orderBook.addOrder(order);
            }
        }
        else if (order.getOrderType() == OrderType.SELL){

            double topBuyPrice = orderBook.getTopBuyPrice();
            while (order.getQuantity() > 0 && !orderBook.isBuyEmpty()){

                if (order.getPrice() <= topBuyPrice){
                    Order orderBeingMatched = orderBook.getTopBuyOrder(topBuyPrice);
                    int quantityRemaining = orderBeingMatched.getQuantity() - order.getQuantity();

                    if (quantityRemaining >= 0){
                        order.setQuantity(0);
                        if (quantityRemaining == 0){
                            orderBook.removeTopBuyOrder(topBuyPrice);
                            System.out.println("Order has been filled, and fully matched");
                        }
                        else {
                            orderBeingMatched.setQuantity(quantityRemaining);
                            System.out.println("Order has been filled, but partially matched");
                        }
                    }
                    else{
                        orderBook.removeTopBuyOrder(topBuyPrice);
                        order.setQuantity(Math.abs(quantityRemaining));
                    }
                }
                else {
                    break;
                }
                topBuyPrice = orderBook.getTopBuyPrice();
            }

            if (order.getQuantity() > 0){
                orderBook.addOrder(order);
            }
        }
    }

    private void processMarketOrder(Order order){
        if (order.getOrderType() == OrderType.BUY){
            double topSellPrice = orderBook.getTopSellPrice();
            while (order.getQuantity() > 0 && !orderBook.isSellEmpty()){

                Order orderBeingMatched = orderBook.getTopSellOrder(topSellPrice);
                int quantityRemaining = orderBeingMatched.getQuantity() - order.getQuantity();
                if (quantityRemaining >= 0){
                    order.setQuantity(0);
                    if (quantityRemaining == 0){
                        orderBook.removeTopSellOrder(topSellPrice);
                    }
                    else {
                        orderBeingMatched.setQuantity(quantityRemaining);
                    }
                }
                else {
                    order.setQuantity(Math.abs(quantityRemaining));
                }
                topSellPrice = orderBook.getTopSellPrice();
            }
        }

        else if (order.getOrderType() == OrderType.SELL){
            double topBuyPrice = orderBook.getTopBuyPrice();
            while (order.getQuantity() > 0 && !orderBook.isBuyEmpty()){

                Order orderBeingMatched = orderBook.getTopBuyOrder(topBuyPrice);
                int quantityRemaining = orderBeingMatched.getQuantity() - order.getQuantity();

                if (quantityRemaining >= 0){
                    order.setQuantity(0);
                    if (quantityRemaining == 0){
                        orderBook.removeTopBuyOrder(topBuyPrice);
                    }
                    else{
                        orderBeingMatched.setQuantity(quantityRemaining);
                    }
                }
                else {
                    order.setQuantity(Math.abs(quantityRemaining));
                }
                topBuyPrice = orderBook.getTopBuyPrice();
            }
        }
    }

    public OrderBook getOrderBook() {
        return orderBook;
    }
}
