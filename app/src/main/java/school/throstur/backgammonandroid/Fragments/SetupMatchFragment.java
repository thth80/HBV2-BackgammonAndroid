package school.throstur.backgammonandroid.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import school.throstur.backgammonandroid.LobbyActivity;

/**
 * Created by Þröstur on 25.3.2016.
 */
public class SetupMatchFragment extends Fragment {
    private Button mSetupMatchButton;
    private RadioGroup mPointsSelection;
    private RadioGroup mTimeSelection;
    private RadioGroup mHumanSelection;
    private RadioGroup mDifficultySelection;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO ÞÞ: Tengja breytur við rétt element
        mSetupMatchButton = (Button)new View(getActivity()); //setup_match
        mPointsSelection = (RadioGroup)new View(getActivity());
        mTimeSelection = (RadioGroup)new View(getActivity());
        mDifficultySelection = (RadioGroup)new View(getActivity());
        mHumanSelection = (RadioGroup)new View(getActivity());

        mHumanSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //TODO ÞÞ: Disable/Enable difficulty grúppuna eftir því hvort hakað sé við Human eða Bot
            }
        });

        mSetupMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //TODO ÞÞ: Extracta gildin úr RadioGroups og setja í breyturnar

                String points = "9"; //1, 3, 5, 7 eða 9
                String addedTime = "15"; //0, 15, 30, 45
                String botDifficulty = "1"; //1, 2, 3
                boolean isHumanMatch = true;

                LobbyActivity lobby = (LobbyActivity) getActivity();
                lobby.setupNewMatch(points, addedTime, botDifficulty, isHumanMatch);
            }
        });

    }
}
