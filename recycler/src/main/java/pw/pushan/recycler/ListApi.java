package pw.pushan.recycler;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;

import java.util.List;

/**
 * Created by pushan on 10/08/17.
 */

public final class ListApi {

    protected final DefaultItemAnimator feedItemAnimator;
    protected AppList<ViewModel> appList;
    private Context context;
    /* On refresh page is 0 and page 1 after loader is added */
    public int page;
    private LoadMoreModel loadFeedApiModel;
    private boolean isRefresh;
    private ListApiListener listApiListener;

    public ListApi(ListApiListener listApiListener) {
        this.listApiListener = listApiListener;
        this.context = listApiListener.getContext();
        appList = new AppList<>();
        loadFeedApiModel = new LoadMoreModel(this, 0);
        final DefaultItemAnimator animator = listApiListener.getAnimator();
        feedItemAnimator = animator == null ? new DefaultItemAnimator() : animator;

    }

    public void addModels(List<ViewModel> models) {
        appList.addAll(models);
    }

    public void noResult() {
        appList.noResult();
    }

    public void apiError() {
        appList.error();
    }

    public AppList<ViewModel> getAppList() {
        return appList;
    }


    /*// TODO: 13/07/17 covert below all to abstract methods
    public WebServiceHelper.Callback getCallback() {
        return null;
    }*/

    public LoadMoreModel getLoadMoreModel() {
        return loadFeedApiModel;
    }

    public void refresh() {
        isRefresh = true;
        page = 0;
        getAppList().addLoader(addNewModelsForPullToRefresh(), getLoadMoreModel());
    }

    public boolean isRefresh() {
        boolean tempIsRefresh = isRefresh;
        isRefresh = false;
        return tempIsRefresh;
    }

    public int addNewModelsForPullToRefresh() {
        return 0;
    }

    public void loaderAdded() {
        ++page;
        listApiListener.loaderAdded();
    }

    public boolean disablePullRefresh() {
        return listApiListener.disablePullRefresh();
    }

    public DefaultItemAnimator getAnimator() {
        return feedItemAnimator;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        final RecyclerView.LayoutManager layoutManager = listApiListener.getLayoutManager();
        if (layoutManager == null) {
            return getLinearLayoutManager();
        } else {
            return layoutManager;
        }
    }

    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(context) {
            @Override
            public boolean canScrollHorizontally() {
                return listApiListener.canScrollHorizontally();
            }

            @Override
            public boolean canScrollVertically() {
                return listApiListener.canScrollVertically();
            }
        };
    }

    public StaggeredGridLayoutManager getStaggeredGridLayoutManager() {
        return new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
    }

    public GridLayoutManager getGridLayoutManager(int spanCount) {
        return new GridLayoutManager(context, spanCount);
    }

    public boolean isGrid() {
        return listApiListener.isGrid();
    }

    public int getOrientation() {
        return listApiListener.getOrientation();
    }

    public int getCardSpaceDimen() {
        int dimen = listApiListener.getCardSpaceDimen();
        return dimen == -1 ? context.getResources().getDimensionPixelOffset(R.dimen.feed_space) : dimen;
    }

    public Context getContext() {
        return context;
    }


    public boolean isAutoLoad() {
        return listApiListener.isAutoLoad();
    }

    public boolean isAutoLoadMore() {
        return listApiListener.isAutoLoadMore();
    }

    public int[] getPadding() {
        return listApiListener.getPadding();
    }

    public int getBackgroundColor() {
        return listApiListener.getBackgroundColor();
    }
}
