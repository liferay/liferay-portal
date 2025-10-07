FROM liferay/jar-runner:latest

COPY --chown=liferay:liferay *.jar /opt/liferay/jar-runner.jar

ENTRYPOINT ["tini", "-v", "--", "/usr/local/bin/liferay_jar_runner_entrypoint.sh", "-h", "8282", "-p", "2525"]

#ENV JAVA_VERSION="zulu21"
#ENV LIFERAY_JAR_RUNNER_JAVA_OPTS="-Xmx512m"