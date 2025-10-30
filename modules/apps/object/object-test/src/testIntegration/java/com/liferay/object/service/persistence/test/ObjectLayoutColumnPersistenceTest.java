/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectLayoutColumnException;
import com.liferay.object.model.ObjectLayoutColumn;
import com.liferay.object.service.persistence.ObjectLayoutColumnPersistence;
import com.liferay.object.service.persistence.ObjectLayoutColumnUtil;
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
public class ObjectLayoutColumnPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectLayoutColumnUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectLayoutColumn> iterator = _objectLayoutColumns.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutColumn objectLayoutColumn = _persistence.create(pk);

		Assert.assertNotNull(objectLayoutColumn);

		Assert.assertEquals(objectLayoutColumn.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectLayoutColumn newObjectLayoutColumn = addObjectLayoutColumn();

		_persistence.remove(newObjectLayoutColumn);

		ObjectLayoutColumn existingObjectLayoutColumn =
			_persistence.fetchByPrimaryKey(
				newObjectLayoutColumn.getPrimaryKey());

		Assert.assertNull(existingObjectLayoutColumn);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectLayoutColumn();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutColumn newObjectLayoutColumn = _persistence.create(pk);

		newObjectLayoutColumn.setMvccVersion(RandomTestUtil.nextLong());

		newObjectLayoutColumn.setUuid(RandomTestUtil.randomString());

		newObjectLayoutColumn.setCompanyId(RandomTestUtil.nextLong());

		newObjectLayoutColumn.setUserId(RandomTestUtil.nextLong());

		newObjectLayoutColumn.setUserName(RandomTestUtil.randomString());

		newObjectLayoutColumn.setCreateDate(RandomTestUtil.nextDate());

		newObjectLayoutColumn.setModifiedDate(RandomTestUtil.nextDate());

		newObjectLayoutColumn.setObjectFieldId(RandomTestUtil.nextLong());

		newObjectLayoutColumn.setObjectLayoutRowId(RandomTestUtil.nextLong());

		newObjectLayoutColumn.setPriority(RandomTestUtil.nextInt());

		newObjectLayoutColumn.setSize(RandomTestUtil.nextInt());

		_objectLayoutColumns.add(_persistence.update(newObjectLayoutColumn));

		ObjectLayoutColumn existingObjectLayoutColumn =
			_persistence.findByPrimaryKey(
				newObjectLayoutColumn.getPrimaryKey());

		Assert.assertEquals(
			existingObjectLayoutColumn.getMvccVersion(),
			newObjectLayoutColumn.getMvccVersion());
		Assert.assertEquals(
			existingObjectLayoutColumn.getUuid(),
			newObjectLayoutColumn.getUuid());
		Assert.assertEquals(
			existingObjectLayoutColumn.getObjectLayoutColumnId(),
			newObjectLayoutColumn.getObjectLayoutColumnId());
		Assert.assertEquals(
			existingObjectLayoutColumn.getCompanyId(),
			newObjectLayoutColumn.getCompanyId());
		Assert.assertEquals(
			existingObjectLayoutColumn.getUserId(),
			newObjectLayoutColumn.getUserId());
		Assert.assertEquals(
			existingObjectLayoutColumn.getUserName(),
			newObjectLayoutColumn.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectLayoutColumn.getCreateDate()),
			Time.getShortTimestamp(newObjectLayoutColumn.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectLayoutColumn.getModifiedDate()),
			Time.getShortTimestamp(newObjectLayoutColumn.getModifiedDate()));
		Assert.assertEquals(
			existingObjectLayoutColumn.getObjectFieldId(),
			newObjectLayoutColumn.getObjectFieldId());
		Assert.assertEquals(
			existingObjectLayoutColumn.getObjectLayoutRowId(),
			newObjectLayoutColumn.getObjectLayoutRowId());
		Assert.assertEquals(
			existingObjectLayoutColumn.getPriority(),
			newObjectLayoutColumn.getPriority());
		Assert.assertEquals(
			existingObjectLayoutColumn.getSize(),
			newObjectLayoutColumn.getSize());
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
	public void testCountByObjectFieldId() throws Exception {
		_persistence.countByObjectFieldId(RandomTestUtil.nextLong());

		_persistence.countByObjectFieldId(0L);
	}

	@Test
	public void testCountByObjectLayoutRowId() throws Exception {
		_persistence.countByObjectLayoutRowId(RandomTestUtil.nextLong());

		_persistence.countByObjectLayoutRowId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectLayoutColumn newObjectLayoutColumn = addObjectLayoutColumn();

		ObjectLayoutColumn existingObjectLayoutColumn =
			_persistence.findByPrimaryKey(
				newObjectLayoutColumn.getPrimaryKey());

		Assert.assertEquals(existingObjectLayoutColumn, newObjectLayoutColumn);
	}

	@Test(expected = NoSuchObjectLayoutColumnException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectLayoutColumn> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectLayoutColumn", "mvccVersion", true, "uuid", true,
			"objectLayoutColumnId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectFieldId", true, "objectLayoutRowId", true, "priority", true,
			"size", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectLayoutColumn newObjectLayoutColumn = addObjectLayoutColumn();

		ObjectLayoutColumn existingObjectLayoutColumn =
			_persistence.fetchByPrimaryKey(
				newObjectLayoutColumn.getPrimaryKey());

		Assert.assertEquals(existingObjectLayoutColumn, newObjectLayoutColumn);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutColumn missingObjectLayoutColumn =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectLayoutColumn);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectLayoutColumn newObjectLayoutColumn1 = addObjectLayoutColumn();
		ObjectLayoutColumn newObjectLayoutColumn2 = addObjectLayoutColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutColumn1.getPrimaryKey());
		primaryKeys.add(newObjectLayoutColumn2.getPrimaryKey());

		Map<Serializable, ObjectLayoutColumn> objectLayoutColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectLayoutColumns.size());
		Assert.assertEquals(
			newObjectLayoutColumn1,
			objectLayoutColumns.get(newObjectLayoutColumn1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectLayoutColumn2,
			objectLayoutColumns.get(newObjectLayoutColumn2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectLayoutColumn> objectLayoutColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayoutColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectLayoutColumn newObjectLayoutColumn = addObjectLayoutColumn();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutColumn.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectLayoutColumn> objectLayoutColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayoutColumns.size());
		Assert.assertEquals(
			newObjectLayoutColumn,
			objectLayoutColumns.get(newObjectLayoutColumn.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectLayoutColumn> objectLayoutColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayoutColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectLayoutColumn newObjectLayoutColumn = addObjectLayoutColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutColumn.getPrimaryKey());

		Map<Serializable, ObjectLayoutColumn> objectLayoutColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayoutColumns.size());
		Assert.assertEquals(
			newObjectLayoutColumn,
			objectLayoutColumns.get(newObjectLayoutColumn.getPrimaryKey()));
	}

	protected ObjectLayoutColumn addObjectLayoutColumn() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutColumn objectLayoutColumn = _persistence.create(pk);

		objectLayoutColumn.setMvccVersion(RandomTestUtil.nextLong());

		objectLayoutColumn.setUuid(RandomTestUtil.randomString());

		objectLayoutColumn.setCompanyId(RandomTestUtil.nextLong());

		objectLayoutColumn.setUserId(RandomTestUtil.nextLong());

		objectLayoutColumn.setUserName(RandomTestUtil.randomString());

		objectLayoutColumn.setCreateDate(RandomTestUtil.nextDate());

		objectLayoutColumn.setModifiedDate(RandomTestUtil.nextDate());

		objectLayoutColumn.setObjectFieldId(RandomTestUtil.nextLong());

		objectLayoutColumn.setObjectLayoutRowId(RandomTestUtil.nextLong());

		objectLayoutColumn.setPriority(RandomTestUtil.nextInt());

		objectLayoutColumn.setSize(RandomTestUtil.nextInt());

		_objectLayoutColumns.add(_persistence.update(objectLayoutColumn));

		return objectLayoutColumn;
	}

	private List<ObjectLayoutColumn> _objectLayoutColumns =
		new ArrayList<ObjectLayoutColumn>();
	private ObjectLayoutColumnPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}