# Sequence

Sequence is a MIDI sequencer designed to run on a headless computer (e.g. a Raspberry Pi), using a USB MIDI controller 
(e.g. a Novation Launchpad) for the UI. It is being developed on a Raspberry Pi and Launchpad (original, not an S, RGB, or Pro)
but should be adaptable to other hardware.

[Wiki Home](https://github.com/perkowitz/sequence/wiki)

[Getting Started](https://github.com/perkowitz/sequence/wiki/Getting%20Started)

[User Manual](https://github.com/perkowitz/sequence/wiki/User%20Manual)

## Versions and status

### Version 0.1

#### Features
- It's a usable drum sequencer!
- Files, sessions, patterns, tracks, steps, velocity all working properly
- Fairly stable running from internal clock or in MIDI trigger mode

#### Issues/missing
- No MIDI clock sync
- Limited tempo settings
- Occasional issue where won't play on startup; select any track to continue normally
- Can't change MIDI send/trigger channels or note numbers
- No fill patterns
