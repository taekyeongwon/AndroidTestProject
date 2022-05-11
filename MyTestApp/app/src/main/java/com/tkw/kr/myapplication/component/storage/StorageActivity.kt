package com.tkw.kr.myapplication.component.storage

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_storage.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * built-in 외부저장소가 존재하며, SD카드를 지원하는 디바이스라면 다음과 같이 됩니다.
내장 메모리 : 내부저장소 + built in 외부저장소
외장 메모리 : SD카드 외부저장소

built-in 외부저장소가 존재하며, SD카드를 지원하지 않는 디바이스라면 다음과 같이 됩니다.
내장 메모리 - 내부저장소 + built in 외부저장소

https://hellose7.tistory.com/96
https://youngest-programming.tistory.com/386
 */
class StorageActivity: BaseView<StorageViewModel>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_storage
    override lateinit var viewModel: StorageViewModel

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(StorageViewModel::class.java)
    }

    override fun initObserver() {

    }

    override fun initListener() {
        btn_save.setOnSingleClickListener {
            internalStorageIndividual()
            externalStorageIndividual()
            //버전에 따라 아래 legacy scoped 나눠서 사용.. SDK 29 이상(Android 10) - scoped
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                externalScopedStroageShared()
            } else {
                externalLegacyStorageShared()
            }
        }
    }

    //내부 저장소
    //내부 저장소에서 getFilesDir() 또는 getCacheDir()
    // /data/data/com.xxx.xxx
    // 다른 앱에서 접근 시 예외 발생
    private fun internalStorageIndividual() {
        val fileDirString: String = filesDir.path

        val fileNameString = "internalFile.txt"

        val savedPathString = "$fileDirString/$fileNameString"
        writeFileToStorage(savedPathString)
    }

    // 공유 공간 : /mnt/sdcard /storage/emulated/0 /storage/self/primary

    //외부 저장소(legacy, scoped storage) 개별 공간
    //외부 저장소에서 getExternalFilesDir() 또는 getExternalCacheDir()
    // todo 개별 공간 : 위 공유공간 경로/Android/data/com.xxx.xxx/files -> 권한만 있으면 다른 앱에서 접근 시 접근 가능? 레거시냐 스코프냐에 따라 다른가?
    // 개별공간 경로는 getExternalFilesDir(null)로 scoped랑 동일한지? ㅇㅇ
    // getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) 형태로 가져온 경로가 공유공간? ㄴㄴ 개별공간 안의 해당 앱만 접근 가능한 미디어 공간인것(절대 경로를 얻기 위해 MediaStore로 저장한 파일을 옮겨와서 절대경로를 얻을 수 있다. ex 서버 업로드 시)
    // -> 위 공유공간 경로/Android/data/com.xxx.xxx/files/Download 아래에 생김
    // 같은 앱 내에서 권한없이 접근 가능, 다른 앱에 접근할 때만 권한 필요
    // todo 개별 공간 샌드박스 처리 되어 외부 앱에서 접근 불가능 -> 레거시냐 스코프냐에 따라 다른가?
    // 앱 삭제 시 관련 파일 같이 삭제됨
    private fun externalStorageIndividual() {
//        getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val fileDirString: String? = getExternalFilesDir(null)?.path
        val fileNameString = "externalFile.txt"

        val savedPathString = "$fileDirString/$fileNameString"
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            writeFileToStorage(savedPathString)
        }
    }

    //외부 저장소(legacy storage) 공유 공간 - 접근하려면 저장소 권한 필요
    // Environment.getExternalStorageDirectory() 형태로 가져온 경로가 공유공간? ㅇㅇ
    // 개별 공간 : 위 공유공간 경로/Android/data/com.xxx.xxx/files -> 권한만 있으면 다른 앱에서 접근 시 접근 가능
    // 개별공간 경로는 getExternalFilesDir(null)로 scoped랑 동일한지? ㅇㅇ
    // 앱 삭제 시 파일 삭제 안됨
    // 안드로이드 11에서도 Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) 경로에 저장 시 저장 됨. - deprecated됐으나 아직 사용 가능한 상태
    // Environment.getExternalStorageDirectory() 경로에 저장 시 crash 발생함. -> 권한 필요, 11부턴 권한 있어도 저장 불가(write권한 무시되기 때문)
    private fun externalLegacyStorageShared() {
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 0x01)
        } else {
            val fileDirString: String? = Environment.getExternalStorageDirectory().path
            val fileNameString = "externalLegacySharedFile.txt"

            val savedPathString = "$fileDirString/$fileNameString"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                writeFileToStorage(savedPathString)
            }
        }
    }

    //외부저장소(scoped storage) 공유 공간
    // 공유 공간 : /mnt/sdcard /storage/emulated/0 /storage/self/primary 내 Media, Download, ..
    // Environment.getExternalStorageDirectory() deprecated 되면서 /sdcard 이하의 영역 직접접근 불가능하여 MediaStore API 사용하여 접근, MediaStore로 절대경로 가져오는건 deprecated.
    // todo Android 11에선 다시 File API 사용 가능?
    // todo 같은 앱 내에서 권한없이 접근 가능, 다른 앱에 접근할 때만 권한 필요 - 어떤 권한?
    // todo 공유 공간 샌드박스 처리 되어 외부 앱에서 접근 불가능?
    // 앱 삭제 시 파일 삭제 안됨
    private fun externalScopedStroageShared() {
        sharedMediaArea()
        sharedDownloadArea()
    }

    //저장소에 write
    private fun writeFileToStorage(pathString: String) {
        val file = File(pathString)
        val fileWriter = FileWriter(file, false)
        val bufferedWriter = BufferedWriter(fileWriter)
        bufferedWriter.append(et_input.text.toString())
        bufferedWriter.close()
    }

    //MediaStore API로 접근
    private fun sharedMediaArea() {
        Log.d("test", MediaStore.Images.Media.EXTERNAL_CONTENT_URI.path ?: "null")
        Log.d("test", getGalleryImageFullPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)?.path ?: "null")
    }

    //SAF(Storage Access Framework)로 접근
    private fun sharedDownloadArea() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0x01 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            externalLegacyStorageShared()
        }
    }

    private fun getGalleryImageFullPath(uri: Uri): Uri? {
        val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)   //29부터 deprecate 되어 경로 가져올수 없음. file descriptor 사용하는 방법으로 가져오기
        val cursor: Cursor? = contentResolver.query(uri, filePathColumn, null, null, null)
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
        var picturePath: String? = ""
        if(columnIndex != -1) {
            picturePath = cursor?.getString(columnIndex)
        }
        cursor?.close()

        return if(picturePath != null) Uri.fromFile(File(picturePath)) else null
    }
}