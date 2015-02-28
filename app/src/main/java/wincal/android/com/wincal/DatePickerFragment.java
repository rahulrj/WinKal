package wincal.android.com.wincal;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


public class DatePickerFragment extends DialogFragment {


    private RelativeLayout mRootLayout;
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
    private int mCurrentDummyDate;
    private int mCurrentYearPosition;
    private int mCurrentMonthPosition;
    private int mCurrentDatePosition;

    private int mNumberOfMonthDays;

    private String[] daysOfTheMonth;


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

    private View mDummyView;



    //private int mInitialMonth;

    private int mMiddlePositionFromTop;

    private int mInitialMonth = 0;
    private int mFinalMonth = 0;

    private int ACTION_MOVED=0;
    private String mDialogTitle;

    //private int mFirstVisiblePosition;
    private ColorDrawable mColorDrawable;
    //private boolean mLowerHalf=false;
    private int mDateInDummyView;
    boolean mMonthOrYearTouched;
    int mDelta;


    private StringBuilder mFirstVisibleMonth=new StringBuilder("");
    private StringBuilder mFirstVisibleYear=new StringBuilder("");
   private AtomicInteger mFirstVisiblePositionMonth=new AtomicInteger(0);
   private AtomicInteger mFirstVisiblePositionYear=new AtomicInteger(0);
   private AtomicBoolean mLowerHalfMonth=new AtomicBoolean(false);
   private AtomicBoolean mLowerHalfYear=new AtomicBoolean(false);
   private AtomicInteger mInitialPositionMonth=new AtomicInteger(0);
   private AtomicInteger mInitialPositionYear=new AtomicInteger(0);
   private int mStartingPositionOfScrollMonth;
   private int mStartingPositionOfScrollYear;

   private ActionBar mActionBar;
   private DateSelectListener mDateSelectListener;
   private Handler mHandler;
    private Runnable mStatusChecker;
    private Runnable mScrollDownTask;
    private Runnable mScrollUpTask;

    private AtomicBoolean itemScrolledToMiddleMonth=new AtomicBoolean(false);
    private AtomicBoolean itemScrolledToMiddleDate=new AtomicBoolean(false);
    private AtomicBoolean itemScrolledToMiddleYear=new AtomicBoolean(false);
    private boolean mDummyTouched=false;

    private AtomicBoolean mOneViewScrolledMonth=new AtomicBoolean(false);
    private AtomicBoolean mOneViewScrolledYear=new AtomicBoolean(false);


    @Override
    public View  onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


