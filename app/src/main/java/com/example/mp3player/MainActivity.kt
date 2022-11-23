package com.example.mp3player

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mp3player.databinding.ActivityMainBinding
import com.example.mp3player.databinding.UsertabButtonBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    companion object {
        val REQ_READ = 99
        val DB_NAME = "musicDB"
        var VERSION = 1
    }

    lateinit var playlistFragment: PlaylistFragment
    lateinit var favorlistFragment: FavorlistFragment
    lateinit var binding: ActivityMainBinding
    lateinit var adapter: MusicRecyclerAdapter
    private var musicList: MutableList<Music>? = mutableListOf<Music>()

    //승인받을 퍼미션항목요청
    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isPermitted()) {
            //페이저 어답터 연결
            attachPager()
        } else {
            //외부저장소 읽기 권한이 없다면 , 유저에게 읽기권한 요청
            ActivityCompat.requestPermissions(this, permissions, REQ_READ)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_READ && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //페이저 어답터 연결
            attachPager()
        } else {
            Toast.makeText(this, "권한요청을 승인해야 뮤직플레이어앱을 실행할 수 있습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    fun attachPager() {
        val pagerAdapter = PagerAdapter(this)
        val title = mutableListOf<String>("PlayList", "FavorList")
        playlistFragment = PlaylistFragment()
        favorlistFragment = FavorlistFragment()
        pagerAdapter.addFragment(playlistFragment, title[0])
        pagerAdapter.addFragment(favorlistFragment, title[1])
        binding.viewpager.adapter = pagerAdapter

        //탭레이아웃과 뷰페이저 연결
        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab, position ->
            tab.setCustomView(createTabView(title[position]))
        }.attach()
    }

    private fun isPermitted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permissions[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        } else {
            return false
        }
    }

    private fun createTabView(title: String): View {
        val userTabBinding = UsertabButtonBinding.inflate(layoutInflater)
        userTabBinding.tvtabName.text = title
        return userTabBinding.root
    }
}