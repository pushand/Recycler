package pw.pushan.recycler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pushan on 13/07/17.
 */

public class AppList<T extends ViewModel> {

    private ListObserver observers;
    //private List<ListObserver> observers = new ArrayList<>();
    private ArrayList<T> list;
    private T loader;
    private int addPosition;

    public AppList() {
        list = new ArrayList<>();
    }

    /**
     * Should only be used by App Recyceler view
     */
    public ArrayList<T> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

    public T get(int index) {
        return list.get(index);
    }

    public void attach(ListObserver observer) {
        //observers.add(observer);
        observers = observer;
    }

    public void switchLayoutManager(T t) {
        notifySwitchLayoutManager(t != null ? list.indexOf(t) : -1);
    }

    public void changed(T t, String payload) {
        final int indexOf = list.indexOf(t);
        if (indexOf != -1) {
            notifyChanged(indexOf, payload);
        }
    }

    public boolean add(T t) {
        notifyAdding();
        int position = getAddPositionOfLoader();
        list.add(position, t);
        notifyItemAddedObservers(position);
        return true;
    }

    /** This method adds view model to index without removing loader */
    public void add(int index, T t) {
        list.add(index, t);
        notifyItemAddedObservers(index);
    }

    public boolean addAll(List<T> t) {
        notifyAdding();
        if (t.size() > 0) {
            int position = getAddPositionOfLoader();
            boolean result = list.addAll(position, t);
            int count = t.size();
            if (result) notifyItemsAddedObservers(position, count);
            return result;
        } else {
            return false;
        }
    }

    public boolean addAll(int index, List<T> t) {
        if (t.size() > 0) {
            boolean result = list.addAll(index, t);
            int count = t.size();
            if (result) notifyItemsAddedObservers(index, count);
            return result;
        } else {
            return false;
        }
    }

    public boolean remove(T t) {
        int position = list.indexOf(t);
        if (position != -1) {
            final T remove = list.remove(position);
            if (remove != null) notifyItemRemovedObservers(position);
            return remove != null;
        } else {
            return false;
        }
    }

    public boolean addLoader(int index, T t) {
        if (loader == null) {
            this.loader = t;
            list.add(index, t);
            notifyLoaderAddedObservers(index, t);
            return true;
        } else {
            return false;
        }
    }

    /**
     * It 1. Changes loader to retry
     * 2. Removes all item from a position to list end for pull to refresh
     * 3. Removes loader
     *
     * @param clearListFrom signifies two things
     *                      1. if clearListFrom > -1 means its pull to refresh hence we need to remove all other items else point 2 is skipped
     *                      2. To removel all other items we need position where to remove from which is defined in clearListFrom param itself
     */
    public void removeLoader(int clearListFrom, boolean retry, boolean noResult) {
        addPosition = loader != null ? list.indexOf(loader) : -1;
        if (addPosition != -1) {
            if (retry) {
                final LoadMoreModel remove = (LoadMoreModel) loader;
                if (remove.getParentTownApi() != null) {
                    --remove.getParentTownApi().page;
                }
                remove.setToRetry();
            } else if (clearListFrom > -1) {
                clearList(clearListFrom);
            } else {
                final LoadMoreModel remove = (LoadMoreModel) list.remove(addPosition);
                remove.stopLoading();
                notifyItemRemovedObservers(addPosition);
                loader = null;
                if (noResult) {
                    --remove.getParentTownApi().page;
                }
            }
        }
    }

    /**
     * Clears list from position and stops loader. Will stop loader loading animation but May not remove loader if position of loader is less than clearListFrom
     */
    public void clearList(int clearListFrom) {
        if (loader != null) {
            final LoadMoreModel remove = (LoadMoreModel) loader;
            remove.stopLoading();
        }
        int count = list.size();
        list.subList(clearListFrom, list.size()).clear();
        notifyItemsRemovedObservers(clearListFrom, count);
        loader = null;
    }

    public int getAddPositionOfLoader() {
        return addPosition == -1 ? list.size() : addPosition;
    }

    public int indexOf(T t) {
        return list.indexOf(t);
    }

    //// TODO: 13/07/17 removing should be in series like 1-10 9-12 etc
    /*@Override
    public boolean removeAll(Collection<?> c) {
        int position = size();
        boolean result = super.removeAll(c);
        int count = c.size();
        if (result) notifyItemsRemovedObservers(position, count);
        return result;
    }*/

    public void notifySwitchLayoutManager(int index) {
        if (observers != null) {
            observers.switchLayoutManager(index);
        }
    }

    public void notifyLoaderAddedObservers(int position, T t) {
        if (observers != null) {
            observers.loaderAdded(position, (LoadMoreModel) t);
        }
    }

    public void notifyItemAddedObservers(int position) {
        if (observers != null) {
            observers.itemAdded(position);
        }
    }

    public void notifyItemsAddedObservers(int from, int count) {
        if (observers != null) {
            observers.itemsAdded(from, count);
        }
    }

    public void notifyItemRemovedObservers(int position) {
        if (observers != null) {
            observers.itemRemoved(position);
        }
    }

    public void notifyItemsRemovedObservers(int from, int count) {
        if (observers != null) {
            observers.itemsRemoved(from, count);
        }
    }

    private void notifyChanged(int position, String payload) {
        if (observers != null) {
            observers.itemChanged(position, payload);
        }
    }

    private void notifyListCleared() {
        if (observers != null) {
            observers.listCleared();
        }
    }

    private void notifyAdding() {
        if (observers != null) {
            observers.adding();
        }
    }

    public void noResult() {
        if (observers != null) {
            observers.noResult();
        }
    }

    public void error() {
        if (observers != null) {
            observers.error();
        }
    }

    public void moveToPosition(int position) {
        if (observers != null) {
            observers.moveToPosition(position);
        }
        // TODO: 28/08/17 As of now this method is only called when wheel is added. so if user is scrolling and wheel is added than shall we force scroll to position
    }
}
