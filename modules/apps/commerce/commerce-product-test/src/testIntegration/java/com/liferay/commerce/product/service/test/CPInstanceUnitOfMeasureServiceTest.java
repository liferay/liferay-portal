/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureService;
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
public class CPInstanceUnitOfMeasureServiceTest {

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

		_cpInstanceUnitOfMeasure = _addCPInstanceUnitOfMeasure(
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
	public void testUpdateCPInstanceUnitOfMeasure() throws Exception {
		_testUpdateCPInstanceUnitOfMeasure();
		_testUpdateCPInstanceUnitOfMeasureWithDifferentCatalog();
	}

	private CPInstanceUnitOfMeasure _addCPInstanceUnitOfMeasure(long groupId)
		throws Exception {

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(groupId);

		return CPTestUtil.addCPInstanceUnitOfMeasure(
			groupId, cpInstance.getCPInstanceId(),
			RandomTestUtil.randomString(), BigDecimal.ONE, cpInstance.getSku());
	}

	private void _testUpdateCPInstanceUnitOfMeasure() throws Exception {
		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			String key = RandomTestUtil.randomString();

			CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
				_cpInstanceUnitOfMeasureService.updateCPInstanceUnitOfMeasure(
					_cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
					_cpInstanceUnitOfMeasure.getCPInstanceId(), true,
					BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(),
					0, BigDecimal.ONE, true, 0.0, BigDecimal.ONE,
					_cpInstanceUnitOfMeasure.getSku());

			Assert.assertEquals(key, cpInstanceUnitOfMeasure.getKey());
		}
	}

	private void _testUpdateCPInstanceUnitOfMeasureWithDifferentCatalog()
		throws Exception {

		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
			TestPropsValues.getUserId(), _commerceCurrency.getCode());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			_addCPInstanceUnitOfMeasure(commerceCatalog.getGroupId());

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertThrows(
				NoSuchCPInstanceUnitOfMeasureException.class,
				() ->
					_cpInstanceUnitOfMeasureService.
						updateCPInstanceUnitOfMeasure(
							cpInstanceUnitOfMeasure1.
								getCPInstanceUnitOfMeasureId(),
							_cpInstanceUnitOfMeasure.getCPInstanceId(), true,
							BigDecimal.ONE, RandomTestUtil.randomString(),
							RandomTestUtil.randomLocaleStringMap(), 0,
							BigDecimal.ONE, true, 0.0, BigDecimal.ONE,
							_cpInstanceUnitOfMeasure.getSku()));
		}

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			_cpInstanceUnitOfMeasureLocalService.getCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure1.getCPInstanceUnitOfMeasureId());

		Assert.assertEquals(
			cpInstanceUnitOfMeasure1.getKey(),
			cpInstanceUnitOfMeasure2.getKey());
	}

	private CommerceCurrency _commerceCurrency;
	private CPInstanceUnitOfMeasure _cpInstanceUnitOfMeasure;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@Inject
	private CPInstanceUnitOfMeasureService _cpInstanceUnitOfMeasureService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}