package com.ivanna.oneweather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val isEnglish = selectedLanguage == "English"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = if (isEnglish) "Settings" else "Pengaturan",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = if (isEnglish) "Appearance" else "Tampilan",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(if (isEnglish) "Dark Mode" else "Mode Gelap")

            Switch(
                checked = isDarkMode,
                onCheckedChange = onDarkModeChange
            )
        }

        HorizontalDivider()

        Text(
            text = if (isEnglish) "Language" else "Bahasa",
            style = MaterialTheme.typography.titleMedium
        )

        listOf("Indonesia", "English").forEach { language ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(language)

                RadioButton(
                    selected = selectedLanguage == language,
                    onClick = { onLanguageChange(language) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEnglish) "Back" else "Kembali")
        }
    }
}