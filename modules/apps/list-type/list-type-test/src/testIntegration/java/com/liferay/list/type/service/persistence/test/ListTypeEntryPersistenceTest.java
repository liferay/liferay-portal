/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.list.type.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.list.type.exception.NoSuchListTypeEntryException;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalServiceUtil;
import com.liferay.list.type.service.persistence.ListTypeEntryPersistence;
import com.liferay.list.type.service.persistence.ListTypeEntryUtil;
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
public class ListTypeEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.list.type.service"));

	@Before
	public void setUp() {
		_persistence = ListTypeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ListTypeEntry> iterator = _listTypeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListTypeEntry listTypeEntry = _persistence.create(pk);

		Assert.assertNotNull(listTypeEntry);

		Assert.assertEquals(listTypeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		_persistence.remove(newListTypeEntry);

		ListTypeEntry existingListTypeEntry = _persistence.fetchByPrimaryKey(
			newListTypeEntry.getPrimaryKey());

		Assert.assertNull(existingListTypeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addListTypeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		newListTypeEntry.setUuid(RandomTestUtil.randomString());

		newListTypeEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newListTypeEntry.setCompanyId(RandomTestUtil.nextLong());

		newListTypeEntry.setUserId(RandomTestUtil.nextLong());

		newListTypeEntry.setUserName(RandomTestUtil.randomString());

		newListTypeEntry.setCreateDate(RandomTestUtil.nextDate());

		newListTypeEntry.setModifiedDate(RandomTestUtil.nextDate());

		newListTypeEntry.setListTypeDefinitionId(RandomTestUtil.nextLong());

		newListTypeEntry.setKey(RandomTestUtil.randomString());

		newListTypeEntry.setName(RandomTestUtil.randomString());

		newListTypeEntry.setSystem(RandomTestUtil.randomBoolean());

		newListTypeEntry.setType(RandomTestUtil.randomString());

		newListTypeEntry.setStatus(RandomTestUtil.nextInt());

		newListTypeEntry = _persistence.update(newListTypeEntry);

		_listTypeEntries.add(newListTypeEntry);

		ListTypeEntry existingListTypeEntry = _persistence.findByPrimaryKey(
			newListTypeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingListTypeEntry.getMvccVersion(),
			newListTypeEntry.getMvccVersion());
		Assert.assertEquals(
			existingListTypeEntry.getUuid(), newListTypeEntry.getUuid());
		Assert.assertEquals(
			existingListTypeEntry.getExternalReferenceCode(),
			newListTypeEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingListTypeEntry.getListTypeEntryId(),
			newListTypeEntry.getListTypeEntryId());
		Assert.assertEquals(
			existingListTypeEntry.getCompanyId(),
			newListTypeEntry.getCompanyId());
		Assert.assertEquals(
			existingListTypeEntry.getUserId(), newListTypeEntry.getUserId());
		Assert.assertEquals(
			existingListTypeEntry.getUserName(),
			newListTypeEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingListTypeEntry.getCreateDate()),
			Time.getShortTimestamp(newListTypeEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingListTypeEntry.getModifiedDate()),
			Time.getShortTimestamp(newListTypeEntry.getModifiedDate()));
		Assert.assertEquals(
			existingListTypeEntry.getListTypeDefinitionId(),
			newListTypeEntry.getListTypeDefinitionId());
		Assert.assertEquals(
			existingListTypeEntry.getKey(), newListTypeEntry.getKey());
		Assert.assertEquals(
			existingListTypeEntry.getName(), newListTypeEntry.getName());
		Assert.assertEquals(
			existingListTypeEntry.isSystem(), newListTypeEntry.isSystem());
		Assert.assertEquals(
			existingListTypeEntry.getType(), newListTypeEntry.getType());
		Assert.assertEquals(
			existingListTypeEntry.getStatus(), newListTypeEntry.getStatus());
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
	public void testCountByListTypeEntryId() throws Exception {
		_persistence.countByListTypeEntryId(RandomTestUtil.nextLong());

		_persistence.countByListTypeEntryId(0L);
	}

	@Test
	public void testCountByListTypeEntryIdArrayable() throws Exception {
		_persistence.countByListTypeEntryId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByListTypeDefinitionId() throws Exception {
		_persistence.countByListTypeDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByListTypeDefinitionId(0L);
	}

	@Test
	public void testCountByListTypeDefinitionIdArrayable() throws Exception {
		_persistence.countByListTypeDefinitionId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByLTDI_K() throws Exception {
		_persistence.countByLTDI_K(RandomTestUtil.nextLong(), "");

		_persistence.countByLTDI_K(0L, "null");

		_persistence.countByLTDI_K(0L, (String)null);
	}

	@Test
	public void testCountByERC_C_LTDI() throws Exception {
		_persistence.countByERC_C_LTDI(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_C_LTDI("null", 0L, 0L);

		_persistence.countByERC_C_LTDI((String)null, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		ListTypeEntry existingListTypeEntry = _persistence.findByPrimaryKey(
			newListTypeEntry.getPrimaryKey());

		Assert.assertEquals(existingListTypeEntry, newListTypeEntry);
	}

	@Test(expected = NoSuchListTypeEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ListTypeEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ListTypeEntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "listTypeEntryId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "listTypeDefinitionId", true, "key", true,
			"name", true, "system", true, "type", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		ListTypeEntry existingListTypeEntry = _persistence.fetchByPrimaryKey(
			newListTypeEntry.getPrimaryKey());

		Assert.assertEquals(existingListTypeEntry, newListTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListTypeEntry missingListTypeEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingListTypeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ListTypeEntry newListTypeEntry1 = addListTypeEntry();
		ListTypeEntry newListTypeEntry2 = addListTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListTypeEntry1.getPrimaryKey());
		primaryKeys.add(newListTypeEntry2.getPrimaryKey());

		Map<Serializable, ListTypeEntry> listTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, listTypeEntries.size());
		Assert.assertEquals(
			newListTypeEntry1,
			listTypeEntries.get(newListTypeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newListTypeEntry2,
			listTypeEntries.get(newListTypeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ListTypeEntry> listTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(listTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ListTypeEntry newListTypeEntry = addListTypeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListTypeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ListTypeEntry> listTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, listTypeEntries.size());
		Assert.assertEquals(
			newListTypeEntry,
			listTypeEntries.get(newListTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ListTypeEntry> listTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(listTypeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListTypeEntry.getPrimaryKey());

		Map<Serializable, ListTypeEntry> listTypeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, listTypeEntries.size());
		Assert.assertEquals(
			newListTypeEntry,
			listTypeEntries.get(newListTypeEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ListTypeEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ListTypeEntry>() {

				@Override
				public void performAction(ListTypeEntry listTypeEntry) {
					Assert.assertNotNull(listTypeEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ListTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"listTypeEntryId", newListTypeEntry.getListTypeEntryId()));

		List<ListTypeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ListTypeEntry existingListTypeEntry = result.get(0);

		Assert.assertEquals(existingListTypeEntry, newListTypeEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ListTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"listTypeEntryId", RandomTestUtil.nextLong()));

		List<ListTypeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ListTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("listTypeEntryId"));

		Object newListTypeEntryId = newListTypeEntry.getListTypeEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"listTypeEntryId", new Object[] {newListTypeEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingListTypeEntryId = result.get(0);

		Assert.assertEquals(existingListTypeEntryId, newListTypeEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ListTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("listTypeEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"listTypeEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ListTypeEntry newListTypeEntry = addListTypeEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newListTypeEntry.getPrimaryKey()));
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

		ListTypeEntry newListTypeEntry = addListTypeEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ListTypeEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"listTypeEntryId", newListTypeEntry.getListTypeEntryId()));

		List<ListTypeEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ListTypeEntry listTypeEntry) {
		Assert.assertEquals(
			Long.valueOf(listTypeEntry.getListTypeDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				listTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "listTypeDefinitionId"));
		Assert.assertEquals(
			listTypeEntry.getKey(),
			ReflectionTestUtil.invoke(
				listTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			listTypeEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				listTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(listTypeEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				listTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(listTypeEntry.getListTypeDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				listTypeEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "listTypeDefinitionId"));
	}

	protected ListTypeEntry addListTypeEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListTypeEntry listTypeEntry = _persistence.create(pk);

		listTypeEntry.setUuid(RandomTestUtil.randomString());

		listTypeEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		listTypeEntry.setCompanyId(RandomTestUtil.nextLong());

		listTypeEntry.setUserId(RandomTestUtil.nextLong());

		listTypeEntry.setUserName(RandomTestUtil.randomString());

		listTypeEntry.setCreateDate(RandomTestUtil.nextDate());

		listTypeEntry.setModifiedDate(RandomTestUtil.nextDate());

		listTypeEntry.setListTypeDefinitionId(RandomTestUtil.nextLong());

		listTypeEntry.setKey(RandomTestUtil.randomString());

		listTypeEntry.setName(RandomTestUtil.randomString());

		listTypeEntry.setSystem(RandomTestUtil.randomBoolean());

		listTypeEntry.setType(RandomTestUtil.randomString());

		listTypeEntry.setStatus(RandomTestUtil.nextInt());

		_listTypeEntries.add(_persistence.update(listTypeEntry));

		return listTypeEntry;
	}

	private List<ListTypeEntry> _listTypeEntries =
		new ArrayList<ListTypeEntry>();
	private ListTypeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1261078717