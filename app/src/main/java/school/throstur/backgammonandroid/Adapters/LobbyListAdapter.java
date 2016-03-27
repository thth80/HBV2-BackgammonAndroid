package school.throstur.backgammonandroid.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.Fragments.ListsFragment;
import school.throstur.backgammonandroid.R;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.ListEntryHolder>{
    private ArrayList<HashMap<String, String>> mEntryList;
    private Context mContext;
    private ListsFragment mParent;

    public LobbyListAdapter(Context context, ArrayList<HashMap<String, String>> entryList, ListsFragment parent) {
        mContext = context;
        mEntryList = entryList;
        mParent = parent;
    }

    public LobbyListAdapter(Context context, ListsFragment parent)
    {
        mContext = context;
        mParent = parent;
        mEntryList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mEntryList.size();
    }

    public void setEntryList(ArrayList<HashMap<String, String>> entryList) {
        mEntryList = entryList;
    }

    public void appendEntry(HashMap<String, String> entry)
    {
        mEntryList.add(entry);
    }

    public void prependEntry(HashMap<String, String> entry)
    {
        ArrayList<HashMap<String, String>> newList = new ArrayList();
        newList.add(entry);
        for(HashMap<String, String> oldEntry: mEntryList)
            newList.add(oldEntry);
        mEntryList = newList;

        notifyDataSetChanged();
    }

    @Override
    public ListEntryHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //View view = inflater.inflate(R.layout.list_item_tvseries, parent, false);
        //Hérna að ofan gerast töfrarnir þegar layout fyrir entry er tengt við view
        //View view = new View(mContext);
        View view = inflater.inflate(R.layout.list_item_lobby, parent, false);
        return new ListEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(ListEntryHolder holder, int index)
    {
        HashMap<String, String> entryData = mEntryList.get(index);
        holder.bindEntryData(entryData);
    }

    public int removeEntryById(String id) {;
        for (int i = 0; i < mEntryList.size(); i++)
        {
            HashMap<String, String> listEntry = mEntryList.get(i);
            if (listEntry.get("id").equals(id))
            {
                mEntryList.remove(i);
                return i;
            }
        }
        return -1;
    }

    public class ListEntryHolder extends RecyclerView.ViewHolder
    {
        private ImageView mPlayerOneImgView;
        private ImageView mPlayerTwoImgView;
        private TextView mPlayerOneTextView;
        private TextView mPlayerTwoTextView;
        private ImageView mPointsImgView;
        private TextView mPointsTextView;
        private ImageView mAddedTimeImgView;
        private TextView mAddedTimeTextView;
        private Button mEntryButton;

        // TODO AE: Ertu sáttur með þessa hönnun?
        // Finna hönnun á entry sem getur bæði verið ongoing match og waiting list. Eini munurinn
        //á þessum 2 entries er að playerTwo er óákveðið fyrir waiting. Kannski nóg að aðgreina entries þannig að
        // playerTwo = PENDING hjá waiting list og takkarnir(OBSERVE/JOIN) eru mismunandi á litinn

        private String mButtonType;
        private String mId;
        private Context mContext;

        public ListEntryHolder(View view)
        {
            super(view);

            mPlayerOneImgView = (ImageView) view.findViewById(R.id.list_item_lobby_playerOneImg);
            mPlayerOneTextView = (TextView) view.findViewById(R.id.list_item_lobby_playerOne);
            mPlayerTwoImgView = (ImageView) view.findViewById(R.id.list_item_lobby_playerTwoImg);
            mPlayerTwoTextView = (TextView) view.findViewById(R.id.list_item_lobby_playerTwo);

            mPointsImgView = (ImageView) view.findViewById(R.id.list_item_lobby_pointImage);
            mPointsTextView = (TextView) view.findViewById(R.id.list_item_lobby_points);

            mAddedTimeImgView = (ImageView) view.findViewById(R.id.list_item_lobby_clockImage);
            mAddedTimeTextView = (TextView) view.findViewById(R.id.list_item_lobby_clockText);

            mEntryButton = (Button)new View(mContext);
            mEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    switch(mButtonType)
                    {
                        case "cancel":
                            int position = removeEntryById(mId);
                            if(position != -1)
                                notifyItemRemoved(position);
                            mParent.cancelWasClicked(mId);
                            break;
                        case "observe":
                            mParent.observeWasClicked(mId);
                            break;
                        case "join":
                            mParent.joinWasClicked(mId);
                    }
                }
            });

        }

        public void bindEntryData(HashMap<String, String> entryData)
        {
            // TODO AE: Breyta player img breytum m.v. gildum úr lista?
            mPlayerOneImgView.setImageResource(R.drawable.ic_face);
            mPlayerOneTextView.setText(entryData.get("playerOne"));
            mPlayerTwoImgView.setImageResource(R.drawable.ic_face);
            mPlayerTwoTextView.setText(entryData.get("playerOne"));
            mPointsImgView.setImageResource(R.drawable.ic_point);
            mPointsTextView.setText(entryData.get("points"));
            mAddedTimeImgView.setImageResource(R.drawable.ic_clock);
            mAddedTimeTextView.setText(entryData.get("addedTime"));


            mButtonType = entryData.get("type");
            mId = entryData.get("id");

            //TODO ÞÞ: Þarf að tengja réttan streng og lit á takkann út frá type. Type = "cancel" / "join" / "observe"
        }
    }

}
