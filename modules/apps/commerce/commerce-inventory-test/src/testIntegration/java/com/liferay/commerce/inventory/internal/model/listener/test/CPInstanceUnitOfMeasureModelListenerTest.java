/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseItemLocalService;
import com.liferay.commerce.inventory.service.CommerceInventoryWarehouseLocalService;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseItemUtil;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class CPInstanceUnitOfMeasureModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addUser(_company);
	}

	@Before
	public void setUp() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_company.getCompanyId(), _company.getGroupId(),
				_user.getUserId());

		_commerceCatalog = CommerceCatalogLocalServiceUtil.addCommerceCatalog(
			null, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			LocaleUtil.US.getDisplayLanguage(), serviceContext);

		_cpInstance = CPTestUtil.addCPInstanceFromCatalog(
			_commerceCatalog.getGroupId());

		_cpDefinition = _cpInstance.getCPDefinition();

		_commerceInventoryWarehouse =
			_commerceInventoryWarehouseLocalService.
				addCommerceInventoryWarehouse(
					RandomTestUtil.randomString(),
					RandomTestUtil.randomLocaleStringMap(),
					RandomTestUtil.randomLocaleStringMap(), true,
					RandomTestUtil.randomString(), null, null,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), 10, 10, serviceContext);

		for (CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure :
				_cpInstanceUnitOfMeasureLocalService.
					getCPInstanceUnitOfMeasures(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

			_cpInstanceUnitOfMeasureLocalService.deleteCPInstanceUnitOfMeasure(
				cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());
		}
	}

	@Test
	public void testAddFirstCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Create the first CP Instance Unit Of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"The first CP Instance Unit Of Measure is added"
		).then(
			"The Commerce Inventory Warehouse Items related to the SKU will " +
				"be updated for the unitOfMeasureKey field."
		);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				BigDecimal.TEN, BigDecimal.ZERO, _cpInstance.getSku(),
				StringPool.BLANK);

		String key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(), 2,
				BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());

		List<CommerceInventoryWarehouseItem> commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 1,
			commerceInventoryWarehouseItems.size());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_getCommerceInventoryWarehouseItem(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		Assert.assertNotNull(commerceInventoryWarehouseItem);

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), key,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 1,
			commerceInventoryWarehouseItems.size());
	}

	@Test
	public void testAddMultipleCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Create multiple CP Instance Unit Of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU"
		).when(
			"Multiple CP Instance Unit Of Measure are added"
		).then(
			"A Commerce Inventory Warehouse Items related to the SKU will be " +
				"updated for the unitOfMeasureKey field and other Commerce " +
					"Inventory Warehouse Items will be added."
		);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				BigDecimal.TEN, BigDecimal.ZERO, _cpInstance.getSku(),
				StringPool.BLANK);

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), 2, BigDecimal.ZERO,
				true, 0.0, BigDecimal.ONE, _cpInstance.getSku());

		List<CommerceInventoryWarehouseItem> commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 1,
			commerceInventoryWarehouseItems.size());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_getCommerceInventoryWarehouseItem(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		Assert.assertNotNull(commerceInventoryWarehouseItem);

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), 2, BigDecimal.ZERO,
				true, 0.0, BigDecimal.ONE, _cpInstance.getSku());

		commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 2,
			commerceInventoryWarehouseItems.size());

		commerceInventoryWarehouseItem = _getCommerceInventoryWarehouseItem(
			_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
			_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		Assert.assertNotNull(commerceInventoryWarehouseItem);
	}

	@Test
	public void testDeleteCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Delete a CP Instance Unit Of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU with multiple " +
				"Unit Of Measure"
		).when(
			"A Unit Of Measure is deleted"
		).then(
			"The Commerce Inventory Warehouse Items related to the Unit of " +
				"Measure will be deleted."
		);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				BigDecimal.TEN, BigDecimal.ZERO, _cpInstance.getSku(),
				StringPool.BLANK);

		String key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(), 2,
				BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, RandomTestUtil.randomString(),
				RandomTestUtil.randomLocaleStringMap(), 2, BigDecimal.ZERO,
				true, 0.0, BigDecimal.ONE, _cpInstance.getSku());

		List<CommerceInventoryWarehouseItem> commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 2,
			commerceInventoryWarehouseItems.size());

		_cpInstanceUnitOfMeasureLocalService.deleteCPInstanceUnitOfMeasure(
			_cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());

		commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 1,
			commerceInventoryWarehouseItems.size());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			commerceInventoryWarehouseItems.get(0);

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), key,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteLastCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Delete the last CP Instance Unit Of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU with a Unit Of " +
				"Measure"
		).when(
			"The Unit Of Measure is deleted"
		).then(
			"The UnitOfMeasureKey field of the Commerce Inventory Warehouse " +
				"Items related to the Unit of Measure will be blanked."
		);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				BigDecimal.TEN, BigDecimal.ZERO, _cpInstance.getSku(),
				StringPool.BLANK);

		String key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(), 2,
				BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_getCommerceInventoryWarehouseItem(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		Assert.assertNotNull(commerceInventoryWarehouseItem);

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), key,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		_cpInstanceUnitOfMeasureLocalService.deleteCPInstanceUnitOfMeasure(
			_cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());

		List<CommerceInventoryWarehouseItem> commerceInventoryWarehouseItems =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItems(
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			commerceInventoryWarehouseItems.toString(), 1,
			commerceInventoryWarehouseItems.size());

		commerceInventoryWarehouseItem = commerceInventoryWarehouseItems.get(0);

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), StringPool.BLANK,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());
	}

	@Test
	public void testUpdateCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Update a CP Instance Unit Of Measure for a CPInstance"
		).given(
			"I have a product definition with a default SKU and a Unit Of " +
				"Measure"
		).when(
			"The key of the CP Instance Unit Of Measure is updated"
		).then(
			"The Commerce Inventory Warehouse Items related to the SKU will " +
				"be updated for the unitOfMeasureKey field."
		);

		_commerceInventoryWarehouseItemLocalService.
			addCommerceInventoryWarehouseItem(
				RandomTestUtil.randomString(), _user.getUserId(),
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				BigDecimal.TEN, BigDecimal.ZERO, _cpInstance.getSku(),
				StringPool.BLANK);

		String key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(), 2,
				BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_getCommerceInventoryWarehouseItem(
				_commerceInventoryWarehouse.getCommerceInventoryWarehouseId(),
				_cpInstance.getSku(), _cpInstanceUnitOfMeasure.getKey());

		Assert.assertNotNull(commerceInventoryWarehouseItem);

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), key,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.updateCPInstanceUnitOfMeasure(
				_cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
				_cpInstance.getCPInstanceId(), true, BigDecimal.ONE, key,
				RandomTestUtil.randomLocaleStringMap(), 2, BigDecimal.ZERO,
				true, 0.0, BigDecimal.ONE, _cpInstance.getSku());

		commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId());

		Assert.assertEquals(
			commerceInventoryWarehouseItem.toString(), key,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());
	}

	@Test
	public void testWarehouseItemQuantityIsPreservedAcrossUnitOfMeasureChanges()
		throws Exception {

		frutillaRule.scenario(
			"Preserve the warehouse item quantity across the Unit Of Measure " +
				"lifecycle"
		).given(
			"A CPInstance with a warehouse item with a quantity"
		).when(
			"A Unit Of Measure is added and then deleted"
		).then(
			"The warehouse item quantity is unchanged at every step"
		);

		BigDecimal quantity = BigDecimal.TEN;

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				addCommerceInventoryWarehouseItem(
					RandomTestUtil.randomString(), _user.getUserId(),
					_commerceInventoryWarehouse.
						getCommerceInventoryWarehouseId(),
					quantity, BigDecimal.ZERO, _cpInstance.getSku(),
					StringPool.BLANK);

		Assert.assertEquals(
			0,
			quantity.compareTo(commerceInventoryWarehouseItem.getQuantity()));
		Assert.assertEquals(
			StringPool.BLANK,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());

		String key = RandomTestUtil.randomString();

		_cpInstanceUnitOfMeasure =
			_cpInstanceUnitOfMeasureLocalService.addCPInstanceUnitOfMeasure(
				_user.getUserId(), _cpInstance.getCPInstanceId(), true,
				BigDecimal.ONE, key, RandomTestUtil.randomLocaleStringMap(), 3,
				BigDecimal.ZERO, true, 0.0, BigDecimal.ONE,
				_cpInstance.getSku());

		commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId());

		Assert.assertEquals(
			key, commerceInventoryWarehouseItem.getUnitOfMeasureKey());
		Assert.assertEquals(
			0,
			quantity.compareTo(commerceInventoryWarehouseItem.getQuantity()));

		_cpInstanceUnitOfMeasureLocalService.deleteCPInstanceUnitOfMeasure(
			_cpInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());

		commerceInventoryWarehouseItem =
			_commerceInventoryWarehouseItemLocalService.
				getCommerceInventoryWarehouseItem(
					commerceInventoryWarehouseItem.
						getCommerceInventoryWarehouseItemId());

		Assert.assertEquals(
			StringPool.BLANK,
			commerceInventoryWarehouseItem.getUnitOfMeasureKey());
		Assert.assertEquals(
			0,
			quantity.compareTo(commerceInventoryWarehouseItem.getQuantity()));
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private CommerceInventoryWarehouseItem _getCommerceInventoryWarehouseItem(
			long commerceInventoryWarehouseId, String sku,
			String unitOfMeasureKey)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommerceInventoryWarehouseItemUtil.getPersistence(),
			commerceInventoryWarehouseItemPersistence ->
				commerceInventoryWarehouseItemPersistence.findByCIWI_S_U(
					commerceInventoryWarehouseId, sku, unitOfMeasureKey));
	}

	private static Company _company;
	private static User _user;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceInventoryWarehouse _commerceInventoryWarehouse;

	@Inject
	private CommerceInventoryWarehouseItemLocalService
		_commerceInventoryWarehouseItemLocalService;

	@Inject
	private CommerceInventoryWarehouseLocalService
		_commerceInventoryWarehouseLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private CPInstanceUnitOfMeasure _cpInstanceUnitOfMeasure;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

}