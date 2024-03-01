package com.example.bottomnestednavigationtest.ui.dialogs

import android.app.*
import android.os.*
import com.google.android.material.bottomsheet.*
import com.google.android.material.dialog.*

class InfoDialog : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Test")
            .setMessage("Message")
            .setPositiveButton("OK", null)
            .create()
}