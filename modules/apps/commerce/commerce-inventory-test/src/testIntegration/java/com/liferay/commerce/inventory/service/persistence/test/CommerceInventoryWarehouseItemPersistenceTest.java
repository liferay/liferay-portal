/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseItemExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseItemException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseItem;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseItemPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseItemUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class CommerceInventoryWarehouseItemPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.inventory.service"));

	@Before
	public void setUp() {
		_persistence = CommerceInventoryWarehouseItemUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceInventoryWarehouseItem> iterator =
			_commerceInventoryWarehouseItems.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_persistence.create(pk);

		Assert.assertNotNull(commerceInventoryWarehouseItem);

		Assert.assertEquals(commerceInventoryWarehouseItem.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		_persistence.remove(newCommerceInventoryWarehouseItem);

		CommerceInventoryWarehouseItem existingCommerceInventoryWarehouseItem =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouseItem.getPrimaryKey());

		Assert.assertNull(existingCommerceInventoryWarehouseItem);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceInventoryWarehouseItem();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			_persistence.create(pk);

		newCommerceInventoryWarehouseItem.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseItem.setUuid(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouseItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouseItem.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseItem.setUserId(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseItem.setUserName(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouseItem.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryWarehouseItem.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryWarehouseItem.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseItem.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceInventoryWarehouseItem.setReservedQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceInventoryWarehouseItem.setSku(RandomTestUtil.randomString());

		newCommerceInventoryWarehouseItem.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryWarehouseItems.add(
			_persistence.update(newCommerceInventoryWarehouseItem));

		CommerceInventoryWarehouseItem existingCommerceInventoryWarehouseItem =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getMvccVersion(),
			newCommerceInventoryWarehouseItem.getMvccVersion());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getUuid(),
			newCommerceInventoryWarehouseItem.getUuid());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getExternalReferenceCode(),
			newCommerceInventoryWarehouseItem.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.
				getCommerceInventoryWarehouseItemId(),
			newCommerceInventoryWarehouseItem.
				getCommerceInventoryWarehouseItemId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getCompanyId(),
			newCommerceInventoryWarehouseItem.getCompanyId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getUserId(),
			newCommerceInventoryWarehouseItem.getUserId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getUserName(),
			newCommerceInventoryWarehouseItem.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouseItem.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouseItem.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouseItem.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouseItem.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.
				getCommerceInventoryWarehouseId(),
			newCommerceInventoryWarehouseItem.
				getCommerceInventoryWarehouseId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getQuantity(),
			newCommerceInventoryWarehouseItem.getQuantity());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getReservedQuantity(),
			newCommerceInventoryWarehouseItem.getReservedQuantity());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getSku(),
			newCommerceInventoryWarehouseItem.getSku());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem.getUnitOfMeasureKey(),
			newCommerceInventoryWarehouseItem.getUnitOfMeasureKey());
	}

	@Test(
		expected = DuplicateCommerceInventoryWarehouseItemExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		newCommerceInventoryWarehouseItem.setCompanyId(
			commerceInventoryWarehouseItem.getCompanyId());

		newCommerceInventoryWarehouseItem = _persistence.update(
			newCommerceInventoryWarehouseItem);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceInventoryWarehouseItem);

		newCommerceInventoryWarehouseItem.setExternalReferenceCode(
			commerceInventoryWarehouseItem.getExternalReferenceCode());

		_persistence.update(newCommerceInventoryWarehouseItem);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCommerceInventoryWarehouseId() throws Exception {
		_persistence.countByCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceInventoryWarehouseId(0L);
	}

	@Test
	public void testCountByC_S_U() throws Exception {
		_persistence.countByC_S_U(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_S_U(0L, "null", "null");

		_persistence.countByC_S_U(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByCIWI_S_U() throws Exception {
		_persistence.countByCIWI_S_U(RandomTestUtil.nextLong(), "", "");

		_persistence.countByCIWI_S_U(0L, "null", "null");

		_persistence.countByCIWI_S_U(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		CommerceInventoryWarehouseItem existingCommerceInventoryWarehouseItem =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem,
			newCommerceInventoryWarehouseItem);
	}

	@Test(expected = NoSuchInventoryWarehouseItemException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceInventoryWarehouseItem>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CIWarehouseItem", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceInventoryWarehouseItemId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true,
			"commerceInventoryWarehouseId", true, "quantity", true,
			"reservedQuantity", true, "sku", true, "unitOfMeasureKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		CommerceInventoryWarehouseItem existingCommerceInventoryWarehouseItem =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouseItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseItem,
			newCommerceInventoryWarehouseItem);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseItem missingCommerceInventoryWarehouseItem =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceInventoryWarehouseItem);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem1 =
			addCommerceInventoryWarehouseItem();
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem2 =
			addCommerceInventoryWarehouseItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseItem1.getPrimaryKey());
		primaryKeys.add(newCommerceInventoryWarehouseItem2.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouseItem>
			commerceInventoryWarehouseItems = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceInventoryWarehouseItems.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseItem1,
			commerceInventoryWarehouseItems.get(
				newCommerceInventoryWarehouseItem1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceInventoryWarehouseItem2,
			commerceInventoryWarehouseItems.get(
				newCommerceInventoryWarehouseItem2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceInventoryWarehouseItem>
			commerceInventoryWarehouseItems = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouseItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseItem.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceInventoryWarehouseItem>
			commerceInventoryWarehouseItems = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouseItems.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseItem,
			commerceInventoryWarehouseItems.get(
				newCommerceInventoryWarehouseItem.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceInventoryWarehouseItem>
			commerceInventoryWarehouseItems = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouseItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseItem.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouseItem>
			commerceInventoryWarehouseItems = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouseItems.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseItem,
			commerceInventoryWarehouseItems.get(
				newCommerceInventoryWarehouseItem.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceInventoryWarehouseItem newCommerceInventoryWarehouseItem =
			addCommerceInventoryWarehouseItem();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseItem.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem) {

		Assert.assertEquals(
			Long.valueOf(
				commerceInventoryWarehouseItem.
					getCommerceInventoryWarehouseId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouseItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceInventoryWarehouseId"));
		Assert.assertEquals(
			commerceInventoryWarehouseItem.getSku(),
			ReflectionTestUtil.invoke(
				commerceInventoryWarehouseItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sku"));
		Assert.assertEquals(
			commerceInventoryWarehouseItem.getUnitOfMeasureKey(),
			ReflectionTestUtil.invoke(
				commerceInventoryWarehouseItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "unitOfMeasureKey"));

		Assert.assertEquals(
			commerceInventoryWarehouseItem.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceInventoryWarehouseItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceInventoryWarehouseItem.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouseItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceInventoryWarehouseItem addCommerceInventoryWarehouseItem()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseItem commerceInventoryWarehouseItem =
			_persistence.create(pk);

		commerceInventoryWarehouseItem.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceInventoryWarehouseItem.setUuid(RandomTestUtil.randomString());

		commerceInventoryWarehouseItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceInventoryWarehouseItem.setCompanyId(RandomTestUtil.nextLong());

		commerceInventoryWarehouseItem.setUserId(RandomTestUtil.nextLong());

		commerceInventoryWarehouseItem.setUserName(
			RandomTestUtil.randomString());

		commerceInventoryWarehouseItem.setCreateDate(RandomTestUtil.nextDate());

		commerceInventoryWarehouseItem.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceInventoryWarehouseItem.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		commerceInventoryWarehouseItem.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceInventoryWarehouseItem.setReservedQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceInventoryWarehouseItem.setSku(RandomTestUtil.randomString());

		commerceInventoryWarehouseItem.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryWarehouseItems.add(
			_persistence.update(commerceInventoryWarehouseItem));

		return commerceInventoryWarehouseItem;
	}

	private List<CommerceInventoryWarehouseItem>
		_commerceInventoryWarehouseItems =
			new ArrayList<CommerceInventoryWarehouseItem>();
	private CommerceInventoryWarehouseItemPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}