# Rem v4

[PVGA](https://pvga.hu) multipurpose Discord Bot.

Rem was originally an IRC bot that later got converted to a Discord bot. I got tired of its 
legacy codebase and fully rewritten it with better practices and ease of extendability 
in mind.

## Features

### Discord Commands / Features

| Feature     | Description                                                 |
|-------------|-------------------------------------------------------------|
| Citatum     | [citatum.hu](https://citatum.hu) quotes                     |
| Currency    | currency changer                                            |
| Dice        | throwing with a standard 6 sided dice                       |
| Events      | schedule events and announce them                           |
| Gold        | simple alternative to bash.org                              |
| Meme        | quick way to create an "always has been" meme               |
| Nmap        | quick TCP portscan                                          |
| Stopwatch   | simple stopwatch                                            |
| Uptime      | shows server uptime                                         |
| UptimeRobot | shows your UptimeRobot monitors all time availability       |
| Weather     | weather from OpenWeatherMap                                 |
| Synonym     | finds synonyms to a word (using [poet.hu](https://poet.hu)) |
| WordCounter | counts predefined words in messages                         |
| RoleAdder   | gives newcomers a role                                      |
| BotLibre    | connect your BotLibre bot to a channel                      |

Every feature can be disabled and most of them are configurable.

### Web API

You can enable a simple JSON API. At the time some basic endpoints are available:

| Endpoint    | Description                                     |
|-------------|-------------------------------------------------|
| /status     | Basic status info: version string and RAM usage |
| /events     | **Events** event list                           |
| /wordcounts | **WordCounter** all wordcounts                  |
