package jp.ne.suehiro.ErMusou

import java.io.File
import java.nio.file.Paths
import com.typesafe.config.{Config, ConfigException, ConfigFactory}
import scala.io.Source

object ErMusou extends App {
  var config: Config = _

  override def main(args: Array[String]) {
    try {
      config = ConfigFactory.parseFile(new File("er_musou.conf"))
    } catch {
      case e: ConfigException =>
        Console.println(e.getMessage)
        return
    }
    val file_set = parseCommand(args.toList)
    val input_data = Source.fromFile(file_set.source_file).getLines().mkString("\n")
    val erd_file = new File(file_set.source_file + ".png")
    val db_data = ErtxtConverter(Ertxt.stringParse(input_data, erd_file))
    db_data.mkErd(erd_file, getDotPath)
    db_data.mkSql(new File(file_set.source_file + ".ddl"))
    db_data.mkHtml(new File(file_set.source_file + ".html"))
  }

  def getDotPath: String = {
    config.getString("dot.path")
  }

  def parseCommand(args: List[String]): FileSet = {
    if (args.size != 1) throw new ErMusouUsageException
    val source_file = Paths.get(args.head).toAbsolutePath
    if (!source_file.toFile.exists()) {
      throw new ErMusouUsageException
    }
    FileSet(source_file.toString)
  }
}
