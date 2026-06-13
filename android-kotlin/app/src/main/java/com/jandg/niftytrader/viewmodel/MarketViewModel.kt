package com.jandg.niftytrader.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jandg.niftytrader.data.MarketApi
import com.jandg.niftytrader.data.MarketData
import com.jandg.niftytrader.logic.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarketUiState(
    val market: MarketData? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val phase: MarketPhase = MarketPhase.CLOSED,
    val isThursday: Boolean = false,
    val gates: List<Gate> = emptyList(),
    val strategy: Strategy = Strategy.SKIP,
    val tradeCalc: TradeCalc? = null,
    val verdict: TradeVerdict = TradeVerdict.NO_GO,
    val ppSignal: PpSignal? = null,
    val lotSize: Int = 65,
    val lastRefreshed: String = "",
)

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val api: MarketApi,
) : ViewModel() {

    private val _state = MutableStateFlow(MarketUiState())
    val state: StateFlow<MarketUiState> = _state.asStateFlow()

    init {
        refresh()
        startAutoRefresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val data = api.getMarket()
                if (data.error != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = data.message ?: "Market data unavailable"
                    )
                    return@launch
                }
                analyzeAndUpdate(data)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Cannot reach Railway server — check connection\n${e.message}"
                )
            }
        }
    }

    private fun analyzeAndUpdate(data: MarketData) {
        val phase      = TimeEngine.marketPhase()
        val isThursday = TimeEngine.isThursday()
        val strategy   = StrategyEngine.select(data.vix)
        val lotSize    = _state.value.lotSize

        val calc = StrikeCalc.calculate(
            strategy     = strategy,
            nifty        = data.nifty,
            vix          = data.vix,
            lotSize      = lotSize,
            expiry       = data.bestExpiry,
            expiryLabel  = data.expiryLabel,
            daysToExpiry = data.daysToExpiry,
        )

        val gates = GateEngine.run(
            phase        = phase,
            isThursday   = isThursday,
            gapPct       = data.gapPct,
            daysToExpiry = data.daysToExpiry,
            vixDirection = data.vixDirection,
        )

        val verdict  = GateEngine.verdict(gates)
        val ppSignal = StrategyEngine.ppSignal(data.vix)

        _state.value = _state.value.copy(
            market       = data,
            isLoading    = false,
            error        = null,
            phase        = phase,
            isThursday   = isThursday,
            gates        = gates,
            strategy     = strategy,
            tradeCalc    = calc,
            verdict      = verdict,
            ppSignal     = ppSignal,
            lastRefreshed = data.timestamp.take(19).replace("T", " "),
        )
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(5 * 60 * 1000L) // 5 minutes
                val phase = TimeEngine.marketPhase()
                if (phase == MarketPhase.ENTRY || phase == MarketPhase.WAIT || phase == MarketPhase.PRE) {
                    refresh()
                }
            }
        }
    }

    fun setLotSize(size: Int) {
        _state.value = _state.value.copy(lotSize = size)
        _state.value.market?.let { analyzeAndUpdate(it) }
    }
}
