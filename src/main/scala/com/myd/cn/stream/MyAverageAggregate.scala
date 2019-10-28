package com.myd.cn.stream

import org.apache.flink.api.common.functions.AggregateFunction


/**
  * 自定义聚合函数类
  * 进入聚合函数的数据格式 字段名:字段值
  * * author:jerry
  * * email:dymllt@163.com
  */
class MyAverageAggregate extends AggregateFunction[(String,Long),(Long,Long),Double]{
  //定义创建累加器的元组  该字段值累加的和,字段累加的总数
  override def createAccumulator(): (Long, Long) = (0L,0L)

  //新增进入的数据,以及处理累加到累加器的逻辑
  override def add(in: (String, Long), acc: (Long, Long)): (Long, Long) = {
      //将字段已累计的值与新进的字段值相加
      (acc._1 + in._2,acc._2+1) //并将该字段的数量增加1
  }

  //根据累加器得出结果 字段累加总和/ 同一个字段的个数
  override def getResult(acc: (Long, Long)): Double = acc._1/acc._2

  //定义累加合并的逻辑
  override def merge(acc: (Long, Long), acc1: (Long, Long)): (Long, Long) = {
    //将每个分区(pipeline)计算的和相加 ,同一字段个数进行相加
    (acc1._1+acc._1,acc1._2+acc._2)
  }
}
