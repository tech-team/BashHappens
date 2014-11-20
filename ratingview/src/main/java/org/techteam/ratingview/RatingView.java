package org.techteam.ratingview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;



public class RatingView extends View {

    private Paint textPaint;

    public interface OnStateChanged {
        void ratingViewStateChanged(RatingView ratingView, State oldState, State newState);
    }

    private OnStateChanged stateChangedListener;

    public enum State {IDLE, LIKED, DISLIKED}

    private int value = 0;
    private State state = State.IDLE;

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RatingView,
                0, 0);

        try {
            value = a.getInteger(R.styleable.RatingView_value, value);
            int stateOrdinal = a.getInteger(R.styleable.RatingView_state, state.ordinal());
            state = State.values()[stateOrdinal];
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        //setup paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xff000000);
//        if (mTextHeight == 0) {
//            mTextHeight = textPaint.getTextSize();
//        } else {
//            textPaint.setTextSize(mTextHeight);
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw contour
        canvas.drawRect(0, 0, 20, 30, textPaint);

        //draw dislike

        //draw text

        //draw like
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Context context = getContext();

        float w = UnitConverter.toPx(48*2, context);
        float h = UnitConverter.toPx(48, context);

        setMeasuredDimension((int)w, (int)h);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;

        invalidate();
        requestLayout();
    }

    public State getState() {
        return state;
    }

    /**
     * Changes current control state with calling stateChangedListener
     * @param state new state
     */
    public void setState(State state) {
        State oldState = this.state;
        _setState(state);
        stateChangedListener.ratingViewStateChanged(this, oldState, state);
    }

    /**
     * Changes current control state without calling stateChangedListener
     * @param state new state
     */
    public void _setState(State state) {
        this.state = state;

        invalidate();
        requestLayout();
    }

    public OnStateChanged getStateChangedListener() {
        return stateChangedListener;
    }

    public void setStateChangedListener(OnStateChanged stateChangedListener) {
        this.stateChangedListener = stateChangedListener;
    }
}

