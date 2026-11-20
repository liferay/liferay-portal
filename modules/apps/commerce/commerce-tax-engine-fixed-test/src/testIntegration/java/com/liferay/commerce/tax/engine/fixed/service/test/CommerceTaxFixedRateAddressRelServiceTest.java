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
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelLocalService;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateAddressRelService;
import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.test.util.CommerceTaxTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.petra.string.StringPool;
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
public class CommerceTaxFixedRateAddressRelServiceTest {

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
	public void testAddCommerceTaxFixedRateAddressRel() throws Exception {
		CPTaxCategory cpTaxCategory =
			_cpTaxCategoryLocalService.createCPTaxCategory(
				RandomTestUtil.nextLong());

		cpTaxCategory.setCompanyId(CompanyConstants.SYSTEM);

		_cpTaxCategoryLocalService.addCPTaxCategory(cpTaxCategory);

		Assert.assertThrows(
			NoSuchCPTaxCategoryException.class,
			() ->
				_commerceTaxFixedRateAddressRelService.
					addCommerceTaxFixedRateAddressRel(
						_commerceChannel1.getGroupId(),
						_commerceTaxMethod1.getCommerceTaxMethodId(),
						cpTaxCategory.getCPTaxCategoryId(), 0, 0,
						StringPool.BLANK, RandomTestUtil.randomDouble()));

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertNotNull(
				_commerceTaxFixedRateAddressRelService.
					addCommerceTaxFixedRateAddressRel(
						_commerceChannel1.getGroupId(),
						_commerceTaxMethod1.getCommerceTaxMethodId(),
						_cpTaxCategoryId, 0, 0, StringPool.BLANK,
						RandomTestUtil.randomDouble()));

			CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
				_commerceTaxFixedRateAddressRelService.
					addCommerceTaxFixedRateAddressRel(
						0, _commerceTaxMethod1.getCommerceTaxMethodId(),
						_cpTaxCategoryId, 0, 0, StringPool.BLANK,
						RandomTestUtil.randomDouble());

			Assert.assertEquals(
				_commerceChannel1.getGroupId(),
				commerceTaxFixedRateAddressRel.getGroupId());

			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						addCommerceTaxFixedRateAddressRel(
							_commerceChannel1.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId(),
							_cpTaxCategoryId, 0, 0, StringPool.BLANK,
							RandomTestUtil.randomDouble()));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						addCommerceTaxFixedRateAddressRel(
							_commerceChannel2.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId(),
							_cpTaxCategoryId, 0, 0, StringPool.BLANK,
							RandomTestUtil.randomDouble()));
		}

		Assert.assertNull(
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					_commerceTaxMethod2.getCommerceTaxMethodId(),
					_cpTaxCategoryId, 0, 0, StringPool.BLANK));
	}

	@Test
	public void testDeleteCommerceTaxFixedRateAddressRel() throws Exception {
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel1 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel2 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			_commerceTaxFixedRateAddressRelService.
				deleteCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRel1.
						getCommerceTaxFixedRateAddressRelId());

			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						deleteCommerceTaxFixedRateAddressRel(
							commerceTaxFixedRateAddressRel2.
								getCommerceTaxFixedRateAddressRelId()));
		}

		Assert.assertNull(
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRel1.
						getCommerceTaxFixedRateAddressRelId()));
		Assert.assertNotNull(
			_commerceTaxFixedRateAddressRelLocalService.
				fetchCommerceTaxFixedRateAddressRel(
					commerceTaxFixedRateAddressRel2.
						getCommerceTaxFixedRateAddressRelId()));
	}

	@Test
	public void testFetchCommerceTaxFixedRateAddressRel() throws Exception {
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel1 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel2 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				commerceTaxFixedRateAddressRel1,
				_commerceTaxFixedRateAddressRelService.
					fetchCommerceTaxFixedRateAddressRel(
						commerceTaxFixedRateAddressRel1.
							getCommerceTaxFixedRateAddressRelId()));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						fetchCommerceTaxFixedRateAddressRel(
							commerceTaxFixedRateAddressRel2.
								getCommerceTaxFixedRateAddressRelId()));
		}
	}

	@Test
	public void testGetCommerceTaxMethodFixedRateAddressRels()
		throws Exception {

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod1);

		_addCommerceTaxFixedRateAddressRel(
			_commerceChannel2, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				Collections.singletonList(commerceTaxFixedRateAddressRel),
				_commerceTaxFixedRateAddressRelService.
					getCommerceTaxMethodFixedRateAddressRels(
						_commerceChannel1.getGroupId(),
						_commerceTaxMethod1.getCommerceTaxMethodId(),
						QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						getCommerceTaxMethodFixedRateAddressRels(
							_commerceChannel1.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						getCommerceTaxMethodFixedRateAddressRels(
							_commerceChannel2.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId(),
							QueryUtil.ALL_POS, QueryUtil.ALL_POS, null));
		}
	}

	@Test
	public void testGetCommerceTaxMethodFixedRateAddressRelsCount()
		throws Exception {

		_addCommerceTaxFixedRateAddressRel(
			_commerceChannel1, _commerceTaxMethod1);
		_addCommerceTaxFixedRateAddressRel(
			_commerceChannel2, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			Assert.assertEquals(
				1,
				_commerceTaxFixedRateAddressRelService.
					getCommerceTaxMethodFixedRateAddressRelsCount(
						_commerceChannel1.getGroupId(),
						_commerceTaxMethod1.getCommerceTaxMethodId()));
			Assert.assertThrows(
				NoSuchTaxMethodException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						getCommerceTaxMethodFixedRateAddressRelsCount(
							_commerceChannel1.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId()));
			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						getCommerceTaxMethodFixedRateAddressRelsCount(
							_commerceChannel2.getGroupId(),
							_commerceTaxMethod2.getCommerceTaxMethodId()));
		}
	}

	@Test
	public void testUpdateCommerceTaxFixedRateAddressRel() throws Exception {
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel1 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod1);
		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel2 =
			_addCommerceTaxFixedRateAddressRel(
				_commerceChannel1, _commerceTaxMethod2);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user)) {

			double rate = RandomTestUtil.randomDouble();

			commerceTaxFixedRateAddressRel1 =
				_commerceTaxFixedRateAddressRelService.
					updateCommerceTaxFixedRateAddressRel(
						commerceTaxFixedRateAddressRel1.
							getCommerceTaxFixedRateAddressRelId(),
						0, 0, StringPool.BLANK, rate);

			Assert.assertEquals(
				rate, commerceTaxFixedRateAddressRel1.getRate(), 0);

			Assert.assertThrows(
				PrincipalException.class,
				() ->
					_commerceTaxFixedRateAddressRelService.
						updateCommerceTaxFixedRateAddressRel(
							commerceTaxFixedRateAddressRel2.
								getCommerceTaxFixedRateAddressRelId(),
							0, 0, StringPool.BLANK,
							RandomTestUtil.randomDouble()));
		}
	}

	private CommerceTaxFixedRateAddressRel _addCommerceTaxFixedRateAddressRel(
			CommerceChannel commerceChannel,
			CommerceTaxMethod commerceTaxMethod)
		throws Exception {

		return _commerceTaxFixedRateAddressRelLocalService.
			addCommerceTaxFixedRateAddressRel(
				TestPropsValues.getUserId(), commerceChannel.getGroupId(),
				commerceTaxMethod.getCommerceTaxMethodId(), _cpTaxCategoryId, 0,
				0, StringPool.BLANK, RandomTestUtil.randomDouble());
	}

	private CommerceChannel _commerceChannel1;
	private CommerceChannel _commerceChannel2;

	@Inject
	private CommerceTaxFixedRateAddressRelLocalService
		_commerceTaxFixedRateAddressRelLocalService;

	@Inject
	private CommerceTaxFixedRateAddressRelService
		_commerceTaxFixedRateAddressRelService;

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