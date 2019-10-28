package com.myd.cn.stream

import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *自定义聚合函数处理流失窗口数据
  * author:jerry
  * email:dymllt@163.com
  */
object StreamingWCWithAggregateFunction  extends {



  def main(args: Array[String]): Unit = {

    val senv:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //设置计算当前水位线的时间为EventTime
    senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //接受数据
    val text = senv.socketTextStream("jerry",9001).assignTimestampsAndWatermarks(new CusTimeStampExtractor)

    //wc  map(x=>(x(0),x(1).toLong) 字段,字段值
    val counts = text.filter(x=>(x.nonEmpty && !x.contains("-"))).map{(m:String) => (m.split(","))}.map(x=>(x(0),x(1).trim.toLong)).keyBy(_._1)
      //指定窗口类型
      .window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
      //.timeWindow(Time.seconds(10),Time.seconds(5))
      //指定聚合函数逻辑,根据ID第二个字段求平均值
      .aggregate(new MyAverageAggregate)
      //  .reduce((t1,t2)=>(t1._1,t1._2+t2._2))

    counts.print



    senv.execute("计算平均值")


  }

}
