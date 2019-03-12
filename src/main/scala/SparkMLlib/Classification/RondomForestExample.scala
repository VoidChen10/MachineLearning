package SparkMLlib.Classification


import SparkMLlib.Base.MNIST_Util
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.RandomForest

object RondomForestExample {

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

    // Train a RandomForest model.
    // Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 10
    val categoricalFeaturesInfo = Map[Int, Int]()
    val numTrees = 3 // Use more in practice.
    val featureSubsetStrategy = "auto" // Let the algorithm choose.
    val impurity = "gini"
    val maxDepth = 5
    val maxBins = 32

    //处理成mlLib能用的基本类型 LabeledPoint
    if(trainLabel.length == trainImages.length) {
      //标签数量和图像数量能对上则合并数组  Array[(labe,images)]

      val data = trainLabel.zip(trainImages).map( d =>
        LabeledPoint(d._1.toInt, Vectors.dense(d._2.map(p => (p & 0xFF).toDouble)))
      )

      val trainRdd = sc.makeRDD(data)

      println("开始计算")
//      val model = NaiveBayes.train(trainRdd)
      val model = RandomForest.trainClassifier(trainRdd, numClasses, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

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
}
