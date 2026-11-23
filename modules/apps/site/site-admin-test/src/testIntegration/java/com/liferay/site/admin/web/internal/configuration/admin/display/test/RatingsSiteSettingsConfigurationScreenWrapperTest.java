/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.ratings.kernel.RatingsType;
import com.liferay.ratings.kernel.definition.PortletRatingsDefinitionUtil;
import com.liferay.ratings.kernel.transformer.RatingsDataTransformerUtil;

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
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class RatingsSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			RatingsSiteSettingsConfigurationScreenWrapperTest.class);

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
	@TestInfo("LPD-116328")
	public void testExportImportProperties() throws Exception {
		Group sourceGroup = GroupTestUtil.addGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			sourceGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			_PROPERTY_KEY, RatingsType.STACKED_STARS.getValue());

		_groupLocalService.updateGroup(
			sourceGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-ratings");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(1, properties.size());
		Assert.assertEquals(
			RatingsType.STACKED_STARS.getValue(),
			properties.get(_PROPERTY_KEY));

		Group targetGroup = GroupTestUtil.addGroup();

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		Assert.assertEquals(
			RatingsType.STACKED_STARS,
			PortletRatingsDefinitionUtil.getRatingsType(
				targetGroup.getCompanyId(), targetGroup.getGroupId(),
				BlogsEntry.class.getName()));

		properties = configurationScreen.exportProperties(
			sourceGroup.getGroupId());

		Assert.assertEquals(
			RatingsType.STACKED_STARS.getValue(),
			properties.get(_PROPERTY_KEY));
	}

	@Test
	@TestInfo("LPD-116328")
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-ratings");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	@TestInfo("LPD-116328")
	public void testImportProperties() throws Exception {
		_testImportPropertiesWithInvalidRatingsType();
		_testImportPropertiesWithStagingGroup();
		_testImportPropertiesWithoutRatingsType();
	}

	private void _testImportPropertiesWithInvalidRatingsType()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-ratings");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				_PROPERTY_KEY, RatingsType.STACKED_STARS.getValue()
			).build(),
			group.getGroupId());

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				_PROPERTY_KEY, RandomTestUtil.randomString()
			).build(),
			group.getGroupId());

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	private void _testImportPropertiesWithStagingGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-ratings");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				_PROPERTY_KEY, RatingsType.STACKED_STARS.getValue()
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(
			RatingsType.STACKED_STARS.getValue(),
			group.getTypeSettingsProperty(_PROPERTY_KEY));

		Group stagingGroup = group.getStagingGroup();

		Assert.assertNull(stagingGroup.getTypeSettingsProperty(_PROPERTY_KEY));
	}

	private void _testImportPropertiesWithoutRatingsType() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-ratings");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				_PROPERTY_KEY, RatingsType.STACKED_STARS.getValue()
			).build(),
			group.getGroupId());

		configurationScreen.importProperties(
			new HashMapDictionary<>(), group.getGroupId());

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertEquals(
			RatingsType.STACKED_STARS.getValue(),
			properties.get(_PROPERTY_KEY));
	}

	private static final String _PROPERTY_KEY =
		RatingsDataTransformerUtil.getPropertyKey(BlogsEntry.class.getName());

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

}