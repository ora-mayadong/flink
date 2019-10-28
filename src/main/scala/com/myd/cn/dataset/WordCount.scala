package com.myd.cn.dataset

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.core.fs.FileSystem
import org.apache.flink.table.api.scala._
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.TableEnvironment

//case class 可无类内容
case class WordCount(word:String,count:Long) extends  Serializable


/**
  * flink读取文本文件，进行wordCount
  * * author:jerry
  * * email:dymllt@163.com
  *
  */
object WordCount {

  def main(args: Array[String]): Unit = {


    //获取执行环境,批处理运行环境,如果集群,用集群flink命令提交,直接会获取对应的flinK集群
    //val env:ExecutionEnvironment = ExecutionEnvironment.createRemoteEnvironment("jerry",6123,5)
    val env:ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    val tableEnv = TableEnvironment.getTableEnvironment(env)

    //val env:ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment
    //ExecutionEnvironment.createLocalEnvironment(1)

    //通过读取文件,转换为DataSet
    val text = env.readTextFile("/home/jerry/wc")
    //转换数据，分组，窗口时间是5s(计算窗口为5s),滑动窗口为1s,聚合
    //val windowCount = text.flatMap{_.split(",")}.filter{x=>(x!=""&& null != x)}.map((_,1)).map{w=>com.myd.cn.dataset.WordCount(w._1,w._2)}
    //val windowCount = text.filter{x=>(!"".equals(x))}.flatMap{_.split(",")}.map((_,1)).map{x=>com.myd.cn.dataset.WordCount(x._1,x._2)}
    //.keyBy("word")
    //.sum("count") //以count列进行求和

    //val windowCount = text.filter(_.nonEmpty).flatMap(_.split(",")).map((_,1)).map(x=>WordCount(x._1,x._2))
    //keyBy("word").sum("count") //根据 word列值进行分组，对count列进行求和


    val windowCount = text.filter(_.nonEmpty).flatMap(_.split(",")).map((_,1)).map(x=>WordCount(x._1,x._2)).groupBy(0).sum(1)

    windowCount.print()


    //保存结果
    //windowCount.writeAsCsv("/tmp/csv",rowDelimiter = "\n",fieldDelimiter = ",",FileSystem.WriteMode.OVERWRITE)




    //以单线程打印结果(设置并行数）
    //    windowCount.print().setParallelism(1)
    //
    //    println(windowCount.getExecutionEnvironment.getConfig)



    //action,启动程序,batch 处理不需要
    //print()方法自动会调用execute()
    //env.execute("Offline WC")

  }

}