        getCurrentDate();
        retrieveInitialArgs();
        mHandler=new Handler();
        if (getDialog() != null) {
            try {
                setRetainInstance(true);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        View view = inflater.inflate(R.layout.date_picker, container, false);

        mMonthListview = (ListView) view.findViewById(R.id.month_listview);
        mDateListView = (ListView) view.findViewById(R.id.date_listview);
        mYearListView = (ListView) view.findViewById(R.id.year_listview);
        mColorDrawable = new ColorDrawable(Color.rgb(255, 255, 255));

        initializeObjects();

        //mMonthListview.setClickable(true);
        String monthNames[] = getResources().getStringArray(R.array.month_names);
        mMonthAdapter = new MonthYearAdapter(getActivity(), monthNames, monthNames.length, Constants.NOT_FOR_DATE_VIEW,mMonthListview);
        mMonthAdapter.setAllItemsVisible(true);

        mYearAdapter = new MonthYearAdapter(getActivity(), null, Constants.NO_OF_YEARS, Constants.NOT_FOR_DATE_VIEW,mYearListView);
        mDateAdapter = new MonthYearAdapter(getActivity(), daysOfTheMonth, daysOfTheMonth.length, Constants.FOR_DATE_VIEW,mDateListView);
        mDateAdapter.setCurrentMonth(mCurrentMonth);
        mDateAdapter.setCurrentYear(mCurrentYear);
        mYearAdapter.setAllItemsVisible(false);
        mDateAdapter.setAllItemsVisible(false);

        mMonthListview.setAdapter(mMonthAdapter);
        mYearListView.setAdapter(mYearAdapter);
        mDateListView.setAdapter(mDateAdapter);

        setCurrentPositionsInListViews();
        mRootLayout = (RelativeLayout)view. findViewById(R.id.root_layout);


        ViewTreeObserver vto = mRootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                mRootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight = mRootLayout.getMeasuredHeight();

                mMonthListview.setSelectionFromTop(mCurrentMonthPosition, mRootLayoutHeight / 3);
                mYearListView.setSelectionFromTop(mCurrentYearPosition, mRootLayoutHeight / 3);
                mDateListView.setSelectionFromTop(mCurrentDatePosition, mRootLayoutHeight / 3);

                mMonthListview.post(new Runnable() {
                    @Override
                    public void run() {

                        getMiddlePosition();
                        setAllListeners();
                       // Log.d("rahulll",""+mDummyView+" "+mFirstVisibleMonth+" "+mFirstVisibleYear);

                    }
                });
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActionBar=((MainActivity)getActivity()).getSupportActionBar();
        setClickListenerOnActionBar(mActionBar);

    }

    public void setDateSelectListener(DateSelectListener dateSelectListener){

            this.mDateSelectListener=dateSelectListener;
    }

    public void setClickListenerOnActionBar(ActionBar actionbar){

        ImageView done=(ImageView)mActionBar.getCustomView().findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View monthView=getMiddleView(mMonthListview,mMiddlePositionFromTop);
                View yearView=getMiddleView(mYearListView,mMiddlePositionFromTop);
                View dateView=getMiddleView(mDateListView,mMiddlePositionFromTop);

                int month=(Integer.parseInt(((TextView)monthView.findViewById(R.id.row_number)).getText().toString()));
                int year=(Integer.parseInt(((TextView)yearView.findViewById(R.id.row_number)).getText().toString()));
                int date=(Integer.parseInt(((TextView)dateView.findViewById(R.id.row_number)).getText().toString()));

                mDateSelectListener.onSelectDate(date,month,year);
            }
        });
    }


    private void setAllListeners(){

        //Log.d("rahul",""+mDummyView+" "+mFirstVisibleMonth+" "+mFirstVisibleYear);
        //Toast.makeText(getActivity(),""+abc+" "+mFirstVisiblePositionMonth+" "+mFirstVisiblePositionYear, Toast.LENGTH_LONG).show();
        setListenersOnListView(mMonthAdapter, mMonthListview, mMonthListBeingTouched, mScrollStateOfMonthView, mMonthViewVisible, mFirstVisiblePositionMonth, mLowerHalfMonth, mInitialPositionMonth, mStartingPositionOfScrollMonth, mFirstVisibleMonth,itemScrolledToMiddleMonth,mOneViewScrolledMonth);
        setListenersOnListView(mYearAdapter, mYearListView, mYearListBeingTouched, mScrollStateOfYearView, mYearViewVisible,mFirstVisiblePositionYear,mLowerHalfYear,mInitialPositionYear,mStartingPositionOfScrollYear,mFirstVisibleYear,itemScrolledToMiddleYear,mOneViewScrolledYear);
        setListenersOnListView(mDateAdapter, mDateListView, mDateListBeingTouched, mScrollStateOfDayView, mDateViewVisible,new AtomicInteger(0),new AtomicBoolean(),new AtomicInteger(0),0,null,itemScrolledToMiddleDate,null);
        //mDateListView.setVisibility(View.INVISIBLE);

    }

    protected void retrieveInitialArgs(){

        Bundle args=getArguments();
        if(args!=null){

            mCurrentMonth=args.getInt(Constants.MONTH,mCurrentMonth);
            mCurrentYear=args.getInt(Constants.YEAR,mCurrentYear);
            mCurrentDate=args.getInt(Constants.DATE,mCurrentDate);
            mCurrentDummyDate=args.getInt(Constants.DUMMY_DATE,mCurrentDummyDate);
            mDialogTitle=args.getString(Constants.DIALOG_TITLE);

        }
        mDateInDummyView=mCurrentDate;
        findCalendarForCurrentMonth(mCurrentYear, mCurrentMonth + 1);
        Dialog dialog = getDialog();
        if (dialog != null) {
            if (mDialogTitle != null) {
                dialog.setTitle(mDialogTitle);
            } else {
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            }
        }
    }

    /**
     * Restore current states from savedInstanceState
     *
     * @param savedInstanceState
     * @param key
     */
    public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
        if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
            Bundle savedState = savedInstanceState.getBundle(key);
            setArguments(savedState);
        }
    }


    /**
     * Restore state for dialog
     *
     * @param savedInstanceState
     * @param key
     * @param dialogTag
     */
    public void restoreDialogStatesFromKey(FragmentManager manager,
                                           Bundle savedInstanceState, String key, String dialogTag) {
        restoreStatesFromKey(savedInstanceState, key);

        DatePickerFragment existingDialog = (DatePickerFragment) manager
                .findFragmentByTag(dialogTag);
        if (existingDialog != null) {
            existingDialog.dismiss();
            show(manager, dialogTag);
        }
    }

    /**
     * Save current state to bundle outState
     *
     * @param outState
     * @param key
     */
    public void saveStatesToKey(Bundle outState, String key) {
        outState.putBundle(key, getSavedStates());
    }

    /**
     * Get current saved sates of the Calendar. Useful for handling rotation.
     */
    public Bundle getSavedStates() {

        Bundle bundle = new Bundle();

        try {
            int middlePosition = mMonthAdapter.getCurrentPos() - mMonthListview.getFirstVisiblePosition();
            TextView monthView = (TextView) mMonthListview.getChildAt(middlePosition).findViewById(R.id.row_number);
            TextView yearView = (TextView) mYearListView.getChildAt(middlePosition).findViewById(R.id.row_number);
            TextView dateView = (TextView) mDateListView.getChildAt(middlePosition).findViewById(R.id.row_number);
            TextView dummyView = (TextView) mDummyView.findViewById(R.id.row_number);


            bundle.putInt(Constants.MONTH, Integer.parseInt(monthView.getText().toString()) - 1);
            bundle.putInt(Constants.YEAR, Integer.parseInt(yearView.getText().toString()));
            bundle.putInt(Constants.DATE, Integer.parseInt(dateView.getText().toString()));
            bundle.putInt(Constants.DUMMY_DATE, Integer.parseInt(dummyView.getText().toString()));
            if (mDialogTitle != null) {
                bundle.putString(Constants.DIALOG_TITLE, mDialogTitle);
            }
        }catch (Exception e){
            return null;
        }

        return bundle;
    }


