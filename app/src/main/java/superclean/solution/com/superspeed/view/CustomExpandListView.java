package superclean.solution.com.superspeed.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import superclean.solution.com.superspeed.controller.IDockingController;
import superclean.solution.com.superspeed.controller.IDockingHeaderUpdateListener;


/**
 * Created by admin on 2017/10/21.
 */

public class CustomExpandListView extends ExpandableListView implements AbsListView.OnScrollListener {
    private View mDockingHeader;
    private int mDockingHeaderWidth;
    private int mDockingHeaderHeight;
    private boolean mDockingHeaderVisible;
    private int mDockingHeaderState = IDockingController.DOCKING_HEADER_HIDDEN;

    private IDockingHeaderUpdateListener mListener;

    public CustomExpandListView (Context context) {
        this(context, null);
    }

    public CustomExpandListView (Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomExpandListView (Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnScrollListener(this);
    }

    public void setDockingHeader (View header, IDockingHeaderUpdateListener listener) {
        mDockingHeader = header;
        mListener = listener;
    }

    @Override
    protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if ( mDockingHeader != null ) {
            measureChild(mDockingHeader, widthMeasureSpec, heightMeasureSpec);
            mDockingHeaderWidth = mDockingHeader.getMeasuredWidth();
            mDockingHeaderHeight = mDockingHeader.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout (boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if ( mDockingHeader != null ) {
            mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);
        }
    }

    @Override
    protected void dispatchDraw (Canvas canvas) {
        super.dispatchDraw(canvas);
        if ( mDockingHeaderVisible ) {
            drawChild(canvas, mDockingHeader, getDrawingTime());
        }
    }

    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        long packedPosition = getExpandableListPosition(firstVisibleItem);
        int groupPosition = getPackedPositionGroup(packedPosition);
        int childPosition = getPackedPositionChild(packedPosition);
        updateDockingHeader(groupPosition, childPosition);
    }

    @Override
    public void onScrollStateChanged (AbsListView view, int scrollState) {

    }

    private void updateDockingHeader (int groupPosition, int childPosition) {
        if ( getExpandableListAdapter() == null ) {
            return;
        }
        if ( getExpandableListAdapter() instanceof IDockingController ) {
            IDockingController dockingController = (IDockingController) getExpandableListAdapter();
            mDockingHeaderState = dockingController.getDockingState(groupPosition, childPosition);
            switch ( mDockingHeaderState ) {
                case IDockingController.DOCKING_HEADER_HIDDEN:
                    mDockingHeaderVisible = false;
                    break;
                case IDockingController.DOCKING_HEADER_DOCKED:
                    if ( mListener != null ) {
                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));
                    }
                    mDockingHeader.measure(MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));
                    mDockingHeader.layout(0, 0, mDockingHeaderWidth, mDockingHeaderHeight);
                    mDockingHeaderVisible = true;
                    break;
                case IDockingController.DOCKING_HEADER_DOCKING:
                    if ( mListener != null ) {
                        mListener.onUpdate(mDockingHeader, groupPosition, isGroupExpanded(groupPosition));
                    }

                    View firstVisibleView = getChildAt(0);
                    int yOffset;
                    if ( firstVisibleView != null && firstVisibleView.getBottom() < mDockingHeaderHeight ) {
                        yOffset = firstVisibleView.getBottom() - mDockingHeaderHeight;
                    } else {
                        yOffset = 0;
                    }
                    mDockingHeader.measure(MeasureSpec.makeMeasureSpec(mDockingHeaderWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(mDockingHeaderHeight, MeasureSpec.AT_MOST));
                    mDockingHeader.layout(0, yOffset, mDockingHeaderWidth, mDockingHeaderHeight + yOffset);
                    mDockingHeaderVisible = true;
                    break;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent ev) {
        if ( ev.getAction() == MotionEvent.ACTION_DOWN && mDockingHeaderVisible ) {
            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);
            if ( rect.contains((int) ev.getX(), (int) ev.getY()) && mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED ) {
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent (MotionEvent ev) {
        if ( mDockingHeaderVisible ) {
            Rect rect = new Rect();
            mDockingHeader.getDrawingRect(rect);
            switch ( ev.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    if ( rect.contains((int) ev.getX(), (int) ev.getY()) ) {
                        return true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
                    int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
                    if ( rect.contains((int) ev.getX(), (int) ev.getY()) && mDockingHeaderState == IDockingController.DOCKING_HEADER_DOCKED ) {
                        if ( isGroupExpanded(groupPos) ) {
                            collapseGroup(groupPos);
                        } else {
                            expandGroup(groupPos);
                        }
                        return true;
                    }
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }
}