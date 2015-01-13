package zcheck.macros

import scala.language.experimental.macros

object ClassMacros {

  def className(that: Any): String = macro classNameMacro

  def classNameMacro(c: XScala#Context)( that: c.Expr[Any]): c.Expr[String] = {
    import c.universe._
    c.Expr[String](q"zcheck.macros.ClassMacros.classNameImpl($that)")

  }

  def classNameImpl(that: Any): String = {
    val result:String = that.getClass.getName.stripSuffix("$")
    //println(  "XSEARCHX " + result)
    //SpecLiteSuite().property(result) = that.asInstanceOf[Properties]
    result
  }

}
