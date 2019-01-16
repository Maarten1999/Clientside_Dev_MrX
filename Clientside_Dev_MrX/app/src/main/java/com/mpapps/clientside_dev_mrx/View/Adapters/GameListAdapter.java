package com.mpapps.clientside_dev_mrx.View.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mpapps.clientside_dev_mrx.Models.GameMode;
import com.mpapps.clientside_dev_mrx.Models.GameModel;
import com.mpapps.clientside_dev_mrx.R;
import com.mpapps.clientside_dev_mrx.Services.CurrentGameInstance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context context;
    private Map<String, Map<GameModel, Boolean>> gameModels;
    private onItemClicklistener listener;

    public GameListAdapter(Context ctx, onItemClicklistener listener)
    {
        gameModels = new HashMap<>();
        gameModels.put("current", new HashMap<>());
        gameModels.put("history", new HashMap<>());
        this.listener = listener;
        this.context = ctx;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        switch (viewType){
            case 0:
                return new HeaderViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.start_activity_recyclerview_header_item, viewGroup, false));
            case 1:
                return new CurrentGameViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.start_activity_recyclerview_current_game_item, viewGroup, false));
            case 2:
                return new HistoryGameViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.start_activity_recyclerview_history_game_item, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i)
    {
        HashMap<GameModel, Boolean> temp = new LinkedHashMap<>();
        temp.putAll(gameModels.get("current"));
        temp.putAll(gameModels.get("history"));
        GameModel model = null;
        if(getItemCount() != 1)
        model = (new ArrayList<>(temp.keySet())).get(i);

        switch (viewHolder.getItemViewType()){
            case 0: {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
                if(getItemCount() == 1)
                    holder.title.setText(R.string.no_games);
                else if (i == 0 && gameModels.get("current").size() == 0)
                    holder.title.setText(R.string.history);
                else if (i == 0)
                    holder.title.setText(R.string.games);
                else
                    holder.title.setText(R.string.history);
                break;
            }
            case 1: {
                CurrentGameViewHolder holder = (CurrentGameViewHolder) viewHolder;
                holder.title.setText(model.getName());
                holder.mode.setText(getStringFromGameMode(model.getMode()));
                DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
                holder.date.setText(dateFormat.format(model.getDate()));
                if(holder.nameList.getAdapter() == null){
                    NameListAdapter adapter = new NameListAdapter(context, model);
                    holder.nameList.setAdapter(adapter);
                }
                holder.nameList.getAdapter().notifyDataSetChanged();
                break;
            }
            case 2: {
                HistoryGameViewHolder holder = (HistoryGameViewHolder) viewHolder;
                holder.title.setText(model.getName());
                holder.mode.setText(getStringFromGameMode(model.getMode()));
                DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
                holder.date.setText(dateFormat.format(model.getDate()));
                if(holder.nameList.getAdapter() == null){
                    NameListAdapter adapter = new NameListAdapter(context, model);
                    holder.nameList.setAdapter(adapter);
                }
                holder.nameList.getAdapter().notifyDataSetChanged();
                if(model.isWon())
                    holder.imageView.setImageResource(R.drawable.recyclerview_item_imageview_gradient_green);
                else
                    holder.imageView.setImageResource(R.drawable.recyclerview_item_imageview_gradient_red);
                break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        int currentModelsSize = gameModels.get("current").size();
        int historyModelsSize = gameModels.get("history").size();
        if(currentModelsSize + historyModelsSize == 0)
            return 1;
        return currentModelsSize + historyModelsSize;
    }

    @Override
    public int getItemViewType(int position)
    {
        int currentModelsSize = gameModels.get("current").size();
        int historyModelsSize = gameModels.get("history").size();

        HashMap<GameModel, Boolean> temp = new LinkedHashMap<>();
        temp.putAll(gameModels.get("current"));
        temp.putAll(gameModels.get("history"));

        if(getItemCount() == 1){
            temp.put(null, true);
        }
        Boolean model = (new ArrayList<>(temp.values())).get(position);
        if(model)
            return 0;
        else{
            if(position >= currentModelsSize)
                return 2;
            else
                return 1;
        }
    }

    public List<GameModel> getCurrentGames()
    {
        return new ArrayList<>(gameModels.get("current").keySet());
    }

    public void setCurrentGames(List<GameModel> currentGames)
    {
        if(currentGames.size() == 0)
            gameModels.put("current", new LinkedHashMap<>());
        else{
            HashMap<GameModel, Boolean> tempModels = new LinkedHashMap<>();
            tempModels.put(new GameModel("HeaderCurrent", GameMode.Easy, null, null, false), true);
            for (GameModel currentGame : currentGames) {
                tempModels.put(currentGame, false);
            }
            gameModels.put("current", tempModels);
        }
    }

    public List<GameModel> getHistoryGames()
    {
        return new ArrayList<>(gameModels.get("history").keySet());
    }

    public void setHistoryGames(List<GameModel> historyGames)
    {
        if(historyGames.size() == 0)
            gameModels.put("history", new LinkedHashMap<>());
        else{
            HashMap<GameModel, Boolean> tempModels = new LinkedHashMap<>();
            tempModels.put(new GameModel("HeaderHistory", GameMode.Easy, null, null, false), true);
            for (GameModel historyGame : historyGames) {
                tempModels.put(historyGame, false);
            }
            gameModels.put("history", tempModels);
        }
    }

    private String getStringFromGameMode(GameMode mode)
    {
        switch (mode){
            case Easy:
                return context.getString(R.string.gamemode_easy_title) + " - 15 min";
            case Normal:
                return context.getString(R.string.gamemode_normal_title) + " - 1 "
                        + context.getString(R.string.hour);
            case Hard:
                return context.getString(R.string.gamemode_normal_title) + " - 2 "
                        + context.getString(R.string.hours);
            case MisterX:
                return context.getString(R.string.gamemode_normal_title) + " - 4 "
                    + context.getString(R.string.hours);
        }
        return "";
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        public HeaderViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.startactivity_recyclerview_header_text);
        }
    }

    public class CurrentGameViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title, mode, date;
        private RecyclerView nameList;
        public CurrentGameViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.recyclerview_item_current_title);
            mode = itemView.findViewById(R.id.recyclerview_item_current_mode);
            date = itemView.findViewById(R.id.recyclerview_item_current_date);
            nameList = itemView.findViewById(R.id.recyclerview_item_current_recyclerview);
            nameList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            itemView.setOnClickListener(view -> {
                GameModel model = (new ArrayList<>(gameModels.get("current").keySet())).get(getAdapterPosition());
                SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.sharedPreferences), Context.MODE_PRIVATE);
                String gameCode = sharedPref.getString("GameCode", "");
                String misterXCode = sharedPref.getString("MisterXCode", "");
                CurrentGameInstance.initialize(model, gameCode, misterXCode);
                if(listener != null)
                    listener.onItemClick();
            });
        }
    }

    public class HistoryGameViewHolder extends RecyclerView.ViewHolder
    {
        private TextView title, mode, date;
        private RecyclerView nameList;
        private ImageView imageView;
        public HistoryGameViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.recyclerview_item_history_title);
            mode = itemView.findViewById(R.id.recyclerview_item_history_mode);
            date = itemView.findViewById(R.id.recyclerview_item_history_date);
            nameList = itemView.findViewById(R.id.recyclerview_item_history_recyclerview);
            nameList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            imageView = itemView.findViewById(R.id.recyclerview_item_history_imageview);
        }
    }

    public interface onItemClicklistener{
        void onItemClick();
    }
}
