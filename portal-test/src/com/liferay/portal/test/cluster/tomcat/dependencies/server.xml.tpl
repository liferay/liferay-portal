<?xml version="1.0"?>

<Server port="$SHUTDOWN_PORT$" shutdown="SHUTDOWN">
	<Listener className="org.apache.catalina.startup.VersionLoggerListener" />
	<Service name="Catalina">
		<Connector
			maxThreads="5"
			port="$CONNECTOR_PORT$"
			protocol="HTTP/1.1"
			redirectPort="8443"
		/>
		<Engine defaultHost="localhost" name="Catalina">
			<Host
				appBase="webapps"
				autoDeploy="true"
				name="localhost"
				unpackWARs="true"
			>
				<Valve className="org.apache.catalina.valves.ErrorReportValve" showReport="false" showServerInfo="false" />
			</Host>
		</Engine>
	</Service>
</Server>