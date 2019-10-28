package com.myd.cn.stream

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark

/**
  * 自定义获取数据eventTime 作为waterMark
  * author:jerry
  * email:dymllt@163.com
  */
object TimeLagStampExtractor extends AssignerWithPeriodicWatermarks[String]{

  //最大延迟
  val maxOutOfOrderness = 5000L //1s延迟

  //声明当前时间戳
  var currentMaxTimestamp:Long = _

  override def extractTimestamp(element: String, previousElementTimestamp: Long): Long = {
    val timestamp = element.split(",")(1).toLong
    currentMaxTimestamp = math.max(timestamp,currentMaxTimestamp)
    timestamp
  }

  //覆写 接口的方法 getCurrentWatermark
  override def getCurrentWatermark: Watermark = {
    //new Watermark(System.currentTimeMillis())
    new Watermark(currentMaxTimestamp-maxOutOfOrderness)
  }


}
