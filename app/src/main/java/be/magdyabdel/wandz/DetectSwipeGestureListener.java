package be.magdyabdel.wandz;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static int MIN_SWIPE_DISTANCE_X = 200;
    private static int MAX_SWIPE_DISTANCE_X = 1000;

    private LearnGesture activity = null;

    public LearnGesture getActivity() {
        return activity;
    }

    public void setActivity(LearnGesture activity) {
        this.activity = activity;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float deltaX = e1.getX() - e2.getX();
        float deltaY = e1.getY() - e2.getY();

        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        if (deltaXAbs >= MIN_SWIPE_DISTANCE_X && deltaX <= MAX_SWIPE_DISTANCE_X) {
            if (deltaX > 0) {
                this.activity.changeGestureImage(1);
            } else {
                this.activity.changeGestureImage(-1);
            }
        }
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        this.activity.changeGestureImage(0);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.activity.changeGestureImage(0);
        return true;
    }
}
