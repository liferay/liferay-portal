/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalServiceUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.discount.constants.CommerceDiscountConstants;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.CommerceDiscountLocalServiceUtil;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.term.constants.CommerceTermEntryConstants;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalServiceUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountChannelEntry;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alessio Antonio Rendina
 */
@RunWith(Arquillian.class)
public class AccountChannelEntryResourceTest
	extends BaseAccountChannelEntryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId());

		_accountEntry = AccountEntryLocalServiceUtil.addOrUpdateAccountEntry(
			RandomTestUtil.randomString(), _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS, 10, serviceContext);

		_country = CountryLocalServiceUtil.addCountry(
			"XY", "XYZ", true, true, null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomDouble(), true,
			false, false, serviceContext);

		_region = RegionLocalServiceUtil.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomString(),
			serviceContext);

		_address = AddressLocalServiceUtil.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			User.class.getName(), _user.getUserId(), _country.getCountryId(), 2,
			_region.getRegionId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			false, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, String.valueOf(30133),
			RandomTestUtil.randomString(), serviceContext);

		_commerceCurrency =
			CommerceCurrencyLocalServiceUtil.addCommerceCurrency(
				null, _user.getUserId(), RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), BigDecimal.ONE, new HashMap<>(),
				2, 2, "HALF_EVEN", false, 0, true);
		_commerceDeliveryTerm =
			CommerceTermEntryLocalServiceUtil.addCommerceTermEntry(
				RandomTestUtil.randomString(), _user.getUserId(), true,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				1, 1, 2022, 12, 0, 0, 0, 0, 0, 0, true,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), 1000,
				CommerceTermEntryConstants.TYPE_DELIVERY_TERMS, null,
				serviceContext);
		_commerceDiscount =
			CommerceDiscountLocalServiceUtil.addCommerceDiscount(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomString(),
				CommerceDiscountConstants.TARGET_CATEGORIES, false, null, true,
				BigDecimal.ZERO, StringPool.BLANK, BigDecimal.TEN,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				CommerceDiscountConstants.LIMITATION_TYPE_UNLIMITED, 0, true,
				true, 1, 1, 2022, 12, 0, 0, 0, 0, 0, 0, true, serviceContext);
		_commercePaymentTerm =
			CommerceTermEntryLocalServiceUtil.addCommerceTermEntry(
				RandomTestUtil.randomString(), _user.getUserId(), true,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				1, 1, 2022, 12, 0, 0, 0, 0, 0, 0, true,
				Collections.singletonMap(
					LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
				RandomTestUtil.randomString(), 1000,
				CommerceTermEntryConstants.TYPE_PAYMENT_TERMS, null,
				serviceContext);
		_commercePriceList =
			CommercePriceListLocalServiceUtil.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				testGroup.getGroupId(), _commerceCurrency.getCode(), true,
				CommercePriceListConstants.TYPE_PRICE_LIST, 0, true,
				RandomTestUtil.randomString(), 1000, 1, 1, 2022, 12, 0, 0, 0, 0,
				0, 0, true, serviceContext);
		_commerceUser = UserLocalServiceUtil.addUser(
			_user.getUserId(), testCompany.getCompanyId(), true,
			StringPool.BLANK, StringPool.BLANK, true,
			RandomTestUtil.randomString(),
			RandomTestUtil.randomString() + "@liferay.com",
			LocaleUtil.getSiteDefault(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 0, 0,
			true, 1, 1, 2022, RandomTestUtil.randomString(),
			UserConstants.TYPE_REGULAR, null, null, null, null, false,
			serviceContext);

		Role role = RoleLocalServiceUtil.getRole(
			testCompany.getCompanyId(), RoleConstants.ADMINISTRATOR);

		UserLocalServiceUtil.addRoleUser(
			role.getRoleId(), _commerceUser.getUserId());
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelCurrencyId() throws Exception {
		super.testGraphQLDeleteAccountChannelCurrencyId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelDeliveryTermId()
		throws Exception {

		super.testGraphQLDeleteAccountChannelDeliveryTermId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelDiscountId() throws Exception {
		super.testGraphQLDeleteAccountChannelDiscountId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelPaymentMethodId()
		throws Exception {

		super.testGraphQLDeleteAccountChannelPaymentMethodId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelPaymentTermId()
		throws Exception {

		super.testGraphQLDeleteAccountChannelPaymentTermId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelPriceListId() throws Exception {
		super.testGraphQLDeleteAccountChannelPriceListId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelShippingAddressId()
		throws Exception {

		super.testGraphQLDeleteAccountChannelShippingAddressId();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteAccountChannelUserId() throws Exception {
		super.testGraphQLDeleteAccountChannelUserId();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"overrideEligibility", "priority"};
	}

	@Override
	protected AccountChannelEntry randomAccountChannelEntry() throws Exception {
		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		_commerceChannels.add(commerceChannel);

		return new AccountChannelEntry() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = commerceChannel.getPrimaryKey();
				classExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classPK = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				overrideEligibility = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	@Override
	protected AccountChannelEntry randomPatchAccountChannelEntry()
		throws Exception {

		return new AccountChannelEntry() {
			{
				overrideEligibility = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
			}
		};
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelBillingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountChannelEntryBillingAddress();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelCurrencyId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelCurrency();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelDeliveryTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDeliveryTerm();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelDiscountId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDiscount();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelPaymentMethodId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentMethod();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelPaymentTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentTerm();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelPriceListId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPriceList();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelShippingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelShippingAddress();
	}

	@Override
	protected AccountChannelEntry
			testDeleteAccountChannelUserId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelUser();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelBillingAddressesPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelBillingAddress(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelBillingAddressesPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelCurrenciesPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceCurrency.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelCurrency(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelCurrenciesPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDeliveryTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelDeliveryTerm(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelDeliveryTermsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelDiscountsPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDiscount.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelDiscount(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelDiscountsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_addCommercePaymentMethodGroupRel(
				accountChannelEntry.getChannelId());

		accountChannelEntry.setClassPK(
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPaymentMethod(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelPaymentMethodsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelPaymentTermsPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePaymentTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPaymentTerm(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelPaymentTermsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelPriceListsPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePriceList.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPriceList(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelPriceListsPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelShippingAddressesPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelShippingAddress(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelShippingAddressesPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountByExternalReferenceCodeAccountChannelUsersPage_addAccountChannelEntry(
				String externalReferenceCode,
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceUser.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelUser(
				externalReferenceCode, accountChannelEntry);
	}

	@Override
	protected String
			testGetAccountByExternalReferenceCodeAccountChannelUsersPage_getExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelBillingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountChannelEntryBillingAddress();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelCurrencyId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelCurrency();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelDeliveryTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDeliveryTerm();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelDiscountId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDiscount();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelPaymentMethodId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentMethod();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelPaymentTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentTerm();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelPriceListId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPriceList();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelShippingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelShippingAddress();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountChannelUserId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelUser();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelBillingAddressesPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelBillingAddress(id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelBillingAddressesPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelCurrenciesPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceCurrency.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelCurrency(
			id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelCurrenciesPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelDeliveryTermsPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDeliveryTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelDeliveryTerm(id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelDeliveryTermsPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelDiscountsPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDiscount.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelDiscount(
			id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelDiscountsPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelPaymentMethodsPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_addCommercePaymentMethodGroupRel(
				accountChannelEntry.getChannelId());

		accountChannelEntry.setClassPK(
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentMethod(id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelPaymentMethodsPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelPaymentTermsPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePaymentTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentTerm(id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelPaymentTermsPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelPriceListsPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePriceList.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelPriceList(
			id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelPriceListsPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelShippingAddressesPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelShippingAddress(id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelShippingAddressesPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGetAccountIdAccountChannelUsersPage_addAccountChannelEntry(
				Long id, AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceUser.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelUser(
			id, accountChannelEntry);
	}

	@Override
	protected Long testGetAccountIdAccountChannelUsersPage_getId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLAccountChannelEntry_addAccountChannelEntry()
		throws Exception {

		return _postAccountChannelEntryBillingAddress();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelBillingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountChannelEntryBillingAddress();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelCurrencyId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelCurrency();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelDeliveryTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDeliveryTerm();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelDiscountId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDiscount();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelPaymentMethodId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentMethod();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelPaymentTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentTerm();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelPriceListId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPriceList();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelShippingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelShippingAddress();
	}

	@Override
	protected AccountChannelEntry
			testGraphQLGetAccountChannelUserId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelUser();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelBillingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountChannelEntryBillingAddress();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelCurrencyId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelCurrency();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelDeliveryTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDeliveryTerm();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelDiscountId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelDiscount();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelPaymentMethodId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentMethod();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelPaymentTermId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPaymentTerm();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelPriceListId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelPriceList();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelShippingAddressId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelShippingAddress();
	}

	@Override
	protected AccountChannelEntry
			testPatchAccountChannelUserId_addAccountChannelEntry()
		throws Exception {

		return _postAccountIdAccountChannelUser();
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelBillingAddress_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelBillingAddress(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelCurrency_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceCurrency.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelCurrency(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelDeliveryTerm_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDeliveryTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelDeliveryTerm(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelDiscount_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDiscount.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelDiscount(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelPaymentMethod_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_addCommercePaymentMethodGroupRel(
				accountChannelEntry.getChannelId());

		accountChannelEntry.setClassPK(
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPaymentMethod(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelPaymentTerm_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePaymentTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPaymentTerm(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelPriceList_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePriceList.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelPriceList(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelShippingAddress_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelShippingAddress(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountByExternalReferenceCodeAccountChannelUser_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceUser.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountByExternalReferenceCodeAccountChannelUser(
				_accountEntry.getExternalReferenceCode(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelBillingAddress_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelBillingAddress(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelCurrency_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceCurrency.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelCurrency(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelDeliveryTerm_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDeliveryTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelDeliveryTerm(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelDiscount_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceDiscount.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelDiscount(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelPaymentMethod_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_addCommercePaymentMethodGroupRel(
				accountChannelEntry.getChannelId());

		accountChannelEntry.setClassPK(
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentMethod(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelPaymentTerm_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePaymentTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentTerm(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelPriceList_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commercePriceList.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelPriceList(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelShippingAddress_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelShippingAddress(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@Override
	protected AccountChannelEntry
			testPostAccountIdAccountChannelUser_addAccountChannelEntry(
				AccountChannelEntry accountChannelEntry)
		throws Exception {

		accountChannelEntry.setClassPK(_commerceUser.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelUser(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private CommercePaymentMethodGroupRel _addCommercePaymentMethodGroupRel(
			long channelId)
		throws Exception {

		CommerceChannel commerceChannel =
			CommerceChannelLocalServiceUtil.getCommerceChannel(channelId);

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			CommerceTestUtil.addCommercePaymentMethodGroupRel(
				_user.getUserId(), commerceChannel.getGroupId());

		_commercePaymentMethodGroupRels.add(commercePaymentMethodGroupRel);

		return commercePaymentMethodGroupRel;
	}

	private AccountChannelEntry _postAccountChannelEntryBillingAddress()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelBillingAddress(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelCurrency()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commerceCurrency.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelCurrency(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelDeliveryTerm()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commerceDeliveryTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelDeliveryTerm(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelDiscount()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commerceDiscount.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelDiscount(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelPaymentMethod()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_addCommercePaymentMethodGroupRel(
				accountChannelEntry.getChannelId());

		accountChannelEntry.setClassPK(
			commercePaymentMethodGroupRel.getCommercePaymentMethodGroupRelId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentMethod(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelPaymentTerm()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commercePaymentTerm.getPrimaryKey());

		return accountChannelEntryResource.
			postAccountIdAccountChannelPaymentTerm(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelPriceList()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commercePriceList.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelPriceList(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelShippingAddress()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_address.getAddressId());

		return accountChannelEntryResource.
			postAccountIdAccountChannelShippingAddress(
				_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	private AccountChannelEntry _postAccountIdAccountChannelUser()
		throws Exception {

		AccountChannelEntry accountChannelEntry = randomAccountChannelEntry();

		accountChannelEntry.setClassPK(_commerceUser.getPrimaryKey());

		return accountChannelEntryResource.postAccountIdAccountChannelUser(
			_accountEntry.getAccountEntryId(), accountChannelEntry);
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@DeleteAfterTestRun
	private Address _address;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CommerceTermEntry _commerceDeliveryTerm;

	@DeleteAfterTestRun
	private CommerceDiscount _commerceDiscount;

	@DeleteAfterTestRun
	private CommercePaymentMethodGroupRel _commercePaymentMethodGroupRel;

	@DeleteAfterTestRun
	private List<CommercePaymentMethodGroupRel>
		_commercePaymentMethodGroupRels = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceTermEntry _commercePaymentTerm;

	@DeleteAfterTestRun
	private CommercePriceList _commercePriceList;

	@DeleteAfterTestRun
	private User _commerceUser;

	@DeleteAfterTestRun
	private Country _country;

	@DeleteAfterTestRun
	private Region _region;

	@DeleteAfterTestRun
	private User _user;

}