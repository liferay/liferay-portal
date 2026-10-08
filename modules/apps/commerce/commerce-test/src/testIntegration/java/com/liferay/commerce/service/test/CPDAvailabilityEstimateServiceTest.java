/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.model.CPDAvailabilityEstimate;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CPDAvailabilityEstimateLocalService;
import com.liferay.commerce.service.CPDAvailabilityEstimateService;
import com.liferay.commerce.service.CommerceAvailabilityEstimateLocalService;
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
public class CPDAvailabilityEstimateServiceTest {

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

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_addCommerceAvailabilityEstimate();

		_cpdAvailabilityEstimate =
			_cpdAvailabilityEstimateLocalService.
				updateCPDAvailabilityEstimateByCProductId(
					TestPropsValues.getUserId(), 0,
					_cpDefinition.getCProductId(),
					commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(_role.getRoleId(), _user);
	}

	@Test
	public void testUpdateCPDAvailabilityEstimate() throws Exception {
		_addResourcePermission(ActionKeys.VIEW);

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_addCommerceAvailabilityEstimate();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_cpdAvailabilityEstimateService.updateCPDAvailabilityEstimate(
				_cpdAvailabilityEstimate.getCPDAvailabilityEstimateId(),
				_cpDefinition.getCPDefinitionId(),
				commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId());

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

			CPDAvailabilityEstimate cpdAvailabilityEstimate =
				_cpdAvailabilityEstimateService.updateCPDAvailabilityEstimate(
					_cpdAvailabilityEstimate.getCPDAvailabilityEstimateId(),
					_cpDefinition.getCPDefinitionId(),
					commerceAvailabilityEstimate.
						getCommerceAvailabilityEstimateId());

			Assert.assertEquals(
				commerceAvailabilityEstimate.
					getCommerceAvailabilityEstimateId(),
				cpdAvailabilityEstimate.getCommerceAvailabilityEstimateId());
		}
	}

	private CommerceAvailabilityEstimate _addCommerceAvailabilityEstimate()
		throws Exception {

		return _commerceAvailabilityEstimateLocalService.
			addCommerceAvailabilityEstimate(
				RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.nextDouble(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
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
	private CommerceAvailabilityEstimateLocalService
		_commerceAvailabilityEstimateLocalService;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	private CPDefinition _cpDefinition;
	private CPDAvailabilityEstimate _cpdAvailabilityEstimate;

	@Inject
	private CPDAvailabilityEstimateLocalService
		_cpdAvailabilityEstimateLocalService;

	@Inject
	private CPDAvailabilityEstimateService _cpdAvailabilityEstimateService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}