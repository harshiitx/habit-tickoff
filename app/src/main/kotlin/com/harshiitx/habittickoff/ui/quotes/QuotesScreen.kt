package com.harshiitx.habittickoff.ui.quotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harshiitx.habittickoff.domain.quotes.QUOTES

@Composable
fun QuotesScreen(viewModel: QuotesViewModel) {
    val quoteIndex by viewModel.quoteIndex.collectAsState()
    val quote = QUOTES.getOrElse(quoteIndex) { "" }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = quote,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = { viewModel.shuffle() },
            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
        ) {
            Text("Another one")
        }
    }
}
