/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.auto.tagger.exception.NoSuchEntryException;
import com.liferay.asset.auto.tagger.model.AssetAutoTaggerEntry;
import com.liferay.asset.auto.tagger.service.AssetAutoTaggerEntryLocalServiceUtil;
import com.liferay.asset.auto.tagger.service.persistence.AssetAutoTaggerEntryPersistence;
import com.liferay.asset.auto.tagger.service.persistence.AssetAutoTaggerEntryUtil;
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
public class AssetAutoTaggerEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.auto.tagger.service"));

	@Before
	public void setUp() {
		_persistence = AssetAutoTaggerEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetAutoTaggerEntry> iterator =
			_assetAutoTaggerEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetAutoTaggerEntry assetAutoTaggerEntry = _persistence.create(pk);

		Assert.assertNotNull(assetAutoTaggerEntry);

		Assert.assertEquals(assetAutoTaggerEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		_persistence.remove(newAssetAutoTaggerEntry);

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry =
			_persistence.fetchByPrimaryKey(
				newAssetAutoTaggerEntry.getPrimaryKey());

		Assert.assertNull(existingAssetAutoTaggerEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetAutoTaggerEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		newAssetAutoTaggerEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetAutoTaggerEntry.setGroupId(RandomTestUtil.nextLong());

		newAssetAutoTaggerEntry.setCompanyId(RandomTestUtil.nextLong());

		newAssetAutoTaggerEntry.setCreateDate(RandomTestUtil.nextDate());

		newAssetAutoTaggerEntry.setModifiedDate(RandomTestUtil.nextDate());

		newAssetAutoTaggerEntry.setAssetEntryId(RandomTestUtil.nextLong());

		newAssetAutoTaggerEntry.setAssetTagId(RandomTestUtil.nextLong());

		newAssetAutoTaggerEntry = _persistence.update(newAssetAutoTaggerEntry);

		_assetAutoTaggerEntries.add(newAssetAutoTaggerEntry);

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry =
			_persistence.findByPrimaryKey(
				newAssetAutoTaggerEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getMvccVersion(),
			newAssetAutoTaggerEntry.getMvccVersion());
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getCtCollectionId(),
			newAssetAutoTaggerEntry.getCtCollectionId());
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getAssetAutoTaggerEntryId(),
			newAssetAutoTaggerEntry.getAssetAutoTaggerEntryId());
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getGroupId(),
			newAssetAutoTaggerEntry.getGroupId());
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getCompanyId(),
			newAssetAutoTaggerEntry.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetAutoTaggerEntry.getCreateDate()),
			Time.getShortTimestamp(newAssetAutoTaggerEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetAutoTaggerEntry.getModifiedDate()),
			Time.getShortTimestamp(newAssetAutoTaggerEntry.getModifiedDate()));
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getAssetEntryId(),
			newAssetAutoTaggerEntry.getAssetEntryId());
		Assert.assertEquals(
			existingAssetAutoTaggerEntry.getAssetTagId(),
			newAssetAutoTaggerEntry.getAssetTagId());
	}

	@Test
	public void testCountByAssetEntryId() throws Exception {
		_persistence.countByAssetEntryId(RandomTestUtil.nextLong());

		_persistence.countByAssetEntryId(0L);
	}

	@Test
	public void testCountByAssetTagId() throws Exception {
		_persistence.countByAssetTagId(RandomTestUtil.nextLong());

		_persistence.countByAssetTagId(0L);
	}

	@Test
	public void testCountByA_A() throws Exception {
		_persistence.countByA_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_A(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry =
			_persistence.findByPrimaryKey(
				newAssetAutoTaggerEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAssetAutoTaggerEntry, newAssetAutoTaggerEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetAutoTaggerEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetAutoTaggerEntry", "mvccVersion", true, "ctCollectionId", true,
			"assetAutoTaggerEntryId", true, "groupId", true, "companyId", true,
			"createDate", true, "modifiedDate", true, "assetEntryId", true,
			"assetTagId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry =
			_persistence.fetchByPrimaryKey(
				newAssetAutoTaggerEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAssetAutoTaggerEntry, newAssetAutoTaggerEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetAutoTaggerEntry missingAssetAutoTaggerEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetAutoTaggerEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetAutoTaggerEntry newAssetAutoTaggerEntry1 =
			addAssetAutoTaggerEntry();
		AssetAutoTaggerEntry newAssetAutoTaggerEntry2 =
			addAssetAutoTaggerEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetAutoTaggerEntry1.getPrimaryKey());
		primaryKeys.add(newAssetAutoTaggerEntry2.getPrimaryKey());

		Map<Serializable, AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetAutoTaggerEntries.size());
		Assert.assertEquals(
			newAssetAutoTaggerEntry1,
			assetAutoTaggerEntries.get(
				newAssetAutoTaggerEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetAutoTaggerEntry2,
			assetAutoTaggerEntries.get(
				newAssetAutoTaggerEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetAutoTaggerEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetAutoTaggerEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetAutoTaggerEntries.size());
		Assert.assertEquals(
			newAssetAutoTaggerEntry,
			assetAutoTaggerEntries.get(
				newAssetAutoTaggerEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetAutoTaggerEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetAutoTaggerEntry.getPrimaryKey());

		Map<Serializable, AssetAutoTaggerEntry> assetAutoTaggerEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetAutoTaggerEntries.size());
		Assert.assertEquals(
			newAssetAutoTaggerEntry,
			assetAutoTaggerEntries.get(
				newAssetAutoTaggerEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AssetAutoTaggerEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<AssetAutoTaggerEntry>() {

				@Override
				public void performAction(
					AssetAutoTaggerEntry assetAutoTaggerEntry) {

					Assert.assertNotNull(assetAutoTaggerEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetAutoTaggerEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetAutoTaggerEntryId",
				newAssetAutoTaggerEntry.getAssetAutoTaggerEntryId()));

		List<AssetAutoTaggerEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AssetAutoTaggerEntry existingAssetAutoTaggerEntry = result.get(0);

		Assert.assertEquals(
			existingAssetAutoTaggerEntry, newAssetAutoTaggerEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetAutoTaggerEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetAutoTaggerEntryId", RandomTestUtil.nextLong()));

		List<AssetAutoTaggerEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetAutoTaggerEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetAutoTaggerEntryId"));

		Object newAssetAutoTaggerEntryId =
			newAssetAutoTaggerEntry.getAssetAutoTaggerEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetAutoTaggerEntryId",
				new Object[] {newAssetAutoTaggerEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAssetAutoTaggerEntryId = result.get(0);

		Assert.assertEquals(
			existingAssetAutoTaggerEntryId, newAssetAutoTaggerEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetAutoTaggerEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("assetAutoTaggerEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"assetAutoTaggerEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetAutoTaggerEntry.getPrimaryKey()));
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

		AssetAutoTaggerEntry newAssetAutoTaggerEntry =
			addAssetAutoTaggerEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AssetAutoTaggerEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"assetAutoTaggerEntryId",
				newAssetAutoTaggerEntry.getAssetAutoTaggerEntryId()));

		List<AssetAutoTaggerEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		AssetAutoTaggerEntry assetAutoTaggerEntry) {

		Assert.assertEquals(
			Long.valueOf(assetAutoTaggerEntry.getAssetEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetAutoTaggerEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetEntryId"));
		Assert.assertEquals(
			Long.valueOf(assetAutoTaggerEntry.getAssetTagId()),
			ReflectionTestUtil.<Long>invoke(
				assetAutoTaggerEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetTagId"));
	}

	protected AssetAutoTaggerEntry addAssetAutoTaggerEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetAutoTaggerEntry assetAutoTaggerEntry = _persistence.create(pk);

		assetAutoTaggerEntry.setCtCollectionId(RandomTestUtil.nextLong());

		assetAutoTaggerEntry.setGroupId(RandomTestUtil.nextLong());

		assetAutoTaggerEntry.setCompanyId(RandomTestUtil.nextLong());

		assetAutoTaggerEntry.setCreateDate(RandomTestUtil.nextDate());

		assetAutoTaggerEntry.setModifiedDate(RandomTestUtil.nextDate());

		assetAutoTaggerEntry.setAssetEntryId(RandomTestUtil.nextLong());

		assetAutoTaggerEntry.setAssetTagId(RandomTestUtil.nextLong());

		_assetAutoTaggerEntries.add(_persistence.update(assetAutoTaggerEntry));

		return assetAutoTaggerEntry;
	}

	private List<AssetAutoTaggerEntry> _assetAutoTaggerEntries =
		new ArrayList<AssetAutoTaggerEntry>();
	private AssetAutoTaggerEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1036378817