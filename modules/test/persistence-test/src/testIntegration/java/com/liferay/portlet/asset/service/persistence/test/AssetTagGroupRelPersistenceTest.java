/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.asset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.NoSuchTagGroupRelException;
import com.liferay.asset.kernel.model.AssetTagGroupRel;
import com.liferay.asset.kernel.service.persistence.AssetTagGroupRelPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagGroupRelUtil;
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
public class AssetTagGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AssetTagGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetTagGroupRel> iterator = _assetTagGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetTagGroupRel assetTagGroupRel = _persistence.create(pk);

		Assert.assertNotNull(assetTagGroupRel);

		Assert.assertEquals(assetTagGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		_persistence.remove(newAssetTagGroupRel);

		AssetTagGroupRel existingAssetTagGroupRel =
			_persistence.fetchByPrimaryKey(newAssetTagGroupRel.getPrimaryKey());

		Assert.assertNull(existingAssetTagGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetTagGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetTagGroupRel newAssetTagGroupRel = _persistence.create(pk);

		newAssetTagGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		newAssetTagGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetTagGroupRel.setUuid(RandomTestUtil.randomString());

		newAssetTagGroupRel.setGroupId(RandomTestUtil.nextLong());

		newAssetTagGroupRel.setCompanyId(RandomTestUtil.nextLong());

		newAssetTagGroupRel.setTagId(RandomTestUtil.nextLong());

		_assetTagGroupRels.add(_persistence.update(newAssetTagGroupRel));

		AssetTagGroupRel existingAssetTagGroupRel =
			_persistence.findByPrimaryKey(newAssetTagGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetTagGroupRel.getMvccVersion(),
			newAssetTagGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetTagGroupRel.getCtCollectionId(),
			newAssetTagGroupRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetTagGroupRel.getUuid(), newAssetTagGroupRel.getUuid());
		Assert.assertEquals(
			existingAssetTagGroupRel.getAssetTagGroupRelId(),
			newAssetTagGroupRel.getAssetTagGroupRelId());
		Assert.assertEquals(
			existingAssetTagGroupRel.getGroupId(),
			newAssetTagGroupRel.getGroupId());
		Assert.assertEquals(
			existingAssetTagGroupRel.getCompanyId(),
			newAssetTagGroupRel.getCompanyId());
		Assert.assertEquals(
			existingAssetTagGroupRel.getTagId(),
			newAssetTagGroupRel.getTagId());
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
	public void testCountByTagId() throws Exception {
		_persistence.countByTagId(RandomTestUtil.nextLong());

		_persistence.countByTagId(0L);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_T(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		AssetTagGroupRel existingAssetTagGroupRel =
			_persistence.findByPrimaryKey(newAssetTagGroupRel.getPrimaryKey());

		Assert.assertEquals(existingAssetTagGroupRel, newAssetTagGroupRel);
	}

	@Test(expected = NoSuchTagGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetTagGroupRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetTagGroupRel", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "assetTagGroupRelId", true, "groupId", true,
			"companyId", true, "tagId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		AssetTagGroupRel existingAssetTagGroupRel =
			_persistence.fetchByPrimaryKey(newAssetTagGroupRel.getPrimaryKey());

		Assert.assertEquals(existingAssetTagGroupRel, newAssetTagGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetTagGroupRel missingAssetTagGroupRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetTagGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetTagGroupRel newAssetTagGroupRel1 = addAssetTagGroupRel();
		AssetTagGroupRel newAssetTagGroupRel2 = addAssetTagGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetTagGroupRel1.getPrimaryKey());
		primaryKeys.add(newAssetTagGroupRel2.getPrimaryKey());

		Map<Serializable, AssetTagGroupRel> assetTagGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetTagGroupRels.size());
		Assert.assertEquals(
			newAssetTagGroupRel1,
			assetTagGroupRels.get(newAssetTagGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetTagGroupRel2,
			assetTagGroupRels.get(newAssetTagGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetTagGroupRel> assetTagGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetTagGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetTagGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetTagGroupRel> assetTagGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetTagGroupRels.size());
		Assert.assertEquals(
			newAssetTagGroupRel,
			assetTagGroupRels.get(newAssetTagGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetTagGroupRel> assetTagGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetTagGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetTagGroupRel.getPrimaryKey());

		Map<Serializable, AssetTagGroupRel> assetTagGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetTagGroupRels.size());
		Assert.assertEquals(
			newAssetTagGroupRel,
			assetTagGroupRels.get(newAssetTagGroupRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetTagGroupRel newAssetTagGroupRel = addAssetTagGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAssetTagGroupRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(AssetTagGroupRel assetTagGroupRel) {
		Assert.assertEquals(
			assetTagGroupRel.getUuid(),
			ReflectionTestUtil.invoke(
				assetTagGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetTagGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetTagGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetTagGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetTagGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(assetTagGroupRel.getTagId()),
			ReflectionTestUtil.<Long>invoke(
				assetTagGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "tagId"));
	}

	protected AssetTagGroupRel addAssetTagGroupRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetTagGroupRel assetTagGroupRel = _persistence.create(pk);

		assetTagGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		assetTagGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		assetTagGroupRel.setUuid(RandomTestUtil.randomString());

		assetTagGroupRel.setGroupId(RandomTestUtil.nextLong());

		assetTagGroupRel.setCompanyId(RandomTestUtil.nextLong());

		assetTagGroupRel.setTagId(RandomTestUtil.nextLong());

		_assetTagGroupRels.add(_persistence.update(assetTagGroupRel));

		return assetTagGroupRel;
	}

	private List<AssetTagGroupRel> _assetTagGroupRels =
		new ArrayList<AssetTagGroupRel>();
	private AssetTagGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}