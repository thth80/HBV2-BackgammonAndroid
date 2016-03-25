package school.throstur.backgammonandroid.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import school.throstur.backgammonandroid.LobbyActivity;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
    private ArrayList<String> mChatList;
    private Context mContext;
    private Activity mParent;

    public ChatAdapter(Context context, ArrayList<String> chatList, LobbyActivity parent)
    {
        mContext = context;
        mChatList = chatList;
        mParent = parent;
    }

    public ChatAdapter(Context context, LobbyActivity parent)
    {
        mContext = context;
        mChatList = new ArrayList<>();
        mParent = parent;
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
        //View view = inflater.inflate(R.layout.list_item_tvseries, parent, false);
        //TODO ÞÞ: kalla á inflater.inflate með rétt layout
        View view = new View(mContext);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int index)
    {
        String chatEntry = mChatList.get(index);
        holder.bindEntryData(chatEntry);
    }

    public int latestIndex()
    {
        return mChatList.size() - 1;
    }

    public class ChatHolder extends RecyclerView.ViewHolder
    {
        private TextView mChatTextView;

        //TODO ÞÞ AE: Hanna chat UI element, væntanlega bara textView sem getur verið 1+ lína
        private Context mContext;

        public ChatHolder(View view)
        {
            super(view);
            //TODO ÞÞ: TEngja þetta element við actual UI element í gegnum view
            mChatTextView = (TextView)new View(mContext);
        }

        public void bindEntryData(String chatEntry)
        {
            mChatTextView.setText(chatEntry);;
        }
    }
}
