package ru.trader.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.core.*;

import java.util.*;
import java.util.stream.Stream;

public class MarketUtils {
    private final static Logger LOG = LoggerFactory.getLogger(MarketUtils.class);

    public static List<Order> getStack(List<Order> orders, double balance, long cargo){
        List<Order> o = new ArrayList<>(orders);
        LOG.trace("Fill stack orders {}, balance {}", o, balance);
        o.forEach(or -> or.setMax(balance, cargo));
        LOG.trace("Sort by profit by tonne");
        o.sort(orderComparatorByTonneProfit.reversed());
        LOG.trace("New order of orders {}", o);
        List<Order> stack = new ArrayList<>();
        long count = cargo;
        double remain = balance;
        for (Order order : o) {
            order = new Order(order.getSell(), order.getBuy(), remain, count);
            LOG.trace("Next best order {}", order);
            if (order.getProfit() > 0) {
                stack.add(order);
                remain -= order.getCount() * order.getSell().getPrice();
                count -= order.getCount();
                LOG.trace("Remain cargo {}, remain balance {}", count, remain);
            } else {
                LOG.trace("Low profit, stopped");
                if (order.getCount() == 0 && order.getSell().getCount() > 0) {
                    remain = -1;
                } else {
                    remain = 0;
                }
            }
            if (count <= 0 || remain <= 0) {
                break;
            }
        }
        if (remain < 0) {
            LOG.trace("Low balance, try fill");
            LOG.trace("Simple sort");
            o.sort(Comparator.<Order>reverseOrder());
            LOG.trace("New order of orders {}", o);
            stack.clear();
            count = cargo;
            remain = balance;
            for (Order order : o) {
                order = new Order(order.getSell(), order.getBuy(), remain, count);
                LOG.trace("Next best order {}", order);
                if (order.getProfit() > 0) {
                    stack.add(order);
                    remain -= order.getCount() * order.getSell().getPrice();
                    count -= order.getCount();
                    LOG.trace("Remain cargo {}, remain balance {}", count, remain);
                } else {
                    LOG.trace("Low profit, stopped");
                    remain = 0;
                }
                if (count <= 0 || remain <= 0) {
                    break;
                }
            }
        }
        LOG.trace("Stack: {}", stack);
        return stack;
    }

    public static List<Order> getOrders(Vendor seller, Vendor buyer){
        LOG.trace("Get orders from {}, to {}", seller, buyer);
        List<Order> orders = new ArrayList<>();
        if (seller.isTransit() || buyer.isTransit()) return orders;
        for (Iterator<Offer> iterator = seller.getSellOffers().iterator(); iterator.hasNext(); ) {
            Offer sell = iterator.next();
            Offer buy = buyer.getBuy(sell.getItem());
            if (buy != null) {
                Order order = new Order(sell, buy, 1);
                if (order.getProfit() > 0){
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    public static boolean isIncorrect(Offer offer){
        return offer != null && isIncorrect(offer.getVendor(), offer.isIllegal(), offer.getType());
    }

    public static boolean isIncorrect(Vendor vendor, Item item, OFFER_TYPE type){
        return isIncorrect(vendor, item.isIllegal(vendor), type);
    }

    public static boolean isIncorrect(Vendor vendor, boolean isIllegal, OFFER_TYPE type){
        if (type == OFFER_TYPE.SELL){
            return isIllegal || !vendor.has(SERVICE_TYPE.MARKET);
        } else {
            return isIllegal ? !vendor.has(SERVICE_TYPE.BLACK_MARKET) : !vendor.has(SERVICE_TYPE.MARKET);
        }
    }

    public static Stream<Offer> getOffers(Collection<Offer> offers){
        return offers.stream().filter(o -> !isIncorrect(o));
    }

    public static boolean hasMarket(Vendor vendor) {
        return vendor.has(SERVICE_TYPE.MARKET) || vendor.has(SERVICE_TYPE.BLACK_MARKET);
    }

    private final static Comparator<Order> orderComparatorByTonneProfit = (o1, o2) -> {
        if (o1 == o2) return 0;
        if (o1.getProfit() > 0 || o2.getProfit() > 0) {
            if (o1.getProfit() <= 0) return -1;
            if (o2.getProfit() <= 0) return 1;
            return Double.compare(o1.getProfitByTonne(), o2.getProfitByTonne());
        }
        return o1.compareTo(o2);
    };
}
