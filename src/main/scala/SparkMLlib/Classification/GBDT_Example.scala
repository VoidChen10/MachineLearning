package SparkMLlib.Classification

import SparkMLlib.Base.MNIST_Util
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.{GradientBoostedTrees}
import org.apache.spark.mllib.tree.configuration.BoostingStrategy
import org.apache.spark.{SparkConf, SparkContext}

object GBDT_Example {

  def main(args: Array[String]): Unit = {
    // 获取当前运行路径
    val userPath = System.getProperty("user.dir")
    val trainLabelFilePath = userPath + "/src/main/resources/data/train-labels.idx1-ubyte"
    val trainImageFilePath = userPath + "/src/main/resources/data/train-images.idx3-ubyte"

    val testLabelFilePath = userPath + "/src/main/resources/data/t10k-labels.idx1-ubyte"
    val testImageFilePath = userPath + "/src/main/resources/data/t10k-images.idx3-ubyte"
//        val testLabelFilePath = "C:\\Users\\42532\\Desktop\\test-labels.idx1-ubyte"
//        val testImageFilePath = "C:\\Users\\42532\\Desktop\\test-images.idx3-ubyte"


    val conf = new SparkConf().setMaster("local[*]").setAppName("NaiveBayesExample")
    val sc = new SparkContext(conf)

    val trainLabel = MNIST_Util.loadLabel(trainLabelFilePath)
    val trainImages = MNIST_Util.loadImages(trainImageFilePath)
    val testLabel = MNIST_Util.loadLabel(testLabelFilePath)
    val testImages = MNIST_Util.loadImages(testImageFilePath)

    // Train a GradientBoostedTrees model.
    // The defaultParams for Classification use LogLoss by default.
    val boostingStrategy = BoostingStrategy.defaultParams("Classification")
    boostingStrategy.numIterations = 3 // Note: Use more iterations in practice.
    boostingStrategy.treeStrategy.numClasses = 2
    boostingStrategy.treeStrategy.maxDepth = 5
    // Empty categoricalFeaturesInfo indicates all features are continuous.
    boostingStrategy.treeStrategy.categoricalFeaturesInfo = Map[Int, Int]()

    //处理成mlLib能用的基本类型 LabeledPoint
    if(trainLabel.length == trainImages.length) {
      //标签数量和图像数量能对上则合并数组  Array[(labe,images)]

      val data = trainLabel.zip(trainImages)
        .filter(d => {d._1.toInt == 0 || d._1.toInt == 1}) //梯度树不能处理多分类问题，这里处理成判断0和1
        .map( d =>
        LabeledPoint(d._1.toInt, Vectors.dense(d._2.map(p => (p & 0xFF).toDouble)))
      )

      val trainRdd = sc.makeRDD(data)

      println("开始计算")

      val model = GradientBoostedTrees.train(trainRdd, boostingStrategy)
      model.save(sc,userPath + "/src/main/resources/model/GBDT")

      println("检验结果")
      val testData = testLabel.zip(testImages)
        .filter(d => {d._1.toInt == 0 || d._1.toInt == 1}) //梯度树不能处理多分类问题，这里处理成判断0和1
        .map(d =>( d._1.toInt,Vectors.dense(d._2.map(p => (p & 0xFF).toDouble )) ))
      val testRDD = sc.makeRDD(testData.map(_._2))
      val res = model.predict(testRDD).map(l => l.toInt).collect()

      //res.foreach(println(_))

      val tr = res.zip(testData.map(_._1))

      val sum = tr.map( f =>{
        if(f._1 == f._2.toInt) 1 else 0
      }).sum

      println("准确率为："+ sum.toDouble /tr.length.toDouble)

    }
  }
}
