package io.ossim.omar.scdf.image.info

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Processor
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.SendTo
import joms.oms.Init

/**
 * Created by slallier on 7/10/2017
 *
 * The OmarScdfImageInfoApplication is a purpose built image info grabber for integration with a full SCDF stack.
 */
@SpringBootApplication
@EnableBinding(Processor.class)
@Slf4j
class OmarScdfImageInfoApplication implements CommandLineRunner
{
    // OSSIM Environment variables

    @Value('${ossim.prefs.file:/usr/share/ossim/ossim-site-preferences}')
    private String ossimPrefsFile

    @Value('${ossim.data:/data}')
    private String ossimData

    /**
     * The main entry point of the SCDF Image Info application.
     * @param args
     */
    static final void main(String[] args)
    {
        SpringApplication.run OmarScdfImageInfoApplication, args
    }

    /**
     * The method that handles the image info request when a filename of a staged image is received
     * @param message the message containing the image filename
     */
    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    final String handleImageInfoRequest(final Message<?> message)
    {
        log.debug("Received message ${message} containing the name of a file to get the image info for")
        boolean metadataCreated = false

        if (null != message.payload)
        {
            // Parse filename from message
            final def parsedJson = new JsonSlurper().parseText(message.payload)
            final String filename = parsedJson.filename

            // generate image info/metadata
            log.debug("Generating image metadata for ${filename}")

            JsonBuilder metadata = generateImageMetadata(filename)
            metadataCreated = true

            // Return filename and result of image info request
            JsonBuilder imageInfo = new JsonBuilder()
            imageInfo(
                    filename : filename,
                    metadataCreated : metadataCreated,
                    metadata: metadata
            )

            log.debug("Sending result to output stream -- ${imageInfo.toString()}")
            return imageInfo.toString()
        }
        else
        {
            log.warn("Received null payload for message: ${message}")
            return null
        }
    }

    /**
    * Method to generate image info given a filename
    * @return String containing Json image metadata
    */
    final private JsonBuilder generateImageMetadata(String filename)
    {
        // generate image metadata
        log.info("Generating image metadata")
        return new JsonBuilder()
    }

    @Override
    void run(String... args) throws Exception {
        log.debug("OSSIM_PREFS_FILE: ${ossimPrefsFile}")
        log.debug("OSSIM_DATA: ${ossimData}")

        String[] newArgs = ["dummy",
                            "--env",
                            "OSSIM_PREFS_FILE=${ossimPrefsFile}",
                            "--env",
                            "OSSIM_DATA=${ossimData}"]

        log.debug("JNI Init arguments remaining: ${Init.instance().initialize(newArgs.size(), newArgs)}")
    }
}
