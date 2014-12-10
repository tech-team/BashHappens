package org.techteam.bashhappens.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.media.Rating;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techteam.bashhappens.R;


public class RatingView extends FrameLayout {

    public interface Listener {
        void ratingViewStateChanged(RatingView ratingView, State oldState, State newState);
    }

    private Listener listener;

    public enum State {IDLE, LIKED, DISLIKED}

    private String value = "0";
    private State state = State.IDLE;

    private ImageButton likeButton;
    private TextView ratingText;
    private ImageButton dislikeButton;

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RatingView,
                0, 0);

        try {
            value = a.getText(R.styleable.RatingView_value).toString();
            int stateOrdinal = a.getInteger(R.styleable.RatingView_state, state.ordinal());
            state = State.values()[stateOrdinal];
        } finally {
            a.recycle();
        }

        init(context);
    }


    private void init(Context context) {
        View.inflate(context, R.layout.rating_view, this);

        likeButton = (ImageButton) findViewById(R.id.post_like);
        dislikeButton = (ImageButton) findViewById(R.id.post_dislike);

        ratingText = (TextView) findViewById(R.id.post_rating);

        likeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == State.IDLE) {
                    setState(State.LIKED);

                    if (listener != null)
                        listener.ratingViewStateChanged(RatingView.this, State.IDLE, state);
                }
            }
        });

        dislikeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == State.IDLE) {
                    setState(State.DISLIKED);

                    if (listener != null)
                        listener.ratingViewStateChanged(RatingView.this, State.IDLE, state);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;

        ratingText.setText(this.value);

        invalidate();
        requestLayout();
    }

    public State getState() {
        return state;
    }

    /**
     * Changes current control state with calling listener
     * @param state new state
     */
    public void setState(State state) {
        State oldState = this.state;
        _setState(state);
        listener.ratingViewStateChanged(this, oldState, state);
    }

    /**
     * Changes current control state without calling listener
     * @param state new state
     */
    public void _setState(State state) {
        this.state = state;

        invalidate();
        requestLayout();
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).layout(0, 0, r - l, b - t);
        }
    }
}

