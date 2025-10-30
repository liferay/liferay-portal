/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchAvailabilityEstimateException;
import com.liferay.commerce.model.CommerceAvailabilityEstimate;
import com.liferay.commerce.service.persistence.CommerceAvailabilityEstimatePersistence;
import com.liferay.commerce.service.persistence.CommerceAvailabilityEstimateUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommerceAvailabilityEstimatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceAvailabilityEstimateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceAvailabilityEstimate> iterator =
			_commerceAvailabilityEstimates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_persistence.create(pk);

		Assert.assertNotNull(commerceAvailabilityEstimate);

		Assert.assertEquals(commerceAvailabilityEstimate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			addCommerceAvailabilityEstimate();

		_persistence.remove(newCommerceAvailabilityEstimate);

		CommerceAvailabilityEstimate existingCommerceAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(
				newCommerceAvailabilityEstimate.getPrimaryKey());

		Assert.assertNull(existingCommerceAvailabilityEstimate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceAvailabilityEstimate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			_persistence.create(pk);

		newCommerceAvailabilityEstimate.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceAvailabilityEstimate.setUuid(RandomTestUtil.randomString());

		newCommerceAvailabilityEstimate.setCompanyId(RandomTestUtil.nextLong());

		newCommerceAvailabilityEstimate.setUserId(RandomTestUtil.nextLong());

		newCommerceAvailabilityEstimate.setUserName(
			RandomTestUtil.randomString());

		newCommerceAvailabilityEstimate.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceAvailabilityEstimate.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceAvailabilityEstimate.setTitle(RandomTestUtil.randomString());

		newCommerceAvailabilityEstimate.setPriority(
			RandomTestUtil.nextDouble());

		newCommerceAvailabilityEstimate.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commerceAvailabilityEstimates.add(
			_persistence.update(newCommerceAvailabilityEstimate));

		CommerceAvailabilityEstimate existingCommerceAvailabilityEstimate =
			_persistence.findByPrimaryKey(
				newCommerceAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getMvccVersion(),
			newCommerceAvailabilityEstimate.getMvccVersion());
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getUuid(),
			newCommerceAvailabilityEstimate.getUuid());
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.
				getCommerceAvailabilityEstimateId(),
			newCommerceAvailabilityEstimate.
				getCommerceAvailabilityEstimateId());
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getCompanyId(),
			newCommerceAvailabilityEstimate.getCompanyId());
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getUserId(),
			newCommerceAvailabilityEstimate.getUserId());
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getUserName(),
			newCommerceAvailabilityEstimate.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceAvailabilityEstimate.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceAvailabilityEstimate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceAvailabilityEstimate.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceAvailabilityEstimate.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceAvailabilityEstimate.getTitle(),
			newCommerceAvailabilityEstimate.getTitle());
		AssertUtils.assertEquals(
			existingCommerceAvailabilityEstimate.getPriority(),
			newCommerceAvailabilityEstimate.getPriority());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceAvailabilityEstimate.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommerceAvailabilityEstimate.getLastPublishDate()));
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			addCommerceAvailabilityEstimate();

		CommerceAvailabilityEstimate existingCommerceAvailabilityEstimate =
			_persistence.findByPrimaryKey(
				newCommerceAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAvailabilityEstimate,
			newCommerceAvailabilityEstimate);
	}

	@Test(expected = NoSuchAvailabilityEstimateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceAvailabilityEstimate>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceAvailabilityEstimate", "mvccVersion", true, "uuid", true,
			"commerceAvailabilityEstimateId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"title", true, "priority", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			addCommerceAvailabilityEstimate();

		CommerceAvailabilityEstimate existingCommerceAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(
				newCommerceAvailabilityEstimate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAvailabilityEstimate,
			newCommerceAvailabilityEstimate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAvailabilityEstimate missingCommerceAvailabilityEstimate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceAvailabilityEstimate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate1 =
			addCommerceAvailabilityEstimate();
		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate2 =
			addCommerceAvailabilityEstimate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAvailabilityEstimate1.getPrimaryKey());
		primaryKeys.add(newCommerceAvailabilityEstimate2.getPrimaryKey());

		Map<Serializable, CommerceAvailabilityEstimate>
			commerceAvailabilityEstimates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceAvailabilityEstimates.size());
		Assert.assertEquals(
			newCommerceAvailabilityEstimate1,
			commerceAvailabilityEstimates.get(
				newCommerceAvailabilityEstimate1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceAvailabilityEstimate2,
			commerceAvailabilityEstimates.get(
				newCommerceAvailabilityEstimate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceAvailabilityEstimate>
			commerceAvailabilityEstimates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceAvailabilityEstimates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			addCommerceAvailabilityEstimate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAvailabilityEstimate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceAvailabilityEstimate>
			commerceAvailabilityEstimates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceAvailabilityEstimates.size());
		Assert.assertEquals(
			newCommerceAvailabilityEstimate,
			commerceAvailabilityEstimates.get(
				newCommerceAvailabilityEstimate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceAvailabilityEstimate>
			commerceAvailabilityEstimates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceAvailabilityEstimates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceAvailabilityEstimate newCommerceAvailabilityEstimate =
			addCommerceAvailabilityEstimate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAvailabilityEstimate.getPrimaryKey());

		Map<Serializable, CommerceAvailabilityEstimate>
			commerceAvailabilityEstimates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceAvailabilityEstimates.size());
		Assert.assertEquals(
			newCommerceAvailabilityEstimate,
			commerceAvailabilityEstimates.get(
				newCommerceAvailabilityEstimate.getPrimaryKey()));
	}

	protected CommerceAvailabilityEstimate addCommerceAvailabilityEstimate()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceAvailabilityEstimate commerceAvailabilityEstimate =
			_persistence.create(pk);

		commerceAvailabilityEstimate.setMvccVersion(RandomTestUtil.nextLong());

		commerceAvailabilityEstimate.setUuid(RandomTestUtil.randomString());

		commerceAvailabilityEstimate.setCompanyId(RandomTestUtil.nextLong());

		commerceAvailabilityEstimate.setUserId(RandomTestUtil.nextLong());

		commerceAvailabilityEstimate.setUserName(RandomTestUtil.randomString());

		commerceAvailabilityEstimate.setCreateDate(RandomTestUtil.nextDate());

		commerceAvailabilityEstimate.setModifiedDate(RandomTestUtil.nextDate());

		commerceAvailabilityEstimate.setTitle(RandomTestUtil.randomString());

		commerceAvailabilityEstimate.setPriority(RandomTestUtil.nextDouble());

		commerceAvailabilityEstimate.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commerceAvailabilityEstimates.add(
			_persistence.update(commerceAvailabilityEstimate));

		return commerceAvailabilityEstimate;
	}

	private List<CommerceAvailabilityEstimate> _commerceAvailabilityEstimates =
		new ArrayList<CommerceAvailabilityEstimate>();
	private CommerceAvailabilityEstimatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}