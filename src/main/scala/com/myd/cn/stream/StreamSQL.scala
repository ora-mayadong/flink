package com.myd.cn.stream

import org.apache.flink.core.fs.FileSystem
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.table.api.{TableConfig, TableEnvironment}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row


/**
  *
  *flink sql Table API
  * author:jerry
  * email:dymllt@163.com
  */
object StreamSQL {
  def main(args: Array[String]): Unit = {

    //1.获取执行环境
    val senv = StreamExecutionEnvironment.getExecutionEnvironment
    //senv.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    val tableEnv = TableEnvironment.getTableEnvironment(senv)



    //2.读取数据
    val stream :DataStream[(Long,String,Long,String)] = senv.fromElements(
       (192,"foo",34,"M")
      ,(152,"fun",43,"F")
      ,(123,"fun",23,"M")
      ,(184,"fun",73,"F")
      ,(135,"fun",13,"M"))

    //创建Table操作实例
    //注册成flink table,默认字段名为f0,f1
    tableEnv.registerDataStream("userInfo",stream,'id,'name,'age,'sex)

    //3.逻辑处理
    //获取注册表 tables
    val orders = tableEnv.scan("userInfo")
    //逻辑处理 分组sql的select的字段 必须出现在聚合函数，分组字段 中
    //select c1,avg(c2),distinct(c3)...  from tableA A join tableB B on (A.id = B.id ) where c1 > 0 groupby c1 have c1 > 3 order by  c2 asc.
    // 1>from 抽A,B表后根据on条件 join
    // 2>where 选择符合条件的行
    // 3>进行分组(group by)后聚合(avg,sum,count) ，再对分组进行选择(having)
    // 4>选择的字段
    // 5> 去重
    // 6>根据某(些)字段排序
    val condition_value = 192
    //val revenueTalbe = orders.filter('id < condition_value).groupBy("id").select('id,'age.sum )
    //相同的功能
    val revenueTalbe = tableEnv.sqlQuery(
        s"""
          |select id,sum(age) as ageSum
          |from userInfo
          |where id < $condition_value
          |group by sex,id
        """.stripMargin)
    //过滤结果,转换DataStream为Table
   //val redsTUps = tStreamEnv.toAppendStream[(Long,String,Long,String)](table = revenue) 非Append数据流，无法使用Append数据流
    val revenuDataStream = tableEnv.toRetractStream[Row](revenueTalbe)

    //4.输出结果
    //val redsTUps = tStreamEnv.toAppendStream[(Long,String,Long,String)](table = orders)

    //打印表结构或保存结果
    revenueTalbe.printSchema()
    //打印table处理后 转换为DataStream的结果
    revenuDataStream.print()

    //保存到csv
    revenuDataStream.setParallelism(1).writeAsCsv("/home/jerry/table.csv",FileSystem.WriteMode.OVERWRITE,rowDelimiter = "\n",fieldDelimiter = ",")

    //5.触发执行
    senv.execute("Table AIP")
  }

  //封装数据结构
  case class Order(user:Long,product:String,amount:Int) extends  Serializable

}
