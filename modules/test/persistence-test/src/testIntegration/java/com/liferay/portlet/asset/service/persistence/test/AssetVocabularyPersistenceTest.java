/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.DuplicateAssetVocabularyExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchVocabularyException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyUtil;
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
public class AssetVocabularyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AssetVocabularyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetVocabulary> iterator = _assetVocabularies.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabulary assetVocabulary = _persistence.create(pk);

		Assert.assertNotNull(assetVocabulary);

		Assert.assertEquals(assetVocabulary.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		_persistence.remove(newAssetVocabulary);

		AssetVocabulary existingAssetVocabulary =
			_persistence.fetchByPrimaryKey(newAssetVocabulary.getPrimaryKey());

		Assert.assertNull(existingAssetVocabulary);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetVocabulary();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabulary newAssetVocabulary = _persistence.create(pk);

		newAssetVocabulary.setMvccVersion(RandomTestUtil.nextLong());

		newAssetVocabulary.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetVocabulary.setUuid(RandomTestUtil.randomString());

		newAssetVocabulary.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newAssetVocabulary.setGroupId(RandomTestUtil.nextLong());

		newAssetVocabulary.setCompanyId(RandomTestUtil.nextLong());

		newAssetVocabulary.setUserId(RandomTestUtil.nextLong());

		newAssetVocabulary.setUserName(RandomTestUtil.randomString());

		newAssetVocabulary.setCreateDate(RandomTestUtil.nextDate());

		newAssetVocabulary.setModifiedDate(RandomTestUtil.nextDate());

		newAssetVocabulary.setName(RandomTestUtil.randomString());

		newAssetVocabulary.setTitle(RandomTestUtil.randomString());

		newAssetVocabulary.setDescription(RandomTestUtil.randomString());

		newAssetVocabulary.setSettings(RandomTestUtil.randomString());

		newAssetVocabulary.setVisibilityType(RandomTestUtil.nextInt());

		newAssetVocabulary.setLastPublishDate(RandomTestUtil.nextDate());

		newAssetVocabulary.setStatus(RandomTestUtil.nextInt());

		_assetVocabularies.add(_persistence.update(newAssetVocabulary));

		AssetVocabulary existingAssetVocabulary = _persistence.findByPrimaryKey(
			newAssetVocabulary.getPrimaryKey());

		Assert.assertEquals(
			existingAssetVocabulary.getMvccVersion(),
			newAssetVocabulary.getMvccVersion());
		Assert.assertEquals(
			existingAssetVocabulary.getCtCollectionId(),
			newAssetVocabulary.getCtCollectionId());
		Assert.assertEquals(
			existingAssetVocabulary.getUuid(), newAssetVocabulary.getUuid());
		Assert.assertEquals(
			existingAssetVocabulary.getExternalReferenceCode(),
			newAssetVocabulary.getExternalReferenceCode());
		Assert.assertEquals(
			existingAssetVocabulary.getVocabularyId(),
			newAssetVocabulary.getVocabularyId());
		Assert.assertEquals(
			existingAssetVocabulary.getGroupId(),
			newAssetVocabulary.getGroupId());
		Assert.assertEquals(
			existingAssetVocabulary.getCompanyId(),
			newAssetVocabulary.getCompanyId());
		Assert.assertEquals(
			existingAssetVocabulary.getUserId(),
			newAssetVocabulary.getUserId());
		Assert.assertEquals(
			existingAssetVocabulary.getUserName(),
			newAssetVocabulary.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetVocabulary.getCreateDate()),
			Time.getShortTimestamp(newAssetVocabulary.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetVocabulary.getModifiedDate()),
			Time.getShortTimestamp(newAssetVocabulary.getModifiedDate()));
		Assert.assertEquals(
			existingAssetVocabulary.getName(), newAssetVocabulary.getName());
		Assert.assertEquals(
			existingAssetVocabulary.getTitle(), newAssetVocabulary.getTitle());
		Assert.assertEquals(
			existingAssetVocabulary.getDescription(),
			newAssetVocabulary.getDescription());
		Assert.assertEquals(
			existingAssetVocabulary.getSettings(),
			newAssetVocabulary.getSettings());
		Assert.assertEquals(
			existingAssetVocabulary.getVisibilityType(),
			newAssetVocabulary.getVisibilityType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetVocabulary.getLastPublishDate()),
			Time.getShortTimestamp(newAssetVocabulary.getLastPublishDate()));
		Assert.assertEquals(
			existingAssetVocabulary.getStatus(),
			newAssetVocabulary.getStatus());
	}

	@Test(
		expected = DuplicateAssetVocabularyExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AssetVocabulary assetVocabulary = addAssetVocabulary();

		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		newAssetVocabulary.setGroupId(assetVocabulary.getGroupId());

		newAssetVocabulary = _persistence.update(newAssetVocabulary);

		Session session = _persistence.getCurrentSession();

		session.evict(newAssetVocabulary);

		newAssetVocabulary.setExternalReferenceCode(
			assetVocabulary.getExternalReferenceCode());

		_persistence.update(newAssetVocabulary);
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
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeN() throws Exception {
		_persistence.countByG_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeN(0L, "null");

		_persistence.countByG_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByG_V() throws Exception {
		_persistence.countByG_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_V(0L, 0);
	}

	@Test
	public void testCountByG_VArrayable() throws Exception {
		_persistence.countByG_V(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		AssetVocabulary existingAssetVocabulary = _persistence.findByPrimaryKey(
			newAssetVocabulary.getPrimaryKey());

		Assert.assertEquals(existingAssetVocabulary, newAssetVocabulary);
	}

	@Test(expected = NoSuchVocabularyException.class)
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

	protected OrderByComparator<AssetVocabulary> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetVocabulary", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "vocabularyId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "name", true,
			"title", true, "description", true, "settings", true,
			"visibilityType", true, "lastPublishDate", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		AssetVocabulary existingAssetVocabulary =
			_persistence.fetchByPrimaryKey(newAssetVocabulary.getPrimaryKey());

		Assert.assertEquals(existingAssetVocabulary, newAssetVocabulary);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabulary missingAssetVocabulary = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingAssetVocabulary);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetVocabulary newAssetVocabulary1 = addAssetVocabulary();
		AssetVocabulary newAssetVocabulary2 = addAssetVocabulary();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabulary1.getPrimaryKey());
		primaryKeys.add(newAssetVocabulary2.getPrimaryKey());

		Map<Serializable, AssetVocabulary> assetVocabularies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetVocabularies.size());
		Assert.assertEquals(
			newAssetVocabulary1,
			assetVocabularies.get(newAssetVocabulary1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetVocabulary2,
			assetVocabularies.get(newAssetVocabulary2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetVocabulary> assetVocabularies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetVocabularies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabulary.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetVocabulary> assetVocabularies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetVocabularies.size());
		Assert.assertEquals(
			newAssetVocabulary,
			assetVocabularies.get(newAssetVocabulary.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetVocabulary> assetVocabularies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetVocabularies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabulary.getPrimaryKey());

		Map<Serializable, AssetVocabulary> assetVocabularies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetVocabularies.size());
		Assert.assertEquals(
			newAssetVocabulary,
			assetVocabularies.get(newAssetVocabulary.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetVocabulary newAssetVocabulary = addAssetVocabulary();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAssetVocabulary.getPrimaryKey()));
	}

	private void _assertOriginalValues(AssetVocabulary assetVocabulary) {
		Assert.assertEquals(
			assetVocabulary.getUuid(),
			ReflectionTestUtil.invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetVocabulary.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetVocabulary.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			assetVocabulary.getName(),
			ReflectionTestUtil.invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			assetVocabulary.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(assetVocabulary.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabulary, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected AssetVocabulary addAssetVocabulary() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabulary assetVocabulary = _persistence.create(pk);

		assetVocabulary.setMvccVersion(RandomTestUtil.nextLong());

		assetVocabulary.setCtCollectionId(RandomTestUtil.nextLong());

		assetVocabulary.setUuid(RandomTestUtil.randomString());

		assetVocabulary.setExternalReferenceCode(RandomTestUtil.randomString());

		assetVocabulary.setGroupId(RandomTestUtil.nextLong());

		assetVocabulary.setCompanyId(RandomTestUtil.nextLong());

		assetVocabulary.setUserId(RandomTestUtil.nextLong());

		assetVocabulary.setUserName(RandomTestUtil.randomString());

		assetVocabulary.setCreateDate(RandomTestUtil.nextDate());

		assetVocabulary.setModifiedDate(RandomTestUtil.nextDate());

		assetVocabulary.setName(RandomTestUtil.randomString());

		assetVocabulary.setTitle(RandomTestUtil.randomString());

		assetVocabulary.setDescription(RandomTestUtil.randomString());

		assetVocabulary.setSettings(RandomTestUtil.randomString());

		assetVocabulary.setVisibilityType(RandomTestUtil.nextInt());

		assetVocabulary.setLastPublishDate(RandomTestUtil.nextDate());

		assetVocabulary.setStatus(RandomTestUtil.nextInt());

		_assetVocabularies.add(_persistence.update(assetVocabulary));

		return assetVocabulary;
	}

	private List<AssetVocabulary> _assetVocabularies =
		new ArrayList<AssetVocabulary>();
	private AssetVocabularyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}