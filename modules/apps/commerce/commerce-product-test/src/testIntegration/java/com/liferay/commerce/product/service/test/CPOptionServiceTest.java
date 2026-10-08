/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.constants.CPActionKeys;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPOptionService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
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
public class CPOptionServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_cpOption = CPTestUtil.addCPOption(TestPropsValues.getGroupId(), false);

		CPTestUtil.addCPOptionValue(_cpOption);

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCPOption() throws Exception {
		_addResourcePermission(
			CPConstants.RESOURCE_NAME_PRODUCT,
			CPActionKeys.ADD_COMMERCE_PRODUCT_OPTION);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_addOrUpdateCPOption();

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(CPOption.class.getName(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_addOrUpdateCPOption();
		}
	}

	@Test
	public void testDeleteCPOption() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionService.deleteCPOption(_cpOption.getCPOptionId());

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.DELETE, principalException.getMessage(),
				_user.getUserId());
		}

		Assert.assertNotNull(
			_cpOptionLocalService.fetchCPOption(_cpOption.getCPOptionId()));
		Assert.assertEquals(
			1,
			_cpOptionValueLocalService.getCPOptionValuesCount(
				_cpOption.getCPOptionId()));

		_addResourcePermission(CPOption.class.getName(), ActionKeys.DELETE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpOptionService.deleteCPOption(_cpOption.getCPOptionId());
		}

		Assert.assertNull(
			_cpOptionLocalService.fetchCPOption(_cpOption.getCPOptionId()));
		Assert.assertEquals(
			0,
			_cpOptionValueLocalService.getCPOptionValuesCount(
				_cpOption.getCPOptionId()));
	}

	@Test
	public void testUpdateCPOption() throws Exception {
		_addResourcePermission(CPOption.class.getName(), ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_updateCPOption();

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			_assertMessage(
				ActionKeys.UPDATE, principalException.getMessage(),
				_user.getUserId());
		}

		_addResourcePermission(CPOption.class.getName(), ActionKeys.UPDATE);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_updateCPOption();
		}
	}

	private void _addOrUpdateCPOption() throws Exception {
		_cpOptionService.addOrUpdateCPOption(
			_cpOption.getExternalReferenceCode(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			_cpOption.getCommerceOptionTypeKey(), _cpOption.isFacetable(),
			_cpOption.isRequired(), _cpOption.isSkuContributor(),
			_cpOption.getKey(), ServiceContextTestUtil.getServiceContext());
	}

	private void _addResourcePermission(String name, String actionId)
		throws Exception {

		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), name,
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

	private void _updateCPOption() throws Exception {
		_cpOptionService.updateCPOption(
			_cpOption.getCPOptionId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			_cpOption.getCommerceOptionTypeKey(), _cpOption.isFacetable(),
			_cpOption.isRequired(), _cpOption.isSkuContributor(),
			_cpOption.getKey(), ServiceContextTestUtil.getServiceContext());
	}

	private CPOption _cpOption;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@Inject
	private CPOptionService _cpOptionService;

	@Inject
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}