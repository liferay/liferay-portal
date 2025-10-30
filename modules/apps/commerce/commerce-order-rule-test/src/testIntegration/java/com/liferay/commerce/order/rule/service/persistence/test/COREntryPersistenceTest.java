/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.exception.DuplicateCOREntryExternalReferenceCodeException;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryException;
import com.liferay.commerce.order.rule.model.COREntry;
import com.liferay.commerce.order.rule.service.persistence.COREntryPersistence;
import com.liferay.commerce.order.rule.service.persistence.COREntryUtil;
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
public class COREntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.order.rule.service"));

	@Before
	public void setUp() {
		_persistence = COREntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<COREntry> iterator = _corEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntry corEntry = _persistence.create(pk);

		Assert.assertNotNull(corEntry);

		Assert.assertEquals(corEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		COREntry newCOREntry = addCOREntry();

		_persistence.remove(newCOREntry);

		COREntry existingCOREntry = _persistence.fetchByPrimaryKey(
			newCOREntry.getPrimaryKey());

		Assert.assertNull(existingCOREntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCOREntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntry newCOREntry = _persistence.create(pk);

		newCOREntry.setMvccVersion(RandomTestUtil.nextLong());

		newCOREntry.setUuid(RandomTestUtil.randomString());

		newCOREntry.setExternalReferenceCode(RandomTestUtil.randomString());

		newCOREntry.setCompanyId(RandomTestUtil.nextLong());

		newCOREntry.setUserId(RandomTestUtil.nextLong());

		newCOREntry.setUserName(RandomTestUtil.randomString());

		newCOREntry.setCreateDate(RandomTestUtil.nextDate());

		newCOREntry.setModifiedDate(RandomTestUtil.nextDate());

		newCOREntry.setActive(RandomTestUtil.randomBoolean());

		newCOREntry.setDescription(RandomTestUtil.randomString());

		newCOREntry.setDisplayDate(RandomTestUtil.nextDate());

		newCOREntry.setExpirationDate(RandomTestUtil.nextDate());

		newCOREntry.setName(RandomTestUtil.randomString());

		newCOREntry.setPriority(RandomTestUtil.nextInt());

		newCOREntry.setType(RandomTestUtil.randomString());

		newCOREntry.setTypeSettings(RandomTestUtil.randomString());

		newCOREntry.setLastPublishDate(RandomTestUtil.nextDate());

		newCOREntry.setStatus(RandomTestUtil.nextInt());

		newCOREntry.setStatusByUserId(RandomTestUtil.nextLong());

		newCOREntry.setStatusByUserName(RandomTestUtil.randomString());

		newCOREntry.setStatusDate(RandomTestUtil.nextDate());

		_corEntries.add(_persistence.update(newCOREntry));

		COREntry existingCOREntry = _persistence.findByPrimaryKey(
			newCOREntry.getPrimaryKey());

		Assert.assertEquals(
			existingCOREntry.getMvccVersion(), newCOREntry.getMvccVersion());
		Assert.assertEquals(existingCOREntry.getUuid(), newCOREntry.getUuid());
		Assert.assertEquals(
			existingCOREntry.getExternalReferenceCode(),
			newCOREntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCOREntry.getCOREntryId(), newCOREntry.getCOREntryId());
		Assert.assertEquals(
			existingCOREntry.getCompanyId(), newCOREntry.getCompanyId());
		Assert.assertEquals(
			existingCOREntry.getUserId(), newCOREntry.getUserId());
		Assert.assertEquals(
			existingCOREntry.getUserName(), newCOREntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getCreateDate()),
			Time.getShortTimestamp(newCOREntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getModifiedDate()),
			Time.getShortTimestamp(newCOREntry.getModifiedDate()));
		Assert.assertEquals(
			existingCOREntry.isActive(), newCOREntry.isActive());
		Assert.assertEquals(
			existingCOREntry.getDescription(), newCOREntry.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getDisplayDate()),
			Time.getShortTimestamp(newCOREntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getExpirationDate()),
			Time.getShortTimestamp(newCOREntry.getExpirationDate()));
		Assert.assertEquals(existingCOREntry.getName(), newCOREntry.getName());
		Assert.assertEquals(
			existingCOREntry.getPriority(), newCOREntry.getPriority());
		Assert.assertEquals(existingCOREntry.getType(), newCOREntry.getType());
		Assert.assertEquals(
			existingCOREntry.getTypeSettings(), newCOREntry.getTypeSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getLastPublishDate()),
			Time.getShortTimestamp(newCOREntry.getLastPublishDate()));
		Assert.assertEquals(
			existingCOREntry.getStatus(), newCOREntry.getStatus());
		Assert.assertEquals(
			existingCOREntry.getStatusByUserId(),
			newCOREntry.getStatusByUserId());
		Assert.assertEquals(
			existingCOREntry.getStatusByUserName(),
			newCOREntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntry.getStatusDate()),
			Time.getShortTimestamp(newCOREntry.getStatusDate()));
	}

	@Test(expected = DuplicateCOREntryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		COREntry corEntry = addCOREntry();

		COREntry newCOREntry = addCOREntry();

		newCOREntry.setCompanyId(corEntry.getCompanyId());

		newCOREntry = _persistence.update(newCOREntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCOREntry);

		newCOREntry.setExternalReferenceCode(
			corEntry.getExternalReferenceCode());

		_persistence.update(newCOREntry);
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
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		COREntry newCOREntry = addCOREntry();

		COREntry existingCOREntry = _persistence.findByPrimaryKey(
			newCOREntry.getPrimaryKey());

		Assert.assertEquals(existingCOREntry, newCOREntry);
	}

	@Test(expected = NoSuchCOREntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<COREntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"COREntry", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "COREntryId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "active", true, "description", true,
			"displayDate", true, "expirationDate", true, "name", true,
			"priority", true, "type", true, "lastPublishDate", true, "status",
			true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		COREntry newCOREntry = addCOREntry();

		COREntry existingCOREntry = _persistence.fetchByPrimaryKey(
			newCOREntry.getPrimaryKey());

		Assert.assertEquals(existingCOREntry, newCOREntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntry missingCOREntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCOREntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		COREntry newCOREntry1 = addCOREntry();
		COREntry newCOREntry2 = addCOREntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntry1.getPrimaryKey());
		primaryKeys.add(newCOREntry2.getPrimaryKey());

		Map<Serializable, COREntry> corEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, corEntries.size());
		Assert.assertEquals(
			newCOREntry1, corEntries.get(newCOREntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCOREntry2, corEntries.get(newCOREntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, COREntry> corEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(corEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		COREntry newCOREntry = addCOREntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, COREntry> corEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, corEntries.size());
		Assert.assertEquals(
			newCOREntry, corEntries.get(newCOREntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, COREntry> corEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(corEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		COREntry newCOREntry = addCOREntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntry.getPrimaryKey());

		Map<Serializable, COREntry> corEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, corEntries.size());
		Assert.assertEquals(
			newCOREntry, corEntries.get(newCOREntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		COREntry newCOREntry = addCOREntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCOREntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(COREntry corEntry) {
		Assert.assertEquals(
			corEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				corEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(corEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				corEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected COREntry addCOREntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntry corEntry = _persistence.create(pk);

		corEntry.setMvccVersion(RandomTestUtil.nextLong());

		corEntry.setUuid(RandomTestUtil.randomString());

		corEntry.setExternalReferenceCode(RandomTestUtil.randomString());

		corEntry.setCompanyId(RandomTestUtil.nextLong());

		corEntry.setUserId(RandomTestUtil.nextLong());

		corEntry.setUserName(RandomTestUtil.randomString());

		corEntry.setCreateDate(RandomTestUtil.nextDate());

		corEntry.setModifiedDate(RandomTestUtil.nextDate());

		corEntry.setActive(RandomTestUtil.randomBoolean());

		corEntry.setDescription(RandomTestUtil.randomString());

		corEntry.setDisplayDate(RandomTestUtil.nextDate());

		corEntry.setExpirationDate(RandomTestUtil.nextDate());

		corEntry.setName(RandomTestUtil.randomString());

		corEntry.setPriority(RandomTestUtil.nextInt());

		corEntry.setType(RandomTestUtil.randomString());

		corEntry.setTypeSettings(RandomTestUtil.randomString());

		corEntry.setLastPublishDate(RandomTestUtil.nextDate());

		corEntry.setStatus(RandomTestUtil.nextInt());

		corEntry.setStatusByUserId(RandomTestUtil.nextLong());

		corEntry.setStatusByUserName(RandomTestUtil.randomString());

		corEntry.setStatusDate(RandomTestUtil.nextDate());

		_corEntries.add(_persistence.update(corEntry));

		return corEntry;
	}

	private List<COREntry> _corEntries = new ArrayList<COREntry>();
	private COREntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}