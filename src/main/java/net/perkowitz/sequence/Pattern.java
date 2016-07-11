package net.perkowitz.sequence;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Pattern {

    // lay out the note numbers across the tracks like a keyboard octave
    private static int[] noteNumbers = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
                                                   36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter @Setter private static int trackCount = 16;
    @Getter private Track[] tracks;

    public Pattern() {

        this.tracks = new Track[trackCount];
        for (int i = 0; i < trackCount; i++) {
            tracks[i] = new Track(i);
            tracks[i].setMidiChannel(9);
            tracks[i].setNoteNumber(noteNumbers[i]);
        }

    }

    public Track getTrack(int index) {
        return tracks[index % trackCount];
    }


}
