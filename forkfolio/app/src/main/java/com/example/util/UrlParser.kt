package com.example.util

import java.util.regex.Pattern

object UrlParser {
    private val YOUTUBE_REGEX = Pattern.compile(
        "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/|shorts\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]*).*",
        Pattern.CASE_INSENSITIVE
    )

    fun extractYouTubeVideoId(url: String): String? {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return null

        val matcher = YOUTUBE_REGEX.matcher(trimmed)
        if (matcher.matches()) {
            val id = matcher.group(1)
            if (id != null && id.length in 5..15) return id
        }

        // Alternative manual fallback parsing for shorts, embed, youtu.be, etc.
        return when {
            trimmed.contains("youtu.be/") -> {
                val candidate = trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("&").substringBefore("#")
                if (candidate.isNotEmpty()) candidate else null
            }
            trimmed.contains("/shorts/") -> {
                val candidate = trimmed.substringAfter("/shorts/").substringBefore("?").substringBefore("&").substringBefore("#").substringBefore("/")
                if (candidate.isNotEmpty()) candidate else null
            }
            trimmed.contains("/embed/") -> {
                val candidate = trimmed.substringAfter("/embed/").substringBefore("?").substringBefore("&").substringBefore("#").substringBefore("/")
                if (candidate.isNotEmpty()) candidate else null
            }
            trimmed.contains("v=") -> {
                val candidate = trimmed.substringAfter("v=").substringBefore("&").substringBefore("#")
                if (candidate.isNotEmpty()) candidate else null
            }
            else -> null
        }
    }

    fun isYouTubeShort(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("/shorts/")
    }

    fun isInstagramReel(url: String): Boolean {
        val lower = url.lowercase()
        return lower.contains("/reel/") || lower.contains("/reels/")
    }

    fun detectPlatform(url: String): String {
        val lower = url.lowercase()
        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") -> "YOUTUBE"
            lower.contains("instagram.com") || lower.contains("instagr.am") -> "INSTAGRAM"
            else -> "OTHER"
        }
    }

    fun getYouTubeThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    fun getYouTubeEmbedHtml(videoId: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                    * { margin: 0; padding: 0; box-sizing: border-box; }
                    html, body {
                        width: 100%;
                        height: 100%;
                        background-color: #000000;
                        overflow: hidden;
                    }
                    #player {
                        width: 100%;
                        height: 100%;
                    }
                </style>
            </head>
            <body>
                <div id="player"></div>
                <script>
                    var tag = document.createElement('script');
                    tag.src = "https://www.youtube.com/iframe_api";
                    var firstScriptTag = document.getElementsByTagName('script')[0];
                    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                    var player;
                    function onYouTubeIframeAPIReady() {
                        player = new YT.Player('player', {
                            height: '100%',
                            width: '100%',
                            videoId: '$videoId',
                            playerVars: {
                                'autoplay': 1,
                                'playsinline': 1,
                                'rel': 0,
                                'modestbranding': 1,
                                'fs': 1,
                                'origin': 'https://www.youtube.com'
                            },
                            events: {
                                'onReady': onPlayerReady,
                                'onError': onPlayerError
                            }
                        });
                    }
                    function onPlayerReady(event) {
                        try {
                            event.target.playVideo();
                        } catch (e) {}
                    }
                    function onPlayerError(event) {
                        try {
                            if (window.AndroidBridge && window.AndroidBridge.onVideoError) {
                                window.AndroidBridge.onVideoError(event.data);
                            }
                        } catch (e) {}
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    }
}

