/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.reports.engine.console.exception.NoSuchEntryException;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.service.EntryLocalServiceUtil;
import com.liferay.portal.reports.engine.console.service.persistence.EntryPersistence;
import com.liferay.portal.reports.engine.console.service.persistence.EntryUtil;
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
public class EntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.reports.engine.console.service"));

	@Before
	public void setUp() {
		_persistence = EntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Entry> iterator = _entries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Entry entry = _persistence.create(pk);

		Assert.assertNotNull(entry);

		Assert.assertEquals(entry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Entry newEntry = addEntry();

		_persistence.remove(newEntry);

		Entry existingEntry = _persistence.fetchByPrimaryKey(
			newEntry.getPrimaryKey());

		Assert.assertNull(existingEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		Entry newEntry = addEntry();

		newEntry.setGroupId(RandomTestUtil.nextLong());

		newEntry.setCompanyId(RandomTestUtil.nextLong());

		newEntry.setUserId(RandomTestUtil.nextLong());

		newEntry.setUserName(RandomTestUtil.randomString());

		newEntry.setCreateDate(RandomTestUtil.nextDate());

		newEntry.setModifiedDate(RandomTestUtil.nextDate());

		newEntry.setDefinitionId(RandomTestUtil.nextLong());

		newEntry.setFormat(RandomTestUtil.randomString());

		newEntry.setScheduleRequest(RandomTestUtil.randomBoolean());

		newEntry.setStartDate(RandomTestUtil.nextDate());

		newEntry.setEndDate(RandomTestUtil.nextDate());

		newEntry.setRepeating(RandomTestUtil.randomBoolean());

		newEntry.setRecurrence(RandomTestUtil.randomString());

		newEntry.setEmailNotifications(RandomTestUtil.randomString());

		newEntry.setEmailDelivery(RandomTestUtil.randomString());

		newEntry.setPortletId(RandomTestUtil.randomString());

		newEntry.setPageURL(RandomTestUtil.randomString());

		newEntry.setReportParameters(RandomTestUtil.randomString());

		newEntry.setErrorMessage(RandomTestUtil.randomString());

		newEntry.setStatus(RandomTestUtil.randomString());

		newEntry = _persistence.update(newEntry);

		_entries.add(newEntry);

		Entry existingEntry = _persistence.findByPrimaryKey(
			newEntry.getPrimaryKey());

		Assert.assertEquals(existingEntry.getEntryId(), newEntry.getEntryId());
		Assert.assertEquals(existingEntry.getGroupId(), newEntry.getGroupId());
		Assert.assertEquals(
			existingEntry.getCompanyId(), newEntry.getCompanyId());
		Assert.assertEquals(existingEntry.getUserId(), newEntry.getUserId());
		Assert.assertEquals(
			existingEntry.getUserName(), newEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingEntry.getCreateDate()),
			Time.getShortTimestamp(newEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingEntry.getModifiedDate()),
			Time.getShortTimestamp(newEntry.getModifiedDate()));
		Assert.assertEquals(
			existingEntry.getDefinitionId(), newEntry.getDefinitionId());
		Assert.assertEquals(existingEntry.getFormat(), newEntry.getFormat());
		Assert.assertEquals(
			existingEntry.isScheduleRequest(), newEntry.isScheduleRequest());
		Assert.assertEquals(
			Time.getShortTimestamp(existingEntry.getStartDate()),
			Time.getShortTimestamp(newEntry.getStartDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingEntry.getEndDate()),
			Time.getShortTimestamp(newEntry.getEndDate()));
		Assert.assertEquals(
			existingEntry.isRepeating(), newEntry.isRepeating());
		Assert.assertEquals(
			existingEntry.getRecurrence(), newEntry.getRecurrence());
		Assert.assertEquals(
			existingEntry.getEmailNotifications(),
			newEntry.getEmailNotifications());
		Assert.assertEquals(
			existingEntry.getEmailDelivery(), newEntry.getEmailDelivery());
		Assert.assertEquals(
			existingEntry.getPortletId(), newEntry.getPortletId());
		Assert.assertEquals(existingEntry.getPageURL(), newEntry.getPageURL());
		Assert.assertEquals(
			existingEntry.getReportParameters(),
			newEntry.getReportParameters());
		Assert.assertEquals(
			existingEntry.getErrorMessage(), newEntry.getErrorMessage());
		Assert.assertEquals(existingEntry.getStatus(), newEntry.getStatus());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Entry newEntry = addEntry();

		Entry existingEntry = _persistence.findByPrimaryKey(
			newEntry.getPrimaryKey());

		Assert.assertEquals(existingEntry, newEntry);
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

	protected OrderByComparator<Entry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Reports_Entry", "entryId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "definitionId", true, "format", true,
			"scheduleRequest", true, "startDate", true, "endDate", true,
			"repeating", true, "recurrence", true, "emailNotifications", true,
			"emailDelivery", true, "portletId", true, "pageURL", true,
			"errorMessage", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Entry newEntry = addEntry();

		Entry existingEntry = _persistence.fetchByPrimaryKey(
			newEntry.getPrimaryKey());

		Assert.assertEquals(existingEntry, newEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Entry missingEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Entry newEntry1 = addEntry();
		Entry newEntry2 = addEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEntry1.getPrimaryKey());
		primaryKeys.add(newEntry2.getPrimaryKey());

		Map<Serializable, Entry> entries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, entries.size());
		Assert.assertEquals(newEntry1, entries.get(newEntry1.getPrimaryKey()));
		Assert.assertEquals(newEntry2, entries.get(newEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Entry> entries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(entries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Entry newEntry = addEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Entry> entries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(newEntry, entries.get(newEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Entry> entries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(entries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Entry newEntry = addEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEntry.getPrimaryKey());

		Map<Serializable, Entry> entries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(newEntry, entries.get(newEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			EntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Entry>() {

				@Override
				public void performAction(Entry entry) {
					Assert.assertNotNull(entry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Entry newEntry = addEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Entry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", newEntry.getEntryId()));

		List<Entry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Entry existingEntry = result.get(0);

		Assert.assertEquals(existingEntry, newEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Entry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("entryId", RandomTestUtil.nextLong()));

		List<Entry> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Entry newEntry = addEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Entry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		Object newEntryId = newEntry.getEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("entryId", new Object[] {newEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingEntryId = result.get(0);

		Assert.assertEquals(existingEntryId, newEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Entry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("entryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"entryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected Entry addEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Entry entry = _persistence.create(pk);

		entry.setGroupId(RandomTestUtil.nextLong());

		entry.setCompanyId(RandomTestUtil.nextLong());

		entry.setUserId(RandomTestUtil.nextLong());

		entry.setUserName(RandomTestUtil.randomString());

		entry.setCreateDate(RandomTestUtil.nextDate());

		entry.setModifiedDate(RandomTestUtil.nextDate());

		entry.setDefinitionId(RandomTestUtil.nextLong());

		entry.setFormat(RandomTestUtil.randomString());

		entry.setScheduleRequest(RandomTestUtil.randomBoolean());

		entry.setStartDate(RandomTestUtil.nextDate());

		entry.setEndDate(RandomTestUtil.nextDate());

		entry.setRepeating(RandomTestUtil.randomBoolean());

		entry.setRecurrence(RandomTestUtil.randomString());

		entry.setEmailNotifications(RandomTestUtil.randomString());

		entry.setEmailDelivery(RandomTestUtil.randomString());

		entry.setPortletId(RandomTestUtil.randomString());

		entry.setPageURL(RandomTestUtil.randomString());

		entry.setReportParameters(RandomTestUtil.randomString());

		entry.setErrorMessage(RandomTestUtil.randomString());

		entry.setStatus(RandomTestUtil.randomString());

		_entries.add(_persistence.update(entry));

		return entry;
	}

	private List<Entry> _entries = new ArrayList<Entry>();
	private EntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-112768687