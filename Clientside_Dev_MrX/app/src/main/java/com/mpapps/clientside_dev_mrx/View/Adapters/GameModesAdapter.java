package com.mpapps.clientside_dev_mrx.View.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.Models.GameLevel;
import com.mpapps.clientside_dev_mrx.R;

import java.util.List;

public class GameModesAdapter extends RecyclerView.Adapter<GameModesAdapter.ViewHolder>
{

    private List<GameLevel> gameModes;
    private Context context;
    private int lastSelectedPos = -1;

    public GameModesAdapter(Context context, List<GameLevel> gameLevels)
    {
        this.context = context;
        gameModes = gameLevels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.gamemode_recyclerview_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i)
    {
        GameLevel level = gameModes.get(i);
        String title = "", info = "";
        switch (level.getGameMode()){
            case Easy:
                title = context.getResources().getString(R.string.gamemode_easy_title);
                info = context.getResources().getString(R.string.gamemode_easy_description);
                break;
            case Normal:
                title = context.getResources().getString(R.string.gamemode_normal_title);
                info = context.getResources().getString(R.string.gamemode_normal_description);
                break;
            case Hard:
                title = context.getResources().getString(R.string.gamemode_hard_title);
                info = context.getResources().getString(R.string.gamemode_hard_description);
                break;
            case MisterX:
                title = context.getResources().getString(R.string.gamemode_misterx_title);
                info = context.getResources().getString(R.string.gamemode_misterx_description);
                break;
        }
        viewHolder.gameModeTitle.setText(title);
        viewHolder.gameModeInfo.setText(info);

        viewHolder.selectionState.setChecked(lastSelectedPos == i);
    }

    @Override
    public int getItemCount()
    {
        return gameModes.size();
    }

    public int getLastSelectedPos()
    {
        return lastSelectedPos;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView gameModeTitle;
        public TextView gameModeInfo;
        public RadioButton selectionState;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            gameModeTitle = itemView.findViewById(R.id.gamemode_item_title);
            gameModeInfo = itemView.findViewById(R.id.gamemode_item_info);
            selectionState = itemView.findViewById(R.id.gamemode_item_radiobutton);

            selectionState.setOnClickListener((view -> {
                lastSelectedPos = getAdapterPosition();
                notifyDataSetChanged();
            }));

            itemView.setOnClickListener(view ->{
                lastSelectedPos = getAdapterPosition();
                notifyDataSetChanged();
            });
        }
    }
}
