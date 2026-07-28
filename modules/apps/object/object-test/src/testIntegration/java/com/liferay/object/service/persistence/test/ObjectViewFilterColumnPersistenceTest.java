/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectViewFilterColumnException;
import com.liferay.object.model.ObjectViewFilterColumn;
import com.liferay.object.service.ObjectViewFilterColumnLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectViewFilterColumnPersistence;
import com.liferay.object.service.persistence.ObjectViewFilterColumnUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
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
public class ObjectViewFilterColumnPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectViewFilterColumnUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectViewFilterColumn> iterator =
			_objectViewFilterColumns.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewFilterColumn objectViewFilterColumn = _persistence.create(pk);

		Assert.assertNotNull(objectViewFilterColumn);

		Assert.assertEquals(objectViewFilterColumn.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		_persistence.remove(newObjectViewFilterColumn);

		ObjectViewFilterColumn existingObjectViewFilterColumn =
			_persistence.fetchByPrimaryKey(
				newObjectViewFilterColumn.getPrimaryKey());

		Assert.assertNull(existingObjectViewFilterColumn);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectViewFilterColumn();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		newObjectViewFilterColumn.setUuid(RandomTestUtil.randomString());

		newObjectViewFilterColumn.setCompanyId(RandomTestUtil.nextLong());

		newObjectViewFilterColumn.setUserId(RandomTestUtil.nextLong());

		newObjectViewFilterColumn.setUserName(RandomTestUtil.randomString());

		newObjectViewFilterColumn.setCreateDate(RandomTestUtil.nextDate());

		newObjectViewFilterColumn.setModifiedDate(RandomTestUtil.nextDate());

		newObjectViewFilterColumn.setObjectViewId(RandomTestUtil.nextLong());

		newObjectViewFilterColumn.setFilterType(RandomTestUtil.randomString());

		newObjectViewFilterColumn.setJSON(RandomTestUtil.randomString());

		newObjectViewFilterColumn.setObjectFieldName(
			RandomTestUtil.randomString());

		newObjectViewFilterColumn = _persistence.update(
			newObjectViewFilterColumn);

		_objectViewFilterColumns.add(newObjectViewFilterColumn);

		ObjectViewFilterColumn existingObjectViewFilterColumn =
			_persistence.findByPrimaryKey(
				newObjectViewFilterColumn.getPrimaryKey());

		Assert.assertEquals(
			existingObjectViewFilterColumn.getMvccVersion(),
			newObjectViewFilterColumn.getMvccVersion());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getUuid(),
			newObjectViewFilterColumn.getUuid());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getObjectViewFilterColumnId(),
			newObjectViewFilterColumn.getObjectViewFilterColumnId());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getCompanyId(),
			newObjectViewFilterColumn.getCompanyId());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getUserId(),
			newObjectViewFilterColumn.getUserId());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getUserName(),
			newObjectViewFilterColumn.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectViewFilterColumn.getCreateDate()),
			Time.getShortTimestamp(newObjectViewFilterColumn.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectViewFilterColumn.getModifiedDate()),
			Time.getShortTimestamp(
				newObjectViewFilterColumn.getModifiedDate()));
		Assert.assertEquals(
			existingObjectViewFilterColumn.getObjectViewId(),
			newObjectViewFilterColumn.getObjectViewId());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getFilterType(),
			newObjectViewFilterColumn.getFilterType());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getJSON(),
			newObjectViewFilterColumn.getJSON());
		Assert.assertEquals(
			existingObjectViewFilterColumn.getObjectFieldName(),
			newObjectViewFilterColumn.getObjectFieldName());
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
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		ObjectViewFilterColumn existingObjectViewFilterColumn =
			_persistence.findByPrimaryKey(
				newObjectViewFilterColumn.getPrimaryKey());

		Assert.assertEquals(
			existingObjectViewFilterColumn, newObjectViewFilterColumn);
	}

	@Test(expected = NoSuchObjectViewFilterColumnException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectViewFilterColumn> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectViewFilterColumn", "mvccVersion", true, "uuid", true,
			"objectViewFilterColumnId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectViewId", true, "filterType", true, "objectFieldName", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		ObjectViewFilterColumn existingObjectViewFilterColumn =
			_persistence.fetchByPrimaryKey(
				newObjectViewFilterColumn.getPrimaryKey());

		Assert.assertEquals(
			existingObjectViewFilterColumn, newObjectViewFilterColumn);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectViewFilterColumn missingObjectViewFilterColumn =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectViewFilterColumn);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectViewFilterColumn newObjectViewFilterColumn1 =
			addObjectViewFilterColumn();
		ObjectViewFilterColumn newObjectViewFilterColumn2 =
			addObjectViewFilterColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewFilterColumn1.getPrimaryKey());
		primaryKeys.add(newObjectViewFilterColumn2.getPrimaryKey());

		Map<Serializable, ObjectViewFilterColumn> objectViewFilterColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectViewFilterColumns.size());
		Assert.assertEquals(
			newObjectViewFilterColumn1,
			objectViewFilterColumns.get(
				newObjectViewFilterColumn1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectViewFilterColumn2,
			objectViewFilterColumns.get(
				newObjectViewFilterColumn2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectViewFilterColumn> objectViewFilterColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectViewFilterColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewFilterColumn.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectViewFilterColumn> objectViewFilterColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectViewFilterColumns.size());
		Assert.assertEquals(
			newObjectViewFilterColumn,
			objectViewFilterColumns.get(
				newObjectViewFilterColumn.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectViewFilterColumn> objectViewFilterColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectViewFilterColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectViewFilterColumn.getPrimaryKey());

		Map<Serializable, ObjectViewFilterColumn> objectViewFilterColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectViewFilterColumns.size());
		Assert.assertEquals(
			newObjectViewFilterColumn,
			objectViewFilterColumns.get(
				newObjectViewFilterColumn.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectViewFilterColumnLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ObjectViewFilterColumn>() {

				@Override
				public void performAction(
					ObjectViewFilterColumn objectViewFilterColumn) {

					Assert.assertNotNull(objectViewFilterColumn);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectViewFilterColumn.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectViewFilterColumnId",
				newObjectViewFilterColumn.getObjectViewFilterColumnId()));

		List<ObjectViewFilterColumn> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectViewFilterColumn existingObjectViewFilterColumn = result.get(0);

		Assert.assertEquals(
			existingObjectViewFilterColumn, newObjectViewFilterColumn);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectViewFilterColumn.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectViewFilterColumnId", RandomTestUtil.nextLong()));

		List<ObjectViewFilterColumn> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectViewFilterColumn newObjectViewFilterColumn =
			addObjectViewFilterColumn();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectViewFilterColumn.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectViewFilterColumnId"));

		Object newObjectViewFilterColumnId =
			newObjectViewFilterColumn.getObjectViewFilterColumnId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectViewFilterColumnId",
				new Object[] {newObjectViewFilterColumnId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectViewFilterColumnId = result.get(0);

		Assert.assertEquals(
			existingObjectViewFilterColumnId, newObjectViewFilterColumnId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectViewFilterColumn.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectViewFilterColumnId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectViewFilterColumnId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ObjectViewFilterColumn addObjectViewFilterColumn()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ObjectViewFilterColumn objectViewFilterColumn = _persistence.create(pk);

		objectViewFilterColumn.setUuid(RandomTestUtil.randomString());

		objectViewFilterColumn.setCompanyId(RandomTestUtil.nextLong());

		objectViewFilterColumn.setUserId(RandomTestUtil.nextLong());

		objectViewFilterColumn.setUserName(RandomTestUtil.randomString());

		objectViewFilterColumn.setCreateDate(RandomTestUtil.nextDate());

		objectViewFilterColumn.setModifiedDate(RandomTestUtil.nextDate());

		objectViewFilterColumn.setObjectViewId(RandomTestUtil.nextLong());

		objectViewFilterColumn.setFilterType(RandomTestUtil.randomString());

		objectViewFilterColumn.setJSON(RandomTestUtil.randomString());

		objectViewFilterColumn.setObjectFieldName(
			RandomTestUtil.randomString());

		_objectViewFilterColumns.add(
			_persistence.update(objectViewFilterColumn));

		return objectViewFilterColumn;
	}

	private List<ObjectViewFilterColumn> _objectViewFilterColumns =
		new ArrayList<ObjectViewFilterColumn>();
	private ObjectViewFilterColumnPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1496982826