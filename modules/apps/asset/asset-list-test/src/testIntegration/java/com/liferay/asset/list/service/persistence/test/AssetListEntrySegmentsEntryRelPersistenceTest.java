/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.exception.NoSuchEntrySegmentsEntryRelException;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.persistence.AssetListEntrySegmentsEntryRelPersistence;
import com.liferay.asset.list.service.persistence.AssetListEntrySegmentsEntryRelUtil;
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
public class AssetListEntrySegmentsEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() {
		_persistence = AssetListEntrySegmentsEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetListEntrySegmentsEntryRel> iterator =
			_assetListEntrySegmentsEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(assetListEntrySegmentsEntryRel);

		Assert.assertEquals(assetListEntrySegmentsEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		_persistence.remove(newAssetListEntrySegmentsEntryRel);

		AssetListEntrySegmentsEntryRel existingAssetListEntrySegmentsEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey());

		Assert.assertNull(existingAssetListEntrySegmentsEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetListEntrySegmentsEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			_persistence.create(pk);

		newAssetListEntrySegmentsEntryRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setUuid(
			RandomTestUtil.randomString());

		newAssetListEntrySegmentsEntryRel.setGroupId(RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setUserId(RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setUserName(
			RandomTestUtil.randomString());

		newAssetListEntrySegmentsEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		newAssetListEntrySegmentsEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newAssetListEntrySegmentsEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setPriority(RandomTestUtil.nextInt());

		newAssetListEntrySegmentsEntryRel.setSegmentsEntryId(
			RandomTestUtil.nextLong());

		newAssetListEntrySegmentsEntryRel.setTypeSettings(
			RandomTestUtil.randomString());

		newAssetListEntrySegmentsEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_assetListEntrySegmentsEntryRels.add(
			_persistence.update(newAssetListEntrySegmentsEntryRel));

		AssetListEntrySegmentsEntryRel existingAssetListEntrySegmentsEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getMvccVersion(),
			newAssetListEntrySegmentsEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getCtCollectionId(),
			newAssetListEntrySegmentsEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getUuid(),
			newAssetListEntrySegmentsEntryRel.getUuid());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.
				getAssetListEntrySegmentsEntryRelId(),
			newAssetListEntrySegmentsEntryRel.
				getAssetListEntrySegmentsEntryRelId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getGroupId(),
			newAssetListEntrySegmentsEntryRel.getGroupId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getCompanyId(),
			newAssetListEntrySegmentsEntryRel.getCompanyId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getUserId(),
			newAssetListEntrySegmentsEntryRel.getUserId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getUserName(),
			newAssetListEntrySegmentsEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntrySegmentsEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newAssetListEntrySegmentsEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntrySegmentsEntryRel.getModifiedDate()),
			Time.getShortTimestamp(
				newAssetListEntrySegmentsEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getAssetListEntryId(),
			newAssetListEntrySegmentsEntryRel.getAssetListEntryId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getPriority(),
			newAssetListEntrySegmentsEntryRel.getPriority());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getSegmentsEntryId(),
			newAssetListEntrySegmentsEntryRel.getSegmentsEntryId());
		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel.getTypeSettings(),
			newAssetListEntrySegmentsEntryRel.getTypeSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetListEntrySegmentsEntryRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newAssetListEntrySegmentsEntryRel.getLastPublishDate()));
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
	public void testCountBySegmentsEntryId() throws Exception {
		_persistence.countBySegmentsEntryId(RandomTestUtil.nextLong());

		_persistence.countBySegmentsEntryId(0L);
	}

	@Test
	public void testCountByA_S() throws Exception {
		_persistence.countByA_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_S(0L, 0L);
	}

	@Test
	public void testCountByA_S_C() throws Exception {
		_persistence.countByA_S_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_S_C(0L, 0L);
	}

	@Test
	public void testCountByA_S_CArrayable() throws Exception {
		_persistence.countByA_S_C(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		AssetListEntrySegmentsEntryRel existingAssetListEntrySegmentsEntryRel =
			_persistence.findByPrimaryKey(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel,
			newAssetListEntrySegmentsEntryRel);
	}

	@Test(expected = NoSuchEntrySegmentsEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetListEntrySegmentsEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AssetListEntrySegmentsEntryRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"assetListEntrySegmentsEntryRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "assetListEntryId", true, "priority",
			true, "segmentsEntryId", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		AssetListEntrySegmentsEntryRel existingAssetListEntrySegmentsEntryRel =
			_persistence.fetchByPrimaryKey(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingAssetListEntrySegmentsEntryRel,
			newAssetListEntrySegmentsEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetListEntrySegmentsEntryRel missingAssetListEntrySegmentsEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetListEntrySegmentsEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel1 =
			addAssetListEntrySegmentsEntryRel();
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel2 =
			addAssetListEntrySegmentsEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntrySegmentsEntryRel1.getPrimaryKey());
		primaryKeys.add(newAssetListEntrySegmentsEntryRel2.getPrimaryKey());

		Map<Serializable, AssetListEntrySegmentsEntryRel>
			assetListEntrySegmentsEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, assetListEntrySegmentsEntryRels.size());
		Assert.assertEquals(
			newAssetListEntrySegmentsEntryRel1,
			assetListEntrySegmentsEntryRels.get(
				newAssetListEntrySegmentsEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetListEntrySegmentsEntryRel2,
			assetListEntrySegmentsEntryRels.get(
				newAssetListEntrySegmentsEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetListEntrySegmentsEntryRel>
			assetListEntrySegmentsEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntrySegmentsEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntrySegmentsEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetListEntrySegmentsEntryRel>
			assetListEntrySegmentsEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntrySegmentsEntryRels.size());
		Assert.assertEquals(
			newAssetListEntrySegmentsEntryRel,
			assetListEntrySegmentsEntryRels.get(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetListEntrySegmentsEntryRel>
			assetListEntrySegmentsEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(assetListEntrySegmentsEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetListEntrySegmentsEntryRel.getPrimaryKey());

		Map<Serializable, AssetListEntrySegmentsEntryRel>
			assetListEntrySegmentsEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, assetListEntrySegmentsEntryRels.size());
		Assert.assertEquals(
			newAssetListEntrySegmentsEntryRel,
			assetListEntrySegmentsEntryRels.get(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetListEntrySegmentsEntryRel newAssetListEntrySegmentsEntryRel =
			addAssetListEntrySegmentsEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetListEntrySegmentsEntryRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel) {

		Assert.assertEquals(
			assetListEntrySegmentsEntryRel.getUuid(),
			ReflectionTestUtil.invoke(
				assetListEntrySegmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(assetListEntrySegmentsEntryRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntrySegmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(assetListEntrySegmentsEntryRel.getAssetListEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntrySegmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "assetListEntryId"));
		Assert.assertEquals(
			Long.valueOf(assetListEntrySegmentsEntryRel.getSegmentsEntryId()),
			ReflectionTestUtil.<Long>invoke(
				assetListEntrySegmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsEntryId"));
	}

	protected AssetListEntrySegmentsEntryRel addAssetListEntrySegmentsEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			_persistence.create(pk);

		assetListEntrySegmentsEntryRel.setMvccVersion(
			RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setUuid(RandomTestUtil.randomString());

		assetListEntrySegmentsEntryRel.setGroupId(RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setCompanyId(RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setUserId(RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setUserName(
			RandomTestUtil.randomString());

		assetListEntrySegmentsEntryRel.setCreateDate(RandomTestUtil.nextDate());

		assetListEntrySegmentsEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		assetListEntrySegmentsEntryRel.setAssetListEntryId(
			RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setPriority(RandomTestUtil.nextInt());

		assetListEntrySegmentsEntryRel.setSegmentsEntryId(
			RandomTestUtil.nextLong());

		assetListEntrySegmentsEntryRel.setTypeSettings(
			RandomTestUtil.randomString());

		assetListEntrySegmentsEntryRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_assetListEntrySegmentsEntryRels.add(
			_persistence.update(assetListEntrySegmentsEntryRel));

		return assetListEntrySegmentsEntryRel;
	}

	private List<AssetListEntrySegmentsEntryRel>
		_assetListEntrySegmentsEntryRels =
			new ArrayList<AssetListEntrySegmentsEntryRel>();
	private AssetListEntrySegmentsEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}