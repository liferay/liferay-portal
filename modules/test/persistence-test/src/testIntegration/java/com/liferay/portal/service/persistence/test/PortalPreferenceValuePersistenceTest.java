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
import com.liferay.portal.kernel.exception.NoSuchPreferenceValueException;
import com.liferay.portal.kernel.model.PortalPreferenceValue;
import com.liferay.portal.kernel.service.PortalPreferenceValueLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValuePersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferenceValueUtil;
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
public class PortalPreferenceValuePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PortalPreferenceValueUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PortalPreferenceValue> iterator =
			_portalPreferenceValues.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferenceValue portalPreferenceValue = _persistence.create(pk);

		Assert.assertNotNull(portalPreferenceValue);

		Assert.assertEquals(portalPreferenceValue.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		_persistence.remove(newPortalPreferenceValue);

		PortalPreferenceValue existingPortalPreferenceValue =
			_persistence.fetchByPrimaryKey(
				newPortalPreferenceValue.getPrimaryKey());

		Assert.assertNull(existingPortalPreferenceValue);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPortalPreferenceValue();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferenceValue newPortalPreferenceValue = _persistence.create(
			pk);

		newPortalPreferenceValue.setMvccVersion(RandomTestUtil.nextLong());

		newPortalPreferenceValue.setCompanyId(RandomTestUtil.nextLong());

		newPortalPreferenceValue.setPortalPreferencesId(
			RandomTestUtil.nextLong());

		newPortalPreferenceValue.setIndex(RandomTestUtil.nextInt());

		newPortalPreferenceValue.setKey(RandomTestUtil.randomString());

		newPortalPreferenceValue.setLargeValue(RandomTestUtil.randomString());

		newPortalPreferenceValue.setNamespace(RandomTestUtil.randomString());

		newPortalPreferenceValue.setSmallValue(RandomTestUtil.randomString());

		_portalPreferenceValues.add(
			_persistence.update(newPortalPreferenceValue));

		PortalPreferenceValue existingPortalPreferenceValue =
			_persistence.findByPrimaryKey(
				newPortalPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortalPreferenceValue.getMvccVersion(),
			newPortalPreferenceValue.getMvccVersion());
		Assert.assertEquals(
			existingPortalPreferenceValue.getPortalPreferenceValueId(),
			newPortalPreferenceValue.getPortalPreferenceValueId());
		Assert.assertEquals(
			existingPortalPreferenceValue.getCompanyId(),
			newPortalPreferenceValue.getCompanyId());
		Assert.assertEquals(
			existingPortalPreferenceValue.getPortalPreferencesId(),
			newPortalPreferenceValue.getPortalPreferencesId());
		Assert.assertEquals(
			existingPortalPreferenceValue.getIndex(),
			newPortalPreferenceValue.getIndex());
		Assert.assertEquals(
			existingPortalPreferenceValue.getKey(),
			newPortalPreferenceValue.getKey());
		Assert.assertEquals(
			existingPortalPreferenceValue.getLargeValue(),
			newPortalPreferenceValue.getLargeValue());
		Assert.assertEquals(
			existingPortalPreferenceValue.getNamespace(),
			newPortalPreferenceValue.getNamespace());
		Assert.assertEquals(
			existingPortalPreferenceValue.getSmallValue(),
			newPortalPreferenceValue.getSmallValue());
	}

	@Test
	public void testCountByPortalPreferencesId() throws Exception {
		_persistence.countByPortalPreferencesId(RandomTestUtil.nextLong());

		_persistence.countByPortalPreferencesId(0L);
	}

	@Test
	public void testCountByPortalPreferencesIdArrayable() throws Exception {
		_persistence.countByPortalPreferencesId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByP_N() throws Exception {
		_persistence.countByP_N(RandomTestUtil.nextLong(), "");

		_persistence.countByP_N(0L, "null");

		_persistence.countByP_N(0L, (String)null);
	}

	@Test
	public void testCountByP_K_N() throws Exception {
		_persistence.countByP_K_N(RandomTestUtil.nextLong(), "", "");

		_persistence.countByP_K_N(0L, "null", "null");

		_persistence.countByP_K_N(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByP_I_K_N() throws Exception {
		_persistence.countByP_I_K_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "", "");

		_persistence.countByP_I_K_N(0L, 0, "null", "null");

		_persistence.countByP_I_K_N(0L, 0, (String)null, (String)null);
	}

	@Test
	public void testCountByP_K_N_SV() throws Exception {
		_persistence.countByP_K_N_SV(RandomTestUtil.nextLong(), "", "", "");

		_persistence.countByP_K_N_SV(0L, "null", "null", "null");

		_persistence.countByP_K_N_SV(
			0L, (String)null, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		PortalPreferenceValue existingPortalPreferenceValue =
			_persistence.findByPrimaryKey(
				newPortalPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortalPreferenceValue, newPortalPreferenceValue);
	}

	@Test(expected = NoSuchPreferenceValueException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PortalPreferenceValue> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PortalPreferenceValue", "mvccVersion", true,
			"portalPreferenceValueId", true, "companyId", true,
			"portalPreferencesId", true, "index", true, "key", true,
			"namespace", true, "smallValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		PortalPreferenceValue existingPortalPreferenceValue =
			_persistence.fetchByPrimaryKey(
				newPortalPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortalPreferenceValue, newPortalPreferenceValue);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferenceValue missingPortalPreferenceValue =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPortalPreferenceValue);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PortalPreferenceValue newPortalPreferenceValue1 =
			addPortalPreferenceValue();
		PortalPreferenceValue newPortalPreferenceValue2 =
			addPortalPreferenceValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferenceValue1.getPrimaryKey());
		primaryKeys.add(newPortalPreferenceValue2.getPrimaryKey());

		Map<Serializable, PortalPreferenceValue> portalPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, portalPreferenceValues.size());
		Assert.assertEquals(
			newPortalPreferenceValue1,
			portalPreferenceValues.get(
				newPortalPreferenceValue1.getPrimaryKey()));
		Assert.assertEquals(
			newPortalPreferenceValue2,
			portalPreferenceValues.get(
				newPortalPreferenceValue2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PortalPreferenceValue> portalPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portalPreferenceValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferenceValue.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PortalPreferenceValue> portalPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portalPreferenceValues.size());
		Assert.assertEquals(
			newPortalPreferenceValue,
			portalPreferenceValues.get(
				newPortalPreferenceValue.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PortalPreferenceValue> portalPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portalPreferenceValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferenceValue.getPrimaryKey());

		Map<Serializable, PortalPreferenceValue> portalPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portalPreferenceValues.size());
		Assert.assertEquals(
			newPortalPreferenceValue,
			portalPreferenceValues.get(
				newPortalPreferenceValue.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PortalPreferenceValueLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PortalPreferenceValue>() {

				@Override
				public void performAction(
					PortalPreferenceValue portalPreferenceValue) {

					Assert.assertNotNull(portalPreferenceValue);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PortalPreferenceValue.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"portalPreferenceValueId",
				newPortalPreferenceValue.getPortalPreferenceValueId()));

		List<PortalPreferenceValue> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PortalPreferenceValue existingPortalPreferenceValue = result.get(0);

		Assert.assertEquals(
			existingPortalPreferenceValue, newPortalPreferenceValue);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PortalPreferenceValue.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"portalPreferenceValueId", RandomTestUtil.nextLong()));

		List<PortalPreferenceValue> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PortalPreferenceValue.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("portalPreferenceValueId"));

		Object newPortalPreferenceValueId =
			newPortalPreferenceValue.getPortalPreferenceValueId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"portalPreferenceValueId",
				new Object[] {newPortalPreferenceValueId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPortalPreferenceValueId = result.get(0);

		Assert.assertEquals(
			existingPortalPreferenceValueId, newPortalPreferenceValueId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PortalPreferenceValue.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("portalPreferenceValueId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"portalPreferenceValueId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPortalPreferenceValue.getPrimaryKey()));
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

		PortalPreferenceValue newPortalPreferenceValue =
			addPortalPreferenceValue();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PortalPreferenceValue.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"portalPreferenceValueId",
				newPortalPreferenceValue.getPortalPreferenceValueId()));

		List<PortalPreferenceValue> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		PortalPreferenceValue portalPreferenceValue) {

		Assert.assertEquals(
			Long.valueOf(portalPreferenceValue.getPortalPreferencesId()),
			ReflectionTestUtil.<Long>invoke(
				portalPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portalPreferencesId"));
		Assert.assertEquals(
			Integer.valueOf(portalPreferenceValue.getIndex()),
			ReflectionTestUtil.<Integer>invoke(
				portalPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "index_"));
		Assert.assertEquals(
			portalPreferenceValue.getKey(),
			ReflectionTestUtil.invoke(
				portalPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
		Assert.assertEquals(
			portalPreferenceValue.getNamespace(),
			ReflectionTestUtil.invoke(
				portalPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "namespace"));
	}

	protected PortalPreferenceValue addPortalPreferenceValue()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		PortalPreferenceValue portalPreferenceValue = _persistence.create(pk);

		portalPreferenceValue.setMvccVersion(RandomTestUtil.nextLong());

		portalPreferenceValue.setCompanyId(RandomTestUtil.nextLong());

		portalPreferenceValue.setPortalPreferencesId(RandomTestUtil.nextLong());

		portalPreferenceValue.setIndex(RandomTestUtil.nextInt());

		portalPreferenceValue.setKey(RandomTestUtil.randomString());

		portalPreferenceValue.setLargeValue(RandomTestUtil.randomString());

		portalPreferenceValue.setNamespace(RandomTestUtil.randomString());

		portalPreferenceValue.setSmallValue(RandomTestUtil.randomString());

		_portalPreferenceValues.add(_persistence.update(portalPreferenceValue));

		return portalPreferenceValue;
	}

	private List<PortalPreferenceValue> _portalPreferenceValues =
		new ArrayList<PortalPreferenceValue>();
	private PortalPreferenceValuePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}