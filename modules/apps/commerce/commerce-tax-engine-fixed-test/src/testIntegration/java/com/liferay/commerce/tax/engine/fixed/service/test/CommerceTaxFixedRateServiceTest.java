/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPTaxCategoryLocalService;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateLocalService;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateService;
import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceTaxFixedRateServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Group group = GroupTestUtil.addGroup();

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(group.getCompanyId());

		_commerceChannel1 = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		_commerceTaxMethod1 = CommerceTaxTestUtil.addFixedTaxCommerceTaxMethod(
			TestPropsValues.getUserId(), _commerceChannel1.getGroupId(), true);

		_commerceChannel2 = CommerceTestUtil.addCommerceChannel(
			group.getGroupId(), commerceCurrency.getCode());

		_commerceTaxMethod2 = CommerceTaxTestUtil.addFixedTaxCommerceTaxMethod(
			TestPropsValues.getUserId(), _commerceChannel2.getGroupId(), true);

		_cpTaxCategoryId = CommerceTaxTestUtil.addTaxCategoryId(
			group.getGroupId());

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			role.getCompanyId(), CommerceChannel.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_commerceChannel1.getCommerceChannelId()),
			role.getRoleId(), new String[] {ActionKeys.UPDATE});

		_user = UserTestUtil.addUser();

		_userLocalService.addRoleUser(role.getRoleId(), _user.getUserId());
	}

	@Test
	public void testAddCommerceTaxFixedRate() throws Exception {
		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.createCPTaxCategory(
				RandomTestUtil.nextLong());

		cpTaxCategory.setCompanyId(CompanyConstants.SYSTEM);

		_cpTaxCategoryLocalService.addCPTaxCategory(cpTaxCategory);

		Assert.assertThrows(
			NoSuchCPTaxCategoryException.class,
			() -> _commerceTaxFixedRateService.addCommerceTaxFixedRate(
				_commerceChannel1.getGroupId(),
				_commerceTaxMethod1.getCommerceTaxMethodId(),
				cpTaxCategory.getCPTaxCategoryId(),
				RandomTestUtil.randomDouble()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(
				_commerceTaxFixedRateService.addCommerceTaxFixedRate(
					_commerceChannel1.getGroupId(),
					_commerceTaxMethod1.getCommerceTaxMethodId(),
					_cpTaxCategoryId, RandomTestUtil.randomDouble()));

			CommerceTaxFixedRate commerceTaxFixedRate =
				_commerceTaxFixedRateService.addCommerceTaxFixedRate(
					0, _commerceTaxMethod1.getCommerceTaxMethodId(),
					CommerceTaxTestUtil.addTaxCategoryId(
						_commerceChannel1.getGroupId()),
					RandomTestUtil.randomDouble());

			Assert.assertEquals(
				_commerceChannel1.getGroupId(),
				commerceTaxFixedRate.getGroupId());

			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() -> _commerceTaxFixedRateService.addCommerceTaxFixedRate(
					_commerceChannel1.getGroupId(),
					_commerceTaxMethod2.getCommerceTaxMethodId(),
					_cpTaxCategoryId, RandomTestUtil.randomDouble()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.addCommerceTaxFixedRate(
					_commerceChannel2.getGroupId(),
					_commerceTaxMethod2.getCommerceTaxMethodId(),
					_cpTaxCategoryId, RandomTestUtil.randomDouble()));
		}

		Assert.assertNull(
			_commerceTaxFixedRateLocalService.fetchCommerceTaxFixedRate(
				_cpTaxCategoryId,
				_commerceTaxMethod2.getCommerceTaxMethodId()));
	}

	@Test
	public void testDeleteCommerceTaxFixedRate() throws Exception {
		CommerceTaxFixedRate commerceTaxFixedRate1 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRate commerceTaxFixedRate2 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceTaxFixedRateService.deleteCommerceTaxFixedRate(
				commerceTaxFixedRate1.getCommerceTaxFixedRateId());

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.deleteCommerceTaxFixedRate(
					commerceTaxFixedRate2.getCommerceTaxFixedRateId()));
		}

		Assert.assertNull(
			_commerceTaxFixedRateLocalService.fetchCommerceTaxFixedRate(
				commerceTaxFixedRate1.getCommerceTaxFixedRateId()));
		Assert.assertNotNull(
			_commerceTaxFixedRateLocalService.fetchCommerceTaxFixedRate(
				commerceTaxFixedRate2.getCommerceTaxFixedRateId()));
	}

	@Test
	public void testFetchCommerceTaxFixedRate() throws Exception {
		CommerceTaxFixedRate commerceTaxFixedRate1 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRate commerceTaxFixedRate2 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				commerceTaxFixedRate1,
				_commerceTaxFixedRateService.fetchCommerceTaxFixedRate(
					commerceTaxFixedRate1.getCommerceTaxFixedRateId()));
			Assert.assertEquals(
				commerceTaxFixedRate1,
				_commerceTaxFixedRateService.fetchCommerceTaxFixedRate(
					_cpTaxCategoryId,
					_commerceTaxMethod1.getCommerceTaxMethodId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.fetchCommerceTaxFixedRate(
					commerceTaxFixedRate2.getCommerceTaxFixedRateId()));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.fetchCommerceTaxFixedRate(
					_cpTaxCategoryId,
					_commerceTaxMethod2.getCommerceTaxMethodId()));
		}
	}

	@Test
	public void testGetCommerceTaxFixedRates() throws Exception {
		CommerceTaxFixedRate commerceTaxFixedRate = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod1);

		_addCommerceTaxFixedRate(_commerceChannel2, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				Collections.singletonList(commerceTaxFixedRate),
				_commerceTaxFixedRateService.getCommerceTaxFixedRates(
					_commerceChannel1.getGroupId(),
					_commerceTaxMethod1.getCommerceTaxMethodId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() -> _commerceTaxFixedRateService.getCommerceTaxFixedRates(
					_commerceChannel1.getGroupId(),
					_commerceTaxMethod2.getCommerceTaxMethodId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.getCommerceTaxFixedRates(
					_commerceChannel2.getGroupId(),
					_commerceTaxMethod2.getCommerceTaxMethodId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
		}
	}

	@Test
	public void testGetCommerceTaxFixedRatesCount() throws Exception {
		_addCommerceTaxFixedRate(_commerceChannel1, _commerceTaxMethod1);
		_addCommerceTaxFixedRate(_commerceChannel2, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				1,
				_commerceTaxFixedRateService.getCommerceTaxFixedRatesCount(
					_commerceChannel1.getGroupId(),
					_commerceTaxMethod1.getCommerceTaxMethodId()));
			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() ->
					_commerceTaxFixedRateService.getCommerceTaxFixedRatesCount(
						_commerceChannel1.getGroupId(),
						_commerceTaxMethod2.getCommerceTaxMethodId()));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateService.getCommerceTaxFixedRatesCount(
						_commerceChannel2.getGroupId(),
						_commerceTaxMethod2.getCommerceTaxMethodId()));
		}
	}

	@Test
	public void testUpdateCommerceTaxFixedRate() throws Exception {
		CommerceTaxFixedRate commerceTaxFixedRate1 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRate commerceTaxFixedRate2 = _addCommerceTaxFixedRate(
			_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			double rate = RandomTestUtil.randomDouble();

			commerceTaxFixedRate1 =
				_commerceTaxFixedRateService.updateCommerceTaxFixedRate(
					commerceTaxFixedRate1.getCommerceTaxFixedRateId(), rate);

			Assert.assertEquals(rate, commerceTaxFixedRate1.getRate(), 0);

			Assert.assertThrows(
				PrincipalException.class,
				() -> _commerceTaxFixedRateService.updateCommerceTaxFixedRate(
					commerceTaxFixedRate2.getCommerceTaxFixedRateId(),
					RandomTestUtil.randomDouble()));
		}
	}

	private CommerceTaxFixedRate _addCommerceTaxFixedRate(
			CommerceChannel commerceChannel,
			CommerceTaxMethod commerceTaxMethod)
		throws Exception {

		return _commerceTaxFixedRateLocalService.addCommerceTaxFixedRate(
			TestPropsValues.getUserId(), commerceChannel.getGroupId(),
			commerceTaxMethod.getCommerceTaxMethodId(), _cpTaxCategoryId,
			RandomTestUtil.randomDouble());
	}

	private CommerceChannel _commerceChannel1;
	private CommerceChannel _commerceChannel2;

	@Inject
	private CommerceTaxFixedRateLocalService _commerceTaxFixedRateLocalService;

	@Inject
	private CommerceTaxFixedRateService _commerceTaxFixedRateService;

	private CommerceTaxMethod _commerceTaxMethod1;
	private CommerceTaxMethod _commerceTaxMethod2;
	private long _cpTaxCategoryId;

	@Inject
	private CPTaxCategoryLocalService _cpTaxCategoryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}