package com.sam10795.shareacab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by SAM10795 on 13-08-2015.
 */
public class JoinAdapter extends ArrayAdapter {
    Context mContext;
    ArrayList<Person> mper;
    boolean madmin;
    public JoinAdapter(Context context,ArrayList<Person> per, boolean admin)
    {
        super(context,R.layout.list_item_person,per);
        mContext = context;
        mper = per;
        madmin = admin;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(position%2==0)
            {
                convertView = inflater.inflate(R.layout.list_item_person,parent,false);
            }
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.rem = (ImageView)convertView.findViewById(R.id.delname);
            holder.contact = (ImageView)convertView.findViewById(R.id.contact);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        Person person = (Person)getItem(position);
        String name = person.getName();
        holder.name.setText(name);
        if(madmin)
        {
            holder.rem.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.rem.setVisibility(View.GONE);
        }

        return convertView;
    }

    static class ViewHolder
    {
        TextView name;
        ImageView rem;
        ImageView contact;
    }
}
