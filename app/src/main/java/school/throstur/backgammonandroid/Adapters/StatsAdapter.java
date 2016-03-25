package school.throstur.backgammonandroid.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import school.throstur.backgammonandroid.StatsActivity;

/**
 * Created by Aðalsteinn on 25.3.2016.
 */
public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatHolder> {
    private ArrayList<HashMap<String, String>> mStatsList;
    private Context mContext;
    private StatsActivity mParent;

    public StatsAdapter(Context context, ArrayList<HashMap<String, String>> chatList, StatsActivity parent)
    {
        mContext = context;
        mStatsList = chatList;
        mParent = parent;
    }

    @Override
    public int getItemCount() {
        return mStatsList.size();
    }

    public void setEntryList(ArrayList<HashMap<String, String>> statsList) {
        mStatsList = statsList;
    }

    @Override
    public StatHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        //View view = inflater.inflate(R.layout.list_item_tvseries, parent, false);
        //TODO ÞÞ: kalla á inflater.inflate með rétt layout
        View view = new View(mContext);
        return new StatHolder(view);
    }

    @Override
    public void onBindViewHolder(StatHolder holder, int index)
    {
        HashMap<String, String> stats = mStatsList.get(index);
        holder.bindEntryData(stats);
    }

    public class StatHolder extends RecyclerView.ViewHolder
    {
        private TextView oppoNameTextView;
        private ProgressBar mPointsProgress;
        private ProgressBar mPawnsProgress;
        private HashMap<String, String> mStats;

        //TODO ÞÞ: Hanna Stats entry, freistandi að nota bara 3 progress bars, textview fyrir nafnið og henda í Toast ef ýtt er á progress bar
        private Context mContext;

        public StatHolder(View view)
        {
            super(view);
            //TODO ÞÞ: TEngja þessi element við actual UI element í gegnum view
            oppoNameTextView = (TextView)new View(mContext);
            mPointsProgress = (ProgressBar)new View(mContext);
            mPawnsProgress = (ProgressBar)new View(mContext);

            mPointsProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toast = "You have earned " + mStats.get("pointsOne") + " points and your opponent has earned " + mStats.get("pointsTwo");
                    mParent.makeToast(toast);
                }
            });

            mPawnsProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String toast = "Your have saved " + mStats.get("pawnsOne") + " pawns nd your opponent has saved " + mStats.get("pawnsTwo");
                    mParent.makeToast(toast);
                }
            });
        }

        public void bindEntryData(HashMap<String, String> stats)
        {
            oppoNameTextView.setText(stats.get("name"));

            int pointsWon = Integer.parseInt(stats.get("pointsOne"));
            int pointsLost = Integer.parseInt(stats.get("pointsTwo"));
            int userPawns = Integer.parseInt(stats.get("pawnsOne"));
            int oppoPawns = Integer.parseInt(stats.get("pawnsTwo"));

            mPointsProgress.setMax(pointsLost + pointsWon);
            mPointsProgress.setProgress(pointsWon);

            mPawnsProgress.setMax(oppoPawns + userPawns);
            mPawnsProgress.setProgress(userPawns);

            mStats = stats;

        }
    }
}

