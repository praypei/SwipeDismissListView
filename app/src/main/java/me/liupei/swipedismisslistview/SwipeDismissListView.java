package me.liupei.swipedismisslistview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
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

        float x = ev.getX();
        float y = ev.getY();

        System.out.println("onTouchEvent:" + ev);

        if (isHorizontalScroll) {

            final View currentView = getChildAt(currentIndex);

            if (currentView != null) {

                currentView.setTranslationX(x - downX);

            }

            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {

                if (Math.abs(x - downX) < getWidth() * DISMISS_FACTOR) {
                    //还原
                    xTranslationAnim(currentView, 0, null);
                } else {
                    //dismiss
                    if (x - downX >= 0) {
                        //向右
                        xTranslationAnim(currentView, getWidth(), new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                shorten(currentView, currentIndex + getFirstVisiblePosition() - getHeaderViewsCount());
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    } else {
                        //向左
                        xTranslationAnim(currentView, -getWidth(), new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                shorten(currentView, currentIndex + getFirstVisiblePosition() - getHeaderViewsCount());
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

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

        float x = ev.getX();
        float y = ev.getY();

        System.out.println("onInterceptTouchEvent:" + ev);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {

                downX = x;
                downY = y;

                isHorizontalScroll = false;

                handleCurrentIndex();

                Toast.makeText(getContext(), "onDown:" + currentIndex + " onFirst:" + getFirstVisiblePosition(), Toast.LENGTH_SHORT).show();

                break;
            }
            case MotionEvent.ACTION_MOVE: {

                //判断点还是滑动
                if (Math.abs(x - downX) > HORIZONTAL_SCROLL_THRESHOLD
                        && Math.abs(x - downX) > Math.abs(y - downY) * HORIZONTAL_SCROLL_FACTOR) {
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

    private void xTranslationAnim(View v, float desc, Animator.AnimatorListener listener) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "translationX", v.getTranslationX(), desc);
        anim.setDuration(ANIM_DURATION);
        if (listener != null)
            anim.addListener(listener);
        anim.start();
    }

    private class HeightAnim extends Animation {

        private View v;
        private int src, desc;

        public HeightAnim(View v, int desc, AnimationListener listener) {
            setAnimationListener(listener);
            this.v = v;
            this.desc = desc;
            this.src = v.getHeight();
            this.setDuration(ANIM_DURATION);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            if (hasEnded())
                return;
            v.getLayoutParams().height = (int) (src + (desc - src) * interpolatedTime);
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
                v.getLayoutParams().height = -2;
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

        public void onDismiss(int position);

    }

}
