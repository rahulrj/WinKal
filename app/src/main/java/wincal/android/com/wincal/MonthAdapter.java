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

    public MonthAdapter(Context context, String[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.calendar_row, null);

       TextView subText = (TextView) vi.findViewById(R.id.sub_text);
       TextView mainText=(TextView)vi.findViewById(R.id.main_text);

        int actualPosition = position % data.length;
        subText.setText(data[actualPosition]);
        mainText.setText(String.valueOf(actualPosition+1));

        return vi;
    }
}


