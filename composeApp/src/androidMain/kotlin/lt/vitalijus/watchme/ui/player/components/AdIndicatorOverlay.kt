package lt.vitalijus.watchme.ui.player.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.watchme.streaming.AdPod
import lt.vitalijus.watchme.streaming.LinearAdReplacementManager

@Composable
fun AdIndicatorOverlay(
    adPod: AdPod,
    currentPosition: Long
) {
    val positionInPod = currentPosition - adPod.startPosition
    val currentAd = LinearAdReplacementManager.getCurrentAd(positionInPod = positionInPod)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        Card(
            modifier = Modifier.align(Alignment.TopEnd),
            shape = RoundedCornerShape(size = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(color = 0xFFF57C00).copy(alpha = 0.9f)
            )
        ) {
            Column(
                modifier = Modifier.padding(all = 12.dp)
            ) {
                Text(
                    text = "📺 AD",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                currentAd?.let { ad ->
                    Text(
                        text = ad.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}
