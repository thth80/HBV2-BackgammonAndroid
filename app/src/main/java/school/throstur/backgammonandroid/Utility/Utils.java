package school.throstur.backgammonandroid.Utility;

import android.content.Context;
import android.util.Log;

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

import school.throstur.backgammonandroid.GameBoard.AnimationCoordinator;

/**
 * Created by Aðalsteinn on 14.3.2016.
 */
public class Utils {
    public static final int TEAM_WH = 0;
    public static final int TEAM_BL = 1;
    public static final int TEAM_NONE = 2;
    public static int WIDTH = 450;
    public static int HEIGHT = 450;
    public static int SQUARE_WIDTH = 60;
    public static int SQUARE_HEIGHT = 200;
    public static final int WHITE_LIGHT = 5;
    public static final int GREEN_LIGHT = 6;
    public static final int NO_LIGHT = 9;

    public static final String[] trophyNames = new String[]
            {
                    "Baby Steps",
                    "Bigger Baby Steps",
                    "Triple Double",
                    "Double Double",
                    "Try Hard",
                    "Centurion",
                    "50 Centurion",
                    "We need a bigger cube!",
                    "Double Trouble",
                    "A Leader of Pawns",
                    "Pawn Moses",
                    "Slightly Unlucky",
                    "Donald Duck Unlucky",
                    "Played Some",
                    "Spares Nobody",
                    "The Bully",
                    "El Bulli!",
                    "Not Pointless",
                    "Point Hoarder",
                    "Fun Up to a Point"
            };

    public static final String[] trophyDesc = new String[]
        {
                "Make your pawns progress by 500 squares",
                "Make your pawns progress by 1000 squares",
                "Win by Backgammons 10 times",
                "Win by Gammon 10 times",
                "Get a 50% winrate or better versus Hard Bot(At least 10 points played)",
                "Win a game where your opponent was at least 100 steps behind you",
                "Win 5 games where your opponent was at least 100 steps behind you",
                "Play a game that gets doubled 4 times ",
                "Win a 10+ point game",
                "Get 100 pawns to safety",
                "Get 500 pawns to safety",
                "Lose a game while being one step away from victory",
                "Lose 5 games one step away from victory",
                "Win a game against 5 different players",
                "Win a game against 15 different players",
                "Win 10 points against a player before he wins a single point against you",
                "Win 20 points against a player before he wins a single point against you",
                "Earn your first point",
                "Earn 50 Backgammon points",
                "Earn 500 Backgammon points"
        };

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
        }
        catch(Exception e)
        {
            Log.d("UTILS", e.getMessage());
            return new byte[0];
        }
        finally {
            connection.disconnect();
        }
    }

    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public static ArrayList<HashMap<String, String>> JSONToMapList(JSONArray jsonArray)
    {
        @SuppressWarnings("unchecked")
        ArrayList<HashMap<String, String>> messages = new ArrayList <HashMap<String, String>>();
        Log.d("LOGINACTIVITY", "RESPONSE: "+ jsonArray.toString());
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
                Log.d("LOGINACTIVITY", "CAUGTH JSON ERROR: "+e.getMessage());
                e.printStackTrace();
            }
        }

        Log.d("LOGINACTIVITY", "ARRAYLIST: "+messages.toString());

        return messages;
    }

    public static ArrayList<HashMap<String, Integer>> convertToAnimationMoves(HashMap<String, String> moveInfo)
    {
        moveInfo.remove("action");

        ArrayList<HashMap<String, Integer>> animMoves = new ArrayList<>();
        for(int i = 0; i < moveInfo.size()/3; i++)
        {
            HashMap<String, Integer> singleAnim = new HashMap<>();
            singleAnim.put("from", Integer.parseInt(moveInfo.get("from" + i)));
            singleAnim.put("to", Integer.parseInt(moveInfo.get("to" + i)));

            int killMove = moveInfo.get("kill" + i).equals("true")? 1 : 0;
            singleAnim.put("killMove",killMove ) ;
            singleAnim.put("finished", 0);
            animMoves.add(singleAnim);
        }

        return animMoves;
    }

    public static AnimationCoordinator buildBoardFromDescription(HashMap<String, String> description, Context context)
    {
        int[] counts = new int[28];
        int[] teams = new int[28];
        int[] diceVals = new int[4];
        int cubeValue = Integer.parseInt(description.get("cube"));

        for(int i = 0; i < 28; i++)
        {
            counts[i] = Integer.parseInt(description.get("c" + i));
            teams[i] = Integer.parseInt(description.get("t" + i));
        }
        for(int i = 0; i < 4; i++)
            diceVals[i] = Integer.parseInt(description.get("d" + i));

        AnimationCoordinator animator = AnimationCoordinator.buildExistingBoard(teams, counts, diceVals, cubeValue, context);
        return animator;
    }

    public static int[] extractIntsFromPositionMessage(HashMap<String, String> positions)
    {
        positions.remove("action");
        int[] squarePos = new int[positions.size()];
        for(int i = 0; i < squarePos.length; i++)
            squarePos[i] = Integer.parseInt(positions.get(""+i));
        return squarePos;
    }

    public static HashMap<String, String> extractSpecificAction(ArrayList<HashMap<String, String>> allMsgs, String action)
    {
        for(HashMap<String, String> msg: allMsgs)
            if(msg.get("action").equals(action))
                return msg;
        return null;
    }

    public static ArrayList<HashMap<String, String>> extractSpecificActions(ArrayList<HashMap<String, String>> allMsgs, String action)
    {
        ArrayList<HashMap<String, String>> onlyAction = new ArrayList<>();
        for(HashMap<String, String> msg: allMsgs)
            if(msg.get("action").equals(action))
                onlyAction.add(msg);

        return onlyAction;
    }
}
