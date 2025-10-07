/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.constants.CommerceOrderConstants;
import com.liferay.commerce.constants.CommercePaymentMethodConstants;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalService;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelQualifierService;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.service.CommerceShippingMethodLocalServiceUtil;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalService;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierService;
import com.liferay.commerce.term.constants.CommerceTermEntryConstants;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.CommerceTermEntryLocalService;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.Term;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.CountryLocalService;
import com.liferay.portal.kernel.service.RegionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class TermResourceTest extends BaseTermResourceTestCase {

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

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_country = _countryLocalService.addCountry(
			"XY", "XYZ", true, true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), true, true, false, _serviceContext);

		_region = _regionLocalService.addRegion(
			_country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			_serviceContext);

		_address = _addressLocalService.addAddress(
			RandomTestUtil.randomString(), _user.getUserId(),
			AccountEntry.class.getName(), _accountEntry.getAccountEntryId(),
			_country.getCountryId(), 0, _region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), false,
			RandomTestUtil.randomString(), true, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			_serviceContext);

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomString(), BigDecimal.ONE,
			RandomTestUtil.randomLocaleStringMap(), 2, 2, "HALF_EVEN", false,
			RandomTestUtil.nextDouble(), true);

		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT, testGroup.getGroupId(),
			RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			_commerceCurrency.getCode(), _serviceContext);

		_commerceShippingMethod =
			CommerceShippingMethodLocalServiceUtil.addCommerceShippingMethod(
				_user.getUserId(), _commerceChannel.getGroupId(),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				Collections.singletonMap(
					LocaleUtil.US, RandomTestUtil.randomString()),
				true, "fixed", null, 1, RandomTestUtil.randomString());

		String shippingOptionName = RandomTestUtil.randomString();

		_commerceShippingFixedOption =
			_commerceShippingFixedOptionLocalService.
				addCommerceShippingFixedOption(
					_user.getUserId(), _commerceChannel.getGroupId(),
					_commerceShippingMethod.getCommerceShippingMethodId(),
					BigDecimal.valueOf(RandomTestUtil.nextDouble()),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomString(),
					Collections.singletonMap(LocaleUtil.US, shippingOptionName),
					RandomTestUtil.nextDouble());

		_commerceOrder = _commerceOrderLocalService.addCommerceOrder(
			_user.getUserId(), _commerceChannel.getGroupId(),
			_address.getAddressId(), _accountEntry.getAccountEntryId(),
			_commerceCurrency.getCode(),
			CommerceOrderConstants.TYPE_PK_FULFILLMENT,
			_commerceShippingMethod.getCommerceShippingMethodId(),
			_address.getAddressId(), "money-order",
			RandomTestUtil.randomString(), 1, 1, 2022, 0, 0,
			CommerceOrderConstants.ORDER_STATUS_OPEN,
			CommercePaymentMethodConstants.TYPE_OFFLINE,
			RandomTestUtil.randomString(), BigDecimal.ONE, shippingOptionName,
			BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE,
			BigDecimal.TEN, BigDecimal.TEN, _serviceContext);

		_commerceOrder.setOrderStatus(
			CommerceOrderConstants.ORDER_STATUS_COMPLETED);

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		_commercePaymentMethodGroupRel =
			_commercePaymentMethodGroupRelLocalService.
				addCommercePaymentMethodGroupRel(
					_user.getUserId(), _commerceChannel.getGroupId(),
					Collections.singletonMap(
						LocaleUtil.US, RandomTestUtil.randomString()),
					Collections.singletonMap(
						LocaleUtil.US, RandomTestUtil.randomString()),
					true, null, "money-order", 1, null);
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodeDeliveryTerm()
		throws Exception {

		super.testGraphQLGetPlacedOrderByExternalReferenceCodeDeliveryTerm();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderByExternalReferenceCodePaymentTerm()
		throws Exception {

		super.testGraphQLGetPlacedOrderByExternalReferenceCodePaymentTerm();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderDeliveryTerm() throws Exception {
		super.testGraphQLGetPlacedOrderDeliveryTerm();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPlacedOrderPaymentTerm() throws Exception {
		super.testGraphQLGetPlacedOrderPaymentTerm();
	}

	@Override
	protected Term
			testGetPlacedOrderByExternalReferenceCodeDeliveryTerm_addTerm()
		throws Exception {

		return _addDeliveryTerm();
	}

	@Override
	protected String
			testGetPlacedOrderByExternalReferenceCodeDeliveryTerm_getExternalReferenceCode(
				Term term)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Term
			testGetPlacedOrderByExternalReferenceCodePaymentTerm_addTerm()
		throws Exception {

		return _addPaymentTerm();
	}

	@Override
	protected String
			testGetPlacedOrderByExternalReferenceCodePaymentTerm_getExternalReferenceCode(
				Term term)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Term testGetPlacedOrderDeliveryTerm_addTerm() throws Exception {
		return _addDeliveryTerm();
	}

	@Override
	protected Long testGetPlacedOrderDeliveryTerm_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Term testGetPlacedOrderPaymentTerm_addTerm() throws Exception {
		return _addPaymentTerm();
	}

	@Override
	protected Long testGetPlacedOrderPaymentTerm_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Term
			testGraphQLGetPlacedOrderByExternalReferenceCodeDeliveryTerm_addTerm()
		throws Exception {

		return _addDeliveryTerm();
	}

	@Override
	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodeDeliveryTerm_getExternalReferenceCode(
				Term term)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Term
			testGraphQLGetPlacedOrderByExternalReferenceCodePaymentTerm_addTerm()
		throws Exception {

		return _addPaymentTerm();
	}

	@Override
	protected String
			testGraphQLGetPlacedOrderByExternalReferenceCodePaymentTerm_getExternalReferenceCode(
				Term term)
		throws Exception {

		return _commerceOrder.getExternalReferenceCode();
	}

	@Override
	protected Term testGraphQLGetPlacedOrderDeliveryTerm_addTerm()
		throws Exception {

		return _addDeliveryTerm();
	}

	@Override
	protected Long testGraphQLGetPlacedOrderDeliveryTerm_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	@Override
	protected Term testGraphQLGetPlacedOrderPaymentTerm_addTerm()
		throws Exception {

		return _addPaymentTerm();
	}

	@Override
	protected Long testGraphQLGetPlacedOrderPaymentTerm_getPlacedOrderId()
		throws Exception {

		return _commerceOrder.getCommerceOrderId();
	}

	private Term _addDeliveryTerm() throws Exception {
		CommerceTermEntry commerceTermEntry = _addTerm(
			CommerceTermEntryConstants.TYPE_DELIVERY_TERMS);

		_commerceOrder.setDeliveryCommerceTermEntryId(
			commerceTermEntry.getCommerceTermEntryId());
		_commerceOrder.setDeliveryCommerceTermEntryDescription(
			commerceTermEntry.getDescription());
		_commerceOrder.setDeliveryCommerceTermEntryName(
			commerceTermEntry.getLabel());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return new Term() {
			{
				description = commerceTermEntry.getDescription(null, true);
				externalReferenceCode =
					commerceTermEntry.getExternalReferenceCode();
				id = commerceTermEntry.getCommerceTermEntryId();
				name = commerceTermEntry.getName();
			}
		};
	}

	private Term _addPaymentTerm() throws Exception {
		CommerceTermEntry commerceTermEntry = _addTerm(
			CommerceTermEntryConstants.TYPE_PAYMENT_TERMS);

		_commerceOrder.setPaymentCommerceTermEntryId(
			commerceTermEntry.getCommerceTermEntryId());
		_commerceOrder.setPaymentCommerceTermEntryDescription(
			commerceTermEntry.getDescription());
		_commerceOrder.setPaymentCommerceTermEntryName(
			commerceTermEntry.getLabel());

		_commerceOrder = _commerceOrderLocalService.updateCommerceOrder(
			_commerceOrder);

		return new Term() {
			{
				description = commerceTermEntry.getDescription(null, true);
				externalReferenceCode =
					commerceTermEntry.getExternalReferenceCode();
				id = commerceTermEntry.getCommerceTermEntryId();
				name = commerceTermEntry.getName();
			}
		};
	}

	private CommerceTermEntry _addTerm(String type) throws Exception {
		Term term = randomTerm();
		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		return _commerceTermEntryLocalService.addCommerceTermEntry(
			term.getExternalReferenceCode(), _user.getUserId(), true,
			Collections.singletonMap(LocaleUtil.US, term.getDescription()),
			displayDateConfig.getMonth(), displayDateConfig.getDay(),
			displayDateConfig.getYear(), displayDateConfig.getHour(),
			displayDateConfig.getMinute(), expirationDateConfig.getMonth(),
			expirationDateConfig.getDay(), expirationDateConfig.getYear(),
			expirationDateConfig.getHour(), expirationDateConfig.getMinute(),
			true, Collections.singletonMap(LocaleUtil.US, term.getName()),
			term.getName(), RandomTestUtil.nextDouble(), type, StringPool.BLANK,
			_serviceContext);
	}

	@DeleteAfterTestRun
	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@DeleteAfterTestRun
	private Address _address;

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
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private CommercePaymentMethodGroupRel _commercePaymentMethodGroupRel;

	@Inject
	private CommercePaymentMethodGroupRelLocalService
		_commercePaymentMethodGroupRelLocalService;

	@Inject
	private CommercePaymentMethodGroupRelQualifierService
		_commercePaymentMethodGroupRelQualifierService;

	@DeleteAfterTestRun
	private CommerceShippingFixedOption _commerceShippingFixedOption;

	@Inject
	private CommerceShippingFixedOptionLocalService
		_commerceShippingFixedOptionLocalService;

	@Inject
	private CommerceShippingFixedOptionQualifierService
		_commerceShippingFixedOptionQualifierService;

	@DeleteAfterTestRun
	private CommerceShippingMethod _commerceShippingMethod;

	@Inject
	private CommerceTermEntryLocalService _commerceTermEntryLocalService;

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