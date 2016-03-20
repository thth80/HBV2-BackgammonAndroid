package school.throstur.backgammonandroid;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class LobbyNetworking {

    public static List<HashMap<String, String>> removeWaitEntry(String waitId ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/removeWaitEntry")
                    .buildUpon()
                    .appendQueryParameter("waitId", waitId)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> startBotMatch(String name, String points, String clock, String addedTime ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/startBotMatch")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("points", points)
                    .appendQueryParameter("clock", clock)
                    .appendQueryParameter("addedTime", addedTime)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> addWaitEntry(String name, String points, String clock ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/addWaitEntry")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("clock", clock)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> joinHumanMatch(String user, String id ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/joinHumanMatch")
                    .buildUpon()
                    .appendQueryParameter("matchId", id)
                    .appendQueryParameter("joiner", user)
                    .build().toString();
            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> submitLobbyChat(String chatter, String chatEntry ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/submitLobbyChat")
                    .buildUpon()
                    .appendQueryParameter("name", chatter)
                    .appendQueryParameter("chatEntry", chatEntry)
                    .build().toString();
            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> observeMatch(String username, String matchId ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/observeMatch")
                    .buildUpon()
                    .appendQueryParameter("waitId", matchId)
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> refresh(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/refresh")
                    .buildUpon()
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> goToTrophy(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/goToTrophy")
                    .buildUpon()
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> leaveApp(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/leaveApp")
                    .buildUpon()
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> goToStats(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/goToStats")
                    .buildUpon()
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> initLobby(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/initLobby")
                    .buildUpon()
                    .appendQueryParameter("name", username)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

}
