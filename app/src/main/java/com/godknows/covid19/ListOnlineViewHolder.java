package com.godknows.covid19;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListOnlineViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView txtEmail;
    itemClickListener itemClickListener1;

    public void setItemClickListener(itemClickListener itemClickListener1) {
        this.itemClickListener1 = itemClickListener1;
    }

    public ListOnlineViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener1.onClick(v,getAdapterPosition());
            }
        });
        itemView.setOnClickListener(this);
        txtEmail=(TextView) itemView.findViewById(R.id.txt_email);
    }

    @Override
    public void onClick(View v) {
        itemClickListener1.onClick(v,getAdapterPosition());
    }
}
