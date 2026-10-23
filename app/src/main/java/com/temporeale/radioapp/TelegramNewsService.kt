package com.temporeale.radioapp

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.concurrent.thread

// Struttura dati per contenere una notizia
data class NewsItem(val text: String)

object TelegramNewsService {
    
    // Funzione per scaricare le ultime notizie da un canale Telegram pubblico senza usare API Key
    fun fetchLatestNews(channelName: String, onResult: (List<NewsItem>) -> Unit) {
        thread {
            val newsList = mutableListOf<NewsItem>()
            try {
                // Utilizziamo l'embed pubblico di Telegram per estrarre i testi in tempo reale
                val url = URL("https://t.me")
                val connection = url.openConnection()
                val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
                val html = StringBuilder()
                var line: String?
                
                while (reader.readLine().also { line = it } != null) {
                    html.append(line)
                }
                reader.close()

                // Cerchiamo i blocchi di testo dei messaggi nel codice della pagina
                val htmlString = html.toString()
                var index = 0
                while (true) {
                    // I messaggi di testo nei canali Telegram hanno questa classe HTML specifica
                    index = htmlString.indexOf("class=\"tgme_widget_message_text", index)
                    if (index == -1) break
                    
                    val startPos = htmlString.indexOf(">", index) + 1
                    val endPos = htmlString.indexOf("</div>", startPos)
                    if (endPos != -1) {
                        var rawText = htmlString.substring(startPos, endPos)
                        // Pulizia minima dei tag HTML più comuni (come i link o i ritorni a capo)
                        rawText = rawText.replace("<br/>", "\n")
                        rawText = rawText.replace(Regex("<[^>]*>"), "")
                        
                        if (rawText.isNotBlank()) {
                            newsList.add(NewsItem(rawText.trim()))
                        }
                    }
                    index = endPos
                }
            } catch (e: Exception) {
                e.printStackTrace()
                newsList.add(NewsItem("Impossibile caricare le news in questo momento. Verifica la connessione."))
            }
            
            // Restituiamo la lista invertita per mostrare l'ultimo messaggio in cima
            onResult(newsList.reversed())
        }
    }
}
