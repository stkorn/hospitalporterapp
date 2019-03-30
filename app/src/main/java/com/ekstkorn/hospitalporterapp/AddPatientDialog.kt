package com.ekstkorn.hospitalporterapp

import android.app.Dialog
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
import com.ekstkorn.hospitalporterapp.view.JobViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddPatientDialog : DialogFragment() {

    private val jobViewModel by viewModel<JobViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
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
        val view = inflater.inflate(R.layout.dialog_add_patient, container, false)
        val list = jobViewModel.getDataRepository().initBuilding.value?.data?.map {
            it.buildingName
        }?.toMutableList() ?: emptyList<String>()
        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, list)
        val spinner: Spinner = view.findViewById(R.id.spinnerBuilding)
        val buttonCancel: Button = view.findViewById(R.id.buttonCancel)
        val buttonConfirm: Button = view.findViewById(R.id.buttonConfirm)
        spinner.adapter = arrayAdapter

        buttonCancel.setOnClickListener { dismiss() }
        buttonConfirm.setOnClickListener { dismiss() }

        return view

    }

}