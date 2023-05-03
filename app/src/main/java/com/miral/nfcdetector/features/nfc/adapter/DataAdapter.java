package com.miral.nfcdetector.features.nfc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miral.nfcdetector.databinding.ResponseListLayoutBinding;
import com.miral.nfcdetector.manager.DatabaseManager;
import com.miral.nfcdetector.features.nfc.model.NfcData;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataViewHolder> {

    private Context context;
    private ArrayList<NfcData> listData;

    private DatabaseManager mDatabaseManager;

    public DataAdapter(Context context, ArrayList<NfcData> listData) {
        this.context = context;
        this.listData = listData;
        this.mDatabaseManager = new DatabaseManager().getInstance();
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        return new DataViewHolder(ResponseListLayoutBinding.inflate(LayoutInflater.from(context),
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        final String uid, command, result;

        uid = listData.get(position).getUid();
        command = listData.get(position).getCommand();
        result = listData.get(position).getResult();

        if (uid.length()>0 || !uid.isEmpty()) {
            holder.binding.txtChip.setVisibility(View.VISIBLE);
            holder.binding.txtChip.setText("> Chip Detected: " + uid);
        } else {
            holder.binding.txtChip.setVisibility(View.GONE);
        }

        if (command.length()>0 || !command.isEmpty()) {
            holder.binding.txtCommand.setVisibility(View.VISIBLE);
            holder.binding.txtCommand.setText("> Command: " + command);
        } else {
            holder.binding.txtCommand.setVisibility(View.GONE);
        }

        if (result.length()>0 || !result.isEmpty()) {
            holder.binding.txtResponse.setVisibility(View.VISIBLE);
            holder.binding.txtResponse.setText("> Result: " + result);
        } else {
            holder.binding.txtResponse.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{

        public ResponseListLayoutBinding binding;

        public DataViewHolder(ResponseListLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

}
