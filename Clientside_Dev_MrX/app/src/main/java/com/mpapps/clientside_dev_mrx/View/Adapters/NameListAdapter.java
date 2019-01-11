package com.mpapps.clientside_dev_mrx.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.R;

import java.util.ArrayList;
import java.util.List;

public class NameListAdapter extends RecyclerView.Adapter<NameListAdapter.ViewHolder>
{
    private Context context;
    private GameModel gameModel;

    public NameListAdapter(Context context, GameModel gameModel)
    {
        this.context = context;
        this.gameModel = gameModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.recyclerview_item_namelist, viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        List<String> keys = new ArrayList<>(gameModel.getPlayers().keySet());
        List<Boolean> values = new ArrayList<>(gameModel.getPlayers().values());

        if(values.get(i)){
            viewHolder.name.setBackground(context.getDrawable(R.drawable.circular_name_textview_yellow_2));
        }else{
            viewHolder.name.setBackground(context.getDrawable(R.drawable.circular_name_textview_2));
        }

        String name = keys.get(i);
        String initials = "";
        if (name.contains(" ")) {
            String[] names = name.split("\\s+");
            initials += names[0].substring(0,1) + names[1].substring(0,1);
        }else{
            initials = name.substring(0, 2);
        }

        viewHolder.name.setText(initials.toUpperCase());

    }

    @Override
    public int getItemCount()
    {
        return gameModel.getPlayers().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.recyclerview_item_namelist_textview);
        }
    }

    class MapActivityViewHolder extends RecyclerView.ViewHolder
    {
        TextView initials, name;
        public MapActivityViewHolder(@NonNull View itemView)
        {
            super(itemView);
            initials = itemView.findViewById(R.id.map_activity_recyclerview_item_initials);
            name = itemView.findViewById(R.id.map_activity_recyclerview_item_name);
        }
    }
}
