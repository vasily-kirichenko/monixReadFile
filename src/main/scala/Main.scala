import java.io.{BufferedReader, FileReader}
import java.nio.charset.Charset

import monix.eval.Task
import monix.reactive.Observable

import scala.io.Codec

object Main extends App {
  val file = "..."

  def doSource() = {
    var n = 0
    val start = System.currentTimeMillis()
    val source = scala.io.Source.fromFile(file)(Codec.ISO8859)

    source
      .getLines()
      .map { x =>
        n += 1
        if (n % 1e6 == 0) {
          println(s"[Source] $n, ${(n / (System.currentTimeMillis() - start)) * 1000} elems/s")
        }
        x
      }.length

    source.close()
  }

  def doMonix() = {
    import monix.execution.Scheduler.Implicits.global

    var n = 0
    val start = System.currentTimeMillis()

    Observable.fromLinesReader(Task {
      new BufferedReader(new FileReader(file, Charset.forName("ISO-8859-1")))
    })
      .map(x => {
        n += 1
        if (n % 1e6 == 0) {
          println(s"[Monix] $n, ${(n / (System.currentTimeMillis() - start)) * 1000} elems/s")
        }
        x
      }).countL.runSyncUnsafe()
  }

  doSource()
  doMonix()
}
