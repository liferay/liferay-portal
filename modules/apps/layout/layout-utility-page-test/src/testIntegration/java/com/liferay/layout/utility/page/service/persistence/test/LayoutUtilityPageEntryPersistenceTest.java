/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.utility.page.exception.DuplicateLayoutUtilityPageEntryExternalReferenceCodeException;
import com.liferay.layout.utility.page.exception.NoSuchLayoutUtilityPageEntryException;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryPersistence;
import com.liferay.layout.utility.page.service.persistence.LayoutUtilityPageEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class LayoutUtilityPageEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.layout.utility.page.service"));

	@Before
	public void setUp() {
		_persistence = LayoutUtilityPageEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutUtilityPageEntry> iterator =
			_layoutUtilityPageEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutUtilityPageEntry layoutUtilityPageEntry = _persistence.create(pk);

		Assert.assertNotNull(layoutUtilityPageEntry);

		Assert.assertEquals(layoutUtilityPageEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		_persistence.remove(newLayoutUtilityPageEntry);

		LayoutUtilityPageEntry existingLayoutUtilityPageEntry =
			_persistence.fetchByPrimaryKey(
				newLayoutUtilityPageEntry.getPrimaryKey());

		Assert.assertNull(existingLayoutUtilityPageEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutUtilityPageEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutUtilityPageEntry newLayoutUtilityPageEntry = _persistence.create(
			pk);

		newLayoutUtilityPageEntry.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setUuid(RandomTestUtil.randomString());

		newLayoutUtilityPageEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newLayoutUtilityPageEntry.setGroupId(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setCompanyId(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setUserId(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setUserName(RandomTestUtil.randomString());

		newLayoutUtilityPageEntry.setCreateDate(RandomTestUtil.nextDate());

		newLayoutUtilityPageEntry.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutUtilityPageEntry.setPlid(RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setPreviewFileEntryId(
			RandomTestUtil.nextLong());

		newLayoutUtilityPageEntry.setDefaultLayoutUtilityPageEntry(
			RandomTestUtil.randomBoolean());

		newLayoutUtilityPageEntry.setName(RandomTestUtil.randomString());

		newLayoutUtilityPageEntry.setType(RandomTestUtil.randomString());

		newLayoutUtilityPageEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutUtilityPageEntries.add(
			_persistence.update(newLayoutUtilityPageEntry));

		LayoutUtilityPageEntry existingLayoutUtilityPageEntry =
			_persistence.findByPrimaryKey(
				newLayoutUtilityPageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getMvccVersion(),
			newLayoutUtilityPageEntry.getMvccVersion());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getCtCollectionId(),
			newLayoutUtilityPageEntry.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getUuid(),
			newLayoutUtilityPageEntry.getUuid());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getExternalReferenceCode(),
			newLayoutUtilityPageEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
			newLayoutUtilityPageEntry.getLayoutUtilityPageEntryId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getGroupId(),
			newLayoutUtilityPageEntry.getGroupId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getCompanyId(),
			newLayoutUtilityPageEntry.getCompanyId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getUserId(),
			newLayoutUtilityPageEntry.getUserId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getUserName(),
			newLayoutUtilityPageEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutUtilityPageEntry.getCreateDate()),
			Time.getShortTimestamp(newLayoutUtilityPageEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutUtilityPageEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutUtilityPageEntry.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getPlid(),
			newLayoutUtilityPageEntry.getPlid());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getPreviewFileEntryId(),
			newLayoutUtilityPageEntry.getPreviewFileEntryId());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry(),
			newLayoutUtilityPageEntry.isDefaultLayoutUtilityPageEntry());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getName(),
			newLayoutUtilityPageEntry.getName());
		Assert.assertEquals(
			existingLayoutUtilityPageEntry.getType(),
			newLayoutUtilityPageEntry.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutUtilityPageEntry.getLastPublishDate()),
			Time.getShortTimestamp(
				newLayoutUtilityPageEntry.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateLayoutUtilityPageEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		LayoutUtilityPageEntry layoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		newLayoutUtilityPageEntry.setGroupId(
			layoutUtilityPageEntry.getGroupId());

		newLayoutUtilityPageEntry = _persistence.update(
			newLayoutUtilityPageEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newLayoutUtilityPageEntry);

		newLayoutUtilityPageEntry.setExternalReferenceCode(
			layoutUtilityPageEntry.getExternalReferenceCode());

		_persistence.update(newLayoutUtilityPageEntry);
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
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_TArrayable() throws Exception {
		_persistence.countByG_T(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
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
	public void testCountByG_N_T() throws Exception {
		_persistence.countByG_N_T(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_N_T(0L, "null", "null");

		_persistence.countByG_N_T(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_LikeN_T() throws Exception {
		_persistence.countByG_LikeN_T(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_LikeN_T(0L, "null", "null");

		_persistence.countByG_LikeN_T(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_LikeN_TArrayable() throws Exception {
		_persistence.countByG_LikeN_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		LayoutUtilityPageEntry existingLayoutUtilityPageEntry =
			_persistence.findByPrimaryKey(
				newLayoutUtilityPageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutUtilityPageEntry, newLayoutUtilityPageEntry);
	}

	@Test(expected = NoSuchLayoutUtilityPageEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutUtilityPageEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutUtilityPageEntry", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"LayoutUtilityPageEntryId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "plid", true, "previewFileEntryId", true,
			"defaultLayoutUtilityPageEntry", true, "name", true, "type", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		LayoutUtilityPageEntry existingLayoutUtilityPageEntry =
			_persistence.fetchByPrimaryKey(
				newLayoutUtilityPageEntry.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutUtilityPageEntry, newLayoutUtilityPageEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutUtilityPageEntry missingLayoutUtilityPageEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutUtilityPageEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutUtilityPageEntry newLayoutUtilityPageEntry1 =
			addLayoutUtilityPageEntry();
		LayoutUtilityPageEntry newLayoutUtilityPageEntry2 =
			addLayoutUtilityPageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutUtilityPageEntry1.getPrimaryKey());
		primaryKeys.add(newLayoutUtilityPageEntry2.getPrimaryKey());

		Map<Serializable, LayoutUtilityPageEntry> layoutUtilityPageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutUtilityPageEntries.size());
		Assert.assertEquals(
			newLayoutUtilityPageEntry1,
			layoutUtilityPageEntries.get(
				newLayoutUtilityPageEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutUtilityPageEntry2,
			layoutUtilityPageEntries.get(
				newLayoutUtilityPageEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutUtilityPageEntry> layoutUtilityPageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutUtilityPageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutUtilityPageEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutUtilityPageEntry> layoutUtilityPageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutUtilityPageEntries.size());
		Assert.assertEquals(
			newLayoutUtilityPageEntry,
			layoutUtilityPageEntries.get(
				newLayoutUtilityPageEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutUtilityPageEntry> layoutUtilityPageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutUtilityPageEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutUtilityPageEntry.getPrimaryKey());

		Map<Serializable, LayoutUtilityPageEntry> layoutUtilityPageEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutUtilityPageEntries.size());
		Assert.assertEquals(
			newLayoutUtilityPageEntry,
			layoutUtilityPageEntries.get(
				newLayoutUtilityPageEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutUtilityPageEntry newLayoutUtilityPageEntry =
			addLayoutUtilityPageEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutUtilityPageEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LayoutUtilityPageEntry layoutUtilityPageEntry) {

		Assert.assertEquals(
			layoutUtilityPageEntry.getUuid(),
			ReflectionTestUtil.invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutUtilityPageEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(layoutUtilityPageEntry.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));

		Assert.assertEquals(
			Long.valueOf(layoutUtilityPageEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			layoutUtilityPageEntry.getName(),
			ReflectionTestUtil.invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			layoutUtilityPageEntry.getType(),
			ReflectionTestUtil.invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));

		Assert.assertEquals(
			layoutUtilityPageEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(layoutUtilityPageEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutUtilityPageEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected LayoutUtilityPageEntry addLayoutUtilityPageEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutUtilityPageEntry layoutUtilityPageEntry = _persistence.create(pk);

		layoutUtilityPageEntry.setMvccVersion(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setCtCollectionId(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setUuid(RandomTestUtil.randomString());

		layoutUtilityPageEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		layoutUtilityPageEntry.setGroupId(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setCompanyId(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setUserId(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setUserName(RandomTestUtil.randomString());

		layoutUtilityPageEntry.setCreateDate(RandomTestUtil.nextDate());

		layoutUtilityPageEntry.setModifiedDate(RandomTestUtil.nextDate());

		layoutUtilityPageEntry.setPlid(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setPreviewFileEntryId(RandomTestUtil.nextLong());

		layoutUtilityPageEntry.setDefaultLayoutUtilityPageEntry(
			RandomTestUtil.randomBoolean());

		layoutUtilityPageEntry.setName(RandomTestUtil.randomString());

		layoutUtilityPageEntry.setType(RandomTestUtil.randomString());

		layoutUtilityPageEntry.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutUtilityPageEntries.add(
			_persistence.update(layoutUtilityPageEntry));

		return layoutUtilityPageEntry;
	}

	private List<LayoutUtilityPageEntry> _layoutUtilityPageEntries =
		new ArrayList<LayoutUtilityPageEntry>();
	private LayoutUtilityPageEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}