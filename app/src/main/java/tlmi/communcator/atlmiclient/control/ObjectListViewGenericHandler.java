package tlmi.communcator.atlmiclient.control;

import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ObjectListViewGenericHandler {

    public ObjectListViewGenericHandler(ArrayAdapter adapter, ListView listview){

        setAdapter(adapter);
        setListview(listview);
        listview.setAdapter(adapter);
    }

    public ArrayAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ArrayAdapter adapter) {
        this.adapter = adapter;
    }

    private ArrayAdapter adapter;

    public ListView getListview() {
        return listview;
    }

    public void setListview(ListView listview) {
        this.listview = listview;
    }

    private ListView listview;

    public void add(Object item){
        getAdapter().add(item);
        getAdapter().notifyDataSetChanged();
    }

} // ObjectListViewGenericHandler