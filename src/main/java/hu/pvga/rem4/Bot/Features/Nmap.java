/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Bot.Exceptions.RequiredParameterException;
import hu.pvga.rem4.Bot.Extends.BotEmbedBuilder;
import hu.pvga.rem4.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Nmap Feature
 *
 * Simple nmap search, a basic TCP portscan.
 */
public class Nmap extends BaseFeature {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (isCommand(event, "nmap")) {
            try {
                String args[] = getCommandParameters(event, 1);
                if (!args[0].matches("[a-zA-Z0-9.\\-]*")) {
                    event.getChannel().sendMessage(Main.localization.get("nmap_invalid_address")).queue();
                    return;
                }

                NmapThread nmapThread = new NmapThread(args[0], event);
                nmapThread.start();
            } catch (RequiredParameterException e) {
                event.getChannel().sendMessage(Main.localization.get("nmap_parameters")).queue();
            }
        }
    }

    /**
     * We want to run this on a separate thread to avoid blocking.
     */
    public static class NmapThread extends Thread {
        String address;
        MessageReceivedEvent event;

        NmapThread(String address, MessageReceivedEvent event) {
            this.address = address;
            this.event = event;
        }

        public void run() {
            try {
                Runtime rt = Runtime.getRuntime();
                String[] commands = {"nmap", address};
                Process proc = rt.exec(commands);

                BufferedReader stdInput = new BufferedReader(new
                        InputStreamReader(proc.getInputStream()));

                BufferedReader stdError = new BufferedReader(
                        new InputStreamReader(proc.getErrorStream()));

                String line;
                ArrayList<String> openTcpPorts = new ArrayList<>();
                boolean error = false;

                while ((line = stdError.readLine()) != null) {
                    if (line.contains("Failed to resolve")) {
                        event.getChannel()
                            .sendMessage(Main.localization.get("nmap_failed_resolve"))
                            .queue();
                        error = true;
                        break;
                    }
                }

                while ((line = stdInput.readLine()) != null) {
                    if (line.contains("Host seems down.")) {
                        event.getChannel()
                                .sendMessage(Main.localization.get("nmap_host_down"))
                                .queue();
                        error = true;
                        break;
                    }

                    if (line.contains("/tcp") && line.contains("open")) {
                        openTcpPorts.add(line.split("/")[0]);
                    }
                }

                stdInput.close();
                proc.destroy();

                if (!error) {
                    BotEmbedBuilder embedBuilder = new BotEmbedBuilder();
                    embedBuilder.setTitle(Main.localization.get("nmap_tcp_portscan"));
                    embedBuilder.setDescription(
                            MessageFormat.format(
                                    Main.localization.get("nmap_target"),
                                    address
                            )
                    );
                    embedBuilder.setAuthor(
                            "Nmap",
                            "https://nmap.org/",
                            "https://nmap.org/shared/images/tiny-eyeicon.png"
                    );

                    for (String port: openTcpPorts) {
                        embedBuilder.addField(
                                Main.localization.get("nmap_tcp"),
                                port,
                                true
                        );
                    }

                    event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();
                }

            } catch (Exception ex) {
                event.getChannel().sendMessage(Main.localization.get("error_unknown")).queue();
            }
        }

    }
}
