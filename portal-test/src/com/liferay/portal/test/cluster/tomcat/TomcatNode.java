/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.cluster.tomcat;

import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.ClassLoaderObjectInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.lang.ThreadContextClassLoaderUtil;
import com.liferay.petra.process.PathHolder;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessException;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.process.local.LocalProcessExecutor;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.util.PortalClassPathUtil;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.lang.reflect.Method;

import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.catalina.startup.Bootstrap;

/**
 * @author Shuyang Zhou
 */
public class TomcatNode {

	public void destroy() throws IOException {
		LogHolder.info("Destroying Tomcat node " + toString());

		_destroyed = true;

		Files.walkFileTree(
			Paths.get(_liferayHome),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult postVisitDirectory(
						Path dirPath, IOException ioException)
					throws IOException {

					Files.delete(dirPath);

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (Files.isSymbolicLink(dirPath)) {
						Files.delete(dirPath);

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Files.delete(filePath);

					return FileVisitResult.CONTINUE;
				}

			});
	}

	public <V extends Serializable> NoticeableFuture<V> execute(
		ClusterExecutable<V> clusterExecutable) {

		ProcessChannel<String> processChannel = _processChannel;

		if (processChannel == null) {
			throw new IllegalStateException("Tomcat node is not running");
		}

		return processChannel.write(
			new ClusterExecutableProcessCallable<>(clusterExecutable));
	}

	public ProcessChannel<String> start(boolean loadHomePage) throws Exception {
		if (_destroyed) {
			throw new IllegalStateException(
				"Unable to start destroyed Tomcat node " + toString());
		}

		LogHolder.info("Starting Tomcat node " + toString());

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		List<String> arguments = new ArrayList<>();

		if (_jpdaEnabled) {
			arguments.add(
				"-agentlib:jdwp=transport=dt_socket,address=" + _jpdaPort +
					",server=y,suspend=y");
		}

		arguments.add("-Dcatalina.base=" + _catalinaBase);
		arguments.add("-Dcatalina.home=" + _catalinaHome);
		arguments.add("-Dfile.encoding=UTF-8");
		arguments.add("-Djava.io.tmpdir=" + _catalinaBase + "/temp");
		arguments.add("-Djava.net.preferIPv4Stack=true");
		arguments.add(
			"-Djava.protocol.handler.pkgs=org.apache.catalina.webresources");
		arguments.add(
			"-Djava.util.logging.config.file=" + _catalinaHome +
				"/conf/logging.properties");
		arguments.add(
			"-Djava.util.logging.manager=org.apache.juli." +
				"ClassLoaderLogManager");
		arguments.add("-Djdk.tls.ephemeralDHKeySize=2048");
		arguments.add(
			"-Dorg.apache.catalina.security.SecurityListener.UMASK=0027");

		String runtimeClassPath = _buildRuntimeClassPath();

		arguments.add("-Druntime.class.path=" + runtimeClassPath);

		arguments.add("-Duser.timezone=GMT");
		arguments.add("-Xmx2g");

		Collections.addAll(
			arguments, StringUtil.split(_jvmArgs, CharPool.SPACE));

		builder.setArguments(arguments);

		builder.setBootstrapClassPath(_buildBoostrapClassPath());
		builder.setEnvironment(System.getenv());
		builder.setReactClassLoader(PortalClassLoaderUtil.getClassLoader());
		builder.setRuntimeClassPath(runtimeClassPath);

		ProcessConfig processConfig = builder.build();

		ProcessExecutor processExecutor = new LocalProcessExecutor();

		ProcessChannel<String> processChannel = processExecutor.execute(
			processConfig, new BootstrapStartProcessCallable(_nodeId));

		NoticeableFuture<String> noticeableFuture =
			processChannel.getProcessNoticeableFuture();

		_processChannel = new ProcessChannel<String>() {

			@Override
			public NoticeableFuture<String> getProcessNoticeableFuture() {
				return noticeableFuture;
			}

			@Override
			public <V extends Serializable> NoticeableFuture<V> write(
				ProcessCallable<V> processCallable) {

				return processChannel.write(
					new BridgeProcessCallable<>(processCallable));
			}

		};

		noticeableFuture.addFutureListener(future -> _processChannel = null);

		if (loadHomePage) {

			// Make sure the follow lambda is capturing the local variable
			// rather than the instance field to ensure serializability

			int connectorPort = _connectorPort;

			syncExecute(
				() -> {
					try {
						URI uri = new URI("http://localhost:" + connectorPort);

						URL url = uri.toURL();

						try (InputStream inputStream = url.openStream()) {
							return new String(inputStream.readAllBytes());
						}
					}
					catch (Exception exception) {
						throw new ProcessException(exception);
					}
				});
		}

		return _processChannel;
	}

