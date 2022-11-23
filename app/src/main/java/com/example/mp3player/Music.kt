package com.example.mp3player

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.io.IOException

@Parcelize
class Music(
    var id: String?,
    var title: String?,
    var artist: String?,
    var albumId: String?,
    var duration: Int?,
    var likes: Int?
) :
    Parcelable {

    //serializable -> parcelable 속도처리, 용량처리
    companion object : Parceler<Music> {
        override fun create(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun Music.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(title)
            parcel.writeString(artist)
            parcel.writeString(albumId)
            parcel.writeInt(duration!!)
            parcel.writeInt(likes!!)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt()
    )

    //앨범 Uri 가져온다.
    fun getAlbumUri(): Uri {
        return Uri.parse("content://media/external/audio/albumart/" + albumId)
    }

    //음악 Uri
    fun getMusicUri(): Uri {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
    }

    //음악 비트맵 가져와서 원하는 사이즈로 비트맵 만들기
    fun getAlbumImage(context: Context, albumImageSize: Int): Bitmap? {

        val contentResolver: ContentResolver = context.contentResolver
        val uri = getAlbumUri()        // 앨범 경로(Uri)
        //비트맵옵션
        val options = BitmapFactory.Options()

        if (uri != null) {
            var parcelFileDescriptor: ParcelFileDescriptor? = null
            try {
                parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
                var bitmap = BitmapFactory.decodeFileDescriptor(
                    parcelFileDescriptor!!.fileDescriptor,
                    null,
                    options
                )

                //비트맵을 가져왔는데 우리가 원하는 사이즈가 아닐경우를 위해서 처리
                if (bitmap != null) {
                    val tempBitmap =
                        Bitmap.createScaledBitmap(bitmap, albumImageSize, albumImageSize, true)
                    bitmap.recycle()
                    bitmap = tempBitmap
                }
                return bitmap
            } catch (e: Exception) {
                Log.d("Mp3_db", "getAlbumImage() ${e.toString()}")
            } finally {
                try {
                    parcelFileDescriptor?.close()
                } catch (e: IOException) {
                    Log.d("Mp3_db", "getAlbumImage() parcelFileDescriptor ${e.toString()}")
                }
            }
        }
        return null
    }
}