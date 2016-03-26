package school.throstur.backgammonandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;

/**
 * Created by Aðalsteinn on 26.3.2016.
 */
public class DrawingCanvas extends View {

    private Bitmap mBitMap;
    private AnimationCoordinator mAnimator;
    private Context context;
    private int mWidth, mHeight;

    public DrawingCanvas(Context context) {
        super(context);
        this.context = context;
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
        canvas.setBitmap(mBitMap);
        mAnimator.render(canvas);
    }

    public void setAnimator(AnimationCoordinator animator)
    {
        mAnimator = animator;
    }

}
