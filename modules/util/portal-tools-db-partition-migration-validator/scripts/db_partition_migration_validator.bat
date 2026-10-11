@echo off

pushd "%~dp0"

path %PATH%;%JAVA_HOME%\bin

java ^
	--add-opens=java.base/java.lang=ALL-UNNAMED ^
	--add-opens=java.base/java.lang.invoke=ALL-UNNAMED ^
	--add-opens=java.base/java.lang.reflect=ALL-UNNAMED ^
	--add-opens=java.base/java.net=ALL-UNNAMED ^
	--add-opens=java.base/java.util=ALL-UNNAMED ^
	--add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED ^
	--add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED ^
	--add-opens=java.base/sun.util.calendar=ALL-UNNAMED ^
	--add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED ^
	--add-opens=jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED ^
	-Dlauncher="%~nx0" ^
	-jar com.liferay.portal.tools.db.partition.migration.validator.jar %*

popd

@echo on

@exit /b %ERRORLEVEL%