package com.myd.cn.stream

import java.text.SimpleDateFormat

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

/**
  * 自定义设置event time 为 watermark
  * 传入参数是string(get timestamp from spiltted string)
  * author:jerry
  * email:dymllt@163.com
  */
class JoinTimeExtractor  extends  AssignerWithPeriodicWatermarks[String]{

  //获取当前waterMark
  var waterMarker:Watermark = _

  //获取当前数据时间戳
  var currentMaxTimeStamp:Long = _


  //flink 默认定时（不自定义设置）获取CurrentWatermark
  override def getCurrentWatermark: Watermark = {
    waterMarker = new Watermark(currentMaxTimeStamp-5000)
    waterMarker
  }

  override def extractTimestamp(event: String, previousTimeStamp: Long): Long = {
    var currentTimestamp = 0L

    if(event.nonEmpty && !event.contains("-")){
      currentTimestamp = event.split(",")(3).trim.toLong

      val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      println("======当前时间戳======",simpleDateFormat.format(currentTimestamp),"-- 最大时间戳 --",simpleDateFormat.format(currentMaxTimeStamp),"水位线: ",waterMarker.toString,"水位线时间:",simpleDateFormat.format(waterMarker.getTimestamp))
      //从当前水位线和当前eventTime 中 获取最大时间戳作为水位线


    }
    currentMaxTimeStamp = math.max(currentTimestamp,currentMaxTimeStamp)
    currentTimestamp

  }
}
