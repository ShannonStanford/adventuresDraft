package com.example.shannonyan.adventuresdraft;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DemoFragment extends Fragment {


    public DemoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_demo, container, false);

        TextView date = (TextView) v.findViewById(R.id.txt_display);
        date.setText(getArguments().getString("dateLabel"));

        TextView tvTime = (TextView) v.findViewById(R.id.tvTime);
        tvTime.setText(getArguments().getString("timeLabel"));

        //setContentView(R.layout.calendar_layout);
        CalendarView mCalendarView = (CalendarView) v.findViewById(R.id.cvCalendar);

        EditText etDate = v.findViewById(R.id.etDate);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                //SEND TO DATABASE
                Log.d("DemoFragment", "year: " + year + "month: " + month + "day: " + day);
            }
        });



        return v;
    }

    public static DemoFragment newInstance(String text) {

        DemoFragment f = new DemoFragment();
        Bundle b = new Bundle();
        b.putString("dateLabel", "Pick up date");
        b.putString("timeLabel", "Pick up time");

        f.setArguments(b);

        return f;
    }

}
