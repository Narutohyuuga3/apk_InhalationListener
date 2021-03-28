package com.example.flopfl.inhalationlistener.Recording;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.flopfl.inhalationlistener.Data_Inhalation.InhalationEntry;
import com.example.flopfl.inhalationlistener.R;

import java.util.List;

//Taskadapter der RecyclerView der Inhalation-Activity
public class TaskAdapter_Inhalation extends RecyclerView.Adapter<TaskAdapter_Inhalation.TaskViewHolder> {
    final private ItemClickListener mItemClickListener;
    private List<InhalationEntry> mInhalationEntry;
    private Context mcontext;

    public TaskAdapter_Inhalation(ItemClickListener listener,Context contex) {
        mItemClickListener =listener ;
        mcontext=contex;
    }

//erstellen der Viewholder mit dem zugehörigen Layout für die Einträge
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mcontext).inflate(R.layout.inhalation_task_layout,parent,false);

        return new TaskViewHolder(view);
    }

    //Befüllen des Viewholders mit dem Eintrag an der Position
    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        InhalationEntry entry= mInhalationEntry.get(position);
        int schuetteln=entry.getSchuetteln();
        int akku=entry.getAkku();
        int innehalten=entry.getInnehalten();
        int inhalation=entry.getInhalation();
        int exhalation=entry.getExhalation();

        if(akku>100 && akku<130){
            akku=100;
        }
        if(akku<5){
            holder.mAkku.setText("--");
        }
        else{
            if(akku<101){
                holder.mAkku.setText(""+akku+"% Akku");
            }
        }

        holder.mInnehalten.setText(""+innehalten+"%");
        holder.mInhalation.setText(""+inhalation+"%");
        holder.mExhalation.setText(""+exhalation+"%");

        if(exhalation==100){
            holder.mstaticExhalation.setBackgroundColor( Color.GREEN);
            holder.mExhalation.setBackgroundColor( Color.GREEN);
        }
        if(exhalation==0){
            holder.mExhalation.setBackgroundColor( Color.RED);
            holder.mstaticExhalation.setBackgroundColor( Color.RED);
        }



        if(schuetteln>=80){
            holder.mSchuetteln.setText("geschüttelt");
            holder.mSchuetteln.setBackgroundColor( Color.GREEN);
            holder.mstaticSchuetteln.setBackgroundColor( Color.GREEN);
        }
        if(schuetteln>=60 && schuetteln<80){
            holder.mSchuetteln.setBackgroundColor( Color.YELLOW);
            holder.mstaticSchuetteln.setBackgroundColor( Color.YELLOW);
        }
        if(schuetteln<60){
            holder.mSchuetteln.setText("");
            holder.mSchuetteln.setBackgroundColor( Color.RED);
            holder.mstaticSchuetteln.setBackgroundColor( Color.RED);
        }

        if(innehalten>=85){
            holder.mInnehalten.setBackgroundColor( Color.GREEN);
            holder.mstaticInnehalten.setBackgroundColor( Color.GREEN);
        }
        if(innehalten>=50 && innehalten<85){
            holder.mInnehalten.setBackgroundColor( Color.YELLOW);
            holder.mstaticInnehalten.setBackgroundColor( Color.YELLOW);
        }
        if(innehalten<50){
            holder.mInnehalten.setBackgroundColor( Color.RED);
            holder.mstaticInnehalten.setBackgroundColor( Color.RED);
        }

        if(inhalation>=80){
            holder.mInhalation.setBackgroundColor( Color.GREEN);
            holder.mstaticInhalation.setBackgroundColor( Color.GREEN);
        }
        if(inhalation>=40 && inhalation<80){
            holder.mInhalation.setBackgroundColor( Color.YELLOW);
            holder.mstaticInhalation.setBackgroundColor( Color.YELLOW);
        }
        if(inhalation<40){
            holder.mInhalation.setBackgroundColor( Color.RED);
            holder.mstaticInhalation.setBackgroundColor( Color.RED);
        }
        if(innehalten==0){
            if(inhalation>=40){
                holder.mInnehalten.setText("--");
                holder.mInnehalten.setBackgroundColor( Color.GRAY);
                holder.mstaticInnehalten.setBackgroundColor( Color.GRAY);
            }
        }

        if(innehalten>100){
            innehalten=100;
        }


        int prog=(60*schuetteln+100*innehalten+100*inhalation+40*exhalation)/300;
        Log.d("teststs", ""+prog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(prog>=80){
                holder.mProgress.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            }
            if(prog>=60 && prog<80){
                holder.mProgress.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
            }
            if(prog<60){
                holder.mProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));
            }
        }

        holder.mProgress.setProgress(prog);
       // holder.mResult.setText(entry.getInfo());
        holder.mTime.setText(entry.getUpdatedAt());


    }
    //getter für Einträge des Adapters
    public List<InhalationEntry> getEntries(){
        return mInhalationEntry;
    }
    //Setter für Einträge des Adapters
    public void setTasks(List<InhalationEntry> entries){
        mInhalationEntry=entries;
        notifyDataSetChanged();
    }

    //getter für die Anzahl der Einträge
    @Override
    public int getItemCount() {
        if(mInhalationEntry==null){
            return 0;
        }
        return mInhalationEntry.size();
    }

    //Listener für die einzelnen Einträge in dem Protokoll
    public interface ItemClickListener{
        void onItemClickListener(int itemID);
    }

    //Viewholder Klasse
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mstaticInhalation;
        TextView mstaticInnehalten;
        TextView mstaticSchuetteln;
        TextView mTime;
        TextView mInhalation;
        TextView mInnehalten;
        TextView mAkku;
        TextView mSchuetteln;
        TextView mExhalation;
        TextView mstaticExhalation;
        ProgressBar mProgress;

        public TaskViewHolder(View itemView) {
            super(itemView);
            mTime=(TextView)itemView.findViewById(R.id.Inhalation_Task_Uhrzeit);
            mInhalation=(TextView)itemView.findViewById(R.id.Inhalation_Task_Inhalation);
            mInnehalten=(TextView)itemView.findViewById(R.id.Inhalation_Task_Innehalten);
            mAkku=(TextView)itemView.findViewById(R.id.Inhalation_Task_Akku);
            mSchuetteln=(TextView)itemView.findViewById(R.id.Inhalation_Task_Schuetteln);
            mProgress=(ProgressBar)itemView.findViewById(R.id.Inhalation_Task_ProgressBar);
            mstaticInhalation=(TextView)itemView.findViewById(R.id.Inhalation_Task_Inhalation_static);
            mstaticInnehalten=(TextView)itemView.findViewById(R.id.Inhalation_Task_Innehalten_static);
            mstaticSchuetteln=(TextView)itemView.findViewById(R.id.Inhalation_Task_Schuetteln_static);
            mstaticExhalation=(TextView)itemView.findViewById(R.id.Inhalation_Task_Exhalation_static);
            mExhalation=(TextView)itemView.findViewById(R.id.Inhalation_Task_Exhalation);
            itemView.setOnClickListener(this);
        }

        //übergibt bei Onclick die ID des Eintrags
        @Override
        public void onClick(View v) {
            int id=mInhalationEntry.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(id);
        }
    }
}
