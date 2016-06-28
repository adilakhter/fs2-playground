package io.untyped

import twitter4j.conf.ConfigurationBuilder
import com.typesafe.config.ConfigFactory

package object fs2playground {

  trait TwitterConfig {
    val configuration = ConfigFactory.load()
    val configurationBuilder = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(configuration.getString("consumer.key"))
      .setOAuthConsumerSecret(configuration.getString("consumer.secret"))
      .setOAuthAccessToken(configuration.getString("access.key"))
      .setOAuthAccessTokenSecret(configuration.getString("access.secret"))

    val twitterConfig = configurationBuilder.build()
  }

  final case class Author(handle: String) {
    override def toString: String = "@" + handle
  }

  final case class Hashtag(name: String) {
    override def toString = "#" + name
  }

  final case class Tweet(author: Author, body: String, retweetCount: Int) {
    def hashtags: Set[Hashtag] =
      body.split(" ").collect { case t if t.startsWith("#") ⇒ Hashtag(t) }.toSet
  }

  final case class EnrichedTweet(author: Author, body: String, retweetCount: Int, sentiment: Int) {
    def sentimentEmoticon = sentiment match {
      case i if i < 2 ⇒ "\uD83D\uDE1E"
      case i if i > 2 ⇒ "\uD83D\uDE0A"
      case _          ⇒ "\uD83D\uDE12"
    }

    // "\uD83D\uDE01" =>
    // "\uD83D\uDE0A" => happy
    // "\uD83D\uDE1E" => sad
    // "\uD83D\uDE12" => unamused
    // "\uD83D\uDE01" =>
    // "\uD83D\uDE0A" => happy
    // "\uD83D\uDE1E" => sad
    // "\uD83D\uDE12" => unamused

    val reweetSting = "\uD83D\uDD01"
    val newlineUnicode = "\u000A"

    override def toString: String = s"$sentimentEmoticon $reweetSting $retweetCount ➠ $author≫ $body $newlineUnicode  $newlineUnicode"
  }

  val AkkaTeam = Author("akkateam")
  val Akka = Hashtag("akka")
  val Scala = Hashtag("scala")
  val ScalaDaysHashTag = Hashtag("scaladays")
  val FridayTheThirteen = Hashtag("Fridaythe13th")

}
