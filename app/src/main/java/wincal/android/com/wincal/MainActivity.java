package wincal.android.com.wincal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Rahul Raja on 12/24/2014.
 */
public class MainActivity extends FragmentActivity {

    private DatePickerFragment mDatePickerFragment;
    private DatePickerFragment mDatePickerDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
