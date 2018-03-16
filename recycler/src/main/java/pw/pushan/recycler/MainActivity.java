package pw.pushan.recycler;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import pw.pushan.recycler.model.SimpleViewModel;
import pw.pushan.recyclerlib.AppRecyclerView;
import pw.pushan.recyclerlib.ListApi;
import pw.pushan.recyclerlib.ListApiListener;
import pw.pushan.recyclerlib.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListApi listApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppRecyclerView appRecyclerView = findViewById(R.id.rv);
        listApi = createListApi();
        appRecyclerView.init(listApi);
    }

    private ListApi createListApi() {
        return new ListApi(new ListApiListener() {
            @Override
            public int getCardSpaceDimen() {
                return 0;
            }

            @Override
            public int getOrientation() {
                return 0;
            }

            @Override
            public Context getContext() {
                return null;
            }

            @Override
            public DefaultItemAnimator getAnimator() {
                return null;
            }

            @Override
            public boolean isGrid() {
                return false;
            }

            @Override
            public boolean isAutoLoad() {
                return true;
            }

            @Override
            public boolean isAutoLoadMore() {
                return false;
            }

            @Override
            public int addNewModelsForPullToRefresh() {
                return 0;
            }

            @Override
            public void loaderAdded() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<ViewModel> models = new ArrayList<>();
                        models.add(new SimpleViewModel());
                        listApi.addModels(models);
                    }
                }, 4000);

            }

            @Override
            public boolean disablePullRefresh() {
                return false;
            }

            @Override
            public int[] getPadding() {
                return new int[0];
            }

            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }

            @Override
            public RecyclerView.LayoutManager getLayoutManager() {
                return null;
            }

            @Override
            public int getBackgroundColor() {
                return 0;
            }
        });
    }
}
