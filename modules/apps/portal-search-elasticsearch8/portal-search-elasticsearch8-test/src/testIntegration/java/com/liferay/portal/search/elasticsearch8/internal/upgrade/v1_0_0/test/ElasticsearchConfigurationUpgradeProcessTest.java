/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.search.elasticsearch8.configuration.ElasticsearchConfiguration;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;

import java.util.Dictionary;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
public class ElasticsearchConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Configuration configuration = _getConfiguration(
			ElasticsearchConfiguration.class.getName());

		_originalProperties = configuration.getProperties();

		configuration.update(
			HashMapDictionaryBuilder.putAll(
				configuration.getProperties()
			).put(
				"clusterName", "alpha"
			).put(
				"discoveryZenPingUnicastHostsPort", "9300-9400"
			).put(
				"embeddedHttpPort", 9202
			).put(
				"operationMode", "EMBEDDED"
			).put(
				"restClientLoggerLevel", "ERROR"
			).put(
				"trackTotalHits", Boolean.TRUE
			).build());

		configuration = _getConfiguration(
			_CLASS_NAME_ELASTICSEARCH_CONFIGURATION);

		configuration.update(
			HashMapDictionaryBuilder.putAll(
				configuration.getProperties()
			).put(
				"clusterName", "alpha"
			).put(
				"discoveryZenPingUnicastHostsPort", "9300-9400"
			).put(
				"embeddedHttpPort", 9202
			).put(
				"operationMode", "EMBEDDED"
			).put(
				"restClientLoggerLevel", "ERROR"
			).put(
				"trackTotalHits", Boolean.TRUE
			).build());
	}

	@After
	public void tearDown() throws Exception {
		Configuration configuration = _getConfiguration(
			ElasticsearchConfiguration.class.getName());

		configuration.update(_originalProperties);
	}

	@Test
	public void testUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = _getUpgradeProcess();

		upgradeProcess.upgrade();

		Configuration configuration = _getConfiguration(
			ElasticsearchConfiguration.class.getName());

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertEquals(
			"alpha", GetterUtil.getString(properties.get("clusterName")));
		Assert.assertNull(properties.get("discoveryZenPingUnicastHostsPort"));
		Assert.assertNull(properties.get("embeddedHttpPort"));
		Assert.assertNull(properties.get("operationMode"));
		Assert.assertEquals(
			Boolean.FALSE,
			GetterUtil.getBoolean(properties.get("productionModeEnabled")));
		Assert.assertNull(properties.get("restClientLoggerLevel"));
		Assert.assertEquals(
			9202, GetterUtil.getInteger(properties.get("sidecarHttpPort")));
		Assert.assertNull(properties.get("trackTotalHits"));
	}

	private Configuration _getConfiguration(String className) throws Exception {
		return _configurationAdmin.getConfiguration(
			className, StringPool.QUESTION);
	}

	private UpgradeProcess _getUpgradeProcess() {
		UpgradeProcess[] upgradeProcesses = new UpgradeProcess[1];

		_upgradeStepRegistrator.register(
			(fromSchemaVersionString, toSchemaVersionString, upgradeSteps) -> {
				for (UpgradeStep upgradeStep : upgradeSteps) {
					Class<? extends UpgradeStep> clazz = upgradeStep.getClass();

					if (Objects.equals(
							clazz.getName(), _CLASS_NAME_UPGRADE_PROCESS)) {

						upgradeProcesses[0] = (UpgradeProcess)upgradeStep;

						break;
					}
				}
			});

		return upgradeProcesses[0];
	}

	private static final String _CLASS_NAME_ELASTICSEARCH_CONFIGURATION =
		"com.liferay.portal.search.elasticsearch7.configuration." +
			"ElasticsearchConfiguration";

	private static final String _CLASS_NAME_UPGRADE_PROCESS =
		"com.liferay.portal.search.elasticsearch8.internal.upgrade.v1_0_0." +
			"ElasticsearchConfigurationUpgradeProcess";

	@Inject(
		filter = "component.name=com.liferay.portal.search.elasticsearch8.internal.upgrade.registry.ElasticsearchUpgradeStepRegistrator"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private Dictionary<String, Object> _originalProperties;

}