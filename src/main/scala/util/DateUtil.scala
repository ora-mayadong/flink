package util

import java.text.SimpleDateFormat
import java.util.Date

object DateUtil {

  def getDateFromDate(timestamp:Long):Date={
      val simpleFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

      var longTimestamp:Long = 0

      var finalStr = ""

      longTimestamp = timestamp

      //finalStr = simpleFormat.format(new Date(longTimestamp))

      //返回值
      new Date(longTimestamp*1000)
  }


  def main(args: Array[String]): Unit = {
       println(getDateFromDate(1569492766))
  }
}
