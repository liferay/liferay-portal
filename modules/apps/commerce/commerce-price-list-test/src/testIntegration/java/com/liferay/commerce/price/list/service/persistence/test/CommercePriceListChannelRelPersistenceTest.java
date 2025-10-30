/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.NoSuchPriceListChannelRelException;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListChannelRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListChannelRelUtil;
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
public class CommercePriceListChannelRelPersistenceTest {

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
		_persistence = CommercePriceListChannelRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceListChannelRel> iterator =
			_commercePriceListChannelRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListChannelRel commercePriceListChannelRel =
			_persistence.create(pk);

		Assert.assertNotNull(commercePriceListChannelRel);

		Assert.assertEquals(commercePriceListChannelRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		_persistence.remove(newCommercePriceListChannelRel);

		CommercePriceListChannelRel existingCommercePriceListChannelRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListChannelRel.getPrimaryKey());

		Assert.assertNull(existingCommercePriceListChannelRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceListChannelRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListChannelRel newCommercePriceListChannelRel =
			_persistence.create(pk);

		newCommercePriceListChannelRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setUuid(RandomTestUtil.randomString());

		newCommercePriceListChannelRel.setCompanyId(RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setUserId(RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePriceListChannelRel.setCreateDate(RandomTestUtil.nextDate());

		newCommercePriceListChannelRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePriceListChannelRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		newCommercePriceListChannelRel.setOrder(RandomTestUtil.nextInt());

		newCommercePriceListChannelRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListChannelRels.add(
			_persistence.update(newCommercePriceListChannelRel));

		CommercePriceListChannelRel existingCommercePriceListChannelRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListChannelRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListChannelRel.getMvccVersion(),
			newCommercePriceListChannelRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getCtCollectionId(),
			newCommercePriceListChannelRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getUuid(),
			newCommercePriceListChannelRel.getUuid());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.
				getCommercePriceListChannelRelId(),
			newCommercePriceListChannelRel.getCommercePriceListChannelRelId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getCompanyId(),
			newCommercePriceListChannelRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getUserId(),
			newCommercePriceListChannelRel.getUserId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getUserName(),
			newCommercePriceListChannelRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListChannelRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePriceListChannelRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListChannelRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePriceListChannelRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getCommerceChannelId(),
			newCommercePriceListChannelRel.getCommerceChannelId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getCommercePriceListId(),
			newCommercePriceListChannelRel.getCommercePriceListId());
		Assert.assertEquals(
			existingCommercePriceListChannelRel.getOrder(),
			newCommercePriceListChannelRel.getOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListChannelRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommercePriceListChannelRel.getLastPublishDate()));
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
	public void testCountByCCI_CPI() throws Exception {
		_persistence.countByCCI_CPI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCCI_CPI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		CommercePriceListChannelRel existingCommercePriceListChannelRel =
			_persistence.findByPrimaryKey(
				newCommercePriceListChannelRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListChannelRel,
			newCommercePriceListChannelRel);
	}

	@Test(expected = NoSuchPriceListChannelRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceListChannelRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePriceListChannelRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"CommercePriceListChannelRelId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commerceChannelId", true, "commercePriceListId", true, "order",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		CommercePriceListChannelRel existingCommercePriceListChannelRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceListChannelRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListChannelRel,
			newCommercePriceListChannelRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListChannelRel missingCommercePriceListChannelRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceListChannelRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceListChannelRel newCommercePriceListChannelRel1 =
			addCommercePriceListChannelRel();
		CommercePriceListChannelRel newCommercePriceListChannelRel2 =
			addCommercePriceListChannelRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListChannelRel1.getPrimaryKey());
		primaryKeys.add(newCommercePriceListChannelRel2.getPrimaryKey());

		Map<Serializable, CommercePriceListChannelRel>
			commercePriceListChannelRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commercePriceListChannelRels.size());
		Assert.assertEquals(
			newCommercePriceListChannelRel1,
			commercePriceListChannelRels.get(
				newCommercePriceListChannelRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceListChannelRel2,
			commercePriceListChannelRels.get(
				newCommercePriceListChannelRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceListChannelRel>
			commercePriceListChannelRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListChannelRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListChannelRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceListChannelRel>
			commercePriceListChannelRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListChannelRels.size());
		Assert.assertEquals(
			newCommercePriceListChannelRel,
			commercePriceListChannelRels.get(
				newCommercePriceListChannelRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceListChannelRel>
			commercePriceListChannelRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePriceListChannelRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceListChannelRel.getPrimaryKey());

		Map<Serializable, CommercePriceListChannelRel>
			commercePriceListChannelRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePriceListChannelRels.size());
		Assert.assertEquals(
			newCommercePriceListChannelRel,
			commercePriceListChannelRels.get(
				newCommercePriceListChannelRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceListChannelRel newCommercePriceListChannelRel =
			addCommercePriceListChannelRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceListChannelRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePriceListChannelRel commercePriceListChannelRel) {

		Assert.assertEquals(
			Long.valueOf(commercePriceListChannelRel.getCommerceChannelId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListChannelRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceChannelId"));
		Assert.assertEquals(
			Long.valueOf(commercePriceListChannelRel.getCommercePriceListId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListChannelRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePriceListId"));
	}

	protected CommercePriceListChannelRel addCommercePriceListChannelRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePriceListChannelRel commercePriceListChannelRel =
			_persistence.create(pk);

		commercePriceListChannelRel.setMvccVersion(RandomTestUtil.nextLong());

		commercePriceListChannelRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commercePriceListChannelRel.setUuid(RandomTestUtil.randomString());

		commercePriceListChannelRel.setCompanyId(RandomTestUtil.nextLong());

		commercePriceListChannelRel.setUserId(RandomTestUtil.nextLong());

		commercePriceListChannelRel.setUserName(RandomTestUtil.randomString());

		commercePriceListChannelRel.setCreateDate(RandomTestUtil.nextDate());

		commercePriceListChannelRel.setModifiedDate(RandomTestUtil.nextDate());

		commercePriceListChannelRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		commercePriceListChannelRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		commercePriceListChannelRel.setOrder(RandomTestUtil.nextInt());

		commercePriceListChannelRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListChannelRels.add(
			_persistence.update(commercePriceListChannelRel));

		return commercePriceListChannelRel;
	}

	private List<CommercePriceListChannelRel> _commercePriceListChannelRels =
		new ArrayList<CommercePriceListChannelRel>();
	private CommercePriceListChannelRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}