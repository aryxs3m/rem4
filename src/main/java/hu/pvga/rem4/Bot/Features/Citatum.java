/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.CitatumConfig;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.FeatureSet;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Citatum Feature
 *
 * Citatum (https://citatum.hu) is a hungarian quote portal. This feature contains one command that can get a random
 * quote from it.
 *
 * Configuration: citatum.hu API username and password and quote category name
 */
public class Citatum extends BaseFeature {
    private final CitatumConfig citatumConfig;

    public Citatum() throws IOException {
        citatumConfig = ConfigManager.load(CitatumConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "citatum")) {
            try {
                String xmlRecords = FeatureSet.getHTTP(
                        "https://api.citatum.hu/idezet.php?f="+citatumConfig.getUsername()
                        +"&j="+citatumConfig.getPassword()
                        +"&kat="+citatumConfig.getCategory()
                        +"&rendez=veletlen"
                );

                // TODO: refactor legacy code

                DocumentBuilderFactory dbf =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xmlRecords));

                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("idezet");

                Element element = (Element) nodes.item(0);
                NodeList name = element.getElementsByTagName("idezetszoveg");
                Element line = (Element) name.item(0);
                String quoteText = getCharacterDataFromElement(line);

                name = element.getElementsByTagName("szerzo");
                line = (Element) name.item(0);
                String quoteAuthor = getCharacterDataFromElement(line);

                NodeList urlNode = element.getElementsByTagName("url");
                String url = getCharacterDataFromElement((Element) urlNode.item(0));

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(Main.localization.get("citatum_random_quote"));
                embedBuilder.setDescription(quoteText);
                embedBuilder.setAuthor(
                        "Citatum",
                        url,
                        "https://www.citatum.hu/favicon.png"
                );
                embedBuilder.addField(new MessageEmbed.Field(
                        Main.localization.get("citatum_author"),
                        quoteAuthor,
                        true
                ));

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (IOException | ParserConfigurationException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_reach_api")).queue();
                logger.error("Cannot reach citatum.hu API", e);
            } catch (SAXException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_parse_api_response")).queue();
                logger.error("Error while parsing citatum.hu API response", e);
            }
        }
    }

    /**
     * Legacy Rem v3 code. Duplicated too.
     * @todo Change later.
     * @param e
     * @return
     */
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }
}
