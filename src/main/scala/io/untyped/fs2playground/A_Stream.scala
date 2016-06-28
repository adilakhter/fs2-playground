package io.untyped.fs2playground

import fs2._
import fs2.util._
import twitter4j._

import scala.collection.JavaConversions._

object A_Stream extends App {
  import fs2._

  val s: Stream[Nothing, Int] = Stream(1, 2, 3)

  println(s.toList)
  println(s.toVector)
}


