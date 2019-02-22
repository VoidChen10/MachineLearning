package SparkMLlib.Clustering

import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author Chenyl
  * @date 2019/2/22 15:10
  */
object KMeansClustering {
  def main (args: Array[String]) {

    val trainingPath = "src/main/resources/Wholesale customers data_training.txt"  //训练数据集文件路径
    val testPath = "src/main/resources/Wholesale customers data_test.txt" //测试数据集文件路径

    val numClusters = 8  //聚类的个数
    val numIterations = 30  //K-means 算法的迭代次数
    val runTimes = 3   //K-means 算法 run 的次数

    val conf = new SparkConf().setAppName("Spark MLlib Exercise:K-Means Clustering")
    conf
      //TODO: 生成打包前，需注释掉此行
      .setMaster("local[*]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")  //设置日志级别，info会有很多运行日志出现，也可以打开看看

    /**
      *Channel Region Fresh Milk Grocery Frozen Detergents_Paper Delicassen
      * 2 3 12669 9656 7561 214 2674 1338
      * 2 3 7057 9810 9568 1762 3293 1776
      * 2 3 6353 8808 7684 2405 3516 7844
      */
    val rawTrainingData = sc.textFile(trainingPath) //加载测试数据

    //去表头处理
    val parsedTrainingData = rawTrainingData.filter(!isColumnNameLine(_)).map(line => {
      Vectors.dense(line.split("\t").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
    }).cache()

//    findK(parsedTrainingData)

    // Cluster the data into two classes using KMeans

    var clusterIndex:Int = 0
    //使用K-Means训练
    val clusters:KMeansModel = KMeans.train(parsedTrainingData, numClusters, numIterations,runTimes)

    println("Cluster Number:" + clusters.clusterCenters.length)
    println("Cluster Centers Information Overview:")
    clusters.clusterCenters.foreach(x => {
      println("Center Point of Cluster " + clusterIndex + ":")
      println(x)
      clusterIndex += 1
    })

    //begin to check which cluster each test data belongs to based on the clustering result
    val rawTestData = sc.textFile(testPath) //加载测试数据
    val parsedTestData = rawTestData.map(line =>{
      Vectors.dense(line.split("\t").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
    })

    parsedTestData.collect().foreach(testDataLine => {
      val predictedClusterIndex: Int = clusters.predict(testDataLine) //使用训练好的模型进行分类
      println("The data " + testDataLine.toString + " belongs to cluster " +predictedClusterIndex)
    })
    println("Spark MLlib K-means clustering test finished.")



  }

  private def isColumnNameLine(line:String):Boolean = {
    if (line != null && line.contains("Channel")) true
    else false
  }

  /**
    * 查看最佳的K值
    * @param parsedTrainingData
    */
  private def findK(parsedTrainingData:org.apache.spark.rdd.RDD[org.apache.spark.mllib.linalg.Vector]): Unit ={
    val ks:Array[Int] = Array(3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20)
    ks.foreach(cluster => {
      val model:KMeansModel = KMeans.train(parsedTrainingData, cluster,30,1)
      val ssd = model.computeCost(parsedTrainingData)
      println("sum of squared distances of points to their nearest center when k=" + cluster + " -> "+ ssd)
    })
  }
}
