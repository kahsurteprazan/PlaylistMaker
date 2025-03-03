package com.example.playlistmaker

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityAudioPlayerBinding
import java.text.SimpleDateFormat
import java.util.Locale

const val TRACK_KEY = "track"

class AudioPlayerActivity: AppCompatActivity() {
    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val track: Track? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_KEY, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_KEY)
        }


        if (track != null) {
            binding.trackNameAudioPlayer.text = track.trackName
            binding.artistNameAudioPlayer.text = track.artistName
            binding.textViewRightDuration.text = formatTrackTime(track.trackTimeMillis)

            if (track.collectionName.isNotEmpty()) {
                binding.textViewRightAlbum.text = track.collectionName
            } else {
                binding.textViewRightAlbum.isGone = true
                binding.textViewLeftAlbum.isGone = true
            }

            binding.textViewRightYear.text = formatYearFromDate(track.releaseDate)
            binding.textViewRightGenre.text = track.primaryGenreName
            binding.textViewRightCountry.text = track.country


            Glide.with(this)
                .load(track.getCoverArtwork())
                .transform(RoundedCorners(8.toPx()))
                .fitCenter()
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(binding.trackArtAudioPlayer)
        } else {
            finish()
        }


        binding.btnBackAudioPlayer.setOnClickListener {
            finish()
        }
    }
    private fun formatTrackTime(timeMillis: Long): String {
        val minutes = timeMillis / 1000 / 60
        val seconds = (timeMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    fun formatYearFromDate(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return null
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        return yearFormat.format(date)
    }

    fun Int.toPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}