package com.myd.cn.TwoStreamJoin

import java.io._
import java.util
import java.util.Properties

import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import org.apache.flink.table.expressions.UUID

import scala.io.Source


/**
  * 生成数据到kafka
  * 数据格式 Int类型,TimeStampStr,Int类型
  * author:jerry
  * email:dymllt@163.com
  *
  */
object KafkaProduerUtil {
  def sendMessageFromLogFile(file:File,topic:String,bokerList:String):Unit = {
      val props = new Properties()
    //props.put("metadata.broker.list", "hadoop26.geotmt.com:6667,hadoop206.geotmt.com:6667,hadoop207.geotmt.com:6667");
    props.put("metadata.broker.list", "xyy-hadoop-kafka-11.geotmt.com:6667,xyy-hadoop-kafka-12.geotmt.com:6667,xyy-hadoop-kafka-13.geotmt.com:6667")

    //配置value的序列化类
    props.put("serializer.class", "kafka.serializer.StringEncoder")
    //配置key的序列化类
    props.put("key.serializer.class", "kafka.serializer.StringEncoder")

    val producerConfig = new ProducerConfig(props)
    val produer = new Producer(producerConfig)

    var fr:FileReader = null
    var br:BufferedReader = null

    var line = null
    val source = Source.fromFile(file)
    try {

      //从字节数组byte[] 里面读取数据
//        br = new BufferedReader(fr)
//        fr = new FileReader(file)
      while (null !=  source.bufferedReader.readLine()  ) {
        System.out.println("============发送的数据============ " + line)
        //val key = util.UUID.randomUUID()
        //produer.send(new KeyedMessage[String,String](topic,line))
        println("================ ",line)

      }


     }catch {
      case e: IOException =>
        e.printStackTrace()
      case e:FileNotFoundException =>
          e.printStackTrace()
    }finally{
        if (null != fr) {
          try {
            source.bufferedReader().close()
          } catch {
            case e:IOException =>
            e.printStackTrace()
          }
        }
      }



  }

  def main(args: Array[String]): Unit = {
     sendMessageFromLogFile(new File("/home/jerry/wc"),"mykafka","jerry:9002")
  }

}
