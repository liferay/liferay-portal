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
import com.liferay.portal.kernel.exception.NoSuchServiceComponentException;
import com.liferay.portal.kernel.model.ServiceComponent;
import com.liferay.portal.kernel.service.ServiceComponentLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.ServiceComponentPersistence;
import com.liferay.portal.kernel.service.persistence.ServiceComponentUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class ServiceComponentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ServiceComponentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ServiceComponent> iterator = _serviceComponents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ServiceComponent serviceComponent = _persistence.create(pk);

		Assert.assertNotNull(serviceComponent);

		Assert.assertEquals(serviceComponent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		_persistence.remove(newServiceComponent);

		ServiceComponent existingServiceComponent =
			_persistence.fetchByPrimaryKey(newServiceComponent.getPrimaryKey());

		Assert.assertNull(existingServiceComponent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addServiceComponent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		newServiceComponent.setBuildNamespace(RandomTestUtil.randomString());

		newServiceComponent.setBuildNumber(RandomTestUtil.nextLong());

		newServiceComponent.setBuildDate(RandomTestUtil.nextLong());

		newServiceComponent.setData(RandomTestUtil.randomString());

		newServiceComponent = _persistence.update(newServiceComponent);

		_serviceComponents.add(newServiceComponent);

		ServiceComponent existingServiceComponent =
			_persistence.findByPrimaryKey(newServiceComponent.getPrimaryKey());

		Assert.assertEquals(
			existingServiceComponent.getMvccVersion(),
			newServiceComponent.getMvccVersion());
		Assert.assertEquals(
			existingServiceComponent.getServiceComponentId(),
			newServiceComponent.getServiceComponentId());
		Assert.assertEquals(
			existingServiceComponent.getBuildNamespace(),
			newServiceComponent.getBuildNamespace());
		Assert.assertEquals(
			existingServiceComponent.getBuildNumber(),
			newServiceComponent.getBuildNumber());
		Assert.assertEquals(
			existingServiceComponent.getBuildDate(),
			newServiceComponent.getBuildDate());
		Assert.assertEquals(
			existingServiceComponent.getData(), newServiceComponent.getData());
	}

	@Test
	public void testCountByBuildNamespace() throws Exception {
		_persistence.countByBuildNamespace("");

		_persistence.countByBuildNamespace("null");

		_persistence.countByBuildNamespace((String)null);
	}

	@Test
	public void testCountByBNS_BNU() throws Exception {
		_persistence.countByBNS_BNU("", RandomTestUtil.nextLong());

		_persistence.countByBNS_BNU("null", 0L);

		_persistence.countByBNS_BNU((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		ServiceComponent existingServiceComponent =
			_persistence.findByPrimaryKey(newServiceComponent.getPrimaryKey());

		Assert.assertEquals(existingServiceComponent, newServiceComponent);
	}

	@Test(expected = NoSuchServiceComponentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ServiceComponent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ServiceComponent", "mvccVersion", true, "serviceComponentId", true,
			"buildNamespace", true, "buildNumber", true, "buildDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		ServiceComponent existingServiceComponent =
			_persistence.fetchByPrimaryKey(newServiceComponent.getPrimaryKey());

		Assert.assertEquals(existingServiceComponent, newServiceComponent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ServiceComponent missingServiceComponent =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingServiceComponent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ServiceComponent newServiceComponent1 = addServiceComponent();
		ServiceComponent newServiceComponent2 = addServiceComponent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newServiceComponent1.getPrimaryKey());
		primaryKeys.add(newServiceComponent2.getPrimaryKey());

		Map<Serializable, ServiceComponent> serviceComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, serviceComponents.size());
		Assert.assertEquals(
			newServiceComponent1,
			serviceComponents.get(newServiceComponent1.getPrimaryKey()));
		Assert.assertEquals(
			newServiceComponent2,
			serviceComponents.get(newServiceComponent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ServiceComponent> serviceComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(serviceComponents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ServiceComponent newServiceComponent = addServiceComponent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newServiceComponent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ServiceComponent> serviceComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, serviceComponents.size());
		Assert.assertEquals(
			newServiceComponent,
			serviceComponents.get(newServiceComponent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ServiceComponent> serviceComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(serviceComponents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newServiceComponent.getPrimaryKey());

		Map<Serializable, ServiceComponent> serviceComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, serviceComponents.size());
		Assert.assertEquals(
			newServiceComponent,
			serviceComponents.get(newServiceComponent.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ServiceComponentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ServiceComponent>() {

				@Override
				public void performAction(ServiceComponent serviceComponent) {
					Assert.assertNotNull(serviceComponent);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ServiceComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"serviceComponentId",
				newServiceComponent.getServiceComponentId()));

		List<ServiceComponent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ServiceComponent existingServiceComponent = result.get(0);

		Assert.assertEquals(existingServiceComponent, newServiceComponent);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ServiceComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"serviceComponentId", RandomTestUtil.nextLong()));

		List<ServiceComponent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ServiceComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("serviceComponentId"));

		Object newServiceComponentId =
			newServiceComponent.getServiceComponentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"serviceComponentId", new Object[] {newServiceComponentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingServiceComponentId = result.get(0);

		Assert.assertEquals(existingServiceComponentId, newServiceComponentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ServiceComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("serviceComponentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"serviceComponentId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ServiceComponent newServiceComponent = addServiceComponent();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newServiceComponent.getPrimaryKey()));
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

		ServiceComponent newServiceComponent = addServiceComponent();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ServiceComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"serviceComponentId",
				newServiceComponent.getServiceComponentId()));

		List<ServiceComponent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ServiceComponent serviceComponent) {
		Assert.assertEquals(
			serviceComponent.getBuildNamespace(),
			ReflectionTestUtil.invoke(
				serviceComponent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "buildNamespace"));
		Assert.assertEquals(
			Long.valueOf(serviceComponent.getBuildNumber()),
			ReflectionTestUtil.<Long>invoke(
				serviceComponent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "buildNumber"));
	}

	protected ServiceComponent addServiceComponent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ServiceComponent serviceComponent = _persistence.create(pk);

		serviceComponent.setBuildNamespace(RandomTestUtil.randomString());

		serviceComponent.setBuildNumber(RandomTestUtil.nextLong());

		serviceComponent.setBuildDate(RandomTestUtil.nextLong());

		serviceComponent.setData(RandomTestUtil.randomString());

		_serviceComponents.add(_persistence.update(serviceComponent));

		return serviceComponent;
	}

	private List<ServiceComponent> _serviceComponents =
		new ArrayList<ServiceComponent>();
	private ServiceComponentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1392823828