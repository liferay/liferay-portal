/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderAddress;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class PlacedOrderAddressResourceTest
	extends BasePlacedOrderAddressResourceTestCase {

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
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(), "business", 1, _serviceContext);

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), BigDecimal.ONE, new HashMap<>(), 2,
			2, "HALF_EVEN", false, RandomTestUtil.nextDouble(), true);

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_commerceOrder = CommerceTestUtil.addB2BCommerceOrder(
			testGroup.getGroupId(), _user.getUserId(),
			_accountEntry.getAccountEntryId(),
			_commerceCurrency.getCommerceCurrencyId());

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

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
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress()
		throws Exception {

		super.
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress()
		throws Exception {

		super.
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderBillingAddress()
		throws Exception {

		super.testGraphQLGetPlacedOrderPlacedOrderBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderPlacedOrderShippingAddress()
		throws Exception {

		super.testGraphQLGetPlacedOrderPlacedOrderShippingAddress();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"city", "description", "name", "phoneNumber", "street1", "street2",
			"street3", "subtype", "zip"
		};
	}

	@Override
	protected PlacedOrderAddress randomPlacedOrderAddress() throws Exception {
		Address address = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			_country.getCountryId(), 0, _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		_addresses.add(address);

		return new PlacedOrderAddress() {
			{
				city = address.getCity();
				country = _country.getName();
				countryISOCode = _country.getA2();
				description = address.getDescription();
				externalReferenceCode = address.getExternalReferenceCode();
				id = address.getAddressId();
				latitude = address.getLatitude();
				longitude = address.getLongitude();
				name = address.getName();
				phoneNumber = address.getPhoneNumber();
				region = _region.getName();
				regionISOCode = _region.getRegionCode();
				street1 = address.getStreet1();
				street2 = address.getStreet2();
				street3 = address.getStreet3();
				subtype = address.getSubtype();
				type = RandomTestUtil.randomString();
				typeId = RandomTestUtil.randomInt();
				vatNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				zip = address.getZip();
			}
		};
	}

	@Override
	protected PlacedOrderAddress
			testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		return _updateBillingAndShippingAddresses();
	}

	@Override
	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected PlacedOrderAddress
			testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		return _updateBillingAndShippingAddresses();
	}

	@Override
	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected PlacedOrderAddress
			testGetPlacedOrderPlacedOrderBillingAddress_addPlacedOrderAddress()
		throws Exception {

		return _updateBillingAndShippingAddresses();
	}

	@Override
	protected Long
			testGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected PlacedOrderAddress
			testGetPlacedOrderPlacedOrderShippingAddress_addPlacedOrderAddress()
		throws Exception {

		return _updateBillingAndShippingAddresses();
	}

	@Override
	protected Long
			testGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderBillingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodePlacedOrderShippingAddress_getExternalReferenceCode(
				PlacedOrderAddress placedOrderAddress)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGraphQLGetPlacedOrderPlacedOrderBillingAddress_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Long
			testGraphQLGetPlacedOrderPlacedOrderShippingAddress_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	private PlacedOrderAddress _updateBillingAndShippingAddresses()
		throws Exception {

		PlacedOrderAddress placedOrderAddress = randomPlacedOrderAddress();

		_commerceOrderLocalService.updateBillingAddress(
			_commerceOrder.getCommerceOrderId(), placedOrderAddress.getId());
		_commerceOrderLocalService.updateShippingAddress(
			_commerceOrder.getCommerceOrderId(), placedOrderAddress.getId());

		return placedOrderAddress;
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private final List<Address> _addresses = new ArrayList<>();

	@Inject
	private AddressLocalService _addressLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@DeleteAfterTestRun
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}