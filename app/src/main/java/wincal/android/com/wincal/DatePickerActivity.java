package wincal.android.com.wincal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    private int mMiddlePositionFromTop;

    private int mInitialMonth = 0;
    private int mFinalMonth = 0;

    private int ACTION_MOVED=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.date_picker);
        getCurrentDate();
        findCalendarForCurrentMonth(mCurrentYear, mCurrentMonth + 1);

        mMonthListview = (ListView) findViewById(R.id.month_listview);
        mDateListView = (ListView) findViewById(R.id.date_listview);
        mYearListView = (ListView) findViewById(R.id.year_listview);

        initializeObjects();

        //mMonthListview.setClickable(true);
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

                getMiddlePosition();


            }
        });


    }


    /**
     * returns the midlle position which has the highlight
     */
    protected void getMiddlePosition() {

        mMonthListview.post(new Runnable() {
            public void run() {

                mMiddlePositionFromTop = mMonthAdapter.getCurrentPos() - mMonthListview.getFirstVisiblePosition();
                getInitialAndFinalMonth(mMiddlePositionFromTop);

            }
        });

    }

    protected void getInitialAndFinalMonth(int position) {

        View middleView = mMonthListview.getChildAt(position);
        TextView monthView = (TextView) middleView.findViewById(R.id.row_number);
        mInitialMonth = Integer.parseInt(monthView.getText().toString());

    }


    private void initializeObjects() {

        mMonthViewVisible = new ListViewVisible();
        mMonthViewVisible.setCompleteListViewVisible(true);
        mYearViewVisible = new ListViewVisible();
        mDateViewVisible = new ListViewVisible();

        mScrollStateOfMonthView = new ScrollState();
        mScrollStateOfYearView = new ScrollState();
        mScrollStateOfDayView = new ScrollState();


    }

    private void getCurrentDate() {

        Date currentDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);

        mCurrentMonth = cal.get(Calendar.MONTH);
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentDate = cal.get(Calendar.DAY_OF_MONTH);


    }

    private void findCalendarForCurrentMonth(int currentYear, int currentMonth) {

        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(currentYear, currentMonth - 1, 1);

        // mFirstWeekDayOfMonth=cal.get(Calendar.DAY_OF_WEEK);
        mNumberOfMonthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.d("abc", "" + mNumberOfMonthDays + " " + currentMonth);

        daysOfTheMonth = new String[mNumberOfMonthDays];
        for (int i = 0; i < mNumberOfMonthDays; i++) {

            daysOfTheMonth[i] = String.valueOf(i + 1);
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

                    if(state.getScrollState()==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || state.getScrollState()==OnScrollListener.SCROLL_STATE_FLING){

                        ACTION_MOVED=1;
                    }
                    else if(state.getScrollState()==OnScrollListener.SCROLL_STATE_IDLE){

                       ACTION_MOVED=0;
                    }

                    disableOtherListViews(listView);
                    setOtherListViewsInvisible(listView);
                    stopOtherScrolls(listView);


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


                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {

                        if (listView.getId() == R.id.date_listview) {

                            addDatesInDateView();
                        }

                    } else if (state.getScrollState() == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                        listBeingTouched.set(true);
                    }

                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    enableAllListViews();
                    listBeingTouched.compareAndSet(true, false);

                    if(ACTION_MOVED!=1) {
                        if (completeListVisible.isCompleteListViewVisible()) {
                            putThisViewInMiddle(event.getY(), listView, adapter);
                        }

                    }





                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {

                        adapter.highlightCurrentMonthColor(true);
                        if (completeListVisible.isCompleteListViewVisible()) {
                            //putThisViewInMiddle(event.getY(), listView, adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }

                        completeListVisible.setCompleteListViewVisible(true);
                    }


                }
                else if(event.getAction()==MotionEvent.ACTION_MOVE){

                    ACTION_MOVED=1;

                }

                  return false;
            }
        });


        listView.setOnScrollListener(new OnScrollListener() {

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {

                state.setScrollState(scrollState);

                if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){
                    Log.d("yes","idle");
                    //ACTION_MOVED=0;
                }
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !listBeingTouched.get()) {

                    listBeingTouched.set(true);
                    putSomeRowInMiddle(listView, adapter);

                }

            }
        });


    }


    protected void addDatesInDateView() {

        View monthChildView = getMiddleView(mMonthListview, mMiddlePositionFromTop);
        View yearChildView = getMiddleView(mYearListView, mMiddlePositionFromTop);

        TextView month = (TextView) monthChildView.findViewById(R.id.row_number);
        TextView year = (TextView) yearChildView.findViewById(R.id.row_number);

        int yearText = Integer.parseInt(year.getText().toString());
        int monthText = Integer.parseInt(month.getText().toString());

        mFinalMonth = monthText;
        findCalendarForCurrentMonth(yearText, monthText);
        setNewDatesInDateAdapter(yearText, monthText);

    }

    protected void setNewDatesInDateAdapter(int year, int month) {

        mDateAdapter.setNewDateParameters(daysOfTheMonth);
        int daysInInitialMonth = getNumberOfDaysInMonth(mInitialMonth, year);
        int daysInFinalMonth = getNumberOfDaysInMonth(mFinalMonth, year);
        int offsetForAdapter = findOffestForAdapter(daysInInitialMonth, daysInFinalMonth);

        Log.d("rahu;l", "" + daysInInitialMonth + " " + daysInFinalMonth);
        mDateAdapter.setCurrentPos(mDateAdapter.getCurrentPos() - offsetForAdapter);              // to keep the same date when month changes
        mDateAdapter.setCurrentMonth(month - 1);
        mDateAdapter.setCurrentYear(year);
        mDateAdapter.notifyDataSetChanged();
        mDateListView.setSelectionFromTop(mDateAdapter.getCurrentPos(), mRootLayoutHeight / 3);  //to keep the same date when month changes
        mInitialMonth = mFinalMonth;


    }

    protected int getNumberOfDaysInMonth(int month, int year) {

        Calendar cal = new GregorianCalendar();
        cal.set(year, month - 1, 1);
        int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return numberOfDays;


    }

    protected View getMiddleView(ListView listView, int position) {

        return listView.getChildAt(mMiddlePositionFromTop);

    }

    private void disableOtherListViews(ListView listView) {

        if (listView.getId() == R.id.month_listview) {
            mDateListView.setEnabled(false);
            mYearListView.setEnabled(false);

        } else if (listView.getId() == R.id.year_listview) {
            mDateListView.setEnabled(false);
            mMonthListview.setEnabled(false);
        } else if (listView.getId() == R.id.date_listview) {
            mMonthListview.setEnabled(false);
            mYearListView.setEnabled(false);
        }

    }

    private void enableAllListViews() {

        mMonthListview.setEnabled(true);
        mDateListView.setEnabled(true);
        mYearListView.setEnabled(true);
    }

    private ListView getScrollingListView(ScrollState state1, ScrollState state2, ListView view1, ListView view2) {

        ListView listView = (state1.getScrollState() == OnScrollListener.SCROLL_STATE_FLING) ? view1 : (state2.getScrollState() == OnScrollListener.SCROLL_STATE_FLING) ? view2 : null;

        return listView;

    }

    private void stopOtherScrolls(ListView listView) {


        if (listView.getId() == R.id.month_listview) {
            listView = getScrollingListView(mScrollStateOfDayView, mScrollStateOfYearView, mDateListView, mYearListView);
        } else if (listView.getId() == R.id.date_listview) {
            listView = getScrollingListView(mScrollStateOfMonthView, mScrollStateOfYearView, mMonthListview, mYearListView);
        } else if (listView.getId() == R.id.year_listview) {
            listView = getScrollingListView(mScrollStateOfMonthView, mScrollStateOfDayView, mMonthListview, mDateListView);
        }

        if (listView != null) {

            try {
                Field field = android.widget.AbsListView.class.getDeclaredField("mFlingRunnable");
                field.setAccessible(true);
                Object flingRunnable = field.get(listView);
                if (flingRunnable != null) {
                    Method method = Class.forName("android.widget.AbsListView$FlingRunnable").getDeclaredMethod("endFling");
                    method.setAccessible(true);
                    method.invoke(flingRunnable);
                }
            } catch (Exception e) {
            }

        }

    }

    void putThisViewInMiddle(float y, final ListView listView, MonthYearAdapter adapter) {

        double yValue = Math.ceil((double) y);
        //Log.d("yval", "" + yValue);
        for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {

            View v = listView.getChildAt(i);
            if (v != null) {

                // Rect viewRect=new Rect();
                // v.getGlobalVisibleRect(viewRect);
                if (yValue >= v.getTop() - listView.getDividerHeight() && yValue <= v.getBottom()) {

                    //Log.d("rajul", "" + v.getTop() + " " + v.getBottom() + " " + mRootLayoutHeight / 3);
                    scrollToMiddle(listView, i, v, adapter);
                    break;
                }
            }
        }

    }

    void scrollToMiddle(final ListView listView, final int i, final View v, final MonthYearAdapter adapter) {

        adapter.setCurrentPos(listView.getFirstVisiblePosition() + i);
        adapter.notifyDataSetChanged();
        listView.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);





        // Log.d("yhape", "" + v.getTop() + " " + v.getBottom() + " " + mRootLayoutHeight / 3);
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

                /// Log.d("A",""+v.getTop()+ " "+mRootLayoutHeight/3+ " "+mMiddlePositionInScreen);
                if ((v.getTop() >= mRootLayoutHeight / 3) && v.getTop() < mMiddlePositionInScreen) {
                    scrollUp(v.getTop(), listView, adapter, listView.getFirstVisiblePosition() + i);
                    break;
                }

                // Log.d("B",""+v.getBottom()+ " "+mMiddlePositionInScreen+ " "+mBottomPositionOfMiddleElement);
                if ((v.getBottom() >= mMiddlePositionInScreen) && v.getBottom() < mBottomPositionOfMiddleElement) {

                    // Log.d("rahulrajayes",""+v.getBottom()+" "+mMiddlePositionInScreen+" "+mBottomPositionOfMiddleElement);
                    scrollDown(v.getBottom(), v.getHeight(), listView, adapter, listView.getFirstVisiblePosition() + i);
                    break;
                }


                // Log.d("C",""+v.getBottom()+ " "+mMiddlePositionInScreen+ " "+mRootLayoutHeight/3+ " "+(v.getBottom()+listView.getDividerHeight()/2));
                if (v.getBottom() <= mMiddlePositionInScreen && v.getBottom() > mRootLayoutHeight / 3) {

                    if (v.getBottom() + listView.getDividerHeight() / 2 >= mMiddlePositionInScreen) {
//                        Log.d("rahulraja",""+v.getBottom()+" "+mMiddlePositionInScreen+" "+mRootLayoutHeight/3);
                        scrollDown(v.getBottom(), v.getHeight(), listView, adapter, listView.getFirstVisiblePosition() + i);
                        break;
                    }

                }

                //Log.d("D",""+v.getTop()+ " "+mMiddlePositionInScreen+ " "+mBottomPositionOfMiddleElement+" "+(v.getTop()-listView.getDividerHeight()/2));

                if (v.getTop() >= mMiddlePositionInScreen && v.getTop() < mBottomPositionOfMiddleElement) {

                    if (v.getTop() - listView.getDividerHeight() / 2 <= mMiddlePositionInScreen) {
                        scrollUp(v.getTop(), listView, adapter, listView.getFirstVisiblePosition() + i);
                        break;
                    }

                }


            }

        }

    }

    private void scrollDown(final int viewBottom, final int viewHeight, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

        listView.post(new Runnable() {
            @Override
            public void run() {
                // Log.d("rahulrajadown","gonedown"+v.getBottom());
                listView.smoothScrollBy(viewBottom - (mRootLayoutHeight / 3 + viewHeight), 1000);
                highLightMiddleRow(adapter, currentPosInMiddle);

            }
        });

    }


    private void scrollUp(final int viewTop, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

        listView.post(new Runnable() {
            @Override
            public void run() {
                // Log.d("rahulraja","goneup"+v.getTop());
                listView.smoothScrollBy(viewTop - mRootLayoutHeight / 3, 1000);
                highLightMiddleRow(adapter, currentPosInMiddle);

            }
        });

    }

    private void highLightMiddleRow(MonthYearAdapter adapter, int currentPosInMiddle) {

        adapter.setCurrentPos(currentPosInMiddle);
        adapter.highlightCurrentMonthColor(true);
        adapter.notifyDataSetChanged();


    }

    private int findOffestForAdapter(int initialMonth, int finalMonth) {

        switch (initialMonth) {

            case 31:
                if (finalMonth == 30 || finalMonth == 29) {
                    return 3;

                } else if (finalMonth == 28) {
                    return 7;

                }
                break;

            case 30:
                if (finalMonth == 31) {
                    return -3;
                } else if (finalMonth == 28)
                    return 4;
                break;

            case 28:
                if (finalMonth == 31) {
                    return -7;
                } else if (finalMonth == 30)
                    return -4;
                break;

            case 29:
                if(finalMonth==31){

                    return -3;
                }
                else if(finalMonth==30){

                }
        }
        return 0;
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
