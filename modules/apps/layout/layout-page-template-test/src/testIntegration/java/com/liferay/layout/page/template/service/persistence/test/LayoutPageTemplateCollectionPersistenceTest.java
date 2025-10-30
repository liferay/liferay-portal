/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.exception.DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateCollectionException;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateCollectionUtil;
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
public class LayoutPageTemplateCollectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.layout.page.template.service"));

	@Before
	public void setUp() {
		_persistence = LayoutPageTemplateCollectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPageTemplateCollection> iterator =
			_layoutPageTemplateCollections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_persistence.create(pk);

		Assert.assertNotNull(layoutPageTemplateCollection);

		Assert.assertEquals(layoutPageTemplateCollection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		_persistence.remove(newLayoutPageTemplateCollection);

		LayoutPageTemplateCollection existingLayoutPageTemplateCollection =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateCollection.getPrimaryKey());

		Assert.assertNull(existingLayoutPageTemplateCollection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPageTemplateCollection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			_persistence.create(pk);

		newLayoutPageTemplateCollection.setMvccVersion(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setCtCollectionId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setUuid(RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setGroupId(RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setCompanyId(RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setUserId(RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setUserName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setCreateDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateCollection.setModifiedDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateCollection.setParentLayoutPageTemplateCollectionId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateCollection.setLayoutPageTemplateCollectionKey(
			RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setName(RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setDescription(
			RandomTestUtil.randomString());

		newLayoutPageTemplateCollection.setType(RandomTestUtil.nextInt());

		newLayoutPageTemplateCollection.setLastPublishDate(
			RandomTestUtil.nextDate());

		_layoutPageTemplateCollections.add(
			_persistence.update(newLayoutPageTemplateCollection));

		LayoutPageTemplateCollection existingLayoutPageTemplateCollection =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateCollection.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getMvccVersion(),
			newLayoutPageTemplateCollection.getMvccVersion());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getCtCollectionId(),
			newLayoutPageTemplateCollection.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getUuid(),
			newLayoutPageTemplateCollection.getUuid());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getExternalReferenceCode(),
			newLayoutPageTemplateCollection.getExternalReferenceCode());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId(),
			newLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getGroupId(),
			newLayoutPageTemplateCollection.getGroupId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getCompanyId(),
			newLayoutPageTemplateCollection.getCompanyId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getUserId(),
			newLayoutPageTemplateCollection.getUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getUserName(),
			newLayoutPageTemplateCollection.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateCollection.getCreateDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateCollection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateCollection.getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateCollection.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.
				getParentLayoutPageTemplateCollectionId(),
			newLayoutPageTemplateCollection.
				getParentLayoutPageTemplateCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionKey(),
			newLayoutPageTemplateCollection.
				getLayoutPageTemplateCollectionKey());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getName(),
			newLayoutPageTemplateCollection.getName());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getDescription(),
			newLayoutPageTemplateCollection.getDescription());
		Assert.assertEquals(
			existingLayoutPageTemplateCollection.getType(),
			newLayoutPageTemplateCollection.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateCollection.getLastPublishDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateCollection.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateLayoutPageTemplateCollectionExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		newLayoutPageTemplateCollection.setGroupId(
			layoutPageTemplateCollection.getGroupId());

		newLayoutPageTemplateCollection = _persistence.update(
			newLayoutPageTemplateCollection);

		Session session = _persistence.getCurrentSession();

		session.evict(newLayoutPageTemplateCollection);

		newLayoutPageTemplateCollection.setExternalReferenceCode(
			layoutPageTemplateCollection.getExternalReferenceCode());

		_persistence.update(newLayoutPageTemplateCollection);
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
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_T(0L, 0);
	}

	@Test
	public void testCountByG_P_T() throws Exception {
		_persistence.countByG_P_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_T(0L, 0L, 0);
	}

	@Test
	public void testCountByG_LPTCK_T() throws Exception {
		_persistence.countByG_LPTCK_T(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_LPTCK_T(0L, "null", 0);

		_persistence.countByG_LPTCK_T(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_N_T() throws Exception {
		_persistence.countByG_N_T(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_N_T(0L, "null", 0);

		_persistence.countByG_N_T(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_LikeN_T() throws Exception {
		_persistence.countByG_LikeN_T(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByG_LikeN_T(0L, "null", 0);

		_persistence.countByG_LikeN_T(0L, (String)null, 0);
	}

	@Test
	public void testCountByG_P_N_T() throws Exception {
		_persistence.countByG_P_N_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_P_N_T(0L, 0L, "null", 0);

		_persistence.countByG_P_N_T(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		LayoutPageTemplateCollection existingLayoutPageTemplateCollection =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateCollection.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateCollection,
			newLayoutPageTemplateCollection);
	}

	@Test(expected = NoSuchPageTemplateCollectionException.class)
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

	protected OrderByComparator<LayoutPageTemplateCollection>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LayoutPageTemplateCollection", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true, "externalReferenceCode", true,
			"layoutPageTemplateCollectionId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "parentLayoutPageTemplateCollectionId",
			true, "layoutPageTemplateCollectionKey", true, "name", true,
			"description", true, "type", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		LayoutPageTemplateCollection existingLayoutPageTemplateCollection =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateCollection.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateCollection,
			newLayoutPageTemplateCollection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateCollection missingLayoutPageTemplateCollection =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutPageTemplateCollection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateCollection newLayoutPageTemplateCollection1 =
			addLayoutPageTemplateCollection();
		LayoutPageTemplateCollection newLayoutPageTemplateCollection2 =
			addLayoutPageTemplateCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateCollection1.getPrimaryKey());
		primaryKeys.add(newLayoutPageTemplateCollection2.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateCollection>
			layoutPageTemplateCollections = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, layoutPageTemplateCollections.size());
		Assert.assertEquals(
			newLayoutPageTemplateCollection1,
			layoutPageTemplateCollections.get(
				newLayoutPageTemplateCollection1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPageTemplateCollection2,
			layoutPageTemplateCollections.get(
				newLayoutPageTemplateCollection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutPageTemplateCollection>
			layoutPageTemplateCollections = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateCollection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutPageTemplateCollection>
			layoutPageTemplateCollections = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateCollections.size());
		Assert.assertEquals(
			newLayoutPageTemplateCollection,
			layoutPageTemplateCollections.get(
				newLayoutPageTemplateCollection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutPageTemplateCollection>
			layoutPageTemplateCollections = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateCollection.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateCollection>
			layoutPageTemplateCollections = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateCollections.size());
		Assert.assertEquals(
			newLayoutPageTemplateCollection,
			layoutPageTemplateCollections.get(
				newLayoutPageTemplateCollection.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutPageTemplateCollection newLayoutPageTemplateCollection =
			addLayoutPageTemplateCollection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateCollection.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LayoutPageTemplateCollection layoutPageTemplateCollection) {

		Assert.assertEquals(
			layoutPageTemplateCollection.getUuid(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			layoutPageTemplateCollection.getLayoutPageTemplateCollectionKey(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "lptCollectionKey"));
		Assert.assertEquals(
			Integer.valueOf(layoutPageTemplateCollection.getType()),
			ReflectionTestUtil.<Integer>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));

		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateCollection.
					getParentLayoutPageTemplateCollectionId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentLPTCollectionId"));
		Assert.assertEquals(
			layoutPageTemplateCollection.getName(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Integer.valueOf(layoutPageTemplateCollection.getType()),
			ReflectionTestUtil.<Integer>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));

		Assert.assertEquals(
			layoutPageTemplateCollection.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected LayoutPageTemplateCollection addLayoutPageTemplateCollection()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_persistence.create(pk);

		layoutPageTemplateCollection.setMvccVersion(RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setCtCollectionId(
			RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setUuid(RandomTestUtil.randomString());

		layoutPageTemplateCollection.setExternalReferenceCode(
			RandomTestUtil.randomString());

		layoutPageTemplateCollection.setGroupId(RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setCompanyId(RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setUserId(RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setUserName(RandomTestUtil.randomString());

		layoutPageTemplateCollection.setCreateDate(RandomTestUtil.nextDate());

		layoutPageTemplateCollection.setModifiedDate(RandomTestUtil.nextDate());

		layoutPageTemplateCollection.setParentLayoutPageTemplateCollectionId(
			RandomTestUtil.nextLong());

		layoutPageTemplateCollection.setLayoutPageTemplateCollectionKey(
			RandomTestUtil.randomString());

		layoutPageTemplateCollection.setName(RandomTestUtil.randomString());

		layoutPageTemplateCollection.setDescription(
			RandomTestUtil.randomString());

		layoutPageTemplateCollection.setType(RandomTestUtil.nextInt());

		layoutPageTemplateCollection.setLastPublishDate(
			RandomTestUtil.nextDate());

		_layoutPageTemplateCollections.add(
			_persistence.update(layoutPageTemplateCollection));

		return layoutPageTemplateCollection;
	}

	private List<LayoutPageTemplateCollection> _layoutPageTemplateCollections =
		new ArrayList<LayoutPageTemplateCollection>();
	private LayoutPageTemplateCollectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}