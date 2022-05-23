package com.tkw.kr.myapplication.component.storage

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.tkw.kr.myapplication.R
import com.tkw.kr.myapplication.base.BaseView
import com.tkw.kr.myapplication.core.factory.MyProviderFactory
import com.tkw.kr.myapplication.util.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_storage.*
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

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

    private lateinit var cameraResultLauncher: ActivityResultLauncher<Intent>
    private var createContentUri: Uri? = null
    private var contentValues: ContentValues? = null
    private var imageFilePath: String? = null   //Q 미만 file로 저장 시 사용

    override fun initView() {
        viewModel = ViewModelProvider(this, MyProviderFactory()).get(StorageViewModel::class.java)

        cameraResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                createContentUri?.let { saveImageToGallery(it) }
            } else {
                createContentUri?.let {
                    contentResolver.delete(it, null, null)  //Q 아래에서도 contentResolver delete로 tempFile 삭제 됨
                }
            }
            createContentUri = null
        }
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

        btn_camera.setOnSingleClickListener {
            takePhoto()
        }

        btn_gallery.setOnSingleClickListener {
            takeGallery()
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
        Log.d("test", MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())
//        Log.d("test", getGalleryImageFullPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)?.path ?: "null")
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
        } else if(requestCode == 0x02 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runCamera()
        } else if(requestCode == 0x03 && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takePhoto()
        }
    }

//    private fun getGalleryImageFullPath(uri: Uri): Uri? { //Q 아래에서 file로 저장하지 않고 mediastore 사용할 때 MediaStore.Images.Media.DATA 사용해서 읽고 쓰고.. write 권한 필요
//        val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)   //29부터 deprecate 되어 경로 가져올수 없음. file descriptor 사용하는 방법으로 가져오기
//        val cursor: Cursor? = contentResolver.query(uri, filePathColumn, null, null, null)
//        val columnIndex = cursor?.getColumnIndex(filePathColumn[0]) ?: 0
//        var picturePath: String? = ""
//        if(columnIndex != -1) {
//            picturePath = cursor?.getString(columnIndex)
//        }
//        cursor?.close()
//
//        return if(picturePath != null) Uri.fromFile(File(picturePath)) else null

