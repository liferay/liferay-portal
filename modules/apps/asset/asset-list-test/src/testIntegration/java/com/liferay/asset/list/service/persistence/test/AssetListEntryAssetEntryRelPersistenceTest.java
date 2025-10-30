/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.exception.NoSuchEntryAssetEntryRelException;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class AssetListEntryAssetEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() {
		_persistence = AssetListEntryAssetEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetListEntryAssetEntryRel> iterator =
			_assetListEntryAssetEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(assetListEntryAssetEntryRel);

		Assert.assertEquals(assetListEntryAssetEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		_persistence.remove(newAssetListEntryAssetEntryRel);

		AssetListEntryAssetEntryRel existingAssetListEntryAssetEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryAssetEntryRel.getPrimaryKey());

		Assert.assertNull(existingAssetListEntryAssetEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetListEntryAssetEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			_persistence.create(pk);

		newAssetListEntryAssetEntryRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setUuid(RandomTestUtil.randomString());

		newAssetListEntryAssetEntryRel.setGroupId(RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setCompanyId(RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setUserId(RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setUserName(
			RandomTestUtil.randomString());

		newAssetListEntryAssetEntryRel.setCreateDate(RandomTestUtil.nextDate());

		newAssetListEntryAssetEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newAssetListEntryAssetEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setAssetEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setSegmentsEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntryAssetEntryRel.setPosition(RandomTestUtil.nextInt());

		newAssetListEntryAssetEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_assetListEntryAssetEntryRels.add(
			_persistence.update(newAssetListEntryAssetEntryRel));

		AssetListEntryAssetEntryRel existingAssetListEntryAssetEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntryAssetEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getMvccVersion(),
			newAssetListEntryAssetEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getCtCollectionId(),
			newAssetListEntryAssetEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getUuid(),
			newAssetListEntryAssetEntryRel.getUuid());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.
				getAssetListEntryAssetEntryRelId(),
			newAssetListEntryAssetEntryRel.getAssetListEntryAssetEntryRelId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getGroupId(),
			newAssetListEntryAssetEntryRel.getGroupId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getCompanyId(),
			newAssetListEntryAssetEntryRel.getCompanyId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getUserId(),
			newAssetListEntryAssetEntryRel.getUserId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getUserName(),
			newAssetListEntryAssetEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAssetEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newAssetListEntryAssetEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAssetEntryRel.getModifiedDate()),
			Time.getShortTimestamp(
				newAssetListEntryAssetEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getAssetListEntryId(),
			newAssetListEntryAssetEntryRel.getAssetListEntryId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getAssetEntryId(),
			newAssetListEntryAssetEntryRel.getAssetEntryId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getSegmentsEntryId(),
			newAssetListEntryAssetEntryRel.getSegmentsEntryId());
		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel.getPosition(),
			newAssetListEntryAssetEntryRel.getPosition());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntryAssetEntryRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newAssetListEntryAssetEntryRel.getLastPublishDate()));
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
	public void testCountByAssetEntryId() throws Exception {
		_persistence.countByAssetEntryId(RandomTestUtil.nextLong());

		_persistence.countByAssetEntryId(0L);
	}

	@Test
	public void testCountByA_S() throws Exception {
		_persistence.countByA_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_S(0L, 0L);
	}

	@Test
	public void testCountByA_SArrayable() throws Exception {
		_persistence.countByA_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByA_S_P() throws Exception {
		_persistence.countByA_S_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByA_S_P(0L, 0L, 0);
	}

	@Test
	public void testCountByA_S_GtP() throws Exception {
		_persistence.countByA_S_GtP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByA_S_GtP(0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		AssetListEntryAssetEntryRel existingAssetListEntryAssetEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntryAssetEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel,
			newAssetListEntryAssetEntryRel);
	}

	@Test(expected = NoSuchEntryAssetEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetListEntryAssetEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AssetListEntryAssetEntryRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"assetListEntryAssetEntryRelId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "assetListEntryId", true, "assetEntryId",
			true, "segmentsEntryId", true, "position", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		AssetListEntryAssetEntryRel existingAssetListEntryAssetEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntryAssetEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntryAssetEntryRel,
			newAssetListEntryAssetEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel missingAssetListEntryAssetEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetListEntryAssetEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel1 =
			addAssetListEntryAssetEntryRel();
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel2 =
			addAssetListEntryAssetEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAssetEntryRel1.getPrimaryKey());
		primaryKeys.add(newAssetListEntryAssetEntryRel2.getPrimaryKey());

		Map<Serializable, AssetListEntryAssetEntryRel>
			assetListEntryAssetEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, assetListEntryAssetEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAssetEntryRel1,
			assetListEntryAssetEntryRels.get(
				newAssetListEntryAssetEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetListEntryAssetEntryRel2,
			assetListEntryAssetEntryRels.get(
				newAssetListEntryAssetEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetListEntryAssetEntryRel>
			assetListEntryAssetEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntryAssetEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAssetEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetListEntryAssetEntryRel>
			assetListEntryAssetEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntryAssetEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAssetEntryRel,
			assetListEntryAssetEntryRels.get(
				newAssetListEntryAssetEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetListEntryAssetEntryRel>
			assetListEntryAssetEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntryAssetEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntryAssetEntryRel.getPrimaryKey());

		Map<Serializable, AssetListEntryAssetEntryRel>
			assetListEntryAssetEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntryAssetEntryRels.size());
		Assert.assertEquals(
			newAssetListEntryAssetEntryRel,
			assetListEntryAssetEntryRels.get(
				newAssetListEntryAssetEntryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetListEntryAssetEntryRel newAssetListEntryAssetEntryRel =
			addAssetListEntryAssetEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetListEntryAssetEntryRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel) {

		Assert.assertEquals(
			assetListEntryAssetEntryRel.getUuid(),
			ReflectionTestUtil.invoke(
				assetListEntryAssetEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryAssetEntryRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryAssetEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetListEntryAssetEntryRel.getAssetListEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryAssetEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetListEntryId"));
		Assert.assertEquals(
			Long.valueOf(assetListEntryAssetEntryRel.getSegmentsEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntryAssetEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsEntryId"));
		Assert.assertEquals(
			Integer.valueOf(assetListEntryAssetEntryRel.getPosition()),
			ReflectionTestUtil.<Integer>invoke(
				assetListEntryAssetEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "position"));
	}

	protected AssetListEntryAssetEntryRel addAssetListEntryAssetEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			_persistence.create(pk);

		assetListEntryAssetEntryRel.setMvccVersion(RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setUuid(RandomTestUtil.randomString());

		assetListEntryAssetEntryRel.setGroupId(RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setCompanyId(RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setUserId(RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setUserName(RandomTestUtil.randomString());

		assetListEntryAssetEntryRel.setCreateDate(RandomTestUtil.nextDate());

		assetListEntryAssetEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		assetListEntryAssetEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setAssetEntryId(RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setSegmentsEntryId(
			RandomTestUtil.nextLong());

		assetListEntryAssetEntryRel.setPosition(RandomTestUtil.nextInt());

		assetListEntryAssetEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_assetListEntryAssetEntryRels.add(
			_persistence.update(assetListEntryAssetEntryRel));

		return assetListEntryAssetEntryRel;
	}

	private List<AssetListEntryAssetEntryRel> _assetListEntryAssetEntryRels =
		new ArrayList<AssetListEntryAssetEntryRel>();
	private AssetListEntryAssetEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}