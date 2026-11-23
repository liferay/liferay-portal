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
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.TrashHelper;

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
public class RecycleBinSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			RecycleBinSiteSettingsConfigurationScreenWrapperTest.class);

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
	@TestInfo("LPD-116329")
	public void testExportImportProperties() throws Exception {
		Group sourceGroup = GroupTestUtil.addGroup();

		UnicodeProperties typeSettingsUnicodeProperties =
			sourceGroup.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put("trashEnabled", "false");

		int companyTrashEntriesMaxAge = PrefsPropsUtil.getInteger(
			sourceGroup.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		int trashEntriesMaxAge =
			companyTrashEntriesMaxAge + RandomTestUtil.randomInt();

		typeSettingsUnicodeProperties.put(
			"trashEntriesMaxAge", String.valueOf(trashEntriesMaxAge));

		_groupLocalService.updateGroup(
			sourceGroup.getGroupId(), typeSettingsUnicodeProperties.toString());

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(2, properties.size());
		Assert.assertEquals("false", properties.get("trashEnabled"));
		Assert.assertEquals(
			String.valueOf(trashEntriesMaxAge),
			properties.get("trashEntriesMaxAge"));

		Group targetGroup = GroupTestUtil.addGroup();

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		targetGroup = _groupLocalService.getGroup(targetGroup.getGroupId());

		Assert.assertFalse(_trashHelper.isTrashEnabled(targetGroup));
		Assert.assertEquals(
			trashEntriesMaxAge, _trashHelper.getMaxAge(targetGroup));

		properties = configurationScreen.exportProperties(
			sourceGroup.getGroupId());

		Assert.assertEquals("false", properties.get("trashEnabled"));
		Assert.assertEquals(
			String.valueOf(trashEntriesMaxAge),
			properties.get("trashEntriesMaxAge"));
	}

	@Test
	@TestInfo("LPD-116329")
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	@TestInfo("LPD-116329")
	public void testImportProperties() throws Exception {
		_testImportPropertiesWithDefaultTrashEntriesMaxAge();
		_testImportPropertiesWithInvalidValues();
		_testImportPropertiesWithMissingProperties();
		_testImportPropertiesWithStagingGroup();
	}

	private void _testImportPropertiesWithDefaultTrashEntriesMaxAge()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		int companyTrashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEntriesMaxAge",
				String.valueOf(
					companyTrashEntriesMaxAge + RandomTestUtil.randomInt())
			).build(),
			group.getGroupId());

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEntriesMaxAge", String.valueOf(companyTrashEntriesMaxAge)
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertNull(group.getTypeSettingsProperty("trashEntriesMaxAge"));
	}

	private void _testImportPropertiesWithInvalidValues() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		int companyTrashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE);

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEntriesMaxAge",
				String.valueOf(
					companyTrashEntriesMaxAge + RandomTestUtil.randomInt())
			).build(),
			group.getGroupId());

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEnabled", RandomTestUtil.randomString()
			).put(
				"trashEntriesMaxAge", RandomTestUtil.randomString()
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertFalse(_trashHelper.isTrashEnabled(group));
		Assert.assertEquals(
			companyTrashEntriesMaxAge, _trashHelper.getMaxAge(group));
	}

	private void _testImportPropertiesWithMissingProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEnabled", "false"
			).build(),
			group.getGroupId());

		configurationScreen.importProperties(
			new HashMapDictionary<>(), group.getGroupId());

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertEquals("false", properties.get("trashEnabled"));
	}

	private void _testImportPropertiesWithStagingGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		GroupTestUtil.enableLocalStaging(group);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-recycle-bin");

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"trashEnabled", "false"
			).build(),
			group.getGroupId());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(
			"false", group.getTypeSettingsProperty("trashEnabled"));

		Group stagingGroup = group.getStagingGroup();

		Assert.assertNull(stagingGroup.getTypeSettingsProperty("trashEnabled"));
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

	@Inject
	private TrashHelper _trashHelper;

}