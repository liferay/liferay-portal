/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.engine.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.model.AccountGroup;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.account.test.util.CommerceAccountTestUtil;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.inventory.constants.CommerceInventoryConstants;
import com.liferay.commerce.inventory.engine.CommerceInventoryEngine;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseException;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryBookedQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryAudit;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryAuditLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryBookedQuantityLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseRelLocalService;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditType;
import com.liferay.commerce.inventory.type.CommerceInventoryAuditTypeRegistry;
import com.liferay.commerce.inventory.type.constants.CommerceInventoryAuditTypeConstants;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceChannelLocalService;
import com.liferay.commerce.product.service.CommerceChannelRelLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.service.CommerceOrderItemLocalService;
import com.liferay.commerce.service.CommerceOrderLocalService;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.commerce.test.util.context.TestCommerceContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
public class CommerceInventoryEngineTest {

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
			_group.getCompanyId(), _group.getGroupId(), _user.getUserId());

		try {
			_accountEntry = CommerceAccountTestUtil.addPersonAccountEntry(
				_user.getUserId(), _serviceContext);
		}
		catch (Exception exception) {
			_accountEntry = CommerceAccountTestUtil.getPersonAccountEntry(
				_user.getUserId());
		}

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			_group.getCompanyId());
		_commerceChannel = _commerceChannelLocalService.addCommerceChannel(
			StringPool.BLANK, AccountConstants.ACCOUNT_ENTRY_ID_DEFAULT,
			_group.getGroupId(),
			_group.getName(_serviceContext.getLanguageId()) + " Portal",
			CommerceChannelConstants.CHANNEL_TYPE_SITE, null, StringPool.BLANK,
			_serviceContext);

		_commerceContext = new TestCommerceContext(
			null, _commerceCurrency, _commerceChannel, _user, _group, null);

		_cpInstance1 = CommerceInventoryTestUtil.addRandomCPInstanceSku(
			_group.getGroupId());
		_cpInstance2 = CommerceInventoryTestUtil.addRandomCPInstanceSku(
			_group.getGroupId());
	}

	@After
	public void tearDown() throws Exception {
		List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(_group.getCompanyId());

		for (CommerceInventoryWarehouse commerceInventoryWarehouse :
				commerceInventoryWarehouses) {

			_commerceInventoryWarehouseLocalService.
				deleteCommerceInventoryWarehouse(commerceInventoryWarehouse);
		}
	}

	@Test(expected = DuplicateCommerceInventoryWarehouseItemException.class)
	public void testAddMultipleItemsWithSameSkuToWarehouse() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to add multiple items with same SKU"
		).given(
			"1 active warehouse"
		).when(
			"The same SKU is added twice to the same warehouse"
		).then(
			"An exception is raised"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouseActive.
					getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouseActive.
					getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);
	}

	@Test
	public void testBookedQuantityFromMultipleWarehouses() throws Exception {
		frutillaRule.scenario(
			"When the same warehouse item is added to 2 active warehouse the " +
				"maximum bookable quantity is equal to the sum of the stock " +
					"in both warehouses"
		).given(
			"2 active warehouses"
		).and(
			"A product sku in different quantities added to the warehouses"
		).when(
			"I retrieve the stock quantity after a booking quantity is added"
		).then(
			"The stock quantity shall take into consideration only the " +
				"remaining stock"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		CommerceInventoryWarehouse commerceInventoryWarehouse2 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		BigDecimal warehouse1ItemQuantity = BigDecimal.valueOf(5);
		BigDecimal warehouse2ItemQuantity = BigDecimal.valueOf(5);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				warehouse1ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse2.getCommerceInventoryWarehouseId(),
				warehouse2ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		BigDecimal companyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				warehouse1ItemQuantity.add(warehouse2ItemQuantity),
				companyStockQuantity));

		BigDecimal bookedQuantity = new BigDecimal(7);

		_commerceInventoryBookedQuantityLocalService.
			addCommerceInventoryBookedQuantity(
				_user.getUserId(), null, bookedQuantity, _cpInstance1.getSku(),
				StringPool.BLANK, Collections.emptyMap());

		BigDecimal remainingCompanyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				companyStockQuantity.subtract(bookedQuantity),
				remainingCompanyStockQuantity));
	}

	@Test
	public void testConsumeBookedQuantityDoesNotRestock() throws Exception {
		frutillaRule.scenario(
			"Shipping an order consumes its booked quantity"
		).given(
			"A booked quantity against the full available stock"
		).when(
			"The booked quantity is consumed from the warehouse"
		).then(
			"The on order quantity is zero and the available stock stays zero"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.valueOf(3),
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_commerceInventoryBookedQuantityLocalService.
				addCommerceInventoryBookedQuantity(
					_user.getUserId(), null, BigDecimal.valueOf(3),
					_cpInstance1.getSku(), StringPool.BLANK,
					Collections.emptyMap());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			_cpInstance1.getGroupId(),
			_commerceInventoryWarehouseItem.getCommerceInventoryWarehouseId(),
			BigDecimal.valueOf(3), _cpInstance1.getSku(), StringPool.BLANK,
			Collections.emptyMap());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryBookedQuantityLocalService.
					getCommerceInventoryBookedQuantity(
						_group.getCompanyId(), _cpInstance1.getSku(),
						StringPool.BLANK)));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testConsumeBookedQuantityDoesNotRestockWithUnitOfMeasure()
		throws Exception {

		frutillaRule.scenario(
			"Shipping an order consumes its booked unit of measure quantity"
		).given(
			"A booked unit of measure quantity against the full available stock"
		).when(
			"The booked quantity is consumed from the warehouse"
		).then(
			"The on order quantity is zero and the available stock stays zero"
		);

		String unitOfMeasureKey = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance1.getCPInstanceId(),
			unitOfMeasureKey, BigDecimal.ONE, _cpInstance1.getSku());

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.valueOf(3),
				_cpInstance1.getSku(), unitOfMeasureKey, _serviceContext);

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_commerceInventoryBookedQuantityLocalService.
				addCommerceInventoryBookedQuantity(
					_user.getUserId(), null, BigDecimal.valueOf(3),
					_cpInstance1.getSku(), unitOfMeasureKey,
					Collections.emptyMap());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), unitOfMeasureKey)));

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			_cpInstance1.getGroupId(),
			_commerceInventoryWarehouseItem.getCommerceInventoryWarehouseId(),
			BigDecimal.valueOf(3), _cpInstance1.getSku(), unitOfMeasureKey,
			Collections.emptyMap());

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryBookedQuantityLocalService.
					getCommerceInventoryBookedQuantity(
						_group.getCompanyId(), _cpInstance1.getSku(),
						unitOfMeasureKey)));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), unitOfMeasureKey)));
	}

	@Test
	public void testConsumeBookedQuantityFromMultipleWarehouses()
		throws Exception {

		frutillaRule.scenario(
			"When the same warehouse item is added to 2 active warehouse the " +
				"maximum bookable quantity is equal to the sum of the stock " +
					"in both warehouses"
		).given(
			"A channel"
		).and(
			"2 active warehouses"
		).and(
			"A product sku in different quantities added to the warehouses"
		).when(
			"I retrieve the stock quantity after a booking quantity is added"
		).then(
			"The stock quantity shall take into consideration only the " +
				"remaining stock"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		CommerceInventoryWarehouse commerceInventoryWarehouse2 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		BigDecimal warehouse1ItemQuantity = BigDecimal.valueOf(5);
		BigDecimal warehouse2ItemQuantity = BigDecimal.valueOf(5);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				warehouse1ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse2.getCommerceInventoryWarehouseId(),
				warehouse2ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		BigDecimal companyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				warehouse1ItemQuantity.add(warehouse2ItemQuantity),
				companyStockQuantity));

		BigDecimal bookedQuantity = new BigDecimal(12);

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_commerceInventoryBookedQuantityLocalService.
				addCommerceInventoryBookedQuantity(
					_user.getUserId(), null, bookedQuantity,
					_cpInstance1.getSku(), StringPool.BLANK,
					Collections.emptyMap());

		BigDecimal consumedQuantity = BigDecimal.ZERO;

		BigDecimal quantity = BigDecimal.valueOf(3);

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			_cpInstance1.getGroupId(),
			commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
			quantity, _cpInstance1.getSku(), StringPool.BLANK,
			Collections.emptyMap());

		BigDecimal remainingCompanyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		consumedQuantity = consumedQuantity.add(quantity);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				companyStockQuantity.subtract(bookedQuantity),
				remainingCompanyStockQuantity));

		quantity = BigDecimal.TEN;

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			_cpInstance1.getGroupId(),
			commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
			quantity, _cpInstance1.getSku(), StringPool.BLANK,
			Collections.emptyMap());

		remainingCompanyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		consumedQuantity = consumedQuantity.add(quantity);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				companyStockQuantity.subtract(consumedQuantity),
				remainingCompanyStockQuantity));
	}

	@Test(expected = NoSuchInventoryBookedQuantityException.class)
	public void testConsumeQuantity() throws Exception {
		frutillaRule.scenario(
			"When the booked quantity is consumed also the DB record shall " +
				"be deleted"
		).given(
			"1 warehouse item"
		).and(
			"Some booked quantity of that item"
		).when(
			"The quantity is consumed"
		).then(
			"The booked quantity record shall not be present"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		BigDecimal bookQuantity = new BigDecimal(5);

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		_commerceOrders.add(commerceOrder);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				_cpInstance1.getCPInstanceId(), null, bookQuantity, 0,
				BigDecimal.ZERO, StringPool.BLANK, _commerceContext,
				_serviceContext);

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_commerceInventoryBookedQuantityLocalService.
				addCommerceInventoryBookedQuantity(
					_user.getUserId(), null, bookQuantity,
					_cpInstance1.getSku(), StringPool.BLANK,
					Collections.emptyMap());

		commerceOrderItem =
			_commerceOrderItemLocalService.updateCommerceOrderItem(
				commerceOrderItem.getCommerceOrderItemId(),
				commerceInventoryBookedQuantity.
					getCommerceInventoryBookedQuantityId());

		BigDecimal quantity = _commerceInventoryWarehouseItem.getQuantity();

		Assert.assertTrue(
			BigDecimalUtil.eq(
				quantity.subtract(bookQuantity),
				_commerceInventoryEngine.getStockQuantity(
					commerceOrderItem.getCompanyId(),
					_accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			_cpInstance1.getGroupId(),
			_commerceInventoryWarehouseItem.getCommerceInventoryWarehouseId(),
			bookQuantity, _cpInstance1.getSku(), StringPool.BLANK,
			Collections.emptyMap());

		quantity = _commerceInventoryWarehouseItem.getQuantity();

		Assert.assertTrue(
			BigDecimalUtil.eq(
				quantity.subtract(bookQuantity),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));

		_commerceInventoryBookedQuantityLocalService.
			getCommerceInventoryBookedQuantity(
				commerceInventoryBookedQuantity.
					getCommerceInventoryBookedQuantityId());
	}

	@Test
	public void testConsumeQuantityFromMultipleWarehouses() throws Exception {
		frutillaRule.scenario(
			"When the same warehouse item is added to 2 active warehouse the " +
				"maximum consumable quantity is equal to the sum of the " +
					"stock in both warehouses"
		).given(
			"2 active warehouses"
		).and(
			"A product sku in different quantities added to the warehouses"
		).when(
			"I retrieve the stock quantity after a consuming quantity is added"
		).then(
			"The stock quantity shall take into consideration only the " +
				"remaining stock"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		CommerceInventoryWarehouse commerceInventoryWarehouse2 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				_serviceContext);

		BigDecimal warehouse1ItemQuantity = BigDecimal.valueOf(5);
		BigDecimal warehouse2ItemQuantity = BigDecimal.valueOf(5);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				warehouse1ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse2.getCommerceInventoryWarehouseId(),
				warehouse2ItemQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		BigDecimal companyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				warehouse1ItemQuantity.add(warehouse2ItemQuantity),
				companyStockQuantity));

		BigDecimal quantity = BigDecimal.valueOf(7);

		_commerceInventoryEngine.consumeQuantity(
			_user.getUserId(), 0, _cpInstance1.getGroupId(),
			commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
			quantity, _cpInstance1.getSku(), StringPool.BLANK,
			Collections.emptyMap());

		BigDecimal remainingCompanyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				warehouse1ItemQuantity.add(
					warehouse2ItemQuantity.subtract(quantity)),
				remainingCompanyStockQuantity));
	}

	@Test(expected = DuplicateCommerceInventoryWarehouseException.class)
	public void testCreateMultipleWarehousesWithSameAttributes()
		throws Exception {

		frutillaRule.scenario(
			"It should not be possible to create multiple warehouses with " +
				"same attributes"
		).given(
			"One warehouse is created with external reference"
		).when(
			"Another warehouse with same attributes is created"
		).then(
			"An exception shall be raised"
		);

		Map<Locale, String> name = RandomTestUtil.randomLocaleStringMap();

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.
				addCommerceInventoryWarehouseWithExternalReferenceCode(
					_user.getGroupId(), name);

		_commerceInventoryWarehouseLocalService.addCommerceInventoryWarehouse(
			commerceInventoryWarehouse.getExternalReferenceCode(),
			commerceInventoryWarehouse.getNameMap(),
			commerceInventoryWarehouse.getDescriptionMap(),
			commerceInventoryWarehouse.isActive(),
			commerceInventoryWarehouse.getStreet1(),
			commerceInventoryWarehouse.getStreet2(),
			commerceInventoryWarehouse.getStreet3(),
			commerceInventoryWarehouse.getCity(),
			commerceInventoryWarehouse.getZip(),
			commerceInventoryWarehouse.getCommerceRegionCode(),
			commerceInventoryWarehouse.getCountryTwoLettersISOCode(),
			commerceInventoryWarehouse.getLatitude(),
			commerceInventoryWarehouse.getLongitude(),
			ServiceContextTestUtil.getServiceContext(_user.getGroupId()));
	}

	@Test
	public void testCreateWarehouse() throws Exception {
		frutillaRule.scenario(
			"It should be possible to create a warehouse"
		).given(
			"One warehouse is created"
		).when(
			"The list of warehouses is retrieved for the company"
		).then(
			"The retrieved warehouse name is equal to the created one"
		);

		Map<Locale, String> name = RandomTestUtil.randomLocaleStringMap();

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.
				addCommerceInventoryWarehouseWithExternalReferenceCode(
					_user.getGroupId(), name);

		Assert.assertEquals(name, commerceInventoryWarehouse.getNameMap());

		List<CommerceInventoryWarehouse> commerceInventoryWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(_user.getCompanyId());

		CommerceInventoryWarehouse retrievedCommerceInventoryWarehouse =
			commerceInventoryWarehouses.get(0);

		Assert.assertEquals(
			commerceInventoryWarehouse.getName(),
			retrievedCommerceInventoryWarehouse.getName());
	}

	@Test
	public void testDeleteOrderCreatesInventoryAudit() throws Exception {
		frutillaRule.scenario(
			"Deleting an order writes a delete booked quantity inventory audit"
		).given(
			"An order that booked stock against a SKU"
		).when(
			"The order is deleted"
		).then(
			"An inventory audit tagged with the deleted order id records the " +
				"restock"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.valueOf(3),
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		int auditCountBefore =
			_commerceInventoryAuditLocalService.getCommerceInventoryAuditsCount(
				_group.getCompanyId(), _cpInstance1.getSku(), StringPool.BLANK);

		CommerceOrder commerceOrder = _addCommerceOrderWithBookedQuantity(
			BigDecimal.valueOf(3), StringPool.BLANK);

		long commerceOrderId = commerceOrder.getCommerceOrderId();

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);

		List<CommerceInventoryAudit> commerceInventoryAudits =
			_commerceInventoryAuditLocalService.getCommerceInventoryAudits(
				_group.getCompanyId(), _cpInstance1.getSku(), StringPool.BLANK,
				auditCountBefore, auditCountBefore + 10);

		CommerceInventoryAudit orderDeletionCommerceInventoryAudit = null;

		for (CommerceInventoryAudit commerceInventoryAudit :
				commerceInventoryAudits) {

			String logTypeSettings =
				commerceInventoryAudit.getLogTypeSettings();

			if ((logTypeSettings != null) &&
				logTypeSettings.contains(
					CommerceInventoryAuditTypeConstants.ORDER_ID) &&
				logTypeSettings.contains(String.valueOf(commerceOrderId))) {

				orderDeletionCommerceInventoryAudit = commerceInventoryAudit;

				break;
			}
		}

		Assert.assertNotNull(orderDeletionCommerceInventoryAudit);

		Assert.assertEquals(
			CommerceInventoryConstants.AUDIT_TYPE_DELETE_BOOKED_QUANTITY,
			orderDeletionCommerceInventoryAudit.getLogType());

		CommerceInventoryAuditType commerceInventoryAuditType =
			_commerceInventoryAuditTypeRegistry.getCommerceInventoryAuditType(
				orderDeletionCommerceInventoryAudit.getLogType());

		String log = commerceInventoryAuditType.formatLog(
			orderDeletionCommerceInventoryAudit.getUserId(),
			orderDeletionCommerceInventoryAudit.getLogTypeSettings(),
			LocaleUtil.getDefault());

		Assert.assertTrue(log.contains(String.valueOf(commerceOrderId)));
	}

	@Test
	public void testDeleteOrderRestocksBookedQuantity() throws Exception {
		frutillaRule.scenario(
			"Deleting an order restocks the booked quantity"
		).given(
			"An order that booked the full available stock"
		).when(
			"The order is deleted"
		).then(
			"The booked quantity is released and the available stock is " +
				"restored"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.valueOf(3),
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		CommerceOrder commerceOrder = _addCommerceOrderWithBookedQuantity(
			BigDecimal.valueOf(3), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.valueOf(3),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testDeleteOrderRestocksBookedQuantityWithUnitOfMeasure()
		throws Exception {

		frutillaRule.scenario(
			"Deleting an order restocks the booked unit of measure quantity"
		).given(
			"An order that booked the full available stock of a unit of measure"
		).when(
			"The order is deleted"
		).then(
			"The booked quantity is released and the available stock is " +
				"restored"
		);

		String unitOfMeasureKey = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance1.getCPInstanceId(),
			unitOfMeasureKey, BigDecimal.ONE, _cpInstance1.getSku());

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.valueOf(3),
				_cpInstance1.getSku(), unitOfMeasureKey, _serviceContext);

		CommerceOrder commerceOrder = _addCommerceOrderWithBookedQuantity(
			BigDecimal.valueOf(3), unitOfMeasureKey);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), unitOfMeasureKey)));

		_commerceOrderLocalService.deleteCommerceOrder(commerceOrder);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.valueOf(3),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), unitOfMeasureKey)));
	}

	@Test
	public void testGetStockFromInactiveWarehouseUsingChannel()
		throws Exception {

		frutillaRule.scenario(
			"When the same warehouse item is added to one active warehouse " +
				"and an inactive warehouse associated to a channel, only the " +
					"active warehouse items shall be considered"
		).given(
			"An active warehouse"
		).and(
			"An inactive warehouse with associated channel"
		).and(
			"A product sku in different quantities added to the warehouses"
		).when(
			"I retrieve the stock quantity"
		).then(
			"The stock quantity shall take into consideration only the " +
				"active warehouse stock"
		);

		CommerceInventoryWarehouse inactiveCommerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		CommerceInventoryWarehouse activeCommerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			inactiveCommerceInventoryWarehouse.
				getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		BigDecimal inactiveWarehouseQuantity = BigDecimal.valueOf(5);
		BigDecimal activeWarehouseQuantity = BigDecimal.TEN;

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				inactiveCommerceInventoryWarehouse.
					getCommerceInventoryWarehouseId(),
				inactiveWarehouseQuantity, BigDecimal.ZERO,
				_cpInstance1.getSku(), StringPool.BLANK);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				activeCommerceInventoryWarehouse.
					getCommerceInventoryWarehouseId(),
				activeWarehouseQuantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		BigDecimal companyStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _cpInstance1.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(activeWarehouseQuantity, companyStockQuantity));

		BigDecimal channelStockQuantity =
			_commerceInventoryEngine.getStockQuantity(
				_group.getCompanyId(), _accountEntry.getAccountEntryId(),
				_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
				_cpInstance1.getSku(), StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(BigDecimal.ZERO, channelStockQuantity));
	}

	@Test
	public void testGetStockQuantitiesForInactiveWarehouse() throws Exception {
		frutillaRule.scenario(
			"It shall not be possible to retrieve stocks from an inactive " +
				"warehouse"
		).given(
			"One inactive warehouse is created"
		).when(
			"A product is added to the warehouse"
		).then(
			"The retrieved stock quantity shall not contain the inactive " +
				"warehouse stocks"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		BigDecimal quantity = BigDecimal.TEN;

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				quantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _cpInstance1.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetStockQuantitiesForInactiveWarehouseUsingChannel()
		throws Exception {

		frutillaRule.scenario(
			"It shall not be possible to retrieve stocks from an inactive " +
				"warehouse using a commerce channel to the warehouse"
		).given(
			"One inactive warehouse is created"
		).when(
			"A product is added to the warehouse"
		).then(
			"The retrieved stock quantity shall not contain the inactive " +
				"warehouse stocks"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		BigDecimal quantity = BigDecimal.TEN;

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				quantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetStockQuantitiesForInactiveWarehouseUsingCPChannel()
		throws Exception {

		frutillaRule.scenario(
			"It shall not be possible to retrieve stocks from an inactive " +
				"warehouse using a commerce channel to the warehouse item"
		).given(
			"One inactive warehouse is created"
		).when(
			"A product is added to the warehouse"
		).then(
			"The retrieved stock quantity shall not contain the inactive " +
				"warehouse stocks"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CPDefinition.class.getName(), _cpInstance1.getCPDefinitionId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		BigDecimal quantity = BigDecimal.TEN;

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				quantity, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.ZERO,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetStockQuantity() throws Exception {
		frutillaRule.scenario(
			"The stock quantity of an item in an active warehouse is " +
				"correctly retrieved"
		).given(
			"A warehouse item added to an active warehouse with a channel"
		).when(
			"I get the stock quantity"
		).then(
			"The stock quantity is correctly retrieved"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				_commerceInventoryWarehouseItem.getQuantity(),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetStockQuantityUsingAccount() throws Exception {
		frutillaRule.scenario(
			"The stock quantity of an item in an active warehouse is " +
				"correctly retrieved"
		).given(
			"A warehouse item added to an active warehouse with a account " +
				"eligibility assigned"
		).when(
			"I get the stock quantity"
		).then(
			"The stock quantity is correctly retrieved"
		);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		_commerceInventoryWarehouseRelLocalService.
			addCommerceInventoryWarehouseRel(
				_user.getUserId(), AccountEntry.class.getName(),
				_accountEntry.getAccountEntryId(),
				_commerceInventoryWarehouseItem.
					getCommerceInventoryWarehouseId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
			_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.TEN,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), 0, _cpInstance1.getGroupId(),
					_commerceChannel.getGroupId(), _cpInstance1.getSku(),
					StringPool.BLANK)));
		Assert.assertTrue(
			BigDecimalUtil.eq(
				new BigDecimal(20),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetStockQuantityUsingAccountGroup() throws Exception {
		frutillaRule.scenario(
			"The stock quantity of an item in an active warehouse is " +
				"correctly retrieved"
		).given(
			"A warehouse item added to an active warehouse with a account " +
				"group eligibility assigned"
		).when(
			"I get the stock quantity"
		).then(
			"The stock quantity is correctly retrieved"
		);

		AccountGroup accountGroup =
			CommerceAccountTestUtil.addAccountGroupAndAccountRel(
				_group.getCompanyId(), RandomTestUtil.randomString(),
				AccountConstants.ACCOUNT_GROUP_TYPE_STATIC,
				_accountEntry.getAccountEntryId(), _serviceContext);

		_commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		_commerceInventoryWarehouseRelLocalService.
			addCommerceInventoryWarehouseRel(
				_user.getUserId(), AccountGroup.class.getName(),
				accountGroup.getAccountGroupId(),
				_commerceInventoryWarehouseItem.
					getCommerceInventoryWarehouseId());

		CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
			_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
			_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		Assert.assertTrue(
			BigDecimalUtil.eq(
				BigDecimal.TEN,
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), 0, _cpInstance1.getGroupId(),
					_commerceChannel.getGroupId(), _cpInstance1.getSku(),
					StringPool.BLANK)));

		Assert.assertTrue(
			BigDecimalUtil.eq(
				new BigDecimal(20),
				_commerceInventoryEngine.getStockQuantity(
					_group.getCompanyId(), _accountEntry.getAccountEntryId(),
					_cpInstance1.getGroupId(), _commerceChannel.getGroupId(),
					_cpInstance1.getSku(), StringPool.BLANK)));
	}

	@Test
	public void testGetWarehouse() throws Exception {
		frutillaRule.scenario(
			"It should be possible to filter warehouses based on their status"
		).given(
			"1 active and 1 inactive warehouse"
		).when(
			"I search by company, groupId and status"
		).then(
			"The correct warehouses are retrieved"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceInventoryWarehouse commerceInventoryWarehouseInactive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouseActive.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouseInactive.
				getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_user.getGroupId());

		List<CommerceInventoryWarehouse> activeWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					serviceContext.getCompanyId(), 0,
					_commerceChannel.getGroupId(), true);
		List<CommerceInventoryWarehouse> inactiveWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					serviceContext.getCompanyId(), 0,
					_commerceChannel.getGroupId(), false);

		Assert.assertEquals(
			activeWarehouses.toString(), 1, activeWarehouses.size());
		Assert.assertEquals(
			inactiveWarehouses.toString(), 1, inactiveWarehouses.size());

		Assert.assertEquals(
			commerceInventoryWarehouseActive, activeWarehouses.get(0));
		Assert.assertEquals(
			commerceInventoryWarehouseInactive, inactiveWarehouses.get(0));
	}

	@Test
	public void testGetWarehouseBySku() throws Exception {
		frutillaRule.scenario(
			"It should be possible to search warehouses by SKUs only for " +
				"active warehouses"
		).given(
			"1 active and 1 inactive warehouse"
		).when(
			"I search by groupId and SKUs"
		).then(
			"The correct warehouses are retrieved"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouse1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse1.getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		List<CommerceInventoryWarehouse> expectedWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					0, _commerceChannel.getGroupId(), _cpInstance1.getSku());

		Assert.assertEquals(
			expectedWarehouses.toString(), 1, expectedWarehouses.size());

		CommerceInventoryWarehouse retrievedWarehouse = expectedWarehouses.get(
			0);

		Assert.assertEquals(commerceInventoryWarehouse1, retrievedWarehouse);

		CommerceInventoryWarehouse commerceInventoryWarehouse2 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				false, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse2.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse2.getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, _cpInstance1.getSku(),
				StringPool.BLANK);

		expectedWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					0, _commerceChannel.getGroupId(), _cpInstance1.getSku());

		Assert.assertEquals(
			expectedWarehouses.toString(), 1, expectedWarehouses.size());

		retrievedWarehouse = expectedWarehouses.get(0);

		Assert.assertEquals(commerceInventoryWarehouse1, retrievedWarehouse);

		CommerceInventoryWarehouse commerceInventoryWarehouse3 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceChannelRelLocalServiceUtil.addCommerceChannelRel(
			CommerceInventoryWarehouse.class.getName(),
			commerceInventoryWarehouse3.getCommerceInventoryWarehouseId(),
			_commerceChannel.getCommerceChannelId(), _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouse3.getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, _cpInstance2.getSku(),
				StringPool.BLANK);

		expectedWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					0, _commerceChannel.getGroupId(), _cpInstance1.getSku());

		Assert.assertEquals(
			expectedWarehouses.toString(), 1, expectedWarehouses.size());

		expectedWarehouses =
			_commerceInventoryWarehouseLocalService.
				getCommerceInventoryWarehouses(
					0, _commerceChannel.getGroupId(), _cpInstance2.getSku());

		Assert.assertEquals(
			expectedWarehouses.toString(), 1, expectedWarehouses.size());

		retrievedWarehouse = expectedWarehouses.get(0);

		Assert.assertEquals(commerceInventoryWarehouse3, retrievedWarehouse);
	}

	@Test
	public void testGetWarehouseItemByDate() throws Exception {
		frutillaRule.scenario(
			"It should be possible to filter warehousesItes based on their " +
				"modified date"
		).given(
			"1 warehouse item"
		).when(
			"I search by company and date range"
		).then(
			"The warehouse item is retrieved"
		);

		Date startDate = new Date();

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_commerceChannel.getCommerceChannelId(), BigDecimal.TEN,
				_cpInstance1.getSku(), StringPool.BLANK, _serviceContext);

		Date endDate = new Date();

		int countWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItemsCountByModifiedDate(
					_group.getCompanyId(), startDate, endDate);

		Assert.assertEquals(1, countWarehouseItems);

		List<CommerceInventoryWarehouseItem> warehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItemsByModifiedDate(
					_group.getCompanyId(), startDate, endDate,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			warehouseItems.get(0), commerceInventoryWarehouseItem);
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceOrder _addCommerceOrderWithBookedQuantity(
			BigDecimal quantity, String unitOfMeasureKey)
		throws Exception {

		CommerceOrder commerceOrder =
			_commerceOrderLocalService.addCommerceOrder(
				_user.getUserId(), _commerceChannel.getGroupId(),
				_accountEntry.getAccountEntryId(), _commerceCurrency.getCode(),
				0);

		CommerceOrderItem commerceOrderItem =
			_commerceOrderItemLocalService.addCommerceOrderItem(
				_user.getUserId(), commerceOrder.getCommerceOrderId(),
				_cpInstance1.getCPInstanceId(), null, quantity, 0,
				BigDecimal.ZERO, unitOfMeasureKey, _commerceContext,
				_serviceContext);

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_commerceInventoryBookedQuantityLocalService.
				addCommerceInventoryBookedQuantity(
					_user.getUserId(), null, quantity, _cpInstance1.getSku(),
					unitOfMeasureKey, Collections.emptyMap());

		_commerceOrderItemLocalService.updateCommerceOrderItem(
			commerceOrderItem.getCommerceOrderItemId(),
			commerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId());

		return commerceOrder;
	}

	private AccountEntry _accountEntry;
	private CommerceChannel _commerceChannel;

	@Inject
	private CommerceChannelLocalService _commerceChannelLocalService;

	private CommerceContext _commerceContext;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceInventoryAuditLocalService
		_commerceInventoryAuditLocalService;

	@Inject
	private CommerceInventoryAuditTypeRegistry
		_commerceInventoryAuditTypeRegistry;

	@Inject
	private CommerceInventoryBookedQuantityLocalService
		_commerceInventoryBookedQuantityLocalService;

	@Inject
	private CommerceInventoryEngine _commerceInventoryEngine;

	private CommerceInventoryWarehouseItem _commerceInventoryWarehouseItem;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@Inject
	private CommerceInventoryWarehouseRelLocalService
		_commerceInventoryWarehouseRelLocalService;

	@Inject
	private CommerceOrderItemLocalService _commerceOrderItemLocalService;

	@Inject
	private CommerceOrderLocalService _commerceOrderLocalService;

	@DeleteAfterTestRun
	private List<CommerceOrder> _commerceOrders = new ArrayList<>();

	private CPInstance _cpInstance1;
	private CPInstance _cpInstance2;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private Group _group;
	private ServiceContext _serviceContext;
	private User _user;

}