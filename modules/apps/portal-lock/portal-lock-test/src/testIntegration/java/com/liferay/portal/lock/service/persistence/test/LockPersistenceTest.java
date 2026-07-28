/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.lock.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.lock.exception.NoSuchLockException;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.lock.service.LockLocalServiceUtil;
import com.liferay.portal.lock.service.persistence.LockPersistence;
import com.liferay.portal.lock.service.persistence.LockUtil;
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
public class LockPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.portal.lock.service"));

	@Before
	public void setUp() {
		_persistence = LockUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Lock> iterator = _locks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Lock lock = _persistence.create(pk);

		Assert.assertNotNull(lock);

		Assert.assertEquals(lock.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Lock newLock = addLock();

		_persistence.remove(newLock);

		Lock existingLock = _persistence.fetchByPrimaryKey(
			newLock.getPrimaryKey());

		Assert.assertNull(existingLock);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLock();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		Lock newLock = addLock();

		newLock.setUuid(RandomTestUtil.randomString());

		newLock.setCompanyId(RandomTestUtil.nextLong());

		newLock.setUserId(RandomTestUtil.nextLong());

		newLock.setUserName(RandomTestUtil.randomString());

		newLock.setCreateDate(RandomTestUtil.nextDate());

		newLock.setClassName(RandomTestUtil.randomString());

		newLock.setKey(RandomTestUtil.randomString());

		newLock.setOwner(RandomTestUtil.randomString());

		newLock.setInheritable(RandomTestUtil.randomBoolean());

		newLock.setExpirationDate(RandomTestUtil.nextDate());

		newLock = _persistence.update(newLock);

		_locks.add(newLock);

		Lock existingLock = _persistence.findByPrimaryKey(
			newLock.getPrimaryKey());

		Assert.assertEquals(
			existingLock.getMvccVersion(), newLock.getMvccVersion());
		Assert.assertEquals(existingLock.getUuid(), newLock.getUuid());
		Assert.assertEquals(existingLock.getLockId(), newLock.getLockId());
		Assert.assertEquals(
			existingLock.getCompanyId(), newLock.getCompanyId());
		Assert.assertEquals(existingLock.getUserId(), newLock.getUserId());
		Assert.assertEquals(existingLock.getUserName(), newLock.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLock.getCreateDate()),
			Time.getShortTimestamp(newLock.getCreateDate()));
		Assert.assertEquals(
			existingLock.getClassName(), newLock.getClassName());
		Assert.assertEquals(existingLock.getKey(), newLock.getKey());
		Assert.assertEquals(existingLock.getOwner(), newLock.getOwner());
		Assert.assertEquals(
			existingLock.isInheritable(), newLock.isInheritable());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLock.getExpirationDate()),
			Time.getShortTimestamp(newLock.getExpirationDate()));
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
	public void testCountByClassName() throws Exception {
		_persistence.countByClassName("");

		_persistence.countByClassName("null");

		_persistence.countByClassName((String)null);
	}

	@Test
	public void testCountByLtExpirationDate() throws Exception {
		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());

		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K("", "");

		_persistence.countByC_K("null", "null");

		_persistence.countByC_K((String)null, (String)null);
	}

	@Test
	public void testCountByC_U_C() throws Exception {
		_persistence.countByC_U_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_U_C(0L, 0L, "null");

		_persistence.countByC_U_C(0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Lock newLock = addLock();

		Lock existingLock = _persistence.findByPrimaryKey(
			newLock.getPrimaryKey());

		Assert.assertEquals(existingLock, newLock);
	}

	@Test(expected = NoSuchLockException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Lock> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Lock_", "mvccVersion", true, "uuid", true, "lockId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "className", true, "key", true, "owner", true, "inheritable",
			true, "expirationDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Lock newLock = addLock();

		Lock existingLock = _persistence.fetchByPrimaryKey(
			newLock.getPrimaryKey());

		Assert.assertEquals(existingLock, newLock);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Lock missingLock = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLock);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Lock newLock1 = addLock();
		Lock newLock2 = addLock();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLock1.getPrimaryKey());
		primaryKeys.add(newLock2.getPrimaryKey());

		Map<Serializable, Lock> locks = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, locks.size());
		Assert.assertEquals(newLock1, locks.get(newLock1.getPrimaryKey()));
		Assert.assertEquals(newLock2, locks.get(newLock2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Lock> locks = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(locks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Lock newLock = addLock();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLock.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Lock> locks = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, locks.size());
		Assert.assertEquals(newLock, locks.get(newLock.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Lock> locks = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(locks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Lock newLock = addLock();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLock.getPrimaryKey());

		Map<Serializable, Lock> locks = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, locks.size());
		Assert.assertEquals(newLock, locks.get(newLock.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			LockLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Lock>() {

				@Override
				public void performAction(Lock lock) {
					Assert.assertNotNull(lock);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Lock newLock = addLock();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Lock.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("lockId", newLock.getLockId()));

		List<Lock> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Lock existingLock = result.get(0);

		Assert.assertEquals(existingLock, newLock);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Lock.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("lockId", RandomTestUtil.nextLong()));

		List<Lock> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Lock newLock = addLock();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Lock.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("lockId"));

		Object newLockId = newLock.getLockId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("lockId", new Object[] {newLockId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingLockId = result.get(0);

		Assert.assertEquals(existingLockId, newLockId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Lock.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("lockId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"lockId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Lock newLock = addLock();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLock.getPrimaryKey()));
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

		Lock newLock = addLock();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Lock.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("lockId", newLock.getLockId()));

		List<Lock> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Lock lock) {
		Assert.assertEquals(
			lock.getClassName(),
			ReflectionTestUtil.invoke(
				lock, "getColumnOriginalValue", new Class<?>[] {String.class},
				"className"));
		Assert.assertEquals(
			lock.getKey(),
			ReflectionTestUtil.invoke(
				lock, "getColumnOriginalValue", new Class<?>[] {String.class},
				"key_"));
	}

	protected Lock addLock() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Lock lock = _persistence.create(pk);

		lock.setUuid(RandomTestUtil.randomString());

		lock.setCompanyId(RandomTestUtil.nextLong());

		lock.setUserId(RandomTestUtil.nextLong());

		lock.setUserName(RandomTestUtil.randomString());

		lock.setCreateDate(RandomTestUtil.nextDate());

		lock.setClassName(RandomTestUtil.randomString());

		lock.setKey(RandomTestUtil.randomString());

		lock.setOwner(RandomTestUtil.randomString());

		lock.setInheritable(RandomTestUtil.randomBoolean());

		lock.setExpirationDate(RandomTestUtil.nextDate());

		_locks.add(_persistence.update(lock));

		return lock;
	}

	private List<Lock> _locks = new ArrayList<Lock>();
	private LockPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:410881603