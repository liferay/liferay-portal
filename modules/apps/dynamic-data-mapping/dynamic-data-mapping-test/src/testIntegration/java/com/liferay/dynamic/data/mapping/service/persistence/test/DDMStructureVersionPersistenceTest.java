/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureVersionException;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureVersionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class DDMStructureVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMStructureVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStructureVersion> iterator =
			_ddmStructureVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureVersion ddmStructureVersion = _persistence.create(pk);

		Assert.assertNotNull(ddmStructureVersion);

		Assert.assertEquals(ddmStructureVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		_persistence.remove(newDDMStructureVersion);

		DDMStructureVersion existingDDMStructureVersion =
			_persistence.fetchByPrimaryKey(
				newDDMStructureVersion.getPrimaryKey());

		Assert.assertNull(existingDDMStructureVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStructureVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureVersion newDDMStructureVersion = _persistence.create(pk);

		newDDMStructureVersion.setMvccVersion(RandomTestUtil.nextLong());

		newDDMStructureVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setGroupId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setCompanyId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setUserId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setUserName(RandomTestUtil.randomString());

		newDDMStructureVersion.setCreateDate(RandomTestUtil.nextDate());

		newDDMStructureVersion.setStructureId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setVersion(RandomTestUtil.randomString());

		newDDMStructureVersion.setParentStructureId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setName(RandomTestUtil.randomString());

		newDDMStructureVersion.setDescription(RandomTestUtil.randomString());

		newDDMStructureVersion.setDefinition(RandomTestUtil.randomString());

		newDDMStructureVersion.setStorageType(RandomTestUtil.randomString());

		newDDMStructureVersion.setType(RandomTestUtil.nextInt());

		newDDMStructureVersion.setStatus(RandomTestUtil.nextInt());

		newDDMStructureVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newDDMStructureVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newDDMStructureVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddmStructureVersions.add(_persistence.update(newDDMStructureVersion));

		DDMStructureVersion existingDDMStructureVersion =
			_persistence.findByPrimaryKey(
				newDDMStructureVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureVersion.getMvccVersion(),
			newDDMStructureVersion.getMvccVersion());
		Assert.assertEquals(
			existingDDMStructureVersion.getCtCollectionId(),
			newDDMStructureVersion.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStructureVersion.getStructureVersionId(),
			newDDMStructureVersion.getStructureVersionId());
		Assert.assertEquals(
			existingDDMStructureVersion.getGroupId(),
			newDDMStructureVersion.getGroupId());
		Assert.assertEquals(
			existingDDMStructureVersion.getCompanyId(),
			newDDMStructureVersion.getCompanyId());
		Assert.assertEquals(
			existingDDMStructureVersion.getUserId(),
			newDDMStructureVersion.getUserId());
		Assert.assertEquals(
			existingDDMStructureVersion.getUserName(),
			newDDMStructureVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructureVersion.getCreateDate()),
			Time.getShortTimestamp(newDDMStructureVersion.getCreateDate()));
		Assert.assertEquals(
			existingDDMStructureVersion.getStructureId(),
			newDDMStructureVersion.getStructureId());
		Assert.assertEquals(
			existingDDMStructureVersion.getVersion(),
			newDDMStructureVersion.getVersion());
		Assert.assertEquals(
			existingDDMStructureVersion.getParentStructureId(),
			newDDMStructureVersion.getParentStructureId());
		Assert.assertEquals(
			existingDDMStructureVersion.getName(),
			newDDMStructureVersion.getName());
		Assert.assertEquals(
			existingDDMStructureVersion.getDescription(),
			newDDMStructureVersion.getDescription());
		Assert.assertEquals(
			existingDDMStructureVersion.getDefinition(),
			newDDMStructureVersion.getDefinition());
		Assert.assertEquals(
			existingDDMStructureVersion.getStorageType(),
			newDDMStructureVersion.getStorageType());
		Assert.assertEquals(
			existingDDMStructureVersion.getType(),
			newDDMStructureVersion.getType());
		Assert.assertEquals(
			existingDDMStructureVersion.getStatus(),
			newDDMStructureVersion.getStatus());
		Assert.assertEquals(
			existingDDMStructureVersion.getStatusByUserId(),
			newDDMStructureVersion.getStatusByUserId());
		Assert.assertEquals(
			existingDDMStructureVersion.getStatusByUserName(),
			newDDMStructureVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructureVersion.getStatusDate()),
			Time.getShortTimestamp(newDDMStructureVersion.getStatusDate()));
	}

	@Test
	public void testCountByStructureId() throws Exception {
		_persistence.countByStructureId(RandomTestUtil.nextLong());

		_persistence.countByStructureId(0L);
	}

	@Test
	public void testCountByS_V() throws Exception {
		_persistence.countByS_V(RandomTestUtil.nextLong(), "");

		_persistence.countByS_V(0L, "null");

		_persistence.countByS_V(0L, (String)null);
	}

	@Test
	public void testCountByS_S() throws Exception {
		_persistence.countByS_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByS_S(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		DDMStructureVersion existingDDMStructureVersion =
			_persistence.findByPrimaryKey(
				newDDMStructureVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureVersion, newDDMStructureVersion);
	}

	@Test(expected = NoSuchStructureVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStructureVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStructureVersion", "mvccVersion", true, "ctCollectionId", true,
			"structureVersionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true, "structureId",
			true, "version", true, "parentStructureId", true, "name", true,
			"storageType", true, "type", true, "status", true, "statusByUserId",
			true, "statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		DDMStructureVersion existingDDMStructureVersion =
			_persistence.fetchByPrimaryKey(
				newDDMStructureVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureVersion, newDDMStructureVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureVersion missingDDMStructureVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMStructureVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStructureVersion newDDMStructureVersion1 = addDDMStructureVersion();
		DDMStructureVersion newDDMStructureVersion2 = addDDMStructureVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureVersion1.getPrimaryKey());
		primaryKeys.add(newDDMStructureVersion2.getPrimaryKey());

		Map<Serializable, DDMStructureVersion> ddmStructureVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStructureVersions.size());
		Assert.assertEquals(
			newDDMStructureVersion1,
			ddmStructureVersions.get(newDDMStructureVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStructureVersion2,
			ddmStructureVersions.get(newDDMStructureVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStructureVersion> ddmStructureVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStructureVersion> ddmStructureVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureVersions.size());
		Assert.assertEquals(
			newDDMStructureVersion,
			ddmStructureVersions.get(newDDMStructureVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStructureVersion> ddmStructureVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureVersion.getPrimaryKey());

		Map<Serializable, DDMStructureVersion> ddmStructureVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureVersions.size());
		Assert.assertEquals(
			newDDMStructureVersion,
			ddmStructureVersions.get(newDDMStructureVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStructureVersion newDDMStructureVersion = addDDMStructureVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMStructureVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMStructureVersion ddmStructureVersion) {

		Assert.assertEquals(
			Long.valueOf(ddmStructureVersion.getStructureId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureId"));
		Assert.assertEquals(
			ddmStructureVersion.getVersion(),
			ReflectionTestUtil.invoke(
				ddmStructureVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DDMStructureVersion addDDMStructureVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureVersion ddmStructureVersion = _persistence.create(pk);

		ddmStructureVersion.setMvccVersion(RandomTestUtil.nextLong());

		ddmStructureVersion.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStructureVersion.setGroupId(RandomTestUtil.nextLong());

		ddmStructureVersion.setCompanyId(RandomTestUtil.nextLong());

		ddmStructureVersion.setUserId(RandomTestUtil.nextLong());

		ddmStructureVersion.setUserName(RandomTestUtil.randomString());

		ddmStructureVersion.setCreateDate(RandomTestUtil.nextDate());

		ddmStructureVersion.setStructureId(RandomTestUtil.nextLong());

		ddmStructureVersion.setVersion(RandomTestUtil.randomString());

		ddmStructureVersion.setParentStructureId(RandomTestUtil.nextLong());

		ddmStructureVersion.setName(RandomTestUtil.randomString());

		ddmStructureVersion.setDescription(RandomTestUtil.randomString());

		ddmStructureVersion.setDefinition(RandomTestUtil.randomString());

		ddmStructureVersion.setStorageType(RandomTestUtil.randomString());

		ddmStructureVersion.setType(RandomTestUtil.nextInt());

		ddmStructureVersion.setStatus(RandomTestUtil.nextInt());

		ddmStructureVersion.setStatusByUserId(RandomTestUtil.nextLong());

		ddmStructureVersion.setStatusByUserName(RandomTestUtil.randomString());

		ddmStructureVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddmStructureVersions.add(_persistence.update(ddmStructureVersion));

		return ddmStructureVersion;
	}

	private List<DDMStructureVersion> _ddmStructureVersions =
		new ArrayList<DDMStructureVersion>();
	private DDMStructureVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}