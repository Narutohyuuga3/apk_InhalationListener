package com.example.flopfl.inhalationlistener;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.flopfl.inhalationlistener.Data.MedikamentEntry;

import java.util.List;
//Taskadapter für Protokoll-Funktion der Medikament-Activity
public class TaskAdpater extends RecyclerView.Adapter<TaskAdpater.TaskViewHolder> {
    final private ItemClickListener mItemClickListener;
    private List<MedikamentEntry> mMedikamentEntry;
    private Context mcontext;

    public TaskAdpater(Context context, ItemClickListener listener){
        mcontext=context;
        mItemClickListener=listener;
    }

    //erstellen der Viewholder mit dem zugehörigen Layout für die Einträge
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int Type) {//erstellt die verwendete Zahl an viewholder
        View view=LayoutInflater.from(mcontext).inflate(R.layout.task_layout,parent,false);// das zugehörige Layout wird erstellt

        return new TaskViewHolder(view);
    }

    //Befüllen des Viewholders mit dem Eintrag an der Position
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder taskViewHolder, int position) {//befüllt die Viewholder mit Informationen, Position beschreibt dabei die Listenposition es eintrags
        MedikamentEntry entry= mMedikamentEntry.get(position);
        String description = entry.getDescription();
        String time2=entry.getTime2();
        String time1=entry.getTime1();
        boolean M= entry.isMonday();
        boolean Tu= entry.isTuesday();
        boolean Th=entry.isThursday();
        boolean W=entry.isWednesday();
        boolean F=entry.isFriday();
        boolean Sa=entry.isSaturday();
        boolean Su=entry.isSunday();
        boolean No=entry.isNotification();
        String device=entry.getDeviceName();
        String updatedAt=entry.getUpdatedAt();
        String updatedAt2=entry.getUpdatedAt2();
        String updatedAt3=entry.getUpdatedAt3();

        taskViewHolder.mDescription.setText(description);
        taskViewHolder.mTime1.setText(time1);
        taskViewHolder.mTime2.setText(time2);
        taskViewHolder.mDeviceName.setText(device);
        taskViewHolder.mUpdatedAt.setText(updatedAt);
        taskViewHolder.mUpdatedAt2.setText(updatedAt2);
        taskViewHolder.mUpdatedAt3.setText(updatedAt3);

        if(No){
            taskViewHolder.mLayout.setBackgroundColor(ContextCompat.getColor(mcontext,R.color.colorPrimaryLight));
        }
        else {
            taskViewHolder.mLayout.setBackgroundColor(ContextCompat.getColor(mcontext,R.color.white));

        }

        GradientDrawable Monday_Circle= (GradientDrawable)taskViewHolder.mMonday.getBackground();
        Monday_Circle.setColor(getPriorityColor(M));
        GradientDrawable T_Circle= (GradientDrawable)taskViewHolder.mTuesday.getBackground();
        T_Circle.setColor(getPriorityColor(Tu));
        GradientDrawable W_Circle= (GradientDrawable)taskViewHolder.mWednesday.getBackground();
        W_Circle.setColor(getPriorityColor(W));
        GradientDrawable Th_Circle= (GradientDrawable)taskViewHolder.mThursday.getBackground();
        Th_Circle.setColor(getPriorityColor(Th));
        GradientDrawable Fr_Circle= (GradientDrawable)taskViewHolder.mFriday.getBackground();
        Fr_Circle.setColor(getPriorityColor(F));
        GradientDrawable Sa_Circle= (GradientDrawable)taskViewHolder.mSaturday.getBackground();
        Sa_Circle.setColor(getPriorityColor(Sa));
        GradientDrawable Su_Circle= (GradientDrawable)taskViewHolder.mSunday.getBackground();
        Su_Circle.setColor(getPriorityColor(Su));

    }

    //übergibt farbe für selektiert und nicht selektierte Tage
    private int getPriorityColor(boolean selected){
        if(selected){
            return ContextCompat.getColor(mcontext,R.color.colorPrimary);
        }
        else{
            return ContextCompat.getColor(mcontext,R.color.colorPrimaryLight);
        }
    }


    public List<MedikamentEntry> getEntries(){//rückgabe liste mit entries
        return mMedikamentEntry;
    }

    //getter für die Anzahl der Einträge
    @Override
    public int getItemCount() {
        if(mMedikamentEntry==null){
            return 0;
        }
        return mMedikamentEntry.size();
    }

    //Listener für die einzelnen Einträge in dem Protokoll
    public interface ItemClickListener{// definiert interface für itemclick
        void onItemClickListener(int itemID);
    }

    //setter für die Einträge des Adapters
    public void setTasks(List<MedikamentEntry> entries){
        mMedikamentEntry=entries;
        notifyDataSetChanged();
    }

    //Viewholder für die Einträge
    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mDescription;
        TextView mTime1;
        TextView mTime2;
        TextView mMonday;
        TextView mTuesday;
        TextView mWednesday;
        TextView mThursday;
        TextView mFriday;
        TextView mSaturday;
        TextView mSunday;
        TextView mDeviceName;
        TextView mUpdatedAt;
        TextView mUpdatedAt2;
        TextView mUpdatedAt3;
        LinearLayout mLayout;


        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            mDescription=(TextView)itemView.findViewById(R.id.taskDescription);
            mTime1=(TextView)itemView.findViewById(R.id.Time1);
            mTime2=(TextView)itemView.findViewById(R.id.Time2);
            mMonday=(TextView)itemView.findViewById(R.id.priorityTextView_Monday);
            mTuesday=(TextView)itemView.findViewById(R.id.priorityTextView_Tuesday);
            mWednesday=(TextView)itemView.findViewById(R.id.priorityTextView_Wednesday);
            mThursday=(TextView)itemView.findViewById(R.id.priorityTextView_Thursday);
            mFriday=(TextView)itemView.findViewById(R.id.priorityTextView_Friday);
            mSaturday=(TextView)itemView.findViewById(R.id.priorityTextView_Saturday);
            mSunday=(TextView)itemView.findViewById(R.id.priorityTextView_Sunday);
            mUpdatedAt=(TextView)itemView.findViewById(R.id.UpdateTime_Textview);
            mUpdatedAt2=(TextView)itemView.findViewById(R.id.UpdateTime2_Textview);
            mUpdatedAt3=(TextView)itemView.findViewById(R.id.UpdateTime3_Textview);
            mDeviceName=(TextView)itemView.findViewById(R.id.Device_Name_Textview);
            mLayout=(LinearLayout)itemView.findViewById(R.id.Background_Layout);
            itemView.setOnClickListener(this);
        }

        //übergibt bei Onclick die ID des Eintrags
        @Override
        public void onClick(View v) {
            int id=mMedikamentEntry.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(id);//übergibt dem Interface Object die ID wenn auf Viewholder geklickt wird
        }
    }
}
