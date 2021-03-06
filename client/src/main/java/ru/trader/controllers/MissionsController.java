package ru.trader.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import ru.trader.model.*;
import ru.trader.view.support.DurationField;
import ru.trader.view.support.NumberField;
import ru.trader.view.support.autocomplete.AutoCompletion;
import ru.trader.view.support.autocomplete.CachedSuggestionProvider;
import ru.trader.view.support.autocomplete.ItemsProvider;
import ru.trader.view.support.autocomplete.StationsProvider;

import java.time.Duration;


public class MissionsController {

    @FXML
    private TextField starportText;
    private AutoCompletion<StationModel> starport;
    @FXML
    private TextField itemText;
    private AutoCompletion<ItemModel> cargo;
    @FXML
    private NumberField quantity;
    @FXML
    private DurationField leftTime;
    @FXML
    private NumberField reward;
    @FXML
    private ToggleButton courierBtn;
    @FXML
    private ToggleButton deliveryBtn;
    @FXML
    private ToggleButton supplyBtn;

    private enum MISSION_TYPE {COURIER, DELIVERY, SUPPLY}

    private final ToggleGroup missionTypeGroup;
    private final ObservableList<MissionModel> missions;
    private StationModel station;
    private MISSION_TYPE missionType;

    public MissionsController() {
        missionTypeGroup = new ToggleGroup();
        missions = FXCollections.observableArrayList();
    }

    @FXML
    private void initialize(){
        missionTypeGroup.selectedToggleProperty().addListener((ov, o, n) ->{
            if (courierBtn.equals(n)){
                missionType = MISSION_TYPE.COURIER;
                starportText.setDisable(false);
                itemText.setDisable(true);
                quantity.setDisable(true);
                leftTime.setDisable(false);
                reward.setDisable(false);
            } else
            if (deliveryBtn.equals(n)){
                missionType = MISSION_TYPE.DELIVERY;
                starportText.setDisable(false);
                itemText.setDisable(true);
                quantity.setDisable(false);
                leftTime.setDisable(false);
                reward.setDisable(false);
            } else
            if (supplyBtn.equals(n)){
                missionType = MISSION_TYPE.SUPPLY;
                if (station != null) {
                    starport.setValue(station);
                }
                starportText.setDisable(false);
                itemText.setDisable(false);
                quantity.setDisable(false);
                leftTime.setDisable(false);
                reward.setDisable(false);
            } else {
                missionType = null;
                starportText.setDisable(true);
                itemText.setDisable(true);
                quantity.setDisable(true);
                leftTime.setDisable(true);
                reward.setDisable(true);
            }
        });
        courierBtn.setToggleGroup(missionTypeGroup);
        deliveryBtn.setToggleGroup(missionTypeGroup);
        supplyBtn.setToggleGroup(missionTypeGroup);
        init();
    }

    void init(){
        MarketModel world = MainController.getWorld();
        StationsProvider provider = new StationsProvider(world);
        if (starport == null){
            starport = new AutoCompletion<>(starportText, new CachedSuggestionProvider<>(provider), ModelFabric.NONE_STATION, provider.getConverter());
        } else {
            starport.setSuggestions(provider.getPossibleSuggestions());
            starport.setConverter(provider.getConverter());
        }
        ItemsProvider itemsProvider = new ItemsProvider(world);
        if (cargo == null){
            cargo = new AutoCompletion<>(itemText, new CachedSuggestionProvider<>(itemsProvider), ModelFabric.NONE_ITEM, itemsProvider.getConverter());
        } else {
            cargo.setSuggestions(itemsProvider.getPossibleSuggestions());
            cargo.setConverter(itemsProvider.getConverter());
        }
    }

    void setStations(ObservableList<String> stationNames) {
        starport.setSuggestions(stationNames);
    }

    void setItems(ObservableList<String> itemNames){
        cargo.setSuggestions(itemNames);
    }

    public void add(){
        StationModel station = starport.getValue();
        ItemModel item = cargo.getValue();
        long count = quantity.getValue().longValue();
        Duration time = leftTime.getValue();
        double profit = reward.getValue().doubleValue();
        if (!ModelFabric.isFake(station) && profit > 0){
            switch (missionType){
                case COURIER: missions.add(new MissionModel(station, time, profit));
                    break;
                case DELIVERY: if (count > 0) missions.add(new MissionModel(station, count, time, profit));
                    break;
                case SUPPLY: if (!ModelFabric.isFake(item) && count > 0) missions.add(new MissionModel(station, item, count, time, profit));
                    break;
            }
        }
    }

    public void remove(int index){
        missions.remove(index);
    }

    public void clear(){
        missions.clear();
    }

    public ObservableList<MissionModel> getMissions() {
        return missions;
    }

    public void setStation(StationModel station) {
        this.station = station;
    }
}
