package com.matchingengine.test;

import com.matchingengine.core.Order;
import com.matchingengine.core.OrderStrat;
import com.matchingengine.core.OrderType;
import com.matchingengine.matching.MatchingEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MatchingEngineTest {
    private MatchingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new MatchingEngine();
    }

    @Test
    public void testLimitBuyOrder(){
        Order sellOrder = new Order("order1", 100, 10, OrderType.SELL, OrderStrat.Limit, "AAPL");
        engine.processOrder(sellOrder);

        Assertions.assertEquals(sellOrder.getID(), engine.getOrderBook().getTopSellOrder("AAPL").getID());

        Order buyOrder = new Order("order2", 100, 5, OrderType.BUY, OrderStrat.Limit, "AAPL");
        engine.processOrder(buyOrder);

        Assertions.assertEquals(5, sellOrder.getQuantity());
        Assertions.assertEquals(0, buyOrder.getQuantity());
    }

    @Test
    public void testLimitSellOrder(){
        Order buyOrder = new Order("order1", 100, 10, OrderType.BUY, OrderStrat.Limit, "AAPL");
        engine.processOrder(buyOrder);

        Assertions.assertEquals(10, engine.getOrderBook().getTopBuyOrder("AAPL").getQuantity());

        Order sellOrder = new Order("order2", 90, 5, OrderType.SELL, OrderStrat.Limit, "AAPL");
        engine.processOrder(sellOrder);

        Assertions.assertEquals(5, buyOrder.getQuantity());
        Assertions.assertEquals(0, sellOrder.getQuantity());
    }
}
