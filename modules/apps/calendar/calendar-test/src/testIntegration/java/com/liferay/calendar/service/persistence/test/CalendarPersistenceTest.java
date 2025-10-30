/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.exception.NoSuchCalendarException;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.service.persistence.CalendarPersistence;
import com.liferay.calendar.service.persistence.CalendarUtil;
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
public class CalendarPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.calendar.service"));

	@Before
	public void setUp() {
		_persistence = CalendarUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Calendar> iterator = _calendars.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Calendar calendar = _persistence.create(pk);

		Assert.assertNotNull(calendar);

		Assert.assertEquals(calendar.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Calendar newCalendar = addCalendar();

		_persistence.remove(newCalendar);

		Calendar existingCalendar = _persistence.fetchByPrimaryKey(
			newCalendar.getPrimaryKey());

		Assert.assertNull(existingCalendar);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCalendar();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Calendar newCalendar = _persistence.create(pk);

		newCalendar.setMvccVersion(RandomTestUtil.nextLong());

		newCalendar.setCtCollectionId(RandomTestUtil.nextLong());

		newCalendar.setUuid(RandomTestUtil.randomString());

		newCalendar.setGroupId(RandomTestUtil.nextLong());

		newCalendar.setCompanyId(RandomTestUtil.nextLong());

		newCalendar.setUserId(RandomTestUtil.nextLong());

		newCalendar.setUserName(RandomTestUtil.randomString());

		newCalendar.setCreateDate(RandomTestUtil.nextDate());

		newCalendar.setModifiedDate(RandomTestUtil.nextDate());

		newCalendar.setCalendarResourceId(RandomTestUtil.nextLong());

		newCalendar.setName(RandomTestUtil.randomString());

		newCalendar.setDescription(RandomTestUtil.randomString());

		newCalendar.setTimeZoneId(RandomTestUtil.randomString());

		newCalendar.setColor(RandomTestUtil.nextInt());

		newCalendar.setDefaultCalendar(RandomTestUtil.randomBoolean());

		newCalendar.setEnableComments(RandomTestUtil.randomBoolean());

		newCalendar.setEnableRatings(RandomTestUtil.randomBoolean());

		newCalendar.setLastPublishDate(RandomTestUtil.nextDate());

		_calendars.add(_persistence.update(newCalendar));

		Calendar existingCalendar = _persistence.findByPrimaryKey(
			newCalendar.getPrimaryKey());

		Assert.assertEquals(
			existingCalendar.getMvccVersion(), newCalendar.getMvccVersion());
		Assert.assertEquals(
			existingCalendar.getCtCollectionId(),
			newCalendar.getCtCollectionId());
		Assert.assertEquals(existingCalendar.getUuid(), newCalendar.getUuid());
		Assert.assertEquals(
			existingCalendar.getCalendarId(), newCalendar.getCalendarId());
		Assert.assertEquals(
			existingCalendar.getGroupId(), newCalendar.getGroupId());
		Assert.assertEquals(
			existingCalendar.getCompanyId(), newCalendar.getCompanyId());
		Assert.assertEquals(
			existingCalendar.getUserId(), newCalendar.getUserId());
		Assert.assertEquals(
			existingCalendar.getUserName(), newCalendar.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendar.getCreateDate()),
			Time.getShortTimestamp(newCalendar.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendar.getModifiedDate()),
			Time.getShortTimestamp(newCalendar.getModifiedDate()));
		Assert.assertEquals(
			existingCalendar.getCalendarResourceId(),
			newCalendar.getCalendarResourceId());
		Assert.assertEquals(existingCalendar.getName(), newCalendar.getName());
		Assert.assertEquals(
			existingCalendar.getDescription(), newCalendar.getDescription());
		Assert.assertEquals(
			existingCalendar.getTimeZoneId(), newCalendar.getTimeZoneId());
		Assert.assertEquals(
			existingCalendar.getColor(), newCalendar.getColor());
		Assert.assertEquals(
			existingCalendar.isDefaultCalendar(),
			newCalendar.isDefaultCalendar());
		Assert.assertEquals(
			existingCalendar.isEnableComments(),
			newCalendar.isEnableComments());
		Assert.assertEquals(
			existingCalendar.isEnableRatings(), newCalendar.isEnableRatings());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCalendar.getLastPublishDate()),
			Time.getShortTimestamp(newCalendar.getLastPublishDate()));
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
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_C_D() throws Exception {
		_persistence.countByG_C_D(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_C_D(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Calendar newCalendar = addCalendar();

		Calendar existingCalendar = _persistence.findByPrimaryKey(
			newCalendar.getPrimaryKey());

		Assert.assertEquals(existingCalendar, newCalendar);
	}

	@Test(expected = NoSuchCalendarException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Calendar> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Calendar", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "calendarId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "calendarResourceId", true, "name", true,
			"description", true, "timeZoneId", true, "color", true,
			"defaultCalendar", true, "enableComments", true, "enableRatings",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Calendar newCalendar = addCalendar();

		Calendar existingCalendar = _persistence.fetchByPrimaryKey(
			newCalendar.getPrimaryKey());

		Assert.assertEquals(existingCalendar, newCalendar);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Calendar missingCalendar = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCalendar);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Calendar newCalendar1 = addCalendar();
		Calendar newCalendar2 = addCalendar();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendar1.getPrimaryKey());
		primaryKeys.add(newCalendar2.getPrimaryKey());

		Map<Serializable, Calendar> calendars = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, calendars.size());
		Assert.assertEquals(
			newCalendar1, calendars.get(newCalendar1.getPrimaryKey()));
		Assert.assertEquals(
			newCalendar2, calendars.get(newCalendar2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Calendar> calendars = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(calendars.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Calendar newCalendar = addCalendar();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendar.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Calendar> calendars = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, calendars.size());
		Assert.assertEquals(
			newCalendar, calendars.get(newCalendar.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Calendar> calendars = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(calendars.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Calendar newCalendar = addCalendar();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendar.getPrimaryKey());

		Map<Serializable, Calendar> calendars = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, calendars.size());
		Assert.assertEquals(
			newCalendar, calendars.get(newCalendar.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Calendar newCalendar = addCalendar();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCalendar.getPrimaryKey()));
	}

	private void _assertOriginalValues(Calendar calendar) {
		Assert.assertEquals(
			calendar.getUuid(),
			ReflectionTestUtil.invoke(
				calendar, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(calendar.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				calendar, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected Calendar addCalendar() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Calendar calendar = _persistence.create(pk);

		calendar.setMvccVersion(RandomTestUtil.nextLong());

		calendar.setCtCollectionId(RandomTestUtil.nextLong());

		calendar.setUuid(RandomTestUtil.randomString());

		calendar.setGroupId(RandomTestUtil.nextLong());

		calendar.setCompanyId(RandomTestUtil.nextLong());

		calendar.setUserId(RandomTestUtil.nextLong());

		calendar.setUserName(RandomTestUtil.randomString());

		calendar.setCreateDate(RandomTestUtil.nextDate());

		calendar.setModifiedDate(RandomTestUtil.nextDate());

		calendar.setCalendarResourceId(RandomTestUtil.nextLong());

		calendar.setName(RandomTestUtil.randomString());

		calendar.setDescription(RandomTestUtil.randomString());

		calendar.setTimeZoneId(RandomTestUtil.randomString());

		calendar.setColor(RandomTestUtil.nextInt());

		calendar.setDefaultCalendar(RandomTestUtil.randomBoolean());

		calendar.setEnableComments(RandomTestUtil.randomBoolean());

		calendar.setEnableRatings(RandomTestUtil.randomBoolean());

		calendar.setLastPublishDate(RandomTestUtil.nextDate());

		_calendars.add(_persistence.update(calendar));

		return calendar;
	}

	private List<Calendar> _calendars = new ArrayList<Calendar>();
	private CalendarPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}