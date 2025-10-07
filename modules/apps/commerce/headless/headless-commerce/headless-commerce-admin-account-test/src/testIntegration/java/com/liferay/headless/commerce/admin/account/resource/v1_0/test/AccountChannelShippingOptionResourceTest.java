/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalService;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountChannelShippingOption;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class AccountChannelShippingOptionResourceTest
	extends BaseAccountChannelShippingOptionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, 1,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId()));
		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(),
			RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), BigDecimal.ONE, new HashMap<>(), 2,
			2, "HALF_EVEN", false, 0, true);
		_commerceShippingMethod =
			_commerceShippingMethodLocalService.addCommerceShippingMethod(
				TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				RandomTestUtil.randomString(), null,
				RandomTestUtil.nextDouble(), null);
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPageWithPagination()
		throws Exception {

		super.
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetAccountIdAccountChannelShippingOptionPageWithPagination()
		throws Exception {

		super.testGetAccountIdAccountChannelShippingOptionPageWithPagination();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountId", "shippingMethodKey", "shippingOptionId",
			"shippingOptionKey"
		};
	}

	@Override
	protected AccountChannelShippingOption randomAccountChannelShippingOption()
		throws Exception {

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.addCommerceChannel(
				RandomTestUtil.randomString(), 0, TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(),
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				_commerceCurrency.getCode(),
				ServiceContextTestUtil.getServiceContext(
					TestPropsValues.getCompanyId(),
					TestPropsValues.getGroupId(), TestPropsValues.getUserId()));
		CommerceShippingFixedOption shippingFixedOption =
			_commerceShippingFixedOptionLocalService.
				addCommerceShippingFixedOption(
					TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
					_commerceShippingMethod.getCommerceShippingMethodId(),
					BigDecimal.TEN, RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomDouble());

		return new AccountChannelShippingOption() {
			{
				accountExternalReferenceCode =
					_accountEntry.getExternalReferenceCode();
				accountId = _accountEntry.getAccountEntryId();
				channelExternalReferenceCode =
					commerceChannel.getExternalReferenceCode();
				channelId = commerceChannel.getCommerceChannelId();
				id = RandomTestUtil.randomLong();
				shippingMethodId =
					_commerceShippingMethod.getCommerceShippingMethodId();
				shippingMethodKey = _commerceShippingMethod.getEngineKey();
				shippingOptionId =
					shippingFixedOption.getCommerceShippingFixedOptionId();
				shippingOptionKey = shippingFixedOption.getKey();
			}
		};
	}

	@Override
	protected AccountChannelShippingOption
			testDeleteAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				_accountEntry.getAccountEntryId(),
				randomAccountChannelShippingOption());
	}

	@Override
	protected AccountChannelShippingOption
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				String externalReferenceCode,
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountByExternalReferenceCodeAccountChannelShippingOption(
				externalReferenceCode, accountChannelShippingOption);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelShippingOption
			testGetAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				_accountEntry.getAccountEntryId(),
				randomAccountChannelShippingOption());
	}

	@Override
	protected AccountChannelShippingOption
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				Long id,
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				id, accountChannelShippingOption);
	}

	@Override
	protected Long testGetAccountIdAccountChannelShippingOptionPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelShippingOption
			testGraphQLAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				_accountEntry.getAccountEntryId(),
				randomAccountChannelShippingOption());
	}

	@Override
	protected AccountChannelShippingOption
			testPatchAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				_accountEntry.getAccountEntryId(),
				randomAccountChannelShippingOption());
	}

	@Override
	protected AccountChannelShippingOption
			testPostAccountByExternalReferenceCodeAccountChannelShippingOption_addAccountChannelShippingOption(
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountByExternalReferenceCodeAccountChannelShippingOption(
				_accountEntry.getExternalReferenceCode(),
				accountChannelShippingOption);
	}

	@Override
	protected AccountChannelShippingOption
			testPostAccountIdAccountChannelShippingOption_addAccountChannelShippingOption(
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		return accountChannelShippingOptionResource.
			postAccountIdAccountChannelShippingOption(
				_accountEntry.getAccountEntryId(),
				accountChannelShippingOption);
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	private CommerceShippingMethod _commerceShippingMethod;

	@Inject
	private CommerceShippingMethodLocalService
		_commerceShippingMethodLocalService;

}