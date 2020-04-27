package com.example.revolut.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.revolut.BindableAdapter
import com.example.revolut.R
import com.example.revolut.data.Currency
import com.example.revolut.toFormattedDouble
import com.example.revolut.toFormattedString
import kotlinx.android.synthetic.main.item_country.view.amountText
import kotlinx.android.synthetic.main.item_country.view.flag
import kotlinx.android.synthetic.main.item_country.view.nameText
import java.util.Locale


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
    }

    override fun onBindViewHolder(
        viewHolder: CountryItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(viewHolder, position, payloads);
        } else {
            val bundle = payloads[0] as Bundle
            for (key in bundle.keySet()) {
                if (key == RATE) {
                    val rate = bundle.getParcelable<Currency>(key)
                    rate?.let {
                        viewHolder.updateRate(it)
                    }
                }
            }
        }
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
            setImage(currency)
            updateRate(currency)
        }

        private fun setImage(currency: Currency) {
            var id: Int = view.context.resources.getIdentifier(
                currency.country.toLowerCase(Locale.ROOT),
                "drawable",
                view.context.packageName
            )
            if (id == 0) {
                id = R.drawable.unknown
            }
            view.flag.setImageResource(id)
        }

        fun updateRate(currency: Currency) {
            view.amountText.setText(currency.amount.toFormattedString())
            view.amountText.setOnFocusChangeListener { view, focused ->
                if (focused) {
                    setChangingRate(currency)
                    setCursorToEnd(view.amountText)
                } else {
                    view.amountText.setText(currency.amount.toFormattedString())
                }
            }
            view.amountText.doOnTextChanged { text, _, _, _ ->
                if (view.amountText.hasFocus()) {
                    currency.amount = text.toString().toFormattedDouble()
                    setChangingRate(currency)
                }
            }
            view.setOnClickListener {
                onClick(currency)
                view.amountText.requestFocus()
                setCursorToEnd(view.amountText)
            }
        }

        private fun setChangingRate(currency: Currency) {
            countryDiffCallback.setChangedRate(currency)
            amountChanged(currency)
        }

        private fun setCursorToEnd(editText: EditText) {
            editText.setSelection(editText.text.length)
        }
    }
}

private const val RATE = "rate"

class CountryDiffCallback : DiffUtil.ItemCallback<Currency>() {
    // use this so we are not updating our currently edited value
    private var changedRate: Currency? = null

    fun setChangedRate(currency: Currency) {
        changedRate = currency
    }

    override fun areItemsTheSame(p0: Currency, p1: Currency) = p0.country == p1.country


    override fun areContentsTheSame(p0: Currency, p1: Currency) =
        if (p1.country == changedRate?.country) {
            true
        } else {
            p0.amount == p1.amount
        }


    override fun getChangePayload(oldItem: Currency, newItem: Currency): Any? {
        val diff = Bundle()
        diff.putParcelable(RATE, newItem);
        return diff
    }
}
