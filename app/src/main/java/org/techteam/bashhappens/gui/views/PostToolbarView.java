package org.techteam.bashhappens.gui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.techteam.bashhappens.R;

import java.util.List;


public class PostToolbarView extends FrameLayout {

    public interface Listener {
        void likePressed(PostToolbarView view);
        void dislikePressed(PostToolbarView view);
        void bayanPressed(PostToolbarView view);
        void sharePressed(PostToolbarView view);
        void favPressed(PostToolbarView view);
    }

    private Listener listener;

    private RatingView ratingView;
    private Button bayanButton;
    private ImageButton shareButton;
    private ImageButton favButton;

    private boolean bayaned;
    private boolean faved;

    public PostToolbarView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PostToolbarView,
                0, 0);

        try {
            String rating = a.getString(R.styleable.PostToolbarView_rating);
            ratingView.setValue(rating);

            boolean liked = a.getBoolean(R.styleable.PostToolbarView_liked, false);
            if (liked)
                ratingView._setState(RatingView.State.LIKED);

            boolean disliked = a.getBoolean(R.styleable.PostToolbarView_disliked, false);
            if (disliked)
                ratingView._setState(RatingView.State.DISLIKED);

            _setBayaned(a.getBoolean(R.styleable.PostToolbarView_bayaned, false));
            _setFaved(a.getBoolean(R.styleable.PostToolbarView_faved, false));
        } finally {
            a.recycle();
        }
    }


    private void init(Context context) {
        View.inflate(context, R.layout.post_toolbar_view, this);

        ratingView = (RatingView) findViewById(R.id.rating_view);
        bayanButton = (Button) findViewById(R.id.post_bayan);
        shareButton = (ImageButton) findViewById(R.id.post_share);
        favButton = (ImageButton) findViewById(R.id.post_fav);

        ratingView.setListener(new RatingView.Listener() {
            @Override
            public void ratingViewStateChanged(RatingView ratingView, RatingView.State oldState, RatingView.State newState) {
                if (PostToolbarView.this.listener == null)
                    return;

                if (newState == RatingView.State.LIKED)
                    PostToolbarView.this.listener.likePressed(PostToolbarView.this);
                else if (newState == RatingView.State.DISLIKED)
                    PostToolbarView.this.listener.dislikePressed(PostToolbarView.this);
            }
        });

        bayanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if (listener != null)
                listener.bayanPressed(PostToolbarView.this);
            }
        });

        shareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if (listener != null)
                listener.sharePressed(PostToolbarView.this);
            }
        });

        favButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            if (listener != null)
                listener.favPressed(PostToolbarView.this);
            }
        });
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int i = 0 ; i < getChildCount() ; i++){
            getChildAt(i).layout(0, 0, r - l, b - t);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public boolean isLiked() {
        return ratingView.getState() == RatingView.State.LIKED;
    }

    public void _setLiked(boolean liked) {
        ratingView._setState(RatingView.State.LIKED);
        invalidate();
    }

    public void setLiked(boolean liked) {
        ratingView.setState(RatingView.State.LIKED);
        invalidate();
    }

    public boolean isDisliked() {
        return ratingView.getState() == RatingView.State.DISLIKED;
    }

    public void _setDisliked(boolean disliked) {
        ratingView._setState(RatingView.State.DISLIKED);
        invalidate();
    }

    public void setDisliked(boolean disliked) {
        ratingView.setState(RatingView.State.DISLIKED);
        invalidate();
    }

    public boolean isBayaned() {
        return bayaned;
    }
    public void _setBayaned(boolean bayaned) {
        this.bayaned = bayaned;
        invalidate();
    }

    public void setBayaned(boolean bayaned) {
        _setBayaned(bayaned);
    }

    public boolean isFaved() {
        return faved;
    }

    public void _setFaved(boolean faved) {
        this.faved = faved;
        //TODO: set color filter and disable button here and elsewhere
        invalidate();
    }

    public void setFaved(boolean faved) {
        _setFaved(faved);
    }

    public void setRating(String rating) {
        ratingView.setValue(rating);
    }

    public String getRating() {
        return ratingView.getValue();
    }
}

