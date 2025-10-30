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
import com.liferay.portal.workflow.metrics.exception.NoSuchSLADefinitionException;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinition;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionPersistence;
import com.liferay.portal.workflow.metrics.service.persistence.WorkflowMetricsSLADefinitionUtil;

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
public class WorkflowMetricsSLADefinitionPersistenceTest {

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
		_persistence = WorkflowMetricsSLADefinitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WorkflowMetricsSLADefinition> iterator =
			_workflowMetricsSLADefinitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			_persistence.create(pk);

		Assert.assertNotNull(workflowMetricsSLADefinition);

		Assert.assertEquals(workflowMetricsSLADefinition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		_persistence.remove(newWorkflowMetricsSLADefinition);

		WorkflowMetricsSLADefinition existingWorkflowMetricsSLADefinition =
			_persistence.fetchByPrimaryKey(
				newWorkflowMetricsSLADefinition.getPrimaryKey());

		Assert.assertNull(existingWorkflowMetricsSLADefinition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWorkflowMetricsSLADefinition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			_persistence.create(pk);

		newWorkflowMetricsSLADefinition.setMvccVersion(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setUuid(RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setGroupId(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setCompanyId(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setUserId(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setUserName(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setCreateDate(
			RandomTestUtil.nextDate());

		newWorkflowMetricsSLADefinition.setModifiedDate(
			RandomTestUtil.nextDate());

		newWorkflowMetricsSLADefinition.setActive(
			RandomTestUtil.randomBoolean());

		newWorkflowMetricsSLADefinition.setCalendarKey(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setDescription(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setDuration(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setName(RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setPauseNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setProcessId(RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setProcessVersion(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setStartNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setStopNodeKeys(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setVersion(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setStatus(RandomTestUtil.nextInt());

		newWorkflowMetricsSLADefinition.setStatusByUserId(
			RandomTestUtil.nextLong());

		newWorkflowMetricsSLADefinition.setStatusByUserName(
			RandomTestUtil.randomString());

		newWorkflowMetricsSLADefinition.setStatusDate(
			RandomTestUtil.nextDate());

		_workflowMetricsSLADefinitions.add(
			_persistence.update(newWorkflowMetricsSLADefinition));

		WorkflowMetricsSLADefinition existingWorkflowMetricsSLADefinition =
			_persistence.findByPrimaryKey(
				newWorkflowMetricsSLADefinition.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getMvccVersion(),
			newWorkflowMetricsSLADefinition.getMvccVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getUuid(),
			newWorkflowMetricsSLADefinition.getUuid());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.
				getWorkflowMetricsSLADefinitionId(),
			newWorkflowMetricsSLADefinition.
				getWorkflowMetricsSLADefinitionId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getGroupId(),
			newWorkflowMetricsSLADefinition.getGroupId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getCompanyId(),
			newWorkflowMetricsSLADefinition.getCompanyId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getUserId(),
			newWorkflowMetricsSLADefinition.getUserId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getUserName(),
			newWorkflowMetricsSLADefinition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinition.getCreateDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinition.getModifiedDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinition.getModifiedDate()));
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.isActive(),
			newWorkflowMetricsSLADefinition.isActive());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getCalendarKey(),
			newWorkflowMetricsSLADefinition.getCalendarKey());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getDescription(),
			newWorkflowMetricsSLADefinition.getDescription());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getDuration(),
			newWorkflowMetricsSLADefinition.getDuration());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getName(),
			newWorkflowMetricsSLADefinition.getName());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getPauseNodeKeys(),
			newWorkflowMetricsSLADefinition.getPauseNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getProcessId(),
			newWorkflowMetricsSLADefinition.getProcessId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getProcessVersion(),
			newWorkflowMetricsSLADefinition.getProcessVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getStartNodeKeys(),
			newWorkflowMetricsSLADefinition.getStartNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getStopNodeKeys(),
			newWorkflowMetricsSLADefinition.getStopNodeKeys());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getVersion(),
			newWorkflowMetricsSLADefinition.getVersion());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getStatus(),
			newWorkflowMetricsSLADefinition.getStatus());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getStatusByUserId(),
			newWorkflowMetricsSLADefinition.getStatusByUserId());
		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition.getStatusByUserName(),
			newWorkflowMetricsSLADefinition.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowMetricsSLADefinition.getStatusDate()),
			Time.getShortTimestamp(
				newWorkflowMetricsSLADefinition.getStatusDate()));
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
	public void testCountByWMSLAD_A() throws Exception {
		_persistence.countByWMSLAD_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByWMSLAD_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByC_A_P() throws Exception {
		_persistence.countByC_A_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByC_A_P(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByC_A_N_P() throws Exception {
		_persistence.countByC_A_N_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextLong());

		_persistence.countByC_A_N_P(
			0L, RandomTestUtil.randomBoolean(), "null", 0L);

		_persistence.countByC_A_N_P(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0L);
	}

	@Test
	public void testCountByC_A_P_S() throws Exception {
		_persistence.countByC_A_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_A_P_S(0L, RandomTestUtil.randomBoolean(), 0L, 0);
	}

	@Test
	public void testCountByC_A_P_NotPV_S() throws Exception {
		_persistence.countByC_A_P_NotPV_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByC_A_P_NotPV_S(
			0L, RandomTestUtil.randomBoolean(), 0L, "null", 0);

		_persistence.countByC_A_P_NotPV_S(
			0L, RandomTestUtil.randomBoolean(), 0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		WorkflowMetricsSLADefinition existingWorkflowMetricsSLADefinition =
			_persistence.findByPrimaryKey(
				newWorkflowMetricsSLADefinition.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition,
			newWorkflowMetricsSLADefinition);
	}

	@Test(expected = NoSuchSLADefinitionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WorkflowMetricsSLADefinition>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"WMSLADefinition", "mvccVersion", true, "uuid", true,
			"workflowMetricsSLADefinitionId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "active", true, "calendarKey", true,
			"duration", true, "name", true, "pauseNodeKeys", true, "processId",
			true, "processVersion", true, "startNodeKeys", true, "stopNodeKeys",
			true, "version", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		WorkflowMetricsSLADefinition existingWorkflowMetricsSLADefinition =
			_persistence.fetchByPrimaryKey(
				newWorkflowMetricsSLADefinition.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowMetricsSLADefinition,
			newWorkflowMetricsSLADefinition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinition missingWorkflowMetricsSLADefinition =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWorkflowMetricsSLADefinition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition1 =
			addWorkflowMetricsSLADefinition();
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition2 =
			addWorkflowMetricsSLADefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowMetricsSLADefinition1.getPrimaryKey());
		primaryKeys.add(newWorkflowMetricsSLADefinition2.getPrimaryKey());

		Map<Serializable, WorkflowMetricsSLADefinition>
			workflowMetricsSLADefinitions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, workflowMetricsSLADefinitions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinition1,
			workflowMetricsSLADefinitions.get(
				newWorkflowMetricsSLADefinition1.getPrimaryKey()));
		Assert.assertEquals(
			newWorkflowMetricsSLADefinition2,
			workflowMetricsSLADefinitions.get(
				newWorkflowMetricsSLADefinition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WorkflowMetricsSLADefinition>
			workflowMetricsSLADefinitions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(workflowMetricsSLADefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowMetricsSLADefinition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WorkflowMetricsSLADefinition>
			workflowMetricsSLADefinitions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, workflowMetricsSLADefinitions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinition,
			workflowMetricsSLADefinitions.get(
				newWorkflowMetricsSLADefinition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WorkflowMetricsSLADefinition>
			workflowMetricsSLADefinitions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(workflowMetricsSLADefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowMetricsSLADefinition.getPrimaryKey());

		Map<Serializable, WorkflowMetricsSLADefinition>
			workflowMetricsSLADefinitions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, workflowMetricsSLADefinitions.size());
		Assert.assertEquals(
			newWorkflowMetricsSLADefinition,
			workflowMetricsSLADefinitions.get(
				newWorkflowMetricsSLADefinition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WorkflowMetricsSLADefinition newWorkflowMetricsSLADefinition =
			addWorkflowMetricsSLADefinition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newWorkflowMetricsSLADefinition.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		WorkflowMetricsSLADefinition workflowMetricsSLADefinition) {

		Assert.assertEquals(
			workflowMetricsSLADefinition.getUuid(),
			ReflectionTestUtil.invoke(
				workflowMetricsSLADefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(workflowMetricsSLADefinition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowMetricsSLADefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(
				workflowMetricsSLADefinition.
					getWorkflowMetricsSLADefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				workflowMetricsSLADefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "wmSLADefinitionId"));
		Assert.assertEquals(
			Boolean.valueOf(workflowMetricsSLADefinition.getActive()),
			ReflectionTestUtil.<Boolean>invoke(
				workflowMetricsSLADefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "active_"));
	}

	protected WorkflowMetricsSLADefinition addWorkflowMetricsSLADefinition()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		WorkflowMetricsSLADefinition workflowMetricsSLADefinition =
			_persistence.create(pk);

		workflowMetricsSLADefinition.setMvccVersion(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setUuid(RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setGroupId(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setCompanyId(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setUserId(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setUserName(RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setCreateDate(RandomTestUtil.nextDate());

		workflowMetricsSLADefinition.setModifiedDate(RandomTestUtil.nextDate());

		workflowMetricsSLADefinition.setActive(RandomTestUtil.randomBoolean());

		workflowMetricsSLADefinition.setCalendarKey(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setDescription(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setDuration(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setName(RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setPauseNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setProcessId(RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setProcessVersion(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setStartNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setStopNodeKeys(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setVersion(RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setStatus(RandomTestUtil.nextInt());

		workflowMetricsSLADefinition.setStatusByUserId(
			RandomTestUtil.nextLong());

		workflowMetricsSLADefinition.setStatusByUserName(
			RandomTestUtil.randomString());

		workflowMetricsSLADefinition.setStatusDate(RandomTestUtil.nextDate());

		_workflowMetricsSLADefinitions.add(
			_persistence.update(workflowMetricsSLADefinition));

		return workflowMetricsSLADefinition;
	}

	private List<WorkflowMetricsSLADefinition> _workflowMetricsSLADefinitions =
		new ArrayList<WorkflowMetricsSLADefinition>();
	private WorkflowMetricsSLADefinitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}