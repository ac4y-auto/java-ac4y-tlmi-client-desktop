package tlmi.communcator.atlmiclient.control;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.util.List;

import tlmi.communcator.atlmiclient.AppEnvironmentVariableHandler;
import tlmi.communcator.atlmiclient.R;
import tlmi.communcator.atlmiclient.model.ChatEvent;

public class ChatHistoryAdapter extends ArrayAdapter<Object> {

    public ChatHistoryAdapter(Context context, int textViewResourceId, List<Object> objects, AppEnvironmentVariableHandler appEnvironmentVariableHandler) {
        super(context, textViewResourceId, objects);
        setAppEnvironmentVariableHandler(appEnvironmentVariableHandler);
    }

    private AppEnvironmentVariableHandler appEnvironmentVariableHandler;

    private AppEnvironmentVariableHandler getAppEnvironmentVariableHandler() {
        return appEnvironmentVariableHandler;
    }

    private void setAppEnvironmentVariableHandler(AppEnvironmentVariableHandler appEnvironmentVariableHandler) {
        this.appEnvironmentVariableHandler = appEnvironmentVariableHandler;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ChatEvent item = (ChatEvent) getItem(position);

        View rowView;

        if (item.isIncoming())
            rowView = inflater.inflate(R.layout.incoming_chat_row, parent, false);
        else
            rowView = inflater.inflate(R.layout.outgoing_chat_row, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.message);
        textView.setText(item.getMessage());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.avatar);

        if (item.isIncoming()) {

            if (getAppEnvironmentVariableHandler().getPartnerAvatar().get()!=null)
                imageView.setImageBitmap(getBitmapFromString(getAppEnvironmentVariableHandler().getPartnerAvatar().get()));

        }
        else {

            if (getAppEnvironmentVariableHandler().getUserAvatar().get()!=null)
                imageView.setImageBitmap(getBitmapFromString(getAppEnvironmentVariableHandler().getUserAvatar().get()));

        }

        return rowView;

    } // getView

    private Bitmap getBitmapFromString(String completeImageData) {

        return BitmapFactory.decodeStream(
                    new ByteArrayInputStream(
                            Base64.decode(completeImageData.substring(
                                completeImageData.indexOf(",")+1
                            ).getBytes(), Base64.DEFAULT)
                        )
                );

    } // getBitmapFromString

}
