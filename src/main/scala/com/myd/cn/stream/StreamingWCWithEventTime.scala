package com.myd.cn.stream

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.triggers.{CountTrigger, EventTimeTrigger}


/**
  *使用窗口函数处理流式数据
  * author:jerry
  * email:dymllt@163.com
  */
object StreamingWCWithEventTime {
  def main(args: Array[String]): Unit = {



    val senv:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //设置触发窗口函数执行的时间方式,用于更新水位线
    senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)



    val text = senv.socketTextStream("jerry",9001).assignTimestampsAndWatermarks(new CusTimeStampExtractor)


    val counts = text.filter(x=>(x.nonEmpty && !x.contains("-")) ).map{(m:String) =>(m.split(",")(0),1)}.keyBy(0)
      //.timeWindow(Time.seconds(10),Time.seconds(5))
      .window(TumblingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
      //.window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
      .allowedLateness(Time.seconds(5))
      //.trigger(EventTimeTrigger.create())
      .sum(1)

    counts.print

    senv.execute("EventTime processing example")
  }

}
