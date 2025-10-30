/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.calendar.exception.NoSuchNotificationTemplateException;
import com.liferay.calendar.model.CalendarNotificationTemplate;
import com.liferay.calendar.service.persistence.CalendarNotificationTemplatePersistence;
import com.liferay.calendar.service.persistence.CalendarNotificationTemplateUtil;
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
public class CalendarNotificationTemplatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.calendar.service"));

	@Before
	public void setUp() {
		_persistence = CalendarNotificationTemplateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CalendarNotificationTemplate> iterator =
			_calendarNotificationTemplates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarNotificationTemplate calendarNotificationTemplate =
			_persistence.create(pk);

		Assert.assertNotNull(calendarNotificationTemplate);

		Assert.assertEquals(calendarNotificationTemplate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		_persistence.remove(newCalendarNotificationTemplate);

		CalendarNotificationTemplate existingCalendarNotificationTemplate =
			_persistence.fetchByPrimaryKey(
				newCalendarNotificationTemplate.getPrimaryKey());

		Assert.assertNull(existingCalendarNotificationTemplate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCalendarNotificationTemplate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarNotificationTemplate newCalendarNotificationTemplate =
			_persistence.create(pk);

		newCalendarNotificationTemplate.setMvccVersion(
			RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setUuid(RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setGroupId(RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setCompanyId(RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setUserId(RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setUserName(
			RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setCreateDate(
			RandomTestUtil.nextDate());

		newCalendarNotificationTemplate.setModifiedDate(
			RandomTestUtil.nextDate());

		newCalendarNotificationTemplate.setCalendarId(
			RandomTestUtil.nextLong());

		newCalendarNotificationTemplate.setNotificationType(
			RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setNotificationTypeSettings(
			RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setNotificationTemplateType(
			RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setSubject(
			RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setBody(RandomTestUtil.randomString());

		newCalendarNotificationTemplate.setLastPublishDate(
			RandomTestUtil.nextDate());

		_calendarNotificationTemplates.add(
			_persistence.update(newCalendarNotificationTemplate));

		CalendarNotificationTemplate existingCalendarNotificationTemplate =
			_persistence.findByPrimaryKey(
				newCalendarNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCalendarNotificationTemplate.getMvccVersion(),
			newCalendarNotificationTemplate.getMvccVersion());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getCtCollectionId(),
			newCalendarNotificationTemplate.getCtCollectionId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getUuid(),
			newCalendarNotificationTemplate.getUuid());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.
				getCalendarNotificationTemplateId(),
			newCalendarNotificationTemplate.
				getCalendarNotificationTemplateId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getGroupId(),
			newCalendarNotificationTemplate.getGroupId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getCompanyId(),
			newCalendarNotificationTemplate.getCompanyId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getUserId(),
			newCalendarNotificationTemplate.getUserId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getUserName(),
			newCalendarNotificationTemplate.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCalendarNotificationTemplate.getCreateDate()),
			Time.getShortTimestamp(
				newCalendarNotificationTemplate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCalendarNotificationTemplate.getModifiedDate()),
			Time.getShortTimestamp(
				newCalendarNotificationTemplate.getModifiedDate()));
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getCalendarId(),
			newCalendarNotificationTemplate.getCalendarId());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getNotificationType(),
			newCalendarNotificationTemplate.getNotificationType());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getNotificationTypeSettings(),
			newCalendarNotificationTemplate.getNotificationTypeSettings());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getNotificationTemplateType(),
			newCalendarNotificationTemplate.getNotificationTemplateType());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getSubject(),
			newCalendarNotificationTemplate.getSubject());
		Assert.assertEquals(
			existingCalendarNotificationTemplate.getBody(),
			newCalendarNotificationTemplate.getBody());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCalendarNotificationTemplate.getLastPublishDate()),
			Time.getShortTimestamp(
				newCalendarNotificationTemplate.getLastPublishDate()));
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
	public void testCountByC_NT_NTT() throws Exception {
		_persistence.countByC_NT_NTT(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_NT_NTT(0L, "null", "null");

		_persistence.countByC_NT_NTT(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		CalendarNotificationTemplate existingCalendarNotificationTemplate =
			_persistence.findByPrimaryKey(
				newCalendarNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCalendarNotificationTemplate,
			newCalendarNotificationTemplate);
	}

	@Test(expected = NoSuchNotificationTemplateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CalendarNotificationTemplate>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CalendarNotificationTemplate", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"calendarNotificationTemplateId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "calendarId", true, "notificationType",
			true, "notificationTypeSettings", true, "notificationTemplateType",
			true, "subject", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		CalendarNotificationTemplate existingCalendarNotificationTemplate =
			_persistence.fetchByPrimaryKey(
				newCalendarNotificationTemplate.getPrimaryKey());

		Assert.assertEquals(
			existingCalendarNotificationTemplate,
			newCalendarNotificationTemplate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CalendarNotificationTemplate missingCalendarNotificationTemplate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCalendarNotificationTemplate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CalendarNotificationTemplate newCalendarNotificationTemplate1 =
			addCalendarNotificationTemplate();
		CalendarNotificationTemplate newCalendarNotificationTemplate2 =
			addCalendarNotificationTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarNotificationTemplate1.getPrimaryKey());
		primaryKeys.add(newCalendarNotificationTemplate2.getPrimaryKey());

		Map<Serializable, CalendarNotificationTemplate>
			calendarNotificationTemplates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, calendarNotificationTemplates.size());
		Assert.assertEquals(
			newCalendarNotificationTemplate1,
			calendarNotificationTemplates.get(
				newCalendarNotificationTemplate1.getPrimaryKey()));
		Assert.assertEquals(
			newCalendarNotificationTemplate2,
			calendarNotificationTemplates.get(
				newCalendarNotificationTemplate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CalendarNotificationTemplate>
			calendarNotificationTemplates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(calendarNotificationTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarNotificationTemplate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CalendarNotificationTemplate>
			calendarNotificationTemplates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, calendarNotificationTemplates.size());
		Assert.assertEquals(
			newCalendarNotificationTemplate,
			calendarNotificationTemplates.get(
				newCalendarNotificationTemplate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CalendarNotificationTemplate>
			calendarNotificationTemplates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(calendarNotificationTemplates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCalendarNotificationTemplate.getPrimaryKey());

		Map<Serializable, CalendarNotificationTemplate>
			calendarNotificationTemplates = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, calendarNotificationTemplates.size());
		Assert.assertEquals(
			newCalendarNotificationTemplate,
			calendarNotificationTemplates.get(
				newCalendarNotificationTemplate.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CalendarNotificationTemplate newCalendarNotificationTemplate =
			addCalendarNotificationTemplate();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCalendarNotificationTemplate.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CalendarNotificationTemplate calendarNotificationTemplate) {

		Assert.assertEquals(
			calendarNotificationTemplate.getUuid(),
			ReflectionTestUtil.invoke(
				calendarNotificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(calendarNotificationTemplate.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				calendarNotificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(calendarNotificationTemplate.getCalendarId()),
			ReflectionTestUtil.<Long>invoke(
				calendarNotificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "calendarId"));
		Assert.assertEquals(
			calendarNotificationTemplate.getNotificationType(),
			ReflectionTestUtil.invoke(
				calendarNotificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "notificationType"));
		Assert.assertEquals(
			calendarNotificationTemplate.getNotificationTemplateType(),
			ReflectionTestUtil.invoke(
				calendarNotificationTemplate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "notificationTemplateType"));
	}

	protected CalendarNotificationTemplate addCalendarNotificationTemplate()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CalendarNotificationTemplate calendarNotificationTemplate =
			_persistence.create(pk);

		calendarNotificationTemplate.setMvccVersion(RandomTestUtil.nextLong());

		calendarNotificationTemplate.setCtCollectionId(
			RandomTestUtil.nextLong());

		calendarNotificationTemplate.setUuid(RandomTestUtil.randomString());

		calendarNotificationTemplate.setGroupId(RandomTestUtil.nextLong());

		calendarNotificationTemplate.setCompanyId(RandomTestUtil.nextLong());

		calendarNotificationTemplate.setUserId(RandomTestUtil.nextLong());

		calendarNotificationTemplate.setUserName(RandomTestUtil.randomString());

		calendarNotificationTemplate.setCreateDate(RandomTestUtil.nextDate());

		calendarNotificationTemplate.setModifiedDate(RandomTestUtil.nextDate());

		calendarNotificationTemplate.setCalendarId(RandomTestUtil.nextLong());

		calendarNotificationTemplate.setNotificationType(
			RandomTestUtil.randomString());

		calendarNotificationTemplate.setNotificationTypeSettings(
			RandomTestUtil.randomString());

		calendarNotificationTemplate.setNotificationTemplateType(
			RandomTestUtil.randomString());

		calendarNotificationTemplate.setSubject(RandomTestUtil.randomString());

		calendarNotificationTemplate.setBody(RandomTestUtil.randomString());

		calendarNotificationTemplate.setLastPublishDate(
			RandomTestUtil.nextDate());

		_calendarNotificationTemplates.add(
			_persistence.update(calendarNotificationTemplate));

		return calendarNotificationTemplate;
	}

	private List<CalendarNotificationTemplate> _calendarNotificationTemplates =
		new ArrayList<CalendarNotificationTemplate>();
	private CalendarNotificationTemplatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}