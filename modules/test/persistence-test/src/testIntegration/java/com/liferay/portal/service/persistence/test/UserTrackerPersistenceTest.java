/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserTrackerException;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.service.persistence.UserTrackerPersistence;
import com.liferay.portal.kernel.service.persistence.UserTrackerUtil;
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
public class UserTrackerPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserTrackerUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserTracker> iterator = _userTrackers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTracker userTracker = _persistence.create(pk);

		Assert.assertNotNull(userTracker);

		Assert.assertEquals(userTracker.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserTracker newUserTracker = addUserTracker();

		_persistence.remove(newUserTracker);

		UserTracker existingUserTracker = _persistence.fetchByPrimaryKey(
			newUserTracker.getPrimaryKey());

		Assert.assertNull(existingUserTracker);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserTracker();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTracker newUserTracker = _persistence.create(pk);

		newUserTracker.setMvccVersion(RandomTestUtil.nextLong());

		newUserTracker.setCompanyId(RandomTestUtil.nextLong());

		newUserTracker.setUserId(RandomTestUtil.nextLong());

		newUserTracker.setModifiedDate(RandomTestUtil.nextDate());

		newUserTracker.setSessionId(RandomTestUtil.randomString());

		newUserTracker.setRemoteAddr(RandomTestUtil.randomString());

		newUserTracker.setRemoteHost(RandomTestUtil.randomString());

		newUserTracker.setUserAgent(RandomTestUtil.randomString());

		_userTrackers.add(_persistence.update(newUserTracker));

		UserTracker existingUserTracker = _persistence.findByPrimaryKey(
			newUserTracker.getPrimaryKey());

		Assert.assertEquals(
			existingUserTracker.getMvccVersion(),
			newUserTracker.getMvccVersion());
		Assert.assertEquals(
			existingUserTracker.getUserTrackerId(),
			newUserTracker.getUserTrackerId());
		Assert.assertEquals(
			existingUserTracker.getCompanyId(), newUserTracker.getCompanyId());
		Assert.assertEquals(
			existingUserTracker.getUserId(), newUserTracker.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUserTracker.getModifiedDate()),
			Time.getShortTimestamp(newUserTracker.getModifiedDate()));
		Assert.assertEquals(
			existingUserTracker.getSessionId(), newUserTracker.getSessionId());
		Assert.assertEquals(
			existingUserTracker.getRemoteAddr(),
			newUserTracker.getRemoteAddr());
		Assert.assertEquals(
			existingUserTracker.getRemoteHost(),
			newUserTracker.getRemoteHost());
		Assert.assertEquals(
			existingUserTracker.getUserAgent(), newUserTracker.getUserAgent());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountBySessionId() throws Exception {
		_persistence.countBySessionId("");

		_persistence.countBySessionId("null");

		_persistence.countBySessionId((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserTracker newUserTracker = addUserTracker();

		UserTracker existingUserTracker = _persistence.findByPrimaryKey(
			newUserTracker.getPrimaryKey());

		Assert.assertEquals(existingUserTracker, newUserTracker);
	}

	@Test(expected = NoSuchUserTrackerException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserTracker> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UserTracker", "mvccVersion", true, "userTrackerId", true,
			"companyId", true, "userId", true, "modifiedDate", true,
			"sessionId", true, "remoteAddr", true, "remoteHost", true,
			"userAgent", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserTracker newUserTracker = addUserTracker();

		UserTracker existingUserTracker = _persistence.fetchByPrimaryKey(
			newUserTracker.getPrimaryKey());

		Assert.assertEquals(existingUserTracker, newUserTracker);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTracker missingUserTracker = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUserTracker);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserTracker newUserTracker1 = addUserTracker();
		UserTracker newUserTracker2 = addUserTracker();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTracker1.getPrimaryKey());
		primaryKeys.add(newUserTracker2.getPrimaryKey());

		Map<Serializable, UserTracker> userTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userTrackers.size());
		Assert.assertEquals(
			newUserTracker1, userTrackers.get(newUserTracker1.getPrimaryKey()));
		Assert.assertEquals(
			newUserTracker2, userTrackers.get(newUserTracker2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserTracker> userTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userTrackers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserTracker newUserTracker = addUserTracker();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTracker.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserTracker> userTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userTrackers.size());
		Assert.assertEquals(
			newUserTracker, userTrackers.get(newUserTracker.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserTracker> userTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userTrackers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserTracker newUserTracker = addUserTracker();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTracker.getPrimaryKey());

		Map<Serializable, UserTracker> userTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userTrackers.size());
		Assert.assertEquals(
			newUserTracker, userTrackers.get(newUserTracker.getPrimaryKey()));
	}

	protected UserTracker addUserTracker() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTracker userTracker = _persistence.create(pk);

		userTracker.setMvccVersion(RandomTestUtil.nextLong());

		userTracker.setCompanyId(RandomTestUtil.nextLong());

		userTracker.setUserId(RandomTestUtil.nextLong());

		userTracker.setModifiedDate(RandomTestUtil.nextDate());

		userTracker.setSessionId(RandomTestUtil.randomString());

		userTracker.setRemoteAddr(RandomTestUtil.randomString());

		userTracker.setRemoteHost(RandomTestUtil.randomString());

		userTracker.setUserAgent(RandomTestUtil.randomString());

		_userTrackers.add(_persistence.update(userTracker));

		return userTracker;
	}

	private List<UserTracker> _userTrackers = new ArrayList<UserTracker>();
	private UserTrackerPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}