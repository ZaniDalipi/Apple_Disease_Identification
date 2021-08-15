package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentAccountAnalyticsManagmentBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionDatabase
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.TransactionRepository
import com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics.adapters.TransactionRecyclerViewAdapterRecyclerView


class AccountAnalyticsFragment : Fragment() {

    private lateinit var adapter: TransactionRecyclerViewAdapterRecyclerView
    private lateinit var createTransactionFab: FloatingActionButton
    private lateinit var editTransactionFab: FloatingActionButton
    private lateinit var viewModel: AccountAnalyticsViewModel
    private lateinit var binding: FragmentAccountAnalyticsManagmentBinding
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
        binding =
            FragmentAccountAnalyticsManagmentBinding.inflate(layoutInflater, container, false)

        setupRecyclerViewAdapter()
        chipsFilteringOfTransactions()

        return binding.root
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
        createTransactionFab = binding.createTransactionFab
        editTransactionFab = binding.editTransactionsFab

        refreshTransactionsListener()
        fabButtonsListener()

        onItemSwipeCallback()

        binding.balanceTextView.text =
            viewModel.currentBalance.value.toString()

    }

    /** Fab button listener when are clicked*/
    private fun fabButtonsListener() {

        binding.transactionFab.setOnClickListener {
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
            binding.transactionFab.startAnimation(rotateOpen)
        } else {
            createTransactionFab.startAnimation(toBottom)
            editTransactionFab.startAnimation(toBottom)
            binding.transactionFab.startAnimation(rotateClose)
        }
    }

    /** refresh listener for the list of transactions */

    private fun refreshTransactionsListener() {
        binding.refreshRecyclerview.setOnRefreshListener {
            when {
                binding.showAllTransactionChip.isChecked -> {
                    viewModel.getAllTransactions().also {
                        binding.refreshRecyclerview.isRefreshing = false
                    }
                }
                binding.showIncomesChip.isChecked -> {
                    viewModel.getIncomeTransactions().also {
                        binding.transactionListTypeContainerTextView.setText(
                            R.string.ins
                        )
                        binding.refreshRecyclerview.isRefreshing = false
                    }
                }
                binding.showExpensesChip.isChecked -> {
                    viewModel.getExpenseTransactions().also {
                        binding.transactionListTypeContainerTextView.setText(
                            R.string.outs
                        )
                        binding.refreshRecyclerview.isRefreshing = false
                    }
                }


            }
            binding.refreshRecyclerview.isRefreshing = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.refreshRecyclerview.setColorSchemeColors(
                resources.getColor(R.color.primaryColor, context?.theme),
                resources.getColor(R.color.secondaryDarkColor, context?.theme),
                resources.getColor(R.color.secondaryLightColor, context?.theme)
            )
        }
    }


    private fun setupRecyclerViewAdapter() {
        adapter = TransactionRecyclerViewAdapterRecyclerView()
        binding.transactionsRecyclerView.adapter = adapter



        viewModel.transactions.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.refreshRecyclerview.isRefreshing = false
        })

        val manager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.transactionsRecyclerView.layoutManager = manager

    }

    fun chipsFilteringOfTransactions() {

        binding.showAllTransactionChip.setOnClickListener {
            viewModel.getAllTransactions()
            binding.transactionListTypeContainerTextView.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_transaction_type, 0, 0, 0)
                setText(R.string.transactions)
            }

        }

        binding.showIncomesChip.setOnClickListener {
            viewModel.getIncomeTransactions()
            viewModel.incomeTransactions.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
            binding.transactionListTypeContainerTextView.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__01_receive_amount, 0, 0, 0)
                setText(R.string.ins)
            }
        }

        binding.showExpensesChip.setOnClickListener {
            viewModel.getExpenseTransactions()
            viewModel.expensesTransactions.observe(viewLifecycleOwner, {
                adapter.submitList(it)
            })
            binding.transactionListTypeContainerTextView.apply {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__02_sending_amount, 0, 0, 0)
                setText(R.string.outs)
            }
        }
    }

    fun onItemSwipeCallback() {
        var itemTouchHelperCallback = object : SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                      binding.transactionsRecyclerView.removeViewAt(viewHolder.absoluteAdapterPosition)
                        val selectedTransactionId = adapter.currentList[viewHolder.absoluteAdapterPosition]
                        viewModel.deleteTransaction(selectedTransactionId.transactionId).also {
                            refreshTransactionsListener()
                        }
                        Toast.makeText(
                            requireContext(),
                            "Got the item at position: ${viewHolder.absoluteAdapterPosition} " +
                                    "with item id : $selectedTransactionId",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    ItemTouchHelper.LEFT -> {
                        Toast.makeText(
                            requireContext(),
                            "(left)Got the item at position: ${viewHolder.absoluteAdapterPosition}" +
                                    " with item id : ${viewHolder.itemId}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.transactionsRecyclerView)
    }
}
