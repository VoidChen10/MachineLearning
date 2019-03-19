package SparkMLlib.Clustering

import org.apache.spark.SparkConf
import org.apache.spark.mllib.clustering.StreamingKMeans
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.{Seconds, StreamingContext}
/**
  * @Author: voidChen
  * @Date: 2019/3/19 17:24
  * @Version 1.0
  */
object StreamingKMeansExample {
  def main(args: Array[String]) {
    val trainingPath =  "D:\\data\\train\\"  //训练数据集文件路径
//    val testPath =  userPath + "/src/main/resources/data/StreamingKmeans-test.txt" //测试数据集文件路径
    val testPath =  "D:\\data\\test\\" //测试数据集文件路径

    val conf = new SparkConf().setAppName("StreamingKMeansExample")
      .setMaster("local[*]") //TODO: 生成打包前，需注释掉此行

    val ssc = new StreamingContext(conf, Seconds(10)) //数据量太少这里设置了1秒给它。。

    val trainingData = ssc.textFileStream(trainingPath)
      .filter(!isColumnNameLine(_))
      .map(line => {//处理数据成标准向量
        println(line)
        Vectors.dense(line.split("\t").map(_.trim).filter(!"".equals(_)).map(_.toDouble))
      })
    val testData = ssc.textFileStream(testPath).map(LabeledPoint.parse)


    val model = new StreamingKMeans()
      .setK(8) //聚类中心
      .setDecayFactor(1.0) //损失因子
      .setRandomCenters(8, 0.0) //初始化随机中心，只需要维数。 第一个参数是维数，第二个参数是簇权重

    model.trainOn(trainingData)
    model.predictOnValues(testData.map(lp => (lp.label, lp.features))).print()

    ssc.start()
    ssc.awaitTermination()
  }


  /**
    * 只是用来去掉表头
    * @param line
    * @return
    */
  def isColumnNameLine(line:String):Boolean = {
    if (line != null && line.contains("Channel")) true
    else false
  }
}
