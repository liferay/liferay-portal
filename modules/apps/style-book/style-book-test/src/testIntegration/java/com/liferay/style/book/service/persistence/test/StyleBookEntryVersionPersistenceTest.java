/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.style.book.exception.NoSuchEntryVersionException;
import com.liferay.style.book.model.StyleBookEntryVersion;
import com.liferay.style.book.service.persistence.StyleBookEntryVersionPersistence;
import com.liferay.style.book.service.persistence.StyleBookEntryVersionUtil;

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
public class StyleBookEntryVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.style.book.service"));

	@Before
	public void setUp() {
		_persistence = StyleBookEntryVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<StyleBookEntryVersion> iterator =
			_styleBookEntryVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntryVersion styleBookEntryVersion = _persistence.create(pk);

		Assert.assertNotNull(styleBookEntryVersion);

		Assert.assertEquals(styleBookEntryVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		_persistence.remove(newStyleBookEntryVersion);

		StyleBookEntryVersion existingStyleBookEntryVersion =
			_persistence.fetchByPrimaryKey(
				newStyleBookEntryVersion.getPrimaryKey());

		Assert.assertNull(existingStyleBookEntryVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addStyleBookEntryVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntryVersion newStyleBookEntryVersion = _persistence.create(
			pk);

		newStyleBookEntryVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setVersion(RandomTestUtil.nextInt());

		newStyleBookEntryVersion.setUuid(RandomTestUtil.randomString());

		newStyleBookEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newStyleBookEntryVersion.setStyleBookEntryId(RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setGroupId(RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setUserId(RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setUserName(RandomTestUtil.randomString());

		newStyleBookEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		newStyleBookEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		newStyleBookEntryVersion.setDefaultStyleBookEntry(
			RandomTestUtil.randomBoolean());

		newStyleBookEntryVersion.setFrontendTokensValues(
			RandomTestUtil.randomString());

		newStyleBookEntryVersion.setName(RandomTestUtil.randomString());

		newStyleBookEntryVersion.setPreviewFileEntryId(
			RandomTestUtil.nextLong());

		newStyleBookEntryVersion.setStyleBookEntryKey(
			RandomTestUtil.randomString());

		newStyleBookEntryVersion.setThemeId(RandomTestUtil.randomString());

		newStyleBookEntryVersion = _persistence.update(
			newStyleBookEntryVersion);

		_styleBookEntryVersions.add(newStyleBookEntryVersion);

		StyleBookEntryVersion existingStyleBookEntryVersion =
			_persistence.findByPrimaryKey(
				newStyleBookEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookEntryVersion.getMvccVersion(),
			newStyleBookEntryVersion.getMvccVersion());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getCtCollectionId(),
			newStyleBookEntryVersion.getCtCollectionId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getStyleBookEntryVersionId(),
			newStyleBookEntryVersion.getStyleBookEntryVersionId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getVersion(),
			newStyleBookEntryVersion.getVersion());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getUuid(),
			newStyleBookEntryVersion.getUuid());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getExternalReferenceCode(),
			newStyleBookEntryVersion.getExternalReferenceCode());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getStyleBookEntryId(),
			newStyleBookEntryVersion.getStyleBookEntryId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getGroupId(),
			newStyleBookEntryVersion.getGroupId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getCompanyId(),
			newStyleBookEntryVersion.getCompanyId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getUserId(),
			newStyleBookEntryVersion.getUserId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getUserName(),
			newStyleBookEntryVersion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingStyleBookEntryVersion.getCreateDate()),
			Time.getShortTimestamp(newStyleBookEntryVersion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingStyleBookEntryVersion.getModifiedDate()),
			Time.getShortTimestamp(newStyleBookEntryVersion.getModifiedDate()));
		Assert.assertEquals(
			existingStyleBookEntryVersion.isDefaultStyleBookEntry(),
			newStyleBookEntryVersion.isDefaultStyleBookEntry());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getFrontendTokensValues(),
			newStyleBookEntryVersion.getFrontendTokensValues());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getName(),
			newStyleBookEntryVersion.getName());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getPreviewFileEntryId(),
			newStyleBookEntryVersion.getPreviewFileEntryId());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getStyleBookEntryKey(),
			newStyleBookEntryVersion.getStyleBookEntryKey());
		Assert.assertEquals(
			existingStyleBookEntryVersion.getThemeId(),
			newStyleBookEntryVersion.getThemeId());
	}

	@Test
	public void testCountByStyleBookEntryId() throws Exception {
		_persistence.countByStyleBookEntryId(RandomTestUtil.nextLong());

		_persistence.countByStyleBookEntryId(0L);
	}

	@Test
	public void testCountByStyleBookEntryId_Version() throws Exception {
		_persistence.countByStyleBookEntryId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByStyleBookEntryId_Version(0L, 0);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_Version() throws Exception {
		_persistence.countByUuid_Version("", RandomTestUtil.nextInt());

		_persistence.countByUuid_Version("null", 0);

		_persistence.countByUuid_Version((String)null, 0);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUUID_G_Version() throws Exception {
		_persistence.countByUUID_G_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUUID_G_Version("null", 0L, 0);

		_persistence.countByUUID_G_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C_Version() throws Exception {
		_persistence.countByUuid_C_Version(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByUuid_C_Version("null", 0L, 0);

		_persistence.countByUuid_C_Version((String)null, 0L, 0);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupId_Version() throws Exception {
		_persistence.countByGroupId_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByGroupId_Version(0L, 0);
	}

	@Test
	public void testCountByG_D() throws Exception {
		_persistence.countByG_D(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_D(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_D_Version() throws Exception {
		_persistence.countByG_D_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_D_Version(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByG_N_Version() throws Exception {
		_persistence.countByG_N_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_N_Version(0L, "null", 0);

		_persistence.countByG_N_Version(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_LikeN() throws Exception {
		_persistence.countByG_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeN(0L, "null");

		_persistence.countByG_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeN_Version() throws Exception {
		_persistence.countByG_LikeN_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_LikeN_Version(0L, "null", 0);

		_persistence.countByG_LikeN_Version(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_SBEK() throws Exception {
		_persistence.countByG_SBEK(RandomTestUtil.nextLong(), "");

		_persistence.countByG_SBEK(0L, "null");

		_persistence.countByG_SBEK(0L, (String)null);
	}

	@Test
	public void testCountByG_SBEK_Version() throws Exception {
		_persistence.countByG_SBEK_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_SBEK_Version(0L, "null", 0);

		_persistence.countByG_SBEK_Version(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_T_Version() throws Exception {
		_persistence.countByG_T_Version(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_T_Version(0L, "null", 0);

		_persistence.countByG_T_Version(0L, (String)null, 0);
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
	public void testCountByG_D_T_Version() throws Exception {
		_persistence.countByG_D_T_Version(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_D_T_Version(
			0L, RandomTestUtil.randomBoolean(), "null", 0);

		_persistence.countByG_D_T_Version(
			0L, RandomTestUtil.randomBoolean(), (String)null, 0);
	}

	@Test
	public void testCountByG_LikeN_T() throws Exception {
		_persistence.countByG_LikeN_T(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_LikeN_T(0L, "null", "null");

		_persistence.countByG_LikeN_T(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_LikeN_T_Version() throws Exception {
		_persistence.countByG_LikeN_T_Version(
			RandomTestUtil.nextLong(), "", "", RandomTestUtil.nextInt());

		_persistence.countByG_LikeN_T_Version(0L, "null", "null", 0);

		_persistence.countByG_LikeN_T_Version(
			0L, (String)null, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		StyleBookEntryVersion existingStyleBookEntryVersion =
			_persistence.findByPrimaryKey(
				newStyleBookEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookEntryVersion, newStyleBookEntryVersion);
	}

	@Test(expected = NoSuchEntryVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<StyleBookEntryVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"StyleBookEntryVersion", "mvccVersion", true, "ctCollectionId",
			true, "styleBookEntryVersionId", true, "version", true, "uuid",
			true, "externalReferenceCode", true, "styleBookEntryId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true,
			"defaultStyleBookEntry", true, "name", true, "previewFileEntryId",
			true, "styleBookEntryKey", true, "themeId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		StyleBookEntryVersion existingStyleBookEntryVersion =
			_persistence.fetchByPrimaryKey(
				newStyleBookEntryVersion.getPrimaryKey());

		Assert.assertEquals(
			existingStyleBookEntryVersion, newStyleBookEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		StyleBookEntryVersion missingStyleBookEntryVersion =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingStyleBookEntryVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		StyleBookEntryVersion newStyleBookEntryVersion1 =
			addStyleBookEntryVersion();
		StyleBookEntryVersion newStyleBookEntryVersion2 =
			addStyleBookEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntryVersion1.getPrimaryKey());
		primaryKeys.add(newStyleBookEntryVersion2.getPrimaryKey());

		Map<Serializable, StyleBookEntryVersion> styleBookEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, styleBookEntryVersions.size());
		Assert.assertEquals(
			newStyleBookEntryVersion1,
			styleBookEntryVersions.get(
				newStyleBookEntryVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newStyleBookEntryVersion2,
			styleBookEntryVersions.get(
				newStyleBookEntryVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, StyleBookEntryVersion> styleBookEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntryVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, StyleBookEntryVersion> styleBookEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookEntryVersions.size());
		Assert.assertEquals(
			newStyleBookEntryVersion,
			styleBookEntryVersions.get(
				newStyleBookEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, StyleBookEntryVersion> styleBookEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(styleBookEntryVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newStyleBookEntryVersion.getPrimaryKey());

		Map<Serializable, StyleBookEntryVersion> styleBookEntryVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, styleBookEntryVersions.size());
		Assert.assertEquals(
			newStyleBookEntryVersion,
			styleBookEntryVersions.get(
				newStyleBookEntryVersion.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookEntryVersionId",
				newStyleBookEntryVersion.getStyleBookEntryVersionId()));

		List<StyleBookEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		StyleBookEntryVersion existingStyleBookEntryVersion = result.get(0);

		Assert.assertEquals(
			existingStyleBookEntryVersion, newStyleBookEntryVersion);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookEntryVersionId", RandomTestUtil.nextLong()));

		List<StyleBookEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("styleBookEntryVersionId"));

		Object newStyleBookEntryVersionId =
			newStyleBookEntryVersion.getStyleBookEntryVersionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"styleBookEntryVersionId",
				new Object[] {newStyleBookEntryVersionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStyleBookEntryVersionId = result.get(0);

		Assert.assertEquals(
			existingStyleBookEntryVersionId, newStyleBookEntryVersionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("styleBookEntryVersionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"styleBookEntryVersionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newStyleBookEntryVersion.getPrimaryKey()));
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

		StyleBookEntryVersion newStyleBookEntryVersion =
			addStyleBookEntryVersion();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			StyleBookEntryVersion.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"styleBookEntryVersionId",
				newStyleBookEntryVersion.getStyleBookEntryVersionId()));

		List<StyleBookEntryVersion> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		StyleBookEntryVersion styleBookEntryVersion) {

		Assert.assertEquals(
			Long.valueOf(styleBookEntryVersion.getStyleBookEntryId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "styleBookEntryId"));
		Assert.assertEquals(
			Integer.valueOf(styleBookEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			styleBookEntryVersion.getUuid(),
			ReflectionTestUtil.invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(styleBookEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Integer.valueOf(styleBookEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));

		Assert.assertEquals(
			Long.valueOf(styleBookEntryVersion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			styleBookEntryVersion.getStyleBookEntryKey(),
			ReflectionTestUtil.invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "styleBookEntryKey"));
		Assert.assertEquals(
			Integer.valueOf(styleBookEntryVersion.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				styleBookEntryVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected StyleBookEntryVersion addStyleBookEntryVersion()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		StyleBookEntryVersion styleBookEntryVersion = _persistence.create(pk);

		styleBookEntryVersion.setCtCollectionId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setVersion(RandomTestUtil.nextInt());

		styleBookEntryVersion.setUuid(RandomTestUtil.randomString());

		styleBookEntryVersion.setExternalReferenceCode(
			RandomTestUtil.randomString());

		styleBookEntryVersion.setStyleBookEntryId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setGroupId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setCompanyId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setUserId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setUserName(RandomTestUtil.randomString());

		styleBookEntryVersion.setCreateDate(RandomTestUtil.nextDate());

		styleBookEntryVersion.setModifiedDate(RandomTestUtil.nextDate());

		styleBookEntryVersion.setDefaultStyleBookEntry(
			RandomTestUtil.randomBoolean());

		styleBookEntryVersion.setFrontendTokensValues(
			RandomTestUtil.randomString());

		styleBookEntryVersion.setName(RandomTestUtil.randomString());

		styleBookEntryVersion.setPreviewFileEntryId(RandomTestUtil.nextLong());

		styleBookEntryVersion.setStyleBookEntryKey(
			RandomTestUtil.randomString());

		styleBookEntryVersion.setThemeId(RandomTestUtil.randomString());

		_styleBookEntryVersions.add(_persistence.update(styleBookEntryVersion));

		return styleBookEntryVersion;
	}

	private List<StyleBookEntryVersion> _styleBookEntryVersions =
		new ArrayList<StyleBookEntryVersion>();
	private StyleBookEntryVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:467862989