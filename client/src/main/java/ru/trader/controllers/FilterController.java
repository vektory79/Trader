package ru.trader.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.DialogAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.trader.core.MarketFilter;
import ru.trader.core.SERVICE_TYPE;
import ru.trader.model.MarketModel;
import ru.trader.model.ModelFabric;
import ru.trader.model.StationModel;
import ru.trader.model.SystemModel;
import ru.trader.model.support.BindingsHelper;
import ru.trader.view.support.Localization;
import ru.trader.view.support.NumberField;
import ru.trader.view.support.cells.CustomListCell;


public class FilterController {
    private final static Logger LOG = LoggerFactory.getLogger(FilterController.class);

    @FXML
    private ComboBox<SystemModel> center;
    @FXML
    private NumberField radius;
    @FXML
    private NumberField distance;
    @FXML
    private ComboBox<SystemModel> system;
    @FXML
    private ComboBox<StationModel> station;
    @FXML
    private CheckBox cbMarket;
    @FXML
    private CheckBox cbBlackMarket;
    @FXML
    private CheckBox cbRepair;
    @FXML
    private CheckBox cbMunition;
    @FXML
    private CheckBox cbOutfit;
    @FXML
    private CheckBox cbShipyard;
    @FXML
    private CheckBox cbMediumLandpad;
    @FXML
    private CheckBox cbLargeLandpad;
    @FXML
    private ListView<StationModel> excludes;

    public final Action actSave = new DialogAction(Localization.getString("dialog.button.save"), ButtonBar.ButtonType.OK_DONE, false, true, false, (e) -> save());

    private MarketModel market;
    private MarketFilter filter;

    @FXML
    private void initialize(){
        system.valueProperty().addListener((ov, o, n) -> station.setItems(n.getStationsList()));
        excludes.setCellFactory(new CustomListCell<>(s -> String.format("%s (%s)", s.getSystem().getName(), s.getName())));
        init();
    }

    void init(){
        market = MainController.getMarket();
        center.setItems(market.systemsListProperty());
        system.setItems(market.systemsProperty());
    }

    private void fill(MarketFilter filter){
        this.filter = filter;
        center.setValue(market.getModeler().get(filter.getCenter()));
        radius.setValue(filter.getRadius());
        distance.setValue(filter.getDistance());
        cbMarket.setSelected(filter.has(SERVICE_TYPE.MARKET));
        cbBlackMarket.setSelected(filter.has(SERVICE_TYPE.BLACK_MARKET));
        cbMunition.setSelected(filter.has(SERVICE_TYPE.MUNITION));
        cbRepair.setSelected(filter.has(SERVICE_TYPE.REPAIR));
        cbOutfit.setSelected(filter.has(SERVICE_TYPE.OUTFIT));
        cbShipyard.setSelected(filter.has(SERVICE_TYPE.SHIPYARD));
        cbMediumLandpad.setSelected(filter.has(SERVICE_TYPE.MEDIUM_LANDPAD));
        cbLargeLandpad.setSelected(filter.has(SERVICE_TYPE.LARGE_LANDPAD));
        excludes.setItems(BindingsHelper.observableList(filter.getExcludes(), market.getModeler()::get));
    }

    private void save() {
        SystemModel s = center.getValue();
        LOG.trace("Old filter", filter);
        filter.setCenter(s == ModelFabric.NONE_SYSTEM ? null : market.getModeler().get(s));
        filter.setRadius(radius.getValue().doubleValue());
        filter.setDistance(distance.getValue().doubleValue());
        if (cbMarket.isSelected()) filter.add(SERVICE_TYPE.MARKET); else filter.remove(SERVICE_TYPE.MARKET);
        if (cbBlackMarket.isSelected()) filter.add(SERVICE_TYPE.BLACK_MARKET); else filter.remove(SERVICE_TYPE.BLACK_MARKET);
        if (cbMunition.isSelected()) filter.add(SERVICE_TYPE.MUNITION); else filter.remove(SERVICE_TYPE.MUNITION);
        if (cbRepair.isSelected()) filter.add(SERVICE_TYPE.REPAIR); else filter.remove(SERVICE_TYPE.REPAIR);
        if (cbOutfit.isSelected()) filter.add(SERVICE_TYPE.OUTFIT); else filter.remove(SERVICE_TYPE.OUTFIT);
        if (cbShipyard.isSelected()) filter.add(SERVICE_TYPE.SHIPYARD); else filter.remove(SERVICE_TYPE.SHIPYARD);
        if (cbMediumLandpad.isSelected()) filter.add(SERVICE_TYPE.MEDIUM_LANDPAD); else filter.remove(SERVICE_TYPE.MEDIUM_LANDPAD);
        if (cbLargeLandpad.isSelected()) filter.add(SERVICE_TYPE.LARGE_LANDPAD); else filter.remove(SERVICE_TYPE.LARGE_LANDPAD);
        filter.clearExcludes();
        excludes.getItems().forEach(st -> filter.addExclude(market.getModeler().get(st)));
        LOG.trace("New filter", filter);
    }

    public Action showDialog(Parent parent, Parent content){
        return showDialog(parent, content, new MarketFilter());
    }

    public Action showDialog(Parent parent, Parent content, MarketFilter filter){
        Dialog dlg = new Dialog(parent, Localization.getString("filter.title"));
        dlg.setContent(content);
        dlg.getActions().addAll(actSave, Dialog.ACTION_CANCEL);
        dlg.setResizable(false);
        fill(filter);
        return dlg.show();
    }

    public void add(ActionEvent actionEvent) {
        SystemModel s = system.getValue();
        if (s != null){
            StationModel st = station.getValue();
            if (st != null && st != ModelFabric.NONE_STATION){
                excludes.getItems().add(st);
            } else {
                excludes.getItems().addAll(s.getStations());
            }
        }
    }

    public void remove(ActionEvent actionEvent) {
        int index = excludes.getSelectionModel().getSelectedIndex();
        if (index >= 0){
            excludes.getItems().remove(index);
        }
    }

    public void clean(ActionEvent actionEvent) {
        excludes.getItems().clear();
    }

    public MarketFilter getFilter(){
        return filter;
    }
}