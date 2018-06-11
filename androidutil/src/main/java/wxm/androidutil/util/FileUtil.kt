package wxm.androidutil.util

import java.io.File
import java.util.LinkedList

/**
 * 文件操作辅助类
 * Created by WangXM on 2016/8/17.
 */
object FileUtil {
    /**
     * 删除目录
     * @param path  待删除目录路径
     */
    fun DeleteDirectory(path: String) {
        val f = File(path)
        if (f.isDirectory) {
            val childFiles = f.listFiles()
            if (childFiles == null || childFiles.size == 0) {
                f.delete()
            } else {
                for (ff in childFiles) {
                    ff.delete()
                }

                f.delete()
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
        val files = File(Path).listFiles()
        for (f in files) {
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
        val files = File(Path).listFiles()
        if (null != files) {
            for (f in files) {
                if (f.isFile) {
                    if (f.path.substring(f.path.length - Extension.length) == Extension)
                        ret++
                } else if (f.isDirectory && !f.path.contains("/.")) {
                    //忽略点文件（隐藏文件/文件夹）
                    if (IsIterative)
                        getDirFiles(f.path, Extension, true)
                }
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
        val files = File(path).listFiles()
        for (f in files) {
            if (f.isDirectory) {
                if (!f.path.contains("/.")) {
                    ret.add(f.path)

                    if (isInterative)
                        getDirDirs(f.path, true)
                }
            }
        }

        return ret
    }
}
