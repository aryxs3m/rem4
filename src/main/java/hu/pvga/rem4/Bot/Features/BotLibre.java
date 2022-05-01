/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Config.BotLibreConfig;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.FeatureSet;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * BotLibre Feature
 *
 * This feature can connect your BotLibre AI bot to a channel. This way your users can talk to the bot.
 *
 * Configuration: BotLibre application key, instance key, username and password, and a channel id
 */
public class BotLibre extends BaseFeature {
    private final BotLibreConfig botLibreConfig;
    private boolean initialized = false;
    private String conversation;

    public BotLibre() throws IOException {
        botLibreConfig = ConfigManager.load(BotLibreConfig.class);
    }

    @Override
    public void onJDAInit() {
        try {
            // First message will create a conversation id. We need to use this later.
            String initResponse = FeatureSet.getHTTP(
                    "https://www.botlibre.com/rest/api/form-chat?&application=" + botLibreConfig.getApplicationId() +
                            "&instance=" + botLibreConfig.getInstanceId() +
                            "&user=" + botLibreConfig.getUsername() +
                            "&password="+ botLibreConfig.getPassword() +
                            "&message=hello");

            DocumentBuilderFactory dbf =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(initResponse));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("response");

            this.conversation = nodes.item(0).getAttributes().getNamedItem("conversation").getTextContent();
            this.initialized = true;

            logger.info("BotLibre initialized. Conversation ID: " + this.conversation);
        } catch (IOException e) {
            logger.error("Could not init BotLibre feature", e);
        } catch (ParserConfigurationException | SAXException e) {
            logger.error("Could not parse BotLibre init response", e);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!initialized || event.getMessage().getAuthor().isBot()) {
            return;
        }

        if (event.getChannel().getId().equals(botLibreConfig.getChannelId())) {
            try {
                replyToMessage(event);
            } catch (IOException e) {
                logger.error("IOException while replying with AI", e);
            } catch (ParserConfigurationException | SAXException e) {
                logger.error("Parse error while replying with AI", e);
            }
        }
    }

    /**
     * Sends the message to your BotLibre bot, and responds with the bots reply
     * @param event
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private void replyToMessage(MessageReceivedEvent event) throws IOException, ParserConfigurationException, SAXException {
        String message = event.getMessage().getContentDisplay();

        // If you don't want to send a message to the bot, send it as a quote
        if (message.trim().startsWith(">")) {
            return;
        }

        // If you reply to a message from the bot, it will be sent with the correction flag
        String isCorrection = "false";
        if (event.getMessage().getReferencedMessage() != null) {
            if (event.getMessage().getReferencedMessage().getAuthor().getId().equals(getJDA().getSelfUser().getId())) {
                isCorrection = "true";
            }
        }

        String response = FeatureSet.getHTTP(
                "https://www.botlibre.com/rest/api/form-chat?&application=" + botLibreConfig.getApplicationId() +
                        "&instance=" + botLibreConfig.getInstanceId() +
                        "&conversation=" + this.conversation +
                        "&user=" + botLibreConfig.getUsername() +
                        "&password="+ botLibreConfig.getPassword() +
                        "&correction=" + isCorrection +
                        "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8.toString()));

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(response));

        Document doc = db.parse(is);
        NodeList nodes = doc.getElementsByTagName("response");

        event.getChannel().sendMessage(
                nodes.item(0).getTextContent()
        ).queue();
    }
}
