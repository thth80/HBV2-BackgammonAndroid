package school.throstur.backgammonandroid;

/**
 * Created by Aðalsteinn on 15.3.2016.
 */
public class DicePair {
    private int imageId;
    private boolean isWhite, isAnimating;
    private int first, second, firstCopy, secondCopy;
    private int animTimeLeft, timeBtwFlips;

    //TODO ÞÞ: Það þarf að vista teningamyndina í /res. Tengja ID svo hérna í imageId. Reyndar best að láta
    //Halla eða einhvern skera hana í minni búta fyrst
    public DicePair(int team)
    {
        imageId = 666;
        isWhite = (team == Utils.TEAM_WH);
        first = second = firstCopy = secondCopy = 1;
        isAnimating = false;
        animTimeLeft = 0;
        timeBtwFlips = 80;
    }

    public boolean update(int deltaMs)
    {
        int oldFlipsLeft = animTimeLeft/timeBtwFlips;
        animTimeLeft -= deltaMs;
        int currFlipsLeft = animTimeLeft/timeBtwFlips;

        if(oldFlipsLeft > currFlipsLeft)
        {
            int oldFirst = first;
            int oldSecond = second;
            while(oldFirst == first)
                first = 1 + (int)(Math.random()*6);
            while(oldSecond == second)
                second = 1 + (int)(Math.random()*6);
        }

        if(animTimeLeft <= 0)
        {
            isAnimating = false;
            first = firstCopy;
            second = secondCopy;
        }

        return isAnimating;
    }

    public boolean isRolling()
    {
        return isAnimating;
    }

    public void prepareForAnimation(int updatedFirst, int updatedSecond)
    {
        isAnimating = true;
        animTimeLeft = 1350;
        firstCopy = updatedFirst;
        secondCopy = updatedSecond;
    }

    public void setBoth(int first, int second)
    {
        this.first = first;
        this.second = second;
    }
    public void setFirst(int val)
    {
        first = val;
    }
    public void setSecond(int val)
    {
        second = val;
    }

    public void render(String ctx)
    {

    }
}
