package io.untyped.fs2playground

import fs2.async.mutable.Queue
import fs2.util.Task
import fs2._
import twitter4j.{StallWarning, _}


object D_TwitterStreamClient extends App with TwitterConfig {
  implicit val scheduler: Scheduler = Scheduler.fromFixedDaemonPool(8)
  implicit val strategy: Strategy = Strategy.fromFixedDaemonPool(8)

  val factory = new TwitterStreamFactory(twitterConfig)
  val twitterStream: TwitterStream = factory.getInstance()

  def log[A] (prefix: String): Pipe[Task, A, A] =
    _.evalMap{ a ⇒
      Task.delay{
        println(s"$prefix > $a")
        a
      }
    }

  Stream.eval(async.boundedQueue[Task, Status](100)).flatMap { q =>
    val reader: Stream[Task, Status] =
      q.dequeue
        .map(s ⇒ Tweet(author = Author(s.getUser.getScreenName), retweetCount = s.getRetweetCount, body = s.getText))
        .through(analysisPipe)
        .through(log(">"))
        .drain
    val writer = Stream.eval_(stream(q))
    reader merge writer

  }.run.unsafeRun


  def analyze(t: Tweet): Task[EnrichedTweet] = Task {
    EnrichedTweet(t.author, t.body, t.retweetCount, SentimentAnalyzer.sentiment(t.body))
  }

  def analysisPipe: Pipe[Task, Tweet, EnrichedTweet] = _.evalMap{tweet ⇒ analyze(tweet)}

  def stream(q: Queue[Task, Status]) = Task {
    twitterStream.addListener(twitterStatusListener(q))
    //twitterStream.filter(query) // does not seems to work well for the demo
    twitterStream.sample("en")
  }

  def twitterStatusListener(q: Queue[Task, Status]) = new StatusListener {

    def onStatus(status: Status) = {
      (Stream(status) to q.enqueue).run.unsafeRun
    }

    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}

    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}

    def onException(ex: Exception) {
      ex.printStackTrace()
    }

    def onScrubGeo(arg0: Long, arg1: Long) {}

    def onStallWarning(warning: StallWarning) {}
  }

  def stop(twitterStream: TwitterStream) = Task.delay {
    twitterStream.cleanUp()
    twitterStream.shutdown()
  }
}
