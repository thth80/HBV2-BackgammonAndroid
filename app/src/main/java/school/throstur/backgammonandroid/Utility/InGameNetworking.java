package school.throstur.backgammonandroid.Utility;

import android.net.Uri;

import org.json.JSONArray;

import java.io.IOException;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class InGameNetworking {

    public static JSONArray diceThrown(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/dice")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("pw", "derp")
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }
    public static JSONArray cubeThrown(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/cube")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray timeOut(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/timeOut")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray doublingDecision(String name, String accepted) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/startNewGame")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("accepted", accepted)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray startNewGame(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/startNewGame")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray refresh(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/refresh")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray whiteSquare(String name, String pos) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/whiteSquare")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("pos", pos)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray greenSquare(String name, String pos) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/greenSquare")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("pos", pos)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray pivotClicked(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/pivot")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray leaveMatch(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/leaveMatch")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static JSONArray endTurn(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/endTurn")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();
            String json = Utils.getUrlString(url);
            return new JSONArray(json);
        }
        catch(Exception e)
        {
            return null;
        }
    }

}
