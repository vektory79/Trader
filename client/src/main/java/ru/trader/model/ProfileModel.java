package ru.trader.model;

import javafx.beans.property.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.core.Engine;
import ru.trader.core.Profile;
import ru.trader.core.Ship;

public class ProfileModel {
    private final static Logger LOG = LoggerFactory.getLogger(ProfileModel.class);

    private final Profile profile;
    private final MarketModel market;
    private final StringProperty name;
    private final DoubleProperty balance;
    private final ObjectProperty<SystemModel> prevSystem;
    private final ObjectProperty<SystemModel> system;
    private final ObjectProperty<StationModel> station;
    private final BooleanProperty docked;
    private final DoubleProperty shipMass;
    private final DoubleProperty shipTank;
    private final IntegerProperty shipCargo;
    private final ObjectProperty<Engine> shipEngine;
    private final ObjectProperty<RouteModel> route;

    public ProfileModel(Profile profile, MarketModel market) {
        this.market = market;
        this.profile = profile;
        name = new SimpleStringProperty();
        balance = new SimpleDoubleProperty();
        system = new SimpleObjectProperty<>();
        prevSystem = new SimpleObjectProperty<>();
        station = new SimpleObjectProperty<>();
        docked = new SimpleBooleanProperty();
        shipMass = new SimpleDoubleProperty();
        shipTank = new SimpleDoubleProperty();
        shipCargo = new SimpleIntegerProperty();
        shipEngine = new SimpleObjectProperty<>();
        route = new SimpleObjectProperty<>();
        refresh();
        initListeners();
    }

    private void initListeners() {
        name.addListener((ov, o, n) -> LOG.debug("Change name, old: {}, new: {}", o, n));
        balance.addListener((ov, o, n) -> LOG.debug("Change balance, old: {}, new: {}", o, n));
        system.addListener((ov, o, n) -> {
            LOG.debug("Change system, old: {}, new: {}", o, n);
            if (!ModelFabric.isFake(o)) prevSystem.setValue(o);
            if (!ModelFabric.isFake(n)){
                if (route.getValue() != null) {getRoute().updateCurrentEntry(n);}
            }
        });
        station.addListener((ov, o, n) -> {
            LOG.debug("Change station, old: {}, new: {}", o, n);
            if (route.getValue() != null) {getRoute().updateCurrentEntry(getSystem(), n);}
        });
        docked.addListener((ov, o, n) -> {
            LOG.debug("Change docked, old: {}, new: {}", o, n);
            if (!n && route.getValue() != null) {getRoute().updateCurrentEntry(getSystem(), getStation(), true);}
        });
        shipMass.addListener((ov, o, n) -> LOG.debug("Change ship laden mass, old: {}, new: {}", o, n));
        shipTank.addListener((ov, o, n) -> LOG.debug("Change ship tank, old: {}, new: {}", o, n));
        shipCargo.addListener((ov, o, n) -> LOG.debug("Change ship cargo, old: {}, new: {}", o, n));
        shipEngine.addListener((ov, o, n) -> LOG.debug("Change ship engine, old: {}, new: {}", o, n));
        route.addListener((ov, o, n) -> LOG.debug("Change route, old: {}, new: {}", o, n));
    }

    Profile getProfile() {
        return profile;
    }

    public MarketModel getMarket() {
        return market;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        profile.setName(name);
        this.name.set(name);
    }

    public double getBalance() {
        return balance.get();
    }

    public DoubleProperty balanceProperty() {
        return balance;
    }

    public void setBalance(double balance) {
        profile.setBalance(balance);
        this.balance.set(balance);
    }

    public SystemModel getPrevSystem() {
        return prevSystem.get();
    }

    public ReadOnlyObjectProperty<SystemModel> prevSystemProperty() {
        return prevSystem;
    }

    public SystemModel getSystem() {
        return system.get();
    }

    public ObjectProperty<SystemModel> systemProperty() {
        return system;
    }

    public void setSystem(SystemModel system) {
        profile.setSystem(ModelFabric.get(system));
        this.system.set(system);
    }

    public StationModel getStation() {
        return station.get();
    }

    public ObjectProperty<StationModel> stationProperty() {
        return station;
    }

    public void setStation(StationModel station) {
        profile.setStation(ModelFabric.get(station));
        this.station.set(station);
    }

    public boolean isDocked() {
        return docked.get();
    }

    public BooleanProperty dockedProperty() {
        return docked;
    }

    public void setDocked(boolean docked) {
        profile.setDocked(docked);
        this.docked.set(docked);
    }

    public double getShipMass() {
        return shipMass.get();
    }

    public ReadOnlyDoubleProperty shipMassProperty() {
        return shipMass;
    }

    public void setShipMass(double shipMass) {
        double unladenMass = shipMass - profile.getShip().getTank() - profile.getShip().getCargo();
        profile.getShip().setMass(unladenMass);
        this.shipMass.set(profile.getShip().getLadenMass());
    }

    public double getShipTank() {
        return shipTank.get();
    }

    public ReadOnlyDoubleProperty shipTankProperty() {
        return shipTank;
    }

    public void setShipTank(double shipTank) {
        profile.getShip().setTank(shipTank);
        this.shipMass.set(profile.getShip().getLadenMass());
        this.shipTank.set(shipTank);
    }

    public int getShipCargo() {
        return shipCargo.get();
    }

    public ReadOnlyIntegerProperty shipCargoProperty() {
        return shipCargo;
    }

    public void setShipCargo(int shipCargo) {
        profile.getShip().setCargo(shipCargo);
        this.shipMass.set(profile.getShip().getLadenMass());
        this.shipCargo.set(shipCargo);
    }

    public Engine getShipEngine() {
        return shipEngine.get();
    }

    public ObjectProperty<Engine> shipEngineProperty() {
        return shipEngine;
    }

    public void setShipEngine(Engine engine) {
        profile.getShip().setEngine(engine);
        this.shipEngine.set(engine);
    }

    public RouteModel getRoute() {
        return route.get();
    }

    public ObjectProperty<RouteModel> routeProperty() {
        return route;
    }

    public void setRoute(RouteModel route) {
        this.route.set(route);
    }

    public double getShipJumpRange(){
        return profile.getShip().getJumpRange();
    }

    public double getMaxShipJumpRange(){
        return profile.getShip().getMaxJumpRange();
    }

    public double getEmptyMaxShipJumpRange(){
        return profile.getShip().getEmptyMaxJumpRange();
    }

    public void clearRoute(){
        route.setValue(null);
    }

    private void refresh(){
        name.setValue(profile.getName());
        balance.setValue(profile.getBalance());
        system.setValue(market.getModeler().get(profile.getSystem()));
        station.setValue(market.getModeler().get(profile.getStation()));
        docked.setValue(profile.isDocked());
        Ship ship = profile.getShip();
        shipMass.setValue(ship.getLadenMass());
        shipTank.setValue(ship.getTank());
        shipCargo.setValue(ship.getCargo());
        shipEngine.setValue(ship.getEngine());
    }
}
