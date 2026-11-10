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
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

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
@FeatureFlag("LPD-82960")
@RunWith(Arquillian.class)
public class DetailsSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			DetailsSiteSettingsConfigurationScreenWrapperTest.class);

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

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-details");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(
			Boolean.FALSE,
			properties.get(GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE));
		Assert.assertEquals(properties.toString(), 1, properties.size());

		Group targetGroup = GroupTestUtil.addGroup();

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		targetGroup = _groupLocalService.getGroup(targetGroup.getGroupId());

		Assert.assertTrue(targetGroup.isActive());
		Assert.assertFalse(targetGroup.isMaintenanceMode());

		UnicodeProperties typeSettingsUnicodeProperties =
			sourceGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE,
			Boolean.TRUE.toString());

		_groupLocalService.updateGroup(
			sourceGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		properties = configurationScreen.exportProperties(
			sourceGroup.getGroupId());

		Assert.assertEquals(
			Boolean.TRUE,
			properties.get(GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE));

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		targetGroup = _groupLocalService.getGroup(targetGroup.getGroupId());

		Assert.assertFalse(targetGroup.isActive());
		Assert.assertTrue(targetGroup.isMaintenanceMode());

		Group companyGroup = _groupLocalService.getCompanyGroup(
			TestPropsValues.getCompanyId());

		configurationScreen.importProperties(
			properties, companyGroup.getGroupId());

		companyGroup = _groupLocalService.getGroup(companyGroup.getGroupId());

		Assert.assertTrue(companyGroup.isActive());
		Assert.assertFalse(companyGroup.isMaintenanceMode());

		properties = configurationScreen.exportProperties(
			companyGroup.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@FeatureFlag(enable = false, value = "LPD-82960")
	@Test
	public void testExportPropertiesWhenFeatureFlagIsDisabled()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-details");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@FeatureFlag(enable = false, value = "LPD-82960")
	@Test
	public void testImportPropertiesWhenFeatureFlagIsDisabled()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-details");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				GroupConstants.TYPE_SETTINGS_KEY_MAINTENANCE_MODE, true
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertFalse(group.isMaintenanceMode());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}