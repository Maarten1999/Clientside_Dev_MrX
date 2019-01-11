package com.mpapps.clientside_dev_mrx.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private Map<String,Boolean> players;
    private String lastSelectedName = "";

    public ParticipantsAdapter(Context context)
    {
        this.context = context;
        this.players = new LinkedHashMap<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        switch (viewType){
            case 0:
                return new HeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.start_activity_recyclerview_header_item, viewGroup, false));
            default:
                return new ViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.detailed_activity_recyclerview_item, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i)
    {
        switch (viewHolder.getItemViewType()){
            case 0: {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                holder.title.setText(context.getString(R.string.participants));
                break;
            }
            default: {
                ViewHolder holder = (ViewHolder) viewHolder;
                String name = new ArrayList<>(players.keySet()).get(i - 1);
                String initials = "";
                if (name.contains(" ")) {
                    String[] names = name.split("\\s+");
                    initials += names[0].substring(0,1) + names[1].substring(0,1);
                }else{
                    initials = name.substring(0, 2);
                }
                holder.name.setText(name);
                holder.initials.setText(initials);
                holder.misterX.setChecked(new ArrayList<>(players.values()).get(i - 1));
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return players.size() + 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return 0;
        else
            return 1;
    }

    public void setPlayers(Map<String, Boolean> players)
    {
        this.players = players;

        for (int i = 0; i < players.size(); i++) {
            if(new ArrayList<>(players.values()).get(i))
                lastSelectedName = new ArrayList<>(players.keySet()).get(i);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView initials;
        TextView name;
        RadioButton misterX;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            initials = itemView.findViewById(R.id.detailed_activity_recyclerview_item_initials);
            name = itemView.findViewById(R.id.detailed_activity_recyclerview_item_name);
            misterX = itemView.findViewById(R.id.detailed_activity_recyclerview_item_radiobutton);

            misterX.setOnClickListener((view -> {
                lastSelectedName = new ArrayList<>(players.keySet()).get(getAdapterPosition() - 1);
                for (int i = 0; i < players.size(); i++) {
                    if (new ArrayList<>(players.keySet()).get(i).equals(lastSelectedName))
                        players.put(new ArrayList<>(players.keySet()).get(i), true);
                     else {
                        players.put(new ArrayList<>(players.keySet()).get(i), false);
                    }
                }
                //TODO send via firebase
                notifyDataSetChanged();
            }));
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        public HeaderViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.startactivity_recyclerview_header_text);
        }
    }
}
