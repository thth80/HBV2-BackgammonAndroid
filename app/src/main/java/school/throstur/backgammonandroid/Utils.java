package school.throstur.backgammonandroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class Utils {

    private static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try
        {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);

            int bytesRead = 0;
            byte[] buffer = new byte[10024];
            while ((bytesRead = in.read(buffer)) > 0)
                out.write(buffer, 0, bytesRead);

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public static List<HashMap<String, String>> JSONToMapList(JSONArray jsonArray)
    {
        @SuppressWarnings("unchecked")
        List<HashMap<String, String>> messages = new ArrayList <HashMap<String, String>>();
        for(int i = 0; i < jsonArray.length(); i++)
        {
            JSONObject msg = null;
            try
            {
                msg = jsonArray.getJSONObject(i);
                Iterator<String> keys = msg.keys();
                HashMap<String, String> currentMessage = new HashMap<String, String>();
                while (keys.hasNext()) {
                    String key = keys.next();
                    currentMessage.put(key, msg.getString(key));
                }
                messages.add(currentMessage);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return messages;
    }
}
