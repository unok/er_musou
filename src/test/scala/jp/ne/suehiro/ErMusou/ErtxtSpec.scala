package jp.ne.suehiro.ErMusou

import com.typesafe.config.ConfigFactory
import org.specs2.mutable.{Before, Specification}
import org.specs2.specification.Scope

import java.io.File

import scala.io.Source

class ErtxtSpec extends Specification {

  trait scope extends Scope {
    val data_list = Source.fromFile("test/source_sample_files/test.ertxt").getLines().mkString("\n")
    val erd = new File("test/source_sample_files/test.png")
    val ertxt = Ertxt.stringParse(data_list, erd)
    val data_list_with_space = Source.fromFile("test/source_sample_files/test_with_spaces.ertxt").getLines().mkString("\n")
    val ertxt_with_space = Ertxt.stringParse(data_list_with_space, erd)
    val config = ConfigFactory.parseFile(new File("er_musou.conf"))
  }

  "Ertxtクラス" should {
    "パースしてタイトルが取得できる" in new scope {
      ertxt.title.title must_== "ERサンプル"
    }
    "パースしてテーブルが全て取得できる" in new scope {
      ertxt.getTables.size must_== 4
    }
    "スペースのみの行があるファイルをパースしてテーブルが全て取得できる" in new scope {
      ertxt_with_space.getTables.size must_== 4
    }
    "一つのプライマリキーが正常に設定されている" in new scope {
      ertxt_with_space.getTables.filter(t => t.name.table_name == "users").head.primary_keys.size must_== 1
    }
    "複数のプライマリキーが正常に設定されている" in new scope {
      ertxt_with_space.getTables.filter(t => t.name.table_name == "article_tags").head.primary_keys.size must_== 2
    }
    "NotNullが設定される" in new scope {
      ertxt_with_space.getTables.filter(t => t.name.table_name == "users").head.col_list.filter(c => c.name.name == "nick_name").head.not_null.is_not_null must_== true
    }
    "NotNullが指定してなければ設定されない" in new scope {
      ertxt_with_space.getTables.filter(t => t.name.table_name == "users").head.col_list.filter(c => c.name.name == "profile").head.not_null.is_not_null must_== false
    }

    "dot用ファイルが出力できる" in new scope {
      ErtxtConverter(ertxt_with_space).toDot must_==
        """
          |
          |digraph g {
          |    node [shape=box,style=rounded,height=0.08,fontname="Ricty-Regular"];
          |
          |    users [label = <<table border="0" cellborder="0" cellpadding="0">
          |      <tr><td colspan="2"><font face="Ricty-Bold">会員/users</font></td></tr>
          |      <tr><td align="left">会員ID/id bigserial (UNN)</td><td>P</td></tr>
          |      <tr><td align="left">ニックネーム/nick_name varchar(128) (NN)</td><td></td></tr>
          |      <tr><td align="left">パスワード/password varchar(128) </td><td></td></tr>
          |      <tr><td align="left">プロフィール/profile text </td><td></td></tr>
          |      <tr><td align="left">更新日時/updated timestamp with timezone (NN)</td><td></td></tr>
          |      <tr><td align="left">作成日時/created timestamp with timezone (NN)</td><td></td></tr>
          |    </table>>];
          |
          |    articles [label = <<table border="0" cellborder="0" cellpadding="0">
          |      <tr><td colspan="2"><font face="Ricty-Bold">記事 /articles</font></td></tr>
          |      <tr><td align="left">記事ID/id bigserial (UNN)</td><td>P</td></tr>
          |      <tr><td align="left">タイトル/title varchar(256) (NN)</td><td></td></tr>
          |      <tr><td align="left">内容/contents text (NN)</td><td></td></tr>
          |      <tr><td align="left">投稿者/owner_user_id bigint (NN)</td><td>F</td></tr>
          |      <tr><td align="left">更新日時/updated timestamp with timezone (NN)</td><td></td></tr>
          |      <tr><td align="left">作成日時/created timestamp with timezone (NN)</td><td></td></tr>
          |    </table>>];
          |
          |    tags [label = <<table border="0" cellborder="0" cellpadding="0">
          |      <tr><td colspan="2"><font face="Ricty-Bold">タグ /tags</font></td></tr>
          |      <tr><td align="left">タグID/id bigserial (UNN)</td><td>P</td></tr>
          |      <tr><td align="left">タグ/name varchar(256) (UNN)</td><td></td></tr>
          |      <tr><td align="left">更新日時/updated timestamp with timezone (NN)</td><td></td></tr>
          |      <tr><td align="left">作成日時/created timestamp with timezone (NN)</td><td></td></tr>
          |    </table>>];
          |
          |    article_tags [label = <<table border="0" cellborder="0" cellpadding="0">
          |      <tr><td colspan="2"><font face="Ricty-Bold">記事タグ管理/article_tags</font></td></tr>
          |      <tr><td align="left">記事ID/article_id bigint (NN)</td><td>PF</td></tr>
          |      <tr><td align="left">タグID/tag_id bigint (NN)</td><td>PF</td></tr>
          |      <tr><td align="left">更新日時/updated timestamp with timezone (NN)</td><td></td></tr>
          |      <tr><td align="left">作成日時/created timestamp with timezone (NN)</td><td></td></tr>
          |    </table>>];
          |
          |  
          |    edge [
          |        arrowhead = "empty"
          |        headlabel = "0..*"
          |        taillabel = "1"
          |    ]
          |    articles -> users
          |  
          |    edge [
          |        arrowhead = "empty"
          |        headlabel = "0..*"
          |        taillabel = "1"
          |    ]
          |    article_tags -> users
          |
          |    edge [
          |        arrowhead = "empty"
          |        headlabel = "0..*"
          |        taillabel = "1"
          |    ]
          |    article_tags -> tags
          |
          |}
          |""".stripMargin
    }

    "sql用ファイルが出力できる" in new scope {
      ErtxtConverter(ertxt_with_space).toSQL must_==
        """
          |
          |DROP TABLE users CASCADE;
          |DROP TABLE articles CASCADE;
          |DROP TABLE tags CASCADE;
          |DROP TABLE article_tags CASCADE;
          |
          |CREATE TABLE users (
          |    id bigserial UNIQUE NOT NULL,
          |    nick_name varchar(128) NOT NULL,
          |    password varchar(128) DEFAULT '********',
          |    profile text,
          |    updated timestamp with timezone NOT NULL DEFAULT now(),
          |    created timestamp with timezone NOT NULL DEFAULT now(),
          |    PRIMARY KEY(id)
          |);
          |
          |CREATE TABLE articles (
          |    id bigserial UNIQUE NOT NULL,
          |    title varchar(256) NOT NULL,
          |    contents text NOT NULL,
          |    owner_user_id bigint NOT NULL,
          |    updated timestamp with timezone NOT NULL DEFAULT now(),
          |    created timestamp with timezone NOT NULL DEFAULT now(),
          |    PRIMARY KEY(id)
          |);
          |
          |CREATE TABLE tags (
          |    id bigserial UNIQUE NOT NULL,
          |    name varchar(256) UNIQUE NOT NULL,
          |    updated timestamp with timezone NOT NULL DEFAULT now(),
          |    created timestamp with timezone NOT NULL DEFAULT now(),
          |    PRIMARY KEY(id)
          |);
          |
          |CREATE TABLE article_tags (
          |    article_id bigint NOT NULL,
          |    tag_id bigint NOT NULL,
          |    updated timestamp with timezone NOT NULL DEFAULT now(),
          |    created timestamp with timezone NOT NULL DEFAULT now(),
          |    PRIMARY KEY(article_id, tag_id)
          |);
          |""".stripMargin
    }
    
    "ERDファイルが出力できる" in new scope {
      ErtxtConverter(ertxt_with_space).mkErd(new File("test/source_sample_files/test.erd"), config.getString("dot.path")) must_== true
    }
    
    "DDLファイルが出力できる" in new scope {
      ErtxtConverter(ertxt_with_space).mkSql(new File("test/source_sample_files/test.sql")) must_== true
    }
    
    "HTMLファイルが出力できる" in new scope {
      ErtxtConverter(ertxt_with_space).mkHtml(new File("test/source_sample_files/test.html")) must_== true
    }
  }
}
