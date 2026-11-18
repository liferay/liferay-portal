/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalService;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelService;
import com.liferay.commerce.pricing.service.CommercePricingClassLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.test.util.CPTestUtil;
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
import com.liferay.portal.kernel.util.ArrayUtil;
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
public class CommercePricingClassCPDefinitionRelServiceTest {

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
	public void testAddCommercePricingClassCPDefinitionRel() throws Exception {
		CommercePricingClass commercePricingClass = _addCommercePricingClass();

		_resourcePermissionLocalService.setResourcePermissions(
			commercePricingClass.getCompanyId(),
			CommercePricingClass.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commercePricingClass.getCommercePricingClassId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CPInstance cpInstance = CPTestUtil.addCPInstance(
			TestPropsValues.getGroupId());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission principalException =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() ->
						_commercePricingClassCPDefinitionRelService.
							addCommercePricingClassCPDefinitionRel(
								commercePricingClass.
									getCommercePricingClassId(),
								cpDefinition.getCPDefinitionId(),
								ServiceContextTestUtil.getServiceContext(
									TestPropsValues.getGroupId(),
									_user.getUserId())));

			CommerceCatalog commerceCatalog = cpDefinition.getCommerceCatalog();

			Assert.assertEquals(
				commerceCatalog.getCommerceCatalogId(),
				principalException.resourceId);

			Assert.assertFalse(
				ArrayUtil.contains(
					_commercePricingClassCPDefinitionRelLocalService.
						getCPDefinitionIds(
							commercePricingClass.getCommercePricingClassId()),
					cpDefinition.getCPDefinitionId()));

			_resourcePermissionLocalService.setResourcePermissions(
				commerceCatalog.getCompanyId(), CommerceCatalog.class.getName(),
				ResourceConstants.SCOPE_INDIVIDUAL,
				String.valueOf(commerceCatalog.getCommerceCatalogId()),
				_role.getRoleId(), new String[] {ActionKeys.VIEW});

			_commercePricingClassCPDefinitionRelService.
				addCommercePricingClassCPDefinitionRel(
					commercePricingClass.getCommercePricingClassId(),
					cpDefinition.getCPDefinitionId(),
					ServiceContextTestUtil.getServiceContext(
						TestPropsValues.getGroupId(), _user.getUserId()));

			Assert.assertTrue(
				ArrayUtil.contains(
					_commercePricingClassCPDefinitionRelLocalService.
						getCPDefinitionIds(
							commercePricingClass.getCommercePricingClassId()),
					cpDefinition.getCPDefinitionId()));
		}
	}

	@Test
	public void testDeleteCommercePricingClassCPDefinitionRel()
		throws Exception {

		CommercePricingClass commercePricingClass1 = _addCommercePricingClass();

		_resourcePermissionLocalService.setResourcePermissions(
			commercePricingClass1.getCompanyId(),
			CommercePricingClass.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commercePricingClass1.getCommercePricingClassId()),
			_role.getRoleId(), new String[] {ActionKeys.UPDATE});

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel1 =
				_addCommercePricingClassCPDefinitionRel(
					commercePricingClass1.getCommercePricingClassId());

		CommercePricingClass commercePricingClass2 = _addCommercePricingClass();

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel2 =
				_addCommercePricingClassCPDefinitionRel(
					commercePricingClass2.getCommercePricingClassId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			PrincipalException.MustHavePermission principalException =
				Assert.assertThrows(
					PrincipalException.MustHavePermission.class,
					() ->
						_commercePricingClassCPDefinitionRelService.
							deleteCommercePricingClassCPDefinitionRel(
								_createCommercePricingClassCPDefinitionRel(
									commercePricingClassCPDefinitionRel2.
										getCommercePricingClassCPDefinitionRelId(),
									commercePricingClassCPDefinitionRel1.
										getCommercePricingClassId())));

			Assert.assertEquals(
				commercePricingClass2.getCommercePricingClassId(),
				principalException.resourceId);

			Assert.assertNotNull(
				_commercePricingClassCPDefinitionRelLocalService.
					fetchCommercePricingClassCPDefinitionRel(
						commercePricingClassCPDefinitionRel2.
							getCommercePricingClassCPDefinitionRelId()));

			_commercePricingClassCPDefinitionRelService.
				deleteCommercePricingClassCPDefinitionRel(
					commercePricingClassCPDefinitionRel1);

			Assert.assertNull(
				_commercePricingClassCPDefinitionRelLocalService.
					fetchCommercePricingClassCPDefinitionRel(
						commercePricingClassCPDefinitionRel1.
							getCommercePricingClassCPDefinitionRelId()));
		}
	}

	private CommercePricingClass _addCommercePricingClass() throws Exception {
		return _commercePricingClassLocalService.addCommercePricingClass(
			TestPropsValues.getUserId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private CommercePricingClassCPDefinitionRel
			_addCommercePricingClassCPDefinitionRel(long commercePricingClassId)
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstance(
			TestPropsValues.getGroupId());

		return _commercePricingClassCPDefinitionRelLocalService.
			addCommercePricingClassCPDefinitionRel(
				commercePricingClassId, cpInstance.getCPDefinitionId(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getGroupId()));
	}

	private CommercePricingClassCPDefinitionRel
		_createCommercePricingClassCPDefinitionRel(
			long commercePricingClassCPDefinitionRelId,
			long commercePricingClassId) {

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel =
				_commercePricingClassCPDefinitionRelLocalService.
					createCommercePricingClassCPDefinitionRel(
						commercePricingClassCPDefinitionRelId);

		commercePricingClassCPDefinitionRel.setCommercePricingClassId(
			commercePricingClassId);

		return commercePricingClassCPDefinitionRel;
	}

	@Inject
	private CommercePricingClassCPDefinitionRelLocalService
		_commercePricingClassCPDefinitionRelLocalService;

	@Inject
	private CommercePricingClassCPDefinitionRelService
		_commercePricingClassCPDefinitionRelService;

	@Inject
	private CommercePricingClassLocalService _commercePricingClassLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private Role _role;
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}