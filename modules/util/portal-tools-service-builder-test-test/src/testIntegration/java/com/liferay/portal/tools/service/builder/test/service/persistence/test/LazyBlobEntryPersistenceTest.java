/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLazyBlobEntryException;
import com.liferay.portal.tools.service.builder.test.model.LazyBlobEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.LazyBlobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LazyBlobEntryUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.sql.Blob;

import java.util.ArrayList;
import java.util.Arrays;
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
public class LazyBlobEntryPersistenceTest {

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
		_persistence = LazyBlobEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LazyBlobEntry> iterator = _lazyBlobEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LazyBlobEntry lazyBlobEntry = _persistence.create(pk);

		Assert.assertNotNull(lazyBlobEntry);

		Assert.assertEquals(lazyBlobEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		_persistence.remove(newLazyBlobEntry);

		LazyBlobEntry existingLazyBlobEntry = _persistence.fetchByPrimaryKey(
			newLazyBlobEntry.getPrimaryKey());

		Assert.assertNull(existingLazyBlobEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLazyBlobEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LazyBlobEntry newLazyBlobEntry = _persistence.create(pk);

		newLazyBlobEntry.setUuid(RandomTestUtil.randomString());

		newLazyBlobEntry.setGroupId(RandomTestUtil.nextLong());
		String newBlob1String = RandomTestUtil.randomString();

		byte[] newBlob1Bytes = newBlob1String.getBytes("UTF-8");

		Blob newBlob1Blob = new OutputBlob(
			new ByteArrayInputStream(newBlob1Bytes), newBlob1Bytes.length);

		newLazyBlobEntry.setBlob1(newBlob1Blob);
		String newBlob2String = RandomTestUtil.randomString();

		byte[] newBlob2Bytes = newBlob2String.getBytes("UTF-8");

		Blob newBlob2Blob = new OutputBlob(
			new ByteArrayInputStream(newBlob2Bytes), newBlob2Bytes.length);

		newLazyBlobEntry.setBlob2(newBlob2Blob);

		_lazyBlobEntries.add(_persistence.update(newLazyBlobEntry));

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		LazyBlobEntry existingLazyBlobEntry = _persistence.findByPrimaryKey(
			newLazyBlobEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLazyBlobEntry.getUuid(), newLazyBlobEntry.getUuid());
		Assert.assertEquals(
			existingLazyBlobEntry.getLazyBlobEntryId(),
			newLazyBlobEntry.getLazyBlobEntryId());
		Assert.assertEquals(
			existingLazyBlobEntry.getGroupId(), newLazyBlobEntry.getGroupId());
		Blob existingBlob1 = existingLazyBlobEntry.getBlob1();

		Assert.assertTrue(
			Arrays.equals(
				existingBlob1.getBytes(1, (int)existingBlob1.length()),
				newBlob1Bytes));
		Blob existingBlob2 = existingLazyBlobEntry.getBlob2();

		Assert.assertTrue(
			Arrays.equals(
				existingBlob2.getBytes(1, (int)existingBlob2.length()),
				newBlob2Bytes));
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
		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		LazyBlobEntry existingLazyBlobEntry = _persistence.findByPrimaryKey(
			newLazyBlobEntry.getPrimaryKey());

		Assert.assertEquals(existingLazyBlobEntry, newLazyBlobEntry);
	}

	@Test(expected = NoSuchLazyBlobEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LazyBlobEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LazyBlobEntry", "uuid", true, "lazyBlobEntryId", true, "groupId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		LazyBlobEntry existingLazyBlobEntry = _persistence.fetchByPrimaryKey(
			newLazyBlobEntry.getPrimaryKey());

		Assert.assertEquals(existingLazyBlobEntry, newLazyBlobEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LazyBlobEntry missingLazyBlobEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLazyBlobEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LazyBlobEntry newLazyBlobEntry1 = addLazyBlobEntry();
		LazyBlobEntry newLazyBlobEntry2 = addLazyBlobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLazyBlobEntry1.getPrimaryKey());
		primaryKeys.add(newLazyBlobEntry2.getPrimaryKey());

		Map<Serializable, LazyBlobEntry> lazyBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, lazyBlobEntries.size());
		Assert.assertEquals(
			newLazyBlobEntry1,
			lazyBlobEntries.get(newLazyBlobEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newLazyBlobEntry2,
			lazyBlobEntries.get(newLazyBlobEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LazyBlobEntry> lazyBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lazyBlobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLazyBlobEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LazyBlobEntry> lazyBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lazyBlobEntries.size());
		Assert.assertEquals(
			newLazyBlobEntry,
			lazyBlobEntries.get(newLazyBlobEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LazyBlobEntry> lazyBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lazyBlobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLazyBlobEntry.getPrimaryKey());

		Map<Serializable, LazyBlobEntry> lazyBlobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lazyBlobEntries.size());
		Assert.assertEquals(
			newLazyBlobEntry,
			lazyBlobEntries.get(newLazyBlobEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LazyBlobEntry newLazyBlobEntry = addLazyBlobEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLazyBlobEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(LazyBlobEntry lazyBlobEntry) {
		Assert.assertEquals(
			lazyBlobEntry.getUuid(),
			ReflectionTestUtil.invoke(
				lazyBlobEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(lazyBlobEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				lazyBlobEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected LazyBlobEntry addLazyBlobEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LazyBlobEntry lazyBlobEntry = _persistence.create(pk);

		lazyBlobEntry.setUuid(RandomTestUtil.randomString());

		lazyBlobEntry.setGroupId(RandomTestUtil.nextLong());
		String blob1String = RandomTestUtil.randomString();

		byte[] blob1Bytes = blob1String.getBytes("UTF-8");

		Blob blob1Blob = new OutputBlob(
			new ByteArrayInputStream(blob1Bytes), blob1Bytes.length);

		lazyBlobEntry.setBlob1(blob1Blob);
		String blob2String = RandomTestUtil.randomString();

		byte[] blob2Bytes = blob2String.getBytes("UTF-8");

		Blob blob2Blob = new OutputBlob(
			new ByteArrayInputStream(blob2Bytes), blob2Bytes.length);

		lazyBlobEntry.setBlob2(blob2Blob);

		_lazyBlobEntries.add(_persistence.update(lazyBlobEntry));

		return lazyBlobEntry;
	}

	private List<LazyBlobEntry> _lazyBlobEntries =
		new ArrayList<LazyBlobEntry>();
	private LazyBlobEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}