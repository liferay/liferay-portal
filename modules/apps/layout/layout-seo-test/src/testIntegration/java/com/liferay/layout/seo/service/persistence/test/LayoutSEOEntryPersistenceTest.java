/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.seo.exception.NoSuchEntryException;
import com.liferay.layout.seo.model.LayoutSEOEntry;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryPersistence;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryUtil;
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
public class LayoutSEOEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.layout.seo.service"));

	@Before
	public void setUp() {
		_persistence = LayoutSEOEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutSEOEntry> iterator = _layoutSEOEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntry layoutSEOEntry = _persistence.create(pk);

		Assert.assertNotNull(layoutSEOEntry);

		Assert.assertEquals(layoutSEOEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		_persistence.remove(newLayoutSEOEntry);

		LayoutSEOEntry existingLayoutSEOEntry = _persistence.fetchByPrimaryKey(
			newLayoutSEOEntry.getPrimaryKey());

		Assert.assertNull(existingLayoutSEOEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutSEOEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntry newLayoutSEOEntry = _persistence.create(pk);

		newLayoutSEOEntry.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setUuid(RandomTestUtil.randomString());

		newLayoutSEOEntry.setGroupId(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setCompanyId(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setUserId(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setUserName(RandomTestUtil.randomString());

		newLayoutSEOEntry.setCreateDate(RandomTestUtil.nextDate());

		newLayoutSEOEntry.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutSEOEntry.setPrivateLayout(RandomTestUtil.randomBoolean());

		newLayoutSEOEntry.setLayoutId(RandomTestUtil.nextLong());

		newLayoutSEOEntry.setCanonicalURL(RandomTestUtil.randomString());

		newLayoutSEOEntry.setCanonicalURLEnabled(
			RandomTestUtil.randomBoolean());

		newLayoutSEOEntry.setOpenGraphDescription(
			RandomTestUtil.randomString());

		newLayoutSEOEntry.setOpenGraphDescriptionEnabled(
			RandomTestUtil.randomBoolean());

		newLayoutSEOEntry.setOpenGraphImageAlt(RandomTestUtil.randomString());

		newLayoutSEOEntry.setOpenGraphImageFileEntryId(
			RandomTestUtil.nextLong());

		newLayoutSEOEntry.setOpenGraphTitle(RandomTestUtil.randomString());

		newLayoutSEOEntry.setOpenGraphTitleEnabled(
			RandomTestUtil.randomBoolean());

		newLayoutSEOEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutSEOEntries.add(_persistence.update(newLayoutSEOEntry));

		LayoutSEOEntry existingLayoutSEOEntry = _persistence.findByPrimaryKey(
			newLayoutSEOEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSEOEntry.getMvccVersion(),
			newLayoutSEOEntry.getMvccVersion());
		Assert.assertEquals(
			existingLayoutSEOEntry.getCtCollectionId(),
			newLayoutSEOEntry.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getUuid(), newLayoutSEOEntry.getUuid());
		Assert.assertEquals(
			existingLayoutSEOEntry.getLayoutSEOEntryId(),
			newLayoutSEOEntry.getLayoutSEOEntryId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getGroupId(),
			newLayoutSEOEntry.getGroupId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getCompanyId(),
			newLayoutSEOEntry.getCompanyId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getUserId(), newLayoutSEOEntry.getUserId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getUserName(),
			newLayoutSEOEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSEOEntry.getCreateDate()),
			Time.getShortTimestamp(newLayoutSEOEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSEOEntry.getModifiedDate()),
			Time.getShortTimestamp(newLayoutSEOEntry.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutSEOEntry.isPrivateLayout(),
			newLayoutSEOEntry.isPrivateLayout());
		Assert.assertEquals(
			existingLayoutSEOEntry.getLayoutId(),
			newLayoutSEOEntry.getLayoutId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getCanonicalURL(),
			newLayoutSEOEntry.getCanonicalURL());
		Assert.assertEquals(
			existingLayoutSEOEntry.isCanonicalURLEnabled(),
			newLayoutSEOEntry.isCanonicalURLEnabled());
		Assert.assertEquals(
			existingLayoutSEOEntry.getOpenGraphDescription(),
			newLayoutSEOEntry.getOpenGraphDescription());
		Assert.assertEquals(
			existingLayoutSEOEntry.isOpenGraphDescriptionEnabled(),
			newLayoutSEOEntry.isOpenGraphDescriptionEnabled());
		Assert.assertEquals(
			existingLayoutSEOEntry.getOpenGraphImageAlt(),
			newLayoutSEOEntry.getOpenGraphImageAlt());
		Assert.assertEquals(
			existingLayoutSEOEntry.getOpenGraphImageFileEntryId(),
			newLayoutSEOEntry.getOpenGraphImageFileEntryId());
		Assert.assertEquals(
			existingLayoutSEOEntry.getOpenGraphTitle(),
			newLayoutSEOEntry.getOpenGraphTitle());
		Assert.assertEquals(
			existingLayoutSEOEntry.isOpenGraphTitleEnabled(),
			newLayoutSEOEntry.isOpenGraphTitleEnabled());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSEOEntry.getLastPublishDate()),
			Time.getShortTimestamp(newLayoutSEOEntry.getLastPublishDate()));
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
	public void testCountByG_P_L() throws Exception {
		_persistence.countByG_P_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByG_P_L(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		LayoutSEOEntry existingLayoutSEOEntry = _persistence.findByPrimaryKey(
			newLayoutSEOEntry.getPrimaryKey());

		Assert.assertEquals(existingLayoutSEOEntry, newLayoutSEOEntry);
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

	protected OrderByComparator<LayoutSEOEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutSEOEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "layoutSEOEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "privateLayout", true, "layoutId", true,
			"canonicalURL", true, "canonicalURLEnabled", true,
			"openGraphDescription", true, "openGraphDescriptionEnabled", true,
			"openGraphImageAlt", true, "openGraphImageFileEntryId", true,
			"openGraphTitle", true, "openGraphTitleEnabled", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		LayoutSEOEntry existingLayoutSEOEntry = _persistence.fetchByPrimaryKey(
			newLayoutSEOEntry.getPrimaryKey());

		Assert.assertEquals(existingLayoutSEOEntry, newLayoutSEOEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntry missingLayoutSEOEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingLayoutSEOEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutSEOEntry newLayoutSEOEntry1 = addLayoutSEOEntry();
		LayoutSEOEntry newLayoutSEOEntry2 = addLayoutSEOEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntry1.getPrimaryKey());
		primaryKeys.add(newLayoutSEOEntry2.getPrimaryKey());

		Map<Serializable, LayoutSEOEntry> layoutSEOEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutSEOEntries.size());
		Assert.assertEquals(
			newLayoutSEOEntry1,
			layoutSEOEntries.get(newLayoutSEOEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutSEOEntry2,
			layoutSEOEntries.get(newLayoutSEOEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutSEOEntry> layoutSEOEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSEOEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutSEOEntry> layoutSEOEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSEOEntries.size());
		Assert.assertEquals(
			newLayoutSEOEntry,
			layoutSEOEntries.get(newLayoutSEOEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutSEOEntry> layoutSEOEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSEOEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntry.getPrimaryKey());

		Map<Serializable, LayoutSEOEntry> layoutSEOEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSEOEntries.size());
		Assert.assertEquals(
			newLayoutSEOEntry,
			layoutSEOEntries.get(newLayoutSEOEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutSEOEntry newLayoutSEOEntry = addLayoutSEOEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLayoutSEOEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(LayoutSEOEntry layoutSEOEntry) {
		Assert.assertEquals(
			layoutSEOEntry.getUuid(),
			ReflectionTestUtil.invoke(
				layoutSEOEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutSEOEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutSEOEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(layoutSEOEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutSEOEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layoutSEOEntry.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layoutSEOEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "privateLayout"));
		Assert.assertEquals(
			Long.valueOf(layoutSEOEntry.getLayoutId()),
			ReflectionTestUtil.<Long>invoke(
				layoutSEOEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "layoutId"));
	}

	protected LayoutSEOEntry addLayoutSEOEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntry layoutSEOEntry = _persistence.create(pk);

		layoutSEOEntry.setMvccVersion(RandomTestUtil.nextLong());

		layoutSEOEntry.setCtCollectionId(RandomTestUtil.nextLong());

		layoutSEOEntry.setUuid(RandomTestUtil.randomString());

		layoutSEOEntry.setGroupId(RandomTestUtil.nextLong());

		layoutSEOEntry.setCompanyId(RandomTestUtil.nextLong());

		layoutSEOEntry.setUserId(RandomTestUtil.nextLong());

		layoutSEOEntry.setUserName(RandomTestUtil.randomString());

		layoutSEOEntry.setCreateDate(RandomTestUtil.nextDate());

		layoutSEOEntry.setModifiedDate(RandomTestUtil.nextDate());

		layoutSEOEntry.setPrivateLayout(RandomTestUtil.randomBoolean());

		layoutSEOEntry.setLayoutId(RandomTestUtil.nextLong());

		layoutSEOEntry.setCanonicalURL(RandomTestUtil.randomString());

		layoutSEOEntry.setCanonicalURLEnabled(RandomTestUtil.randomBoolean());

		layoutSEOEntry.setOpenGraphDescription(RandomTestUtil.randomString());

		layoutSEOEntry.setOpenGraphDescriptionEnabled(
			RandomTestUtil.randomBoolean());

		layoutSEOEntry.setOpenGraphImageAlt(RandomTestUtil.randomString());

		layoutSEOEntry.setOpenGraphImageFileEntryId(RandomTestUtil.nextLong());

		layoutSEOEntry.setOpenGraphTitle(RandomTestUtil.randomString());

		layoutSEOEntry.setOpenGraphTitleEnabled(RandomTestUtil.randomBoolean());

		layoutSEOEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutSEOEntries.add(_persistence.update(layoutSEOEntry));

		return layoutSEOEntry;
	}

	private List<LayoutSEOEntry> _layoutSEOEntries =
		new ArrayList<LayoutSEOEntry>();
	private LayoutSEOEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}