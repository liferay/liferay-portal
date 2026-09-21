/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.constants.CommerceCurrencyConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
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
 */
@RunWith(Arquillian.class)
public class CommerceCurrencyServiceTest {

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
	public void testGetCommerceCurrencies() throws Exception {
		_commerceCurrency = _commerceCurrencyService.addCommerceCurrency(
			null, RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(3), BigDecimal.ONE,
			LocalizationUtil.getLocalizationMap(
				CommerceCurrencyConstants.DECIMAL_FORMAT_PATTERN),
			2, 2, "HALF_EVEN", false, 0.0, true);

		Assert.assertTrue(
			ListUtil.exists(
				_commerceCurrencyService.getCommerceCurrencies(
					_user.getCompanyId(), true, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
				commerceCurrency ->
					commerceCurrency.getCommerceCurrencyId() ==
						_commerceCurrency.getCommerceCurrencyId()));

		_commerceCurrency = _commerceCurrencyService.setActive(
			_commerceCurrency.getCommerceCurrencyId(), false);

		Assert.assertFalse(
			ListUtil.exists(
				_commerceCurrencyService.getCommerceCurrencies(
					_user.getCompanyId(), true, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS, null),
				commerceCurrency ->
					commerceCurrency.getCommerceCurrencyId() ==
						_commerceCurrency.getCommerceCurrencyId()));
	}

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyService _commerceCurrencyService;

	private User _user;

}