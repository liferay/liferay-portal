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
import com.liferay.portal.tools.service.builder.test.exception.DuplicateERCGroupEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchERCGroupEntryException;
import com.liferay.portal.tools.service.builder.test.model.ERCGroupEntry;
import com.liferay.portal.tools.service.builder.test.service.ERCGroupEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCGroupEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCGroupEntryUtil;

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
public class ERCGroupEntryPersistenceTest {

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
		_persistence = ERCGroupEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ERCGroupEntry> iterator = _ercGroupEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCGroupEntry ercGroupEntry = _persistence.create(pk);

		Assert.assertNotNull(ercGroupEntry);

		Assert.assertEquals(ercGroupEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		_persistence.remove(newERCGroupEntry);

		ERCGroupEntry existingERCGroupEntry = _persistence.fetchByPrimaryKey(
			newERCGroupEntry.getPrimaryKey());

		Assert.assertNull(existingERCGroupEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addERCGroupEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		newERCGroupEntry.setUuid(RandomTestUtil.randomString());

		newERCGroupEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newERCGroupEntry.setGroupId(RandomTestUtil.nextLong());

		newERCGroupEntry.setCompanyId(RandomTestUtil.nextLong());

		newERCGroupEntry = _persistence.update(newERCGroupEntry);

		_ercGroupEntries.add(newERCGroupEntry);

		ERCGroupEntry existingERCGroupEntry = _persistence.findByPrimaryKey(
			newERCGroupEntry.getPrimaryKey());

		Assert.assertEquals(
			existingERCGroupEntry.getUuid(), newERCGroupEntry.getUuid());
		Assert.assertEquals(
			existingERCGroupEntry.getExternalReferenceCode(),
			newERCGroupEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingERCGroupEntry.getErcGroupEntryId(),
			newERCGroupEntry.getErcGroupEntryId());
		Assert.assertEquals(
			existingERCGroupEntry.getGroupId(), newERCGroupEntry.getGroupId());
		Assert.assertEquals(
			existingERCGroupEntry.getCompanyId(),
			newERCGroupEntry.getCompanyId());
	}

	@Test(expected = DuplicateERCGroupEntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ERCGroupEntry ercGroupEntry = addERCGroupEntry();

		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		newERCGroupEntry.setGroupId(ercGroupEntry.getGroupId());

		newERCGroupEntry = _persistence.update(newERCGroupEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newERCGroupEntry);

		newERCGroupEntry.setExternalReferenceCode(
			ercGroupEntry.getExternalReferenceCode());

		_persistence.update(newERCGroupEntry);
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
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		ERCGroupEntry existingERCGroupEntry = _persistence.findByPrimaryKey(
			newERCGroupEntry.getPrimaryKey());

		Assert.assertEquals(existingERCGroupEntry, newERCGroupEntry);
	}

	@Test(expected = NoSuchERCGroupEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ERCGroupEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ERCGroupEntry", "uuid", true, "externalReferenceCode", true,
			"ercGroupEntryId", true, "groupId", true, "companyId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		ERCGroupEntry existingERCGroupEntry = _persistence.fetchByPrimaryKey(
			newERCGroupEntry.getPrimaryKey());

		Assert.assertEquals(existingERCGroupEntry, newERCGroupEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCGroupEntry missingERCGroupEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingERCGroupEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ERCGroupEntry newERCGroupEntry1 = addERCGroupEntry();
		ERCGroupEntry newERCGroupEntry2 = addERCGroupEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCGroupEntry1.getPrimaryKey());
		primaryKeys.add(newERCGroupEntry2.getPrimaryKey());

		Map<Serializable, ERCGroupEntry> ercGroupEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ercGroupEntries.size());
		Assert.assertEquals(
			newERCGroupEntry1,
			ercGroupEntries.get(newERCGroupEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newERCGroupEntry2,
			ercGroupEntries.get(newERCGroupEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ERCGroupEntry> ercGroupEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercGroupEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCGroupEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ERCGroupEntry> ercGroupEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercGroupEntries.size());
		Assert.assertEquals(
			newERCGroupEntry,
			ercGroupEntries.get(newERCGroupEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ERCGroupEntry> ercGroupEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercGroupEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCGroupEntry.getPrimaryKey());

		Map<Serializable, ERCGroupEntry> ercGroupEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercGroupEntries.size());
		Assert.assertEquals(
			newERCGroupEntry,
			ercGroupEntries.get(newERCGroupEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ERCGroupEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ERCGroupEntry>() {

				@Override
				public void performAction(ERCGroupEntry ercGroupEntry) {
					Assert.assertNotNull(ercGroupEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCGroupEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercGroupEntryId", newERCGroupEntry.getErcGroupEntryId()));

		List<ERCGroupEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ERCGroupEntry existingERCGroupEntry = result.get(0);

		Assert.assertEquals(existingERCGroupEntry, newERCGroupEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCGroupEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercGroupEntryId", RandomTestUtil.nextLong()));

		List<ERCGroupEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCGroupEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercGroupEntryId"));

		Object newErcGroupEntryId = newERCGroupEntry.getErcGroupEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercGroupEntryId", new Object[] {newErcGroupEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingErcGroupEntryId = result.get(0);

		Assert.assertEquals(existingErcGroupEntryId, newErcGroupEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCGroupEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercGroupEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercGroupEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newERCGroupEntry.getPrimaryKey()));
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

		ERCGroupEntry newERCGroupEntry = addERCGroupEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCGroupEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercGroupEntryId", newERCGroupEntry.getErcGroupEntryId()));

		List<ERCGroupEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ERCGroupEntry ercGroupEntry) {
		Assert.assertEquals(
			ercGroupEntry.getUuid(),
			ReflectionTestUtil.invoke(
				ercGroupEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ercGroupEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ercGroupEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			ercGroupEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ercGroupEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ercGroupEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ercGroupEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected ERCGroupEntry addERCGroupEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCGroupEntry ercGroupEntry = _persistence.create(pk);

		ercGroupEntry.setUuid(RandomTestUtil.randomString());

		ercGroupEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		ercGroupEntry.setGroupId(RandomTestUtil.nextLong());

		ercGroupEntry.setCompanyId(RandomTestUtil.nextLong());

		_ercGroupEntries.add(_persistence.update(ercGroupEntry));

		return ercGroupEntry;
	}

	private List<ERCGroupEntry> _ercGroupEntries =
		new ArrayList<ERCGroupEntry>();
	private ERCGroupEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1410283148