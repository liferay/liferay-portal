/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

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
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTaskFormInstanceException;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskFormInstance;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskFormInstanceLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormInstancePersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskFormInstanceUtil;

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
public class KaleoTaskFormInstancePersistenceTest {

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
		_persistence = KaleoTaskFormInstanceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoTaskFormInstance> iterator =
			_kaleoTaskFormInstances.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskFormInstance kaleoTaskFormInstance = _persistence.create(pk);

		Assert.assertNotNull(kaleoTaskFormInstance);

		Assert.assertEquals(kaleoTaskFormInstance.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		_persistence.remove(newKaleoTaskFormInstance);

		KaleoTaskFormInstance existingKaleoTaskFormInstance =
			_persistence.fetchByPrimaryKey(
				newKaleoTaskFormInstance.getPrimaryKey());

		Assert.assertNull(existingKaleoTaskFormInstance);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoTaskFormInstance();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		newKaleoTaskFormInstance.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setGroupId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setCompanyId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setUserId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setUserName(RandomTestUtil.randomString());

		newKaleoTaskFormInstance.setCreateDate(RandomTestUtil.nextDate());

		newKaleoTaskFormInstance.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoTaskFormInstance.setKaleoDefinitionId(
			RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setKaleoInstanceId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setKaleoTaskId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setKaleoTaskInstanceTokenId(
			RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setKaleoTaskFormId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setFormValues(RandomTestUtil.randomString());

		newKaleoTaskFormInstance.setFormValueEntryGroupId(
			RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setFormValueEntryId(RandomTestUtil.nextLong());

		newKaleoTaskFormInstance.setFormValueEntryUuid(
			RandomTestUtil.randomString());

		newKaleoTaskFormInstance.setMetadata(RandomTestUtil.randomString());

		newKaleoTaskFormInstance = _persistence.update(
			newKaleoTaskFormInstance);

		_kaleoTaskFormInstances.add(newKaleoTaskFormInstance);

		KaleoTaskFormInstance existingKaleoTaskFormInstance =
			_persistence.findByPrimaryKey(
				newKaleoTaskFormInstance.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskFormInstance.getMvccVersion(),
			newKaleoTaskFormInstance.getMvccVersion());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getCtCollectionId(),
			newKaleoTaskFormInstance.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoTaskFormInstanceId(),
			newKaleoTaskFormInstance.getKaleoTaskFormInstanceId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getGroupId(),
			newKaleoTaskFormInstance.getGroupId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getCompanyId(),
			newKaleoTaskFormInstance.getCompanyId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getUserId(),
			newKaleoTaskFormInstance.getUserId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getUserName(),
			newKaleoTaskFormInstance.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTaskFormInstance.getCreateDate()),
			Time.getShortTimestamp(newKaleoTaskFormInstance.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingKaleoTaskFormInstance.getModifiedDate()),
			Time.getShortTimestamp(newKaleoTaskFormInstance.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoDefinitionId(),
			newKaleoTaskFormInstance.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoDefinitionVersionId(),
			newKaleoTaskFormInstance.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoInstanceId(),
			newKaleoTaskFormInstance.getKaleoInstanceId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoTaskId(),
			newKaleoTaskFormInstance.getKaleoTaskId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoTaskInstanceTokenId(),
			newKaleoTaskFormInstance.getKaleoTaskInstanceTokenId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getKaleoTaskFormId(),
			newKaleoTaskFormInstance.getKaleoTaskFormId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getFormValues(),
			newKaleoTaskFormInstance.getFormValues());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getFormValueEntryGroupId(),
			newKaleoTaskFormInstance.getFormValueEntryGroupId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getFormValueEntryId(),
			newKaleoTaskFormInstance.getFormValueEntryId());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getFormValueEntryUuid(),
			newKaleoTaskFormInstance.getFormValueEntryUuid());
		Assert.assertEquals(
			existingKaleoTaskFormInstance.getMetadata(),
			newKaleoTaskFormInstance.getMetadata());
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
	public void testCountByKaleoTaskId() throws Exception {
		_persistence.countByKaleoTaskId(RandomTestUtil.nextLong());

		_persistence.countByKaleoTaskId(0L);
	}

	@Test
	public void testCountByKaleoTaskInstanceTokenId() throws Exception {
		_persistence.countByKaleoTaskInstanceTokenId(RandomTestUtil.nextLong());

		_persistence.countByKaleoTaskInstanceTokenId(0L);
	}

	@Test
	public void testCountByKaleoTaskFormId() throws Exception {
		_persistence.countByKaleoTaskFormId(RandomTestUtil.nextLong());

		_persistence.countByKaleoTaskFormId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		KaleoTaskFormInstance existingKaleoTaskFormInstance =
			_persistence.findByPrimaryKey(
				newKaleoTaskFormInstance.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskFormInstance, newKaleoTaskFormInstance);
	}

	@Test(expected = NoSuchTaskFormInstanceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoTaskFormInstance> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoTaskFormInstance", "mvccVersion", true, "ctCollectionId",
			true, "kaleoTaskFormInstanceId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoDefinitionId", true,
			"kaleoDefinitionVersionId", true, "kaleoInstanceId", true,
			"kaleoTaskId", true, "kaleoTaskInstanceTokenId", true,
			"kaleoTaskFormId", true, "formValues", true,
			"formValueEntryGroupId", true, "formValueEntryId", true,
			"formValueEntryUuid", true, "metadata", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		KaleoTaskFormInstance existingKaleoTaskFormInstance =
			_persistence.fetchByPrimaryKey(
				newKaleoTaskFormInstance.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTaskFormInstance, newKaleoTaskFormInstance);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTaskFormInstance missingKaleoTaskFormInstance =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoTaskFormInstance);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoTaskFormInstance newKaleoTaskFormInstance1 =
			addKaleoTaskFormInstance();
		KaleoTaskFormInstance newKaleoTaskFormInstance2 =
			addKaleoTaskFormInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskFormInstance1.getPrimaryKey());
		primaryKeys.add(newKaleoTaskFormInstance2.getPrimaryKey());

