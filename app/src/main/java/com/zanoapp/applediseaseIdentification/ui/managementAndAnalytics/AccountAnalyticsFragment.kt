package com.zanoapp.applediseaseIdentification.ui.managementAndAnalytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zanoapp.applediseaseIdentification.R

class AccountAnalyticsFragment : Fragment() {

    companion object {
        fun newInstance() = AccountAnalyticsFragment()
    }

    private lateinit var viewModel: AccountAnalyticsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.account_analytics_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AccountAnalyticsViewModel::class.java)
    }
}