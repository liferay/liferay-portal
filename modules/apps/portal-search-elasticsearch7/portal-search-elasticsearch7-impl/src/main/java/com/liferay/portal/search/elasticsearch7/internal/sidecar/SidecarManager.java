/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch7.internal.sidecar;

import com.liferay.petra.process.ProcessExecutor;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationObserver;
import com.liferay.portal.search.elasticsearch7.internal.configuration.ElasticsearchConfigurationWrapper;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionBuilder;
import com.liferay.portal.search.elasticsearch7.internal.connection.ElasticsearchConnectionManager;
import com.liferay.portal.search.elasticsearch7.internal.connection.constants.ConnectionConstants;
import com.liferay.portal.search.elasticsearch7.internal.sidecar.constants.SidecarConstants;
import com.liferay.portal.search.elasticsearch7.internal.util.ResourceUtil;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.BundleContext;
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

		if (elasticsearchConfigurationWrapper.isProductionModeEnabled()) {
			elasticsearchConnectionManager.removeElasticsearchConnection(
				ConnectionConstants.SIDECAR_CONNECTION_ID);

			processFile.delete();
		}
		else {
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

			ElasticsearchConnectionBuilder elasticsearchConnectionBuilder =
				new ElasticsearchConnectionBuilder();

			elasticsearchConnectionBuilder.active(
				true
			).connectionId(
				ConnectionConstants.SIDECAR_CONNECTION_ID
			).maxConnections(
				elasticsearchConfigurationWrapper.maxConnections()
			).maxConnectionsPerRoute(
				elasticsearchConfigurationWrapper.maxConnectionsPerRoute()
			).postCloseRunnable(
				_sidecar::stop
			).preConnectElasticsearchConnectionConsumer(
				elasticsearchConnection -> {
					_sidecar.start();

					elasticsearchConnection.setNetworkHostAddresses(
						new String[] {_sidecar.getNetworkHostAddress()});
				}
			);

			elasticsearchConnectionManager.addElasticsearchConnection(
				elasticsearchConnectionBuilder.build());

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
	protected ElasticsearchConfigurationWrapper
		elasticsearchConfigurationWrapper;

	@Reference
	protected ElasticsearchConnectionManager elasticsearchConnectionManager;

	@Reference
	protected ProcessExecutor processExecutor;

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