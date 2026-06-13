package com.jandg.niftytrader.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jandg.niftytrader.ui.components.NiftyCard
import com.jandg.niftytrader.ui.theme.*

@Composable
fun SettingsScreen(
    apiKey: String,
    apiUrl: String,
    lotSize: Int,
    portfolio: Int,
    paperMode: Boolean,
    onApiKeyChange: (String) -> Unit,
    onApiUrlChange: (String) -> Unit,
    onLotSizeChange: (Int) -> Unit,
    onPortfolioChange: (Int) -> Unit,
    onPaperModeChange: (Boolean) -> Unit,
    onSeedHistorical: () -> Unit,
) {
    val scroll = rememberScrollState()

    Column(
        Modifier.fillMaxSize().background(BgDeep).verticalScroll(scroll).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("SETTINGS", fontFamily = Syne, fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp, color = TextPrimary)

        // API Config
        NiftyCard {
            SettingSectionHeader("🔗 API CONFIGURATION")
            Spacer(Modifier.height(10.dp))
            SettingTextField(
                label = "Railway API URL",
                value = apiUrl,
                hint = "https://niftytrader-api.up.railway.app/",
                onChange = onApiUrlChange,
            )
            Spacer(Modifier.height(8.dp))
            SettingTextField(
                label = "Claude API Key (optional — for AI tab)",
                value = apiKey,
                hint = "sk-ant-...",
                onChange = onApiKeyChange,
                password = true,
            )
        }

        // Trading config
        NiftyCard {
            SettingSectionHeader("📊 TRADING PARAMETERS")
            Spacer(Modifier.height(10.dp))
            SettingRow(label = "Lot Size (shares)", sub = "Nifty 50 = 65") {
                var text by remember { mutableStateOf(lotSize.toString()) }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; it.toIntOrNull()?.let { v -> onLotSizeChange(v) } },
                    modifier = Modifier.width(100.dp),
                    textStyle = LocalTextStyle.current.copy(fontFamily = SpaceMono, fontSize = 13.sp),
                    colors = settingFieldColors(),
                    singleLine = true,
                )
            }
            Spacer(Modifier.height(8.dp))
            SettingRow(label = "Portfolio Value (₹)", sub = "For PP coverage calculation") {
                var text by remember { mutableStateOf(portfolio.toString()) }
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it; it.toIntOrNull()?.let { v -> onPortfolioChange(v) } },
                    modifier = Modifier.width(130.dp),
                    textStyle = LocalTextStyle.current.copy(fontFamily = SpaceMono, fontSize = 13.sp),
                    colors = settingFieldColors(),
                    singleLine = true,
                )
            }
        }

        // Mode toggle
        NiftyCard {
            SettingSectionHeader("🎯 TRADING MODE")
            Spacer(Modifier.height(10.dp))
            SettingRow(label = "Paper Trade Mode", sub = "Disable for live trading") {
                Switch(
                    checked = paperMode,
                    onCheckedChange = onPaperModeChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = BgDeep, checkedTrackColor = Cyan),
                )
            }
            if (paperMode) {
                Spacer(Modifier.height(6.dp))
                Text("📝 PAPER MODE — All trades are simulated",
                    fontFamily = SpaceMono, fontSize = 10.sp, color = Gold)
            }
        }

        // Data management
        NiftyCard {
            SettingSectionHeader("💾 DATA MANAGEMENT")
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onSeedHistorical,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(8.dp),
            ) { Text("📥 Seed Historical Trades (May 2026)", fontFamily = Syne, fontWeight = FontWeight.Bold) }
        }

        // Rules reminder
        NiftyCard {
            SettingSectionHeader("📜 HARD TRADING RULES")
            Spacer(Modifier.height(10.dp))
            val rules = listOf(
                "❌ Never enter before 9:00 AM Dubai",
                "❌ Never buy options when VIX > 17",
                "❌ Never pay > ₹150 per strangle leg",
                "❌ Never hold into weekend",
                "❌ Never sell ITM options",
                "✅ Always exit at 50% profit",
                "✅ Always use 2× credit as stop loss",
                "✅ Sensibull must show \"GET\" for IC/BPS",
                "✅ PP: buy when VIX LOW (< 16)",
            )
            rules.forEach { rule ->
                Text(rule, fontFamily = SpaceMono, fontSize = 11.sp,
                    color = if (rule.startsWith("✅")) Green else Red,
                    modifier = Modifier.padding(vertical = 2.dp))
            }
        }

        // Version
        Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Text("NiftyTrader v1.0 — Juli's NSE Trading System\nJandG Investment | Dubai GST",
                fontFamily = SpaceMono, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(title, fontFamily = Syne, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextSecondary)
}

@Composable
fun SettingRow(label: String, sub: String, control: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, fontFamily = Syne, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
            Text(sub, fontFamily = SpaceMono, fontSize = 10.sp, color = TextMuted)
        }
        control()
    }
}

@Composable
fun SettingTextField(label: String, value: String, hint: String, onChange: (String) -> Unit, password: Boolean = false) {
    Column {
        Text(label, fontFamily = SpaceMono, fontSize = 10.sp, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            placeholder = { Text(hint, fontSize = 11.sp, color = TextMuted, fontFamily = SpaceMono) },
            modifier = Modifier.fillMaxWidth(),
            colors = settingFieldColors(),
            textStyle = LocalTextStyle.current.copy(fontFamily = SpaceMono, fontSize = 12.sp),
            singleLine = true,
            visualTransformation = if (password && value.isNotEmpty())
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            else androidx.compose.ui.text.input.VisualTransformation.None,
        )
    }
}

@Composable
fun settingFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Cyan,
    unfocusedBorderColor = BgBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = Cyan,
)
