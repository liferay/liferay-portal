/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.exception.CPDefinitionGroupIdException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
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

import java.math.BigDecimal;

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
public class CPInstanceServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			TestPropsValues.getCompanyId());

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), _commerceCurrency.getCode());

		_cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), CommerceCatalog.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(commerceCatalog.getCommerceCatalogId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), _user);
	}

	@Test
	public void testAddOrUpdateCPInstance() throws Exception {
		_testAddOrUpdateCPInstance();
		_testAddOrUpdateCPInstanceWithDifferentCatalog();
	}

	private void _testAddOrUpdateCPInstance() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			String sku = RandomTestUtil.randomString();

			CPInstance cpInstance = _cpInstanceService.addOrUpdateCPInstance(
				_cpInstance.getExternalReferenceCode(),
				_cpInstance.getCPDefinitionId(), _cpInstance.getGroupId(), sku,
				null, null, true, null, RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				RandomTestUtil.randomDouble(),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()),
				BigDecimal.valueOf(RandomTestUtil.randomDouble()), true, 0, 1,
				2026, 0, 0, 0, 0, 0, 0, 0, true, false, false,
				RandomTestUtil.randomInt(), null, null,
				RandomTestUtil.randomLong(), false, RandomTestUtil.randomInt(),
				null, null, RandomTestUtil.randomLong(), null, false, null, 0,
				0, 0, 0,
				ServiceContextTestUtil.getServiceContext(
					_cpInstance.getGroupId()));

			Assert.assertEquals(sku, cpInstance.getSku());
		}
	}

	private void _testAddOrUpdateCPInstanceWithDifferentCatalog()
		throws Exception {

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), _commerceCurrency.getCode());

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			commerceCatalog.getGroupId());

		String externalReferenceCode = RandomTestUtil.randomString();

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				CPDefinitionGroupIdException.class,
				() -> _cpInstanceService.addOrUpdateCPInstance(
					cpInstance1.getExternalReferenceCode(),
					_cpInstance.getCPDefinitionId(), _cpInstance.getGroupId(),
					RandomTestUtil.randomString(), null, null, true, null,
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()), true, 0,
					1, 2026, 0, 0, 0, 0, 0, 0, 0, true, false, false,
					RandomTestUtil.randomInt(), null, null,
					RandomTestUtil.randomLong(), false,
					RandomTestUtil.randomInt(), null, null,
					RandomTestUtil.randomLong(), null, false, null, 0, 0, 0, 0,
					ServiceContextTestUtil.getServiceContext(
						_cpInstance.getGroupId())));
			Assert.assertThrows(
				CPDefinitionGroupIdException.class,
				() -> _cpInstanceService.addOrUpdateCPInstance(
					externalReferenceCode, cpInstance1.getCPDefinitionId(),
					_cpInstance.getGroupId(), RandomTestUtil.randomString(),
					null, null, true, null, RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					RandomTestUtil.randomDouble(),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()),
					BigDecimal.valueOf(RandomTestUtil.randomDouble()), true, 0,
					1, 2026, 0, 0, 0, 0, 0, 0, 0, true, false, false,
					RandomTestUtil.randomInt(), null, null,
					RandomTestUtil.randomLong(), false,
					RandomTestUtil.randomInt(), null, null,
					RandomTestUtil.randomLong(), null, false, null, 0, 0, 0, 0,
					ServiceContextTestUtil.getServiceContext(
						_cpInstance.getGroupId())));
		}

		Assert.assertNull(
			_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
				externalReferenceCode, TestPropsValues.getCompanyId()));

		CPInstance cpInstance2 = _cpInstanceLocalService.getCPInstance(
			cpInstance1.getCPInstanceId());

		Assert.assertEquals(cpInstance1.getSku(), cpInstance2.getSku());
	}

	private CommerceCurrency _commerceCurrency;
	private CPInstance _cpInstance;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPInstanceService _cpInstanceService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}