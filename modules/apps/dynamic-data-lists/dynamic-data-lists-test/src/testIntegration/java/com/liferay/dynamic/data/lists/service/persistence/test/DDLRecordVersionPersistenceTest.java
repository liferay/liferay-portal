/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.exception.NoSuchRecordVersionException;
import com.liferay.dynamic.data.lists.model.DDLRecordVersion;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordVersionPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordVersionUtil;
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
public class DDLRecordVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.lists.service"));

	@Before
	public void setUp() {
		_persistence = DDLRecordVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDLRecordVersion> iterator = _ddlRecordVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordVersion ddlRecordVersion = _persistence.create(pk);

		Assert.assertNotNull(ddlRecordVersion);

		Assert.assertEquals(ddlRecordVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		_persistence.remove(newDDLRecordVersion);

		DDLRecordVersion existingDDLRecordVersion =
			_persistence.fetchByPrimaryKey(newDDLRecordVersion.getPrimaryKey());

		Assert.assertNull(existingDDLRecordVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDLRecordVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordVersion newDDLRecordVersion = _persistence.create(pk);

		newDDLRecordVersion.setMvccVersion(RandomTestUtil.nextLong());

		newDDLRecordVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setGroupId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setCompanyId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setUserId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setUserName(RandomTestUtil.randomString());

		newDDLRecordVersion.setCreateDate(RandomTestUtil.nextDate());

		newDDLRecordVersion.setDDMStorageId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setRecordSetId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setRecordSetVersion(RandomTestUtil.randomString());

		newDDLRecordVersion.setRecordId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setVersion(RandomTestUtil.randomString());

		newDDLRecordVersion.setDisplayIndex(RandomTestUtil.nextInt());

		newDDLRecordVersion.setStatus(RandomTestUtil.nextInt());

		newDDLRecordVersion.setStatusByUserId(RandomTestUtil.nextLong());

		newDDLRecordVersion.setStatusByUserName(RandomTestUtil.randomString());

		newDDLRecordVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddlRecordVersions.add(_persistence.update(newDDLRecordVersion));

		DDLRecordVersion existingDDLRecordVersion =
			_persistence.findByPrimaryKey(newDDLRecordVersion.getPrimaryKey());

		Assert.assertEquals(
			existingDDLRecordVersion.getMvccVersion(),
			newDDLRecordVersion.getMvccVersion());
		Assert.assertEquals(
			existingDDLRecordVersion.getCtCollectionId(),
			newDDLRecordVersion.getCtCollectionId());
		Assert.assertEquals(
			existingDDLRecordVersion.getRecordVersionId(),
			newDDLRecordVersion.getRecordVersionId());
		Assert.assertEquals(
			existingDDLRecordVersion.getGroupId(),
			newDDLRecordVersion.getGroupId());
		Assert.assertEquals(
			existingDDLRecordVersion.getCompanyId(),
			newDDLRecordVersion.getCompanyId());
		Assert.assertEquals(
			existingDDLRecordVersion.getUserId(),
			newDDLRecordVersion.getUserId());
		Assert.assertEquals(
			existingDDLRecordVersion.getUserName(),
			newDDLRecordVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecordVersion.getCreateDate()),
			Time.getShortTimestamp(newDDLRecordVersion.getCreateDate()));
		Assert.assertEquals(
			existingDDLRecordVersion.getDDMStorageId(),
			newDDLRecordVersion.getDDMStorageId());
		Assert.assertEquals(
			existingDDLRecordVersion.getRecordSetId(),
			newDDLRecordVersion.getRecordSetId());
		Assert.assertEquals(
			existingDDLRecordVersion.getRecordSetVersion(),
			newDDLRecordVersion.getRecordSetVersion());
		Assert.assertEquals(
			existingDDLRecordVersion.getRecordId(),
			newDDLRecordVersion.getRecordId());
		Assert.assertEquals(
			existingDDLRecordVersion.getVersion(),
			newDDLRecordVersion.getVersion());
		Assert.assertEquals(
			existingDDLRecordVersion.getDisplayIndex(),
			newDDLRecordVersion.getDisplayIndex());
		Assert.assertEquals(
			existingDDLRecordVersion.getStatus(),
			newDDLRecordVersion.getStatus());
		Assert.assertEquals(
			existingDDLRecordVersion.getStatusByUserId(),
			newDDLRecordVersion.getStatusByUserId());
		Assert.assertEquals(
			existingDDLRecordVersion.getStatusByUserName(),
			newDDLRecordVersion.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecordVersion.getStatusDate()),
			Time.getShortTimestamp(newDDLRecordVersion.getStatusDate()));
	}

	@Test
	public void testCountByRecordId() throws Exception {
		_persistence.countByRecordId(RandomTestUtil.nextLong());

		_persistence.countByRecordId(0L);
	}

	@Test
	public void testCountByR_R() throws Exception {
		_persistence.countByR_R(RandomTestUtil.nextLong(), "");

		_persistence.countByR_R(0L, "null");

		_persistence.countByR_R(0L, (String)null);
	}

	@Test
	public void testCountByR_V() throws Exception {
		_persistence.countByR_V(RandomTestUtil.nextLong(), "");

		_persistence.countByR_V(0L, "null");

		_persistence.countByR_V(0L, (String)null);
	}

	@Test
	public void testCountByR_S() throws Exception {
		_persistence.countByR_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_S(0L, 0);
	}

	@Test
	public void testCountByU_R_R_S() throws Exception {
		_persistence.countByU_R_R_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByU_R_R_S(0L, 0L, "null", 0);

		_persistence.countByU_R_R_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		DDLRecordVersion existingDDLRecordVersion =
			_persistence.findByPrimaryKey(newDDLRecordVersion.getPrimaryKey());

		Assert.assertEquals(existingDDLRecordVersion, newDDLRecordVersion);
	}

	@Test(expected = NoSuchRecordVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDLRecordVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDLRecordVersion", "mvccVersion", true, "ctCollectionId", true,
			"recordVersionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"DDMStorageId", true, "recordSetId", true, "recordSetVersion", true,
			"recordId", true, "version", true, "displayIndex", true, "status",
			true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		DDLRecordVersion existingDDLRecordVersion =
			_persistence.fetchByPrimaryKey(newDDLRecordVersion.getPrimaryKey());

		Assert.assertEquals(existingDDLRecordVersion, newDDLRecordVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordVersion missingDDLRecordVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDLRecordVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDLRecordVersion newDDLRecordVersion1 = addDDLRecordVersion();
		DDLRecordVersion newDDLRecordVersion2 = addDDLRecordVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordVersion1.getPrimaryKey());
		primaryKeys.add(newDDLRecordVersion2.getPrimaryKey());

		Map<Serializable, DDLRecordVersion> ddlRecordVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddlRecordVersions.size());
		Assert.assertEquals(
			newDDLRecordVersion1,
			ddlRecordVersions.get(newDDLRecordVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newDDLRecordVersion2,
			ddlRecordVersions.get(newDDLRecordVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDLRecordVersion> ddlRecordVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecordVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDLRecordVersion> ddlRecordVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecordVersions.size());
		Assert.assertEquals(
			newDDLRecordVersion,
			ddlRecordVersions.get(newDDLRecordVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDLRecordVersion> ddlRecordVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecordVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordVersion.getPrimaryKey());

		Map<Serializable, DDLRecordVersion> ddlRecordVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecordVersions.size());
		Assert.assertEquals(
			newDDLRecordVersion,
			ddlRecordVersions.get(newDDLRecordVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDLRecordVersion newDDLRecordVersion = addDDLRecordVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDLRecordVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDLRecordVersion ddlRecordVersion) {
		Assert.assertEquals(
			Long.valueOf(ddlRecordVersion.getRecordId()),
			ReflectionTestUtil.<Long>invoke(
				ddlRecordVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "recordId"));
		Assert.assertEquals(
			ddlRecordVersion.getVersion(),
			ReflectionTestUtil.invoke(
				ddlRecordVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected DDLRecordVersion addDDLRecordVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordVersion ddlRecordVersion = _persistence.create(pk);

		ddlRecordVersion.setMvccVersion(RandomTestUtil.nextLong());

		ddlRecordVersion.setCtCollectionId(RandomTestUtil.nextLong());

		ddlRecordVersion.setGroupId(RandomTestUtil.nextLong());

		ddlRecordVersion.setCompanyId(RandomTestUtil.nextLong());

		ddlRecordVersion.setUserId(RandomTestUtil.nextLong());

		ddlRecordVersion.setUserName(RandomTestUtil.randomString());

		ddlRecordVersion.setCreateDate(RandomTestUtil.nextDate());

		ddlRecordVersion.setDDMStorageId(RandomTestUtil.nextLong());

		ddlRecordVersion.setRecordSetId(RandomTestUtil.nextLong());

		ddlRecordVersion.setRecordSetVersion(RandomTestUtil.randomString());

		ddlRecordVersion.setRecordId(RandomTestUtil.nextLong());

		ddlRecordVersion.setVersion(RandomTestUtil.randomString());

		ddlRecordVersion.setDisplayIndex(RandomTestUtil.nextInt());

		ddlRecordVersion.setStatus(RandomTestUtil.nextInt());

		ddlRecordVersion.setStatusByUserId(RandomTestUtil.nextLong());

		ddlRecordVersion.setStatusByUserName(RandomTestUtil.randomString());

		ddlRecordVersion.setStatusDate(RandomTestUtil.nextDate());

		_ddlRecordVersions.add(_persistence.update(ddlRecordVersion));

		return ddlRecordVersion;
	}

	private List<DDLRecordVersion> _ddlRecordVersions =
		new ArrayList<DDLRecordVersion>();
	private DDLRecordVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}