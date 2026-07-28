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
import com.liferay.portal.workflow.kaleo.exception.NoSuchNotificationException;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNotificationUtil;

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
public class KaleoNotificationPersistenceTest {

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
		_persistence = KaleoNotificationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoNotification> iterator = _kaleoNotifications.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotification kaleoNotification = _persistence.create(pk);

		Assert.assertNotNull(kaleoNotification);

		Assert.assertEquals(kaleoNotification.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		_persistence.remove(newKaleoNotification);

		KaleoNotification existingKaleoNotification =
			_persistence.fetchByPrimaryKey(
				newKaleoNotification.getPrimaryKey());

		Assert.assertNull(existingKaleoNotification);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoNotification();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		newKaleoNotification.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoNotification.setGroupId(RandomTestUtil.nextLong());

		newKaleoNotification.setCompanyId(RandomTestUtil.nextLong());

		newKaleoNotification.setUserId(RandomTestUtil.nextLong());

		newKaleoNotification.setUserName(RandomTestUtil.randomString());

		newKaleoNotification.setCreateDate(RandomTestUtil.nextDate());

		newKaleoNotification.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoNotification.setKaleoClassName(RandomTestUtil.randomString());

		newKaleoNotification.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoNotification.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoNotification.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoNotification.setKaleoNodeName(RandomTestUtil.randomString());

		newKaleoNotification.setName(RandomTestUtil.randomString());

		newKaleoNotification.setDescription(RandomTestUtil.randomString());

		newKaleoNotification.setExecutionType(RandomTestUtil.randomString());

		newKaleoNotification.setTemplate(RandomTestUtil.randomString());

		newKaleoNotification.setTemplateLanguage(RandomTestUtil.randomString());

		newKaleoNotification.setNotificationTypes(
			RandomTestUtil.randomString());

		newKaleoNotification = _persistence.update(newKaleoNotification);

		_kaleoNotifications.add(newKaleoNotification);

		KaleoNotification existingKaleoNotification =
			_persistence.findByPrimaryKey(newKaleoNotification.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoNotification.getMvccVersion(),
			newKaleoNotification.getMvccVersion());
		Assert.assertEquals(
			existingKaleoNotification.getCtCollectionId(),
			newKaleoNotification.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoNotification.getKaleoNotificationId(),
			newKaleoNotification.getKaleoNotificationId());
		Assert.assertEquals(
			existingKaleoNotification.getGroupId(),
			newKaleoNotification.getGroupId());
		Assert.assertEquals(
			existingKaleoNotification.getCompanyId(),
			newKaleoNotification.getCompanyId());
		Assert.assertEquals(
			existingKaleoNotification.getUserId(),
			newKaleoNotification.getUserId());
		Assert.assertEquals(
			existingKaleoNotification.getUserName(),
			newKaleoNotification.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoNotification.getCreateDate()),
			Time.getShortTimestamp(newKaleoNotification.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoNotification.getModifiedDate()),
			Time.getShortTimestamp(newKaleoNotification.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoNotification.getKaleoClassName(),
			newKaleoNotification.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoNotification.getKaleoClassPK(),
			newKaleoNotification.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoNotification.getKaleoDefinitionId(),
			newKaleoNotification.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoNotification.getKaleoDefinitionVersionId(),
			newKaleoNotification.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoNotification.getKaleoNodeName(),
			newKaleoNotification.getKaleoNodeName());
		Assert.assertEquals(
			existingKaleoNotification.getName(),
			newKaleoNotification.getName());
		Assert.assertEquals(
			existingKaleoNotification.getDescription(),
			newKaleoNotification.getDescription());
		Assert.assertEquals(
			existingKaleoNotification.getExecutionType(),
			newKaleoNotification.getExecutionType());
		Assert.assertEquals(
			existingKaleoNotification.getTemplate(),
			newKaleoNotification.getTemplate());
		Assert.assertEquals(
			existingKaleoNotification.getTemplateLanguage(),
			newKaleoNotification.getTemplateLanguage());
		Assert.assertEquals(
			existingKaleoNotification.getNotificationTypes(),
			newKaleoNotification.getNotificationTypes());
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
	public void testCountByKCN_KDVI() throws Exception {
		_persistence.countByKCN_KDVI("", RandomTestUtil.nextLong());

		_persistence.countByKCN_KDVI("null", 0L);

		_persistence.countByKCN_KDVI((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		KaleoNotification existingKaleoNotification =
			_persistence.findByPrimaryKey(newKaleoNotification.getPrimaryKey());

		Assert.assertEquals(existingKaleoNotification, newKaleoNotification);
	}

	@Test(expected = NoSuchNotificationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoNotification> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoNotification", "mvccVersion", true, "ctCollectionId", true,
			"kaleoNotificationId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoClassName", true, "kaleoClassPK", true,
			"kaleoDefinitionId", true, "kaleoDefinitionVersionId", true,
			"kaleoNodeName", true, "name", true, "description", true,
			"executionType", true, "templateLanguage", true,
			"notificationTypes", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		KaleoNotification existingKaleoNotification =
			_persistence.fetchByPrimaryKey(
				newKaleoNotification.getPrimaryKey());

		Assert.assertEquals(existingKaleoNotification, newKaleoNotification);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotification missingKaleoNotification =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoNotification);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoNotification newKaleoNotification1 = addKaleoNotification();
		KaleoNotification newKaleoNotification2 = addKaleoNotification();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotification1.getPrimaryKey());
		primaryKeys.add(newKaleoNotification2.getPrimaryKey());

		Map<Serializable, KaleoNotification> kaleoNotifications =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoNotifications.size());
		Assert.assertEquals(
			newKaleoNotification1,
			kaleoNotifications.get(newKaleoNotification1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoNotification2,
			kaleoNotifications.get(newKaleoNotification2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoNotification> kaleoNotifications =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoNotifications.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoNotification newKaleoNotification = addKaleoNotification();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotification.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoNotification> kaleoNotifications =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoNotifications.size());
		Assert.assertEquals(
			newKaleoNotification,
			kaleoNotifications.get(newKaleoNotification.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoNotification> kaleoNotifications =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoNotifications.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNotification.getPrimaryKey());

		Map<Serializable, KaleoNotification> kaleoNotifications =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoNotifications.size());
		Assert.assertEquals(
			newKaleoNotification,
			kaleoNotifications.get(newKaleoNotification.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			KaleoNotificationLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<KaleoNotification>() {

				@Override
				public void performAction(KaleoNotification kaleoNotification) {
					Assert.assertNotNull(kaleoNotification);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNotification.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoNotificationId",
				newKaleoNotification.getKaleoNotificationId()));

		List<KaleoNotification> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		KaleoNotification existingKaleoNotification = result.get(0);

		Assert.assertEquals(existingKaleoNotification, newKaleoNotification);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNotification.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoNotificationId", RandomTestUtil.nextLong()));

		List<KaleoNotification> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		KaleoNotification newKaleoNotification = addKaleoNotification();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNotification.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoNotificationId"));

		Object newKaleoNotificationId =
			newKaleoNotification.getKaleoNotificationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoNotificationId", new Object[] {newKaleoNotificationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingKaleoNotificationId = result.get(0);

		Assert.assertEquals(
			existingKaleoNotificationId, newKaleoNotificationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNotification.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoNotificationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoNotificationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected KaleoNotification addKaleoNotification() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNotification kaleoNotification = _persistence.create(pk);

		kaleoNotification.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoNotification.setGroupId(RandomTestUtil.nextLong());

		kaleoNotification.setCompanyId(RandomTestUtil.nextLong());

		kaleoNotification.setUserId(RandomTestUtil.nextLong());

		kaleoNotification.setUserName(RandomTestUtil.randomString());

		kaleoNotification.setCreateDate(RandomTestUtil.nextDate());

		kaleoNotification.setModifiedDate(RandomTestUtil.nextDate());

		kaleoNotification.setKaleoClassName(RandomTestUtil.randomString());

		kaleoNotification.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoNotification.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoNotification.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		kaleoNotification.setKaleoNodeName(RandomTestUtil.randomString());

		kaleoNotification.setName(RandomTestUtil.randomString());

		kaleoNotification.setDescription(RandomTestUtil.randomString());

		kaleoNotification.setExecutionType(RandomTestUtil.randomString());

		kaleoNotification.setTemplate(RandomTestUtil.randomString());

		kaleoNotification.setTemplateLanguage(RandomTestUtil.randomString());

		kaleoNotification.setNotificationTypes(RandomTestUtil.randomString());

		_kaleoNotifications.add(_persistence.update(kaleoNotification));

		return kaleoNotification;
	}

	private List<KaleoNotification> _kaleoNotifications =
		new ArrayList<KaleoNotification>();
	private KaleoNotificationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1239777768