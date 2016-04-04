package school.throstur.backgammonandroid.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.HashMap;

import school.throstur.backgammonandroid.Adapters.LobbyListAdapter;
import school.throstur.backgammonandroid.LobbyActivity;
import school.throstur.backgammonandroid.R;
import school.throstur.backgammonandroid.Utility.LobbyData;


public class ListsFragment extends Fragment {
    private  RecyclerView mListRecycler;
    private  LobbyListAdapter mListAdapter;
    private  LobbyActivity mParent;

    public ListsFragment()
    {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mParent = (LobbyActivity)getActivity();
        mListAdapter = new LobbyListAdapter(getActivity(), mParent, LobbyData.getListEntries());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);

        mListRecycler = (RecyclerView) view.findViewById(R.id.lobby_recycler_view);
        mListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListRecycler.setAdapter(mListAdapter);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mListAdapter.notifyDataSetChanged();
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

}
