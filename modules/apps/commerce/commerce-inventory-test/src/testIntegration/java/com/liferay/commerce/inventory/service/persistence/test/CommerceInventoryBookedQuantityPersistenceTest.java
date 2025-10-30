/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.NoSuchInventoryBookedQuantityException;
import com.liferay.commerce.inventory.model.CommerceInventoryBookedQuantity;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryBookedQuantityPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryBookedQuantityUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CommerceInventoryBookedQuantityPersistenceTest {

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
		_persistence = CommerceInventoryBookedQuantityUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceInventoryBookedQuantity> iterator =
			_commerceInventoryBookedQuantities.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_persistence.create(pk);

		Assert.assertNotNull(commerceInventoryBookedQuantity);

		Assert.assertEquals(
			commerceInventoryBookedQuantity.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			addCommerceInventoryBookedQuantity();

		_persistence.remove(newCommerceInventoryBookedQuantity);

		CommerceInventoryBookedQuantity
			existingCommerceInventoryBookedQuantity =
				_persistence.fetchByPrimaryKey(
					newCommerceInventoryBookedQuantity.getPrimaryKey());

		Assert.assertNull(existingCommerceInventoryBookedQuantity);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceInventoryBookedQuantity();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			_persistence.create(pk);

		newCommerceInventoryBookedQuantity.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceInventoryBookedQuantity.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceInventoryBookedQuantity.setUserId(RandomTestUtil.nextLong());

		newCommerceInventoryBookedQuantity.setUserName(
			RandomTestUtil.randomString());

		newCommerceInventoryBookedQuantity.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryBookedQuantity.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryBookedQuantity.setBookedNote(
			RandomTestUtil.randomString());

		newCommerceInventoryBookedQuantity.setExpirationDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryBookedQuantity.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceInventoryBookedQuantity.setSku(
			RandomTestUtil.randomString());

		newCommerceInventoryBookedQuantity.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryBookedQuantities.add(
			_persistence.update(newCommerceInventoryBookedQuantity));

		CommerceInventoryBookedQuantity
			existingCommerceInventoryBookedQuantity =
				_persistence.findByPrimaryKey(
					newCommerceInventoryBookedQuantity.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getMvccVersion(),
			newCommerceInventoryBookedQuantity.getMvccVersion());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId(),
			newCommerceInventoryBookedQuantity.
				getCommerceInventoryBookedQuantityId());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getCompanyId(),
			newCommerceInventoryBookedQuantity.getCompanyId());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getUserId(),
			newCommerceInventoryBookedQuantity.getUserId());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getUserName(),
			newCommerceInventoryBookedQuantity.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryBookedQuantity.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceInventoryBookedQuantity.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryBookedQuantity.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceInventoryBookedQuantity.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getBookedNote(),
			newCommerceInventoryBookedQuantity.getBookedNote());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryBookedQuantity.getExpirationDate()),
			Time.getShortTimestamp(
				newCommerceInventoryBookedQuantity.getExpirationDate()));
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getQuantity(),
			newCommerceInventoryBookedQuantity.getQuantity());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getSku(),
			newCommerceInventoryBookedQuantity.getSku());
		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity.getUnitOfMeasureKey(),
			newCommerceInventoryBookedQuantity.getUnitOfMeasureKey());
	}

	@Test
	public void testCountByLtExpirationDate() throws Exception {
		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());

		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			addCommerceInventoryBookedQuantity();

		CommerceInventoryBookedQuantity
			existingCommerceInventoryBookedQuantity =
				_persistence.findByPrimaryKey(
					newCommerceInventoryBookedQuantity.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity,
			newCommerceInventoryBookedQuantity);
	}

	@Test(expected = NoSuchInventoryBookedQuantityException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceInventoryBookedQuantity>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CIBookedQuantity", "mvccVersion", true,
			"commerceInventoryBookedQuantityId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "bookedNote", true, "expirationDate", true,
			"quantity", true, "sku", true, "unitOfMeasureKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			addCommerceInventoryBookedQuantity();

		CommerceInventoryBookedQuantity
			existingCommerceInventoryBookedQuantity =
				_persistence.fetchByPrimaryKey(
					newCommerceInventoryBookedQuantity.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryBookedQuantity,
			newCommerceInventoryBookedQuantity);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryBookedQuantity missingCommerceInventoryBookedQuantity =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceInventoryBookedQuantity);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity1 =
			addCommerceInventoryBookedQuantity();
		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity2 =
			addCommerceInventoryBookedQuantity();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryBookedQuantity1.getPrimaryKey());
		primaryKeys.add(newCommerceInventoryBookedQuantity2.getPrimaryKey());

		Map<Serializable, CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceInventoryBookedQuantities.size());
		Assert.assertEquals(
			newCommerceInventoryBookedQuantity1,
			commerceInventoryBookedQuantities.get(
				newCommerceInventoryBookedQuantity1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceInventoryBookedQuantity2,
			commerceInventoryBookedQuantities.get(
				newCommerceInventoryBookedQuantity2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryBookedQuantities.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			addCommerceInventoryBookedQuantity();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryBookedQuantity.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryBookedQuantities.size());
		Assert.assertEquals(
			newCommerceInventoryBookedQuantity,
			commerceInventoryBookedQuantities.get(
				newCommerceInventoryBookedQuantity.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryBookedQuantities.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceInventoryBookedQuantity newCommerceInventoryBookedQuantity =
			addCommerceInventoryBookedQuantity();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryBookedQuantity.getPrimaryKey());

		Map<Serializable, CommerceInventoryBookedQuantity>
			commerceInventoryBookedQuantities = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryBookedQuantities.size());
		Assert.assertEquals(
			newCommerceInventoryBookedQuantity,
			commerceInventoryBookedQuantities.get(
				newCommerceInventoryBookedQuantity.getPrimaryKey()));
	}

	protected CommerceInventoryBookedQuantity
			addCommerceInventoryBookedQuantity()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceInventoryBookedQuantity commerceInventoryBookedQuantity =
			_persistence.create(pk);

		commerceInventoryBookedQuantity.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceInventoryBookedQuantity.setCompanyId(RandomTestUtil.nextLong());

		commerceInventoryBookedQuantity.setUserId(RandomTestUtil.nextLong());

		commerceInventoryBookedQuantity.setUserName(
			RandomTestUtil.randomString());

		commerceInventoryBookedQuantity.setCreateDate(
			RandomTestUtil.nextDate());

		commerceInventoryBookedQuantity.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceInventoryBookedQuantity.setBookedNote(
			RandomTestUtil.randomString());

		commerceInventoryBookedQuantity.setExpirationDate(
			RandomTestUtil.nextDate());

		commerceInventoryBookedQuantity.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceInventoryBookedQuantity.setSku(RandomTestUtil.randomString());

		commerceInventoryBookedQuantity.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_commerceInventoryBookedQuantities.add(
			_persistence.update(commerceInventoryBookedQuantity));

		return commerceInventoryBookedQuantity;
	}

	private List<CommerceInventoryBookedQuantity>
		_commerceInventoryBookedQuantities =
			new ArrayList<CommerceInventoryBookedQuantity>();
	private CommerceInventoryBookedQuantityPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}