/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.reading.time.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.reading.time.exception.NoSuchEntryException;
import com.liferay.reading.time.model.ReadingTimeEntry;
import com.liferay.reading.time.service.ReadingTimeEntryLocalServiceUtil;
import com.liferay.reading.time.service.persistence.ReadingTimeEntryPersistence;
import com.liferay.reading.time.service.persistence.ReadingTimeEntryUtil;

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
public class ReadingTimeEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.reading.time.service"));

	@Before
	public void setUp() {
		_persistence = ReadingTimeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ReadingTimeEntry> iterator = _readingTimeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReadingTimeEntry readingTimeEntry = _persistence.create(pk);

		Assert.assertNotNull(readingTimeEntry);

		Assert.assertEquals(readingTimeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		_persistence.remove(newReadingTimeEntry);

		ReadingTimeEntry existingReadingTimeEntry =
			_persistence.fetchByPrimaryKey(newReadingTimeEntry.getPrimaryKey());

		Assert.assertNull(existingReadingTimeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addReadingTimeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		newReadingTimeEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newReadingTimeEntry.setUuid(RandomTestUtil.randomString());

		newReadingTimeEntry.setGroupId(RandomTestUtil.nextLong());

		newReadingTimeEntry.setCompanyId(RandomTestUtil.nextLong());

		newReadingTimeEntry.setCreateDate(RandomTestUtil.nextDate());

		newReadingTimeEntry.setModifiedDate(RandomTestUtil.nextDate());

		newReadingTimeEntry.setClassNameId(RandomTestUtil.nextLong());

		newReadingTimeEntry.setClassPK(RandomTestUtil.nextLong());

		newReadingTimeEntry.setReadingTime(RandomTestUtil.nextLong());

		newReadingTimeEntry = _persistence.update(newReadingTimeEntry);

		_readingTimeEntries.add(newReadingTimeEntry);

		ReadingTimeEntry existingReadingTimeEntry =
			_persistence.findByPrimaryKey(newReadingTimeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingReadingTimeEntry.getMvccVersion(),
			newReadingTimeEntry.getMvccVersion());
		Assert.assertEquals(
			existingReadingTimeEntry.getCtCollectionId(),
			newReadingTimeEntry.getCtCollectionId());
		Assert.assertEquals(
			existingReadingTimeEntry.getUuid(), newReadingTimeEntry.getUuid());
		Assert.assertEquals(
			existingReadingTimeEntry.getReadingTimeEntryId(),
			newReadingTimeEntry.getReadingTimeEntryId());
		Assert.assertEquals(
			existingReadingTimeEntry.getGroupId(),
			newReadingTimeEntry.getGroupId());
		Assert.assertEquals(
			existingReadingTimeEntry.getCompanyId(),
			newReadingTimeEntry.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingReadingTimeEntry.getCreateDate()),
			Time.getShortTimestamp(newReadingTimeEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingReadingTimeEntry.getModifiedDate()),
			Time.getShortTimestamp(newReadingTimeEntry.getModifiedDate()));
		Assert.assertEquals(
			existingReadingTimeEntry.getClassNameId(),
			newReadingTimeEntry.getClassNameId());
		Assert.assertEquals(
			existingReadingTimeEntry.getClassPK(),
			newReadingTimeEntry.getClassPK());
		Assert.assertEquals(
			existingReadingTimeEntry.getReadingTime(),
			newReadingTimeEntry.getReadingTime());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		ReadingTimeEntry existingReadingTimeEntry =
			_persistence.findByPrimaryKey(newReadingTimeEntry.getPrimaryKey());

		Assert.assertEquals(existingReadingTimeEntry, newReadingTimeEntry);
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

	protected OrderByComparator<ReadingTimeEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ReadingTimeEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "readingTimeEntryId", true, "groupId", true,
			"companyId", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "readingTime", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		ReadingTimeEntry existingReadingTimeEntry =
			_persistence.fetchByPrimaryKey(newReadingTimeEntry.getPrimaryKey());

		Assert.assertEquals(existingReadingTimeEntry, newReadingTimeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReadingTimeEntry missingReadingTimeEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingReadingTimeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ReadingTimeEntry newReadingTimeEntry1 = addReadingTimeEntry();
		ReadingTimeEntry newReadingTimeEntry2 = addReadingTimeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReadingTimeEntry1.getPrimaryKey());
		primaryKeys.add(newReadingTimeEntry2.getPrimaryKey());

		Map<Serializable, ReadingTimeEntry> readingTimeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, readingTimeEntries.size());
		Assert.assertEquals(
			newReadingTimeEntry1,
			readingTimeEntries.get(newReadingTimeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newReadingTimeEntry2,
			readingTimeEntries.get(newReadingTimeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ReadingTimeEntry> readingTimeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(readingTimeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReadingTimeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ReadingTimeEntry> readingTimeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, readingTimeEntries.size());
		Assert.assertEquals(
			newReadingTimeEntry,
			readingTimeEntries.get(newReadingTimeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ReadingTimeEntry> readingTimeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(readingTimeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newReadingTimeEntry.getPrimaryKey());

		Map<Serializable, ReadingTimeEntry> readingTimeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, readingTimeEntries.size());
		Assert.assertEquals(
			newReadingTimeEntry,
			readingTimeEntries.get(newReadingTimeEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ReadingTimeEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ReadingTimeEntry>() {

				@Override
				public void performAction(ReadingTimeEntry readingTimeEntry) {
					Assert.assertNotNull(readingTimeEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReadingTimeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"readingTimeEntryId",
				newReadingTimeEntry.getReadingTimeEntryId()));

		List<ReadingTimeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ReadingTimeEntry existingReadingTimeEntry = result.get(0);

		Assert.assertEquals(existingReadingTimeEntry, newReadingTimeEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReadingTimeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"readingTimeEntryId", RandomTestUtil.nextLong()));

		List<ReadingTimeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReadingTimeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("readingTimeEntryId"));

		Object newReadingTimeEntryId =
			newReadingTimeEntry.getReadingTimeEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"readingTimeEntryId", new Object[] {newReadingTimeEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingReadingTimeEntryId = result.get(0);

		Assert.assertEquals(existingReadingTimeEntryId, newReadingTimeEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReadingTimeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("readingTimeEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"readingTimeEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newReadingTimeEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		ReadingTimeEntry newReadingTimeEntry = addReadingTimeEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ReadingTimeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"readingTimeEntryId",
				newReadingTimeEntry.getReadingTimeEntryId()));

		List<ReadingTimeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ReadingTimeEntry readingTimeEntry) {
		Assert.assertEquals(
			readingTimeEntry.getUuid(),
			ReflectionTestUtil.invoke(
				readingTimeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(readingTimeEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				readingTimeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(readingTimeEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				readingTimeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(readingTimeEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				readingTimeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(readingTimeEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				readingTimeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected ReadingTimeEntry addReadingTimeEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ReadingTimeEntry readingTimeEntry = _persistence.create(pk);

		readingTimeEntry.setCtCollectionId(RandomTestUtil.nextLong());

		readingTimeEntry.setUuid(RandomTestUtil.randomString());

		readingTimeEntry.setGroupId(RandomTestUtil.nextLong());

		readingTimeEntry.setCompanyId(RandomTestUtil.nextLong());

		readingTimeEntry.setCreateDate(RandomTestUtil.nextDate());

		readingTimeEntry.setModifiedDate(RandomTestUtil.nextDate());

		readingTimeEntry.setClassNameId(RandomTestUtil.nextLong());

		readingTimeEntry.setClassPK(RandomTestUtil.nextLong());

		readingTimeEntry.setReadingTime(RandomTestUtil.nextLong());

		_readingTimeEntries.add(_persistence.update(readingTimeEntry));

		return readingTimeEntry;
	}

	private List<ReadingTimeEntry> _readingTimeEntries =
		new ArrayList<ReadingTimeEntry>();
	private ReadingTimeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1023054752