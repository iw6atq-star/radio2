package com.temporeale.radioapp

// Struttura dati per definire una singola radio
data class RadioStation(
    val name: String, 
    val streamUrl: String
)

// Lista fissa e definitiva delle tue 4 radio con flussi audio diretti
val radioList = listOf(
    // Aggiunto il suffisso /; o /stream se richiesto da Asurahosting per agganciare il flusso sorgente Icecast
    RadioStation("Discovery 2 Radio", "https://cast5.asurahosting.com/proxy/giancar1/stream"),
    
    // Per Laut.fm l'indirizzo diretto del flusso audio MP3 nativo è /wolf-one (senza stream. iniziale o con estensione diretta)
    RadioStation("Wolf 1 Radio", "http://stream.laut.fm/wolf-one"),
    
    // Configurazione proxy corretta per l'ascolto diretto sui player Android
    RadioStation("Radio Solaris", "https://cast6.asurahosting.com/proxy/radiosolaris/stream"),
    
    // Porta e flusso dedicati per agganciare il server pioneer01
    RadioStation("Laika 2 Radio", "https://fra-pioneer01.dedicateware.com:3155/stream")
