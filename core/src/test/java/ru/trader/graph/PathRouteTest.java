package ru.trader.graph;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.TestUtil;
import ru.trader.core.*;

import java.util.Collection;

public class PathRouteTest extends Assert {
    private final static Logger LOG = LoggerFactory.getLogger(PathRouteTest.class);

    private final static Item ITEM1 = new Item("ITEM1");
    private final static Item ITEM2 = new Item("ITEM2");
    private final static Item ITEM3 = new Item("ITEM3");
    private static Vendor v1;
    private static Vendor v2;
    private static Vendor v3;
    private static Vendor v4;
    private static Vendor v5;

    private PathRoute initTest1(){
        LOG.info("Init test 1");
        v1 = new SimpleVendor("v1");
        v2 = new SimpleVendor("v2");

        v1.add(new Offer(OFFER_TYPE.SELL, ITEM1, 100));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM2, 200));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM3, 300));
        v2.add(new Offer(OFFER_TYPE.BUY, ITEM1, 300));
        v2.add(new Offer(OFFER_TYPE.BUY, ITEM2, 350));
        v2.add(new Offer(OFFER_TYPE.BUY, ITEM3, 400));

        PathRoute res = new PathRoute(new Vertex<>(v1));
        res = (PathRoute) res.connectTo(new Vertex<>(v2), false);
        res.finish();
        res.resort(10000, 5);
        return (PathRoute) res.getRoot();
    }


    @Test
    public void testPathRoute1() throws Exception {
        LOG.info("Start path route test 1");
        PathRoute path = initTest1().getNext();
        Collection<Order> orders = path.getOrders();

        Order order1 = new Order(v1.getSell(ITEM1), v2.getBuy(ITEM1), 5);
        Order order2 = new Order(v1.getSell(ITEM2), v2.getBuy(ITEM2), 5);
        Order order3 = new Order(v1.getSell(ITEM3), v2.getBuy(ITEM3), 5);

        assertEquals(1000, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order1, order2, order3, PathRoute.TRANSIT);
    }

    private PathRoute initTest2(){
        LOG.info("Init test 2");
        v1 = new SimpleVendor("v1");
        v2 = new SimpleVendor("v2");
        v3 = new SimpleVendor("v3");

        v1.add(new Offer(OFFER_TYPE.SELL, ITEM1, 100));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM3, 300));
        v2.add(new Offer(OFFER_TYPE.SELL, ITEM2, 200));
        v3.add(new Offer(OFFER_TYPE.BUY, ITEM1, 300));
        v3.add(new Offer(OFFER_TYPE.BUY, ITEM2, 350));
        v3.add(new Offer(OFFER_TYPE.BUY, ITEM3, 400));

        PathRoute res = new PathRoute(new Vertex<>(v1));
        res = (PathRoute) res.connectTo(new Vertex<>(v2), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v3), false);
        res.finish();
        res.resort(10000, 5);
        return (PathRoute) res.getRoot();
    }

    @Test
    public void testPathRoute2() throws Exception {
        LOG.info("Start path route test 2");
        PathRoute path = initTest2().getNext();
        Collection<Order> orders = path.getOrders();

        Order order1 = new Order(v1.getSell(ITEM1), v3.getBuy(ITEM1), 5);
        Order order2 = new Order(v2.getSell(ITEM2), v3.getBuy(ITEM2), 5);
        Order order3 = new Order(v1.getSell(ITEM3), v3.getBuy(ITEM3), 5);

        assertEquals(1000, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order1,  PathRoute.TRANSIT, order3);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(750, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order2, PathRoute.TRANSIT);
    }

    private PathRoute initTest3(){
        LOG.info("Init test 3");
        v1 = new SimpleVendor("v1");
        v2 = new SimpleVendor("v2");
        v3 = new SimpleVendor("v3");
        v4 = new SimpleVendor("v4");

        v1.add(new Offer(OFFER_TYPE.SELL, ITEM1, 100));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM2, 200));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM3, 300));
        v2.add(new Offer(OFFER_TYPE.SELL, ITEM1, 150));
        v2.add(new Offer(OFFER_TYPE.SELL, ITEM3, 320));
        v3.add(new Offer(OFFER_TYPE.SELL, ITEM3, 390));

        v2.add(new Offer(OFFER_TYPE.BUY, ITEM2, 230));
        v3.add(new Offer(OFFER_TYPE.BUY, ITEM1, 200));
        v4.add(new Offer(OFFER_TYPE.BUY, ITEM3, 450));

        PathRoute res = new PathRoute(new Vertex<>(v1));
        res = (PathRoute) res.connectTo(new Vertex<>(v2), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v3), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v4), false);
        res.finish();
        res.resort(10000, 5);
        return (PathRoute) res.getRoot();
    }

    @Test
    public void testPathRoute3() throws Exception {
        LOG.info("Start path route test 3");
        PathRoute path = initTest3().getNext();
        Collection<Order> orders = path.getOrders();

        Order order1 = new Order(v1.getSell(ITEM1), v3.getBuy(ITEM1), 5);
        Order order2 = new Order(v1.getSell(ITEM2), v2.getBuy(ITEM2), 5);
        Order order3 = new Order(v1.getSell(ITEM3), v4.getBuy(ITEM3), 5);
        Order order4 = new Order(v2.getSell(ITEM1), v3.getBuy(ITEM1), 5);
        Order order5 = new Order(v2.getSell(ITEM3), v4.getBuy(ITEM3), 5);
        Order order7 = new Order(v3.getSell(ITEM3), v4.getBuy(ITEM3), 5);

        assertEquals(800, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order2, order1, order3, PathRoute.TRANSIT);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(650, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order5, order4, PathRoute.TRANSIT);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(300, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order7, PathRoute.TRANSIT);
    }

    private PathRoute initTest4(){
        LOG.info("Init test 4");
        v1 = new SimpleVendor("v1");
        v2 = new SimpleVendor("v2");
        v3 = new SimpleVendor("v3");
        v4 = new SimpleVendor("v4");
        v5 = new SimpleVendor("v5");

        v1.add(new Offer(OFFER_TYPE.SELL, ITEM1, 410));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM2, 200));
        v1.add(new Offer(OFFER_TYPE.SELL, ITEM3, 300));
        v2.add(new Offer(OFFER_TYPE.SELL, ITEM2, 270));
        v4.add(new Offer(OFFER_TYPE.SELL, ITEM1, 300));

        v2.add(new Offer(OFFER_TYPE.BUY, ITEM1, 470));
        v3.add(new Offer(OFFER_TYPE.BUY, ITEM2, 300));
        v4.add(new Offer(OFFER_TYPE.BUY, ITEM3, 370));
        v5.add(new Offer(OFFER_TYPE.BUY, ITEM1, 400));

        PathRoute res = new PathRoute(new Vertex<>(v1));
        res = (PathRoute) res.connectTo(new Vertex<>(v2), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v3), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v4), false);
        res = (PathRoute) res.connectTo(new Vertex<>(v5), false);
        res.finish();
        res.resort(10000, 5);
        return (PathRoute) res.getRoot();
    }

    @Test
    public void testPathRoute4() throws Exception {
        LOG.info("Start path route test 4");
        PathRoute path = initTest4().getNext();
        Collection<Order> orders = path.getOrders();

        Order order1 = new Order(v1.getSell(ITEM1), v2.getBuy(ITEM1), 5);
        Order order2 = new Order(v1.getSell(ITEM1), v5.getBuy(ITEM1), 5);
        Order order3 = new Order(v1.getSell(ITEM2), v3.getBuy(ITEM2), 5);
        Order order4 = new Order(v1.getSell(ITEM3), v4.getBuy(ITEM3), 5);
        Order order5 = new Order(v2.getSell(ITEM2), v3.getBuy(ITEM2), 5);
        Order order6 = new Order(v4.getSell(ITEM1), v5.getBuy(ITEM1), 5);


        assertEquals(1000, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order3, order1, order4, PathRoute.TRANSIT, order2);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(650, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order5, PathRoute.TRANSIT);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(500, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, PathRoute.TRANSIT);

        path = path.getNext();
        orders = path.getOrders();

        assertEquals(500, path.getProfit(), 0.0001);
        TestUtil.assertCollectionEquals(orders, order6, PathRoute.TRANSIT);
    }
}