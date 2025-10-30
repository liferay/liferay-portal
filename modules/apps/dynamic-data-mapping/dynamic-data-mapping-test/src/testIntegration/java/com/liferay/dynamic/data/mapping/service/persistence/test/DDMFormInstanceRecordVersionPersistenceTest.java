/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordVersionException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordVersionPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordVersionUtil;
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
public class DDMFormInstanceRecordVersionPersistenceTest {

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
		_persistence = DDMFormInstanceRecordVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMFormInstanceRecordVersion> iterator =
			_ddmFormInstanceRecordVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			_persistence.create(pk);

		Assert.assertNotNull(ddmFormInstanceRecordVersion);

		Assert.assertEquals(ddmFormInstanceRecordVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		_persistence.remove(newDDMFormInstanceRecordVersion);

		DDMFormInstanceRecordVersion existingDDMFormInstanceRecordVersion =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceRecordVersion.getPrimaryKey());

		Assert.assertNull(existingDDMFormInstanceRecordVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMFormInstanceRecordVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			_persistence.create(pk);

		newDDMFormInstanceRecordVersion.setMvccVersion(
			RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setCtCollectionId(
			RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setGroupId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setCompanyId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setUserId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setUserName(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecordVersion.setCreateDate(
			RandomTestUtil.nextDate());

		newDDMFormInstanceRecordVersion.setFormInstanceId(
			RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setFormInstanceVersion(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecordVersion.setFormInstanceRecordId(
			RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setVersion(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecordVersion.setStorageId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setStatus(RandomTestUtil.nextInt());

		newDDMFormInstanceRecordVersion.setStatusByUserId(
			RandomTestUtil.nextLong());

		newDDMFormInstanceRecordVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecordVersion.setStatusDate(
			RandomTestUtil.nextDate());

		_ddmFormInstanceRecordVersions.add(
			_persistence.update(newDDMFormInstanceRecordVersion));

		DDMFormInstanceRecordVersion existingDDMFormInstanceRecordVersion =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecordVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getMvccVersion(),
			newDDMFormInstanceRecordVersion.getMvccVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getCtCollectionId(),
			newDDMFormInstanceRecordVersion.getCtCollectionId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.
				getFormInstanceRecordVersionId(),
			newDDMFormInstanceRecordVersion.getFormInstanceRecordVersionId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getGroupId(),
			newDDMFormInstanceRecordVersion.getGroupId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getCompanyId(),
			newDDMFormInstanceRecordVersion.getCompanyId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getUserId(),
			newDDMFormInstanceRecordVersion.getUserId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getUserName(),
			newDDMFormInstanceRecordVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceRecordVersion.getCreateDate()),
			Time.getShortTimestamp(
				newDDMFormInstanceRecordVersion.getCreateDate()));
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getFormInstanceId(),
			newDDMFormInstanceRecordVersion.getFormInstanceId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getFormInstanceVersion(),
			newDDMFormInstanceRecordVersion.getFormInstanceVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getFormInstanceRecordId(),
			newDDMFormInstanceRecordVersion.getFormInstanceRecordId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getVersion(),
			newDDMFormInstanceRecordVersion.getVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getStorageId(),
			newDDMFormInstanceRecordVersion.getStorageId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getStatus(),
			newDDMFormInstanceRecordVersion.getStatus());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getStatusByUserId(),
			newDDMFormInstanceRecordVersion.getStatusByUserId());
		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion.getStatusByUserName(),
			newDDMFormInstanceRecordVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceRecordVersion.getStatusDate()),
			Time.getShortTimestamp(
				newDDMFormInstanceRecordVersion.getStatusDate()));
	}

	@Test
	public void testCountByFormInstanceRecordId() throws Exception {
		_persistence.countByFormInstanceRecordId(RandomTestUtil.nextLong());

		_persistence.countByFormInstanceRecordId(0L);
	}

	@Test
	public void testCountByU_F() throws Exception {
		_persistence.countByU_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_F(0L, 0L);
	}

	@Test
	public void testCountByF_F() throws Exception {
		_persistence.countByF_F(RandomTestUtil.nextLong(), "");

		_persistence.countByF_F(0L, "null");

		_persistence.countByF_F(0L, (String)null);
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
	public void testCountByU_F_F_S() throws Exception {
		_persistence.countByU_F_F_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByU_F_F_S(0L, 0L, "null", 0);

		_persistence.countByU_F_F_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		DDMFormInstanceRecordVersion existingDDMFormInstanceRecordVersion =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecordVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion,
			newDDMFormInstanceRecordVersion);
	}

	@Test(expected = NoSuchFormInstanceRecordVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMFormInstanceRecordVersion>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"DDMFormInstanceRecordVersion", "mvccVersion", true,
			"ctCollectionId", true, "formInstanceRecordVersionId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "formInstanceId", true,
			"formInstanceVersion", true, "formInstanceRecordId", true,
			"version", true, "storageId", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		DDMFormInstanceRecordVersion existingDDMFormInstanceRecordVersion =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceRecordVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecordVersion,
			newDDMFormInstanceRecordVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecordVersion missingDDMFormInstanceRecordVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMFormInstanceRecordVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion1 =
			addDDMFormInstanceRecordVersion();
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion2 =
			addDDMFormInstanceRecordVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecordVersion1.getPrimaryKey());
		primaryKeys.add(newDDMFormInstanceRecordVersion2.getPrimaryKey());

		Map<Serializable, DDMFormInstanceRecordVersion>
			ddmFormInstanceRecordVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, ddmFormInstanceRecordVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceRecordVersion1,
			ddmFormInstanceRecordVersions.get(
				newDDMFormInstanceRecordVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMFormInstanceRecordVersion2,
			ddmFormInstanceRecordVersions.get(
				newDDMFormInstanceRecordVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMFormInstanceRecordVersion>
			ddmFormInstanceRecordVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(ddmFormInstanceRecordVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecordVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMFormInstanceRecordVersion>
			ddmFormInstanceRecordVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceRecordVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceRecordVersion,
			ddmFormInstanceRecordVersions.get(
				newDDMFormInstanceRecordVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMFormInstanceRecordVersion>
			ddmFormInstanceRecordVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(ddmFormInstanceRecordVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecordVersion.getPrimaryKey());

		Map<Serializable, DDMFormInstanceRecordVersion>
			ddmFormInstanceRecordVersions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceRecordVersions.size());
		Assert.assertEquals(
			newDDMFormInstanceRecordVersion,
			ddmFormInstanceRecordVersions.get(
				newDDMFormInstanceRecordVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMFormInstanceRecordVersion newDDMFormInstanceRecordVersion =
			addDDMFormInstanceRecordVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecordVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion) {

		Assert.assertEquals(
			Long.valueOf(
				ddmFormInstanceRecordVersion.getFormInstanceRecordId()),
			ReflectionTestUtil.<Long>invoke(
				ddmFormInstanceRecordVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "formInstanceRecordId"));
		Assert.assertEquals(
			ddmFormInstanceRecordVersion.getVersion(),
			ReflectionTestUtil.invoke(
				ddmFormInstanceRecordVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DDMFormInstanceRecordVersion addDDMFormInstanceRecordVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
			_persistence.create(pk);

		ddmFormInstanceRecordVersion.setMvccVersion(RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setCtCollectionId(
			RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setGroupId(RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setCompanyId(RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setUserId(RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setUserName(RandomTestUtil.randomString());

		ddmFormInstanceRecordVersion.setCreateDate(RandomTestUtil.nextDate());

		ddmFormInstanceRecordVersion.setFormInstanceId(
			RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setFormInstanceVersion(
			RandomTestUtil.randomString());

		ddmFormInstanceRecordVersion.setFormInstanceRecordId(
			RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setVersion(RandomTestUtil.randomString());

		ddmFormInstanceRecordVersion.setStorageId(RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setStatus(RandomTestUtil.nextInt());

		ddmFormInstanceRecordVersion.setStatusByUserId(
			RandomTestUtil.nextLong());

		ddmFormInstanceRecordVersion.setStatusByUserName(
			RandomTestUtil.randomString());

		ddmFormInstanceRecordVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddmFormInstanceRecordVersions.add(
			_persistence.update(ddmFormInstanceRecordVersion));

		return ddmFormInstanceRecordVersion;
	}

	private List<DDMFormInstanceRecordVersion> _ddmFormInstanceRecordVersions =
		new ArrayList<DDMFormInstanceRecordVersion>();
	private DDMFormInstanceRecordVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}