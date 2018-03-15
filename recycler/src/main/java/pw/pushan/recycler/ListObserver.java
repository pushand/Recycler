package pw.pushan.recycler;

/**
 * Created by pushan on 13/07/17.
 */

public interface ListObserver {

    void adding();
    void noResult();
    void error();

    void itemsAdded(int from, int count);
    void itemsRemoved(int from, int count);
    void itemAdded(int position);
    void itemRemoved(int position);
    void itemChanged(int position, String payLoad);
    void listCleared();
    void moveToPosition(int position);

    void loaderAdded(int position, LoadMoreModel loadMoreModel);

    void switchLayoutManager(int index);

}
