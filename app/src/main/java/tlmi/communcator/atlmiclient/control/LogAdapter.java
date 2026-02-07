package tlmi.communcator.atlmiclient.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tlmi.communcator.atlmiclient.R;
import tlmi.communcator.atlmiclient.model.LogEvent;

public class LogAdapter extends ArrayAdapter<Object> {

    public LogAdapter(Context context, int resource, List<Object> list) {
        super(context, resource, list);
    }

    public LogAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        LogEvent item = (LogEvent) getItem(position);

        View rowView = inflater.inflate(R.layout.log_row, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.logItem);
        textView.setText(item.getMessage());

        return rowView;

    } // getView

} // LogAdapter