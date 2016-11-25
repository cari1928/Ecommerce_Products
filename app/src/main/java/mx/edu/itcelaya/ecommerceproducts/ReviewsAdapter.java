package mx.edu.itcelaya.ecommerceproducts;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Radogan on 2016-11-24.
 */

public class ReviewsAdapter extends BaseAdapter {
    private Context context;
    private List<Reviews> reviews;
    ImageView img1;

    public ReviewsAdapter(Context context, List<Reviews> reviews) {
        super();
        this.context = context;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return this.reviews.size();
    }

    @Override
    public Object getItem(int i) {
        return this.reviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View rowView = view;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.list_reviews, null);
        }

        TextView tvUsuario   = (TextView) rowView.findViewById(R.id.tvUsuario);
        TextView tvCorreo  = (TextView) rowView.findViewById(R.id.tvCorreo);
        TextView tvReview  = (TextView) rowView.findViewById(R.id.tvReview);

        final Reviews item = this.reviews.get(position);
        tvUsuario.setText(item.getCreated_at());
        tvCorreo.setText(item.getReviewer_email());
        tvReview.setText(item.getReview());

        return rowView;
    }

    private InputStream OpenHttpConnection(String urlString)
            throws IOException
    {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");

            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex) {
            throw new IOException("Error connecting" + response + ex.getMessage());
        }
        return in;
    }

    private Bitmap DownloadImage(String URL)     {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            //Toast.makeText(context, e1.getMessage(), Toast.LENGTH_LONG).show();
            e1.printStackTrace();
        }
        return bitmap;
    }

    private class BackgroundTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... url) {
            //---download an image---
            Bitmap bitmap = DownloadImage(url[0]);
            return bitmap;
        }
    }
}

