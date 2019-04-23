/**
 * Copyright (c) 2019-present, AlanWang4523 (alanwang4523@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alanwang.aav.alvideoeditor.core;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: AlanWang4523.
 * Date: 19/4/15 01:46.
 * Mail: alanwang4523@gmail.com
 */
public class AWMp4ParserHelper {
    private final static String PREFIX_VIDEO_HANDLER = "vide";
    private final static String PREFIX_AUDIO_HANDLER = "soun";

    /**
     * 合并视频
     * @param inputVideos
     * @param outputPath
     * @throws IOException
     */
    public static void mergeVideos(List<String> inputVideos, String outputPath) throws IOException {
        List<Movie> inputMovies = new ArrayList<>();
        for (String input : inputVideos) {
            inputMovies.add(MovieCreator.build(input));
        }

        List<Track> videoTracks = new LinkedList<>();
        List<Track> audioTracks = new LinkedList<>();

        for (Movie m : inputMovies) {
            for (Track t : m.getTracks()) {
                if (PREFIX_AUDIO_HANDLER.equals(t.getHandler())) {
                    audioTracks.add(t);
                }
                if (PREFIX_VIDEO_HANDLER.equals(t.getHandler())) {
                    videoTracks.add(t);
                }
            }
        }

        Movie outputMovie = new Movie();
        if (audioTracks.size() > 0) {
            outputMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            outputMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }

        Container out = new DefaultMp4Builder().build(outputMovie);

        FileChannel fc = new RandomAccessFile(outputPath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
    }
}
