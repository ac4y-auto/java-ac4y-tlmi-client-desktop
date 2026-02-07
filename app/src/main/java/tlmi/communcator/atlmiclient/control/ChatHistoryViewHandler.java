package tlmi.communcator.atlmiclient.control;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;

import tlmi.communcator.atlmiclient.AppEnvironmentVariableHandler;
import tlmi.communcator.atlmiclient.R;

public class ChatHistoryViewHandler {

    public ChatHistoryViewHandler(Context context, ListView listview, AppEnvironmentVariableHandler appEnvironmentVariableHandler){
        setAdapter(new ChatHistoryAdapter(context, R.layout.partner_list_item, getList(),appEnvironmentVariableHandler));
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

    public ChatHistoryAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(ChatHistoryAdapter adapter) {
        this.adapter = adapter;
    }

    private ChatHistoryAdapter adapter;

    public ListView getListview() {
        return listview;
    }

    public void setListview(ListView listview) {
        this.listview = listview;
    }

    private ListView listview;

    public void addNewItem(Object item){
        list.add(0,item);
        adapter.notifyDataSetChanged();

    }

}