package tlmi.communcator.atlmiclient.indicator;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

public class VisualIndicator extends VisualIndicatorAlgebra {


    public VisualIndicator(Context context, View indicator){
        setContext(context);
        bindIndicator(indicator);
    }
/*
    protected static int DEFAULT = R.color.indicator_color_default;
    protected static int LIVE = R.color.indicator_color_live;
    protected static int ERROR = R.color.indicator_color_error;
*/
    protected static int DEFAULT = Color.parseColor("#D8D3D5");
    protected static int LIVE = Color.parseColor("#2E2D2D");
    protected static int ERROR = Color.parseColor("#CC4977");


    public void setColor(int color){
        getIndicator().setTextColor(color);
    }


    public void setState(int state){
        getIndicator().setTextColor(state);
    }

    public int getState(){
        return getIndicator().getCurrentTextColor();
    }

    public void reset(){
        setColor(DEFAULT);
    }

    public void error(){
        setColor(ERROR);

    }

    public void live(){
        setColor(LIVE);
    }

}
