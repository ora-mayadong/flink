package com.myd.cn.stream

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.watermark.Watermark
import org.apache.flink.streaming.api.windowing.time.Time


/**
  * 自定义source function设置 Event Time为 Water Mark
  * author:jerry
  * email:dymllt@163.com
  */
object SourceFunction {


  def main(args: Array[String]): Unit = {

      //1.获取数据环境
      val senv:StreamExecutionEnvironment  = StreamExecutionEnvironment.getExecutionEnvironment

      //2.获取数据(从文件,seq)
      //val input = senv.readTextFile("/home/jerry/wc")
      val input = List(("a",1L,1),("b",1L,1),("b",3L,1))

     //3.逻辑处理
     val source:DataStream[(String,Long,Int)] = senv.addSource(new SourceFunction[(String,Long,Int)](){

       override def run(ctx: SourceContext[(String, Long, Int)]): Unit = {
         input.foreach(value => {

            //调用collectWithTimestamp 增加 Event Time抽取,将第二个字段作为数据产生时间
           ctx.collectWithTimestamp(value,value._2)
           //创建watermark,设置最大时延(大于该时延则为异常,迟到数据不予处理)
           ctx.emitWatermark(new Watermark(value._2-1))

         })

         //设置初始Watermark,用于后续覆盖
         ctx.emitWatermark(new Watermark(Long.MaxValue))
       }
       override def cancel(): Unit = {}
     })

    //4.分区指定key
    val text = source.keyBy(0).timeWindow(Time.seconds(2)).sum(2)

    //5.结果输出
    text.print()

    //6.job触发
    senv.execute("Job")
  }

}
