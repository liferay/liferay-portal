/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryMetadataExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryMetadataException;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryMetadataPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryMetadataUtil;
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
public class DLFileEntryMetadataPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFileEntryMetadataUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFileEntryMetadata> iterator =
			_dlFileEntryMetadatas.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryMetadata dlFileEntryMetadata = _persistence.create(pk);

		Assert.assertNotNull(dlFileEntryMetadata);

		Assert.assertEquals(dlFileEntryMetadata.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		_persistence.remove(newDLFileEntryMetadata);

		DLFileEntryMetadata existingDLFileEntryMetadata =
			_persistence.fetchByPrimaryKey(
				newDLFileEntryMetadata.getPrimaryKey());

		Assert.assertNull(existingDLFileEntryMetadata);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFileEntryMetadata();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryMetadata newDLFileEntryMetadata = _persistence.create(pk);

		newDLFileEntryMetadata.setMvccVersion(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setUuid(RandomTestUtil.randomString());

		newDLFileEntryMetadata.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newDLFileEntryMetadata.setCompanyId(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setDDMStorageId(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setDDMStructureId(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setFileEntryId(RandomTestUtil.nextLong());

		newDLFileEntryMetadata.setFileVersionId(RandomTestUtil.nextLong());

		_dlFileEntryMetadatas.add(_persistence.update(newDLFileEntryMetadata));

		DLFileEntryMetadata existingDLFileEntryMetadata =
			_persistence.findByPrimaryKey(
				newDLFileEntryMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileEntryMetadata.getMvccVersion(),
			newDLFileEntryMetadata.getMvccVersion());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getCtCollectionId(),
			newDLFileEntryMetadata.getCtCollectionId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getUuid(),
			newDLFileEntryMetadata.getUuid());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getExternalReferenceCode(),
			newDLFileEntryMetadata.getExternalReferenceCode());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getFileEntryMetadataId(),
			newDLFileEntryMetadata.getFileEntryMetadataId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getCompanyId(),
			newDLFileEntryMetadata.getCompanyId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getDDMStorageId(),
			newDLFileEntryMetadata.getDDMStorageId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getDDMStructureId(),
			newDLFileEntryMetadata.getDDMStructureId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getFileEntryId(),
			newDLFileEntryMetadata.getFileEntryId());
		Assert.assertEquals(
			existingDLFileEntryMetadata.getFileVersionId(),
			newDLFileEntryMetadata.getFileVersionId());
	}

	@Test(
		expected = DuplicateDLFileEntryMetadataExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DLFileEntryMetadata dlFileEntryMetadata = addDLFileEntryMetadata();

		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		newDLFileEntryMetadata.setCompanyId(dlFileEntryMetadata.getCompanyId());

		newDLFileEntryMetadata = _persistence.update(newDLFileEntryMetadata);

		Session session = _persistence.getCurrentSession();

		session.evict(newDLFileEntryMetadata);

		newDLFileEntryMetadata.setExternalReferenceCode(
			dlFileEntryMetadata.getExternalReferenceCode());

		_persistence.update(newDLFileEntryMetadata);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByFileEntryId() throws Exception {
		_persistence.countByFileEntryId(RandomTestUtil.nextLong());

		_persistence.countByFileEntryId(0L);
	}

	@Test
	public void testCountByFileVersionId() throws Exception {
		_persistence.countByFileVersionId(RandomTestUtil.nextLong());

		_persistence.countByFileVersionId(0L);
	}

	@Test
	public void testCountByD_F() throws Exception {
		_persistence.countByD_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByD_F(0L, 0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		DLFileEntryMetadata existingDLFileEntryMetadata =
			_persistence.findByPrimaryKey(
				newDLFileEntryMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileEntryMetadata, newDLFileEntryMetadata);
	}

	@Test(expected = NoSuchFileEntryMetadataException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DLFileEntryMetadata> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFileEntryMetadata", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "fileEntryMetadataId",
			true, "companyId", true, "DDMStorageId", true, "DDMStructureId",
			true, "fileEntryId", true, "fileVersionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		DLFileEntryMetadata existingDLFileEntryMetadata =
			_persistence.fetchByPrimaryKey(
				newDLFileEntryMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileEntryMetadata, newDLFileEntryMetadata);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryMetadata missingDLFileEntryMetadata =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLFileEntryMetadata);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFileEntryMetadata newDLFileEntryMetadata1 = addDLFileEntryMetadata();
		DLFileEntryMetadata newDLFileEntryMetadata2 = addDLFileEntryMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryMetadata1.getPrimaryKey());
		primaryKeys.add(newDLFileEntryMetadata2.getPrimaryKey());

		Map<Serializable, DLFileEntryMetadata> dlFileEntryMetadatas =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlFileEntryMetadatas.size());
		Assert.assertEquals(
			newDLFileEntryMetadata1,
			dlFileEntryMetadatas.get(newDLFileEntryMetadata1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFileEntryMetadata2,
			dlFileEntryMetadatas.get(newDLFileEntryMetadata2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFileEntryMetadata> dlFileEntryMetadatas =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntryMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryMetadata.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFileEntryMetadata> dlFileEntryMetadatas =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntryMetadatas.size());
		Assert.assertEquals(
			newDLFileEntryMetadata,
			dlFileEntryMetadatas.get(newDLFileEntryMetadata.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFileEntryMetadata> dlFileEntryMetadatas =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntryMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryMetadata.getPrimaryKey());

		Map<Serializable, DLFileEntryMetadata> dlFileEntryMetadatas =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntryMetadatas.size());
		Assert.assertEquals(
			newDLFileEntryMetadata,
			dlFileEntryMetadatas.get(newDLFileEntryMetadata.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFileEntryMetadata newDLFileEntryMetadata = addDLFileEntryMetadata();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDLFileEntryMetadata.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		DLFileEntryMetadata dlFileEntryMetadata) {

		Assert.assertEquals(
			Long.valueOf(dlFileEntryMetadata.getDDMStructureId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "DDMStructureId"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntryMetadata.getFileVersionId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileVersionId"));

		Assert.assertEquals(
			dlFileEntryMetadata.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				dlFileEntryMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntryMetadata.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected DLFileEntryMetadata addDLFileEntryMetadata() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryMetadata dlFileEntryMetadata = _persistence.create(pk);

		dlFileEntryMetadata.setMvccVersion(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setCtCollectionId(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setUuid(RandomTestUtil.randomString());

		dlFileEntryMetadata.setExternalReferenceCode(
			RandomTestUtil.randomString());

		dlFileEntryMetadata.setCompanyId(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setDDMStorageId(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setDDMStructureId(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setFileEntryId(RandomTestUtil.nextLong());

		dlFileEntryMetadata.setFileVersionId(RandomTestUtil.nextLong());

		_dlFileEntryMetadatas.add(_persistence.update(dlFileEntryMetadata));

		return dlFileEntryMetadata;
	}

	private List<DLFileEntryMetadata> _dlFileEntryMetadatas =
		new ArrayList<DLFileEntryMetadata>();
	private DLFileEntryMetadataPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}