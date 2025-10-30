/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceVersionUtil;
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
public class DDMFormInstanceVersionPersistenceTest {

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
		_persistence = DDMFormInstanceVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMFormInstanceVersion> iterator =
			_ddmFormInstanceVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceVersion ddmFormInstanceVersion = _persistence.create(pk);

		Assert.assertNotNull(ddmFormInstanceVersion);

		Assert.assertEquals(ddmFormInstanceVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		_persistence.remove(newDDMFormInstanceVersion);

		DDMFormInstanceVersion existingDDMFormInstanceVersion =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceVersion.getPrimaryKey());

		Assert.assertNull(existingDDMFormInstanceVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMFormInstanceVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceVersion newDDMFormInstanceVersion = _persistence.create(
			pk);

		newDDMFormInstanceVersion.setMvccVersion(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setGroupId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setCompanyId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setUserId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setUserName(RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setCreateDate(RandomTestUtil.nextDate());

		newDDMFormInstanceVersion.setFormInstanceId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setStructureVersionId(
			RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setName(RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setDescription(RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setSettings(RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setVersion(RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setStatus(RandomTestUtil.nextInt());

		newDDMFormInstanceVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newDDMFormInstanceVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newDDMFormInstanceVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddmFormInstanceVersions.add(
			_persistence.update(newDDMFormInstanceVersion));

		DDMFormInstanceVersion existingDDMFormInstanceVersion =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceVersion.getMvccVersion(),
			newDDMFormInstanceVersion.getMvccVersion());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getCtCollectionId(),
			newDDMFormInstanceVersion.getCtCollectionId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getFormInstanceVersionId(),
			newDDMFormInstanceVersion.getFormInstanceVersionId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getGroupId(),
			newDDMFormInstanceVersion.getGroupId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getCompanyId(),
			newDDMFormInstanceVersion.getCompanyId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getUserId(),
			newDDMFormInstanceVersion.getUserId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getUserName(),
			newDDMFormInstanceVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceVersion.getCreateDate()),
			Time.getShortTimestamp(newDDMFormInstanceVersion.getCreateDate()));
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getFormInstanceId(),
			newDDMFormInstanceVersion.getFormInstanceId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getStructureVersionId(),
			newDDMFormInstanceVersion.getStructureVersionId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getName(),
			newDDMFormInstanceVersion.getName());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getDescription(),
			newDDMFormInstanceVersion.getDescription());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getSettings(),
			newDDMFormInstanceVersion.getSettings());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getVersion(),
			newDDMFormInstanceVersion.getVersion());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getStatus(),
			newDDMFormInstanceVersion.getStatus());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getStatusByUserId(),
			newDDMFormInstanceVersion.getStatusByUserId());
		Assert.assertEquals(
			existingDDMFormInstanceVersion.getStatusByUserName(),
			newDDMFormInstanceVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceVersion.getStatusDate()),
			Time.getShortTimestamp(newDDMFormInstanceVersion.getStatusDate()));
	}

	@Test
	public void testCountByFormInstanceId() throws Exception {
		_persistence.countByFormInstanceId(RandomTestUtil.nextLong());

		_persistence.countByFormInstanceId(0L);
	}

	@Test
	public void testCountByF_V() throws Exception {
		_persistence.countByF_V(RandomTestUtil.nextLong(), "");

		_persistence.countByF_V(0L, "null");

		_persistence.countByF_V(0L, (String)null);
	}

	@Test
	public void testCountByF_S() throws Exception {
		_persistence.countByF_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByF_S(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		DDMFormInstanceVersion existingDDMFormInstanceVersion =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceVersion, newDDMFormInstanceVersion);
	}

	@Test(expected = NoSuchFormInstanceVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMFormInstanceVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMFormInstanceVersion", "mvccVersion", true, "ctCollectionId",
			true, "formInstanceVersionId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"formInstanceId", true, "structureVersionId", true, "name", true,
			"version", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		DDMFormInstanceVersion existingDDMFormInstanceVersion =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceVersion, newDDMFormInstanceVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceVersion missingDDMFormInstanceVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMFormInstanceVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMFormInstanceVersion newDDMFormInstanceVersion1 =
			addDDMFormInstanceVersion();
		DDMFormInstanceVersion newDDMFormInstanceVersion2 =
			addDDMFormInstanceVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceVersion1.getPrimaryKey());
		primaryKeys.add(newDDMFormInstanceVersion2.getPrimaryKey());

		Map<Serializable, DDMFormInstanceVersion> ddmFormInstanceVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmFormInstanceVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceVersion1,
			ddmFormInstanceVersions.get(
				newDDMFormInstanceVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMFormInstanceVersion2,
			ddmFormInstanceVersions.get(
				newDDMFormInstanceVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMFormInstanceVersion> ddmFormInstanceVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMFormInstanceVersion> ddmFormInstanceVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceVersion,
			ddmFormInstanceVersions.get(
				newDDMFormInstanceVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMFormInstanceVersion> ddmFormInstanceVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceVersion.getPrimaryKey());

		Map<Serializable, DDMFormInstanceVersion> ddmFormInstanceVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceVersion,
			ddmFormInstanceVersions.get(
				newDDMFormInstanceVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMFormInstanceVersion newDDMFormInstanceVersion =
			addDDMFormInstanceVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMFormInstanceVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMFormInstanceVersion ddmFormInstanceVersion) {

		Assert.assertEquals(
			Long.valueOf(ddmFormInstanceVersion.getFormInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				ddmFormInstanceVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "formInstanceId"));
		Assert.assertEquals(
			ddmFormInstanceVersion.getVersion(),
			ReflectionTestUtil.invoke(
				ddmFormInstanceVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DDMFormInstanceVersion addDDMFormInstanceVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceVersion ddmFormInstanceVersion = _persistence.create(pk);

		ddmFormInstanceVersion.setMvccVersion(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setCtCollectionId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setGroupId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setCompanyId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setUserId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setUserName(RandomTestUtil.randomString());

		ddmFormInstanceVersion.setCreateDate(RandomTestUtil.nextDate());

		ddmFormInstanceVersion.setFormInstanceId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setStructureVersionId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setName(RandomTestUtil.randomString());

		ddmFormInstanceVersion.setDescription(RandomTestUtil.randomString());

		ddmFormInstanceVersion.setSettings(RandomTestUtil.randomString());

		ddmFormInstanceVersion.setVersion(RandomTestUtil.randomString());

		ddmFormInstanceVersion.setStatus(RandomTestUtil.nextInt());

		ddmFormInstanceVersion.setStatusByUserId(RandomTestUtil.nextLong());

		ddmFormInstanceVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		ddmFormInstanceVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddmFormInstanceVersions.add(
			_persistence.update(ddmFormInstanceVersion));

		return ddmFormInstanceVersion;
	}

	private List<DDMFormInstanceVersion> _ddmFormInstanceVersions =
		new ArrayList<DDMFormInstanceVersion>();
	private DDMFormInstanceVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}