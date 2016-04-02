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
import android.widget.RadioButton;
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

    private static boolean firstSetup = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_setup_match, container, false);

        mSetupMatchButton = (Button) view.findViewById(R.id.setup_match_btn);
        mPointsSelection = (RadioGroup) view.findViewById(R.id.points_sel);
        mTimeSelection = (RadioGroup) view.findViewById(R.id.time_sel);
        mDifficultySelection = (RadioGroup) view.findViewById(R.id.diff_sel);
        mHumanSelection = (RadioGroup) view.findViewById(R.id.human_sel);

        if(firstSetup)
        {
            firstSetup = false;
            ((RadioButton) view.findViewById(R.id.check_bot)).setChecked(true);
            ((RadioButton) view.findViewById(R.id.easy_difficulty)).setChecked(true);
            ((RadioButton) view.findViewById(R.id.s15_time)).setChecked(true);
            ((RadioButton) view.findViewById(R.id.one_point)).setChecked(true);
        }

        mHumanSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                boolean isEnabled = (checkedId == R.id.check_bot);
                for(int i = 0; i < mDifficultySelection.getChildCount(); i++)
                    ((RadioButton)mDifficultySelection.getChildAt(i)).setEnabled(isEnabled);

            }
        });



        // Rel-view-ið fyrir difficulty (með id setup_match_view_difficulty) er með visibility attribute
        // sem er hægt að setja á gone þegar human er valið.

        mSetupMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean isHumanMatch = mHumanSelection.getCheckedRadioButtonId() == R.id.check_human ;
                String points;
                switch (mPointsSelection.getCheckedRadioButtonId())
                {
                    case R.id.one_point:
                        points = "1";
                        break;
                    case R.id.three_point:
                        points = "3";
                        break;
                    case R.id.five_point:
                        points = "5";
                        break;
                    case R.id.seven_point:
                        points = "7";
                        break;
                    default:
                        points = "9";
                }
                String addedTime;
                switch (mTimeSelection.getCheckedRadioButtonId())
                {
                    case R.id.infinity_time:
                        addedTime = "0";
                        break;
                    case R.id.s15_time:
                        addedTime = "10";
                        break;
                    case R.id.s30_time:
                        addedTime = "20";
                        break;
                    default:
                        addedTime = "30";;
                }
                String botDifficulty = "1";

                LobbyActivity lobby = (LobbyActivity) getActivity();
                lobby.setupNewMatch(points, addedTime, botDifficulty, isHumanMatch);
            }
        });

        return view;
    }
}
