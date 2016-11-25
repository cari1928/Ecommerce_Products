package mx.edu.itcelaya.ecommerceproducts;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ListView listProducts;
    List<Products> items = new ArrayList<Products>();
    List<Reviews> rItems = new ArrayList<Reviews>();
    List aReviews = new ArrayList<>();
    String consumer_key = "ck_1e92f3593393b4b67a9c36b4cc3fa39cec0494fa";
    String consumer_secret = "cs_9acec12116917aaa12187e38cde674e3f1b62057";
    String id;
    AlertDialog dialogFoto;
    Button btnRegresa;

    String url = "https://192.168.1.68/store_itc/wc-api/v3/products";
    //String url = "https://10.247.67.17/store_itc/wc-api/v3/products";
    String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listProducts = (ListView) findViewById(R.id.listProductos);
        listProducts.setOnItemClickListener(listenerProduct); //crear un listener que responda a cada elemento del list view
        NukeSSLCerts.nuke();

        loadProducts();
    }

    //se instancía la variable, es la que se asocia a listproducts
    AdapterView.OnItemClickListener listenerProduct = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Products p = items.get(i); //posición
            Toast.makeText(getBaseContext(), "Producto " + p.getTitle() + " ID: " + p.getId(), Toast.LENGTH_LONG).show();
            loadReviews(p.getId());
        }
    };

    private void loadReviews(int id) {
        url += "/" + id + "/reviews";
        LoadProductsTask tarea = new LoadProductsTask(this, consumer_key, consumer_secret);
        try {
            jsonResult = tarea.execute(new String[] { url }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getBaseContext(), jsonResult, Toast.LENGTH_LONG).show();

        ListReviews();
    }

    public void ListReviews(){
        try {
            //lbl1.setText(jsonResult);
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("product_reviews");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                Integer id = jsonChildNode.optInt("id");
                String created_at = jsonChildNode.optString("created_at");
                String review = jsonChildNode.optString("review");
                String reviewer_name = jsonChildNode.optString("reviewer_name");
                String reviewer_email = jsonChildNode.optString("reviewer_email");

                //aReviews.add(review);
                rItems.add(new Reviews(id, created_at, review, reviewer_name, reviewer_email));
            }

                //para mostrar los reviews
                AlertDialog.Builder builder = new AlertDialog.Builder(this); //recibe el contexto de la app
                LinearLayout layout1 = new LinearLayout(this); //para colocar en él los elementos
                layout1.setOrientation(LinearLayout.VERTICAL);

                //nuevo listview en conjunto con un arrayadapter
                ListView vReviews = new ListView(this);
                vReviews.setAdapter(new ReviewsAdapter(this, rItems));

                //boton
                btnRegresa = new Button(this);
                btnRegresa.setText("Cerrar");
                btnRegresa.setOnClickListener(this);

                //se pasan los elementos al layout
                layout1.addView(vReviews);
                layout1.addView(btnRegresa);

                builder.setView(layout1); //se le pasa el layout a builder
                dialogFoto = builder.create(); //se termina de crear el dialogo
                dialogFoto.show(); //se muestra el dialogo

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }
        //listProducts.setAdapter(new ProductsAdapter(this, items));
    }

    private void loadProducts() {
        LoadProductsTask tarea = new LoadProductsTask(this, consumer_key, consumer_secret);
        try {
            jsonResult = tarea.execute(new String[] { url }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Toast.makeText(getBaseContext(), jsonResult, Toast.LENGTH_LONG).show();
        ListProductos();
    }

    public void ListProductos() {

        try {
            //lbl1.setText(jsonResult);
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainNode = jsonResponse.optJSONArray("products");

            for (int i = 0; i < jsonMainNode.length(); i++) {
                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                String name = jsonChildNode.optString("title");
                String type = jsonChildNode.optString("type");
                Integer id_product = jsonChildNode.optInt("id");
                Double price = jsonChildNode.optDouble("price");
                String ImageURL = jsonChildNode.optString("featured_src");

                items.add(new Products(id_product, name, type, price, ImageURL));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_LONG).show();

        }

        listProducts.setAdapter(new ProductsAdapter(this, items));
    }

    @Override
    public void onClick(View view) {
        if(view == btnRegresa) {
            dialogFoto.dismiss();
        }
    }

    public static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                                return myTrustedAnchors;
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        }
                };

                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

}
