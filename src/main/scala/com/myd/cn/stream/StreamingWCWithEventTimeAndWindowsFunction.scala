package com.myd.cn.stream

import org.apache.flink.api.common.functions.ReduceFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *使用 自定义函数处理流失数据wordcount
  * author:jerry
  * email:dymllt@163.com
  * 数据格式:
  * a,1569486060000,1
  * a,1569486063000,1
  * a,1569486064000,1
  * a,1569486066000,1
  * a,1569486069000,1
  */
object StreamingWCWithEventTimeAndWindowsFunction {
  def main(args: Array[String]): Unit = {


    val senv: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //设置触发窗口函数执行的时间方式,用于更新水位线
    senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)


    val text = senv.socketTextStream("jerry", 9001).assignTimestampsAndWatermarks(new CusTimeStampExtractor)


    val counts = text.filter(x => (x.nonEmpty && !x.contains("-"))).map { (m: String) => (m.split(",")(0), 1) }.keyBy(0)
      //.timeWindow(Time.seconds(10),Time.seconds(5))
      //.window(TumblingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      .window(SlidingEventTimeWindows.of(Time.seconds(10),Time.seconds(5)))
      //.allowedLateness(Time.seconds(5))
      //.trigger(EventTimeTrigger.create())
      .reduce(new ReduceFunction[(String, Int)] {
               override def reduce(t: (String, Int), t1: (String, Int)): (String, Int) = {
                    (t._1,t._2+t1._2)

                }
      })
    //.reduce((t1,t2)=>(t1._1,t2._2+t1._2))


    counts.print

    senv.execute("EventTime processing example")

  }
}
