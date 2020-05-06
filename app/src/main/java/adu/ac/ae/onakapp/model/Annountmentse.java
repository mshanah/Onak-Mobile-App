
package adu.ac.ae.onakapp.model;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

import adu.ac.ae.onakapp.adapters.AnnAdapter;

public class Annountmentse {


    private String annDesc;

    private String annSubject;

    private String annLogo;

    private Object annPage;

    public Drawable getImg() {
        return img;
    }
public void loadImage(AnnAdapter sta){
        this.sta=sta;
    if (annLogo != null && !annLogo.equals("") && img ==null) {
        new ImageLoadTask().execute(this.annLogo);
    }
}
    public void setImg(Drawable img) {
        this.img = img;
    }

    Drawable img;


    public String getAnnDesc() {
        return annDesc;
    }

    public void setAnnDesc(String annDesc) {
        this.annDesc = annDesc;
    }

    public String getAnnSubject() {
        return annSubject;
    }

    public void setAnnSubject(String annSubject) {
        this.annSubject = annSubject;
    }

    public String getAnnLogo() {
        return annLogo;
    }

    public void setAnnLogo(String annLogo) {

        if(annLogo != null && !"".equals(annLogo)){
            this.setImg(LoadImageFromWebOperations(annLogo));
        }
        this.annLogo = annLogo;
    }

    public Object getAnnPage() {
        return annPage;
    }
private  AnnAdapter sta;
    public void setAnnPage(Object annPage) {
        this.annPage = annPage;
    }
    public void setAdapter(AnnAdapter sta) {
        this.sta = sta;
    }
    // ASYNC TASK TO AVOID CHOKING UP UI THREAD
    private class ImageLoadTask extends AsyncTask<String, String, Drawable> {

        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }

        // PARAM[0] IS IMG URL
        protected Drawable doInBackground(String... param) {
            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0]);
            try {
                Drawable b = LoadImageFromWebOperations(param[0]);
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onProgressUpdate(String... progress) {
            // NO OP
        }

        protected void onPostExecute(Drawable ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded "  );
                img = ret;
                if (sta != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                    sta.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load "  );
            }
        }
    }
    private Drawable LoadImageFromWebOperations(String url ) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            System.out.println("Get Image");
            System.out.println(d);
            return d;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
