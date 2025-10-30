/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
import com.liferay.saved.content.exception.NoSuchSavedContentEntryException;
import com.liferay.saved.content.model.SavedContentEntry;
import com.liferay.saved.content.service.persistence.SavedContentEntryPersistence;
import com.liferay.saved.content.service.persistence.SavedContentEntryUtil;

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
public class SavedContentEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saved.content.service"));

	@Before
	public void setUp() {
		_persistence = SavedContentEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SavedContentEntry> iterator = _savedContentEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SavedContentEntry savedContentEntry = _persistence.create(pk);

		Assert.assertNotNull(savedContentEntry);

		Assert.assertEquals(savedContentEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		_persistence.remove(newSavedContentEntry);

		SavedContentEntry existingSavedContentEntry =
			_persistence.fetchByPrimaryKey(
				newSavedContentEntry.getPrimaryKey());

		Assert.assertNull(existingSavedContentEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSavedContentEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SavedContentEntry newSavedContentEntry = _persistence.create(pk);

		newSavedContentEntry.setMvccVersion(RandomTestUtil.nextLong());

		newSavedContentEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newSavedContentEntry.setUuid(RandomTestUtil.randomString());

		newSavedContentEntry.setGroupId(RandomTestUtil.nextLong());

		newSavedContentEntry.setCompanyId(RandomTestUtil.nextLong());

		newSavedContentEntry.setUserId(RandomTestUtil.nextLong());

		newSavedContentEntry.setUserName(RandomTestUtil.randomString());

		newSavedContentEntry.setCreateDate(RandomTestUtil.nextDate());

		newSavedContentEntry.setModifiedDate(RandomTestUtil.nextDate());

		newSavedContentEntry.setClassNameId(RandomTestUtil.nextLong());

		newSavedContentEntry.setClassPK(RandomTestUtil.nextLong());

		_savedContentEntries.add(_persistence.update(newSavedContentEntry));

		SavedContentEntry existingSavedContentEntry =
			_persistence.findByPrimaryKey(newSavedContentEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSavedContentEntry.getMvccVersion(),
			newSavedContentEntry.getMvccVersion());
		Assert.assertEquals(
			existingSavedContentEntry.getCtCollectionId(),
			newSavedContentEntry.getCtCollectionId());
		Assert.assertEquals(
			existingSavedContentEntry.getUuid(),
			newSavedContentEntry.getUuid());
		Assert.assertEquals(
			existingSavedContentEntry.getSavedContentEntryId(),
			newSavedContentEntry.getSavedContentEntryId());
		Assert.assertEquals(
			existingSavedContentEntry.getGroupId(),
			newSavedContentEntry.getGroupId());
		Assert.assertEquals(
			existingSavedContentEntry.getCompanyId(),
			newSavedContentEntry.getCompanyId());
		Assert.assertEquals(
			existingSavedContentEntry.getUserId(),
			newSavedContentEntry.getUserId());
		Assert.assertEquals(
			existingSavedContentEntry.getUserName(),
			newSavedContentEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSavedContentEntry.getCreateDate()),
			Time.getShortTimestamp(newSavedContentEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSavedContentEntry.getModifiedDate()),
			Time.getShortTimestamp(newSavedContentEntry.getModifiedDate()));
		Assert.assertEquals(
			existingSavedContentEntry.getClassNameId(),
			newSavedContentEntry.getClassNameId());
		Assert.assertEquals(
			existingSavedContentEntry.getClassPK(),
			newSavedContentEntry.getClassPK());
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
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByG_CN() throws Exception {
		_persistence.countByG_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_CN(0L, 0L);
	}

	@Test
	public void testCountByU_C() throws Exception {
		_persistence.countByU_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_C(0L, 0L);
	}

	@Test
	public void testCountByG_C_C() throws Exception {
		_persistence.countByG_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByG_U_C_C() throws Exception {
		_persistence.countByG_U_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U_C_C(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByC_U_C_C() throws Exception {
		_persistence.countByC_U_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U_C_C(0L, 0L, 0L, 0L);
	}

	@Test
	public void testCountByC_U_C_CArrayable() throws Exception {
		_persistence.countByC_U_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		SavedContentEntry existingSavedContentEntry =
			_persistence.findByPrimaryKey(newSavedContentEntry.getPrimaryKey());

		Assert.assertEquals(existingSavedContentEntry, newSavedContentEntry);
	}

	@Test(expected = NoSuchSavedContentEntryException.class)
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

	protected OrderByComparator<SavedContentEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SavedContentEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "savedContentEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		SavedContentEntry existingSavedContentEntry =
			_persistence.fetchByPrimaryKey(
				newSavedContentEntry.getPrimaryKey());

		Assert.assertEquals(existingSavedContentEntry, newSavedContentEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SavedContentEntry missingSavedContentEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSavedContentEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SavedContentEntry newSavedContentEntry1 = addSavedContentEntry();
		SavedContentEntry newSavedContentEntry2 = addSavedContentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSavedContentEntry1.getPrimaryKey());
		primaryKeys.add(newSavedContentEntry2.getPrimaryKey());

		Map<Serializable, SavedContentEntry> savedContentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, savedContentEntries.size());
		Assert.assertEquals(
			newSavedContentEntry1,
			savedContentEntries.get(newSavedContentEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newSavedContentEntry2,
			savedContentEntries.get(newSavedContentEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SavedContentEntry> savedContentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(savedContentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSavedContentEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SavedContentEntry> savedContentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, savedContentEntries.size());
		Assert.assertEquals(
			newSavedContentEntry,
			savedContentEntries.get(newSavedContentEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SavedContentEntry> savedContentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(savedContentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSavedContentEntry.getPrimaryKey());

		Map<Serializable, SavedContentEntry> savedContentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, savedContentEntries.size());
		Assert.assertEquals(
			newSavedContentEntry,
			savedContentEntries.get(newSavedContentEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SavedContentEntry newSavedContentEntry = addSavedContentEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSavedContentEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(SavedContentEntry savedContentEntry) {
		Assert.assertEquals(
			savedContentEntry.getUuid(),
			ReflectionTestUtil.invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));

		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(savedContentEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				savedContentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected SavedContentEntry addSavedContentEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SavedContentEntry savedContentEntry = _persistence.create(pk);

		savedContentEntry.setMvccVersion(RandomTestUtil.nextLong());

		savedContentEntry.setCtCollectionId(RandomTestUtil.nextLong());

		savedContentEntry.setUuid(RandomTestUtil.randomString());

		savedContentEntry.setGroupId(RandomTestUtil.nextLong());

		savedContentEntry.setCompanyId(RandomTestUtil.nextLong());

		savedContentEntry.setUserId(RandomTestUtil.nextLong());

		savedContentEntry.setUserName(RandomTestUtil.randomString());

		savedContentEntry.setCreateDate(RandomTestUtil.nextDate());

		savedContentEntry.setModifiedDate(RandomTestUtil.nextDate());

		savedContentEntry.setClassNameId(RandomTestUtil.nextLong());

		savedContentEntry.setClassPK(RandomTestUtil.nextLong());

		_savedContentEntries.add(_persistence.update(savedContentEntry));

		return savedContentEntry;
	}

	private List<SavedContentEntry> _savedContentEntries =
		new ArrayList<SavedContentEntry>();
	private SavedContentEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}