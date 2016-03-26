package school.throstur.backgammonandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import school.throstur.backgammonandroid.Fragments.CanvasFragment;
import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;

/**
 * Created by Aðalsteinn on 26.3.2016.
 */
public class DrawingCanvas extends View {

    private Bitmap bitMap;
    private AnimationCoordinator mAnimator;

    public DrawingCanvas(Context context) {
        super(context);
    }

    public void setAnimator(AnimationCoordinator animator)
    {
        mAnimator = animator;
    }

}
