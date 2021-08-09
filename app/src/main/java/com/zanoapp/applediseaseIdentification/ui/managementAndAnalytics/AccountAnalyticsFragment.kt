package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentAccountAnalyticsManagmentBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters.TransactionRecyclerViewAdapterRecyclerView

class AccountAnalyticsFragment : Fragment() {

    private lateinit var createTransactionFab: FloatingActionButton
    private lateinit var editTransactionFab: FloatingActionButton
    private lateinit var viewModel: AccountAnalyticsViewModel
    private lateinit var accountAnalyticsFragmentBinding: FragmentAccountAnalyticsManagmentBinding
    private var clicked = false

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
        Log.i("ViewModelState", viewModel.toString())
        Log.i("ViewModelState", viewModel.transactions.value?.size.toString())

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        accountAnalyticsFragmentBinding =
            FragmentAccountAnalyticsManagmentBinding.inflate(layoutInflater, container, false)

        setupRecyclerViewAdapter()

        return accountAnalyticsFragmentBinding.root
    }

    private fun initViewModel() {
        val database by lazy { TransactionDatabase.getInstance(requireContext()) }
        val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
        val viewModelFactory = AccountAnalyticsViewModelFactory(transactionRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AccountAnalyticsViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        createTransactionFab = accountAnalyticsFragmentBinding.createTransactionFab
        editTransactionFab = accountAnalyticsFragmentBinding.editTransactionsFab

        refreshTransactionsListener()
        fabButtonsListener()
    }

    /** Fab button listener when are clicked*/
    private fun fabButtonsListener() {
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


    /** Functions to set the visibility of fabs when the transaction happens and defination of transactions */
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

    /** refresh listener for the list of transactions */

    private fun refreshTransactionsListener() {
        accountAnalyticsFragmentBinding.refreshRecyclerview.setOnRefreshListener {
            setupRecyclerViewAdapter()
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accountAnalyticsFragmentBinding.refreshRecyclerview.setColorSchemeColors(
                resources.getColor(R.color.primaryColor, context?.theme),
                resources.getColor(R.color.secondaryDarkColor, context?.theme),
                resources.getColor(R.color.secondaryLightColor, context?.theme)
            )
        }
    }


    private fun setupRecyclerViewAdapter() {
        val adapter = TransactionRecyclerViewAdapterRecyclerView()
        accountAnalyticsFragmentBinding.transactionsRecyclerView.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            accountAnalyticsFragmentBinding.refreshRecyclerview.isRefreshing = false

        })

        val manager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        accountAnalyticsFragmentBinding.transactionsRecyclerView.layoutManager = manager
    }
    
}