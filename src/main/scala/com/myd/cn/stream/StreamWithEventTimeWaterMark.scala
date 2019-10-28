package com.myd.cn.stream

import java.util.Date
//引入隐式类型推断管理
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

case class NginxLog(userId:String,time:Long,count:Long) extends Serializable





/**
  *flink  使用event Time处理数据
  * author:jerry
  * email:dymllt@163.com
  */
object StreamWithEventTimeWaterMark {
  def main(args: Array[String]): Unit = {

    val senv:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //指定系统时间概念为EventTime,即根据数据中的字段来设置水位线(时间戳是毫秒级),(自定义,系统设置eventTime任选一个)
    //senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)

    //设置临时数据
    //val input = senv.fromCollection(List(("a",1L,21),("b",1L,1),("a",3L,13)))
    val streaming = senv.socketTextStream("jerry",9001,'\n')


    //摄用系统默认的Ascending(升序)分配时间和Watermark,因为时间的增加的,升序的,所以水位线也是按升序进行更新
    //  即指定数据中那个字段是eventTime
    //val withEventTimeWaterMarks = input.assignAscendingTimestamps(t=>t._3)
    //val withEventTimeWaterMarks = streaming.filter(_.nonEmpty).flatMap(_.split(",")).map{x=>{
    val input = streaming.filter(x=>(x.nonEmpty && !x.contains("-"))).map{x=>{
      val fields = x.split(",")
      val userid = fields(0)
      //val date = DateUtil.getDateFromDate(fields(1).toLong)
      val time = fields(1).toLong
      val count = fields(2).toLong
      //NginxLog(userid,time,count)
      (userid,time,count)
    }}

    val withEventTimeWaterMarks = input.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[(String,Long,Long)](Time.seconds(5)) {
      override def extractTimestamp(t: (String, Long, Long)): Long = t._2
    })
    //val withEventTimeWaterMarks = input.assignAscendingTimestamps(t=>t._2)




    //对数据集进行窗口运算 ,设置窗口为10,未设置滑动窗口(或者tumbling窗口,用于每十分钟数据统计:非累加的统计)
    //val result = withEventTimeWaterMarks.keyBy(0).timeWindow(Time.seconds(10)).sum("_2") //从1开始,计算第2个数据和

    //withEventTimeWaterMarks.print().setParallelism(1)


    //设置窗口函数时间,滑动窗口时间,
    val result = withEventTimeWaterMarks.keyBy(x=>x._1).timeWindow(Time.seconds(10)).sum(2)
      .map{
      x=>(x._1,x._3)
    }



    result.print().setParallelism(1)




//    val  result = streaming.filter(_.nonEmpty).map(x=>{
//      val fields = x.split(",")
//      (fields(0),fields(1).toLong,fields(2).toLong)
//    }).keyBy(0).timeWindow(Time.seconds(5),Time.seconds(5)).sum(2)
//
//    result.print().setParallelism(1)



    //启动作业
    senv.execute("基于eventTime的  window operator")
  }
}
