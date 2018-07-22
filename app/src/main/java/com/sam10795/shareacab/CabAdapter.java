package com.sam10795.shareacab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sam10795.shareacab.R;

import java.util.ArrayList;

/**
 * Created by SAM10795 on 26-07-2015.
 */
public class CabAdapter extends ArrayAdapter {

    Context mcontext;
    ArrayList<CabDet> mcabDet;

    public CabAdapter(Context context,ArrayList<CabDet> cabDet)
    {
        super(context, R.layout.list_item_request,cabDet);
        mcontext = context;
        mcabDet = cabDet;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(position%2==0)
            {
                convertView = inflater.inflate(R.layout.list_item_request_0,parent,false);
            }
            else
            {
                convertView = inflater.inflate(R.layout.list_item_request, parent, false);
            }
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.nam);
            holder.cabservice = (TextView)convertView.findViewById(R.id.cab);
            holder.pjoin = (TextView)convertView.findViewById(R.id.ppl);
            holder.date = (TextView)convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        CabDet cabDet = (CabDet)getItem(position);
        String dt = cabDet.getDate().toString();
        holder.name.setText(cabDet.getName());
        holder.date.setText(dt.substring(0,dt.indexOf("GMT")-1));
        holder.pjoin.setText(Integer.toString(cabDet.getPjoin()));
        if(cabDet.isBooked())
        {
            holder.cabservice.setText(cabDet.getCabserv());
        }
        else
        {
            holder.cabservice.setText("Cab not Booked");
        }

        return convertView;

    }

    static class ViewHolder
    {
        TextView name;
        TextView date;
        TextView cabservice;
        TextView pjoin;
    }
}
