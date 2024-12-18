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
import com.liferay.portal.kernel.exception.DuplicateWorkflowDefinitionLinkExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchWorkflowDefinitionLinkException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkPersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowDefinitionLinkUtil;
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
public class WorkflowDefinitionLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = WorkflowDefinitionLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<WorkflowDefinitionLink> iterator =
			_workflowDefinitionLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowDefinitionLink workflowDefinitionLink = _persistence.create(pk);

		Assert.assertNotNull(workflowDefinitionLink);

		Assert.assertEquals(workflowDefinitionLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		_persistence.remove(newWorkflowDefinitionLink);

		WorkflowDefinitionLink existingWorkflowDefinitionLink =
			_persistence.fetchByPrimaryKey(
				newWorkflowDefinitionLink.getPrimaryKey());

		Assert.assertNull(existingWorkflowDefinitionLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addWorkflowDefinitionLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowDefinitionLink newWorkflowDefinitionLink = _persistence.create(
			pk);

		newWorkflowDefinitionLink.setMvccVersion(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setCtCollectionId(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setUuid(RandomTestUtil.randomString());

		newWorkflowDefinitionLink.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newWorkflowDefinitionLink.setGroupId(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setCompanyId(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setUserId(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setUserName(RandomTestUtil.randomString());

		newWorkflowDefinitionLink.setCreateDate(RandomTestUtil.nextDate());

		newWorkflowDefinitionLink.setModifiedDate(RandomTestUtil.nextDate());

		newWorkflowDefinitionLink.setClassNameId(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setClassPK(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setTypePK(RandomTestUtil.nextLong());

		newWorkflowDefinitionLink.setWorkflowDefinitionName(
			RandomTestUtil.randomString());

		newWorkflowDefinitionLink.setWorkflowDefinitionVersion(
			RandomTestUtil.nextInt());

		_workflowDefinitionLinks.add(
			_persistence.update(newWorkflowDefinitionLink));

		WorkflowDefinitionLink existingWorkflowDefinitionLink =
			_persistence.findByPrimaryKey(
				newWorkflowDefinitionLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowDefinitionLink.getMvccVersion(),
			newWorkflowDefinitionLink.getMvccVersion());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getCtCollectionId(),
			newWorkflowDefinitionLink.getCtCollectionId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getUuid(),
			newWorkflowDefinitionLink.getUuid());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getExternalReferenceCode(),
			newWorkflowDefinitionLink.getExternalReferenceCode());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getWorkflowDefinitionLinkId(),
			newWorkflowDefinitionLink.getWorkflowDefinitionLinkId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getGroupId(),
			newWorkflowDefinitionLink.getGroupId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getCompanyId(),
			newWorkflowDefinitionLink.getCompanyId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getUserId(),
			newWorkflowDefinitionLink.getUserId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getUserName(),
			newWorkflowDefinitionLink.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowDefinitionLink.getCreateDate()),
			Time.getShortTimestamp(newWorkflowDefinitionLink.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingWorkflowDefinitionLink.getModifiedDate()),
			Time.getShortTimestamp(
				newWorkflowDefinitionLink.getModifiedDate()));
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getClassNameId(),
			newWorkflowDefinitionLink.getClassNameId());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getClassPK(),
			newWorkflowDefinitionLink.getClassPK());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getTypePK(),
			newWorkflowDefinitionLink.getTypePK());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getWorkflowDefinitionName(),
			newWorkflowDefinitionLink.getWorkflowDefinitionName());
		Assert.assertEquals(
			existingWorkflowDefinitionLink.getWorkflowDefinitionVersion(),
			newWorkflowDefinitionLink.getWorkflowDefinitionVersion());
	}

	@Test(
		expected = DuplicateWorkflowDefinitionLinkExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		WorkflowDefinitionLink workflowDefinitionLink =
			addWorkflowDefinitionLink();

		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		newWorkflowDefinitionLink.setGroupId(
			workflowDefinitionLink.getGroupId());

		newWorkflowDefinitionLink = _persistence.update(
			newWorkflowDefinitionLink);

		Session session = _persistence.getCurrentSession();

		session.evict(newWorkflowDefinitionLink);

		newWorkflowDefinitionLink.setExternalReferenceCode(
			workflowDefinitionLink.getExternalReferenceCode());

		_persistence.update(newWorkflowDefinitionLink);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_CPK() throws Exception {
		_persistence.countByG_C_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_CPK(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_W_W() throws Exception {
		_persistence.countByC_W_W(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByC_W_W(0L, "null", 0);

		_persistence.countByC_W_W(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_C_C_C() throws Exception {
		_persistence.countByG_C_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C_C_C(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByG_C_C_W() throws Exception {
		_persistence.countByG_C_C_W(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_C_W(0L, 0L, 0L, "null");

		_persistence.countByG_C_C_W(0L, 0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_C_C_T() throws Exception {
		_persistence.countByG_C_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C_C_T(0L, 0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		WorkflowDefinitionLink existingWorkflowDefinitionLink =
			_persistence.findByPrimaryKey(
				newWorkflowDefinitionLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowDefinitionLink, newWorkflowDefinitionLink);
	}

	@Test(expected = NoSuchWorkflowDefinitionLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<WorkflowDefinitionLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"WorkflowDefinitionLink", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"workflowDefinitionLinkId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"typePK", true, "workflowDefinitionName", true,
			"workflowDefinitionVersion", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		WorkflowDefinitionLink existingWorkflowDefinitionLink =
			_persistence.fetchByPrimaryKey(
				newWorkflowDefinitionLink.getPrimaryKey());

		Assert.assertEquals(
			existingWorkflowDefinitionLink, newWorkflowDefinitionLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		WorkflowDefinitionLink missingWorkflowDefinitionLink =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingWorkflowDefinitionLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		WorkflowDefinitionLink newWorkflowDefinitionLink1 =
			addWorkflowDefinitionLink();
		WorkflowDefinitionLink newWorkflowDefinitionLink2 =
			addWorkflowDefinitionLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowDefinitionLink1.getPrimaryKey());
		primaryKeys.add(newWorkflowDefinitionLink2.getPrimaryKey());

		Map<Serializable, WorkflowDefinitionLink> workflowDefinitionLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, workflowDefinitionLinks.size());
		Assert.assertEquals(
			newWorkflowDefinitionLink1,
			workflowDefinitionLinks.get(
				newWorkflowDefinitionLink1.getPrimaryKey()));
		Assert.assertEquals(
			newWorkflowDefinitionLink2,
			workflowDefinitionLinks.get(
				newWorkflowDefinitionLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, WorkflowDefinitionLink> workflowDefinitionLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowDefinitionLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowDefinitionLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, WorkflowDefinitionLink> workflowDefinitionLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowDefinitionLinks.size());
		Assert.assertEquals(
			newWorkflowDefinitionLink,
			workflowDefinitionLinks.get(
				newWorkflowDefinitionLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, WorkflowDefinitionLink> workflowDefinitionLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(workflowDefinitionLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newWorkflowDefinitionLink.getPrimaryKey());

		Map<Serializable, WorkflowDefinitionLink> workflowDefinitionLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, workflowDefinitionLinks.size());
		Assert.assertEquals(
			newWorkflowDefinitionLink,
			workflowDefinitionLinks.get(
				newWorkflowDefinitionLink.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			WorkflowDefinitionLinkLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<WorkflowDefinitionLink>() {

				@Override
				public void performAction(
					WorkflowDefinitionLink workflowDefinitionLink) {

					Assert.assertNotNull(workflowDefinitionLink);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowDefinitionLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowDefinitionLinkId",
				newWorkflowDefinitionLink.getWorkflowDefinitionLinkId()));

		List<WorkflowDefinitionLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		WorkflowDefinitionLink existingWorkflowDefinitionLink = result.get(0);

		Assert.assertEquals(
			existingWorkflowDefinitionLink, newWorkflowDefinitionLink);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowDefinitionLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowDefinitionLinkId", RandomTestUtil.nextLong()));

		List<WorkflowDefinitionLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowDefinitionLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("workflowDefinitionLinkId"));

		Object newWorkflowDefinitionLinkId =
			newWorkflowDefinitionLink.getWorkflowDefinitionLinkId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"workflowDefinitionLinkId",
				new Object[] {newWorkflowDefinitionLinkId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingWorkflowDefinitionLinkId = result.get(0);

		Assert.assertEquals(
			existingWorkflowDefinitionLinkId, newWorkflowDefinitionLinkId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowDefinitionLink.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("workflowDefinitionLinkId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"workflowDefinitionLinkId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newWorkflowDefinitionLink.getPrimaryKey()));
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

		WorkflowDefinitionLink newWorkflowDefinitionLink =
			addWorkflowDefinitionLink();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			WorkflowDefinitionLink.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"workflowDefinitionLinkId",
				newWorkflowDefinitionLink.getWorkflowDefinitionLinkId()));

		List<WorkflowDefinitionLink> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		WorkflowDefinitionLink workflowDefinitionLink) {

		Assert.assertEquals(
			workflowDefinitionLink.getUuid(),
			ReflectionTestUtil.invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			workflowDefinitionLink.getWorkflowDefinitionName(),
			ReflectionTestUtil.invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "workflowDefinitionName"));

		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getTypePK()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "typePK"));

		Assert.assertEquals(
			workflowDefinitionLink.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(workflowDefinitionLink.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				workflowDefinitionLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected WorkflowDefinitionLink addWorkflowDefinitionLink()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		WorkflowDefinitionLink workflowDefinitionLink = _persistence.create(pk);

		workflowDefinitionLink.setMvccVersion(RandomTestUtil.nextLong());

		workflowDefinitionLink.setCtCollectionId(RandomTestUtil.nextLong());

		workflowDefinitionLink.setUuid(RandomTestUtil.randomString());

		workflowDefinitionLink.setExternalReferenceCode(
			RandomTestUtil.randomString());

		workflowDefinitionLink.setGroupId(RandomTestUtil.nextLong());

		workflowDefinitionLink.setCompanyId(RandomTestUtil.nextLong());

		workflowDefinitionLink.setUserId(RandomTestUtil.nextLong());

		workflowDefinitionLink.setUserName(RandomTestUtil.randomString());

		workflowDefinitionLink.setCreateDate(RandomTestUtil.nextDate());

		workflowDefinitionLink.setModifiedDate(RandomTestUtil.nextDate());

		workflowDefinitionLink.setClassNameId(RandomTestUtil.nextLong());

		workflowDefinitionLink.setClassPK(RandomTestUtil.nextLong());

		workflowDefinitionLink.setTypePK(RandomTestUtil.nextLong());

		workflowDefinitionLink.setWorkflowDefinitionName(
			RandomTestUtil.randomString());

		workflowDefinitionLink.setWorkflowDefinitionVersion(
			RandomTestUtil.nextInt());

		_workflowDefinitionLinks.add(
			_persistence.update(workflowDefinitionLink));

		return workflowDefinitionLink;
	}

	private List<WorkflowDefinitionLink> _workflowDefinitionLinks =
		new ArrayList<WorkflowDefinitionLink>();
	private WorkflowDefinitionLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}