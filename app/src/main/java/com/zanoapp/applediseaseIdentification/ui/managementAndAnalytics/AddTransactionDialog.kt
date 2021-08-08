package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.DialogCreateTransactionBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import java.util.*
import android.app.Dialog as Dialog1


class AddTransactionDialog : DialogFragment() {

    private lateinit var viewModel: AccountAnalyticsViewModel
    private lateinit var binding: DialogCreateTransactionBinding
    private lateinit var productName: TextInputEditText
    private lateinit var massSold: TextInputEditText
    private lateinit var unitPrice: TextInputEditText
    private lateinit var clientName: TextInputEditText
    private lateinit var datePicker: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreateTransactionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    private fun initViewModel() {
        val database by lazy { TransactionDatabase.getInstance(requireContext()) }
        val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
        val viewModelFactory = AccountAnalyticsViewModelFactory(transactionRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AccountAnalyticsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog1 {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
        dialog.window?.attributes?.windowAnimations = R.style.MyDialogAnimation
        return dialog

    }

    private fun createAlertDialogOnExitDialog() {
        dialog?.onBackPressed().apply {
            context?.let {
                MaterialAlertDialogBuilder(
                    it,
                    R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
                )
                    .setMessage(resources.getString(R.string.long_message))
                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                        dialog.apply {

                        }
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        dialog.cancel()
                    }
                    .show()
            }
        }
    }


    override fun onStart() {
        super.onStart()

        bindingFields()

        val transactionOptions = resources.getStringArray(R.array.transaction_options)

        ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            transactionOptions
        ).also { adapter ->
            binding.transactionTypeDropDownList.setText("", false)
            binding.transactionTypeDropDownList.setAdapter(adapter)
        }
        datePickerConfiguration()

        binding.saveTransactionButton.setOnClickListener {
            if (productName.isNotNullOrEmpty("Field is required!!!") &&
                massSold.isNotNullOrEmpty("Field is required!!!") &&
                unitPrice.isNotNullOrEmpty("Field is required!!!") &&
                datePicker.isNotNullOrEmpty("Field is required!!!")
            ) {
                insertingTransactionToDB()
            }
        }
    }


    private fun insertingTransactionToDB() {
        val productName = binding.productTypeEditText.text.toString()
        val massSold = binding.massEditText.text.toString().toInt()
        val unitPrice = binding.priceEditText.text.toString().toDouble()
        val additionalDescription = binding.descriptionEditText.text.toString()
        val clientName = binding.clientNameEditText.text.toString()
        val transactionType = binding.transactionTypeDropDownList.text.toString()
        val datePicker = binding.datePickerEditText.text.toString()

        Log.i("number format", datePicker)


        val currentTransaction = Transaction(
            productName = productName,
            mass = massSold,
            price = unitPrice,
            additionalDescription = additionalDescription,
            clientName = clientName,
            transactionType = transactionType,
            saleDate = datePicker
        )

        Toast.makeText(
            requireContext(),
            "Adding the current transaction : $currentTransaction",
            Toast.LENGTH_SHORT
        ).show()
        viewModel.insertTransactionIntoDB(currentTransaction)
    }

    private fun bindingFields() {
        productName = binding.productTypeEditText
        massSold = binding.massEditText
        unitPrice = binding.priceEditText
        clientName = binding.clientNameEditText
        datePicker = binding.datePickerEditText
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialog.apply {
            createAlertDialogOnExitDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun datePickerConfiguration() {
        binding.datePickerEditText.isFocusable = false
        binding.datePickerEditText.setOnClickListener {

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Set Transaction Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
                .build()

            datePicker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = Date(it)
                binding.datePickerEditText.setText(
                    "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                        calendar.get(
                            Calendar.YEAR
                        )
                    }"
                )
            }
            datePicker.show(parentFragmentManager, datePicker.toString())

        }
    }
}