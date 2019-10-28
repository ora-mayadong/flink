package com.myd.cn.dataset

import org.apache.flink.api.java.aggregation.Aggregations
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.runtime.rest.messages.job.metrics.MetricsAggregationParameter.AggregationMode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._


/**
  * flink读取csv文件
  * author:jerry
  * email:dymllt@163.com
  */
object DatasetDemo {
  def main(args: Array[String]): Unit = {




    //1.获取执行环境,批处理运行环境,如果集群,用集群flink命令提交,直接会获取对应的flinK集群
    //val env:ExecutionEnvironment = ExecutionEnvironment.createRemoteEnvironment("jerry",6123,5)
    val env:ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment


    //2.通过读取文件,转换为DataSet
    val text = env.readCsvFile[(Int,Int,Int,Int,Int,Int)]("/home/jerry/wc.csv",ignoreFirstLine = true,lineDelimiter = "\n",fieldDelimiter = ",") //读入的每行数据个6个字段
    //val text = env.readCsvFile[(Int,Int,Int,Int,Int,Int)]("/home/jerry/wc.csv",ignoreFirstLine = true,lineDelimiter = "\n",fieldDelimiter = ",")


    //3.逻辑处理
   // val windowCount = text.filter(_.nonEmpty).flatMap(_.split(",")).map((_,1)).map(x=>WordCount(x._1,x._2)).groupBy(0).sum(1)
    val windowCount = text.aggregate(Aggregations.MIN,0)//.aggregate(Aggregations.MAX,1)



   //4.结果输出
    windowCount.print()


    //5.action,启动程序,batch 处理不需要
    //print()方法自动会调用execute()
    //env.execute("Offline WC")

  }

}
