/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.configuration.AccountEntryAddressSubtypeConfiguration;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.BillingAddress;
import com.liferay.list.type.model.ListTypeDefinition;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeDefinitionLocalService;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class BillingAddressResourceTest
	extends BaseBillingAddressResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, _user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN",
				false, RandomTestUtil.nextDouble(), true);

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), commerceCurrency.getCode());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(), 0,
			_accountEntry.getAccountEntryId(), commerceCurrency.getCode(),
			CommerceOrderConstants.TYPE_PK_FULFILLMENT, 0, 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), 1, 1,
			2022, 0, 0, CommerceOrderConstants.ORDER_STATUS_OPEN,
			CommercePaymentMethodConstants.TYPE_OFFLINE,
			RandomTestUtil.randomString(), BigDecimal.ONE,
			RandomTestUtil.randomString(), BigDecimal.ONE, BigDecimal.ONE,
			BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN,
			_serviceContext);

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, _serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Ignore
	@Override
	@Test
	public void testGetOrderByExternalReferenceCodeBillingAddress()
		throws Exception {

		super.testGetOrderByExternalReferenceCodeBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrderIdBillingAddress() throws Exception {
		super.testGetOrderIdBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeBillingAddress()
		throws Exception {

		super.testGraphQLGetOrderByExternalReferenceCodeBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderIdBillingAddress() throws Exception {
		super.testGraphQLGetOrderIdBillingAddress();
	}

	@Override
	@Test
	public void testPatchOrderByExternalReferenceCodeBillingAddress()
		throws Exception {

		BillingAddress randomPatchBillingAddress = randomPatchBillingAddress();

		billingAddressResource.patchOrderByExternalReferenceCodeBillingAddress(
			_commerceOrder.getExternalReferenceCode(),
			randomPatchBillingAddress);

		BillingAddress expectedPatchBillingAddress =
			randomPatchBillingAddress.clone();

		BeanPropertiesUtil.copyProperties(
			expectedPatchBillingAddress, randomPatchBillingAddress);

		BillingAddress getBillingAddress =
			billingAddressResource.
				getOrderByExternalReferenceCodeBillingAddress(
					_commerceOrder.getExternalReferenceCode());

		assertEquals(expectedPatchBillingAddress, getBillingAddress);
		assertValid(getBillingAddress);
	}

	@Override
	@Test
	public void testPatchOrderIdBillingAddress() throws Exception {
		BillingAddress randomPatchBillingAddress = randomPatchBillingAddress();

		billingAddressResource.patchOrderIdBillingAddress(
			_commerceOrder.getCommerceOrderId(), randomPatchBillingAddress);

		BillingAddress expectedPatchBillingAddress =
			randomPatchBillingAddress.clone();

		BeanPropertiesUtil.copyProperties(
			expectedPatchBillingAddress, randomPatchBillingAddress);

		BillingAddress getBillingAddress =
			billingAddressResource.getOrderIdBillingAddress(
				_commerceOrder.getCommerceOrderId());

		assertEquals(expectedPatchBillingAddress, getBillingAddress);
		assertValid(getBillingAddress);

		_testPatchOrderIdBillingAddressWithSubtype();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"city", "description", "name", "phoneNumber", "street1", "street2",
			"street3", "subtype", "zip"
		};
	}

	@Override
	protected BillingAddress randomBillingAddress() throws Exception {
		return new BillingAddress() {
			{
				city = StringUtil.toLowerCase(RandomTestUtil.randomString());
				countryISOCode = _country.getA2();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				phoneNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				regionISOCode = _region.getRegionCode();
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				subtype = StringPool.BLANK;
				vatNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected BillingAddress
			testGetOrderByExternalReferenceCodeBillingAddress_addBillingAddress()
		throws Exception {

		return _addAddress();
	}

	@Override
	protected BillingAddress testGetOrderIdBillingAddress_addBillingAddress()
		throws Exception {

		return _addAddress();
	}

	private BillingAddress _addAddress() throws Exception {
		Address address = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			_country.getCountryId(), 0, _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		return new BillingAddress() {
			{
				city = address.getCity();
				countryISOCode = _country.getA2();
				description = address.getDescription();
				externalReferenceCode = address.getExternalReferenceCode();
				id = address.getAddressId();
				latitude = address.getLatitude();
				longitude = address.getLongitude();
				name = address.getName();
				phoneNumber = address.getPhoneNumber();
				regionISOCode = _region.getRegionCode();
				street1 = address.getStreet1();
				street2 = address.getStreet2();
				street3 = address.getStreet3();
				subtype = address.getSubtype();
				zip = address.getZip();
			}
		};
	}

	private void _testPatchOrderIdBillingAddressWithSubtype() throws Exception {
		BillingAddress billingAddress = randomBillingAddress();

		ListTypeDefinition listTypeDefinition =
			_listTypeDefinitionLocalService.addListTypeDefinition(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				false);

		ListTypeEntry listTypeEntry =
			_listTypeEntryLocalService.addListTypeEntry(
				null, TestPropsValues.getUserId(),
				listTypeDefinition.getListTypeDefinitionId(),
				RandomTestUtil.randomString(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				listTypeDefinition.isSystem());

		billingAddress.setSubtype(listTypeEntry.getKey());

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						AccountEntryAddressSubtypeConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"billingAndShippingAddressSubtypeListType" +
								"DefinitionExternalReferenceCode",
							listTypeDefinition.getExternalReferenceCode()
						).build())) {

			billingAddressResource.patchOrderIdBillingAddress(
				_commerceOrder.getCommerceOrderId(), billingAddress);

			billingAddress = billingAddressResource.getOrderIdBillingAddress(
				_commerceOrder.getCommerceOrderId());

			Assert.assertEquals(
				listTypeEntry.getKey(), billingAddress.getSubtype());
		}
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@Inject
	private ListTypeDefinitionLocalService _listTypeDefinitionLocalService;

	@Inject
	private ListTypeEntryLocalService _listTypeEntryLocalService;

	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;
	private User _user;

}