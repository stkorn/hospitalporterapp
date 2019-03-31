package com.ekstkorn.hospitalporterapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ekstkorn.hospitalporterapp.view.model.JobListView
import com.ekstkorn.hospitalporterapp.view.model.JobView
import kotlinx.android.synthetic.main.layout_queue_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class JobAdapter(var jobList: List<JobView>) : RecyclerView.Adapter<JobAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_queue_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jobList.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(jobList[position])
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: JobView) = with(itemView) {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(item.time)
            val dateFormat = SimpleDateFormat("dd/MM\nHH:mm", Locale.US)
            val strDate = dateFormat.format(date)
            textViewTime.text = strDate
            textViewPatientName.text = item.name
            textViewBuilding.text = item.building
            textViewJobStatus.text = if (item.jobStatus == JobStatus.COMPLETE.status) {
                "เสร็จสิ้น"
            } else {
                "กำลังรับ - ส่ง"
            }

            textViewJobStatus.setTextColor(ContextCompat.getColor(context, if (item.jobStatus == JobStatus.COMPLETE.status) {
                R.color.textGreen
            } else {
                R.color.orange_one
            }))
        }

    }

}