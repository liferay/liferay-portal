/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserTrackerPathException;
import com.liferay.portal.kernel.model.UserTrackerPath;
import com.liferay.portal.kernel.service.UserTrackerPathLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.UserTrackerPathPersistence;
import com.liferay.portal.kernel.service.persistence.UserTrackerPathUtil;
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
public class UserTrackerPathPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserTrackerPathUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserTrackerPath> iterator = _userTrackerPaths.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTrackerPath userTrackerPath = _persistence.create(pk);

		Assert.assertNotNull(userTrackerPath);

		Assert.assertEquals(userTrackerPath.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		_persistence.remove(newUserTrackerPath);

		UserTrackerPath existingUserTrackerPath =
			_persistence.fetchByPrimaryKey(newUserTrackerPath.getPrimaryKey());

		Assert.assertNull(existingUserTrackerPath);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserTrackerPath();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		newUserTrackerPath.setCompanyId(RandomTestUtil.nextLong());

		newUserTrackerPath.setUserTrackerId(RandomTestUtil.nextLong());

		newUserTrackerPath.setPath(RandomTestUtil.randomString());

		newUserTrackerPath.setPathDate(RandomTestUtil.nextDate());

		newUserTrackerPath = _persistence.update(newUserTrackerPath);

		_userTrackerPaths.add(newUserTrackerPath);

		UserTrackerPath existingUserTrackerPath = _persistence.findByPrimaryKey(
			newUserTrackerPath.getPrimaryKey());

		Assert.assertEquals(
			existingUserTrackerPath.getMvccVersion(),
			newUserTrackerPath.getMvccVersion());
		Assert.assertEquals(
			existingUserTrackerPath.getUserTrackerPathId(),
			newUserTrackerPath.getUserTrackerPathId());
		Assert.assertEquals(
			existingUserTrackerPath.getCompanyId(),
			newUserTrackerPath.getCompanyId());
		Assert.assertEquals(
			existingUserTrackerPath.getUserTrackerId(),
			newUserTrackerPath.getUserTrackerId());
		Assert.assertEquals(
			existingUserTrackerPath.getPath(), newUserTrackerPath.getPath());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUserTrackerPath.getPathDate()),
			Time.getShortTimestamp(newUserTrackerPath.getPathDate()));
	}

	@Test
	public void testCountByUserTrackerId() throws Exception {
		_persistence.countByUserTrackerId(RandomTestUtil.nextLong());

		_persistence.countByUserTrackerId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		UserTrackerPath existingUserTrackerPath = _persistence.findByPrimaryKey(
			newUserTrackerPath.getPrimaryKey());

		Assert.assertEquals(existingUserTrackerPath, newUserTrackerPath);
	}

	@Test(expected = NoSuchUserTrackerPathException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserTrackerPath> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UserTrackerPath", "mvccVersion", true, "userTrackerPathId", true,
			"companyId", true, "userTrackerId", true, "path", true, "pathDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		UserTrackerPath existingUserTrackerPath =
			_persistence.fetchByPrimaryKey(newUserTrackerPath.getPrimaryKey());

		Assert.assertEquals(existingUserTrackerPath, newUserTrackerPath);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTrackerPath missingUserTrackerPath = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingUserTrackerPath);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserTrackerPath newUserTrackerPath1 = addUserTrackerPath();
		UserTrackerPath newUserTrackerPath2 = addUserTrackerPath();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTrackerPath1.getPrimaryKey());
		primaryKeys.add(newUserTrackerPath2.getPrimaryKey());

		Map<Serializable, UserTrackerPath> userTrackerPaths =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userTrackerPaths.size());
		Assert.assertEquals(
			newUserTrackerPath1,
			userTrackerPaths.get(newUserTrackerPath1.getPrimaryKey()));
		Assert.assertEquals(
			newUserTrackerPath2,
			userTrackerPaths.get(newUserTrackerPath2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserTrackerPath> userTrackerPaths =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userTrackerPaths.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTrackerPath.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserTrackerPath> userTrackerPaths =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userTrackerPaths.size());
		Assert.assertEquals(
			newUserTrackerPath,
			userTrackerPaths.get(newUserTrackerPath.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserTrackerPath> userTrackerPaths =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userTrackerPaths.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserTrackerPath.getPrimaryKey());

		Map<Serializable, UserTrackerPath> userTrackerPaths =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userTrackerPaths.size());
		Assert.assertEquals(
			newUserTrackerPath,
			userTrackerPaths.get(newUserTrackerPath.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			UserTrackerPathLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<UserTrackerPath>() {

				@Override
				public void performAction(UserTrackerPath userTrackerPath) {
					Assert.assertNotNull(userTrackerPath);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserTrackerPath.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"userTrackerPathId",
				newUserTrackerPath.getUserTrackerPathId()));

		List<UserTrackerPath> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		UserTrackerPath existingUserTrackerPath = result.get(0);

		Assert.assertEquals(existingUserTrackerPath, newUserTrackerPath);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserTrackerPath.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"userTrackerPathId", RandomTestUtil.nextLong()));

		List<UserTrackerPath> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		UserTrackerPath newUserTrackerPath = addUserTrackerPath();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserTrackerPath.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("userTrackerPathId"));

		Object newUserTrackerPathId = newUserTrackerPath.getUserTrackerPathId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"userTrackerPathId", new Object[] {newUserTrackerPathId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingUserTrackerPathId = result.get(0);

		Assert.assertEquals(existingUserTrackerPathId, newUserTrackerPathId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			UserTrackerPath.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("userTrackerPathId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"userTrackerPathId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected UserTrackerPath addUserTrackerPath() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserTrackerPath userTrackerPath = _persistence.create(pk);

		userTrackerPath.setCompanyId(RandomTestUtil.nextLong());

		userTrackerPath.setUserTrackerId(RandomTestUtil.nextLong());

		userTrackerPath.setPath(RandomTestUtil.randomString());

		userTrackerPath.setPathDate(RandomTestUtil.nextDate());

		_userTrackerPaths.add(_persistence.update(userTrackerPath));

		return userTrackerPath;
	}

	private List<UserTrackerPath> _userTrackerPaths =
		new ArrayList<UserTrackerPath>();
	private UserTrackerPathPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1266489598