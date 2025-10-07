/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence.test;

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
import com.liferay.trash.exception.NoSuchEntryException;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.trash.service.persistence.TrashEntryPersistence;
import com.liferay.trash.service.persistence.TrashEntryUtil;

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
public class TrashEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.trash.service"));

	@Before
	public void setUp() {
		_persistence = TrashEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<TrashEntry> iterator = _trashEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashEntry trashEntry = _persistence.create(pk);

		Assert.assertNotNull(trashEntry);

		Assert.assertEquals(trashEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		_persistence.remove(newTrashEntry);

		TrashEntry existingTrashEntry = _persistence.fetchByPrimaryKey(
			newTrashEntry.getPrimaryKey());

		Assert.assertNull(existingTrashEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTrashEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashEntry newTrashEntry = _persistence.create(pk);

		newTrashEntry.setMvccVersion(RandomTestUtil.nextLong());

		newTrashEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newTrashEntry.setGroupId(RandomTestUtil.nextLong());

		newTrashEntry.setCompanyId(RandomTestUtil.nextLong());

		newTrashEntry.setUserId(RandomTestUtil.nextLong());

		newTrashEntry.setUserName(RandomTestUtil.randomString());

		newTrashEntry.setCreateDate(RandomTestUtil.nextDate());

		newTrashEntry.setClassNameId(RandomTestUtil.nextLong());

		newTrashEntry.setClassPK(RandomTestUtil.nextLong());

		newTrashEntry.setSystemEventSetKey(RandomTestUtil.nextLong());

		newTrashEntry.setTypeSettings(RandomTestUtil.randomString());

		newTrashEntry.setStatus(RandomTestUtil.nextInt());

		_trashEntries.add(_persistence.update(newTrashEntry));

		TrashEntry existingTrashEntry = _persistence.findByPrimaryKey(
			newTrashEntry.getPrimaryKey());

		Assert.assertEquals(
			existingTrashEntry.getMvccVersion(),
			newTrashEntry.getMvccVersion());
		Assert.assertEquals(
			existingTrashEntry.getCtCollectionId(),
			newTrashEntry.getCtCollectionId());
		Assert.assertEquals(
			existingTrashEntry.getEntryId(), newTrashEntry.getEntryId());
		Assert.assertEquals(
			existingTrashEntry.getGroupId(), newTrashEntry.getGroupId());
		Assert.assertEquals(
			existingTrashEntry.getCompanyId(), newTrashEntry.getCompanyId());
		Assert.assertEquals(
			existingTrashEntry.getUserId(), newTrashEntry.getUserId());
		Assert.assertEquals(
			existingTrashEntry.getUserName(), newTrashEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTrashEntry.getCreateDate()),
			Time.getShortTimestamp(newTrashEntry.getCreateDate()));
		Assert.assertEquals(
			existingTrashEntry.getClassNameId(),
			newTrashEntry.getClassNameId());
		Assert.assertEquals(
			existingTrashEntry.getClassPK(), newTrashEntry.getClassPK());
		Assert.assertEquals(
			existingTrashEntry.getSystemEventSetKey(),
			newTrashEntry.getSystemEventSetKey());
		Assert.assertEquals(
			existingTrashEntry.getTypeSettings(),
			newTrashEntry.getTypeSettings());
		Assert.assertEquals(
			existingTrashEntry.getStatus(), newTrashEntry.getStatus());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_LtCD() throws Exception {
		_persistence.countByG_LtCD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

		_persistence.countByG_LtCD(0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByG_CN() throws Exception {
		_persistence.countByG_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_CN(0L, 0L);
	}

	@Test
	public void testCountByC_CN() throws Exception {
		_persistence.countByC_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_CN(0L, 0L);
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		TrashEntry existingTrashEntry = _persistence.findByPrimaryKey(
			newTrashEntry.getPrimaryKey());

		Assert.assertEquals(existingTrashEntry, newTrashEntry);
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

	protected OrderByComparator<TrashEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"TrashEntry", "mvccVersion", true, "ctCollectionId", true,
			"entryId", true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "classNameId", true,
			"classPK", true, "systemEventSetKey", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		TrashEntry existingTrashEntry = _persistence.fetchByPrimaryKey(
			newTrashEntry.getPrimaryKey());

		Assert.assertEquals(existingTrashEntry, newTrashEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashEntry missingTrashEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTrashEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		TrashEntry newTrashEntry1 = addTrashEntry();
		TrashEntry newTrashEntry2 = addTrashEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashEntry1.getPrimaryKey());
		primaryKeys.add(newTrashEntry2.getPrimaryKey());

		Map<Serializable, TrashEntry> trashEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, trashEntries.size());
		Assert.assertEquals(
			newTrashEntry1, trashEntries.get(newTrashEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newTrashEntry2, trashEntries.get(newTrashEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, TrashEntry> trashEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(trashEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		TrashEntry newTrashEntry = addTrashEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, TrashEntry> trashEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, trashEntries.size());
		Assert.assertEquals(
			newTrashEntry, trashEntries.get(newTrashEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, TrashEntry> trashEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(trashEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashEntry.getPrimaryKey());

		Map<Serializable, TrashEntry> trashEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, trashEntries.size());
		Assert.assertEquals(
			newTrashEntry, trashEntries.get(newTrashEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			TrashEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<TrashEntry>() {

				@Override
				public void performAction(TrashEntry trashEntry) {
					Assert.assertNotNull(trashEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TrashEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", newTrashEntry.getEntryId()));

		List<TrashEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		TrashEntry existingTrashEntry = result.get(0);

		Assert.assertEquals(existingTrashEntry, newTrashEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TrashEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", RandomTestUtil.nextLong()));

		List<TrashEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TrashEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		Object newEntryId = newTrashEntry.getEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("entryId", new Object[] {newEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEntryId = result.get(0);

		Assert.assertEquals(existingEntryId, newEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TrashEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"entryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		TrashEntry newTrashEntry = addTrashEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newTrashEntry.getPrimaryKey()));
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

		TrashEntry newTrashEntry = addTrashEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			TrashEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", newTrashEntry.getEntryId()));

		List<TrashEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(TrashEntry trashEntry) {
		Assert.assertEquals(
			Long.valueOf(trashEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				trashEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(trashEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				trashEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected TrashEntry addTrashEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashEntry trashEntry = _persistence.create(pk);

		trashEntry.setMvccVersion(RandomTestUtil.nextLong());

		trashEntry.setCtCollectionId(RandomTestUtil.nextLong());

		trashEntry.setGroupId(RandomTestUtil.nextLong());

		trashEntry.setCompanyId(RandomTestUtil.nextLong());

		trashEntry.setUserId(RandomTestUtil.nextLong());

		trashEntry.setUserName(RandomTestUtil.randomString());

		trashEntry.setCreateDate(RandomTestUtil.nextDate());

		trashEntry.setClassNameId(RandomTestUtil.nextLong());

		trashEntry.setClassPK(RandomTestUtil.nextLong());

		trashEntry.setSystemEventSetKey(RandomTestUtil.nextLong());

		trashEntry.setTypeSettings(RandomTestUtil.randomString());

		trashEntry.setStatus(RandomTestUtil.nextInt());

		_trashEntries.add(_persistence.update(trashEntry));

		return trashEntry;
	}

	private List<TrashEntry> _trashEntries = new ArrayList<TrashEntry>();
	private TrashEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}