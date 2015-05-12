package jp.ne.suehiro.ErMusou

import java.io.File

import org.specs2.mutable.Specification

class ParseCommandSpec extends Specification {

  "command parser" should {
    "変換元ファイルを指定できる" in {
      ErMusou.parseCommand(List("test/source_sample_files/test.ertxt")) must_== FileSet(new File("test/source_sample_files/test.ertxt").getAbsolutePath.toString)
    }

    "ファイルを指定しないと例外が飛ぶ" in {
      ErMusou.parseCommand(List()) must throwA[ErMusouUsageException]
    }
    "指定したファイルがないと例外が飛ぶ" in {
      ErMusou.parseCommand(List("test/source_sample_files/test.ertxt.not_found")) must throwA[ErMusouUsageException]
    }

    "引数が3つ以上指定すると例外が飛ぶ" in {
      ErMusou.parseCommand(List("test/source_sample_files/test.ertxt", "b", "c")) must throwA[ErMusouUsageException]
    }
  }
}

