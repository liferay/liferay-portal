/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.persistence.test;

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
import com.liferay.redirect.exception.NoSuchEntryException;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.persistence.RedirectEntryPersistence;
import com.liferay.redirect.service.persistence.RedirectEntryUtil;

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
public class RedirectEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.redirect.service"));

	@Before
	public void setUp() {
		_persistence = RedirectEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RedirectEntry> iterator = _redirectEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectEntry redirectEntry = _persistence.create(pk);

		Assert.assertNotNull(redirectEntry);

		Assert.assertEquals(redirectEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RedirectEntry newRedirectEntry = addRedirectEntry();

		_persistence.remove(newRedirectEntry);

		RedirectEntry existingRedirectEntry = _persistence.fetchByPrimaryKey(
			newRedirectEntry.getPrimaryKey());

		Assert.assertNull(existingRedirectEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRedirectEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectEntry newRedirectEntry = _persistence.create(pk);

		newRedirectEntry.setMvccVersion(RandomTestUtil.nextLong());

		newRedirectEntry.setUuid(RandomTestUtil.randomString());

		newRedirectEntry.setGroupId(RandomTestUtil.nextLong());

		newRedirectEntry.setCompanyId(RandomTestUtil.nextLong());

		newRedirectEntry.setUserId(RandomTestUtil.nextLong());

		newRedirectEntry.setUserName(RandomTestUtil.randomString());

		newRedirectEntry.setCreateDate(RandomTestUtil.nextDate());

		newRedirectEntry.setModifiedDate(RandomTestUtil.nextDate());

		newRedirectEntry.setDestinationURL(RandomTestUtil.randomString());

		newRedirectEntry.setExpirationDate(RandomTestUtil.nextDate());

		newRedirectEntry.setLastOccurrenceDate(RandomTestUtil.nextDate());

		newRedirectEntry.setPermanent(RandomTestUtil.randomBoolean());

		newRedirectEntry.setSourceURL(RandomTestUtil.randomString());

		_redirectEntries.add(_persistence.update(newRedirectEntry));

		RedirectEntry existingRedirectEntry = _persistence.findByPrimaryKey(
			newRedirectEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedirectEntry.getMvccVersion(),
			newRedirectEntry.getMvccVersion());
		Assert.assertEquals(
			existingRedirectEntry.getUuid(), newRedirectEntry.getUuid());
		Assert.assertEquals(
			existingRedirectEntry.getRedirectEntryId(),
			newRedirectEntry.getRedirectEntryId());
		Assert.assertEquals(
			existingRedirectEntry.getGroupId(), newRedirectEntry.getGroupId());
		Assert.assertEquals(
			existingRedirectEntry.getCompanyId(),
			newRedirectEntry.getCompanyId());
		Assert.assertEquals(
			existingRedirectEntry.getUserId(), newRedirectEntry.getUserId());
		Assert.assertEquals(
			existingRedirectEntry.getUserName(),
			newRedirectEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRedirectEntry.getCreateDate()),
			Time.getShortTimestamp(newRedirectEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRedirectEntry.getModifiedDate()),
			Time.getShortTimestamp(newRedirectEntry.getModifiedDate()));
		Assert.assertEquals(
			existingRedirectEntry.getDestinationURL(),
			newRedirectEntry.getDestinationURL());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRedirectEntry.getExpirationDate()),
			Time.getShortTimestamp(newRedirectEntry.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingRedirectEntry.getLastOccurrenceDate()),
			Time.getShortTimestamp(newRedirectEntry.getLastOccurrenceDate()));
		Assert.assertEquals(
			existingRedirectEntry.isPermanent(),
			newRedirectEntry.isPermanent());
		Assert.assertEquals(
			existingRedirectEntry.getSourceURL(),
			newRedirectEntry.getSourceURL());
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
	public void testCountByG_D() throws Exception {
		_persistence.countByG_D(RandomTestUtil.nextLong(), "");

		_persistence.countByG_D(0L, "null");

		_persistence.countByG_D(0L, (String)null);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(RandomTestUtil.nextLong(), "");

		_persistence.countByG_S(0L, "null");

		_persistence.countByG_S(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RedirectEntry newRedirectEntry = addRedirectEntry();

		RedirectEntry existingRedirectEntry = _persistence.findByPrimaryKey(
			newRedirectEntry.getPrimaryKey());

		Assert.assertEquals(existingRedirectEntry, newRedirectEntry);
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

	protected OrderByComparator<RedirectEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RedirectEntry", "mvccVersion", true, "uuid", true,
			"redirectEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "destinationURL", true, "expirationDate",
			true, "lastOccurrenceDate", true, "permanent", true, "sourceURL",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RedirectEntry newRedirectEntry = addRedirectEntry();

		RedirectEntry existingRedirectEntry = _persistence.fetchByPrimaryKey(
			newRedirectEntry.getPrimaryKey());

		Assert.assertEquals(existingRedirectEntry, newRedirectEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectEntry missingRedirectEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRedirectEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RedirectEntry newRedirectEntry1 = addRedirectEntry();
		RedirectEntry newRedirectEntry2 = addRedirectEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectEntry1.getPrimaryKey());
		primaryKeys.add(newRedirectEntry2.getPrimaryKey());

		Map<Serializable, RedirectEntry> redirectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, redirectEntries.size());
		Assert.assertEquals(
			newRedirectEntry1,
			redirectEntries.get(newRedirectEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRedirectEntry2,
			redirectEntries.get(newRedirectEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RedirectEntry> redirectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redirectEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RedirectEntry newRedirectEntry = addRedirectEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RedirectEntry> redirectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redirectEntries.size());
		Assert.assertEquals(
			newRedirectEntry,
			redirectEntries.get(newRedirectEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RedirectEntry> redirectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redirectEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RedirectEntry newRedirectEntry = addRedirectEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectEntry.getPrimaryKey());

		Map<Serializable, RedirectEntry> redirectEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redirectEntries.size());
		Assert.assertEquals(
			newRedirectEntry,
			redirectEntries.get(newRedirectEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RedirectEntry newRedirectEntry = addRedirectEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newRedirectEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(RedirectEntry redirectEntry) {
		Assert.assertEquals(
			redirectEntry.getUuid(),
			ReflectionTestUtil.invoke(
				redirectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(redirectEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				redirectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(redirectEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				redirectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			redirectEntry.getSourceURL(),
			ReflectionTestUtil.invoke(
				redirectEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sourceURL"));
	}

	protected RedirectEntry addRedirectEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectEntry redirectEntry = _persistence.create(pk);

		redirectEntry.setMvccVersion(RandomTestUtil.nextLong());

		redirectEntry.setUuid(RandomTestUtil.randomString());

		redirectEntry.setGroupId(RandomTestUtil.nextLong());

		redirectEntry.setCompanyId(RandomTestUtil.nextLong());

		redirectEntry.setUserId(RandomTestUtil.nextLong());

		redirectEntry.setUserName(RandomTestUtil.randomString());

		redirectEntry.setCreateDate(RandomTestUtil.nextDate());

		redirectEntry.setModifiedDate(RandomTestUtil.nextDate());

		redirectEntry.setDestinationURL(RandomTestUtil.randomString());

		redirectEntry.setExpirationDate(RandomTestUtil.nextDate());

		redirectEntry.setLastOccurrenceDate(RandomTestUtil.nextDate());

		redirectEntry.setPermanent(RandomTestUtil.randomBoolean());

		redirectEntry.setSourceURL(RandomTestUtil.randomString());

		_redirectEntries.add(_persistence.update(redirectEntry));

		return redirectEntry;
	}

	private List<RedirectEntry> _redirectEntries =
		new ArrayList<RedirectEntry>();
	private RedirectEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}