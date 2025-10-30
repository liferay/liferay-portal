/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchFormInstanceRecordException;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMFormInstanceRecordUtil;
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
public class DDMFormInstanceRecordPersistenceTest {

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
		_persistence = DDMFormInstanceRecordUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMFormInstanceRecord> iterator =
			_ddmFormInstanceRecords.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecord ddmFormInstanceRecord = _persistence.create(pk);

		Assert.assertNotNull(ddmFormInstanceRecord);

		Assert.assertEquals(ddmFormInstanceRecord.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		_persistence.remove(newDDMFormInstanceRecord);

		DDMFormInstanceRecord existingDDMFormInstanceRecord =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceRecord.getPrimaryKey());

		Assert.assertNull(existingDDMFormInstanceRecord);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMFormInstanceRecord();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecord newDDMFormInstanceRecord = _persistence.create(
			pk);

		newDDMFormInstanceRecord.setMvccVersion(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setUuid(RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setGroupId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setCompanyId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setUserId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setUserName(RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setVersionUserId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setVersionUserName(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setCreateDate(RandomTestUtil.nextDate());

		newDDMFormInstanceRecord.setModifiedDate(RandomTestUtil.nextDate());

		newDDMFormInstanceRecord.setFormInstanceId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setFormInstanceVersion(
			RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setStorageId(RandomTestUtil.nextLong());

		newDDMFormInstanceRecord.setVersion(RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setIpAddress(RandomTestUtil.randomString());

		newDDMFormInstanceRecord.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmFormInstanceRecords.add(
			_persistence.update(newDDMFormInstanceRecord));

		DDMFormInstanceRecord existingDDMFormInstanceRecord =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecord.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecord.getMvccVersion(),
			newDDMFormInstanceRecord.getMvccVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getCtCollectionId(),
			newDDMFormInstanceRecord.getCtCollectionId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getUuid(),
			newDDMFormInstanceRecord.getUuid());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getFormInstanceRecordId(),
			newDDMFormInstanceRecord.getFormInstanceRecordId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getGroupId(),
			newDDMFormInstanceRecord.getGroupId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getCompanyId(),
			newDDMFormInstanceRecord.getCompanyId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getUserId(),
			newDDMFormInstanceRecord.getUserId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getUserName(),
			newDDMFormInstanceRecord.getUserName());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getVersionUserId(),
			newDDMFormInstanceRecord.getVersionUserId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getVersionUserName(),
			newDDMFormInstanceRecord.getVersionUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceRecord.getCreateDate()),
			Time.getShortTimestamp(newDDMFormInstanceRecord.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceRecord.getModifiedDate()),
			Time.getShortTimestamp(newDDMFormInstanceRecord.getModifiedDate()));
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getFormInstanceId(),
			newDDMFormInstanceRecord.getFormInstanceId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getFormInstanceVersion(),
			newDDMFormInstanceRecord.getFormInstanceVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getStorageId(),
			newDDMFormInstanceRecord.getStorageId());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getVersion(),
			newDDMFormInstanceRecord.getVersion());
		Assert.assertEquals(
			existingDDMFormInstanceRecord.getIpAddress(),
			newDDMFormInstanceRecord.getIpAddress());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMFormInstanceRecord.getLastPublishDate()),
			Time.getShortTimestamp(
				newDDMFormInstanceRecord.getLastPublishDate()));
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
	public void testCountByFormInstanceId() throws Exception {
		_persistence.countByFormInstanceId(RandomTestUtil.nextLong());

		_persistence.countByFormInstanceId(0L);
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		DDMFormInstanceRecord existingDDMFormInstanceRecord =
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecord.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecord, newDDMFormInstanceRecord);
	}

	@Test(expected = NoSuchFormInstanceRecordException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMFormInstanceRecord> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMFormInstanceRecord", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "formInstanceRecordId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true,
			"versionUserId", true, "versionUserName", true, "createDate", true,
			"modifiedDate", true, "formInstanceId", true, "formInstanceVersion",
			true, "storageId", true, "version", true, "ipAddress", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		DDMFormInstanceRecord existingDDMFormInstanceRecord =
			_persistence.fetchByPrimaryKey(
				newDDMFormInstanceRecord.getPrimaryKey());

		Assert.assertEquals(
			existingDDMFormInstanceRecord, newDDMFormInstanceRecord);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecord missingDDMFormInstanceRecord =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMFormInstanceRecord);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMFormInstanceRecord newDDMFormInstanceRecord1 =
			addDDMFormInstanceRecord();
		DDMFormInstanceRecord newDDMFormInstanceRecord2 =
			addDDMFormInstanceRecord();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecord1.getPrimaryKey());
		primaryKeys.add(newDDMFormInstanceRecord2.getPrimaryKey());

		Map<Serializable, DDMFormInstanceRecord> ddmFormInstanceRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmFormInstanceRecords.size());
		Assert.assertEquals(
			newDDMFormInstanceRecord1,
			ddmFormInstanceRecords.get(
				newDDMFormInstanceRecord1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMFormInstanceRecord2,
			ddmFormInstanceRecords.get(
				newDDMFormInstanceRecord2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMFormInstanceRecord> ddmFormInstanceRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceRecords.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecord.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMFormInstanceRecord> ddmFormInstanceRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceRecords.size());
		Assert.assertEquals(
			newDDMFormInstanceRecord,
			ddmFormInstanceRecords.get(
				newDDMFormInstanceRecord.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMFormInstanceRecord> ddmFormInstanceRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmFormInstanceRecords.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMFormInstanceRecord.getPrimaryKey());

		Map<Serializable, DDMFormInstanceRecord> ddmFormInstanceRecords =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmFormInstanceRecords.size());
		Assert.assertEquals(
			newDDMFormInstanceRecord,
			ddmFormInstanceRecords.get(
				newDDMFormInstanceRecord.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMFormInstanceRecord newDDMFormInstanceRecord =
			addDDMFormInstanceRecord();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMFormInstanceRecord.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMFormInstanceRecord ddmFormInstanceRecord) {

		Assert.assertEquals(
			ddmFormInstanceRecord.getUuid(),
			ReflectionTestUtil.invoke(
				ddmFormInstanceRecord, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmFormInstanceRecord.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmFormInstanceRecord, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DDMFormInstanceRecord addDDMFormInstanceRecord()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMFormInstanceRecord ddmFormInstanceRecord = _persistence.create(pk);

		ddmFormInstanceRecord.setMvccVersion(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setCtCollectionId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setUuid(RandomTestUtil.randomString());

		ddmFormInstanceRecord.setGroupId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setCompanyId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setUserId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setUserName(RandomTestUtil.randomString());

		ddmFormInstanceRecord.setVersionUserId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setVersionUserName(RandomTestUtil.randomString());

		ddmFormInstanceRecord.setCreateDate(RandomTestUtil.nextDate());

		ddmFormInstanceRecord.setModifiedDate(RandomTestUtil.nextDate());

		ddmFormInstanceRecord.setFormInstanceId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setFormInstanceVersion(
			RandomTestUtil.randomString());

		ddmFormInstanceRecord.setStorageId(RandomTestUtil.nextLong());

		ddmFormInstanceRecord.setVersion(RandomTestUtil.randomString());

		ddmFormInstanceRecord.setIpAddress(RandomTestUtil.randomString());

		ddmFormInstanceRecord.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmFormInstanceRecords.add(_persistence.update(ddmFormInstanceRecord));

		return ddmFormInstanceRecord;
	}

	private List<DDMFormInstanceRecord> _ddmFormInstanceRecords =
		new ArrayList<DDMFormInstanceRecord>();
	private DDMFormInstanceRecordPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}