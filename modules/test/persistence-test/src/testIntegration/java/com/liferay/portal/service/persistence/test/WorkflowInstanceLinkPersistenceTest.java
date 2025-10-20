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
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchWorkflowInstanceLinkException;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class WorkflowInstanceLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = WorkflowInstanceLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WorkflowInstanceLink> iterator =
			_workflowInstanceLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowInstanceLink workflowInstanceLink = _persistence.create(pk);

		Assert.assertNotNull(workflowInstanceLink);

		Assert.assertEquals(workflowInstanceLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		_persistence.remove(newWorkflowInstanceLink);

		WorkflowInstanceLink existingWorkflowInstanceLink =
			_persistence.fetchByPrimaryKey(
				newWorkflowInstanceLink.getPrimaryKey());

		Assert.assertNull(existingWorkflowInstanceLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWorkflowInstanceLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowInstanceLink newWorkflowInstanceLink = _persistence.create(pk);

		newWorkflowInstanceLink.setMvccVersion(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setCtCollectionId(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setGroupId(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setCompanyId(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setUserId(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setUserName(RandomTestUtil.randomString());

		newWorkflowInstanceLink.setCreateDate(RandomTestUtil.nextDate());

		newWorkflowInstanceLink.setModifiedDate(RandomTestUtil.nextDate());

		newWorkflowInstanceLink.setClassNameId(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setClassPK(RandomTestUtil.nextLong());

		newWorkflowInstanceLink.setWorkflowInstanceId(
			RandomTestUtil.nextLong());

		_workflowInstanceLinks.add(
			_persistence.update(newWorkflowInstanceLink));

		WorkflowInstanceLink existingWorkflowInstanceLink =
			_persistence.findByPrimaryKey(
				newWorkflowInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowInstanceLink.getMvccVersion(),
			newWorkflowInstanceLink.getMvccVersion());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getCtCollectionId(),
			newWorkflowInstanceLink.getCtCollectionId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getWorkflowInstanceLinkId(),
			newWorkflowInstanceLink.getWorkflowInstanceLinkId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getGroupId(),
			newWorkflowInstanceLink.getGroupId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getCompanyId(),
			newWorkflowInstanceLink.getCompanyId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getUserId(),
			newWorkflowInstanceLink.getUserId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getUserName(),
			newWorkflowInstanceLink.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowInstanceLink.getCreateDate()),
			Time.getShortTimestamp(newWorkflowInstanceLink.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowInstanceLink.getModifiedDate()),
			Time.getShortTimestamp(newWorkflowInstanceLink.getModifiedDate()));
		Assert.assertEquals(
			existingWorkflowInstanceLink.getClassNameId(),
			newWorkflowInstanceLink.getClassNameId());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getClassPK(),
			newWorkflowInstanceLink.getClassPK());
		Assert.assertEquals(
			existingWorkflowInstanceLink.getWorkflowInstanceId(),
			newWorkflowInstanceLink.getWorkflowInstanceId());
	}

	@Test
	public void testCountByWorkflowInstanceId() throws Exception {
		_persistence.countByWorkflowInstanceId(RandomTestUtil.nextLong());

		_persistence.countByWorkflowInstanceId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_C_C() throws Exception {
		_persistence.countByG_C_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C_C_C(0L, 0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		WorkflowInstanceLink existingWorkflowInstanceLink =
			_persistence.findByPrimaryKey(
				newWorkflowInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowInstanceLink, newWorkflowInstanceLink);
	}

	@Test(expected = NoSuchWorkflowInstanceLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WorkflowInstanceLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WorkflowInstanceLink", "mvccVersion", true, "ctCollectionId", true,
			"workflowInstanceLinkId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"workflowInstanceId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		WorkflowInstanceLink existingWorkflowInstanceLink =
			_persistence.fetchByPrimaryKey(
				newWorkflowInstanceLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowInstanceLink, newWorkflowInstanceLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowInstanceLink missingWorkflowInstanceLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWorkflowInstanceLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WorkflowInstanceLink newWorkflowInstanceLink1 =
			addWorkflowInstanceLink();
		WorkflowInstanceLink newWorkflowInstanceLink2 =
			addWorkflowInstanceLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowInstanceLink1.getPrimaryKey());
		primaryKeys.add(newWorkflowInstanceLink2.getPrimaryKey());

		Map<Serializable, WorkflowInstanceLink> workflowInstanceLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, workflowInstanceLinks.size());
		Assert.assertEquals(
			newWorkflowInstanceLink1,
			workflowInstanceLinks.get(
				newWorkflowInstanceLink1.getPrimaryKey()));
		Assert.assertEquals(
			newWorkflowInstanceLink2,
			workflowInstanceLinks.get(
				newWorkflowInstanceLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WorkflowInstanceLink> workflowInstanceLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowInstanceLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowInstanceLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WorkflowInstanceLink> workflowInstanceLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowInstanceLinks.size());
		Assert.assertEquals(
			newWorkflowInstanceLink,
			workflowInstanceLinks.get(newWorkflowInstanceLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WorkflowInstanceLink> workflowInstanceLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowInstanceLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowInstanceLink.getPrimaryKey());

		Map<Serializable, WorkflowInstanceLink> workflowInstanceLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowInstanceLinks.size());
		Assert.assertEquals(
			newWorkflowInstanceLink,
			workflowInstanceLinks.get(newWorkflowInstanceLink.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			WorkflowInstanceLinkLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<WorkflowInstanceLink>() {

				@Override
				public void performAction(
					WorkflowInstanceLink workflowInstanceLink) {

					Assert.assertNotNull(workflowInstanceLink);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowInstanceLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowInstanceLinkId",
				newWorkflowInstanceLink.getWorkflowInstanceLinkId()));

		List<WorkflowInstanceLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		WorkflowInstanceLink existingWorkflowInstanceLink = result.get(0);

		Assert.assertEquals(
			existingWorkflowInstanceLink, newWorkflowInstanceLink);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowInstanceLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowInstanceLinkId", RandomTestUtil.nextLong()));

		List<WorkflowInstanceLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowInstanceLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("workflowInstanceLinkId"));

		Object newWorkflowInstanceLinkId =
			newWorkflowInstanceLink.getWorkflowInstanceLinkId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"workflowInstanceLinkId",
				new Object[] {newWorkflowInstanceLinkId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingWorkflowInstanceLinkId = result.get(0);

		Assert.assertEquals(
			existingWorkflowInstanceLinkId, newWorkflowInstanceLinkId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowInstanceLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("workflowInstanceLinkId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"workflowInstanceLinkId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newWorkflowInstanceLink.getPrimaryKey()));
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

		WorkflowInstanceLink newWorkflowInstanceLink =
			addWorkflowInstanceLink();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowInstanceLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowInstanceLinkId",
				newWorkflowInstanceLink.getWorkflowInstanceLinkId()));

		List<WorkflowInstanceLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		WorkflowInstanceLink workflowInstanceLink) {

		Assert.assertEquals(
			Long.valueOf(workflowInstanceLink.getWorkflowInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				workflowInstanceLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "workflowInstanceId"));
	}

	protected WorkflowInstanceLink addWorkflowInstanceLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowInstanceLink workflowInstanceLink = _persistence.create(pk);

		workflowInstanceLink.setMvccVersion(RandomTestUtil.nextLong());

		workflowInstanceLink.setCtCollectionId(RandomTestUtil.nextLong());

		workflowInstanceLink.setGroupId(RandomTestUtil.nextLong());

		workflowInstanceLink.setCompanyId(RandomTestUtil.nextLong());

		workflowInstanceLink.setUserId(RandomTestUtil.nextLong());

		workflowInstanceLink.setUserName(RandomTestUtil.randomString());

		workflowInstanceLink.setCreateDate(RandomTestUtil.nextDate());

		workflowInstanceLink.setModifiedDate(RandomTestUtil.nextDate());

		workflowInstanceLink.setClassNameId(RandomTestUtil.nextLong());

		workflowInstanceLink.setClassPK(RandomTestUtil.nextLong());

		workflowInstanceLink.setWorkflowInstanceId(RandomTestUtil.nextLong());

		_workflowInstanceLinks.add(_persistence.update(workflowInstanceLink));

		return workflowInstanceLink;
	}

	private List<WorkflowInstanceLink> _workflowInstanceLinks =
		new ArrayList<WorkflowInstanceLink>();
	private WorkflowInstanceLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}