/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionSpecificationOptionValue;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueLocalService;
import com.liferay.commerce.product.service.CPDefinitionSpecificationOptionValueService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
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
import com.liferay.portal.kernel.util.LocaleUtil;
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
public class CPDefinitionSpecificationOptionValueServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		CPDefinition cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CPSpecificationOption cpSpecificationOption =
			CPTestUtil.addCPSpecificationOption(
				commerceCatalog.getGroupId(), false);

		_cpDefinitionSpecificationOptionValue =
			_cpDefinitionSpecificationOptionValueLocalService.
				addCPDefinitionSpecificationOptionValue(
					null, cpDefinition.getCPDefinitionId(),
					cpSpecificationOption.getCPSpecificationOptionId(), 0,
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomLocaleStringMap(), true,
					ServiceContextTestUtil.getServiceContext(
						commerceCatalog.getGroupId()));

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testDeleteCPDefinitionSpecificationOptionValue()
		throws Exception {

		_addResourcePermission(ActionKeys.VIEW);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpDefinitionSpecificationOptionValueService.
				deleteCPDefinitionSpecificationOptionValue(
					_cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId());

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

			_cpDefinitionSpecificationOptionValueService.
				deleteCPDefinitionSpecificationOptionValue(
					_cpDefinitionSpecificationOptionValue.
						getCPDefinitionSpecificationOptionValueId());
		}
	}

	private void _addResourcePermission(String actionId) throws Exception {
		_resourcePermissionLocalService.addResourcePermission(
			TestPropsValues.getCompanyId(), CommerceCatalog.class.getName(),
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

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CPDefinitionSpecificationOptionValue
		_cpDefinitionSpecificationOptionValue;

	@Inject
	private CPDefinitionSpecificationOptionValueLocalService
		_cpDefinitionSpecificationOptionValueLocalService;

	@Inject
	private CPDefinitionSpecificationOptionValueService
		_cpDefinitionSpecificationOptionValueService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}