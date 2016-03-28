package school.throstur.backgammonandroid.Utility;

import android.net.Uri;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class LobbyNetworking {

    public static ArrayList<HashMap<String, String>> removeWaitEntry(String waitId ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/removeWaitEntry")
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

    public static ArrayList<HashMap<String, String>> startBotMatch(String name, String points, String addedTime, String botDiff ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/startBotMatch")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("points", points)
                    .appendQueryParameter("diff", botDiff)
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

    public static ArrayList<HashMap<String, String>> addWaitEntry(String name, String points, String clock ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/addWaitEntry")
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

    public static ArrayList<HashMap<String, String>> joinHumanMatch(String user, String id ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/joinHumanMatch")
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

    public static ArrayList<HashMap<String, String>> submitLobbyChat(String chatter, String chatEntry ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/submitLobbyChat")
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

    public static ArrayList<HashMap<String, String>> observeMatch(String username, String matchId ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/observeMatch")
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

    public static ArrayList<HashMap<String, String>> refresh(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/refresh")
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

    public static ArrayList<HashMap<String, String>> goToTrophy(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/goToTrophy")
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

    public static ArrayList<HashMap<String, String>> leaveApp(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/leaveApp")
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

    public static ArrayList<HashMap<String, String>> goToStats(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/goToStats")
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

    public static ArrayList<HashMap<String, String>> initLobby(String username ) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/initLobby")
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
