/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryException;
import com.liferay.portal.tools.service.builder.test.model.LVEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryUtil;

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
public class LVEntryPersistenceTest {

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
		_persistence = LVEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LVEntry> iterator = _lvEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntry lvEntry = _persistence.create(pk);

		Assert.assertNotNull(lvEntry);

		Assert.assertEquals(lvEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LVEntry newLVEntry = addLVEntry();

		_persistence.remove(newLVEntry);

		LVEntry existingLVEntry = _persistence.fetchByPrimaryKey(
			newLVEntry.getPrimaryKey());

		Assert.assertNull(existingLVEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLVEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntry newLVEntry = _persistence.create(pk);

		newLVEntry.setMvccVersion(RandomTestUtil.nextLong());

		newLVEntry.setUuid(RandomTestUtil.randomString());

		newLVEntry.setHeadId(RandomTestUtil.nextLong());

		newLVEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		newLVEntry.setCompanyId(RandomTestUtil.nextLong());

		newLVEntry.setGroupId(RandomTestUtil.nextLong());

		newLVEntry.setUniqueGroupKey(RandomTestUtil.randomString());

		_lvEntries.add(_persistence.update(newLVEntry));

		LVEntry existingLVEntry = _persistence.findByPrimaryKey(
			newLVEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntry.getMvccVersion(), newLVEntry.getMvccVersion());
		Assert.assertEquals(existingLVEntry.getUuid(), newLVEntry.getUuid());
		Assert.assertEquals(
			existingLVEntry.getHeadId(), newLVEntry.getHeadId());
		Assert.assertEquals(
			existingLVEntry.getDefaultLanguageId(),
			newLVEntry.getDefaultLanguageId());
		Assert.assertEquals(
			existingLVEntry.getLvEntryId(), newLVEntry.getLvEntryId());
		Assert.assertEquals(
			existingLVEntry.getCompanyId(), newLVEntry.getCompanyId());
		Assert.assertEquals(
			existingLVEntry.getGroupId(), newLVEntry.getGroupId());
		Assert.assertEquals(
			existingLVEntry.getUniqueGroupKey(),
			newLVEntry.getUniqueGroupKey());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Head() throws Exception {
		_persistence.countByUuid_Head("", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head("null", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head(
			(String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Head() throws Exception {
		_persistence.countByUUID_G_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Head() throws Exception {
		_persistence.countByUuid_C_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByGroupId_Head() throws Exception {
		_persistence.countByGroupId_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByGroupId_Head(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByGroupId_HeadArrayable() throws Exception {
		_persistence.countByGroupId_Head(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_UGK() throws Exception {
		_persistence.countByG_UGK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_UGK(0L, "null");

		_persistence.countByG_UGK(0L, (String)null);
	}

	@Test
	public void testCountByG_UGK_Head() throws Exception {
		_persistence.countByG_UGK_Head(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_UGK_Head(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_UGK_Head(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LVEntry newLVEntry = addLVEntry();

		LVEntry existingLVEntry = _persistence.findByPrimaryKey(
			newLVEntry.getPrimaryKey());

		Assert.assertEquals(existingLVEntry, newLVEntry);
	}

	@Test(expected = NoSuchLVEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LVEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LVEntry", "mvccVersion", true, "uuid", true, "headId", true,
			"defaultLanguageId", true, "lvEntryId", true, "companyId", true,
			"groupId", true, "uniqueGroupKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LVEntry newLVEntry = addLVEntry();

		LVEntry existingLVEntry = _persistence.fetchByPrimaryKey(
			newLVEntry.getPrimaryKey());

		Assert.assertEquals(existingLVEntry, newLVEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntry missingLVEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLVEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LVEntry newLVEntry1 = addLVEntry();
		LVEntry newLVEntry2 = addLVEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntry1.getPrimaryKey());
		primaryKeys.add(newLVEntry2.getPrimaryKey());

		Map<Serializable, LVEntry> lvEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, lvEntries.size());
		Assert.assertEquals(
			newLVEntry1, lvEntries.get(newLVEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newLVEntry2, lvEntries.get(newLVEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LVEntry> lvEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(lvEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LVEntry newLVEntry = addLVEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LVEntry> lvEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, lvEntries.size());
		Assert.assertEquals(
			newLVEntry, lvEntries.get(newLVEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LVEntry> lvEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(lvEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LVEntry newLVEntry = addLVEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntry.getPrimaryKey());

		Map<Serializable, LVEntry> lvEntries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, lvEntries.size());
		Assert.assertEquals(
			newLVEntry, lvEntries.get(newLVEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LVEntry newLVEntry = addLVEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLVEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(LVEntry lvEntry) {
		Assert.assertEquals(
			lvEntry.getUuid(),
			ReflectionTestUtil.invoke(
				lvEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(lvEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(lvEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			lvEntry.getUniqueGroupKey(),
			ReflectionTestUtil.invoke(
				lvEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uniqueGroupKey"));

		Assert.assertEquals(
			Long.valueOf(lvEntry.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected LVEntry addLVEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntry lvEntry = _persistence.create(pk);

		lvEntry.setMvccVersion(RandomTestUtil.nextLong());

		lvEntry.setUuid(RandomTestUtil.randomString());

		lvEntry.setHeadId(-pk);

		lvEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		lvEntry.setCompanyId(RandomTestUtil.nextLong());

		lvEntry.setGroupId(RandomTestUtil.nextLong());

		lvEntry.setUniqueGroupKey(RandomTestUtil.randomString());

		_lvEntries.add(_persistence.update(lvEntry));

		return lvEntry;
	}

	private List<LVEntry> _lvEntries = new ArrayList<LVEntry>();
	private LVEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}