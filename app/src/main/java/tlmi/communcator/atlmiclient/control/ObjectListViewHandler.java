package tlmi.communcator.atlmiclient.control;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;

import tlmi.communcator.atlmiclient.R;

public class ObjectListViewHandler {

    public ObjectListViewHandler(Context context, ListView listview){
        setAdapter(new ListViewObjectArrayAdapter(context, R.layout.partner_list_item, getList()));
        setListview(listview);
        listview.setAdapter(adapter);
    }

    public ArrayList<Object> getList() {
        return list;
    }

    public void setList(ArrayList<Object> list) {
        this.list = list;
    }

    private ArrayList<Object> list = new ArrayList<Object>();

    public ListViewObjectArrayAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ListViewObjectArrayAdapter adapter) {
        this.adapter = adapter;
    }

    private ListViewObjectArrayAdapter adapter;

    public ListView getListview() {
        return listview;
    }

    public void setListview(ListView listview) {
        this.listview = listview;
    }

    private ListView listview;

    public void addNewItem(Object item){
        list.add(item);
        adapter.notifyDataSetChanged();

    }

}