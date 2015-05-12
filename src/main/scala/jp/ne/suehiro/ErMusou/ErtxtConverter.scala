package jp.ne.suehiro.ErMusou

import java.io.File
import org.apache.commons.io.FileUtils
import scala.sys.process._

case class ErtxtConverter(ertxt: Ertxt) {
  def toSQL: String = {
    txt.ddl.render(ertxt).toString()
  }


  def toDot: String = {
    txt.erd.render(ertxt).toString()
  }

  def toHtml: String = {
    txt.db.render(ertxt).toString()
  }

  def mkErd(file_path: File, dot_path: String): Boolean = {
    val tmp_file = new File(System.getProperty("java.io.tmpdir"), "erd." + java.util.UUID.randomUUID.toString + "dot")
    FileUtils.writeStringToFile(tmp_file, toDot)

    ("\"" + dot_path + "\" " + tmp_file.toString + " -T png") #> file_path !!

    FileUtils.deleteQuietly(tmp_file)
    true
  }

  def mkSql(file_path: File): Boolean = {
    FileUtils.writeStringToFile(file_path, toSQL)
    true
  }

  def mkHtml(file_path: File): Boolean = {
    FileUtils.writeStringToFile(file_path, toHtml)
    true
  }
}
