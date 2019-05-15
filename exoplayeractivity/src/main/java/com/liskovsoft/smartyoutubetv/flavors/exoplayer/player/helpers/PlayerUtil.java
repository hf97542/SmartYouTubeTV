/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.helpers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.MimeTypes;
import com.liskovsoft.exoplayeractivity.R;
import com.liskovsoft.smartyoutubetv.flavors.exoplayer.player.ExoPreferences;

import java.util.Locale;

/**
 * Utility methods for demo application.
 */
/*package*/ public final class PlayerUtil {

    private PlayerUtil() {
    }

    /**
     * Builds a track name for display.
     *
     * @param format {@link Format} of the track.
     * @return a generated name specific to the track.
     */
    public static String buildTrackName(Format format) {
        String trackName;
        if (MimeTypes.isVideo(format.sampleMimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(joinWithSeparator(joinWithSeparator(buildResolutionString(format),
                    buildFPSString(format)), buildBitrateString(format)), buildTrackIdString(format)), buildCodecTypeString(format)), buildHDRString(format));
        } else if (MimeTypes.isAudio(format.sampleMimeType)) {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format),
                    buildAudioPropertyString(format)), buildBitrateString(format)), buildTrackIdString(format)), buildCodecTypeString(format));
        } else {
            trackName = joinWithSeparator(joinWithSeparator(joinWithSeparator(buildLanguageString(format), buildBitrateString(format)),
                    buildTrackIdString(format)), buildSampleMimeTypeString(format));
        }
        return trackName.length() == 0 ? "unknown" : trackName;
    }

    private static String buildCodecTypeString(Format format) {
        if (format.sampleMimeType == null ||
            format.codecs == null)
            return "";
        String prefix = format.sampleMimeType.split("/")[0];
        return String.format("%s/%s", prefix, format.codecs);
    }

    private static String buildHDRString(Format format) {
        return format.codecs.equals("vp9.2") ? "HDR" : "";
    }

    private static String buildFPSString(Format format) {
        return format.frameRate == Format.NO_VALUE ? "" : format.frameRate + "fps";
    }

    private static String buildResolutionString(Format format) {
        return format.width == Format.NO_VALUE || format.height == Format.NO_VALUE ? "" : format.width + "x" + format.height;
    }

    private static String buildAudioPropertyString(Format format) {
        return format.channelCount == Format.NO_VALUE || format.sampleRate == Format.NO_VALUE ? "" : format.channelCount + "ch, " + format
                .sampleRate + "Hz";
    }

    private static String buildLanguageString(Format format) {
        return TextUtils.isEmpty(format.language) || "und".equals(format.language) ? "" : format.language;
    }

    private static String buildBitrateString(Format format) {
        return format.bitrate == Format.NO_VALUE ? "" : String.format(Locale.US, "%.2fMbit", format.bitrate / 1000000f);
    }

    private static String joinWithSeparator(String first, String second) {
        return first.length() == 0 ? second : (second.length() == 0 ? first : first + ", " + second);
    }

    private static String buildTrackIdString(Format format) {
        return format.id == null ? "" : ("id:" + format.id);
    }

    private static String buildSampleMimeTypeString(Format format) {
        return format.sampleMimeType == null ? "" : format.sampleMimeType;
    }

    /**
     * Test format against user preferred one (selected in bootstrap)
     * @param format format
     * @return is test passed
     */
    public static boolean isPreferredFormat(Context ctx, Format format) {
        if (notAVideo(format)) {
            return true;
        }

        ExoPreferences prefs = ExoPreferences.instance(ctx);
        String codecAndHeight = prefs.getPreferredCodec();
        if (codecAndHeight.isEmpty()) { // all formats are preferred
            return true;
        }

        String[] split = codecAndHeight.split("\\|");
        String codec = split[0];
        String height = split[1];
        if (format.codecs.contains(codec) &&
                format.height <= Integer.parseInt(height)) {
            return true;
        }

        return false;
    }

    private static boolean notAVideo(Format format) {
        return format.height == -1;
    }

    public static Uri convertToFullUrl(String videoId) {
        String url = String.format("https://www.youtube.com/watch?v=%s", videoId);
        return Uri.parse(url);
    }

    @TargetApi(17)
    public static void showMultiChooser(Context context, Uri url) {
        Intent primaryIntent = new Intent(Intent.ACTION_VIEW);
        Intent secondaryIntent = new Intent(Intent.ACTION_SEND);
        primaryIntent.setData(url);
        secondaryIntent.putExtra(Intent.EXTRA_TEXT, url.toString());
        secondaryIntent.setType("text/plain");
        Intent chooserIntent = Intent.createChooser(primaryIntent, context.getResources().getText(R.string.send_to));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { secondaryIntent });
        chooserIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.startActivity(chooserIntent);
    }
}
