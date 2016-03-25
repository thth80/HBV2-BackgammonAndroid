package school.throstur.backgammonandroid.Adapters;

import android.app.Fragment;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class LobbyListAdapter extends RecyclerView.Adapter<LobbyListAdapter.ListEntryHolder>{
    private ArrayList<HashMap<String, String>> mEntryList;
    private Context mContext;
    private Fragment mParent;

    public LobbyListAdapter(Context context, ArrayList<HashMap<String, String>> entryList, Fragment parent) {
        mContext = context;
        mEntryList = entryList;
        mParent = parent;
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
        notifyItemInserted(mEntryList.size() - 1);
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
        View view = new View(mContext);
        return new ListEntryHolder(view);
    }

    @Override
    public void onBindViewHolder(ListEntryHolder holder, int index)
    {
        HashMap<String, String> entryData = mEntryList.get(index);
        holder.bindEntryData(entryData);
    }

    private int removeEntryById(String id) {;
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
        private TextView mPlayerOneTextView;
        private TextView mPlayerTwoTextView;
        private TextView mPointsTextView;
        private TextView mAddedTimeTextView;
        private Button mEntryButton;

        //TODO ÞÞ AE: Finna hönnun á entry sem getur bæði verið ongoing match og waiting list. Eini munurinn
        //á þessum 2 entries er að playerTwo er óákveðið fyrir waiting. Kannski nóg að aðgreina entries þannig að
        // playerTwo = PENDING hjá waiting list og takkarnir(OBSERVE/JOIN) eru mismunandi á litinn

        private String mButtonType;
        private String mId;
        private Context mContext;

        public ListEntryHolder(View view)
        {
            super(view);
            //TODO ÞÞ: TEngja þessi element við actual UI element í gegnum ID. Notast skal við færibreytuna view.
            mPlayerOneTextView = (TextView)new View(mContext);
            mPlayerTwoTextView = (TextView)new View(mContext);
            mPointsTextView = (TextView)new View(mContext);
            mAddedTimeTextView = (TextView)new View(mContext);

            mEntryButton = (Button)new View(mContext);
            mEntryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    //TODO AE: Hvert get ég sent upplýsingar héðan? Til að byrja með köllum við bara á server-inn og eyðum ef cancel
                    switch(mButtonType)
                    {
                        case "cancel":
                            int position = removeEntryById(mId);
                            if(position != -1)
                                notifyItemRemoved(position);
                            //mParent.cancelWasClicked(mId)
                            break;
                        case "observe":
                            //mParent.observeWasClicked(mId)
                            break;
                        case "join":
                            //mParent.joinWasCliked(mId)
                            break;
                    }
                }
            });

        }

        //TODO AE; Treysta þarf á að sá sem fyllir listann láti PENDING eða álíka í playerTwo, einnig type
        public void bindEntryData(HashMap<String, String> entryData)
        {
            mPlayerOneTextView.setText(entryData.get("playerOne"));;
            mPlayerTwoTextView.setText(entryData.get("playerOne"));
            mPointsTextView.setText(entryData.get("points"));
            mAddedTimeTextView.setText(entryData.get("addedTime"));

            mButtonType = entryData.get("type");
            mId = entryData.get("id");

            //TODO ÞÞ: Þarf að tengja réttan streng og lit á takkann út frá type. Type = "cancel" / "join" / "observe"
        }
    }

}
