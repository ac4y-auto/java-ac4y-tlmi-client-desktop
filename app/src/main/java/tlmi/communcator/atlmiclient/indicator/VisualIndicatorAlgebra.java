package tlmi.communcator.atlmiclient.indicator;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class VisualIndicatorAlgebra implements IVisualIndicator {


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;



    public TextView getIndicator() {
        return indicator;
    }

    public void setIndicator(TextView indicator) {
        this.indicator = indicator;
    }

    public void bindIndicator(View view){
        setIndicator((TextView) view);
    }

    private TextView indicator;



    
    public void setValue(int resource){

    }

    public void populateLegend(ArrayAdapter adapter){

    }

    public void reset(){

    }

}