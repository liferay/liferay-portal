/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseRelException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouseRel;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseRelPersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CommerceInventoryWarehouseRelPersistenceTest {

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
		_persistence = CommerceInventoryWarehouseRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceInventoryWarehouseRel> iterator =
			_commerceInventoryWarehouseRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_persistence.create(pk);

		Assert.assertNotNull(commerceInventoryWarehouseRel);

		Assert.assertEquals(commerceInventoryWarehouseRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		_persistence.remove(newCommerceInventoryWarehouseRel);

		CommerceInventoryWarehouseRel existingCommerceInventoryWarehouseRel =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouseRel.getPrimaryKey());

		Assert.assertNull(existingCommerceInventoryWarehouseRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceInventoryWarehouseRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			_persistence.create(pk);

		newCommerceInventoryWarehouseRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseRel.setUserId(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouseRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryWarehouseRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryWarehouseRel.setClassNameId(
			RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseRel.setClassPK(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouseRel.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		_commerceInventoryWarehouseRels.add(
			_persistence.update(newCommerceInventoryWarehouseRel));

		CommerceInventoryWarehouseRel existingCommerceInventoryWarehouseRel =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getMvccVersion(),
			newCommerceInventoryWarehouseRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.
				getCommerceInventoryWarehouseRelId(),
			newCommerceInventoryWarehouseRel.
				getCommerceInventoryWarehouseRelId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getCompanyId(),
			newCommerceInventoryWarehouseRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getUserId(),
			newCommerceInventoryWarehouseRel.getUserId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getUserName(),
			newCommerceInventoryWarehouseRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouseRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouseRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouseRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouseRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getClassNameId(),
			newCommerceInventoryWarehouseRel.getClassNameId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.getClassPK(),
			newCommerceInventoryWarehouseRel.getClassPK());
		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel.
				getCommerceInventoryWarehouseId(),
			newCommerceInventoryWarehouseRel.getCommerceInventoryWarehouseId());
	}

	@Test
	public void testCountByCommerceInventoryWarehouseId() throws Exception {
		_persistence.countByCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceInventoryWarehouseId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_CIWI() throws Exception {
		_persistence.countByC_C_CIWI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_CIWI(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		CommerceInventoryWarehouseRel existingCommerceInventoryWarehouseRel =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel,
			newCommerceInventoryWarehouseRel);
	}

	@Test(expected = NoSuchInventoryWarehouseRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceInventoryWarehouseRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CIWarehouseRel", "mvccVersion", true,
			"commerceInventoryWarehouseRelId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"commerceInventoryWarehouseId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		CommerceInventoryWarehouseRel existingCommerceInventoryWarehouseRel =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouseRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouseRel,
			newCommerceInventoryWarehouseRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseRel missingCommerceInventoryWarehouseRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceInventoryWarehouseRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel1 =
			addCommerceInventoryWarehouseRel();
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel2 =
			addCommerceInventoryWarehouseRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseRel1.getPrimaryKey());
		primaryKeys.add(newCommerceInventoryWarehouseRel2.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouseRel>
			commerceInventoryWarehouseRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceInventoryWarehouseRels.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseRel1,
			commerceInventoryWarehouseRels.get(
				newCommerceInventoryWarehouseRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceInventoryWarehouseRel2,
			commerceInventoryWarehouseRels.get(
				newCommerceInventoryWarehouseRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceInventoryWarehouseRel>
			commerceInventoryWarehouseRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouseRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceInventoryWarehouseRel>
			commerceInventoryWarehouseRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouseRels.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseRel,
			commerceInventoryWarehouseRels.get(
				newCommerceInventoryWarehouseRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceInventoryWarehouseRel>
			commerceInventoryWarehouseRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouseRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouseRel.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouseRel>
			commerceInventoryWarehouseRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouseRels.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouseRel,
			commerceInventoryWarehouseRels.get(
				newCommerceInventoryWarehouseRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceInventoryWarehouseRel newCommerceInventoryWarehouseRel =
			addCommerceInventoryWarehouseRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouseRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel) {

		Assert.assertEquals(
			Long.valueOf(commerceInventoryWarehouseRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouseRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceInventoryWarehouseRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouseRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(
				commerceInventoryWarehouseRel.
					getCommerceInventoryWarehouseId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouseRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CIWarehouseId"));
	}

	protected CommerceInventoryWarehouseRel addCommerceInventoryWarehouseRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouseRel commerceInventoryWarehouseRel =
			_persistence.create(pk);

		commerceInventoryWarehouseRel.setMvccVersion(RandomTestUtil.nextLong());

		commerceInventoryWarehouseRel.setCompanyId(RandomTestUtil.nextLong());

		commerceInventoryWarehouseRel.setUserId(RandomTestUtil.nextLong());

		commerceInventoryWarehouseRel.setUserName(
			RandomTestUtil.randomString());

		commerceInventoryWarehouseRel.setCreateDate(RandomTestUtil.nextDate());

		commerceInventoryWarehouseRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceInventoryWarehouseRel.setClassNameId(RandomTestUtil.nextLong());

		commerceInventoryWarehouseRel.setClassPK(RandomTestUtil.nextLong());

		commerceInventoryWarehouseRel.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		_commerceInventoryWarehouseRels.add(
			_persistence.update(commerceInventoryWarehouseRel));

		return commerceInventoryWarehouseRel;
	}

	private List<CommerceInventoryWarehouseRel>
		_commerceInventoryWarehouseRels =
			new ArrayList<CommerceInventoryWarehouseRel>();
	private CommerceInventoryWarehouseRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}