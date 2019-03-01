package SparkMLlib.Classification

import java.io.{File, FileInputStream}
import java.nio.ByteBuffer

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.mutable.ArrayBuffer

/**
  * @author Chenyl
  * @date 2019/2/20 9:15
  */
object NaiveBayesExample {
  def main(args: Array[String]): Unit = {
    // 获取当前运行路径
    val userPath = System.getProperty("user.dir")
    val trainLabelFilePath = userPath + "/src/main/resources/data/train-labels.idx1-ubyte"
    val trainImageFilePath = userPath + "/src/main/resources/data/train-images.idx3-ubyte"

//    val testLabelFilePath = userPath + "/src/main/resources/data/t10k-labels.idx1-ubyte"
//    val testImageFilePath = userPath + "/src/main/resources/data/t10k-images.idx3-ubyte"
    val testLabelFilePath = "C:\\Users\\Void\\Desktop\\test-labels.idx1-ubyte"
    val testImageFilePath = "C:\\Users\\Void\\Desktop\\test-images.idx3-ubyte"


    val conf = new SparkConf().setMaster("local[*]").setAppName("NaiveBayesExample")
    val sc = new SparkContext(conf)

    val trainLabel = loadLabel(trainLabelFilePath)
    val trainImages = loadImages(trainImageFilePath)
    val testLabel = loadLabel(testLabelFilePath)
    val testImages = loadImages(testImageFilePath)

    //打印训练矩阵
//    printMatrix(trainLabel,trainImages)
    //打印测试矩阵
//    printMatrix(testLabel,testImages)


    //处理成mlLib能用的基本类型 LabeledPoint
    if(trainLabel.length == trainImages.length) {
      //标签数量和图像数量能对上则合并数组  Array[(labe,images)]

      val data = trainLabel.zip(trainImages).map( d =>
        LabeledPoint(d._1.toInt, Vectors.dense(d._2.map(p => (p & 0xFF).toDouble)))
      )

      val trainRdd = sc.makeRDD(data)

      println("开始计算")
      val model = NaiveBayes.train(trainRdd)

      println("检验结果")
      val testData = testImages.map(d => Vectors.dense(d.map(p => (p & 0xFF).toDouble )))
      val testRDD = sc.makeRDD(testData)
      val res = model.predict(testRDD).map(l => l.toInt).collect()

      res.foreach(println(_))

      val tr = res.zip(testLabel)

      val sum = tr.map( f =>{
        if(f._1 == f._2.toInt) 1 else 0
      }).sum

      println("准确率为："+ sum.toDouble /tr.length)

    }

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

  def printMatrix(label:Array[Byte],images: Array[Array[Byte]]): Unit ={
    label.zip(images).filter(n => n._1.toInt == 1).take(2).foreach(d =>{
      println("图片标签：" + d._1.toInt)
      var i = 0;
      d._2.foreach(p =>{
//          print((p & 0xFF) + "   ")
        print(p + " ")
        i = i + 1
        if(i == 28) {
          println()
          i = 0
        }

      })
    })
  }


}
