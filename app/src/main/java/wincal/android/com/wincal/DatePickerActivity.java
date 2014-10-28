package wincal.android.com.wincal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;


public class DatePickerActivity extends ActionBarActivity {


    private ListView mMonthListview;
    private ListView mDateListView;
    private ListView mYearListView;
    private MonthYearAdapter mMonthAdapter;
    private MonthYearAdapter mYearAdapter;
    private MonthYearAdapter mDateAdapter;

    private int mMiddlePositionInScreen = 0;
    private int mBottomPositionOfMiddleElement = 0;
    private int mCurrentMonth;
    private int mCurrentYear;
    private int mCurrentDate;
    private int mCurrentYearPosition;
    private int mCurrentMonthPosition;
    private int mCurrentDatePosition;


    private int mOffsetForDate;
    private int mNumberOfMonthDays;

    private String[] daysOfTheMonth;


    private int currentMonthPosition;
    private AtomicBoolean mMonthListBeingTouched = new AtomicBoolean(false);
    private AtomicBoolean mYearListBeingTouched = new AtomicBoolean(false);
    private AtomicBoolean mDateListBeingTouched = new AtomicBoolean(false);
    private int mRootLayoutHeight;
    private ScrollState mScrollStateOfMonthView;
    private ScrollState mScrollStateOfYearView;
    private ScrollState mScrollStateOfDayView;

    private ListViewVisible mMonthViewVisible;
    private ListViewVisible mYearViewVisible;
    private ListViewVisible mDateViewVisible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);
        getCurrentDate();
        findCalendarForCurrentMonth();

        mMonthListview = (ListView) findViewById(R.id.month_listview);
        mDateListView = (ListView) findViewById(R.id.date_listview);
        mYearListView = (ListView) findViewById(R.id.year_listview);

        initializeObjects();


        mMonthListview.setClickable(true);
        String monthNames[] = getResources().getStringArray(R.array.month_names);
        mMonthAdapter = new MonthYearAdapter(this, monthNames, monthNames.length, Constants.NOT_FOR_DATE_VIEW);
        mMonthAdapter.setAllItemsVisible(true);

        mYearAdapter = new MonthYearAdapter(this, null, Constants.NO_OF_YEARS, Constants.NOT_FOR_DATE_VIEW);
        mDateAdapter = new MonthYearAdapter(this, daysOfTheMonth, daysOfTheMonth.length, Constants.FOR_DATE_VIEW);
        mDateAdapter.setCurrentMonth(mCurrentMonth);
        mDateAdapter.setCurrentYear(mCurrentYear);
        mYearAdapter.setAllItemsVisible(false);
        mDateAdapter.setAllItemsVisible(false);

        mMonthListview.setAdapter(mMonthAdapter);
        mYearListView.setAdapter(mYearAdapter);
        mDateListView.setAdapter(mDateAdapter);

        setCurrentPositionsInListViews();

        final RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight = rootLayout.getMeasuredHeight();

                mMonthListview.setSelectionFromTop(mCurrentMonthPosition, mRootLayoutHeight / 3);
                mYearListView.setSelectionFromTop(mCurrentYearPosition, mRootLayoutHeight / 3);
                mDateListView.setSelectionFromTop(mCurrentDatePosition, mRootLayoutHeight / 3);

                setListenersOnListView(mMonthAdapter, mMonthListview, mMonthListBeingTouched, mScrollStateOfMonthView, mMonthViewVisible);
                setListenersOnListView(mYearAdapter, mYearListView, mYearListBeingTouched, mScrollStateOfYearView, mYearViewVisible);
                setListenersOnListView(mDateAdapter, mDateListView, mDateListBeingTouched, mScrollStateOfDayView, mDateViewVisible);


            }
        });


    }

    private void initializeObjects(){

        mMonthViewVisible = new ListViewVisible();
        mMonthViewVisible.setCompleteListViewVisible(true);
        mYearViewVisible = new ListViewVisible();
        mDateViewVisible = new ListViewVisible();

        mScrollStateOfMonthView=new ScrollState();
        mScrollStateOfYearView=new ScrollState();
        mScrollStateOfDayView=new ScrollState();


    }

    private void getCurrentDate() {

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        mCurrentMonth = cal.get(Calendar.MONTH);
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentDate = cal.get(Calendar.DAY_OF_MONTH);


    }

    private void findCalendarForCurrentMonth() {

        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(mCurrentYear, mCurrentMonth - 1, 1);

        // mFirstWeekDayOfMonth=cal.get(Calendar.DAY_OF_WEEK);
        mNumberOfMonthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        daysOfTheMonth = new String[mNumberOfMonthDays + 1];
        for (int i = 1; i <= mNumberOfMonthDays; i++) {

            daysOfTheMonth[i] = String.valueOf(i);
        }

    }

    private void setCurrentPositionsInListViews() {

        mCurrentMonthPosition = mMonthAdapter.getCount() / 2 - Constants.OFFSET_FOR_MONTH + mCurrentMonth;
        mCurrentYearPosition = mYearAdapter.getCount() / 2 - Constants.OFFSET_FOR_YEAR + (mCurrentYear - Constants.STARTING_YEAR);

        mOffsetForDate = findOffsetForDate(mNumberOfMonthDays + 1);
        mCurrentDatePosition = mDateAdapter.getCount() / 2 - mOffsetForDate + mCurrentDate - 1;

        mMonthAdapter.setCurrentPos(mCurrentMonthPosition);
        mYearAdapter.setCurrentPos(mCurrentYearPosition);
        mDateAdapter.setCurrentPos(mCurrentDatePosition);


    }


    private void setListenersOnListView(final MonthYearAdapter adapter, final ListView listView, final AtomicBoolean listBeingTouched, final ScrollState state, final ListViewVisible completeListVisible) {

        state.setScrollState(OnScrollListener.SCROLL_STATE_IDLE);
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {

                        setOtherListViewsInvisible(listView);
                        stopOtherScrolls(mMonthListview);
                        if (!adapter.getAllItemsVisible()) {
                            listBeingTouched.set(true);
                            adapter.setAllItemsVisible(true);
                            adapter.highlightCurrentMonthColor(false);
                            adapter.notifyDataSetChanged();

                        } else if (adapter.getAllItemsVisible() && adapter.getHighlightCurrentMonth()) {
                            listBeingTouched.set(true);
                            adapter.highlightCurrentMonthColor(false);
                            adapter.notifyDataSetChanged();


                        }

                    } else if (state.getScrollState() == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        listBeingTouched.set(true);
                    }

                    //return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {

                        listBeingTouched.compareAndSet(true, false);
                        adapter.highlightCurrentMonthColor(true);

                        // adapter.notifyDataSetChanged();
                        if (completeListVisible.isCompleteListViewVisible()) {
                            putThisViewInMiddle(event.getY(), listView, adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        completeListVisible.setCompleteListViewVisible(true);

                    } else if (state.getScrollState() == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                        listBeingTouched.compareAndSet(true, false);

                    }

                    //return true;

                }


                return false;
            }
        });


        listView.setOnScrollListener(new OnScrollListener() {

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {

                state.setScrollState(scrollState);
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !listBeingTouched.get()) {
                    listBeingTouched.set(true);
                    putSomeRowInMiddle(listView, adapter);

                }

            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                listBeingTouched.compareAndSet(true, false);
//               // putThisViewInMiddle(event.getY(),listView);
//                adapter.highlightCurrentMonthColor(true);
//                adapter.notifyDataSetChanged();
            }
        });


    }

