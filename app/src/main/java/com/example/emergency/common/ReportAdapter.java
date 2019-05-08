package com.example.emergency.common;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.emergency.R;

import java.util.ArrayList;
import java.util.Date;

public class ReportAdapter extends RecyclerView.Adapter <ReportAdapter.ReportViewHolder> {
    private ArrayList<Report> emergencyReports;
    private Context context;

    public ReportAdapter(ArrayList<Report> emergencyReports, Context context){
        this.emergencyReports = emergencyReports;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {

        switch (emergencyReports.get(position).getEmergencyType()){
            case "Police":
                holder.emergencyTypeImage.setBackgroundResource(R.drawable.police);
                break;
            case "Fire":
                holder.emergencyTypeImage.setBackgroundResource(R.drawable.fire);
                break;
            case "Medical":
                holder.emergencyTypeImage.setBackgroundResource(R.drawable.medical);
                break;
                default:
                    holder.emergencyTypeImage.setBackgroundResource(R.drawable.circle_solid_color_primary);
        }

        holder.emergencyType.setText(context.getString(R.string.emergencyTypeString, emergencyReports.get(position).getEmergencyType()));

        holder.emergencyDate.setText(DateFormat.format("dd-MMM-yyyy",
                new Date(Long.valueOf(emergencyReports.get(position).getEmergencyTimeStamp()))));

        holder.emergencyStatus.setText(emergencyReports.get(position).getEmergencyStatus());

    }

    @Override
    public int getItemCount() {
        return emergencyReports.size();
    }

    public class ReportViewHolder extends RecyclerView.ViewHolder{

        private TextView emergencyType;
        private TextView emergencyStatus;
        private TextView emergencyDate;
        private ImageView emergencyTypeImage;

        public ReportViewHolder(View itemView){
            super(itemView);

            emergencyType = itemView.findViewById(R.id.emergency_type_textView);
            emergencyStatus = itemView.findViewById(R.id.status_textView);
            emergencyDate = itemView.findViewById(R.id.date_textView);
            emergencyTypeImage = itemView.findViewById(R.id.report_image);
        }

    }
}


