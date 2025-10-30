/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryReplenishmentItemExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryReplenishmentItemException;
import com.liferay.commerce.inventory.model.CommerceInventoryReplenishmentItem;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryReplenishmentItemPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryReplenishmentItemUtil;
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
public class CommerceInventoryReplenishmentItemPersistenceTest {

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
		_persistence = CommerceInventoryReplenishmentItemUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceInventoryReplenishmentItem> iterator =
			_commerceInventoryReplenishmentItems.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			_persistence.create(pk);

		Assert.assertNotNull(commerceInventoryReplenishmentItem);

		Assert.assertEquals(
			commerceInventoryReplenishmentItem.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		_persistence.remove(newCommerceInventoryReplenishmentItem);

		CommerceInventoryReplenishmentItem
			existingCommerceInventoryReplenishmentItem =
				_persistence.fetchByPrimaryKey(
					newCommerceInventoryReplenishmentItem.getPrimaryKey());

		Assert.assertNull(existingCommerceInventoryReplenishmentItem);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceInventoryReplenishmentItem();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem = _persistence.create(pk);

		newCommerceInventoryReplenishmentItem.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceInventoryReplenishmentItem.setUuid(
			RandomTestUtil.randomString());

		newCommerceInventoryReplenishmentItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceInventoryReplenishmentItem.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceInventoryReplenishmentItem.setUserId(
			RandomTestUtil.nextLong());

		newCommerceInventoryReplenishmentItem.setUserName(
			RandomTestUtil.randomString());

		newCommerceInventoryReplenishmentItem.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryReplenishmentItem.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryReplenishmentItem.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		newCommerceInventoryReplenishmentItem.setAvailabilityDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryReplenishmentItem.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceInventoryReplenishmentItem.setSku(
			RandomTestUtil.randomString());

		newCommerceInventoryReplenishmentItem.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryReplenishmentItems.add(
			_persistence.update(newCommerceInventoryReplenishmentItem));

		CommerceInventoryReplenishmentItem
			existingCommerceInventoryReplenishmentItem =
				_persistence.findByPrimaryKey(
					newCommerceInventoryReplenishmentItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getMvccVersion(),
			newCommerceInventoryReplenishmentItem.getMvccVersion());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getUuid(),
			newCommerceInventoryReplenishmentItem.getUuid());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.
				getExternalReferenceCode(),
			newCommerceInventoryReplenishmentItem.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.
				getCommerceInventoryReplenishmentItemId(),
			newCommerceInventoryReplenishmentItem.
				getCommerceInventoryReplenishmentItemId());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getCompanyId(),
			newCommerceInventoryReplenishmentItem.getCompanyId());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getUserId(),
			newCommerceInventoryReplenishmentItem.getUserId());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getUserName(),
			newCommerceInventoryReplenishmentItem.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryReplenishmentItem.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceInventoryReplenishmentItem.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryReplenishmentItem.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceInventoryReplenishmentItem.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.
				getCommerceInventoryWarehouseId(),
			newCommerceInventoryReplenishmentItem.
				getCommerceInventoryWarehouseId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryReplenishmentItem.
					getAvailabilityDate()),
			Time.getShortTimestamp(
				newCommerceInventoryReplenishmentItem.getAvailabilityDate()));
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getQuantity(),
			newCommerceInventoryReplenishmentItem.getQuantity());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getSku(),
			newCommerceInventoryReplenishmentItem.getSku());
		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem.getUnitOfMeasureKey(),
			newCommerceInventoryReplenishmentItem.getUnitOfMeasureKey());
	}

	@Test(
		expected = DuplicateCommerceInventoryReplenishmentItemExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			addCommerceInventoryReplenishmentItem();

		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		newCommerceInventoryReplenishmentItem.setCompanyId(
			commerceInventoryReplenishmentItem.getCompanyId());

		newCommerceInventoryReplenishmentItem = _persistence.update(
			newCommerceInventoryReplenishmentItem);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceInventoryReplenishmentItem);

		newCommerceInventoryReplenishmentItem.setExternalReferenceCode(
			commerceInventoryReplenishmentItem.getExternalReferenceCode());

		_persistence.update(newCommerceInventoryReplenishmentItem);
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
	public void testCountByCommerceInventoryWarehouseId() throws Exception {
		_persistence.countByCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceInventoryWarehouseId(0L);
	}

	@Test
	public void testCountByAvailabilityDate() throws Exception {
		_persistence.countByAvailabilityDate(RandomTestUtil.nextDate());

		_persistence.countByAvailabilityDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountBySku() throws Exception {
		_persistence.countBySku("");

		_persistence.countBySku("null");

		_persistence.countBySku((String)null);
	}

	@Test
	public void testCountByC_S_U() throws Exception {
		_persistence.countByC_S_U(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_S_U(0L, "null", "null");

		_persistence.countByC_S_U(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByAD_S_U() throws Exception {
		_persistence.countByAD_S_U(RandomTestUtil.nextDate(), "", "");

		_persistence.countByAD_S_U(RandomTestUtil.nextDate(), "null", "null");

		_persistence.countByAD_S_U(
			RandomTestUtil.nextDate(), (String)null, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		CommerceInventoryReplenishmentItem
			existingCommerceInventoryReplenishmentItem =
				_persistence.findByPrimaryKey(
					newCommerceInventoryReplenishmentItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem,
			newCommerceInventoryReplenishmentItem);
	}

	@Test(expected = NoSuchInventoryReplenishmentItemException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceInventoryReplenishmentItem>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CIReplenishmentItem", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true,
			"commerceInventoryReplenishmentItemId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "commerceInventoryWarehouseId", true,
			"availabilityDate", true, "quantity", true, "sku", true,
			"unitOfMeasureKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		CommerceInventoryReplenishmentItem
			existingCommerceInventoryReplenishmentItem =
				_persistence.fetchByPrimaryKey(
					newCommerceInventoryReplenishmentItem.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryReplenishmentItem,
			newCommerceInventoryReplenishmentItem);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryReplenishmentItem
			missingCommerceInventoryReplenishmentItem =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceInventoryReplenishmentItem);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem1 =
				addCommerceInventoryReplenishmentItem();
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem2 =
				addCommerceInventoryReplenishmentItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryReplenishmentItem1.getPrimaryKey());
		primaryKeys.add(newCommerceInventoryReplenishmentItem2.getPrimaryKey());

		Map<Serializable, CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceInventoryReplenishmentItems.size());
		Assert.assertEquals(
			newCommerceInventoryReplenishmentItem1,
			commerceInventoryReplenishmentItems.get(
				newCommerceInventoryReplenishmentItem1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceInventoryReplenishmentItem2,
			commerceInventoryReplenishmentItems.get(
				newCommerceInventoryReplenishmentItem2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceInventoryReplenishmentItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryReplenishmentItem.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceInventoryReplenishmentItems.size());
		Assert.assertEquals(
			newCommerceInventoryReplenishmentItem,
			commerceInventoryReplenishmentItems.get(
				newCommerceInventoryReplenishmentItem.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceInventoryReplenishmentItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryReplenishmentItem.getPrimaryKey());

		Map<Serializable, CommerceInventoryReplenishmentItem>
			commerceInventoryReplenishmentItems =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceInventoryReplenishmentItems.size());
		Assert.assertEquals(
			newCommerceInventoryReplenishmentItem,
			commerceInventoryReplenishmentItems.get(
				newCommerceInventoryReplenishmentItem.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceInventoryReplenishmentItem
			newCommerceInventoryReplenishmentItem =
				addCommerceInventoryReplenishmentItem();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceInventoryReplenishmentItem.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem) {

		Assert.assertEquals(
			commerceInventoryReplenishmentItem.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceInventoryReplenishmentItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceInventoryReplenishmentItem.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryReplenishmentItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceInventoryReplenishmentItem
			addCommerceInventoryReplenishmentItem()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem =
			_persistence.create(pk);

		commerceInventoryReplenishmentItem.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceInventoryReplenishmentItem.setUuid(
			RandomTestUtil.randomString());

		commerceInventoryReplenishmentItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceInventoryReplenishmentItem.setCompanyId(
			RandomTestUtil.nextLong());

		commerceInventoryReplenishmentItem.setUserId(RandomTestUtil.nextLong());

		commerceInventoryReplenishmentItem.setUserName(
			RandomTestUtil.randomString());

		commerceInventoryReplenishmentItem.setCreateDate(
			RandomTestUtil.nextDate());

		commerceInventoryReplenishmentItem.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceInventoryReplenishmentItem.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		commerceInventoryReplenishmentItem.setAvailabilityDate(
			RandomTestUtil.nextDate());

		commerceInventoryReplenishmentItem.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceInventoryReplenishmentItem.setSku(
			RandomTestUtil.randomString());

		commerceInventoryReplenishmentItem.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryReplenishmentItems.add(
			_persistence.update(commerceInventoryReplenishmentItem));

		return commerceInventoryReplenishmentItem;
	}

	private List<CommerceInventoryReplenishmentItem>
		_commerceInventoryReplenishmentItems =
			new ArrayList<CommerceInventoryReplenishmentItem>();
	private CommerceInventoryReplenishmentItemPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}