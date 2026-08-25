/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.background.task.exception.NoSuchBackgroundTaskException;
import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.background.task.service.BackgroundTaskLocalServiceUtil;
import com.liferay.portal.background.task.service.persistence.BackgroundTaskPersistence;
import com.liferay.portal.background.task.service.persistence.BackgroundTaskUtil;
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
import java.util.HashMap;
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
public class BackgroundTaskPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.background.task.service"));

	@Before
	public void setUp() {
		_persistence = BackgroundTaskUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BackgroundTask> iterator = _backgroundTasks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BackgroundTask backgroundTask = _persistence.create(pk);

		Assert.assertNotNull(backgroundTask);

		Assert.assertEquals(backgroundTask.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		_persistence.remove(newBackgroundTask);

		BackgroundTask existingBackgroundTask = _persistence.fetchByPrimaryKey(
			newBackgroundTask.getPrimaryKey());

		Assert.assertNull(existingBackgroundTask);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBackgroundTask();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		newBackgroundTask.setGroupId(RandomTestUtil.nextLong());

		newBackgroundTask.setCompanyId(RandomTestUtil.nextLong());

		newBackgroundTask.setUserId(RandomTestUtil.nextLong());

		newBackgroundTask.setUserName(RandomTestUtil.randomString());

		newBackgroundTask.setCreateDate(RandomTestUtil.nextDate());

		newBackgroundTask.setModifiedDate(RandomTestUtil.nextDate());

		newBackgroundTask.setName(RandomTestUtil.randomString());

		newBackgroundTask.setServletContextNames(RandomTestUtil.randomString());

		newBackgroundTask.setTaskExecutorClassName(
			RandomTestUtil.randomString());

		newBackgroundTask.setTaskContextMap(
			new HashMap<String, Serializable>());

		newBackgroundTask.setCompleted(RandomTestUtil.randomBoolean());

		newBackgroundTask.setCompletionDate(RandomTestUtil.nextDate());

		newBackgroundTask.setErrorStackTrace(RandomTestUtil.randomString());

		newBackgroundTask.setStatus(RandomTestUtil.nextInt());

		newBackgroundTask.setStatusMessage(RandomTestUtil.randomString());

		newBackgroundTask = _persistence.update(newBackgroundTask);

		_backgroundTasks.add(newBackgroundTask);

		BackgroundTask existingBackgroundTask = _persistence.findByPrimaryKey(
			newBackgroundTask.getPrimaryKey());

		Assert.assertEquals(
			existingBackgroundTask.getMvccVersion(),
			newBackgroundTask.getMvccVersion());
		Assert.assertEquals(
			existingBackgroundTask.getBackgroundTaskId(),
			newBackgroundTask.getBackgroundTaskId());
		Assert.assertEquals(
			existingBackgroundTask.getGroupId(),
			newBackgroundTask.getGroupId());
		Assert.assertEquals(
			existingBackgroundTask.getCompanyId(),
			newBackgroundTask.getCompanyId());
		Assert.assertEquals(
			existingBackgroundTask.getUserId(), newBackgroundTask.getUserId());
		Assert.assertEquals(
			existingBackgroundTask.getUserName(),
			newBackgroundTask.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBackgroundTask.getCreateDate()),
			Time.getShortTimestamp(newBackgroundTask.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingBackgroundTask.getModifiedDate()),
			Time.getShortTimestamp(newBackgroundTask.getModifiedDate()));
		Assert.assertEquals(
			existingBackgroundTask.getName(), newBackgroundTask.getName());
		Assert.assertEquals(
			existingBackgroundTask.getServletContextNames(),
			newBackgroundTask.getServletContextNames());
		Assert.assertEquals(
			existingBackgroundTask.getTaskExecutorClassName(),
			newBackgroundTask.getTaskExecutorClassName());
		Assert.assertEquals(
			existingBackgroundTask.getTaskContextMap(),
			newBackgroundTask.getTaskContextMap());
		Assert.assertEquals(
			existingBackgroundTask.isCompleted(),
			newBackgroundTask.isCompleted());
		Assert.assertEquals(
			Time.getShortTimestamp(existingBackgroundTask.getCompletionDate()),
			Time.getShortTimestamp(newBackgroundTask.getCompletionDate()));
		Assert.assertEquals(
			existingBackgroundTask.getErrorStackTrace(),
			newBackgroundTask.getErrorStackTrace());
		Assert.assertEquals(
			existingBackgroundTask.getStatus(), newBackgroundTask.getStatus());
		Assert.assertEquals(
			existingBackgroundTask.getStatusMessage(),
			newBackgroundTask.getStatusMessage());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCompleted() throws Exception {
		_persistence.countByCompleted(RandomTestUtil.randomBoolean());

		_persistence.countByCompleted(RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByStatus() throws Exception {
		_persistence.countByStatus(RandomTestUtil.nextInt());

		_persistence.countByStatus(0);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_TArrayable() throws Exception {
		_persistence.countByG_T(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByT_S() throws Exception {
		_persistence.countByT_S("", RandomTestUtil.nextInt());

		_persistence.countByT_S("null", 0);

		_persistence.countByT_S((String)null, 0);
	}

	@Test
	public void testCountByT_SArrayable() throws Exception {
		_persistence.countByT_S(
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_N_T() throws Exception {
		_persistence.countByG_N_T(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_N_T(0L, "null", "null");

		_persistence.countByG_N_T(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_N_TArrayable() throws Exception {
		_persistence.countByG_N_T(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByG_T_C() throws Exception {
		_persistence.countByG_T_C(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_T_C(0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_T_C(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_T_CArrayable() throws Exception {
		_persistence.countByG_T_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_T_S() throws Exception {
		_persistence.countByG_T_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_T_S(0L, "null", 0);

		_persistence.countByG_T_S(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_T_SArrayable() throws Exception {
		_persistence.countByG_T_S(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_N_T_C() throws Exception {
		_persistence.countByG_N_T_C(
			RandomTestUtil.nextLong(), "", "", RandomTestUtil.randomBoolean());

		_persistence.countByG_N_T_C(
			0L, "null", "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_N_T_C(
			0L, (String)null, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_N_T_CArrayable() throws Exception {
		_persistence.countByG_N_T_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		BackgroundTask existingBackgroundTask = _persistence.findByPrimaryKey(
			newBackgroundTask.getPrimaryKey());

		Assert.assertEquals(existingBackgroundTask, newBackgroundTask);
	}

	@Test(expected = NoSuchBackgroundTaskException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BackgroundTask> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BackgroundTask", "mvccVersion", true, "backgroundTaskId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "name", true,
			"servletContextNames", true, "taskExecutorClassName", true,
			"completed", true, "completionDate", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		BackgroundTask existingBackgroundTask = _persistence.fetchByPrimaryKey(
			newBackgroundTask.getPrimaryKey());

		Assert.assertEquals(existingBackgroundTask, newBackgroundTask);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BackgroundTask missingBackgroundTask = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingBackgroundTask);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BackgroundTask newBackgroundTask1 = addBackgroundTask();
		BackgroundTask newBackgroundTask2 = addBackgroundTask();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBackgroundTask1.getPrimaryKey());
		primaryKeys.add(newBackgroundTask2.getPrimaryKey());

		Map<Serializable, BackgroundTask> backgroundTasks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, backgroundTasks.size());
		Assert.assertEquals(
			newBackgroundTask1,
			backgroundTasks.get(newBackgroundTask1.getPrimaryKey()));
		Assert.assertEquals(
			newBackgroundTask2,
			backgroundTasks.get(newBackgroundTask2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BackgroundTask> backgroundTasks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(backgroundTasks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BackgroundTask newBackgroundTask = addBackgroundTask();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBackgroundTask.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BackgroundTask> backgroundTasks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, backgroundTasks.size());
		Assert.assertEquals(
			newBackgroundTask,
			backgroundTasks.get(newBackgroundTask.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BackgroundTask> backgroundTasks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(backgroundTasks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBackgroundTask.getPrimaryKey());

		Map<Serializable, BackgroundTask> backgroundTasks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, backgroundTasks.size());
		Assert.assertEquals(
			newBackgroundTask,
			backgroundTasks.get(newBackgroundTask.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			BackgroundTaskLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<BackgroundTask>() {

				@Override
				public void performAction(BackgroundTask backgroundTask) {
					Assert.assertNotNull(backgroundTask);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BackgroundTask.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"backgroundTaskId", newBackgroundTask.getBackgroundTaskId()));

		List<BackgroundTask> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		BackgroundTask existingBackgroundTask = result.get(0);

		Assert.assertEquals(existingBackgroundTask, newBackgroundTask);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BackgroundTask.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"backgroundTaskId", RandomTestUtil.nextLong()));

		List<BackgroundTask> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		BackgroundTask newBackgroundTask = addBackgroundTask();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BackgroundTask.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("backgroundTaskId"));

		Object newBackgroundTaskId = newBackgroundTask.getBackgroundTaskId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"backgroundTaskId", new Object[] {newBackgroundTaskId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingBackgroundTaskId = result.get(0);

		Assert.assertEquals(existingBackgroundTaskId, newBackgroundTaskId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			BackgroundTask.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("backgroundTaskId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"backgroundTaskId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected BackgroundTask addBackgroundTask() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BackgroundTask backgroundTask = _persistence.create(pk);

		backgroundTask.setGroupId(RandomTestUtil.nextLong());

		backgroundTask.setCompanyId(RandomTestUtil.nextLong());

		backgroundTask.setUserId(RandomTestUtil.nextLong());

		backgroundTask.setUserName(RandomTestUtil.randomString());

		backgroundTask.setCreateDate(RandomTestUtil.nextDate());

		backgroundTask.setModifiedDate(RandomTestUtil.nextDate());

		backgroundTask.setName(RandomTestUtil.randomString());

		backgroundTask.setServletContextNames(RandomTestUtil.randomString());

		backgroundTask.setTaskExecutorClassName(RandomTestUtil.randomString());

		backgroundTask.setTaskContextMap(new HashMap<String, Serializable>());

		backgroundTask.setCompleted(RandomTestUtil.randomBoolean());

		backgroundTask.setCompletionDate(RandomTestUtil.nextDate());

		backgroundTask.setErrorStackTrace(RandomTestUtil.randomString());

		backgroundTask.setStatus(RandomTestUtil.nextInt());

		backgroundTask.setStatusMessage(RandomTestUtil.randomString());

		_backgroundTasks.add(_persistence.update(backgroundTask));

		return backgroundTask;
	}

	private List<BackgroundTask> _backgroundTasks =
		new ArrayList<BackgroundTask>();
	private BackgroundTaskPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2145964843