/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectViewColumnException;
import com.liferay.object.model.ObjectViewColumn;
import com.liferay.object.service.persistence.ObjectViewColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewColumnUtil;
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
public class ObjectViewColumnPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectViewColumnUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectViewColumn> iterator = _objectViewColumns.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewColumn objectViewColumn = _persistence.create(pk);

		Assert.assertNotNull(objectViewColumn);

		Assert.assertEquals(objectViewColumn.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectViewColumn newObjectViewColumn = addObjectViewColumn();

		_persistence.remove(newObjectViewColumn);

		ObjectViewColumn existingObjectViewColumn =
			_persistence.fetchByPrimaryKey(newObjectViewColumn.getPrimaryKey());

		Assert.assertNull(existingObjectViewColumn);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectViewColumn();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewColumn newObjectViewColumn = _persistence.create(pk);

		newObjectViewColumn.setMvccVersion(RandomTestUtil.nextLong());

		newObjectViewColumn.setUuid(RandomTestUtil.randomString());

		newObjectViewColumn.setCompanyId(RandomTestUtil.nextLong());

		newObjectViewColumn.setUserId(RandomTestUtil.nextLong());

		newObjectViewColumn.setUserName(RandomTestUtil.randomString());

		newObjectViewColumn.setCreateDate(RandomTestUtil.nextDate());

		newObjectViewColumn.setModifiedDate(RandomTestUtil.nextDate());

		newObjectViewColumn.setObjectViewId(RandomTestUtil.nextLong());

		newObjectViewColumn.setLabel(RandomTestUtil.randomString());

		newObjectViewColumn.setObjectFieldName(RandomTestUtil.randomString());

		newObjectViewColumn.setPriority(RandomTestUtil.nextInt());

		_objectViewColumns.add(_persistence.update(newObjectViewColumn));

		ObjectViewColumn existingObjectViewColumn =
			_persistence.findByPrimaryKey(newObjectViewColumn.getPrimaryKey());

		Assert.assertEquals(
			existingObjectViewColumn.getMvccVersion(),
			newObjectViewColumn.getMvccVersion());
		Assert.assertEquals(
			existingObjectViewColumn.getUuid(), newObjectViewColumn.getUuid());
		Assert.assertEquals(
			existingObjectViewColumn.getObjectViewColumnId(),
			newObjectViewColumn.getObjectViewColumnId());
		Assert.assertEquals(
			existingObjectViewColumn.getCompanyId(),
			newObjectViewColumn.getCompanyId());
		Assert.assertEquals(
			existingObjectViewColumn.getUserId(),
			newObjectViewColumn.getUserId());
		Assert.assertEquals(
			existingObjectViewColumn.getUserName(),
			newObjectViewColumn.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectViewColumn.getCreateDate()),
			Time.getShortTimestamp(newObjectViewColumn.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectViewColumn.getModifiedDate()),
			Time.getShortTimestamp(newObjectViewColumn.getModifiedDate()));
		Assert.assertEquals(
			existingObjectViewColumn.getObjectViewId(),
			newObjectViewColumn.getObjectViewId());
		Assert.assertEquals(
			existingObjectViewColumn.getLabel(),
			newObjectViewColumn.getLabel());
		Assert.assertEquals(
			existingObjectViewColumn.getObjectFieldName(),
			newObjectViewColumn.getObjectFieldName());
		Assert.assertEquals(
			existingObjectViewColumn.getPriority(),
			newObjectViewColumn.getPriority());
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
	public void testCountByObjectViewId() throws Exception {
		_persistence.countByObjectViewId(RandomTestUtil.nextLong());

		_persistence.countByObjectViewId(0L);
	}

	@Test
	public void testCountByOVI_OFN() throws Exception {
		_persistence.countByOVI_OFN(RandomTestUtil.nextLong(), "");

		_persistence.countByOVI_OFN(0L, "null");

		_persistence.countByOVI_OFN(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectViewColumn newObjectViewColumn = addObjectViewColumn();

		ObjectViewColumn existingObjectViewColumn =
			_persistence.findByPrimaryKey(newObjectViewColumn.getPrimaryKey());

		Assert.assertEquals(existingObjectViewColumn, newObjectViewColumn);
	}

	@Test(expected = NoSuchObjectViewColumnException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectViewColumn> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectViewColumn", "mvccVersion", true, "uuid", true,
			"objectViewColumnId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectViewId", true, "label", true, "objectFieldName", true,
			"priority", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectViewColumn newObjectViewColumn = addObjectViewColumn();

		ObjectViewColumn existingObjectViewColumn =
			_persistence.fetchByPrimaryKey(newObjectViewColumn.getPrimaryKey());

		Assert.assertEquals(existingObjectViewColumn, newObjectViewColumn);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewColumn missingObjectViewColumn =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectViewColumn);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectViewColumn newObjectViewColumn1 = addObjectViewColumn();
		ObjectViewColumn newObjectViewColumn2 = addObjectViewColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewColumn1.getPrimaryKey());
		primaryKeys.add(newObjectViewColumn2.getPrimaryKey());

		Map<Serializable, ObjectViewColumn> objectViewColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectViewColumns.size());
		Assert.assertEquals(
			newObjectViewColumn1,
			objectViewColumns.get(newObjectViewColumn1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectViewColumn2,
			objectViewColumns.get(newObjectViewColumn2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectViewColumn> objectViewColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectViewColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectViewColumn newObjectViewColumn = addObjectViewColumn();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewColumn.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectViewColumn> objectViewColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectViewColumns.size());
		Assert.assertEquals(
			newObjectViewColumn,
			objectViewColumns.get(newObjectViewColumn.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectViewColumn> objectViewColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectViewColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectViewColumn newObjectViewColumn = addObjectViewColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewColumn.getPrimaryKey());

		Map<Serializable, ObjectViewColumn> objectViewColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectViewColumns.size());
		Assert.assertEquals(
			newObjectViewColumn,
			objectViewColumns.get(newObjectViewColumn.getPrimaryKey()));
	}

	protected ObjectViewColumn addObjectViewColumn() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewColumn objectViewColumn = _persistence.create(pk);

		objectViewColumn.setMvccVersion(RandomTestUtil.nextLong());

		objectViewColumn.setUuid(RandomTestUtil.randomString());

		objectViewColumn.setCompanyId(RandomTestUtil.nextLong());

		objectViewColumn.setUserId(RandomTestUtil.nextLong());

		objectViewColumn.setUserName(RandomTestUtil.randomString());

		objectViewColumn.setCreateDate(RandomTestUtil.nextDate());

		objectViewColumn.setModifiedDate(RandomTestUtil.nextDate());

		objectViewColumn.setObjectViewId(RandomTestUtil.nextLong());

		objectViewColumn.setLabel(RandomTestUtil.randomString());

		objectViewColumn.setObjectFieldName(RandomTestUtil.randomString());

		objectViewColumn.setPriority(RandomTestUtil.nextInt());

		_objectViewColumns.add(_persistence.update(objectViewColumn));

		return objectViewColumn;
	}

	private List<ObjectViewColumn> _objectViewColumns =
		new ArrayList<ObjectViewColumn>();
	private ObjectViewColumnPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}