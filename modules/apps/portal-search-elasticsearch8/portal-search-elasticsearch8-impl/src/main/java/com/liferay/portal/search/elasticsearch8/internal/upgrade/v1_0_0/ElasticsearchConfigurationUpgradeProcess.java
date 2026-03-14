/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.persistence.upgrade.ConfigurationUpgradeStepFactory;
import com.liferay.portal.file.install.constants.FileInstallConstants;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConnectionConfiguration;

import java.util.Dictionary;
import java.util.Enumeration;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Bryan Engler
 */
public class ElasticsearchConfigurationUpgradeProcess extends UpgradeProcess {

	public ElasticsearchConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin,
		ConfigurationUpgradeStepFactory configurationUpgradeStepFactory) {

		_configurationAdmin = configurationAdmin;
		_configurationUpgradeStepFactory = configurationUpgradeStepFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeElasticsearchConfiguration();
		_upgradeElasticsearchConnectionConfigurations();
	}

	private void _updateProperties(Dictionary<String, Object> properties) {
		properties.remove("discoveryZenPingUnicastHostsPort");

		Object embeddedHttpPort = properties.remove("embeddedHttpPort");
		String sidecarHttpPort = GetterUtil.getString(
			properties.get("sidecarHttpPort"));

		if ((embeddedHttpPort != null) &&
			sidecarHttpPort.equals(StringPool.BLANK)) {

			properties.put("sidecarHttpPort", String.valueOf(embeddedHttpPort));
		}

		String operationMode = GetterUtil.getString(
			properties.remove("operationMode"));

		if (StringUtil.equals(operationMode, "REMOTE")) {
			properties.put("productionModeEnabled", Boolean.TRUE);
		}

		Object trackTotalHits = properties.remove("trackTotalHits");

		if ((trackTotalHits != null) &&
			!GetterUtil.getBoolean(trackTotalHits)) {

			int indexMaxResultWindow = GetterUtil.getInteger(
				properties.get("indexMaxResultWindow"));

			if (indexMaxResultWindow > 0) {
				properties.put("trackTotalHitsLimit", indexMaxResultWindow);
			}
		}

		properties.remove("restClientLoggerLevel");
	}

	private void _upgradeElasticsearchConfiguration() throws Exception {
		Configuration elasticsearch7configuration =
			_configurationAdmin.getConfiguration(
				_CLASS_NAME_ELASTICSEARCH_CONFIGURATION, StringPool.QUESTION);

		Dictionary<String, Object> elasticsearch7properties =
			elasticsearch7configuration.getProperties();

		if (elasticsearch7properties == null) {
			return;
		}

		if (_log.isInfoEnabled()) {
			_log.info(
				"Elasticsearch 7 configuration detected. Attempting to " +
					"migrate properties to Elasticsearch 8. Manual updates " +
						"to the configuration may be required.");
		}

		_updateProperties(elasticsearch7properties);

		UpgradeStep upgradeStep =
			_configurationUpgradeStepFactory.createUpgradeStep(
				_CLASS_NAME_ELASTICSEARCH_CONFIGURATION,
				ElasticsearchConfiguration.class.getName());

		upgradeStep.upgrade();

		Configuration elasticsearch8configuration =
			_configurationAdmin.getConfiguration(
				ElasticsearchConfiguration.class.getName(),
				StringPool.QUESTION);

		Dictionary<String, Object> elasticsearch8properties =
			elasticsearch8configuration.getProperties();

		_updateProperties(elasticsearch8properties);

		Enumeration<String> enumeration = elasticsearch7properties.keys();

		while (enumeration.hasMoreElements()) {
			String key = enumeration.nextElement();

			if (key.equals(FileInstallConstants.FELIX_FILE_INSTALL_FILENAME) ||
				key.startsWith("service.")) {

				continue;
			}

			elasticsearch8properties.put(
				key, elasticsearch7properties.get(key));
		}

		elasticsearch8configuration.update(elasticsearch8properties);
	}

	private void _upgradeElasticsearchConnectionConfigurations()
		throws Exception {

		UpgradeStep upgradeStep =
			_configurationUpgradeStepFactory.createUpgradeStep(
				_CLASS_NAME_ELASTICSEARCH_CONNECTION_CONFIGURATION,
				ElasticsearchConnectionConfiguration.class.getName());

		upgradeStep.upgrade();
	}

	private static final String _CLASS_NAME_ELASTICSEARCH_CONFIGURATION =
		"com.liferay.portal.search.elasticsearch7.configuration." +
			"ElasticsearchConfiguration";

	private static final String
		_CLASS_NAME_ELASTICSEARCH_CONNECTION_CONFIGURATION =
			"com.liferay.portal.search.elasticsearch7.configuration." +
				"ElasticsearchConnectionConfiguration";

	private static final Log _log = LogFactoryUtil.getLog(
		ElasticsearchConfigurationUpgradeProcess.class);

	private final ConfigurationAdmin _configurationAdmin;
	private final ConfigurationUpgradeStepFactory
		_configurationUpgradeStepFactory;

}