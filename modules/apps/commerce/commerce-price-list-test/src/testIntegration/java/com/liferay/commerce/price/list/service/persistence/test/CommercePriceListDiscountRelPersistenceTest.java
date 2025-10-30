/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.NoSuchPriceListDiscountRelException;
import com.liferay.commerce.price.list.model.CommercePriceListDiscountRel;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListDiscountRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListDiscountRelUtil;
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
public class CommercePriceListDiscountRelPersistenceTest {

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
		_persistence = CommercePriceListDiscountRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceListDiscountRel> iterator =
			_commercePriceListDiscountRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			_persistence.create(pk);

		Assert.assertNotNull(commercePriceListDiscountRel);

		Assert.assertEquals(commercePriceListDiscountRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		_persistence.remove(newCommercePriceListDiscountRel);

		CommercePriceListDiscountRel existingCommercePriceListDiscountRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListDiscountRel.getPrimaryKey());

		Assert.assertNull(existingCommercePriceListDiscountRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceListDiscountRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			_persistence.create(pk);

		newCommercePriceListDiscountRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setUuid(RandomTestUtil.randomString());

		newCommercePriceListDiscountRel.setCompanyId(RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setUserId(RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePriceListDiscountRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePriceListDiscountRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePriceListDiscountRel.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		newCommercePriceListDiscountRel.setOrder(RandomTestUtil.nextInt());

		newCommercePriceListDiscountRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListDiscountRels.add(
			_persistence.update(newCommercePriceListDiscountRel));

		CommercePriceListDiscountRel existingCommercePriceListDiscountRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getMvccVersion(),
			newCommercePriceListDiscountRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getCtCollectionId(),
			newCommercePriceListDiscountRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getUuid(),
			newCommercePriceListDiscountRel.getUuid());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.
				getCommercePriceListDiscountRelId(),
			newCommercePriceListDiscountRel.
				getCommercePriceListDiscountRelId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getCompanyId(),
			newCommercePriceListDiscountRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getUserId(),
			newCommercePriceListDiscountRel.getUserId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getUserName(),
			newCommercePriceListDiscountRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListDiscountRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePriceListDiscountRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListDiscountRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePriceListDiscountRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getCommerceDiscountId(),
			newCommercePriceListDiscountRel.getCommerceDiscountId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getCommercePriceListId(),
			newCommercePriceListDiscountRel.getCommercePriceListId());
		Assert.assertEquals(
			existingCommercePriceListDiscountRel.getOrder(),
			newCommercePriceListDiscountRel.getOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListDiscountRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommercePriceListDiscountRel.getLastPublishDate()));
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
	public void testCountByCDI_CPI() throws Exception {
		_persistence.countByCDI_CPI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCDI_CPI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		CommercePriceListDiscountRel existingCommercePriceListDiscountRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListDiscountRel,
			newCommercePriceListDiscountRel);
	}

	@Test(expected = NoSuchPriceListDiscountRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceListDiscountRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePriceListDiscountRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"commercePriceListDiscountRelId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commerceDiscountId", true, "commercePriceListId", true, "order",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		CommercePriceListDiscountRel existingCommercePriceListDiscountRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListDiscountRel,
			newCommercePriceListDiscountRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListDiscountRel missingCommercePriceListDiscountRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceListDiscountRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceListDiscountRel newCommercePriceListDiscountRel1 =
			addCommercePriceListDiscountRel();
		CommercePriceListDiscountRel newCommercePriceListDiscountRel2 =
			addCommercePriceListDiscountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListDiscountRel1.getPrimaryKey());
		primaryKeys.add(newCommercePriceListDiscountRel2.getPrimaryKey());

		Map<Serializable, CommercePriceListDiscountRel>
			commercePriceListDiscountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commercePriceListDiscountRels.size());
		Assert.assertEquals(
			newCommercePriceListDiscountRel1,
			commercePriceListDiscountRels.get(
				newCommercePriceListDiscountRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceListDiscountRel2,
			commercePriceListDiscountRels.get(
				newCommercePriceListDiscountRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceListDiscountRel>
			commercePriceListDiscountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListDiscountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListDiscountRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceListDiscountRel>
			commercePriceListDiscountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListDiscountRels.size());
		Assert.assertEquals(
			newCommercePriceListDiscountRel,
			commercePriceListDiscountRels.get(
				newCommercePriceListDiscountRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceListDiscountRel>
			commercePriceListDiscountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListDiscountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListDiscountRel.getPrimaryKey());

		Map<Serializable, CommercePriceListDiscountRel>
			commercePriceListDiscountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListDiscountRels.size());
		Assert.assertEquals(
			newCommercePriceListDiscountRel,
			commercePriceListDiscountRels.get(
				newCommercePriceListDiscountRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceListDiscountRel newCommercePriceListDiscountRel =
			addCommercePriceListDiscountRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceListDiscountRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePriceListDiscountRel commercePriceListDiscountRel) {

		Assert.assertEquals(
			Long.valueOf(commercePriceListDiscountRel.getCommerceDiscountId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListDiscountRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceDiscountId"));
		Assert.assertEquals(
			Long.valueOf(commercePriceListDiscountRel.getCommercePriceListId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListDiscountRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePriceListId"));
	}

	protected CommercePriceListDiscountRel addCommercePriceListDiscountRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePriceListDiscountRel commercePriceListDiscountRel =
			_persistence.create(pk);

		commercePriceListDiscountRel.setMvccVersion(RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setUuid(RandomTestUtil.randomString());

		commercePriceListDiscountRel.setCompanyId(RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setUserId(RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setUserName(RandomTestUtil.randomString());

		commercePriceListDiscountRel.setCreateDate(RandomTestUtil.nextDate());

		commercePriceListDiscountRel.setModifiedDate(RandomTestUtil.nextDate());

		commercePriceListDiscountRel.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		commercePriceListDiscountRel.setOrder(RandomTestUtil.nextInt());

		commercePriceListDiscountRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListDiscountRels.add(
			_persistence.update(commercePriceListDiscountRel));

		return commercePriceListDiscountRel;
	}

	private List<CommercePriceListDiscountRel> _commercePriceListDiscountRels =
		new ArrayList<CommercePriceListDiscountRel>();
	private CommercePriceListDiscountRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}