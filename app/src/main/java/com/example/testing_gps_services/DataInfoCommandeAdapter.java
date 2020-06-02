package com.example.testing_gps_services;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import testing.gps_service.R;

public class DataInfoCommandeAdapter extends RecyclerView.Adapter<DataInfoCommandeAdapter.DataInfoCommandeViewHolder> {

    private Context mCtx;

    //we are storing all the products in a list
    private List<DataInfoCommande>commandesList;

    //getting the context and product list with constructor
    public DataInfoCommandeAdapter(Context mCtx, List<DataInfoCommande> commandesList) {
        this.mCtx = mCtx;
        this.commandesList = commandesList;
    }

    @Override
    public DataInfoCommandeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.layout_commandes, parent,false);
        return new DataInfoCommandeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataInfoCommandeViewHolder holder, int position) {
        //getting the product of the specified position
        DataInfoCommande commande = commandesList.get(position);

        //binding the data with the viewholder views
        holder.textViewId.setText(String.valueOf(commande.getIdCommande()));
        holder.textViewClientName.setText(String.valueOf(commande.getNom()+" "+commande.getPrenom()));
        holder.textViewTelephone.setText(String.valueOf(commande.getTelephone()));
        holder.textViewAdresse.setText(String.valueOf(commande.getAdresse()));



    }


    @Override
    public int getItemCount() {
        return commandesList.size();
    }


    class DataInfoCommandeViewHolder extends RecyclerView.ViewHolder {
        CardView mcarview;
        TextView textViewId,textViewClientName,textViewAdresse,textViewTelephone;


        public DataInfoCommandeViewHolder(View itemView) {
            super(itemView);
            mcarview = itemView.findViewById(R.id.card_view);
            textViewId = itemView.findViewById(R.id.textViewId);
            textViewClientName = itemView.findViewById(R.id.textViewClientName);
            textViewTelephone = itemView.findViewById(R.id.textViewTelephone);
            textViewAdresse = itemView.findViewById(R.id.textViewAdresse);

        }
    }
}
