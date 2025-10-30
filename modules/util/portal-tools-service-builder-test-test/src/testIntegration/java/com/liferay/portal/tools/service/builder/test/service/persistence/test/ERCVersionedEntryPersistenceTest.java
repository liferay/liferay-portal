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
import com.liferay.portal.tools.service.builder.test.exception.DuplicateERCVersionedEntryExternalReferenceCodeException;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchERCVersionedEntryException;
import com.liferay.portal.tools.service.builder.test.model.ERCVersionedEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ERCVersionedEntryUtil;

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

		newERCVersionedEntry.setMvccVersion(RandomTestUtil.nextLong());

		newERCVersionedEntry.setUuid(RandomTestUtil.randomString());

		newERCVersionedEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newERCVersionedEntry.setHeadId(RandomTestUtil.nextLong());

		newERCVersionedEntry.setGroupId(RandomTestUtil.nextLong());

		newERCVersionedEntry.setCompanyId(RandomTestUtil.nextLong());

		_ercVersionedEntries.add(_persistence.update(newERCVersionedEntry));

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
	}

	@Test
	public void testCreateDraft() throws Exception {
		ERCVersionedEntry ercVersionedEntry = addERCVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry draftERCVersionedEntry = _persistence.create(pk);

		draftERCVersionedEntry.setMvccVersion(
			ercVersionedEntry.getMvccVersion());
		draftERCVersionedEntry.setUuid(ercVersionedEntry.getUuid());
		draftERCVersionedEntry.setExternalReferenceCode(
			ercVersionedEntry.getExternalReferenceCode());
		draftERCVersionedEntry.setHeadId(-ercVersionedEntry.getHeadId());
		draftERCVersionedEntry.setGroupId(ercVersionedEntry.getGroupId());
		draftERCVersionedEntry.setCompanyId(ercVersionedEntry.getCompanyId());

		_ercVersionedEntries.add(_persistence.update(draftERCVersionedEntry));

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
	}

	@Test(
		expected = DuplicateERCVersionedEntryExternalReferenceCodeException.class
	)
	public void testCreateWithExistingExternalReferenceCodeHead()
		throws Exception {

		ERCVersionedEntry ercVersionedEntry1 = addERCVersionedEntry();

		long pk = RandomTestUtil.nextLong();

		ERCVersionedEntry ercVersionedEntry2 = _persistence.create(pk);

		ercVersionedEntry2.setMvccVersion(RandomTestUtil.nextLong());

		ercVersionedEntry2.setUuid(RandomTestUtil.randomString());

		ercVersionedEntry2.setExternalReferenceCode(
			ercVersionedEntry1.getExternalReferenceCode());

		ercVersionedEntry2.setHeadId(-RandomTestUtil.nextLong());

		ercVersionedEntry2.setGroupId(ercVersionedEntry1.getGroupId());

		ercVersionedEntry2.setCompanyId(RandomTestUtil.nextLong());

		_ercVersionedEntries.add(_persistence.update(ercVersionedEntry2));
	}

	@Test(
		expected = DuplicateERCVersionedEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		ERCVersionedEntry ercVersionedEntry = addERCVersionedEntry();

		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		newERCVersionedEntry.setGroupId(ercVersionedEntry.getGroupId());

		newERCVersionedEntry = _persistence.update(newERCVersionedEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newERCVersionedEntry);

		newERCVersionedEntry.setExternalReferenceCode(
			ercVersionedEntry.getExternalReferenceCode());

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
	public void testResetOriginalValues() throws Exception {
		ERCVersionedEntry newERCVersionedEntry = addERCVersionedEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newERCVersionedEntry.getPrimaryKey()));
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

		ercVersionedEntry.setMvccVersion(RandomTestUtil.nextLong());

		ercVersionedEntry.setUuid(RandomTestUtil.randomString());

		ercVersionedEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		ercVersionedEntry.setHeadId(-pk);

		ercVersionedEntry.setGroupId(RandomTestUtil.nextLong());

		ercVersionedEntry.setCompanyId(RandomTestUtil.nextLong());

		_ercVersionedEntries.add(_persistence.update(ercVersionedEntry));

		return ercVersionedEntry;
	}

	private List<ERCVersionedEntry> _ercVersionedEntries =
		new ArrayList<ERCVersionedEntry>();
	private ERCVersionedEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}