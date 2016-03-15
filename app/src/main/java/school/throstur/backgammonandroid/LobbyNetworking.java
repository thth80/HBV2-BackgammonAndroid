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

    public static List<HashMap<String, String>> addHumanMatch(String name, String points, String clock ) throws IOException
    {
        try
        {
            String url = Uri.parse("http://localhost:9090/addHumanMatch")
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

}
