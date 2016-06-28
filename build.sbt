// import scalariform.formatter.preferences._

name := "fs2-playground"

organization  := "org.xiaon"

version := "1.0"

scalaVersion := "2.11.8" 

val akkaVersion = "2.4.3"
val sprayVersion = "1.3.3"
val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
    "co.fs2"            %% "fs2-core"              % "0.9.0-M3",
    "co.fs2"            %% "fs2-io"                % "0.9.0-M3",
    "com.typesafe"       % "config"                % "1.2.1",
    "edu.stanford.nlp"   % "stanford-corenlp"      % "3.5.2" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp")),
    "org.twitter4j"      % "twitter4j-stream"      % "4.0.3",
    "org.twitter4j"      % "twitter4j-core"        % "4.0.3"
)

//scalacOptions := Seq("-encoding", "utf8",
//                     "-target:jvm-1.8",
//                     "-feature",
//                     "-language:implicitConversions",
//                     "-language:postfixOps",
//                     "-unchecked",
//                     "-Xfatal-warnings",
//                     "-Xlint",
//                     "-deprecation",
//                     "-Xlog-reflective-calls",
//                     "-Ywarn-unused",
//                     //"-Ywarn-unused-import",
//                     "-Ywarn-dead-code")
//scalariformSettings
//
//ScalariformKeys.preferences := ScalariformKeys.preferences.value
//  .setPreference(AlignParameters, false)
//  .setPreference(AlignSingleLineCaseStatements, true)
//  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 90)
//  .setPreference(DoubleIndentClassDeclaration, true)
//  .setPreference(PreserveDanglingCloseParenthesis, true)
//  .setPreference(RewriteArrowSymbols, true)

//scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))
//scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value
//
initialCommands in console := "import fs2._, fs2.util._"
//tutSettings


