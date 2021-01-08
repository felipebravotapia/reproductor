package com.felipebravo.reproductor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class AndroidSongAdapterMain extends ArrayAdapter<listSong> {

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private static final String LOG_TAG = AndroidSongAdapterMain.class.getSimpleName();
    public AndroidSongAdapterMain(Activity context, ArrayList<listSong> androidSong) {
        super(context, 0, androidSong);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_main, parent, false);
        }

        listSong currentAndroidSong = getItem(position);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.version_name);
        nameTextView.setText(currentAndroidSong.getTrackName());
        TextView numberTextView = (TextView) listItemView.findViewById(R.id.version_number);
        numberTextView.setText(currentAndroidSong.getArtistName());

        ImageView iconView = (ImageView) listItemView.findViewById(R.id.list_item_icon);
        new DownloadImageTask().execute(new Object[] {iconView, currentAndroidSong.getImage()});


/*
        try {
        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(currentAndroidSong.getImage()).getContent());
        iconView.setImageBitmap(bitmap);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/

        return listItemView;
    }


    private class DownloadImageTask extends AsyncTask<Object, Integer, Bitmap> {
        private ImageView iv;
        protected Bitmap doInBackground(Object... params) {
            try {
                iv = (ImageView) params[0];
                URL aURL = new URL(params[1] +"");
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                Bitmap bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                return bm;
            } catch (IOException e) {
                return null;
            }
        }

        protected void onPostExecute(Bitmap result) {
            iv.setImageBitmap((Bitmap) result);
        }
    }

}










