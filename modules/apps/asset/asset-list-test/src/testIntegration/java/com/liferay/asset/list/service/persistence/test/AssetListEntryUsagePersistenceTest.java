/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.exception.NoSuchEntryUsageException;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.service.AssetListEntryUsageLocalServiceUtil;
import com.liferay.asset.list.service.persistence.AssetListEntryUsagePersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryUsageUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class AssetListEntryUsagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() {
		_persistence = AssetListEntryUsageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetListEntryUsage> iterator =
			_assetListEntryUsages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryUsage assetListEntryUsage = _persistence.create(pk);

		Assert.assertNotNull(assetListEntryUsage);

		Assert.assertEquals(assetListEntryUsage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		_persistence.remove(newAssetListEntryUsage);

		AssetListEntryUsage existingAssetListEntryUsage =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryUsage.getPrimaryKey());

		Assert.assertNull(existingAssetListEntryUsage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetListEntryUsage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		newAssetListEntryUsage.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setUuid(RandomTestUtil.randomString());

		newAssetListEntryUsage.setGroupId(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setCompanyId(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setUserId(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setUserName(RandomTestUtil.randomString());

		newAssetListEntryUsage.setCreateDate(RandomTestUtil.nextDate());

		newAssetListEntryUsage.setModifiedDate(RandomTestUtil.nextDate());

		newAssetListEntryUsage.setClassNameId(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setContainerKey(RandomTestUtil.randomString());

		newAssetListEntryUsage.setContainerType(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setKey(RandomTestUtil.randomString());

		newAssetListEntryUsage.setPlid(RandomTestUtil.nextLong());

		newAssetListEntryUsage.setType(RandomTestUtil.nextInt());

		newAssetListEntryUsage.setLastPublishDate(RandomTestUtil.nextDate());

		newAssetListEntryUsage = _persistence.update(newAssetListEntryUsage);

		_assetListEntryUsages.add(newAssetListEntryUsage);

		AssetListEntryUsage existingAssetListEntryUsage =
			_persistence.findByPrimaryKey(
				newAssetListEntryUsage.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryUsage.getMvccVersion(),
			newAssetListEntryUsage.getMvccVersion());
		Assert.assertEquals(
			existingAssetListEntryUsage.getCtCollectionId(),
			newAssetListEntryUsage.getCtCollectionId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getUuid(),
			newAssetListEntryUsage.getUuid());
		Assert.assertEquals(
			existingAssetListEntryUsage.getAssetListEntryUsageId(),
			newAssetListEntryUsage.getAssetListEntryUsageId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getGroupId(),
			newAssetListEntryUsage.getGroupId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getCompanyId(),
			newAssetListEntryUsage.getCompanyId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getUserId(),
			newAssetListEntryUsage.getUserId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getUserName(),
			newAssetListEntryUsage.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetListEntryUsage.getCreateDate()),
			Time.getShortTimestamp(newAssetListEntryUsage.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryUsage.getModifiedDate()),
			Time.getShortTimestamp(newAssetListEntryUsage.getModifiedDate()));
		Assert.assertEquals(
			existingAssetListEntryUsage.getClassNameId(),
			newAssetListEntryUsage.getClassNameId());
		Assert.assertEquals(
			existingAssetListEntryUsage.getContainerKey(),
			newAssetListEntryUsage.getContainerKey());
		Assert.assertEquals(
			existingAssetListEntryUsage.getContainerType(),
			newAssetListEntryUsage.getContainerType());
		Assert.assertEquals(
			existingAssetListEntryUsage.getKey(),
			newAssetListEntryUsage.getKey());
		Assert.assertEquals(
			existingAssetListEntryUsage.getPlid(),
			newAssetListEntryUsage.getPlid());
		Assert.assertEquals(
			existingAssetListEntryUsage.getType(),
			newAssetListEntryUsage.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryUsage.getLastPublishDate()),
			Time.getShortTimestamp(
				newAssetListEntryUsage.getLastPublishDate()));
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
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountByCT_P() throws Exception {
		_persistence.countByCT_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCT_P(0L, 0L);
	}

	@Test
	public void testCountByG_C_K() throws Exception {
		_persistence.countByG_C_K(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_K(0L, 0L, "null");

		_persistence.countByG_C_K(0L, 0L, (String)null);
	}

	@Test
	public void testCountByC_C_K() throws Exception {
		_persistence.countByC_C_K(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_C_K(0L, 0L, "null");

		_persistence.countByC_C_K(0L, 0L, (String)null);
	}

	@Test
	public void testCountByCK_CT_P() throws Exception {
		_persistence.countByCK_CT_P(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCK_CT_P("null", 0L, 0L);

		_persistence.countByCK_CT_P((String)null, 0L, 0L);
	}

	@Test
	public void testCountByG_C_K_T() throws Exception {
		_persistence.countByG_C_K_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_C_K_T(0L, 0L, "null", 0);

		_persistence.countByG_C_K_T(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_C_CK_CT_K_P() throws Exception {
		_persistence.countByG_C_CK_CT_K_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByG_C_CK_CT_K_P(0L, 0L, "null", 0L, "null", 0L);

		_persistence.countByG_C_CK_CT_K_P(
			0L, 0L, (String)null, 0L, (String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		AssetListEntryUsage existingAssetListEntryUsage =
			_persistence.findByPrimaryKey(
				newAssetListEntryUsage.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryUsage, newAssetListEntryUsage);
	}

	@Test(expected = NoSuchEntryUsageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetListEntryUsage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetListEntryUsage", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "assetListEntryUsageId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "containerKey",
			true, "containerType", true, "key", true, "plid", true, "type",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		AssetListEntryUsage existingAssetListEntryUsage =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryUsage.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryUsage, newAssetListEntryUsage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryUsage missingAssetListEntryUsage =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetListEntryUsage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetListEntryUsage newAssetListEntryUsage1 = addAssetListEntryUsage();
		AssetListEntryUsage newAssetListEntryUsage2 = addAssetListEntryUsage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryUsage1.getPrimaryKey());
		primaryKeys.add(newAssetListEntryUsage2.getPrimaryKey());

		Map<Serializable, AssetListEntryUsage> assetListEntryUsages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetListEntryUsages.size());
		Assert.assertEquals(
			newAssetListEntryUsage1,
			assetListEntryUsages.get(newAssetListEntryUsage1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetListEntryUsage2,
			assetListEntryUsages.get(newAssetListEntryUsage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetListEntryUsage> assetListEntryUsages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetListEntryUsages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryUsage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetListEntryUsage> assetListEntryUsages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetListEntryUsages.size());
		Assert.assertEquals(
			newAssetListEntryUsage,
			assetListEntryUsages.get(newAssetListEntryUsage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetListEntryUsage> assetListEntryUsages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetListEntryUsages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryUsage.getPrimaryKey());

		Map<Serializable, AssetListEntryUsage> assetListEntryUsages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetListEntryUsages.size());
		Assert.assertEquals(
			newAssetListEntryUsage,
			assetListEntryUsages.get(newAssetListEntryUsage.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AssetListEntryUsageLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<AssetListEntryUsage>() {

				@Override
				public void performAction(
					AssetListEntryUsage assetListEntryUsage) {

					Assert.assertNotNull(assetListEntryUsage);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryUsage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryUsageId",
				newAssetListEntryUsage.getAssetListEntryUsageId()));

		List<AssetListEntryUsage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AssetListEntryUsage existingAssetListEntryUsage = result.get(0);

		Assert.assertEquals(
			existingAssetListEntryUsage, newAssetListEntryUsage);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryUsage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryUsageId", RandomTestUtil.nextLong()));

		List<AssetListEntryUsage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryUsage.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetListEntryUsageId"));

		Object newAssetListEntryUsageId =
			newAssetListEntryUsage.getAssetListEntryUsageId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetListEntryUsageId",
				new Object[] {newAssetListEntryUsageId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAssetListEntryUsageId = result.get(0);

		Assert.assertEquals(
			existingAssetListEntryUsageId, newAssetListEntryUsageId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryUsage.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetListEntryUsageId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetListEntryUsageId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetListEntryUsage.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		AssetListEntryUsage newAssetListEntryUsage = addAssetListEntryUsage();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryUsage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryUsageId",
				newAssetListEntryUsage.getAssetListEntryUsageId()));

		List<AssetListEntryUsage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		AssetListEntryUsage assetListEntryUsage) {

		Assert.assertEquals(
			assetListEntryUsage.getUuid(),
			ReflectionTestUtil.invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryUsage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetListEntryUsage.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryUsage.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			assetListEntryUsage.getContainerKey(),
			ReflectionTestUtil.invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "containerKey"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryUsage.getContainerType()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "containerType"));
		Assert.assertEquals(
			assetListEntryUsage.getKey(),
			ReflectionTestUtil.invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryUsage.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryUsage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
	}

	protected AssetListEntryUsage addAssetListEntryUsage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryUsage assetListEntryUsage = _persistence.create(pk);

		assetListEntryUsage.setCtCollectionId(RandomTestUtil.nextLong());

		assetListEntryUsage.setUuid(RandomTestUtil.randomString());

		assetListEntryUsage.setGroupId(RandomTestUtil.nextLong());

		assetListEntryUsage.setCompanyId(RandomTestUtil.nextLong());

		assetListEntryUsage.setUserId(RandomTestUtil.nextLong());

		assetListEntryUsage.setUserName(RandomTestUtil.randomString());

		assetListEntryUsage.setCreateDate(RandomTestUtil.nextDate());

		assetListEntryUsage.setModifiedDate(RandomTestUtil.nextDate());

		assetListEntryUsage.setClassNameId(RandomTestUtil.nextLong());

		assetListEntryUsage.setContainerKey(RandomTestUtil.randomString());

		assetListEntryUsage.setContainerType(RandomTestUtil.nextLong());

		assetListEntryUsage.setKey(RandomTestUtil.randomString());

		assetListEntryUsage.setPlid(RandomTestUtil.nextLong());

		assetListEntryUsage.setType(RandomTestUtil.nextInt());

		assetListEntryUsage.setLastPublishDate(RandomTestUtil.nextDate());

		_assetListEntryUsages.add(_persistence.update(assetListEntryUsage));

		return assetListEntryUsage;
	}

	private List<AssetListEntryUsage> _assetListEntryUsages =
		new ArrayList<AssetListEntryUsage>();
	private AssetListEntryUsagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1326769940