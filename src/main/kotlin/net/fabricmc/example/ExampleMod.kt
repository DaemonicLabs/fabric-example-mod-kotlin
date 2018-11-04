package net.fabricmc.example

import javafx.application.Application.launch
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.fabricmc.api.ModInitializer
import org.apache.logging.log4j.LogManager

object ExampleMod : ModInitializer {

    val logger = LogManager.getLogger("KotlinExample")

    override fun onInitialize() = runBlocking {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        logger.info("Hello Fabric world!")

        val prev = System.setProperty("kotlinx.coroutines.debug", "")
        logger.debug("'kotlinx.coroutines.debug' prev: $prev")

        val channel = Channel<Int>()
        launch(CoroutineName("printer")) {
            for (k in channel) {
                logger.info("received: $k")
            }
        }
        launch(CoroutineName("sender")) {
            for (i in (0 until 10)) {
                delay(100)
                channel.send(i)
            }
            channel.close()
        }
        logger.info("KotlinExample loaded")
    }
}
