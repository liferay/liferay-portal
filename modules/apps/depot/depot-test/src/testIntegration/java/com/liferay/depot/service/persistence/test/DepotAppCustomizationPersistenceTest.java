/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.exception.NoSuchAppCustomizationException;
import com.liferay.depot.model.DepotAppCustomization;
import com.liferay.depot.service.DepotAppCustomizationLocalServiceUtil;
import com.liferay.depot.service.persistence.DepotAppCustomizationPersistence;
import com.liferay.depot.service.persistence.DepotAppCustomizationUtil;
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
public class DepotAppCustomizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.depot.service"));

	@Before
	public void setUp() {
		_persistence = DepotAppCustomizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DepotAppCustomization> iterator =
			_depotAppCustomizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotAppCustomization depotAppCustomization = _persistence.create(pk);

		Assert.assertNotNull(depotAppCustomization);

		Assert.assertEquals(depotAppCustomization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		_persistence.remove(newDepotAppCustomization);

		DepotAppCustomization existingDepotAppCustomization =
			_persistence.fetchByPrimaryKey(
				newDepotAppCustomization.getPrimaryKey());

		Assert.assertNull(existingDepotAppCustomization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDepotAppCustomization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		newDepotAppCustomization.setCtCollectionId(RandomTestUtil.nextLong());

		newDepotAppCustomization.setCompanyId(RandomTestUtil.nextLong());

		newDepotAppCustomization.setDepotEntryId(RandomTestUtil.nextLong());

		newDepotAppCustomization.setEnabled(RandomTestUtil.randomBoolean());

		newDepotAppCustomization.setPortletId(RandomTestUtil.randomString());

		newDepotAppCustomization = _persistence.update(
			newDepotAppCustomization);

		_depotAppCustomizations.add(newDepotAppCustomization);

		DepotAppCustomization existingDepotAppCustomization =
			_persistence.findByPrimaryKey(
				newDepotAppCustomization.getPrimaryKey());

		Assert.assertEquals(
			existingDepotAppCustomization.getMvccVersion(),
			newDepotAppCustomization.getMvccVersion());
		Assert.assertEquals(
			existingDepotAppCustomization.getCtCollectionId(),
			newDepotAppCustomization.getCtCollectionId());
		Assert.assertEquals(
			existingDepotAppCustomization.getDepotAppCustomizationId(),
			newDepotAppCustomization.getDepotAppCustomizationId());
		Assert.assertEquals(
			existingDepotAppCustomization.getCompanyId(),
			newDepotAppCustomization.getCompanyId());
		Assert.assertEquals(
			existingDepotAppCustomization.getDepotEntryId(),
			newDepotAppCustomization.getDepotEntryId());
		Assert.assertEquals(
			existingDepotAppCustomization.isEnabled(),
			newDepotAppCustomization.isEnabled());
		Assert.assertEquals(
			existingDepotAppCustomization.getPortletId(),
			newDepotAppCustomization.getPortletId());
	}

	@Test
	public void testCountByDepotEntryId() throws Exception {
		_persistence.countByDepotEntryId(RandomTestUtil.nextLong());

		_persistence.countByDepotEntryId(0L);
	}

	@Test
	public void testCountByD_E() throws Exception {
		_persistence.countByD_E(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByD_E(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByD_P() throws Exception {
		_persistence.countByD_P(RandomTestUtil.nextLong(), "");

		_persistence.countByD_P(0L, "null");

		_persistence.countByD_P(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		DepotAppCustomization existingDepotAppCustomization =
			_persistence.findByPrimaryKey(
				newDepotAppCustomization.getPrimaryKey());

		Assert.assertEquals(
			existingDepotAppCustomization, newDepotAppCustomization);
	}

	@Test(expected = NoSuchAppCustomizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DepotAppCustomization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DepotAppCustomization", "mvccVersion", true, "ctCollectionId",
			true, "depotAppCustomizationId", true, "companyId", true,
			"depotEntryId", true, "enabled", true, "portletId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		DepotAppCustomization existingDepotAppCustomization =
			_persistence.fetchByPrimaryKey(
				newDepotAppCustomization.getPrimaryKey());

		Assert.assertEquals(
			existingDepotAppCustomization, newDepotAppCustomization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotAppCustomization missingDepotAppCustomization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDepotAppCustomization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DepotAppCustomization newDepotAppCustomization1 =
			addDepotAppCustomization();
		DepotAppCustomization newDepotAppCustomization2 =
			addDepotAppCustomization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotAppCustomization1.getPrimaryKey());
		primaryKeys.add(newDepotAppCustomization2.getPrimaryKey());

		Map<Serializable, DepotAppCustomization> depotAppCustomizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, depotAppCustomizations.size());
		Assert.assertEquals(
			newDepotAppCustomization1,
			depotAppCustomizations.get(
				newDepotAppCustomization1.getPrimaryKey()));
		Assert.assertEquals(
			newDepotAppCustomization2,
			depotAppCustomizations.get(
				newDepotAppCustomization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DepotAppCustomization> depotAppCustomizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotAppCustomizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotAppCustomization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DepotAppCustomization> depotAppCustomizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotAppCustomizations.size());
		Assert.assertEquals(
			newDepotAppCustomization,
			depotAppCustomizations.get(
				newDepotAppCustomization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DepotAppCustomization> depotAppCustomizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotAppCustomizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotAppCustomization.getPrimaryKey());

		Map<Serializable, DepotAppCustomization> depotAppCustomizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotAppCustomizations.size());
		Assert.assertEquals(
			newDepotAppCustomization,
			depotAppCustomizations.get(
				newDepotAppCustomization.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DepotAppCustomizationLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<DepotAppCustomization>() {

				@Override
				public void performAction(
					DepotAppCustomization depotAppCustomization) {

					Assert.assertNotNull(depotAppCustomization);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotAppCustomization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotAppCustomizationId",
				newDepotAppCustomization.getDepotAppCustomizationId()));

		List<DepotAppCustomization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DepotAppCustomization existingDepotAppCustomization = result.get(0);

		Assert.assertEquals(
			existingDepotAppCustomization, newDepotAppCustomization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotAppCustomization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotAppCustomizationId", RandomTestUtil.nextLong()));

		List<DepotAppCustomization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotAppCustomization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("depotAppCustomizationId"));

		Object newDepotAppCustomizationId =
			newDepotAppCustomization.getDepotAppCustomizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"depotAppCustomizationId",
				new Object[] {newDepotAppCustomizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDepotAppCustomizationId = result.get(0);

		Assert.assertEquals(
			existingDepotAppCustomizationId, newDepotAppCustomizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotAppCustomization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("depotAppCustomizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"depotAppCustomizationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDepotAppCustomization.getPrimaryKey()));
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

		DepotAppCustomization newDepotAppCustomization =
			addDepotAppCustomization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotAppCustomization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotAppCustomizationId",
				newDepotAppCustomization.getDepotAppCustomizationId()));

		List<DepotAppCustomization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		DepotAppCustomization depotAppCustomization) {

		Assert.assertEquals(
			Long.valueOf(depotAppCustomization.getDepotEntryId()),
			ReflectionTestUtil.<Long>invoke(
				depotAppCustomization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "depotEntryId"));
		Assert.assertEquals(
			Boolean.valueOf(depotAppCustomization.getEnabled()),
			ReflectionTestUtil.<Boolean>invoke(
				depotAppCustomization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "enabled"));

		Assert.assertEquals(
			Long.valueOf(depotAppCustomization.getDepotEntryId()),
			ReflectionTestUtil.<Long>invoke(
				depotAppCustomization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "depotEntryId"));
		Assert.assertEquals(
			depotAppCustomization.getPortletId(),
			ReflectionTestUtil.invoke(
				depotAppCustomization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));
	}

	protected DepotAppCustomization addDepotAppCustomization()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DepotAppCustomization depotAppCustomization = _persistence.create(pk);

		depotAppCustomization.setCtCollectionId(RandomTestUtil.nextLong());

		depotAppCustomization.setCompanyId(RandomTestUtil.nextLong());

		depotAppCustomization.setDepotEntryId(RandomTestUtil.nextLong());

		depotAppCustomization.setEnabled(RandomTestUtil.randomBoolean());

		depotAppCustomization.setPortletId(RandomTestUtil.randomString());

		_depotAppCustomizations.add(_persistence.update(depotAppCustomization));

		return depotAppCustomization;
	}

	private List<DepotAppCustomization> _depotAppCustomizations =
		new ArrayList<DepotAppCustomization>();
	private DepotAppCustomizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1284350686