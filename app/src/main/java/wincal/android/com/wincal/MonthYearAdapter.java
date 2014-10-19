package wincal.android.com.wincal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.IllegalFormatConversionException;
import java.util.IllegalFormatException;
import java.util.Locale;
import java.text.DateFormat;

/**
 * Created by Rahul Raja on 9/25/2014.
 */
public class MonthYearAdapter extends BaseAdapter{

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;
    private int currentMonthPos=0;
    private boolean alIItemsVisible=false;
    private boolean highlightCurrentMonth=true;
    private int dataLength=0;
    private boolean isForDateView=false;

    private int mCurrentMonth;
    private int mCurrentYear;


    public MonthYearAdapter(Context context, String[] data,int length,boolean forDateView) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        this.isForDateView=forDateView;
        if(data!=null)
            this.dataLength=data.length;
        else
            this.dataLength=length;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public boolean getHighlightCurrentMonth(){

        return this.highlightCurrentMonth;
    }

    protected void setAllItemsVisible(boolean choice){

        this.alIItemsVisible=choice;
    }

    public boolean getAllItemsVisible(){

        return this.alIItemsVisible;
    }

    protected void  setCurrentMonthPos(int pos){

        currentMonthPos=pos;

    }

    protected void highlightCurrentMonthColor(boolean choice){

        highlightCurrentMonth=false;
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
            vi = inflater.inflate(R.layout.calendar_row, null);
        else {
            if(vi.getVisibility()==View.INVISIBLE)
                vi.setVisibility(View.VISIBLE);
            vi.setBackgroundResource(R.drawable.list_border);
        }


//        if(!this.alIItemsVisible)
//            vi.setVisibility(View.INVISIBLE);

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
        }



        if(position==currentMonthPos && highlightCurrentMonth ){

          vi.setVisibility(View.VISIBLE);
           vi.setBackgroundColor(context.getResources().getColor(R.color.selected_row_color));
        }

        if (isForDateView){

            Calendar cal=new GregorianCalendar(mCurrentYear,mCurrentMonth,Integer.valueOf(mainText.getText().toString()));
            Date date=cal.getTime();
            DateFormat format2=new SimpleDateFormat("EEEE");
            String dayOfTheWeek=format2.format(date);
            subText.setText(dayOfTheWeek);


        }


        return vi;
    }
}


