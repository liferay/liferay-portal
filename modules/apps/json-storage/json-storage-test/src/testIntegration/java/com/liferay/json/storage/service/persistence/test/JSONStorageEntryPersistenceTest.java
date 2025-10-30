/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.json.storage.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.json.storage.exception.NoSuchJSONStorageEntryException;
import com.liferay.json.storage.model.JSONStorageEntry;
import com.liferay.json.storage.service.persistence.JSONStorageEntryPersistence;
import com.liferay.json.storage.service.persistence.JSONStorageEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class JSONStorageEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.json.storage.service"));

	@Before
	public void setUp() {
		_persistence = JSONStorageEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JSONStorageEntry> iterator = _jsonStorageEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JSONStorageEntry jsonStorageEntry = _persistence.create(pk);

		Assert.assertNotNull(jsonStorageEntry);

		Assert.assertEquals(jsonStorageEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		_persistence.remove(newJSONStorageEntry);

		JSONStorageEntry existingJSONStorageEntry =
			_persistence.fetchByPrimaryKey(newJSONStorageEntry.getPrimaryKey());

		Assert.assertNull(existingJSONStorageEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJSONStorageEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JSONStorageEntry newJSONStorageEntry = _persistence.create(pk);

		newJSONStorageEntry.setMvccVersion(RandomTestUtil.nextLong());

		newJSONStorageEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newJSONStorageEntry.setCompanyId(RandomTestUtil.nextLong());

		newJSONStorageEntry.setClassNameId(RandomTestUtil.nextLong());

		newJSONStorageEntry.setClassPK(RandomTestUtil.nextLong());

		newJSONStorageEntry.setParentJSONStorageEntryId(
			RandomTestUtil.nextLong());

		newJSONStorageEntry.setIndex(RandomTestUtil.nextInt());

		newJSONStorageEntry.setKey(RandomTestUtil.randomString());

		newJSONStorageEntry.setType(RandomTestUtil.nextInt());

		newJSONStorageEntry.setValueLong(RandomTestUtil.nextLong());

		newJSONStorageEntry.setValueString(RandomTestUtil.randomString());

		_jsonStorageEntries.add(_persistence.update(newJSONStorageEntry));

		JSONStorageEntry existingJSONStorageEntry =
			_persistence.findByPrimaryKey(newJSONStorageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingJSONStorageEntry.getMvccVersion(),
			newJSONStorageEntry.getMvccVersion());
		Assert.assertEquals(
			existingJSONStorageEntry.getCtCollectionId(),
			newJSONStorageEntry.getCtCollectionId());
		Assert.assertEquals(
			existingJSONStorageEntry.getJsonStorageEntryId(),
			newJSONStorageEntry.getJsonStorageEntryId());
		Assert.assertEquals(
			existingJSONStorageEntry.getCompanyId(),
			newJSONStorageEntry.getCompanyId());
		Assert.assertEquals(
			existingJSONStorageEntry.getClassNameId(),
			newJSONStorageEntry.getClassNameId());
		Assert.assertEquals(
			existingJSONStorageEntry.getClassPK(),
			newJSONStorageEntry.getClassPK());
		Assert.assertEquals(
			existingJSONStorageEntry.getParentJSONStorageEntryId(),
			newJSONStorageEntry.getParentJSONStorageEntryId());
		Assert.assertEquals(
			existingJSONStorageEntry.getIndex(),
			newJSONStorageEntry.getIndex());
		Assert.assertEquals(
			existingJSONStorageEntry.getKey(), newJSONStorageEntry.getKey());
		Assert.assertEquals(
			existingJSONStorageEntry.getType(), newJSONStorageEntry.getType());
		Assert.assertEquals(
			existingJSONStorageEntry.getValueLong(),
			newJSONStorageEntry.getValueLong());
		Assert.assertEquals(
			existingJSONStorageEntry.getValueString(),
			newJSONStorageEntry.getValueString());
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testCountByC_CN_I_T_VL() throws Exception {
		_persistence.countByC_CN_I_T_VL(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong());

		_persistence.countByC_CN_I_T_VL(0L, 0L, 0, 0, 0L);
	}

	@Test
	public void testCountByC_CN_K_T_VL() throws Exception {
		_persistence.countByC_CN_K_T_VL(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt(), RandomTestUtil.nextLong());

		_persistence.countByC_CN_K_T_VL(0L, 0L, "null", 0, 0L);

		_persistence.countByC_CN_K_T_VL(0L, 0L, (String)null, 0, 0L);
	}

	@Test
	public void testCountByCN_CPK_P_I_K() throws Exception {
		_persistence.countByCN_CPK_P_I_K(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "");

		_persistence.countByCN_CPK_P_I_K(0L, 0L, 0L, 0, "null");

		_persistence.countByCN_CPK_P_I_K(0L, 0L, 0L, 0, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		JSONStorageEntry existingJSONStorageEntry =
			_persistence.findByPrimaryKey(newJSONStorageEntry.getPrimaryKey());

		Assert.assertEquals(existingJSONStorageEntry, newJSONStorageEntry);
	}

	@Test(expected = NoSuchJSONStorageEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<JSONStorageEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JSONStorageEntry", "mvccVersion", true, "ctCollectionId", true,
			"jsonStorageEntryId", true, "companyId", true, "classNameId", true,
			"classPK", true, "parentJSONStorageEntryId", true, "index", true,
			"key", true, "type", true, "valueLong", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		JSONStorageEntry existingJSONStorageEntry =
			_persistence.fetchByPrimaryKey(newJSONStorageEntry.getPrimaryKey());

		Assert.assertEquals(existingJSONStorageEntry, newJSONStorageEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JSONStorageEntry missingJSONStorageEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJSONStorageEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JSONStorageEntry newJSONStorageEntry1 = addJSONStorageEntry();
		JSONStorageEntry newJSONStorageEntry2 = addJSONStorageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJSONStorageEntry1.getPrimaryKey());
		primaryKeys.add(newJSONStorageEntry2.getPrimaryKey());

		Map<Serializable, JSONStorageEntry> jsonStorageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, jsonStorageEntries.size());
		Assert.assertEquals(
			newJSONStorageEntry1,
			jsonStorageEntries.get(newJSONStorageEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newJSONStorageEntry2,
			jsonStorageEntries.get(newJSONStorageEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JSONStorageEntry> jsonStorageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(jsonStorageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJSONStorageEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JSONStorageEntry> jsonStorageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, jsonStorageEntries.size());
		Assert.assertEquals(
			newJSONStorageEntry,
			jsonStorageEntries.get(newJSONStorageEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JSONStorageEntry> jsonStorageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(jsonStorageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJSONStorageEntry.getPrimaryKey());

		Map<Serializable, JSONStorageEntry> jsonStorageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, jsonStorageEntries.size());
		Assert.assertEquals(
			newJSONStorageEntry,
			jsonStorageEntries.get(newJSONStorageEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JSONStorageEntry newJSONStorageEntry = addJSONStorageEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newJSONStorageEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(JSONStorageEntry jsonStorageEntry) {
		Assert.assertEquals(
			Long.valueOf(jsonStorageEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				jsonStorageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(jsonStorageEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				jsonStorageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(jsonStorageEntry.getParentJSONStorageEntryId()),
			ReflectionTestUtil.<Long>invoke(
				jsonStorageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentJSONStorageEntryId"));
		Assert.assertEquals(
			Integer.valueOf(jsonStorageEntry.getIndex()),
			ReflectionTestUtil.<Integer>invoke(
				jsonStorageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "index_"));
		Assert.assertEquals(
			jsonStorageEntry.getKey(),
			ReflectionTestUtil.invoke(
				jsonStorageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
	}

	protected JSONStorageEntry addJSONStorageEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JSONStorageEntry jsonStorageEntry = _persistence.create(pk);

		jsonStorageEntry.setMvccVersion(RandomTestUtil.nextLong());

		jsonStorageEntry.setCtCollectionId(RandomTestUtil.nextLong());

		jsonStorageEntry.setCompanyId(RandomTestUtil.nextLong());

		jsonStorageEntry.setClassNameId(RandomTestUtil.nextLong());

		jsonStorageEntry.setClassPK(RandomTestUtil.nextLong());

		jsonStorageEntry.setParentJSONStorageEntryId(RandomTestUtil.nextLong());

		jsonStorageEntry.setIndex(RandomTestUtil.nextInt());

		jsonStorageEntry.setKey(RandomTestUtil.randomString());

		jsonStorageEntry.setType(RandomTestUtil.nextInt());

		jsonStorageEntry.setValueLong(RandomTestUtil.nextLong());

		jsonStorageEntry.setValueString(RandomTestUtil.randomString());

		_jsonStorageEntries.add(_persistence.update(jsonStorageEntry));

		return jsonStorageEntry;
	}

	private List<JSONStorageEntry> _jsonStorageEntries =
		new ArrayList<JSONStorageEntry>();
	private JSONStorageEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}