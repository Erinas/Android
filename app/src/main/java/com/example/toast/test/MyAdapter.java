package com.example.toast.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Toast on 2017/1/28.
 */
public class MyAdapter extends BaseAdapter {
    private List<Data> datas;
    private LayoutInflater myInflater;
    public MyAdapter(Context context, List<Data> datas){
        myInflater = LayoutInflater.from(context);
        this.datas = datas;
    }
    @Override
    public int getCount(){
        return datas.size();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = myInflater.inflate(R.layout.listview, null);
            holder = new ViewHolder((TextView)convertView.findViewById(R.id.title), (TextView)convertView.findViewById(R.id.dec));
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.txttitle.setText(datas.get(position).getTitle());
        holder.txtdec.setText(datas.get(position).getDec());
        return convertView;
    }
    @Override
    public Object getItem(int position){
        return datas.get(position);
    }
    @Override
    public long getItemId(int position){
        return datas.indexOf(getItem(position));
    }
}
class ViewHolder{
    TextView txttitle;
    TextView txtdec;
    public ViewHolder(TextView txttitle, TextView txtdec){
        this.txttitle = txttitle;
        this.txtdec = txtdec;
    }
}
