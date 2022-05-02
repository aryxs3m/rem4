/*
 * Copyright (c) 2022.
 * Author: aryxs3m
 * All rights reserved.
 */

package hu.pvga.rem4.Bot.Features;

import hu.pvga.rem4.Config.ConfigManager;
import hu.pvga.rem4.Config.RoleAdderConfig;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * RoleAdder
 *
 * This feature will add a role to new users when them joining your server.
 */
public class RoleAdder extends BaseFeature {
    RoleAdderConfig roleAdderConfig;

    public RoleAdder() throws IOException {
        roleAdderConfig = ConfigManager.load(RoleAdderConfig.class);
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        try {
            event.getGuild().addRoleToMember(
                    UserSnowflake.fromId(event.getMember().getId()),
                    event.getGuild().getRolesByName(roleAdderConfig.getRoleName(), false).get(0)
            ).queue();
            logger.info("Added " + roleAdderConfig.getRoleName() + " role to " + event.getMember().getId());
        } catch (Exception e) {
            logger.error("Cannot add role to new member", e);
        }
    }
}
