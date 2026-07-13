/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactoryUtil;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalServiceUtil;
import com.liferay.exportimport.kernel.service.ExportImportServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class ExportImportServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupLocalServiceUtil.getCompanyGroup(
			TestPropsValues.getCompanyId());
	}

	@Test
	@TestInfo("LPD-78893")
	public void testExportLayoutsAsFileInBackground() throws Exception {
		_testExportLayoutsAsFileInBackgroundWithControlPanelPermission();
		_testExportLayoutsAsFileInBackgroundWithoutPermission();
	}

	private ExportImportConfiguration _addExportImportConfiguration(User user)
		throws Exception {

		Map<String, Serializable> settingsMap =
			ExportImportConfigurationSettingsMapFactoryUtil.
				buildExportLayoutSettingsMap(
					user.getUserId(), _group.getGroupId(), false, new long[0],
					new HashMap<>(), user.getLocale(), user.getTimeZone());

		return ExportImportConfigurationLocalServiceUtil.
			addExportImportConfiguration(
				user.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				ExportImportConfigurationConstants.TYPE_EXPORT_LAYOUT,
				settingsMap,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), user.getUserId()));
	}

	private User _addUserWithControlPanelPermission(String portletId)
		throws Exception {

		User user = UserTestUtil.addUser();
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		RoleLocalServiceUtil.addUserRole(user.getUserId(), role.getRoleId());

		RoleTestUtil.addResourcePermission(
			role, portletId, ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()),
			ActionKeys.ACCESS_IN_CONTROL_PANEL);

		return user;
	}

	private void _testExportLayoutsAsFileInBackgroundWithControlPanelPermission()
		throws Exception {

		User user = _addUserWithControlPanelPermission(
			PortletKeys.COMPANY_EXPORT);

		ExportImportConfiguration exportImportConfiguration =
			_addExportImportConfiguration(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			long backgroundTaskId =
				ExportImportServiceUtil.exportLayoutsAsFileInBackground(
					exportImportConfiguration);

			Assert.assertTrue(backgroundTaskId > 0);
		}
	}

	private void _testExportLayoutsAsFileInBackgroundWithoutPermission()
		throws Exception {

		User user = UserTestUtil.addUser();

		ExportImportConfiguration exportImportConfiguration =
			_addExportImportConfiguration(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			Assert.assertThrows(
				PrincipalException.class,
				() -> ExportImportServiceUtil.exportLayoutsAsFileInBackground(
					exportImportConfiguration));
		}
	}

	private Group _group;

}