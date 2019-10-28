package com.myd.cn.TwoStreamJoin

import com.myd.cn.stream.JoinTimeExtractor
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.scala._

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09

/**
  * 两个DataStream进行join操作
  * author:jerry
  * email:dymllt@163.com
  */
object StreamIntervalJoin {
  def main(args: Array[String]): Unit = {
     //1.获取当前执行环境
     val senv = StreamExecutionEnvironment.getExecutionEnvironment
     //以数据的产生时间处理
     //senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
      //senv.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    //2.读取数据（构造数据）
    //(userLog)
//    val dataStream1:DataStream[(Int,String,Long,Long)] = senv.fromElements(
//          (1,"foo",1,1569486060000L),
//          (2,"bar",2,1569486063000L),
//          (3,"dag",3,1569486064000L),
//          ( 2,"cdg",4,1569486066000L),
//          ( 4,"jog",2,1569486069000L)
//    )
//    //adsLog
//    val dataStream2:DataStream[(Int,String,Long)] = senv.fromElements(
//      (1,"NBA","sport",1569486060000L),
//      (2,"DayUp","entertainment",569486063000L),
//      (3,"ECUP","sport",1569486064000L),
//      ( 2,"Qcamp","entertainment",1569486066000L),
//      ( 4,"ACUP","sport",1569486069000L)
//    )
    val props = new java.util.Properties()

    //props.setProperty("broker.list","jerry:9092")\
    //设置broker-list,zookeeper,group_id
    props.setProperty("bootstrap.servers","jerry:9092")
    props.setProperty("zookeeper.connect","jerry:2181/kafka0.9")
    props.setProperty("group.id","testp")
    props.setProperty("auto.offset.reset","latest")

    //点击流
    val dataStream1 = senv.addSource(new FlinkKafkaConsumer09[String]("clickTopic",new SimpleStringSchema(),props)).assignTimestampsAndWatermarks(new JoinTimeExtractor).filter(x=>{
      x.nonEmpty
      !x.contains("-")
    }).map(clickLog=>{
      val fields = clickLog.split(",")
      ClickLog(fields(0).toInt,fields(1),fields(2).toLong,fields(3).toLong)

    })

    //广告流
    val dataStream2 = senv.addSource(new FlinkKafkaConsumer09[String]("adTopic",new SimpleStringSchema(),props)).assignTimestampsAndWatermarks(new JoinTimeExtractor).filter(x=>{
      x.nonEmpty
      !x.contains("-")
    }).map(adLog=>{
      val fields = adLog.split(",")
      AdLog(fields(0).toLong,fields(1),fields(2),fields(3).toLong)
    })

    //3.处理逻辑
    val join = dataStream1.join(dataStream2)
      .where(_.adId) //指定dataStream1(第一个dataStream的关联key on 左边的关联字段
      .equalTo(_.adId) //指定dataStream2(第一个stream的关联key) on 条件的关联字段

      .window(TumblingEventTimeWindows.of(Time.seconds(10))) //窗口滚动时间
      .apply((x,y)=>(x.username,y.adName,y.adType))



    //4.输出（打印）结果
    //join.writeAsCsv("/home/jerry/join")
    join.print()

    //5.触发执行
    senv.execute("Two Steam join")


  }
  case class ClickLog(userId:Int,username:String,adId:Long,emitTime:Long)
  case class AdLog(adId:Long,adName:String,adType:String,emitTime:Long)
}
