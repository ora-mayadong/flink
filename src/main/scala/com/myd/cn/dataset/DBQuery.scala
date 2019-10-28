package com.myd.cn.dataset

import java.sql.Types
import scala.collection.mutable.ArrayBuffer

import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.flink.api.java.io.jdbc.{JDBCInputFormat, JDBCOutputFormat}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.types.Row


/**
  * flink  JDBC CRUD
  * author:jerry
  * email:dymllt@163.com
  */
object DBQuery {
  def main(args: Array[String]): Unit = {
    val benv = ExecutionEnvironment.getExecutionEnvironment
    // =================== 查询操作开始 ===================
    val jdbcReadResult = JDBCInputFormat.buildJDBCInputFormat()
      .setDBUrl("jdbc:mysql://MySQLHostname:3306/gai?&&erifyServerCertificate=false&&useSSL=false")
      .setDrivername("com.mysql.jdbc.Driver")
      .setUsername("root")
      .setPassword("Geotmt_123")
      .setQuery("select score_stability ,feature_stability from gai_model_params where model_id in (10,11);")
      .setRowTypeInfo(new RowTypeInfo(
        BasicTypeInfo.STRING_TYPE_INFO,
        BasicTypeInfo.STRING_TYPE_INFO
      )) //设置返回值的类型
      .finish()
    // 返回查询结果
    val source = benv.createInput(jdbcReadResult)
    source.collect().foreach(x => println("每行输出： ", x))
    // =================== 查询操作结束 ===================
    val jdbcInsert = JDBCOutputFormat.buildJDBCOutputFormat()
      .setBatchInterval(2)
      .setDBUrl("jdbc:mysql://MySQLHostname:3306/dbName?&&erifyServerCertificate=false&&useSSL=false")
      .setDrivername("com.mysql.jdbc.Driver")
      .setUsername("root")
      .setPassword("Geotmt_123")
      .setQuery("insert into flink(a,c,d) values(?,?,?)")
      .setSqlTypes(Array[Int](Types.INTEGER, Types.VARCHAR, Types.VARCHAR)) //入参的数据类型，指定每个字段对应的类型，与数据库中数据表的每个字段保持
      .finish()
    //create table `flink`(`a` Int NOT NULL DEFAULT 1,`b` Int,`c` varchar(20),`d` varchar(20));
    //插入几个，行长度就为多少
    val row1 = new Row(3)
    row1.setField(0, 120)
    row1.setField(1, "ab")
    row1.setField(2, "cd")
    val row2 = new Row(3)
    row2.setField(0, 121)
    row2.setField(1, "aa")
    row2.setField(2, "dd")
    //放入集合
    val rows = ArrayBuffer[Row]()
    rows += row1
    rows += row2
    //产生会话
    jdbcInsert.open(0, 1)
    //迭代插入
    for (row <- rows) {
      jdbcInsert.writeRecord(row)
    }
    //执行完毕后关闭会话
    jdbcInsert.close()
    // =================== 删除数据 ===================
    val jdbcDelete = JDBCOutputFormat.buildJDBCOutputFormat()
      .setDBUrl("jdbc:mysql://MySQLHostname:3306/gai?&&erifyServerCertificate=false&&useSSL=false")
      .setDrivername("com.mysql.jdbc.Driver")
      .setUsername("root")
      .setPassword("Geotmt_123")
      .setQuery("delete  from  flink where a = ?")
      .finish()
    //创建会话
    jdbcDelete.open(0, 1)
    //删除条件
    val row = new Row(1)
    row.setField(0, 120)
    //执行操作
    jdbcDelete.writeRecord(row)
    //关闭会话
    jdbcDelete.close()
  }
}
