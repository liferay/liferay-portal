/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectStateTransitionException;
import com.liferay.object.model.ObjectStateTransition;
import com.liferay.object.service.persistence.ObjectStateTransitionPersistence;
import com.liferay.object.service.persistence.ObjectStateTransitionUtil;
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
public class ObjectStateTransitionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectStateTransitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectStateTransition> iterator =
			_objectStateTransitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateTransition objectStateTransition = _persistence.create(pk);

		Assert.assertNotNull(objectStateTransition);

		Assert.assertEquals(objectStateTransition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectStateTransition newObjectStateTransition =
			addObjectStateTransition();

		_persistence.remove(newObjectStateTransition);

		ObjectStateTransition existingObjectStateTransition =
			_persistence.fetchByPrimaryKey(
				newObjectStateTransition.getPrimaryKey());

		Assert.assertNull(existingObjectStateTransition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectStateTransition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateTransition newObjectStateTransition = _persistence.create(
			pk);

		newObjectStateTransition.setMvccVersion(RandomTestUtil.nextLong());

		newObjectStateTransition.setUuid(RandomTestUtil.randomString());

		newObjectStateTransition.setCompanyId(RandomTestUtil.nextLong());

		newObjectStateTransition.setUserId(RandomTestUtil.nextLong());

		newObjectStateTransition.setUserName(RandomTestUtil.randomString());

		newObjectStateTransition.setCreateDate(RandomTestUtil.nextDate());

		newObjectStateTransition.setModifiedDate(RandomTestUtil.nextDate());

		newObjectStateTransition.setObjectStateFlowId(
			RandomTestUtil.nextLong());

		newObjectStateTransition.setSourceObjectStateId(
			RandomTestUtil.nextLong());

		newObjectStateTransition.setTargetObjectStateId(
			RandomTestUtil.nextLong());

		_objectStateTransitions.add(
			_persistence.update(newObjectStateTransition));

		ObjectStateTransition existingObjectStateTransition =
			_persistence.findByPrimaryKey(
				newObjectStateTransition.getPrimaryKey());

		Assert.assertEquals(
			existingObjectStateTransition.getMvccVersion(),
			newObjectStateTransition.getMvccVersion());
		Assert.assertEquals(
			existingObjectStateTransition.getUuid(),
			newObjectStateTransition.getUuid());
		Assert.assertEquals(
			existingObjectStateTransition.getObjectStateTransitionId(),
			newObjectStateTransition.getObjectStateTransitionId());
		Assert.assertEquals(
			existingObjectStateTransition.getCompanyId(),
			newObjectStateTransition.getCompanyId());
		Assert.assertEquals(
			existingObjectStateTransition.getUserId(),
			newObjectStateTransition.getUserId());
		Assert.assertEquals(
			existingObjectStateTransition.getUserName(),
			newObjectStateTransition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectStateTransition.getCreateDate()),
			Time.getShortTimestamp(newObjectStateTransition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingObjectStateTransition.getModifiedDate()),
			Time.getShortTimestamp(newObjectStateTransition.getModifiedDate()));
		Assert.assertEquals(
			existingObjectStateTransition.getObjectStateFlowId(),
			newObjectStateTransition.getObjectStateFlowId());
		Assert.assertEquals(
			existingObjectStateTransition.getSourceObjectStateId(),
			newObjectStateTransition.getSourceObjectStateId());
		Assert.assertEquals(
			existingObjectStateTransition.getTargetObjectStateId(),
			newObjectStateTransition.getTargetObjectStateId());
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
	public void testCountByObjectStateFlowId() throws Exception {
		_persistence.countByObjectStateFlowId(RandomTestUtil.nextLong());

		_persistence.countByObjectStateFlowId(0L);
	}

	@Test
	public void testCountBySourceObjectStateId() throws Exception {
		_persistence.countBySourceObjectStateId(RandomTestUtil.nextLong());

		_persistence.countBySourceObjectStateId(0L);
	}

	@Test
	public void testCountByTargetObjectStateId() throws Exception {
		_persistence.countByTargetObjectStateId(RandomTestUtil.nextLong());

		_persistence.countByTargetObjectStateId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectStateTransition newObjectStateTransition =
			addObjectStateTransition();

		ObjectStateTransition existingObjectStateTransition =
			_persistence.findByPrimaryKey(
				newObjectStateTransition.getPrimaryKey());

		Assert.assertEquals(
			existingObjectStateTransition, newObjectStateTransition);
	}

	@Test(expected = NoSuchObjectStateTransitionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectStateTransition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectStateTransition", "mvccVersion", true, "uuid", true,
			"objectStateTransitionId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectStateFlowId", true, "sourceObjectStateId", true,
			"targetObjectStateId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectStateTransition newObjectStateTransition =
			addObjectStateTransition();

		ObjectStateTransition existingObjectStateTransition =
			_persistence.fetchByPrimaryKey(
				newObjectStateTransition.getPrimaryKey());

		Assert.assertEquals(
			existingObjectStateTransition, newObjectStateTransition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectStateTransition missingObjectStateTransition =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectStateTransition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectStateTransition newObjectStateTransition1 =
			addObjectStateTransition();
		ObjectStateTransition newObjectStateTransition2 =
			addObjectStateTransition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateTransition1.getPrimaryKey());
		primaryKeys.add(newObjectStateTransition2.getPrimaryKey());

		Map<Serializable, ObjectStateTransition> objectStateTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectStateTransitions.size());
		Assert.assertEquals(
			newObjectStateTransition1,
			objectStateTransitions.get(
				newObjectStateTransition1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectStateTransition2,
			objectStateTransitions.get(
				newObjectStateTransition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectStateTransition> objectStateTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStateTransitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectStateTransition newObjectStateTransition =
			addObjectStateTransition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateTransition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectStateTransition> objectStateTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStateTransitions.size());
		Assert.assertEquals(
			newObjectStateTransition,
			objectStateTransitions.get(
				newObjectStateTransition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectStateTransition> objectStateTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectStateTransitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectStateTransition newObjectStateTransition =
			addObjectStateTransition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectStateTransition.getPrimaryKey());

		Map<Serializable, ObjectStateTransition> objectStateTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectStateTransitions.size());
		Assert.assertEquals(
			newObjectStateTransition,
			objectStateTransitions.get(
				newObjectStateTransition.getPrimaryKey()));
	}

	protected ObjectStateTransition addObjectStateTransition()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ObjectStateTransition objectStateTransition = _persistence.create(pk);

		objectStateTransition.setMvccVersion(RandomTestUtil.nextLong());

		objectStateTransition.setUuid(RandomTestUtil.randomString());

		objectStateTransition.setCompanyId(RandomTestUtil.nextLong());

		objectStateTransition.setUserId(RandomTestUtil.nextLong());

		objectStateTransition.setUserName(RandomTestUtil.randomString());

		objectStateTransition.setCreateDate(RandomTestUtil.nextDate());

		objectStateTransition.setModifiedDate(RandomTestUtil.nextDate());

		objectStateTransition.setObjectStateFlowId(RandomTestUtil.nextLong());

		objectStateTransition.setSourceObjectStateId(RandomTestUtil.nextLong());

		objectStateTransition.setTargetObjectStateId(RandomTestUtil.nextLong());

		_objectStateTransitions.add(_persistence.update(objectStateTransition));

		return objectStateTransition;
	}

	private List<ObjectStateTransition> _objectStateTransitions =
		new ArrayList<ObjectStateTransition>();
	private ObjectStateTransitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}