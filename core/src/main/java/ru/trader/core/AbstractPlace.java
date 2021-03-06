package ru.trader.core;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.analysis.graph.Connectable;

import java.util.Objects;

public abstract class AbstractPlace implements Place {
    private final static Logger LOG = LoggerFactory.getLogger(AbstractPlace.class);
    private AbstractMarket market;

    protected abstract Vendor createVendor(String name);
    protected abstract void updateName(String name);
    protected abstract void updateFaction(FACTION faction);
    protected abstract void updateGovernment(GOVERNMENT government);
    protected abstract void updatePower(POWER power, POWER_STATE state);
    protected abstract void updatePosition(double x, double y, double z);
    protected abstract void addVendor(Vendor vendor);
    protected abstract void removeVendor(Vendor vendor);

    protected final void setMarket(AbstractMarket market){
        assert this.market == null;
        this.market = market;
    }

    protected final AbstractMarket getMarket(){
        return market;
    }

    @Override
    public final void setName(String name){
        if (market != null){
            LOG.debug("Change name of place {} to {}", this, name);
            market.updateName(this, name);
            market.setChange(true);
        } else {
            updateName(name);
        }
    }

    @Override
    public final void setPosition(double x, double y, double z) {
        if (market != null){
            LOG.debug("Change position of place {} to {},{},{}", this, x, y, z);
            market.updatePosition(this, x, y, z);
            market.setChange(true);
        } else {
            updatePosition(x, y, z);
        }
    }

    @Override
    public final void setFaction(FACTION faction){
        if (market != null){
            LOG.debug("Change faction of place {} to {}", this, faction);
            market.updateFaction(this, faction);
            market.setChange(true);
        } else {
            updateFaction(faction);
        }
    }

    @Override
    public final void setGovernment(GOVERNMENT government){
        if (market != null){
            LOG.debug("Change government of place {} to {}", this, government);
            market.updateGovernment(this, government);
            market.setChange(true);
        } else {
            updateGovernment(government);
        }
    }

    @Override
    public final void setPower(POWER power, POWER_STATE state){
        if (market != null){
            LOG.debug("Change power of place {} to {} of {}", this, state, power);
            updatePower(power, state);
            market.setChange(true);
        } else {
            updatePower(power, state);
        }
    }

    @Override
    public final void add(Vendor vendor) {
        if (market != null){
            LOG.debug("Add vendor {} to place {}", vendor, this);
            addVendor(vendor);
            market.setChange(true);
            market.onAdd(vendor);
        } else {
            addVendor(vendor);
        }
    }

    @Override
    public Vendor addVendor(String name) {
        Vendor vendor = createVendor(name);
        add(vendor);
        return vendor;
    }

    @Override
    public final void remove(Vendor vendor) {
        if (market != null){
            LOG.debug("Remove vendor {} from place {}", vendor, this);
            removeVendor(vendor);
            market.setChange(true);
            market.onRemove(vendor);
        } else {
            removeVendor(vendor);
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(@NotNull Connectable<Place> o) {
        Objects.requireNonNull(o, "Not compare with null");
        Place other = (Place) o;
        if (this == other) return 0;
        String name = getName();
        String otherName = other.getName();
        return name != null ? otherName != null ? name.compareTo(otherName) : -1 : 0;
    }

}
