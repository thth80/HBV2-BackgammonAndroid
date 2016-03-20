package school.throstur.backgammonandroid;

import android.net.Uri;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class TrophyStatsNetworking {
    public static List<HashMap<String, String>> initStats(String name) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/initStats")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> initTrophies(String name) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/initTrophies")
                    .buildUpon()
                    .appendQueryParameter("name", name)
                    .build().toString();

            JSONArray jsonArray = new JSONArray(Utils.getUrlString(url));
            return Utils.JSONToMapList(jsonArray);
        }
        catch(Exception e)
        {
            return null;
        }
    }

    public static List<HashMap<String, String>> checkForJoinedMatch(String name) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/checkForJoin")
                    .buildUpon()
                    .appendQueryParameter("name", name)
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
