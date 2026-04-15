/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.sidecar;

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch8.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnection;
import com.liferay.portal.search.elasticsearch8.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch8.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch8.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch8.internal.util.ResourceUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tina Tian
 */
@Component(enabled = true, service = {})
public class SidecarManager implements ElasticsearchConfigurationObserver {

	@Override
	public int compareTo(
		ElasticsearchConfigurationObserver elasticsearchConfigurationObserver) {

		return elasticsearchConfigurationWrapper.compare(
			this, elasticsearchConfigurationObserver);
	}

	@Override
	public int getPriority() {
		return 1;
	}

	@Override
	public void onElasticsearchConfigurationUpdate() {
		applyConfigurations();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		elasticsearchConfigurationWrapper.register(this);

		applyConfigurations();
	}

	protected void applyConfigurations() {
		File processFile = _bundleContext.getDataFile("sidecar.process");

		boolean es8ProductionMode = elasticsearchConfigurationWrapper.productionModeEnabled();
		boolean es7ProductionMode = _isLegacyProductionModeEnabled();

		if (_log.isInfoEnabled()) {
			_log.info("SidecarManager applyConfigurations: ES8 productionModeEnabled=" + es8ProductionMode);
			_log.info("SidecarManager applyConfigurations: Legacy ES7 productionModeEnabled=" + es7ProductionMode);
		}

		if (es8ProductionMode || es7ProductionMode) {
			if (_log.isInfoEnabled()) {
				_log.info("SidecarManager: Production mode or legacy remote setup detected. Sidecar will NOT start.");
			}

			elasticsearchConnectionManager.removeElasticsearchConnection(
				ConnectionConstants.SIDECAR_CONNECTION_ID);

			processFile.delete();
		}
		else {
			if (_log.isInfoEnabled()) {
				_log.info("SidecarManager: Development mode detected. Attempting to start Sidecar.");
			}

			_startupSuccessful = false;

			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Liferay automatically starts a child process of ",
						"Elasticsearch named sidecar for convenient ",
						"development and demonstration purposes. Do NOT use ",
						"sidecar in production. Refer to the documentation ",
						"for details on the limitations of sidecar and ",
						"instructions on configuring a remote Elasticsearch ",
						"connection in the Control Panel."));
			}

			if (_sidecar != null) {
				_sidecar.stop();
			}

			Path workPath = Paths.get(PropsValues.LIFERAY_HOME);

			_sidecar = new Sidecar(
				elasticsearchConfigurationWrapper, processExecutor,
				_resolveHomePath(workPath), this, processFile, workPath);

			ElasticsearchConnection.Builder builder =
				new ElasticsearchConnection.Builder(
					() -> new String[] {_sidecar.getNetworkHostAddress()});

			builder.active(
				true
			).compressionEnabled(
				elasticsearchConfigurationWrapper.compressionEnabled()
			).connectionId(
				ConnectionConstants.SIDECAR_CONNECTION_ID
			).maxConnections(
				elasticsearchConfigurationWrapper.maxConnections()
			).maxConnectionsPerRoute(
				elasticsearchConfigurationWrapper.maxConnectionsPerRoute()
			).postCloseRunnable(
				_sidecar::stop
			).preConnectRunnable(
				_sidecar::start
			);

			elasticsearchConnectionManager.addElasticsearchConnection(
				builder.build());

			_startupSuccessful = true;
		}
	}

	@Deactivate
	protected void deactivate() {
		elasticsearchConfigurationWrapper.unregister(this);
	}

	protected boolean isStartupSuccessful() {
		return _startupSuccessful;
	}

	@Reference
	protected ConfigurationAdmin configurationAdmin;

	@Reference
	protected ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected ProcessExecutor processExecutor;

	private boolean _isLegacyProductionModeEnabled() {
		try {
			// --- DEBUG MOCKING BLOCK START ---
			// Uncomment the next line to manually simulate a legacy remote ES7 setup:
			// return true;
			// --- DEBUG MOCKING BLOCK END ---

			Configuration elasticsearch8Configuration =
				configurationAdmin.getConfiguration(
					"com.liferay.portal.search.elasticsearch8.configuration." +
						"ElasticsearchConfiguration",
					StringPool.QUESTION);

			if (elasticsearch8Configuration.getProperties() != null) {
				if (_log.isInfoEnabled()) {
					_log.info("SidecarManager: ES8 configuration already exists. Skipping ES7 check.");
				}

				return false;
			}

			Configuration elasticsearch7Configuration =
				configurationAdmin.getConfiguration(
					"com.liferay.portal.search.elasticsearch7.configuration." +
						"ElasticsearchConfiguration",
					StringPool.QUESTION);

			Dictionary<String, Object> properties =
				elasticsearch7Configuration.getProperties();

			if (properties == null) {
				if (_log.isInfoEnabled()) {
					_log.info("SidecarManager: No ES7 configuration found.");
				}

				return false;
			}

			String operationMode = GetterUtil.getString(
				properties.get("operationMode"));

			if (_log.isInfoEnabled()) {
				_log.info("SidecarManager: Found legacy ES7 config. operationMode=" + operationMode);
			}

			if (StringUtil.equals(operationMode, "REMOTE")) {
				return true;
			}

			boolean productionModeEnabled = GetterUtil.getBoolean(
				properties.get("productionModeEnabled"));

			if (_log.isInfoEnabled()) {
				_log.info("SidecarManager: Legacy ES7 productionModeEnabled=" + productionModeEnabled);
			}

			return productionModeEnabled;
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("SidecarManager: Error during legacy ES7 configuration check.", exception);
			}

			return false;
		}
	}

	private Path _resolveHomePath(Path path) {
		String sidecarHome = elasticsearchConfigurationWrapper.sidecarHome();

		if (sidecarHome.equals("elasticsearch-sidecar")) {
			String versionNumber = ResourceUtil.getResourceAsString(
				getClass(), SidecarConstants.SIDECAR_VERSION_FILE_NAME);

			sidecarHome = sidecarHome + "/" + versionNumber;
		}

		Path relativeSidecarHomePath = path.resolve(sidecarHome);

		if (!Files.isDirectory(relativeSidecarHomePath)) {
			Path absoluteSidecarHomePath = Paths.get(sidecarHome);

			if (Files.isDirectory(absoluteSidecarHomePath)) {
				return absoluteSidecarHomePath;
			}
		}

		return relativeSidecarHomePath;
	}

	private static final Log _log = LogFactoryUtil.getLog(SidecarManager.class);

	private BundleContext _bundleContext;
	private Sidecar _sidecar;
	private boolean _startupSuccessful;

}