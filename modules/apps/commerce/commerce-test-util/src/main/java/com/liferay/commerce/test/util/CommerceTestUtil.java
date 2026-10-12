/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.test.util;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.constants.CommerceAddressConstants;
import com.liferay.commerce.constants.CommerceConstants;
import com.liferay.commerce.constants.CommerceShipmentConstants;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalServiceUtil;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.model.CPDefinitionInventory;
import com.liferay.commerce.model.CommerceAddress;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.CommercePaymentMethodGroupRelLocalServiceUtil;
import com.liferay.commerce.payment.test.util.TestCommercePaymentMethod;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.CPDefinitionLocalServiceUtil;
import com.liferay.commerce.product.service.CPInstanceLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelLocalServiceUtil;
import com.liferay.commerce.product.service.CommerceChannelRelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CPDefinitionInventoryLocalServiceUtil;
import com.liferay.commerce.service.CommerceAddressLocalServiceUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalServiceUtil;
import com.liferay.commerce.service.CommerceOrderLocalServiceUtil;
import com.liferay.commerce.service.CommerceShippingMethodLocalServiceUtil;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionLocalServiceUtil;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.service.CommerceTaxFixedRateLocalServiceUtil;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.CommerceTaxMethodLocalServiceUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.GroupConfigurationTemporarySwapper;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.model.Region;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.RegionLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Andrea Di Giorgi
 * @author Luca Pellizzon
 */
public class CommerceTestUtil {

	public static AccountEntry addAccount(long groupId, long userId)
		throws Exception {

		return CommerceAccountTestUtil.addPersonAccountEntry(
			userId, ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static CommerceOrder addB2BCommerceOrder(
			long groupId, long userId, long commerceAccountId,
			long commerceCurrencyId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		if (userId == 0) {
			userId = serviceContext.getUserId();
		}

		long commerceChannelGroupId =
			CommerceChannelLocalServiceUtil.
				getCommerceChannelGroupIdBySiteGroupId(groupId);

		CommerceCurrency commerceCurrency =
			CommerceCurrencyLocalServiceUtil.getCommerceCurrency(
				commerceCurrencyId);

		return CommerceOrderLocalServiceUtil.addCommerceOrder(
			userId, commerceChannelGroupId, commerceAccountId,
			commerceCurrency.getCode(), 0);
	}

	public static CommerceOrder addB2CCommerceOrder(
			long userId, long groupId, CommerceCurrency commerceCurrency)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		if (userId == 0) {
			userId = serviceContext.getUserId();
		}

		AccountEntry accountEntry =
			CommerceAccountTestUtil.getPersonAccountEntry(userId);

		return CommerceOrderLocalServiceUtil.addCommerceOrder(
			userId, groupId, accountEntry.getAccountEntryId(),
			commerceCurrency.getCode(), 0);
	}

	public static CommerceOrder addB2CCommerceOrder(
			long userId, long groupId, long commerceCurrencyId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		if (userId == 0) {
			userId = serviceContext.getUserId();
		}

		AccountEntry accountEntry;

		try {
			accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
				userId, serviceContext);
		}
		catch (Exception exception) {
			accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
				userId);
		}

		CommerceCurrency commerceCurrency =
			CommerceCurrencyLocalServiceUtil.getCommerceCurrency(
				commerceCurrencyId);

