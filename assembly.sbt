import AssemblyKeys._

assemblySettings

jarName in assembly := s"FunkyPanda_RPC-${version.value}.jar"

mainClass in assembly := Some("funkypanda.server.Server")