package ru.trader.core;

import ru.trader.analysis.graph.Connectable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public interface Place extends Connectable<Place> {

    String getName();
    void setName(String name);

    double getX();
    double getY();
    double getZ();
    void setPosition(double x, double y, double z);

    FACTION getFaction();
    void setFaction(FACTION faction);

    GOVERNMENT getGovernment();
    void setGovernment(GOVERNMENT government);

    POWER getPower();
    POWER_STATE getPowerState();
    void setPower(POWER power, POWER_STATE state);

    Collection<Vendor> get();
    default Collection<Vendor> get(boolean withTransit){
        if (withTransit){
            Collection<Vendor> vendors = new ArrayList<>();
            vendors.add(new TransitVendor(this));
            vendors.addAll(get());
            return vendors;
        } else {
            return get();
        }
    }
    default Collection<String> getVendorNames(){
        return get().stream().map(Vendor::getName).collect(Collectors.toList());
    }

    default Vendor get(String name){
        Optional<Vendor> vendor = get().stream().filter(p -> name.equalsIgnoreCase(p.getName())).findFirst();
        return vendor.isPresent() ? vendor.get() : null;
    }
    void add(Vendor vendor);
    Vendor addVendor(String name);
    void remove(Vendor vendor);

    default long count(){
        return get().size();
    }

    default boolean isEmpty(){
        return get().isEmpty();
    }

    @Override
    default boolean canRefill() {
        return !isEmpty() && get().stream().filter(v -> v.has(SERVICE_TYPE.REFUEL)).findAny().isPresent();
    }

    @Override
    default double getDistance(Place other){
        return getDistance(other.getX(), other.getY(), other.getZ());
    }

    default double getDistance(double x, double y, double z){
        return Math.sqrt(Math.pow(x - getX(), 2) + Math.pow(y - getY(), 2) + Math.pow(z - getZ(), 2));
    }

    default void clear(){
        Collection<Vendor> vendors = new ArrayList<>(get());
        for (Vendor vendor : vendors) {
            remove(vendor);
        }
    }

    default void clearOffers(){
        for (Vendor vendor : get()) {
            vendor.clear();
        }
    }

    default Vendor asTransit(){
        return new TransitVendor(this);
    }
}
