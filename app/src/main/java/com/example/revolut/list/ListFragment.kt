package com.example.revolut.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.revolut.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.revolut.databinding.FragmentListBinding
import com.example.revolut.setupToolBar

class ListFragment : Fragment() {

    private val viewModel: ListViewModel by viewModel()

    private lateinit var binding: FragmentListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupToolBar(binding.toolbar, getString(R.string.currency_title), false)
        setupRecycleView()
        return binding.root
    }

    private fun setupRecycleView() {
        with(binding.currencyList) {
            layoutManager = LinearLayoutManager(context)
            adapter = CurrencyRecyclerAdapter(
                onClick = { viewModel.onItemClicked(it) },
                amountChanged = { viewModel.amountChanged(it) })
            adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    this@with.scrollToPosition(toPosition)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUpdates()
    }

    override fun onPause() {
        viewModel.pauseUpdates()
        super.onPause()
    }
}
