package jp.ne.suehiro.ErMusou
import java.io.File

object Ertxt {
  def stringParse(strings: String, erd_file: File): Ertxt = {
    val result = ErtxtParser.parse(strings).get
    new Ertxt(result._1, result._2, erd_file)
  }
}

case class Ertxt(title: Title, tables: List[Table], erd_file: File) {
  def getTables: List[Table] = {
    tables
  }
}
