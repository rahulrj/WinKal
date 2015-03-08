package wincal.android.com.wincal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Rahul Raja on 9/25/2014.
 */
public class MonthYearAdapter extends BaseAdapter{

    private static LayoutInflater inflater = null;
    Context context;
    String[] data;
    private int currentPos=0;
    private boolean alIItemsVisible=false;
    private boolean highlightCurrentMonth=true;
    private int dataLength=0;
    private boolean isForDateView=false;

    private int mCurrentMonth;
    private int mCurrentYear;
    long mAnimationOffset=0;

    private int mSelectedRowBackground;
    private int mListRowTextColor;
    private int  mSelectedRowTextColor;
    private int mListRowBackground;

    // Used for animating the listview
    public   static int mMiddlePositionFromTop;
    private ListView mListView;
    private DatePickerFragment.ScrollState mScrollState;
    private AtomicBoolean mListViewBeingTouched=new AtomicBoolean(false);


    public MonthYearAdapter(Context context, String[] data,int length,boolean forDateView,ListView listView) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.isForDateView=forDateView;
        this.mListView=listView;
        this.mMiddlePositionFromTop=0;
        if(data!=null)
            this.dataLength=data.length;
        else
            this.dataLength=length;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    protected void  setNewDateParameters(String[] data){

         this.data=data;
         this.dataLength=data.length;

    }

    protected void setColorValues(int listRowTextColor,int selectedRowTextColor,int selectedRowBackground,int listRowBackground){

        mSelectedRowTextColor=selectedRowTextColor;
        mListRowTextColor=listRowTextColor;
        mSelectedRowBackground=selectedRowBackground;
        mListRowBackground=listRowBackground;
    }

    protected void setScrollState(DatePickerFragment.ScrollState scrollState){

        this.mScrollState=scrollState;
    }

    public void setTouchedParam(AtomicBoolean listViewTouched){

        this.mListViewBeingTouched=listViewTouched;
    }

    public boolean getHighlightCurrentMonth(){

        return this.highlightCurrentMonth;
    }

    public boolean getAllItemsVisible(){

        return this.alIItemsVisible;
    }

    protected void setAllItemsVisible(boolean choice){

        this.alIItemsVisible=choice;
        mAnimationOffset=0;
    }

    protected void  setCurrentPos(int pos){

        currentPos=pos;

    }

    protected int getCurrentPos(){

        return this.currentPos;
    }

    protected void highlightCurrentMonthColor(boolean choice){

        this.highlightCurrentMonth=choice;
    }

    protected void setCurrentMonth(int currentMonth){

        this.mCurrentMonth=currentMonth;
    }

    protected void setCurrentYear(int currentYear){

        this.mCurrentYear=currentYear;
    }

    @Override
    public int getCount() {

        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {

        int actualPosition=position%dataLength;
        return data[actualPosition];


    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.calendar_row, parent,false);
        else {
            if(vi.getVisibility()==View.INVISIBLE)
                vi.setVisibility(View.VISIBLE);
            vi.setBackgroundResource(mListRowBackground);
        }

     //   vi.setTag(""+position);

        if(!this.alIItemsVisible)
            vi.setVisibility(View.INVISIBLE);

        TextView subText = (TextView) vi.findViewById(R.id.row_text);
        TextView mainText=(TextView)vi.findViewById(R.id.row_number);

        int actualPosition= position % dataLength;

        if(data!=null){

            subText.setText(data[actualPosition]);

            try {
                mainText.setText(String.format(Locale.ENGLISH, "%02d", (actualPosition + 1)));
            }catch(IllegalFormatConversionException e){

                mainText.setText(String.valueOf(actualPosition+1));
            }
            catch(IllegalFormatException e){

                mainText.setText(String.valueOf(actualPosition+1));
            }
        }

        else{

            mainText.setText(String.valueOf(Constants.STARTING_YEAR+actualPosition));
            subText.setText("");
        }

        if(position==currentPos ){

            vi.setVisibility(View.VISIBLE);
            vi.setBackgroundResource(mSelectedRowBackground);
            ((TextView) vi.findViewById(R.id.row_text)).setTextColor(context.getResources().getColor(mSelectedRowTextColor));
            ((TextView) vi.findViewById(R.id.row_number)).setTextColor(context.getResources().getColor(mSelectedRowTextColor));
           // vi.setBackgroundColor(context.getResources().getColor(R.color.material_selected_row_color));


        }
        else{


          //  ((TextView) vi.findViewById(R.id.row_text)).setTextColor(context.getResources().getColor(R.color.material_text_color));
          //  ((TextView) vi.findViewById(R.id.row_number)).setTextColor(context.getResources().getColor(R.color.material_text_color));
              ((TextView) vi.findViewById(R.id.row_text)).setTextColor(context.getResources().getColor(mListRowTextColor));
              ((TextView) vi.findViewById(R.id.row_number)).setTextColor(context.getResources().getColor(mListRowTextColor));
        }

        if (isForDateView){

            Calendar cal=new GregorianCalendar(mCurrentYear,mCurrentMonth,Integer.valueOf(mainText.getText().toString()));
            Date date=cal.getTime();
            DateFormat format2=new SimpleDateFormat("EEEE");
            String dayOfTheWeek=format2.format(date);
            subText.setText(dayOfTheWeek);


        }

       if (getAllItemsVisible() && mScrollState!=null && mScrollState.getScrollState()== AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mListViewBeingTouched.get()) {


           if(mMiddlePositionFromTop!=0 && position!=mMiddlePositionFromTop+mListView.getFirstVisiblePosition()) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                for (int i = 1; i < 10; i++) {

                    if (position == mListView.getFirstVisiblePosition() + mMiddlePositionFromTop - i) {
                        mAnimationOffset = (20 * i);
                        animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                        break;
                    } else if (position == mListView.getFirstVisiblePosition() + mMiddlePositionFromTop + i) {
                        mAnimationOffset = (20 * i);
                        animation = AnimationUtils.loadAnimation(context, R.anim.fade_in_lower);
                        break;
                    }
                }

              // Log.d("rahul","come");
                if (position != mMiddlePositionFromTop + mListView.getFirstVisiblePosition()) {
                    animation.setDuration(100);
                    animation.setStartOffset(mAnimationOffset);
                    vi.startAnimation(animation);
                }
            }

        }

        return vi;
    }
}


