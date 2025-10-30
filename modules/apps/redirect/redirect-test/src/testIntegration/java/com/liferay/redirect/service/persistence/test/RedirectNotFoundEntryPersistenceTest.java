/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.redirect.exception.NoSuchNotFoundEntryException;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.persistence.RedirectNotFoundEntryPersistence;
import com.liferay.redirect.service.persistence.RedirectNotFoundEntryUtil;

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
public class RedirectNotFoundEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.redirect.service"));

	@Before
	public void setUp() {
		_persistence = RedirectNotFoundEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RedirectNotFoundEntry> iterator =
			_redirectNotFoundEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectNotFoundEntry redirectNotFoundEntry = _persistence.create(pk);

		Assert.assertNotNull(redirectNotFoundEntry);

		Assert.assertEquals(redirectNotFoundEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		_persistence.remove(newRedirectNotFoundEntry);

		RedirectNotFoundEntry existingRedirectNotFoundEntry =
			_persistence.fetchByPrimaryKey(
				newRedirectNotFoundEntry.getPrimaryKey());

		Assert.assertNull(existingRedirectNotFoundEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRedirectNotFoundEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectNotFoundEntry newRedirectNotFoundEntry = _persistence.create(
			pk);

		newRedirectNotFoundEntry.setMvccVersion(RandomTestUtil.nextLong());

		newRedirectNotFoundEntry.setGroupId(RandomTestUtil.nextLong());

		newRedirectNotFoundEntry.setCompanyId(RandomTestUtil.nextLong());

		newRedirectNotFoundEntry.setUserId(RandomTestUtil.nextLong());

		newRedirectNotFoundEntry.setUserName(RandomTestUtil.randomString());

		newRedirectNotFoundEntry.setCreateDate(RandomTestUtil.nextDate());

		newRedirectNotFoundEntry.setModifiedDate(RandomTestUtil.nextDate());

		newRedirectNotFoundEntry.setIgnored(RandomTestUtil.randomBoolean());

		newRedirectNotFoundEntry.setUrl(RandomTestUtil.randomString());

		_redirectNotFoundEntries.add(
			_persistence.update(newRedirectNotFoundEntry));

		RedirectNotFoundEntry existingRedirectNotFoundEntry =
			_persistence.findByPrimaryKey(
				newRedirectNotFoundEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedirectNotFoundEntry.getMvccVersion(),
			newRedirectNotFoundEntry.getMvccVersion());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getRedirectNotFoundEntryId(),
			newRedirectNotFoundEntry.getRedirectNotFoundEntryId());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getGroupId(),
			newRedirectNotFoundEntry.getGroupId());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getCompanyId(),
			newRedirectNotFoundEntry.getCompanyId());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getUserId(),
			newRedirectNotFoundEntry.getUserId());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getUserName(),
			newRedirectNotFoundEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingRedirectNotFoundEntry.getCreateDate()),
			Time.getShortTimestamp(newRedirectNotFoundEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingRedirectNotFoundEntry.getModifiedDate()),
			Time.getShortTimestamp(newRedirectNotFoundEntry.getModifiedDate()));
		Assert.assertEquals(
			existingRedirectNotFoundEntry.isIgnored(),
			newRedirectNotFoundEntry.isIgnored());
		Assert.assertEquals(
			existingRedirectNotFoundEntry.getUrl(),
			newRedirectNotFoundEntry.getUrl());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(RandomTestUtil.nextLong(), "");

		_persistence.countByG_U(0L, "null");

		_persistence.countByG_U(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		RedirectNotFoundEntry existingRedirectNotFoundEntry =
			_persistence.findByPrimaryKey(
				newRedirectNotFoundEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedirectNotFoundEntry, newRedirectNotFoundEntry);
	}

	@Test(expected = NoSuchNotFoundEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RedirectNotFoundEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RedirectNotFoundEntry", "mvccVersion", true,
			"redirectNotFoundEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "ignored", true, "url", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		RedirectNotFoundEntry existingRedirectNotFoundEntry =
			_persistence.fetchByPrimaryKey(
				newRedirectNotFoundEntry.getPrimaryKey());

		Assert.assertEquals(
			existingRedirectNotFoundEntry, newRedirectNotFoundEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RedirectNotFoundEntry missingRedirectNotFoundEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRedirectNotFoundEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RedirectNotFoundEntry newRedirectNotFoundEntry1 =
			addRedirectNotFoundEntry();
		RedirectNotFoundEntry newRedirectNotFoundEntry2 =
			addRedirectNotFoundEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectNotFoundEntry1.getPrimaryKey());
		primaryKeys.add(newRedirectNotFoundEntry2.getPrimaryKey());

		Map<Serializable, RedirectNotFoundEntry> redirectNotFoundEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, redirectNotFoundEntries.size());
		Assert.assertEquals(
			newRedirectNotFoundEntry1,
			redirectNotFoundEntries.get(
				newRedirectNotFoundEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newRedirectNotFoundEntry2,
			redirectNotFoundEntries.get(
				newRedirectNotFoundEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RedirectNotFoundEntry> redirectNotFoundEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redirectNotFoundEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectNotFoundEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RedirectNotFoundEntry> redirectNotFoundEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redirectNotFoundEntries.size());
		Assert.assertEquals(
			newRedirectNotFoundEntry,
			redirectNotFoundEntries.get(
				newRedirectNotFoundEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RedirectNotFoundEntry> redirectNotFoundEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(redirectNotFoundEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRedirectNotFoundEntry.getPrimaryKey());

		Map<Serializable, RedirectNotFoundEntry> redirectNotFoundEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, redirectNotFoundEntries.size());
		Assert.assertEquals(
			newRedirectNotFoundEntry,
			redirectNotFoundEntries.get(
				newRedirectNotFoundEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RedirectNotFoundEntry newRedirectNotFoundEntry =
			addRedirectNotFoundEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newRedirectNotFoundEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		RedirectNotFoundEntry redirectNotFoundEntry) {

		Assert.assertEquals(
			Long.valueOf(redirectNotFoundEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				redirectNotFoundEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			redirectNotFoundEntry.getUrl(),
			ReflectionTestUtil.invoke(
				redirectNotFoundEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "url"));
	}

	protected RedirectNotFoundEntry addRedirectNotFoundEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		RedirectNotFoundEntry redirectNotFoundEntry = _persistence.create(pk);

		redirectNotFoundEntry.setMvccVersion(RandomTestUtil.nextLong());

		redirectNotFoundEntry.setGroupId(RandomTestUtil.nextLong());

		redirectNotFoundEntry.setCompanyId(RandomTestUtil.nextLong());

		redirectNotFoundEntry.setUserId(RandomTestUtil.nextLong());

		redirectNotFoundEntry.setUserName(RandomTestUtil.randomString());

		redirectNotFoundEntry.setCreateDate(RandomTestUtil.nextDate());

		redirectNotFoundEntry.setModifiedDate(RandomTestUtil.nextDate());

		redirectNotFoundEntry.setIgnored(RandomTestUtil.randomBoolean());

		redirectNotFoundEntry.setUrl(RandomTestUtil.randomString());

		_redirectNotFoundEntries.add(
			_persistence.update(redirectNotFoundEntry));

		return redirectNotFoundEntry;
	}

	private List<RedirectNotFoundEntry> _redirectNotFoundEntries =
		new ArrayList<RedirectNotFoundEntry>();
	private RedirectNotFoundEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}