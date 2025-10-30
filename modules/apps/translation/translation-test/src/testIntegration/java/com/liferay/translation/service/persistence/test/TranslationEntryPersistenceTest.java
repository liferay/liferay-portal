/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.translation.exception.NoSuchEntryException;
import com.liferay.translation.model.TranslationEntry;
import com.liferay.translation.service.persistence.TranslationEntryPersistence;
import com.liferay.translation.service.persistence.TranslationEntryUtil;

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
public class TranslationEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.translation.service"));

	@Before
	public void setUp() {
		_persistence = TranslationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<TranslationEntry> iterator = _translationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TranslationEntry translationEntry = _persistence.create(pk);

		Assert.assertNotNull(translationEntry);

		Assert.assertEquals(translationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		TranslationEntry newTranslationEntry = addTranslationEntry();

		_persistence.remove(newTranslationEntry);

		TranslationEntry existingTranslationEntry =
			_persistence.fetchByPrimaryKey(newTranslationEntry.getPrimaryKey());

		Assert.assertNull(existingTranslationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTranslationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TranslationEntry newTranslationEntry = _persistence.create(pk);

		newTranslationEntry.setMvccVersion(RandomTestUtil.nextLong());

		newTranslationEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newTranslationEntry.setUuid(RandomTestUtil.randomString());

		newTranslationEntry.setGroupId(RandomTestUtil.nextLong());

		newTranslationEntry.setCompanyId(RandomTestUtil.nextLong());

		newTranslationEntry.setUserId(RandomTestUtil.nextLong());

		newTranslationEntry.setUserName(RandomTestUtil.randomString());

		newTranslationEntry.setCreateDate(RandomTestUtil.nextDate());

		newTranslationEntry.setModifiedDate(RandomTestUtil.nextDate());

		newTranslationEntry.setClassNameId(RandomTestUtil.nextLong());

		newTranslationEntry.setClassPK(RandomTestUtil.nextLong());

		newTranslationEntry.setContent(RandomTestUtil.randomString());

		newTranslationEntry.setContentType(RandomTestUtil.randomString());

		newTranslationEntry.setLanguageId(RandomTestUtil.randomString());

		newTranslationEntry.setStatus(RandomTestUtil.nextInt());

		newTranslationEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newTranslationEntry.setStatusByUserName(RandomTestUtil.randomString());

		newTranslationEntry.setStatusDate(RandomTestUtil.nextDate());

		_translationEntries.add(_persistence.update(newTranslationEntry));

		TranslationEntry existingTranslationEntry =
			_persistence.findByPrimaryKey(newTranslationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingTranslationEntry.getMvccVersion(),
			newTranslationEntry.getMvccVersion());
		Assert.assertEquals(
			existingTranslationEntry.getCtCollectionId(),
			newTranslationEntry.getCtCollectionId());
		Assert.assertEquals(
			existingTranslationEntry.getUuid(), newTranslationEntry.getUuid());
		Assert.assertEquals(
			existingTranslationEntry.getTranslationEntryId(),
			newTranslationEntry.getTranslationEntryId());
		Assert.assertEquals(
			existingTranslationEntry.getGroupId(),
			newTranslationEntry.getGroupId());
		Assert.assertEquals(
			existingTranslationEntry.getCompanyId(),
			newTranslationEntry.getCompanyId());
		Assert.assertEquals(
			existingTranslationEntry.getUserId(),
			newTranslationEntry.getUserId());
		Assert.assertEquals(
			existingTranslationEntry.getUserName(),
			newTranslationEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTranslationEntry.getCreateDate()),
			Time.getShortTimestamp(newTranslationEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingTranslationEntry.getModifiedDate()),
			Time.getShortTimestamp(newTranslationEntry.getModifiedDate()));
		Assert.assertEquals(
			existingTranslationEntry.getClassNameId(),
			newTranslationEntry.getClassNameId());
		Assert.assertEquals(
			existingTranslationEntry.getClassPK(),
			newTranslationEntry.getClassPK());
		Assert.assertEquals(
			existingTranslationEntry.getContent(),
			newTranslationEntry.getContent());
		Assert.assertEquals(
			existingTranslationEntry.getContentType(),
			newTranslationEntry.getContentType());
		Assert.assertEquals(
			existingTranslationEntry.getLanguageId(),
			newTranslationEntry.getLanguageId());
		Assert.assertEquals(
			existingTranslationEntry.getStatus(),
			newTranslationEntry.getStatus());
		Assert.assertEquals(
			existingTranslationEntry.getStatusByUserId(),
			newTranslationEntry.getStatusByUserId());
		Assert.assertEquals(
			existingTranslationEntry.getStatusByUserName(),
			newTranslationEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingTranslationEntry.getStatusDate()),
			Time.getShortTimestamp(newTranslationEntry.getStatusDate()));
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
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_L() throws Exception {
		_persistence.countByC_C_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_C_L(0L, 0L, "null");

		_persistence.countByC_C_L(0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		TranslationEntry newTranslationEntry = addTranslationEntry();

		TranslationEntry existingTranslationEntry =
			_persistence.findByPrimaryKey(newTranslationEntry.getPrimaryKey());

		Assert.assertEquals(existingTranslationEntry, newTranslationEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<TranslationEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"TranslationEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "translationEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true,
			"contentType", true, "languageId", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		TranslationEntry newTranslationEntry = addTranslationEntry();

		TranslationEntry existingTranslationEntry =
			_persistence.fetchByPrimaryKey(newTranslationEntry.getPrimaryKey());

		Assert.assertEquals(existingTranslationEntry, newTranslationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TranslationEntry missingTranslationEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTranslationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		TranslationEntry newTranslationEntry1 = addTranslationEntry();
		TranslationEntry newTranslationEntry2 = addTranslationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTranslationEntry1.getPrimaryKey());
		primaryKeys.add(newTranslationEntry2.getPrimaryKey());

		Map<Serializable, TranslationEntry> translationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, translationEntries.size());
		Assert.assertEquals(
			newTranslationEntry1,
			translationEntries.get(newTranslationEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newTranslationEntry2,
			translationEntries.get(newTranslationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, TranslationEntry> translationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(translationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		TranslationEntry newTranslationEntry = addTranslationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTranslationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, TranslationEntry> translationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, translationEntries.size());
		Assert.assertEquals(
			newTranslationEntry,
			translationEntries.get(newTranslationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, TranslationEntry> translationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(translationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		TranslationEntry newTranslationEntry = addTranslationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTranslationEntry.getPrimaryKey());

		Map<Serializable, TranslationEntry> translationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, translationEntries.size());
		Assert.assertEquals(
			newTranslationEntry,
			translationEntries.get(newTranslationEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		TranslationEntry newTranslationEntry = addTranslationEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newTranslationEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(TranslationEntry translationEntry) {
		Assert.assertEquals(
			translationEntry.getUuid(),
			ReflectionTestUtil.invoke(
				translationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(translationEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				translationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(translationEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				translationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(translationEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				translationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			translationEntry.getLanguageId(),
			ReflectionTestUtil.invoke(
				translationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected TranslationEntry addTranslationEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TranslationEntry translationEntry = _persistence.create(pk);

		translationEntry.setMvccVersion(RandomTestUtil.nextLong());

		translationEntry.setCtCollectionId(RandomTestUtil.nextLong());

		translationEntry.setUuid(RandomTestUtil.randomString());

		translationEntry.setGroupId(RandomTestUtil.nextLong());

		translationEntry.setCompanyId(RandomTestUtil.nextLong());

		translationEntry.setUserId(RandomTestUtil.nextLong());

		translationEntry.setUserName(RandomTestUtil.randomString());

		translationEntry.setCreateDate(RandomTestUtil.nextDate());

		translationEntry.setModifiedDate(RandomTestUtil.nextDate());

		translationEntry.setClassNameId(RandomTestUtil.nextLong());

		translationEntry.setClassPK(RandomTestUtil.nextLong());

		translationEntry.setContent(RandomTestUtil.randomString());

		translationEntry.setContentType(RandomTestUtil.randomString());

		translationEntry.setLanguageId(RandomTestUtil.randomString());

		translationEntry.setStatus(RandomTestUtil.nextInt());

		translationEntry.setStatusByUserId(RandomTestUtil.nextLong());

		translationEntry.setStatusByUserName(RandomTestUtil.randomString());

		translationEntry.setStatusDate(RandomTestUtil.nextDate());

		_translationEntries.add(_persistence.update(translationEntry));

		return translationEntry;
	}

	private List<TranslationEntry> _translationEntries =
		new ArrayList<TranslationEntry>();
	private TranslationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}