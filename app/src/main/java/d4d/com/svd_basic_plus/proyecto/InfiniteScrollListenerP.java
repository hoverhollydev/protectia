package d4d.com.svd_basic_plus.proyecto;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InfiniteScrollListenerP extends RecyclerView.OnScrollListener {

    private final static int VISIBLE_THRESHOLD = 2;
    private LinearLayoutManager linearLayoutManager;
    private boolean loading; // LOAD MORE Progress dialog
    private OnLoadMoreListener listener;
    private boolean pauseListening = false;


    private boolean END_OF_FEED_ADDED = false;
    private int NUM_LOAD_ITEMS = 10;

    public InfiniteScrollListenerP(LinearLayoutManager linearLayoutManager, OnLoadMoreListener listener) {
        this.linearLayoutManager = linearLayoutManager;
        this.listener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx == 0 && dy == 0)
            return;
        int totalItemCount = linearLayoutManager.getItemCount();
        //int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
        int firtsVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        if (!loading && firtsVisibleItem==0 + VISIBLE_THRESHOLD && totalItemCount != 0 && !END_OF_FEED_ADDED && !pauseListening) {
            //recyclerView.scrollToPosition(12);
            if (listener != null) {
                listener.onLoadMore();
            }
            loading = true;
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public void addEndOfRequests() {
        this.END_OF_FEED_ADDED = true;
    }

    public void pauseScrollListener(boolean pauseListening) {
        this.pauseListening = pauseListening;
    }
}
