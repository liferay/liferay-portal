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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskAssignmentException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskAssignmentUtil;

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
public class KaleoTaskAssignmentPersistenceTest {

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
		_persistence = KaleoTaskAssignmentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoTaskAssignment> iterator =
			_kaleoTaskAssignments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskAssignment kaleoTaskAssignment = _persistence.create(pk);

		Assert.assertNotNull(kaleoTaskAssignment);

		Assert.assertEquals(kaleoTaskAssignment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoTaskAssignment newKaleoTaskAssignment = addKaleoTaskAssignment();

		_persistence.remove(newKaleoTaskAssignment);

		KaleoTaskAssignment existingKaleoTaskAssignment =
			_persistence.fetchByPrimaryKey(
				newKaleoTaskAssignment.getPrimaryKey());

		Assert.assertNull(existingKaleoTaskAssignment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoTaskAssignment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskAssignment newKaleoTaskAssignment = _persistence.create(pk);

		newKaleoTaskAssignment.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setGroupId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setCompanyId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setUserId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setUserName(RandomTestUtil.randomString());

		newKaleoTaskAssignment.setCreateDate(RandomTestUtil.nextDate());

		newKaleoTaskAssignment.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoTaskAssignment.setKaleoClassName(RandomTestUtil.randomString());

		newKaleoTaskAssignment.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setAssigneeClassName(
			RandomTestUtil.randomString());

		newKaleoTaskAssignment.setAssigneeClassPK(RandomTestUtil.nextLong());

		newKaleoTaskAssignment.setAssigneeActionId(
			RandomTestUtil.randomString());

		newKaleoTaskAssignment.setAssigneeScript(RandomTestUtil.randomString());

		newKaleoTaskAssignment.setAssigneeScriptLanguage(
			RandomTestUtil.randomString());

		newKaleoTaskAssignment.setAssigneeScriptRequiredContexts(
			RandomTestUtil.randomString());

		_kaleoTaskAssignments.add(_persistence.update(newKaleoTaskAssignment));

		KaleoTaskAssignment existingKaleoTaskAssignment =
			_persistence.findByPrimaryKey(
				newKaleoTaskAssignment.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskAssignment.getMvccVersion(),
			newKaleoTaskAssignment.getMvccVersion());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getCtCollectionId(),
			newKaleoTaskAssignment.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoTaskAssignmentId(),
			newKaleoTaskAssignment.getKaleoTaskAssignmentId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getGroupId(),
			newKaleoTaskAssignment.getGroupId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getCompanyId(),
			newKaleoTaskAssignment.getCompanyId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getUserId(),
			newKaleoTaskAssignment.getUserId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getUserName(),
			newKaleoTaskAssignment.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoTaskAssignment.getCreateDate()),
			Time.getShortTimestamp(newKaleoTaskAssignment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTaskAssignment.getModifiedDate()),
			Time.getShortTimestamp(newKaleoTaskAssignment.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoClassName(),
			newKaleoTaskAssignment.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoClassPK(),
			newKaleoTaskAssignment.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoDefinitionId(),
			newKaleoTaskAssignment.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoDefinitionVersionId(),
			newKaleoTaskAssignment.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getKaleoNodeId(),
			newKaleoTaskAssignment.getKaleoNodeId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeClassName(),
			newKaleoTaskAssignment.getAssigneeClassName());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeClassPK(),
			newKaleoTaskAssignment.getAssigneeClassPK());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeActionId(),
			newKaleoTaskAssignment.getAssigneeActionId());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeScript(),
			newKaleoTaskAssignment.getAssigneeScript());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeScriptLanguage(),
			newKaleoTaskAssignment.getAssigneeScriptLanguage());
		Assert.assertEquals(
			existingKaleoTaskAssignment.getAssigneeScriptRequiredContexts(),
			newKaleoTaskAssignment.getAssigneeScriptRequiredContexts());
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
	public void testCountByKCN_KCPK() throws Exception {
		_persistence.countByKCN_KCPK("", RandomTestUtil.nextLong());

		_persistence.countByKCN_KCPK("null", 0L);

		_persistence.countByKCN_KCPK((String)null, 0L);
	}

	@Test
	public void testCountByKCN_KCPK_ACN() throws Exception {
		_persistence.countByKCN_KCPK_ACN("", RandomTestUtil.nextLong(), "");

		_persistence.countByKCN_KCPK_ACN("null", 0L, "null");

		_persistence.countByKCN_KCPK_ACN((String)null, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoTaskAssignment newKaleoTaskAssignment = addKaleoTaskAssignment();

		KaleoTaskAssignment existingKaleoTaskAssignment =
			_persistence.findByPrimaryKey(
				newKaleoTaskAssignment.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskAssignment, newKaleoTaskAssignment);
	}

	@Test(expected = NoSuchTaskAssignmentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoTaskAssignment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoTaskAssignment", "mvccVersion", true, "ctCollectionId", true,
			"kaleoTaskAssignmentId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoClassName", true, "kaleoClassPK", true,
			"kaleoDefinitionId", true, "kaleoDefinitionVersionId", true,
			"kaleoNodeId", true, "assigneeClassName", true, "assigneeClassPK",
			true, "assigneeActionId", true, "assigneeScriptLanguage", true,
			"assigneeScriptRequiredContexts", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoTaskAssignment newKaleoTaskAssignment = addKaleoTaskAssignment();

		KaleoTaskAssignment existingKaleoTaskAssignment =
			_persistence.fetchByPrimaryKey(
				newKaleoTaskAssignment.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskAssignment, newKaleoTaskAssignment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskAssignment missingKaleoTaskAssignment =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoTaskAssignment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoTaskAssignment newKaleoTaskAssignment1 = addKaleoTaskAssignment();
		KaleoTaskAssignment newKaleoTaskAssignment2 = addKaleoTaskAssignment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskAssignment1.getPrimaryKey());
		primaryKeys.add(newKaleoTaskAssignment2.getPrimaryKey());

		Map<Serializable, KaleoTaskAssignment> kaleoTaskAssignments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoTaskAssignments.size());
		Assert.assertEquals(
			newKaleoTaskAssignment1,
			kaleoTaskAssignments.get(newKaleoTaskAssignment1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoTaskAssignment2,
			kaleoTaskAssignments.get(newKaleoTaskAssignment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoTaskAssignment> kaleoTaskAssignments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTaskAssignments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoTaskAssignment newKaleoTaskAssignment = addKaleoTaskAssignment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskAssignment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoTaskAssignment> kaleoTaskAssignments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTaskAssignments.size());
		Assert.assertEquals(
			newKaleoTaskAssignment,
			kaleoTaskAssignments.get(newKaleoTaskAssignment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoTaskAssignment> kaleoTaskAssignments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTaskAssignments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoTaskAssignment newKaleoTaskAssignment = addKaleoTaskAssignment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskAssignment.getPrimaryKey());

		Map<Serializable, KaleoTaskAssignment> kaleoTaskAssignments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTaskAssignments.size());
		Assert.assertEquals(
			newKaleoTaskAssignment,
			kaleoTaskAssignments.get(newKaleoTaskAssignment.getPrimaryKey()));
	}

	protected KaleoTaskAssignment addKaleoTaskAssignment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskAssignment kaleoTaskAssignment = _persistence.create(pk);

		kaleoTaskAssignment.setMvccVersion(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setGroupId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setCompanyId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setUserId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setUserName(RandomTestUtil.randomString());

		kaleoTaskAssignment.setCreateDate(RandomTestUtil.nextDate());

		kaleoTaskAssignment.setModifiedDate(RandomTestUtil.nextDate());

		kaleoTaskAssignment.setKaleoClassName(RandomTestUtil.randomString());

		kaleoTaskAssignment.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoTaskAssignment.setKaleoNodeId(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setAssigneeClassName(RandomTestUtil.randomString());

		kaleoTaskAssignment.setAssigneeClassPK(RandomTestUtil.nextLong());

		kaleoTaskAssignment.setAssigneeActionId(RandomTestUtil.randomString());

		kaleoTaskAssignment.setAssigneeScript(RandomTestUtil.randomString());

		kaleoTaskAssignment.setAssigneeScriptLanguage(
			RandomTestUtil.randomString());

		kaleoTaskAssignment.setAssigneeScriptRequiredContexts(
			RandomTestUtil.randomString());

		_kaleoTaskAssignments.add(_persistence.update(kaleoTaskAssignment));

		return kaleoTaskAssignment;
	}

	private List<KaleoTaskAssignment> _kaleoTaskAssignments =
		new ArrayList<KaleoTaskAssignment>();
	private KaleoTaskAssignmentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}