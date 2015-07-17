package ru.trader.graph;

import ru.trader.analysis.graph.Vertex;

public class GraphCallBack<T extends Connectable<T>> {

    private volatile boolean cancel = false;

    public void onStartBuild(T from){}
    public void onEndBuild(){}


    public void onStartFind(Vertex<T> from, Vertex<T> to){}
    public void onFound(){}
    public void onEndFind(){}


    public void setMax(long count){}
    public void inc(){}

    public final boolean isCancel() {
        return cancel;
    }

    public final void cancel(){
        this.cancel = true;
    }

}
