/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.documentlibrary.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.DuplicateDLFileEntryTypeExternalReferenceCodeException;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryTypeException;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypePersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryTypeUtil;
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
public class DLFileEntryTypePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = DLFileEntryTypeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLFileEntryType> iterator = _dlFileEntryTypes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryType dlFileEntryType = _persistence.create(pk);

		Assert.assertNotNull(dlFileEntryType);

		Assert.assertEquals(dlFileEntryType.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		_persistence.remove(newDLFileEntryType);

		DLFileEntryType existingDLFileEntryType =
			_persistence.fetchByPrimaryKey(newDLFileEntryType.getPrimaryKey());

		Assert.assertNull(existingDLFileEntryType);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLFileEntryType();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryType newDLFileEntryType = _persistence.create(pk);

		newDLFileEntryType.setMvccVersion(RandomTestUtil.nextLong());

		newDLFileEntryType.setCtCollectionId(RandomTestUtil.nextLong());

		newDLFileEntryType.setUuid(RandomTestUtil.randomString());

		newDLFileEntryType.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newDLFileEntryType.setGroupId(RandomTestUtil.nextLong());

		newDLFileEntryType.setCompanyId(RandomTestUtil.nextLong());

		newDLFileEntryType.setUserId(RandomTestUtil.nextLong());

		newDLFileEntryType.setUserName(RandomTestUtil.randomString());

		newDLFileEntryType.setCreateDate(RandomTestUtil.nextDate());

		newDLFileEntryType.setModifiedDate(RandomTestUtil.nextDate());

		newDLFileEntryType.setDataDefinitionId(RandomTestUtil.nextLong());

		newDLFileEntryType.setFileEntryTypeKey(RandomTestUtil.randomString());

		newDLFileEntryType.setName(RandomTestUtil.randomString());

		newDLFileEntryType.setDescription(RandomTestUtil.randomString());

		newDLFileEntryType.setScope(RandomTestUtil.nextInt());

		newDLFileEntryType.setLastPublishDate(RandomTestUtil.nextDate());

		_dlFileEntryTypes.add(_persistence.update(newDLFileEntryType));

		DLFileEntryType existingDLFileEntryType = _persistence.findByPrimaryKey(
			newDLFileEntryType.getPrimaryKey());

		Assert.assertEquals(
			existingDLFileEntryType.getMvccVersion(),
			newDLFileEntryType.getMvccVersion());
		Assert.assertEquals(
			existingDLFileEntryType.getCtCollectionId(),
			newDLFileEntryType.getCtCollectionId());
		Assert.assertEquals(
			existingDLFileEntryType.getUuid(), newDLFileEntryType.getUuid());
		Assert.assertEquals(
			existingDLFileEntryType.getExternalReferenceCode(),
			newDLFileEntryType.getExternalReferenceCode());
		Assert.assertEquals(
			existingDLFileEntryType.getFileEntryTypeId(),
			newDLFileEntryType.getFileEntryTypeId());
		Assert.assertEquals(
			existingDLFileEntryType.getGroupId(),
			newDLFileEntryType.getGroupId());
		Assert.assertEquals(
			existingDLFileEntryType.getCompanyId(),
			newDLFileEntryType.getCompanyId());
		Assert.assertEquals(
			existingDLFileEntryType.getUserId(),
			newDLFileEntryType.getUserId());
		Assert.assertEquals(
			existingDLFileEntryType.getUserName(),
			newDLFileEntryType.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntryType.getCreateDate()),
			Time.getShortTimestamp(newDLFileEntryType.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDLFileEntryType.getModifiedDate()),
			Time.getShortTimestamp(newDLFileEntryType.getModifiedDate()));
		Assert.assertEquals(
			existingDLFileEntryType.getDataDefinitionId(),
			newDLFileEntryType.getDataDefinitionId());
		Assert.assertEquals(
			existingDLFileEntryType.getFileEntryTypeKey(),
			newDLFileEntryType.getFileEntryTypeKey());
		Assert.assertEquals(
			existingDLFileEntryType.getName(), newDLFileEntryType.getName());
		Assert.assertEquals(
			existingDLFileEntryType.getDescription(),
			newDLFileEntryType.getDescription());
		Assert.assertEquals(
			existingDLFileEntryType.getScope(), newDLFileEntryType.getScope());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDLFileEntryType.getLastPublishDate()),
			Time.getShortTimestamp(newDLFileEntryType.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateDLFileEntryTypeExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		DLFileEntryType dlFileEntryType = addDLFileEntryType();

		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		newDLFileEntryType.setGroupId(dlFileEntryType.getGroupId());

		newDLFileEntryType = _persistence.update(newDLFileEntryType);

		Session session = _persistence.getCurrentSession();

		session.evict(newDLFileEntryType);

		newDLFileEntryType.setExternalReferenceCode(
			dlFileEntryType.getExternalReferenceCode());

		_persistence.update(newDLFileEntryType);
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
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_DDI() throws Exception {
		_persistence.countByG_DDI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_DDI(0L, 0L);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(RandomTestUtil.nextLong(), "");

		_persistence.countByG_F(0L, "null");

		_persistence.countByG_F(0L, (String)null);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		DLFileEntryType existingDLFileEntryType = _persistence.findByPrimaryKey(
			newDLFileEntryType.getPrimaryKey());

		Assert.assertEquals(existingDLFileEntryType, newDLFileEntryType);
	}

	@Test(expected = NoSuchFileEntryTypeException.class)
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

	protected OrderByComparator<DLFileEntryType> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLFileEntryType", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "fileEntryTypeId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"dataDefinitionId", true, "fileEntryTypeKey", true, "name", true,
			"description", true, "scope", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		DLFileEntryType existingDLFileEntryType =
			_persistence.fetchByPrimaryKey(newDLFileEntryType.getPrimaryKey());

		Assert.assertEquals(existingDLFileEntryType, newDLFileEntryType);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryType missingDLFileEntryType = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDLFileEntryType);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLFileEntryType newDLFileEntryType1 = addDLFileEntryType();
		DLFileEntryType newDLFileEntryType2 = addDLFileEntryType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryType1.getPrimaryKey());
		primaryKeys.add(newDLFileEntryType2.getPrimaryKey());

		Map<Serializable, DLFileEntryType> dlFileEntryTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlFileEntryTypes.size());
		Assert.assertEquals(
			newDLFileEntryType1,
			dlFileEntryTypes.get(newDLFileEntryType1.getPrimaryKey()));
		Assert.assertEquals(
			newDLFileEntryType2,
			dlFileEntryTypes.get(newDLFileEntryType2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLFileEntryType> dlFileEntryTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntryTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryType.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLFileEntryType> dlFileEntryTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntryTypes.size());
		Assert.assertEquals(
			newDLFileEntryType,
			dlFileEntryTypes.get(newDLFileEntryType.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLFileEntryType> dlFileEntryTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlFileEntryTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLFileEntryType.getPrimaryKey());

		Map<Serializable, DLFileEntryType> dlFileEntryTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlFileEntryTypes.size());
		Assert.assertEquals(
			newDLFileEntryType,
			dlFileEntryTypes.get(newDLFileEntryType.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLFileEntryType newDLFileEntryType = addDLFileEntryType();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLFileEntryType.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLFileEntryType dlFileEntryType) {
		Assert.assertEquals(
			dlFileEntryType.getUuid(),
			ReflectionTestUtil.invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntryType.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(dlFileEntryType.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntryType.getDataDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "dataDefinitionId"));

		Assert.assertEquals(
			Long.valueOf(dlFileEntryType.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			dlFileEntryType.getFileEntryTypeKey(),
			ReflectionTestUtil.invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "fileEntryTypeKey"));

		Assert.assertEquals(
			dlFileEntryType.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(dlFileEntryType.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				dlFileEntryType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DLFileEntryType addDLFileEntryType() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLFileEntryType dlFileEntryType = _persistence.create(pk);

		dlFileEntryType.setMvccVersion(RandomTestUtil.nextLong());

		dlFileEntryType.setCtCollectionId(RandomTestUtil.nextLong());

		dlFileEntryType.setUuid(RandomTestUtil.randomString());

		dlFileEntryType.setExternalReferenceCode(RandomTestUtil.randomString());

		dlFileEntryType.setGroupId(RandomTestUtil.nextLong());

		dlFileEntryType.setCompanyId(RandomTestUtil.nextLong());

		dlFileEntryType.setUserId(RandomTestUtil.nextLong());

		dlFileEntryType.setUserName(RandomTestUtil.randomString());

		dlFileEntryType.setCreateDate(RandomTestUtil.nextDate());

		dlFileEntryType.setModifiedDate(RandomTestUtil.nextDate());

		dlFileEntryType.setDataDefinitionId(RandomTestUtil.nextLong());

		dlFileEntryType.setFileEntryTypeKey(RandomTestUtil.randomString());

		dlFileEntryType.setName(RandomTestUtil.randomString());

		dlFileEntryType.setDescription(RandomTestUtil.randomString());

		dlFileEntryType.setScope(RandomTestUtil.nextInt());

		dlFileEntryType.setLastPublishDate(RandomTestUtil.nextDate());

		_dlFileEntryTypes.add(_persistence.update(dlFileEntryType));

		return dlFileEntryType;
	}

	private List<DLFileEntryType> _dlFileEntryTypes =
		new ArrayList<DLFileEntryType>();
	private DLFileEntryTypePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}