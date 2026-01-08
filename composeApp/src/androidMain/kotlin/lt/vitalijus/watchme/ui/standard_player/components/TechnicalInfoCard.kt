package lt.vitalijus.watchme.ui.standard_player.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.watchme.domain.model.Video
import lt.vitalijus.watchme.ui.util.formatDuration

@Composable
fun TechnicalInfoCard(
    video: Video,
    quality: String,
    duration: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(all = 16.dp)
        ) {
            Text(
                text = "📊 Technical Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(height = 8.dp))
            InfoRow(
                label = "Format",
                value = if (video.videoUrl.contains(".m3u8")) "HLS" else "DASH"
            )
            InfoRow(
                label = "Quality",
                value = quality
            )

            // Duration with loading state
            if (duration > 0L) {
                InfoRow(
                    label = "Duration",
                    value = formatDuration(duration / 1000)
                )
            } else {
                InfoRowWithLoading(label = "Duration")
            }

            InfoRow(
                label = "DRM",
                value = if (video.hasDrm) "✓ Widevine" else "None"
            )
            InfoRow(
                label = "LAR Enabled",
                value = if (video.hasAds) "✓ Yes" else "No"
            )
        }
    }
}

@Composable
fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun InfoRowWithLoading(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )
        CircularProgressIndicator(
            modifier = Modifier.size(size = 12.dp),
            strokeWidth = 1.dp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}
