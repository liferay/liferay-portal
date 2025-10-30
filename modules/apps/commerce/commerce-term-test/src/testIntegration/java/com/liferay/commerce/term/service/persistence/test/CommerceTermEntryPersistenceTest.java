/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.term.exception.DuplicateCommerceTermEntryExternalReferenceCodeException;
import com.liferay.commerce.term.exception.NoSuchTermEntryException;
import com.liferay.commerce.term.model.CommerceTermEntry;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryPersistence;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommerceTermEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.term.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTermEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTermEntry> iterator = _commerceTermEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntry commerceTermEntry = _persistence.create(pk);

		Assert.assertNotNull(commerceTermEntry);

		Assert.assertEquals(commerceTermEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		_persistence.remove(newCommerceTermEntry);

		CommerceTermEntry existingCommerceTermEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceTermEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceTermEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTermEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntry newCommerceTermEntry = _persistence.create(pk);

		newCommerceTermEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceTermEntry.setUuid(RandomTestUtil.randomString());

		newCommerceTermEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceTermEntry.setDefaultLanguageId(
			RandomTestUtil.randomString());

		newCommerceTermEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTermEntry.setUserId(RandomTestUtil.nextLong());

		newCommerceTermEntry.setUserName(RandomTestUtil.randomString());

		newCommerceTermEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTermEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceTermEntry.setActive(RandomTestUtil.randomBoolean());

		newCommerceTermEntry.setDisplayDate(RandomTestUtil.nextDate());

		newCommerceTermEntry.setExpirationDate(RandomTestUtil.nextDate());

		newCommerceTermEntry.setName(RandomTestUtil.randomString());

		newCommerceTermEntry.setPriority(RandomTestUtil.nextDouble());

		newCommerceTermEntry.setType(RandomTestUtil.randomString());

		newCommerceTermEntry.setTypeSettings(RandomTestUtil.randomString());

		newCommerceTermEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newCommerceTermEntry.setStatus(RandomTestUtil.nextInt());

		newCommerceTermEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newCommerceTermEntry.setStatusByUserName(RandomTestUtil.randomString());

		newCommerceTermEntry.setStatusDate(RandomTestUtil.nextDate());

		_commerceTermEntries.add(_persistence.update(newCommerceTermEntry));

		CommerceTermEntry existingCommerceTermEntry =
			_persistence.findByPrimaryKey(newCommerceTermEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTermEntry.getMvccVersion(),
			newCommerceTermEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTermEntry.getUuid(),
			newCommerceTermEntry.getUuid());
		Assert.assertEquals(
			existingCommerceTermEntry.getExternalReferenceCode(),
			newCommerceTermEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceTermEntry.getDefaultLanguageId(),
			newCommerceTermEntry.getDefaultLanguageId());
		Assert.assertEquals(
			existingCommerceTermEntry.getCommerceTermEntryId(),
			newCommerceTermEntry.getCommerceTermEntryId());
		Assert.assertEquals(
			existingCommerceTermEntry.getCompanyId(),
			newCommerceTermEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceTermEntry.getUserId(),
			newCommerceTermEntry.getUserId());
		Assert.assertEquals(
			existingCommerceTermEntry.getUserName(),
			newCommerceTermEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTermEntry.getCreateDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTermEntry.getModifiedDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTermEntry.isActive(),
			newCommerceTermEntry.isActive());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTermEntry.getDisplayDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTermEntry.getExpirationDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getExpirationDate()));
		Assert.assertEquals(
			existingCommerceTermEntry.getName(),
			newCommerceTermEntry.getName());
		AssertUtils.assertEquals(
			existingCommerceTermEntry.getPriority(),
			newCommerceTermEntry.getPriority());
		Assert.assertEquals(
			existingCommerceTermEntry.getType(),
			newCommerceTermEntry.getType());
		Assert.assertEquals(
			existingCommerceTermEntry.getTypeSettings(),
			newCommerceTermEntry.getTypeSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTermEntry.getLastPublishDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingCommerceTermEntry.getStatus(),
			newCommerceTermEntry.getStatus());
		Assert.assertEquals(
			existingCommerceTermEntry.getStatusByUserId(),
			newCommerceTermEntry.getStatusByUserId());
		Assert.assertEquals(
			existingCommerceTermEntry.getStatusByUserName(),
			newCommerceTermEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTermEntry.getStatusDate()),
			Time.getShortTimestamp(newCommerceTermEntry.getStatusDate()));
	}

	@Test(
		expected = DuplicateCommerceTermEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceTermEntry commerceTermEntry = addCommerceTermEntry();

		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		newCommerceTermEntry.setCompanyId(commerceTermEntry.getCompanyId());

		newCommerceTermEntry = _persistence.update(newCommerceTermEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceTermEntry);

		newCommerceTermEntry.setExternalReferenceCode(
			commerceTermEntry.getExternalReferenceCode());

		_persistence.update(newCommerceTermEntry);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByC_LikeType() throws Exception {
		_persistence.countByC_LikeType(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeType(0L, "null");

		_persistence.countByC_LikeType(0L, (String)null);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByLtE_S() throws Exception {
		_persistence.countByLtE_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtE_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_A_LikeType() throws Exception {
		_persistence.countByC_A_LikeType(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByC_A_LikeType(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByC_A_LikeType(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByC_P_T() throws Exception {
		_persistence.countByC_P_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDouble(), "");

		_persistence.countByC_P_T(0L, 0D, "null");

		_persistence.countByC_P_T(0L, 0D, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		CommerceTermEntry existingCommerceTermEntry =
			_persistence.findByPrimaryKey(newCommerceTermEntry.getPrimaryKey());

		Assert.assertEquals(existingCommerceTermEntry, newCommerceTermEntry);
	}

	@Test(expected = NoSuchTermEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTermEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceTermEntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "defaultLanguageId", true,
			"commerceTermEntryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"active", true, "displayDate", true, "expirationDate", true, "name",
			true, "priority", true, "type", true, "typeSettings", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		CommerceTermEntry existingCommerceTermEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceTermEntry.getPrimaryKey());

		Assert.assertEquals(existingCommerceTermEntry, newCommerceTermEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntry missingCommerceTermEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTermEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTermEntry newCommerceTermEntry1 = addCommerceTermEntry();
		CommerceTermEntry newCommerceTermEntry2 = addCommerceTermEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceTermEntry2.getPrimaryKey());

		Map<Serializable, CommerceTermEntry> commerceTermEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceTermEntries.size());
		Assert.assertEquals(
			newCommerceTermEntry1,
			commerceTermEntries.get(newCommerceTermEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTermEntry2,
			commerceTermEntries.get(newCommerceTermEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTermEntry> commerceTermEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTermEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTermEntry> commerceTermEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTermEntries.size());
		Assert.assertEquals(
			newCommerceTermEntry,
			commerceTermEntries.get(newCommerceTermEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTermEntry> commerceTermEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTermEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntry.getPrimaryKey());

		Map<Serializable, CommerceTermEntry> commerceTermEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTermEntries.size());
		Assert.assertEquals(
			newCommerceTermEntry,
			commerceTermEntries.get(newCommerceTermEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTermEntry newCommerceTermEntry = addCommerceTermEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTermEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceTermEntry commerceTermEntry) {
		Assert.assertEquals(
			Long.valueOf(commerceTermEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			commerceTermEntry.getName(),
			ReflectionTestUtil.invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(commerceTermEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		AssertUtils.assertEquals(
			commerceTermEntry.getPriority(),
			ReflectionTestUtil.<Double>invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "priority"));
		Assert.assertEquals(
			commerceTermEntry.getType(),
			ReflectionTestUtil.invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));

		Assert.assertEquals(
			commerceTermEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceTermEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceTermEntry addCommerceTermEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntry commerceTermEntry = _persistence.create(pk);

		commerceTermEntry.setMvccVersion(RandomTestUtil.nextLong());

		commerceTermEntry.setUuid(RandomTestUtil.randomString());

		commerceTermEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceTermEntry.setDefaultLanguageId(RandomTestUtil.randomString());

		commerceTermEntry.setCompanyId(RandomTestUtil.nextLong());

		commerceTermEntry.setUserId(RandomTestUtil.nextLong());

		commerceTermEntry.setUserName(RandomTestUtil.randomString());

		commerceTermEntry.setCreateDate(RandomTestUtil.nextDate());

		commerceTermEntry.setModifiedDate(RandomTestUtil.nextDate());

		commerceTermEntry.setActive(RandomTestUtil.randomBoolean());

		commerceTermEntry.setDisplayDate(RandomTestUtil.nextDate());

		commerceTermEntry.setExpirationDate(RandomTestUtil.nextDate());

		commerceTermEntry.setName(RandomTestUtil.randomString());

		commerceTermEntry.setPriority(RandomTestUtil.nextDouble());

		commerceTermEntry.setType(RandomTestUtil.randomString());

		commerceTermEntry.setTypeSettings(RandomTestUtil.randomString());

		commerceTermEntry.setLastPublishDate(RandomTestUtil.nextDate());

		commerceTermEntry.setStatus(RandomTestUtil.nextInt());

		commerceTermEntry.setStatusByUserId(RandomTestUtil.nextLong());

		commerceTermEntry.setStatusByUserName(RandomTestUtil.randomString());

		commerceTermEntry.setStatusDate(RandomTestUtil.nextDate());

		_commerceTermEntries.add(_persistence.update(commerceTermEntry));

		return commerceTermEntry;
	}

	private List<CommerceTermEntry> _commerceTermEntries =
		new ArrayList<CommerceTermEntry>();
	private CommerceTermEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}