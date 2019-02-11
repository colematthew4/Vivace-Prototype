package cole.matthew.vivace.Helpers;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

public class RecyclerViewItemTouchHelper extends ItemTouchHelper.SimpleCallback {
    private RecyclerViewItemTouchHelperListener _listener;

    public interface RecyclerViewItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link ItemTouchHelper.SimpleCallback#getSwipeDirs(RecyclerView, RecyclerView.ViewHolder)}
     * and / or {@link ItemTouchHelper.SimpleCallback#getDragDirs(RecyclerView, RecyclerView.ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link ItemTouchHelper.SimpleCallback#LEFT},
     *                  {@link ItemTouchHelper.SimpleCallback#RIGHT},
     *                  {@link ItemTouchHelper.SimpleCallback#START},
     *                  {@link ItemTouchHelper.SimpleCallback#END},
     *                  {@link ItemTouchHelper.SimpleCallback#UP} and
     *                  {@link ItemTouchHelper.SimpleCallback#DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link ItemTouchHelper.SimpleCallback#LEFT},
     *                  {@link ItemTouchHelper.SimpleCallback#RIGHT},
     *                  {@link ItemTouchHelper.SimpleCallback#START},
     *                  {@link ItemTouchHelper.SimpleCallback#END},
     *                  {@link ItemTouchHelper.SimpleCallback#UP} and
     *                  {@link ItemTouchHelper.SimpleCallback#DOWN}.
     * @param listener  The
     */
    public RecyclerViewItemTouchHelper(int dragDirs, int swipeDirs, RecyclerViewItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        _listener = listener;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            final View foregroundView = ((RecordingListRecyclerViewAdapter.RecordingViewHolder) viewHolder).getForeground();
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((RecordingListRecyclerViewAdapter.RecordingViewHolder) viewHolder).getForeground();
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    /** {@inheritDoc} */
    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        getDefaultUIUtil().clearView(((RecordingListRecyclerViewAdapter.RecordingViewHolder) viewHolder).getForeground());
    }

    /** {@inheritDoc} */
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((RecordingListRecyclerViewAdapter.RecordingViewHolder) viewHolder).getForeground();
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }

    /** {@inheritDoc} */
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        _listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    /** {@inheritDoc} */
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}