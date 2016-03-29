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

/**
 * Created by Þröstur on 25.3.2016.
 */
public class ListsFragment extends Fragment {
    private RecyclerView mListRecycler;
    private LobbyListAdapter mListAdapter;
    private Button mToNewGame;

    public ListsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);

        mToNewGame = (Button) view.findViewById(R.id.to_new_game);
        mToNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetupMatchFragment matchFragment = ((LobbyActivity)getActivity()).getSetupMatch();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.lobby_fragment_container, matchFragment);
                ft.commit();
            }
        });

        mListRecycler = (RecyclerView) view.findViewById(R.id.lobby_recycler_view);
        mListRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        mListAdapter = new LobbyListAdapter(getActivity(), this);
        mListRecycler.setAdapter(mListAdapter);

        return view;
    }

    @Override
    public void onResume()
    {
        Log.d("LOGINACTIVITY", "KALLAÐI víst á SUOE, eða gerði ég?????");
        super.onResume();
        Log.d("LOGINACTIVITY", "KALLAÐI víst á SUOER!");
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
