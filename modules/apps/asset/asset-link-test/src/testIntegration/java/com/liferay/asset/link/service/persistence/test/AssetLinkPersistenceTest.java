/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.link.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.link.exception.NoSuchLinkException;
import com.liferay.asset.link.model.AssetLink;
import com.liferay.asset.link.service.persistence.AssetLinkPersistence;
import com.liferay.asset.link.service.persistence.AssetLinkUtil;
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
public class AssetLinkPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.link.service"));

	@Before
	public void setUp() {
		_persistence = AssetLinkUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetLink> iterator = _assetLinks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetLink assetLink = _persistence.create(pk);

		Assert.assertNotNull(assetLink);

		Assert.assertEquals(assetLink.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetLink newAssetLink = addAssetLink();

		_persistence.remove(newAssetLink);

		AssetLink existingAssetLink = _persistence.fetchByPrimaryKey(
			newAssetLink.getPrimaryKey());

		Assert.assertNull(existingAssetLink);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetLink();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetLink newAssetLink = _persistence.create(pk);

		newAssetLink.setMvccVersion(RandomTestUtil.nextLong());

		newAssetLink.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetLink.setCompanyId(RandomTestUtil.nextLong());

		newAssetLink.setUserId(RandomTestUtil.nextLong());

		newAssetLink.setUserName(RandomTestUtil.randomString());

		newAssetLink.setCreateDate(RandomTestUtil.nextDate());

		newAssetLink.setEntryId1(RandomTestUtil.nextLong());

		newAssetLink.setEntryId2(RandomTestUtil.nextLong());

		newAssetLink.setType(RandomTestUtil.nextInt());

		newAssetLink.setWeight(RandomTestUtil.nextInt());

		_assetLinks.add(_persistence.update(newAssetLink));

		AssetLink existingAssetLink = _persistence.findByPrimaryKey(
			newAssetLink.getPrimaryKey());

		Assert.assertEquals(
			existingAssetLink.getMvccVersion(), newAssetLink.getMvccVersion());
		Assert.assertEquals(
			existingAssetLink.getCtCollectionId(),
			newAssetLink.getCtCollectionId());
		Assert.assertEquals(
			existingAssetLink.getLinkId(), newAssetLink.getLinkId());
		Assert.assertEquals(
			existingAssetLink.getCompanyId(), newAssetLink.getCompanyId());
		Assert.assertEquals(
			existingAssetLink.getUserId(), newAssetLink.getUserId());
		Assert.assertEquals(
			existingAssetLink.getUserName(), newAssetLink.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAssetLink.getCreateDate()),
			Time.getShortTimestamp(newAssetLink.getCreateDate()));
		Assert.assertEquals(
			existingAssetLink.getEntryId1(), newAssetLink.getEntryId1());
		Assert.assertEquals(
			existingAssetLink.getEntryId2(), newAssetLink.getEntryId2());
		Assert.assertEquals(
			existingAssetLink.getType(), newAssetLink.getType());
		Assert.assertEquals(
			existingAssetLink.getWeight(), newAssetLink.getWeight());
	}

	@Test
	public void testCountByEntryId1() throws Exception {
		_persistence.countByEntryId1(RandomTestUtil.nextLong());

		_persistence.countByEntryId1(0L);
	}

	@Test
	public void testCountByEntryId2() throws Exception {
		_persistence.countByEntryId2(RandomTestUtil.nextLong());

		_persistence.countByEntryId2(0L);
	}

	@Test
	public void testCountByE_E() throws Exception {
		_persistence.countByE_E(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByE_E(0L, 0L);
	}

	@Test
	public void testCountByE1_T() throws Exception {
		_persistence.countByE1_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByE1_T(0L, 0);
	}

	@Test
	public void testCountByE2_T() throws Exception {
		_persistence.countByE2_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByE2_T(0L, 0);
	}

	@Test
	public void testCountByE_E_T() throws Exception {
		_persistence.countByE_E_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByE_E_T(0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetLink newAssetLink = addAssetLink();

		AssetLink existingAssetLink = _persistence.findByPrimaryKey(
			newAssetLink.getPrimaryKey());

		Assert.assertEquals(existingAssetLink, newAssetLink);
	}

	@Test(expected = NoSuchLinkException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetLink> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetLink", "mvccVersion", true, "ctCollectionId", true, "linkId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "entryId1", true, "entryId2", true, "type",
			true, "weight", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetLink newAssetLink = addAssetLink();

		AssetLink existingAssetLink = _persistence.fetchByPrimaryKey(
			newAssetLink.getPrimaryKey());

		Assert.assertEquals(existingAssetLink, newAssetLink);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetLink missingAssetLink = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetLink);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetLink newAssetLink1 = addAssetLink();
		AssetLink newAssetLink2 = addAssetLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetLink1.getPrimaryKey());
		primaryKeys.add(newAssetLink2.getPrimaryKey());

		Map<Serializable, AssetLink> assetLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetLinks.size());
		Assert.assertEquals(
			newAssetLink1, assetLinks.get(newAssetLink1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetLink2, assetLinks.get(newAssetLink2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetLink> assetLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetLink newAssetLink = addAssetLink();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetLink.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetLink> assetLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetLinks.size());
		Assert.assertEquals(
			newAssetLink, assetLinks.get(newAssetLink.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetLink> assetLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetLinks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetLink newAssetLink = addAssetLink();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetLink.getPrimaryKey());

		Map<Serializable, AssetLink> assetLinks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetLinks.size());
		Assert.assertEquals(
			newAssetLink, assetLinks.get(newAssetLink.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetLink newAssetLink = addAssetLink();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAssetLink.getPrimaryKey()));
	}

	private void _assertOriginalValues(AssetLink assetLink) {
		Assert.assertEquals(
			Long.valueOf(assetLink.getEntryId1()),
			ReflectionTestUtil.<Long>invoke(
				assetLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "entryId1"));
		Assert.assertEquals(
			Long.valueOf(assetLink.getEntryId2()),
			ReflectionTestUtil.<Long>invoke(
				assetLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "entryId2"));
		Assert.assertEquals(
			Integer.valueOf(assetLink.getType()),
			ReflectionTestUtil.<Integer>invoke(
				assetLink, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected AssetLink addAssetLink() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetLink assetLink = _persistence.create(pk);

		assetLink.setMvccVersion(RandomTestUtil.nextLong());

		assetLink.setCtCollectionId(RandomTestUtil.nextLong());

		assetLink.setCompanyId(RandomTestUtil.nextLong());

		assetLink.setUserId(RandomTestUtil.nextLong());

		assetLink.setUserName(RandomTestUtil.randomString());

		assetLink.setCreateDate(RandomTestUtil.nextDate());

		assetLink.setEntryId1(RandomTestUtil.nextLong());

		assetLink.setEntryId2(RandomTestUtil.nextLong());

		assetLink.setType(RandomTestUtil.nextInt());

		assetLink.setWeight(RandomTestUtil.nextInt());

		_assetLinks.add(_persistence.update(assetLink));

		return assetLink;
	}

	private List<AssetLink> _assetLinks = new ArrayList<AssetLink>();
	private AssetLinkPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}