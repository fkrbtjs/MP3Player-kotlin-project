package com.example.mp3player

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, dbName: String, version: Int) :
    SQLiteOpenHelper(context, dbName, null, version) {

    // DBHelper 처음 객체가 만들어질 때 한번만 실행됨
    override fun onCreate(db: SQLiteDatabase?) {
        val query = """
            create table musicTBL( id text primary key , title text , artist text , albumId text , duration integer , likes integer)
            """.trimIndent()
        db?.execSQL(query)
    }

    //버전이 변경되었을 때 불러지는 콜백함수
    override fun onUpgrade(db: SQLiteDatabase?, newVersion: Int, oldVersion: Int) {
        val query = """
            drop table if exists musicsTBL
        """.trimIndent()
        db?.execSQL(query)
        this.onCreate(db)
    }

    fun selectMusicAll(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """select * from musicTBL""".trimIndent()
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("Mp3_db", "DBHelper.selectMusicAll() ${e.printStackTrace()}")
            musicList = null
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun insertMusic(music: Music): Boolean {
        var flag = false
        val query = """insert into musicTBL(id,title,artist,albumId,duration,likes)
            values('${music.id}','${music.title}','${music.artist}','${music.albumId}',${music.duration},${music.likes})
        """.trimIndent()
        val db = this.writableDatabase
        try {
            db.execSQL(query)
            flag = true
        } catch (e: Exception) {
            Log.d("Mp3_db", "DBHelper.selectMusicAll() ${e.printStackTrace()}")
            flag = false
        } finally {
            db.close()
        }
        return flag
    }

    fun updateLikes(music: Music): Boolean {
        var flag = false
        val query = """
            update musicTBL set likes = ${music.likes} where id = '${music.id}'
        """.trimIndent()
        val db = this.writableDatabase
        try {
            db.execSQL(query)
            flag = true
        } catch (e: Exception) {
            Log.d("Mp3_db", "DBHelper.updateLikes() ${e.printStackTrace()}")
            flag = false
        }
        return flag
    }

    fun searchMusic(query: String?): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query =
            """select * from musicTBL where title like '${query}%' or artist like '${query}%' """.trimIndent()
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("Mp3_db", "DBHelper.searchMusic() ${e.printStackTrace()}")
            musicList = null
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }

    fun selectMusicLike(): MutableList<Music>? {
        var musicList: MutableList<Music>? = mutableListOf<Music>()
        var cursor: Cursor? = null
        val query = """select * from musicTBL where likes = 1 """.trimIndent()
        val db = this.readableDatabase
        try {
            cursor = db.rawQuery(query, null)
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumId = cursor.getString(3)
                    val duration = cursor.getInt(4)
                    val likes = cursor.getInt(5)
                    val music = Music(id, title, artist, albumId, duration, likes)
                    musicList?.add(music)
                }
            } else {
                musicList = null
            }
        } catch (e: Exception) {
            Log.d("Mp3_db", "DBHelper.selectMusicLike() ${e.printStackTrace()}")
            musicList = null
        } finally {
            cursor?.close()
            db.close()
        }
        return musicList
    }
}