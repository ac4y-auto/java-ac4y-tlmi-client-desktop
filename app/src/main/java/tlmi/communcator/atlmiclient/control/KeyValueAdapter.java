package tlmi.communcator.atlmiclient.control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import tlmi.communcator.atlmiclient.R;
import tlmi.communcator.atlmiclient.model.KeyValue;

public class KeyValueAdapter extends ArrayAdapter<Object> {

    public KeyValueAdapter(Context context, int resource, List<Object> list) {
        super(context, resource, list);
    }

    public KeyValueAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        KeyValue item = (KeyValue) getItem(position);

        View rowView = inflater.inflate(R.layout.key_value_row, parent, false);

        TextView key = (TextView) rowView.findViewById(R.id.key);
        key.setText(item.getKey());

        TextView value = (TextView) rowView.findViewById(R.id.value);
        value.setText(item.getValue());

        return rowView;

    } // getView

} // KeyValueAdapter