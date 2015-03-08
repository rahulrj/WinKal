package project.com.application;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import wincal.android.com.wincal.DatePickerFragment;
import wincal.android.com.wincal.DateSelectListener;

/**
 * Created by Rahul Raja on 12/24/2014.
 */
public class MainActivity extends ActionBarActivity {

    private DatePickerFragment mDatePickerFragment;
    private DatePickerFragment mDatePickerDialogFragment;

    private int mSelectedMonth;
    private int mSelectedYear;
    private int mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpTitleBar();

       /* Code for dialog*/
//        mDatePickerDialogFragment=new DatePickerFragment();
//        FragmentManager manager=getSupportFragmentManager();
//        if(savedInstanceState!=null){
//
//                mDatePickerDialogFragment.restoreStatesFromKey(savedInstanceState,"CALENDAR_SAVED_STATE");
//        }
//
//    else
//       mDatePickerDialogFragment.show(manager,"Dialog");


        /* Code for normal fragment*/
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mDatePickerFragment=new DatePickerFragment();
        if(savedInstanceState!=null){

            mDatePickerFragment.restoreStatesFromKey(savedInstanceState,"CALENDAR_SAVED_STATE");
        }

        transaction.replace(R.id.container, mDatePickerFragment);
        transaction.commit();



        DateSelectListener dateSelectListener=new DateSelectListener() {
            @Override
            public void onSelectDate(int date, int month, int year) {

                mSelectedDate=date;
                mSelectedMonth=month;
                mSelectedYear=year;

                Toast.makeText(MainActivity.this, "" + month + " " + year + " " + date, Toast.LENGTH_LONG).show();
            }
        };

        mDatePickerFragment.setDateSelectListener(dateSelectListener);

    }

    private void setUpTitleBar(){

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));

        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (mDatePickerFragment != null) {
            mDatePickerFragment.saveStatesToKey(outState, "CALENDAR_SAVED_STATE");
        }

        if (mDatePickerDialogFragment != null) {
            mDatePickerDialogFragment.saveStatesToKey(outState,
                    "DIALOG_CALENDAR_SAVED_STATE");
        }
    }
}
