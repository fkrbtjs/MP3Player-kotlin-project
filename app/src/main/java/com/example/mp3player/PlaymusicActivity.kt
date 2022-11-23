package com.example.mp3player

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.SearchView
import android.widget.SeekBar
import android.widget.Toast
import com.example.mp3player.databinding.ActivityPlaymusicBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat

class PlaymusicActivity : AppCompatActivity() {
    companion object {
        val ALBUM_SIZE = 80
    }

    private lateinit var binding: ActivityPlaymusicBinding
    lateinit var musicRecyclerAdapter: MusicRecyclerAdapter
    private var playList: MutableList<Parcelable>? = null
    private var position: Int = 0
    private var music: Music? = null
    private var mediaPlayer: MediaPlayer? = null
    private var messengerJob: Job? = null
    private var repeatFlag = false
    private var shuffleFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaymusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인텐트 정보 가져오기
        playList = intent.getParcelableArrayListExtra("playList")
        position = intent.getIntExtra("position", 0)
        music = playList?.get(position) as Music

        //화면에 binding
        bindingMusic()
        startMusic(1)
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.btnRepeat.setOnClickListener {
            if (repeatFlag == false) {
                Toast.makeText(applicationContext, "반복재생 기능을 사용합니다", Toast.LENGTH_SHORT).show()
                repeatFlag = true
                binding.btnRepeat.setImageResource(R.drawable.ic_baseline_repeat_one_24)
            } else {
                Toast.makeText(applicationContext, "반복재생 기능을 해제합니다", Toast.LENGTH_SHORT).show()
                repeatFlag = false
                binding.btnRepeat.setImageResource(R.drawable.ic_baseline_repeat_24)
            }
        }
        binding.btnShuffle.setOnClickListener {
            if (shuffleFlag == false) {
                Toast.makeText(applicationContext, "셔플기능을 사용합니다", Toast.LENGTH_SHORT).show()
                binding.btnShuffle.setImageResource(R.drawable.ic_baseline_shuffle_on_24)
                shuffleFlag = true
            } else {
                Toast.makeText(applicationContext, "셔플기능을 해제합니다", Toast.LENGTH_SHORT).show()
                binding.btnShuffle.setImageResource(R.drawable.ic_baseline_shuffle_24)
                shuffleFlag = false
            }
        }

        binding.listButton.setOnClickListener {
            stopMusic()
            finish()
        }

        binding.playButton.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.playButton.setImageResource(R.drawable.play_button)
            } else {
                startMusic(1)
            }
        }
        binding.rewindButton.setOnClickListener {
            changeMusic("rewind")
        }
        binding.forwardButton.setOnClickListener {
            changeMusic("forward")
        }
    }

    fun bindingMusic() {
        //화면에 binding
        binding.albumTitle.text = music?.title
        binding.albumArtist.text = music?.artist
        binding.totalDuration.text = SimpleDateFormat("mm : ss").format(music?.duration)
        binding.playDuration.text = "00 : 00"

        val bitmap = music?.getAlbumImage(this, ALBUM_SIZE)
        if (bitmap != null) {
            binding.albumImage.setImageBitmap(bitmap)
        } else {
            binding.albumImage.setImageResource(R.drawable.ic_music_note_24)
        }

        //음악등록
        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())

        //시크바 음악재생위치 변경
        binding.seekBar.max = mediaPlayer!!.duration
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        messengerJob?.cancel()
    }

    fun startMusic(num: Int) {
        when (num) {
            1 -> {}
            2 -> mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
        }
        mediaPlayer?.start()
        binding.playButton.setImageResource(R.drawable.ic_baseline_pause_24)

        val backgroundScope = CoroutineScope(Dispatchers.Default + Job())
        messengerJob = backgroundScope.launch {
            while (mediaPlayer?.isPlaying == true) {

                runOnUiThread {
                    var currentPosition = mediaPlayer?.currentPosition!!
                    binding.seekBar.progress = currentPosition
                    val currentDuration =
                        SimpleDateFormat("mm : ss").format(mediaPlayer!!.currentPosition)
                    binding.playDuration.text = currentDuration
                }
                try {
                    // 1초마다 수행되도록 딜레이
                    delay(1000)
                } catch (e: Exception) {
                    Log.d("로그", "스레드 오류 발생")
                }
            }//end of while
            runOnUiThread {
                if (mediaPlayer!!.currentPosition >= (binding.seekBar.max - 1000)) {
                    if (repeatFlag == false) {
                        shuffleMusic()
                        music = playList?.get(position) as Music
                        bindingMusic()
                        startMusic(2)
                    } else {
                        bindingMusic()
                        startMusic(2)
                    }
                }
            }
        }//end of messengerJob
    }

    fun changeMusic(type: String) {
        when (type) {
            "rewind" -> {
                if (mediaPlayer!!.currentPosition < 3000) {
                    if (position == 0) {
                        position = playList!!.size - 1
                    } else {
                        position -= 1
                    }
                } else {
                    stopMusic()
                    mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
                    binding.seekBar.progress = 0
                    binding.playDuration.text = "00 : 00"
                    startMusic(1)
                }
            }
            "forward" -> {
                shuffleMusic()
            }
        }
        music = playList?.get(position) as Music
        stopMusic()
        mediaPlayer = MediaPlayer.create(this, music?.getMusicUri())
        binding.seekBar.progress = 0
        bindingMusic()
        startMusic(1)
    }

    private fun shuffleMusic() {
        if (shuffleFlag == true) {
            position = (Math.random() * playList!!.size - 1).toInt()
        } else {
            if (position == playList!!.size - 1) {
                position = 0
            } else {
                position += 1
            }
        }
    }
}