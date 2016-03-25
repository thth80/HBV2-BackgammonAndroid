package school.throstur.backgammonandroid.Adapters;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class TrophyAdapter extends RecyclerView.Adapter<TrophyAdapter.TrophyHolder> {
    private ArrayList<HashMap<String, String>> mTrophyList;
    private Context mContext;
    private Activity mParent;

    public TrophyAdapter(Context context, ArrayList<HashMap<String, String>>chatList, Activity parent)
    {
        mContext = context;
        mTrophyList = chatList;
        mParent = parent;
    }

    @Override
    public int getItemCount() {
        return mTrophyList.size();
    }

    public void setEntryList(ArrayList<HashMap<String, String>> trophyList) {
        mTrophyList = trophyList;
    }

    @Override
    public TrophyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //View view = inflater.inflate(R.layout.list_item_tvseries, parent, false);
        //TODO ÞÞ: kalla á inflater.inflate með rétt layout
        View view = new View(mContext);
        return new TrophyHolder(view);
    }

    @Override
    public void onBindViewHolder(TrophyHolder holder, int index)
    {
        HashMap<String, String> trophy = mTrophyList.get(index);
        holder.bindEntryData(trophy);
    }

    public class TrophyHolder extends RecyclerView.ViewHolder
    {
        private TextView mNameTextView;
        private TextView mDecriptTextView;
        private ProgressBar mTrophyProgress;
        private Image mTrophyImage;

        //TODO ÞÞ: Hanna trophy entry, mun innihalda nafn(stuttur texti), lýsingu(lengri texti), mynd(þurfum bara ID) og progress bar
        private Context mContext;

        public TrophyHolder(View view)
        {
            super(view);

            //TODO ÞÞ: TEngja þessi element við actual UI element í gegnum view
            mNameTextView = (TextView)new View(mContext);
            mDecriptTextView = (TextView)new View(mContext);
            mTrophyProgress = (ProgressBar)new View(mContext);
            mTrophyProgress.setMax(100);
            //tengja mTrophyImage

        }

        public void bindEntryData(HashMap<String, String> trophy)
        {
            mNameTextView.setText(trophy.get("name"));
            mDecriptTextView.setText(trophy.get("descript"));
            boolean isAccumulated = Boolean.parseBoolean(trophy.get("isAccum"));
            int progress = Math.min(100, Integer.parseInt(trophy.get("percent")));
            if(!isAccumulated)
                progress = (progress >= 100) ? 100 : 0;

            mTrophyProgress.setProgress(progress);
            int imageId = Integer.parseInt(trophy.get("imageId"));

            //TODO ÞÞ: Nota imageId til að setja rétta mynd
        }
    }
}
