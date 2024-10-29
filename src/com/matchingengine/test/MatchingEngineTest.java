package com.matchingengine.test;

import com.matchingengine.core.Order;
import com.matchingengine.core.OrderStrat;
import com.matchingengine.core.OrderType;
import com.matchingengine.matching.MatchingEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineTest {

    private MatchingEngine engine;

    @BeforeEach
    void setUp() {
        engine = new MatchingEngine();
    }

    @Test
    void testLimitBuyOrdersAddedToBook() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);
        Order buyOrder1 = new Order("2", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder1);
        Order buyOrder2 = new Order("3", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder2);
        Order buyOrder3 = new Order("4", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder3);
        Order buyOrder4 = new Order("5", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder4);

        assertEquals(5, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
    }

    @Test
    void testLimitSellOrdersAddedToBook() {
        Order sellOrder = new Order("1", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);
        Order sellOrder1 = new Order("2", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder1);
        Order sellOrder2 = new Order("3", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder2);

        assertEquals(3, engine.getOrderBook().getSellPriceLevels().get(100.0).size());
    }

    @Test
    void testBuyOrderPartiallyFilled() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);
        Order sellOrder = new Order("2", 50, 850, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);

        assertEquals(1, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
        assertFalse(engine.getOrderBook().getSellPriceLevels().containsKey(50.0));
    }

    @Test
    void testBuyOrderFullyFilled() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);
        Order sellOrder = new Order("2", 100, 900, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);

        // Assert that buy order is fully filled and removed from the book
        assertFalse(engine.getOrderBook().getBuyPriceLevels().containsKey(900.0));
        // Assert that sell order is fully filled and removed from the book
        assertFalse(engine.getOrderBook().getSellPriceLevels().containsKey(900.0));
    }

    @Test
    void testUnmatchedLimitBuyOrderAddedBackToBook() {
        Order buyOrder = new Order("1", 100, 700, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);
        Order sellOrder = new Order("2", 100, 600, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);

        // Assert that the buy order is added back to the book since no match is found
        assertEquals(1, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
        assertEquals(100, engine.getOrderBook().getBuyPriceLevels().get(100.0).peek().getQuantity());
    }

    @Test
    void testUnmatchedLimitSellOrderAddedBackToBook() {
        Order sellOrder = new Order("1", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);
        Order buyOrder = new Order("2", 100, 700, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);

        // Assert that the sell order is added back to the book since no match is found
        assertEquals(1, engine.getOrderBook().getSellPriceLevels().get(100.0).size());
        assertEquals(100, engine.getOrderBook().getSellPriceLevels().get(100.0).peek().getQuantity());
    }

    @Test
    void testCancelBuyOrder() {
        Order buyOrder1 = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        Order buyOrder2 = new Order("2", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder1);
        engine.processOrder(buyOrder2);

        // Cancel one of the buy orders
        engine.getOrderBook().cancelOrder("1", OrderType.BUY, 100);

        // Assert that only one order remains at the price level
        assertEquals(1, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
        assertEquals("2", engine.getOrderBook().getBuyPriceLevels().get(100.0).peek().getID());
    }

    @Test
    void testCancelSellOrder() {
        Order sellOrder1 = new Order("1", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        Order sellOrder2 = new Order("2", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder1);
        engine.processOrder(sellOrder2);

        // Cancel one of the sell orders
        engine.getOrderBook().cancelOrder("1", OrderType.SELL, 100);

        // Assert that only one order remains at the price level
        assertEquals(1, engine.getOrderBook().getSellPriceLevels().get(100.0).size());
        assertEquals("2", engine.getOrderBook().getSellPriceLevels().get(100.0).peek().getID());
    }

    @Test
    void testCancelLastBuyOrderAtPriceLevel() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);

        // Cancel the buy order
        engine.getOrderBook().cancelOrder("1", OrderType.BUY, 100);

        // Assert that the price level is removed from the book
        assertTrue(engine.getOrderBook().getBuyPriceLevels().containsKey(100.0));
        assertTrue(engine.getOrderBook().getBuyBook().contains(100.0));
        assertEquals(0, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
    }

    @Test
    void testCancelLastSellOrderAtPriceLevel() {
        Order sellOrder = new Order("1", 100, 800, OrderType.SELL, OrderStrat.Limit, "aapl");
        engine.processOrder(sellOrder);

        // Cancel the sell order
        engine.getOrderBook().cancelOrder("1", OrderType.SELL, 100);

        // Assert that the price level is removed from the book
        assertTrue(engine.getOrderBook().getSellPriceLevels().containsKey(100.0));
        assertTrue(engine.getOrderBook().getSellBook().contains(100.0));
        assertEquals(0, engine.getOrderBook().getSellPriceLevels().get(100.0).size());
    }

    @Test
    void testCancelNonExistentOrder() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        engine.processOrder(buyOrder);

        // Attempt to cancel a non-existent order
        engine.getOrderBook().cancelOrder("2", OrderType.BUY, 100);

        // Assert that the existing order is still in the book
        assertEquals(1, engine.getOrderBook().getBuyPriceLevels().get(100.0).size());
        assertEquals("1", engine.getOrderBook().getBuyPriceLevels().get(100.0).peek().getID());
    }

    @Test
    void testPriorityQueueBuy() {
        Order buyOrder = new Order("1", 100, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        Order buyOrder1 = new Order("2", 150, 900, OrderType.BUY, OrderStrat.Limit, "aapl");
        Order buyOrder2 = new Order("3", 200, 900, OrderType.BUY, OrderStrat.Limit, "aapl");

        engine.processOrder(buyOrder);
        engine.processOrder(buyOrder1);
        engine.processOrder(buyOrder2);

        Assertions.assertEquals(200.0, engine.getOrderBook().getBuyBook().poll());
        Assertions.assertEquals(150.0, engine.getOrderBook().getBuyBook().poll());
        Assertions.assertEquals(100.0, engine.getOrderBook().getBuyBook().poll());
    }

    @Test
    void testPriorityQueueSell() {
        Order sellOrder = new Order("1", 50, 900, OrderType.SELL, OrderStrat.Limit, "aapl");
        Order sellOrder1 = new Order("2", 100, 900, OrderType.SELL, OrderStrat.Limit, "aapl");
        Order sellOrder2 = new Order("3", 150, 900, OrderType.SELL, OrderStrat.Limit, "aapl");

        engine.processOrder(sellOrder);
        engine.processOrder(sellOrder1);
        engine.processOrder(sellOrder2);

        Assertions.assertEquals(50.0, engine.getOrderBook().getSellBook().poll());
        Assertions.assertEquals(100.0, engine.getOrderBook().getSellBook().poll());
        Assertions.assertEquals(150.0, engine.getOrderBook().getSellBook().poll());
    }

}
