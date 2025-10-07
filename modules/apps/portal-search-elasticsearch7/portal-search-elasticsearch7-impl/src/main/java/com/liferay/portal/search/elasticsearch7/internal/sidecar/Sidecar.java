/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.concurrent.FutureListener;
import com.liferay.petra.concurrent.NoticeableFuture;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.process.ProcessChannel;
import com.liferay.petra.process.ProcessConfig;
import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.JavaDetector;
import com.liferay.portal.kernel.util.OSDetector;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.settings.SettingsHelperImpl;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.activator.SearchElasticsearch7ImplBundleActivator;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;
import com.liferay.portal.search.elasticsearch7.sidecar.agent.SidecarAgent;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.net.URISyntaxException;
import java.net.URL;

import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.common.settings.Settings;

/**
 * @author Tina Tian
 */
public class Sidecar {

	public Sidecar(
		ElasticsearchConfigurationWrapper elasticsearchConfigurationWrapper,
		ProcessExecutor processExecutor, Path sidecarHomePath,
		SidecarManager sidecarManager, File sidecarProcessFile,
		Path sidecarWorkPath) {

		_elasticsearchConfigurationWrapper = elasticsearchConfigurationWrapper;
		_processExecutor = processExecutor;
		_sidecarHomePath = sidecarHomePath;
		_sidecarManager = sidecarManager;
		_sidecarProcessFile = sidecarProcessFile;
		_sidecarWorkPath = sidecarWorkPath;

		_sidecarTempPath = Path.of(
			SystemProperties.get(SystemProperties.TMP_DIR), "sidecar");
	}

	public String getNetworkHostAddress() {
		return _address;
	}

	public void start() {
		if (_log.isDebugEnabled()) {
			_log.debug("Sidecar Elasticsearch starting");
		}

		String sidecarVersion = _getSidecarVersion();

		_installElasticsearchIfNeeded(sidecarVersion);

		if (!Files.isDirectory(_sidecarHomePath)) {
			throw new IllegalArgumentException(
				"Sidecar Elasticsearch home does not exist: " +
					_sidecarHomePath);
		}

		String bootstrapClassPath = _getBootstrapClassPath();
		URL bundleURL = _getBundleURL(Sidecar.class);

		PersistedProcess persistedProcess = new PersistedProcess(
			bundleURL,
			new String[] {
				SidecarMainProcessCallable.class.getName(),
				StartSidecarProcessCallable.class.getName()
			},
			_createProcessConfig(
				_getJVMArguments(), bootstrapClassPath, _getEnvironment(),
				StringBundler.concat(
					bundleURL.getPath(), File.pathSeparator,
					bootstrapClassPath)),
			"sidecar");

		Serializer serializer = new Serializer();

		serializer.writeObject(persistedProcess);

		ByteBuffer byteBuffer = serializer.toByteBuffer();

		byte[] bytes = byteBuffer.array();

		ProcessChannel<Serializable> processChannel = null;
		Future<ObjectValuePair<ProcessChannel<Serializable>, byte[]>> future =
			SearchElasticsearch7ImplBundleActivator.getFuture();

		if (future != null) {
			try {
				ObjectValuePair<ProcessChannel<Serializable>, byte[]>
					objectValuePair = future.get();

				processChannel = objectValuePair.getKey();

				if (!Arrays.equals(bytes, objectValuePair.getValue())) {
					NoticeableFuture<Serializable> noticeableFuture =
						processChannel.getProcessNoticeableFuture();

					processChannel.write(new StopSidecarProcessCallable());

					noticeableFuture.get(
						_elasticsearchConfigurationWrapper.
							sidecarShutdownTimeout(),
						TimeUnit.MILLISECONDS);

					processChannel = null;

					_sidecarProcessFile.delete();
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to get a started sidecar process, will restart",
						exception);
				}
			}
		}

