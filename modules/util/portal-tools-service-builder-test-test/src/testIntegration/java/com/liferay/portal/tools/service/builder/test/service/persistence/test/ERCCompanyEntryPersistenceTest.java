/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

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
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.DuplicateERCCompanyEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchERCCompanyEntryException;
import com.liferay.portal.tools.service.builder.test.model.ERCCompanyEntry;
import com.liferay.portal.tools.service.builder.test.service.ERCCompanyEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCCompanyEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCCompanyEntryUtil;

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
public class ERCCompanyEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = ERCCompanyEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ERCCompanyEntry> iterator = _ercCompanyEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCCompanyEntry ercCompanyEntry = _persistence.create(pk);

		Assert.assertNotNull(ercCompanyEntry);

		Assert.assertEquals(ercCompanyEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		_persistence.remove(newERCCompanyEntry);

		ERCCompanyEntry existingERCCompanyEntry =
			_persistence.fetchByPrimaryKey(newERCCompanyEntry.getPrimaryKey());

		Assert.assertNull(existingERCCompanyEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addERCCompanyEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		newERCCompanyEntry.setUuid(RandomTestUtil.randomString());

		newERCCompanyEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newERCCompanyEntry.setCompanyId(RandomTestUtil.nextLong());

		newERCCompanyEntry.setUserId(RandomTestUtil.nextLong());

		newERCCompanyEntry.setUserName(RandomTestUtil.randomString());

		newERCCompanyEntry.setColumn1(RandomTestUtil.nextInt());

		newERCCompanyEntry = _persistence.update(newERCCompanyEntry);

		_ercCompanyEntries.add(newERCCompanyEntry);

		ERCCompanyEntry existingERCCompanyEntry = _persistence.findByPrimaryKey(
			newERCCompanyEntry.getPrimaryKey());

		Assert.assertEquals(
			existingERCCompanyEntry.getUuid(), newERCCompanyEntry.getUuid());
		Assert.assertEquals(
			existingERCCompanyEntry.getExternalReferenceCode(),
			newERCCompanyEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingERCCompanyEntry.getErcCompanyEntryId(),
			newERCCompanyEntry.getErcCompanyEntryId());
		Assert.assertEquals(
			existingERCCompanyEntry.getCompanyId(),
			newERCCompanyEntry.getCompanyId());
		Assert.assertEquals(
			existingERCCompanyEntry.getUserId(),
			newERCCompanyEntry.getUserId());
		Assert.assertEquals(
			existingERCCompanyEntry.getUserName(),
			newERCCompanyEntry.getUserName());
		Assert.assertEquals(
			existingERCCompanyEntry.getColumn1(),
			newERCCompanyEntry.getColumn1());
	}

	@Test(
		expected = DuplicateERCCompanyEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ERCCompanyEntry ercCompanyEntry = addERCCompanyEntry();

		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		newERCCompanyEntry.setCompanyId(ercCompanyEntry.getCompanyId());

		newERCCompanyEntry = _persistence.update(newERCCompanyEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newERCCompanyEntry);

		newERCCompanyEntry.setExternalReferenceCode(
			ercCompanyEntry.getExternalReferenceCode());

		_persistence.update(newERCCompanyEntry);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		ERCCompanyEntry existingERCCompanyEntry = _persistence.findByPrimaryKey(
			newERCCompanyEntry.getPrimaryKey());

		Assert.assertEquals(existingERCCompanyEntry, newERCCompanyEntry);
	}

	@Test(expected = NoSuchERCCompanyEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ERCCompanyEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ERCCompanyEntry", "uuid", true, "externalReferenceCode", true,
			"ercCompanyEntryId", true, "companyId", true, "userId", true,
			"userName", true, "column1", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		ERCCompanyEntry existingERCCompanyEntry =
			_persistence.fetchByPrimaryKey(newERCCompanyEntry.getPrimaryKey());

		Assert.assertEquals(existingERCCompanyEntry, newERCCompanyEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCCompanyEntry missingERCCompanyEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingERCCompanyEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ERCCompanyEntry newERCCompanyEntry1 = addERCCompanyEntry();
		ERCCompanyEntry newERCCompanyEntry2 = addERCCompanyEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCCompanyEntry1.getPrimaryKey());
		primaryKeys.add(newERCCompanyEntry2.getPrimaryKey());

		Map<Serializable, ERCCompanyEntry> ercCompanyEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ercCompanyEntries.size());
		Assert.assertEquals(
			newERCCompanyEntry1,
			ercCompanyEntries.get(newERCCompanyEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newERCCompanyEntry2,
			ercCompanyEntries.get(newERCCompanyEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ERCCompanyEntry> ercCompanyEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercCompanyEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCCompanyEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ERCCompanyEntry> ercCompanyEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercCompanyEntries.size());
		Assert.assertEquals(
			newERCCompanyEntry,
			ercCompanyEntries.get(newERCCompanyEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ERCCompanyEntry> ercCompanyEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercCompanyEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCCompanyEntry.getPrimaryKey());

		Map<Serializable, ERCCompanyEntry> ercCompanyEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercCompanyEntries.size());
		Assert.assertEquals(
			newERCCompanyEntry,
			ercCompanyEntries.get(newERCCompanyEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ERCCompanyEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ERCCompanyEntry>() {

				@Override
				public void performAction(ERCCompanyEntry ercCompanyEntry) {
					Assert.assertNotNull(ercCompanyEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCCompanyEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercCompanyEntryId",
				newERCCompanyEntry.getErcCompanyEntryId()));

		List<ERCCompanyEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ERCCompanyEntry existingERCCompanyEntry = result.get(0);

		Assert.assertEquals(existingERCCompanyEntry, newERCCompanyEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCCompanyEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercCompanyEntryId", RandomTestUtil.nextLong()));

		List<ERCCompanyEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCCompanyEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercCompanyEntryId"));

		Object newErcCompanyEntryId = newERCCompanyEntry.getErcCompanyEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercCompanyEntryId", new Object[] {newErcCompanyEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingErcCompanyEntryId = result.get(0);

		Assert.assertEquals(existingErcCompanyEntryId, newErcCompanyEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCCompanyEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercCompanyEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercCompanyEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newERCCompanyEntry.getPrimaryKey()));
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

		ERCCompanyEntry newERCCompanyEntry = addERCCompanyEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCCompanyEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercCompanyEntryId",
				newERCCompanyEntry.getErcCompanyEntryId()));

		List<ERCCompanyEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ERCCompanyEntry ercCompanyEntry) {
		Assert.assertEquals(
			ercCompanyEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ercCompanyEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ercCompanyEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				ercCompanyEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected ERCCompanyEntry addERCCompanyEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCCompanyEntry ercCompanyEntry = _persistence.create(pk);

		ercCompanyEntry.setUuid(RandomTestUtil.randomString());

		ercCompanyEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		ercCompanyEntry.setCompanyId(RandomTestUtil.nextLong());

		ercCompanyEntry.setUserId(RandomTestUtil.nextLong());

		ercCompanyEntry.setUserName(RandomTestUtil.randomString());

		ercCompanyEntry.setColumn1(RandomTestUtil.nextInt());

		_ercCompanyEntries.add(_persistence.update(ercCompanyEntry));

		return ercCompanyEntry;
	}

	private List<ERCCompanyEntry> _ercCompanyEntries =
		new ArrayList<ERCCompanyEntry>();
	private ERCCompanyEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:14794364