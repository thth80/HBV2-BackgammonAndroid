package school.throstur.backgammonandroid;

import android.net.Uri;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class InGameNetworking {

    public static JSONArray diceThrown(String name) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/dice")
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
            String url = Uri.parse("http://localhost:9090/cube")
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
            String url = Uri.parse("http://localhost:9090/timeOut")
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
            String url = Uri.parse("http://localhost:9090/whiteSquare")
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
            String url = Uri.parse("http://localhost:9090/greenSquare")
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
            String url = Uri.parse("http://localhost:9090/pivot")
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
            String url = Uri.parse("http://localhost:9090/leaveMatch")
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
            String url = Uri.parse("http://localhost:9090/endTurn")
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
