package lt.vitalijus.watchme.streaming

import androidx.media3.common.C
import androidx.media3.common.Metadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.extractor.metadata.id3.TextInformationFrame
import androidx.media3.extractor.metadata.scte35.SpliceInsertCommand
import androidx.media3.extractor.metadata.scte35.TimeSignalCommand

/**
 * SCTE-35 Handler for parsing and handling SCTE-35 markers in video streams
 *
 * SCTE-35 is the industry standard for signaling ad insertion opportunities
 * in video streams. This handler listens for SCTE-35 metadata and triggers
 * ad break callbacks.
 */
@UnstableApi
class Scte35Handler(
    private val onAdBreakStart: (durationMs: Long, positionMs: Long) -> Unit,
    private val onAdBreakEnd: () -> Unit
) : Player.Listener {

    override fun onMetadata(metadata: Metadata) {
        for (i in 0 until metadata.length()) {
            when (val entry = metadata.get(i)) {
                // SCTE-35 Splice Insert Command (most common for ad breaks)
                is SpliceInsertCommand -> {
                    handleSpliceInsert(command = entry)
                }

                // SCTE-35 Time Signal Command (alternative format)
                is TimeSignalCommand -> {
                    handleTimeSignal(command = entry)
                }

                // HLS ID3 tags (sometimes used for ad markers)
                is TextInformationFrame -> {
                    if (entry.id == "TXXX" && entry.description?.contains(
                            "ad",
                            ignoreCase = true
                        ) == true
                    ) {
                        handleHlsAdMarker(frame = entry)
                    }
                }
            }
        }
    }

    private fun handleSpliceInsert(command: SpliceInsertCommand) {
        println("🎬 SCTE-35 Splice Insert detected:")
        println("  - Splice Event ID: ${command.spliceEventId}")
        println("  - Out of Network: ${command.outOfNetworkIndicator}")

        if (command.programSplicePlaybackPositionUs != C.TIME_UNSET) {
            println("  - Splice Time: ${command.programSplicePlaybackPositionUs / 1000}ms")
        }

        if (command.breakDurationUs != C.TIME_UNSET) {
            println("  - Break Duration: ${command.breakDurationUs / 1000}ms")
        }

        // If this is an ad break start (out of network = going to ads)
        if (command.outOfNetworkIndicator && command.breakDurationUs != C.TIME_UNSET) {
            val durationMs = command.breakDurationUs / 1000
            val positionMs = if (command.programSplicePlaybackPositionUs != C.TIME_UNSET) {
                command.programSplicePlaybackPositionUs / 1000
            } else {
                0L
            }

            println("🎯 Triggering ad break: ${durationMs}ms at position ${positionMs}ms")
            onAdBreakStart(durationMs, positionMs)
        }
        // If returning to content (back to network)
        else if (!command.outOfNetworkIndicator) {
            println("✅ Returning to content")
            onAdBreakEnd()
        }
    }

    private fun handleTimeSignal(command: TimeSignalCommand) {
        println("⏰ SCTE-35 Time Signal detected:")
        if (command.playbackPositionUs != C.TIME_UNSET) {
            println("  - Playback position: ${command.playbackPositionUs / 1000}ms")
        }

        // Time signals often accompany segmentation descriptors
        // In a full implementation, you'd parse those for detailed ad break info
    }

    private fun handleHlsAdMarker(frame: TextInformationFrame) {
        println("📺 HLS Ad Marker detected:")
        println("  - Description: ${frame.description}")
        println("  - Value: ${frame.value}")

        // Some HLS streams use ID3 tags instead of SCTE-35
        // This is common in Apple HLS implementations
    }
}
