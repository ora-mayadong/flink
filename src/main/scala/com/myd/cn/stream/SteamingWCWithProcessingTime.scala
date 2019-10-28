package com.myd.cn.stream


import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *
  * 流式处理wordCount
  * author:jerry
  * email:dymllt@163.com
  */
object SteamingWCWithProcessingTime {
  def main(args: Array[String]): Unit = {

    val senv:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment


    val text = senv.socketTextStream("jerry",9001)

    val counts = text.map{(m:String) =>(m.split(",")(0),1)}.keyBy(0)
      .timeWindow(Time.seconds(10),Time.seconds(5)).sum(1)

    counts.print

    senv.execute("Procession Time process example")



  }
}
