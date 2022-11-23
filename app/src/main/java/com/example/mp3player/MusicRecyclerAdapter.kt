package com.example.mp3player

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mp3player.databinding.ItemRecyclerBinding
import kotlinx.coroutines.Job
import java.text.SimpleDateFormat

class MusicRecyclerAdapter(val context : Context, val musicList : MutableList<Music>?) : RecyclerView.Adapter<MusicRecyclerAdapter.CustomViewHolder>() {

    companion object{
        val ALBUM_SIZE = 80
    }

    private var mediaPlayer : MediaPlayer? = null
    private var messengerJob : Job? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val binding = (holder as CustomViewHolder).binding
        val music = musicList?.get(position)
        binding.tvArtist.text = music?.artist
        binding.tvTitle.text = music?.title
        binding.tvDuration.text = SimpleDateFormat("mm : ss").format(music?.duration)
        val bitmap = music?.getAlbumImage(context,ALBUM_SIZE)
        if(bitmap != null){
            binding.ivAlbumArt.setImageBitmap(bitmap)
        }else{
            binding.ivAlbumArt.setImageResource(R.drawable.ic_music_note_24)
        }
        when(music?.likes){
            0->{binding.ivItemLike.setImageResource(R.drawable.ic_favorite_border_24)}
            1->{binding.ivItemLike.setImageResource(R.drawable.ic_favorite_fill_24)}
        }

        binding.root.setOnClickListener {
            if(mediaPlayer != null){
                mediaPlayer?.stop()
                messengerJob?.cancel()
            }
            val playList : ArrayList<Parcelable>? = musicList as ArrayList<Parcelable>
            val intent = Intent(binding.root.context,PlaymusicActivity::class.java)
            intent.putExtra("playList",playList)
            intent.putExtra("position",position)
            binding.root.context.startActivity(intent)
        }

        //이벤트처리 좋아요 눌렀을때 데이타베이스 좋아요 등록돼야한다
        binding.ivItemLike.setOnClickListener {
            if(music?.likes == 0){
                binding.ivItemLike.setImageResource(R.drawable.ic_favorite_fill_24)
                music?.likes = 1
            }else{
                binding.ivItemLike.setImageResource(R.drawable.ic_favorite_border_24)
                music?.likes = 0
            }
            if(music != null){
                val dbHelper = DBHelper(context, MainActivity.DB_NAME, MainActivity.VERSION)
                var flag = dbHelper.updateLikes(music)
                if(flag == false){
                    Log.d("Mp3_db","MusicRecyclerAdapter.onBindViewHolder 업데이트 실패 ${music.toString()}")
                }else{
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList?.size?:0
    }

    class CustomViewHolder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root)
}