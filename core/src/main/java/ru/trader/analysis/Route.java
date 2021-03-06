package ru.trader.analysis;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.core.Order;
import ru.trader.core.TransitVendor;
import ru.trader.core.Vendor;

import java.util.*;

public class Route implements Comparable<Route> {
    private final static Logger LOG = LoggerFactory.getLogger(Route.class);

    private final List<RouteEntry> entries;
    private double profit = 0;
    private double balance = 0;
    private double distance = 0;
    private double fuel = 0;
    private long time = 0;
    private int lands = 0;
    private int refills = 0;
    private long cargo=0;

    public Route(RouteEntry root) {
        entries = new ArrayList<>();
        entries.add(root);
    }

    public Route(List<RouteEntry> entries) {
        this.entries = new ArrayList<>(entries);
        updateStats();
    }

    protected Route(Route route) {
        this.profit = route.profit;
        this.balance = route.balance;
        this.distance = route.distance;
        this.fuel = route.fuel;
        this.time = route.time;
        this.lands = route.lands;
        this.refills = route.refills;
        this.cargo = route.cargo;
        this.entries = new ArrayList<>(route.entries.size());
        route.entries.forEach(e -> this.entries.add(RouteEntry.clone(e)));
    }


    public List<RouteEntry> getEntries() {
        return entries;
    }

    public RouteEntry get(int index) {
        return entries.get(index);
    }

    public boolean isEmpty(){
        return entries.isEmpty();
    }

