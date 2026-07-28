/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchPreferencesException;
import com.liferay.change.tracking.model.CTPreferences;
import com.liferay.change.tracking.service.CTPreferencesLocalServiceUtil;
import com.liferay.change.tracking.service.persistence.CTPreferencesPersistence;
import com.liferay.change.tracking.service.persistence.CTPreferencesUtil;
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
public class CTPreferencesPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTPreferencesUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTPreferences> iterator = _ctPreferenceses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTPreferences ctPreferences = _persistence.create(pk);

		Assert.assertNotNull(ctPreferences);

		Assert.assertEquals(ctPreferences.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		_persistence.remove(newCTPreferences);

		CTPreferences existingCTPreferences = _persistence.fetchByPrimaryKey(
			newCTPreferences.getPrimaryKey());

		Assert.assertNull(existingCTPreferences);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTPreferences();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		newCTPreferences.setCompanyId(RandomTestUtil.nextLong());

		newCTPreferences.setUserId(RandomTestUtil.nextLong());

		newCTPreferences.setCtCollectionId(RandomTestUtil.nextLong());

		newCTPreferences.setPreviousCtCollectionId(RandomTestUtil.nextLong());

		newCTPreferences.setConfirmationEnabled(RandomTestUtil.randomBoolean());

		newCTPreferences = _persistence.update(newCTPreferences);

		_ctPreferenceses.add(newCTPreferences);

		CTPreferences existingCTPreferences = _persistence.findByPrimaryKey(
			newCTPreferences.getPrimaryKey());

		Assert.assertEquals(
			existingCTPreferences.getMvccVersion(),
			newCTPreferences.getMvccVersion());
		Assert.assertEquals(
			existingCTPreferences.getCtPreferencesId(),
			newCTPreferences.getCtPreferencesId());
		Assert.assertEquals(
			existingCTPreferences.getCompanyId(),
			newCTPreferences.getCompanyId());
		Assert.assertEquals(
			existingCTPreferences.getUserId(), newCTPreferences.getUserId());
		Assert.assertEquals(
			existingCTPreferences.getCtCollectionId(),
			newCTPreferences.getCtCollectionId());
		Assert.assertEquals(
			existingCTPreferences.getPreviousCtCollectionId(),
			newCTPreferences.getPreviousCtCollectionId());
		Assert.assertEquals(
			existingCTPreferences.isConfirmationEnabled(),
			newCTPreferences.isConfirmationEnabled());
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testCountByPreviousCtCollectionId() throws Exception {
		_persistence.countByPreviousCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByPreviousCtCollectionId(0L);
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		CTPreferences existingCTPreferences = _persistence.findByPrimaryKey(
			newCTPreferences.getPrimaryKey());

		Assert.assertEquals(existingCTPreferences, newCTPreferences);
	}

	@Test(expected = NoSuchPreferencesException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTPreferences> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTPreferences", "mvccVersion", true, "ctPreferencesId", true,
			"companyId", true, "userId", true, "ctCollectionId", true,
			"previousCtCollectionId", true, "confirmationEnabled", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		CTPreferences existingCTPreferences = _persistence.fetchByPrimaryKey(
			newCTPreferences.getPrimaryKey());

		Assert.assertEquals(existingCTPreferences, newCTPreferences);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTPreferences missingCTPreferences = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTPreferences);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTPreferences newCTPreferences1 = addCTPreferences();
		CTPreferences newCTPreferences2 = addCTPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTPreferences1.getPrimaryKey());
		primaryKeys.add(newCTPreferences2.getPrimaryKey());

		Map<Serializable, CTPreferences> ctPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctPreferenceses.size());
		Assert.assertEquals(
			newCTPreferences1,
			ctPreferenceses.get(newCTPreferences1.getPrimaryKey()));
		Assert.assertEquals(
			newCTPreferences2,
			ctPreferenceses.get(newCTPreferences2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTPreferences> ctPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTPreferences newCTPreferences = addCTPreferences();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTPreferences.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTPreferences> ctPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctPreferenceses.size());
		Assert.assertEquals(
			newCTPreferences,
			ctPreferenceses.get(newCTPreferences.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTPreferences> ctPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTPreferences.getPrimaryKey());

		Map<Serializable, CTPreferences> ctPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctPreferenceses.size());
		Assert.assertEquals(
			newCTPreferences,
			ctPreferenceses.get(newCTPreferences.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTPreferencesLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CTPreferences>() {

				@Override
				public void performAction(CTPreferences ctPreferences) {
					Assert.assertNotNull(ctPreferences);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTPreferences.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctPreferencesId", newCTPreferences.getCtPreferencesId()));

		List<CTPreferences> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTPreferences existingCTPreferences = result.get(0);

		Assert.assertEquals(existingCTPreferences, newCTPreferences);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTPreferences.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctPreferencesId", RandomTestUtil.nextLong()));

		List<CTPreferences> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTPreferences.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctPreferencesId"));

		Object newCtPreferencesId = newCTPreferences.getCtPreferencesId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctPreferencesId", new Object[] {newCtPreferencesId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtPreferencesId = result.get(0);

		Assert.assertEquals(existingCtPreferencesId, newCtPreferencesId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTPreferences.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctPreferencesId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctPreferencesId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTPreferences newCTPreferences = addCTPreferences();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCTPreferences.getPrimaryKey()));
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

		CTPreferences newCTPreferences = addCTPreferences();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTPreferences.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctPreferencesId", newCTPreferences.getCtPreferencesId()));

		List<CTPreferences> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CTPreferences ctPreferences) {
		Assert.assertEquals(
			Long.valueOf(ctPreferences.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ctPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(ctPreferences.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				ctPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
	}

	protected CTPreferences addCTPreferences() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTPreferences ctPreferences = _persistence.create(pk);

		ctPreferences.setCompanyId(RandomTestUtil.nextLong());

		ctPreferences.setUserId(RandomTestUtil.nextLong());

		ctPreferences.setCtCollectionId(RandomTestUtil.nextLong());

		ctPreferences.setPreviousCtCollectionId(RandomTestUtil.nextLong());

		ctPreferences.setConfirmationEnabled(RandomTestUtil.randomBoolean());

		_ctPreferenceses.add(_persistence.update(ctPreferences));

		return ctPreferences;
	}

	private List<CTPreferences> _ctPreferenceses =
		new ArrayList<CTPreferences>();
	private CTPreferencesPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:2036796056