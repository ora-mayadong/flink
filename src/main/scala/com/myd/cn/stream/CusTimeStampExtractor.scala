package com.myd.cn.stream

import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

/**
  *自定义设置event time 为 watermark
  *author:jerry
  * email:dymllt@163.com
  */
class CusTimeStampExtractor  extends  AssignerWithPeriodicWatermarks[String] with  Serializable {
    //获取当前的waterMark对象
    var waterMarker : Watermark = _

  //定义常量,进行赋值
  var currentMaxTimestamp:Long = _


  //先获取当前时间戳,生成水位线
  override def getCurrentWatermark: Watermark = {
    // println("=====当前时间=====  ",currentMaxTimestamp)

    //根据最大时间时间减去最大的延长度,即:迟来在5s之内(包含5s)的数据会被处理,大于5s的延迟不被处理
    waterMarker = new Watermark(currentMaxTimestamp-5000)
    //new Watermark(time-5000)

    waterMarker

  }


  //再最大时间戳设置当前的最新时间戳
  override def extractTimestamp(element: String, previousElementTimestamp: Long): Long = {
    //waterMark


    var currentTimestamp = 0L
    //println(element)
   if(element.nonEmpty && !element.contains("-")){
     //println("============",element)

     //取第二个字段作为时间戳
     currentTimestamp = element.split(",")(1).trim.toLong
     val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
     //println("======当前时间戳======",currentTimestamp,"-- 最大时间戳 --",currentMaxTimestamp,"水位线: ",waterMarker.toString,"水位线时间:",simpleDateFormat.format(waterMarker.getTimestamp))

     println("======当前时间戳======",simpleDateFormat.format(currentTimestamp),"-- 最大时间戳 --",simpleDateFormat.format(currentMaxTimestamp),"水位线: ",waterMarker.toString,"水位线时间:",simpleDateFormat.format(waterMarker.getTimestamp))
   }

    //从当前时间戳,水位线时间戳取最大值,用于计算延迟数据
    currentMaxTimestamp = math.max(currentTimestamp,currentMaxTimestamp)

    //返回值
    currentTimestamp
  }


}