		return CommerceOrderLocalServiceUtil.addCommerceOrder(
			userId, groupId, accountEntry.getAccountEntryId(),
			commerceCurrency.getCode(), 0);
	}

	public static CommerceOrder addCheckoutDetailsToCommerceOrder(
			CommerceOrder commerceOrder, long userId,
			boolean paymentSubscription)
		throws Exception {

		return addCheckoutDetailsToCommerceOrder(
			commerceOrder, userId, paymentSubscription, false,
			RandomTestUtil.randomDouble());
	}

	public static CommerceOrder addCheckoutDetailsToCommerceOrder(
			CommerceOrder commerceOrder, long userId,
			boolean paymentSubscription, boolean deliverySubscription)
		throws Exception {

		return addCheckoutDetailsToCommerceOrder(
			commerceOrder, userId, paymentSubscription, deliverySubscription,
			RandomTestUtil.randomDouble());
	}

	public static CommerceOrder addCheckoutDetailsToCommerceOrder(
			CommerceOrder commerceOrder, long userId,
			boolean paymentSubscription, boolean deliverySubscription,
			double cpInstancePrice)
		throws Exception {

		long groupId = commerceOrder.getGroupId();

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			groupId, BigDecimal.valueOf(cpInstancePrice));

		if (paymentSubscription) {
			cpInstance.setOverrideSubscriptionInfo(true);
			cpInstance.setSubscriptionEnabled(true);
			cpInstance.setSubscriptionLength(1);
			cpInstance.setSubscriptionType(CPConstants.DAILY_SUBSCRIPTION_TYPE);
			cpInstance.setMaxSubscriptionCycles(2);
		}

		if (deliverySubscription) {
			cpInstance.setOverrideSubscriptionInfo(true);
			cpInstance.setDeliverySubscriptionEnabled(true);
			cpInstance.setDeliverySubscriptionLength(1);
			cpInstance.setDeliverySubscriptionType(
				CPConstants.DAILY_SUBSCRIPTION_TYPE);
			cpInstance.setDeliveryMaxSubscriptionCycles(2);
		}

		cpInstance = CPInstanceLocalServiceUtil.updateCPInstance(cpInstance);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(groupId));

		CommerceChannel commerceChannel =
			CommerceChannelLocalServiceUtil.getCommerceChannelByOrderGroupId(
				commerceOrder.getGroupId());

		addWarehouseCommerceChannelRel(
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			commerceChannel.getCommerceChannelId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			userId, commerceInventoryWarehouse, BigDecimal.TEN,
			cpInstance.getSku(), StringPool.BLANK);

		addCommerceOrderItem(
			commerceOrder.getCommerceOrderId(), cpInstance.getCPInstanceId(),
			BigDecimal.valueOf(4));

		CommerceAddress billingCommerceAddress = addUserCommerceAddress(
			groupId, userId);
		CommerceAddress shippingCommerceAddress = addUserCommerceAddress(
			groupId, userId);

		commerceOrder = CommerceOrderLocalServiceUtil.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		commerceOrder.setBillingAddressId(
			billingCommerceAddress.getCommerceAddressId());
		commerceOrder.setShippingAddressId(
			shippingCommerceAddress.getCommerceAddressId());

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel(
				userId, commerceChannel.getGroupId());

		commerceOrder.setCommercePaymentMethodKey(
			commercePaymentMethodGroupRel.getPaymentIntegrationKey());

		CommerceShippingMethod commerceShippingMethod =
			addCommerceShippingMethod(userId, commerceChannel.getGroupId());

		commerceOrder.setCommerceShippingMethodId(
			commerceShippingMethod.getCommerceShippingMethodId());

		CommerceShippingFixedOption commerceShippingFixedOption =
			addCommerceShippingFixedOption(commerceShippingMethod);

		commerceOrder.setShippingAmount(
			commerceShippingFixedOption.getAmount());
		commerceOrder.setShippingOptionName(
			commerceShippingFixedOption.getName());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		cpDefinition.setCPTaxCategoryId(
			CommerceTaxTestUtil.addTaxCategoryId(groupId));

		cpDefinition = CPDefinitionLocalServiceUtil.updateCPDefinition(
			cpDefinition);

		addCommerceTaxFixedRate(
			userId, commerceOrder.getGroupId(),
			cpDefinition.getCPTaxCategoryId(), false, true);

		return CommerceOrderLocalServiceUtil.updateCommerceOrder(commerceOrder);
	}

	public static CommerceCatalog addCommerceCatalog(
			long companyId, long groupId, long userId,
			String commerceCurrencyCode)
		throws Exception {

		return CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), commerceCurrencyCode,
			LocaleUtil.toLanguageId(LocaleUtil.US),
			ServiceContextTestUtil.getServiceContext(
				companyId, groupId, userId));
	}

	public static CommerceChannel addCommerceChannel(
			long groupId, String commerceCurrencyCode)
		throws Exception {

		return CommerceChannelLocalServiceUtil.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			groupId, RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrencyCode,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static CommerceChannel addCommerceChannel(
			String commerceCurrencyCode)
		throws Exception {

		return CommerceChannelLocalServiceUtil.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null,
			commerceCurrencyCode, ServiceContextTestUtil.getServiceContext());
	}

	public static CommerceChannelRel addCommerceChannelRel(
			long groupId, long commerceChannelId, long warehouseId)
		throws Exception {

		return CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			RandomTestUtil.randomString(), warehouseId, commerceChannelId,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	public static CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, BigDecimal quantity)
		throws Exception {

		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.getCommerceOrder(commerceOrderId);

		if (commerceOrder.getCommerceCurrency() == null) {
			CommerceCurrency commerceCurrency =
				CommerceCurrencyTestUtil.addCommerceCurrency(
					commerceOrder.getCompanyId());

			commerceOrder.setCommerceCurrencyCode(commerceCurrency.getCode());

			commerceOrder = CommerceOrderLocalServiceUtil.updateCommerceOrder(
				commerceOrder);
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), null, null,
			serviceContext.getScopeGroup(), commerceOrder);

		return addCommerceOrderItem(
			commerceOrderId, cpInstanceId, quantity, commerceContext);
	}

	public static CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, BigDecimal quantity,
			CommerceContext commerceContext)
		throws Exception {

		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.getCommerceOrder(commerceOrderId);

		return CommerceOrderItemLocalServiceUtil.addCommerceOrderItem(
			commerceOrder.getUserId(), commerceOrderId, cpInstanceId, null,
			quantity, 0, BigDecimal.ZERO, StringPool.BLANK, commerceContext,
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId()));
	}

	public static CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, BigDecimal quantity,
			String unitOfMeasureKey)
		throws Exception {

		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.getCommerceOrder(commerceOrderId);

		if (commerceOrder.getCommerceCurrency() == null) {
			CommerceCurrency commerceCurrency =
				CommerceCurrencyTestUtil.addCommerceCurrency(
					commerceOrder.getCompanyId());

			commerceOrder.setCommerceCurrencyCode(commerceCurrency.getCode());

			commerceOrder = CommerceOrderLocalServiceUtil.updateCommerceOrder(
				commerceOrder);
		}

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId());

		CommerceContext commerceContext = new TestCommerceContext(
			null, commerceOrder.getCommerceCurrency(), null, null,
			serviceContext.getScopeGroup(), commerceOrder);

		return addCommerceOrderItem(
			commerceOrderId, cpInstanceId, quantity, unitOfMeasureKey,
			commerceContext);
	}

	public static CommerceOrderItem addCommerceOrderItem(
			long commerceOrderId, long cpInstanceId, BigDecimal quantity,
			String unitOfMeasureKey, CommerceContext commerceContext)
		throws Exception {

		CommerceOrder commerceOrder =
			CommerceOrderLocalServiceUtil.getCommerceOrder(commerceOrderId);

		return CommerceOrderItemLocalServiceUtil.addCommerceOrderItem(
			commerceOrder.getUserId(), commerceOrderId, cpInstanceId, null,
			quantity, 0, BigDecimal.ZERO, unitOfMeasureKey, commerceContext,
			ServiceContextTestUtil.getServiceContext(
				commerceOrder.getGroupId()));
	}

	public static CommerceOrder addCommerceOrderShippingDetails(
			CommerceOrder commerceOrder, BigDecimal value)
		throws Exception {

		int orderStatusIndex = RandomTestUtil.randomInt(
			0, CommerceShipmentConstants.ALLOWED_ORDER_STATUSES.length - 1);

		int orderStatus =
			CommerceShipmentConstants.ALLOWED_ORDER_STATUSES[orderStatusIndex];

		commerceOrder.setOrderStatus(orderStatus);

		CommerceAddress billingCommerceAddress = addUserCommerceAddress(
			commerceOrder.getGroupId(), commerceOrder.getUserId());
		CommerceAddress shippingCommerceAddress = addUserCommerceAddress(
			commerceOrder.getGroupId(), commerceOrder.getUserId());

		commerceOrder.setBillingAddressId(
			billingCommerceAddress.getCommerceAddressId());
		commerceOrder.setShippingAddressId(
			shippingCommerceAddress.getCommerceAddressId());

		CommerceShippingMethod commerceShippingMethod =
			addFixedRateCommerceShippingMethod(
				commerceOrder.getUserId(), commerceOrder.getGroupId(), value);

		commerceOrder.setCommerceShippingMethodId(
			commerceShippingMethod.getCommerceShippingMethodId());

		CommerceShippingFixedOption commerceShippingFixedOption =
			addCommerceShippingFixedOption(commerceShippingMethod, value);

		commerceOrder.setShippingAmount(
			commerceShippingFixedOption.getAmount());
		commerceOrder.setShippingOptionName(
			commerceShippingFixedOption.getNameCurrentValue());

		return CommerceOrderLocalServiceUtil.updateCommerceOrder(commerceOrder);
	}

	public static CommercePaymentMethodGroupRel
			addCommercePaymentMethodGroupRel(long userId, long groupId)
		throws Exception {

		Map<Locale, String> nameMap = Collections.singletonMap(
			LocaleUtil.US, "Test Payment Method Group Rel");

		return CommercePaymentMethodGroupRelLocalServiceUtil.
			addCommercePaymentMethodGroupRel(
				userId, groupId, nameMap, null, true, null,
				TestCommercePaymentMethod.KEY, 1, null);
	}

	public static CommerceShippingFixedOption addCommerceShippingFixedOption(
			CommerceShippingMethod commerceShippingMethod)
		throws Exception {

		BigDecimal value = BigDecimal.valueOf(RandomTestUtil.randomDouble());

		return addCommerceShippingFixedOption(commerceShippingMethod, value);
	}

	public static CommerceShippingFixedOption addCommerceShippingFixedOption(
			CommerceShippingMethod commerceShippingMethod, BigDecimal value)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				commerceShippingMethod.getGroupId());

		return CommerceShippingFixedOptionLocalServiceUtil.
			addCommerceShippingFixedOption(
				serviceContext.getUserId(), serviceContext.getScopeGroupId(),
				commerceShippingMethod.getCommerceShippingMethodId(), value,
				RandomTestUtil.randomLocaleStringMap(), null,
				RandomTestUtil.randomLocaleStringMap(), 1);
	}

	public static CommerceShippingMethod addCommerceShippingMethod(
			long userId, long groupId)
		throws Exception {

		return CommerceShippingMethodLocalServiceUtil.addCommerceShippingMethod(
			userId, groupId, RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(), true, "fixed", null, 1,
			RandomTestUtil.randomString());
	}

	public static CommerceShippingMethod addCommerceShippingMethod(
			long userId, long groupId, Map<Locale, String> shippingMethodName,
			Map<Locale, String> shippingMethodDescription, boolean active,
			String engineKey)
		throws Exception {

		return CommerceShippingMethodLocalServiceUtil.addCommerceShippingMethod(
			userId, groupId, shippingMethodName, shippingMethodDescription,
			active, engineKey, null, 1, RandomTestUtil.randomString());
	}

	public static CommerceTaxFixedRate addCommerceTaxFixedRate(
			long userId, long groupId, long cpTaxCategoryId, boolean percentage,
			boolean active)
		throws PortalException {

		CommerceTaxMethod commerceTaxMethod =
			CommerceTaxMethodLocalServiceUtil.addCommerceTaxMethod(
				userId, groupId, RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(), "fixed-tax", percentage,
				active);

		return CommerceTaxFixedRateLocalServiceUtil.addCommerceTaxFixedRate(
			userId, commerceTaxMethod.getGroupId(),
			commerceTaxMethod.getCommerceTaxMethodId(), cpTaxCategoryId,
			RandomTestUtil.randomDouble());
	}

	public static CommerceShippingMethod addFixedRateCommerceShippingMethod(
			long userId, long groupId, BigDecimal value)
		throws Exception {

		CommerceShippingMethod commerceShippingMethod =
			addCommerceShippingMethod(userId, groupId);

		addCommerceShippingFixedOption(commerceShippingMethod, value);

		return commerceShippingMethod;
	}

	public static CommerceAddress addUserCommerceAddress(
			long groupId, long userId)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(groupId);

		Country country = _setUpCountry(serviceContext);

		Region region = _setUpRegion(country, serviceContext);

		return CommerceAddressLocalServiceUtil.addCommerceAddress(
			StringPool.BLANK, User.class.getName(), userId,
			country.getCountryId(), region.getRegionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), StringPool.BLANK,
			CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING,
			String.valueOf(30133), serviceContext);
	}

	public static CommerceChannelRel addWarehouseCommerceChannelRel(
			long warehouseId, long commerceChannelId)
		throws Exception {

		CommerceChannel commerceChannel =
			CommerceChannelLocalServiceUtil.getCommerceChannel(
				commerceChannelId);

		return CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(), warehouseId,
			commerceChannelId,
			ServiceContextTestUtil.getServiceContext(
				commerceChannel.getGroupId()));
	}

	public static CommerceOrder createCommerceOrderForShipping(
			long userId, long groupId, long commerceCurrencyId,
			long cpInstanceId, BigDecimal amount, BigDecimal quantity,
			int commerceOrderItemQuantity)
		throws Exception {

		CommerceOrder commerceOrder = addB2CCommerceOrder(
			userId, groupId, commerceCurrencyId);

		for (int i = 0; i < commerceOrderItemQuantity; i++) {
			addCommerceOrderItem(
				commerceOrder.getCommerceOrderId(), cpInstanceId, quantity);
		}

		int orderStatusIndex = RandomTestUtil.randomInt(
			0, CommerceShipmentConstants.ALLOWED_ORDER_STATUSES.length - 1);

		int orderStatus =
			CommerceShipmentConstants.ALLOWED_ORDER_STATUSES[orderStatusIndex];

		commerceOrder.setOrderStatus(orderStatus);

		commerceOrder = CommerceOrderLocalServiceUtil.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		CommerceAddress billingCommerceAddress = addUserCommerceAddress(
			groupId, userId);
		CommerceAddress shippingCommerceAddress = addUserCommerceAddress(
			groupId, userId);

		commerceOrder.setBillingAddressId(
			billingCommerceAddress.getCommerceAddressId());
		commerceOrder.setShippingAddressId(
			shippingCommerceAddress.getCommerceAddressId());

		CommerceShippingMethod commerceShippingMethod =
			addFixedRateCommerceShippingMethod(userId, groupId, amount);

		commerceOrder.setCommerceShippingMethodId(
			commerceShippingMethod.getCommerceShippingMethodId());

		CommerceShippingFixedOption commerceShippingFixedOption =
			addCommerceShippingFixedOption(commerceShippingMethod, amount);

		commerceOrder.setShippingAmount(
			commerceShippingFixedOption.getAmount());
		commerceOrder.setShippingOptionName(
			commerceShippingFixedOption.getNameCurrentValue());

		return CommerceOrderLocalServiceUtil.updateCommerceOrder(commerceOrder);
	}

	public static void runWithGuestBrowsingDisabled(
			long groupId, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				groupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT,
						HashMapDictionaryBuilder.<String, Object>put(
							"guestBrowsingEnabled", false
						).build())) {

			unsafeRunnable.run();
		}
	}

	public static void runWithGuestCheckoutDisabledOnB2BChannel(
			long groupId, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try (GroupConfigurationTemporarySwapper
				guestCheckoutGroupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						groupId, CommerceConstants.SERVICE_NAME_COMMERCE_ORDER,
						HashMapDictionaryBuilder.<String, Object>put(
							"guestCheckoutEnabled", false
						).build());
			GroupConfigurationTemporarySwapper
				siteTypeGroupConfigurationTemporarySwapper =
					new GroupConfigurationTemporarySwapper(
						groupId,
						CommerceConstants.SERVICE_NAME_COMMERCE_ACCOUNT,
						HashMapDictionaryBuilder.<String, Object>put(
							"commerceSiteType",
							CommerceChannelConstants.SITE_TYPE_B2B
						).build())) {

			unsafeRunnable.run();
		}
	}

	public static CPDefinitionInventory updateBackOrderCPDefinitionInventory(
			CPDefinition cpDefinition)
		throws PortalException {

		CPDefinitionInventory cpDefinitionInventory =
			CPDefinitionInventoryLocalServiceUtil.
				fetchCPDefinitionInventoryByCPDefinitionId(
					cpDefinition.getCPDefinitionId());

		if (cpDefinitionInventory != null) {
			cpDefinitionInventory.setBackOrders(true);

			cpDefinitionInventory =
				CPDefinitionInventoryLocalServiceUtil.
					updateCPDefinitionInventory(cpDefinitionInventory);
		}

		return cpDefinitionInventory;
	}

	private static Country _setUpCountry(ServiceContext serviceContext)
		throws Exception {

		Country country = CountryLocalServiceUtil.fetchCountryByNumber(
			serviceContext.getCompanyId(), "000");

		if (country == null) {
			country = CountryLocalServiceUtil.addCountry(
				null, "ZZ", "ZZZ", true, true, null,
				RandomTestUtil.randomString(), "000",
				RandomTestUtil.randomDouble(), true, false, false,
				serviceContext);
		}

		return country;
	}

	private static Region _setUpRegion(
			Country country, ServiceContext serviceContext)
		throws Exception {

		Region region = RegionLocalServiceUtil.fetchRegion(
			country.getCountryId(), "ZZ");

		if (region != null) {
			return region;
		}

		return RegionLocalServiceUtil.addRegion(
			null, country.getCountryId(), true, RandomTestUtil.randomString(),
			RandomTestUtil.randomDouble(), RandomTestUtil.randomString(),
			serviceContext);
	}

}