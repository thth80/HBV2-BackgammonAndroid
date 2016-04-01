package school.throstur.backgammonandroid.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import school.throstur.backgammonandroid.LobbyActivity;
import school.throstur.backgammonandroid.R;

/**
 * Created by Þröstur on 25.3.2016.
 */
public class SetupMatchFragment extends Fragment {
    private Button mSetupMatchButton;
    private RadioGroup mPointsSelection;
    private RadioGroup mTimeSelection;
    private RadioGroup mHumanSelection;
    private RadioGroup mDifficultySelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        //TODO ÞÞ: Tengja breytur við rétt element
        View view = inflater.inflate(R.layout.fragment_lobby_list, container, false);

        mSetupMatchButton = (Button) view.findViewById(R.id.setup_match_btn);
        mPointsSelection = (RadioGroup) view.findViewById(R.id.points_sel);
        mTimeSelection = (RadioGroup) view.findViewById(R.id.time_sel);
        mDifficultySelection = (RadioGroup) view.findViewById(R.id.diff_sel);
        mHumanSelection = (RadioGroup) view.findViewById(R.id.human_sel);

        mHumanSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //TODO AE: Disable/Enable difficulty grúppuna eftir því hvort hakað sé við Human eða Bot
                // Rel-view-ið fyrir difficulty (með id setup_match_view_difficulty) er með visibility attribute
                // sem er hægt að setja á gone þegar human er valið.
            }
        });

        mSetupMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //TODO AE: Extracta gildin úr RadioGroups og setja í breyturnar

                String points = "9"; //1, 3, 5, 7 eða 9
                String addedTime = "15"; //0, 15, 30, 45
                String botDifficulty = "1"; //1, 2, 3
                boolean isHumanMatch = true;

                LobbyActivity lobby = (LobbyActivity) getActivity();
                lobby.setupNewMatch(points, addedTime, botDifficulty, isHumanMatch);
            }
        });

        return view;
    }
}
