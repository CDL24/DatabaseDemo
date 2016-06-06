package com.example.climbachiya.databasedemo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by C.limbachiya on 6/1/2016.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private List<Modal> items;
    Context mContext;

    public MyRecyclerAdapter(Context context, List<Modal> items) {
        this.items = items;
        this.mContext = context;
    }

    @Override
    public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_cell, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.ViewHolder holder, final int position) {
        Modal item = items.get(position);
        holder.textName.setText(item.getName());
        holder.textEmail.setText(item.getEmail());

        holder.imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRow(position);
            }
        });
        holder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                // Setting Dialog Title
                alertDialog.setTitle("Confirm Delete...");
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete this?");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_remove);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete item
                        removeItem(position);
                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textName;
        public TextView textEmail;
        public ImageButton imgBtnDelete;
        public ImageButton imgBtnEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.text_name);
            textEmail = (TextView) itemView.findViewById(R.id.text_email);
            imgBtnDelete = (ImageButton) itemView.findViewById(R.id.image_btn_delete);
            imgBtnEdit = (ImageButton) itemView.findViewById(R.id.image_btn_edit);
        }
    }

    /**
     * Edit record in database
     * @param position - row position in array list
     */
    private void editRow(int position) {
        String rowId = items.get(position).getId();
        Intent intentEdit = new Intent(mContext, MainActivity.class);
        intentEdit.putExtra("ROW_ID", rowId);
        mContext.startActivity(intentEdit);
        ((Activity)mContext).finish();
    }

    //Remove item
    public void removeItem(int position) {
        DatabaseHandler dbHandler = DatabaseHandler.getInstance(mContext);
        try {
            String rowId = items.get(position).getId();

            String whereClause = DatabaseHandler.KEY_ID+" = ?";
            String[] whereArgs = new String[]{rowId};
            int result = dbHandler.deleteRecord(DatabaseHandler.TABLE_CONTACTS, whereClause, whereArgs);

            if(result > 0){
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, items.size());
                items.remove(position);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != dbHandler){
                dbHandler.close();
            }
        }

    }
}