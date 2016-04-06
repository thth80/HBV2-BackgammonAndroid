package school.throstur.backgammonandroid.Utility;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class InGameNetworking {

    public static ArrayList<HashMap<String, String>> diceThrown(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/diceThrow")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }
    public static ArrayList<HashMap<String, String>> cubeThrown(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/cube")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> timeOut(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/timeOut")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> startNewGame(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/startNewGame")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> rejectOffer(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/reject")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> acceptOffer(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/accept")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> refresh(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/refreshMatch")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> greenSquare(String name, String from, String to) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/greenSquare")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .appendQueryParameter("from", from)
                    .appendQueryParameter("to", to)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> playerLeaving(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/playerLeaving")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

    public static ArrayList<HashMap<String, String>> observerLeaving(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/observerLeaving")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }


    public static ArrayList<HashMap<String, String>> endTurn(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/endTurn")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            Log.d("MATCH NETWORK", e.getMessage());
            return new ArrayList<>();
        }
    }

}
