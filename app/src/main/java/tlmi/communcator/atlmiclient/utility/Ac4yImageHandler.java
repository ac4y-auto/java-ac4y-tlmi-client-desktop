package tlmi.communcator.atlmiclient.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayInputStream;

public class Ac4yImageHandler {

    public Bitmap getBitmapFromString(String completeImageData) {

        return BitmapFactory.decodeStream(
                new ByteArrayInputStream(
                        Base64.decode(completeImageData.substring(
                                completeImageData.indexOf(",")+1
                        ).getBytes(), Base64.DEFAULT)
                )
        );

    } // getBitmapFromString

}