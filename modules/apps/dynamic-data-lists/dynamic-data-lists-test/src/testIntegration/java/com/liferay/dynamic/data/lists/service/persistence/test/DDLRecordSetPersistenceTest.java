/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.lists.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.lists.exception.NoSuchRecordSetException;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetPersistence;
import com.liferay.dynamic.data.lists.service.persistence.DDLRecordSetUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class DDLRecordSetPersistenceTest {

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
		_persistence = DDLRecordSetUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDLRecordSet> iterator = _ddlRecordSets.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordSet ddlRecordSet = _persistence.create(pk);

		Assert.assertNotNull(ddlRecordSet);

		Assert.assertEquals(ddlRecordSet.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		_persistence.remove(newDDLRecordSet);

		DDLRecordSet existingDDLRecordSet = _persistence.fetchByPrimaryKey(
			newDDLRecordSet.getPrimaryKey());

		Assert.assertNull(existingDDLRecordSet);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDLRecordSet();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordSet newDDLRecordSet = _persistence.create(pk);

		newDDLRecordSet.setMvccVersion(RandomTestUtil.nextLong());

		newDDLRecordSet.setCtCollectionId(RandomTestUtil.nextLong());

		newDDLRecordSet.setUuid(RandomTestUtil.randomString());

		newDDLRecordSet.setGroupId(RandomTestUtil.nextLong());

		newDDLRecordSet.setCompanyId(RandomTestUtil.nextLong());

		newDDLRecordSet.setUserId(RandomTestUtil.nextLong());

		newDDLRecordSet.setUserName(RandomTestUtil.randomString());

		newDDLRecordSet.setVersionUserId(RandomTestUtil.nextLong());

		newDDLRecordSet.setVersionUserName(RandomTestUtil.randomString());

		newDDLRecordSet.setCreateDate(RandomTestUtil.nextDate());

		newDDLRecordSet.setModifiedDate(RandomTestUtil.nextDate());

		newDDLRecordSet.setDDMStructureId(RandomTestUtil.nextLong());

		newDDLRecordSet.setRecordSetKey(RandomTestUtil.randomString());

		newDDLRecordSet.setVersion(RandomTestUtil.randomString());

		newDDLRecordSet.setName(RandomTestUtil.randomString());

		newDDLRecordSet.setDescription(RandomTestUtil.randomString());

		newDDLRecordSet.setMinDisplayRows(RandomTestUtil.nextInt());

		newDDLRecordSet.setScope(RandomTestUtil.nextInt());

		newDDLRecordSet.setSettings(RandomTestUtil.randomString());

		newDDLRecordSet.setLastPublishDate(RandomTestUtil.nextDate());

		_ddlRecordSets.add(_persistence.update(newDDLRecordSet));

		DDLRecordSet existingDDLRecordSet = _persistence.findByPrimaryKey(
			newDDLRecordSet.getPrimaryKey());

		Assert.assertEquals(
			existingDDLRecordSet.getMvccVersion(),
			newDDLRecordSet.getMvccVersion());
		Assert.assertEquals(
			existingDDLRecordSet.getCtCollectionId(),
			newDDLRecordSet.getCtCollectionId());
		Assert.assertEquals(
			existingDDLRecordSet.getUuid(), newDDLRecordSet.getUuid());
		Assert.assertEquals(
			existingDDLRecordSet.getRecordSetId(),
			newDDLRecordSet.getRecordSetId());
		Assert.assertEquals(
			existingDDLRecordSet.getGroupId(), newDDLRecordSet.getGroupId());
		Assert.assertEquals(
			existingDDLRecordSet.getCompanyId(),
			newDDLRecordSet.getCompanyId());
		Assert.assertEquals(
			existingDDLRecordSet.getUserId(), newDDLRecordSet.getUserId());
		Assert.assertEquals(
			existingDDLRecordSet.getUserName(), newDDLRecordSet.getUserName());
		Assert.assertEquals(
			existingDDLRecordSet.getVersionUserId(),
			newDDLRecordSet.getVersionUserId());
		Assert.assertEquals(
			existingDDLRecordSet.getVersionUserName(),
			newDDLRecordSet.getVersionUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecordSet.getCreateDate()),
			Time.getShortTimestamp(newDDLRecordSet.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecordSet.getModifiedDate()),
			Time.getShortTimestamp(newDDLRecordSet.getModifiedDate()));
		Assert.assertEquals(
			existingDDLRecordSet.getDDMStructureId(),
			newDDLRecordSet.getDDMStructureId());
		Assert.assertEquals(
			existingDDLRecordSet.getRecordSetKey(),
			newDDLRecordSet.getRecordSetKey());
		Assert.assertEquals(
			existingDDLRecordSet.getVersion(), newDDLRecordSet.getVersion());
		Assert.assertEquals(
			existingDDLRecordSet.getName(), newDDLRecordSet.getName());
		Assert.assertEquals(
			existingDDLRecordSet.getDescription(),
			newDDLRecordSet.getDescription());
		Assert.assertEquals(
			existingDDLRecordSet.getMinDisplayRows(),
			newDDLRecordSet.getMinDisplayRows());
		Assert.assertEquals(
			existingDDLRecordSet.getScope(), newDDLRecordSet.getScope());
		Assert.assertEquals(
			existingDDLRecordSet.getSettings(), newDDLRecordSet.getSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDLRecordSet.getLastPublishDate()),
			Time.getShortTimestamp(newDDLRecordSet.getLastPublishDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByDDMStructureId() throws Exception {
		_persistence.countByDDMStructureId(RandomTestUtil.nextLong());

		_persistence.countByDDMStructureId(0L);
	}

	@Test
	public void testCountByDDMStructureIdArrayable() throws Exception {
		_persistence.countByDDMStructureId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_R() throws Exception {
		_persistence.countByG_R(RandomTestUtil.nextLong(), "");

		_persistence.countByG_R(0L, "null");

		_persistence.countByG_R(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		DDLRecordSet existingDDLRecordSet = _persistence.findByPrimaryKey(
			newDDLRecordSet.getPrimaryKey());

		Assert.assertEquals(existingDDLRecordSet, newDDLRecordSet);
	}

	@Test(expected = NoSuchRecordSetException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDLRecordSet> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDLRecordSet", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "recordSetId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "versionUserId", true,
			"versionUserName", true, "createDate", true, "modifiedDate", true,
			"DDMStructureId", true, "recordSetKey", true, "version", true,
			"name", true, "description", true, "minDisplayRows", true, "scope",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		DDLRecordSet existingDDLRecordSet = _persistence.fetchByPrimaryKey(
			newDDLRecordSet.getPrimaryKey());

		Assert.assertEquals(existingDDLRecordSet, newDDLRecordSet);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordSet missingDDLRecordSet = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDLRecordSet);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDLRecordSet newDDLRecordSet1 = addDDLRecordSet();
		DDLRecordSet newDDLRecordSet2 = addDDLRecordSet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordSet1.getPrimaryKey());
		primaryKeys.add(newDDLRecordSet2.getPrimaryKey());

		Map<Serializable, DDLRecordSet> ddlRecordSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddlRecordSets.size());
		Assert.assertEquals(
			newDDLRecordSet1,
			ddlRecordSets.get(newDDLRecordSet1.getPrimaryKey()));
		Assert.assertEquals(
			newDDLRecordSet2,
			ddlRecordSets.get(newDDLRecordSet2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDLRecordSet> ddlRecordSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecordSets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordSet.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDLRecordSet> ddlRecordSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecordSets.size());
		Assert.assertEquals(
			newDDLRecordSet,
			ddlRecordSets.get(newDDLRecordSet.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDLRecordSet> ddlRecordSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddlRecordSets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDLRecordSet.getPrimaryKey());

		Map<Serializable, DDLRecordSet> ddlRecordSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddlRecordSets.size());
		Assert.assertEquals(
			newDDLRecordSet,
			ddlRecordSets.get(newDDLRecordSet.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDLRecordSet newDDLRecordSet = addDDLRecordSet();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDLRecordSet.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDLRecordSet ddlRecordSet) {
		Assert.assertEquals(
			ddlRecordSet.getUuid(),
			ReflectionTestUtil.invoke(
				ddlRecordSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddlRecordSet.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddlRecordSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(ddlRecordSet.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddlRecordSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			ddlRecordSet.getRecordSetKey(),
			ReflectionTestUtil.invoke(
				ddlRecordSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "recordSetKey"));
	}

	protected DDLRecordSet addDDLRecordSet() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDLRecordSet ddlRecordSet = _persistence.create(pk);

		ddlRecordSet.setMvccVersion(RandomTestUtil.nextLong());

		ddlRecordSet.setCtCollectionId(RandomTestUtil.nextLong());

		ddlRecordSet.setUuid(RandomTestUtil.randomString());

		ddlRecordSet.setGroupId(RandomTestUtil.nextLong());

		ddlRecordSet.setCompanyId(RandomTestUtil.nextLong());

		ddlRecordSet.setUserId(RandomTestUtil.nextLong());

		ddlRecordSet.setUserName(RandomTestUtil.randomString());

		ddlRecordSet.setVersionUserId(RandomTestUtil.nextLong());

		ddlRecordSet.setVersionUserName(RandomTestUtil.randomString());

		ddlRecordSet.setCreateDate(RandomTestUtil.nextDate());

		ddlRecordSet.setModifiedDate(RandomTestUtil.nextDate());

		ddlRecordSet.setDDMStructureId(RandomTestUtil.nextLong());

		ddlRecordSet.setRecordSetKey(RandomTestUtil.randomString());

		ddlRecordSet.setVersion(RandomTestUtil.randomString());

		ddlRecordSet.setName(RandomTestUtil.randomString());

		ddlRecordSet.setDescription(RandomTestUtil.randomString());

		ddlRecordSet.setMinDisplayRows(RandomTestUtil.nextInt());

		ddlRecordSet.setScope(RandomTestUtil.nextInt());

		ddlRecordSet.setSettings(RandomTestUtil.randomString());

		ddlRecordSet.setLastPublishDate(RandomTestUtil.nextDate());

		_ddlRecordSets.add(_persistence.update(ddlRecordSet));

		return ddlRecordSet;
	}

	private List<DDLRecordSet> _ddlRecordSets = new ArrayList<DDLRecordSet>();
	private DDLRecordSetPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}