    void setBalance(double balance){
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public long getCargo() {
        return cargo;
    }

    void setCargo(long cargo) {
        this.cargo = cargo;
    }

    public double getProfit() {
        return profit;
    }

    public double getDistance() {
        return distance;
    }

    public int getLands() {
        return lands;
    }

    public int getRefills() {
        return refills;
    }

    public long getTime() {
        return time;
    }

    public double getFuel() {
        return fuel;
    }

    public double getScore() {
        return profit / time;
    }

    public int getJumps(){
        return entries.size();
    }

    public boolean isLoop(){
        return !isEmpty() && entries.get(0).is(entries.get(entries.size()-1).getVendor());
    }

    public void add(RouteEntry entry){
        LOG.trace("Add entry {} to route {}", entry, this);
        entries.add(entry);
        updateStats();
    }

    public void add(int index, Order order){
        LOG.trace("Add order {} to route {}, index = {}", order, this, index);
        RouteEntry entry = entries.get(index);
        entry.add(order);
        updateStats();
    }

    public void remove(int index, Order order){
        LOG.trace("Remove order {} from route {}, index = {}", order, this, index);
        RouteEntry entry = entries.get(index);
        entry.remove(order);
        updateStats();
    }

    public void removeAllOrders(int index){
        LOG.trace("Remove all orders from route {}, index = {}", this, index);
        RouteEntry entry = entries.get(index);
        entry.clear();
        updateStats();
    }

    public void addAll(Collection<RouteEntry> entries){
        LOG.trace("Add {} entries {} to route {}", entries, this);
        this.entries.addAll(entries);
        updateStats();
    }

    public Collection<Vendor> getVendors() {
        return getVendors(0);
    }

    public Collection<Vendor> getVendors(int index){
        if (index < 0 || index >= entries.size()) return Collections.emptyList();
        Collection<Vendor> vendors = new HashSet<>();
        for (int i = index; i < entries.size(); i++) {
            RouteEntry entry = entries.get(i);
            vendors.add(entry.getVendor());
        }
        return vendors;
    }

    public int find(Vendor vendor, int fromIndex){
        for (Route.LoopIterator iterator = loopIterator(fromIndex); iterator.hasNext(); ) {
            RouteEntry entry = iterator.next();
            if (entry.is(vendor)) {
                return iterator.getRealIndex();
            }
        }
        return -1;
    }

    public boolean reserve(final RouteReserve reserve){
        for (Route.LoopIterator iterator = loopIterator(reserve.getFromIndex()); iterator.hasNext(); ) {
            RouteEntry entry = iterator.next();
            if (entry.isTransit()) continue;
            if (iterator.getRealIndex() == reserve.getToIndex() && (reserve.getFromIndex() != reserve.getToIndex() || iterator.getIndex() > 0)) {
                break;
            }
            if (!entry.reserve(reserve.getCount(), cargo)){
                return false;
            }
        }
        if (reserve.getOrder() != null) {
            entries.get(reserve.getFromIndex()).addOrder(reserve.getOrder());
        }
        return true;
    }

    public boolean reserve(Collection<RouteReserve> reserves){
        for (RouteReserve reserve : reserves) {
            if (!reserve(reserve)){
                return false;
            }
        }
        return true;
    }

    public void unreserve(final RouteReserve reserve){
        for (Route.LoopIterator iterator = loopIterator(reserve.getFromIndex()); iterator.hasNext(); ) {
            RouteEntry entry = iterator.next();
            if (entry.isTransit()) continue;
            if (iterator.getRealIndex() == reserve.getToIndex() && (reserve.getFromIndex() != reserve.getToIndex() || iterator.getIndex() > 0)) {
                break;
            }
            entry.fill(reserve.getCount());
        }
        if (reserve.getOrder() != null) {
            entries.get(reserve.getFromIndex()).removeOrder(reserve.getOrder());
        }
    }

    public void unreserve(Collection<RouteReserve> reserves){
        reserves.forEach(this::unreserve);
    }

    public boolean contains(Collection<Vendor> vendors){
        return vendors.isEmpty()
               || vendors.size() <= entries.size()
               && vendors.stream().allMatch(v -> entries.stream().anyMatch(e -> v.equals(e.getVendor())));
    }

    public void join(Route route){
        LOG.trace("Join route {}", route);
        if (route.isEmpty()) return;
        RouteEntry end = entries.get(entries.size()-1);
        if (route.entries.get(0).is(end.getVendor())){
            entries.remove(entries.size()-1);
        } else {
            LOG.trace("Is not connected route, set refill");
            end.setRefill(end.getFuel());
        }
        entries.addAll(route.entries);
        updateStats();
    }

    public void dropTo(Vendor vendor){
        for (ListIterator<RouteEntry> iterator = entries.listIterator(entries.size()); iterator.hasPrevious(); ) {
            RouteEntry entry = iterator.previous();
            if (entry.is(vendor)){
                break;
            }
            iterator.remove();
        }
        updateStats();
    }

    public void dropTo(int index){
        for (ListIterator<RouteEntry> iterator = entries.listIterator(entries.size()); iterator.hasPrevious(); ) {
            if (iterator.previousIndex() == index){
                break;
            }
            iterator.previous();
            iterator.remove();
        }
        updateStats();
    }

    void updateStats(){
        LOG.trace("Update stats, old: profit={}, distance={}, lands={}, fuel={}, refills={}, time={}", profit, distance, lands, fuel, refills, time);
        profit = 0; distance = 0; lands = 0; fuel = 0; refills = 0; time = 0;
        if (entries.isEmpty()) return;
        RouteEntry entry = entries.get(0);
        time = entry.getTime();
        for (int i = 1; i < entries.size(); i++) {
            RouteEntry next = entries.get(i);
            distance += entry.getVendor().getDistance(next.getVendor());
            profit += entry.getProfit();
            time += next.getTime();
            fuel += entry.getFuel();
            if (entry.isLand()){
                lands++;
            }
            if (entry.isRefill()){
                refills++;
            }
            entry = next;
        }
        LOG.trace("new stats profit={}, distance={}, lands={}, fuel={}, time={}", profit, distance, lands, fuel, time);
    }

    public boolean isOverload(){
        for (RouteEntry entry : entries) {
            if (entry.getCargo() > cargo) return true;
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull Route o) {
        return Double.compare(getScore(), o.getScore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        return (Double.compare(route.profit, profit) == 0 || Math.abs(profit - route.profit) < 0.1)&& entries.equals(route.entries);
    }

    @Override
    public int hashCode() {
        return entries.hashCode();
    }

    @Override
    public String toString() {
        return "Route{" +
                "entries=" + entries +
                ", profit=" + profit +
                ", balance=" + balance +
                ", distance=" + distance +
                ", time=" + time +
                ", score=" + getScore() +
                ", fuel=" + fuel +
                ", lands=" + lands +
                '}';
    }

    public interface LoopIterator extends Iterator<RouteEntry>{
        int getIndex();
        int getRealIndex();
    }

    public LoopIterator loopIterator(int from){
        return new LoopIterator() {
            private final int size = entries.size() - (isLoop() ? 1 : 0);
            private final int maxIndex = isLoop() ? size - 1 : size - from;
            private int i = -1;

            @Override
            public int getIndex(){
                return i;
            }

            @Override
            public int getRealIndex(){
                int index = i + from;
                if (index >= size) index -= size;
                return index;
            }

            @Override
            public boolean hasNext() {
                return i < maxIndex;
            }

            @Override
            public RouteEntry next() {
                i++;
                return entries.get(getRealIndex());
            }
        };
    }

    public static Route singletone(Vendor root, double balance, long cargo){
        RouteEntry entry = new RouteEntry(root, 0,0,0);
        if (!root.isTransit()){
            entry.setLand(true);
        }
        Route route = new Route(entry);
        route.setBalance(balance);
        route.setCargo(cargo);
        return route;
    }

    public static Route clone(Route route){
        return route != null ? new Route(route) : null;
    }

}
