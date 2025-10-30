/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.exception.DuplicateAssetListEntryExternalReferenceCodeException;
import com.liferay.asset.list.exception.NoSuchEntryException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.persistence.AssetListEntryPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class AssetListEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() {
		_persistence = AssetListEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetListEntry> iterator = _assetListEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntry assetListEntry = _persistence.create(pk);

		Assert.assertNotNull(assetListEntry);

		Assert.assertEquals(assetListEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetListEntry newAssetListEntry = addAssetListEntry();

		_persistence.remove(newAssetListEntry);

		AssetListEntry existingAssetListEntry = _persistence.fetchByPrimaryKey(
			newAssetListEntry.getPrimaryKey());

		Assert.assertNull(existingAssetListEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetListEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntry newAssetListEntry = _persistence.create(pk);

		newAssetListEntry.setMvccVersion(RandomTestUtil.nextLong());

		newAssetListEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetListEntry.setUuid(RandomTestUtil.randomString());

		newAssetListEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newAssetListEntry.setGroupId(RandomTestUtil.nextLong());

		newAssetListEntry.setCompanyId(RandomTestUtil.nextLong());

		newAssetListEntry.setUserId(RandomTestUtil.nextLong());

		newAssetListEntry.setUserName(RandomTestUtil.randomString());

		newAssetListEntry.setCreateDate(RandomTestUtil.nextDate());

		newAssetListEntry.setModifiedDate(RandomTestUtil.nextDate());

		newAssetListEntry.setAssetListEntryKey(RandomTestUtil.randomString());

		newAssetListEntry.setTitle(RandomTestUtil.randomString());

		newAssetListEntry.setType(RandomTestUtil.nextInt());

		newAssetListEntry.setAssetEntrySubtype(RandomTestUtil.randomString());

		newAssetListEntry.setAssetEntryType(RandomTestUtil.randomString());

		newAssetListEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_assetListEntries.add(_persistence.update(newAssetListEntry));

		AssetListEntry existingAssetListEntry = _persistence.findByPrimaryKey(
			newAssetListEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntry.getMvccVersion(),
			newAssetListEntry.getMvccVersion());
		Assert.assertEquals(
			existingAssetListEntry.getCtCollectionId(),
			newAssetListEntry.getCtCollectionId());
		Assert.assertEquals(
			existingAssetListEntry.getUuid(), newAssetListEntry.getUuid());
		Assert.assertEquals(
			existingAssetListEntry.getExternalReferenceCode(),
			newAssetListEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingAssetListEntry.getAssetListEntryId(),
			newAssetListEntry.getAssetListEntryId());
		Assert.assertEquals(
			existingAssetListEntry.getGroupId(),
			newAssetListEntry.getGroupId());
		Assert.assertEquals(
			existingAssetListEntry.getCompanyId(),
			newAssetListEntry.getCompanyId());
		Assert.assertEquals(
			existingAssetListEntry.getUserId(), newAssetListEntry.getUserId());
		Assert.assertEquals(
			existingAssetListEntry.getUserName(),
			newAssetListEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetListEntry.getCreateDate()),
			Time.getShortTimestamp(newAssetListEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetListEntry.getModifiedDate()),
			Time.getShortTimestamp(newAssetListEntry.getModifiedDate()));
		Assert.assertEquals(
			existingAssetListEntry.getAssetListEntryKey(),
			newAssetListEntry.getAssetListEntryKey());
		Assert.assertEquals(
			existingAssetListEntry.getTitle(), newAssetListEntry.getTitle());
		Assert.assertEquals(
			existingAssetListEntry.getType(), newAssetListEntry.getType());
		Assert.assertEquals(
			existingAssetListEntry.getAssetEntrySubtype(),
			newAssetListEntry.getAssetEntrySubtype());
		Assert.assertEquals(
			existingAssetListEntry.getAssetEntryType(),
			newAssetListEntry.getAssetEntryType());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetListEntry.getLastPublishDate()),
			Time.getShortTimestamp(newAssetListEntry.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateAssetListEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AssetListEntry assetListEntry = addAssetListEntry();

		AssetListEntry newAssetListEntry = addAssetListEntry();

		newAssetListEntry.setGroupId(assetListEntry.getGroupId());

		newAssetListEntry = _persistence.update(newAssetListEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newAssetListEntry);

		newAssetListEntry.setExternalReferenceCode(
			assetListEntry.getExternalReferenceCode());

		_persistence.update(newAssetListEntry);
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
	public void testCountByG_ALEK() throws Exception {
		_persistence.countByG_ALEK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_ALEK(0L, "null");

		_persistence.countByG_ALEK(0L, (String)null);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeT() throws Exception {
		_persistence.countByG_LikeT(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeT(0L, "null");

		_persistence.countByG_LikeT(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeTArrayable() throws Exception {
		_persistence.countByG_LikeT(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString());
	}

	@Test
	public void testCountByG_TY() throws Exception {
		_persistence.countByG_TY(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_TY(0L, 0);
	}

	@Test
	public void testCountByG_AET() throws Exception {
		_persistence.countByG_AET(RandomTestUtil.nextLong(), "");

		_persistence.countByG_AET(0L, "null");

		_persistence.countByG_AET(0L, (String)null);
	}

	@Test
	public void testCountByG_AETArrayable() throws Exception {
		_persistence.countByG_AET(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByG_LikeT_AET() throws Exception {
		_persistence.countByG_LikeT_AET(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_LikeT_AET(0L, "null", "null");

		_persistence.countByG_LikeT_AET(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_LikeT_AETArrayable() throws Exception {
		_persistence.countByG_LikeT_AET(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByG_AES_AET() throws Exception {
		_persistence.countByG_AES_AET(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_AES_AET(0L, "null", "null");

		_persistence.countByG_AES_AET(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_AES_AETArrayable() throws Exception {
		_persistence.countByG_AES_AET(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testCountByG_LikeT_AES_AET() throws Exception {
		_persistence.countByG_LikeT_AES_AET(
			RandomTestUtil.nextLong(), "", "", "");

		_persistence.countByG_LikeT_AES_AET(0L, "null", "null", "null");

		_persistence.countByG_LikeT_AES_AET(
			0L, (String)null, (String)null, (String)null);
	}

	@Test
	public void testCountByG_LikeT_AES_AETArrayable() throws Exception {
		_persistence.countByG_LikeT_AES_AET(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetListEntry newAssetListEntry = addAssetListEntry();

		AssetListEntry existingAssetListEntry = _persistence.findByPrimaryKey(
			newAssetListEntry.getPrimaryKey());

		Assert.assertEquals(existingAssetListEntry, newAssetListEntry);
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

	protected OrderByComparator<AssetListEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetListEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "assetListEntryId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"assetListEntryKey", true, "title", true, "type", true,
			"assetEntrySubtype", true, "assetEntryType", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetListEntry newAssetListEntry = addAssetListEntry();

		AssetListEntry existingAssetListEntry = _persistence.fetchByPrimaryKey(
			newAssetListEntry.getPrimaryKey());

		Assert.assertEquals(existingAssetListEntry, newAssetListEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntry missingAssetListEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingAssetListEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetListEntry newAssetListEntry1 = addAssetListEntry();
		AssetListEntry newAssetListEntry2 = addAssetListEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntry1.getPrimaryKey());
		primaryKeys.add(newAssetListEntry2.getPrimaryKey());

		Map<Serializable, AssetListEntry> assetListEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetListEntries.size());
		Assert.assertEquals(
			newAssetListEntry1,
			assetListEntries.get(newAssetListEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetListEntry2,
			assetListEntries.get(newAssetListEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetListEntry> assetListEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetListEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetListEntry newAssetListEntry = addAssetListEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetListEntry> assetListEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetListEntries.size());
		Assert.assertEquals(
			newAssetListEntry,
			assetListEntries.get(newAssetListEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetListEntry> assetListEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetListEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetListEntry newAssetListEntry = addAssetListEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntry.getPrimaryKey());

		Map<Serializable, AssetListEntry> assetListEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetListEntries.size());
		Assert.assertEquals(
			newAssetListEntry,
			assetListEntries.get(newAssetListEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetListEntry newAssetListEntry = addAssetListEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAssetListEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(AssetListEntry assetListEntry) {
		Assert.assertEquals(
			assetListEntry.getUuid(),
			ReflectionTestUtil.invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetListEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			assetListEntry.getAssetListEntryKey(),
			ReflectionTestUtil.invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetListEntryKey"));

		Assert.assertEquals(
			Long.valueOf(assetListEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			assetListEntry.getTitle(),
			ReflectionTestUtil.invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "title"));

		Assert.assertEquals(
			assetListEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(assetListEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected AssetListEntry addAssetListEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntry assetListEntry = _persistence.create(pk);

		assetListEntry.setMvccVersion(RandomTestUtil.nextLong());

		assetListEntry.setCtCollectionId(RandomTestUtil.nextLong());

		assetListEntry.setUuid(RandomTestUtil.randomString());

		assetListEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		assetListEntry.setGroupId(RandomTestUtil.nextLong());

		assetListEntry.setCompanyId(RandomTestUtil.nextLong());

		assetListEntry.setUserId(RandomTestUtil.nextLong());

		assetListEntry.setUserName(RandomTestUtil.randomString());

		assetListEntry.setCreateDate(RandomTestUtil.nextDate());

		assetListEntry.setModifiedDate(RandomTestUtil.nextDate());

		assetListEntry.setAssetListEntryKey(RandomTestUtil.randomString());

		assetListEntry.setTitle(RandomTestUtil.randomString());

		assetListEntry.setType(RandomTestUtil.nextInt());

		assetListEntry.setAssetEntrySubtype(RandomTestUtil.randomString());

		assetListEntry.setAssetEntryType(RandomTestUtil.randomString());

		assetListEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_assetListEntries.add(_persistence.update(assetListEntry));

		return assetListEntry;
	}

	private List<AssetListEntry> _assetListEntries =
		new ArrayList<AssetListEntry>();
	private AssetListEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}