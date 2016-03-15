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

}
