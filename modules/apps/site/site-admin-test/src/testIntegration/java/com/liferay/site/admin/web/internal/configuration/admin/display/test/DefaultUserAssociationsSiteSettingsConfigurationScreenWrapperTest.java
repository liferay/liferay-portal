/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.NoSuchRoleException;
import com.liferay.portal.kernel.exception.NoSuchTeamException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
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
@RunWith(Arquillian.class)
public class DefaultUserAssociationsSiteSettingsConfigurationScreenWrapperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			DefaultUserAssociationsSiteSettingsConfigurationScreenWrapperTest.
				class);

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
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		Group sourceGroup = GroupTestUtil.addGroup();

		Team sourceTeam = _addTeam(sourceGroup, RandomTestUtil.randomString());

		_updateTypeSettings(
			new long[] {role.getRoleId()}, new long[] {sourceTeam.getTeamId()},
			sourceGroup);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-default-user-associations");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(sourceGroup.getGroupId());

		Assert.assertEquals(properties.toString(), 2, properties.size());
		Assert.assertEquals(
			ListUtil.fromArray(role.getExternalReferenceCode()),
			properties.get("defaultSiteRoleExternalReferenceCodes"));
		Assert.assertEquals(
			ListUtil.fromArray(sourceTeam.getName()),
			properties.get("defaultTeamNames"));

		Group targetGroup = GroupTestUtil.addGroup();

		Team targetTeam = _addTeam(targetGroup, sourceTeam.getName());

		configurationScreen.importProperties(
			properties, targetGroup.getGroupId());

		_assertDefaultSiteRoleIds(targetGroup, role.getRoleId());
		_assertDefaultTeamIds(targetGroup, targetTeam.getTeamId());
	}

	@Test
	public void testExportProperties() throws Exception {
		Group group = GroupTestUtil.addGroup();

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-default-user-associations");

		Dictionary<String, Object> properties =
			configurationScreen.exportProperties(group.getGroupId());

		Assert.assertTrue(properties.isEmpty());
	}

	@Test
	public void testImportProperties() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);

		long[] defaultSiteRoleIds = {role.getRoleId()};

		Group group = GroupTestUtil.addGroup();

		Team team = _addTeam(group, RandomTestUtil.randomString());

		long[] defaultTeamIds = {team.getTeamId()};

		_updateTypeSettings(defaultSiteRoleIds, defaultTeamIds, group);

		ConfigurationScreen configurationScreen = _serviceTrackerMap.getService(
			"site-configuration-default-user-associations");

		configurationScreen.importProperties(
			new HashMapDictionary<>(), group.getGroupId());

		_assertDefaultSiteRoleIds(group, defaultSiteRoleIds);
		_assertDefaultTeamIds(group, defaultTeamIds);

		team = _addTeam(group, RandomTestUtil.randomString());

		defaultTeamIds = new long[] {team.getTeamId()};

		configurationScreen.importProperties(
			HashMapDictionaryBuilder.<String, Object>put(
				"defaultTeamNames", ListUtil.fromArray(team.getName())
			).build(),
			group.getGroupId());

		_assertDefaultSiteRoleIds(group, defaultSiteRoleIds);
		_assertDefaultTeamIds(group, defaultTeamIds);

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"defaultSiteRoleExternalReferenceCodes",
					ListUtil.fromArray(RandomTestUtil.randomString())
				).put(
					"defaultTeamNames", ListUtil.fromArray(team.getName())
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchRoleException noSuchRoleException) {
			_assertDefaultSiteRoleIds(group, defaultSiteRoleIds);
			_assertDefaultTeamIds(group, defaultTeamIds);
		}

		try {
			configurationScreen.importProperties(
				HashMapDictionaryBuilder.<String, Object>put(
					"defaultSiteRoleExternalReferenceCodes",
					ListUtil.fromArray(role.getExternalReferenceCode())
				).put(
					"defaultTeamNames",
					ListUtil.fromArray(
						team.getName(), RandomTestUtil.randomString())
				).build(),
				group.getGroupId());

			Assert.fail();
		}
		catch (NoSuchTeamException noSuchTeamException) {
			_assertDefaultSiteRoleIds(group, defaultSiteRoleIds);
			_assertDefaultTeamIds(group, defaultTeamIds);
		}
	}

	private Team _addTeam(Group group, String name) throws Exception {
		return _teamLocalService.addTeam(
			TestPropsValues.getUserId(), group.getGroupId(), name,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private void _assertDefaultSiteRoleIds(
			Group group, long... defaultSiteRoleIds)
		throws Exception {

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(
			StringUtil.merge(defaultSiteRoleIds),
			group.getTypeSettingsProperty("defaultSiteRoleIds"));
	}

	private void _assertDefaultTeamIds(Group group, long... defaultTeamIds)
		throws Exception {

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(
			StringUtil.merge(defaultTeamIds),
			group.getTypeSettingsProperty("defaultTeamIds"));
	}

	private void _updateTypeSettings(
			long[] defaultSiteRoleIds, long[] defaultTeamIds, Group group)
		throws Exception {

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.put(
			"defaultSiteRoleIds", StringUtil.merge(defaultSiteRoleIds));
		typeSettingsUnicodeProperties.put(
			"defaultTeamIds", StringUtil.merge(defaultTeamIds));

		_groupLocalService.updateGroup(
			group.getGroupId(), typeSettingsUnicodeProperties.toString());
	}

	@Inject
	private GroupLocalService _groupLocalService;

	private ServiceTrackerMap<String, ConfigurationScreen> _serviceTrackerMap;

	@Inject
	private TeamLocalService _teamLocalService;

}