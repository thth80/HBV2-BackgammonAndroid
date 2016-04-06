package school.throstur.backgammonandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;

public class DrawingCanvas extends View {

    private Bitmap mBitMap;
    private AnimationCoordinator mAnimator;
    private Context context;
    private int mWidth, mHeight;

    public DrawingCanvas(Context context) {
        super(context);
        setWillNotDraw(false);
        this.context = context;
    }

    public DrawingCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        this.context = context;
    }

    public DrawingCanvas(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWillNotDraw(false);
        this.context = context;
    }

    public int getCanvasWidth()
    {
        return mWidth;
    }
    public int getCanvasHeight()
    {
        return mHeight;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        mHeight = h;
        mBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if(mAnimator != null)
            mAnimator.render(canvas);
        else
            Log.d("MATCH ERROR", "mAnimator was NULL in onDraw");
    }

    public void setAnimator(AnimationCoordinator animator)
    {
        mAnimator = animator;
    }

}
