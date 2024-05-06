package com.frank.ffmpeg.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.frank.ffmpeg.FFmpegApplication
import com.frank.ffmpeg.R
import com.frank.ffmpeg.util.ContentUtil


/**
 * base Activity
 * Created by frank on 2019/11/2.
 */

abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
        setContentView(layoutId)
    }

    protected fun hideActionBar() {
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    private fun requestPermission() {
        requestPermission(permissions)
    }

    protected fun requestPermission(permissions: Array<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE)
        }
    }

    protected fun initViewsWithClick(vararg viewIds: Int) {
        for (viewId in viewIds) {
            getView<View>(viewId).setOnClickListener(this)
        }
    }

    override fun onClick(v: View) {
        onViewClick(v)
    }

    protected fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        this.startActivityForResult(intent, 123)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        try {
            if (data != null && data.data != null) {
                val uri = data.data;
//                val projection = arrayOf(MediaStore.Video.Media.DATA)
//                val cursor = uri?.let { contentResolver.query(it, projection, null, null, null) }
//                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//                cursor!!.moveToFirst()
//                val filePath = cursor!!.getString(column_index)
//                cursor!!.close()

                val checkSelfPermission =
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                val checkSelfPermission1 =
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (checkSelfPermission == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission1 == PackageManager.PERMISSION_GRANTED
                ){

                }else{
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        this.requestPermissions(
                            arrayOf<String>(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ), 10
                        )
                    }
                }
//                gets()
//                val filePath = "/storage/emulated/0/input.mp4"
                val filePath = ContentUtil.getPath(this, data.data!!)
                Log.i(TAG, "filePath=" + filePath!!)
                onSelectedFile(filePath)
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }
    }
    fun gets(){
        val REQUEST_MANAGE_FILES_ACCESS = 2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //判断是否有管理外部存储的权限
            if (!Environment.isExternalStorageManager()) {
                val intent =  Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_MANAGE_FILES_ACCESS);
            } else {
                // TODO: 2023/11/22
                // 已有所有文件访问权限，可直接执行文件相关操作
            }
        } else {
            // TODO: 2023/11/22
            //非android11及以上版本，走正常申请权限流程
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_select -> selectFile()
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    protected fun showToast(msg: String) {
        if (TextUtils.isEmpty(msg)) {
            return
        }
        Toast.makeText(FFmpegApplication.getInstance(), msg, Toast.LENGTH_SHORT).show()
    }

    protected fun showSelectFile() {
        showToast(getString(R.string.please_select))
    }

    protected fun <T : View> getView(viewId: Int): T {
        return findViewById<View>(viewId) as T
    }

    internal abstract fun onViewClick(view: View)

    internal abstract fun onSelectedFile(filePath: String)

    companion object {

        private val TAG = BaseActivity::class.java.simpleName

        private const val REQUEST_CODE = 1234
        private val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO)
    }

}
