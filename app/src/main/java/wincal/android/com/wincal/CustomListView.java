package wincal.android.com.wincal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by Rahul Raja on 10/25/2014.
 */
public class CustomListView extends ListView {

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomListView(Context context) {
        super(context);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_UP:
                Log.d("rahulraja", "upme");
                break;

            case MotionEvent.ACTION_DOWN:
                Log.d("rahulraja", "downme");
                return true;

            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return super.onInterceptTouchEvent(event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.d("rahulraja","downmeplus");
               break;
            case MotionEvent.ACTION_UP:
                Log.d("rahulraja","upmeplus");
                break;

        }
        return super.onTouchEvent(event);

    }



}
