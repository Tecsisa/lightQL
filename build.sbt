lazy val `wr-kql` = project
  .copy(id = "wr-kql")
  .in(file("."))

name := "wr-kql"

libraryDependencies ++= Vector(
  Library.fastParse,
  Library.elastic,
  Library.scalaTest % "test",
  Library.scalaCheck % "test"
)

initialCommands := """|import com.tecsisa.wr.kql.parser.KqlParser._
                      |import com.tecsisa.wr.kql.repo.Repo
                      |import org.elasticsearch.action.ActionFuture
                      |import org.elasticsearch.action.search.SearchResponse
                      |import org.elasticsearch.client.transport.TransportClient
                      |import org.elasticsearch.common.settings.Settings
                      |import org.elasticsearch.common.transport.InetSocketTransportAddress
                      |import java.net.InetAddress
                      |""".stripMargin
