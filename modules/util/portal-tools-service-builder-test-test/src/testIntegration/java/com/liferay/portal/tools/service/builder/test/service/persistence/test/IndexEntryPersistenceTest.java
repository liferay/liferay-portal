/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.DuplicateIndexEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchIndexEntryException;
import com.liferay.portal.tools.service.builder.test.model.IndexEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.IndexEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.IndexEntryUtil;

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
public class IndexEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = IndexEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<IndexEntry> iterator = _indexEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IndexEntry indexEntry = _persistence.create(pk);

		Assert.assertNotNull(indexEntry);

		Assert.assertEquals(indexEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		IndexEntry newIndexEntry = addIndexEntry();

		_persistence.remove(newIndexEntry);

		IndexEntry existingIndexEntry = _persistence.fetchByPrimaryKey(
			newIndexEntry.getPrimaryKey());

		Assert.assertNull(existingIndexEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addIndexEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IndexEntry newIndexEntry = _persistence.create(pk);

		newIndexEntry.setMvccVersion(RandomTestUtil.nextLong());

		newIndexEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newIndexEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		newIndexEntry.setCompanyId(RandomTestUtil.nextLong());

		newIndexEntry.setOwnerId(RandomTestUtil.nextLong());

		newIndexEntry.setOwnerType(RandomTestUtil.nextInt());

		newIndexEntry.setPlid(RandomTestUtil.nextLong());

		newIndexEntry.setPortletId(RandomTestUtil.randomString());

		_indexEntries.add(_persistence.update(newIndexEntry));

		IndexEntry existingIndexEntry = _persistence.findByPrimaryKey(
			newIndexEntry.getPrimaryKey());

		Assert.assertEquals(
			existingIndexEntry.getMvccVersion(),
			newIndexEntry.getMvccVersion());
		Assert.assertEquals(
			existingIndexEntry.getCtCollectionId(),
			newIndexEntry.getCtCollectionId());
		Assert.assertEquals(
			existingIndexEntry.getExternalReferenceCode(),
			newIndexEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingIndexEntry.getIndexEntryId(),
			newIndexEntry.getIndexEntryId());
		Assert.assertEquals(
			existingIndexEntry.getCompanyId(), newIndexEntry.getCompanyId());
		Assert.assertEquals(
			existingIndexEntry.getOwnerId(), newIndexEntry.getOwnerId());
		Assert.assertEquals(
			existingIndexEntry.getOwnerType(), newIndexEntry.getOwnerType());
		Assert.assertEquals(
			existingIndexEntry.getPlid(), newIndexEntry.getPlid());
		Assert.assertEquals(
			existingIndexEntry.getPortletId(), newIndexEntry.getPortletId());
	}

	@Test(expected = DuplicateIndexEntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		IndexEntry indexEntry = addIndexEntry();

		IndexEntry newIndexEntry = addIndexEntry();

		newIndexEntry.setCompanyId(indexEntry.getCompanyId());

		newIndexEntry = _persistence.update(newIndexEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newIndexEntry);

		newIndexEntry.setExternalReferenceCode(
			indexEntry.getExternalReferenceCode());

		_persistence.update(newIndexEntry);
	}

	@Test
	public void testCountByOwnerId() throws Exception {
		_persistence.countByOwnerId(RandomTestUtil.nextLong());

		_persistence.countByOwnerId(0L);
	}

	@Test
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountByPortletId() throws Exception {
		_persistence.countByPortletId("");

		_persistence.countByPortletId("null");

		_persistence.countByPortletId((String)null);
	}

	@Test
	public void testCountByO_P() throws Exception {
		_persistence.countByO_P(RandomTestUtil.nextInt(), "");

		_persistence.countByO_P(0, "null");

		_persistence.countByO_P(0, (String)null);
	}

	@Test
	public void testCountByP_P() throws Exception {
		_persistence.countByP_P(RandomTestUtil.nextLong(), "");

		_persistence.countByP_P(0L, "null");

		_persistence.countByP_P(0L, (String)null);
	}

	@Test
	public void testCountByO_O_P() throws Exception {
		_persistence.countByO_O_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong());

		_persistence.countByO_O_P(0L, 0, 0L);
	}

	@Test
	public void testCountByO_O_PI() throws Exception {
		_persistence.countByO_O_PI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "");

		_persistence.countByO_O_PI(0L, 0, "null");

		_persistence.countByO_O_PI(0L, 0, (String)null);
	}

	@Test
	public void testCountByO_P_P() throws Exception {
		_persistence.countByO_P_P(
			RandomTestUtil.nextInt(), RandomTestUtil.nextLong(), "");

		_persistence.countByO_P_P(0, 0L, "null");

		_persistence.countByO_P_P(0, 0L, (String)null);
	}

	@Test
	public void testCountByC_O_O_LikeP() throws Exception {
		_persistence.countByC_O_O_LikeP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), "");

		_persistence.countByC_O_O_LikeP(0L, 0L, 0, "null");

		_persistence.countByC_O_O_LikeP(0L, 0L, 0, (String)null);
	}

	@Test
	public void testCountByO_O_P_P() throws Exception {
		_persistence.countByO_O_P_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByO_O_P_P(0L, 0, 0L, "null");

		_persistence.countByO_O_P_P(0L, 0, 0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		IndexEntry newIndexEntry = addIndexEntry();

		IndexEntry existingIndexEntry = _persistence.findByPrimaryKey(
			newIndexEntry.getPrimaryKey());

		Assert.assertEquals(existingIndexEntry, newIndexEntry);
	}

	@Test(expected = NoSuchIndexEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<IndexEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"IndexEntry", "mvccVersion", true, "ctCollectionId", true,
			"externalReferenceCode", true, "indexEntryId", true, "companyId",
			true, "ownerId", true, "ownerType", true, "plid", true, "portletId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		IndexEntry newIndexEntry = addIndexEntry();

		IndexEntry existingIndexEntry = _persistence.fetchByPrimaryKey(
			newIndexEntry.getPrimaryKey());

		Assert.assertEquals(existingIndexEntry, newIndexEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IndexEntry missingIndexEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingIndexEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		IndexEntry newIndexEntry1 = addIndexEntry();
		IndexEntry newIndexEntry2 = addIndexEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIndexEntry1.getPrimaryKey());
		primaryKeys.add(newIndexEntry2.getPrimaryKey());

		Map<Serializable, IndexEntry> indexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, indexEntries.size());
		Assert.assertEquals(
			newIndexEntry1, indexEntries.get(newIndexEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newIndexEntry2, indexEntries.get(newIndexEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, IndexEntry> indexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(indexEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		IndexEntry newIndexEntry = addIndexEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIndexEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, IndexEntry> indexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, indexEntries.size());
		Assert.assertEquals(
			newIndexEntry, indexEntries.get(newIndexEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, IndexEntry> indexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(indexEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		IndexEntry newIndexEntry = addIndexEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newIndexEntry.getPrimaryKey());

		Map<Serializable, IndexEntry> indexEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, indexEntries.size());
		Assert.assertEquals(
			newIndexEntry, indexEntries.get(newIndexEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		IndexEntry newIndexEntry = addIndexEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newIndexEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(IndexEntry indexEntry) {
		Assert.assertEquals(
			Long.valueOf(indexEntry.getOwnerId()),
			ReflectionTestUtil.<Long>invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerId"));
		Assert.assertEquals(
			Integer.valueOf(indexEntry.getOwnerType()),
			ReflectionTestUtil.<Integer>invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerType"));
		Assert.assertEquals(
			Long.valueOf(indexEntry.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
		Assert.assertEquals(
			indexEntry.getPortletId(),
			ReflectionTestUtil.invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));

		Assert.assertEquals(
			indexEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(indexEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				indexEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected IndexEntry addIndexEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		IndexEntry indexEntry = _persistence.create(pk);

		indexEntry.setMvccVersion(RandomTestUtil.nextLong());

		indexEntry.setCtCollectionId(RandomTestUtil.nextLong());

		indexEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		indexEntry.setCompanyId(RandomTestUtil.nextLong());

		indexEntry.setOwnerId(RandomTestUtil.nextLong());

		indexEntry.setOwnerType(RandomTestUtil.nextInt());

		indexEntry.setPlid(RandomTestUtil.nextLong());

		indexEntry.setPortletId(RandomTestUtil.randomString());

		_indexEntries.add(_persistence.update(indexEntry));

		return indexEntry;
	}

	private List<IndexEntry> _indexEntries = new ArrayList<IndexEntry>();
	private IndexEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}