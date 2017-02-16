package siesgst.tml17.idscan;

import android.content.Context;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PlayerViewHolder> {

    List<Player> player;
    private SparseBooleanArray selectedItems;
    List<Player> selected=new ArrayList<Player>();
    ActionMode action;


    // UpdateSQLite sql;
    private LayoutInflater layoutInflater;
    Context ctx;

    public List<Player> getselectedList(){
        return selected;
    }

    public RecyclerViewAdapter(Context context, List<Player> p){
//        persons=sql.getAllContacts();
        //      layoutInflater=LayoutInflater.from(context);
        player=p;
        selectedItems = new SparseBooleanArray();

        ctx=context;
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public static class PlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        //   CardView cv;
        TextView playerName;
        TextView UniqueID;
        TextView PlayTime;
        TextView ContactNumber;
        CheckBox check;
        ItemClickListener itemClickListener;
        //  ImageView personPhoto;
        View view;

        PlayerViewHolder(View itemView) {
            super(itemView);
            // cv = (CardView) itemView.findViewById(R.id.cv);
            playerName = (TextView)itemView.findViewById(R.id.player_name);
            UniqueID=(TextView)itemView.findViewById(R.id.playerID);
            // PlayTime = (TextView)itemView.findViewById(R.id.play_time);
            ContactNumber=(TextView)itemView.findViewById(R.id.ContactNumber);
            check=(CheckBox)itemView.findViewById(R.id.checkBox);
            itemView.setLongClickable(true);
            check.setOnClickListener(this);
            //   personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);

        }


        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v,getLayoutPosition());
        }

        public void setItemClickListener(ItemClickListener i){
            this.itemClickListener=i;
        }






    }


    @Override
    public int getItemCount() {
        if(player==null)
            return 0;
        else {
            return (int) player.size();
        }
    }

    @Override
    public PlayerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.player_design, viewGroup, false);
        PlayerViewHolder pvh = new PlayerViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PlayerViewHolder playerViewHolder, int i) {
        playerViewHolder.playerName.setText(player.get(i).name);

        playerViewHolder.UniqueID.setText(player.get(i).UID);

        //  playerViewHolder.PlayTime.setText(player.get(i).Timestamp);

        playerViewHolder.ContactNumber.setText(player.get(i).Number);

        //personViewHolder.personPhoto.setImageResource(R.drawable.updates);
        playerViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox ch=(CheckBox)v;
                if(ch.isChecked()){
                    selected.add(player.get(pos));

               }
                else{
                    selected.remove(player.get(pos));
                }
            }
        });



    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}