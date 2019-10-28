package com.myd.cn.stream

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time


case class WC(word:String,flag:Long) extends Serializable

/**
  * 数据格式:
  * a,1569486060000,1
  * a,1569486063000,1
  * a,1569486064000,1
  * a,1569486066000,1
  * a,1569486069000,1
  *
  * author:jerry
  * email:dymllt@163.com
  *
  */
object StreamingWordCount {
  def main(args: Array[String]): Unit = {



    val env:StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
   // val env:StreamExecutionEnvironment = StreamExecutionEnvironment.createRemoteEnvironment("jerry",6123,5)

    val hostName:String = try{
      ParameterTool.fromArgs(args).get("hostname")
    }catch {
      case  e:Exception => {
        println("未输入正确的netcat主机")
        return
      }
    }

    //netcat连接端口
    val port:Int = try {

      ParameterTool.fromArgs(args).getInt("port")
    }catch {
      case  e:Exception=>{
        println("未输入正确的netcat端口")
        return
      }
    }

    //通过socket连接到netcat,以换行分割数据，模拟流数据
    val text = env.socketTextStream(hostName,port,'\n')

    //转换数据，分组，窗口时间是5s(计算窗口为5s),滑动窗口为1s,聚合
    val windowCount = text.filter(_.nonEmpty).flatMap{_.split(",")}.filter(_.nonEmpty).map((_,1)).map{w=>WC(w._1,w._2)}
      .keyBy("word")
      .timeWindow(Time.seconds(10),Time.seconds(5))
      .sum(1)

    //以单线程打印结果(设置并行数）
    windowCount.print().setParallelism(1)

    //流处理需要执行环境
    env.execute("Flink window com.myd.cn.dataset.WordCount")



  }
}
