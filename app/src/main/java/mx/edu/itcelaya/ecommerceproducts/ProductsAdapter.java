package mx.edu.itcelaya.ecommerceproducts;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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
 * Created by niluxer on 5/16/16.
 */
public class ProductsAdapter extends BaseAdapter {
    private Context context;
    private List<Products> productos;
    ImageView img1;

    public ProductsAdapter(Context context, List<Products> productos) {
        super();
        this.context = context;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return this.productos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.productos.get(position);
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
            rowView = inflater.inflate(R.layout.list_products, null);
        }

        TextView tvName   = (TextView) rowView.findViewById(R.id.tvTitle);
        TextView tvPrice  = (TextView) rowView.findViewById(R.id.tvPrice);
        img1 = (ImageView) rowView.findViewById(R.id.imgIdProduct);

        final Products item = this.productos.get(position);
        tvName.setText(item.getTitle());
        tvPrice.setText(item.getPrice()+ "");
        rowView.setTag(item.getId());
        String sUrl = item.getImageUrl();
        //String sUrl = "http://gravatar.com/avatar/1c57c8eea18ec3bbf43b81432e61132f";

        try {
            final Bitmap bitmap = new BackgroundTask().execute(sUrl).get();
            img1.setImageBitmap(bitmap);

            //---------------------------------------------------------------------------------------------------------
            //código agregado
            final String url = "https://172.20.118.67/store_itc/wc-api/v3/products/" + item.getId() + "/reviews";
            final String consumer_key = "ck_1e92f3593393b4b67a9c36b4cc3fa39cec0494fa";
            final String consumer_secret = "cs_9acec12116917aaa12187e38cde674e3f1b62057";
            //---------------------------------------------------------------------------------------------------------

            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //arreglo para guardar los elementos obtenidos de json
                    List<Reviews> items = new ArrayList<Reviews>();

                    //elementos para obtener el json
                    LoadProductsTask tarea = new LoadProductsTask(null, consumer_key, consumer_secret);
                    String jsonResult;
                    try {
                        jsonResult = tarea.execute(new String[] { url }).get();
                        JSONObject jsonResponse = new JSONObject(jsonResult);
                        JSONArray jsonMainNode = jsonResponse.optJSONArray("product_reviews");

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            Integer id = jsonChildNode.optInt("id");
                            String created_at = jsonChildNode.optString("created_at");
                            String review = jsonChildNode.optString("review");
                            String reviewer_name = jsonChildNode.optString("reviewer_name");
                            String reviewer_email = jsonChildNode.optString("reviewer_email");

                            items.add(new Reviews(id, created_at, review, reviewer_name, reviewer_email));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //Esto es del código original
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    TextView tvNombre = new TextView(context);
                    tvNombre.setText(item.getTitle());

                    LinearLayout layout1 = new LinearLayout(context);
                    layout1.setOrientation(LinearLayout.VERTICAL);

                    layout1.addView(tvNombre);

                    //--------------------------------------------------------------------------------------
                    //código agregado
                    Reviews[] aReviews = new Reviews[items.size()];

                    for(int i = 0; i < items.size(); i++) {
                        aReviews[i] = items.get(i);
                        TextView review = new TextView(context);
                        review.setText("Creado: " + aReviews[i].getCreated_at() + "\nUsuario: " + aReviews[i].getReviewer_name()
                            + "\nEmail: " + aReviews[i].getReviewer_email() + "\n" + aReviews[i].getReview() + "\n");
                        layout1.addView(review);
                    }
                    //-----------------------------------------------------------------------------------------

                    //comentados para que se vean los reviews
                    ImageView ivFoto = new ImageView(context);
                    ivFoto.setImageBitmap(bitmap);
                    layout1.addView(ivFoto);
                    builder.setView(ivFoto);

                    builder.setView(layout1);
                    AlertDialog dialogFoto = builder.create();
                    dialogFoto.show();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
