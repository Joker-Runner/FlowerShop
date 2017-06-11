package com.joker.flowershop.adapter.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

/**
 * Created by joker on 5/23 0023.
 */

public class WrapContentLinearLayoutManager extends LinearLayoutManager {
    public WrapContentLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
        }
    }
//    java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder{3c9234e3 position=1 id=-1, oldPos=-1, pLpos:-1 no parent}
//    at android.support.v7.widget.RecyclerView$Recycler.validateViewHolderForOffsetPosition(RecyclerView.java:5297)
//    at android.support.v7.widget.RecyclerView$Recycler.tryGetViewHolderForPositionByDeadline(RecyclerView.java:5479)
//    at android.support.v7.widget.GapWorker.prefetchPositionWithDeadline(GapWorker.java:282)
//    at android.support.v7.widget.GapWorker.flushTaskWithDeadline(GapWorker.java:336)
//    at android.support.v7.widget.GapWorker.flushTasksWithDeadline(GapWorker.java:349)
//    at android.support.v7.widget.GapWorker.prefetch(GapWorker.java:356)
//    at android.support.v7.widget.GapWorker.run(GapWorker.java:387)
//    at android.os.Handler.handleCallback(Handler.java:739)
//    at android.os.Handler.dispatchMessage(Handler.java:95)
//    at android.os.Looper.loop(Looper.java:135)
//    at android.app.ActivityThread.main(ActivityThread.java:5248)
//    at java.lang.reflect.Method.invoke(Native Method)
//    at java.lang.reflect.Method.invoke(Method.java:372)
//    at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:909)
//    at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:704)
}
