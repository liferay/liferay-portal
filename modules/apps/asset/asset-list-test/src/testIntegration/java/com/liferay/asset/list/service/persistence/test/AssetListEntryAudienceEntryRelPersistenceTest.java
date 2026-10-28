/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.exception.NoSuchEntryAudienceEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalServiceUtil;
import com.liferay.asset.list.service.persistence.AssetListEntryAudienceEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryAudienceEntryRelUtil;
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
public class AssetListEntryAudienceEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() {
		_persistence = AssetListEntryAudienceEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetListEntryAudienceEntryRel> iterator =
			_assetListEntryAudienceEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(assetListEntryAudienceEntryRel);

		Assert.assertEquals(assetListEntryAudienceEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		_persistence.remove(newAssetListEntryAudienceEntryRel);

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryAudienceEntryRel.getPrimaryKey());

		Assert.assertNull(existingAssetListEntryAudienceEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetListEntryAudienceEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		newAssetListEntryAudienceEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newAssetListEntryAudienceEntryRel.setUuid(
			RandomTestUtil.randomString());

		newAssetListEntryAudienceEntryRel.setGroupId(RandomTestUtil.nextLong());

		newAssetListEntryAudienceEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		newAssetListEntryAudienceEntryRel.setUserId(RandomTestUtil.nextLong());

		newAssetListEntryAudienceEntryRel.setUserName(
			RandomTestUtil.randomString());

		newAssetListEntryAudienceEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		newAssetListEntryAudienceEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newAssetListEntryAudienceEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntryAudienceEntryRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		newAssetListEntryAudienceEntryRel.setPriority(RandomTestUtil.nextInt());

		newAssetListEntryAudienceEntryRel.setTypeSettings(
			RandomTestUtil.randomString());

		newAssetListEntryAudienceEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		newAssetListEntryAudienceEntryRel = _persistence.update(
			newAssetListEntryAudienceEntryRel);

		_assetListEntryAudienceEntryRels.add(newAssetListEntryAudienceEntryRel);

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntryAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getMvccVersion(),
			newAssetListEntryAudienceEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getCtCollectionId(),
			newAssetListEntryAudienceEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getUuid(),
			newAssetListEntryAudienceEntryRel.getUuid());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.
				getAssetListEntryAudienceEntryRelId(),
			newAssetListEntryAudienceEntryRel.
				getAssetListEntryAudienceEntryRelId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getGroupId(),
			newAssetListEntryAudienceEntryRel.getGroupId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getCompanyId(),
			newAssetListEntryAudienceEntryRel.getCompanyId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getUserId(),
			newAssetListEntryAudienceEntryRel.getUserId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getUserName(),
			newAssetListEntryAudienceEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAudienceEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newAssetListEntryAudienceEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAudienceEntryRel.getModifiedDate()),
			Time.getShortTimestamp(
				newAssetListEntryAudienceEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getAssetListEntryId(),
			newAssetListEntryAudienceEntryRel.getAssetListEntryId());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getAudienceEntryERC(),
			newAssetListEntryAudienceEntryRel.getAudienceEntryERC());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getPriority(),
			newAssetListEntryAudienceEntryRel.getPriority());
		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel.getTypeSettings(),
			newAssetListEntryAudienceEntryRel.getTypeSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAudienceEntryRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newAssetListEntryAudienceEntryRel.getLastPublishDate()));
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
	public void testCountByAssetListEntryId() throws Exception {
		_persistence.countByAssetListEntryId(RandomTestUtil.nextLong());

		_persistence.countByAssetListEntryId(0L);
	}

	@Test
	public void testCountByC_AEERC() throws Exception {
		_persistence.countByC_AEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByC_AEERC(0L, "null");

		_persistence.countByC_AEERC(0L, (String)null);
	}

	@Test
	public void testCountByA_AEERC() throws Exception {
		_persistence.countByA_AEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByA_AEERC(0L, "null");

		_persistence.countByA_AEERC(0L, (String)null);
	}

	@Test
	public void testCountByA_AEERC_C() throws Exception {
		_persistence.countByA_AEERC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByA_AEERC_C(0L, "null");

		_persistence.countByA_AEERC_C(0L, (String)null);
	}

	@Test
	public void testCountByA_AEERC_CArrayable() throws Exception {
		_persistence.countByA_AEERC_C(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntryAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel,
			newAssetListEntryAudienceEntryRel);
	}

	@Test(expected = NoSuchEntryAudienceEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetListEntryAudienceEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AssetListEntryAudienceEntryRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"assetListEntryAudienceEntryRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "assetListEntryId", true,
			"audienceEntryERC", true, "priority", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel,
			newAssetListEntryAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryAudienceEntryRel missingAssetListEntryAudienceEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetListEntryAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel1 =
			addAssetListEntryAudienceEntryRel();
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel2 =
			addAssetListEntryAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAudienceEntryRel1.getPrimaryKey());
		primaryKeys.add(newAssetListEntryAudienceEntryRel2.getPrimaryKey());

		Map<Serializable, AssetListEntryAudienceEntryRel>
			assetListEntryAudienceEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, assetListEntryAudienceEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAudienceEntryRel1,
			assetListEntryAudienceEntryRels.get(
				newAssetListEntryAudienceEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetListEntryAudienceEntryRel2,
			assetListEntryAudienceEntryRels.get(
				newAssetListEntryAudienceEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetListEntryAudienceEntryRel>
			assetListEntryAudienceEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntryAudienceEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAudienceEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetListEntryAudienceEntryRel>
			assetListEntryAudienceEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntryAudienceEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAudienceEntryRel,
			assetListEntryAudienceEntryRels.get(
				newAssetListEntryAudienceEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetListEntryAudienceEntryRel>
			assetListEntryAudienceEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntryAudienceEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAudienceEntryRel.getPrimaryKey());

		Map<Serializable, AssetListEntryAudienceEntryRel>
			assetListEntryAudienceEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntryAudienceEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAudienceEntryRel,
			assetListEntryAudienceEntryRels.get(
				newAssetListEntryAudienceEntryRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AssetListEntryAudienceEntryRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<AssetListEntryAudienceEntryRel>() {

				@Override
				public void performAction(
					AssetListEntryAudienceEntryRel
						assetListEntryAudienceEntryRel) {

					Assert.assertNotNull(assetListEntryAudienceEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryAudienceEntryRelId",
				newAssetListEntryAudienceEntryRel.
					getAssetListEntryAudienceEntryRelId()));

		List<AssetListEntryAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		AssetListEntryAudienceEntryRel existingAssetListEntryAudienceEntryRel =
			result.get(0);

		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRel,
			newAssetListEntryAudienceEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryAudienceEntryRelId", RandomTestUtil.nextLong()));

		List<AssetListEntryAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetListEntryAudienceEntryRelId"));

		Object newAssetListEntryAudienceEntryRelId =
			newAssetListEntryAudienceEntryRel.
				getAssetListEntryAudienceEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetListEntryAudienceEntryRelId",
				new Object[] {newAssetListEntryAudienceEntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAssetListEntryAudienceEntryRelId = result.get(0);

		Assert.assertEquals(
			existingAssetListEntryAudienceEntryRelId,
			newAssetListEntryAudienceEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetListEntryAudienceEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetListEntryAudienceEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetListEntryAudienceEntryRel.getPrimaryKey()));
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

		AssetListEntryAudienceEntryRel newAssetListEntryAudienceEntryRel =
			addAssetListEntryAudienceEntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetListEntryAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetListEntryAudienceEntryRelId",
				newAssetListEntryAudienceEntryRel.
					getAssetListEntryAudienceEntryRelId()));

		List<AssetListEntryAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel) {

		Assert.assertEquals(
			assetListEntryAudienceEntryRel.getUuid(),
			ReflectionTestUtil.invoke(
				assetListEntryAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryAudienceEntryRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetListEntryAudienceEntryRel.getAssetListEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetListEntryId"));
		Assert.assertEquals(
			assetListEntryAudienceEntryRel.getAudienceEntryERC(),
			ReflectionTestUtil.invoke(
				assetListEntryAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "audienceEntryERC"));
	}

	protected AssetListEntryAudienceEntryRel addAssetListEntryAudienceEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			_persistence.create(pk);

		assetListEntryAudienceEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		assetListEntryAudienceEntryRel.setUuid(RandomTestUtil.randomString());

		assetListEntryAudienceEntryRel.setGroupId(RandomTestUtil.nextLong());

		assetListEntryAudienceEntryRel.setCompanyId(RandomTestUtil.nextLong());

		assetListEntryAudienceEntryRel.setUserId(RandomTestUtil.nextLong());

		assetListEntryAudienceEntryRel.setUserName(
			RandomTestUtil.randomString());

		assetListEntryAudienceEntryRel.setCreateDate(RandomTestUtil.nextDate());

		assetListEntryAudienceEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		assetListEntryAudienceEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		assetListEntryAudienceEntryRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		assetListEntryAudienceEntryRel.setPriority(RandomTestUtil.nextInt());

		assetListEntryAudienceEntryRel.setTypeSettings(
			RandomTestUtil.randomString());

		assetListEntryAudienceEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_assetListEntryAudienceEntryRels.add(
			_persistence.update(assetListEntryAudienceEntryRel));

		return assetListEntryAudienceEntryRel;
	}

	private List<AssetListEntryAudienceEntryRel>
		_assetListEntryAudienceEntryRels =
			new ArrayList<AssetListEntryAudienceEntryRel>();
	private AssetListEntryAudienceEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1431954237