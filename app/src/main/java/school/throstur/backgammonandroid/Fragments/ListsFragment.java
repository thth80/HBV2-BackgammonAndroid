package school.throstur.backgammonandroid.Fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

import school.throstur.backgammonandroid.Adapters.LobbyListAdapter;
import school.throstur.backgammonandroid.LobbyActivity;

/**
 * Created by Þröstur on 25.3.2016.
 */
public class ListsFragment extends Fragment {
    private RecyclerView mListRecycler;
    private LobbyListAdapter mListAdapter;

    public ListsFragment()
    {
        //TODO ÞÞ: Tengja recyclerinn rétt
        mListRecycler = (RecyclerView)new View(getActivity());
        mListAdapter = new LobbyListAdapter(getActivity(), this);

    }

    public void addWaitingEntry(HashMap<String, String> waitEntry, boolean canCancel)
    {
        String type = (canCancel)? "cancel" : "join" ;
        waitEntry.put("type", type);

        mListAdapter.appendEntry(waitEntry);
        mListAdapter.notifyItemInserted(mListAdapter.getItemCount() - 1);
    }

    public void addOngoingEntry(HashMap<String, String> matchEntry)
    {
        matchEntry.put("type", "observe");

        mListAdapter.prependEntry(matchEntry);
        mListAdapter.notifyItemInserted(mListAdapter.getItemCount() - 1);
    }

    public void removeListEntry(String id)
    {
        mListAdapter.removeEntryById(id);
    }

    public void refreshList()
    {
        mListAdapter.notifyDataSetChanged();
    }

    public void joinWasClicked(String id)
    {
        LobbyActivity parent = (LobbyActivity)getActivity();
        parent.attemptJoiningMatch(id);
    }

    public void cancelWasClicked(String id)
    {
        LobbyActivity parent = (LobbyActivity)getActivity();
        parent.cancelWaitEntry(id);
    }

    public void observeWasClicked(String id)
    {
        LobbyActivity parent = (LobbyActivity)getActivity();
        parent.attemptObservingMatch(id);
    }
}
