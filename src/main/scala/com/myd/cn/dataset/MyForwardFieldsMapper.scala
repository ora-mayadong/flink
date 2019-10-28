package com.myd.cn.dataset

import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.java.functions.FunctionAnnotation.ForwardedFields

/**
  * 转发性注解，不需要参与计算，直接发送某个字段到结果的对应位置
  * author:jerry
  * email:dymllt@163.com
  * 入参类型为Int,Double
  * 出参类型为Double,Int
  * //userid,username,adId,adName,adType,clickTimes,timestamp
  * //userid,adType,clickTimes,timestamp
  */

//@ForwardedFields("_4->_2")
@ForwardedFields(Array("_4->_2"))
class MyForwardFieldsMapper extends  MapFunction[(Int,String,Int,String,String,Long,Long),(Int,String,Long,Long)]{
  override def map(input: (Int,String,Int,String,String,Long,Long)): (Int,String,Long,Long) = {
    //将入参 第4 个字段直接放到输出数据集的 第2 个字段
    //返回值userid,adType,clickTime,1(用于计数）
    (input._1,input._4,input._6,1)
  }
}
