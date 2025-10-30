/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectFilterException;
import com.liferay.object.model.ObjectFilter;
import com.liferay.object.service.persistence.ObjectFilterPersistence;
import com.liferay.object.service.persistence.ObjectFilterUtil;
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
public class ObjectFilterPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectFilterUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectFilter> iterator = _objectFilters.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFilter objectFilter = _persistence.create(pk);

		Assert.assertNotNull(objectFilter);

		Assert.assertEquals(objectFilter.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectFilter newObjectFilter = addObjectFilter();

		_persistence.remove(newObjectFilter);

		ObjectFilter existingObjectFilter = _persistence.fetchByPrimaryKey(
			newObjectFilter.getPrimaryKey());

		Assert.assertNull(existingObjectFilter);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectFilter();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFilter newObjectFilter = _persistence.create(pk);

		newObjectFilter.setMvccVersion(RandomTestUtil.nextLong());

		newObjectFilter.setUuid(RandomTestUtil.randomString());

		newObjectFilter.setCompanyId(RandomTestUtil.nextLong());

		newObjectFilter.setUserId(RandomTestUtil.nextLong());

		newObjectFilter.setUserName(RandomTestUtil.randomString());

		newObjectFilter.setCreateDate(RandomTestUtil.nextDate());

		newObjectFilter.setModifiedDate(RandomTestUtil.nextDate());

		newObjectFilter.setObjectFieldId(RandomTestUtil.nextLong());

		newObjectFilter.setFilterBy(RandomTestUtil.randomString());

		newObjectFilter.setFilterType(RandomTestUtil.randomString());

		newObjectFilter.setJSON(RandomTestUtil.randomString());

		_objectFilters.add(_persistence.update(newObjectFilter));

		ObjectFilter existingObjectFilter = _persistence.findByPrimaryKey(
			newObjectFilter.getPrimaryKey());

		Assert.assertEquals(
			existingObjectFilter.getMvccVersion(),
			newObjectFilter.getMvccVersion());
		Assert.assertEquals(
			existingObjectFilter.getUuid(), newObjectFilter.getUuid());
		Assert.assertEquals(
			existingObjectFilter.getObjectFilterId(),
			newObjectFilter.getObjectFilterId());
		Assert.assertEquals(
			existingObjectFilter.getCompanyId(),
			newObjectFilter.getCompanyId());
		Assert.assertEquals(
			existingObjectFilter.getUserId(), newObjectFilter.getUserId());
		Assert.assertEquals(
			existingObjectFilter.getUserName(), newObjectFilter.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFilter.getCreateDate()),
			Time.getShortTimestamp(newObjectFilter.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFilter.getModifiedDate()),
			Time.getShortTimestamp(newObjectFilter.getModifiedDate()));
		Assert.assertEquals(
			existingObjectFilter.getObjectFieldId(),
			newObjectFilter.getObjectFieldId());
		Assert.assertEquals(
			existingObjectFilter.getFilterBy(), newObjectFilter.getFilterBy());
		Assert.assertEquals(
			existingObjectFilter.getFilterType(),
			newObjectFilter.getFilterType());
		Assert.assertEquals(
			existingObjectFilter.getJSON(), newObjectFilter.getJSON());
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectFilter newObjectFilter = addObjectFilter();

		ObjectFilter existingObjectFilter = _persistence.findByPrimaryKey(
			newObjectFilter.getPrimaryKey());

		Assert.assertEquals(existingObjectFilter, newObjectFilter);
	}

	@Test(expected = NoSuchObjectFilterException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectFilter> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectFilter", "mvccVersion", true, "uuid", true, "objectFilterId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "objectFieldId", true,
			"filterBy", true, "filterType", true, "json", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectFilter newObjectFilter = addObjectFilter();

		ObjectFilter existingObjectFilter = _persistence.fetchByPrimaryKey(
			newObjectFilter.getPrimaryKey());

		Assert.assertEquals(existingObjectFilter, newObjectFilter);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFilter missingObjectFilter = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectFilter);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectFilter newObjectFilter1 = addObjectFilter();
		ObjectFilter newObjectFilter2 = addObjectFilter();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFilter1.getPrimaryKey());
		primaryKeys.add(newObjectFilter2.getPrimaryKey());

		Map<Serializable, ObjectFilter> objectFilters =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectFilters.size());
		Assert.assertEquals(
			newObjectFilter1,
			objectFilters.get(newObjectFilter1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectFilter2,
			objectFilters.get(newObjectFilter2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectFilter> objectFilters =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFilters.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectFilter newObjectFilter = addObjectFilter();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFilter.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectFilter> objectFilters =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFilters.size());
		Assert.assertEquals(
			newObjectFilter,
			objectFilters.get(newObjectFilter.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectFilter> objectFilters =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFilters.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectFilter newObjectFilter = addObjectFilter();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFilter.getPrimaryKey());

		Map<Serializable, ObjectFilter> objectFilters =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFilters.size());
		Assert.assertEquals(
			newObjectFilter,
			objectFilters.get(newObjectFilter.getPrimaryKey()));
	}

	protected ObjectFilter addObjectFilter() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFilter objectFilter = _persistence.create(pk);

		objectFilter.setMvccVersion(RandomTestUtil.nextLong());

		objectFilter.setUuid(RandomTestUtil.randomString());

		objectFilter.setCompanyId(RandomTestUtil.nextLong());

		objectFilter.setUserId(RandomTestUtil.nextLong());

		objectFilter.setUserName(RandomTestUtil.randomString());

		objectFilter.setCreateDate(RandomTestUtil.nextDate());

		objectFilter.setModifiedDate(RandomTestUtil.nextDate());

		objectFilter.setObjectFieldId(RandomTestUtil.nextLong());

		objectFilter.setFilterBy(RandomTestUtil.randomString());

		objectFilter.setFilterType(RandomTestUtil.randomString());

		objectFilter.setJSON(RandomTestUtil.randomString());

		_objectFilters.add(_persistence.update(objectFilter));

		return objectFilter;
	}

	private List<ObjectFilter> _objectFilters = new ArrayList<ObjectFilter>();
	private ObjectFilterPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}