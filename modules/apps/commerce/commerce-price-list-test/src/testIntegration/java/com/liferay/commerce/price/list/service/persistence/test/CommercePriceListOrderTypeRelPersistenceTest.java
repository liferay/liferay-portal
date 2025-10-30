/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.NoSuchPriceListOrderTypeRelException;
import com.liferay.commerce.price.list.model.CommercePriceListOrderTypeRel;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListOrderTypeRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListOrderTypeRelUtil;
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
public class CommercePriceListOrderTypeRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.price.list.service"));

	@Before
	public void setUp() {
		_persistence = CommercePriceListOrderTypeRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceListOrderTypeRel> iterator =
			_commercePriceListOrderTypeRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			_persistence.create(pk);

		Assert.assertNotNull(commercePriceListOrderTypeRel);

		Assert.assertEquals(commercePriceListOrderTypeRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		_persistence.remove(newCommercePriceListOrderTypeRel);

		CommercePriceListOrderTypeRel existingCommercePriceListOrderTypeRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListOrderTypeRel.getPrimaryKey());

		Assert.assertNull(existingCommercePriceListOrderTypeRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceListOrderTypeRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			_persistence.create(pk);

		newCommercePriceListOrderTypeRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setUuid(RandomTestUtil.randomString());

		newCommercePriceListOrderTypeRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setUserId(RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePriceListOrderTypeRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePriceListOrderTypeRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePriceListOrderTypeRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setCommerceOrderTypeId(
			RandomTestUtil.nextLong());

		newCommercePriceListOrderTypeRel.setPriority(RandomTestUtil.nextInt());

		newCommercePriceListOrderTypeRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListOrderTypeRels.add(
			_persistence.update(newCommercePriceListOrderTypeRel));

		CommercePriceListOrderTypeRel existingCommercePriceListOrderTypeRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListOrderTypeRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getMvccVersion(),
			newCommercePriceListOrderTypeRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getCtCollectionId(),
			newCommercePriceListOrderTypeRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getUuid(),
			newCommercePriceListOrderTypeRel.getUuid());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.
				getCommercePriceListOrderTypeRelId(),
			newCommercePriceListOrderTypeRel.
				getCommercePriceListOrderTypeRelId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getCompanyId(),
			newCommercePriceListOrderTypeRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getUserId(),
			newCommercePriceListOrderTypeRel.getUserId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getUserName(),
			newCommercePriceListOrderTypeRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListOrderTypeRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePriceListOrderTypeRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListOrderTypeRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePriceListOrderTypeRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getCommercePriceListId(),
			newCommercePriceListOrderTypeRel.getCommercePriceListId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getCommerceOrderTypeId(),
			newCommercePriceListOrderTypeRel.getCommerceOrderTypeId());
		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel.getPriority(),
			newCommercePriceListOrderTypeRel.getPriority());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListOrderTypeRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommercePriceListOrderTypeRel.getLastPublishDate()));
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
	public void testCountByCommercePriceListId() throws Exception {
		_persistence.countByCommercePriceListId(RandomTestUtil.nextLong());

		_persistence.countByCommercePriceListId(0L);
	}

	@Test
	public void testCountByCPI_COTI() throws Exception {
		_persistence.countByCPI_COTI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCPI_COTI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		CommercePriceListOrderTypeRel existingCommercePriceListOrderTypeRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListOrderTypeRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel,
			newCommercePriceListOrderTypeRel);
	}

	@Test(expected = NoSuchPriceListOrderTypeRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceListOrderTypeRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePriceListOrderTypeRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"commercePriceListOrderTypeRelId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "commercePriceListId", true,
			"commerceOrderTypeId", true, "priority", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		CommercePriceListOrderTypeRel existingCommercePriceListOrderTypeRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListOrderTypeRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListOrderTypeRel,
			newCommercePriceListOrderTypeRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListOrderTypeRel missingCommercePriceListOrderTypeRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceListOrderTypeRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel1 =
			addCommercePriceListOrderTypeRel();
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel2 =
			addCommercePriceListOrderTypeRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListOrderTypeRel1.getPrimaryKey());
		primaryKeys.add(newCommercePriceListOrderTypeRel2.getPrimaryKey());

		Map<Serializable, CommercePriceListOrderTypeRel>
			commercePriceListOrderTypeRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commercePriceListOrderTypeRels.size());
		Assert.assertEquals(
			newCommercePriceListOrderTypeRel1,
			commercePriceListOrderTypeRels.get(
				newCommercePriceListOrderTypeRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceListOrderTypeRel2,
			commercePriceListOrderTypeRels.get(
				newCommercePriceListOrderTypeRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceListOrderTypeRel>
			commercePriceListOrderTypeRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListOrderTypeRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListOrderTypeRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceListOrderTypeRel>
			commercePriceListOrderTypeRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListOrderTypeRels.size());
		Assert.assertEquals(
			newCommercePriceListOrderTypeRel,
			commercePriceListOrderTypeRels.get(
				newCommercePriceListOrderTypeRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceListOrderTypeRel>
			commercePriceListOrderTypeRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListOrderTypeRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListOrderTypeRel.getPrimaryKey());

		Map<Serializable, CommercePriceListOrderTypeRel>
			commercePriceListOrderTypeRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListOrderTypeRels.size());
		Assert.assertEquals(
			newCommercePriceListOrderTypeRel,
			commercePriceListOrderTypeRels.get(
				newCommercePriceListOrderTypeRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceListOrderTypeRel newCommercePriceListOrderTypeRel =
			addCommercePriceListOrderTypeRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceListOrderTypeRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel) {

		Assert.assertEquals(
			Long.valueOf(
				commercePriceListOrderTypeRel.getCommercePriceListId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListOrderTypeRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePriceListId"));
		Assert.assertEquals(
			Long.valueOf(
				commercePriceListOrderTypeRel.getCommerceOrderTypeId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListOrderTypeRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceOrderTypeId"));
	}

	protected CommercePriceListOrderTypeRel addCommercePriceListOrderTypeRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePriceListOrderTypeRel commercePriceListOrderTypeRel =
			_persistence.create(pk);

		commercePriceListOrderTypeRel.setMvccVersion(RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setUuid(RandomTestUtil.randomString());

		commercePriceListOrderTypeRel.setCompanyId(RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setUserId(RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setUserName(
			RandomTestUtil.randomString());

		commercePriceListOrderTypeRel.setCreateDate(RandomTestUtil.nextDate());

		commercePriceListOrderTypeRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commercePriceListOrderTypeRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setCommerceOrderTypeId(
			RandomTestUtil.nextLong());

		commercePriceListOrderTypeRel.setPriority(RandomTestUtil.nextInt());

		commercePriceListOrderTypeRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListOrderTypeRels.add(
			_persistence.update(commercePriceListOrderTypeRel));

		return commercePriceListOrderTypeRel;
	}

	private List<CommercePriceListOrderTypeRel>
		_commercePriceListOrderTypeRels =
			new ArrayList<CommercePriceListOrderTypeRel>();
	private CommercePriceListOrderTypeRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}