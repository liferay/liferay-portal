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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryVersionException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryVersion;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryVersionUtil;

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
public class LVEntryVersionPersistenceTest {

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
		_persistence = LVEntryVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LVEntryVersion> iterator = _lvEntryVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryVersion lvEntryVersion = _persistence.create(pk);

		Assert.assertNotNull(lvEntryVersion);

		Assert.assertEquals(lvEntryVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		_persistence.remove(newLVEntryVersion);

		LVEntryVersion existingLVEntryVersion = _persistence.fetchByPrimaryKey(
			newLVEntryVersion.getPrimaryKey());

		Assert.assertNull(existingLVEntryVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLVEntryVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryVersion newLVEntryVersion = _persistence.create(pk);

		newLVEntryVersion.setVersion(RandomTestUtil.nextInt());

		newLVEntryVersion.setUuid(RandomTestUtil.randomString());

		newLVEntryVersion.setDefaultLanguageId(RandomTestUtil.randomString());

		newLVEntryVersion.setLvEntryId(RandomTestUtil.nextLong());

		newLVEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		newLVEntryVersion.setGroupId(RandomTestUtil.nextLong());

		newLVEntryVersion.setUniqueGroupKey(RandomTestUtil.randomString());

		_lvEntryVersions.add(_persistence.update(newLVEntryVersion));

		LVEntryVersion existingLVEntryVersion = _persistence.findByPrimaryKey(
			newLVEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryVersion.getLvEntryVersionId(),
			newLVEntryVersion.getLvEntryVersionId());
		Assert.assertEquals(
			existingLVEntryVersion.getVersion(),
			newLVEntryVersion.getVersion());
		Assert.assertEquals(
			existingLVEntryVersion.getUuid(), newLVEntryVersion.getUuid());
		Assert.assertEquals(
			existingLVEntryVersion.getDefaultLanguageId(),
			newLVEntryVersion.getDefaultLanguageId());
		Assert.assertEquals(
			existingLVEntryVersion.getLvEntryId(),
			newLVEntryVersion.getLvEntryId());
		Assert.assertEquals(
			existingLVEntryVersion.getCompanyId(),
			newLVEntryVersion.getCompanyId());
		Assert.assertEquals(
			existingLVEntryVersion.getGroupId(),
			newLVEntryVersion.getGroupId());
		Assert.assertEquals(
			existingLVEntryVersion.getUniqueGroupKey(),
			newLVEntryVersion.getUniqueGroupKey());
	}

	@Test
	public void testCountByLvEntryId() throws Exception {
		_persistence.countByLvEntryId(RandomTestUtil.nextLong());

		_persistence.countByLvEntryId(0L);
	}

	@Test
	public void testCountByLvEntryId_Version() throws Exception {
		_persistence.countByLvEntryId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByLvEntryId_Version(0L, 0);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Version() throws Exception {
		_persistence.countByUuid_Version("", RandomTestUtil.nextInt());

		_persistence.countByUuid_Version("null", 0);

		_persistence.countByUuid_Version((String)null, 0);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Version() throws Exception {
		_persistence.countByUUID_G_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUUID_G_Version("null", 0L, 0);

		_persistence.countByUUID_G_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Version() throws Exception {
		_persistence.countByUuid_C_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUuid_C_Version("null", 0L, 0);

		_persistence.countByUuid_C_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Version() throws Exception {
		_persistence.countByGroupId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByGroupId_Version(0L, 0);
	}

	@Test
	public void testCountByG_UGK() throws Exception {
		_persistence.countByG_UGK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_UGK(0L, "null");

		_persistence.countByG_UGK(0L, (String)null);
	}

	@Test
	public void testCountByG_UGK_Version() throws Exception {
		_persistence.countByG_UGK_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_UGK_Version(0L, "null", 0);

		_persistence.countByG_UGK_Version(0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		LVEntryVersion existingLVEntryVersion = _persistence.findByPrimaryKey(
			newLVEntryVersion.getPrimaryKey());

		Assert.assertEquals(existingLVEntryVersion, newLVEntryVersion);
	}

	@Test(expected = NoSuchLVEntryVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LVEntryVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LVEntryVersion", "lvEntryVersionId", true, "version", true, "uuid",
			true, "defaultLanguageId", true, "lvEntryId", true, "companyId",
			true, "groupId", true, "uniqueGroupKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		LVEntryVersion existingLVEntryVersion = _persistence.fetchByPrimaryKey(
			newLVEntryVersion.getPrimaryKey());

		Assert.assertEquals(existingLVEntryVersion, newLVEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryVersion missingLVEntryVersion = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingLVEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LVEntryVersion newLVEntryVersion1 = addLVEntryVersion();
		LVEntryVersion newLVEntryVersion2 = addLVEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryVersion1.getPrimaryKey());
		primaryKeys.add(newLVEntryVersion2.getPrimaryKey());

		Map<Serializable, LVEntryVersion> lvEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, lvEntryVersions.size());
		Assert.assertEquals(
			newLVEntryVersion1,
			lvEntryVersions.get(newLVEntryVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newLVEntryVersion2,
			lvEntryVersions.get(newLVEntryVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LVEntryVersion> lvEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lvEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LVEntryVersion> lvEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lvEntryVersions.size());
		Assert.assertEquals(
			newLVEntryVersion,
			lvEntryVersions.get(newLVEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LVEntryVersion> lvEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lvEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryVersion.getPrimaryKey());

		Map<Serializable, LVEntryVersion> lvEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lvEntryVersions.size());
		Assert.assertEquals(
			newLVEntryVersion,
			lvEntryVersions.get(newLVEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LVEntryVersion newLVEntryVersion = addLVEntryVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLVEntryVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(LVEntryVersion lvEntryVersion) {
		Assert.assertEquals(
			Long.valueOf(lvEntryVersion.getLvEntryId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "lvEntryId"));
		Assert.assertEquals(
			Integer.valueOf(lvEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			lvEntryVersion.getUuid(),
			ReflectionTestUtil.invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(lvEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Integer.valueOf(lvEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(lvEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			lvEntryVersion.getUniqueGroupKey(),
			ReflectionTestUtil.invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uniqueGroupKey"));
		Assert.assertEquals(
			Integer.valueOf(lvEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				lvEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected LVEntryVersion addLVEntryVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryVersion lvEntryVersion = _persistence.create(pk);

		lvEntryVersion.setVersion(RandomTestUtil.nextInt());

		lvEntryVersion.setUuid(RandomTestUtil.randomString());

		lvEntryVersion.setDefaultLanguageId(RandomTestUtil.randomString());

		lvEntryVersion.setLvEntryId(RandomTestUtil.nextLong());

		lvEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		lvEntryVersion.setGroupId(RandomTestUtil.nextLong());

		lvEntryVersion.setUniqueGroupKey(RandomTestUtil.randomString());

		_lvEntryVersions.add(_persistence.update(lvEntryVersion));

		return lvEntryVersion;
	}

	private List<LVEntryVersion> _lvEntryVersions =
		new ArrayList<LVEntryVersion>();
	private LVEntryVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}