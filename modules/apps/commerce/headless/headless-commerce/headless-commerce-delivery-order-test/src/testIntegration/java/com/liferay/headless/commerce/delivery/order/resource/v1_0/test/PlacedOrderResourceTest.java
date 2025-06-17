/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrder;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderAddress;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderResource;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class PlacedOrderResourceTest extends BasePlacedOrderResourceTestCase {

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

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, _serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			_serviceContext);
	}

	@Override
	@Test
	public void testGetChannelPlacedOrdersPage() throws Exception {
		super.testGetChannelPlacedOrdersPage();

		_testGetChannelPlacedOrdersPageWithFilter();
	}

	@Override
	@Test
	public void testGetPlacedOrder() throws Exception {
		super.testGetPlacedOrder();

		_testGetPlacedOrderWithPlacedOrderBillingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGetPlacedOrderByExternalReferenceCodePaymentURL()
		throws Exception {

		super.testGetPlacedOrderByExternalReferenceCodePaymentURL();
	}

	@Ignore
	@Override
	@Test
	public void testGetPlacedOrderPaymentURL() throws Exception {
		super.testGetPlacedOrderPaymentURL();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"accountId", "name", "printedNote", "purchaseOrderNumber"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"account", "accountId", "author", "orderDate", "orderId",
			"orderType"
		};
	}

	@Override
	protected PlacedOrder randomPlacedOrder() throws Exception {
		DateConfig dateConfig = DateConfig.toDisplayDateConfig(
			new Date(), _user.getTimeZone());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true,
				dateConfig.getMonth(), dateConfig.getDay(),
				dateConfig.getYear(), dateConfig.getHour(),
				dateConfig.getMinute(), 0, dateConfig.getMonth() + 1,
				dateConfig.getDay(), dateConfig.getYear(), dateConfig.getHour(),
				dateConfig.getMinute(), true, _serviceContext);

		return new PlacedOrder() {
			{
				accountId = _accountEntry.getAccountEntryId();
				channelId = _commerceChannel.getCommerceChannelId();
				couponCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				currencyCode = _commerceCurrency.getCode();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				lastPriceUpdateDate = RandomTestUtil.nextDate();
				modifiedDate = RandomTestUtil.nextDate();
				name = RandomTestUtil.randomString();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrderType.getCommerceOrderTypeId();
				orderUUID = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				paymentStatus = RandomTestUtil.randomInt();
				paymentStatusLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				placedOrderBillingAddressId = RandomTestUtil.randomLong();
				placedOrderShippingAddressId = RandomTestUtil.randomLong();
				printedNote = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				purchaseOrderNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippingMethod = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingOption = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				status = StringUtil.toLowerCase(RandomTestUtil.randomString());
				useAsBilling = RandomTestUtil.randomBoolean();
				valid = true;
			}
		};
	}

	@Override
	protected PlacedOrder testGetChannelAccountPlacedOrdersPage_addPlacedOrder(
			Long accountId, Long channelId, PlacedOrder placedOrder)
		throws Exception {

		return _addCommerceOrder(placedOrder);
	}

	@Override
	protected Long testGetChannelAccountPlacedOrdersPage_getAccountId()
		throws Exception {

		return _accountEntry.getAccountEntryId();
	}

	@Override
	protected Long testGetChannelAccountPlacedOrdersPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected PlacedOrder
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				String accountExternalReferenceCode,
				String channelExternalReferenceCode, PlacedOrder placedOrder)
		throws Exception {

		return _addCommerceOrder(placedOrder);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getAccountExternalReferenceCode()
		throws Exception {

		return _accountEntry.getExternalReferenceCode();
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeAccountByExternalReferenceCodeAccountExternalReferenceCodePlacedOrdersPage_getChannelExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected PlacedOrder
			testGetChannelByExternalReferenceCodePlacedOrdersPage_addPlacedOrder(
				String externalReferenceCode, PlacedOrder placedOrder)
		throws Exception {

		return _addCommerceOrder(placedOrder);
	}

	@Override
	protected String
			testGetChannelByExternalReferenceCodePlacedOrdersPage_getExternalReferenceCode()
		throws Exception {

		return _commerceChannel.getExternalReferenceCode();
	}

	@Override
	protected PlacedOrder testGetChannelPlacedOrdersPage_addPlacedOrder(
			Long channelId, PlacedOrder placedOrder)
		throws Exception {

		return _addCommerceOrder(placedOrder);
	}

	@Override
	protected Long testGetChannelPlacedOrdersPage_getChannelId()
		throws Exception {

		return _commerceChannel.getCommerceChannelId();
	}

	@Override
	protected PlacedOrder testGetPlacedOrder_addPlacedOrder() throws Exception {
		return _addCommerceOrder(randomPlacedOrder());
	}

	@Override
	protected PlacedOrder
			testGetPlacedOrderByExternalReferenceCode_addPlacedOrder()
		throws Exception {

		return _addCommerceOrder(randomPlacedOrder());
	}

	@Override
	protected PlacedOrder testGraphQLPlacedOrder_addPlacedOrder()
		throws Exception {

		return _addCommerceOrder(randomPlacedOrder());
	}

	@Override
	protected PlacedOrder testPatchPlacedOrder_addPlacedOrder()
		throws Exception {

		return _addCommerceOrder(randomPlacedOrder());
	}

	@Override
	protected PlacedOrder
			testPatchPlacedOrderByExternalReferenceCode_addPlacedOrder()
		throws Exception {

		return _addCommerceOrder(randomPlacedOrder());
	}

	private PlacedOrder _addCommerceOrder(PlacedOrder placedOrder)
		throws Exception {

		DateConfig orderDateConfig = DateConfig.toDisplayDateConfig(
			placedOrder.getCreateDate(), _user.getTimeZone());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				placedOrder.getPlacedOrderBillingAddressId(),
				placedOrder.getAccountId(), _commerceCurrency.getCode(),
				placedOrder.getOrderTypeId(), 0,
				placedOrder.getPlacedOrderShippingAddressId(),
				placedOrder.getPaymentMethod(), placedOrder.getName(),
				orderDateConfig.getMonth(), orderDateConfig.getDay(),
				orderDateConfig.getYear(), orderDateConfig.getHour(),
				orderDateConfig.getMinute(),
				CommerceOrderConstants.ORDER_STATUS_COMPLETED,
				placedOrder.getPaymentStatus(),
				placedOrder.getPurchaseOrderNumber(), BigDecimal.ZERO,
				placedOrder.getShippingOption(), BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
				BigDecimal.ZERO, BigDecimal.ZERO, _serviceContext);

		commerceOrder.setRequestedDeliveryDate(
			placedOrder.getRequestedDeliveryDate());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		_commerceOrders.add(commerceOrder);

		return _addPlacedOrder(commerceOrder);
	}

	private PlacedOrder _addPlacedOrder(CommerceOrder commerceOrder)
		throws Exception {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.getCommerceOrderType(
				commerceOrder.getCommerceOrderTypeId());

		return new PlacedOrder() {
			{
				accountId = commerceOrder.getCommerceAccountId();
				channelId = _commerceChannel.getCommerceChannelId();
				couponCode = commerceOrder.getCouponCode();
				createDate = commerceOrder.getCreateDate();
				currencyCode = _commerceCurrency.getCode();
				externalReferenceCode =
					commerceOrder.getExternalReferenceCode();
				id = commerceOrder.getCommerceOrderId();
				lastPriceUpdateDate = commerceOrder.getLastPriceUpdateDate();
				modifiedDate = commerceOrder.getModifiedDate();
				name = commerceOrder.getName();
				orderTypeExternalReferenceCode =
					commerceOrderType.getExternalReferenceCode();
				orderTypeId = commerceOrder.getCommerceOrderTypeId();
				orderUUID = commerceOrder.getUuid();
				paymentMethod = commerceOrder.getCommercePaymentMethodKey();
				paymentStatus = commerceOrder.getPaymentStatus();
				placedOrderBillingAddressId =
					commerceOrder.getBillingAddressId();
				placedOrderShippingAddressId =
					commerceOrder.getShippingAddressId();
				printedNote = commerceOrder.getPrintedNote();
				purchaseOrderNumber = commerceOrder.getPurchaseOrderNumber();
				requestedDeliveryDate =
					commerceOrder.getRequestedDeliveryDate();
				shippingOption = commerceOrder.getShippingOptionName();
				status = commerceOrder.getAdvanceStatus();
				valid = true;
			}
		};
	}

	private PlacedOrderAddress _addPlacedOrderAddress() throws Exception {
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

	private void _testGetChannelPlacedOrdersPageWithFilter() throws Exception {
		PlacedOrder placedOrder = _addCommerceOrder(randomPlacedOrder());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.getCommerceOrder(placedOrder.getId());

		AccountEntry accountEntry =
			CommerceAccountTestUtil.addBusinessAccountEntry(
				_serviceContext.getUserId(),
				RandomTestUtil.randomString() + StringPool.SEMICOLON, null,
				null, new long[] {_user.getUserId()}, null, _serviceContext);

		commerceOrder.setCommerceAccountId(accountEntry.getAccountEntryId());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString() + StringPool.CARET,
				_user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), true, 0, 1, 1970, 12, 0,
				0, 0, 0, 0, 0, 0, true, _serviceContext);

		commerceOrder.setCommerceOrderTypeId(
			commerceOrderType.getCommerceOrderTypeId());

		commerceOrder.setExternalReferenceCode(
			RandomTestUtil.randomString() + StringPool.CLOSE_CURLY_BRACE);
		commerceOrder.setName(RandomTestUtil.randomString() + StringPool.AT);
		commerceOrder.setPurchaseOrderNumber(
			RandomTestUtil.randomString() + StringPool.AMPERSAND);
		commerceOrder.setRequestedDeliveryDate(RandomTestUtil.nextDate());

		commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			commerceOrder);

		Page<PlacedOrder> page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format("(account eq '%s')", accountEntry.getName()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(externalReferenceCode eq '%s')",
				commerceOrder.getExternalReferenceCode()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format("(name eq '%s')", commerceOrder.getName()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(orderTypeExternalReferenceCode eq '%s')",
				commerceOrderType.getExternalReferenceCode()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(purchaseOrderNumber eq '%s')",
				commerceOrder.getPurchaseOrderNumber()),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		page = placedOrderResource.getChannelPlacedOrdersPage(
			_commerceChannel.getCommerceChannelId(), null,
			String.format(
				"(requestedDeliveryDate eq %s)",
				dateFormat.format(commerceOrder.getRequestedDeliveryDate())),
			Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());
	}

	private void _testGetPlacedOrderWithPlacedOrderBillingAddress()
		throws Exception {

		User omniadminUser = UserTestUtil.addOmniadminUser();
		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		PlacedOrderResource placedOrderResource = PlacedOrderResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "placedOrderBillingAddress"
		).build();

		PlacedOrder placedOrder = randomPlacedOrder();

		PlacedOrderAddress placedOrderAddress = _addPlacedOrderAddress();

		placedOrder.setPlacedOrderBillingAddressId(placedOrderAddress.getId());

		PlacedOrder postPlacedOrder = _addCommerceOrder(placedOrder);

		PlacedOrder getPlacedOrder = placedOrderResource.getPlacedOrder(
			postPlacedOrder.getId());

		PlacedOrderAddress getPlacedOrderBillingAddress =
			getPlacedOrder.getPlacedOrderBillingAddress();

		Assert.assertNotNull(getPlacedOrderBillingAddress);
		Assert.assertEquals(
			getPlacedOrderBillingAddress.getExternalReferenceCode(),
			placedOrderAddress.getExternalReferenceCode());
		Assert.assertEquals(
			getPlacedOrderBillingAddress.getId(), placedOrderAddress.getId());
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

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private final List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

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

	@Inject
	private UserLocalService _userLocalService;

}