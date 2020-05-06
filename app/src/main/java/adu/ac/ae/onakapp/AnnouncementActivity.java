package adu.ac.ae.onakapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import adu.ac.ae.onakapp.adapters.AnnAdapter;
import adu.ac.ae.onakapp.common.OnakActivity;
import adu.ac.ae.onakapp.model.Annountmentse;
import adu.ac.ae.onakapp.model.RestClient;
import adu.ac.ae.onakapp.services.AnnouncementService;

public class AnnouncementActivity extends OnakActivity {

    List<Annountmentse> annArray=new ArrayList<>();
    private final AtomicReference<List<Annountmentse>> info = new AtomicReference<>();
    AnnAdapter m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_announcement);

        AnnAdapter m=new AnnAdapter(this,R.layout.sann_row,annArray);

        ListView l=findViewById(R.id.lv);
        l.setAdapter(m);
       /* try {
            performGet("https://onakpublicapi.herokuapp.com/api/public/anns","",mUserInfoJson,this::afterSuccess);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/
       mExecutor.execute(this::getAnnouncement);

    }
    @WorkerThread
    public void getAnnouncement(){
        try {
            AnnouncementService obj = RestClient.getClient().create(AnnouncementService.class);
            info.set(obj.listRepos().execute().body().getEmbedded().getAnnountmentses());
            runOnUiThread(this::updateLV);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @MainThread
    public void updateLV(){
        annArray.clear();
        annArray.addAll(info.get());
        info.get().clear();
        ListView l=findViewById(R.id.lv);
        l.setVisibility(View.VISIBLE);
        RelativeLayout sk=findViewById(R.id.sk);
        sk.setVisibility(View.GONE);
        AnnAdapter m=(AnnAdapter)l.getAdapter();
        m.notifyDataSetChanged();
    }

}
