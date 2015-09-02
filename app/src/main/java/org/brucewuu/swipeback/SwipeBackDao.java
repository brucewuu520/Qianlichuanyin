package org.brucewuu.swipeback;


public interface SwipeBackDao {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    SwipeBackLayout getSwipeBackLayout();
    /**
     * set enable swipe back or not.
     * @param enable
     */
    void setSwipeBackEnable(boolean enable);
    /**
     * Scroll out contentView and finish the activity
     */
    void scrollToFinishActivity();
}
