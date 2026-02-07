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

import tlmi.communcator.atlmiclient.R;
import tlmi.user.domain.TlmiTranslateUser;

public class ListViewObjectArrayAdapter extends ArrayAdapter<Object> {

    public ListViewObjectArrayAdapter(Context context, int textViewResourceId, List<Object> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TlmiTranslateUser item = (TlmiTranslateUser) getItem(position);
/*
        View rowView;

        if (item.isIncoming())
            rowView = inflater.inflate(R.layout.partner_list_item, parent, false);
        else
            rowView = inflater.inflate(R.layout.listview_with_image_left, parent, false);
*/
        View rowView = inflater.inflate(R.layout.partner_list_item, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.Itemname);
        //textView.setText(item.getHumanName());
        textView.setText(item.getHumanName());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        imageView.setImageBitmap(getBitmapFromString(item.getAvatar()));
        //imageView.setImageResource(R.drawable..ic_launcher);


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
