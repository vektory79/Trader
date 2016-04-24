package ru.trader.maddavo;

import java.util.HashMap;
import java.util.Map;

public class ItemConverter {
    private final static Map<String, String> IDS = new HashMap<>(85, 0.9f);


    static {
        IDS.put("explosives", "explosives");
        IDS.put("hydrogen fuel", "hydrogenfuel");
        IDS.put("mineral oil", "mineraloil");
        IDS.put("pesticides", "pesticides");
        IDS.put("clothing", "clothing");
        IDS.put("consumer technology", "consumertechnology");
        IDS.put("domestic appliances", "domesticappliances");
        IDS.put("algae", "algae");
        IDS.put("animal meat", "animalmeat");
        IDS.put("coffee", "coffee");
        IDS.put("energy drinks", "energydrinks");
        IDS.put("fish", "fish");
        IDS.put("food cartridges", "foodcartridges");
        IDS.put("fruit and vegetables", "fruitandvegetables");
        IDS.put("grain", "grain");
        IDS.put("synthetic meat", "syntheticmeat");
        IDS.put("tea", "tea");
        IDS.put("polymers", "polymers");
        IDS.put("semiconductors", "semiconductors");
        IDS.put("superconductors", "superconductors");
        IDS.put("beer", "beer");
        IDS.put("liquor", "liquor");
        IDS.put("narcotics", "basicnarcotics");
        IDS.put("tobacco", "tobacco");
        IDS.put("wine", "wine");
        IDS.put("atmospheric processors", "atmosphericprocessors");
        IDS.put("crop harvesters", "cropharvesters");
        IDS.put("marine equipment", "marinesupplies");
        IDS.put("microbial furnaces", "microbialfurnaces");
        IDS.put("mineral extractors", "mineralextractors");
        IDS.put("power generators", "powergenerators");
        IDS.put("water purifiers", "waterpurifiers");
        IDS.put("agri-medicines", "agriculturalmedicines");
        IDS.put("basic medicines", "basicmedicines");
        IDS.put("combat stabilisers", "combatstabilisers");
        IDS.put("performance enhancers", "performanceenhancers");
        IDS.put("progenitor cells", "progenitorcells");
        IDS.put("aluminium", "aluminium");
        IDS.put("beryllium", "beryllium");
        IDS.put("copper", "copper");
        IDS.put("cobalt", "cobalt");
        IDS.put("gallium", "gallium");
        IDS.put("gold", "gold");
        IDS.put("indium", "indium");
        IDS.put("lithium", "lithium");
        IDS.put("osmium", "osmium");
        IDS.put("palladium", "palladium");
        IDS.put("platinum", "platinum");
        IDS.put("tantalum", "tantalum");
        IDS.put("titanium", "titanium");
        IDS.put("silver", "silver");
        IDS.put("uranium", "uranium");
        IDS.put("minerals", "group.minerals");
        IDS.put("bauxite", "bauxite");
        IDS.put("bertrandite", "bertrandite");
        IDS.put("coltan", "coltan");
        IDS.put("gallite", "gallite");
        IDS.put("indite", "indite");
        IDS.put("lepidolite", "lepidolite");
        IDS.put("painite", "painite");
        IDS.put("rutile", "rutile");
        IDS.put("uraninite", "uraninite");
        IDS.put("imperial slaves", "imperialslaves");
        IDS.put("slaves", "slaves");
        IDS.put("sap 8 core container", "sap8corecontainer");
        IDS.put("advanced catalysers", "advancedcatalysers");
        IDS.put("animal monitors", "animalmonitors");
        IDS.put("aquaponic systems", "aquaponicsystems");
        IDS.put("auto-fabricators", "autofabricators");
        IDS.put("bioreducing lichen", "bioreducinglichen");
        IDS.put("computer components", "computercomponents");
        IDS.put("h.e. suits", "hazardousenvironmentsuits");
        IDS.put("resonating separators", "resonatingseparators");
        IDS.put("robotics", "robotics");
        IDS.put("land enrichment systems", "landenrichmentsystems");
        IDS.put("leather", "leather");
        IDS.put("natural fabrics", "naturalfabrics");
        IDS.put("synthetic fabrics", "syntheticfabrics");
        IDS.put("biowaste", "biowaste");
        IDS.put("chemical waste", "chemicalwaste");
        IDS.put("scrap", "scrap");
        IDS.put("battle weapons", "battleweapons");
        IDS.put("non-lethal weapons", "nonlethalweapons");
        IDS.put("personal weapons", "personalweapons");
        IDS.put("reactive armour", "reactivearmour");
    }

    public static String getItemId(String name){
        name = name.toLowerCase();
        String id = IDS.get(name);
        return  id != null ? id : name.replace(" ","_");
    }
}