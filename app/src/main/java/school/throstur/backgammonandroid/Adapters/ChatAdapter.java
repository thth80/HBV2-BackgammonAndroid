package school.throstur.backgammonandroid.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import school.throstur.backgammonandroid.LobbyActivity;
import school.throstur.backgammonandroid.R;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private ArrayList<String> mChatList;
    private Context mContext;

    public ChatAdapter(Context context, ArrayList<String> chatList, LobbyActivity parent)
    {
        mContext = context;
        mChatList = chatList;
    }

    public ChatAdapter(Context context, LobbyActivity parent)
    {
        mContext = context;
        mChatList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public void setEntryList(ArrayList<String> chatList) {
        mChatList = chatList;
    }

    public void appendEntry(String entry)
    {
        mChatList.add(entry);
    }

    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.chat_entry, parent, false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int index)
    {
        String chatEntry = mChatList.get(index);
        holder.bindEntryData(chatEntry, index);
    }

    public int latestIndex()
    {
        return mChatList.size() - 1;
    }

    public class ChatHolder extends RecyclerView.ViewHolder
    {
        private TextView mChatTextView;

        public ChatHolder(View view)
        {
            super(view);
            mChatTextView = (TextView) view.findViewById(R.id.chat_entry);
        }

        public void bindEntryData(String chatEntry, int index)
        {
            mChatTextView.setText(chatEntry);
            int background = (index%2 == 0)? Color.argb(36, 255, 0, 0): Color.argb(36, 0, 255, 0) ;
            mChatTextView.setBackgroundColor(background);
        }
    }
}
