package eu.robertboloc.holaurv.fragments;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.robertboloc.holaurv.HoLaURV;
import eu.robertboloc.holaurv.R;
import eu.robertboloc.holaurv.activities.LoginActivity_;
import eu.robertboloc.holaurv.helpers.Evalos;
import eu.robertboloc.holaurv.models.Day;
import eu.robertboloc.holaurv.models.Entry;

public class DayObjectFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    TextView mFirstEntry;
    TextView mFirstExit;
    TextView mSecondEntry;
    TextView mSecondExit;
    TextView mFirstAccumulate;
    TextView mSecondAccumulate;

    LinearLayout mDisplay;

    LayoutInflater mInflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        this.mInflater = inflater;

        View rootView = inflater.inflate(R.layout.fragment_collection_object,
                container, false);

        mDisplay = (LinearLayout) rootView.findViewById(R.id.display);

        Bundle args = getArguments();

        // Obtain the application state
        HoLaURV appState = ((HoLaURV) getActivity().getApplicationContext());
        Evalos eva = appState.getEva();

        // In case the app was killed by the OS
        if (!(eva instanceof Evalos)) {
            Intent intent = new Intent(getActivity(), LoginActivity_.class);
            startActivity(intent);
            getActivity().finish();
        } else {
            // If the day is greater than today, then just load the ad
            if (args.getInt(ARG_OBJECT) < Day.today()) {
                refresh(eva.getDay(args.getInt(ARG_OBJECT)));
            }
        }

        return rootView;
    }

    protected void refresh(Day day) {

        Period accumulate = null;

        // Period formatter instance
        PeriodFormatter HHMMSSFormater = new PeriodFormatterBuilder()
                .printZeroAlways().minimumPrintedDigits(2).appendHours()
                .appendSeparator("h").appendMinutes().appendLiteral("m")
                .toFormatter();

        // First Entry
        if (day.getEntry(Entry.FIRST_ENTRY) != null) {
            mDisplay.addView(mInflater.inflate(R.layout.first_entry, mDisplay,
                    false));
            mFirstEntry = (TextView) mDisplay.findViewById(R.id.firstEntry);
            mFirstEntry.setText(day.getEntry(Entry.FIRST_ENTRY)
                    .getDisplayHourAndMinute());
        }

        // First Exit
        if (day.getEntry(Entry.FIRST_EXIT) != null) {
            mDisplay.addView(mInflater.inflate(R.layout.first_exit, mDisplay,
                    false));
            mFirstExit = (TextView) mDisplay.findViewById(R.id.firstExit);
            mFirstExit.setText(day.getEntry(Entry.FIRST_EXIT)
                    .getDisplayHourAndMinute());

            // Compute the first accumulated
            accumulate = day.getAccumulate(Entry.FIRST_ENTRY, Entry.FIRST_EXIT);

            mFirstAccumulate = (TextView) mDisplay
                    .findViewById(R.id.firstAccumulated);
            mFirstAccumulate.setText(HHMMSSFormater.print(accumulate));
        }

        // Second Entry
        if (day.getEntry(Entry.SECOND_ENTRY) != null) {
            mDisplay.addView(mInflater.inflate(R.layout.second_entry, mDisplay,
                    false));
            mSecondEntry = (TextView) mDisplay.findViewById(R.id.secondEntry);
            mSecondEntry.setText(day.getEntry(Entry.SECOND_ENTRY)
                    .getDisplayHourAndMinute());
        }

        // Second Exit
        if (day.getEntry(Entry.SECOND_EXIT) != null) {
            mDisplay.addView(mInflater.inflate(R.layout.second_exit, mDisplay,
                    false));
            mSecondExit = (TextView) mDisplay.findViewById(R.id.secondExit);
            mSecondExit.setText(day.getEntry(Entry.SECOND_EXIT)
                    .getDisplayHourAndMinute());

            Period secondAccumulate = day.getAccumulate(Entry.SECOND_ENTRY,
                    Entry.SECOND_EXIT);

            accumulate = accumulate.plusHours(secondAccumulate.getHours())
                    .plusMinutes(secondAccumulate.getMinutes()).toPeriod()
                    .normalizedStandard();

            mSecondAccumulate = (TextView) mDisplay
                    .findViewById(R.id.secondAccumulate);

            mSecondAccumulate.setText(HHMMSSFormater.print(accumulate));
        }
    }
}