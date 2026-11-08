/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.sites.kernel.util.Sites;

import java.util.Dictionary;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class AnalyticsSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			AnalyticsSiteSettingsConfigurationScreenWrapperTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ConfigurationScreen.class, null,
			(serviceReference, emitter) -> {
				ConfigurationScreen configurationScreen =
					bundleContext.getService(serviceReference);

				emitter.emit(configurationScreen.getKey());
			});
	}

	@After
	public void tearDown() {
		_serviceTrackerMap.close();
	}

	@Test
	public void testExportImportProperties() throws Exception {
		Group sourceGroup = GroupTestUtil.addGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			sourceGroup.getTypeSettingsProperties();

		String analyticsKey =
			Sites.ANALYTICS_PREFIX + RandomTestUtil.randomString();
		String analyticsScript = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(analyticsKey, analyticsScript);

		typeSettingsUnicodeProperties.put(
			"directoryIndexingEnabled", RandomTestUtil.randomString());

		String googleAnalytics4CustomConfiguration =
			RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalytics4CustomConfiguration",
			googleAnalytics4CustomConfiguration);

		String googleAnalytics4Id = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalytics4Id", googleAnalytics4Id);

		String googleAnalyticsCreateCustomConfiguration =
			RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalyticsCreateCustomConfiguration",
			googleAnalyticsCreateCustomConfiguration);

		String googleAnalyticsCustomConfiguration =
			RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalyticsCustomConfiguration",
			googleAnalyticsCustomConfiguration);

		String googleAnalyticsId = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalyticsId", googleAnalyticsId);

		_groupLocalService.updateGroup(
			sourceGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-analytics");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(properties.toString(), 6, properties.size());
		Assert.assertEquals(analyticsScript, properties.get(analyticsKey));
		Assert.assertEquals(
			googleAnalytics4CustomConfiguration,
			properties.get("googleAnalytics4CustomConfiguration"));
		Assert.assertEquals(
			googleAnalytics4Id, properties.get("googleAnalytics4Id"));
		Assert.assertEquals(
			googleAnalyticsCreateCustomConfiguration,
			properties.get("googleAnalyticsCreateCustomConfiguration"));
		Assert.assertEquals(
			googleAnalyticsCustomConfiguration,
			properties.get("googleAnalyticsCustomConfiguration"));
		Assert.assertEquals(
			googleAnalyticsId, properties.get("googleAnalyticsId"));

		Group targetGroup = GroupTestUtil.addGroup();

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		targetGroup = _groupLocalService.getGroup(targetGroup.getGroupId());

		Assert.assertEquals(
			analyticsScript, targetGroup.getTypeSettingsProperty(analyticsKey));
		Assert.assertNull(
			targetGroup.getTypeSettingsProperty("directoryIndexingEnabled"));
		Assert.assertEquals(
			googleAnalytics4CustomConfiguration,
			targetGroup.getTypeSettingsProperty(
				"googleAnalytics4CustomConfiguration"));
		Assert.assertEquals(
			googleAnalytics4Id,
			targetGroup.getTypeSettingsProperty("googleAnalytics4Id"));
		Assert.assertEquals(
			googleAnalyticsCreateCustomConfiguration,
			targetGroup.getTypeSettingsProperty(
				"googleAnalyticsCreateCustomConfiguration"));
		Assert.assertEquals(
			googleAnalyticsCustomConfiguration,
			targetGroup.getTypeSettingsProperty(
				"googleAnalyticsCustomConfiguration"));
		Assert.assertEquals(
			googleAnalyticsId,
			targetGroup.getTypeSettingsProperty("googleAnalyticsId"));
	}

	@Test
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-analytics");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testImportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		String analyticsKey =
			Sites.ANALYTICS_PREFIX + RandomTestUtil.randomString();
		String analyticsScript = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(analyticsKey, analyticsScript);

		String googleAnalyticsId = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			"googleAnalyticsId", googleAnalyticsId);

		_groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-analytics");

		String googleAnalytics4Id = RandomTestUtil.randomString();

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"directoryIndexingEnabled", RandomTestUtil.randomString()
			).put(
				"googleAnalytics4Id", googleAnalytics4Id
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(
			analyticsScript, group.getTypeSettingsProperty(analyticsKey));
		Assert.assertNull(
			group.getTypeSettingsProperty("directoryIndexingEnabled"));
		Assert.assertEquals(
			googleAnalytics4Id,
			group.getTypeSettingsProperty("googleAnalytics4Id"));
		Assert.assertEquals(
			googleAnalyticsId,
			group.getTypeSettingsProperty("googleAnalyticsId"));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}