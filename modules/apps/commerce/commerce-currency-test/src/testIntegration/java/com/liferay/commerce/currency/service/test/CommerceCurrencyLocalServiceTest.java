/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.exception.CommerceCurrencyRateException;
import com.liferay.commerce.currency.exception.DuplicateCommerceCurrencyException;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.TestExchangeRateProvider;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceCurrencyLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_user = UserTestUtil.addUser();
	}

	@Test
	public void testAddCommerceCurrency() throws Exception {
		AssertUtils.assertFailure(
			DuplicateCommerceCurrencyException.class, null,
			() -> {
				CommerceCurrency commerceCurrency =
					_commerceCurrencyLocalService.addCommerceCurrency(
						null, _user.getUserId(), RandomTestUtil.randomString(3),
						RandomTestUtil.randomLocaleStringMap(),
						RandomTestUtil.randomString(3), BigDecimal.ONE,
						LocalizationUtil.getLocalizationMap(
							CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
						2, 2, "HALF_EVEN", false, 0.0, false);

				_commerceCurrencyLocalService.addCommerceCurrency(
					null, _user.getUserId(), commerceCurrency.getCode(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomString(3), BigDecimal.ONE,
					LocalizationUtil.getLocalizationMap(
						CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
					2, 2, "HALF_EVEN", false, 0.0, false);
			});

		AssertUtils.assertFailure(
			CommerceCurrencyRateException.class, null,
			() -> _commerceCurrencyLocalService.addCommerceCurrency(
				null, _user.getUserId(), RandomTestUtil.randomString(3),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(3), BigDecimal.ZERO,
				LocalizationUtil.getLocalizationMap(
					CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
				2, 2, "HALF_EVEN", false, 0.0, false));
	}

	@Test
	public void testGetOrAddEmptyCommerceCurrency() throws Exception {
		String code = RandomTestUtil.randomString(3);
		String externalReferenceCode = RandomTestUtil.randomString();

		try {
			_commerceCurrencyLocalService.getOrAddEmptyCommerceCurrency(
				externalReferenceCode, _user.getCompanyId(), _user.getUserId(),
				code);

			Assert.fail();
		}
		catch (NoSuchCurrencyException noSuchCurrencyException) {
			Assert.assertNotNull(noSuchCurrencyException);
		}

		CommerceCurrency commerceCurrency = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			commerceCurrency =
				_commerceCurrencyLocalService.getOrAddEmptyCommerceCurrency(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId(), code);

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, commerceCurrency.getStatus());
			Assert.assertEquals(code, commerceCurrency.getCode());
			Assert.assertEquals(
				externalReferenceCode,
				commerceCurrency.getExternalReferenceCode());

			CommerceCurrency resolvedCommerceCurrency =
				_commerceCurrencyLocalService.getOrAddEmptyCommerceCurrency(
					externalReferenceCode, _user.getCompanyId(),
					_user.getUserId(), code);

			Assert.assertEquals(
				commerceCurrency.getCommerceCurrencyId(),
				resolvedCommerceCurrency.getCommerceCurrencyId());
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_user.getCompanyId(), _user.getGroupId(), _user.getUserId());

		serviceContext.setLanguageId("en_US");

		commerceCurrency = _commerceCurrencyLocalService.updateCommerceCurrency(
			commerceCurrency.getExternalReferenceCode(),
			commerceCurrency.getCommerceCurrencyId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(3), BigDecimal.ONE,
			commerceCurrency.getFormatPatternMap(),
			commerceCurrency.getMaxFractionDigits(),
			commerceCurrency.getMinFractionDigits(),
			commerceCurrency.getRoundingMode(), commerceCurrency.isPrimary(),
			commerceCurrency.getPriority(), commerceCurrency.isActive(),
			serviceContext);

		Assert.assertNotEquals(
			WorkflowConstants.STATUS_EMPTY, commerceCurrency.getStatus());
		Assert.assertEquals(code, commerceCurrency.getCode());
	}

	@Test
	public void testUpdateCommerceCurrency() throws Exception {
		AssertUtils.assertFailure(
			CommerceCurrencyRateException.class, null,
			() -> {
				CommerceCurrency commerceCurrency =
					_commerceCurrencyLocalService.addCommerceCurrency(
						null, _user.getUserId(), RandomTestUtil.randomString(3),
						RandomTestUtil.randomLocaleStringMap(),
						RandomTestUtil.randomString(3), BigDecimal.ONE,
						LocalizationUtil.getLocalizationMap(
							CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
						2, 2, "HALF_EVEN", false, 0.0, false);

				ServiceContext serviceContext =
					ServiceContextTestUtil.getServiceContext(
						_user.getCompanyId(), _user.getGroupId(),
						_user.getUserId());

				serviceContext.setLanguageId("en_US");

				_commerceCurrencyLocalService.updateCommerceCurrency(
					commerceCurrency.getExternalReferenceCode(),
					commerceCurrency.getCommerceCurrencyId(),
					commerceCurrency.getNameMap(), commerceCurrency.getCode(),
					BigDecimal.ZERO, commerceCurrency.getFormatPatternMap(),
					commerceCurrency.getMaxFractionDigits(),
					commerceCurrency.getMinFractionDigits(),
					commerceCurrency.getRoundingMode(),
					commerceCurrency.isPrimary(),
					commerceCurrency.getPriority(), commerceCurrency.isActive(),
					serviceContext);
			});

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, _user.getUserId(), "FAIL",
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(3), BigDecimal.ONE,
				LocalizationUtil.getLocalizationMap(
					CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
				2, 2, "HALF_EVEN", false, 0.0, true);

		BigDecimal oldExchangeRate = commerceCurrency.getRate();

		_commerceCurrencyLocalService.updateExchangeRate(
			commerceCurrency.getCommerceCurrencyId(),
			TestExchangeRateProvider.NAME);

		commerceCurrency = _commerceCurrencyLocalService.getCommerceCurrency(
			commerceCurrency.getCommerceCurrencyId());

		Assert.assertEquals(oldExchangeRate, commerceCurrency.getRate());
	}

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private User _user;

}