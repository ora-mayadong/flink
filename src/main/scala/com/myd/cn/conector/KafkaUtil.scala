package com.myd.cn.conector

import com.myd.cn.stream.CusTimeStampExtractor
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time


/**
  * Flink 连接 kafka处理数据
  * author:jerry
  * email:dymllt@163.com
  */
object KafkaUtil {

  def main(args: Array[String]): Unit = {

    //1.获取执行环境
    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //保存检查点状态，用于故障恢复
    senv.enableCheckpointing(5000) // checkpoint every 5000 msecs

    val props = new java.util.Properties()

    //props.setProperty("broker.list","jerry:9092")\
    //设置broker-list,zookeeper,group_id
    props.setProperty("bootstrap.servers","jerry:9092")
    props.setProperty("zookeeper.connect","jerry:2181/kafka0.9")
    props.setProperty("group.id","testp")
    props.setProperty("auto.offset.reset","latest")


    //2.读取数据（添加数据源）
    /**
      * If you have a problem with Kafka when using Flink,
      * keep in mind that Flink only wraps KafkaConsumer or KafkaProducer and your problem might be independent of Flink and sometimes can be solved by upgrading Kafka brokers,
      * reconfiguring Kafka brokers or reconfiguring KafkaConsumer or KafkaProducer in Flink.
      * Some examples of common problems are listed below.
      */
    val stream = senv.addSource(new FlinkKafkaConsumer09[String]("mykafka",new SimpleStringSchema(),props)).assignTimestampsAndWatermarks(new CusTimeStampExtractor)


    //3.业务逻辑,wc
    val count = stream.filter(x=>{
                        x.nonEmpty
                        !x.contains("-")
                      })
                      .map{(m:String) => (m.split(",")(0),1)} //split lines to get one col
                      .keyBy(0)
                      .timeWindow(Time.seconds(10),Time.seconds(5))
                      .sum(1)



    //4.输出结果
    count.print()

    //5.触发程序
    senv.execute("Streaming")






  }

}
