/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.constants.DDMActionKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.test.util.DDMFormInstanceTestUtil;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceNameComparator;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class DDMFormInstanceServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGetFormInstance() throws Exception {
		User user = UserTestUtil.addUser();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user)) {

			DDMFormInstance ddmFormInstance =
				DDMFormInstanceTestUtil.addDDMFormInstance(
					_groupLocalService.getGroup(TestPropsValues.getGroupId()),
					TestPropsValues.getUserId());
			Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DDMFormInstance.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(ddmFormInstance.getFormInstanceId()),
				role.getRoleId(),
				new String[] {DDMActionKeys.ADD_FORM_INSTANCE_RECORD});

			_userLocalService.addRoleUser(role.getRoleId(), user);

			AssertUtils.assertFailure(
				PrincipalException.MustHavePermission.class,
				StringBundler.concat(
					"User ", user.getUserId(),
					" must have VIEW permission for ",
					"com.liferay.dynamic.data.mapping.model.DDMFormInstance ",
					ddmFormInstance.getFormInstanceId()),
				() -> _ddmFormInstanceService.getFormInstance(
					ddmFormInstance.getFormInstanceId()));

			_resourcePermissionLocalService.setResourcePermissions(
				TestPropsValues.getCompanyId(), DDMFormInstance.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(ddmFormInstance.getFormInstanceId()),
				role.getRoleId(), new String[] {ActionKeys.VIEW});

			_ddmFormInstanceService.getFormInstance(
				ddmFormInstance.getFormInstanceId());
		}
	}

	@Test
	public void testSearch() throws Exception {
		Locale defaultLocale = LocaleThreadLocal.getDefaultLocale();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				TestPropsValues.getUser())) {

			Group group = GroupTestUtil.addGroup();

			_addDDMFormInstance(group, "able");
			_addDDMFormInstance(group, "[bob");

			LocaleThreadLocal.setDefaultLocale(LocaleUtil.JAPAN);

			Assert.assertEquals(
				Arrays.asList("[bob", "able"),
				ListUtil.toList(
					_ddmFormInstanceService.search(
						group.getCompanyId(), group.getGroupId(), null,
						QueryUtil.ALL_POS, QueryUtil.ALL_POS,
						DDMFormInstanceNameComparator.getInstance(true)),
					ddmFormInstance -> ddmFormInstance.getName(LocaleUtil.US)));
		}
		finally {
			LocaleThreadLocal.setDefaultLocale(defaultLocale);
		}
	}

	private void _addDDMFormInstance(Group group, String name)
		throws Exception {

		DDMFormInstance ddmFormInstance =
			DDMFormInstanceTestUtil.addDDMFormInstance(
				group, TestPropsValues.getUserId());

		ddmFormInstance.setNameMap(
			Collections.singletonMap(LocaleUtil.US, name));

		_ddmFormInstanceLocalService.updateDDMFormInstance(ddmFormInstance);
	}

	@Inject
	private DDMFormInstanceLocalService _ddmFormInstanceLocalService;

	@Inject
	private DDMFormInstanceService _ddmFormInstanceService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}