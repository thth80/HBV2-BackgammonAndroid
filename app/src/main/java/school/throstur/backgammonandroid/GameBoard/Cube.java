package school.throstur.backgammonandroid.GameBoard;

/**
 * Created by Aðalsteinn on 20.3.2016.
 */
public class Cube {
    private int cubeVal;
    private boolean isAnimating;
    public Cube(int val)
    {
        cubeVal = val;
        isAnimating = false;
    }

    public void startFlipping(int newVal)
    {
        cubeVal = newVal;
    }

    public void render()
    {

    }
}
