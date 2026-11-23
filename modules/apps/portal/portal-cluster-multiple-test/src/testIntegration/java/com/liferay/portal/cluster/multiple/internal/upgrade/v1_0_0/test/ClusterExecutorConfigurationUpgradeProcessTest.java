/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cluster.multiple.internal.upgrade.v1_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class ClusterExecutorConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@After
	public void tearDown() throws Exception {
		ConfigurationTestUtil.deleteConfiguration(_PID);
	}

	@Test
	public void testUpgrade() throws Exception {
		boolean debugEnabled = RandomTestUtil.randomBoolean();

		ConfigurationTestUtil.saveConfiguration(
			_PID,
			HashMapDictionaryBuilder.<String, Object>put(
				"clusterNodeAddressTimeout", RandomTestUtil.randomLong()
			).put(
				"debugEnabled", debugEnabled
			).build());

		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator,
			"com.liferay.portal.cluster.multiple.internal.upgrade.v1_0_0." +
				"ClusterExecutorConfigurationUpgradeProcess");

		upgradeProcess.upgrade();

		Configuration configuration = _configurationAdmin.getConfiguration(
			_PID, StringPool.QUESTION);

		Dictionary<String, Object> properties = configuration.getProperties();

		Assert.assertNull(properties.get("clusterNodeAddressTimeout"));
		Assert.assertEquals(debugEnabled, properties.get("debugEnabled"));
	}

	private static final String _PID =
		"com.liferay.portal.cluster.multiple.configuration." +
			"ClusterExecutorConfiguration";

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	@Inject(
		filter = "(&(component.name=com.liferay.portal.cluster.multiple.internal.upgrade.registry.PortalClusterMultipleUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}