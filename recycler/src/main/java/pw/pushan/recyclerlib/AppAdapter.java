package pw.pushan.recyclerlib;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by pushan on 3/16/16.
 */
public class AppAdapter<T extends ViewModel> extends RecyclerView.Adapter<AppAdapter.ViewHolder> implements AdapterCallback {

    private List<T> list;
    private Map<Integer, Integer[]> mapClickListener;
    private Map<Integer, Integer[]> mapLongClickListener;
    private AppClickListener.Callback appClickListenerCallback;
    private boolean hasClickListener;
    private boolean hasLongClickListener;
    private LoadMoreModel loadMoreModel;

    public void addClickListener(Map<Integer, Integer[]> mapClickListener, Map<Integer, Integer[]> mapLongClickListener, AppClickListener.Callback appClickListenerCallback) {
        this.mapClickListener = mapClickListener;
        this.mapLongClickListener = mapLongClickListener;
        this.appClickListenerCallback = appClickListenerCallback;
    }

    private void assignClickListener(View view, int viewType, ViewHolder<ViewDataBinding> holder) {
        if (mapClickListener != null) {
            final Iterator<Map.Entry<Integer, Integer[]>> iterator = mapClickListener.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<Integer, Integer[]> next = iterator.next();
                if (next.getKey() == viewType) {
                    final Integer[] value = next.getValue();
                    for (Integer id : value) {
                        final AppClickListener appClickListener = new AppClickListener(appClickListenerCallback, holder);
                        view.findViewById(id).setOnClickListener(appClickListener);
                    }
                }
            }
        }

        if (mapLongClickListener != null) {
            final Iterator<Map.Entry<Integer, Integer[]>> iterator = mapLongClickListener.entrySet().iterator();
            final Map.Entry<Integer, Integer[]> next = iterator.next();
            if (next.getKey() == viewType) {
                final Integer[] value = next.getValue();
                for (Integer id : value) {
                    final AppClickListener appClickListener = new AppClickListener(appClickListenerCallback, holder);
                    view.findViewById(id).setOnLongClickListener(appClickListener);
                }
            }
        }
    }

    public List<T> getList() {
        return list;
    }

    public AppAdapter(List<T> list) {
        this.list = list;
    }

    public void newDataSet(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public static class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {
        private V v;

        public ViewHolder(V v) {
            super(v.getRoot());
            this.v = v;
        }

        public V getBinding() {
            return v;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).layoutId();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding bind = DataBindingUtil.bind(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        final ViewHolder<ViewDataBinding> holder = new ViewHolder<>(bind);
        assignClickListener(holder.getBinding().getRoot(), viewType, holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final T group = list.get(position);
        holder.getBinding().setVariable(BR.model, group);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public void itemAdded(int position) {
        notifyItemInserted(position);
    }

    @Override
    public void itemAdded(int from, int count) {
        notifyItemChanged(from, count);
    }

    @Override
    public void itemRemoved(int position) {
        notifyItemRemoved(position);
    }

    @Override
    public void itemRemoved(int from, int count) {
        notifyItemRangeRemoved(from, count);
    }

    @Override
    public void itemChanged(int position) {
        notifyItemChanged(position);
    }

    @Override
    public void itemChanged(int from, int count) {
        notifyItemRangeChanged(from, count);
    }

    /*@Override
    public void addLoadingLoader(LoadMoreModel.Callback callback) {
        if (loadMoreModel == null) {
            loadMoreModel = new LoadMoreModel(callback);
            int position = list.size();
            getListAsViewModel().add(loadMoreModel);
            notifyItemInserted(position);
        } else {
            loadMoreModel.isRetry.set(false);
        }
    }*/

    @Override
    public void removeLoadingLoader() {
        final int indexOf = getListAsViewModel().indexOf(loadMoreModel);
        if (indexOf != -1) {
            getListAsViewModel().remove(indexOf);
            notifyItemRemoved(indexOf);
        }
        loadMoreModel = null;
    }

    @Override
    public void chageLoadingToRetryLoader() {
        if (loadMoreModel != null) {
            loadMoreModel.isRetry.set(true);
        }
    }

    private List<ViewModel> getListAsViewModel() {
        return (List<ViewModel>) list;
    }

}
