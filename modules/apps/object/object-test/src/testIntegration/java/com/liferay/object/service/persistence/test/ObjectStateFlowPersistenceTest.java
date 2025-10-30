/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectStateFlowException;
import com.liferay.object.model.ObjectStateFlow;
import com.liferay.object.service.persistence.ObjectStateFlowPersistence;
import com.liferay.object.service.persistence.ObjectStateFlowUtil;
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
public class ObjectStateFlowPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectStateFlowUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectStateFlow> iterator = _objectStateFlows.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateFlow objectStateFlow = _persistence.create(pk);

		Assert.assertNotNull(objectStateFlow);

		Assert.assertEquals(objectStateFlow.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		_persistence.remove(newObjectStateFlow);

		ObjectStateFlow existingObjectStateFlow =
			_persistence.fetchByPrimaryKey(newObjectStateFlow.getPrimaryKey());

		Assert.assertNull(existingObjectStateFlow);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectStateFlow();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateFlow newObjectStateFlow = _persistence.create(pk);

		newObjectStateFlow.setMvccVersion(RandomTestUtil.nextLong());

		newObjectStateFlow.setUuid(RandomTestUtil.randomString());

		newObjectStateFlow.setCompanyId(RandomTestUtil.nextLong());

		newObjectStateFlow.setUserId(RandomTestUtil.nextLong());

		newObjectStateFlow.setUserName(RandomTestUtil.randomString());

		newObjectStateFlow.setCreateDate(RandomTestUtil.nextDate());

		newObjectStateFlow.setModifiedDate(RandomTestUtil.nextDate());

		newObjectStateFlow.setObjectFieldId(RandomTestUtil.nextLong());

		_objectStateFlows.add(_persistence.update(newObjectStateFlow));

		ObjectStateFlow existingObjectStateFlow = _persistence.findByPrimaryKey(
			newObjectStateFlow.getPrimaryKey());

		Assert.assertEquals(
			existingObjectStateFlow.getMvccVersion(),
			newObjectStateFlow.getMvccVersion());
		Assert.assertEquals(
			existingObjectStateFlow.getUuid(), newObjectStateFlow.getUuid());
		Assert.assertEquals(
			existingObjectStateFlow.getObjectStateFlowId(),
			newObjectStateFlow.getObjectStateFlowId());
		Assert.assertEquals(
			existingObjectStateFlow.getCompanyId(),
			newObjectStateFlow.getCompanyId());
		Assert.assertEquals(
			existingObjectStateFlow.getUserId(),
			newObjectStateFlow.getUserId());
		Assert.assertEquals(
			existingObjectStateFlow.getUserName(),
			newObjectStateFlow.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectStateFlow.getCreateDate()),
			Time.getShortTimestamp(newObjectStateFlow.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectStateFlow.getModifiedDate()),
			Time.getShortTimestamp(newObjectStateFlow.getModifiedDate()));
		Assert.assertEquals(
			existingObjectStateFlow.getObjectFieldId(),
			newObjectStateFlow.getObjectFieldId());
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
		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		ObjectStateFlow existingObjectStateFlow = _persistence.findByPrimaryKey(
			newObjectStateFlow.getPrimaryKey());

		Assert.assertEquals(existingObjectStateFlow, newObjectStateFlow);
	}

	@Test(expected = NoSuchObjectStateFlowException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectStateFlow> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectStateFlow", "mvccVersion", true, "uuid", true,
			"objectStateFlowId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectFieldId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		ObjectStateFlow existingObjectStateFlow =
			_persistence.fetchByPrimaryKey(newObjectStateFlow.getPrimaryKey());

		Assert.assertEquals(existingObjectStateFlow, newObjectStateFlow);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateFlow missingObjectStateFlow = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingObjectStateFlow);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectStateFlow newObjectStateFlow1 = addObjectStateFlow();
		ObjectStateFlow newObjectStateFlow2 = addObjectStateFlow();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateFlow1.getPrimaryKey());
		primaryKeys.add(newObjectStateFlow2.getPrimaryKey());

		Map<Serializable, ObjectStateFlow> objectStateFlows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectStateFlows.size());
		Assert.assertEquals(
			newObjectStateFlow1,
			objectStateFlows.get(newObjectStateFlow1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectStateFlow2,
			objectStateFlows.get(newObjectStateFlow2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectStateFlow> objectStateFlows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStateFlows.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateFlow.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectStateFlow> objectStateFlows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStateFlows.size());
		Assert.assertEquals(
			newObjectStateFlow,
			objectStateFlows.get(newObjectStateFlow.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectStateFlow> objectStateFlows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStateFlows.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateFlow.getPrimaryKey());

		Map<Serializable, ObjectStateFlow> objectStateFlows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStateFlows.size());
		Assert.assertEquals(
			newObjectStateFlow,
			objectStateFlows.get(newObjectStateFlow.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectStateFlow newObjectStateFlow = addObjectStateFlow();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectStateFlow.getPrimaryKey()));
	}

	private void _assertOriginalValues(ObjectStateFlow objectStateFlow) {
		Assert.assertEquals(
			Long.valueOf(objectStateFlow.getObjectFieldId()),
			ReflectionTestUtil.<Long>invoke(
				objectStateFlow, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectFieldId"));
	}

	protected ObjectStateFlow addObjectStateFlow() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateFlow objectStateFlow = _persistence.create(pk);

		objectStateFlow.setMvccVersion(RandomTestUtil.nextLong());

		objectStateFlow.setUuid(RandomTestUtil.randomString());

		objectStateFlow.setCompanyId(RandomTestUtil.nextLong());

		objectStateFlow.setUserId(RandomTestUtil.nextLong());

		objectStateFlow.setUserName(RandomTestUtil.randomString());

		objectStateFlow.setCreateDate(RandomTestUtil.nextDate());

		objectStateFlow.setModifiedDate(RandomTestUtil.nextDate());

		objectStateFlow.setObjectFieldId(RandomTestUtil.nextLong());

		_objectStateFlows.add(_persistence.update(objectStateFlow));

		return objectStateFlow;
	}

	private List<ObjectStateFlow> _objectStateFlows =
		new ArrayList<ObjectStateFlow>();
	private ObjectStateFlowPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}