package com.example.revolut.list


import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import com.example.revolut.ExtendedBaseRobot
import com.example.revolut.R
import com.example.revolut.data.Currency
import com.example.revolut.setupKoinModule
import com.example.revolut.stopKoin
import com.example.revolut.toFormattedString
import org.koin.androidx.viewmodel.dsl.viewModel
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.BDDMockito.times
import org.mockito.Mockito.mock

class ListFragmentUIRobot : ExtendedBaseRobot() {
    private val viewModel: ListViewModel = mocksViewModel()
    private lateinit var scenario: FragmentScenario<ListFragment>
    private val currencyList = MutableLiveData<ArrayList<Currency>>()
    val recyclerViewId = R.id.currencyList
    override fun setupInjections() {
        setupKoinModule {
            viewModel {
                viewModel
            }
        }
    }

    override fun stopDependencyInjection() {
        stopKoin()
    }

    override fun setupScenario() {
        scenario = setupFragmentScenario(theme = R.style.AppTheme)
    }


    private fun mocksViewModel(): ListViewModel {
        val viewModel = mock(ListViewModel::class.java)
        given(viewModel.currencyList).will { currencyList }
        return viewModel
    }

    fun setUpList(list: ArrayList<Currency>) {
        currencyList.postValue(list)
    }

    fun clickItem(position: Int) {
        clickOnItemInRecyclerView(recyclerViewId, position)
    }

    fun editItem(position: Int, amount: String) {
        editTextInsideRecyclerView(
            recyclerViewId,
            position,
            R.id.amountText,
            amount
        )
    }

    fun checkListVisible(list: ArrayList<Currency>) {
        checkRecyclerViewNumberOfEntries(recyclerViewId, list.size)
    }

    fun checkTextVisible(list: ArrayList<Currency>) {
        for (i in 0 until list.size) {
            checkTextInsideRecyclerView(
                recyclerViewId, i,
                R.id.nameText, list[i].country
            )
        }
    }

    fun checkEditTextVisible(list: ArrayList<Currency>) {
        for (i in 0 until list.size) {
            checkTextInsideRecyclerView(
                recyclerViewId,
                i,
                R.id.amountText,
                list[i].amount.toFormattedString()
            )
        }
    }

    fun verifyItemClickedCalled(position: Int) {
        then(viewModel).should().onItemClicked(currencyList.value!![position])
    }

    fun verifyAmountChangedCalled(position: Int) {
        // Should be called to times because it is also called when edit text gets focus to fix value
        // and prevent override by new network call
        then(viewModel).should(times(2)).amountChanged(currencyList.value!![position])
    }
}