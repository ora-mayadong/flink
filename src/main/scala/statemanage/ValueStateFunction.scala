package statemanage

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector


/**
  * 流处理模式中保存某个key的中间计算结果
  * author:jerry
  * email:dymllt@163.com
  */
object ValueStateFunction {
  def main(args: Array[String]): Unit = {
    //1.获取执行环境
    val senv = StreamExecutionEnvironment.getExecutionEnvironment


    //2.读取数据，构造数据
    val inputStream:DataStream[(Int,Long)] = senv.fromElements(
      (2,21L),
      (4,1L),
      (5,4L)
    )

    //3.逻辑处理
    inputStream.keyBy(0).flatMap{
      //定义算子内部函数,入参：(Int,Long) ；出参：(Int,Long,Long
      new RichFlatMapFunction[(Int,Long),(Int,Long,Long)] {
        private var leastValueState:ValueState[Long] = _

        override def open(parameters: Configuration): Unit = {
          //创建ValueStateDescriptor,定义状态名称为leastValue,并指定数据类型
          val leastValuestateDescriptor = new ValueStateDescriptor[Long]("leastValue",classOf[Long])
          //通过 getRuntimeContext 获取state
          leastValueState = getRuntimeContext.getState(leastValuestateDescriptor)
        }

        override def flatMap(in: (Int, Long), out: Collector[(Int, Long, Long)]): Unit = {
            //获取状态里面的最小值
            val leastValue = leastValueState.value()

          //当前指标大于最小值，直接输出数据
          if(in._2 > leastValue){
            out.collect((in._1,in._2,leastValue))
          }else{
            //若小于状态里面最小值，更新状态的最小值
            leastValueState.update(in._2)

            //将当前数输出
            out.collect(in._1,in._2,in._2)
          }

        }
      }
    }

    //4.结果输出（打印）
    inputStream.print()

    //5.触发执行
    senv.execute("ValuteStateFunction")


  }

}
