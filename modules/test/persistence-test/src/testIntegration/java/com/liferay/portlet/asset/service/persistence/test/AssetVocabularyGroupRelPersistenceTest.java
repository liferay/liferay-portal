/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchVocabularyGroupRelException;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyGroupRelPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyGroupRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class AssetVocabularyGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AssetVocabularyGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetVocabularyGroupRel> iterator =
			_assetVocabularyGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabularyGroupRel assetVocabularyGroupRel = _persistence.create(
			pk);

		Assert.assertNotNull(assetVocabularyGroupRel);

		Assert.assertEquals(assetVocabularyGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		_persistence.remove(newAssetVocabularyGroupRel);

		AssetVocabularyGroupRel existingAssetVocabularyGroupRel =
			_persistence.fetchByPrimaryKey(
				newAssetVocabularyGroupRel.getPrimaryKey());

		Assert.assertNull(existingAssetVocabularyGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetVocabularyGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			_persistence.create(pk);

		newAssetVocabularyGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		newAssetVocabularyGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetVocabularyGroupRel.setUuid(RandomTestUtil.randomString());

		newAssetVocabularyGroupRel.setGroupId(RandomTestUtil.nextLong());

		newAssetVocabularyGroupRel.setCompanyId(RandomTestUtil.nextLong());

		newAssetVocabularyGroupRel.setVocabularyId(RandomTestUtil.nextLong());

		_assetVocabularyGroupRels.add(
			_persistence.update(newAssetVocabularyGroupRel));

		AssetVocabularyGroupRel existingAssetVocabularyGroupRel =
			_persistence.findByPrimaryKey(
				newAssetVocabularyGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getMvccVersion(),
			newAssetVocabularyGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getCtCollectionId(),
			newAssetVocabularyGroupRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getUuid(),
			newAssetVocabularyGroupRel.getUuid());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getAssetVocabularyGroupRelId(),
			newAssetVocabularyGroupRel.getAssetVocabularyGroupRelId());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getGroupId(),
			newAssetVocabularyGroupRel.getGroupId());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getCompanyId(),
			newAssetVocabularyGroupRel.getCompanyId());
		Assert.assertEquals(
			existingAssetVocabularyGroupRel.getVocabularyId(),
			newAssetVocabularyGroupRel.getVocabularyId());
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
	public void testCountByVocabularyId() throws Exception {
		_persistence.countByVocabularyId(RandomTestUtil.nextLong());

		_persistence.countByVocabularyId(0L);
	}

	@Test
	public void testCountByG_V() throws Exception {
		_persistence.countByG_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_V(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		AssetVocabularyGroupRel existingAssetVocabularyGroupRel =
			_persistence.findByPrimaryKey(
				newAssetVocabularyGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetVocabularyGroupRel, newAssetVocabularyGroupRel);
	}

	@Test(expected = NoSuchVocabularyGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetVocabularyGroupRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AssetVocabularyGroupRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "assetVocabularyGroupRelId", true, "groupId",
			true, "companyId", true, "vocabularyId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		AssetVocabularyGroupRel existingAssetVocabularyGroupRel =
			_persistence.fetchByPrimaryKey(
				newAssetVocabularyGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetVocabularyGroupRel, newAssetVocabularyGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetVocabularyGroupRel missingAssetVocabularyGroupRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetVocabularyGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetVocabularyGroupRel newAssetVocabularyGroupRel1 =
			addAssetVocabularyGroupRel();
		AssetVocabularyGroupRel newAssetVocabularyGroupRel2 =
			addAssetVocabularyGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabularyGroupRel1.getPrimaryKey());
		primaryKeys.add(newAssetVocabularyGroupRel2.getPrimaryKey());

		Map<Serializable, AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetVocabularyGroupRels.size());
		Assert.assertEquals(
			newAssetVocabularyGroupRel1,
			assetVocabularyGroupRels.get(
				newAssetVocabularyGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetVocabularyGroupRel2,
			assetVocabularyGroupRels.get(
				newAssetVocabularyGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetVocabularyGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabularyGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetVocabularyGroupRels.size());
		Assert.assertEquals(
			newAssetVocabularyGroupRel,
			assetVocabularyGroupRels.get(
				newAssetVocabularyGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetVocabularyGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetVocabularyGroupRel.getPrimaryKey());

		Map<Serializable, AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetVocabularyGroupRels.size());
		Assert.assertEquals(
			newAssetVocabularyGroupRel,
			assetVocabularyGroupRels.get(
				newAssetVocabularyGroupRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetVocabularyGroupRel newAssetVocabularyGroupRel =
			addAssetVocabularyGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetVocabularyGroupRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AssetVocabularyGroupRel assetVocabularyGroupRel) {

		Assert.assertEquals(
			assetVocabularyGroupRel.getUuid(),
			ReflectionTestUtil.invoke(
				assetVocabularyGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetVocabularyGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabularyGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetVocabularyGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabularyGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(assetVocabularyGroupRel.getVocabularyId()),
			ReflectionTestUtil.<Long>invoke(
				assetVocabularyGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "vocabularyId"));
	}

	protected AssetVocabularyGroupRel addAssetVocabularyGroupRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetVocabularyGroupRel assetVocabularyGroupRel = _persistence.create(
			pk);

		assetVocabularyGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		assetVocabularyGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		assetVocabularyGroupRel.setUuid(RandomTestUtil.randomString());

		assetVocabularyGroupRel.setGroupId(RandomTestUtil.nextLong());

		assetVocabularyGroupRel.setCompanyId(RandomTestUtil.nextLong());

		assetVocabularyGroupRel.setVocabularyId(RandomTestUtil.nextLong());

		_assetVocabularyGroupRels.add(
			_persistence.update(assetVocabularyGroupRel));

		return assetVocabularyGroupRel;
	}

	private List<AssetVocabularyGroupRel> _assetVocabularyGroupRels =
		new ArrayList<AssetVocabularyGroupRel>();
	private AssetVocabularyGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}