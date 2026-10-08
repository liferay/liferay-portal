/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.CPOptionValueService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringBundler;
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
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class CPOptionValueServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_cpOption = CPTestUtil.addCPOption(TestPropsValues.getGroupId(), false);

		_cpOptionValue = CPTestUtil.addCPOptionValue(_cpOption);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCPOptionValue() throws Exception {
		CPOption cpOption = CPTestUtil.addCPOption(
			TestPropsValues.getGroupId(), false);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CPOption.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(cpOption.getCPOptionId()), _role.getRoleId(),
			new String[] {ActionKeys.UPDATE});

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionValueService.addOrUpdateCPOptionValue(
				_cpOptionValue.getExternalReferenceCode(),
				cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomDouble(), _cpOptionValue.getKey(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionValueService.addOrUpdateCPOptionValue(
				_cpOptionValue.getExternalReferenceCode(),
				_cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomDouble(), _cpOptionValue.getKey(),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	@Test
	public void testUpdateCPOptionValue() throws Exception {
		_addResourcePermission(ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionValueService.updateCPOptionValue(
				_cpOptionValue.getCPOptionValueId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomDouble(), _cpOptionValue.getKey(),
				ServiceContextTestUtil.getServiceContext());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionValueService.updateCPOptionValue(
				_cpOptionValue.getCPOptionValueId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomDouble(), _cpOptionValue.getKey(),
				ServiceContextTestUtil.getServiceContext());
		}
	}

	private void _addResourcePermission(String actionId) throws Exception {
		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), CPOption.class.getName(),
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(TestPropsValues.getCompanyId()), _role.getRoleId(),
			actionId);
	}

	private void _assertMessage(String actionId, String message, long userId) {
		Assert.assertTrue(
			message.contains(
				StringBundler.concat(
					"User ", userId, " must have ", actionId,
					" permission for")));
	}

	private CPOption _cpOption;
	private CPOptionValue _cpOptionValue;

	@Inject
	private CPOptionValueService _cpOptionValueService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}