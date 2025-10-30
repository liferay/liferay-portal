/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.exception.NoSuchEntryAssetCategoryRelException;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.persistence.AssetEntryAssetCategoryRelPersistence;
import com.liferay.asset.entry.rel.service.persistence.AssetEntryAssetCategoryRelUtil;
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
public class AssetEntryAssetCategoryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.entry.rel.service"));

	@Before
	public void setUp() {
		_persistence = AssetEntryAssetCategoryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetEntryAssetCategoryRel> iterator =
			_assetEntryAssetCategoryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			_persistence.create(pk);

		Assert.assertNotNull(assetEntryAssetCategoryRel);

		Assert.assertEquals(assetEntryAssetCategoryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		_persistence.remove(newAssetEntryAssetCategoryRel);

		AssetEntryAssetCategoryRel existingAssetEntryAssetCategoryRel =
			_persistence.fetchByPrimaryKey(
				newAssetEntryAssetCategoryRel.getPrimaryKey());

		Assert.assertNull(existingAssetEntryAssetCategoryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetEntryAssetCategoryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			_persistence.create(pk);

		newAssetEntryAssetCategoryRel.setMvccVersion(RandomTestUtil.nextLong());

		newAssetEntryAssetCategoryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newAssetEntryAssetCategoryRel.setCompanyId(RandomTestUtil.nextLong());

		newAssetEntryAssetCategoryRel.setAssetEntryId(
			RandomTestUtil.nextLong());

		newAssetEntryAssetCategoryRel.setAssetCategoryId(
			RandomTestUtil.nextLong());

		newAssetEntryAssetCategoryRel.setPriority(RandomTestUtil.nextInt());

		_assetEntryAssetCategoryRels.add(
			_persistence.update(newAssetEntryAssetCategoryRel));

		AssetEntryAssetCategoryRel existingAssetEntryAssetCategoryRel =
			_persistence.findByPrimaryKey(
				newAssetEntryAssetCategoryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getMvccVersion(),
			newAssetEntryAssetCategoryRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getCtCollectionId(),
			newAssetEntryAssetCategoryRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.
				getAssetEntryAssetCategoryRelId(),
			newAssetEntryAssetCategoryRel.getAssetEntryAssetCategoryRelId());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getCompanyId(),
			newAssetEntryAssetCategoryRel.getCompanyId());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getAssetEntryId(),
			newAssetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getAssetCategoryId(),
			newAssetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel.getPriority(),
			newAssetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testCountByAssetEntryId() throws Exception {
		_persistence.countByAssetEntryId(RandomTestUtil.nextLong());

		_persistence.countByAssetEntryId(0L);
	}

	@Test
	public void testCountByAssetCategoryId() throws Exception {
		_persistence.countByAssetCategoryId(RandomTestUtil.nextLong());

		_persistence.countByAssetCategoryId(0L);
	}

	@Test
	public void testCountByA_A() throws Exception {
		_persistence.countByA_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_A(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		AssetEntryAssetCategoryRel existingAssetEntryAssetCategoryRel =
			_persistence.findByPrimaryKey(
				newAssetEntryAssetCategoryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel, newAssetEntryAssetCategoryRel);
	}

	@Test(expected = NoSuchEntryAssetCategoryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetEntryAssetCategoryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AssetEntryAssetCategoryRel", "mvccVersion", true, "ctCollectionId",
			true, "assetEntryAssetCategoryRelId", true, "companyId", true,
			"assetEntryId", true, "assetCategoryId", true, "priority", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		AssetEntryAssetCategoryRel existingAssetEntryAssetCategoryRel =
			_persistence.fetchByPrimaryKey(
				newAssetEntryAssetCategoryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetEntryAssetCategoryRel, newAssetEntryAssetCategoryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetEntryAssetCategoryRel missingAssetEntryAssetCategoryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetEntryAssetCategoryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel1 =
			addAssetEntryAssetCategoryRel();
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel2 =
			addAssetEntryAssetCategoryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetEntryAssetCategoryRel1.getPrimaryKey());
		primaryKeys.add(newAssetEntryAssetCategoryRel2.getPrimaryKey());

		Map<Serializable, AssetEntryAssetCategoryRel>
			assetEntryAssetCategoryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, assetEntryAssetCategoryRels.size());
		Assert.assertEquals(
			newAssetEntryAssetCategoryRel1,
			assetEntryAssetCategoryRels.get(
				newAssetEntryAssetCategoryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetEntryAssetCategoryRel2,
			assetEntryAssetCategoryRels.get(
				newAssetEntryAssetCategoryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetEntryAssetCategoryRel>
			assetEntryAssetCategoryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetEntryAssetCategoryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetEntryAssetCategoryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetEntryAssetCategoryRel>
			assetEntryAssetCategoryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetEntryAssetCategoryRels.size());
		Assert.assertEquals(
			newAssetEntryAssetCategoryRel,
			assetEntryAssetCategoryRels.get(
				newAssetEntryAssetCategoryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetEntryAssetCategoryRel>
			assetEntryAssetCategoryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetEntryAssetCategoryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetEntryAssetCategoryRel.getPrimaryKey());

		Map<Serializable, AssetEntryAssetCategoryRel>
			assetEntryAssetCategoryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetEntryAssetCategoryRels.size());
		Assert.assertEquals(
			newAssetEntryAssetCategoryRel,
			assetEntryAssetCategoryRels.get(
				newAssetEntryAssetCategoryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetEntryAssetCategoryRel newAssetEntryAssetCategoryRel =
			addAssetEntryAssetCategoryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetEntryAssetCategoryRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel) {

		Assert.assertEquals(
			Long.valueOf(assetEntryAssetCategoryRel.getAssetEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetEntryAssetCategoryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetEntryId"));
		Assert.assertEquals(
			Long.valueOf(assetEntryAssetCategoryRel.getAssetCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				assetEntryAssetCategoryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetCategoryId"));
	}

	protected AssetEntryAssetCategoryRel addAssetEntryAssetCategoryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			_persistence.create(pk);

		assetEntryAssetCategoryRel.setMvccVersion(RandomTestUtil.nextLong());

		assetEntryAssetCategoryRel.setCtCollectionId(RandomTestUtil.nextLong());

		assetEntryAssetCategoryRel.setCompanyId(RandomTestUtil.nextLong());

		assetEntryAssetCategoryRel.setAssetEntryId(RandomTestUtil.nextLong());

		assetEntryAssetCategoryRel.setAssetCategoryId(
			RandomTestUtil.nextLong());

		assetEntryAssetCategoryRel.setPriority(RandomTestUtil.nextInt());

		_assetEntryAssetCategoryRels.add(
			_persistence.update(assetEntryAssetCategoryRel));

		return assetEntryAssetCategoryRel;
	}

	private List<AssetEntryAssetCategoryRel> _assetEntryAssetCategoryRels =
		new ArrayList<AssetEntryAssetCategoryRel>();
	private AssetEntryAssetCategoryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}