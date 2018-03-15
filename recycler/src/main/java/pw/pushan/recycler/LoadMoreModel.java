package pw.pushan.recycler;

import android.databinding.BindingAdapter;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by pushan on 16/05/17.
 */

public class LoadMoreModel implements ViewModel {

    public ObservableBoolean isRetry = new ObservableBoolean(false);
    private ListApi parentTownApi;
    public ObservableField<String> loadingText;
    private String loadingTextValue = "";
    public int loaderColor;
    public int layoutId;

    /**
     * Api that needs to be called
     */
    public LoadMoreModel(ListApi parentTownApi, int loadingText) {
        this.parentTownApi = parentTownApi;
        this.loadingText = new ObservableField<>();
        if (loadingText > 0) {
            setLoaderText(loadingTextValue = parentTownApi.getContext().getString(loadingText));
        }
        layoutId = R.layout.row_feed_loader;
    }

    public void setLoaderText(String loadingText) {
        this.loadingText.set(loadingText);
    }

    public void onRetryClick(View view) {
        isRetry.set(false);
        loadingText.set(loadingTextValue);
        parentTownApi.loaderAdded();
    }

    public void stopLoading() {

    }

    /*@BindingAdapter("bind:load")
    public static void showLoadingVoid(AVLoadingIndicatorView avLoadingIndicatorView, LoadMoreModel loadMoreModel) {
        loadMoreModel.avLoadingIndicatorView = avLoadingIndicatorView;
        avLoadingIndicatorView.smoothToShow();
    }*/


    public ListApi getParentTownApi() {
        return parentTownApi;
    }

    @Override
    public int layoutId() {
        return layoutId;
    }

    public void setToRetry() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingText.set("");
                stopLoading();
                isRetry.set(true);

            }
        }, 1000);
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
}
