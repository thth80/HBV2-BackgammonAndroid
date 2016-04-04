package school.throstur.backgammonandroid.Utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.LobbyActivity;

/**
 * Created by Aðalsteinn on 3.4.2016.
 */
public class LobbyData
{
    public static ArrayList<HashMap<String, String>> mEntryList = new ArrayList<>();
    public static ArrayList<String> mChatList = new ArrayList<>();

    public static void setData(ArrayList<HashMap<String, String>> initialData, String username)
    {
        for(HashMap<String, String> msg: initialData)
        {
            //Log.d(LobbyActivity.TAG + " Init", msg.get("action"));
            switch (msg.get("action"))
            {
                case "chatBatch":
                    msg.remove("action");
                    for(int i = 0; i < msg.size(); i++)
                        mChatList.add(msg.get("" + i));
                    break;
                case "waitEntry":
                    boolean canCancel = msg.get("playerOne").equals(username);
                    String type = (canCancel)? "cancel" : "join" ;
                    msg.put("type", type);
                    mEntryList.add(msg);
                    break;
                case "ongoingEntry":
                    msg.put("type", "observe");
                    mEntryList.add(msg);
                    break;
            }
        }
    }

    public static void clearData()
    {
        mEntryList = new ArrayList<>();
        mChatList = new ArrayList<>();
    }

    public static ArrayList<HashMap<String, String>> getListEntries()
    {
        return mEntryList;
    }
    public static ArrayList<String> getChatEntries()
    {
        return mChatList;
    }
}
