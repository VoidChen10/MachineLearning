package SparkMLlib.Base

import java.io.File

import org.slf4j.LoggerFactory

import scala.collection.mutable.ArrayBuffer

/**
  * @author Chenyl
  * @date 2019/2/28 9:47
  */
object FileUtil {
  val log = LoggerFactory.getLogger(FileUtil.getClass)

  def main(args: Array[String]): Unit = {
    val arr = getFileList("C:\\Users\\Void\\Desktop\\own\\","","")
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


}
