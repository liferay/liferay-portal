/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchDataLimitEntryException;
import com.liferay.portal.tools.service.builder.test.model.DataLimitEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.DataLimitEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.DataLimitEntryUtil;

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
public class DataLimitEntryPersistenceTest {

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
		_persistence = DataLimitEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DataLimitEntry> iterator = _dataLimitEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DataLimitEntry dataLimitEntry = _persistence.create(pk);

		Assert.assertNotNull(dataLimitEntry);

		Assert.assertEquals(dataLimitEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DataLimitEntry newDataLimitEntry = addDataLimitEntry();

		_persistence.remove(newDataLimitEntry);

		DataLimitEntry existingDataLimitEntry = _persistence.fetchByPrimaryKey(
			newDataLimitEntry.getPrimaryKey());

		Assert.assertNull(existingDataLimitEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDataLimitEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DataLimitEntry newDataLimitEntry = _persistence.create(pk);

		newDataLimitEntry.setCompanyId(RandomTestUtil.nextLong());

		newDataLimitEntry.setUserId(RandomTestUtil.nextLong());

		newDataLimitEntry.setUserName(RandomTestUtil.randomString());

		newDataLimitEntry.setCreateDate(RandomTestUtil.nextDate());

		newDataLimitEntry.setModifiedDate(RandomTestUtil.nextDate());

		_dataLimitEntries.add(_persistence.update(newDataLimitEntry));

		DataLimitEntry existingDataLimitEntry = _persistence.findByPrimaryKey(
			newDataLimitEntry.getPrimaryKey());

		Assert.assertEquals(
			existingDataLimitEntry.getDataLimitEntryId(),
			newDataLimitEntry.getDataLimitEntryId());
		Assert.assertEquals(
			existingDataLimitEntry.getCompanyId(),
			newDataLimitEntry.getCompanyId());
		Assert.assertEquals(
			existingDataLimitEntry.getUserId(), newDataLimitEntry.getUserId());
		Assert.assertEquals(
			existingDataLimitEntry.getUserName(),
			newDataLimitEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDataLimitEntry.getCreateDate()),
			Time.getShortTimestamp(newDataLimitEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDataLimitEntry.getModifiedDate()),
			Time.getShortTimestamp(newDataLimitEntry.getModifiedDate()));
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DataLimitEntry newDataLimitEntry = addDataLimitEntry();

		DataLimitEntry existingDataLimitEntry = _persistence.findByPrimaryKey(
			newDataLimitEntry.getPrimaryKey());

		Assert.assertEquals(existingDataLimitEntry, newDataLimitEntry);
	}

	@Test(expected = NoSuchDataLimitEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DataLimitEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DataLimitEntry", "dataLimitEntryId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DataLimitEntry newDataLimitEntry = addDataLimitEntry();

		DataLimitEntry existingDataLimitEntry = _persistence.fetchByPrimaryKey(
			newDataLimitEntry.getPrimaryKey());

		Assert.assertEquals(existingDataLimitEntry, newDataLimitEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DataLimitEntry missingDataLimitEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDataLimitEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DataLimitEntry newDataLimitEntry1 = addDataLimitEntry();
		DataLimitEntry newDataLimitEntry2 = addDataLimitEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDataLimitEntry1.getPrimaryKey());
		primaryKeys.add(newDataLimitEntry2.getPrimaryKey());

		Map<Serializable, DataLimitEntry> dataLimitEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dataLimitEntries.size());
		Assert.assertEquals(
			newDataLimitEntry1,
			dataLimitEntries.get(newDataLimitEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newDataLimitEntry2,
			dataLimitEntries.get(newDataLimitEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DataLimitEntry> dataLimitEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dataLimitEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DataLimitEntry newDataLimitEntry = addDataLimitEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDataLimitEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DataLimitEntry> dataLimitEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dataLimitEntries.size());
		Assert.assertEquals(
			newDataLimitEntry,
			dataLimitEntries.get(newDataLimitEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DataLimitEntry> dataLimitEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dataLimitEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DataLimitEntry newDataLimitEntry = addDataLimitEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDataLimitEntry.getPrimaryKey());

		Map<Serializable, DataLimitEntry> dataLimitEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dataLimitEntries.size());
		Assert.assertEquals(
			newDataLimitEntry,
			dataLimitEntries.get(newDataLimitEntry.getPrimaryKey()));
	}

	protected DataLimitEntry addDataLimitEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DataLimitEntry dataLimitEntry = _persistence.create(pk);

		dataLimitEntry.setCompanyId(RandomTestUtil.nextLong());

		dataLimitEntry.setUserId(RandomTestUtil.nextLong());

		dataLimitEntry.setUserName(RandomTestUtil.randomString());

		dataLimitEntry.setCreateDate(RandomTestUtil.nextDate());

		dataLimitEntry.setModifiedDate(RandomTestUtil.nextDate());

		_dataLimitEntries.add(_persistence.update(dataLimitEntry));

		return dataLimitEntry;
	}

	private List<DataLimitEntry> _dataLimitEntries =
		new ArrayList<DataLimitEntry>();
	private DataLimitEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}