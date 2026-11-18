/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.account.model.AccountGroup;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalService;
import com.liferay.commerce.product.service.CPConfigurationListRelService;
import com.liferay.commerce.test.util.CommerceAccountGroupTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CPConfigurationListRelServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testDeleteCPConfigurationListRel() throws Exception {
		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CPConfigurationListRel cpConfigurationListRel1 =
			_addCPConfigurationListRel(commerceCatalog1.getGroupId());

		CommerceCatalog commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		CPConfigurationListRel cpConfigurationListRel2 =
			_addCPConfigurationListRel(commerceCatalog2.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission principalException =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() ->
						_cpConfigurationListRelService.
							deleteCPConfigurationListRel(
								_createCPConfigurationListRel(
									cpConfigurationListRel1.
										getCPConfigurationListId(),
									cpConfigurationListRel2.
										getCPConfigurationListRelId())));

			Assert.assertEquals(
				commerceCatalog2.getCommerceCatalogId(),
				principalException.resourceId);

			Assert.assertNotNull(
				_cpConfigurationListRelLocalService.fetchCPConfigurationListRel(
					cpConfigurationListRel2.getCPConfigurationListRelId()));

			_cpConfigurationListRelService.deleteCPConfigurationListRel(
				cpConfigurationListRel1);

			Assert.assertNull(
				_cpConfigurationListRelLocalService.fetchCPConfigurationListRel(
					cpConfigurationListRel1.getCPConfigurationListRelId()));
		}
	}

	private CPConfigurationListRel _addCPConfigurationListRel(long groupId)
		throws Exception {

		AccountGroup accountGroup =
			CommerceAccountGroupTestUtil.addAccountGroup(groupId);

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.addCPConfigurationList(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				groupId, 0, false, RandomTestUtil.randomString(),
				RandomTestUtil.randomDouble(), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
				calendar.get(Calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE), 0, 0, 0, 0, 0, true,
				ServiceContextTestUtil.getServiceContext(groupId));

		return _cpConfigurationListRelLocalService.addCPConfigurationListRel(
			TestPropsValues.getUserId(), AccountGroup.class.getName(),
			accountGroup.getAccountGroupId(),
			cpConfigurationList.getCPConfigurationListId());
	}

	private CPConfigurationListRel _createCPConfigurationListRel(
		long cpConfigurationListId, long cpConfigurationListRelId) {

		CPConfigurationListRel cpConfigurationListRel =
			_cpConfigurationListRelLocalService.createCPConfigurationListRel(
				cpConfigurationListRelId);

		cpConfigurationListRel.setCPConfigurationListId(cpConfigurationListId);

		return cpConfigurationListRel;
	}

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListRelLocalService
		_cpConfigurationListRelLocalService;

	@Inject
	private CPConfigurationListRelService _cpConfigurationListRelService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}