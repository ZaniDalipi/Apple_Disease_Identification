package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.transactionDetailsScreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentTransactionDetailsBinding
import com.zanoapp.applediseaseIdentification.databinding.TransactionBackdropBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.AccountAnalyticsViewModel
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.AccountAnalyticsViewModelFactory
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.isNotNullOrEmpty
import com.zanoapp.applediseaseIdentification.utils.isDarkTheme
import java.util.*
import kotlin.math.roundToInt

class TransactionDetailsFragment : Fragment() {


    private lateinit var binding: FragmentTransactionDetailsBinding
    private lateinit var transactionBackdropBinding: TransactionBackdropBinding
    private var isEditableModeOn = false
    private lateinit var viewModel: AccountAnalyticsViewModel
    private lateinit var selectedTransaction: Transaction
    private var newTransactionData: Transaction? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()

        selectedTransaction = arguments?.getParcelable("transaction")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        transactionBackdropBinding = binding.backdropId
        setBackdropBackground()

        populateFields(selectedTransaction)

        transactionBackdropBinding.detailsDateEditText.setOnClickListener {
            if (isEditableModeOn) {
                datePickerConfiguration()
            }
        }

        binding.editTransactionButton.setOnClickListener {
            Toast.makeText(context, "Edit Mode", Toast.LENGTH_SHORT).show()
            onEditButtonClicked()
        }
        binding.saveTransactionButton.setOnClickListener {
            if (transactionBackdropBinding.run {
                    detailsProductTypeEditText.isNotNullOrEmpty("Field is required!!!") and
                            detailsDateEditText.isNotNullOrEmpty("Field is required!!!") and
                            detailsMassEditText.isNotNullOrEmpty("Field is required!!!") and
                            detailsPriceEditText.isNotNullOrEmpty("Field is required!!!")
                }) {
                configureUIStateOnEdit()
                saveUpdatedTransaction()


            } else {
                Toast.makeText(context, "Please fill the required fields ", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }

    private fun populateFields(transaction: Transaction) {
        val transactionTotal = transaction.calculateTotal()
        transactionBackdropBinding.apply {
            transactionTotalTextView.text = transactionTotal.toString().plus("€")
            detailsProductTypeEditText.setText(transaction.productName)
            detailsTransactionIdEditText.setText(transaction.transactionId.toString())
            detailsDescriptionEditText.setText(transaction.additionalDescription)
            detailsClientNameEditText.setText(transaction.clientName)
            detailsDateEditText.setText(transaction.saleDate)
            detailsPriceEditText.setText(transaction.price.toString())
            detailsMassEditText.setText(transaction.mass.toString())
        }.also {
            when (transaction.transactionType) {
                "Incomes" -> {
                    binding.gradientBackgroundTransactionType.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.income_background_gradient,
                            null
                        )

                    binding.transactionTypeIconImageView.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic__01_receive_amount,
                            null
                        )
                }

                "Expenses" -> {
                    binding.gradientBackgroundTransactionType.background =
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.expenses_background_gradient,
                            null
                        )

                    binding.transactionTypeIconImageView.background =
                        ResourcesCompat.getDrawable(
                            resources, R.drawable.ic__02_sending_amount, null
                        )
                }
            }
        }
    }


    /** Function that have to save the edited transaction to the database and controlling the state of the view */
    private fun configureUIStateOnEdit() {
        isEditableModeOn = false
        transactionBackdropBinding.apply {
            detailsProductTypeEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
            detailsTransactionIdEditText.apply {
                isFocusableInTouchMode = false; isFocusable = false
            }
            detailsDescriptionEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
            detailsClientNameEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
            detailsDateEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
            detailsPriceEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
            detailsMassEditText.apply { isFocusableInTouchMode = false; isFocusable = false }
        }

        binding.editTransactionButton.visibility = View.VISIBLE
        binding.saveTransactionButton.visibility = View.GONE

    }


    private fun saveUpdatedTransaction() {

        newTransactionData = Transaction(
            transactionId = transactionBackdropBinding.detailsTransactionIdEditText.text.toString()
                .toLong(),
            transactionType = selectedTransaction.transactionType,
            productName = transactionBackdropBinding.detailsProductTypeEditText.text.toString(),
            mass = transactionBackdropBinding.detailsMassEditText.text.toString().toInt(),
            price = transactionBackdropBinding.detailsPriceEditText.text.toString().toDouble(),
            saleDate = transactionBackdropBinding.detailsDateEditText.text.toString(),
            additionalDescription = transactionBackdropBinding.detailsDescriptionEditText.text.toString(),
            clientName = transactionBackdropBinding.detailsClientNameEditText.toString()

        )

        if (newTransactionData!!.transactionId == selectedTransaction.transactionId) {
            viewModel.updateTransaction(newTransactionData!!)
            Toast.makeText(
                context,
                "Saved Transaction: ${newTransactionData!!.productName}",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    /** Function to set the field to editable mode */
    private fun onEditButtonClicked() {
        isEditableModeOn = true
        binding.editTransactionButton.visibility = View.GONE
        binding.saveTransactionButton.visibility = View.VISIBLE

        transactionBackdropBinding.apply {
            detailsProductTypeEditText.isFocusableInTouchMode = true
            detailsTransactionIdEditText.apply {
                isFocusableInTouchMode = false; isFocusable = false
            }
            detailsDescriptionEditText.isFocusableInTouchMode = true
            detailsClientNameEditText.isFocusableInTouchMode = true
            detailsDateEditText.apply { isFocusableInTouchMode = true; isFocusable = false }
            detailsPriceEditText.isFocusableInTouchMode = true
            detailsMassEditText.isFocusableInTouchMode = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun datePickerConfiguration() {
        transactionBackdropBinding.detailsDateEditText.isFocusable = false

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Set Transaction Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            transactionBackdropBinding.detailsDateEditText.setText(
                "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}/${
                    calendar.get(
                        Calendar.YEAR
                    )
                }"
            )
        }
        datePicker.show(parentFragmentManager, datePicker.toString())
    }

    /** Function that sets the background of the backdrop part of details */
    private fun setBackdropBackground() {
        if (isDarkTheme(requireContext())) {
            binding.backdropView.background =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.backdrop_background_night,
                    null
                )
        } else {
            binding.backdropView.background =
                ResourcesCompat.getDrawable(resources, R.drawable.backdrop_backgorund_day, null)

        }
    }

    private fun initViewModel() {
        val database by lazy { TransactionDatabase.getInstance(requireContext()) }
        val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
        val viewModelFactory = AccountAnalyticsViewModelFactory(transactionRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AccountAnalyticsViewModel::class.java)
    }

    private fun Transaction.calculateTotal(): Int {
        val result = mass * price
        when (transactionType) {
            "Incomes" -> {
                transactionBackdropBinding.transactionTotalTextView.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.primaryColor, null)
                )

            }
            "Expenses" -> {
                transactionBackdropBinding.transactionTotalTextView.setTextColor(
                    ResourcesCompat.getColor(resources, R.color.onError, null)
                )
            }
            else -> {
                transactionBackdropBinding.transactionTotalTextView.setTextColor(Color.GRAY)
            }
        }
        return result.roundToInt()
    }
}