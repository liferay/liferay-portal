/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.OutputBlob;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.DuplicateERCVersionedEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchERCVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.service.ERCVersionedEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryUtil;

import java.io.ByteArrayInputStream;
import java.io.Serializable;

import java.sql.Blob;

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
public class ERCVersionedEntryPersistenceTest {

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
		_persistence = ERCVersionedEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ERCVersionedEntry> iterator = _ercVersionedEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry ercVersionedEntry = _persistence.create(pk);

		Assert.assertNotNull(ercVersionedEntry);

		Assert.assertEquals(ercVersionedEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		_persistence.remove(newERCVersionedEntry);

		ERCVersionedEntry existingERCVersionedEntry =
			_persistence.fetchByPrimaryKey(
				newERCVersionedEntry.getPrimaryKey());

		Assert.assertNull(existingERCVersionedEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addERCVersionedEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry newERCVersionedEntry = _persistence.create(pk);

		newERCVersionedEntry.setUuid(RandomTestUtil.randomString());

		newERCVersionedEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newERCVersionedEntry.setHeadId(RandomTestUtil.nextLong());

		newERCVersionedEntry.setGroupId(RandomTestUtil.nextLong());

		newERCVersionedEntry.setCompanyId(RandomTestUtil.nextLong());
		String newBlobString = RandomTestUtil.randomString();

		byte[] newBlobBytes = newBlobString.getBytes("UTF-8");

		Blob newBlobBlob = new OutputBlob(
			new ByteArrayInputStream(newBlobBytes), newBlobBytes.length);

		newERCVersionedEntry.setBlob(newBlobBlob);

		newERCVersionedEntry = _persistence.update(newERCVersionedEntry);

		_ercVersionedEntries.add(newERCVersionedEntry);

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		ERCVersionedEntry existingERCVersionedEntry =
			_persistence.findByPrimaryKey(newERCVersionedEntry.getPrimaryKey());

		Assert.assertEquals(
			existingERCVersionedEntry.getMvccVersion(),
			newERCVersionedEntry.getMvccVersion());
		Assert.assertEquals(
			existingERCVersionedEntry.getUuid(),
			newERCVersionedEntry.getUuid());
		Assert.assertEquals(
			existingERCVersionedEntry.getExternalReferenceCode(),
			newERCVersionedEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingERCVersionedEntry.getHeadId(),
			newERCVersionedEntry.getHeadId());
		Assert.assertEquals(
			existingERCVersionedEntry.getErcVersionedEntryId(),
			newERCVersionedEntry.getErcVersionedEntryId());
		Assert.assertEquals(
			existingERCVersionedEntry.getGroupId(),
			newERCVersionedEntry.getGroupId());
		Assert.assertEquals(
			existingERCVersionedEntry.getCompanyId(),
			newERCVersionedEntry.getCompanyId());
		Blob existingBlob = existingERCVersionedEntry.getBlob();

		Assert.assertArrayEquals(
			existingBlob.getBytes(1, (int)existingBlob.length()), newBlobBytes);
	}

	@Test
	public void testCreateDraft() throws Exception {
		ERCVersionedEntry ercVersionedEntry = addERCVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry draftERCVersionedEntry = _persistence.create(pk);

		draftERCVersionedEntry.setUuid(ercVersionedEntry.getUuid());
		draftERCVersionedEntry.setExternalReferenceCode(
			ercVersionedEntry.getExternalReferenceCode());
		draftERCVersionedEntry.setHeadId(-ercVersionedEntry.getHeadId());
		draftERCVersionedEntry.setGroupId(ercVersionedEntry.getGroupId());
		draftERCVersionedEntry.setCompanyId(ercVersionedEntry.getCompanyId());

		String draftBlobString = RandomTestUtil.randomString();

		byte[] draftBlobBytes = draftBlobString.getBytes("UTF-8");

		draftERCVersionedEntry.setBlob(
			new OutputBlob(
				new ByteArrayInputStream(draftBlobBytes),
				draftBlobBytes.length));

		_ercVersionedEntries.add(_persistence.update(draftERCVersionedEntry));

		Session session = _persistence.openSession();

		session.flush();

		session.clear();

		ERCVersionedEntry persistedDraftERCVersionedEntry =
			_persistence.findByPrimaryKey(pk);

		Assert.assertEquals(
			ercVersionedEntry.getMvccVersion(),
			draftERCVersionedEntry.getMvccVersion());
		Assert.assertEquals(
			ercVersionedEntry.getUuid(), draftERCVersionedEntry.getUuid());
		Assert.assertEquals(
			ercVersionedEntry.getExternalReferenceCode(),
			draftERCVersionedEntry.getExternalReferenceCode());
		Assert.assertEquals(
			ercVersionedEntry.getHeadId(), -draftERCVersionedEntry.getHeadId());
		Assert.assertEquals(
			ercVersionedEntry.getGroupId(),
			draftERCVersionedEntry.getGroupId());
		Assert.assertEquals(
			ercVersionedEntry.getCompanyId(),
			draftERCVersionedEntry.getCompanyId());
		Blob persistedDraftBlob = persistedDraftERCVersionedEntry.getBlob();

		Assert.assertArrayEquals(
			persistedDraftBlob.getBytes(1, (int)persistedDraftBlob.length()),
			draftBlobBytes);
	}

	@Test(
		expected = DuplicateERCVersionedEntryExternalReferenceCodeException.class
	)
	public void testCreateWithExistingExternalReferenceCodeHead()
		throws Exception {

		ERCVersionedEntry ercVersionedEntry1 = addERCVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry ercVersionedEntry2 = _persistence.create(pk);

		ercVersionedEntry2.setUuid(RandomTestUtil.randomString());

		ercVersionedEntry2.setExternalReferenceCode(
			ercVersionedEntry1.getExternalReferenceCode());

		ercVersionedEntry2.setHeadId(-RandomTestUtil.nextLong());

		ercVersionedEntry2.setGroupId(ercVersionedEntry1.getGroupId());

		ercVersionedEntry2.setCompanyId(RandomTestUtil.nextLong());
		String blobString = RandomTestUtil.randomString();

		byte[] blobBytes = blobString.getBytes("UTF-8");

		Blob blobBlob = new OutputBlob(
			new ByteArrayInputStream(blobBytes), blobBytes.length);

		ercVersionedEntry2.setBlob(blobBlob);

		_ercVersionedEntries.add(_persistence.update(ercVersionedEntry2));
	}

	@Test(
		expected = DuplicateERCVersionedEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ERCVersionedEntry ercVersionedEntry = addERCVersionedEntry();

		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		newERCVersionedEntry.setGroupId(ercVersionedEntry.getGroupId());

		String blobString = RandomTestUtil.randomString();

		byte[] blobBytes = blobString.getBytes("UTF-8");

		newERCVersionedEntry.setBlob(
			new OutputBlob(
				new ByteArrayInputStream(blobBytes), blobBytes.length));

		newERCVersionedEntry = _persistence.update(newERCVersionedEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newERCVersionedEntry);

		newERCVersionedEntry.setExternalReferenceCode(
			ercVersionedEntry.getExternalReferenceCode());

		newERCVersionedEntry.setBlob(
			new OutputBlob(
				new ByteArrayInputStream(blobBytes), blobBytes.length));

		_persistence.update(newERCVersionedEntry);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Head() throws Exception {
		_persistence.countByUuid_Head("", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head("null", RandomTestUtil.randomBoolean());

		_persistence.countByUuid_Head(
			(String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Head() throws Exception {
		_persistence.countByUUID_G_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Head() throws Exception {
		_persistence.countByUuid_C_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUuid_C_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testCountByERC_G_Head() throws Exception {
		_persistence.countByERC_G_Head(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByERC_G_Head(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByERC_G_Head(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		ERCVersionedEntry existingERCVersionedEntry =
			_persistence.findByPrimaryKey(newERCVersionedEntry.getPrimaryKey());

		Assert.assertEquals(existingERCVersionedEntry, newERCVersionedEntry);
	}

	@Test(expected = NoSuchERCVersionedEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ERCVersionedEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ERCVersionedEntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "headId", true,
			"ercVersionedEntryId", true, "groupId", true, "companyId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		ERCVersionedEntry existingERCVersionedEntry =
			_persistence.fetchByPrimaryKey(
				newERCVersionedEntry.getPrimaryKey());

		Assert.assertEquals(existingERCVersionedEntry, newERCVersionedEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry missingERCVersionedEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingERCVersionedEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ERCVersionedEntry newERCVersionedEntry1 = addERCVersionedEntry();
		ERCVersionedEntry newERCVersionedEntry2 = addERCVersionedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntry1.getPrimaryKey());
		primaryKeys.add(newERCVersionedEntry2.getPrimaryKey());

		Map<Serializable, ERCVersionedEntry> ercVersionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ercVersionedEntries.size());
		Assert.assertEquals(
			newERCVersionedEntry1,
			ercVersionedEntries.get(newERCVersionedEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newERCVersionedEntry2,
			ercVersionedEntries.get(newERCVersionedEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ERCVersionedEntry> ercVersionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercVersionedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ERCVersionedEntry> ercVersionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercVersionedEntries.size());
		Assert.assertEquals(
			newERCVersionedEntry,
			ercVersionedEntries.get(newERCVersionedEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ERCVersionedEntry> ercVersionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ercVersionedEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newERCVersionedEntry.getPrimaryKey());

		Map<Serializable, ERCVersionedEntry> ercVersionedEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ercVersionedEntries.size());
		Assert.assertEquals(
			newERCVersionedEntry,
			ercVersionedEntries.get(newERCVersionedEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ERCVersionedEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ERCVersionedEntry>() {

				@Override
				public void performAction(ERCVersionedEntry ercVersionedEntry) {
					Assert.assertNotNull(ercVersionedEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCVersionedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercVersionedEntryId",
				newERCVersionedEntry.getErcVersionedEntryId()));

		List<ERCVersionedEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ERCVersionedEntry existingERCVersionedEntry = result.get(0);

		Assert.assertEquals(existingERCVersionedEntry, newERCVersionedEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCVersionedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercVersionedEntryId", RandomTestUtil.nextLong()));

		List<ERCVersionedEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCVersionedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercVersionedEntryId"));

		Object newErcVersionedEntryId =
			newERCVersionedEntry.getErcVersionedEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercVersionedEntryId", new Object[] {newErcVersionedEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingErcVersionedEntryId = result.get(0);

		Assert.assertEquals(
			existingErcVersionedEntryId, newErcVersionedEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCVersionedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ercVersionedEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ercVersionedEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newERCVersionedEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ERCVersionedEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ercVersionedEntryId",
				newERCVersionedEntry.getErcVersionedEntryId()));

		List<ERCVersionedEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ERCVersionedEntry ercVersionedEntry) {
		Assert.assertEquals(
			ercVersionedEntry.getUuid(),
			ReflectionTestUtil.invoke(
				ercVersionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ercVersionedEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ercVersionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			ercVersionedEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ercVersionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ercVersionedEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ercVersionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(ercVersionedEntry.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				ercVersionedEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected ERCVersionedEntry addERCVersionedEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry ercVersionedEntry = _persistence.create(pk);

		ercVersionedEntry.setUuid(RandomTestUtil.randomString());

		ercVersionedEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		ercVersionedEntry.setHeadId(-pk);

		ercVersionedEntry.setGroupId(RandomTestUtil.nextLong());

		ercVersionedEntry.setCompanyId(RandomTestUtil.nextLong());
		String blobString = RandomTestUtil.randomString();

		byte[] blobBytes = blobString.getBytes("UTF-8");

		Blob blobBlob = new OutputBlob(
			new ByteArrayInputStream(blobBytes), blobBytes.length);

		ercVersionedEntry.setBlob(blobBlob);

		_ercVersionedEntries.add(_persistence.update(ercVersionedEntry));

		return ercVersionedEntry;
	}

	private List<ERCVersionedEntry> _ercVersionedEntries =
		new ArrayList<ERCVersionedEntry>();
	private ERCVersionedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-499275962