package com.myd.cn.dataset

import java.util

import org.apache.flink.api.common.functions.{AggregateFunction, MapFunction, ReduceFunction}
import org.apache.flink.api.java.functions.FunctionAnnotation.NonForwardedFields
import org.apache.flink.table.runtime.aggregate.AggregateAggFunction

/**
  * reduceFunction 计算数据格式  userid,adType,clickTimes,timestamp
  *
  * reduce 返回类型 String,Long,Int
  *
  *
  */


//@NonForwardedFields(Array("_2"))
//class MyNonForwardFieldsReducer  extends  AggregateFunction[(Int,String,Long,Long),(Long,Long),(Long,Long)]{
//  //第一个不参与运算，直接输出，第二个要在函数中进行逻辑处理，并产生新的结果，直接在本地计算
//  //根据广告类型统计对应的用户
//  //定义累加器
//  override def createAccumulator(): (Long, Long) = (0L,0L)
//
//  //累加：相同爱好的用户数量，点击次数
//  override def add(value: (Int, String, Long, Long), accumulator: (Long, Long)): (Long, Long) = {
//    (accumulator._1 + value._4,accumulator._2+value._3)
//  }
//
//  override def getResult(accumulator: (Long, Long)): (Long, Long) = {
//    //返回用户数量和点击词素
//    (accumulator._1,accumulator._2)
//  }
//  //累加器逻辑合并
//  override def merge(a: (Long, Long), b: (Long, Long)): (Long, Long) = {
//    (a._1+b._1,a._2+b._2)
//  }
//}




/**
  * 非转发性注解，需要参与函数或函数计算
  * reduceFunction 计算数据格式  userid,adType,clickTimes,timestamp
  * reduce 返回类型 String,Long,Int
  * author:jerry
  * email:dymllt@163.com
  *
  */


@NonForwardedFields(Array("_2"))
class MyNonForwardFieldsReducer  extends  ReduceFunction[(Int,String,Long,Long)]{
  //第一个不参与运算，直接输出，第二个要在函数中进行逻辑处理，并产生新的结果，直接在本地计算
  //根据广告类型统计对应的用户
  //userid,adType,clickTimes,timestamp
  override def reduce(value1: (Int, String, Long, Long), value2: (Int, String, Long, Long)): (Int, String, Long, Long) = {

    var userCount:Long = 0L
    var clickTimesCount:Long = 0L
    if(value1._2 .equals(value1._2)){
      userCount = value1._1+value2._1
      clickTimesCount = value1._3+value2._3
    }
    (0,value1._2,userCount,clickTimesCount)
  }
}


