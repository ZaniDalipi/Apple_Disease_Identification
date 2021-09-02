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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.zanoapp.applediseaseIdentification.R
import com.zanoapp.applediseaseIdentification.databinding.FragmentAccountAnalyticsManagmentBinding
import com.zanoapp.applediseaseIdentification.localDataPersistence.transactionsDB.Transaction
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



        return binding.root
    }


    private fun filterExpensesTransactions() {
        Log.i(
            "DebuggingApp",
            "chipsFilteringOfTransactions: show expenses transactions chip clicked"
        )
        viewModel.expensesTransactions.observe(viewLifecycleOwner, {
            Log.i("DebuggingApp", "ShowExpensesChip data before submitting: ${it.size}")
            adapter.submitList(it)
            Log.i("DebuggingApp", "ShowExpensesChip data : ${it.size}")
        })

        binding.transactionListTypeContainerTextView.apply {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__02_sending_amount, 0, 0, 0)
            setText(R.string.outs)
        }
    }

    private fun filterIncomeTransactions() {
        Log.i(
            "DebuggingApp",
            "chipsFilteringOfTransactions: show income transactions chip clicked"
        )
        viewModel.getIncomeTransactions()
        viewModel.incomeTransactions.observe(viewLifecycleOwner, {
            Log.i("DebuggingApp", "ShowIncomeChip  data before submitting : ${it.size}")
            adapter.submitList(it)
            Log.i("DebuggingApp", "ShowIncomeChip data : ${it.size}")
        })
        binding.transactionListTypeContainerTextView.apply {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic__01_receive_amount, 0, 0, 0)
            setText(R.string.ins)
        }
    }

    private fun filterAllTransactions() {
        Log.i(
            "DebuggingApp",
            "chipsFilteringOfTransactions: show all transactions chip clicked"
        )
        viewModel.getAllTransactions()
        viewModel.transactions.observe(viewLifecycleOwner, {
            Log.i("DebuggingApp", "ShowALLChip data before submitting : ${it.size}")
            adapter.submitList(it)
            Log.i("DebuggingApp", "ShowALLChip data : ${it.size}")
        })
        binding.transactionListTypeContainerTextView.apply {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_transaction_type, 0, 0, 0)
            setText(R.string.transactions)
        }
    }


    override fun onStart() {
        super.onStart()

        Log.i("DebuggingApp", "OnStart() called")

        createTransactionFab = binding.createTransactionFab
        editTransactionFab = binding.editTransactionsFab

        binding.apply {
            showAllTransactionChip.setOnClickListener {
                filterAllTransactions()
            }
            showIncomesChip.setOnClickListener {
                filterIncomeTransactions()
            }
            showExpensesChip.setOnClickListener {
                filterExpensesTransactions()
            }
        }

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
            findNavController().navigate(R.id.action_accountAnalyticsFragment_to_transactionDetailsFragment)
        }
    }


    /** Functions to set the visibility of FAB when the transaction happens and definition of transactions */
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
        Log.i("DebuggingApp", "refreshTransactionsListener: called ")
        binding.refreshRecyclerview.setOnRefreshListener {
            when {
                binding.showAllTransactionChip.isChecked -> {
                    Log.i(
                        "DebuggingApp",
                        "refreshTransactionsListener: show all transaction chip called "
                    )
                    viewModel.getAllTransactions().also {
                        viewModel.transactions.observe(viewLifecycleOwner, {
                            adapter.submitList(it)
                        })
                        binding.refreshRecyclerview.isRefreshing = false
                    }
                }
                binding.showIncomesChip.isChecked -> {
                    Log.i("DebuggingApp", "refreshTransactionsListener: show income chip called ")
                    viewModel.getIncomeTransactions().also {
                        viewModel.incomeTransactions.observe(viewLifecycleOwner, {
                            adapter.submitList(it)
                        })
                        binding.transactionListTypeContainerTextView.setText(
                            R.string.ins
                        )
                        binding.refreshRecyclerview.isRefreshing = false
                    }
                }
                binding.showExpensesChip.isChecked -> {
                    Log.i("DebuggingApp", "refreshTransactionsListener: show expenses chip called ")
                    viewModel.getExpenseTransactions().also {
                        viewModel.expensesTransactions.observe(viewLifecycleOwner, {
                            adapter.submitList(it)
                        })
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

        viewModel.getAllTransactions()

        viewModel.transactions.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.refreshRecyclerview.isRefreshing = false
        })

        val manager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.transactionsRecyclerView.layoutManager = manager

    }


    fun navigateToDetailsIWithTransaction(viewHolder: RecyclerView.ViewHolder) {
        val selectedTransaction = adapter.currentList[viewHolder.absoluteAdapterPosition]
        val bundle = bundleOf("transaction" to selectedTransaction)
        findNavController().navigate(
            R.id.action_accountAnalyticsFragment_to_transactionDetailsFragment,
            bundle
        )

    }

    private fun onItemSwipeCallback() {
        val itemTouchHelperCallback = object : SimpleCallback(
            0,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                navigateToDetailsIWithTransaction(viewHolder)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val selectedTransactionId =
                            adapter.currentList[viewHolder.absoluteAdapterPosition]
                        context?.let {
                            MaterialAlertDialogBuilder(
                                it,
                                R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered
                            )
                                .setMessage(resources.getString(R.string.delete_transaction_message) + " " + selectedTransactionId.productName)
                                .setNegativeButton(resources.getString(R.string.no)) { dialog, which ->
                                    setupRecyclerViewAdapter()
                                    dialog.cancel()
                                }

                                .setPositiveButton(resources.getString(R.string.yes)) { dialog, which ->
                                    viewModel.deleteTransaction(selectedTransactionId.transactionId)
                                    adapter.notifyItemRemoved(viewHolder.absoluteAdapterPosition)
                                    binding.transactionsRecyclerView.scrollToPosition(0)
                                }
                                .show()
                        }
                    }
                    ItemTouchHelper.RIGHT -> {
                        navigateToDetailsIWithTransaction(viewHolder)
                    }

                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.transactionsRecyclerView)
    }

    private fun initViewModel() {
        val database by lazy { TransactionDatabase.getInstance(requireContext()) }
        val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
        val viewModelFactory = AccountAnalyticsViewModelFactory(transactionRepository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AccountAnalyticsViewModel::class.java)
    }
}
