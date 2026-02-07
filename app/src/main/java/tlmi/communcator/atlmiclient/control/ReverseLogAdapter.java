package tlmi.communcator.atlmiclient.control;

import android.content.Context;

import java.util.List;

public class ReverseLogAdapter extends LogAdapter {

    public ReverseLogAdapter(Context context, int resource, List<Object> list) {
        super(context, resource, list);
        setList(list);
    }

    public List<Object> getList() {
        return list;
    }

    public void setList(List<Object> list) {
        this.list = list;
    }
    private List<Object> list;

    public void add(Object object){
        getList().add(0,object);
        notifyDataSetChanged();
    }

} // ListViewObjectArrayGenericAdapter