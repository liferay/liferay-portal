/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectStateException;
import com.liferay.object.model.ObjectState;
import com.liferay.object.service.persistence.ObjectStatePersistence;
import com.liferay.object.service.persistence.ObjectStateUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class ObjectStatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectStateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectState> iterator = _objectStates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectState objectState = _persistence.create(pk);

		Assert.assertNotNull(objectState);

		Assert.assertEquals(objectState.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectState newObjectState = addObjectState();

		_persistence.remove(newObjectState);

		ObjectState existingObjectState = _persistence.fetchByPrimaryKey(
			newObjectState.getPrimaryKey());

		Assert.assertNull(existingObjectState);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectState();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectState newObjectState = _persistence.create(pk);

		newObjectState.setMvccVersion(RandomTestUtil.nextLong());

		newObjectState.setUuid(RandomTestUtil.randomString());

		newObjectState.setCompanyId(RandomTestUtil.nextLong());

		newObjectState.setUserId(RandomTestUtil.nextLong());

		newObjectState.setUserName(RandomTestUtil.randomString());

		newObjectState.setCreateDate(RandomTestUtil.nextDate());

		newObjectState.setModifiedDate(RandomTestUtil.nextDate());

		newObjectState.setListTypeEntryId(RandomTestUtil.nextLong());

		newObjectState.setObjectStateFlowId(RandomTestUtil.nextLong());

		_objectStates.add(_persistence.update(newObjectState));

		ObjectState existingObjectState = _persistence.findByPrimaryKey(
			newObjectState.getPrimaryKey());

		Assert.assertEquals(
			existingObjectState.getMvccVersion(),
			newObjectState.getMvccVersion());
		Assert.assertEquals(
			existingObjectState.getUuid(), newObjectState.getUuid());
		Assert.assertEquals(
			existingObjectState.getObjectStateId(),
			newObjectState.getObjectStateId());
		Assert.assertEquals(
			existingObjectState.getCompanyId(), newObjectState.getCompanyId());
		Assert.assertEquals(
			existingObjectState.getUserId(), newObjectState.getUserId());
		Assert.assertEquals(
			existingObjectState.getUserName(), newObjectState.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectState.getCreateDate()),
			Time.getShortTimestamp(newObjectState.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectState.getModifiedDate()),
			Time.getShortTimestamp(newObjectState.getModifiedDate()));
		Assert.assertEquals(
			existingObjectState.getListTypeEntryId(),
			newObjectState.getListTypeEntryId());
		Assert.assertEquals(
			existingObjectState.getObjectStateFlowId(),
			newObjectState.getObjectStateFlowId());
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
	public void testCountByListTypeEntryId() throws Exception {
		_persistence.countByListTypeEntryId(RandomTestUtil.nextLong());

		_persistence.countByListTypeEntryId(0L);
	}

	@Test
	public void testCountByObjectStateFlowId() throws Exception {
		_persistence.countByObjectStateFlowId(RandomTestUtil.nextLong());

		_persistence.countByObjectStateFlowId(0L);
	}

	@Test
	public void testCountByLTEI_OSFI() throws Exception {
		_persistence.countByLTEI_OSFI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByLTEI_OSFI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectState newObjectState = addObjectState();

		ObjectState existingObjectState = _persistence.findByPrimaryKey(
			newObjectState.getPrimaryKey());

		Assert.assertEquals(existingObjectState, newObjectState);
	}

	@Test(expected = NoSuchObjectStateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectState> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectState", "mvccVersion", true, "uuid", true, "objectStateId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "listTypeEntryId", true,
			"objectStateFlowId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectState newObjectState = addObjectState();

		ObjectState existingObjectState = _persistence.fetchByPrimaryKey(
			newObjectState.getPrimaryKey());

		Assert.assertEquals(existingObjectState, newObjectState);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectState missingObjectState = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectState);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectState newObjectState1 = addObjectState();
		ObjectState newObjectState2 = addObjectState();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectState1.getPrimaryKey());
		primaryKeys.add(newObjectState2.getPrimaryKey());

		Map<Serializable, ObjectState> objectStates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectStates.size());
		Assert.assertEquals(
			newObjectState1, objectStates.get(newObjectState1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectState2, objectStates.get(newObjectState2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectState> objectStates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectState newObjectState = addObjectState();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectState.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectState> objectStates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStates.size());
		Assert.assertEquals(
			newObjectState, objectStates.get(newObjectState.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectState> objectStates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectState newObjectState = addObjectState();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectState.getPrimaryKey());

		Map<Serializable, ObjectState> objectStates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStates.size());
		Assert.assertEquals(
			newObjectState, objectStates.get(newObjectState.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectState newObjectState = addObjectState();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectState.getPrimaryKey()));
	}

	private void _assertOriginalValues(ObjectState objectState) {
		Assert.assertEquals(
			Long.valueOf(objectState.getListTypeEntryId()),
			ReflectionTestUtil.<Long>invoke(
				objectState, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "listTypeEntryId"));
		Assert.assertEquals(
			Long.valueOf(objectState.getObjectStateFlowId()),
			ReflectionTestUtil.<Long>invoke(
				objectState, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectStateFlowId"));
	}

	protected ObjectState addObjectState() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectState objectState = _persistence.create(pk);

		objectState.setMvccVersion(RandomTestUtil.nextLong());

		objectState.setUuid(RandomTestUtil.randomString());

		objectState.setCompanyId(RandomTestUtil.nextLong());

		objectState.setUserId(RandomTestUtil.nextLong());

		objectState.setUserName(RandomTestUtil.randomString());

		objectState.setCreateDate(RandomTestUtil.nextDate());

		objectState.setModifiedDate(RandomTestUtil.nextDate());

		objectState.setListTypeEntryId(RandomTestUtil.nextLong());

		objectState.setObjectStateFlowId(RandomTestUtil.nextLong());

		_objectStates.add(_persistence.update(objectState));

		return objectState;
	}

	private List<ObjectState> _objectStates = new ArrayList<ObjectState>();
	private ObjectStatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}