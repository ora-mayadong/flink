package com.myd.cn.dataset

import org.apache.flink.api.scala.{DataSet, ExecutionEnvironment}
import org.apache.flink.api.scala._


/**
  * 根据数据找出喜欢相同广告类型的用户数量，以便下次广告推送策略制定
  * author:jerry
  * email:dymllt@163.com
  */
object SemanticAnnotation {
  def main(args: Array[String]): Unit = {

    //1.获取当前执行环境
    val benv = ExecutionEnvironment.getExecutionEnvironment

    //2.读取数据或构造数据
    //userid,username,adId,adName,adType,clickTimes,timestamp
    val dataSet:DataSet[(Int,String,Int,String,String,Long,Long)] = benv.fromElements(
              (1,"foo",1,"NBA","sport",10,1569486060000L),
              (2,"bar",2,"The last stand","movie",100,1569486063000L),
              (3,"dag",3,"Climbing","adventurous Sport",3,1569486064000L),
              ( 2,"cdg",4,"voting","polictic",50,1569486066000L),
              ( 4,"jog",2,"Navy in far sea","military",20,1569486069000L)
            )
    //3逻辑处理
    //val dataForwardFiled =   dataSet.map(new MyForwardFieldsMapper).aggregate(new MyNonForwardFieldsReducer)
    val dataForwardField = dataSet.map(new MyForwardFieldsMapper).reduce(new MyNonForwardFieldsReducer)



    //4.输出结果
    dataForwardField.print()

  }

}
