package school.throstur.backgammonandroid.Adapters;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.Utils;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class TrophyAdapter extends RecyclerView.Adapter<TrophyAdapter.TrophyHolder> {
    private ArrayList<HashMap<String, String>> mTrophyList;
    private Context mContext;

    public TrophyAdapter(Context context, ArrayList<HashMap<String, String>> trophyList)
    {
        mContext = context;
        mTrophyList = trophyList;
    }

    @Override
    public int getItemCount() {
        return mTrophyList.size();
    }

    @Override
    public TrophyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.trophy_entry, parent, false);
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
        private ImageView mTrophyImage;

        private Context mContext;

        public TrophyHolder(View view)
        {
            super(view);

            mNameTextView = (TextView) view.findViewById(R.id.trophy_entry_heading);
            mDecriptTextView = (TextView) view.findViewById(R.id.trophy_entry_text);
            mTrophyProgress = (ProgressBar) view.findViewById(R.id.trophy_entry_progbar);
            mTrophyProgress.setMax(100);
            mTrophyImage = (ImageView) view.findViewById(R.id.trophy_entry_img);
        }

        public void bindEntryData(HashMap<String, String> trophy)
        {
            int id = Integer.parseInt(trophy.get("id"));

            mNameTextView.setText(Utils.trophyNames[id]);
            mDecriptTextView.setText(Utils.trophyDesc[id]);
            boolean isAccumulated = true;

            int progress = Math.min(100, Integer.parseInt(trophy.get("percent")));
            if(!isAccumulated)
                progress = (progress >= 100) ? 100 : 0;

            mTrophyProgress.setProgress(55);
            //Drawable image = mContext.getResources().getDrawable(R.drawable.trophy_icon);

            //mTrophyImage.setImageDrawable(image);
        }
    }
}
