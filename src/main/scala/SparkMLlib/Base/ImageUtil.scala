package SparkMLlib.Base

import java.awt.image.BufferedImage


import scala.collection.mutable.ArrayBuffer

/**
  * @author voidChen
  * @date 2019/2/27 14:47
  */
object ImageUtil {



  /*  把图片转成灰度值数组
   *  原理其实还蛮简单的，就是按比例来取RGB三色，例如按比例3:6:1，就是R*0.3 + G*0.59 + B*0.11得出灰度值
   */
  def getGrayArray(img:BufferedImage): Array[Byte] = {
    //宽度（行）
    val width = img.getWidth
    //高度（列）
    val height = img.getHeight


    var bytes = ArrayBuffer[Byte]()
    for(j <- 0 until height ; i <- 0 until width ){
      val color = img.getRGB(i,j)
      val r = (color >> 16) & 0xff
      val g = (color >> 8) & 0xff
      val b = color & 0xff
      val gray = 255 - (0.3 * r + 0.59 * g + 0.11 * b).toInt

      bytes += (gray & 0xFF).toByte

    }

    bytes.toArray
  }


  /*  把图片转成灰度值图片
   *  原理其实还蛮简单的，就是按比例来取RGB三色，例如按比例3:6:1，就是R*0.3 + G*0.59 + B*0.11得出灰度值
   */
  def getGrayImg(img:BufferedImage): BufferedImage = {
    //宽度（行）
    val width = img.getWidth
    //高度（列）
    val height = img.getHeight

    //创建一个灰度图对象
    val grayImg = new BufferedImage(width,height,img.getType)

    for(i <- 0 until width ; j <- 0 until height){
      val color = img.getRGB(i,j)
      val r = (color >> 16) & 0xff
      val g = (color >> 8) & 0xff
      val b = color & 0xff
      val gray =255 - (0.3 * r + 0.59 * g + 0.11 * b).toInt


      val newPixel = colorToRGB(256, gray, gray, gray)
      grayImg.setRGB(i,j,newPixel)
    }

    grayImg
  }


  /**
    * 重新拼装颜色
    * @param alpha
    * @param red
    * @param green
    * @param blue
    * @return
    */
  def colorToRGB(alpha:Int, red:Int,green:Int, blue:Int):Int = {

    var newPixel = 0
    //先直接给出最大值，预留位数，比如alpha = 255 ,则在二进制里面就占了8位，
    // 不过这个java里面最高位是符号位，255弄出来的int不会超过8位？
    newPixel += alpha
    //下面就是由低位开始往高位塞
    newPixel = newPixel << 8
    newPixel += red
    newPixel = newPixel << 8
    newPixel += green
    newPixel = newPixel << 8
    newPixel += blue

    newPixel

  }

}
