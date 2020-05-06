package adu.ac.ae.onakapp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adu.ac.ae.onakapp.R;
import adu.ac.ae.onakapp.model.Annountmentse;

public class AnnAdapter extends ArrayAdapter<Annountmentse> {
    Context mContext;
    int mResource;
    protected ExecutorService mExecutor;
    Drawable d;
    String url="";
    public AnnAdapter(@NonNull Context context,int resource,  @NonNull List<Annountmentse> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
        mExecutor = Executors.newSingleThreadExecutor();
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf=LayoutInflater.from(mContext);
        convertView=inf.inflate(mResource,parent,false);
        TextView sub=convertView.findViewById(R.id.subject);
        sub.setText(getItem(position).getAnnSubject());
        TextView msg=convertView.findViewById(R.id.msg);
        msg.setText(getItem(position).getAnnDesc());
        ImageView im=convertView.findViewById(R.id.img);
        im.setImageDrawable(getItem(position).getImg());
        getItem(position).loadImage(this);
        return convertView;
    }




}
