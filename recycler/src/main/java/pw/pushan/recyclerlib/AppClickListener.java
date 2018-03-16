package pw.pushan.recyclerlib;

import android.view.View;


public class AppClickListener implements View.OnClickListener, View.OnLongClickListener {

    private AppAdapter.ViewHolder holder;
    private Callback callback;

    public interface Callback {
        void handleClick(View view, int postion, boolean isLongClick);
    }

    public AppClickListener(Callback callback, AppAdapter.ViewHolder holder) {
        this.callback = callback;
        this.holder = holder;
    }

    @Override
    public void onClick(View v) {
        callback.handleClick(v, holder.getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        callback.handleClick(v, holder.getAdapterPosition(), true);
        return true;
    }

}