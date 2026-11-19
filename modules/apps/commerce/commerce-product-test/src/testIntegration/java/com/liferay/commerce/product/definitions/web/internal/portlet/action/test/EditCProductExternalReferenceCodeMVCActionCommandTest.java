/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.definitions.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
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
public class EditCProductExternalReferenceCodeMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_cpDefinition = _addCPDefinition();
	}

	@Test
	public void testProcessAction() throws Exception {
		_testProcessActionWithUpdatePermission();
		_testProcessActionWithUpdatePermissionOnAnotherCatalog();
		_testProcessActionWithViewPermission();
	}

	private CPDefinition _addCPDefinition() throws Exception {
		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.addCommerceCatalog(
				null, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LocaleUtil.US.getDisplayLanguage(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));

		return CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);
	}

	private User _addUser(int scope, String primKey, String... actionIds)
		throws Exception {

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommerceCatalog.class.getName(),
			scope, primKey, role.getRoleId(), actionIds);

		User user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), user);

		return user;
	}

	private MockLiferayPortletActionRequest _processAction(
			long cProductId, String externalReferenceCode)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setParameter(
			"cProductId", String.valueOf(cProductId));
		mockLiferayPortletActionRequest.setParameter(
			"externalReferenceCode", externalReferenceCode);
		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		_mvcActionCommand.processAction(
			mockLiferayPortletActionRequest,
			new MockLiferayPortletActionResponse());

		return mockLiferayPortletActionRequest;
	}

	private void _testProcessActionWithUpdatePermission() throws Exception {
		String externalReferenceCode = RandomTestUtil.randomString();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUser(
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.UPDATE, ActionKeys.VIEW))) {

			Assert.assertTrue(
				SessionErrors.isEmpty(
					_processAction(
						_cpDefinition.getCProductId(), externalReferenceCode)));
		}

		CProduct cProduct = _cpDefinition.getCProduct();

		Assert.assertEquals(
			externalReferenceCode, cProduct.getExternalReferenceCode());
	}

	private void _testProcessActionWithUpdatePermissionOnAnotherCatalog()
		throws Exception {

		CProduct cProduct = _cpDefinition.getCProduct();

		String externalReferenceCode = cProduct.getExternalReferenceCode();

		CPDefinition cpDefinition = _addCPDefinition();

		CommerceCatalog commerceCatalog = cpDefinition.getCommerceCatalog();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUser(
					ResourceConstants.SCOPE_INDIVIDUAL,
					String.valueOf(commerceCatalog.getCommerceCatalogId()),
					ActionKeys.UPDATE, ActionKeys.VIEW))) {

			Assert.assertTrue(
				SessionErrors.contains(
					_processAction(
						cProduct.getCProductId(),
						RandomTestUtil.randomString()),
					PrincipalException.MustHavePermission.class));
		}

		cProduct = _cpDefinition.getCProduct();

		Assert.assertEquals(
			externalReferenceCode, cProduct.getExternalReferenceCode());
	}

	private void _testProcessActionWithViewPermission() throws Exception {
		CProduct cProduct = _cpDefinition.getCProduct();

		String externalReferenceCode = cProduct.getExternalReferenceCode();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_addUser(
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW))) {

			Assert.assertTrue(
				SessionErrors.contains(
					_processAction(
						cProduct.getCProductId(),
						RandomTestUtil.randomString()),
					PrincipalException.MustHavePermission.class));
		}

		cProduct = _cpDefinition.getCProduct();

		Assert.assertEquals(
			externalReferenceCode, cProduct.getExternalReferenceCode());
	}

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CPDefinition _cpDefinition;

	@Inject(
		filter = "mvc.command.name=/cp_definitions/edit_c_product_external_reference_code",
		type = MVCActionCommand.class
	)
	private MVCActionCommand _mvcActionCommand;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private UserLocalService _userLocalService;

}