//    }

    //uri는 카메라로 촬영한 이미지, 갤러리에서 선택한 이미지, contentResolver query 조건을 사용한 임의의 n번째 이미지 등등. 실제 이미지에 대한 uri
    private fun getImageFromUri(uri: Uri) {
        //uri에서 이미지 꺼내서 bitmap으로 변환해서 화면에 보여주기
        var bitmap: Bitmap? = null

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        iv_img.setImageBitmap(bitmap)
    }

    //카메라 실행
    private fun takePhoto() {
        //파일 생성(createFile()) 후 해당 uri로 인텐트 실행.
        //ActivityResult에서 취소 또는 종료한 경우 빈 파일이 남아있으므로 지워줘야 함.(contentResolver insert하지 않고 file로 생성한거면?)
        //성공인 경우 saveImageToGallery 호출
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val fileName = sdf.format(Date())

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            createContentUri = createFileAfterQ(fileName, "image/jpeg")
        } else {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0x03)
            } else {
                createContentUri = createFileBeforeQ(fileName)
            }
        }
        createContentUri?.run {
            runCamera()
        }

    }

    private fun runCamera() {
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {  //createContentUri에 카메라 앱이 파일을 쓰기 위해 카메라 권한 필요
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 0x02)
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, createContentUri)
            cameraResultLauncher.launch(intent)
        }
    }

    //갤러리 실행
    private fun takeGallery() {

    }

    //커스텀 그리드 형태 갤러리
    private fun takeCustomGallery() {

    }

    //카메라 실행 시 저장할 파일의 uri를 return
    //파일을 getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) 여기에 저장만 하면 갤러리에서 보이나?
    //보이더라도 앱을 삭제했을 때 사진이 삭제되지 않아야 한다면 mediastore insert -> update해야하고, 파일을 업로드해야 하는 경우 InputStream inputStream = contentResolver.openInputStream(uri);로 임시파일 저장한 뒤 업로드
    //안보이면 MediaStore insert하고 update해야함.
    private fun createFileAfterQ(fileName: String, mimeType: String): Uri? {
        contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//                put(MediaStore.Images.Media.IS_PENDING, 1)    //외부(카메라 앱)에서 접근이 불가능하게 만들기 때문에 여기서 1로 설정하면 카메라 앱 crash 발생함.
        }


        val item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        //todo EXTERNAL_CONTENT_URI랑 File Provider 차이는? File Provider는 외부에서 접근 가능
        return item
    }

    private fun createFileBeforeQ(fileName: String): Uri? {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        try {
            val image = File.createTempFile(
                fileName,
                ".jpg",
                storageDir
            )
            imageFilePath = image.absolutePath
            return FileProvider.getUriForFile(this, packageName, image) //해당 fileprovider content uri로 카메라 실행 후 그냥 꺼버렸을 때 Q 위 단말에서와 동일하게 contentResolver로 delete 하기 위함
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    //카메라 촬영 후 bitmap rotate하고 호출해서 다시 저장하기.
    //MediaStore에 insert 하는 경우 sdk 29 이상은 권한 필요 x, 28 아래는 write권한 필요함. 여기서는 28 아래일 때 파일에 바로 write 하도록 함.
    private fun saveImageToGallery(uri: Uri) {
        val returnSavedUri: Uri?
        returnSavedUri = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImageToGalleryAfterQ(uri)
        } else {
            saveImageToGalleryBeforeQ(uri)
        }

        if(returnSavedUri != null) {
            getImageFromUri(returnSavedUri)
        }
//        returnSavedUri?.let { getImageFromUri(it) }   //immutable 변수에 대해 let 사용 시 decompile 하면 불필요한 변수만 생성됨
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveImageToGalleryAfterQ(uri: Uri): Uri?{
        contentValues?.put(MediaStore.Images.Media.IS_PENDING, 1)   //insert 할 때 추가해주는거 아니면 여기선 의미 없음
        try {
            val rotate = rotateBitmap(uri)
            val pdf: ParcelFileDescriptor? = contentResolver.openFileDescriptor(uri, "w", null)
            pdf?.let {
                FileOutputStream(it.fileDescriptor).use { outputStream ->
                    contentResolver.openInputStream(rotate)?.use { inputStream ->
                        val buf = ByteArray(1024)
                        var len: Int
                        while (inputStream.read(buf).also { len = it } > 0)
                            outputStream.write(buf, 0, len)
                        outputStream.close()
                        inputStream.close()
                    }
                }
            }
            contentValues?.clear()
            contentValues?.put(MediaStore.Images.Media.IS_PENDING, 0)
            contentResolver.update(rotate, contentValues, null, null)

            return rotate
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun saveImageToGalleryBeforeQ(uri: Uri): Uri? {
        if(imageFilePath.isNullOrEmpty()) {
            return null
        }

        val rotate = rotateBitmap(uri)
        var bitmap: Bitmap? = null

        try {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, rotate))
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, rotate)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: OutputStream? = null
        try {
            out = FileOutputStream(File(imageFilePath!!))
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            out?.close()
        }

        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.setData(Uri.fromFile(File(imageFilePath!!))) //Q 아래 갤러리 갱신은 content 스킴이 아닌 실제 파일의 스킴으로!
        Log.d("scheme", Uri.fromFile(File(imageFilePath!!)).toString())
        Log.d("scheme", FileProvider.getUriForFile(this, packageName, File(imageFilePath!!)).toString())
//        intent.setData(FileProvider.getUriForFile(this, packageName, File(imageFilePath!!)))
        sendBroadcast(intent)

        return rotate
    }

    //서버에서 이미지 저장하기
    private fun saveImageFromServer() {

    }

    private fun rotateBitmap(uri: Uri): Uri {
        return uri  //임시로 rotate없이 저장하게
    }
}