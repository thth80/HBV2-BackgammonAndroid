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
public class TrophyStatsNetworking {


    public static ArrayList<HashMap<String, String>> checkForJoinedMatch(String name) throws IOException
    {
        try
        {
            String url = Uri.parse(Utils.HOST + "/checkForJoin")
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
