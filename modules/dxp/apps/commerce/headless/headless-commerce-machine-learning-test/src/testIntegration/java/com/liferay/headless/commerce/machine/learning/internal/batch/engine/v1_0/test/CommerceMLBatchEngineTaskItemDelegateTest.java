/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.internal.batch.engine.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry;
import com.liferay.batch.engine.BatchEngineTaskItemDelegateRegistry.BatchEngineTaskItemDelegateSafeCloseable;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.AccountCategoryForecast;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.AccountForecast;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.FrequentPatternRecommendation;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.ProductContentRecommendation;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.ProductInteractionRecommendation;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.SkuForecast;
import com.liferay.headless.commerce.machine.learning.dto.v1_0.UserRecommendation;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CommerceMLBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testCreate() throws Exception {
		AccountCategoryForecast accountCategoryForecast =
			new AccountCategoryForecast() {
				{
					setAccount((long)RandomTestUtil.randomInt());
					setCategory((long)RandomTestUtil.randomInt());
					setForecast(RandomTestUtil.randomFloat());
					setForecastLowerBound(RandomTestUtil.randomFloat());
					setForecastUpperBound(RandomTestUtil.randomFloat());
					setTimestamp(new Date());
				}
			};
		User user = UserTestUtil.addUser();

		_create(
			true, accountCategoryForecast,
			AccountCategoryForecast.class.getName(), null, user);

		AccountForecast accountForecast = new AccountForecast() {
			{
				setAccount((long)RandomTestUtil.randomInt());
				setForecast(RandomTestUtil.randomFloat());
				setForecastLowerBound(RandomTestUtil.randomFloat());
				setForecastUpperBound(RandomTestUtil.randomFloat());
				setTimestamp(new Date());
			}
		};

		_create(
			true, accountForecast, AccountForecast.class.getName(), null, user);

		FrequentPatternRecommendation frequentPatternRecommendation =
			new FrequentPatternRecommendation() {
				{
					setAntecedentIds(
						new Long[] {(long)RandomTestUtil.randomInt()});
					setAntecedentIdsLength(1L);
					setCreateDate(new Date());
					setJobId(RandomTestUtil.randomString());
					setRecommendedProductId((long)RandomTestUtil.randomInt());
					setScore(RandomTestUtil.randomFloat());
				}
			};

		_create(
			true, frequentPatternRecommendation,
			FrequentPatternRecommendation.class.getName(), null, user);

		ProductContentRecommendation productContentRecommendation =
			new ProductContentRecommendation() {
				{
					setCreateDate(new Date());
					setJobId(RandomTestUtil.randomString());
					setProductId((long)RandomTestUtil.randomInt());
					setRank(RandomTestUtil.randomInt());
					setRecommendedProductId((long)RandomTestUtil.randomInt());
					setScore(RandomTestUtil.randomFloat());
				}
			};

		_create(
			true, productContentRecommendation,
			ProductContentRecommendation.class.getName(), null, user);

		ProductInteractionRecommendation productInteractionRecommendation =
			new ProductInteractionRecommendation() {
				{
					setCreateDate(new Date());
					setJobId(RandomTestUtil.randomString());
					setProductId((long)RandomTestUtil.randomInt());
					setRank(RandomTestUtil.randomInt());
					setRecommendedProductId((long)RandomTestUtil.randomInt());
					setScore(RandomTestUtil.randomFloat());
				}
			};

		_create(
			true, productInteractionRecommendation,
			ProductInteractionRecommendation.class.getName(), null, user);

		SkuForecast skuForecast = new SkuForecast() {
			{
				setForecast(RandomTestUtil.randomFloat());
				setForecastLowerBound(RandomTestUtil.randomFloat());
				setForecastUpperBound(RandomTestUtil.randomFloat());
				setSku(RandomTestUtil.randomString());
				setTimestamp(new Date());
			}
		};

		_create(
			true, skuForecast, SkuForecast.class.getName(),
			"sku-monthly-quantity-forecast", user);

		UserRecommendation userRecommendation = new UserRecommendation() {
			{
				setAssetCategoryIds(
					new Long[] {(long)RandomTestUtil.randomInt()});
				setCreateDate(new Date());
				setJobId(RandomTestUtil.randomString());
				setProductId((long)RandomTestUtil.randomInt());
				setRank(RandomTestUtil.randomInt());
				setRecommendedProductId((long)RandomTestUtil.randomInt());
				setScore(RandomTestUtil.randomFloat());
			}
		};

		_create(
			true, userRecommendation, UserRecommendation.class.getName(), null,
			user);

		Role role = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.ANALYTICS_ADMINISTRATOR);

		_userLocalService.addRoleUser(role.getRoleId(), user.getUserId());

		_create(
			false, accountCategoryForecast,
			AccountCategoryForecast.class.getName(), null, user);
		_create(
			false, accountForecast, AccountForecast.class.getName(), null,
			user);
		_create(
			false, frequentPatternRecommendation,
			FrequentPatternRecommendation.class.getName(), null, user);
		_create(
			false, productContentRecommendation,
			ProductContentRecommendation.class.getName(), null, user);
		_create(
			false, productInteractionRecommendation,
			ProductInteractionRecommendation.class.getName(), null, user);
		_create(
			false, skuForecast, SkuForecast.class.getName(),
			"sku-monthly-quantity-forecast", user);
		_create(
			false, userRecommendation, UserRecommendation.class.getName(), null,
			user);
	}

	private void _create(
			boolean expectPrincipalException, Object item, String itemClassName,
			String taskItemDelegateName, User user)
		throws Exception {

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user);
			BatchEngineTaskItemDelegateSafeCloseable
				batchEngineTaskItemDelegateSafeCloseable =
					_batchEngineTaskItemDelegateRegistry.
						getBatchEngineTaskItemDelegateWithSafeCloseable(
							user.getCompanyId(), itemClassName,
							taskItemDelegateName)) {

			BatchEngineTaskItemDelegate<Object> batchEngineTaskItemDelegate =
				(BatchEngineTaskItemDelegate<Object>)
					batchEngineTaskItemDelegateSafeCloseable.
						getBatchEngineTaskItemDelegate();

			batchEngineTaskItemDelegate.setContextCompany(
				_companyLocalService.getCompany(user.getCompanyId()));
			batchEngineTaskItemDelegate.setContextUser(user);
			batchEngineTaskItemDelegate.setImportItemUnsafeBiConsumer(
				(currentItem, unsafeFunction) -> unsafeFunction.apply(
					currentItem));

			try {
				batchEngineTaskItemDelegate.create(
					Collections.singletonList(item), Collections.emptyMap());

				Assert.assertFalse(expectPrincipalException);
			}
			catch (PrincipalException.MustHavePermission principalException) {
				Assert.assertTrue(expectPrincipalException);
			}
		}
	}

	@Inject
	private BatchEngineTaskItemDelegateRegistry
		_batchEngineTaskItemDelegateRegistry;

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}