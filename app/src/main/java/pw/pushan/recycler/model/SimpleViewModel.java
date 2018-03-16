package pw.pushan.recycler.model;

import pw.pushan.recycler.R;
import pw.pushan.recyclerlib.ViewModel;

public class SimpleViewModel implements ViewModel {

    public String name = "Pushan";

    @Override
    public int layoutId() {
        return R.layout.row_simple_layout;
    }
}
