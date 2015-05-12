package jp.ne.suehiro.ErMusou

import scala.util.parsing.combinator.RegexParsers
class LineElement

case class LineBreak() extends LineElement

case class Title(title: String) extends LineElement

case class Table(name: TableName, logical_name: Option[TableLogicalName], col_list: List[Column], primary_keys: List[Column], relations: List[TableRelation]) extends LineElement

case class TableName(table_name: String) extends LineElement

case class TableLogicalName(table_logical_name: String) extends LineElement

case class Column(name: ColumnName, logical_name: ColumnLogicalName, col_type: ColumnType, pkey: ColumnConditionPrimary, not_null: ColumnConditionNotNull, unique: ColumnConditionUnique, default: Option[ColumnConditionDefault], relation: Option[TableRelation], comment_list: List[String]) extends LineElement

case class ColumnName(name: String) extends LineElement

case class ColumnLogicalName(logical_name: String) extends LineElement

case class ColumnType(col_type: String) extends LineElement

case class ColumnConditionNotNull(is_not_null: Boolean) extends LineElement

case class ColumnConditionUnique(is_unique: Boolean) extends LineElement

case class ColumnConditionDefault(default: String) extends LineElement

case class ColumnConditionPrimary(is_primary: Boolean) extends LineElement

case class TableRelation(line_from: String, line_to: String, to: TableName) extends LineElement

case class Other(other: String) extends LineElement

object ErtxtParser extends RegexParsers {
  def STR = ".*".r

  def SPACE = """\s*""".r

  def OPTION = "[^\\[\\]]*".r

  def NAME = """[a-zA-Z0-9_]*""".r

  def title = STR

  def title_line: ErtxtParser.Parser[Title] = """\A#\s*Title:\s*""".r ~> title <~ SPACE ^^ {
    new Title(_)
  }

  def col_pkey = opt("+") ^^ {
    case None => new ColumnConditionPrimary(false)
    case _ => new ColumnConditionPrimary(true)
  }

  def col_name = NAME ^^ {
    new ColumnName(_)
  }

  def col_logical_name = opt("/" ~> "[^ ]+".r) ^^ {
    case None => new ColumnLogicalName("")
    case Some(name) => new ColumnLogicalName(name)
  }

  def col_type = "[" ~> OPTION <~ "]" ^^ {
    new ColumnType(_)
  }

  def col_cond_not_null: ErtxtParser.Parser[ColumnConditionNotNull] = opt("[NN]") ^^ {
    case None => new ColumnConditionNotNull(false)
    case _ => new ColumnConditionNotNull(true)
  }

  def col_cond_unique: ErtxtParser.Parser[ColumnConditionUnique] = opt("[U]") ^^ {
    case None => new ColumnConditionUnique(false)
    case _ => new ColumnConditionUnique(true)
  }

  def col_cond_default = "[=" ~> OPTION <~ "]" ^^ {
    new ColumnConditionDefault(_)
  }

  def col_relation_line = "([01*](\\.\\.[1*])?)".r ~ """\s*--\s*""".r ~ "([01*](\\.\\.[1*])?)".r ^^ {
    case line_from ~ line ~ line_to => (line_from, line_to)
  }

  def col_relation_table = NAME

  def col_relation = opt(SPACE ~> col_relation_line ~ SPACE ~ col_relation_table) ^^ {
    case None => None
    case Some((line_from, line_to) ~ d ~ t) => Some(TableRelation(line_from, line_to, new TableName(t)))
  }

  def comment = SPACE ~ "#" ~> STR <~ opt(SPACE)

  def column = SPACE ~> col_pkey ~ col_name ~ col_logical_name ~ SPACE ~ col_type ~ col_cond_not_null ~ col_cond_unique ~ opt(col_cond_default) ~ col_relation ~ rep(comment) ^^ {
    case pkey ~ name ~ logic_name ~ space ~ c_type ~ nn ~ unique ~ default ~ rel ~ comment_list =>
      new Column(name, logic_name, c_type, pkey, nn, unique, default, rel, comment_list)
  }


  def table_name = NAME ^^ {
    new TableName(_)
  }

  def table_logical_name = STR ^^ {
    case s: String if s.length > 0 => Some(TableLogicalName(s))
    case _ => None
  }

  def table_header = table_name ~ "/" ~ table_logical_name ^^ {
    case name ~ "/" ~ logical_name => (name, logical_name)
  }

  def table = table_header ~ rep(column) ^^ {
    case (name, logical_name) ~ column_list =>
      new Table(name, logical_name, column_list, column_list.filter(p => p.pkey.is_primary), column_list.flatMap(p => p.relation))
  }

  def other: ErtxtParser.Parser[Other] = """.*""".r ^^ {
    new Other(_)
  }

  def contents = title_line ~ rep(table) ^^ {
    case t ~ table_list => (t, table_list)
  }

  def parse(input: String) = parseAll(contents, input)
}
