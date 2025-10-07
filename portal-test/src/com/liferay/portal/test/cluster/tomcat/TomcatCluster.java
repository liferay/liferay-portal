/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.cluster.tomcat;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.PropsKeys;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author Shuyang Zhou
 */
public class TomcatCluster {

	public TomcatCluster(Class<?> clusterOwnerClass) throws IOException {
		_clusterOwnerClass = clusterOwnerClass;

		_elasticSearchNetworkHostAddresses = SystemBundleUtil.callService(
			"com.liferay.portal.search.elasticsearch7.internal.connection." +
				"ElasticsearchConnectionManager",
			elasticsearchConnectionManager -> {
				Object elasticsearchConnection = ReflectionTestUtil.invoke(
					elasticsearchConnectionManager,
					"getElasticsearchConnection", new Class<?>[0]);

				return ReflectionTestUtil.getFieldValue(
					elasticsearchConnection, "_networkHostAddresses");
			});
	}

	public Builder buildTomcatNode() {
		return new Builder();
	}

	public String getClusterName() {
		return _clusterOwnerClass.getName();
	}

	public List<TomcatNode> getTomcatNodes() {
		return _tomcatNodes;
	}

	public class Builder {

		public TomcatNode build() throws IOException {
			TomcatNode tomcatNode = new TomcatNode(
				_clusterOwnerClass, _getCatalinaHome(), _getConnectorPort(),
				_getCopyPropertyKeys(), _elasticSearchNetworkHostAddresses,
				_getGogoShellPort(), _jpdaEnabled, _getJPDAPort(), _jvmArgs,
				_getLiferayHome(), _nodeId, _getShutdownPort());

			_tomcatNodes.add(tomcatNode);

			return tomcatNode;
		}

		public void configureCopyPropertyKeys(Consumer<List<String>> consumer) {
			_copyPropertyKeys = new ArrayList<>(_getCopyPropertyKeys());

			consumer.accept(_copyPropertyKeys);
		}

		public void setCatalinaHome(String catalinaHome) {
			_catalinaHome = catalinaHome;
		}

		public void setConnectorPort(int connectorPort) {
			_connectorPort = connectorPort;
		}

		public void setGogoShellPort(int gogoShellPort) {
			_gogoShellPort = gogoShellPort;
		}

		public void setJpdaEnabled(boolean jpdaEnabled) {
			_jpdaEnabled = jpdaEnabled;
		}

		public void setJpdaPort(int jpdaPort) {
			_jpdaPort = jpdaPort;
		}

		public void setJvmArgs(String jvmArgs) {
			_jvmArgs = jvmArgs;
		}

		public void setLiferayHome(String liferayHome) {
			_liferayHome = liferayHome;
		}

		public void setShutdownPort(int shutdownPort) {
			_shutdownPort = shutdownPort;
		}

		private String _getCatalinaHome() {
			if (_catalinaHome == null) {
				_catalinaHome = System.getProperty("catalina.home");
			}

			return _catalinaHome;
		}

		private int _getConnectorPort() {
			if (_connectorPort == 0) {
				_connectorPort = _connectorPortCounter.getAndIncrement();
			}

			return _connectorPort;
		}

		private List<String> _getCopyPropertyKeys() {
			if (_copyPropertyKeys == null) {
				_copyPropertyKeys = _defaultCopyPropertyKeys;
			}

			return _copyPropertyKeys;
		}

		private int _getGogoShellPort() {
			if (_gogoShellPort == 0) {
				_gogoShellPort = _gogoShellPortCounter.getAndIncrement();
			}

			return _gogoShellPort;
		}

		private int _getJPDAPort() {
			if (_jpdaPort == 0) {
				_jpdaPort = _jpdaPortCounter.getAndIncrement();
			}

			return _jpdaPort;
		}

		private String _getLiferayHome() {
			if (_liferayHome == null) {
				try {
					Path path = Files.createTempDirectory(
						"tomcat-node-" + _nodeId + "-liferay-home-");

					_liferayHome = path.toString();
				}
				catch (IOException ioException) {
					return ReflectionUtil.throwException(ioException);
				}
			}

			return _liferayHome;
		}

		private int _getShutdownPort() {
			if (_shutdownPort == 0) {
				_shutdownPort = _shutdownPortCounter.getAndIncrement();
			}

			return _shutdownPort;
		}

		private String _catalinaHome;
		private int _connectorPort;
		private List<String> _copyPropertyKeys;
		private int _gogoShellPort;
		private boolean _jpdaEnabled;
		private int _jpdaPort;
		private String _jvmArgs;
		private String _liferayHome;
		private final int _nodeId = _nodeIdCounter.getAndIncrement();
		private int _shutdownPort;

	}

	private static final AtomicInteger _connectorPortCounter =
		new AtomicInteger(8090);
	private static final List<String> _defaultCopyPropertyKeys = Arrays.asList(
		PropsKeys.JDBC_DEFAULT_DRIVER_CLASS_NAME, PropsKeys.JDBC_DEFAULT_URL,
		PropsKeys.JDBC_DEFAULT_USERNAME, PropsKeys.JDBC_DEFAULT_PASSWORD);
	private static final AtomicInteger _gogoShellPortCounter =
		new AtomicInteger(11321);
	private static final AtomicInteger _jpdaPortCounter = new AtomicInteger(
		8010);
	private static final AtomicInteger _nodeIdCounter = new AtomicInteger();
	private static final AtomicInteger _shutdownPortCounter = new AtomicInteger(
		8015);

	private final Class<?> _clusterOwnerClass;
	private final String[] _elasticSearchNetworkHostAddresses;
	private final List<TomcatNode> _tomcatNodes = new ArrayList<>();

}