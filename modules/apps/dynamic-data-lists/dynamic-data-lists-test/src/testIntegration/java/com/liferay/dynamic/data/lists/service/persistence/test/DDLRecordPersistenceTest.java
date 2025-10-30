/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.exception.NoSuchRecordException;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordUtil;
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
public class DDLRecordPersistenceTest {

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
		_persistence = DDLRecordUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDLRecord> iterator = _ddlRecords.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecord ddlRecord = _persistence.create(pk);

		Assert.assertNotNull(ddlRecord);

		Assert.assertEquals(ddlRecord.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDLRecord newDDLRecord = addDDLRecord();

		_persistence.remove(newDDLRecord);

		DDLRecord existingDDLRecord = _persistence.fetchByPrimaryKey(
			newDDLRecord.getPrimaryKey());

		Assert.assertNull(existingDDLRecord);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDLRecord();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecord newDDLRecord = _persistence.create(pk);

		newDDLRecord.setMvccVersion(RandomTestUtil.nextLong());

		newDDLRecord.setCtCollectionId(RandomTestUtil.nextLong());

		newDDLRecord.setUuid(RandomTestUtil.randomString());

		newDDLRecord.setGroupId(RandomTestUtil.nextLong());

		newDDLRecord.setCompanyId(RandomTestUtil.nextLong());

		newDDLRecord.setUserId(RandomTestUtil.nextLong());

		newDDLRecord.setUserName(RandomTestUtil.randomString());

		newDDLRecord.setVersionUserId(RandomTestUtil.nextLong());

		newDDLRecord.setVersionUserName(RandomTestUtil.randomString());

		newDDLRecord.setCreateDate(RandomTestUtil.nextDate());

		newDDLRecord.setModifiedDate(RandomTestUtil.nextDate());

		newDDLRecord.setDDMStorageId(RandomTestUtil.nextLong());

		newDDLRecord.setRecordSetId(RandomTestUtil.nextLong());

		newDDLRecord.setRecordSetVersion(RandomTestUtil.randomString());

		newDDLRecord.setClassName(RandomTestUtil.randomString());

		newDDLRecord.setClassPK(RandomTestUtil.nextLong());

		newDDLRecord.setVersion(RandomTestUtil.randomString());

		newDDLRecord.setDisplayIndex(RandomTestUtil.nextInt());

		newDDLRecord.setLastPublishDate(RandomTestUtil.nextDate());

		_ddlRecords.add(_persistence.update(newDDLRecord));

		DDLRecord existingDDLRecord = _persistence.findByPrimaryKey(
			newDDLRecord.getPrimaryKey());

		Assert.assertEquals(
			existingDDLRecord.getMvccVersion(), newDDLRecord.getMvccVersion());
		Assert.assertEquals(
			existingDDLRecord.getCtCollectionId(),
			newDDLRecord.getCtCollectionId());
		Assert.assertEquals(
			existingDDLRecord.getUuid(), newDDLRecord.getUuid());
		Assert.assertEquals(
			existingDDLRecord.getRecordId(), newDDLRecord.getRecordId());
		Assert.assertEquals(
			existingDDLRecord.getGroupId(), newDDLRecord.getGroupId());
		Assert.assertEquals(
			existingDDLRecord.getCompanyId(), newDDLRecord.getCompanyId());
		Assert.assertEquals(
			existingDDLRecord.getUserId(), newDDLRecord.getUserId());
		Assert.assertEquals(
			existingDDLRecord.getUserName(), newDDLRecord.getUserName());
		Assert.assertEquals(
			existingDDLRecord.getVersionUserId(),
			newDDLRecord.getVersionUserId());
		Assert.assertEquals(
			existingDDLRecord.getVersionUserName(),
			newDDLRecord.getVersionUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecord.getCreateDate()),
			Time.getShortTimestamp(newDDLRecord.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecord.getModifiedDate()),
			Time.getShortTimestamp(newDDLRecord.getModifiedDate()));
		Assert.assertEquals(
			existingDDLRecord.getDDMStorageId(),
			newDDLRecord.getDDMStorageId());
		Assert.assertEquals(
			existingDDLRecord.getRecordSetId(), newDDLRecord.getRecordSetId());
		Assert.assertEquals(
			existingDDLRecord.getRecordSetVersion(),
			newDDLRecord.getRecordSetVersion());
		Assert.assertEquals(
			existingDDLRecord.getClassName(), newDDLRecord.getClassName());
		Assert.assertEquals(
			existingDDLRecord.getClassPK(), newDDLRecord.getClassPK());
		Assert.assertEquals(
			existingDDLRecord.getVersion(), newDDLRecord.getVersion());
		Assert.assertEquals(
			existingDDLRecord.getDisplayIndex(),
			newDDLRecord.getDisplayIndex());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecord.getLastPublishDate()),
			Time.getShortTimestamp(newDDLRecord.getLastPublishDate()));
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByRecordSetId() throws Exception {
		_persistence.countByRecordSetId(RandomTestUtil.nextLong());

		_persistence.countByRecordSetId(0L);
	}

	@Test
	public void testCountByR_U() throws Exception {
		_persistence.countByR_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByR_U(0L, 0L);
	}

	@Test
	public void testCountByR_R() throws Exception {
		_persistence.countByR_R(RandomTestUtil.nextLong(), "");

		_persistence.countByR_R(0L, "null");

		_persistence.countByR_R(0L, (String)null);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C("", RandomTestUtil.nextLong());

		_persistence.countByC_C("null", 0L);

		_persistence.countByC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDLRecord newDDLRecord = addDDLRecord();

		DDLRecord existingDDLRecord = _persistence.findByPrimaryKey(
			newDDLRecord.getPrimaryKey());

		Assert.assertEquals(existingDDLRecord, newDDLRecord);
	}

	@Test(expected = NoSuchRecordException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDLRecord> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDLRecord", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "recordId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "versionUserId", true,
			"versionUserName", true, "createDate", true, "modifiedDate", true,
			"DDMStorageId", true, "recordSetId", true, "recordSetVersion", true,
			"className", true, "classPK", true, "version", true, "displayIndex",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDLRecord newDDLRecord = addDDLRecord();

		DDLRecord existingDDLRecord = _persistence.fetchByPrimaryKey(
			newDDLRecord.getPrimaryKey());

		Assert.assertEquals(existingDDLRecord, newDDLRecord);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecord missingDDLRecord = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDLRecord);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDLRecord newDDLRecord1 = addDDLRecord();
		DDLRecord newDDLRecord2 = addDDLRecord();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecord1.getPrimaryKey());
		primaryKeys.add(newDDLRecord2.getPrimaryKey());

		Map<Serializable, DDLRecord> ddlRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddlRecords.size());
		Assert.assertEquals(
			newDDLRecord1, ddlRecords.get(newDDLRecord1.getPrimaryKey()));
		Assert.assertEquals(
			newDDLRecord2, ddlRecords.get(newDDLRecord2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDLRecord> ddlRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecords.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDLRecord newDDLRecord = addDDLRecord();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecord.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDLRecord> ddlRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecords.size());
		Assert.assertEquals(
			newDDLRecord, ddlRecords.get(newDDLRecord.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDLRecord> ddlRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecords.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDLRecord newDDLRecord = addDDLRecord();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecord.getPrimaryKey());

		Map<Serializable, DDLRecord> ddlRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecords.size());
		Assert.assertEquals(
			newDDLRecord, ddlRecords.get(newDDLRecord.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDLRecord newDDLRecord = addDDLRecord();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDLRecord.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDLRecord ddlRecord) {
		Assert.assertEquals(
			ddlRecord.getUuid(),
			ReflectionTestUtil.invoke(
				ddlRecord, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddlRecord.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddlRecord, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DDLRecord addDDLRecord() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecord ddlRecord = _persistence.create(pk);

		ddlRecord.setMvccVersion(RandomTestUtil.nextLong());

		ddlRecord.setCtCollectionId(RandomTestUtil.nextLong());

		ddlRecord.setUuid(RandomTestUtil.randomString());

		ddlRecord.setGroupId(RandomTestUtil.nextLong());

		ddlRecord.setCompanyId(RandomTestUtil.nextLong());

		ddlRecord.setUserId(RandomTestUtil.nextLong());

		ddlRecord.setUserName(RandomTestUtil.randomString());

		ddlRecord.setVersionUserId(RandomTestUtil.nextLong());

		ddlRecord.setVersionUserName(RandomTestUtil.randomString());

		ddlRecord.setCreateDate(RandomTestUtil.nextDate());

		ddlRecord.setModifiedDate(RandomTestUtil.nextDate());

		ddlRecord.setDDMStorageId(RandomTestUtil.nextLong());

		ddlRecord.setRecordSetId(RandomTestUtil.nextLong());

		ddlRecord.setRecordSetVersion(RandomTestUtil.randomString());

		ddlRecord.setClassName(RandomTestUtil.randomString());

		ddlRecord.setClassPK(RandomTestUtil.nextLong());

		ddlRecord.setVersion(RandomTestUtil.randomString());

		ddlRecord.setDisplayIndex(RandomTestUtil.nextInt());

		ddlRecord.setLastPublishDate(RandomTestUtil.nextDate());

		_ddlRecords.add(_persistence.update(ddlRecord));

		return ddlRecord;
	}

	private List<DDLRecord> _ddlRecords = new ArrayList<DDLRecord>();
	private DDLRecordPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}