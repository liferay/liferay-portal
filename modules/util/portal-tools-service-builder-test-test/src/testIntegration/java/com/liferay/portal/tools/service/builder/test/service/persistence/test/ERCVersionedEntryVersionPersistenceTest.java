/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchERCVersionedEntryVersionException;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntryVersion;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryVersionPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryVersionUtil;

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
public class ERCVersionedEntryVersionPersistenceTest {

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
		_persistence = ERCVersionedEntryVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ERCVersionedEntryVersion> iterator =
			_ercVersionedEntryVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntryVersion ercVersionedEntryVersion = _persistence.create(
			pk);

		Assert.assertNotNull(ercVersionedEntryVersion);

		Assert.assertEquals(ercVersionedEntryVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		_persistence.remove(newERCVersionedEntryVersion);

		ERCVersionedEntryVersion existingERCVersionedEntryVersion =
			_persistence.fetchByPrimaryKey(
				newERCVersionedEntryVersion.getPrimaryKey());

		Assert.assertNull(existingERCVersionedEntryVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addERCVersionedEntryVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			_persistence.create(pk);

		newERCVersionedEntryVersion.setVersion(RandomTestUtil.nextInt());

		newERCVersionedEntryVersion.setUuid(RandomTestUtil.randomString());

		newERCVersionedEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newERCVersionedEntryVersion.setErcVersionedEntryId(
			RandomTestUtil.nextLong());

		newERCVersionedEntryVersion.setGroupId(RandomTestUtil.nextLong());

		newERCVersionedEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		_ercVersionedEntryVersions.add(
			_persistence.update(newERCVersionedEntryVersion));

		ERCVersionedEntryVersion existingERCVersionedEntryVersion =
			_persistence.findByPrimaryKey(
				newERCVersionedEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingERCVersionedEntryVersion.getErcVersionedEntryVersionId(),
			newERCVersionedEntryVersion.getErcVersionedEntryVersionId());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getVersion(),
			newERCVersionedEntryVersion.getVersion());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getUuid(),
			newERCVersionedEntryVersion.getUuid());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getExternalReferenceCode(),
			newERCVersionedEntryVersion.getExternalReferenceCode());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getErcVersionedEntryId(),
			newERCVersionedEntryVersion.getErcVersionedEntryId());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getGroupId(),
			newERCVersionedEntryVersion.getGroupId());
		Assert.assertEquals(
			existingERCVersionedEntryVersion.getCompanyId(),
			newERCVersionedEntryVersion.getCompanyId());
	}

	@Test
	public void testCountByErcVersionedEntryId() throws Exception {
		_persistence.countByErcVersionedEntryId(RandomTestUtil.nextLong());

		_persistence.countByErcVersionedEntryId(0L);
	}

	@Test
	public void testCountByErcVersionedEntryId_Version() throws Exception {
		_persistence.countByErcVersionedEntryId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByErcVersionedEntryId_Version(0L, 0);
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		ERCVersionedEntryVersion existingERCVersionedEntryVersion =
			_persistence.findByPrimaryKey(
				newERCVersionedEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingERCVersionedEntryVersion, newERCVersionedEntryVersion);
	}

	@Test(expected = NoSuchERCVersionedEntryVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ERCVersionedEntryVersion>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"ERCVersionedEntryVersion", "ercVersionedEntryVersionId", true,
			"version", true, "uuid", true, "externalReferenceCode", true,
			"ercVersionedEntryId", true, "groupId", true, "companyId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		ERCVersionedEntryVersion existingERCVersionedEntryVersion =
			_persistence.fetchByPrimaryKey(
				newERCVersionedEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingERCVersionedEntryVersion, newERCVersionedEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntryVersion missingERCVersionedEntryVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingERCVersionedEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ERCVersionedEntryVersion newERCVersionedEntryVersion1 =
			addERCVersionedEntryVersion();
		ERCVersionedEntryVersion newERCVersionedEntryVersion2 =
			addERCVersionedEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntryVersion1.getPrimaryKey());
		primaryKeys.add(newERCVersionedEntryVersion2.getPrimaryKey());

		Map<Serializable, ERCVersionedEntryVersion> ercVersionedEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ercVersionedEntryVersions.size());
		Assert.assertEquals(
			newERCVersionedEntryVersion1,
			ercVersionedEntryVersions.get(
				newERCVersionedEntryVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newERCVersionedEntryVersion2,
			ercVersionedEntryVersions.get(
				newERCVersionedEntryVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ERCVersionedEntryVersion> ercVersionedEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercVersionedEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntryVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ERCVersionedEntryVersion> ercVersionedEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercVersionedEntryVersions.size());
		Assert.assertEquals(
			newERCVersionedEntryVersion,
			ercVersionedEntryVersions.get(
				newERCVersionedEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ERCVersionedEntryVersion> ercVersionedEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercVersionedEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntryVersion.getPrimaryKey());

		Map<Serializable, ERCVersionedEntryVersion> ercVersionedEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercVersionedEntryVersions.size());
		Assert.assertEquals(
			newERCVersionedEntryVersion,
			ercVersionedEntryVersions.get(
				newERCVersionedEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ERCVersionedEntryVersion newERCVersionedEntryVersion =
			addERCVersionedEntryVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newERCVersionedEntryVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		ERCVersionedEntryVersion ercVersionedEntryVersion) {

		Assert.assertEquals(
			Long.valueOf(ercVersionedEntryVersion.getErcVersionedEntryId()),
			ReflectionTestUtil.<Long>invoke(
				ercVersionedEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ercVersionedEntryId"));
		Assert.assertEquals(
			Integer.valueOf(ercVersionedEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				ercVersionedEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			ercVersionedEntryVersion.getUuid(),
			ReflectionTestUtil.invoke(
				ercVersionedEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ercVersionedEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ercVersionedEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Integer.valueOf(ercVersionedEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				ercVersionedEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected ERCVersionedEntryVersion addERCVersionedEntryVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntryVersion ercVersionedEntryVersion = _persistence.create(
			pk);

		ercVersionedEntryVersion.setVersion(RandomTestUtil.nextInt());

		ercVersionedEntryVersion.setUuid(RandomTestUtil.randomString());

		ercVersionedEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		ercVersionedEntryVersion.setErcVersionedEntryId(
			RandomTestUtil.nextLong());

		ercVersionedEntryVersion.setGroupId(RandomTestUtil.nextLong());

		ercVersionedEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		_ercVersionedEntryVersions.add(
			_persistence.update(ercVersionedEntryVersion));

		return ercVersionedEntryVersion;
	}

	private List<ERCVersionedEntryVersion> _ercVersionedEntryVersions =
		new ArrayList<ERCVersionedEntryVersion>();
	private ERCVersionedEntryVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}