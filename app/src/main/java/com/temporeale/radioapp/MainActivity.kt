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
        
        // Inizializza il lettore streaming audio per Android
        exoPlayer = ExoPlayer.Builder(this).build()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RadioAppScreen(
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
fun RadioAppScreen(onRadioSelect: (String) -> Unit, onStopClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🖼️ LOGO IN ALTO (Modificabile inserendo il link diretto al tuo logo reale)
        AsyncImage(
            model = "https://githubusercontent.com",
            contentDescription = "Logo Radio",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = "Radio in Streaming",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 📻 LISTA DEI PULSANTI PER LE RADIO
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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

        Spacer(modifier = Modifier.height(16.dp))

        // 🛑 PULSANTE PER FERMARE L'AUDIO
        Button(
            onClick = { onStopClick() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Spegni Radio")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 📰 SEZIONE NEWS DA COMPLETARE
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Spazio News Temporeale24 / Telegram")
            }
        }
    }
}
