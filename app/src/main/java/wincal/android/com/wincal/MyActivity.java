package wincal.android.com.wincal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
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


public class MyActivity extends ActionBarActivity {


    private int currentMonthPosition;
    private AtomicBoolean mListBeingTouched=new AtomicBoolean(false);
    private ListView month_listview;
    private int mRootLayoutHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new MyView(this));
        setContentView(R.layout.activity_my);

        final RelativeLayout rootLayout=(RelativeLayout)findViewById(R.id.root_layout);
        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mRootLayoutHeight=rootLayout.getHeight();
                month_listview.setSelectionFromTop(adapter.getCount()/2-2,height);

            }
        });



        final MonthAdapter adapter =new MonthAdapter(this, getResources().getStringArray(R.array.month_names));
        adapter.setCurrentMonthPos(adapter.getCount()/2-3);

       month_listview= (ListView) findViewById(R.id.month_listview);
        month_listview.setAdapter(adapter);

        ListView  date_listview= (ListView) findViewById(R.id.date_listview);
        date_listview.setAdapter(adapter);

        ListView  year_listview= (ListView) findViewById(R.id.year_listview);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
       int width= display.getWidth()/2;
        int height=display.getHeight()/2;



        //month_listview.setSelectionFromTop(adapter.getCount()/2-2,height);
        date_listview.setSelectionFromTop(adapter.getCount()/2-2,height);
        month_listview.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!adapter.getAllItemsVisible() && event.getAction() == MotionEvent.ACTION_DOWN){

                    mListBeingTouched.compareAndSet(false,true);
                    adapter.setAllItemsVisible(true);
                    adapter.highlightCurrentMonthColor(false);
                    adapter.notifyDataSetChanged();

                    return true;
                }

                if(event.getAction()==MotionEvent.ACTION_UP) {

                    mListBeingTouched.compareAndSet(true,false);

                }
                return false;
            }
        });

        month_listview.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !mListBeingTouched.get()) {

                   // int visibleChildCount = (month_listview.getLastVisiblePosition() - month_listview.getFirstVisiblePosition()) + 1;
                    putSomeRowInMiddle();
                    //Log.d("rahulraja",""+visibleChildCount);

                }

            }
        });



    }

    public class MyView extends View {
        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 100;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.parseColor("#CD5C5C"));
            canvas.drawRect(0,800,500,500,paint);
        }
    }

    private void putSomeRowInMiddle(){

        Display display = getWindowManager().getDefaultDisplay();
        int height=display.getHeight()/2;

        Canvas c=new Canvas();
        Paint myPaint = new Paint();
        myPaint.setColor(Color.rgb(0, 0, 0));
        myPaint.setStrokeWidth(10);
        c.drawRect(100, 100, 200, 200, myPaint);

        for(int i=0;i<month_listview.getChildCount();i++){

                 View v=month_listview.getChildAt(i);
            //Log.d("rahul", "" + v.getHeight());
            Rect rectf = new Rect();
            v.getGlobalVisibleRect(rectf);

            if(rectf.top<=height && rectf.top>=(height-rectf.height()))

                Log.d("hey",""+i);
//            Log.d("WIDTH        :", String.valueOf(rectf.width()));
//            Log.d("HEIGHT       :", String.valueOf(rectf.height()));
//            Log.d("left         :", String.valueOf(rectf.left));
//            Log.d("right        :", String.valueOf(rectf.right));
//            Log.d("top          :", String.valueOf(rectf.top));
//            Log.d("bottom       :", String.valueOf(rectf.bottom));

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
