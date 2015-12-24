package com.dailyselfie.view.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dailyselfie.R;
import com.dailyselfie.model.content.SelfieResolver;
import com.dailyselfie.model.mediator.SelfieDataMediator;
import com.dailyselfie.model.mediator.webdata.SelfieRecord;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SelfieRecordAdapter extends SelectableAdapter<SelfieRecordAdapter.SelfieRecordViewHolder> implements ItemTouchHelperAdapter{

    private static final String LOG_TAG = SelfieRecordAdapter.class.getSimpleName();
    private List<SelfieRecord> mRecordList = new ArrayList<SelfieRecord>();
    private LayoutInflater inflater;
    private Context mContext;

    private SelfieRecordViewHolder.ClickListener clickListener;


    public SelfieRecordAdapter(Context context, SelfieRecordViewHolder.ClickListener clickListener) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        this.clickListener = clickListener;
    }


    @Override
    public SelfieRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.d(LOG_TAG, "onCreateViewHolder");
        View view = inflater.inflate(R.layout.content_dashboard, parent, false);

        return new SelfieRecordViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(SelfieRecordViewHolder holder, final int position) {
        final SelfieRecord currentRecord = mRecordList.get(position);
        Log.d(LOG_TAG, String.valueOf(mContext.getApplicationContext()));
        Log.d(LOG_TAG, "onBindViewHolder");
        Log.d(LOG_TAG, "name : " + currentRecord.getName() + " Path : " + currentRecord.getPath());

        try {
            if (new File(currentRecord.getPath()).exists()) {
                Picasso
                        .with(mContext)
                        .load(new File(currentRecord.getPath()))
                        .fit() // will explain later
                        .into(holder.thumbnail);
            }else{
                Picasso
                        .with(mContext)
                        .load(R.drawable.ic_placeholder)
                        .fit() // will explain later
                        .into(holder.thumbnail);
            }

        }catch (Exception e){
            Picasso
                    .with(mContext)
                    .load(R.drawable.ic_placeholder)
                    .fit() // will explain later
                    .into(holder.thumbnail);
        }


        holder.selfieDate.setText(currentRecord.getName());
        holder.selfieDateCreated.setText("Date Created : " + currentRecord.getDate_created());
        holder.selfieDateModified.setText("Date Modified : " + currentRecord.getDate_modified());

        holder.selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

        //AnimationUtils.animate(holder);

    }

    public SelfieRecord getItem(int position) {
        final SelfieRecord currentRecord = mRecordList.get(position);
        return currentRecord;
    }

    public List<SelfieRecord> getSelfieList(){
        return mRecordList;
    }

    public void deleteFromLocal(int position) {
        File file = new File(mRecordList.get(position).getPath());
        if(file.exists()){
            file.delete();
        }
        notifyItemChanged(position);
    }


    @Override
    public int getItemCount() {
        return mRecordList.size();
    }


    public void add(SelfieRecord selfieRecord) {
        Log.d(LOG_TAG, "add");
        mRecordList.add(0,selfieRecord);
        notifyItemInserted(mRecordList.indexOf(selfieRecord));

    }

    public ArrayList<SelfieRecord> getSelectedRecords(List<Integer> positions) {

        ArrayList<SelfieRecord> mSelectedRecordList = new ArrayList<>();
        for (Integer positions1 : positions) {
                SelfieRecord nrecord = mRecordList.get(positions1);
                Log.d(LOG_TAG, nrecord.toString());
                mSelectedRecordList.add(nrecord);

        }
        Log.d(LOG_TAG, mSelectedRecordList.toString());
        return mSelectedRecordList;
    }

    public void setSelfies(List<SelfieRecord> selfies) {
        mRecordList = selfies;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {

        SelfieRecord selfieRecord = mRecordList.get(position);
        Log.d(LOG_TAG,selfieRecord.getPath());
        mRecordList.remove(position);
        if (selfieRecord.getPath()!=null){
            if(new File(selfieRecord.getPath()).exists()){
                File newFile = new File(selfieRecord.getPath());
                newFile.delete();

            }
        }
        notifyItemRemoved(position);
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });

        // Split the list in ranges
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i) {
            SelfieRecord selfieRecord = mRecordList.get(positionStart);
            mRecordList.remove(positionStart);
            if (selfieRecord.getPath()!=null){
                if(new File(selfieRecord.getPath()).exists()){
                    File newFile = new File(selfieRecord.getPath());
                    newFile.delete();

                }
            }
        }
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    public void notifySelfieChanged(SelfieRecord selfie) {

        Log.d(LOG_TAG, "***** " + String.valueOf(mRecordList.indexOf(selfie)));
        Log.d(LOG_TAG, "***** " + selfie);
        SelfieResolver selfieResolver = new SelfieResolver(mContext.getContentResolver());
        SelfieRecord selfieRecord = selfieResolver.query(selfie.getId());
        Log.d(LOG_TAG, "***** " + selfieRecord);
        notifyItemChanged(mRecordList.indexOf(selfie), selfieRecord);
    }

    @Override
    public void onItemDismiss(final int position) {

        final SelfieRecord selfieRecord = mRecordList.get(position);
        if (selfieRecord != null) {
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    new SelfieDataMediator(mContext).delete(mContext, selfieRecord.getId());
                    return null;
                }
            }.execute();
        }
        removeItem(position);
            }

    public static class SelfieRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, ItemTouchViewHolderAdapter {

        ImageView thumbnail;
        TextView selfieDate;
        TextView selfieDateCreated;
        TextView selfieDateModified;
        Button deleteButton;
        View selectedOverlay;

        private ClickListener listener;


        public SelfieRecordViewHolder(View itemView, final ClickListener listener) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            selfieDate = (TextView) itemView.findViewById(R.id.selfie_date);
            selfieDateCreated = (TextView)itemView.findViewById(R.id.selfie_date_created);
            selfieDateModified = (TextView)itemView.findViewById(R.id.selfie_date_modified);
            deleteButton = (Button)itemView.findViewById(R.id.delete_button);
            selectedOverlay = itemView.findViewById(R.id.selected_overlay);
            this.listener = listener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            deleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                if (v.getId()==deleteButton.getId()){
                    listener.setButtonClicked(getLayoutPosition());
                }
                else {
                    listener.onItemClicked(getLayoutPosition());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (listener != null) {
                return listener.onItemLongClicked(getLayoutPosition());
            }

            return false;
        }

        @Override
        public void onItemSelected() {
            ((CardView)itemView).setCardBackgroundColor(Color.argb(136, 103, 58, 183));
        }

        @Override
        public void onItemClear() {
            ((CardView)itemView).setCardBackgroundColor(0);
        }

        public interface ClickListener {
            public void onItemClicked(int position);
            public boolean onItemLongClicked(int position);
            public void setButtonClicked(int position);
        }


    }

}
