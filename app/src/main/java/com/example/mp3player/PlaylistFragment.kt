package com.example.mp3player

import android.app.Activity
import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp3player.databinding.FragmentPlaylistBinding


class PlaylistFragment : Fragment() {

    companion object {
        val REQ_READ = 99
        val DB_NAME = "musicDB"
        var VERSION = 1
    }

    lateinit var binding: FragmentPlaylistBinding
    lateinit var adapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf<Music>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        musicList?.clear()
        startProcess()
        adapter.notifyDataSetChanged()

        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        val dbHelper = DBHelper(requireContext(), MainActivity.DB_NAME, MainActivity.VERSION)
        startProcess()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrBlank()) {
                    musicList?.clear()
                    dbHelper.selectMusicAll()?.let { musicList?.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    musicList?.clear()
                    dbHelper.searchMusic(query)?.let { musicList?.addAll(it) }
                    adapter.notifyDataSetChanged()
                }
                return true
            }
        })
        return binding.root
    }

    private fun startProcess() {

        //먼저 데이터베이스에서 음원정보를 가져온다. 없으면 공유메모리에서 음원정보를 가져온다.
        val dbHelper = DBHelper(requireContext(), MainActivity.DB_NAME, MainActivity.VERSION)
        musicList = dbHelper.selectMusicAll()

        //만약 데이터베이스 없으면 contentResolver 통해서 공유메모리에서 음원정보를 가져온다
        if (musicList == null) {
            val playMusicList = getMusicList()
            if (playMusicList != null) {
                for (i in 0..playMusicList.size - 1) {
                    val music = playMusicList.get(i)
                    dbHelper.insertMusic(music)
                }
                musicList = playMusicList
            } else {
                Log.d("Mp3_db", "MainActivity.startProcess() :외장메모리 음원파일이 없음")
            }
        }

        //4.리사이클러뷰에 제공
        adapter = MusicRecyclerAdapter(requireContext(), musicList)
        binding.recyclerView1.adapter = adapter
        binding.recyclerView1.layoutManager = LinearLayoutManager(context)
    }

    private fun getMusicList(): MutableList<Music>? {
        var imsiMusicList: MutableList<Music>? = mutableListOf<Music>()
        //먼저 데이터베이스에서 음원정보를 가져온다. 없으면 공유메모리에서 음원정보를 가져온다.
        val musicURL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )

        //2. contentResolver 쿼리 작성을 통해서 musicList로 가져오기
        val cursor = context?.contentResolver?.query(musicURL, projection, null, null, null)
        if (cursor?.count!! > 0) {
            while (cursor!!.moveToNext()) {
                val id = cursor.getString(0)
                val title = cursor.getString(1).replace("'", "")
                val artist = cursor.getString(2).replace("'", "")
                val albumId = cursor.getString(3)
                val duration = cursor.getInt(4)
                val music = Music(id, title, artist, albumId, duration, 0)
                //데이타베이스 음원정보를 입력한다
                imsiMusicList?.add(music)
            }
        } else {
            imsiMusicList = null
        }
        return imsiMusicList
    }
}