package wxm.androidutil.util;

import java.io.File;
import java.util.LinkedList;

/**
 * 文件操作辅助类
 * Created by 123 on 2016/8/17.
 */
public class FileUtil {

    /**
     * 删除目录
     * @param path  待删除目录路径
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void DeleteDirectory(String path)  {
        File f = new File(path);
        if(f.isDirectory()) {
            File[] childFiles = f.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                f.delete();
            } else  {
                for(File ff : childFiles)   {
                    ff.delete();
                }

                f.delete();
            }
        }
    }


    /**
     *  遍历文件夹，搜索指定扩展名的文件
     * @param Path          搜索目录
     * @param Extension     扩展名
     * @param IsIterative   是否进入子文件夹
     * @return  满足条件的文件名
     */
    public static LinkedList<String> getDirFiles(String Path, String Extension, boolean IsIterative)
    {
        LinkedList<String> ret = new LinkedList<>();
        File[] files =new File(Path).listFiles();
        for (File f : files) {
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))
                    ret.add(f.getPath());
            } else if (f.isDirectory() && !f.getPath().contains("/.")) {
                //忽略点文件（隐藏文件/文件夹）
                if (IsIterative)
                    getDirFiles(f.getPath(), Extension, true);
            }
        }

        return ret;
    }


    /**
     *  遍历文件夹，搜索指定扩展名的文件
     * @param Path          搜索目录
     * @param Extension     扩展名
     * @param IsIterative   是否进入子文件夹
     * @return  满足条件的文件数量
     */
    public static int getDirFilesCount(String Path, String Extension, boolean IsIterative)
    {
        int ret = 0;
        File[] files =new File(Path).listFiles();
        if(null != files) {
            for (File f : files) {
                if (f.isFile()) {
                    if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))
                        ret++;
                } else if (f.isDirectory() && !f.getPath().contains("/.")) {
                    //忽略点文件（隐藏文件/文件夹）
                    if (IsIterative)
                        getDirFiles(f.getPath(), Extension, true);
                }
            }
        }

        return ret;
    }

    /**
     * 遍历文件夹，搜索子文件夹
     * @param path              搜索目录
     * @param isInterative      是否进入子路径
     * @return  满足条件的子文件夹
     */
    public static LinkedList<String> getDirDirs(String path, boolean isInterative)  {
        LinkedList<String> ret = new LinkedList<>();
        File[] files =new File(path).listFiles();
        for(File f : files)     {
            if (f.isDirectory())     {
                if(!f.getPath().contains("/."))     {
                    ret.add(f.getPath());

                    if(isInterative)
                        getDirDirs(f.getPath(), true);
                }
            }
        }

        return ret;
    }
}
