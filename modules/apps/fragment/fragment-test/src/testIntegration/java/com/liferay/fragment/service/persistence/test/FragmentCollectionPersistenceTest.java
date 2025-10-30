/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.exception.DuplicateFragmentCollectionExternalReferenceCodeException;
import com.liferay.fragment.exception.NoSuchCollectionException;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.persistence.FragmentCollectionPersistence;
import com.liferay.fragment.service.persistence.FragmentCollectionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class FragmentCollectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.fragment.service"));

	@Before
	public void setUp() {
		_persistence = FragmentCollectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FragmentCollection> iterator = _fragmentCollections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentCollection fragmentCollection = _persistence.create(pk);

		Assert.assertNotNull(fragmentCollection);

		Assert.assertEquals(fragmentCollection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FragmentCollection newFragmentCollection = addFragmentCollection();

		_persistence.remove(newFragmentCollection);

		FragmentCollection existingFragmentCollection =
			_persistence.fetchByPrimaryKey(
				newFragmentCollection.getPrimaryKey());

		Assert.assertNull(existingFragmentCollection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFragmentCollection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentCollection newFragmentCollection = _persistence.create(pk);

		newFragmentCollection.setMvccVersion(RandomTestUtil.nextLong());

		newFragmentCollection.setCtCollectionId(RandomTestUtil.nextLong());

		newFragmentCollection.setUuid(RandomTestUtil.randomString());

		newFragmentCollection.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newFragmentCollection.setGroupId(RandomTestUtil.nextLong());

		newFragmentCollection.setCompanyId(RandomTestUtil.nextLong());

		newFragmentCollection.setUserId(RandomTestUtil.nextLong());

		newFragmentCollection.setUserName(RandomTestUtil.randomString());

		newFragmentCollection.setCreateDate(RandomTestUtil.nextDate());

		newFragmentCollection.setModifiedDate(RandomTestUtil.nextDate());

		newFragmentCollection.setFragmentCollectionKey(
			RandomTestUtil.randomString());

		newFragmentCollection.setName(RandomTestUtil.randomString());

		newFragmentCollection.setDescription(RandomTestUtil.randomString());

		newFragmentCollection.setMarketplace(RandomTestUtil.randomBoolean());

		newFragmentCollection.setLastPublishDate(RandomTestUtil.nextDate());

		_fragmentCollections.add(_persistence.update(newFragmentCollection));

		FragmentCollection existingFragmentCollection =
			_persistence.findByPrimaryKey(
				newFragmentCollection.getPrimaryKey());

		Assert.assertEquals(
			existingFragmentCollection.getMvccVersion(),
			newFragmentCollection.getMvccVersion());
		Assert.assertEquals(
			existingFragmentCollection.getCtCollectionId(),
			newFragmentCollection.getCtCollectionId());
		Assert.assertEquals(
			existingFragmentCollection.getUuid(),
			newFragmentCollection.getUuid());
		Assert.assertEquals(
			existingFragmentCollection.getExternalReferenceCode(),
			newFragmentCollection.getExternalReferenceCode());
		Assert.assertEquals(
			existingFragmentCollection.getFragmentCollectionId(),
			newFragmentCollection.getFragmentCollectionId());
		Assert.assertEquals(
			existingFragmentCollection.getGroupId(),
			newFragmentCollection.getGroupId());
		Assert.assertEquals(
			existingFragmentCollection.getCompanyId(),
			newFragmentCollection.getCompanyId());
		Assert.assertEquals(
			existingFragmentCollection.getUserId(),
			newFragmentCollection.getUserId());
		Assert.assertEquals(
			existingFragmentCollection.getUserName(),
			newFragmentCollection.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingFragmentCollection.getCreateDate()),
			Time.getShortTimestamp(newFragmentCollection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentCollection.getModifiedDate()),
			Time.getShortTimestamp(newFragmentCollection.getModifiedDate()));
		Assert.assertEquals(
			existingFragmentCollection.getFragmentCollectionKey(),
			newFragmentCollection.getFragmentCollectionKey());
		Assert.assertEquals(
			existingFragmentCollection.getName(),
			newFragmentCollection.getName());
		Assert.assertEquals(
			existingFragmentCollection.getDescription(),
			newFragmentCollection.getDescription());
		Assert.assertEquals(
			existingFragmentCollection.isMarketplace(),
			newFragmentCollection.isMarketplace());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingFragmentCollection.getLastPublishDate()),
			Time.getShortTimestamp(newFragmentCollection.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateFragmentCollectionExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		FragmentCollection fragmentCollection = addFragmentCollection();

		FragmentCollection newFragmentCollection = addFragmentCollection();

		newFragmentCollection.setGroupId(fragmentCollection.getGroupId());

		newFragmentCollection = _persistence.update(newFragmentCollection);

		Session session = _persistence.getCurrentSession();

		session.evict(newFragmentCollection);

		newFragmentCollection.setExternalReferenceCode(
			fragmentCollection.getExternalReferenceCode());

		_persistence.update(newFragmentCollection);
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
	public void testCountByG_FCK() throws Exception {
		_persistence.countByG_FCK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_FCK(0L, "null");

		_persistence.countByG_FCK(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeN() throws Exception {
		_persistence.countByG_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeN(0L, "null");

		_persistence.countByG_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeNArrayable() throws Exception {
		_persistence.countByG_LikeN(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString());
	}

	@Test
	public void testCountByG_M() throws Exception {
		_persistence.countByG_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_MArrayable() throws Exception {
		_persistence.countByG_M(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeN_M() throws Exception {
		_persistence.countByG_LikeN_M(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeN_M(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeN_M(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeN_MArrayable() throws Exception {
		_persistence.countByG_LikeN_M(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FragmentCollection newFragmentCollection = addFragmentCollection();

		FragmentCollection existingFragmentCollection =
			_persistence.findByPrimaryKey(
				newFragmentCollection.getPrimaryKey());

		Assert.assertEquals(existingFragmentCollection, newFragmentCollection);
	}

	@Test(expected = NoSuchCollectionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FragmentCollection> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FragmentCollection", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "fragmentCollectionId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"fragmentCollectionKey", true, "name", true, "description", true,
			"marketplace", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FragmentCollection newFragmentCollection = addFragmentCollection();

		FragmentCollection existingFragmentCollection =
			_persistence.fetchByPrimaryKey(
				newFragmentCollection.getPrimaryKey());

		Assert.assertEquals(existingFragmentCollection, newFragmentCollection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentCollection missingFragmentCollection =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFragmentCollection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FragmentCollection newFragmentCollection1 = addFragmentCollection();
		FragmentCollection newFragmentCollection2 = addFragmentCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentCollection1.getPrimaryKey());
		primaryKeys.add(newFragmentCollection2.getPrimaryKey());

		Map<Serializable, FragmentCollection> fragmentCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, fragmentCollections.size());
		Assert.assertEquals(
			newFragmentCollection1,
			fragmentCollections.get(newFragmentCollection1.getPrimaryKey()));
		Assert.assertEquals(
			newFragmentCollection2,
			fragmentCollections.get(newFragmentCollection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FragmentCollection> fragmentCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FragmentCollection newFragmentCollection = addFragmentCollection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentCollection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FragmentCollection> fragmentCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentCollections.size());
		Assert.assertEquals(
			newFragmentCollection,
			fragmentCollections.get(newFragmentCollection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FragmentCollection> fragmentCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(fragmentCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FragmentCollection newFragmentCollection = addFragmentCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFragmentCollection.getPrimaryKey());

		Map<Serializable, FragmentCollection> fragmentCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, fragmentCollections.size());
		Assert.assertEquals(
			newFragmentCollection,
			fragmentCollections.get(newFragmentCollection.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		FragmentCollection newFragmentCollection = addFragmentCollection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newFragmentCollection.getPrimaryKey()));
	}

	private void _assertOriginalValues(FragmentCollection fragmentCollection) {
		Assert.assertEquals(
			fragmentCollection.getUuid(),
			ReflectionTestUtil.invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(fragmentCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(fragmentCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			fragmentCollection.getFragmentCollectionKey(),
			ReflectionTestUtil.invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fragmentCollectionKey"));

		Assert.assertEquals(
			fragmentCollection.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(fragmentCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				fragmentCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected FragmentCollection addFragmentCollection() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FragmentCollection fragmentCollection = _persistence.create(pk);

		fragmentCollection.setMvccVersion(RandomTestUtil.nextLong());

		fragmentCollection.setCtCollectionId(RandomTestUtil.nextLong());

		fragmentCollection.setUuid(RandomTestUtil.randomString());

		fragmentCollection.setExternalReferenceCode(
			RandomTestUtil.randomString());

		fragmentCollection.setGroupId(RandomTestUtil.nextLong());

		fragmentCollection.setCompanyId(RandomTestUtil.nextLong());

		fragmentCollection.setUserId(RandomTestUtil.nextLong());

		fragmentCollection.setUserName(RandomTestUtil.randomString());

		fragmentCollection.setCreateDate(RandomTestUtil.nextDate());

		fragmentCollection.setModifiedDate(RandomTestUtil.nextDate());

		fragmentCollection.setFragmentCollectionKey(
			RandomTestUtil.randomString());

		fragmentCollection.setName(RandomTestUtil.randomString());

		fragmentCollection.setDescription(RandomTestUtil.randomString());

		fragmentCollection.setMarketplace(RandomTestUtil.randomBoolean());

		fragmentCollection.setLastPublishDate(RandomTestUtil.nextDate());

		_fragmentCollections.add(_persistence.update(fragmentCollection));

		return fragmentCollection;
	}

	private List<FragmentCollection> _fragmentCollections =
		new ArrayList<FragmentCollection>();
	private FragmentCollectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}