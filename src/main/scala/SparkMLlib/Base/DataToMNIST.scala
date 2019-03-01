package SparkMLlib.Base

import java.io.{File, FileOutputStream, PrintWriter}

import SparkMLlib.Base.ImageUtil.{getGrayArray, getGrayImg}
import javax.imageio.ImageIO

import scala.collection.mutable.ArrayBuffer

/**
  * @author Chenyl
  * @date 2019/2/28 9:48
  */
object DataToMNIST {
  def main(args: Array[String]): Unit = {

    val path = "C:\\Users\\Void\\Desktop\\own\\"
    val files = FileUtil.getFileList(path,".jpg","灰")
    //处理成（标签，灰度数组）
    val datas:Array[(Int, Array[Byte])] = files.map(f => (f.getName.substring(0,1).toInt,getGrayArray(ImageIO.read(f))))
    //生成MNIST文件
    imgToMNIST(datas)


    //输出灰度图像
    files.foreach(f => {
      val grayImg = getGrayImg(ImageIO.read(f))
      val newFile = new File(path+"灰"+f.getName);
      ImageIO.write(grayImg, "jpg", newFile);
    })
    println("图像输出完成")

  }

  /**
    * 转换成MNIST数据
    * @param grayArray
    * @return
    */
  def imgToMNIST(data: Array[(Int, Array[Byte])]){

    //组装MNIST格式的图像文件头
    var images = ArrayBuffer[Byte]()
    images ++= intToByteArray(2051)  //magicNum
    images ++= intToByteArray(data.length)  //number of items
    images ++= intToByteArray(28)  //number of rows
    images ++= intToByteArray(28)  //number of columns

    //组装MNIST格式的标签文件头
    var labels = ArrayBuffer[Byte]()
    labels ++= intToByteArray(2049)  //magicNum
    labels ++= intToByteArray(data.length)  //number of items

    //组装数据
    data.foreach(d =>{
      val label = (d._1 & 0xFF).toByte
      val image = d._2
      labels += label
      images ++= image
    })

//    var i =  0
//    images.drop(16).toArray.foreach(
//      b =>{
//        print(b+" ")
//        i = i+1
//        if(i == 28){
//          i = 0
//          println()
//        }}
//    )


    //输出二进制文件
    val imagesFile = new File("C:\\Users\\Void\\Desktop\\test-images.idx3-ubyte");
    val labelsFile = new File("C:\\Users\\Void\\Desktop\\test-labels.idx1-ubyte");

    val imagesWriter = new FileOutputStream(imagesFile)
    imagesWriter.write(images.toArray)
    imagesWriter.close()

    val labelsWriter = new FileOutputStream(labelsFile)
    labelsWriter.write(labels.toArray)
    labelsWriter.close()

    println("文件写入完成")
  }

  /**
    * int到byte[]
    *
    * @param i
    * @return
    */
  def intToByteArray(i: Int): Array[Byte] = {
    val result = new Array[Byte](4)
    //由高位到低位
    result(0) = ((i >> 24) & 0xFF).toByte
    result(1) = ((i >> 16) & 0xFF).toByte
    result(2) = ((i >> 8) & 0xFF).toByte
    result(3) = (i & 0xFF).toByte
    result
  }
}
