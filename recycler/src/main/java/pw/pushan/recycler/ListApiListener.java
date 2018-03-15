package pw.pushan.recycler;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by pushan on 09/01/18.
 * This listener is what list api wants form other party if not it will use default values
 */

public interface ListApiListener {

    int getCardSpaceDimen();
    int getOrientation();
    Context getContext();
    DefaultItemAnimator getAnimator();
    boolean isGrid();
    boolean isAutoLoad();
    boolean isAutoLoadMore();
    int addNewModelsForPullToRefresh();
    void loaderAdded();
    boolean disablePullRefresh();
    int[] getPadding();
    boolean canScrollHorizontally();
    boolean canScrollVertically();
    RecyclerView.LayoutManager getLayoutManager();
    int getBackgroundColor();

}
