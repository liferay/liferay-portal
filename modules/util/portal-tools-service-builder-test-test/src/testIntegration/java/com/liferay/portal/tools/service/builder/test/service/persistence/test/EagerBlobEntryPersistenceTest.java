/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
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
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchEagerBlobEntryException;
import com.liferay.portal.tools.service.builder.test.model.EagerBlobEntry;
import com.liferay.portal.tools.service.builder.test.service.EagerBlobEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.EagerBlobEntryUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.sql.Blob;

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
public class EagerBlobEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = EagerBlobEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<EagerBlobEntry> iterator = _eagerBlobEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerBlobEntry eagerBlobEntry = _persistence.create(pk);

		Assert.assertNotNull(eagerBlobEntry);

		Assert.assertEquals(eagerBlobEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		_persistence.remove(newEagerBlobEntry);

		EagerBlobEntry existingEagerBlobEntry = _persistence.fetchByPrimaryKey(
			newEagerBlobEntry.getPrimaryKey());

		Assert.assertNull(existingEagerBlobEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addEagerBlobEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		newEagerBlobEntry.setUuid(RandomTestUtil.randomString());

		newEagerBlobEntry.setGroupId(RandomTestUtil.nextLong());
		String newBlobString = RandomTestUtil.randomString();

		byte[] newBlobBytes = newBlobString.getBytes("UTF-8");

		Blob newBlobBlob = new OutputBlob(
			new ByteArrayInputStream(newBlobBytes), newBlobBytes.length);

		newEagerBlobEntry.setBlob(newBlobBlob);

		newEagerBlobEntry = _persistence.update(newEagerBlobEntry);

		_eagerBlobEntries.add(newEagerBlobEntry);

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		EagerBlobEntry existingEagerBlobEntry = _persistence.findByPrimaryKey(
			newEagerBlobEntry.getPrimaryKey());

		Assert.assertEquals(
			existingEagerBlobEntry.getUuid(), newEagerBlobEntry.getUuid());
		Assert.assertEquals(
			existingEagerBlobEntry.getEagerBlobEntryId(),
			newEagerBlobEntry.getEagerBlobEntryId());
		Assert.assertEquals(
			existingEagerBlobEntry.getGroupId(),
			newEagerBlobEntry.getGroupId());
		Blob existingBlob = existingEagerBlobEntry.getBlob();

		Assert.assertArrayEquals(
			existingBlob.getBytes(1, (int)existingBlob.length()), newBlobBytes);
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		EagerBlobEntry existingEagerBlobEntry = _persistence.findByPrimaryKey(
			newEagerBlobEntry.getPrimaryKey());

		Assert.assertEquals(existingEagerBlobEntry, newEagerBlobEntry);
	}

	@Test(expected = NoSuchEagerBlobEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<EagerBlobEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"EagerBlobEntry", "uuid", true, "eagerBlobEntryId", true, "groupId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		EagerBlobEntry existingEagerBlobEntry = _persistence.fetchByPrimaryKey(
			newEagerBlobEntry.getPrimaryKey());

		Assert.assertEquals(existingEagerBlobEntry, newEagerBlobEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerBlobEntry missingEagerBlobEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingEagerBlobEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		EagerBlobEntry newEagerBlobEntry1 = addEagerBlobEntry();
		EagerBlobEntry newEagerBlobEntry2 = addEagerBlobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerBlobEntry1.getPrimaryKey());
		primaryKeys.add(newEagerBlobEntry2.getPrimaryKey());

		Map<Serializable, EagerBlobEntry> eagerBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, eagerBlobEntries.size());
		Assert.assertEquals(
			newEagerBlobEntry1,
			eagerBlobEntries.get(newEagerBlobEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newEagerBlobEntry2,
			eagerBlobEntries.get(newEagerBlobEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, EagerBlobEntry> eagerBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(eagerBlobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerBlobEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, EagerBlobEntry> eagerBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, eagerBlobEntries.size());
		Assert.assertEquals(
			newEagerBlobEntry,
			eagerBlobEntries.get(newEagerBlobEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, EagerBlobEntry> eagerBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(eagerBlobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEagerBlobEntry.getPrimaryKey());

		Map<Serializable, EagerBlobEntry> eagerBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, eagerBlobEntries.size());
		Assert.assertEquals(
			newEagerBlobEntry,
			eagerBlobEntries.get(newEagerBlobEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			EagerBlobEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<EagerBlobEntry>() {

				@Override
				public void performAction(EagerBlobEntry eagerBlobEntry) {
					Assert.assertNotNull(eagerBlobEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerBlobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerBlobEntryId", newEagerBlobEntry.getEagerBlobEntryId()));

		List<EagerBlobEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		EagerBlobEntry existingEagerBlobEntry = result.get(0);

		Assert.assertEquals(existingEagerBlobEntry, newEagerBlobEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerBlobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerBlobEntryId", RandomTestUtil.nextLong()));

		List<EagerBlobEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerBlobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("eagerBlobEntryId"));

		Object newEagerBlobEntryId = newEagerBlobEntry.getEagerBlobEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"eagerBlobEntryId", new Object[] {newEagerBlobEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEagerBlobEntryId = result.get(0);

		Assert.assertEquals(existingEagerBlobEntryId, newEagerBlobEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerBlobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("eagerBlobEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"eagerBlobEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newEagerBlobEntry.getPrimaryKey()));
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

		EagerBlobEntry newEagerBlobEntry = addEagerBlobEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			EagerBlobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"eagerBlobEntryId", newEagerBlobEntry.getEagerBlobEntryId()));

		List<EagerBlobEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(EagerBlobEntry eagerBlobEntry) {
		Assert.assertEquals(
			eagerBlobEntry.getUuid(),
			ReflectionTestUtil.invoke(
				eagerBlobEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(eagerBlobEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				eagerBlobEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected EagerBlobEntry addEagerBlobEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EagerBlobEntry eagerBlobEntry = _persistence.create(pk);

		eagerBlobEntry.setUuid(RandomTestUtil.randomString());

		eagerBlobEntry.setGroupId(RandomTestUtil.nextLong());
		String blobString = RandomTestUtil.randomString();

		byte[] blobBytes = blobString.getBytes("UTF-8");

		Blob blobBlob = new OutputBlob(
			new ByteArrayInputStream(blobBytes), blobBytes.length);

		eagerBlobEntry.setBlob(blobBlob);

		_eagerBlobEntries.add(_persistence.update(eagerBlobEntry));

		return eagerBlobEntry;
	}

	private List<EagerBlobEntry> _eagerBlobEntries =
		new ArrayList<EagerBlobEntry>();
	private EagerBlobEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:363374027