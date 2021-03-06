/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.SynonymConfig;
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
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * Synonym Feature
 *
 * This feature uses poet.hu's (hungarian poem/poetry website) API to find synonyms to words. Hungarian only.
 *
 * Configuration: poet.hu API username/password
 */
public class Synonym extends BaseFeature {
    private final SynonymConfig synonymConfig;

    public Synonym() throws IOException {
        synonymConfig = ConfigManager.load(SynonymConfig.class);
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "synonym")) {
            try {
                String[] params = getCommandParameters(event, 1);

                String xmlRecords = FeatureSet.getHTTP(
                "https://api.poet.hu/szinonima.php?f="+synonymConfig.getUsername()
                        +"&j="+synonymConfig.getPassword()
                        +"&s="+URLEncoder.encode(params[0])
                );

                // TODO: refactor legacy code

                DocumentBuilderFactory dbf =
                        DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xmlRecords));

                Document doc = db.parse(is);
                NodeList nodes = doc.getElementsByTagName("szocsoport");

                int curn = 0;
                int max = 5;
                String szinonimak = "";

                Element element = (Element) nodes.item(0);
                NodeList name = element.getElementsByTagName("szinonima");

                BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                embedBuilder.setTitle(
                        MessageFormat.format(
                                Main.localization.get("synonyms_for"),
                                params[0]
                        ));
                embedBuilder.setAuthor(
                        "Poet.hu",
                        "https://poet.hu/",
                        "https://poet.hu/favicon.png"
                );

                for (int i = 0; i<name.getLength(); i++) {
                    if (curn < max) {
                        Element line = (Element) name.item(i);

                        embedBuilder.addField(new MessageEmbed.Field(
                                MessageFormat.format(
                                        Main.localization.get("synonym_synonym"),
                                        curn + 1
                                ),
                                getCharacterDataFromElement(line),
                                true
                        ));

                        curn++;
                    } else {
                        break;
                    }
                }

                event.getChannel().sendMessageEmbeds(
                        embedBuilder.build()
                ).queue();
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage(Main.localization.get("synonym_parameters")).queue();
            } catch (IOException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_reach_api")).queue();
                logger.error("Cannot reach poet.hu API");
            } catch (ParserConfigurationException | SAXException e) {
                event.getChannel().sendMessage(Main.localization.get("error_cannot_parse_api_response")).queue();
                logger.error("Cannot parse poet.hu API response");
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
