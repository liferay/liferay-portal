/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.tools.service.builder.test.model.DateEntry;
import com.liferay.portal.tools.service.builder.test.service.DateEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Tina Tian
 */
@RunWith(Arquillian.class)
public class DateEntryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_companyId = RandomTestUtil.nextLong();
		_microseconds = RandomTestUtil.randomInt(500, 999);

		_millisTime = System.currentTimeMillis();

		_midnightTime = _millisTime - (_millisTime % Time.DAY);

		_microsNanos = _toNanos(_millisTime, _microseconds);
		_millisNanos = _toNanos(_millisTime, 0);

		_midnightDateEntryId = _addDateEntry(_companyId, _midnightTime, 0);

		_millisDateEntryId = _addDateEntry(_companyId, _millisTime, 0);
		_microsDateEntryId = _addDateEntry(
			_companyId, _millisTime, _microseconds);

		_dateEntryPersistence.clearCache();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_dateEntryLocalService.deleteDateEntry(_microsDateEntryId);
		_dateEntryLocalService.deleteDateEntry(_midnightDateEntryId);
		_dateEntryLocalService.deleteDateEntry(_millisDateEntryId);
	}

	@Test
	public void testAddDateEntry() throws Exception {
		long dateEntryId1 = RandomTestUtil.nextLong();
		long dateEntryId2 = RandomTestUtil.nextLong();

		try {
			DateEntry dateEntry = _dateEntryLocalService.createDateEntry(
				dateEntryId1);

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			Timestamp timestamp = _toTimestamp(_millisTime, _microseconds);

			dateEntry.setSnapshotDate(timestamp);

			dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

			_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

			dateEntry = _dateEntryLocalService.createDateEntry(dateEntryId2);

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry.setSnapshotDate(new Date(_millisTime));

			dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

			_assertDate(_millisTime, dateEntry.getSnapshotDate());
		}
		finally {
			_dateEntryLocalService.deleteDateEntry(dateEntryId1);
			_dateEntryLocalService.deleteDateEntry(dateEntryId2);
		}
	}

	@Test
	public void testFetchDateEntry() {
		DateEntry dateEntry = _dateEntryLocalService.fetchDateEntry(
			_companyId, new Date(_millisTime));

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_millisNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_companyId, new Date(_millisTime));

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_millisTime, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_companyId, _toTimestamp(_millisTime, _microseconds));

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_companyId, _toTimestamp(_millisTime, _microseconds));

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_millisTime, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BasePersistenceImpl.class.getName(), LoggerTestUtil.OFF)) {

			_dateEntryLocalService.fetchDateEntry(
				_companyId, new java.sql.Date(_midnightTime));

			Assert.fail();
		}
		catch (SystemException systemException) {
			Throwable throwable = systemException.getCause();

			Assert.assertEquals(
				"Unsupport type java.sql.Date", throwable.getMessage());
		}
	}

	@Test
	public void testFetchDateEntryById() {
		DateEntry dateEntry = _dateEntryLocalService.fetchDateEntry(
			_millisDateEntryId);

		_assertTimestamp(_millisNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(_millisDateEntryId);

		_assertDate(_millisTime, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.fetchDateEntry(_microsDateEntryId);

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(_microsDateEntryId);

		_assertDate(_millisTime, dateEntry.getSnapshotDate());
	}

	@Test
	public void testGetDateEntries() {
		List<DateEntry> dateEntries = _dateEntryLocalService.getDateEntries(
			new Date(_millisTime));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		DateEntry dateEntry = dateEntries.get(0);

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_millisNanos, dateEntry.getSnapshotDate());

		dateEntries = _dateEntryLocalService.getDateEntries(
			new Date(_millisTime));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_millisTime, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntries = _dateEntryLocalService.getDateEntries(
			_toTimestamp(_millisTime, _microseconds));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntries = _dateEntryLocalService.getDateEntries(
			_toTimestamp(_millisTime, _microseconds));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_millisTime, dateEntry.getSnapshotDate());
	}

	@Test
	public void testNullDate() throws Exception {
		long companyId = RandomTestUtil.nextLong();
		long dateEntryId = RandomTestUtil.nextLong();

		try {
			DateEntry dateEntry = _dateEntryLocalService.createDateEntry(
				dateEntryId);

			dateEntry.setCompanyId(companyId);
			dateEntry.setSnapshotDate(null);

			dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

			Assert.assertNull(dateEntry.getSnapshotDate());

			_dateEntryPersistence.clearCache();

			dateEntry = _dateEntryLocalService.fetchDateEntry(dateEntryId);

			Assert.assertNull(dateEntry.getSnapshotDate());

			_dateEntryPersistence.clearCache();

			dateEntry = _dateEntryLocalService.fetchDateEntry(companyId, null);

			Assert.assertEquals(dateEntryId, dateEntry.getDateEntryId());
			Assert.assertNull(dateEntry.getSnapshotDate());

			dateEntry = _dateEntryLocalService.fetchDateEntry(companyId, null);

			Assert.assertEquals(dateEntryId, dateEntry.getDateEntryId());
			Assert.assertNull(dateEntry.getSnapshotDate());
		}
		finally {
			_dateEntryLocalService.deleteDateEntry(dateEntryId);
		}
	}

	@Test
	public void testUpdateDateEntry() throws Exception {
		long dateEntryId = _addDateEntry(
			RandomTestUtil.nextLong(), _millisTime, _microseconds);

		try {
			_dateEntryPersistence.clearCache();

			DateEntry dateEntry = _dateEntryLocalService.fetchDateEntry(
				dateEntryId);

			Timestamp timestamp = _toTimestamp(_millisTime + 1, _microseconds);

			dateEntry.setSnapshotDate(timestamp);

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertTimestamp(timestamp.getNanos(), dateEntry.getSnapshotDate());

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertTimestamp(timestamp.getNanos(), dateEntry.getSnapshotDate());

			dateEntry.setSnapshotDate(new Date(_millisTime + 2));

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertDate(_millisTime + 2, dateEntry.getSnapshotDate());

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertDate(_millisTime + 2, dateEntry.getSnapshotDate());
		}
		finally {
			_dateEntryLocalService.deleteDateEntry(dateEntryId);
		}
	}

	private static long _addDateEntry(
			long companyId, long time, int microseconds)
		throws Exception {

		long dateEntryId = RandomTestUtil.nextLong();

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"insert into DateEntry (dateEntryId, companyId, " +
					"snapshotDate) values (?, ?, ?)")) {

			preparedStatement.setLong(1, dateEntryId);
			preparedStatement.setLong(2, companyId);
			preparedStatement.setTimestamp(3, _toTimestamp(time, microseconds));

			preparedStatement.executeUpdate();
		}

		return dateEntryId;
	}

	private static int _toNanos(long time, int microseconds) {
		Timestamp timestamp = _toTimestamp(time, microseconds);

		return timestamp.getNanos();
	}

	private static Timestamp _toTimestamp(long time, int microseconds) {
		Timestamp timestamp = new Timestamp(time);

		timestamp.setNanos(timestamp.getNanos() + (microseconds * 1000));

		return timestamp;
	}

	private void _assertDate(long expectedTime, Date date) {
		Assert.assertEquals(Date.class, date.getClass());
		Assert.assertEquals(expectedTime, date.getTime());
	}

	private void _assertTimestamp(long expectedNanos, Date date) {
		Assert.assertEquals(Timestamp.class, date.getClass());

		Timestamp timestamp = (Timestamp)date;

		Assert.assertEquals(expectedNanos, timestamp.getNanos());
	}

	private static long _companyId;

	@Inject
	private static DateEntryLocalService _dateEntryLocalService;

	@Inject
	private static DateEntryPersistence _dateEntryPersistence;

	private static long _microsDateEntryId;
	private static int _microseconds;
	private static int _microsNanos;
	private static long _midnightDateEntryId;
	private static long _midnightTime;
	private static long _millisDateEntryId;
	private static int _millisNanos;
	private static long _millisTime;

}