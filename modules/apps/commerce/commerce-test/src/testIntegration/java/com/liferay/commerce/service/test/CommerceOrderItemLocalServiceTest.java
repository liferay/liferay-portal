/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.model.CommerceMoney;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.exception.CommerceOrderValidatorException;
import com.liferay.commerce.exception.ProductBundleException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.price.CommerceProductPrice;
import com.liferay.commerce.price.CommerceProductPriceCalculation;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.test.util.CommercePriceEntryTestUtil;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.constants.CPInstanceConstants;
import com.liferay.commerce.product.exception.CPDefinitionOptionRelException;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.option.CommerceOptionValue;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.test.util.CommerceProductTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.product.util.CPJSONUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceOrderItemLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());

		_commerceChannel = CommerceTestUtil.addCommerceChannel(
			_group.getGroupId(), _commerceCurrency.getCode());

		try {
			_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
				_user.getUserId(), _serviceContext);
		}
		catch (Exception exception) {
			_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
				_user.getUserId());
		}

		_commerceCatalog = _commerceCatalogLocalService.addCommerceCatalog(
			null, RandomTestUtil.randomString(), _commerceCurrency.getCode(),
			LocaleUtil.US.getDisplayLanguage(), _serviceContext);

		_commerceContext = new TestCommerceContext(
			null, _commerceCurrency, _commerceChannel, _user, _group, null);
	}

	@After
	public void tearDown() throws Exception {
		List<CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities =
				_commerceInventoryBookedQuantityLocalService.
					getCommerceInventoryBookedQuantities(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CommerceInventoryBookedQuantity commerceInventoryBookedQuantity :
				commerceInventoryBookedQuantities) {

			_commerceInventoryBookedQuantityLocalService.
				deleteCommerceInventoryBookedQuantity(
					commerceInventoryBookedQuantity);
		}

		for (CommerceOrderItem commerceOrderItem : _commerceOrderItems) {
			_commerceOrderItemLocalService.deleteCommerceOrderItem(
				_user.getUserId(), commerceOrderItem);
		}
	}

	@Test
	public void testAddCommerceOrderItem() throws Exception {
		frutillaRule.scenario(
			"Add a SKU (product instance) to an order"
		).given(
			"A group"
		).and(
			"A user"
		).and(
			"A published SKU"
		).when(
			"There is availability for the SKU"
		).then(
			"I should be able to add the SKU to an order"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(2), cpInstance.getSku(), StringPool.BLANK));

		Assert.assertNotNull(_commerceCurrency);

		Assert.assertNotNull(_accountEntry);

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 1, commerceOrderItems.size());

		CommerceOrderItem actualCommerceOrderItem = commerceOrderItems.get(0);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			actualCommerceOrderItem.getCommerceOrderItemId());
	}

	@Test
	public void testAddCommerceOrderItemUsesOrderCurrencyCode()
		throws Exception {

		frutillaRule.scenario(
			"Add a SKU (product instance) to an order"
		).given(
			"A group"
		).and(
			"A user"
		).and(
			"A published SKU"
		).when(
			"There is availability for the SKU"
		).then(
			"The order item added to the order should be in the same currency"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId(), BigDecimal.TEN);

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(2), cpInstance.getSku(), StringPool.BLANK));

		Assert.assertNotNull(_commerceCurrency);

		Assert.assertNotNull(_accountEntry);

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceCurrency commerceCurrency =
			CommerceCurrencyTestUtil.addCommerceCurrency(
				_commerceChannel.getCompanyId());

		_commerceCurrencies.add(commerceCurrency);

		commerceCurrency =
			_commerceCurrencyLocalService.updateCommerceCurrencyRate(
				commerceCurrency.getCommerceCurrencyId(), BigDecimal.TEN);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		CommerceContext commerceContext = new TestCommerceContext(
			_accountEntry, commerceCurrency, _commerceChannel, _user, _group,
			commerceOrder);

		CommerceProductPrice commerceProductPrice =
			_commerceProductPriceCalculation.getCommerceProductPrice(
				cpInstance.getCPInstanceId(), BigDecimal.ONE, StringPool.BLANK,
				commerceContext);

		CommerceMoney commerceMoney = commerceProductPrice.getFinalPrice();

		Assert.assertEquals(
			commerceMoney.getPrice(), commerceOrderItem.getFinalPrice());
	}

	@Test(expected = CommerceOrderValidatorException.class)
	public void testAddCommerceOrderItemWithDraftCPDefinition()
		throws Exception {

		frutillaRule.scenario(
			"Add a SKU (product instance) to an order"
		).given(
			"A group"
		).and(
			"A user"
		).and(
			"A SKU linked to an unpublished product"
		).when(
			"There is availability for the SKU"
		).then(
			"I should be able to add the SKU to an order"
		);

		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId(), false, true,
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance cpInstance = _cpInstanceLocalService.getCPInstance(
			cpDefinition.getCPDefinitionId(), CPInstanceConstants.DEFAULT_SKU);

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(2), cpInstance.getSku(), StringPool.BLANK));

		Assert.assertNotNull(_commerceCurrency);

		Assert.assertNotNull(_accountEntry);

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);
	}

	@Test(expected = CommerceOrderValidatorException.class)
	public void testAddCommerceOrderItemWithDraftCPInstance() throws Exception {
		frutillaRule.scenario(
			"Add a SKU (product instance) to an order"
		).given(
			"A group"
		).and(
			"A user"
		).and(
			"An unpublished SKU"
		).when(
			"There is availability for the SKU"
		).then(
			"I should not be able to add the SKU to an order"
		);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_cpInstances.add(cpInstance);

		_cpInstanceLocalService.updateStatus(
			_user.getUserId(), cpInstance.getCPInstanceId(),
			WorkflowConstants.STATUS_DRAFT);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(2), cpInstance.getSku(), StringPool.BLANK));

		Assert.assertNotNull(_commerceCurrency);

		Assert.assertNotNull(_accountEntry);

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);
	}

	@Test
	public void testAddOrUpdateCommerceOrderItem() throws Exception {
		frutillaRule.scenario(
			"Add multiple times a product with options"
		).given(
			"An empty order"
		).when(
			"I add the same CPInstance with option json"
		).then(
			"If the json contains the same option values the products are " +
				"merged."
		);

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), cpInstance.getSku(),
				StringPool.BLANK));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		String[] options1 = {
			"[{\"skuOptionKey\": \"quantity\", \"value\": \"12\"}]",
			"[{\"skuOptionKey\": \"quantity\", \"value\": \"12\"}]"
		};
		String[] options2 = {
			"[{\"skuOptionKey\": \"quantity\", \"value\": \"12\"}]",
			"[{\"test\": \"package-quantity\", \"value\": \"12\"}]"
		};

		for (int i = 0; i < options1.length; i++) {
			_commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), options1[i], BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

			List<CommerceOrderItem> commerceOrderItems =
				commerceOrder.getCommerceOrderItems();

			Assert.assertEquals(
				options1[i] + options2[i], 1, commerceOrderItems.size());

			_commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), options1[i], BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

			commerceOrderItems = commerceOrder.getCommerceOrderItems();

			Assert.assertEquals(
				options1[i] + options2[i], 1, commerceOrderItems.size());

			CommerceOrderItem commerceOrderItem = commerceOrderItems.get(0);

			Assert.assertTrue(
				options1[i] + options2[i],
				BigDecimalUtil.eq(
					BigDecimal.valueOf(2), commerceOrderItem.getQuantity()));

			_commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), options2[i], BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

			commerceOrderItems = commerceOrder.getCommerceOrderItems();

			Assert.assertEquals(
				options1[i] + options2[i], 1, commerceOrderItems.size());

			_commerceOrderItemLocalService.deleteCommerceOrderItems(
				_user.getUserId(), commerceOrder.getCommerceOrderId());
		}
	}

	@Test
	public void testAddProductBundleDynamicOptionLinkedToSKUAlreadyInOrder()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle that is linked to a cpInstance already " +
				"present in the order"
		).given(
			"An order with an orderItem containing a cpInstance"
		).when(
			"I add a bundle with an option value linked to the same cpInstance"
		).then(
			"The order is updated adding 2 orderItems. 1 for the bundle and " +
				"1 for the option. Price and quantities of orderItems shall " +
					"be updated accordingly."
		);

		_assertAddProductBundleLinkedToSKUAlreadyInOrder(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);
	}

	@Test
	public void testAddProductBundleStaticOptionLinkedToSKUAlreadyInOrder()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle that is linked to a cpInstance already " +
				"present in the order"
		).given(
			"An order with an orderItem containing a cpInstance"
		).when(
			"I add a bundle with an option value linked to the same cpInstance"
		).then(
			"The order is updated adding 2 orderItems. 1 for the bundle and " +
				"1 for the option. Price and quantities of orderItems shall " +
					"be updated accordingly."
		);

		_assertAddProductBundleLinkedToSKUAlreadyInOrder(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	@Test
	public void testAddProductBundleWithBackOrderAllowedChildSKU()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle to an order with child SKUs"
		).given(
			"A product bundle with 2 approved child SKUs"
		).and(
			"all child SKUs are unavailable but back orders are allowed"
		).when(
			"User adds product bundle to an order"
		).then(
			"Action should succeed"
		);

		_addProductBundleWithUnavailableChildSKU(true);
	}

	@Test
	public void testAddProductBundleWithDynamicOption() throws Exception {
		frutillaRule.scenario(
			"Add a product bundle with dynamic price option linked to a SKU " +
				"to an order"
		).given(
			"A catalog with 2 cpInstances"
		).and(
			"A product bundle with a dynamic-price option with values linked " +
				"to the cpInstances"
		).when(
			"I add the bundle to an order"
		).then(
			"I should have 2 orderItems in the order. 1 for the bundle and 1 " +
				"for the selected value of the option with the correct " +
					"quantities"
		);

		_addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);
	}

	@Test
	public void testAddProductBundleWithDynamicOptionWithSameSKU()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle with dynamic price option linked to a SKU " +
				"to an order"
		).given(
			"A catalog with 2 cpInstances"
		).and(
			"A product bundle with a dynamic-price option with values linked " +
				"to the same cpInstance"
		).when(
			"I add the bundle to an order"
		).then(
			"I should have 2 orderItems in the order. 1 for the bundle and 1 " +
				"for the selected value of the option with the correct " +
					"quantities"
		);

		_addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC, true);
	}

	@Test
	public void testAddProductBundleWithStaticOptionWithNoSKU()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle with static price option not linked to a " +
				"SKU to an order"
		).given(
			"A product bundle with a static-price option with values linked " +
				"to the cpInstances"
		).when(
			"I add the bundle to an order"
		).then(
			"I should have 1 orderItem in the order with final price as the " +
				"sum of bundle price and option price"
		);

		Assert.assertNotNull(_accountEntry);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		String option1Key = RandomTestUtil.randomString();
		BigDecimal option1Price = BigDecimal.valueOf(100);

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, option1Key, _toValueKey(option1Key), option1Price,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC,
				BigDecimal.valueOf(2)));

		String option2Key = FriendlyURLNormalizerUtil.normalize(
			RandomTestUtil.randomString());
		BigDecimal option2Price = BigDecimal.valueOf(200);
		BigDecimal option2Quantity = BigDecimal.valueOf(3);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				0, option2Key, _toValueKey(option2Key), option2Price,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, option2Quantity));

		CPDefinition bundleCPDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_addOptions(
			bundleCPDefinition, false, false,
			commerceOptionValues.subList(0, 1));
		_addOptions(
			bundleCPDefinition, true, true, commerceOptionValues.subList(1, 2));

		CPInstance bundleCPInstance = _buildProductBundleSingleOptionCPInstance(
			bundleCPDefinition.getCPDefinitionId(), _toValueKey(option2Key));

		_cpInstances.add(bundleCPInstance);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				bundleCPInstance.getGroupId());

		_commercePriceEntries.add(
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, bundleCPDefinition.getCProductId(),
				bundleCPInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), bundleCPInstance.getSku(),
				StringPool.BLANK));

		BigDecimal quantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				bundleCPInstance.getCPInstanceId(), null, quantity, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 1, commerceOrderItems.size());

		CommerceOrderItem bundleOrderItem = _getOrderItemByCPInstanceId(
			bundleCPInstance.getCPInstanceId(), false, commerceOrderItems);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			bundleOrderItem.getCommerceOrderItemId());

		Assert.assertTrue(
			BigDecimalUtil.eq(quantity, bundleOrderItem.getQuantity()));

		BigDecimal bundleOrderItemFinalPrice = bundleOrderItem.getFinalPrice();

		Assert.assertEquals(
			option2Price,
			BigDecimalUtil.stripTrailingZeros(bundleOrderItemFinalPrice));

		CommerceOrder retrievedOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

		BigDecimal retrievedOrderTotal = retrievedOrder.getTotal();

		Assert.assertEquals(
			BigDecimalUtil.stripTrailingZeros(bundleOrderItemFinalPrice),
			BigDecimalUtil.stripTrailingZeros(retrievedOrderTotal));
	}

	@Test
	public void testAddProductBundleWithStaticOptionWithSameSKU()
		throws Exception {

		frutillaRule.scenario(
			"Add a product bundle with static price option linked to a SKU " +
				"to an order"
		).given(
			"A catalog with 2 cpInstances"
		).and(
			"A product bundle with a static-price option with values linked " +
				"to the same cpInstance"
		).when(
			"I add the bundle to an order"
		).then(
			"I should have 2 orderItems in the order. 1 for the bundle and 1 " +
				"for the selected value of the option with the correct " +
					"quantities"
		);

		_addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, true);
	}

	@Test
	public void testAddProductBundleWithStaticOptionWithSKU() throws Exception {
		frutillaRule.scenario(
			"Add a product bundle with static price option linked to a SKU " +
				"to an order"
		).given(
			"A catalog with 2 cpInstances"
		).and(
			"A product bundle with a static-price option with values linked " +
				"to the cpInstances"
		).when(
			"I add the bundle to an order"
		).then(
			"I should have 2 orderItems in the order. 1 for the bundle and 1 " +
				"for the selected value of the option with the correct " +
					"quantities"
		);

		_addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);
	}

	@Test(expected = CommerceOrderValidatorException.class)
	public void testAddProductBundleWithUnavailableChildSKU() throws Exception {
		frutillaRule.scenario(
			"Add a product bundle to an order with one of the child SKUs " +
				"being unavailable"
		).given(
			"A product bundle with 2 child SKUs"
		).and(
			"all SKUs are in approved state"
		).but(
			"child SKUs are unavailable in the inventory"
		).when(
			"User adds product bundle to an order"
		).then(
			"Action should fail with appropriate exception"
		);

		_addProductBundleWithUnavailableChildSKU(false);
	}

	@Test
	public void testAddProductWithBundledOptionAndIncompleteJSON()
		throws Exception {

		CPDefinition cpDefinition =
			CPTestUtil.addCPDefinitionWithChildCPDefinitions(
				_commerceCatalog.getGroupId(), 1,
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		List<CPInstance> cpInstances = cpDefinition.getCPInstances();

		CPInstance cpInstance = cpInstances.get(0);

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), cpInstance.getSku(),
				StringPool.BLANK));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		List<CPDefinitionOptionRel> cpDefinitionOptionRels =
			cpDefinition.getCPDefinitionOptionRels();

		CPDefinitionOptionRel cpDefinitionOptionRel =
			cpDefinitionOptionRels.get(0);

		CPOption cpOption = cpDefinitionOptionRel.getCPOption();

		List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
			cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			cpDefinitionOptionValueRels.get(0);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(),
				JSONUtil.put(
					JSONUtil.put(
						"key", cpOption.getKey()
					).put(
						"price", "100"
					).put(
						"quantity", "50"
					).put(
						"skuOptionKey", cpOption.getKey()
					).put(
						"skuOptionName", cpOption.getName()
					).put(
						"skuOptionValueKey", cpDefinitionOptionValueRel.getKey()
					).put(
						"skuOptionValueNames",
						JSONFactoryUtil.createJSONArray(
							Collections.singletonList(
								cpDefinitionOptionValueRel.getName()))
					).put(
						"value", cpDefinitionOptionValueRel.getKey()
					)
				).toString(),
				cpDefinitionOptionValueRel.getQuantity(), 0, BigDecimal.ZERO,
				StringPool.BLANK, _commerceContext, _serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 2, commerceOrderItems.size());

		CommerceOrderItem bundleCommerceOrderItem = _getOrderItemByCPInstanceId(
			cpInstance.getCPInstanceId(), false, commerceOrderItems);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			bundleCommerceOrderItem.getCommerceOrderItemId());

		Assert.assertEquals(
			commerceOrderItem.getSku(), bundleCommerceOrderItem.getSku());

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
			bundleCommerceOrderItem.getJson());

		JSONObject jsonObject = jsonArray.getJSONObject(0);

		Assert.assertEquals(
			jsonObject.getString("key"), cpDefinitionOptionRel.getKey());
		Assert.assertEquals("0", jsonObject.getString("price"));
		Assert.assertEquals(
			jsonObject.getString("priceType"),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		BigDecimal quantity = cpDefinitionOptionValueRel.getQuantity();

		Assert.assertEquals(
			jsonObject.getString("quantity"),
			String.valueOf(quantity.stripTrailingZeros()));

		CPInstance linkedCPInstance = _cpInstanceLocalService.fetchCPInstance(
			cpDefinitionOptionValueRel.getCProductId(),
			cpDefinitionOptionValueRel.getCPInstanceUuid());

		Assert.assertEquals(
			jsonObject.getString("skuId"),
			String.valueOf(linkedCPInstance.getCPInstanceId()));

		Assert.assertEquals(
			jsonObject.getString("skuOptionKey"),
			cpDefinitionOptionRel.getKey());
		Assert.assertEquals(
			jsonObject.getString("skuOptionName"),
			cpDefinitionOptionRel.getName(
				cpDefinitionOptionRel.getDefaultLanguageId()));
		Assert.assertEquals(
			jsonObject.getString("skuOptionValueNames"),
			JSONUtil.put(
				cpDefinitionOptionValueRel.getName(
					cpDefinitionOptionValueRel.getDefaultLanguageId())
			).toString());
		Assert.assertEquals(
			jsonObject.getString("value"),
			JSONUtil.put(
				cpDefinitionOptionValueRel.getKey()
			).toString());
	}

	@Test(expected = CPDefinitionOptionRelException.class)
	public void testAddProductWithInvalidOption() throws Exception {
		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_commerceCatalog.getGroupId());

		CPOption cpOption = _cpOptionLocalService.addCPOption(
			null, _user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			CPConstants.PRODUCT_OPTION_NUMERIC_KEY,
			RandomTestUtil.randomBoolean(), true, false,
			RandomTestUtil.randomString(), _serviceContext);

		CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpInstance.getCPDefinitionId(),
			cpOption.getCPOptionId());

		CPTestUtil.addCPDefinitionOptionValueRel(
			cpInstance.getCPDefinitionId(), cpOption.getCPOptionId(), "9", "9",
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, cpOption.isRequired(),
			cpOption.isSkuContributor(), _serviceContext);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), cpInstance.getSku(),
				StringPool.BLANK));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		_commerceOrderItemLocalService.addCommerceOrderItem(
			_user.getUserId(), commerceOrder.getCommerceOrderId(),
			cpInstance.getCPInstanceId(),
			JSONUtil.put(
				"key", cpOption.getKey()
			).put(
				"value", RandomTestUtil.randomString()
			).toString(),
			BigDecimal.ONE, 0, BigDecimal.ZERO, StringPool.BLANK,
			_commerceContext, _serviceContext);
	}

	@Test(expected = CPDefinitionOptionRelException.class)
	public void testAddProductWithMissingRequiredOption() throws Exception {
		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			_commerceCatalog.getGroupId());

		CPOption cpOption = _cpOptionLocalService.addCPOption(
			null, _user.getUserId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			CPConstants.PRODUCT_OPTION_SELECT_KEY,
			RandomTestUtil.randomBoolean(), true, false,
			RandomTestUtil.randomString(), _serviceContext);

		CPTestUtil.addCPDefinitionOptionRel(
			_commerceCatalog.getGroupId(), cpInstance.getCPDefinitionId(),
			cpOption.getCPOptionId());

		CPTestUtil.addCPDefinitionOptionValueRel(
			cpInstance.getCPDefinitionId(), cpOption.getCPOptionId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC, cpOption.isRequired(),
			cpOption.isSkuContributor(), _serviceContext);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), cpInstance.getSku(),
				StringPool.BLANK));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		_commerceOrderItemLocalService.addCommerceOrderItem(
			_user.getUserId(), commerceOrder.getCommerceOrderId(),
			cpInstance.getCPInstanceId(), StringPool.BLANK, BigDecimal.ONE, 0,
			BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
			_serviceContext);
	}

	@Test
	public void testCRUDCPInstanceLinkedByProductBundle() throws Exception {
		frutillaRule.scenario(
			"Add multiple times a product that is also linked to an option " +
				"in a product bundle already in the order and then delete it"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I add the same CPinstance that is linked to an option the bundle"
		).then(
			"Another order item shall be created with the correct quantity. " +
				"Bundle related items are not modified, even after deletion " +
					"of the new order item"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);

		BigDecimal originalQuantity1 = commerceOrderItem1.getQuantity();

		CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

		BigDecimal originalQuantity2 = commerceOrderItem2.getQuantity();

		CPInstance cpInstance;

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			cpInstance = commerceOrderItem2.fetchCPInstance();
		}
		else {
			cpInstance = commerceOrderItem1.fetchCPInstance();
		}

		_commerceOrderItems.add(
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext));

		commerceOrderItems = commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.size(), 3, commerceOrderItems.size());

		CommerceOrderItem commerceOrderItem3 =
			_commerceOrderItemLocalService.addOrUpdateCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), "[]", BigDecimal.ONE, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		commerceOrderItems = commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.size(), 3, commerceOrderItems.size());

		Assert.assertEquals(
			originalQuantity1, commerceOrderItem1.getQuantity());
		Assert.assertEquals(
			originalQuantity2, commerceOrderItem2.getQuantity());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.valueOf(2), commerceOrderItem3.getQuantity()));

		_commerceOrderItemLocalService.deleteCommerceOrderItem(
			_user.getUserId(), commerceOrderItem3.getCommerceOrderItemId());

		commerceOrderItems = commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.size(), 2, commerceOrderItems.size());

		Assert.assertEquals(
			originalQuantity1, commerceOrderItem1.getQuantity());
		Assert.assertEquals(
			originalQuantity2, commerceOrderItem2.getQuantity());
	}

	@Test(expected = ProductBundleException.class)
	public void testDeleteChildOrderItemProductBundle() throws Exception {
		frutillaRule.scenario(
			"Deleting a child order item of a product bundle is not allowed"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I delete a child order item"
		).then(
			"An exception shall be raised"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

			_assertDeleteOrderItem(commerceOrderItem2);
		}
		else {
			_assertDeleteOrderItem(commerceOrderItem1);
		}
	}

	@Test
	public void testDeleteProductBundleWithOptionWithSKU() throws Exception {
		frutillaRule.scenario(
			"Delete a product bundle with an option linked to a SKU from an " +
				"order"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I delete the bundle"
		).then(
			"The bundle order item and the child order item should be deleted"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			_assertDeleteOrderItem(commerceOrderItem1);
		}
		else {
			CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

			_assertDeleteOrderItem(commerceOrderItem2);
		}
	}

	@Test
	public void testImportCommerceOrderItem() throws Exception {
		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CPInstance cpInstance = CPTestUtil.addCPInstance(_group.getGroupId());

		_cpInstances.add(cpInstance);

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(2), cpInstance.getSku(), StringPool.BLANK));

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		CPOption cpOption = _cpOptionLocalService.addCPOption(
			null, _user.getUserId(),
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), "PCB Reference"
			).build(),
			RandomTestUtil.randomLocaleStringMap(), "text", false, false, false,
			"pcb-reference", _serviceContext);

		_cpOptions.add(cpOption);

		CPDefinitionOptionRel cpDefinitionOptionRel =
			CPTestUtil.addCPDefinitionOptionRel(
				_commerceCatalog.getGroupId(), cpDefinition.getCPDefinitionId(),
				cpOption.getCPOptionId());

		_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		String json = CPJSONUtil.toJSONArray(
			JSONUtil.put(
				"key", "pcb-reference"
			).put(
				"skuOptionKey", "pcb-reference"
			).put(
				"skuOptionName", "PCB Reference"
			).put(
				"value", new String[] {"PCB XYZ"}
			).toString()
		).toString();

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.importCommerceOrderItem(
				_user.getUserId(), null, 0, commerceOrder.getCommerceOrderId(),
				cpInstance.getCPInstanceId(), null, json, BigDecimal.ONE,
				BigDecimal.ONE, null, null, _serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		Assert.assertEquals(json, commerceOrderItem.getJson());
	}

	@Test(expected = ProductBundleException.class)
	public void testUpdateChildOrderItemProductBundle() throws Exception {
		frutillaRule.scenario(
			"Update the product quantity of a child order item of a product " +
				"bundle is not allowed"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I change the quantity of the child order item"
		).then(
			"An exception shall be raised"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);
		CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			_assertUpdateOrderItem(3, commerceOrderItem2, commerceOrderItem1);
		}
		else {
			_assertUpdateOrderItem(3, commerceOrderItem1, commerceOrderItem2);
		}
	}

	@Test
	public void testUpdateProductBundleWithDynamicOptionWithSKU()
		throws Exception {

		frutillaRule.scenario(
			"Update the product quantity of a product bundle with an option " +
				"linked to a SKU to an order"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I change the quantity of the bundle"
		).then(
			"The quantity of the bundle should be update and the quantities " +
				"of the child order items shall be updated according to the " +
					"option set up"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);
		CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			_assertUpdateOrderItem(3, commerceOrderItem1, commerceOrderItem2);
		}
		else {
			_assertUpdateOrderItem(3, commerceOrderItem2, commerceOrderItem1);
		}
	}

	@Test
	public void testUpdateProductBundleWithStaticOptionWithSKU()
		throws Exception {

		frutillaRule.scenario(
			"Update the product quantity of a product bundle with an option " +
				"linked to a SKU to an order"
		).given(
			"An order with a product bundle (2 orderItems)"
		).when(
			"I change the quantity of the bundle"
		).then(
			"The quantity of the bundle should be update and the quantities " +
				"of the child order items shall be updated according to the " +
					"option set up"
		);

		CommerceOrder commerceOrder = _addProductBundleWithOptionLinkedToSKU(
			CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC);

		List<CommerceOrderItem> commerceOrderItems =
			_commerceOrderItemLocalService.getCommerceOrderItems(
				commerceOrder.getCommerceOrderId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		CommerceOrderItem commerceOrderItem1 = commerceOrderItems.get(0);
		CommerceOrderItem commerceOrderItem2 = commerceOrderItems.get(1);

		if (commerceOrderItem1.getParentCommerceOrderItemId() == 0) {
			_assertUpdateOrderItem(3, commerceOrderItem1, commerceOrderItem2);
		}
		else {
			_assertUpdateOrderItem(3, commerceOrderItem2, commerceOrderItem1);
		}
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private void _addOptions(
			CPDefinition cpDefinition, boolean linkToProduct,
			boolean skuContributor,
			List<CommerceOptionValue> commerceOptionValues)
		throws Exception {

		for (CommerceOptionValue commerceOptionValue : commerceOptionValues) {
			CPOption cpOption = _cpOptionLocalService.addCPOption(
				null, _serviceContext.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				CPTestUtil.getDefaultCommerceOptionTypeKey(skuContributor),
				RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
				skuContributor, commerceOptionValue.getOptionKey(),
				_serviceContext);

			_cpOptions.add(cpOption);

			CPOptionValue cpOptionValue =
				_cpOptionValueLocalService.addCPOptionValue(
					cpOption.getCPOptionId(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomDouble(),
					commerceOptionValue.getOptionValueKey(), _serviceContext);

			_cpOptionValues.add(cpOptionValue);

			CPDefinitionOptionRel cpDefinitionOptionRel =
				_cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
					cpDefinition.getCPDefinitionId(), cpOption.getCPOptionId(),
					cpOption.getNameMap(), cpOption.getDescriptionMap(),
					cpOption.getCommerceOptionTypeKey(), 0.0, false, false,
					cpOption.isSkuContributor(), true,
					commerceOptionValue.getPriceType(), _serviceContext);

			_cpDefinitionOptionRels.add(cpDefinitionOptionRel);

			if (!linkToProduct) {
				continue;
			}

			List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
				_cpDefinitionOptionValueRelLocalService.
					getCPDefinitionOptionValueRels(
						cpDefinitionOptionRel.getCPDefinitionOptionRelId());

			CPDefinitionOptionValueRel optionCPDefinitionOptionValueRel =
				cpDefinitionOptionValueRels.get(0);

			_cpDefinitionOptionValueRelLocalService.
				updateCPDefinitionOptionValueRel(
					optionCPDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId(),
					commerceOptionValue.getCPInstanceId(),
					commerceOptionValue.getOptionValueKey(),
					cpOptionValue.getNameMap(), false,
					commerceOptionValue.getPrice(), cpOptionValue.getPriority(),
					commerceOptionValue.getQuantity(), StringPool.BLANK,
					_serviceContext);
		}
	}

	private CommerceOrder _addProductBundleWithOptionLinkedToSKU(
			String priceType)
		throws Exception {

		return _addProductBundleWithOptionLinkedToSKU(priceType, false);
	}

	private CommerceOrder _addProductBundleWithOptionLinkedToSKU(
			String priceType, boolean useSameCPInstanceForOptions)
		throws Exception {

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		BigDecimal option1Price = BigDecimal.valueOf(100);

		CPInstance optionSKU1 =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		_cpInstances.add(optionSKU1);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				optionSKU1.getGroupId());

		CPDefinition option1CPDefinition = optionSKU1.getCPDefinition();

		CommercePriceEntry optionSKU1PriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, option1CPDefinition.getCProductId(),
				optionSKU1.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), option1Price);

		_commercePriceEntries.add(optionSKU1PriceEntry);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.valueOf(100), optionSKU1.getSku(), StringPool.BLANK);

		BigDecimal option2Price = BigDecimal.valueOf(200);

		CPInstance optionSKU2 =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		_cpInstances.add(optionSKU2);

		CPDefinition option2CPDefinition = optionSKU2.getCPDefinition();

		CommercePriceEntry optionSKU2PriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, option2CPDefinition.getCProductId(),
				optionSKU2.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), option2Price);

		_commercePriceEntries.add(optionSKU2PriceEntry);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.valueOf(100), optionSKU2.getSku(), StringPool.BLANK);

		Assert.assertNotNull(_commerceCurrency);

		Assert.assertNotNull(_accountEntry);

		CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		String option1Key = FriendlyURLNormalizerUtil.normalize(
			RandomTestUtil.randomString());

		BigDecimal option1DeltaPrice = BigDecimal.ZERO;
		BigDecimal option2DeltaPrice = BigDecimal.ZERO;

		if (priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC)) {
			option1DeltaPrice = option1Price;
			option2DeltaPrice = option2Price;
		}

		if (useSameCPInstanceForOptions) {
			optionSKU2 = optionSKU1;
		}

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				optionSKU1.getCPInstanceId(), option1Key,
				_toValueKey(option1Key), option1DeltaPrice, priceType,
				BigDecimal.valueOf(2)));

		String option2Key = FriendlyURLNormalizerUtil.normalize(
			FriendlyURLNormalizerUtil.normalize(RandomTestUtil.randomString()));
		BigDecimal option2Quantity = BigDecimal.valueOf(3);

		CommerceOptionValue testCommerceOptionValue =
			CommerceProductTestUtil.getCommerceOptionValue(
				optionSKU2.getCPInstanceId(), option2Key,
				_toValueKey(option2Key), option2DeltaPrice, priceType,
				option2Quantity);

		commerceOptionValues.add(testCommerceOptionValue);

		CPDefinition bundleCPDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_addOptions(
			bundleCPDefinition, false, false,
			commerceOptionValues.subList(0, 1));
		_addOptions(
			bundleCPDefinition, true, true, commerceOptionValues.subList(1, 2));

		CPInstance bundleCPInstance = _buildProductBundleSingleOptionCPInstance(
			bundleCPDefinition.getCPDefinitionId(), _toValueKey(option2Key));

		CommercePriceEntryTestUtil.addCommercePriceEntry(
			StringPool.BLANK, bundleCPDefinition.getCProductId(),
			bundleCPInstance.getCPInstanceUuid(),
			commercePriceList.getCommercePriceListId(), BigDecimal.ZERO);

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_user.getUserId(), _commerceInventoryWarehouse,
			BigDecimal.valueOf(100), bundleCPInstance.getSku(),
			StringPool.BLANK);

		BigDecimal quantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				bundleCPInstance.getCPInstanceId(),
				"[" + testCommerceOptionValue.toJSON() + "]", quantity, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		_commerceOrderItems.add(commerceOrderItem);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 2, commerceOrderItems.size());

		CommerceOrderItem bundleOrderItem = _getOrderItemByCPInstanceId(
			bundleCPInstance.getCPInstanceId(), false, commerceOrderItems);

		CommerceOrderItem optionOrderItem = _getOrderItemByCPInstanceId(
			optionSKU2.getCPInstanceId(), true, commerceOrderItems);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			bundleOrderItem.getCommerceOrderItemId());

		Assert.assertEquals(quantity, bundleOrderItem.getQuantity());

		Assert.assertEquals(option2Quantity, optionOrderItem.getQuantity());

		BigDecimal expectedOrderFinalPrice = option2Price;

		if (priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC)) {
			expectedOrderFinalPrice = optionSKU2PriceEntry.getPrice();

			if (useSameCPInstanceForOptions) {
				expectedOrderFinalPrice = optionSKU1PriceEntry.getPrice();
			}
		}

		expectedOrderFinalPrice = expectedOrderFinalPrice.multiply(
			option2Quantity);

		expectedOrderFinalPrice = expectedOrderFinalPrice.add(
			bundleOrderItem.getFinalPrice());

		CommerceOrder retrievedOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

		Assert.assertEquals(expectedOrderFinalPrice, retrievedOrder.getTotal());

		return retrievedOrder;
	}

	private void _addProductBundleWithUnavailableChildSKU(
			boolean backOrderAllowed)
		throws Exception {

		CPDefinition bundleCPDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			false);

		CPInstance cpInstance1 = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId(), BigDecimal.valueOf(20),
			"cpInstance1SKU");

		_cpInstances.add(cpInstance1);

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.ZERO,
				cpInstance1.getSku(), StringPool.BLANK));

		if (backOrderAllowed) {
			CommerceTestUtil.updateBackOrderCPDefinitionInventory(
				cpInstance1.getCPDefinition());
		}

		CPInstance cpInstance2 = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId(), BigDecimal.valueOf(30),
			"cpInstance2SKU");

		_cpInstances.add(cpInstance2);

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.ONE,
				cpInstance2.getSku(), StringPool.BLANK));

		CPOption dynamicPriceTypeCPOption = CPTestUtil.addCPOption(
			_commerceCatalog.getGroupId(),
			CPTestUtil.getDefaultCommerceOptionTypeKey(true), true);

		_cpOptions.add(dynamicPriceTypeCPOption);

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			CPTestUtil.addCPDefinitionOptionValueRelWithPrice(
				_commerceCatalog.getGroupId(),
				bundleCPDefinition.getCPDefinitionId(),
				cpInstance1.getCPInstanceId(),
				dynamicPriceTypeCPOption.getCPOptionId(),
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
				BigDecimal.valueOf(50), BigDecimal.ONE, true, true,
				_serviceContext);

		_cpDefinitionOptionValueRels.add(cpDefinitionOptionValueRel);

		_cpDefinitionOptionValueRels.add(
			CPTestUtil.addCPDefinitionOptionValueRelWithPrice(
				_commerceCatalog.getGroupId(),
				bundleCPDefinition.getCPDefinitionId(),
				cpInstance2.getCPInstanceId(),
				dynamicPriceTypeCPOption.getCPOptionId(),
				CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC,
				BigDecimal.valueOf(100), BigDecimal.ONE, true, true,
				_serviceContext));

		_cpInstanceLocalService.buildCPInstances(
			bundleCPDefinition.getCPDefinitionId(), _serviceContext);

		List<CPInstance> bundleCPInstances =
			bundleCPDefinition.getCPInstances();

		Assert.assertEquals(
			bundleCPInstances.toString(), 2, bundleCPInstances.size());

		CPInstance bundleCPInstanceWithUnavailableChildSKU =
			_getBundleCPInstanceWithUnavailableChildSKU(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId(),
				bundleCPInstances, cpInstance1);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				bundleCPInstanceWithUnavailableChildSKU.getGroupId());

		_commercePriceEntries.add(
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, bundleCPDefinition.getCProductId(),
				bundleCPInstanceWithUnavailableChildSKU.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse, BigDecimal.ONE,
				bundleCPInstanceWithUnavailableChildSKU.getSku(),
				StringPool.BLANK));

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		_commerceOrderItems.add(
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				bundleCPInstanceWithUnavailableChildSKU.getCPInstanceId(), null,
				BigDecimal.ONE, 0, BigDecimal.ONE, StringPool.BLANK,
				_commerceContext, _serviceContext));

		commerceOrder = _commerceOrderLocalService.getCommerceOrder(
			commerceOrder.getCommerceOrderId());

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 2, commerceOrderItems.size());

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (commerceOrderItem.hasParentCommerceOrderItem()) {
				Assert.assertEquals(
					cpInstance1.getCPInstanceId(),
					commerceOrderItem.getCPInstanceId());
			}
			else {
				Assert.assertEquals(
					bundleCPInstanceWithUnavailableChildSKU.getCPInstanceId(),
					commerceOrderItem.getCPInstanceId());
			}
		}
	}

	private CommerceOrder _assertAddProductBundleLinkedToSKUAlreadyInOrder(
			String priceType)
		throws Exception {

		_commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		CPInstance optionSKU1 =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		_cpInstances.add(optionSKU1);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.fetchCatalogBaseCommercePriceList(
				optionSKU1.getGroupId());

		CPDefinition option1CPDefinition = optionSKU1.getCPDefinition();

		CommercePriceEntry optionSKU1PriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, option1CPDefinition.getCProductId(),
				optionSKU1.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(),
				BigDecimal.valueOf(111));

		_commercePriceEntries.add(optionSKU1PriceEntry);

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), optionSKU1.getSku(),
				StringPool.BLANK));

		_commerceChannelRel = CommerceTestUtil.addWarehouseCommerceChannelRel(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId());

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		BigDecimal nonbundleQuantity = BigDecimal.TEN;

		_commerceOrderItems.add(
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				optionSKU1.getCPInstanceId(), null, nonbundleQuantity, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext));

		BigDecimal option1Price = BigDecimal.valueOf(100);
		BigDecimal option2Price = BigDecimal.valueOf(200);

		CPInstance optionSKU2 =
			CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
				_commerceCatalog.getGroupId());

		_cpInstances.add(optionSKU2);

		CPDefinition option2CPDefinition = optionSKU2.getCPDefinition();

		_commercePriceEntries.add(
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, option2CPDefinition.getCProductId(),
				optionSKU2.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), option2Price));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), optionSKU2.getSku(),
				StringPool.BLANK));

		String option1Key = FriendlyURLNormalizerUtil.normalize(
			RandomTestUtil.randomString());

		BigDecimal option1DeltaPrice = BigDecimal.ZERO;
		BigDecimal option2DeltaPrice = BigDecimal.ZERO;

		if (priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_STATIC)) {
			option1DeltaPrice = option1Price;
			option2DeltaPrice = option2Price;
		}

		List<CommerceOptionValue> commerceOptionValues = new ArrayList<>();
		BigDecimal option1Quantity = BigDecimal.valueOf(2);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				optionSKU1.getCPInstanceId(), option1Key,
				_toValueKey(option1Key), option1DeltaPrice, priceType,
				option1Quantity));

		String option2Key = FriendlyURLNormalizerUtil.normalize(
			RandomTestUtil.randomString());
		BigDecimal option2Quantity = BigDecimal.valueOf(3);

		commerceOptionValues.add(
			CommerceProductTestUtil.getCommerceOptionValue(
				optionSKU2.getCPInstanceId(), option2Key,
				_toValueKey(option2Key), option2DeltaPrice, priceType,
				option2Quantity));

		CPDefinition bundleCPDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, true,
			true);

		_addOptions(
			bundleCPDefinition, true, true, commerceOptionValues.subList(0, 1));
		_addOptions(
			bundleCPDefinition, false, false,
			commerceOptionValues.subList(1, 2));

		CPInstance bundleCPInstance = _buildProductBundleSingleOptionCPInstance(
			bundleCPDefinition.getCPDefinitionId(), _toValueKey(option1Key));

		_cpInstances.add(bundleCPInstance);

		_commercePriceEntries.add(
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				StringPool.BLANK, bundleCPDefinition.getCProductId(),
				bundleCPInstance.getCPInstanceUuid(),
				commercePriceList.getCommercePriceListId(), BigDecimal.ZERO));

		_commerceInventoryWarehouseItems.add(
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), _commerceInventoryWarehouse,
				BigDecimal.valueOf(100), bundleCPInstance.getSku(),
				StringPool.BLANK));

		BigDecimal quantity = BigDecimal.ONE;

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				bundleCPInstance.getCPInstanceId(), null, quantity, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		List<CommerceOrderItem> commerceOrderItems =
			commerceOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 3, commerceOrderItems.size());

		CommerceOrderItem nonbundleOrderItem = _getOrderItemByCPInstanceId(
			optionSKU1.getCPInstanceId(), false, commerceOrderItems);

		CommerceOrderItem bundleOrderItem = _getOrderItemByCPInstanceId(
			bundleCPInstance.getCPInstanceId(), false, commerceOrderItems);

		CommerceOrderItem optionOrderItem = _getOrderItemByCPInstanceId(
			optionSKU1.getCPInstanceId(), true, commerceOrderItems);

		Assert.assertEquals(
			commerceOrderItem.getCommerceOrderItemId(),
			bundleOrderItem.getCommerceOrderItemId());

		Assert.assertEquals(quantity, bundleOrderItem.getQuantity());

		Assert.assertEquals(option1Quantity, optionOrderItem.getQuantity());

		Assert.assertEquals(
			nonbundleQuantity, nonbundleOrderItem.getQuantity());

		BigDecimal expectedOrderFinalPrice = option1Price;

		if (priceType.equals(CPConstants.PRODUCT_OPTION_PRICE_TYPE_DYNAMIC)) {
			expectedOrderFinalPrice = optionSKU1PriceEntry.getPrice();
		}

		expectedOrderFinalPrice = expectedOrderFinalPrice.multiply(
			option1Quantity);

		expectedOrderFinalPrice = expectedOrderFinalPrice.add(
			bundleOrderItem.getFinalPrice());

		BigDecimal nonbundleFinalPrice = optionSKU1PriceEntry.getPrice();

		nonbundleFinalPrice = nonbundleFinalPrice.multiply(nonbundleQuantity);

		expectedOrderFinalPrice = expectedOrderFinalPrice.add(
			nonbundleFinalPrice);

		CommerceOrder retrievedOrder =
			_commerceOrderLocalService.getCommerceOrder(
				commerceOrder.getCommerceOrderId());

		Assert.assertEquals(expectedOrderFinalPrice, retrievedOrder.getTotal());

		return commerceOrder;
	}

	private void _assertDeleteOrderItem(CommerceOrderItem bundleOrderItem)
		throws Exception {

		long commerceOrderId = bundleOrderItem.getCommerceOrderId();

		_commerceOrderItemLocalService.deleteCommerceOrderItem(
			_user.getUserId(), bundleOrderItem);

		CommerceOrder retrieveOrder =
			_commerceOrderLocalService.getCommerceOrder(commerceOrderId);

		List<CommerceOrderItem> commerceOrderItems =
			retrieveOrder.getCommerceOrderItems();

		Assert.assertEquals(
			commerceOrderItems.toString(), 0, commerceOrderItems.size());
	}

	private void _assertUpdateOrderItem(
			int factor, CommerceOrderItem bundleCommerceOrderItem,
			CommerceOrderItem childCommerceOrderItem)
		throws Exception {

		BigDecimal originalBundleQuantity =
			bundleCommerceOrderItem.getQuantity();

		BigDecimal quantity = originalBundleQuantity.multiply(
			BigDecimal.valueOf(factor));

		bundleCommerceOrderItem =
			_commerceOrderItemLocalService.updateCommerceOrderItem(
				_user.getUserId(),
				bundleCommerceOrderItem.getCommerceOrderItemId(), quantity,
				_commerceContext, _serviceContext);

		Assert.assertEquals(quantity, bundleCommerceOrderItem.getQuantity());

		BigDecimal originalChildQuantity = childCommerceOrderItem.getQuantity();

		CommerceOrderItem updatedChildOrderItem =
			_commerceOrderItemLocalService.getCommerceOrderItem(
				childCommerceOrderItem.getCommerceOrderItemId());

		quantity = originalChildQuantity.multiply(BigDecimal.valueOf(factor));

		Assert.assertEquals(quantity, updatedChildOrderItem.getQuantity());

		BigDecimal originalBundlePrice =
			bundleCommerceOrderItem.getFinalPrice();

		BigDecimal expectedBundlePrice = originalBundlePrice.multiply(
			BigDecimal.valueOf(factor));

		Assert.assertEquals(
			expectedBundlePrice, bundleCommerceOrderItem.getFinalPrice());

		BigDecimal originalChildPrice = childCommerceOrderItem.getFinalPrice();

		BigDecimal expectedChildPrice = originalChildPrice.multiply(
			BigDecimal.valueOf(factor));

		Assert.assertEquals(
			expectedChildPrice, updatedChildOrderItem.getFinalPrice());
	}

	private CPInstance _buildProductBundleSingleOptionCPInstance(
			long cpDefinitionId, String key)
		throws Exception {

		_cpInstanceLocalService.buildCPInstances(
			cpDefinitionId, _serviceContext);

		List<CPInstance> bundleCPDefinitionApprovedCPInstances =
			_cpInstanceLocalService.getCPDefinitionApprovedCPInstances(
				cpDefinitionId);

		CPInstance cpInstance = null;

		for (CPInstance bundleCPInstance :
				bundleCPDefinitionApprovedCPInstances) {

			Map<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
				cpInstanceCPDefinitionOptionRelsMap =
					_cpInstanceHelper.getCPInstanceCPDefinitionOptionRelsMap(
						bundleCPInstance.getCPInstanceId());

			for (Map.Entry
					<CPDefinitionOptionRel, List<CPDefinitionOptionValueRel>>
						cpDefinitionOptionRel1 :
							cpInstanceCPDefinitionOptionRelsMap.entrySet()) {

				List<CPDefinitionOptionValueRel> cpDefinitionOptionValueRels =
					cpDefinitionOptionRel1.getValue();

				CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
					cpDefinitionOptionValueRels.get(0);

				String cpDefinitionOptionValueRelKey =
					cpDefinitionOptionValueRel.getKey();

				if (StringUtil.equalsIgnoreCase(
						cpDefinitionOptionValueRelKey, key)) {

					cpInstance = bundleCPInstance;
				}
			}
		}

		Assert.assertNotNull(
			"Instance with option value key " + key, cpInstance);

		return cpInstance;
	}

	private CPInstance _getBundleCPInstanceWithUnavailableChildSKU(
			long cpDefinitionOptionRelId, List<CPInstance> bundleCPInstances,
			CPInstance unavailableCPInstance)
		throws Exception {

		for (CPInstance bundleCPInstance : bundleCPInstances) {
			CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
				_cpDefinitionOptionValueRelLocalService.
					getCPInstanceCPDefinitionOptionValueRel(
						cpDefinitionOptionRelId,
						bundleCPInstance.getCPInstanceId());

			if (Objects.equals(
					cpDefinitionOptionValueRel.getCPInstanceUuid(),
					unavailableCPInstance.getCPInstanceUuid())) {

				return bundleCPInstance;
			}
		}

		return null;
	}

	private CommerceOrderItem _getOrderItemByCPInstanceId(
		long cpInstanceId, boolean insideBundle,
		List<CommerceOrderItem> commerceOrderItems) {

		for (CommerceOrderItem commerceOrderItem : commerceOrderItems) {
			if (commerceOrderItem.getCPInstanceId() == cpInstanceId) {
				if (insideBundle) {
					if (commerceOrderItem.getParentCommerceOrderItemId() != 0) {
						return commerceOrderItem;
					}
				}
				else {
					return commerceOrderItem;
				}
			}
		}

		return null;
	}

	private String _toValueKey(String optionKey) {
		return "value-key-for-" + optionKey;
	}

	private AccountEntry _accountEntry;
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceChannel _commerceChannel;

	@DeleteAfterTestRun
	private CommerceChannelRel _commerceChannelRel;

	private CommerceContext _commerceContext;

	@DeleteAfterTestRun
	private List<CommerceCurrency> _commerceCurrencies = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@DeleteAfterTestRun
	private List<CommerceInventoryWarehouseItem>
		_commerceInventoryWarehouseItems = new ArrayList<>();

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	private final List<CommerceOrderItem> _commerceOrderItems =
		new ArrayList<>();

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private List<CommerceOrder> _commerceOrders = new ArrayList<>();

	@DeleteAfterTestRun
	private List<CommercePriceEntry> _commercePriceEntries = new ArrayList<>();

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceProductPriceCalculation _commerceProductPriceCalculation;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@DeleteAfterTestRun
	private List<CPDefinitionOptionRel> _cpDefinitionOptionRels =
		new ArrayList<>();

	@Inject
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@DeleteAfterTestRun
	private List<CPDefinitionOptionValueRel> _cpDefinitionOptionValueRels =
		new ArrayList<>();

	@Inject
	private CPInstanceHelper _cpInstanceHelper;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@DeleteAfterTestRun
	private List<CPInstance> _cpInstances = new ArrayList<>();

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private List<CPOption> _cpOptions = new ArrayList<>();

	@Inject
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@DeleteAfterTestRun
	private List<CPOptionValue> _cpOptionValues = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}