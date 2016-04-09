package school.throstur.backgammonandroid.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import school.throstur.backgammonandroid.InGameActivity;
import school.throstur.backgammonandroid.R;

public class TrophyFragment extends Fragment
{
    private static final String TROPHY_DESC = "description of the trophy";
    private Button mReturnButton;
    private InGameActivity mParent;
    private HashMap<String, String> mTrophyPres;

    public TrophyFragment()
    {

    }

    public static final TrophyFragment newInstance(HashMap<String, String> trophyPres)
    {
        TrophyFragment frag = new TrophyFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(TROPHY_DESC, trophyPres);

        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mParent = (InGameActivity)getActivity();
        mTrophyPres = (HashMap<String, String>)getArguments().getSerializable(TROPHY_DESC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_present_trophy, container, false);

        TextView name = (TextView) view.findViewById(R.id.present_trophy_entry_heading);
        name.setText(mTrophyPres.get("name"));

        TextView description = (TextView) view.findViewById(R.id.present_trophy_entry_text);
        description.setText(mTrophyPres.get("desc"));

        mReturnButton = (Button) view.findViewById(R.id.present_trophy_btn);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mParent.onRemoveTrophy();
            }
        });

        return view;
    }
}
