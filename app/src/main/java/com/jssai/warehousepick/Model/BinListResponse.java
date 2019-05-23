package com.jssai.warehousepick.Model;

import java.io.Serializable;

public class BinListResponse implements Serializable {

    private Binlist[] binlist;

    public Binlist[] getBinlist() {
        return binlist;
    }

    public void setBinlist(Binlist[] binlist) {
        this.binlist = binlist;
    }

    @Override
    public String toString() {
        return "ClassPojo [binlist = " + binlist + "]";
    }
}
