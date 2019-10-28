package com.myd.cn.stream

import org.apache.flink.api.common.functions.ReduceFunction

/**
  * 自定义reduce 函数类
  * * author:jerry
  * * email:dymllt@163.com
  */
class MyReduceFunction extends ReduceFunction[(String,Long)]{
  override def reduce(t: (String, Long), t1: (String, Long)): (String, Long) = {
    (t._1,t._2+t._2)
  }

}
