/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.qualifier.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.qualifier.exception.NoSuchCommerceQualifierEntryException;
import com.liferay.commerce.qualifier.model.CommerceQualifierEntry;
import com.liferay.commerce.qualifier.service.persistence.CommerceQualifierEntryPersistence;
import com.liferay.commerce.qualifier.service.persistence.CommerceQualifierEntryUtil;
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
public class CommerceQualifierEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.qualifier.service"));

	@Before
	public void setUp() {
		_persistence = CommerceQualifierEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceQualifierEntry> iterator =
			_commerceQualifierEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceQualifierEntry commerceQualifierEntry = _persistence.create(pk);

		Assert.assertNotNull(commerceQualifierEntry);

		Assert.assertEquals(commerceQualifierEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		_persistence.remove(newCommerceQualifierEntry);

		CommerceQualifierEntry existingCommerceQualifierEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceQualifierEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceQualifierEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceQualifierEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceQualifierEntry newCommerceQualifierEntry = _persistence.create(
			pk);

		newCommerceQualifierEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setUserId(RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setUserName(RandomTestUtil.randomString());

		newCommerceQualifierEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommerceQualifierEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceQualifierEntry.setSourceClassNameId(
			RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setSourceClassPK(RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setSourceCommerceQualifierMetadataKey(
			RandomTestUtil.randomString());

		newCommerceQualifierEntry.setTargetClassNameId(
			RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setTargetClassPK(RandomTestUtil.nextLong());

		newCommerceQualifierEntry.setTargetCommerceQualifierMetadataKey(
			RandomTestUtil.randomString());

		_commerceQualifierEntries.add(
			_persistence.update(newCommerceQualifierEntry));

		CommerceQualifierEntry existingCommerceQualifierEntry =
			_persistence.findByPrimaryKey(
				newCommerceQualifierEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceQualifierEntry.getMvccVersion(),
			newCommerceQualifierEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getCommerceQualifierEntryId(),
			newCommerceQualifierEntry.getCommerceQualifierEntryId());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getCompanyId(),
			newCommerceQualifierEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getUserId(),
			newCommerceQualifierEntry.getUserId());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getUserName(),
			newCommerceQualifierEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceQualifierEntry.getCreateDate()),
			Time.getShortTimestamp(newCommerceQualifierEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceQualifierEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceQualifierEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceQualifierEntry.getSourceClassNameId(),
			newCommerceQualifierEntry.getSourceClassNameId());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getSourceClassPK(),
			newCommerceQualifierEntry.getSourceClassPK());
		Assert.assertEquals(
			existingCommerceQualifierEntry.
				getSourceCommerceQualifierMetadataKey(),
			newCommerceQualifierEntry.getSourceCommerceQualifierMetadataKey());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getTargetClassNameId(),
			newCommerceQualifierEntry.getTargetClassNameId());
		Assert.assertEquals(
			existingCommerceQualifierEntry.getTargetClassPK(),
			newCommerceQualifierEntry.getTargetClassPK());
		Assert.assertEquals(
			existingCommerceQualifierEntry.
				getTargetCommerceQualifierMetadataKey(),
			newCommerceQualifierEntry.getTargetCommerceQualifierMetadataKey());
	}

	@Test
	public void testCountByS_S() throws Exception {
		_persistence.countByS_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByS_S(0L, 0L);
	}

	@Test
	public void testCountByT_T() throws Exception {
		_persistence.countByT_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_T(0L, 0L);
	}

	@Test
	public void testCountByS_S_T() throws Exception {
		_persistence.countByS_S_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByS_S_T(0L, 0L, 0L);
	}

	@Test
	public void testCountByS_T_T() throws Exception {
		_persistence.countByS_T_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByS_T_T(0L, 0L, 0L);
	}

	@Test
	public void testCountByS_S_T_T() throws Exception {
		_persistence.countByS_S_T_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByS_S_T_T(0L, 0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		CommerceQualifierEntry existingCommerceQualifierEntry =
			_persistence.findByPrimaryKey(
				newCommerceQualifierEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceQualifierEntry, newCommerceQualifierEntry);
	}

	@Test(expected = NoSuchCommerceQualifierEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceQualifierEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceQualifierEntry", "mvccVersion", true,
			"commerceQualifierEntryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"sourceClassNameId", true, "sourceClassPK", true,
			"sourceCommerceQualifierMetadataKey", true, "targetClassNameId",
			true, "targetClassPK", true, "targetCommerceQualifierMetadataKey",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		CommerceQualifierEntry existingCommerceQualifierEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceQualifierEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceQualifierEntry, newCommerceQualifierEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceQualifierEntry missingCommerceQualifierEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceQualifierEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceQualifierEntry newCommerceQualifierEntry1 =
			addCommerceQualifierEntry();
		CommerceQualifierEntry newCommerceQualifierEntry2 =
			addCommerceQualifierEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceQualifierEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceQualifierEntry2.getPrimaryKey());

		Map<Serializable, CommerceQualifierEntry> commerceQualifierEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceQualifierEntries.size());
		Assert.assertEquals(
			newCommerceQualifierEntry1,
			commerceQualifierEntries.get(
				newCommerceQualifierEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceQualifierEntry2,
			commerceQualifierEntries.get(
				newCommerceQualifierEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceQualifierEntry> commerceQualifierEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceQualifierEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceQualifierEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceQualifierEntry> commerceQualifierEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceQualifierEntries.size());
		Assert.assertEquals(
			newCommerceQualifierEntry,
			commerceQualifierEntries.get(
				newCommerceQualifierEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceQualifierEntry> commerceQualifierEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceQualifierEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceQualifierEntry.getPrimaryKey());

		Map<Serializable, CommerceQualifierEntry> commerceQualifierEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceQualifierEntries.size());
		Assert.assertEquals(
			newCommerceQualifierEntry,
			commerceQualifierEntries.get(
				newCommerceQualifierEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceQualifierEntry newCommerceQualifierEntry =
			addCommerceQualifierEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceQualifierEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceQualifierEntry commerceQualifierEntry) {

		Assert.assertEquals(
			Long.valueOf(commerceQualifierEntry.getSourceClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceQualifierEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sourceClassNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceQualifierEntry.getSourceClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceQualifierEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sourceClassPK"));
		Assert.assertEquals(
			Long.valueOf(commerceQualifierEntry.getTargetClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceQualifierEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "targetClassNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceQualifierEntry.getTargetClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceQualifierEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "targetClassPK"));
	}

	protected CommerceQualifierEntry addCommerceQualifierEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceQualifierEntry commerceQualifierEntry = _persistence.create(pk);

		commerceQualifierEntry.setMvccVersion(RandomTestUtil.nextLong());

		commerceQualifierEntry.setCompanyId(RandomTestUtil.nextLong());

		commerceQualifierEntry.setUserId(RandomTestUtil.nextLong());

		commerceQualifierEntry.setUserName(RandomTestUtil.randomString());

		commerceQualifierEntry.setCreateDate(RandomTestUtil.nextDate());

		commerceQualifierEntry.setModifiedDate(RandomTestUtil.nextDate());

		commerceQualifierEntry.setSourceClassNameId(RandomTestUtil.nextLong());

		commerceQualifierEntry.setSourceClassPK(RandomTestUtil.nextLong());

		commerceQualifierEntry.setSourceCommerceQualifierMetadataKey(
			RandomTestUtil.randomString());

		commerceQualifierEntry.setTargetClassNameId(RandomTestUtil.nextLong());

		commerceQualifierEntry.setTargetClassPK(RandomTestUtil.nextLong());

		commerceQualifierEntry.setTargetCommerceQualifierMetadataKey(
			RandomTestUtil.randomString());

		_commerceQualifierEntries.add(
			_persistence.update(commerceQualifierEntry));

		return commerceQualifierEntry;
	}

	private List<CommerceQualifierEntry> _commerceQualifierEntries =
		new ArrayList<CommerceQualifierEntry>();
	private CommerceQualifierEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}