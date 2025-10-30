/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchDataProviderInstanceException;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstancePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMDataProviderInstanceUtil;
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
public class DDMDataProviderInstancePersistenceTest {

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
		_persistence = DDMDataProviderInstanceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMDataProviderInstance> iterator =
			_ddmDataProviderInstances.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstance ddmDataProviderInstance = _persistence.create(
			pk);

		Assert.assertNotNull(ddmDataProviderInstance);

		Assert.assertEquals(ddmDataProviderInstance.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		_persistence.remove(newDDMDataProviderInstance);

		DDMDataProviderInstance existingDDMDataProviderInstance =
			_persistence.fetchByPrimaryKey(
				newDDMDataProviderInstance.getPrimaryKey());

		Assert.assertNull(existingDDMDataProviderInstance);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMDataProviderInstance();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstance newDDMDataProviderInstance =
			_persistence.create(pk);

		newDDMDataProviderInstance.setMvccVersion(RandomTestUtil.nextLong());

		newDDMDataProviderInstance.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMDataProviderInstance.setUuid(RandomTestUtil.randomString());

		newDDMDataProviderInstance.setGroupId(RandomTestUtil.nextLong());

		newDDMDataProviderInstance.setCompanyId(RandomTestUtil.nextLong());

		newDDMDataProviderInstance.setUserId(RandomTestUtil.nextLong());

		newDDMDataProviderInstance.setUserName(RandomTestUtil.randomString());

		newDDMDataProviderInstance.setCreateDate(RandomTestUtil.nextDate());

		newDDMDataProviderInstance.setModifiedDate(RandomTestUtil.nextDate());

		newDDMDataProviderInstance.setName(RandomTestUtil.randomString());

		newDDMDataProviderInstance.setDescription(
			RandomTestUtil.randomString());

		newDDMDataProviderInstance.setDefinition(RandomTestUtil.randomString());

		newDDMDataProviderInstance.setType(RandomTestUtil.randomString());

		newDDMDataProviderInstance.setLastPublishDate(
			RandomTestUtil.nextDate());

		_ddmDataProviderInstances.add(
			_persistence.update(newDDMDataProviderInstance));

		DDMDataProviderInstance existingDDMDataProviderInstance =
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstance.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstance.getMvccVersion(),
			newDDMDataProviderInstance.getMvccVersion());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getCtCollectionId(),
			newDDMDataProviderInstance.getCtCollectionId());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getUuid(),
			newDDMDataProviderInstance.getUuid());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getDataProviderInstanceId(),
			newDDMDataProviderInstance.getDataProviderInstanceId());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getGroupId(),
			newDDMDataProviderInstance.getGroupId());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getCompanyId(),
			newDDMDataProviderInstance.getCompanyId());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getUserId(),
			newDDMDataProviderInstance.getUserId());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getUserName(),
			newDDMDataProviderInstance.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMDataProviderInstance.getCreateDate()),
			Time.getShortTimestamp(newDDMDataProviderInstance.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMDataProviderInstance.getModifiedDate()),
			Time.getShortTimestamp(
				newDDMDataProviderInstance.getModifiedDate()));
		Assert.assertEquals(
			existingDDMDataProviderInstance.getName(),
			newDDMDataProviderInstance.getName());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getDescription(),
			newDDMDataProviderInstance.getDescription());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getDefinition(),
			newDDMDataProviderInstance.getDefinition());
		Assert.assertEquals(
			existingDDMDataProviderInstance.getType(),
			newDDMDataProviderInstance.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMDataProviderInstance.getLastPublishDate()),
			Time.getShortTimestamp(
				newDDMDataProviderInstance.getLastPublishDate()));
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		DDMDataProviderInstance existingDDMDataProviderInstance =
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstance.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstance, newDDMDataProviderInstance);
	}

	@Test(expected = NoSuchDataProviderInstanceException.class)
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

	protected OrderByComparator<DDMDataProviderInstance>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"DDMDataProviderInstance", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "dataProviderInstanceId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "type", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		DDMDataProviderInstance existingDDMDataProviderInstance =
			_persistence.fetchByPrimaryKey(
				newDDMDataProviderInstance.getPrimaryKey());

		Assert.assertEquals(
			existingDDMDataProviderInstance, newDDMDataProviderInstance);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstance missingDDMDataProviderInstance =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMDataProviderInstance);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMDataProviderInstance newDDMDataProviderInstance1 =
			addDDMDataProviderInstance();
		DDMDataProviderInstance newDDMDataProviderInstance2 =
			addDDMDataProviderInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstance1.getPrimaryKey());
		primaryKeys.add(newDDMDataProviderInstance2.getPrimaryKey());

		Map<Serializable, DDMDataProviderInstance> ddmDataProviderInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmDataProviderInstances.size());
		Assert.assertEquals(
			newDDMDataProviderInstance1,
			ddmDataProviderInstances.get(
				newDDMDataProviderInstance1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMDataProviderInstance2,
			ddmDataProviderInstances.get(
				newDDMDataProviderInstance2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMDataProviderInstance> ddmDataProviderInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmDataProviderInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstance.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMDataProviderInstance> ddmDataProviderInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmDataProviderInstances.size());
		Assert.assertEquals(
			newDDMDataProviderInstance,
			ddmDataProviderInstances.get(
				newDDMDataProviderInstance.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMDataProviderInstance> ddmDataProviderInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmDataProviderInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMDataProviderInstance.getPrimaryKey());

		Map<Serializable, DDMDataProviderInstance> ddmDataProviderInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmDataProviderInstances.size());
		Assert.assertEquals(
			newDDMDataProviderInstance,
			ddmDataProviderInstances.get(
				newDDMDataProviderInstance.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMDataProviderInstance newDDMDataProviderInstance =
			addDDMDataProviderInstance();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMDataProviderInstance.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DDMDataProviderInstance ddmDataProviderInstance) {

		Assert.assertEquals(
			ddmDataProviderInstance.getUuid(),
			ReflectionTestUtil.invoke(
				ddmDataProviderInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmDataProviderInstance.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmDataProviderInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DDMDataProviderInstance addDDMDataProviderInstance()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		DDMDataProviderInstance ddmDataProviderInstance = _persistence.create(
			pk);

		ddmDataProviderInstance.setMvccVersion(RandomTestUtil.nextLong());

		ddmDataProviderInstance.setCtCollectionId(RandomTestUtil.nextLong());

		ddmDataProviderInstance.setUuid(RandomTestUtil.randomString());

		ddmDataProviderInstance.setGroupId(RandomTestUtil.nextLong());

		ddmDataProviderInstance.setCompanyId(RandomTestUtil.nextLong());

		ddmDataProviderInstance.setUserId(RandomTestUtil.nextLong());

		ddmDataProviderInstance.setUserName(RandomTestUtil.randomString());

		ddmDataProviderInstance.setCreateDate(RandomTestUtil.nextDate());

		ddmDataProviderInstance.setModifiedDate(RandomTestUtil.nextDate());

		ddmDataProviderInstance.setName(RandomTestUtil.randomString());

		ddmDataProviderInstance.setDescription(RandomTestUtil.randomString());

		ddmDataProviderInstance.setDefinition(RandomTestUtil.randomString());

		ddmDataProviderInstance.setType(RandomTestUtil.randomString());

		ddmDataProviderInstance.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmDataProviderInstances.add(
			_persistence.update(ddmDataProviderInstance));

		return ddmDataProviderInstance;
	}

	private List<DDMDataProviderInstance> _ddmDataProviderInstances =
		new ArrayList<DDMDataProviderInstance>();
	private DDMDataProviderInstancePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}