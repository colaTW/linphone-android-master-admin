package org.linphone.assistant;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

/**
 * Created on 15.08.2016.
 *
 * @author Slawomir Onyszko
 */
public class ExpandableListViewSynchronisationTouchListener
        implements View.OnTouchListener,
                ExpandableListView.OnChildClickListener,
                ExpandableListView.OnGroupClickListener,
                ExpandableListView.OnGroupExpandListener {

    private static final String TAG =
            ExpandableListViewSynchronisationTouchListener.class.getSimpleName();

    private Context context;

    private View clickSource;
    private View touchSource;

    private View firstExpandable;
    private View secondExpandable;
    private View thirdExpandable;

    public ExpandableListViewSynchronisationTouchListener(
            Context context, View fistExpandable, View secondExpandable, View thirdExpandable) {
        this.context = context;
        this.firstExpandable = fistExpandable;
        this.secondExpandable = secondExpandable;
        this.thirdExpandable = thirdExpandable;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (touchSource == null) {
            touchSource = view;
        }
        if (view == touchSource) {

            if (view == firstExpandable) {
                secondExpandable.dispatchTouchEvent(motionEvent);
                thirdExpandable.dispatchTouchEvent(motionEvent);

            } else if (view == secondExpandable) {
                firstExpandable.dispatchTouchEvent(motionEvent);
                thirdExpandable.dispatchTouchEvent(motionEvent);
            } else if (view == thirdExpandable) {
                firstExpandable.dispatchTouchEvent(motionEvent);
                secondExpandable.dispatchTouchEvent(motionEvent);
            }

            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                clickSource = view;
                touchSource = null;
            }
        }

        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {

        if (parent == clickSource) {
            if (parent == firstExpandable) {
                Toast.makeText(
                                context,
                                "First expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (parent == secondExpandable) {
                Toast.makeText(
                                context,
                                "Second expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (parent == thirdExpandable) {
                Toast.makeText(
                                context,
                                "Third expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }

        return false;
    }

    @Override
    public boolean onChildClick(
            ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

        if (parent == clickSource) {
            if (parent == firstExpandable) {
                Toast.makeText(
                                context,
                                "First expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition
                                        + " child position: "
                                        + childPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (parent == secondExpandable) {
                Toast.makeText(
                                context,
                                "Second expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition
                                        + " child position: "
                                        + childPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            } else if (parent == thirdExpandable) {
                Toast.makeText(
                                context,
                                "Third expandable id: "
                                        + view.getId()
                                        + " group position: "
                                        + groupPosition
                                        + " child position: "
                                        + childPosition,
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }

        return false;
    }

    @Override
    public void onGroupExpand(int groupPosition) {
        Log.d(TAG, "Group expand: " + groupPosition);
    }
}
