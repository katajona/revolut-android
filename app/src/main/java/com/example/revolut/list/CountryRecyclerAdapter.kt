package com.example.revolut.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.revolut.BindableAdapter
import com.example.revolut.R
import com.example.revolut.data.Currency
import com.example.revolut.toFormattedDouble
import kotlinx.android.synthetic.main.item_country.view.amountText
import kotlinx.android.synthetic.main.item_country.view.nameText
import com.example.revolut.toFormattedString


class CountryRecyclerAdapter(
    private val onClick: (Currency) -> Unit,
    private val amountChanged: (Currency) -> Unit,
    private val countryDiffCallback: CountryDiffCallback = CountryDiffCallback()
) :
    ListAdapter<Currency, CountryRecyclerAdapter.CountryItemViewHolder>(
        countryDiffCallback
    ),
    BindableAdapter<List<Currency>> {

    override fun setData(data: List<Currency>) {
        submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_country, parent, false)
        return CountryItemViewHolder(
            view,
            onClick,
            amountChanged,
            countryDiffCallback
        )
    }

    override fun onBindViewHolder(viewHolder: CountryItemViewHolder, position: Int) {
        viewHolder.bind(getItem(position))
        0.9.toFormattedString()
    }


    class CountryItemViewHolder(
        private val view: View,
        private val onClick: (Currency) -> Unit,
        private val amountChanged: (Currency) -> Unit,
        private val countryDiffCallback: CountryDiffCallback
    ) : RecyclerView.ViewHolder(view) {
        fun bind(currency: Currency) {
            view.amountText.setText(currency.amount.toFormattedString())
            view.nameText.text = currency.country
            view.setOnClickListener {
                onClick(currency)
            }
            view.amountText.doOnTextChanged { text, _, _, _ ->
                if (view.amountText.hasFocus()) {
                    currency.amount = text.toString().toFormattedDouble()
                    countryDiffCallback.setChangedRate(currency)
                    amountChanged(currency)
                }
            }
        }
    }
}

class CountryDiffCallback : DiffUtil.ItemCallback<Currency>() {
    // use this so we are not updating our currently edited value
    private var changedRate: Currency? = null
    fun setChangedRate(currency: Currency) {
        changedRate = currency
    }

    override fun areItemsTheSame(p0: Currency, p1: Currency) = p0.country == p1.country


    override fun areContentsTheSame(p0: Currency, p1: Currency) =
        if (p1 == changedRate) {
            true
        } else {
            p0 == p1
        }
}
