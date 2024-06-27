package com.example.shivambhardwaj.shitube;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeGuesture implements View.OnTouchListener {
    private final GestureDetector gestureDetector;
    public OnSwipeGuesture(Context context){
        gestureDetector=new GestureDetector(context,new GestureListenor());
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
    public final class GestureListenor extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(@NonNull MotionEvent e) {
            doubleTap();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
            singleTap();
            return super.onSingleTapConfirmed(e);
        }
        public void doubleTap(){}
        public void singleTap(){}
    }
}
