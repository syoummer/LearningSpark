package sql

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

//
// ***NOTE***: this fails in Spark 1.3.0 with scala.MatchError
// It was written as an attempt to answer the following question:
// http://stackoverflow.com/questions/29310405/what-is-the-right-way-to-represent-an-any-type-in-spark-sql
//
object CaseClassSchemaProblem {

  private abstract class MyHolder

  private case class StringHolder(s: String) extends MyHolder

  private case class IntHolder(i: Int) extends MyHolder

  private case class BooleanHolder(b: Boolean) extends MyHolder

  private case class Thing(key: Integer, foo: MyHolder)

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("CaseClasses").setMaster("local[4]")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)

    import sqlContext.implicits._

    val things = Seq(
      Thing(1, IntHolder(42)),
      Thing(2, StringHolder("hello")),
      Thing(3, BooleanHolder(false))
    )
    val thingsDF = sc.parallelize(things, 4).toDF()

    thingsDF.registerTempTable("things")

    val all = sqlContext.sql("SELECT * from things")

    all.printSchema()

    all.show()
  }
}



