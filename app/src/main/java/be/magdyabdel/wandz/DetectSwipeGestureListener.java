package be.magdyabdel.wandz;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MIN_SWIPE_DISTANCE_Y = 100;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    private Trainingmode activity = null;


    public Trainingmode getActivity() {
        return activity;
    }

    public void setActivity(Trainingmode activity) {
        this.activity = activity;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
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
        this.activity.changeGestureImage(5);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        this.activity.changeGestureImage(0);
        return true;
    }
}
