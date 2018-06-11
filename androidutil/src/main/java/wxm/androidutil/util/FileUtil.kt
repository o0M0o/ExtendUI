@file:Suppress("unused")

package wxm.androidutil.util

import wxm.androidutil.improve.let1
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * 文件操作辅助类
 * Created by WangXM on 2016/8/17.
 */
@Suppress("MemberVisibilityCanBePrivate")
object FileUtil {
    const val FILE_PATH_SEPARATOR = "/"

    /**
     * return full path use [dir] as directory path and [fn] as file name
     */
    fun createPath(dir: String, fn: String): String {
        return "$dir$FILE_PATH_SEPARATOR$fn"
    }

    /**
     * get file name from full path [pn]
     */
    fun getFileName(pn: String): String {
        return pn.lastIndexOf(FILE_PATH_SEPARATOR).let {
            if (-1 != it) pn.substring(it + 1)
            else pn
        }
    }

    /**
     * copy file in path [src] to path [dst]
     */
    fun fileCopy(src: String, dst: String): Boolean {
        val sf = File(src)
        val df = File(dst)
        if (!(sf.exists() && sf.isFile) || df.exists())
            return false

        try {
            fileCopy(sf, df)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * copy file in path [src] to path [dst]
     * then delete file in [src]
     */
    fun fileMove(src: String, dst: String): Boolean {
        if (!fileCopy(src, dst)) {
            return false
        }

        deleteFile(src)
        return true
    }

    /**
     * copy file [src] to [dst]
     */
    @Throws(IOException::class)
    private fun fileCopy(src: File, dst: File) {
        FileInputStream(src).use { inStream ->
            FileOutputStream(dst).use { outStream ->
                inStream.channel.let {
                    it.transferTo(0, it.size(), outStream.channel)
                }
            }
        }
    }

    /**
     * delete directory in [path]
     */
    fun deleteDirectory(path: String) {
        File(path).let1 { f ->
            if (f.isDirectory) {
                f.listFiles().let1 { ff ->
                    ff.forEach {
                        it.delete()
                    }

                    f.delete()
                }
            }
        }
    }

    /**
     * delete file in [path]
     */
    fun deleteFile(path: String) {
        File(path).let1 {
            if (it.exists() && it.isFile) {
                it.delete()
            }
        }
    }


    /**
     * 遍历文件夹，搜索指定扩展名的文件
     * @param Path          搜索目录
     * @param Extension     扩展名
     * @param IsIterative   是否进入子文件夹
     * @return  满足条件的文件名
     */
    fun getDirFiles(Path: String, Extension: String, IsIterative: Boolean): LinkedList<String> {
        val ret = LinkedList<String>()
        File(Path).listFiles().forEach { f ->
            if (f.isFile) {
                if (f.path.substring(f.path.length - Extension.length) == Extension)
                    ret.add(f.path)
            } else if (f.isDirectory && !f.path.contains("/.")) {
                //忽略点文件（隐藏文件/文件夹）
                if (IsIterative)
                    getDirFiles(f.path, Extension, true)
            }
        }

        return ret
    }


    /**
     * 遍历文件夹，搜索指定扩展名的文件
     * @param Path          搜索目录
     * @param Extension     扩展名
     * @param IsIterative   是否进入子文件夹
     * @return  满足条件的文件数量
     */
    fun getDirFilesCount(Path: String, Extension: String, IsIterative: Boolean): Int {
        var ret = 0
        File(Path).listFiles().forEach { f ->
            if (f.isFile) {
                if (f.path.substring(f.path.length - Extension.length) == Extension)
                    ret++
            } else if (f.isDirectory && !f.path.contains("/.")) {
                //忽略点文件（隐藏文件/文件夹）
                if (IsIterative)
                    getDirFiles(f.path, Extension, true)
            }
        }

        return ret
    }

    /**
     * 遍历文件夹，搜索子文件夹
     * @param path              搜索目录
     * @param isInterative      是否进入子路径
     * @return  满足条件的子文件夹
     */
    fun getDirDirs(path: String, isInterative: Boolean): LinkedList<String> {
        val ret = LinkedList<String>()
        File(path).listFiles().forEach { f ->
            if (f.isDirectory && !f.path.contains("/.")) {
                ret.add(f.path)

                if (isInterative)
                    getDirDirs(f.path, true)
            }
        }

        return ret
    }
}
