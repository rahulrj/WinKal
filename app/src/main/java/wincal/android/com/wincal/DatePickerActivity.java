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


    private ListView mMonth_listview;
    private MonthAdapter mMonthAdapter;


    private int currentMonthPosition;
    private AtomicBoolean mListBeingTouched = new AtomicBoolean(false);
    private int mRootLayoutHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);

        mMonthAdapter = new MonthAdapter(this, getResources().getStringArray(R.array.month_names));
        mMonthAdapter.setCurrentMonthPos(mMonthAdapter.getCount() / 2 - 3);

        mMonth_listview = (ListView) findViewById(R.id.month_listview);
        mMonth_listview.setAdapter(mMonthAdapter);


        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight = rootLayout.getMeasuredHeight();
                mMonth_listview.setSelectionFromTop(mMonthAdapter.getCount() / 2 - 3, mRootLayoutHeight / 3);
                setListenersOnMonthView();


            }
        });


    }

    private void setListenersOnMonthView() {

        mMonth_listview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!mMonthAdapter.getAllItemsVisible() && event.getAction() == MotionEvent.ACTION_DOWN) {

                    mListBeingTouched.set(true);
                    mMonthAdapter.setAllItemsVisible(true);
                    mMonthAdapter.highlightCurrentMonthColor(false);
                    mMonthAdapter.notifyDataSetChanged();

                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    mListBeingTouched.compareAndSet(true, false);

                }
                return false;
            }
        });

        mMonth_listview.setOnScrollListener(new OnScrollListener() {

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

        for (int i = 0; i <= mMonth_listview.getLastVisiblePosition() - mMonth_listview.getFirstVisiblePosition(); i++) {
            final View v = mMonth_listview.getChildAt(i);
            if (v != null) {
                if (v.getTop() > mRootLayoutHeight / 3 && v.getTop() < (mRootLayoutHeight / 3 + v.getHeight() / 2)) {
                    mMonth_listview.post(new Runnable() {
                        @Override
                        public void run() {
                            mMonth_listview.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
                        }
                    });

                }

                if (v.getBottom() >= mRootLayoutHeight / 2 && v.getBottom() < (mRootLayoutHeight / 2 + (v.getHeight() / 2))) {
                    mMonth_listview.smoothScrollBy(v.getBottom() - (mRootLayoutHeight / 2 + (v.getHeight() / 2)), 1000);
                }

            }

        }

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
