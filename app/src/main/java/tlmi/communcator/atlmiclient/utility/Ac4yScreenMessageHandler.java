package tlmi.communcator.atlmiclient.utility;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

public class Ac4yScreenMessageHandler extends Ac4yScreenMessageHandlerAlgebra {

    public Ac4yScreenMessageHandler(Context context){
        setContext(context);
    }

    public void message(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }


    public void errorNotifying(String message){

        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.RED);
        toast.show();

        /*
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("This is a custom toast");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
        */

    } // errorNotifying

}
