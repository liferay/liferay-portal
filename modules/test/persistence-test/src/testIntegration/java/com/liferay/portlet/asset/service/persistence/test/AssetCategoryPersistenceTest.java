/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.DuplicateAssetCategoryExternalReferenceCodeException;
import com.liferay.asset.kernel.exception.NoSuchCategoryException;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetCategoryUtil;
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
public class AssetCategoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AssetCategoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetCategory> iterator = _assetCategories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategory assetCategory = _persistence.create(pk);

		Assert.assertNotNull(assetCategory);

		Assert.assertEquals(assetCategory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetCategory newAssetCategory = addAssetCategory();

		_persistence.remove(newAssetCategory);

		AssetCategory existingAssetCategory = _persistence.fetchByPrimaryKey(
			newAssetCategory.getPrimaryKey());

		Assert.assertNull(existingAssetCategory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetCategory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategory newAssetCategory = _persistence.create(pk);

		newAssetCategory.setMvccVersion(RandomTestUtil.nextLong());

		newAssetCategory.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetCategory.setUuid(RandomTestUtil.randomString());

		newAssetCategory.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newAssetCategory.setGroupId(RandomTestUtil.nextLong());

		newAssetCategory.setCompanyId(RandomTestUtil.nextLong());

		newAssetCategory.setUserId(RandomTestUtil.nextLong());

		newAssetCategory.setUserName(RandomTestUtil.randomString());

		newAssetCategory.setCreateDate(RandomTestUtil.nextDate());

		newAssetCategory.setModifiedDate(RandomTestUtil.nextDate());

		newAssetCategory.setParentCategoryId(RandomTestUtil.nextLong());

		newAssetCategory.setTreePath(RandomTestUtil.randomString());

		newAssetCategory.setName(RandomTestUtil.randomString());

		newAssetCategory.setTitle(RandomTestUtil.randomString());

		newAssetCategory.setDescription(RandomTestUtil.randomString());

		newAssetCategory.setVocabularyId(RandomTestUtil.nextLong());

		newAssetCategory.setLastPublishDate(RandomTestUtil.nextDate());

		newAssetCategory.setStatus(RandomTestUtil.nextInt());

		_assetCategories.add(_persistence.update(newAssetCategory));

		AssetCategory existingAssetCategory = _persistence.findByPrimaryKey(
			newAssetCategory.getPrimaryKey());

		Assert.assertEquals(
			existingAssetCategory.getMvccVersion(),
			newAssetCategory.getMvccVersion());
		Assert.assertEquals(
			existingAssetCategory.getCtCollectionId(),
			newAssetCategory.getCtCollectionId());
		Assert.assertEquals(
			existingAssetCategory.getUuid(), newAssetCategory.getUuid());
		Assert.assertEquals(
			existingAssetCategory.getExternalReferenceCode(),
			newAssetCategory.getExternalReferenceCode());
		Assert.assertEquals(
			existingAssetCategory.getCategoryId(),
			newAssetCategory.getCategoryId());
		Assert.assertEquals(
			existingAssetCategory.getGroupId(), newAssetCategory.getGroupId());
		Assert.assertEquals(
			existingAssetCategory.getCompanyId(),
			newAssetCategory.getCompanyId());
		Assert.assertEquals(
			existingAssetCategory.getUserId(), newAssetCategory.getUserId());
		Assert.assertEquals(
			existingAssetCategory.getUserName(),
			newAssetCategory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetCategory.getCreateDate()),
			Time.getShortTimestamp(newAssetCategory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetCategory.getModifiedDate()),
			Time.getShortTimestamp(newAssetCategory.getModifiedDate()));
		Assert.assertEquals(
			existingAssetCategory.getParentCategoryId(),
			newAssetCategory.getParentCategoryId());
		Assert.assertEquals(
			existingAssetCategory.getTreePath(),
			newAssetCategory.getTreePath());
		Assert.assertEquals(
			existingAssetCategory.getName(), newAssetCategory.getName());
		Assert.assertEquals(
			existingAssetCategory.getTitle(), newAssetCategory.getTitle());
		Assert.assertEquals(
			existingAssetCategory.getDescription(),
			newAssetCategory.getDescription());
		Assert.assertEquals(
			existingAssetCategory.getVocabularyId(),
			newAssetCategory.getVocabularyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetCategory.getLastPublishDate()),
			Time.getShortTimestamp(newAssetCategory.getLastPublishDate()));
		Assert.assertEquals(
			existingAssetCategory.getStatus(), newAssetCategory.getStatus());
	}

	@Test(expected = DuplicateAssetCategoryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AssetCategory assetCategory = addAssetCategory();

		AssetCategory newAssetCategory = addAssetCategory();

		newAssetCategory.setGroupId(assetCategory.getGroupId());

		newAssetCategory = _persistence.update(newAssetCategory);

		Session session = _persistence.getCurrentSession();

		session.evict(newAssetCategory);

		newAssetCategory.setExternalReferenceCode(
			assetCategory.getExternalReferenceCode());

		_persistence.update(newAssetCategory);
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
	public void testCountByParentCategoryId() throws Exception {
		_persistence.countByParentCategoryId(RandomTestUtil.nextLong());

		_persistence.countByParentCategoryId(0L);
	}

	@Test
	public void testCountByVocabularyId() throws Exception {
		_persistence.countByVocabularyId(RandomTestUtil.nextLong());

		_persistence.countByVocabularyId(0L);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByG_V() throws Exception {
		_persistence.countByG_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_V(0L, 0L);
	}

	@Test
	public void testCountByG_VArrayable() throws Exception {
		_persistence.countByG_V(
			new long[] {RandomTestUtil.nextLong(), 0L},
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByP_N() throws Exception {
		_persistence.countByP_N(RandomTestUtil.nextLong(), "");

		_persistence.countByP_N(0L, "null");

		_persistence.countByP_N(0L, (String)null);
	}

	@Test
	public void testCountByP_V() throws Exception {
		_persistence.countByP_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByP_V(0L, 0L);
	}

	@Test
	public void testCountByN_V() throws Exception {
		_persistence.countByN_V("", RandomTestUtil.nextLong());

		_persistence.countByN_V("null", 0L);

		_persistence.countByN_V((String)null, 0L);
	}

	@Test
	public void testCountByG_P_V() throws Exception {
		_persistence.countByG_P_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_P_V(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_LikeT_V() throws Exception {
		_persistence.countByG_LikeT_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByG_LikeT_V(0L, "null", 0L);

		_persistence.countByG_LikeT_V(0L, (String)null, 0L);
	}

	@Test
	public void testCountByG_LikeN_V() throws Exception {
		_persistence.countByG_LikeN_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByG_LikeN_V(0L, "null", 0L);

		_persistence.countByG_LikeN_V(0L, (String)null, 0L);
	}

	@Test
	public void testCountByG_LikeN_VArrayable() throws Exception {
		_persistence.countByG_LikeN_V(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByP_N_V() throws Exception {
		_persistence.countByP_N_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByP_N_V(0L, "null", 0L);

		_persistence.countByP_N_V(0L, (String)null, 0L);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetCategory newAssetCategory = addAssetCategory();

		AssetCategory existingAssetCategory = _persistence.findByPrimaryKey(
			newAssetCategory.getPrimaryKey());

		Assert.assertEquals(existingAssetCategory, newAssetCategory);
	}

	@Test(expected = NoSuchCategoryException.class)
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

	protected OrderByComparator<AssetCategory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetCategory", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "categoryId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "parentCategoryId",
			true, "treePath", true, "name", true, "vocabularyId", true,
			"lastPublishDate", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetCategory newAssetCategory = addAssetCategory();

		AssetCategory existingAssetCategory = _persistence.fetchByPrimaryKey(
			newAssetCategory.getPrimaryKey());

		Assert.assertEquals(existingAssetCategory, newAssetCategory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategory missingAssetCategory = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetCategory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetCategory newAssetCategory1 = addAssetCategory();
		AssetCategory newAssetCategory2 = addAssetCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategory1.getPrimaryKey());
		primaryKeys.add(newAssetCategory2.getPrimaryKey());

		Map<Serializable, AssetCategory> assetCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetCategories.size());
		Assert.assertEquals(
			newAssetCategory1,
			assetCategories.get(newAssetCategory1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetCategory2,
			assetCategories.get(newAssetCategory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetCategory> assetCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetCategory newAssetCategory = addAssetCategory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetCategory> assetCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetCategories.size());
		Assert.assertEquals(
			newAssetCategory,
			assetCategories.get(newAssetCategory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetCategory> assetCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetCategory newAssetCategory = addAssetCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategory.getPrimaryKey());

		Map<Serializable, AssetCategory> assetCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetCategories.size());
		Assert.assertEquals(
			newAssetCategory,
			assetCategories.get(newAssetCategory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetCategory newAssetCategory = addAssetCategory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAssetCategory.getPrimaryKey()));
	}

	private void _assertOriginalValues(AssetCategory assetCategory) {
		Assert.assertEquals(
			assetCategory.getUuid(),
			ReflectionTestUtil.invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetCategory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetCategory.getParentCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentCategoryId"));
		Assert.assertEquals(
			assetCategory.getName(),
			ReflectionTestUtil.invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Long.valueOf(assetCategory.getVocabularyId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "vocabularyId"));

		Assert.assertEquals(
			assetCategory.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(assetCategory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected AssetCategory addAssetCategory() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategory assetCategory = _persistence.create(pk);

		assetCategory.setMvccVersion(RandomTestUtil.nextLong());

		assetCategory.setCtCollectionId(RandomTestUtil.nextLong());

		assetCategory.setUuid(RandomTestUtil.randomString());

		assetCategory.setExternalReferenceCode(RandomTestUtil.randomString());

		assetCategory.setGroupId(RandomTestUtil.nextLong());

		assetCategory.setCompanyId(RandomTestUtil.nextLong());

		assetCategory.setUserId(RandomTestUtil.nextLong());

		assetCategory.setUserName(RandomTestUtil.randomString());

		assetCategory.setCreateDate(RandomTestUtil.nextDate());

		assetCategory.setModifiedDate(RandomTestUtil.nextDate());

		assetCategory.setParentCategoryId(RandomTestUtil.nextLong());

		assetCategory.setTreePath(RandomTestUtil.randomString());

		assetCategory.setName(RandomTestUtil.randomString());

		assetCategory.setTitle(RandomTestUtil.randomString());

		assetCategory.setDescription(RandomTestUtil.randomString());

		assetCategory.setVocabularyId(RandomTestUtil.nextLong());

		assetCategory.setLastPublishDate(RandomTestUtil.nextDate());

		assetCategory.setStatus(RandomTestUtil.nextInt());

		_assetCategories.add(_persistence.update(assetCategory));

		return assetCategory;
	}

	private List<AssetCategory> _assetCategories =
		new ArrayList<AssetCategory>();
	private AssetCategoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}