package ru.trader.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import ru.trader.analysis.RouteEntry;
import ru.trader.core.Order;
import ru.trader.model.support.BindingsHelper;

import java.util.Collection;
import java.util.List;

public class RouteEntryModel {
    private final StationModel station;
    private final RouteEntry entry;
    private final DoubleProperty profit;
    private final LongProperty fullTime;
    private final DoubleProperty distance;
    private final ObservableList<OrderModel> orders;
    private final ObservableList<OrderModel> sellOrders;
    private final ObservableList<MissionModel> missions;

    RouteEntryModel(RouteEntry entry, MarketModel market) {
        this.entry = entry;
        station = market.getModeler().get(entry.getVendor());
        List<Order> orderList = entry.getOrders();
        orders = BindingsHelper.observableList(orderList, market.getModeler()::get);
        sellOrders = FXCollections.observableArrayList();
        missions = FXCollections.observableArrayList();
        profit = new SimpleDoubleProperty();
        profit.bind(BindingsHelper.group(Double::sum, OrderModel::profitProperty, orders));
        fullTime = new SimpleLongProperty();
        distance = new SimpleDoubleProperty();
    }

    void addSellOrder(OrderModel order){
        sellOrders.add(order);
    }

    void add(MissionModel mission){
        missions.add(mission);
    }

    void addAll(Collection<MissionModel> missions){
        this.missions.addAll(missions);
    }

    void remove(MissionModel mission){
        missions.remove(mission);
    }

    public StationModel getStation() {
        return station;
    }

    public double getProfit() {
        return profit.get();
    }

    public ReadOnlyDoubleProperty profitProperty() {
        return profit;
    }

    public double getFuel(){
        return entry.getFuel();
    }

    public long getTime(){
        return entry.getTime();
    }

    public long getFullTime() {
        return fullTime.get();
    }

    public ReadOnlyLongProperty fullTimeProperty() {
        return fullTime;
    }

    void setFullTime(long fullTime) {
        this.fullTime.set(fullTime);
    }

    public double getDistance() {
        return distance.get();
    }

    public ReadOnlyDoubleProperty distanceProperty() {
        return distance;
    }

    void setDistance(double distance) {
        this.distance.set(distance);
    }

    public double getRefill(){
        return entry.getRefill();
    }

    public boolean isTransit(){
        return entry.isTransit();
    }

    public boolean isBuy(){
        return !orders.isEmpty();
    }

    public boolean isSell(){
        return !sellOrders.isEmpty();
    }

    public boolean isMissionTarget(){
        return !missions.isEmpty();
    }

    public ObservableList<OrderModel> orders() {
        return orders;
    }

    public ObservableList<OrderModel> sellOrders() {
        return sellOrders;
    }

    public ObservableList<MissionModel> missions() {
        return missions;
    }

    public ObservableList<MissionModel> getCompletedMissions() {
        return new FilteredList<>(missions, MissionModel::isCompleted);
    }

    public long getCargo() {
        return entry.getCargo();
    }

    void refresh(MarketModel market){
        orders.clear();
        orders.addAll(BindingsHelper.observableList(entry.getOrders(), market.getModeler()::get));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteEntryModel)) return false;
        RouteEntryModel that = (RouteEntryModel) o;
        return entry.equals(that.entry);
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }
}
