package wincal.android.com.wincal;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import java.util.concurrent.atomic.AtomicBoolean;


public class MyActivity extends ActionBarActivity {


    private int currentMonthPosition;
    private AtomicBoolean mListBeingTouched=new AtomicBoolean(false);
    private ListView month_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        final MonthAdapter adapter =new MonthAdapter(this, getResources().getStringArray(R.array.month_names));
        adapter.setCurrentMonthPos(adapter.getCount()/2-3);

       month_listview= (ListView) findViewById(R.id.month_listview);
        month_listview.setAdapter(adapter);

        ListView  date_listview= (ListView) findViewById(R.id.date_listview);
        date_listview.setAdapter(adapter);

        ListView  year_listview= (ListView) findViewById(R.id.year_listview);
        //year_listview.setAdapter(new MonthAdapter(this, new String[] { "data1",
                //"data2","data3","data4","data5","data6","data7","data8"  }));

        //date_listview.setSelection(adapter.getCount()/2-2);
       //month_listview.setSelection(adapter.getCount()/2);
      //  year_listview.setSelection(adapter.getCount()/2+1);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
       int width= display.getWidth()/2;
        int height=display.getHeight()/2;
        //int width = size.x/2;
        //int height = size.y/2;

        //Toast.makeText(this,""+size.x+" "+size.y,Toast.LENGTH_LONG).show();

        month_listview.setSelectionFromTop(adapter.getCount()/2-2,height);
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

                    int visibleChildCount = (month_listview.getLastVisiblePosition() - month_listview.getFirstVisiblePosition()) + 1;
                    putSomeRowInMiddle(month_listview.getFirstVisiblePosition(),month_listview.getLastVisiblePosition());
                    //Log.d("rahulraja",""+visibleChildCount);

                }

            }
        });

    }

    private void putSomeRowInMiddle(int firstVisiblePosition,int lastVisiblePosition){

        //Log.d("hey",""+firstVisiblePosition+" "+lastVisiblePosition);

        for(int i=firstVisiblePosition;i<=lastVisiblePosition;i++){

                 View v=month_listview.getAdapter().getView(i,null,null);
                 Log.d("rahulraja", "" + v.getId());

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
