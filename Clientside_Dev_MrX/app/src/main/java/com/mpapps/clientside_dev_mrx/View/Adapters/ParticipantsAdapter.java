package com.mpapps.clientside_dev_mrx.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.Models.Player;
import com.mpapps.clientside_dev_mrx.R;

import java.util.ArrayList;
import java.util.List;

public class ParticipantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private List<Player> players;
    private int lastSelectedPos = -1;

    public ParticipantsAdapter(Context context)
    {
        this.context = context;
        this.players = new ArrayList<>();
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
                Player player = players.get(i - 1);
                holder.name.setText(player.getName());
                holder.initials.setText(player.getInitials());
                holder.misterX.setChecked(player.isMisterX());
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

    public void setPlayers(List<Player> players)
    {
        this.players = players;
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).isMisterX())
                lastSelectedPos = i;
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
                lastSelectedPos = getAdapterPosition() - 1;
                for (int i = 0; i < players.size(); i++) {
                    Player player = players.get(i);
                    if(i == lastSelectedPos)
                        player.setMisterX(true);
                    else
                        player.setMisterX(false);
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
