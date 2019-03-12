package SparkMLlib.Base

import java.io.File

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * @author voidChen
  * @date 2019/2/28 9:47
  */
object FileUtil {
  val log = LoggerFactory.getLogger(FileUtil.getClass)

  def main(args: Array[String]): Unit = {
    val arr = getFileList("C:\\Users\\42532\\Desktop\\own\\","","")
    arr.foreach(f => println(f.getName))
  }

  /**
    * 获取文件列表
    * @param path
    * @return
    */
  def getFileList(path:String): Array[File] ={ getFileList(path:String,"","") }
  def getFileList(path:String,like:String,filter:String): Array[File] ={

    var arrBuf = ArrayBuffer[File]()
    val file = new File(path)
    if(!file.isDirectory)
      log.error("该文件路径非文件夹")
    else{
      file.listFiles().filter(!_.isDirectory)  //排除文件夹
        .filter(n => filter == "" || !n.getName.contains(filter)) //排除需要过滤的文件名
        .filter(n => like == "" || n.getName.contains(like)) //只取需要的文件名
//        .filter(_.getName.substring(0,1).toInt == 1)
        .foreach(arrBuf += _)
    }

    arrBuf.toArray
  }


  /**
    * 安全打开文件流方法
    *
    * 关于这段代码的发展和历史，可以看stackoverflow前辈们的解答：
    * https://stackoverflow.com/questions/4604237/how-to-write-to-a-file-in-scala/34277491#34277491
    * 这里我先记录下来，后面有时间再研究
    */
  def using[A <: { def close(): Unit }, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }


}
