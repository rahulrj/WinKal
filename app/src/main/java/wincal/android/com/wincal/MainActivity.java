package wincal.android.com.wincal;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Rahul Raja on 12/24/2014.
 */
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatePickerFragment datePickerFragment=new DatePickerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        FragmentManager manager=getSupportFragmentManager();

       //datePickerFragment.show(manager,"Dialog");

        transaction.replace(R.id.container, datePickerFragment);
        transaction.commit();



    }
}
