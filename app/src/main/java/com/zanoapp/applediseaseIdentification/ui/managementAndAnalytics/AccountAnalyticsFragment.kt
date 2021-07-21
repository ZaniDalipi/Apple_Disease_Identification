package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.AccountAnalyticsFragmentBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository

class AccountAnalyticsFragment : Fragment() {

    private lateinit var createTransactionFab: FloatingActionButton
    private lateinit var editTransactionFab: FloatingActionButton
    private lateinit var viewModel: AccountAnalyticsViewModel
    private lateinit var accountAnalyticsFragmentBinding: AccountAnalyticsFragmentBinding
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        accountAnalyticsFragmentBinding =
            AccountAnalyticsFragmentBinding.inflate(layoutInflater, container, false)
        return accountAnalyticsFragmentBinding.root
    }

    private fun initViewModel() {
        val database by lazy { TransactionDatabase.getInstance(requireContext()) }
        val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
        val viewModelFactory = AccountAnalyticsViewModelFactory(transactionRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AccountAnalyticsViewModel::class.java)
    }

    private var clicked = false
    override fun onStart() {
        super.onStart()
        createTransactionFab = accountAnalyticsFragmentBinding.createTransactionFab
        editTransactionFab = accountAnalyticsFragmentBinding.editTransactionsFab

        accountAnalyticsFragmentBinding.transactionFab.setOnClickListener {
            clicked = !clicked
            setVisibility(clicked)
            setAnimation(clicked)
        }

        createTransactionFab.setOnClickListener {
            findNavController().navigate(R.id.action_accountAnalyticsFragment_to_addTransactionDialog)
        }

        editTransactionFab.setOnClickListener {
            Toast.makeText(
                context,
                "set the cards of transaction in edit mode by clicking it we go to details of transaction itself",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (clicked) {
            createTransactionFab.visibility = View.VISIBLE
            editTransactionFab.visibility = View.VISIBLE
            createTransactionFab.isClickable = true
            editTransactionFab.isClickable = true

        } else {
            createTransactionFab.visibility = View.GONE
            createTransactionFab.isClickable = false
            editTransactionFab.isClickable = false
            editTransactionFab.visibility = View.GONE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (clicked) {
            createTransactionFab.startAnimation(fromBottom)
            editTransactionFab.startAnimation(fromBottom)
            accountAnalyticsFragmentBinding.transactionFab.startAnimation(rotateOpen)
        } else {
            createTransactionFab.startAnimation(toBottom)
            editTransactionFab.startAnimation(toBottom)
            accountAnalyticsFragmentBinding.transactionFab.startAnimation(rotateClose)
        }

    }



    fun getTransactions() {
        val myTransactions = viewModel.transactions.value
        if (myTransactions != null) {
            for (element in myTransactions) {
                TODO("create card view for each element in transactions and than display them ")

            }
        }

    }

    fun refreshTransactions() {
        TODO(
            "add on swipe up" +
                    "https://developer.android.com/training/swipe/add-swipe-interface"
        )
    }
}