package pw.pushan.recyclerlib;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.*;
import android.util.AttributeSet;

/**
 * Created by pushan on 13/07/17.
 */

public class AppRecyclerView extends RecyclerView implements ListObserver {

    public ListApi listApi;
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private boolean autoLoadMore;
    //// TODO: 18/07/17 dont allow loading until response is received. this is for answer only
    //private boolean loading;
    private AppAdapter<ViewModel> appAdapter;
    private ViewModel loaderModel;
    private VerticalSpaceItemDecoration verticalSpaceItemDecoration;

    public AppRecyclerView(Context context) {
        super(context);
    }

    public AppRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AppRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init(ListApi api) {
        this.listApi = api;
        final AppList<ViewModel> appList = api.getAppList();
        appList.attach(this);
        this.autoLoadMore = listApi.isAutoLoadMore();
        final LayoutManager layoutManager = setListLayoutManager();
        verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(api.getCardSpaceDimen());
        setLayoutManager(layoutManager instanceof  GridLayoutManager ? (GridLayoutManager)layoutManager : layoutManager);
        addItemDecoration(verticalSpaceItemDecoration);
        DefaultItemAnimator animator = api.getAnimator();
        if (animator == null) {
            animator = new DefaultItemAnimator();
        }
        int padding[] = listApi.getPadding();
        if (padding.length == 4) {
            setPadding(padding[0], padding[1], padding[2], padding[3]);
        } else {
            final int dimensionPixelOffset = getResources().getDimensionPixelOffset(R.dimen.feed_space);
            setPadding(dimensionPixelOffset, 0, dimensionPixelOffset, 0);
        }
        setItemAnimator(animator);
        appAdapter = new AppAdapter<>(appList.getList());
        this.loaderModel = api.getLoadMoreModel();
        setAdapter(appAdapter);
        if (api.getBackgroundColor() != 0) {
            setBackgroundColor(ContextCompat.getColor(getContext(), api.getBackgroundColor()));
        }
        //PATCH when fragment is replaced from backstack The api is called again. to avoid that we check
        //if api page count is more than 0 meaning api wall called and we not need to call it again
        if (api.isAutoLoad() && api.page == 0) {
            post(runnableLoader);
        }
    }

    private LayoutManager setListLayoutManager() {
        boolean isLinear = true;
        linearLayoutManager = null;
        staggeredGridLayoutManager = null;
        final LayoutManager layoutManager = listApi.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            linearLayoutManager = (GridLayoutManager) layoutManager;
        } else if (layoutManager instanceof LinearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) layoutManager;
        } else {
            isLinear = false;
            staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        }
        if (isLinear && !(layoutManager instanceof GridLayoutManager)) {
            linearLayoutManager.setOrientation(listApi.getOrientation());
        } else if (!isLinear) {
            staggeredGridLayoutManager.setOrientation(listApi.getOrientation());
        }
        return isLinear ? linearLayoutManager : staggeredGridLayoutManager;
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        //check for scroll down
        if (linearLayoutManager != null) {
            final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            if (autoLoadMore && dy > 0 && lastVisibleItemPosition != -1 && lastVisibleItemPosition == linearLayoutManager.getItemCount() - 1) {
                //Log.log("count", "lastVisibleItemPosition " + lastVisibleItemPosition + " adapterCount " + linearLayoutManager.getItemCount());
                post(runnableLoader);
            }
        } else if (staggeredGridLayoutManager != null) {
            final int[] lastVisibleItemPosition = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
            if (autoLoadMore && dy > 0 && lastVisibleItemPosition[1] != -1 && lastVisibleItemPosition[1] == staggeredGridLayoutManager.getItemCount() - 1) {
                //Log.log("count", "lastVisibleItemPosition " + lastVisibleItemPosition + " adapterCount " + linearLayoutManager.getItemCount());
                post(runnableLoader);
            }
        }
    }

    /**
     * This case comes into action when there's only one item in list and list is not scrollable and list has more items to get via api
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (linearLayoutManager != null) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                if (autoLoadMore && /*dy > 0 && */lastVisibleItemPosition != -1 && lastVisibleItemPosition == linearLayoutManager.getItemCount() - 1) {
                    //Log.log("count", "lastVisibleItemPosition " + lastVisibleItemPosition + " adapterCount " + linearLayoutManager.getItemCount());
                    post(runnableLoader);
                }
            }
        } else if (staggeredGridLayoutManager != null) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                final int[] lastVisibleItemPosition = staggeredGridLayoutManager.findLastVisibleItemPositions(null);
                if (autoLoadMore && /*dy > 0 && */lastVisibleItemPosition[1] != -1 && lastVisibleItemPosition[1] == linearLayoutManager.getItemCount() - 1) {
                    //Log.log("count", "lastVisibleItemPosition " + lastVisibleItemPosition + " adapterCount " + linearLayoutManager.getItemCount());
                    post(runnableLoader);
                }
            }
        }
    }

    private Runnable runnableLoader = new Runnable() {
        @Override
        public void run() {
            final AppList<ViewModel> appList = listApi.getAppList();
            appList.addLoader(appList.size(), loaderModel);
        }
    };


    private void removeLoader(boolean retry, boolean noResult) {
        listApi.getAppList().removeLoader(listApi.isRefresh() ? listApi.addNewModelsForPullToRefresh() : -1, retry, noResult);
    }

    @Override
    public void adding() {
        removeLoader(false, false);
    }

    @Override
    public void noResult() {
        removeLoader(false, true);
    }

    @Override
    public void error() {
        // TODO: 13/07/17 change loader into retry
        //--page
        removeLoader(true, false);
    }

    @Override
    public void loaderAdded(final int position, LoadMoreModel loadMoreModel) {
        appAdapter.notifyItemInserted(position);
        if (!autoLoadMore) this.smoothScrollToPosition(position);
        loadMoreModel.getListApi().loaderAdded();
    }

    @Override
    public void switchLayoutManager(int index) {
        final LayoutManager layoutManager = setListLayoutManager();
        setLayoutManager(layoutManager);
        verticalSpaceItemDecoration.setmVerticalSpaceHeight(listApi.getCardSpaceDimen());
        if (layoutManager instanceof LinearLayoutManager) {
            final int firstVisibleItemPosition = index == -1 ? linearLayoutManager.findFirstVisibleItemPosition() : index;
            scrollToPosition(firstVisibleItemPosition);
        } else {
            // TODO: 08/02/18 handle staggered layout manager
        }
    }

    @Override
    public void itemsAdded(int from, int count) {
        appAdapter.notifyItemRangeInserted(from, count);
    }

    @Override
    public void itemsRemoved(int from, int count) {
        appAdapter.notifyItemRangeRemoved(from, count);
    }

    @Override
    public void itemAdded(int position) {
        appAdapter.notifyItemInserted(position);
    }

    @Override
    public void
    itemRemoved(int position) {
        appAdapter.notifyItemRemoved(position);
    }

    @Override
    public void itemChanged(int position, String payLoad) {
        appAdapter.notifyItemChanged(position, payLoad);
    }

    @Override
    public void listCleared() {
        appAdapter.notifyDataSetChanged();
    }

    @Override
    public void moveToPosition(int position) {
        scrollToPosition(position);
    }


}
