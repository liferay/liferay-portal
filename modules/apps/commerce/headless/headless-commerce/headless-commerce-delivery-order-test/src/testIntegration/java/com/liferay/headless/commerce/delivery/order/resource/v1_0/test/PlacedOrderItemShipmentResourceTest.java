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
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItemShipment;
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
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class PlacedOrderItemShipmentResourceTest
	extends BasePlacedOrderItemShipmentResourceTestCase {

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

		_commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				testGroup.getGroupId(), _commerceCurrency.getCode(), true,
				CommercePriceListConstants.TYPE_PRICE_LIST, 0, true,
				RandomTestUtil.randomString(), RandomTestUtil.nextDouble(), 1,
				1, 2022, 12, 0, 0, 0, 0, 0, 0, true, _serviceContext);

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		_commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				_cpInstance.getCPInstanceId(), null,
				BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)), 0,
				BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)),
				StringPool.BLANK,
				new TestCommerceContext(
					_accountEntry, _commerceCurrency, _commerceChannel, _user,
					testGroup, _commerceOrder),
				_serviceContext);

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

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

	@Override
	protected PlacedOrderItemShipment randomPlacedOrderItemShipment()
		throws Exception {

		Address localShippingAddress = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			_country.getCountryId(), 0, _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			RandomTestUtil.randomString(), false, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		_addresses.add(localShippingAddress);

		return new PlacedOrderItemShipment() {
			{
				accountId = _accountEntry.getAccountEntryId();
				carrier = StringUtil.toLowerCase(RandomTestUtil.randomString());
				createDate = RandomTestUtil.nextDate();
				estimatedDeliveryDate = RandomTestUtil.nextDate();
				estimatedShippingDate = RandomTestUtil.nextDate();
				externalReferenceCode = RandomTestUtil.randomString();
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				orderId = _commerceOrder.getCommerceOrderId();
				quantity = BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10));
				shippingAddressId = localShippingAddress.getAddressId();
				shippingMethodId = RandomTestUtil.randomLong();
				shippingOptionName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				trackingNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected PlacedOrderItemShipment
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				String externalReferenceCode,
				PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		return _addCommerceShipmentItem(placedOrderItemShipment);
	}

	@Override
	protected String
			testGetPlacedOrderItemByExternalReferenceCodePlacedOrderItemShipmentsPage_getExternalReferenceCode()
		throws Exception {

		return _commerceOrderItem.getExternalReferenceCode();
	}

	@Override
	protected PlacedOrderItemShipment
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_addPlacedOrderItemShipment(
				Long placedOrderItemId,
				PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		return _addCommerceShipmentItem(placedOrderItemShipment);
	}

	@Override
	protected Long
			testGetPlacedOrderItemPlacedOrderItemShipmentsPage_getPlacedOrderItemId()
		throws Exception {

		return _commerceOrderItem.getCommerceOrderItemId();
	}

	private PlacedOrderItemShipment _addCommerceShipmentItem(
			PlacedOrderItemShipment placedOrderItemShipment)
		throws Exception {

		CommerceShipment commerceShipment =
			_commerceShipmentLocalService.addCommerceShipment(
				RandomTestUtil.randomString(), _commerceChannel.getGroupId(),
				placedOrderItemShipment.getAccountId(),
				placedOrderItemShipment.getShippingAddressId(),
				placedOrderItemShipment.getShippingMethodId(),
				placedOrderItemShipment.getShippingOptionName(),
				_serviceContext);

		_commerceShipments.add(commerceShipment);

		CommerceShipmentItem commerceShipmentItem =
			_commerceShipmentItemLocalService.addCommerceShipmentItem(
				RandomTestUtil.randomString(),
				commerceShipment.getCommerceShipmentId(),
				_commerceOrderItem.getCommerceOrderItemId(), 0,
				placedOrderItemShipment.getQuantity(), null, false,
				_serviceContext);

		_commerceShipmentItems.add(commerceShipmentItem);

		return new PlacedOrderItemShipment() {
			{
				accountId = commerceShipment.getCommerceAccountId();
				carrier = commerceShipment.getCarrier();
				createDate = commerceShipmentItem.getCreateDate();
				estimatedDeliveryDate =
					placedOrderItemShipment.getEstimatedDeliveryDate();
				estimatedShippingDate =
					placedOrderItemShipment.getEstimatedShippingDate();
				externalReferenceCode =
					placedOrderItemShipment.getExternalReferenceCode();
				id = commerceShipmentItem.getCommerceShipmentItemId();
				modifiedDate = commerceShipmentItem.getModifiedDate();
				orderId = _commerceOrder.getCommerceOrderId();
				quantity = commerceShipmentItem.getQuantity();
				shippingAddressId = commerceShipment.getCommerceAddressId();
				shippingOptionName = commerceShipment.getShippingOptionName();
				trackingNumber = commerceShipment.getTrackingNumber();
			}
		};
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

	@DeleteAfterTestRun
	private CommerceOrderItem _commerceOrderItem;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceShipmentItemLocalService _commerceShipmentItemLocalService;

	@DeleteAfterTestRun
	private final List<CommerceShipmentItem> _commerceShipmentItems =
		new ArrayList<>();

	@Inject
	private CommerceShipmentLocalService _commerceShipmentLocalService;

	@DeleteAfterTestRun
	private final List<CommerceShipment> _commerceShipments = new ArrayList<>();

	@DeleteAfterTestRun
	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}