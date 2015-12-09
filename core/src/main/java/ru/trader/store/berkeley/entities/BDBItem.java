package ru.trader.store.berkeley.entities;

import com.sleepycat.persist.model.*;
import ru.trader.core.FACTION;
import ru.trader.core.GOVERNMENT;

import java.util.Collection;
import java.util.HashSet;

@Entity(version = 2)
public class BDBItem {
    @PrimaryKey(sequence="I_ID")
    private long id;

    private String name;

    @SecondaryKey(relate = Relationship.MANY_TO_ONE,
            relatedEntity = BDBGroup.class,
            onRelatedEntityDelete = DeleteAction.CASCADE)
    private String groupId;

    @SecondaryKey(relate=Relationship.MANY_TO_MANY)
    Collection<FACTION> fIllegals = new HashSet<>();
    @SecondaryKey(relate=Relationship.MANY_TO_MANY)
    Collection<GOVERNMENT> gIllegals = new HashSet<>();

    private BDBItem() {
    }

    public BDBItem(String name, String groupId) {
        this.name = name;
        this.groupId = groupId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIllegal(FACTION faction, boolean illegal) {
        if (illegal) fIllegals.add(faction);
        else fIllegals.remove(faction);
    }

    public boolean isIllegal(FACTION faction) {
        return fIllegals.contains(faction);
    }

    public void setIllegal(GOVERNMENT government, boolean illegal) {
        if (illegal) gIllegals.add(government);
        else gIllegals.remove(government);
    }

    public boolean isIllegal(GOVERNMENT government) {
        return gIllegals.contains(government);
    }

    public String getGroupId() {
        return groupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BDBItem)) return false;

        BDBItem bdbItem = (BDBItem) o;

        if (id != bdbItem.id) return false;
        if (!groupId.equals(bdbItem.groupId)) return false;
        if (!name.equals(bdbItem.name)) return false;
        if (!fIllegals.equals(bdbItem.fIllegals)) return false;
        if (!gIllegals.equals(bdbItem.gIllegals)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
