/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.product.exception.CPInstanceUnitOfMeasureKeyException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.CommerceInventoryTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Andrea Sbarra
 */
@RunWith(Arquillian.class)
public class CommerceInventoryWarehouseItemLocalServiceTest {

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

		_cpInstance = CommerceInventoryTestUtil.addRandomCPInstanceSku(
			_group.getGroupId());

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.TEN, "KEY",
				HashMapBuilder.put(
					LocaleUtil.getDefault(), "NOME"
				).build(),
				2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());
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

	@Test
	public void testAddWarehouseItemWithoutUOM() throws Exception {
		frutillaRule.scenario(
			"It should be possible to add item without UOM if the SKU have " +
				"only one UOM"
		).given(
			"1 active warehouse"
		).when(
			"The item is added without the UOM"
		).and(
			"the sku has only one UOM"
		).then(
			"The Item is created"
		).and(
			"has the UOM of the SKU"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					StringPool.BLANK, _user.getUserId(),
					commerceInventoryWarehouseActive.
						getCommerceInventoryWarehouseId(),
					BigDecimal.ONE, BigDecimal.ZERO, _cpInstance.getSku(),
					StringPool.BLANK);

		Assert.assertNotNull(commerceInventoryWarehouseItem);
		Assert.assertEquals(
			"UOM", _cpInstanceUnitOfMeasure.getKey(),
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());
	}

	@Test(expected = CPInstanceUnitOfMeasureKeyException.class)
	public void testAddWarehouseItemWithoutUOMFails() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to add item without UOM if the SKU " +
				"has more then one UOM"
		).given(
			"1 active warehouse"
		).when(
			"The item is added without the UOM"
		).and(
			"the SKU has more then one UOM"
		).then(
			"The Item is not created"
		).and(
			"CPInstanceUnitOfMeasureKeyException is thrown"
		);

		CPInstance cpInstance =
			CommerceInventoryTestUtil.addRandomCPInstanceSku(
				_group.getGroupId());

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), _cpInstance.getCPInstanceId(), true,
			BigDecimal.TEN, "KEY1",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "NAME-1"
			).build(),
			2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, cpInstance.getSku());

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), _cpInstance.getCPInstanceId(), true,
			BigDecimal.TEN, "KEY2",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "NAME-2"
			).build(),
			2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, cpInstance.getSku());

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouseActive.
					getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, cpInstance.getSku(),
				StringPool.BLANK);
	}

	@Test
	public void testAddWarehouseItemWithRandomSKUAndUOM() throws Exception {
		frutillaRule.scenario(
			"It should be possible to add item with a UOM and the SKU does " +
				"not belong the any catalog"
		).given(
			"1 active warehouse"
		).when(
			"The item is added with a UOM"
		).then(
			"The Item is correctly created"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					StringPool.BLANK, _user.getUserId(),
					commerceInventoryWarehouseActive.
						getCommerceInventoryWarehouseId(),
					BigDecimal.ONE, BigDecimal.ZERO,
					RandomTestUtil.randomString(),
					_cpInstanceUnitOfMeasure.getKey()));
	}

	@Test
	public void testAddWarehouseItemWithRightUOM() throws Exception {
		frutillaRule.scenario(
			"It should be possible to add item with UOM of the SKU"
		).given(
			"1 active warehouse"
		).when(
			"The item is add with UOM of the SKU"
		).then(
			"The Item is correctly created"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					StringPool.BLANK, _user.getUserId(),
					commerceInventoryWarehouseActive.
						getCommerceInventoryWarehouseId(),
					BigDecimal.ONE, BigDecimal.ZERO, _cpInstance.getSku(),
					_cpInstanceUnitOfMeasure.getKey()));
	}

	@Test(expected = NoSuchCPInstanceUnitOfMeasureException.class)
	public void testAddWarehouseItemWithWrongUOM() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to add an item with a UOM not present " +
				"on the SKU"
		).given(
			"1 active warehouse"
		).when(
			"The item is add with a random UOM"
		).then(
			"The Item is not created"
		).and(
			"and a NoSuchCPInstanceUnitOfMeasureException is thrown"
		);

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					StringPool.BLANK, _user.getUserId(),
					commerceInventoryWarehouseActive.
						getCommerceInventoryWarehouseId(),
					BigDecimal.ONE, BigDecimal.ZERO, _cpInstance.getSku(),
					RandomTestUtil.randomString()));
	}

	@Test(expected = NoSuchCPInstanceUnitOfMeasureException.class)
	public void testAddWarehouseItemWithWrongUOMFails() throws Exception {
		frutillaRule.scenario(
			"It should not be possible to add items with a UOM if the SKU " +
				"have more then one UOM and not the UOM used"
		).given(
			"1 active warehouse"
		).when(
			"The item is added with a random UOM"
		).and(
			"the SKU has more then one UOM"
		).then(
			"The Item is not created"
		).and(
			"NoSuchCPInstanceUnitOfMeasureException is thrown"
		);

		CPInstance cpInstance =
			CommerceInventoryTestUtil.addRandomCPInstanceSku(
				_group.getGroupId());

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), _cpInstance.getCPInstanceId(), true,
			BigDecimal.TEN, "KEY1",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "NAME-1"
			).build(),
			2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, cpInstance.getSku());

		_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
			_user.getUserId(), _cpInstance.getCPInstanceId(), true,
			BigDecimal.TEN, "KEY2",
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "NAME-2"
			).build(),
			2, BigDecimal.ZERO, true, 0.0, BigDecimal.ONE, cpInstance.getSku());

		CommerceInventoryWarehouse commerceInventoryWarehouseActive =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				StringPool.BLANK, _user.getUserId(),
				commerceInventoryWarehouseActive.
					getCommerceInventoryWarehouseId(),
				BigDecimal.ONE, BigDecimal.ZERO, cpInstance.getSku(),
				RandomTestUtil.randomString());
	}

	@Test
	public void testDeleteCommerceInventoryWarehouseItem() throws Exception {
		CommerceInventoryWarehouse commerceInventoryWarehouse1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem1 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), commerceInventoryWarehouse1, BigDecimal.ONE,
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure1 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				_group.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.TEN,
				_cpInstance.getSku());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem2 =
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouse1.
						getCommerceInventoryWarehouseId(),
					_cpInstance.getSku(), cpInstanceUnitOfMeasure1.getKey());

		CommerceInventoryWarehouse commerceInventoryWarehouse2 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouse(
				true, _serviceContext);

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem3 =
			CommerceInventoryTestUtil.addCommerceInventoryWarehouseItem(
				_user.getUserId(), commerceInventoryWarehouse2, BigDecimal.ONE,
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		CPInstance cpInstance =
			CommerceInventoryTestUtil.addRandomCPInstanceSku(
				_group.getGroupId());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure2 =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				_group.getGroupId(), cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(), BigDecimal.TEN,
				cpInstance.getSku());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem4 =
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouse1.
						getCommerceInventoryWarehouseId(),
					cpInstance.getSku(), cpInstanceUnitOfMeasure2.getKey());

		_commerceInventoryWarehouseItemLocalService.
			deleteCommerceInventoryWarehouseItem(
				commerceInventoryWarehouseItem1.
					getCommerceInventoryWarehouseItemId());

		Assert.assertNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem1.
						getCommerceInventoryWarehouseItemId()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem2.
						getCommerceInventoryWarehouseItemId()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem3.
						getCommerceInventoryWarehouseItemId()));
		Assert.assertNotNull(
			_commerceInventoryWarehouseItemLocalService.
				fetchCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem4.
						getCommerceInventoryWarehouseItemId()));
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private static User _user;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	private CPInstance _cpInstance;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private CPInstanceUnitOfMeasure _cpInstanceUnitOfMeasure;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	private Group _group;
	private ServiceContext _serviceContext;

}