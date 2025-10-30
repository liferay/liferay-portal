/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
import com.liferay.ratings.kernel.exception.NoSuchEntryException;
import com.liferay.ratings.kernel.model.RatingsEntry;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryPersistence;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryUtil;

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
public class RatingsEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RatingsEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RatingsEntry> iterator = _ratingsEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsEntry ratingsEntry = _persistence.create(pk);

		Assert.assertNotNull(ratingsEntry);

		Assert.assertEquals(ratingsEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RatingsEntry newRatingsEntry = addRatingsEntry();

		_persistence.remove(newRatingsEntry);

		RatingsEntry existingRatingsEntry = _persistence.fetchByPrimaryKey(
			newRatingsEntry.getPrimaryKey());

		Assert.assertNull(existingRatingsEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRatingsEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsEntry newRatingsEntry = _persistence.create(pk);

		newRatingsEntry.setMvccVersion(RandomTestUtil.nextLong());

		newRatingsEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newRatingsEntry.setUuid(RandomTestUtil.randomString());

		newRatingsEntry.setCompanyId(RandomTestUtil.nextLong());

		newRatingsEntry.setUserId(RandomTestUtil.nextLong());

		newRatingsEntry.setUserName(RandomTestUtil.randomString());

		newRatingsEntry.setCreateDate(RandomTestUtil.nextDate());

		newRatingsEntry.setModifiedDate(RandomTestUtil.nextDate());

		newRatingsEntry.setClassNameId(RandomTestUtil.nextLong());

		newRatingsEntry.setClassPK(RandomTestUtil.nextLong());

		newRatingsEntry.setScore(RandomTestUtil.nextDouble());

		_ratingsEntries.add(_persistence.update(newRatingsEntry));

		RatingsEntry existingRatingsEntry = _persistence.findByPrimaryKey(
			newRatingsEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRatingsEntry.getMvccVersion(),
			newRatingsEntry.getMvccVersion());
		Assert.assertEquals(
			existingRatingsEntry.getCtCollectionId(),
			newRatingsEntry.getCtCollectionId());
		Assert.assertEquals(
			existingRatingsEntry.getUuid(), newRatingsEntry.getUuid());
		Assert.assertEquals(
			existingRatingsEntry.getEntryId(), newRatingsEntry.getEntryId());
		Assert.assertEquals(
			existingRatingsEntry.getCompanyId(),
			newRatingsEntry.getCompanyId());
		Assert.assertEquals(
			existingRatingsEntry.getUserId(), newRatingsEntry.getUserId());
		Assert.assertEquals(
			existingRatingsEntry.getUserName(), newRatingsEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRatingsEntry.getCreateDate()),
			Time.getShortTimestamp(newRatingsEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRatingsEntry.getModifiedDate()),
			Time.getShortTimestamp(newRatingsEntry.getModifiedDate()));
		Assert.assertEquals(
			existingRatingsEntry.getClassNameId(),
			newRatingsEntry.getClassNameId());
		Assert.assertEquals(
			existingRatingsEntry.getClassPK(), newRatingsEntry.getClassPK());
		AssertUtils.assertEquals(
			existingRatingsEntry.getScore(), newRatingsEntry.getScore());
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
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByU_C_C() throws Exception {
		_persistence.countByU_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByU_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByU_C_CArrayable() throws Exception {
		_persistence.countByU_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByC_C_S() throws Exception {
		_persistence.countByC_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextDouble());

		_persistence.countByC_C_S(0L, 0L, 0D);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RatingsEntry newRatingsEntry = addRatingsEntry();

		RatingsEntry existingRatingsEntry = _persistence.findByPrimaryKey(
			newRatingsEntry.getPrimaryKey());

		Assert.assertEquals(existingRatingsEntry, newRatingsEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RatingsEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RatingsEntry", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "entryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "score", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RatingsEntry newRatingsEntry = addRatingsEntry();

		RatingsEntry existingRatingsEntry = _persistence.fetchByPrimaryKey(
			newRatingsEntry.getPrimaryKey());

		Assert.assertEquals(existingRatingsEntry, newRatingsEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsEntry missingRatingsEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRatingsEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RatingsEntry newRatingsEntry1 = addRatingsEntry();
		RatingsEntry newRatingsEntry2 = addRatingsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsEntry1.getPrimaryKey());
		primaryKeys.add(newRatingsEntry2.getPrimaryKey());

		Map<Serializable, RatingsEntry> ratingsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ratingsEntries.size());
		Assert.assertEquals(
			newRatingsEntry1,
			ratingsEntries.get(newRatingsEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRatingsEntry2,
			ratingsEntries.get(newRatingsEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RatingsEntry> ratingsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ratingsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RatingsEntry newRatingsEntry = addRatingsEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RatingsEntry> ratingsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ratingsEntries.size());
		Assert.assertEquals(
			newRatingsEntry,
			ratingsEntries.get(newRatingsEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RatingsEntry> ratingsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ratingsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RatingsEntry newRatingsEntry = addRatingsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsEntry.getPrimaryKey());

		Map<Serializable, RatingsEntry> ratingsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ratingsEntries.size());
		Assert.assertEquals(
			newRatingsEntry,
			ratingsEntries.get(newRatingsEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RatingsEntry newRatingsEntry = addRatingsEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newRatingsEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(RatingsEntry ratingsEntry) {
		Assert.assertEquals(
			Long.valueOf(ratingsEntry.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				ratingsEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(ratingsEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ratingsEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(ratingsEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ratingsEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected RatingsEntry addRatingsEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsEntry ratingsEntry = _persistence.create(pk);

		ratingsEntry.setMvccVersion(RandomTestUtil.nextLong());

		ratingsEntry.setCtCollectionId(RandomTestUtil.nextLong());

		ratingsEntry.setUuid(RandomTestUtil.randomString());

		ratingsEntry.setCompanyId(RandomTestUtil.nextLong());

		ratingsEntry.setUserId(RandomTestUtil.nextLong());

		ratingsEntry.setUserName(RandomTestUtil.randomString());

		ratingsEntry.setCreateDate(RandomTestUtil.nextDate());

		ratingsEntry.setModifiedDate(RandomTestUtil.nextDate());

		ratingsEntry.setClassNameId(RandomTestUtil.nextLong());

		ratingsEntry.setClassPK(RandomTestUtil.nextLong());

		ratingsEntry.setScore(RandomTestUtil.nextDouble());

		_ratingsEntries.add(_persistence.update(ratingsEntry));

		return ratingsEntry;
	}

	private List<RatingsEntry> _ratingsEntries = new ArrayList<RatingsEntry>();
	private RatingsEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}