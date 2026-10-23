package com.temporeale.radioapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inizializza il lettore streaming audio
        exoPlayer = ExoPlayer.Builder(this).build()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var newsList by remember { mutableStateOf(listOf<NewsItem>()) }
                    var isLoading by remember { mutableStateOf(true) }

                    // Carica le notizie in tempo reale dal canale Telegram "TempoReale24"
                    LaunchedEffect(Unit) {
                        TelegramNewsService.fetchLatestNews("TempoReale24") { result ->
                            newsList = result
                            isLoading = false
                        }
                    }

                    RadioAppScreen(
                        newsList = newsList,
                        isLoading = isLoading,
                        onRadioSelect = { streamUrl ->
                            playRadio(streamUrl)
                        },
                        onStopClick = {
                            exoPlayer?.stop()
                        }
                    )
                }
            }
        }
    }

    private fun playRadio(url: String) {
        exoPlayer?.let { player ->
            player.stop()
            val mediaItem = MediaItem.fromUri(url)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
        exoPlayer = null
    }
}

@Composable
fun RadioAppScreen(
    newsList: List<NewsItem>,
    isLoading: Boolean,
    onRadioSelect: (String) -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🖼️ LOGO IN ALTO 
        AsyncImage(
            model = "https://githubusercontent.com",
            contentDescription = "Logo Radio",
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .padding(bottom = 12.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Radio & News 24",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // 📻 LISTA DEI PULSANTI PER LE RADIO
        Text(
            text = "Seleziona una Radio:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(radioList) { radio ->
                Button(
                    onClick = { onRadioSelect(radio.streamUrl) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = radio.name)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🛑 PULSANTE PER FERMARE L'AUDIO
        Button(
            onClick = { onStopClick() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Spegni Radio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📰 SEZIONE NEWS DA TELEGRAM (TEMPOREALE24)
        Text(
            text = "Ultime News in Tempo Reale:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.2f)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(newsList) { news ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = news.text,
                                modifier = Modifier.padding(10.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
