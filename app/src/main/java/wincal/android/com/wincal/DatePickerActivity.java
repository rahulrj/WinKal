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

import java.util.concurrent.atomic.AtomicBoolean;


public class DatePickerActivity extends ActionBarActivity {


    private ListView mMonthListview;
    private ListView mDateListView;
    private MonthAdapter mMonthAdapter;

    private int mMiddlePositionInScreen=0;
    private int mBottomPositionOfMiddleElement=0;


    private int currentMonthPosition;
    private AtomicBoolean mListBeingTouched = new AtomicBoolean(false);
    private int mRootLayoutHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);

        mMonthAdapter = new MonthAdapter(this, getResources().getStringArray(R.array.month_names));
        mMonthAdapter.setCurrentMonthPos(mMonthAdapter.getCount() / 2 - 3);

        mMonthListview = (ListView) findViewById(R.id.month_listview);
        mMonthListview.setAdapter(mMonthAdapter);

       mDateListView = (ListView) findViewById(R.id.date_listview);
       mDateListView.setAdapter(mMonthAdapter);


        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight = rootLayout.getMeasuredHeight();
                mMonthListview.setSelectionFromTop(mMonthAdapter.getCount() / 2 - 3, mRootLayoutHeight / 3);
                mDateListView.setSelectionFromTop(mMonthAdapter.getCount() / 2 - 3, mRootLayoutHeight / 3);

                setListenersOnMonthView();


            }
        });


    }

    private void setListenersOnMonthView() {

        mMonthListview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (!mMonthAdapter.getAllItemsVisible()) {
                        mListBeingTouched.set(true);
                        mMonthAdapter.setAllItemsVisible(true);
                        mMonthAdapter.highlightCurrentMonthColor(false);
                        mMonthAdapter.notifyDataSetChanged();
                    } else if (mMonthAdapter.getAllItemsVisible()) {
                        mListBeingTouched.set(true);
                    }

                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    mListBeingTouched.compareAndSet(true, false);
                    // return true;

                }
                return false;
            }
        });

        mMonthListview.setOnScrollListener(new OnScrollListener() {

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !mListBeingTouched.get()) {

                    mListBeingTouched.set(true);
                    putSomeRowInMiddle();

                }

            }
        });


    }


    private void putSomeRowInMiddle() {

        for (int i = 0; i <= mMonthListview.getLastVisiblePosition() - mMonthListview.getFirstVisiblePosition(); i++) {
            final View v = mMonthListview.getChildAt(i);
            if (v != null) {

                if(mMiddlePositionInScreen == 0 ) {
                    mMiddlePositionInScreen = mRootLayoutHeight / 3 + v.getHeight() / 2;
                    mBottomPositionOfMiddleElement=mRootLayoutHeight/3+v.getHeight();
                }

                if ((v.getTop() > mRootLayoutHeight / 3) && v.getTop() < mMiddlePositionInScreen) {
                          scrollUp(v);
                }

                if ((v.getBottom() >= mMiddlePositionInScreen) && v.getBottom() < mBottomPositionOfMiddleElement) {
                         scrollDown(v);
                }


                if(v.getBottom()<=mMiddlePositionInScreen && v.getBottom()>mRootLayoutHeight/3){

                       if(v.getBottom()+ mMonthListview.getDividerHeight()/2>=mMiddlePositionInScreen){
                              scrollDown(v);
                       }

                }

                if(v.getTop()>=mMiddlePositionInScreen && v.getTop()<mBottomPositionOfMiddleElement){

                    if(v.getTop()- mMonthListview.getDividerHeight()/2<=mMiddlePositionInScreen){
                              scrollUp(v);
                    }

                }


            }

        }

    }

    private void scrollDown(final View v){

        mMonthListview.post(new Runnable() {
            @Override
            public void run() {
                mMonthListview.smoothScrollBy(v.getBottom() - (mRootLayoutHeight / 3 + v.getHeight()), 1000);

            }
        });

    }


    private void scrollUp(final View v){

        mMonthListview.post(new Runnable() {
            @Override
            public void run() {
                mMonthListview.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
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
