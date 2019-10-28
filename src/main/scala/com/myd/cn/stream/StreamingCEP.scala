package com.myd.cn.stream



import org.apache.flink.cep.scala.CEP
import org.apache.flink.cep.scala.pattern.Pattern
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._


/**
  * Cep复杂事件处理
  * 处理用户在某个时间窗口内点击数量大于2的用户
  *
  */
object StreamingCEP {

  case class CepWC(id:String,time:Long,count:Long) extends Serializable
  case class CepSubWC(userId:String,clickcount:Long) extends  Serializable

  def main(args: Array[String]): Unit = {
    //1.获取执行环境
    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    //设置flink处理事件的时间方式
    senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    //2.读取数据源
    val inputStream:DataStream[CepWC] = senv.socketTextStream("jerry",9001).filter(x=>{
      //过滤空行；为空格的空行；不包含 "-"的行
      null!=x && !"".equals(x)&& !x.contains("-")
    }).assignTimestampsAndWatermarks(new CusTimeStampExtractor).map(x=>{
      val fields = x.split(",")
      CepWC(fields(0).trim,fields(1).trim.toLong,fields(2).trim.toLong)
    })

    //3.逻辑处理 三个Pattern
    val pattern = Pattern.begin[CepWC]("start").where(_.id == "a")
      .next("middle").where(_.count>2)
        .followedBy("end").where(_.id.contains("a"))
      //获取时间结果
      //指定循环次数(2-4次),optional
      // 要么不触发，要么触发指定次数(2-4次)
      //贪婪模式，匹配成功的前提下，尽可能的触发
      //触发一次或多次
      .times(2,4).greedy

    //将创建好的patter应用在输入事件流上
    val patterStream = CEP.pattern(inputStream,pattern)

    //获取第一个匹配模式处理的数据，根据逻辑条件进行抽取数据，
    //将该函数放在后面的   cep => selectFn(cep)  否则会报：前向引用在未声明定义之前
    def selectFn(pattern:scala.collection.Map[String,Iterable[CepWC]]):CepWC = {
      //获取patter 中的startEvent
      //val startEvent = patter.get("start").iterator.next().toList.head
      val cepWC = pattern.get("start").iterator.next().toList.head
      //CepSubWC(cepWC.id,cepWC.count)
      //CepWC(cepWC.id,cepWC.time,cepWC.count)
      cepWC
    }



    //获取时间触发结果
    val result:DataStream[CepWC] = patterStream.select(cep => selectFn(cep))



    //4.输出结果
    result.print()


    //5.触发出现
    senv.execute("Cep streaming")

  }

}
