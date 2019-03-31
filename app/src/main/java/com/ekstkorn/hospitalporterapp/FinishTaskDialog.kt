package com.ekstkorn.hospitalporterapp

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.Observer
import com.ekstkorn.hospitalporterapp.view.JobViewModel
import com.ekstkorn.hospitalporterapp.view.OnClose
import kotlinx.android.synthetic.main.dialog_finish_task_patient.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class FinishTaskDialog : androidx.fragment.app.DialogFragment() {

    var jobID: String? = null
    var buildingID: String? = null
    var name: String? = null

    val jobViewModel by viewModel<JobViewModel>()

    var listener: OnClose? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        listener = context as OnClose

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        jobID = arguments?.getString("jobId")
        name = arguments?.getString("name")
        buildingID = arguments?.getString("buildingId")
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog.apply {
            window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_finish_task_patient, container, false)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        val textView: TextView = view.findViewById(R.id.textViewFinishJobMsg)
        val msg = String.format(getString(R.string.finish_job_message), name, buildingID)
        textView.text = msg

        buttonCancel.setOnClickListener { dismiss() }
        buttonConfirm.setOnClickListener {
            jobViewModel.finishJob().observe(this, Observer {
                if (it.data!!) {
                    dismiss()
                    listener?.finishJob()
                }
            })
        }

        return view

    }

}