package me.liupei.swipedismisslistview;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by liupei on 15/8/14.
 */
public class SwipeDismissListView extends ListView {

    private boolean isHorizontalScroll = false;

    private static final float HORIZONTAL_SCROLL_FACTOR = 2;

    private static final int HORIZONTAL_SCROLL_THRESHOLD = 20;

    private static final float DISMISS_FACTOR = 0.2f;

    private static final long ANIM_DURATION = 200;

    private int currentIndex = -1;

    private OnDismissListener listener;

    public SwipeDismissListView(Context context) {
        this(context, null, 0);
    }

    public SwipeDismissListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeDismissListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        float dX = ev.getX() - downX;

        if (isHorizontalScroll) {

            final View currentView = getChildAt(currentIndex);

            if (currentView != null) {

                ViewCompat.setTranslationX(currentView, dX);

            }

            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {

                if (Math.abs(dX) < getWidth() * DISMISS_FACTOR) {
                    //还原
                    xTranslationAnim(currentView, 0, null);
                } else {
                    //dismiss
                    if (dX >= 0) {
                        //向右
                        xTranslationAnim(currentView, getWidth(), new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {

                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                shorten(currentView, currentIndex + getFirstVisiblePosition() - getHeaderViewsCount());
                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        });
                    } else {
                        //向左
                        xTranslationAnim(currentView, -getWidth(), new ViewPropertyAnimatorListener() {
                            @Override
                            public void onAnimationStart(View view) {

                            }

                            @Override
                            public void onAnimationEnd(View view) {
                                shorten(currentView, currentIndex + getFirstVisiblePosition() - getHeaderViewsCount());
                            }

                            @Override
                            public void onAnimationCancel(View view) {

                            }
                        });
                    }
                }
            }

            return true;
        }

        return super.onTouchEvent(ev);
    }

    private float downX, downY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();

        float dX = ev.getX() - downX;
        float dY = ev.getY() - downY;

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {

                downX = ev.getX();
                downY = ev.getY();

                isHorizontalScroll = false;

                handleCurrentIndex();

                Toast.makeText(getContext(), "onDown:" + currentIndex + " onFirst:" + getFirstVisiblePosition(), Toast.LENGTH_SHORT).show();

                break;
            }
            case MotionEvent.ACTION_MOVE: {

                //判断点还是滑动
                if (Math.abs(dX) > HORIZONTAL_SCROLL_THRESHOLD
                        && Math.abs(dX) > Math.abs(dY) * HORIZONTAL_SCROLL_FACTOR) {
                    //水平滑动
                    isHorizontalScroll = true;

                    return true;
                }


                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {

                isHorizontalScroll = false;

                break;
            }
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private void handleCurrentIndex() {

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null && child.getTop() <= downY && downY <= child.getBottom()) {
                currentIndex = i;
                return;
            }
        }

    }

    private void xTranslationAnim(View v, float dst, ViewPropertyAnimatorListener listener) {

        ViewCompat.animate(v).translationX(dst).setDuration(ANIM_DURATION)
                .setListener(listener).start();

    }

    private class HeightAnim extends Animation {

        private View v;
        private int src, dst;

        public HeightAnim(View v, int dst, AnimationListener listener) {
            setAnimationListener(listener);
            this.v = v;
            this.dst = dst;
            this.src = v.getHeight();
            this.setDuration(ANIM_DURATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            if (hasEnded())
                return;
            v.getLayoutParams().height = (int) (src + (dst - src) * interpolatedTime);
            v.requestLayout();

        }
    }

    private void shorten(final View v, final int position) {

        v.startAnimation(new HeightAnim(v, 1, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //恢复
                v.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
                v.requestLayout();
                v.setTranslationX(0);
                if (listener != null) {
                    listener.onDismiss(position);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        }));

    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.listener = listener;
    }

    public interface OnDismissListener {

        void onDismiss(int position);

    }

}
