/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.DuplicateMBCategoryExternalReferenceCodeException;
import com.liferay.message.boards.exception.NoSuchCategoryException;
import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.service.persistence.MBCategoryPersistence;
import com.liferay.message.boards.service.persistence.MBCategoryUtil;
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
public class MBCategoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBCategoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBCategory> iterator = _mbCategories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBCategory mbCategory = _persistence.create(pk);

		Assert.assertNotNull(mbCategory);

		Assert.assertEquals(mbCategory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBCategory newMBCategory = addMBCategory();

		_persistence.remove(newMBCategory);

		MBCategory existingMBCategory = _persistence.fetchByPrimaryKey(
			newMBCategory.getPrimaryKey());

		Assert.assertNull(existingMBCategory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBCategory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBCategory newMBCategory = _persistence.create(pk);

		newMBCategory.setMvccVersion(RandomTestUtil.nextLong());

		newMBCategory.setCtCollectionId(RandomTestUtil.nextLong());

		newMBCategory.setUuid(RandomTestUtil.randomString());

		newMBCategory.setExternalReferenceCode(RandomTestUtil.randomString());

		newMBCategory.setGroupId(RandomTestUtil.nextLong());

		newMBCategory.setCompanyId(RandomTestUtil.nextLong());

		newMBCategory.setUserId(RandomTestUtil.nextLong());

		newMBCategory.setUserName(RandomTestUtil.randomString());

		newMBCategory.setCreateDate(RandomTestUtil.nextDate());

		newMBCategory.setModifiedDate(RandomTestUtil.nextDate());

		newMBCategory.setParentCategoryId(RandomTestUtil.nextLong());

		newMBCategory.setName(RandomTestUtil.randomString());

		newMBCategory.setDescription(RandomTestUtil.randomString());

		newMBCategory.setDisplayStyle(RandomTestUtil.randomString());

		newMBCategory.setFriendlyURL(RandomTestUtil.randomString());

		newMBCategory.setLastPublishDate(RandomTestUtil.nextDate());

		newMBCategory.setStatus(RandomTestUtil.nextInt());

		newMBCategory.setStatusByUserId(RandomTestUtil.nextLong());

		newMBCategory.setStatusByUserName(RandomTestUtil.randomString());

		newMBCategory.setStatusDate(RandomTestUtil.nextDate());

		_mbCategories.add(_persistence.update(newMBCategory));

		MBCategory existingMBCategory = _persistence.findByPrimaryKey(
			newMBCategory.getPrimaryKey());

		Assert.assertEquals(
			existingMBCategory.getMvccVersion(),
			newMBCategory.getMvccVersion());
		Assert.assertEquals(
			existingMBCategory.getCtCollectionId(),
			newMBCategory.getCtCollectionId());
		Assert.assertEquals(
			existingMBCategory.getUuid(), newMBCategory.getUuid());
		Assert.assertEquals(
			existingMBCategory.getExternalReferenceCode(),
			newMBCategory.getExternalReferenceCode());
		Assert.assertEquals(
			existingMBCategory.getCategoryId(), newMBCategory.getCategoryId());
		Assert.assertEquals(
			existingMBCategory.getGroupId(), newMBCategory.getGroupId());
		Assert.assertEquals(
			existingMBCategory.getCompanyId(), newMBCategory.getCompanyId());
		Assert.assertEquals(
			existingMBCategory.getUserId(), newMBCategory.getUserId());
		Assert.assertEquals(
			existingMBCategory.getUserName(), newMBCategory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBCategory.getCreateDate()),
			Time.getShortTimestamp(newMBCategory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBCategory.getModifiedDate()),
			Time.getShortTimestamp(newMBCategory.getModifiedDate()));
		Assert.assertEquals(
			existingMBCategory.getParentCategoryId(),
			newMBCategory.getParentCategoryId());
		Assert.assertEquals(
			existingMBCategory.getName(), newMBCategory.getName());
		Assert.assertEquals(
			existingMBCategory.getDescription(),
			newMBCategory.getDescription());
		Assert.assertEquals(
			existingMBCategory.getDisplayStyle(),
			newMBCategory.getDisplayStyle());
		Assert.assertEquals(
			existingMBCategory.getFriendlyURL(),
			newMBCategory.getFriendlyURL());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBCategory.getLastPublishDate()),
			Time.getShortTimestamp(newMBCategory.getLastPublishDate()));
		Assert.assertEquals(
			existingMBCategory.getStatus(), newMBCategory.getStatus());
		Assert.assertEquals(
			existingMBCategory.getStatusByUserId(),
			newMBCategory.getStatusByUserId());
		Assert.assertEquals(
			existingMBCategory.getStatusByUserName(),
			newMBCategory.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBCategory.getStatusDate()),
			Time.getShortTimestamp(newMBCategory.getStatusDate()));
	}

	@Test(expected = DuplicateMBCategoryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		MBCategory mbCategory = addMBCategory();

		MBCategory newMBCategory = addMBCategory();

		newMBCategory.setGroupId(mbCategory.getGroupId());

		newMBCategory = _persistence.update(newMBCategory);

		Session session = _persistence.getCurrentSession();

		session.evict(newMBCategory);

		newMBCategory.setExternalReferenceCode(
			mbCategory.getExternalReferenceCode());

		_persistence.update(newMBCategory);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByG_PArrayable() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(RandomTestUtil.nextLong(), "");

		_persistence.countByG_F(0L, "null");

		_persistence.countByG_F(0L, (String)null);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByNotC_G_P() throws Exception {
		_persistence.countByNotC_G_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByNotC_G_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByNotC_G_PArrayable() throws Exception {
		_persistence.countByNotC_G_P(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_P_S() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_SArrayable() throws Exception {
		_persistence.countByG_P_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_P_NotS() throws Exception {
		_persistence.countByG_P_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_NotSArrayable() throws Exception {
		_persistence.countByG_P_NotS(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByNotC_G_P_S() throws Exception {
		_persistence.countByNotC_G_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByNotC_G_P_S(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByNotC_G_P_SArrayable() throws Exception {
		_persistence.countByNotC_G_P_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBCategory newMBCategory = addMBCategory();

		MBCategory existingMBCategory = _persistence.findByPrimaryKey(
			newMBCategory.getPrimaryKey());

		Assert.assertEquals(existingMBCategory, newMBCategory);
	}

	@Test(expected = NoSuchCategoryException.class)
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

	protected OrderByComparator<MBCategory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBCategory", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "categoryId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "parentCategoryId", true,
			"name", true, "description", true, "displayStyle", true,
			"friendlyURL", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBCategory newMBCategory = addMBCategory();

		MBCategory existingMBCategory = _persistence.fetchByPrimaryKey(
			newMBCategory.getPrimaryKey());

		Assert.assertEquals(existingMBCategory, newMBCategory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBCategory missingMBCategory = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBCategory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBCategory newMBCategory1 = addMBCategory();
		MBCategory newMBCategory2 = addMBCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBCategory1.getPrimaryKey());
		primaryKeys.add(newMBCategory2.getPrimaryKey());

		Map<Serializable, MBCategory> mbCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbCategories.size());
		Assert.assertEquals(
			newMBCategory1, mbCategories.get(newMBCategory1.getPrimaryKey()));
		Assert.assertEquals(
			newMBCategory2, mbCategories.get(newMBCategory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBCategory> mbCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBCategory newMBCategory = addMBCategory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBCategory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBCategory> mbCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbCategories.size());
		Assert.assertEquals(
			newMBCategory, mbCategories.get(newMBCategory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBCategory> mbCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBCategory newMBCategory = addMBCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBCategory.getPrimaryKey());

		Map<Serializable, MBCategory> mbCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbCategories.size());
		Assert.assertEquals(
			newMBCategory, mbCategories.get(newMBCategory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBCategory newMBCategory = addMBCategory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBCategory.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBCategory mbCategory) {
		Assert.assertEquals(
			mbCategory.getUuid(),
			ReflectionTestUtil.invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbCategory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbCategory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			mbCategory.getFriendlyURL(),
			ReflectionTestUtil.invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "friendlyURL"));

		Assert.assertEquals(
			mbCategory.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(mbCategory.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected MBCategory addMBCategory() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBCategory mbCategory = _persistence.create(pk);

		mbCategory.setMvccVersion(RandomTestUtil.nextLong());

		mbCategory.setCtCollectionId(RandomTestUtil.nextLong());

		mbCategory.setUuid(RandomTestUtil.randomString());

		mbCategory.setExternalReferenceCode(RandomTestUtil.randomString());

		mbCategory.setGroupId(RandomTestUtil.nextLong());

		mbCategory.setCompanyId(RandomTestUtil.nextLong());

		mbCategory.setUserId(RandomTestUtil.nextLong());

		mbCategory.setUserName(RandomTestUtil.randomString());

		mbCategory.setCreateDate(RandomTestUtil.nextDate());

		mbCategory.setModifiedDate(RandomTestUtil.nextDate());

		mbCategory.setParentCategoryId(RandomTestUtil.nextLong());

		mbCategory.setName(RandomTestUtil.randomString());

		mbCategory.setDescription(RandomTestUtil.randomString());

		mbCategory.setDisplayStyle(RandomTestUtil.randomString());

		mbCategory.setFriendlyURL(RandomTestUtil.randomString());

		mbCategory.setLastPublishDate(RandomTestUtil.nextDate());

		mbCategory.setStatus(RandomTestUtil.nextInt());

		mbCategory.setStatusByUserId(RandomTestUtil.nextLong());

		mbCategory.setStatusByUserName(RandomTestUtil.randomString());

		mbCategory.setStatusDate(RandomTestUtil.nextDate());

		_mbCategories.add(_persistence.update(mbCategory));

		return mbCategory;
	}

	private List<MBCategory> _mbCategories = new ArrayList<MBCategory>();
	private MBCategoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}