		if (!_sidecarProcessFile.exists()) {
			File parentFile = _sidecarProcessFile.getParentFile();

			try {
				Files.createDirectories(parentFile.toPath());

				Files.write(_sidecarProcessFile.toPath(), bytes);
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to persist sidecar process", ioException);
				}
			}
		}

		if (processChannel == null) {
			try {
				processChannel = PersistedProcessUtil.start(
					persistedProcess, _processExecutor);
			}
			catch (Exception exception) {
				throw new RuntimeException(
					"Unable to start sidecar Elasticsearch process", exception);
			}
		}

		FutureListener<Serializable> futureListener = new RestartFutureListener(
			_sidecarManager);

		_addFutureListener(processChannel, futureListener);

		NoticeableFuture<String> getAddressNoticeableFuture =
			processChannel.write(new GetSidecarAddressProcessCallable());

		String address = null;

		try {
			address = getAddressNoticeableFuture.get();
		}
		catch (Exception exception) {
			processChannel.write(new StopSidecarProcessCallable());

			if (exception instanceof RuntimeException) {
				throw (RuntimeException)exception;
			}

			throw new RuntimeException(exception);
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Sidecar Elasticsearch ", sidecarVersion, StringPool.SPACE,
					_getNodeName(), " started at ", address));
		}

		_address = address;
		_processChannel = processChannel;
		_restartFutureListener = futureListener;
	}

	public void stop() {
		if (_log.isDebugEnabled()) {
			_log.debug("Stopping sidecar Elasticsearch");
		}

		if (_processChannel != null) {
			NoticeableFuture<Serializable> noticeableFuture =
				_processChannel.getProcessNoticeableFuture();

			noticeableFuture.removeFutureListener(_restartFutureListener);

			_processChannel.write(new StopSidecarProcessCallable());

			try {
				noticeableFuture.get(
					_elasticsearchConfigurationWrapper.sidecarShutdownTimeout(),
					TimeUnit.MILLISECONDS);
			}
			catch (Exception exception) {
				if (!noticeableFuture.isDone()) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							StringBundler.concat(
								"Forcibly shutdown sidecar Elasticsearch ",
								"process because it did not shut down in ",
								_elasticsearchConfigurationWrapper.
									sidecarShutdownTimeout(),
								" ms"),
							exception);
					}

					noticeableFuture.cancel(true);
				}
			}

			_processChannel = null;
		}

		PathUtil.deleteDir(_sidecarTempPath);
	}

	private void _addFutureListener(
		ProcessChannel<Serializable> processChannel,
		FutureListener<Serializable> futureListener) {

		NoticeableFuture<Serializable> noticeableFuture =
			processChannel.getProcessNoticeableFuture();

		noticeableFuture.addFutureListener(futureListener);
	}

	private String _createClasspath(
		Path dirPath, DirectoryStream.Filter<Path> filter) {

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				dirPath, filter)) {

			StringBundler sb = new StringBundler();

			directoryStream.forEach(
				path -> {
					sb.append(path);
					sb.append(File.pathSeparator);
				});

			if (sb.index() > 0) {
				sb.setIndex(sb.index() - 1);
			}

			return sb.toString();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to iterate " + dirPath, ioException);
		}
	}

	private ProcessConfig _createProcessConfig(
		List<String> arguments, String bootstrapClassPath,
		Map<String, String> environment, String runtimeClassPath) {

		ProcessConfig.Builder builder = new ProcessConfig.Builder();

		return builder.setArguments(
			arguments
		).setBootstrapClassPath(
			bootstrapClassPath
		).setEnvironment(
			environment
		).setJavaExecutable(
			System.getProperty("java.home") + "/bin/java"
		).setRuntimeClassPath(
			runtimeClassPath
		).build();
	}

	private boolean _fileNameContains(Path path, String s) {
		String name = String.valueOf(path.getFileName());

		return name.contains(s);
	}

	private String _getBootstrapClassPath() {
		return _createClasspath(
			Paths.get(PropsValues.LIFERAY_SHIELDED_CONTAINER_LIB_PORTAL_DIR),
			path -> _fileNameContains(path, "petra"));
	}

	private URL _getBundleURL(Class<?> clazz) {
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();

		CodeSource codeSource = protectionDomain.getCodeSource();

		return codeSource.getLocation();
	}

	private String _getClusterName() {
		return _elasticsearchConfigurationWrapper.clusterName();
	}

	private Distribution _getElasticsearchDistribution(String sidecarVersion) {
		if (sidecarVersion.equals(ElasticsearchDistribution.VERSION)) {
			return new ElasticsearchDistribution();
		}

		throw new IllegalArgumentException(
			"Unsupported Elasticsearch version: " + sidecarVersion);
	}

	private HashMap<String, String> _getEnvironment() {
		return HashMapBuilder.put(
			"HOSTNAME", "localhost"
		).put(
			"LIBFFI_TMPDIR", _sidecarHomePath.toString()
		).build();
	}

	private List<String> _getJVMArguments() {
		List<String> arguments = new ArrayList<>();

		for (String jvmOption :
				_elasticsearchConfigurationWrapper.sidecarJVMOptions()) {

			if (jvmOption.contains("|")) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						jvmOption + " is not a valid format for the JVM " +
							"options and will be ignored");
				}
			}
			else {
				arguments.add(jvmOption);
			}
		}

		if (_elasticsearchConfigurationWrapper.sidecarDebug()) {
			arguments.add(
				_elasticsearchConfigurationWrapper.sidecarDebugSettings());
		}

		arguments.add("-Djava.io.tmpdir=" + _sidecarTempPath.toAbsolutePath());
		arguments.add(
			"-Dsidecar.heart.beat.interval=" +
				_elasticsearchConfigurationWrapper.sidecarHeartbeatInterval());
		arguments.add(
			StringBundler.concat(
				"-Dsidecar.log4j2.properties=",
				"logger.bootstrapchecks.name=org.elasticsearch.bootstrap.",
				"BootstrapChecks\nlogger.bootstrapchecks.level=error\n",
				"logger.deprecation.name=org.elasticsearch.deprecation\n",
				"logger.deprecation.level=error\n",
				ResourceUtil.getResourceAsString(
					Sidecar.class, "/log4j2.properties")));
		arguments.add("-Dsidecar.settings=" + _getSettings());
		arguments.add("--enable-native-access=ALL-UNNAMED");
		arguments.add(
			"--enable-native-access=org.elasticsearch.nativeaccess," +
				"org.apache.lucene.core");

		if (JavaDetector.isJDK21() && OSDetector.isLinux()) {
			arguments.add("-XX:-UseContainerSupport");
		}

		// Modules

		arguments.add("--add-modules=jdk.incubator.vector");
		arguments.add("--add-modules=jdk.management.agent");
		arguments.add("--add-modules=jdk.net");
		arguments.add("--add-modules=ALL-MODULE-PATH");
		arguments.add(
			"--add-opens=org.elasticsearch.server/org.elasticsearch." +
				"bootstrap=ALL-UNNAMED");
		arguments.add("--module-path=" + _sidecarHomePath.resolve("lib"));

		// Apply agent to load modified classes

		Path agentPath = null;

		URL sidecarAgentBundleURL = _getBundleURL(SidecarAgent.class);

		try {
			agentPath = Path.of(sidecarAgentBundleURL.toURI());
		}
		catch (URISyntaxException uriSyntaxException) {
			if (_log.isDebugEnabled()) {
				_log.debug(uriSyntaxException);
			}

			File file = new File(sidecarAgentBundleURL.getPath());

			agentPath = file.toPath();
		}

		arguments.add("-javaagent:" + agentPath);

		return arguments;
	}

	private String _getNodeName() {
		String nodeName = _elasticsearchConfigurationWrapper.nodeName();

		if (!Validator.isBlank(nodeName)) {
			return nodeName;
		}

		return "liferay_sidecar";
	}

	private String _getSettings() {
		SettingsHelperImpl settingsHelperImpl = new SettingsHelperImpl(
			Settings.builder());

		String defaultConfigurations = ResourceUtil.getResourceAsString(
			getClass(),
			SidecarConstants.ELASTICSEARCH_OPTIONAL_DEFAULTS_FILE_NAME);

		settingsHelperImpl.loadFromSource(defaultConfigurations);

		settingsHelperImpl.put("action.auto_create_index", false);
		settingsHelperImpl.put(
			"bootstrap.memory_lock",
			_elasticsearchConfigurationWrapper.bootstrapMlockAll());

		// Configure clustering

		settingsHelperImpl.put("cluster.name", _getClusterName());
		settingsHelperImpl.put(
			"cluster.routing.allocation.disk.threshold_enabled", false);
		settingsHelperImpl.put("discovery.type", "single-node");

		// Configure HTTP

		HttpPortRange httpPortRange = new HttpPortRange(
			_elasticsearchConfigurationWrapper);

		settingsHelperImpl.put("http.port", httpPortRange.toSettingsString());

		settingsHelperImpl.put(
			"http.cors.enabled",
			_elasticsearchConfigurationWrapper.httpCORSEnabled());

		if (_elasticsearchConfigurationWrapper.httpCORSEnabled()) {
			settingsHelperImpl.put(
				"http.cors.allow-origin",
				_elasticsearchConfigurationWrapper.httpCORSAllowOrigin());

			settingsHelperImpl.loadFromSource(
				_elasticsearchConfigurationWrapper.httpCORSConfigurations());
		}

		// Configure networking

		String networkBindHost =
			_elasticsearchConfigurationWrapper.networkBindHost();
		String networkHost = _elasticsearchConfigurationWrapper.networkHost();
		String networkPublishHost =
			_elasticsearchConfigurationWrapper.networkPublishHost();

		if (Validator.isNotNull(networkBindHost)) {
			settingsHelperImpl.put("network.bind_host", networkBindHost);
		}

		if (Validator.isNotNull(networkHost)) {
			settingsHelperImpl.put("network.host", networkHost);
		}

		if (Validator.isNotNull(networkPublishHost)) {
			settingsHelperImpl.put("network.publish_host", networkPublishHost);
		}

		String transportTcpPort =
			_elasticsearchConfigurationWrapper.transportTcpPort();

		if (Validator.isNotNull(transportTcpPort)) {
			settingsHelperImpl.put("transport.port", transportTcpPort);
		}

		settingsHelperImpl.put("node.name", _getNodeName());
		settingsHelperImpl.put(
			"node.roles", List.of("master", "ingest", "data"));

		// Configure paths

		Path dataParentPath = _sidecarWorkPath.resolve("data/elasticsearch7");

		settingsHelperImpl.put(
			"path.data", String.valueOf(dataParentPath.resolve("indices")));

		settingsHelperImpl.put(
			"path.home", String.valueOf(_sidecarHomePath.toAbsolutePath()));

		settingsHelperImpl.put(
			"path.logs", String.valueOf(_sidecarWorkPath.resolve("logs")));

		settingsHelperImpl.put(
			"path.repo", String.valueOf(dataParentPath.resolve("repo")));

		if (JavaDetector.isJDK21()) {
			settingsHelperImpl.put("thread_pool.warmer.max", "20");
		}

		settingsHelperImpl.put("node.store.allow_mmap", false);

		settingsHelperImpl.loadFromSource(
			_elasticsearchConfigurationWrapper.additionalConfigurations());

		Settings settings = settingsHelperImpl.build();

		Set<String> keys = new TreeSet<>(settings.keySet());

		StringBundler sb = new StringBundler(keys.size() * 4);

		for (String key : keys) {
			sb.append(key);
			sb.append(": ");
			sb.append(settings.get(key));
			sb.append(StringPool.NEW_LINE);
		}

		return sb.toString();
	}

	private String _getSidecarVersion() {
		return ResourceUtil.getResourceAsString(
			getClass(), SidecarConstants.SIDECAR_VERSION_FILE_NAME);
	}

	private void _installElasticsearchIfNeeded(String sidecarVersion) {
		ElasticsearchInstaller.builder(
		).distributablesDirectoryPath(
			_sidecarWorkPath
		).distribution(
			_getElasticsearchDistribution(sidecarVersion)
		).installationDirectoryPath(
			_sidecarHomePath
		).build(
		).install();

		Path modulesPath = _sidecarHomePath.resolve(
			_SIDECAR_MODULES_FOLDER_NAME);

		if (Files.exists(modulesPath)) {
			return;
		}

		Path defaultModulesPath = _sidecarHomePath.resolve(
			_DEFAULT_MODULES_FOLDER_NAME);

		try {
			PathUtil.copyDirectory(
				defaultModulesPath, modulesPath,
				dir -> {
					if (Objects.equals(dir, defaultModulesPath)) {
						return false;
					}

					for (String sidecarModuleName :
							_elasticsearchConfigurationWrapper.
								sidecarModuleNames()) {

						if (dir.startsWith(
								defaultModulesPath.resolve(
									sidecarModuleName))) {

							return false;
						}
					}

					return true;
				});
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final String _DEFAULT_MODULES_FOLDER_NAME = "modules";

	private static final String _SIDECAR_MODULES_FOLDER_NAME =
		"liferay-sidecar-modules";

	private static final Log _log = LogFactoryUtil.getLog(Sidecar.class);

	private String _address;
	private final ElasticsearchConfigurationWrapper
		_elasticsearchConfigurationWrapper;
	private ProcessChannel<Serializable> _processChannel;
	private final ProcessExecutor _processExecutor;
	private FutureListener<Serializable> _restartFutureListener;
	private final Path _sidecarHomePath;
	private SidecarManager _sidecarManager;
	private final File _sidecarProcessFile;
	private final Path _sidecarTempPath;
	private final Path _sidecarWorkPath;

	private static class RestartFutureListener
		implements FutureListener<Serializable> {

		public RestartFutureListener(SidecarManager sidecarManager) {
			_sidecarManager = sidecarManager;
		}

		@Override
		public void complete(Future<Serializable> future) {
			try {
				future.get();
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Sidecar Elasticsearch process is aborted", exception);
				}
			}

			if (_sidecarManager.isStartupSuccessful()) {
				if (_log.isInfoEnabled()) {
					_log.info("Restarting sidecar Elasticsearch process");
				}

				_sidecarManager.applyConfigurations();
			}
		}

		private SidecarManager _sidecarManager;

	}

}