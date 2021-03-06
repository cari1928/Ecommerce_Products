package mx.edu.itcelaya.ecommerceproducts;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Radogan on 2016-11-23.
 */

public class LoadReviewsTask {
    String jsonResult;
    String consumer_key, consumer_secret;
    private Context contexto;

    public LoadReviewsTask(String consumer_key, String consumer_secret, Context contexto) {
        this.consumer_key = consumer_key;
        this.consumer_secret = consumer_secret;
        this.contexto = contexto;
    }

    protected String doInBackground(String... params) {

        try {
            jsonResult = inputStreamToString(OpenHttpConnection(params[0])).toString();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return jsonResult;
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
            //httpConn.setAllowUserInteraction(false);
            //httpConn.setInstanceFollowRedirects(true);

            httpConn.setRequestMethod("GET");
            String credentials = consumer_key + ":" + consumer_secret;
            String base64EncodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            httpConn.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);

            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        }
        catch (Exception ex) {
            throw new IOException("Error connecting..." + urlString + " :" + ex.getMessage());
        }
        return in;
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        try {
            while ((rLine = rd.readLine()) != null) {
                answer.append(rLine);
            }
        }

        catch (IOException e) {
            // e.printStackTrace();
            Toast.makeText(contexto,
                    "Error..." + e.toString(), Toast.LENGTH_LONG).show();
        }
        return answer;
    }

}
