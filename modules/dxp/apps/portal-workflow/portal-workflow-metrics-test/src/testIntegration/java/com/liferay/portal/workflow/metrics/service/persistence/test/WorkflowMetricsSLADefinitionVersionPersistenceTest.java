/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.workflow.metrics.exception.NoSuchSLADefinitionVersionException;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionVersionPersistence;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionVersionUtil;

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
public class WorkflowMetricsSLADefinitionVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.metrics.service"));

	@Before
	public void setUp() {
		_persistence = WorkflowMetricsSLADefinitionVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WorkflowMetricsSLADefinitionVersion> iterator =
			_workflowMetricsSLADefinitionVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion = _persistence.create(pk);

		Assert.assertNotNull(workflowMetricsSLADefinitionVersion);

		Assert.assertEquals(
			workflowMetricsSLADefinitionVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		_persistence.remove(newWorkflowMetricsSLADefinitionVersion);

		WorkflowMetricsSLADefinitionVersion
			existingWorkflowMetricsSLADefinitionVersion =
				_persistence.fetchByPrimaryKey(
					newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());

		Assert.assertNull(existingWorkflowMetricsSLADefinitionVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWorkflowMetricsSLADefinitionVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion = _persistence.create(pk);

		newWorkflowMetricsSLADefinitionVersion.setMvccVersion(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setUuid(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setGroupId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setCompanyId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setUserId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setUserName(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setCreateDate(
			RandomTestUtil.nextDate());

		newWorkflowMetricsSLADefinitionVersion.setModifiedDate(
			RandomTestUtil.nextDate());

		newWorkflowMetricsSLADefinitionVersion.setActive(
			RandomTestUtil.randomBoolean());

		newWorkflowMetricsSLADefinitionVersion.setCalendarKey(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setDescription(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setDuration(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setName(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setPauseNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setProcessId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setProcessVersion(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setStartNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setStopNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setVersion(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.
			setWorkflowMetricsSLADefinitionId(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setStatus(
			RandomTestUtil.nextInt());

		newWorkflowMetricsSLADefinitionVersion.setStatusByUserId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinitionVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinitionVersion.setStatusDate(
			RandomTestUtil.nextDate());

		_workflowMetricsSLADefinitionVersions.add(
			_persistence.update(newWorkflowMetricsSLADefinitionVersion));

		WorkflowMetricsSLADefinitionVersion
			existingWorkflowMetricsSLADefinitionVersion =
				_persistence.findByPrimaryKey(
					newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getMvccVersion(),
			newWorkflowMetricsSLADefinitionVersion.getMvccVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getUuid(),
			newWorkflowMetricsSLADefinitionVersion.getUuid());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.
				getWorkflowMetricsSLADefinitionVersionId(),
			newWorkflowMetricsSLADefinitionVersion.
				getWorkflowMetricsSLADefinitionVersionId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getGroupId(),
			newWorkflowMetricsSLADefinitionVersion.getGroupId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getCompanyId(),
			newWorkflowMetricsSLADefinitionVersion.getCompanyId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getUserId(),
			newWorkflowMetricsSLADefinitionVersion.getUserId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getUserName(),
			newWorkflowMetricsSLADefinitionVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinitionVersion.getCreateDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinitionVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinitionVersion.getModifiedDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinitionVersion.getModifiedDate()));
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.isActive(),
			newWorkflowMetricsSLADefinitionVersion.isActive());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getCalendarKey(),
			newWorkflowMetricsSLADefinitionVersion.getCalendarKey());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getDescription(),
			newWorkflowMetricsSLADefinitionVersion.getDescription());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getDuration(),
			newWorkflowMetricsSLADefinitionVersion.getDuration());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getName(),
			newWorkflowMetricsSLADefinitionVersion.getName());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getPauseNodeKeys(),
			newWorkflowMetricsSLADefinitionVersion.getPauseNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getProcessId(),
			newWorkflowMetricsSLADefinitionVersion.getProcessId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getProcessVersion(),
			newWorkflowMetricsSLADefinitionVersion.getProcessVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getStartNodeKeys(),
			newWorkflowMetricsSLADefinitionVersion.getStartNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getStopNodeKeys(),
			newWorkflowMetricsSLADefinitionVersion.getStopNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getVersion(),
			newWorkflowMetricsSLADefinitionVersion.getVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.
				getWorkflowMetricsSLADefinitionId(),
			newWorkflowMetricsSLADefinitionVersion.
				getWorkflowMetricsSLADefinitionId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getStatus(),
			newWorkflowMetricsSLADefinitionVersion.getStatus());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getStatusByUserId(),
			newWorkflowMetricsSLADefinitionVersion.getStatusByUserId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion.getStatusByUserName(),
			newWorkflowMetricsSLADefinitionVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinitionVersion.getStatusDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinitionVersion.getStatusDate()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByWorkflowMetricsSLADefinitionId() throws Exception {
		_persistence.countByWorkflowMetricsSLADefinitionId(
			RandomTestUtil.nextLong());

		_persistence.countByWorkflowMetricsSLADefinitionId(0L);
	}

	@Test
	public void testCountByV_WMSLAD() throws Exception {
		_persistence.countByV_WMSLAD("", RandomTestUtil.nextLong());

		_persistence.countByV_WMSLAD("null", 0L);

		_persistence.countByV_WMSLAD((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		WorkflowMetricsSLADefinitionVersion
			existingWorkflowMetricsSLADefinitionVersion =
				_persistence.findByPrimaryKey(
					newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion,
			newWorkflowMetricsSLADefinitionVersion);
	}

	@Test(expected = NoSuchSLADefinitionVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WorkflowMetricsSLADefinitionVersion>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"WMSLADefinitionVersion", "mvccVersion", true, "uuid", true,
			"workflowMetricsSLADefinitionVersionId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "active", true, "calendarKey", true,
			"duration", true, "name", true, "pauseNodeKeys", true, "processId",
			true, "processVersion", true, "startNodeKeys", true, "stopNodeKeys",
			true, "version", true, "workflowMetricsSLADefinitionId", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		WorkflowMetricsSLADefinitionVersion
			existingWorkflowMetricsSLADefinitionVersion =
				_persistence.fetchByPrimaryKey(
					newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinitionVersion,
			newWorkflowMetricsSLADefinitionVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinitionVersion
			missingWorkflowMetricsSLADefinitionVersion =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWorkflowMetricsSLADefinitionVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion1 =
				addWorkflowMetricsSLADefinitionVersion();
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion2 =
				addWorkflowMetricsSLADefinitionVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newWorkflowMetricsSLADefinitionVersion1.getPrimaryKey());
		primaryKeys.add(
			newWorkflowMetricsSLADefinitionVersion2.getPrimaryKey());

		Map<Serializable, WorkflowMetricsSLADefinitionVersion>
			workflowMetricsSLADefinitionVersions =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, workflowMetricsSLADefinitionVersions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinitionVersion1,
			workflowMetricsSLADefinitionVersions.get(
				newWorkflowMetricsSLADefinitionVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newWorkflowMetricsSLADefinitionVersion2,
			workflowMetricsSLADefinitionVersions.get(
				newWorkflowMetricsSLADefinitionVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WorkflowMetricsSLADefinitionVersion>
			workflowMetricsSLADefinitionVersions =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowMetricsSLADefinitionVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WorkflowMetricsSLADefinitionVersion>
			workflowMetricsSLADefinitionVersions =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowMetricsSLADefinitionVersions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinitionVersion,
			workflowMetricsSLADefinitionVersions.get(
				newWorkflowMetricsSLADefinitionVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WorkflowMetricsSLADefinitionVersion>
			workflowMetricsSLADefinitionVersions =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowMetricsSLADefinitionVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowMetricsSLADefinitionVersion.getPrimaryKey());

		Map<Serializable, WorkflowMetricsSLADefinitionVersion>
			workflowMetricsSLADefinitionVersions =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowMetricsSLADefinitionVersions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinitionVersion,
			workflowMetricsSLADefinitionVersions.get(
				newWorkflowMetricsSLADefinitionVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WorkflowMetricsSLADefinitionVersion
			newWorkflowMetricsSLADefinitionVersion =
				addWorkflowMetricsSLADefinitionVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newWorkflowMetricsSLADefinitionVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion) {

		Assert.assertEquals(
			workflowMetricsSLADefinitionVersion.getUuid(),
			ReflectionTestUtil.invoke(
				workflowMetricsSLADefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(workflowMetricsSLADefinitionVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowMetricsSLADefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			workflowMetricsSLADefinitionVersion.getVersion(),
			ReflectionTestUtil.invoke(
				workflowMetricsSLADefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
		Assert.assertEquals(
			Long.valueOf(
				workflowMetricsSLADefinitionVersion.
					getWorkflowMetricsSLADefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				workflowMetricsSLADefinitionVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "wmSLADefinitionId"));
	}

	protected WorkflowMetricsSLADefinitionVersion
			addWorkflowMetricsSLADefinitionVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinitionVersion
			workflowMetricsSLADefinitionVersion = _persistence.create(pk);

		workflowMetricsSLADefinitionVersion.setMvccVersion(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setUuid(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setGroupId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setCompanyId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setUserId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setUserName(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setCreateDate(
			RandomTestUtil.nextDate());

		workflowMetricsSLADefinitionVersion.setModifiedDate(
			RandomTestUtil.nextDate());

		workflowMetricsSLADefinitionVersion.setActive(
			RandomTestUtil.randomBoolean());

		workflowMetricsSLADefinitionVersion.setCalendarKey(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setDescription(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setDuration(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setName(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setPauseNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setProcessId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setProcessVersion(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setStartNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setStopNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setVersion(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setWorkflowMetricsSLADefinitionId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setStatus(RandomTestUtil.nextInt());

		workflowMetricsSLADefinitionVersion.setStatusByUserId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinitionVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinitionVersion.setStatusDate(
			RandomTestUtil.nextDate());

		_workflowMetricsSLADefinitionVersions.add(
			_persistence.update(workflowMetricsSLADefinitionVersion));

		return workflowMetricsSLADefinitionVersion;
	}

	private List<WorkflowMetricsSLADefinitionVersion>
		_workflowMetricsSLADefinitionVersions =
			new ArrayList<WorkflowMetricsSLADefinitionVersion>();
	private WorkflowMetricsSLADefinitionVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}