/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.headless.commerce.admin.order.client.dto.v1_0.ShippingAddress;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.bean.BeanPropertiesUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class ShippingAddressResourceTest
	extends BaseShippingAddressResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId());

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			_user.getUserId(), 0, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, serviceContext);

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			serviceContext);

		Address address = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), accountEntry.getAccountEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), _region.getRegionId(),
			_country.getCountryId(), 0, false, true,
			RandomTestUtil.randomString(), serviceContext);

		CommerceCurrency commerceCurrency =
			_commerceCurrencyLocalService.addCommerceCurrency(
				null, _user.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomString(), BigDecimal.ONE,
				RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN",
				false, RandomTestUtil.nextDouble(), true);

		CommerceChannel commerceChannel =
			_commerceChannelLocalService.addCommerceChannel(
				RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
				testGroup.getGroupId(), RandomTestUtil.randomString(),
				CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
				commerceCurrency.getCode(), serviceContext);

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), commerceChannel.getGroupId(),
			address.getAddressId(), accountEntry.getAccountEntryId(),
			commerceCurrency.getCode(),
			CommerceOrderConstants.TYPE_PK_FULFILLMENT, 0,
			address.getAddressId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), 1, 1, 2022, 0, 0,
			CommerceOrderConstants.ORDER_STATUS_OPEN,
			CommercePaymentMethodConstants.TYPE_OFFLINE,
			RandomTestUtil.randomString(), BigDecimal.ONE,
			RandomTestUtil.randomString(), BigDecimal.ONE, BigDecimal.ONE,
			BigDecimal.ONE, BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN,
			serviceContext);

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), _commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ONE, StringPool.BLANK,
				new TestCommerceContext(
					accountEntry, commerceCurrency, commerceChannel, _user,
					testGroup, _commerceOrder),
				serviceContext);

		_commerceOrderItemLocalService.updateCommerceOrderItemInfo(
			commerceOrderItem.getCommerceOrderItemId(), address.getAddressId(),
			commerceOrderItem.getDeliveryGroupName(),
			commerceOrderItem.getPrintedNote());

		_commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			_commerceOrder.getCommerceOrderId());

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);
	}

	@Ignore
	@Override
	@Test
	public void testGetOrderByExternalReferenceCodeShippingAddress()
		throws Exception {

		super.testGetOrderByExternalReferenceCodeShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrderIdShippingAddress() throws Exception {
		super.testGetOrderIdShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGetOrderItemShippingAddress() throws Exception {
		super.testGetOrderItemShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderByExternalReferenceCodeShippingAddress()
		throws Exception {

		super.testGraphQLGetOrderByExternalReferenceCodeShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderIdShippingAddress() throws Exception {
		super.testGraphQLGetOrderIdShippingAddress();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetOrderItemShippingAddress() throws Exception {
		super.testGraphQLGetOrderItemShippingAddress();
	}

	@Override
	@Test
	public void testPatchOrderByExternalReferenceCodeShippingAddress()
		throws Exception {

		ShippingAddress randomPatchShippingAddress =
			randomPatchShippingAddress();

		shippingAddressResource.
			patchOrderByExternalReferenceCodeShippingAddress(
				_commerceOrder.getExternalReferenceCode(),
				randomPatchShippingAddress);

		ShippingAddress expectedPatchShippingAddress =
			randomPatchShippingAddress.clone();

		BeanPropertiesUtil.copyProperties(
			expectedPatchShippingAddress, randomPatchShippingAddress);

		ShippingAddress getShippingAddress =
			shippingAddressResource.
				getOrderByExternalReferenceCodeShippingAddress(
					_commerceOrder.getExternalReferenceCode());

		assertEquals(expectedPatchShippingAddress, getShippingAddress);
		assertValid(getShippingAddress);
	}

	@Ignore
	@Override
	@Test
	public void testPatchOrderIdShippingAddress() throws Exception {
		super.testPatchOrderIdShippingAddress();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"city", "countryISOCode", "description", "name", "phoneNumber",
			"street1", "street2", "street3", "zip"
		};
	}

	@Override
	protected ShippingAddress randomShippingAddress() throws Exception {
		return new ShippingAddress() {
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
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@DeleteAfterTestRun
	private CommerceOrder _commerceOrder;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	private Country _country;

	@Inject
	private CountryLocalService _countryLocalService;

	private Region _region;

	@Inject
	private RegionLocalService _regionLocalService;

	@DeleteAfterTestRun
	private User _user;

}