	public void stop() throws Exception {
		ProcessChannel<String> processChannel = _processChannel;

		if (processChannel == null) {
			return;
		}

		LogHolder.info("Stopping Tomcat node " + toString());

		syncExecute(
			() -> {
				Bootstrap.main(new String[] {"stop"});

				return "Done";
			});
	}

	public <V extends Serializable> V syncExecute(
			ClusterExecutable<V> clusterExecutable)
		throws Exception {

		NoticeableFuture<V> noticeableFuture = execute(clusterExecutable);

		return noticeableFuture.get();
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(21);

		sb.append("{catalinaBase=");
		sb.append(_catalinaBase);
		sb.append(", catalinaHome=");
		sb.append(_catalinaHome);
		sb.append(", connectorPort=");
		sb.append(_connectorPort);
		sb.append(", gogoShellPort=");
		sb.append(_gogoShellPort);
		sb.append(", jpdaEnabled=");
		sb.append(_jpdaEnabled);
		sb.append(", jpdaPort=");
		sb.append(_jpdaPort);
		sb.append(", jvmArgs=");
		sb.append(_jvmArgs);
		sb.append(", liferayHome=");
		sb.append(_liferayHome);
		sb.append(", nodeId=");
		sb.append(_nodeId);
		sb.append(", shutdownPort=");
		sb.append(_shutdownPort);
		sb.append("}");

		return sb.toString();
	}

	public interface ClusterExecutable<V extends Serializable>
		extends Serializable {

		public V execute() throws Throwable;

	}

