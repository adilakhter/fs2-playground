package io.untyped.fs2playground

import fs2._
import fs2.time._
import fs2.util._
import twitter4j._
import scala.collection.JavaConversions._

object B_TwitterClient extends App with TwitterConfig {
  implicit val scheduler: Scheduler = Scheduler.fromFixedDaemonPool(8)
  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8)

  val twitterClient: Twitter = new TwitterFactory(twitterConfig).getInstance()

  def statuses(query: Query): Task[List[Status]] = Task {
    val receivedTweets = twitterClient.search(query).getTweets.toList
    println("Received " + receivedTweets.size + " tweets ...") // Logging the response from the server
    receivedTweets
  }

  def analyze(t: Tweet): Task[EnrichedTweet] = Task {
    EnrichedTweet(t.author, t.body, t.retweetCount, SentimentAnalyzer.sentiment(t.body))
  }

  import time._
  import scala.concurrent.duration._

  def twitterPipe: Pipe[Task, Query, List[Status]] = _.evalMap{ query ⇒ statuses(query)}

  def analysisPipe: Pipe[Task, Tweet, EnrichedTweet] = _.evalMap{tweet ⇒ analyze(tweet)}

  def src: Stream[Task, Query] = awakeEvery[Task](15 seconds).map{ _ ⇒ new  Query("#spark")}

  def log[A] (prefix: String): Pipe[Task, A, A] =
    _.evalMap{ a ⇒
      Task.delay{
        println(s"$prefix > $a")
        a
      }
    }

  def snk[A] (prefix: String): Sink[Task, A] =
    _.evalMap{ a ⇒
      Task.delay{
        print(s"$prefix > $a")
      }
    }


  src
    .through(twitterPipe)
    .flatMap(Stream.emits(_))
    .map(s ⇒ Tweet(author = Author(s.getUser.getScreenName), retweetCount = s.getRetweetCount, body = s.getText))
    .through(analysisPipe)
    //.take(10) ←--
    .to(snk(""))
    .run
    .unsafeRun
}
