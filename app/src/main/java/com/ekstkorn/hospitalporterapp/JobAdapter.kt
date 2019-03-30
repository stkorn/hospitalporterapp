package com.ekstkorn.hospitalporterapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ekstkorn.hospitalporterapp.view.model.JobListView
import com.ekstkorn.hospitalporterapp.view.model.JobView
import kotlinx.android.synthetic.main.layout_queue_item.view.*

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
            textViewTime.text = item.time
            textViewPatientName.text = item.name
            textViewBuilding.text = item.building
        }

    }

}