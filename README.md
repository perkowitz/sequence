# Sequence

Sequence is a MIDI sequencer designed to run on a headless computer (e.g. a Raspberry Pi), using a USB MIDI controller 
(e.g. a Novation Launchpad) for the UI. It is being developed on a Raspberry Pi and Launchpad (original, not an S, RGB, or Pro)
but should be adaptable to other hardware.

[Wiki Home](https://github.com/perkowitz/sequence/wiki)

[Getting Started](https://github.com/perkowitz/sequence/wiki/Getting%20Started)

[User Manual](https://github.com/perkowitz/sequence/wiki/User%20Manual)

[Feature and bug tracking](https://github.com/perkowitz/sequence/issues)

## Versions and status

### Version 1.0

####
- Released 9/9/2016
- Added support for Launchpad Pro
- Some refactoring of Sequencer, RunSequencer, other classes to support both Launchpads
- [List open V1.1 issues](https://github.com/perkowitz/sequence/issues?q=is%3Aopen+is%3Aissue+milestone%3AV1.1)


### Version 1.0

####
- Released 9/4/2016
- Files, sessions, patterns, fills, tracks, steps all working properly
- Internal, MIDI clock, MIDI trigger modes all working
- Commit: [7d05816573c6079bcf51220c17ec67ba735f2ca9]
(https://github.com/perkowitz/sequence/commit/7d05816573c6079bcf51220c17ec67ba735f2ca9)


#### Issues/missing
- Limited tempo range when using internal clock
- Occasional issue where won't play on startup; select any track to continue normally
- Can't change MIDI send/trigger channels or note numbers
- Some issues with selecting and playing patterns, especially when 
  restarting a previously-saved session.
- [List open V1 issues](https://github.com/perkowitz/sequence/issues?q=is%3Aopen+is%3Aissue+milestone%3AV1)