		Map<Serializable, KaleoTaskFormInstance> kaleoTaskFormInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoTaskFormInstances.size());
		Assert.assertEquals(
			newKaleoTaskFormInstance1,
			kaleoTaskFormInstances.get(
				newKaleoTaskFormInstance1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoTaskFormInstance2,
			kaleoTaskFormInstances.get(
				newKaleoTaskFormInstance2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoTaskFormInstance> kaleoTaskFormInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTaskFormInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskFormInstance.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoTaskFormInstance> kaleoTaskFormInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTaskFormInstances.size());
		Assert.assertEquals(
			newKaleoTaskFormInstance,
			kaleoTaskFormInstances.get(
				newKaleoTaskFormInstance.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoTaskFormInstance> kaleoTaskFormInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTaskFormInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTaskFormInstance.getPrimaryKey());

		Map<Serializable, KaleoTaskFormInstance> kaleoTaskFormInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTaskFormInstances.size());
		Assert.assertEquals(
			newKaleoTaskFormInstance,
			kaleoTaskFormInstances.get(
				newKaleoTaskFormInstance.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			KaleoTaskFormInstanceLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<KaleoTaskFormInstance>() {

				@Override
				public void performAction(
					KaleoTaskFormInstance kaleoTaskFormInstance) {

					Assert.assertNotNull(kaleoTaskFormInstance);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskFormInstance.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoTaskFormInstanceId",
				newKaleoTaskFormInstance.getKaleoTaskFormInstanceId()));

		List<KaleoTaskFormInstance> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		KaleoTaskFormInstance existingKaleoTaskFormInstance = result.get(0);

		Assert.assertEquals(
			existingKaleoTaskFormInstance, newKaleoTaskFormInstance);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskFormInstance.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoTaskFormInstanceId", RandomTestUtil.nextLong()));

		List<KaleoTaskFormInstance> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskFormInstance.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoTaskFormInstanceId"));

		Object newKaleoTaskFormInstanceId =
			newKaleoTaskFormInstance.getKaleoTaskFormInstanceId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoTaskFormInstanceId",
				new Object[] {newKaleoTaskFormInstanceId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingKaleoTaskFormInstanceId = result.get(0);

		Assert.assertEquals(
			existingKaleoTaskFormInstanceId, newKaleoTaskFormInstanceId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskFormInstance.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoTaskFormInstanceId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoTaskFormInstanceId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newKaleoTaskFormInstance.getPrimaryKey()));
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

		KaleoTaskFormInstance newKaleoTaskFormInstance =
			addKaleoTaskFormInstance();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoTaskFormInstance.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoTaskFormInstanceId",
				newKaleoTaskFormInstance.getKaleoTaskFormInstanceId()));

		List<KaleoTaskFormInstance> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		KaleoTaskFormInstance kaleoTaskFormInstance) {

		Assert.assertEquals(
			Long.valueOf(kaleoTaskFormInstance.getKaleoTaskFormId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoTaskFormInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoTaskFormId"));
	}

	protected KaleoTaskFormInstance addKaleoTaskFormInstance()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		KaleoTaskFormInstance kaleoTaskFormInstance = _persistence.create(pk);

		kaleoTaskFormInstance.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setGroupId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setCompanyId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setUserId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setUserName(RandomTestUtil.randomString());

		kaleoTaskFormInstance.setCreateDate(RandomTestUtil.nextDate());

		kaleoTaskFormInstance.setModifiedDate(RandomTestUtil.nextDate());

		kaleoTaskFormInstance.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setKaleoInstanceId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setKaleoTaskId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setKaleoTaskInstanceTokenId(
			RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setKaleoTaskFormId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setFormValues(RandomTestUtil.randomString());

		kaleoTaskFormInstance.setFormValueEntryGroupId(
			RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setFormValueEntryId(RandomTestUtil.nextLong());

		kaleoTaskFormInstance.setFormValueEntryUuid(
			RandomTestUtil.randomString());

		kaleoTaskFormInstance.setMetadata(RandomTestUtil.randomString());

		_kaleoTaskFormInstances.add(_persistence.update(kaleoTaskFormInstance));

		return kaleoTaskFormInstance;
	}

	private List<KaleoTaskFormInstance> _kaleoTaskFormInstances =
		new ArrayList<KaleoTaskFormInstance>();
	private KaleoTaskFormInstancePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1462258404