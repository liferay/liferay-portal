FROM docker.elastic.co/integrations/crawler:1.0.0 AS crawler
FROM jruby:9.4.12.0-jdk21

RUN groupadd liferay && useradd -g liferay -m -s /bin/bash liferay

COPY --chown=liferay:liferay --from=crawler /home/app /opt/liferay/crawler
COPY --chown=liferay:liferay --from=crawler /usr/local/bundle /usr/local/bundle

ENV BUNDLE_APP_CONFIG=/usr/local/bundle
ENV GEM_HOME=/usr/local/bundle
ENV PATH=/usr/local/bundle/bin:$PATH

COPY --chown=liferay:liferay *.jar /opt/liferay/jar-runner.jar

ENV JDK_JAVA_OPTIONS="--add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.lang.invoke=ALL-UNNAMED --add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.nio.channels=ALL-UNNAMED --add-opens=java.base/java.security=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/sun.nio.ch=ALL-UNNAMED"

USER liferay

EXPOSE 58081

CMD ["java", "-jar", "/opt/liferay/jar-runner.jar"]