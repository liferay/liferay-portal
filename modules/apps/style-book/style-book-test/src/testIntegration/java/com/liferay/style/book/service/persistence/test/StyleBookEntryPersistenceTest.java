/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.style.book.exception.DuplicateStyleBookEntryExternalReferenceCodeException;
import com.liferay.style.book.exception.NoSuchEntryException;
import com.liferay.style.book.model.StyleBookEntry;
import com.liferay.style.book.service.persistence.StyleBookEntryPersistence;
import com.liferay.style.book.service.persistence.StyleBookEntryUtil;

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
public class StyleBookEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.style.book.service"));

	@Before
	public void setUp() {
		_persistence = StyleBookEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<StyleBookEntry> iterator = _styleBookEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntry styleBookEntry = _persistence.create(pk);

		Assert.assertNotNull(styleBookEntry);

		Assert.assertEquals(styleBookEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		_persistence.remove(newStyleBookEntry);

		StyleBookEntry existingStyleBookEntry = _persistence.fetchByPrimaryKey(
			newStyleBookEntry.getPrimaryKey());

		Assert.assertNull(existingStyleBookEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addStyleBookEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntry newStyleBookEntry = _persistence.create(pk);

		newStyleBookEntry.setMvccVersion(RandomTestUtil.nextLong());

		newStyleBookEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newStyleBookEntry.setUuid(RandomTestUtil.randomString());

		newStyleBookEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newStyleBookEntry.setHeadId(RandomTestUtil.nextLong());

		newStyleBookEntry.setGroupId(RandomTestUtil.nextLong());

		newStyleBookEntry.setCompanyId(RandomTestUtil.nextLong());

		newStyleBookEntry.setUserId(RandomTestUtil.nextLong());

		newStyleBookEntry.setUserName(RandomTestUtil.randomString());

		newStyleBookEntry.setCreateDate(RandomTestUtil.nextDate());

		newStyleBookEntry.setModifiedDate(RandomTestUtil.nextDate());

		newStyleBookEntry.setDefaultStyleBookEntry(
			RandomTestUtil.randomBoolean());

		newStyleBookEntry.setFrontendTokensValues(
			RandomTestUtil.randomString());

		newStyleBookEntry.setName(RandomTestUtil.randomString());

		newStyleBookEntry.setPreviewFileEntryId(RandomTestUtil.nextLong());

		newStyleBookEntry.setStyleBookEntryKey(RandomTestUtil.randomString());

		newStyleBookEntry.setThemeId(RandomTestUtil.randomString());

		_styleBookEntries.add(_persistence.update(newStyleBookEntry));

		StyleBookEntry existingStyleBookEntry = _persistence.findByPrimaryKey(
			newStyleBookEntry.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookEntry.getMvccVersion(),
			newStyleBookEntry.getMvccVersion());
		Assert.assertEquals(
			existingStyleBookEntry.getCtCollectionId(),
			newStyleBookEntry.getCtCollectionId());
		Assert.assertEquals(
			existingStyleBookEntry.getUuid(), newStyleBookEntry.getUuid());
		Assert.assertEquals(
			existingStyleBookEntry.getExternalReferenceCode(),
			newStyleBookEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingStyleBookEntry.getHeadId(), newStyleBookEntry.getHeadId());
		Assert.assertEquals(
			existingStyleBookEntry.getStyleBookEntryId(),
			newStyleBookEntry.getStyleBookEntryId());
		Assert.assertEquals(
			existingStyleBookEntry.getGroupId(),
			newStyleBookEntry.getGroupId());
		Assert.assertEquals(
			existingStyleBookEntry.getCompanyId(),
			newStyleBookEntry.getCompanyId());
		Assert.assertEquals(
			existingStyleBookEntry.getUserId(), newStyleBookEntry.getUserId());
		Assert.assertEquals(
			existingStyleBookEntry.getUserName(),
			newStyleBookEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingStyleBookEntry.getCreateDate()),
			Time.getShortTimestamp(newStyleBookEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingStyleBookEntry.getModifiedDate()),
			Time.getShortTimestamp(newStyleBookEntry.getModifiedDate()));
		Assert.assertEquals(
			existingStyleBookEntry.isDefaultStyleBookEntry(),
			newStyleBookEntry.isDefaultStyleBookEntry());
		Assert.assertEquals(
			existingStyleBookEntry.getFrontendTokensValues(),
			newStyleBookEntry.getFrontendTokensValues());
		Assert.assertEquals(
			existingStyleBookEntry.getName(), newStyleBookEntry.getName());
		Assert.assertEquals(
			existingStyleBookEntry.getPreviewFileEntryId(),
			newStyleBookEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			existingStyleBookEntry.getStyleBookEntryKey(),
			newStyleBookEntry.getStyleBookEntryKey());
		Assert.assertEquals(
			existingStyleBookEntry.getThemeId(),
			newStyleBookEntry.getThemeId());
	}

	@Test
	public void testCreateDraft() throws Exception {
		StyleBookEntry styleBookEntry = addStyleBookEntry();

		long pk = RandomTestUtil.nextLong();

		StyleBookEntry draftStyleBookEntry = _persistence.create(pk);

		draftStyleBookEntry.setMvccVersion(styleBookEntry.getMvccVersion());
		draftStyleBookEntry.setCtCollectionId(
			styleBookEntry.getCtCollectionId());
		draftStyleBookEntry.setUuid(styleBookEntry.getUuid());
		draftStyleBookEntry.setExternalReferenceCode(
			styleBookEntry.getExternalReferenceCode());
		draftStyleBookEntry.setHeadId(-styleBookEntry.getHeadId());
		draftStyleBookEntry.setGroupId(styleBookEntry.getGroupId());
		draftStyleBookEntry.setCompanyId(styleBookEntry.getCompanyId());
		draftStyleBookEntry.setUserId(styleBookEntry.getUserId());
		draftStyleBookEntry.setUserName(styleBookEntry.getUserName());
		draftStyleBookEntry.setCreateDate(styleBookEntry.getCreateDate());
		draftStyleBookEntry.setModifiedDate(styleBookEntry.getModifiedDate());
		draftStyleBookEntry.setDefaultStyleBookEntry(
			styleBookEntry.getDefaultStyleBookEntry());
		draftStyleBookEntry.setFrontendTokensValues(
			styleBookEntry.getFrontendTokensValues());
		draftStyleBookEntry.setName(styleBookEntry.getName());
		draftStyleBookEntry.setPreviewFileEntryId(
			styleBookEntry.getPreviewFileEntryId());
		draftStyleBookEntry.setStyleBookEntryKey(
			styleBookEntry.getStyleBookEntryKey());
		draftStyleBookEntry.setThemeId(styleBookEntry.getThemeId());

		_styleBookEntries.add(_persistence.update(draftStyleBookEntry));

		Assert.assertEquals(
			styleBookEntry.getMvccVersion(),
			draftStyleBookEntry.getMvccVersion());
		Assert.assertEquals(
			styleBookEntry.getCtCollectionId(),
			draftStyleBookEntry.getCtCollectionId());
		Assert.assertEquals(
			styleBookEntry.getUuid(), draftStyleBookEntry.getUuid());
		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			draftStyleBookEntry.getExternalReferenceCode());
		Assert.assertEquals(
			styleBookEntry.getHeadId(), -draftStyleBookEntry.getHeadId());
		Assert.assertEquals(
			styleBookEntry.getGroupId(), draftStyleBookEntry.getGroupId());
		Assert.assertEquals(
			styleBookEntry.getCompanyId(), draftStyleBookEntry.getCompanyId());
		Assert.assertEquals(
			styleBookEntry.getUserId(), draftStyleBookEntry.getUserId());
		Assert.assertEquals(
			styleBookEntry.getUserName(), draftStyleBookEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(styleBookEntry.getCreateDate()),
			Time.getShortTimestamp(draftStyleBookEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(styleBookEntry.getModifiedDate()),
			Time.getShortTimestamp(draftStyleBookEntry.getModifiedDate()));
		Assert.assertEquals(
			styleBookEntry.isDefaultStyleBookEntry(),
			draftStyleBookEntry.isDefaultStyleBookEntry());
		Assert.assertEquals(
			styleBookEntry.getFrontendTokensValues(),
			draftStyleBookEntry.getFrontendTokensValues());
		Assert.assertEquals(
			styleBookEntry.getName(), draftStyleBookEntry.getName());
		Assert.assertEquals(
			styleBookEntry.getPreviewFileEntryId(),
			draftStyleBookEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryKey(),
			draftStyleBookEntry.getStyleBookEntryKey());
		Assert.assertEquals(
			styleBookEntry.getThemeId(), draftStyleBookEntry.getThemeId());
	}

	@Test(
		expected = DuplicateStyleBookEntryExternalReferenceCodeException.class
	)
	public void testCreateWithExistingExternalReferenceCodeHead()
		throws Exception {

		StyleBookEntry styleBookEntry1 = addStyleBookEntry();

		long pk = RandomTestUtil.nextLong();

		StyleBookEntry styleBookEntry2 = _persistence.create(pk);

		styleBookEntry2.setMvccVersion(RandomTestUtil.nextLong());

		styleBookEntry2.setCtCollectionId(RandomTestUtil.nextLong());

		styleBookEntry2.setUuid(RandomTestUtil.randomString());

		styleBookEntry2.setExternalReferenceCode(
			styleBookEntry1.getExternalReferenceCode());

		styleBookEntry2.setHeadId(-RandomTestUtil.nextLong());

		styleBookEntry2.setGroupId(styleBookEntry1.getGroupId());

		styleBookEntry2.setCompanyId(RandomTestUtil.nextLong());

		styleBookEntry2.setUserId(RandomTestUtil.nextLong());

		styleBookEntry2.setUserName(RandomTestUtil.randomString());

		styleBookEntry2.setCreateDate(RandomTestUtil.nextDate());

		styleBookEntry2.setModifiedDate(RandomTestUtil.nextDate());

		styleBookEntry2.setDefaultStyleBookEntry(
			RandomTestUtil.randomBoolean());

		styleBookEntry2.setFrontendTokensValues(RandomTestUtil.randomString());

		styleBookEntry2.setName(RandomTestUtil.randomString());

		styleBookEntry2.setPreviewFileEntryId(RandomTestUtil.nextLong());

		styleBookEntry2.setStyleBookEntryKey(RandomTestUtil.randomString());

		styleBookEntry2.setThemeId(RandomTestUtil.randomString());

		_styleBookEntries.add(_persistence.update(styleBookEntry2));
	}

	@Test(
		expected = DuplicateStyleBookEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		StyleBookEntry styleBookEntry = addStyleBookEntry();

		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		newStyleBookEntry.setGroupId(styleBookEntry.getGroupId());

		newStyleBookEntry = _persistence.update(newStyleBookEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newStyleBookEntry);

		newStyleBookEntry.setExternalReferenceCode(
			styleBookEntry.getExternalReferenceCode());

		_persistence.update(newStyleBookEntry);
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Head() throws Exception {
		_persistence.countByGroupId_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByGroupId_Head(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_D() throws Exception {
		_persistence.countByG_D(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_D(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_D_Head() throws Exception {
		_persistence.countByG_D_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_D_Head(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_LikeN() throws Exception {
		_persistence.countByG_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeN(0L, "null");

		_persistence.countByG_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeN_Head() throws Exception {
		_persistence.countByG_LikeN_Head(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeN_Head(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_LikeN_Head(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_SBEK() throws Exception {
		_persistence.countByG_SBEK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_SBEK(0L, "null");

		_persistence.countByG_SBEK(0L, (String)null);
	}

	@Test
	public void testCountByG_SBEK_Head() throws Exception {
		_persistence.countByG_SBEK_Head(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_SBEK_Head(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_SBEK_Head(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_T_Head() throws Exception {
		_persistence.countByG_T_Head(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByG_T_Head(
			0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByG_T_Head(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_D_T() throws Exception {
		_persistence.countByG_D_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_D_T(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_D_T(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_D_T_Head() throws Exception {
		_persistence.countByG_D_T_Head(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_D_T_Head(
			0L, RandomTestUtil.randomBoolean(), "null",
			RandomTestUtil.randomBoolean());

		_persistence.countByG_D_T_Head(
			0L, RandomTestUtil.randomBoolean(), (String)null,
			RandomTestUtil.randomBoolean());
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
		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		StyleBookEntry existingStyleBookEntry = _persistence.findByPrimaryKey(
			newStyleBookEntry.getPrimaryKey());

		Assert.assertEquals(existingStyleBookEntry, newStyleBookEntry);
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

	protected OrderByComparator<StyleBookEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"StyleBookEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "headId", true,
			"styleBookEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "defaultStyleBookEntry", true, "name", true,
			"previewFileEntryId", true, "styleBookEntryKey", true, "themeId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		StyleBookEntry existingStyleBookEntry = _persistence.fetchByPrimaryKey(
			newStyleBookEntry.getPrimaryKey());

		Assert.assertEquals(existingStyleBookEntry, newStyleBookEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntry missingStyleBookEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingStyleBookEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		StyleBookEntry newStyleBookEntry1 = addStyleBookEntry();
		StyleBookEntry newStyleBookEntry2 = addStyleBookEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntry1.getPrimaryKey());
		primaryKeys.add(newStyleBookEntry2.getPrimaryKey());

		Map<Serializable, StyleBookEntry> styleBookEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, styleBookEntries.size());
		Assert.assertEquals(
			newStyleBookEntry1,
			styleBookEntries.get(newStyleBookEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newStyleBookEntry2,
			styleBookEntries.get(newStyleBookEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, StyleBookEntry> styleBookEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, StyleBookEntry> styleBookEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookEntries.size());
		Assert.assertEquals(
			newStyleBookEntry,
			styleBookEntries.get(newStyleBookEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, StyleBookEntry> styleBookEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntry.getPrimaryKey());

		Map<Serializable, StyleBookEntry> styleBookEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookEntries.size());
		Assert.assertEquals(
			newStyleBookEntry,
			styleBookEntries.get(newStyleBookEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		StyleBookEntry newStyleBookEntry = addStyleBookEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newStyleBookEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(StyleBookEntry styleBookEntry) {
		Assert.assertEquals(
			styleBookEntry.getUuid(),
			ReflectionTestUtil.invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(styleBookEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(styleBookEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			styleBookEntry.getStyleBookEntryKey(),
			ReflectionTestUtil.invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "styleBookEntryKey"));

		Assert.assertEquals(
			styleBookEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(styleBookEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(styleBookEntry.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected StyleBookEntry addStyleBookEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntry styleBookEntry = _persistence.create(pk);

		styleBookEntry.setMvccVersion(RandomTestUtil.nextLong());

		styleBookEntry.setCtCollectionId(RandomTestUtil.nextLong());

		styleBookEntry.setUuid(RandomTestUtil.randomString());

		styleBookEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		styleBookEntry.setHeadId(-pk);

		styleBookEntry.setGroupId(RandomTestUtil.nextLong());

		styleBookEntry.setCompanyId(RandomTestUtil.nextLong());

		styleBookEntry.setUserId(RandomTestUtil.nextLong());

		styleBookEntry.setUserName(RandomTestUtil.randomString());

		styleBookEntry.setCreateDate(RandomTestUtil.nextDate());

		styleBookEntry.setModifiedDate(RandomTestUtil.nextDate());

		styleBookEntry.setDefaultStyleBookEntry(RandomTestUtil.randomBoolean());

		styleBookEntry.setFrontendTokensValues(RandomTestUtil.randomString());

		styleBookEntry.setName(RandomTestUtil.randomString());

		styleBookEntry.setPreviewFileEntryId(RandomTestUtil.nextLong());

		styleBookEntry.setStyleBookEntryKey(RandomTestUtil.randomString());

		styleBookEntry.setThemeId(RandomTestUtil.randomString());

		_styleBookEntries.add(_persistence.update(styleBookEntry));

		return styleBookEntry;
	}

	private List<StyleBookEntry> _styleBookEntries =
		new ArrayList<StyleBookEntry>();
	private StyleBookEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}