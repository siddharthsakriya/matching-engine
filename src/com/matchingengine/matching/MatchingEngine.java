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
        //handle buy order logic
        if (order.getOrderType() == OrderType.BUY){
            //retrieve top order for product of order from queue
            Order topSellOrder = orderBook.getTopSellOrder(order.getProduct());
            //check if the price is equal to or less than what they are willing to buy for
            while (order.getQuantity() > 0 && topSellOrder != null){
                if (order.getPrice() < topSellOrder.getPrice()){
                    orderBook.addOrder(order);
                    return;
                }
                else {
                    //see if we need to remove the order from the book or not
                    int remainingQuantity = topSellOrder.getQuantity() - order.getQuantity();
                    if (remainingQuantity >= 0){
                        //check if this updates the one in the queue
                        topSellOrder.setQuantity(remainingQuantity);
                        orderBook.removeTopSellOrder(order.getProduct());
                        order.setQuantity(0);
                        if (topSellOrder.getQuantity() > 0){
                            orderBook.addOrder(topSellOrder);
                        }
                        break;
                    }
                    else {
                        orderBook.removeTopSellOrder(order.getProduct());
                        order.setQuantity(order.getQuantity() - topSellOrder.getQuantity());
                        topSellOrder = orderBook.getTopSellOrder(order.getProduct());
                    }
                }
            }
            if (order.getQuantity() > 0){
                orderBook.addOrder(order);
            }
        }

        else if (order.getOrderType() == OrderType.SELL){
            Order topBuyOrder = orderBook.getTopBuyOrder(order.getProduct());

            while (order.getQuantity() > 0 && topBuyOrder != null){
                if (order.getPrice() > topBuyOrder.getPrice()){
                    orderBook.addOrder(order);
                    return;
                }
                else {
                    int remainingQuantity = topBuyOrder.getQuantity() - order.getQuantity();
                    if (remainingQuantity >= 0){
                        topBuyOrder.setQuantity(remainingQuantity);
                        order.setQuantity(0);
                        orderBook.removeTopBuyOrder(order.getProduct());
                        if (topBuyOrder.getQuantity() > 0){
                            orderBook.addOrder(topBuyOrder);
                        }
                        break;
                    }
                    else {
                        orderBook.removeTopBuyOrder(order.getProduct());
                        order.setQuantity(order.getQuantity() - topBuyOrder.getQuantity());
                        topBuyOrder = orderBook.getTopBuyOrder(order.getProduct());
                    }
                }
            }
            if (order.getQuantity() > 0){
                orderBook.addOrder(order);
            }
        }
    }

    private void processMarketOrder(Order order){
        if (order.getOrderType() == OrderType.BUY){
            Order topSellOrder = orderBook.getTopSellOrder(order.getProduct());
            while (order.getQuantity() > 0 && topSellOrder != null){
                int remainingQuantity = topSellOrder.getQuantity() - order.getQuantity();
                if (remainingQuantity >= 0){
                    topSellOrder.setQuantity(remainingQuantity);
                    orderBook.removeTopSellOrder(order.getProduct());
                    order.setQuantity(0);
                    if (topSellOrder.getQuantity() > 0){
                        orderBook.addOrder(topSellOrder);
                    }
                    break;
                }
                else {
                    orderBook.removeTopSellOrder(order.getProduct());
                    order.setQuantity(order.getQuantity() - topSellOrder.getQuantity());
                    topSellOrder = orderBook.getTopSellOrder(order.getProduct());
                }
            }
        }

        else if (order.getOrderType() == OrderType.SELL){
            Order topBuyOrder = orderBook.getTopBuyOrder(order.getProduct());
            while (order.getQuantity() > 0 && topBuyOrder != null){
                int remainingQuantity = topBuyOrder.getQuantity() - order.getQuantity();
                if (remainingQuantity >= 0){
                    topBuyOrder.setQuantity(remainingQuantity);
                    orderBook.removeTopBuyOrder(order.getProduct());
                    order.setQuantity(0);
                    if (topBuyOrder.getQuantity() > 0){
                        orderBook.addOrder(topBuyOrder);
                    }
                    break;
                }
                else {
                    orderBook.removeTopBuyOrder(order.getProduct());
                    order.setQuantity(order.getQuantity() - topBuyOrder.getQuantity());
                    topBuyOrder = orderBook.getTopBuyOrder(order.getProduct());
                }
            }
        }
    }
}