//A good way to initialize
    public static DatePickerFragment newInstance(String dialogTitle, int month,
                                               int year,int date) {
       DatePickerFragment fragment = new DatePickerFragment();

        Bundle args = new Bundle();
        args.putString(Constants.DIALOG_TITLE, dialogTitle);
        args.putInt(Constants.MONTH, month);
        args.putInt(Constants.YEAR, year);
        args.putInt(Constants.DATE,date);

        fragment.setArguments(args);

        return fragment;
    }


    /**
     * returns the midlle position which has the highlight
     */
    protected void getMiddlePosition() {

//        mMonthListview.post(new Runnable() {
//            public void run() {

                mMiddlePositionFromTop = mMonthAdapter.getCurrentPos() - mMonthListview.getFirstVisiblePosition();
                setAnimationParams();
                putDummyViewInMiddle();
                getInitialAndFinalMonth(mMiddlePositionFromTop);
                mFirstVisiblePositionMonth.set(mMonthListview.getFirstVisiblePosition());
                mFirstVisiblePositionYear.set(mYearListView.getFirstVisiblePosition());

          //  }
     //  });

    }

    private void setAnimationParams(){

        mDateAdapter.mMiddlePositionFromTop=mMiddlePositionFromTop;
        mMonthAdapter.setScrollState(mScrollStateOfMonthView);
        mYearAdapter.setScrollState(mScrollStateOfYearView);
        mDateAdapter.setScrollState(mScrollStateOfDayView);
        mYearAdapter.setTouchedParam(mYearListBeingTouched);
        mMonthAdapter.setTouchedParam(mMonthListBeingTouched);
        mDateAdapter.setTouchedParam(mDateListBeingTouched);


    }

    private void putDummyViewInMiddle(){

                View middleView=mDateListView.getChildAt(mMiddlePositionFromTop);
                LayoutInflater inflater = (LayoutInflater)   getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mDummyView=inflater.inflate(R.layout.calendar_row,null);
                mDummyView.setLayoutParams(new RelativeLayout.LayoutParams(middleView.getWidth(),middleView.getHeight()));

                int location[]=new int[2];
                middleView.getLocationInWindow(location);
                mDummyView.setX(location[0]);
                mDummyView.setY(middleView.getY());

                setDataInDummyView();

                mRootLayout.addView(mDummyView);
                mInitialPositionMonth.set(middleView.getTop());
                mInitialPositionYear.set(middleView.getTop());
                mStartingPositionOfScrollMonth=mStartingPositionOfScrollYear=mInitialPositionMonth.get();
               // mInitialMonthForDummyView=((TextView)(getMiddleView(mMonthListview,0).findViewById(R.id.row_text))).getText().toString();

                mFirstVisibleMonth.delete(0,mFirstVisibleMonth.length());
                mFirstVisibleYear.delete(0,mFirstVisibleYear.length());

                mFirstVisibleMonth.append(((TextView)(getMiddleView(mMonthListview,0).findViewById(R.id.row_text))).getText().toString());
                mFirstVisibleYear.append(((TextView)(getMiddleView(mYearListView,0).findViewById(R.id.row_number))).getText().toString());

              // Toast.makeText(getActivity(),""+mFirstVisibleYear,Toast.LENGTH_SHORT).show();



    }

    private void setDataInDummyView(){

        Log.d("rahul","point1");

        int temporaryDate;
        if(mCurrentDummyDate!=0){
            temporaryDate=mCurrentDummyDate;
        }
        else{
            temporaryDate=mCurrentDate;
        }

         Date date=new GregorianCalendar(mCurrentYear,mCurrentMonth,temporaryDate).getTime();

        String dayOfTheWeek=new SimpleDateFormat("EEEE").format(date);
        ((TextView) mDummyView.findViewById(R.id.row_text)).setText(dayOfTheWeek);
        ((TextView) mDummyView.findViewById(R.id.row_text)).setTextColor(mColorDrawable.getColor());
        ((TextView) mDummyView.findViewById(R.id.row_number)).setText(String.format("%02d", temporaryDate));
        mDummyView.setBackgroundColor(getResources().getColor(R.color.material_selected_row_color));


    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    protected void getInitialAndFinalMonth(int position) {

        View middleView = mMonthListview.getChildAt(position);
        if(middleView!=null) {
            TextView monthView = (TextView) middleView.findViewById(R.id.row_number);
            mInitialMonth = Integer.parseInt(monthView.getText().toString());
        }

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
       // mCurrentMonth=2;
        mCurrentYear = cal.get(Calendar.YEAR);
        mCurrentDate = cal.get(Calendar.DAY_OF_MONTH);


    }

    private void findCalendarForCurrentMonth(int currentYear, int currentMonth) {

        Calendar cal = new GregorianCalendar();
        cal.clear();
        cal.set(currentYear, currentMonth - 1, 1);

        // mFirstWeekDayOfMonth=cal.get(Calendar.DAY_OF_WEEK);
        mNumberOfMonthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        daysOfTheMonth = new String[mNumberOfMonthDays];
        for (int i = 0; i < mNumberOfMonthDays; i++) {

            daysOfTheMonth[i] = String.valueOf(i + 1);
        }

    }


    private void setCurrentPositionsInListViews() {

        //Log.d("initial",""+mMonthListview.getFirstVisiblePosition());

        mCurrentMonthPosition = mMonthAdapter.getCount() / 2 - Constants.OFFSET_FOR_MONTH + mCurrentMonth;
        mCurrentYearPosition = mYearAdapter.getCount() / 2 - Constants.OFFSET_FOR_YEAR + (mCurrentYear - Constants.STARTING_YEAR);


        // For placing the mCurrentDate in the middle of the screen
        View view=mDateAdapter.getView(mDateAdapter.getCount()/2,null,mDateListView);
        int textInView=Integer.parseInt(((TextView) view.findViewById(R.id.row_number)).getText().toString());
        mCurrentDatePosition=mDateAdapter.getCount()/2+(mCurrentDate-textInView);


        mMonthAdapter.setCurrentPos(mCurrentMonthPosition);
        mYearAdapter.setCurrentPos(mCurrentYearPosition);
        mDateAdapter.setCurrentPos(mCurrentDatePosition);

    }


    private void setListenersOnListView(final MonthYearAdapter adapter, final ListView listView, final AtomicBoolean listBeingTouched, final ScrollState state, final ListViewVisible completeListVisible, final AtomicInteger firstVisiblePosition, final AtomicBoolean lowerHalf, final AtomicInteger initialPosition, final int startingPositionOfScroll, final StringBuilder firstVisibleText, final AtomicBoolean itemScrolledToMiddle, final AtomicBoolean oneViewScrolled) {

        state.setScrollState(OnScrollListener.SCROLL_STATE_IDLE);
        listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if(listView.getId()==R.id.month_listview || listView.getId()==R.id.year_listview){
                        if(!mDummyTouched)
                            mDummyView.setVisibility(View.VISIBLE);
                            //mColorDrawable.setAlpha(255);

//                        mDummyView.setVisibility(View.VISIBLE);
//                        mDateListView.setVisibility(View.INVISIBLE);
                        mMonthOrYearTouched=true;

                    }
                    else{
                        mDummyTouched=false;
                        mDummyView.setVisibility(View.INVISIBLE);
                    }

                    itemScrolledToMiddle.set(false);

//                    mItemMovedToMiddle=false;
//                    if(listView.getId()==R.id.year_listview)
//                    Log.d("itemmoved",""+mItemMovedToMiddle);
                    if(ACTION_MOVED==1) {
                        if (state.getScrollState() == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL || state.getScrollState() == OnScrollListener.SCROLL_STATE_FLING)
                            ACTION_MOVED = 1;
                        else if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE)
                            ACTION_MOVED = 0;
                    }

                    disableOtherListViews(listView);
                    setOtherListViewsInvisible(listView);
                    stopOtherScrolls(listView);
                    listBeingTouched.set(true);

                    if (!adapter.getAllItemsVisible()) {
                       // listBeingTouched.set(true);
                        //Log.d("rahul","gaya");
                        adapter.setAllItemsVisible(true);
                        //adapter.highlightCurrentMonthColor(false);
                        adapter.notifyDataSetChanged();

                    }

//                    else if (adapter.getAllItemsVisible() && adapter.getHighlightCurrentMonth()) {
//                       // listBeingTouched.set(true);
//                        adapter.highlightCurrentMonthColor(false);
//                        adapter.notifyDataSetChanged();
//
//                    }


                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE && mMonthOrYearTouched) {

                        if (listView.getId() == R.id.date_listview) {

                            addDatesInDateView();
                        }

                    } else if (state.getScrollState() == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                       // listBeingTouched.set(true);
                    }

                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    enableAllListViews();
                    listBeingTouched.compareAndSet(true, false);
                    //Log.d("list touch",""+listBeingTouched.get());
                    //listBeingTouched.set(false);

                    if(ACTION_MOVED!=1) {
                        if (completeListVisible.isCompleteListViewVisible()) {
                            putThisViewInMiddle(event.getY(), listView, adapter);
                        }

                    }

                    if (state.getScrollState() == OnScrollListener.SCROLL_STATE_IDLE) {

                      //  Log.d("rahu;","called");
                        ///adapter.highlightCurrentMonthColor(true);
                        completeListVisible.setCompleteListViewVisible(true);
                        //adapter.notifyDataSetChanged();



                    }


                }
                else if(event.getAction()==MotionEvent.ACTION_MOVE){

                    ACTION_MOVED=1;
                    if(listView.getId()==R.id.date_listview) {
                        mDummyTouched = true;
                    }

                }

                  return false;
            }
        });


        listView.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (listView.getId() !=R.id.date_listview) {
                   // int position = -(listView.getFirstVisiblePosition() - firstVisiblePosition.get()) + mMiddlePositionFromTop;
                  // Log.d("rahull", "" + listView.getFirstVisiblePosition() + " " + firstVisiblePosition.get() + " " + position);

                    View cc=null;
                    if(!oneViewScrolled.get()) {
                        int position = mMiddlePositionFromTop;
                         cc = listView.getChildAt(position);
                        if(cc!=null) {
                            cc.setTag(String.valueOf(listView.getId()));
                            oneViewScrolled.set(true);
                        }
                    }


                    View c=listView.findViewWithTag(String.valueOf(listView.getId()));
//                    if(c!=null) {
//                        //TextView ab = (TextView) c.findViewById(R.id.row_number);
//                        Log.d("number", "" + c.getTag().toString());
//                    }

                    if (c != null) {

                        int heightOfView = c.getHeight() / 2;
                        if (c.getTop() != 0 && c.getTop() > 0) {

                            fadeView(c,c.getTop(), heightOfView, listView, firstVisiblePosition, lowerHalf, initialPosition, startingPositionOfScroll, firstVisibleText,oneViewScrolled);
                        }
                    }

                }
                else{
                   // mDummyView.setVisibility(View.INVISIBLE);
                }
            }



            public void onScrollStateChanged(AbsListView view, int scrollState) {

                state.setScrollState(scrollState);
                mActionBar.getCustomView().findViewById(R.id.done).setVisibility(View.GONE);
                if(scrollState==OnScrollListener.SCROLL_STATE_IDLE){

                    mActionBar.getCustomView().findViewById(R.id.done).setVisibility(View.VISIBLE);
                    if(listView.getId()==R.id.month_listview || listView.getId()==R.id.year_listview) {
                        putDayOnDummyView();
                    }


                   // mDummyView.setVisibility(View.VISIBLE);
                }

//                if(listView.getId()==R.id.month_listview){
//
//                    Log.d("stats",""+scrollState+" "+listBeingTouched.get()+" "+itemScrolledToMiddle);
//                }
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !listBeingTouched.get()) {

                   // listBeingTouched.set(true);
                    putSomeRowInMiddle(listView, adapter);
//                    if(listView.getId()==R.id.month_listview) {
//                        String s1 = ((TextView) listView.getChildAt(1).findViewById(R.id.row_number)).getText().toString();
//                        String s2 = ((TextView) listView.getChildAt(2).findViewById(R.id.row_number)).getText().toString();
//                        Log.d("values",s1+" "+s2);
//                    }
                    //mItemMovedToMiddle=true;
                    itemScrolledToMiddle.set(true);

                }

            }
        });

    }








    public void fadeView(View c,int top,int heightOfView,ListView listView,AtomicInteger firstVisiblePosition,AtomicBoolean lowerHalf,AtomicInteger initialPosition,int startingPositionForScroll,StringBuilder firstVisibleText,AtomicBoolean oneViewScrolled){


        int fadeFraction=0;
        if(!lowerHalf.get()) {
            fadeFraction = findFadeFraction(top, heightOfView,listView,firstVisiblePosition,lowerHalf,initialPosition,firstVisibleText);

        }
        else{

            fadeFraction=findFadeFractionLower(c,top,heightOfView,listView,firstVisiblePosition,lowerHalf,initialPosition,startingPositionForScroll,oneViewScrolled);

           }

        mColorDrawable.setAlpha(fadeFraction);
        // ScaleAnimation scaleAnimation=new ScaleAnimation((float)1.0,(float)fadeFraction,(float)0.0,(float)fadeFraction,(float)0.5,(float)0.5);
        TextView rowText=(TextView)mDummyView.findViewById(R.id.row_text);
        //Log.d("fade",""+fadeFraction+" "+rowText.getText().toString()+" "+mFadeIncrement+" "+mFadeLowerIncrement);
        if(fadeFraction==0){

            rowText.setTextColor(getResources().getColor(R.color.material_selected_row_color));
        }
        else {
            rowText.setTextColor(mColorDrawable.getColor());
        }

    }


    public int findFadeFraction(int top,int heightOfView,ListView listView,AtomicInteger firstVisiblePosition,AtomicBoolean lowerHalf,AtomicInteger initialPosition,StringBuilder firstVisibleText){

       // Log.d("rahul",""+top+" "+initialPosition.get());
            int id=0;
             if(listView.getId()==R.id.month_listview){
                 id=R.id.row_text;
             }
        else{
                 id=R.id.row_number;
             }
            //Log.d("rahul",""+top+ " "+initialPosition+ " "+heightOfView+ " "+firstVisibleText.toString()+ " "+ ((TextView) (getMiddleView(listView, 0).findViewById(R.id.row_number))).getText().toString());
            if (Math.abs(top- initialPosition.get())>= heightOfView && !firstVisibleText.toString().equalsIgnoreCase(((TextView) (getMiddleView(listView, 0).findViewById(id))).getText().toString())) {

                initialPosition.set(top);
                lowerHalf.set(true);
                firstVisibleText.delete(0,firstVisibleText.length());
                firstVisibleText.append(((TextView) (getMiddleView(listView, 0).findViewById(id))).getText().toString());
                setNewDayOfWeek(listView);
               // mFadeIncrement=1;
                //mFadeLowerIncrement=1;
                return 0;
            }


            else if (top == initialPosition.get())
                return (255);
           // return (int)(255.0/(top*2.3) * mInitialPosition);
            //return (int) ((255.0 /top*4 *( mInitialPosition)));

             //mFadeIncrement+=3;
             int x=(int) ((255.0*1*initialPosition.get() )/(Math.abs(top-initialPosition.get())+initialPosition.get()))-Math.abs(top-initialPosition.get());

        //Log.d("rahul",""+x+" "+mFadeIncrement+" "+Math.abs(top-initialPosition.get()));
             return x>=0?x:0;


    }


    private void setNewDayOfWeek(ListView listView){

        Log.d("rahul","point2");
        View monthView=getMiddleView(mMonthListview,mMiddlePositionFromTop);
        View yearView=getMiddleView(mYearListView,mMiddlePositionFromTop);


        int month=Integer.parseInt(((TextView)monthView.findViewById(R.id.row_number)).getText().toString());
        int year=Integer.parseInt(((TextView)yearView.findViewById(R.id.row_number)).getText().toString());
        int dateInDummyView=Integer.parseInt(((TextView) mDummyView.findViewById(R.id.row_number)).getText().toString());

        //Date date=new GregorianCalendar(year,month-1,mDateInDummyView).getTime();

        Date date=new GregorianCalendar(year,month-1,dateInDummyView).getTime();
        //Date date=new GregorianCalendar(year,month-1,mDateInDummyView).getTime();
        ((TextView) mDummyView.findViewById(R.id.row_text)).setText(new SimpleDateFormat("EEEE").format(date));



        //Toast.makeText(getActivity(),""+new SimpleDateFormat("EEEE").format(date),Toast.LENGTH_SHORT).show();
        //Toast.makeText(getActivity(),""+mMiddlePositionFromTop+ " "+month, Toast.LENGTH_SHORT).show();


    }


    public int findFadeFractionLower(View c,int top,int heightOfView,ListView listView,AtomicInteger firstVisiblePosition,AtomicBoolean lowerhalf,AtomicInteger initialPosition,int startingPositionForScroll,AtomicBoolean oneViewScrolled){

        if(Math.abs(top-initialPosition.get())>=heightOfView) {

                lowerhalf.set(false);
                initialPosition.set(startingPositionForScroll);
                firstVisiblePosition.set(listView.getFirstVisiblePosition());

                c.setTag(null);
                oneViewScrolled.set(false);
               // mFadeLowerIncrement=1;
                //mFadeIncrement=1;
                return 255;

        }
        else if(top<initialPosition.get() || c.getBottom()>initialPosition.get()+heightOfView){
            setNewDayOfWeek(listView);
        }

        else if(top==initialPosition.get())
            return 0;

       // return  (int)(((top*4.0)/mInitialPosition)*(255.0));
        //mFadeLowerIncrement+=3;
        int x= (int)((Math.abs(top-initialPosition.get())*1.0/initialPosition.get())*255.0)+Math.abs(top-initialPosition.get());
        return x<=255?x:255;

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
        mMonthOrYearTouched=false;

    }



    private void setDateParams(int year,int month){

        mDateAdapter.setNewDateParameters(daysOfTheMonth);
        mDateAdapter.setCurrentMonth(month - 1);
        mDateAdapter.setCurrentYear(year);
        mDateAdapter.notifyDataSetChanged();
    }

    protected void setNewDatesInDateAdapter(final int year, final int month) {


          setDateParams(year,month);

          mDateListView.post(new Runnable() {
              @Override
              public void run() {

                  View dateView=getMiddleView(mDateListView,mMiddlePositionFromTop);
                  int date=Integer.parseInt(((TextView)dateView.findViewById(R.id.row_number)).getText().toString());
                  int dateOnDummyView=Integer.parseInt(((TextView)mDummyView.findViewById(R.id.row_number)).getText().toString());
                //  Toast.makeText(getActivity(),""+date+" "+dateOnDummyView+" "+mMiddlePositionFromTop+" "+mDateListView.getFirstVisiblePosition(),Toast.LENGTH_SHORT).show();
                  if(dateOnDummyView>date)
                      mDelta=dateOnDummyView-date;
                  else
                      mDelta=-1*(date-dateOnDummyView);

                  mDateListView.post(new Runnable() {
                      @Override
                      public void run() {

                          int position=(mDelta + mMiddlePositionFromTop) + mDateListView.getFirstVisiblePosition();
                          //mDateListView.setSelectionFromTop(position, mRootLayoutHeight / 3);

                          mDateAdapter.setCurrentPos(position);
                          mDateAdapter.notifyDataSetChanged();
                          mDateListView.setSelectionFromTop(position, mRootLayoutHeight / 3);

//                          View view=mDateAdapter.getView(position,null,mDateListView);
//                          TextView v=(TextView)view.findViewById(R.id.row_number);
//                          Log.d("rah",""+v.getText().toString());
//                          view.setBackgroundColor(getResources().getColor(R.color.material_selected_row_color));
//                          setDateParams(year,month);
//                          mDateAdapter.notifyDataSetChanged();


                        //  Log.d("rahulpos",""+(position-mDateListView.getFirstVisiblePosition()));
//                          mDateListView.getChildAt(position-mDateListView.getFirstVisiblePosition()).setBackgroundColor(getResources().getColor(R.color.material_selected_row_color));
//                          mDateAdapter.notifyDataSetChanged();
//                          mDateAdapter.setCurrentPos(position);
//                          mDateAdapter.notifyDataSetChanged();



                      }
                  });

              }
          });


//        int daysInInitialMonth = getNumberOfDaysInMonth(mInitialMonth, year);
//        int daysInFinalMonth = getNumberOfDaysInMonth(mFinalMonth, year);
//        int offsetForAdapter = findOffestForAdapter(daysInInitialMonth, daysInFinalMonth);
//
//        //Log.d("rahu;l", "" + daysInInitialMonth + " " + daysInFinalMonth);
//        mDateAdapter.setCurrentPos(mDateAdapter.getCurrentPos() - offsetForAdapter);              // to keep the same date when month changes
//        mDateAdapter.setCurrentMonth(month - 1);
//        mDateAdapter.setCurrentYear(year);
//        mDateAdapter.notifyDataSetChanged();
//        mDateListView.setSelectionFromTop(mDateAdapter.getCurrentPos(), mRootLayoutHeight / 3);  //to keep the same date when month changes
//        mInitialMonth = mFinalMonth;


    }

    protected int getNumberOfDaysInMonth(int month, int year) {

        Calendar cal = new GregorianCalendar();
        cal.set(year, month - 1, 1);
        int numberOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return numberOfDays;


    }

    protected View getMiddleView(ListView listView, int position) {

        return listView.getChildAt(position);

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

                // f viewRect=new Rect();
                // v.getGlobalVisibleRect(viewRect);
                if (yValue >= v.getTop() - listView.getDividerHeight() && yValue <= v.getBottom()) {

                    if(i==mMiddlePositionFromTop)
                        return;
                    scrollToMiddle(listView, i, v, adapter);
                    break;
                }
            }
        }

    }


    void scrollToMiddle(final ListView listView, final int i, final View v, final MonthYearAdapter adapter) {

        listView.smoothScrollBy(v.getTop() - mRootLayoutHeight / 3, 1000);
        adapter.setCurrentPos(listView.getFirstVisiblePosition() + i);
        //adapter.highlightCurrentMonthColor(true);
        adapter.notifyDataSetChanged();



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


    private synchronized void putSomeRowInMiddle(ListView listView, MonthYearAdapter adapter) {


            for (int i = 0; i <= listView.getLastVisiblePosition() - listView.getFirstVisiblePosition(); i++) {
                final View v = listView.getChildAt(i);
                if (v != null) {

                    if (mMiddlePositionInScreen == 0) {
                        mMiddlePositionInScreen = mRootLayoutHeight / 3 + v.getHeight() / 2;
                        mBottomPositionOfMiddleElement = mRootLayoutHeight / 3 + v.getHeight();
                    }

                    //if(listView.getId()==R.id.year_listview)
                    //Log.d("A",""+i+" "+v.getTop()+ " "+mRootLayoutHeight/3+ " "+mMiddlePositionInScreen);
                    if ((v.getTop() >= mRootLayoutHeight / 3 - listView.getDividerHeight() / 2) && v.getTop() < mMiddlePositionInScreen) {
                        scrollUp(listView, v, v.getTop(), listView, adapter, listView.getFirstVisiblePosition() + i);
                        break;
                    }

                    //if(listView.getId()==R.id.year_listview)
                    //Log.d("B",""+i+" "+v.getBottom()+ " "+mMiddlePositionInScreen+ " "+mBottomPositionOfMiddleElement);
                    if ((v.getBottom() >= mMiddlePositionInScreen) && v.getBottom() <= mBottomPositionOfMiddleElement + listView.getDividerHeight() / 2) {

                        //if(listView.getId()==R.id.year_listview)
                        //   Log.d("rahulrajayes",""+i+" "+v.getBottom()+" "+mMiddlePositionInScreen+" "+mBottomPositionOfMiddleElement);
                        scrollDown(listView, v, v.getBottom(), v.getHeight(), listView, adapter, listView.getFirstVisiblePosition() + i);
                        break;
                    }

                    //if(listView.getId()==R.id.year_listview)
                    //Log.d("C",""+v.getBottom()+ " "+mMiddlePositionInScreen+ " "+mRootLayoutHeight/3+ " "+(v.getBottom()+listView.getDividerHeight()/2));
                    if (v.getBottom() <= mMiddlePositionInScreen && v.getBottom() > mRootLayoutHeight / 3) {

                        if (v.getBottom() + listView.getDividerHeight() / 2 >= mMiddlePositionInScreen) {
                            //  if(listView.getId()==R.id.year_listview)
                            // Log.d("rahulraja",""+i+" "+v.getBottom()+" "+mMiddlePositionInScreen+" "+mRootLayoutHeight/3);
                            scrollDown(listView, v, v.getBottom(), v.getHeight(), listView, adapter, listView.getFirstVisiblePosition() + i);
                            break;
                        }

                    }

                    //if(listView.getId()==R.id.year_listview)
                    //Log.d("D",""+i+" "+v.getTop()+ " "+mMiddlePositionInScreen+ " "+mBottomPositionOfMiddleElement+" "+(v.getTop()-listView.getDividerHeight()/2));

                    if (v.getTop() >= mMiddlePositionInScreen && v.getTop() < mBottomPositionOfMiddleElement) {

                        if (v.getTop() - listView.getDividerHeight() / 2 <= mMiddlePositionInScreen) {
                            scrollUp(listView, v, v.getTop(), listView, adapter, listView.getFirstVisiblePosition() + i);
                            break;
                        }

                    }


                }

            }

    }

    private void scrollDown(final ListView listview,final View view,final int viewBottom, final int viewHeight, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

//        if(mScrollDownTask!=null) {
//            listView.removeCallbacks(mScrollDownTask);
//        }
//        mScrollDownTask=new Runnable() {
//            @Override
//            public void run() {
//
//                listView.smoothScrollBy(viewBottom - (mRootLayoutHeight / 3 + viewHeight), 1000);
//                highLightMiddleRow(view,adapter, currentPosInMiddle,listView);
//            }
//        };
//
//        listView.post(mScrollDownTask);
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Log.d("rahulrajadown","gonedown"+v.getBottom());
                highLightMiddleRow(listview,view,adapter, currentPosInMiddle);
                listView.smoothScrollBy(viewBottom - (mRootLayoutHeight / 3 + viewHeight), 1000);
//                if(listview.getId()==R.id.month_listview)
//                    Log.d("rahuldown",""+viewBottom);

               // listView.removeCallbacks(this);

            }
        });

    }


    private void scrollUp(final ListView listview,final View view,final int viewTop, final ListView listView, final MonthYearAdapter adapter, final int currentPosInMiddle) {

//        if(mScrollUpTask!=null)
//          listView.removeCallbacks(mScrollUpTask);
//        mScrollUpTask=new Runnable() {
//            @Override
//            public void run() {
//
//
//
//
//            }
//        };
        //listView.post(mScrollUpTask);
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Log.d("rahulraja","goneup"+v.getTop());

                highLightMiddleRow(listView,view,adapter, currentPosInMiddle);
                listView.smoothScrollBy(viewTop - mRootLayoutHeight / 3, 1000);
//                if(listview.getId()==R.id.month_listview)
//                    Log.d("rahulup",""+viewTop);

              //  listView.removeCallbacks(this);

            }
        });

    }

    private void highLightMiddleRow(ListView listview,View view,MonthYearAdapter adapter, int currentPosInMiddle) {

//        if(listview.getId()==R.id.month_listview)
//            Log.d("midldle",""+currentPosInMiddle);
        adapter.setCurrentPos(currentPosInMiddle);
        //adapter.highlightCurrentMonthColor(true);
        adapter.notifyDataSetChanged();
        if(listview.getId()==R.id.date_listview)
            showDummyView(view);
//        if(listView.getId()==R.id.date_listview){
//            mDummyView.setVisibility(View.VISIBLE);
//        }


    }

    private void showDummyView(final View view){

        mStatusChecker = new Runnable() {
            @Override
            public void run() {

               // Log.d("rahul", "" + view.getTop() + " " + mRootLayoutHeight / 3);
                if(view.getTop()!=mRootLayoutHeight/3) {
                    mHandler.postDelayed(mStatusChecker, 200);
                }
                else{
                    mDummyView.setVisibility(View.VISIBLE);
                    mDummyTouched=true;
                   // mColorDrawable.setAlpha(255);
                    putDataOnDummyView();
                    mHandler.removeCallbacks(mStatusChecker);
                }
            }
        };
        mStatusChecker.run();

    }

    private void putDataOnDummyView(){

        Log.d("rahul","point3");
        View dateView=getMiddleView(mDateListView,mMiddlePositionFromTop);
        if(dateView!=null) {
            String date = ((TextView) dateView.findViewById(R.id.row_number)).getText().toString();
            String day = ((TextView) dateView.findViewById(R.id.row_text)).getText().toString();

            ((TextView)mDummyView.findViewById(R.id.row_number)).setText(date);
            ((TextView)mDummyView.findViewById(R.id.row_text)).setText(day);
        }



    }

    private void putDayOnDummyView(){

        View monthView=getMiddleView(mMonthListview,mMiddlePositionFromTop);
        View yearView=getMiddleView(mYearListView,mMiddlePositionFromTop);

        int month=Integer.parseInt(((TextView)monthView.findViewById(R.id.row_number)).getText().toString());
        int year=Integer.parseInt(((TextView)yearView.findViewById(R.id.row_number)).getText().toString());
        int date=Integer.parseInt(((TextView)mDummyView.findViewById(R.id.row_number)).getText().toString());

        Calendar cal=new GregorianCalendar(year,month-1,date);
        String dayOfWeek=new SimpleDateFormat("EEEE").format(cal.getTime());
        ((TextView)mDummyView.findViewById(R.id.row_text)).setText(dayOfWeek);

        Log.d("calcula",""+date+" "+month+" year"+ " "+dayOfWeek);
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
    public void onDetach(){

     super.onDetach();
    try {
        Field childFragmentManager = Fragment.class
                .getDeclaredField("mChildFragmentManager");
        childFragmentManager.setAccessible(true);
        childFragmentManager.set(this, null);

    } catch (NoSuchFieldException e) {
        throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
    }

 }
}
