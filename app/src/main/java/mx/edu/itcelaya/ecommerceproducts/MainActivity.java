package mx.edu.itcelaya.ecommerceproducts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
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

public class MainActivity extends AppCompatActivity {

    ListView listProducts;
    List<Products> items = new ArrayList<Products>();
    String consumer_key = "ck_1e92f3593393b4b67a9c36b4cc3fa39cec0494fa";
    String consumer_secret = "cs_9acec12116917aaa12187e38cde674e3f1b62057";

    //String url = "https://192.168.1.68/store_itc/wc-api/v3/products";
    String url = "https://172.20.118.67/store_itc/wc-api/v3/products";
    String jsonResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listProducts = (ListView) findViewById(R.id.listProductos);
        NukeSSLCerts.nuke();

        loadProducts();
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