	protected TomcatNode(
			Class<?> clusterOwnerClass, String catalineHome, int connectorPort,
			List<String> copyPropertyKeys,
			String[] elasticSearchNetworkHostAddresses, int gogoShellPort,
			boolean jpdaEnabled, int jpdaPort, String jvmArgs,
			String liferayHome, int nodeId, int shutdownPort)
		throws IOException {

		_clusterOwnerClass = clusterOwnerClass;
		_connectorPort = connectorPort;
		_copyPropertyKeys = copyPropertyKeys;
		_elasticSearchNetworkHostAddresses = elasticSearchNetworkHostAddresses;
		_gogoShellPort = gogoShellPort;
		_jpdaEnabled = jpdaEnabled;
		_jpdaPort = jpdaPort;
		_jvmArgs = jvmArgs;
		_liferayHome = liferayHome;
		_nodeId = nodeId;
		_shutdownPort = shutdownPort;

		_catalinaHome = catalineHome;

		_catalinaBase = _liferayHome + "/tomcat";

		StringBundler sb = StringUtil.replaceToStringBundler(
			_TPL_PORTAL_EXT_PROPERTIES, "${", "}",
			HashMapBuilder.put(
				"CLUSTER_NAME", clusterOwnerClass.getSimpleName()
			).put(
				"GOGO_SHELL_PORT", String.valueOf(_gogoShellPort)
			).put(
				"NETWORK_HOST_ADDRESSES",
				StringUtil.merge(_elasticSearchNetworkHostAddresses, "\",\"")
			).put(
				"OSGI_BASE", PropsValues.MODULE_FRAMEWORK_BASE_DIR
			).put(
				"OSGI_CONFIGS", _liferayHome + "/osgi/configs"
			).put(
				"OSGI_STATE", _liferayHome + "/osgi/state"
			).build());

		for (String copyPropertyKey : _copyPropertyKeys) {
			sb.append(StringPool.NEW_LINE);
			sb.append(copyPropertyKey);
			sb.append(StringPool.EQUAL);
			sb.append(PropsUtil.get(copyPropertyKey));
		}

		String portalExtPropertiesContent = sb.toString();

		Files.write(
			Paths.get(_liferayHome, "portal-ext.properties"),
			portalExtPropertiesContent.getBytes(StringPool.UTF8),
			StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		String serverXMLContent = StringUtil.replace(
			_TPL_SERVER_XML,
			new String[] {"$CONNECTOR_PORT$", "$SHUTDOWN_PORT$"},
			new String[] {
				String.valueOf(_connectorPort), String.valueOf(_shutdownPort)
			});

		Path confPath = Paths.get(_catalinaBase, "conf");

		Files.createDirectories(confPath);

		Files.write(
			confPath.resolve("server.xml"),
			serverXMLContent.getBytes(StringPool.UTF8),
			StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		Path localhostPath = confPath.resolve("Catalina/localhost");

		Files.createDirectories(localhostPath);

		Files.copy(
			Paths.get(_catalinaHome + "/conf/Catalina/localhost/ROOT.xml"),
			localhostPath.resolve("ROOT.xml"));

		Files.copy(
			Paths.get(_catalinaHome + "/conf/catalina.properties"),
			confPath.resolve("catalina.properties"));
		Files.copy(
			Paths.get(_catalinaHome + "/conf/context.xml"),
			confPath.resolve("context.xml"));
		Files.copy(
			Paths.get(_catalinaHome + "/conf/logging.properties"),
			confPath.resolve("logging.properties"));
		Files.copy(
			Paths.get(_catalinaHome + "/conf/web.xml"),
			confPath.resolve("web.xml"));

		Files.createSymbolicLink(
			Paths.get(_catalinaBase, "webapps"),
			Paths.get(_catalinaHome, "webapps"));
	}

	private String _buildBoostrapClassPath() {
		ProcessConfig portalProcessConfig =
			PortalClassPathUtil.getPortalProcessConfig();

		PathHolder[] pathHolders =
			portalProcessConfig.getBootstrapClassPathHolders();

		StringBundler sb = new StringBundler((pathHolders.length * 2) + 7);

		for (PathHolder pathHolder : pathHolders) {
			sb.append(pathHolder.getPath());
			sb.append(File.pathSeparator);
		}

		sb.append(_catalinaHome);
		sb.append("/bin/bootstrap.jar");
		sb.append(File.pathSeparator);
		sb.append(_catalinaHome);
		sb.append("/bin/tomcat-juli.jar");

		return sb.toString();
	}

	private String _buildRuntimeClassPath() {
		return StringBundler.concat(
			_toJarFilePath(TomcatNode.class), File.pathSeparator,
			_toJarFilePath(_clusterOwnerClass));
	}

	private String _toJarFilePath(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		URL url = codeSource.getLocation();

		return url.getPath();
	}

	private static final String _KEY =
		"com.liferay.shielded.container.internal.ShieldedContainerClassLoader";

	private static final String _TPL_PORTAL_EXT_PROPERTIES;

	private static final String _TPL_SERVER_XML;

	static {
		try (InputStream inputStream = TomcatNode.class.getResourceAsStream(
				"dependencies/server.xml.tpl")) {

			_TPL_SERVER_XML = StringUtil.read(inputStream);
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}

		try (InputStream inputStream = TomcatNode.class.getResourceAsStream(
				"dependencies/portal-ext.properties.tpl")) {

			_TPL_PORTAL_EXT_PROPERTIES = StringUtil.read(inputStream);
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

	private final String _catalinaBase;
	private final String _catalinaHome;
	private final Class<?> _clusterOwnerClass;
	private final int _connectorPort;
	private final List<String> _copyPropertyKeys;
	private boolean _destroyed;
	private final String[] _elasticSearchNetworkHostAddresses;
	private final int _gogoShellPort;
	private final boolean _jpdaEnabled;
	private final int _jpdaPort;
	private final String _jvmArgs;
	private final String _liferayHome;
	private final int _nodeId;
	private volatile ProcessChannel<String> _processChannel;
	private final int _shutdownPort;

	private static class BootstrapStartProcessCallable
		implements Externalizable, ProcessCallable<String> {

		public BootstrapStartProcessCallable() {
		}

		@Override
		public String call() throws ProcessException {
			Bootstrap.main(new String[] {"start"});

			return "Done";
		}

		@Override
		public void readExternal(ObjectInput objectInput)
			throws ClassNotFoundException, IOException {

			_nodeId = objectInput.readInt();

			Properties properties = System.getProperties();

			properties.put(_KEY, new CompletableFuture<>());
		}

		@Override
		public String toString() {
			return "TomcatNode-" + _nodeId;
		}

		@Override
		public void writeExternal(ObjectOutput objectOutput)
			throws IOException {

			objectOutput.writeInt(_nodeId);
		}

		private BootstrapStartProcessCallable(int nodeId) {
			_nodeId = nodeId;
		}

		private static final long serialVersionUID = 1L;

		private int _nodeId;

	}

	private static class BridgeProcessCallable<T extends Serializable>
		implements ProcessCallable<T> {

		@Override
		public T call() throws ProcessException {
			try (InputStream inputStream = new UnsyncByteArrayInputStream(
					_data);
				ObjectInputStream objectInputStream =
					new ClassLoaderObjectInputStream(
						inputStream,
						BridgeClassLoaderHolder._bridgeClassLoader)) {

				Object payloadProcessCallable = objectInputStream.readObject();

				Class<?> clazz = payloadProcessCallable.getClass();

				Method method = clazz.getMethod("call");

				method.setAccessible(true);

				try (SafeCloseable safeCloseable =
						ThreadContextClassLoaderUtil.swap(
							BridgeClassLoaderHolder._bridgeClassLoader)) {

					return (T)method.invoke(payloadProcessCallable);
				}
			}
			catch (Exception exception) {
				throw new ProcessException(exception);
			}
		}

		private BridgeProcessCallable(ProcessCallable<T> processCallable) {
			UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
				new UnsyncByteArrayOutputStream();

			try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
					unsyncByteArrayOutputStream)) {

				objectOutputStream.writeObject(processCallable);
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			_data = unsyncByteArrayOutputStream.toByteArray();
		}

		private static final long serialVersionUID = 1L;

		private final byte[] _data;

		private static class BridgeClassLoaderHolder {

			private static final ClassLoader _bridgeClassLoader;

			static {
				try {
					String runtimeClassPath = System.getProperty(
						"runtime.class.path");

					if (runtimeClassPath == null) {
						throw new IllegalStateException(
							"Runtime class path is null");
					}

					List<URL> urls = new ArrayList<>();

					for (String path :
							runtimeClassPath.split(File.pathSeparator)) {

						File file = new File(path);

						URI uri = file.toURI();

						urls.add(uri.toURL());
					}

					Properties properties = System.getProperties();

					CompletableFuture<ClassLoader> completableFuture =
						(CompletableFuture<ClassLoader>)properties.get(_KEY);

					if (completableFuture == null) {
						throw new IllegalStateException(
							"Completable future is null");
					}

					_bridgeClassLoader = new URLClassLoader(
						urls.toArray(new URL[0]), completableFuture.get());
				}
				catch (Exception exception) {
					throw new ExceptionInInitializerError(exception);
				}
			}

		}

	}

	private static class ClusterExecutableProcessCallable
		<V extends Serializable>
			implements ProcessCallable<V> {

		@Override
		public V call() throws ProcessException {
			try {
				return _clusterExecutable.execute();
			}
			catch (Throwable throwable) {
				throw new ProcessException(throwable);
			}
		}

		private ClusterExecutableProcessCallable(
			ClusterExecutable<V> clusterExecutable) {

			_clusterExecutable = clusterExecutable;
		}

		private final ClusterExecutable<V> _clusterExecutable;

	}

	// This class is loaded by the testee JVM before Tomcat starts. Lazy load
	// Log to make sure it is only used on the tester JVM and never by the
	// testee JVM.

	private static class LogHolder {

		public static void info(String message) {
			if (_log.isInfoEnabled()) {
				_log.info(message);
			}
		}

		private static final Log _log = LogFactoryUtil.getLog(TomcatNode.class);

	}

}