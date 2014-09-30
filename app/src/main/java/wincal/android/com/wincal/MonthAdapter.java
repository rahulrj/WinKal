package wincal.android.com.wincal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Rahul Raja on 9/25/2014.
 */
public class MonthAdapter extends BaseAdapter{

    Context context;
    String[] data;
    private static LayoutInflater inflater = null;
    private int currentMonthPos=0;
    private boolean alIItemsVisible=false;
    private boolean highlightCurrentMonth=true;

    public MonthAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub

        int actualPosition = position % data.length;
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

        if(!this.alIItemsVisible)
            vi.setVisibility(View.INVISIBLE);

        //Log.d("rahulraja",""+this.alIItemsVisible);

       TextView subText = (TextView) vi.findViewById(R.id.sub_text);
       TextView mainText=(TextView)vi.findViewById(R.id.main_text);

        int actualPosition = position % data.length;
        subText.setText(data[actualPosition]);
        mainText.setText(String.valueOf(actualPosition+1));

        if(position==currentMonthPos && highlightCurrentMonth ){

           vi.setVisibility(View.VISIBLE);
            vi.setBackgroundColor(context.getResources().getColor(R.color.selected_row_color));
        }

        return vi;
    }
}


