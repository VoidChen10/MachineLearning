import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Directory {
    /**
     * 文件夹工具类，返回指定路径下所有的文件夹与文件
     */

    private ArrayList<File> dirList;
    private ArrayList<File> fileList;

    Directory(){
        this.dirList = new ArrayList<File>();
        this.fileList = new ArrayList<File>();
    }
    public Directory walk(){
        return recurseDirs(".", "[\\d\\D]*");
    }
    public Directory walk(String filePath){
        return recurseDirs(filePath, "[\\d\\D]*");
    }
    public Directory walk(String filePath, String regex){
        return recurseDirs(filePath, regex);
    }
    public Directory recurseDirs(String filePath, String regex){
        // 递归返回指定路径下所有的文件夹和文件
        FilenameFilter filter = constructFilenameFilter(regex);
        File file = new File(filePath);
        for(File item : file.listFiles(filter)){
            if(item.isDirectory()){
                this.dirList.add(item);
                recurseDirs(item.toString(), regex);
            }
            else
                this.fileList.add(item);
        }
        return this;
    }
    private FilenameFilter constructFilenameFilter(final String regex){
        // 返回一个FilenameFilter匿名类
        return new FilenameFilter() {
            private Pattern pattern = Pattern.compile(regex);
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        };
    }
    public String toString(){
        return "dirs: "+this.dirList+"\nfiles: "+ this.fileList +"";
    }

    public static void main(String[] args){
        Directory dir = new Directory();
        System.out.println(dir.walk("E:\\1"));
    }
}
