/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Joshua Cords
 */
public class ElasticsearchConfigurationUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpdatePropertiesOperationModeEmbedded() {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"operationMode", "EMBEDDED"
			).build();

		ElasticsearchConfigurationUpgradeProcess.updateProperties(properties);

		Assert.assertNull(properties.get("operationMode"));
		Assert.assertNull(properties.get("productionModeEnabled"));
	}

	@Test
	public void testUpdatePropertiesOperationModeRemote() {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"operationMode", "REMOTE"
			).build();

		ElasticsearchConfigurationUpgradeProcess.updateProperties(properties);

		Assert.assertNull(properties.get("operationMode"));
		Assert.assertEquals(
			Boolean.TRUE, properties.get("productionModeEnabled"));
	}

	@Test
	public void testUpdatePropertiesOperationModeRemoteAndProductionModeDisabled() {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"operationMode", "REMOTE"
			).put(
				"productionModeEnabled", Boolean.FALSE
			).build();

		ElasticsearchConfigurationUpgradeProcess.updateProperties(properties);

		Assert.assertNull(properties.get("operationMode"));
		Assert.assertEquals(
			Boolean.FALSE, properties.get("productionModeEnabled"));
	}

	@Test
	public void testUpdatePropertiesOperationModeRemoteAndProductionModeEnabled() {
		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"operationMode", "REMOTE"
			).put(
				"productionModeEnabled", Boolean.TRUE
			).build();

		ElasticsearchConfigurationUpgradeProcess.updateProperties(properties);

		Assert.assertNull(properties.get("operationMode"));
		Assert.assertEquals(
			Boolean.TRUE, properties.get("productionModeEnabled"));
	}

}