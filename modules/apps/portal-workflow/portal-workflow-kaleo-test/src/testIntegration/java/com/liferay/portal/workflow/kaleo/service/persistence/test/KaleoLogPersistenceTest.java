/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.workflow.kaleo.exception.NoSuchLogException;
import com.liferay.portal.workflow.kaleo.model.KaleoLog;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoLogPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoLogUtil;

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
public class KaleoLogPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.kaleo.service"));

	@Before
	public void setUp() {
		_persistence = KaleoLogUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoLog> iterator = _kaleoLogs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoLog kaleoLog = _persistence.create(pk);

		Assert.assertNotNull(kaleoLog);

		Assert.assertEquals(kaleoLog.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoLog newKaleoLog = addKaleoLog();

		_persistence.remove(newKaleoLog);

		KaleoLog existingKaleoLog = _persistence.fetchByPrimaryKey(
			newKaleoLog.getPrimaryKey());

		Assert.assertNull(existingKaleoLog);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoLog();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoLog newKaleoLog = _persistence.create(pk);

		newKaleoLog.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoLog.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoLog.setGroupId(RandomTestUtil.nextLong());

		newKaleoLog.setCompanyId(RandomTestUtil.nextLong());

		newKaleoLog.setUserId(RandomTestUtil.nextLong());

		newKaleoLog.setUserName(RandomTestUtil.randomString());

		newKaleoLog.setCreateDate(RandomTestUtil.nextDate());

		newKaleoLog.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoLog.setKaleoClassName(RandomTestUtil.randomString());

		newKaleoLog.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoInstanceId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoInstanceTokenId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoTaskInstanceTokenId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoNodeName(RandomTestUtil.randomString());

		newKaleoLog.setTerminalKaleoNode(RandomTestUtil.randomBoolean());

		newKaleoLog.setKaleoActionId(RandomTestUtil.nextLong());

		newKaleoLog.setKaleoActionName(RandomTestUtil.randomString());

		newKaleoLog.setKaleoActionDescription(RandomTestUtil.randomString());

		newKaleoLog.setPreviousKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoLog.setPreviousKaleoNodeName(RandomTestUtil.randomString());

		newKaleoLog.setPreviousAssigneeClassName(RandomTestUtil.randomString());

		newKaleoLog.setPreviousAssigneeClassPK(RandomTestUtil.nextLong());

		newKaleoLog.setCurrentAssigneeClassName(RandomTestUtil.randomString());

		newKaleoLog.setCurrentAssigneeClassPK(RandomTestUtil.nextLong());

		newKaleoLog.setType(RandomTestUtil.randomString());

		newKaleoLog.setComment(RandomTestUtil.randomString());

		newKaleoLog.setStartDate(RandomTestUtil.nextDate());

		newKaleoLog.setEndDate(RandomTestUtil.nextDate());

		newKaleoLog.setDuration(RandomTestUtil.nextLong());

		newKaleoLog.setWorkflowContext(RandomTestUtil.randomString());

		_kaleoLogs.add(_persistence.update(newKaleoLog));

		KaleoLog existingKaleoLog = _persistence.findByPrimaryKey(
			newKaleoLog.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoLog.getMvccVersion(), newKaleoLog.getMvccVersion());
		Assert.assertEquals(
			existingKaleoLog.getCtCollectionId(),
			newKaleoLog.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoLogId(), newKaleoLog.getKaleoLogId());
		Assert.assertEquals(
			existingKaleoLog.getGroupId(), newKaleoLog.getGroupId());
		Assert.assertEquals(
			existingKaleoLog.getCompanyId(), newKaleoLog.getCompanyId());
		Assert.assertEquals(
			existingKaleoLog.getUserId(), newKaleoLog.getUserId());
		Assert.assertEquals(
			existingKaleoLog.getUserName(), newKaleoLog.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoLog.getCreateDate()),
			Time.getShortTimestamp(newKaleoLog.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoLog.getModifiedDate()),
			Time.getShortTimestamp(newKaleoLog.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoLog.getKaleoClassName(),
			newKaleoLog.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoLog.getKaleoClassPK(), newKaleoLog.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoLog.getKaleoDefinitionId(),
			newKaleoLog.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoDefinitionVersionId(),
			newKaleoLog.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoInstanceId(),
			newKaleoLog.getKaleoInstanceId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoInstanceTokenId(),
			newKaleoLog.getKaleoInstanceTokenId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoTaskInstanceTokenId(),
			newKaleoLog.getKaleoTaskInstanceTokenId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoNodeName(),
			newKaleoLog.getKaleoNodeName());
		Assert.assertEquals(
			existingKaleoLog.isTerminalKaleoNode(),
			newKaleoLog.isTerminalKaleoNode());
		Assert.assertEquals(
			existingKaleoLog.getKaleoActionId(),
			newKaleoLog.getKaleoActionId());
		Assert.assertEquals(
			existingKaleoLog.getKaleoActionName(),
			newKaleoLog.getKaleoActionName());
		Assert.assertEquals(
			existingKaleoLog.getKaleoActionDescription(),
			newKaleoLog.getKaleoActionDescription());
		Assert.assertEquals(
			existingKaleoLog.getPreviousKaleoNodeId(),
			newKaleoLog.getPreviousKaleoNodeId());
		Assert.assertEquals(
			existingKaleoLog.getPreviousKaleoNodeName(),
			newKaleoLog.getPreviousKaleoNodeName());
		Assert.assertEquals(
			existingKaleoLog.getPreviousAssigneeClassName(),
			newKaleoLog.getPreviousAssigneeClassName());
		Assert.assertEquals(
			existingKaleoLog.getPreviousAssigneeClassPK(),
			newKaleoLog.getPreviousAssigneeClassPK());
		Assert.assertEquals(
			existingKaleoLog.getCurrentAssigneeClassName(),
			newKaleoLog.getCurrentAssigneeClassName());
		Assert.assertEquals(
			existingKaleoLog.getCurrentAssigneeClassPK(),
			newKaleoLog.getCurrentAssigneeClassPK());
		Assert.assertEquals(existingKaleoLog.getType(), newKaleoLog.getType());
		Assert.assertEquals(
			existingKaleoLog.getComment(), newKaleoLog.getComment());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoLog.getStartDate()),
			Time.getShortTimestamp(newKaleoLog.getStartDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoLog.getEndDate()),
			Time.getShortTimestamp(newKaleoLog.getEndDate()));
		Assert.assertEquals(
			existingKaleoLog.getDuration(), newKaleoLog.getDuration());
		Assert.assertEquals(
			existingKaleoLog.getWorkflowContext(),
			newKaleoLog.getWorkflowContext());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByKaleoDefinitionVersionId() throws Exception {
		_persistence.countByKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		_persistence.countByKaleoDefinitionVersionId(0L);
	}

	@Test
	public void testCountByKaleoInstanceId() throws Exception {
		_persistence.countByKaleoInstanceId(RandomTestUtil.nextLong());

		_persistence.countByKaleoInstanceId(0L);
	}

	@Test
	public void testCountByKaleoTaskInstanceTokenId() throws Exception {
		_persistence.countByKaleoTaskInstanceTokenId(RandomTestUtil.nextLong());

		_persistence.countByKaleoTaskInstanceTokenId(0L);
	}

	@Test
	public void testCountByKITI_T() throws Exception {
		_persistence.countByKITI_T(RandomTestUtil.nextLong(), "");

		_persistence.countByKITI_T(0L, "null");

		_persistence.countByKITI_T(0L, (String)null);
	}

	@Test
	public void testCountByKCN_KCPK_KITI_T() throws Exception {
		_persistence.countByKCN_KCPK_KITI_T(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByKCN_KCPK_KITI_T("null", 0L, 0L, "null");

		_persistence.countByKCN_KCPK_KITI_T((String)null, 0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoLog newKaleoLog = addKaleoLog();

		KaleoLog existingKaleoLog = _persistence.findByPrimaryKey(
			newKaleoLog.getPrimaryKey());

		Assert.assertEquals(existingKaleoLog, newKaleoLog);
	}

	@Test(expected = NoSuchLogException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoLog> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoLog", "mvccVersion", true, "ctCollectionId", true,
			"kaleoLogId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"kaleoClassName", true, "kaleoClassPK", true, "kaleoDefinitionId",
			true, "kaleoDefinitionVersionId", true, "kaleoInstanceId", true,
			"kaleoInstanceTokenId", true, "kaleoTaskInstanceTokenId", true,
			"kaleoNodeName", true, "terminalKaleoNode", true, "kaleoActionId",
			true, "kaleoActionName", true, "kaleoActionDescription", true,
			"previousKaleoNodeId", true, "previousKaleoNodeName", true,
			"previousAssigneeClassName", true, "previousAssigneeClassPK", true,
			"currentAssigneeClassName", true, "currentAssigneeClassPK", true,
			"type", true, "startDate", true, "endDate", true, "duration", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoLog newKaleoLog = addKaleoLog();

		KaleoLog existingKaleoLog = _persistence.fetchByPrimaryKey(
			newKaleoLog.getPrimaryKey());

		Assert.assertEquals(existingKaleoLog, newKaleoLog);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoLog missingKaleoLog = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoLog);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoLog newKaleoLog1 = addKaleoLog();
		KaleoLog newKaleoLog2 = addKaleoLog();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoLog1.getPrimaryKey());
		primaryKeys.add(newKaleoLog2.getPrimaryKey());

		Map<Serializable, KaleoLog> kaleoLogs = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, kaleoLogs.size());
		Assert.assertEquals(
			newKaleoLog1, kaleoLogs.get(newKaleoLog1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoLog2, kaleoLogs.get(newKaleoLog2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoLog> kaleoLogs = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(kaleoLogs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoLog newKaleoLog = addKaleoLog();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoLog.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoLog> kaleoLogs = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, kaleoLogs.size());
		Assert.assertEquals(
			newKaleoLog, kaleoLogs.get(newKaleoLog.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoLog> kaleoLogs = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(kaleoLogs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoLog newKaleoLog = addKaleoLog();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoLog.getPrimaryKey());

		Map<Serializable, KaleoLog> kaleoLogs = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, kaleoLogs.size());
		Assert.assertEquals(
			newKaleoLog, kaleoLogs.get(newKaleoLog.getPrimaryKey()));
	}

	protected KaleoLog addKaleoLog() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoLog kaleoLog = _persistence.create(pk);

		kaleoLog.setMvccVersion(RandomTestUtil.nextLong());

		kaleoLog.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoLog.setGroupId(RandomTestUtil.nextLong());

		kaleoLog.setCompanyId(RandomTestUtil.nextLong());

		kaleoLog.setUserId(RandomTestUtil.nextLong());

		kaleoLog.setUserName(RandomTestUtil.randomString());

		kaleoLog.setCreateDate(RandomTestUtil.nextDate());

		kaleoLog.setModifiedDate(RandomTestUtil.nextDate());

		kaleoLog.setKaleoClassName(RandomTestUtil.randomString());

		kaleoLog.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoLog.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoInstanceId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoInstanceTokenId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoTaskInstanceTokenId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoNodeName(RandomTestUtil.randomString());

		kaleoLog.setTerminalKaleoNode(RandomTestUtil.randomBoolean());

		kaleoLog.setKaleoActionId(RandomTestUtil.nextLong());

		kaleoLog.setKaleoActionName(RandomTestUtil.randomString());

		kaleoLog.setKaleoActionDescription(RandomTestUtil.randomString());

		kaleoLog.setPreviousKaleoNodeId(RandomTestUtil.nextLong());

		kaleoLog.setPreviousKaleoNodeName(RandomTestUtil.randomString());

		kaleoLog.setPreviousAssigneeClassName(RandomTestUtil.randomString());

		kaleoLog.setPreviousAssigneeClassPK(RandomTestUtil.nextLong());

		kaleoLog.setCurrentAssigneeClassName(RandomTestUtil.randomString());

		kaleoLog.setCurrentAssigneeClassPK(RandomTestUtil.nextLong());

		kaleoLog.setType(RandomTestUtil.randomString());

		kaleoLog.setComment(RandomTestUtil.randomString());

		kaleoLog.setStartDate(RandomTestUtil.nextDate());

		kaleoLog.setEndDate(RandomTestUtil.nextDate());

		kaleoLog.setDuration(RandomTestUtil.nextLong());

		kaleoLog.setWorkflowContext(RandomTestUtil.randomString());

		_kaleoLogs.add(_persistence.update(kaleoLog));

		return kaleoLog;
	}

	private List<KaleoLog> _kaleoLogs = new ArrayList<KaleoLog>();
	private KaleoLogPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}