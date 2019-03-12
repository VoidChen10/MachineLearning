package SparkMLlib.Classification

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

import SparkMLlib.Base.MNIST_Util


/**
  * @author voidChen
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
    val testLabelFilePath = "C:\\Users\\42532\\Desktop\\test-labels.idx1-ubyte"
    val testImageFilePath = "C:\\Users\\42532\\Desktop\\test-images.idx3-ubyte"


    val conf = new SparkConf().setMaster("local[*]").setAppName("NaiveBayesExample")
    val sc = new SparkContext(conf)

    val trainLabel = MNIST_Util.loadLabel(trainLabelFilePath)
    val trainImages = MNIST_Util.loadImages(trainImageFilePath)
    val testLabel = MNIST_Util.loadLabel(testLabelFilePath)
    val testImages = MNIST_Util.loadImages(testImageFilePath)

    //打印训练矩阵
    printMatrix(trainLabel,trainImages)
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

      //res.foreach(println(_))

      val tr = res.zip(testLabel)

      val sum = tr.map( f =>{
        if(f._1 == f._2.toInt) 1 else 0
      }).sum

      println("准确率为："+ sum.toDouble /tr.length)
    }
  }

  /**
    * 打印矩阵
    * @param label
    * @param images
    */
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
