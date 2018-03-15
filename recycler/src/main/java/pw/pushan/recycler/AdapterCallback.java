package pw.pushan.recycler;

/**
 * Created by pushan on 16/05/17.
 */

public interface AdapterCallback {

    void itemAdded(int position);
    void itemAdded(int from, int count);
    void itemRemoved(int position);
    void itemRemoved(int from, int count);
    void itemChanged(int position);
    void itemChanged(int from, int count);
    //void addLoadingLoader(LoadMoreModel.Callback callback);
    void removeLoadingLoader();
    void chageLoadingToRetryLoader();

}
