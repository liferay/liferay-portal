/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.exception.DuplicateCalendarBookingExternalReferenceCodeException;
import com.liferay.calendar.exception.NoSuchBookingException;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.service.CalendarBookingLocalServiceUtil;
import com.liferay.calendar.service.persistence.CalendarBookingPersistence;
import com.liferay.calendar.service.persistence.CalendarBookingUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CalendarBookingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.calendar.service"));

	@Before
	public void setUp() {
		_persistence = CalendarBookingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CalendarBooking> iterator = _calendarBookings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarBooking calendarBooking = _persistence.create(pk);

		Assert.assertNotNull(calendarBooking);

		Assert.assertEquals(calendarBooking.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		_persistence.remove(newCalendarBooking);

		CalendarBooking existingCalendarBooking =
			_persistence.fetchByPrimaryKey(newCalendarBooking.getPrimaryKey());

		Assert.assertNull(existingCalendarBooking);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCalendarBooking();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarBooking newCalendarBooking = _persistence.create(pk);

		newCalendarBooking.setMvccVersion(RandomTestUtil.nextLong());

		newCalendarBooking.setCtCollectionId(RandomTestUtil.nextLong());

		newCalendarBooking.setUuid(RandomTestUtil.randomString());

		newCalendarBooking.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCalendarBooking.setGroupId(RandomTestUtil.nextLong());

		newCalendarBooking.setCompanyId(RandomTestUtil.nextLong());

		newCalendarBooking.setUserId(RandomTestUtil.nextLong());

		newCalendarBooking.setUserName(RandomTestUtil.randomString());

		newCalendarBooking.setCreateDate(RandomTestUtil.nextDate());

		newCalendarBooking.setModifiedDate(RandomTestUtil.nextDate());

		newCalendarBooking.setCalendarId(RandomTestUtil.nextLong());

		newCalendarBooking.setCalendarResourceId(RandomTestUtil.nextLong());

		newCalendarBooking.setParentCalendarBookingId(
			RandomTestUtil.nextLong());

		newCalendarBooking.setRecurringCalendarBookingId(
			RandomTestUtil.nextLong());

		newCalendarBooking.setVEventUid(RandomTestUtil.randomString());

		newCalendarBooking.setTitle(RandomTestUtil.randomString());

		newCalendarBooking.setDescription(RandomTestUtil.randomString());

		newCalendarBooking.setLocation(RandomTestUtil.randomString());

		newCalendarBooking.setStartTime(RandomTestUtil.nextLong());

		newCalendarBooking.setEndTime(RandomTestUtil.nextLong());

		newCalendarBooking.setAllDay(RandomTestUtil.randomBoolean());

		newCalendarBooking.setRecurrence(RandomTestUtil.randomString());

		newCalendarBooking.setFirstReminder(RandomTestUtil.nextLong());

		newCalendarBooking.setFirstReminderType(RandomTestUtil.randomString());

		newCalendarBooking.setSecondReminder(RandomTestUtil.nextLong());

		newCalendarBooking.setSecondReminderType(RandomTestUtil.randomString());

		newCalendarBooking.setLastPublishDate(RandomTestUtil.nextDate());

		newCalendarBooking.setStatus(RandomTestUtil.nextInt());

		newCalendarBooking.setStatusByUserId(RandomTestUtil.nextLong());

		newCalendarBooking.setStatusByUserName(RandomTestUtil.randomString());

		newCalendarBooking.setStatusDate(RandomTestUtil.nextDate());

		_calendarBookings.add(_persistence.update(newCalendarBooking));

		CalendarBooking existingCalendarBooking = _persistence.findByPrimaryKey(
			newCalendarBooking.getPrimaryKey());

		Assert.assertEquals(
			existingCalendarBooking.getMvccVersion(),
			newCalendarBooking.getMvccVersion());
		Assert.assertEquals(
			existingCalendarBooking.getCtCollectionId(),
			newCalendarBooking.getCtCollectionId());
		Assert.assertEquals(
			existingCalendarBooking.getUuid(), newCalendarBooking.getUuid());
		Assert.assertEquals(
			existingCalendarBooking.getExternalReferenceCode(),
			newCalendarBooking.getExternalReferenceCode());
		Assert.assertEquals(
			existingCalendarBooking.getCalendarBookingId(),
			newCalendarBooking.getCalendarBookingId());
		Assert.assertEquals(
			existingCalendarBooking.getGroupId(),
			newCalendarBooking.getGroupId());
		Assert.assertEquals(
			existingCalendarBooking.getCompanyId(),
			newCalendarBooking.getCompanyId());
		Assert.assertEquals(
			existingCalendarBooking.getUserId(),
			newCalendarBooking.getUserId());
		Assert.assertEquals(
			existingCalendarBooking.getUserName(),
			newCalendarBooking.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendarBooking.getCreateDate()),
			Time.getShortTimestamp(newCalendarBooking.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendarBooking.getModifiedDate()),
			Time.getShortTimestamp(newCalendarBooking.getModifiedDate()));
		Assert.assertEquals(
			existingCalendarBooking.getCalendarId(),
			newCalendarBooking.getCalendarId());
		Assert.assertEquals(
			existingCalendarBooking.getCalendarResourceId(),
			newCalendarBooking.getCalendarResourceId());
		Assert.assertEquals(
			existingCalendarBooking.getParentCalendarBookingId(),
			newCalendarBooking.getParentCalendarBookingId());
		Assert.assertEquals(
			existingCalendarBooking.getRecurringCalendarBookingId(),
			newCalendarBooking.getRecurringCalendarBookingId());
		Assert.assertEquals(
			existingCalendarBooking.getVEventUid(),
			newCalendarBooking.getVEventUid());
		Assert.assertEquals(
			existingCalendarBooking.getTitle(), newCalendarBooking.getTitle());
		Assert.assertEquals(
			existingCalendarBooking.getDescription(),
			newCalendarBooking.getDescription());
		Assert.assertEquals(
			existingCalendarBooking.getLocation(),
			newCalendarBooking.getLocation());
		Assert.assertEquals(
			existingCalendarBooking.getStartTime(),
			newCalendarBooking.getStartTime());
		Assert.assertEquals(
			existingCalendarBooking.getEndTime(),
			newCalendarBooking.getEndTime());
		Assert.assertEquals(
			existingCalendarBooking.isAllDay(), newCalendarBooking.isAllDay());
		Assert.assertEquals(
			existingCalendarBooking.getRecurrence(),
			newCalendarBooking.getRecurrence());
		Assert.assertEquals(
			existingCalendarBooking.getFirstReminder(),
			newCalendarBooking.getFirstReminder());
		Assert.assertEquals(
			existingCalendarBooking.getFirstReminderType(),
			newCalendarBooking.getFirstReminderType());
		Assert.assertEquals(
			existingCalendarBooking.getSecondReminder(),
			newCalendarBooking.getSecondReminder());
		Assert.assertEquals(
			existingCalendarBooking.getSecondReminderType(),
			newCalendarBooking.getSecondReminderType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCalendarBooking.getLastPublishDate()),
			Time.getShortTimestamp(newCalendarBooking.getLastPublishDate()));
		Assert.assertEquals(
			existingCalendarBooking.getStatus(),
			newCalendarBooking.getStatus());
		Assert.assertEquals(
			existingCalendarBooking.getStatusByUserId(),
			newCalendarBooking.getStatusByUserId());
		Assert.assertEquals(
			existingCalendarBooking.getStatusByUserName(),
			newCalendarBooking.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendarBooking.getStatusDate()),
			Time.getShortTimestamp(newCalendarBooking.getStatusDate()));
	}

	@Test(
		expected = DuplicateCalendarBookingExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CalendarBooking calendarBooking = addCalendarBooking();

		CalendarBooking newCalendarBooking = addCalendarBooking();

		newCalendarBooking.setGroupId(calendarBooking.getGroupId());

		newCalendarBooking = _persistence.update(newCalendarBooking);

		Session session = _persistence.getCurrentSession();

		session.evict(newCalendarBooking);

		newCalendarBooking.setExternalReferenceCode(
			calendarBooking.getExternalReferenceCode());

		_persistence.update(newCalendarBooking);
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
	public void testCountByCalendarId() throws Exception {
		_persistence.countByCalendarId(RandomTestUtil.nextLong());

		_persistence.countByCalendarId(0L);
	}

	@Test
	public void testCountByCalendarResourceId() throws Exception {
		_persistence.countByCalendarResourceId(RandomTestUtil.nextLong());

		_persistence.countByCalendarResourceId(0L);
	}

	@Test
	public void testCountByParentCalendarBookingId() throws Exception {
		_persistence.countByParentCalendarBookingId(RandomTestUtil.nextLong());

		_persistence.countByParentCalendarBookingId(0L);
	}

	@Test
	public void testCountByRecurringCalendarBookingId() throws Exception {
		_persistence.countByRecurringCalendarBookingId(
			RandomTestUtil.nextLong());

		_persistence.countByRecurringCalendarBookingId(0L);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_P(0L, 0L);
	}

	@Test
	public void testCountByC_V() throws Exception {
		_persistence.countByC_V(RandomTestUtil.nextLong(), "");

		_persistence.countByC_V(0L, "null");

		_persistence.countByC_V(0L, (String)null);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByC_SArrayable() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByP_S() throws Exception {
		_persistence.countByP_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByP_S(0L, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		CalendarBooking existingCalendarBooking = _persistence.findByPrimaryKey(
			newCalendarBooking.getPrimaryKey());

		Assert.assertEquals(existingCalendarBooking, newCalendarBooking);
	}

	@Test(expected = NoSuchBookingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CalendarBooking> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CalendarBooking", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "calendarBookingId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"calendarId", true, "calendarResourceId", true,
			"parentCalendarBookingId", true, "recurringCalendarBookingId", true,
			"vEventUid", true, "title", true, "location", true, "startTime",
			true, "endTime", true, "allDay", true, "recurrence", true,
			"firstReminder", true, "firstReminderType", true, "secondReminder",
			true, "secondReminderType", true, "lastPublishDate", true, "status",
			true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		CalendarBooking existingCalendarBooking =
			_persistence.fetchByPrimaryKey(newCalendarBooking.getPrimaryKey());

		Assert.assertEquals(existingCalendarBooking, newCalendarBooking);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarBooking missingCalendarBooking = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCalendarBooking);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CalendarBooking newCalendarBooking1 = addCalendarBooking();
		CalendarBooking newCalendarBooking2 = addCalendarBooking();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarBooking1.getPrimaryKey());
		primaryKeys.add(newCalendarBooking2.getPrimaryKey());

		Map<Serializable, CalendarBooking> calendarBookings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, calendarBookings.size());
		Assert.assertEquals(
			newCalendarBooking1,
			calendarBookings.get(newCalendarBooking1.getPrimaryKey()));
		Assert.assertEquals(
			newCalendarBooking2,
			calendarBookings.get(newCalendarBooking2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CalendarBooking> calendarBookings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(calendarBookings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CalendarBooking newCalendarBooking = addCalendarBooking();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarBooking.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CalendarBooking> calendarBookings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, calendarBookings.size());
		Assert.assertEquals(
			newCalendarBooking,
			calendarBookings.get(newCalendarBooking.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CalendarBooking> calendarBookings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(calendarBookings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarBooking.getPrimaryKey());

		Map<Serializable, CalendarBooking> calendarBookings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, calendarBookings.size());
		Assert.assertEquals(
			newCalendarBooking,
			calendarBookings.get(newCalendarBooking.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CalendarBookingLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CalendarBooking>() {

				@Override
				public void performAction(CalendarBooking calendarBooking) {
					Assert.assertNotNull(calendarBooking);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"calendarBookingId",
				newCalendarBooking.getCalendarBookingId()));

		List<CalendarBooking> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CalendarBooking existingCalendarBooking = result.get(0);

		Assert.assertEquals(existingCalendarBooking, newCalendarBooking);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"calendarBookingId", RandomTestUtil.nextLong()));

		List<CalendarBooking> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("calendarBookingId"));

		Object newCalendarBookingId = newCalendarBooking.getCalendarBookingId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"calendarBookingId", new Object[] {newCalendarBookingId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCalendarBookingId = result.get(0);

		Assert.assertEquals(existingCalendarBookingId, newCalendarBookingId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("calendarBookingId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"calendarBookingId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CalendarBooking newCalendarBooking = addCalendarBooking();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCalendarBooking.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		CalendarBooking newCalendarBooking = addCalendarBooking();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CalendarBooking.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"calendarBookingId",
				newCalendarBooking.getCalendarBookingId()));

		List<CalendarBooking> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CalendarBooking calendarBooking) {
		Assert.assertEquals(
			calendarBooking.getUuid(),
			ReflectionTestUtil.invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(calendarBooking.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(calendarBooking.getCalendarId()),
			ReflectionTestUtil.<Long>invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "calendarId"));
		Assert.assertEquals(
			Long.valueOf(calendarBooking.getParentCalendarBookingId()),
			ReflectionTestUtil.<Long>invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentCalendarBookingId"));

		Assert.assertEquals(
			Long.valueOf(calendarBooking.getCalendarId()),
			ReflectionTestUtil.<Long>invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "calendarId"));
		Assert.assertEquals(
			calendarBooking.getVEventUid(),
			ReflectionTestUtil.invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "vEventUid"));

		Assert.assertEquals(
			calendarBooking.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(calendarBooking.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				calendarBooking, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected CalendarBooking addCalendarBooking() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarBooking calendarBooking = _persistence.create(pk);

		calendarBooking.setMvccVersion(RandomTestUtil.nextLong());

		calendarBooking.setCtCollectionId(RandomTestUtil.nextLong());

		calendarBooking.setUuid(RandomTestUtil.randomString());

		calendarBooking.setExternalReferenceCode(RandomTestUtil.randomString());

		calendarBooking.setGroupId(RandomTestUtil.nextLong());

		calendarBooking.setCompanyId(RandomTestUtil.nextLong());

		calendarBooking.setUserId(RandomTestUtil.nextLong());

		calendarBooking.setUserName(RandomTestUtil.randomString());

		calendarBooking.setCreateDate(RandomTestUtil.nextDate());

		calendarBooking.setModifiedDate(RandomTestUtil.nextDate());

		calendarBooking.setCalendarId(RandomTestUtil.nextLong());

		calendarBooking.setCalendarResourceId(RandomTestUtil.nextLong());

		calendarBooking.setParentCalendarBookingId(RandomTestUtil.nextLong());

		calendarBooking.setRecurringCalendarBookingId(
			RandomTestUtil.nextLong());

		calendarBooking.setVEventUid(RandomTestUtil.randomString());

		calendarBooking.setTitle(RandomTestUtil.randomString());

		calendarBooking.setDescription(RandomTestUtil.randomString());

		calendarBooking.setLocation(RandomTestUtil.randomString());

		calendarBooking.setStartTime(RandomTestUtil.nextLong());

		calendarBooking.setEndTime(RandomTestUtil.nextLong());

		calendarBooking.setAllDay(RandomTestUtil.randomBoolean());

		calendarBooking.setRecurrence(RandomTestUtil.randomString());

		calendarBooking.setFirstReminder(RandomTestUtil.nextLong());

		calendarBooking.setFirstReminderType(RandomTestUtil.randomString());

		calendarBooking.setSecondReminder(RandomTestUtil.nextLong());

		calendarBooking.setSecondReminderType(RandomTestUtil.randomString());

		calendarBooking.setLastPublishDate(RandomTestUtil.nextDate());

		calendarBooking.setStatus(RandomTestUtil.nextInt());

		calendarBooking.setStatusByUserId(RandomTestUtil.nextLong());

		calendarBooking.setStatusByUserName(RandomTestUtil.randomString());

		calendarBooking.setStatusDate(RandomTestUtil.nextDate());

		_calendarBookings.add(_persistence.update(calendarBooking));

		return calendarBooking;
	}

	private List<CalendarBooking> _calendarBookings =
		new ArrayList<CalendarBooking>();
	private CalendarBookingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}