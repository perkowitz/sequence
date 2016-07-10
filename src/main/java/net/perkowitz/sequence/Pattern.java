package net.perkowitz.sequence;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Pattern {

    @Getter @Setter private static int trackCount = 16;
    @Getter private Track[] tracks;

    public Pattern() {

        this.tracks = new Track[trackCount];
        for (int i = 0; i < trackCount; i++) {
            tracks[i] = new Track();
        }

    }

    public Track getTrack(int index) {
        return tracks[index % trackCount];
    }


}