//    void setCompleteListVisible(ListView listView){
//
//        if(listView.getId()==R.id.year_listview){
//            mCompleteYearListVisible=true;
//        }
//        else if(listView.getId()==R.id.month_listview){
//            mCompleteMonthListVisible=true;
//        }
//        else if(listView.getId()==R.id.date_listview){
//            mCompleteDateListVisible=true;
//        }
//
//    }

    private void stopOtherScrolls(ListView listView){

        if(listView.getId()==R.id.month_listview){

            try
            {
                Field field = android.widget.AbsListView.class.getDeclaredField("mFlingRunnable");
                field.setAccessible(true);
                Object flingRunnable = field.get(listView);
                if (flingRunnable != null)
                {
                    Method method = Class.forName("android.widget.AbsListView$FlingRunnable").getDeclaredMethod("endFling");
                    method.setAccessible(true);
                    method.invoke(flingRunnable);
                }
            }
            catch (Exception e) {}

        }

    }

    void putThisViewInMiddle(float y, final ListView listView, MonthYearAdapter adapter) {

        double yValue = Math.ceil((double) y);
        Log.d("yval", "" + yValue);
        for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {

            View v = listView.getChildAt(i);
            if (v != null && v.getTop() != mRootLayoutHeight / 3) {

                // Rect viewRect=new Rect();
                // v.getGlobalVisibleRect(viewRect);
                if (yValue >= v.getTop() - listView.getDividerHeight() && yValue <= v.getBottom()) {

                    Log.d("rajul", "" + v.getTop() + " " + v.getBottom() + " " + mRootLayoutHeight / 3);
                    scrollToMiddle(listView, i, v, adapter);
                }
            }
        }

    }

    void scrollToMiddle(final ListView listView, final int i, final View v, MonthYearAdapter adapter) {

        adapter.setCurrentPos(listView.getFirstVisiblePosition() + i);
        adapter.notifyDataSetChanged();
        listView.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
        Log.d("yhape", "" + v.getTop() + " " + v.getBottom() + " " + mRootLayoutHeight / 3);
//        listView.post(new Runnable() {
//            @Override
//            public void run() {
//
//                Log.d("yhape",""+v.getTop()+" "+v.getBottom()+" "+mRootLayoutHeight/3);
//
//            }
//        });
    }

    class ScrollState {

        private int mScrollStateOfListView;

        public void setScrollState(int scrollState) {

            this.mScrollStateOfListView = scrollState;
        }

        public int getScrollState() {

            return this.mScrollStateOfListView;
        }


    }

    class ListViewVisible {

        private boolean mCompleteListViewVisible;

        public void setCompleteListViewVisible(boolean value) {

            this.mCompleteListViewVisible = value;
        }

        public boolean isCompleteListViewVisible() {
            return this.mCompleteListViewVisible;
        }
    }


    private void setOtherListViewsInvisible(ListView listView) {

        if (listView.getId() == R.id.month_listview) {

            mYearViewVisible.setCompleteListViewVisible(false);
            mDateViewVisible.setCompleteListViewVisible(false);

            if (mYearAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mYearAdapter);
            }
            if (mDateAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mDateAdapter);
            }
        } else if (listView.getId() == R.id.year_listview) {

            mDateViewVisible.setCompleteListViewVisible(false);
            mMonthViewVisible.setCompleteListViewVisible(false);

            if (mMonthAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mMonthAdapter);
            }
            if (mDateAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mDateAdapter);
            }
        } else if (listView.getId() == R.id.date_listview) {

            mMonthViewVisible.setCompleteListViewVisible(false);
            mYearViewVisible.setCompleteListViewVisible(false);

            if (mMonthAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mMonthAdapter);
            }
            if (mYearAdapter.getAllItemsVisible()) {
                makeAllItemsInvisible(mYearAdapter);
            }
        }

    }


    private void makeAllItemsInvisible(MonthYearAdapter adapter) {

        adapter.setAllItemsVisible(false);
        adapter.notifyDataSetChanged();
    }

    private void putSomeRowInMiddle(ListView listView, MonthYearAdapter adapter) {

        for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {
            final View v = listView.getChildAt(i);
            if (v != null) {

                if (mMiddlePositionInScreen == 0) {
                    mMiddlePositionInScreen = mRootLayoutHeight / 3 + v.getHeight() / 2;
                    mBottomPositionOfMiddleElement = mRootLayoutHeight / 3 + v.getHeight();
                }

                if ((v.getTop() >= mRootLayoutHeight / 3) && v.getTop() < mMiddlePositionInScreen) {
                    scrollUp(v, listView, adapter, listView.getFirstVisiblePosition() + i);
                }

                if ((v.getBottom() >= mMiddlePositionInScreen) && v.getBottom() < mBottomPositionOfMiddleElement) {
                    scrollDown(v, listView, adapter, listView.getFirstVisiblePosition() + i);
                }


                if (v.getBottom() <= mMiddlePositionInScreen && v.getBottom() > mRootLayoutHeight / 3) {

                    if (v.getBottom() + listView.getDividerHeight() / 2 >= mMiddlePositionInScreen) {
                        scrollDown(v, listView, adapter, listView.getFirstVisiblePosition() + i);
                    }

                }

                if (v.getTop() >= mMiddlePositionInScreen && v.getTop() < mBottomPositionOfMiddleElement) {

                    if (v.getTop() - listView.getDividerHeight() / 2 <= mMiddlePositionInScreen) {
                        scrollUp(v, listView, adapter, listView.getFirstVisiblePosition() + i);
                    }

                }


            }

        }

    }

    private void scrollDown(final View v, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollBy(v.getBottom() - (mRootLayoutHeight / 3 + v.getHeight()), 1000);
                highLightMiddleRow(adapter, currentPosInMiddle);

            }
        });

    }


    private void highLightMiddleRow(MonthYearAdapter adapter, int currentPosInMiddle) {

        adapter.setCurrentPos(currentPosInMiddle);
        adapter.highlightCurrentMonthColor(true);
        adapter.notifyDataSetChanged();
    }


    private void scrollUp(final View v, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
                highLightMiddleRow(adapter, currentPosInMiddle);

            }
        });

    }

    private int findOffsetForDate(int currentMonth) {

        switch (currentMonth) {

            case 31:
                return 0;
            case 30:
                return -1;
            case 29:
                return -2;
            case 28:
                return -3;

        }
        return 0;
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
