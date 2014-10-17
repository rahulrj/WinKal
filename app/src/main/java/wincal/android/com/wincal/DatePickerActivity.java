package wincal.android.com.wincal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;


public class DatePickerActivity extends ActionBarActivity {


    private ListView mMonthListview;
    private ListView mDateListView;
    private ListView mYearListView;
    private MonthYearAdapter mMonthAdapter;
    private MonthYearAdapter mYearAdapter;

    private int mMiddlePositionInScreen=0;
    private int mBottomPositionOfMiddleElement=0;
    private int mCurrentMonth;
    private int mCurrentYear;
    private int mCurrentYearPosition;
    private int mCurrentMonthPosition;


    private int currentMonthPosition;
    private AtomicBoolean mMonthListBeingTouched = new AtomicBoolean(false);
    private AtomicBoolean mYearListBeingTouched = new AtomicBoolean(false);
    private int mRootLayoutHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);

        mMonthListview = (ListView) findViewById(R.id.month_listview);
        mDateListView = (ListView) findViewById(R.id.date_listview);
        mYearListView = (ListView) findViewById(R.id.year_listview);

        String monthNames[]=getResources().getStringArray(R.array.month_names);
        mMonthAdapter = new MonthYearAdapter(this,monthNames,monthNames.length);


        mYearAdapter = new MonthYearAdapter(this, null,Constants.NO_OF_YEARS);
        //mYearAdapter.setCurrentMonthPos(mYearAdapter.getCount() / 2 - 3);

        mMonthListview.setAdapter(mMonthAdapter);
        mYearListView.setAdapter(mYearAdapter);

        setCurrentPositionsInListViews();

        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight = rootLayout.getMeasuredHeight();

                mMonthListview.setSelectionFromTop(mCurrentMonthPosition, mRootLayoutHeight / 3);
                mYearListView.setSelectionFromTop(mCurrentYearPosition , mRootLayoutHeight / 3);

                setListenersOnListView(mMonthAdapter, mMonthListview,mMonthListBeingTouched);
                setListenersOnListView(mYearAdapter, mYearListView,mYearListBeingTouched);


            }
        });




    }

    private void setCurrentPositionsInListViews(){

        Date currentDate=new Date();
        Calendar cal=Calendar.getInstance();
        cal.setTime(currentDate);

        mCurrentMonth=cal.get(Calendar.MONTH);
        mCurrentYear=cal.get(Calendar.YEAR);

        mCurrentMonthPosition=mMonthAdapter.getCount()/2-Constants.OFFSET_FOR_MONTH+mCurrentMonth;
        mCurrentYearPosition=mYearAdapter.getCount()/2-Constants.OFFSET_FOR_YEAR+(mCurrentYear-Constants.STARTING_YEAR);
        mMonthAdapter.setCurrentMonthPos(mCurrentMonthPosition);
        mYearAdapter.setCurrentMonthPos(mCurrentYearPosition);


    }

    private void setListenersOnListView(final MonthYearAdapter adapter, final ListView listView, final AtomicBoolean listBeingTouched) {

        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (!adapter.getAllItemsVisible()) {
                        listBeingTouched.set(true);
                        adapter.setAllItemsVisible(true);
                        adapter.highlightCurrentMonthColor(false);
                        adapter.notifyDataSetChanged();
                    } else if (adapter.getAllItemsVisible()) {
                        listBeingTouched.set(true);
                    }

                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    listBeingTouched.compareAndSet(true, false);
                    // return true;

                }
                return false;
            }
        });

        listView.setOnScrollListener(new OnScrollListener() {

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !listBeingTouched.get()) {

                    listBeingTouched.set(true);
                    putSomeRowInMiddle(listView);

                }

            }
        });


    }


    private void putSomeRowInMiddle(ListView listView) {

        for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {
            final View v = listView.getChildAt(i);
            if (v != null) {

                if(mMiddlePositionInScreen == 0 ) {
                    mMiddlePositionInScreen = mRootLayoutHeight / 3 + v.getHeight() / 2;
                    mBottomPositionOfMiddleElement=mRootLayoutHeight/3+v.getHeight();
                }

                if ((v.getTop() > mRootLayoutHeight / 3) && v.getTop() < mMiddlePositionInScreen) {
                          scrollUp(v,listView);
                }

                if ((v.getBottom() >= mMiddlePositionInScreen) && v.getBottom() < mBottomPositionOfMiddleElement) {
                         scrollDown(v,listView);
                }


                if(v.getBottom()<=mMiddlePositionInScreen && v.getBottom()>mRootLayoutHeight/3){

                       if(v.getBottom()+ listView.getDividerHeight()/2>=mMiddlePositionInScreen){
                              scrollDown(v,listView);
                       }

                }

                if(v.getTop()>=mMiddlePositionInScreen && v.getTop()<mBottomPositionOfMiddleElement){

                    if(v.getTop()- listView.getDividerHeight()/2<=mMiddlePositionInScreen){
                              scrollUp(v,listView);
                    }

                }


            }

        }

    }

    private void scrollDown(final View v, final ListView listView){

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollBy(v.getBottom() - (mRootLayoutHeight / 3 + v.getHeight()), 1000);

            }
        });

    }


    private void scrollUp(final View v, final ListView listView){

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
