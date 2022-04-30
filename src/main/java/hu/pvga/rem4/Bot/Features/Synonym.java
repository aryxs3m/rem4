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
import hu.pvga.rem4.Config.WeatherConfig;
import hu.pvga.rem4.FeatureSet;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
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
                embedBuilder.setTitle("Synonyms for " + params[0]);
                embedBuilder.setAuthor(
                        "Poet.hu",
                        "https://poet.hu/",
                        "https://poet.hu/favicon.png"
                );

                for (int i = 0; i<name.getLength(); i++) {
                    if (curn < max) {
                        Element line = (Element) name.item(i);

                        embedBuilder.addField(new MessageEmbed.Field(
                                "Synonym " + curn,
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
                event.getChannel().sendMessage("Required parameter: word").queue();
            } catch (IOException e) {
                event.getChannel().sendMessage("Cannot reach poet.hu API.").queue();
            } catch (ParserConfigurationException | SAXException e) {
                event.getChannel().sendMessage("Failed to parse poet.hu API response.").queue();
            }
        }
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }
}
