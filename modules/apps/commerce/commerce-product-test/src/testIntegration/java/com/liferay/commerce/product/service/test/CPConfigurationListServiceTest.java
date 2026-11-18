/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.RequiredCPConfigurationListException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPConfigurationListLocalService;
import com.liferay.commerce.product.service.CPConfigurationListService;
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
public class CPConfigurationListServiceTest {

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
	public void testDeleteCPConfigurationList() throws Exception {
		_testDeleteCPConfigurationListMasterCPConfigurationList();
		_testDeleteCPConfigurationListWithoutPermission();
	}

	private CPConfigurationList _addCPConfigurationList(long groupId)
		throws Exception {

		Calendar calendar = CalendarFactoryUtil.getCalendar();

		return _cpConfigurationListLocalService.addCPConfigurationList(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), groupId,
			0, false, RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), calendar.get(Calendar.MONTH),
			calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR),
			calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
			0, 0, 0, 0, 0, true,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private CPConfigurationList _createCPConfigurationList(
		long cpConfigurationListId, long groupId) {

		CPConfigurationList cpConfigurationList =
			_cpConfigurationListLocalService.createCPConfigurationList(
				cpConfigurationListId);

		cpConfigurationList.setGroupId(groupId);

		return cpConfigurationList;
	}

	private void _testDeleteCPConfigurationListMasterCPConfigurationList()
		throws Exception {

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog.getCommerceCatalogId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CPConfigurationList masterCPConfigurationList =
			_cpConfigurationListLocalService.getMasterCPConfigurationList(
				commerceCatalog.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				RequiredCPConfigurationListException.class,
				() -> _cpConfigurationListService.deleteCPConfigurationList(
					_createCPConfigurationList(
						masterCPConfigurationList.getCPConfigurationListId(),
						commerceCatalog.getGroupId())));

			Assert.assertNotNull(
				_cpConfigurationListLocalService.fetchCPConfigurationList(
					masterCPConfigurationList.getCPConfigurationListId()));
		}
	}

	private void _testDeleteCPConfigurationListWithoutPermission()
		throws Exception {

		CommerceCatalog commerceCatalog1 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		_resourcePermissionLocalService.setResourcePermissions(
			commerceCatalog1.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog1.getCommerceCatalogId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CPConfigurationList cpConfigurationList1 = _addCPConfigurationList(
			commerceCatalog1.getGroupId());

		CommerceCatalog commerceCatalog2 = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), RandomTestUtil.randomString());

		CPConfigurationList cpConfigurationList2 = _addCPConfigurationList(
			commerceCatalog2.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission principalException =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() -> _cpConfigurationListService.deleteCPConfigurationList(
						_createCPConfigurationList(
							cpConfigurationList2.getCPConfigurationListId(),
							commerceCatalog1.getGroupId())));

			Assert.assertEquals(
				commerceCatalog2.getCommerceCatalogId(),
				principalException.resourceId);

			Assert.assertNotNull(
				_cpConfigurationListLocalService.fetchCPConfigurationList(
					cpConfigurationList2.getCPConfigurationListId()));

			_cpConfigurationListService.deleteCPConfigurationList(
				cpConfigurationList1);

			Assert.assertNull(
				_cpConfigurationListLocalService.fetchCPConfigurationList(
					cpConfigurationList1.getCPConfigurationListId()));
		}
	}

	@Inject
	private CPConfigurationListLocalService _cpConfigurationListLocalService;

	@Inject
	private CPConfigurationListService _cpConfigurationListService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}