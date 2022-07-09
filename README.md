![Preview](https://github.com/TeaCondemns/night-skipper-plugin/raw/main/preview.gif)
# Night Skipper [plugin for Minecraft 1.13+]
NightSkipper allows you to skip nights differently!
Players will be able to skip the night with beautiful animation, according to the configuration settings, they will be able to do this either with automatic consideration of their voice, or with consideration of their voice through the use of a command, or if they went to bed.
> **WORKS ONLY WITH SERVERS THAT HAS ONLY ONE WORLD!**

### Commands
`/ns skip`          | `/nightskipper skip`          - command for admins, that let skip night or thunderstorm

`/ns config reset`  | `/nightskipper config reset`  - command for admins, that let reset config values to default

`/ns config reload` | `/nightskipper config reload` - command for admins, that let reload config (apply new config values)

`/ns vote now`      | `/nightskipper vote now`      - command for players, that let vote for skipping night or thunderstorm at the moment

`/ns vote always`   | `/nightskipper vote always`   - command for players, that let always counting vote for skipping night or thunderstorm after using this command

### Settings in `config.yml`

```yml
feature:
  skip:  # if you have disabled both functions, then we recommend that you consider removing this plugin.
    night: true
    thunderstorm: true

  command:             # those commands enabled:
    now-vote: true     # /ns vote now    | /nightskipper vote now
    always-vote: true  # /ns vote always | /nightskipper vote always

  clear-rain: true  # clear rain when night will skipped

  exclude:  # exclude players out of counting with that modes:
    adventure: true
    creative: true
    spectator: true
    vanished: true

  words-list:
    mode: blacklist  # blacklist/whitelist
    words: [minecraft:the_nether, minecraft:the_end]

  animation-frame:
    enabled: true                # skipping night/thunderstorm animation enabled
    night-amplitude: 50          # time increasing into frame when night
    thunderstorm-amplitude: 250  # time increasing into frame when thunder
    frequency: 1                 # animation frequency in server ticks

  reset-phantom-statistic: true  # Treats everyone online as if they have slept in the last 3 days after the night is skipped (check out /gamerule doInsomnia on 1.15+)

condition:       # if condition is true, then night or thunderstorm will skipped (also if one player is sleeping at least)
  op: '>='       # one of operators `>=`, `<=`, `==`, `!=`, `>`, `<`
  lvalue: voted  # variable `voted` online players who voted
  rvalue: 50%    # 50% of online player count

text:  # message texts
  night: night
  thunderstorm: thunderstorm
  and: '&'

  in-progress: '&2Skipping %target% in progress...'
  already-in-progress: '&cSkipping %target% is already in progress!'
  finished: '&2&+%target% skipped!'
  cannot-skip: '&cNow is not night or thunderstorm!'
  goodnight: '&dGood night!'

  always-vote-enabled: '&2%sender%, now your vote will be counted automatically!'
  always-vote-disabled: '&c%sender%, now in order for your voice to be taken into account, you need to use the command `%prefix%%label% vote now` or lie down on the bed'
  vote-taken: '&2%sender%, your vote is taken into account!'
  already-voted: '&c%sender%, your vote has already been counted!'
  cannot-vote: '&c%sender%, you cannot vote now!'

  voted-now: Voted for skipping %voted% out of %players%
  voted-layed-now: Voted for skipping %voted% out of %players% (%sleeping% are sleeping)

  config-reloaded: '&2Config reloaded'
  config-reseted: '&2Config reseted'

  feature-disabled: '&cThis feature is disabled or unavailable!'
  invalid-format: '&cInvalid command format!'

  usage: |-
    &n&lCommand usage:&r
    %prefix%%label% skip
    %prefix%%label% config reset
    %prefix%%label% config reload
    %prefix%%label% vote now
    %prefix%%label% vote always
```

[[Download latest version]](https://github.com/TeaCondemns/night-skipper-plugin/releases/tag/normal-functionality)
