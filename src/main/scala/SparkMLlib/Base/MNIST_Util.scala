package SparkMLlib.Base

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.ByteBuffer

import SparkMLlib.Base.FileUtil.using
import SparkMLlib.Base.ImageUtil.{getGrayArray, getGrayImg}
import javax.imageio.ImageIO

import scala.collection.mutable.ArrayBuffer

/**
  * @author voidChen
  * @date 2019/2/27 14:47
  */
object MNIST_Util {

  /**
    * 生成灰度图和MNIST格式文件
    * @param args
    */
  def main(args: Array[String]): Unit = {

    val path = "C:\\Users\\42532\\Desktop\\own\\"
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
    * 读取标签
    * @param labelPath
    * @return
    */
  def loadLabel(labelPath: String):  Array[Byte] ={
    val file = new File(labelPath)
    val in = new FileInputStream(file)
    var labelDS = new Array[Byte](file.length.toInt)
    using(new FileInputStream(file)) { source =>
    {
      in.read(labelDS)
    }
    }

    //32 bit integer  0x00000801(2049) magic number (MSB first--high endian)
    val magicLabelNum = ByteBuffer.wrap(labelDS.take(4)).getInt
    println(s"magicLabelNum=$magicLabelNum")
    //32 bit integer  60000            number of items
    val numOfLabelItems = ByteBuffer.wrap(labelDS.slice(4, 8)).getInt
    println(s"numOfLabelItems=$numOfLabelItems")
    //删掉前面的文件描述
    labelDS = labelDS.drop(8)
    //打印测试数据
    for ((e, index) <- labelDS.take(3).zipWithIndex) {
      println(s"image$index is $e")
    }

    labelDS
  }

  /**
    * 读取图像文件
    * @param imagesPath
    * @return
    */
  def loadImages(imagesPath: String):  Array[Array[Byte]] ={

    val file = new File(imagesPath)
    val in = new FileInputStream(file)
    var trainingDS = new Array[Byte](file.length.toInt)
    using(new FileInputStream(file)) { source =>
    {
      in.read(trainingDS)
    }
    }

    //32 bit integer  0x00000803(2051) magic number
    val magicNum = ByteBuffer.wrap(trainingDS.take(4)).getInt
    println(s"magicNum=$magicNum")
    //32 bit integer  60000            number of items
    val numOfItems = ByteBuffer.wrap(trainingDS.slice(4, 8)).getInt
    println(s"numOfItems=$numOfItems")
    //32 bit integer  28               number of rows
    val numOfRows = ByteBuffer.wrap(trainingDS.slice(8, 12)).getInt
    println(s"numOfRows=$numOfRows")
    //32 bit integer  28               number of columns
    val numOfCols = ByteBuffer.wrap(trainingDS.slice(12, 16)).getInt
    println(s"numOfCols=$numOfCols")

    trainingDS = trainingDS.drop(16)


    val itemsBuffer = new ArrayBuffer[Array[Byte]]
    for(i <- 0 until numOfItems){
      itemsBuffer += trainingDS.slice( i * numOfCols * numOfRows , (i+1) * numOfCols * numOfRows)
    }

    itemsBuffer.toArray
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
    val imagesFile = new File("C:\\Users\\42532\\Desktop\\test-images.idx3-ubyte");
    val labelsFile = new File("C:\\Users\\42532\\Desktop\\test-labels.idx1-ubyte");

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
