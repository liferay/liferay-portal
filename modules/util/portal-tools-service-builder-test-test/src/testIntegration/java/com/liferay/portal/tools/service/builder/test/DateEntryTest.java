/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.sql.dsl.DSLFunctionFactoryUtil;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionList;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Type;
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
import com.liferay.portal.tools.service.builder.test.model.DateEntryTable;
import com.liferay.portal.tools.service.builder.test.service.DateEntryLocalService;
import com.liferay.portal.tools.service.builder.test.service.persistence.DateEntryPersistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
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
		_microsDateEntryId = _addDateEntry(
			_COMPANY_ID, _MILLIS_TIME, _MICROSECONDS);
		_microsNanos = _toNanos(_MILLIS_TIME, _MICROSECONDS);

		_midnightTime = _MILLIS_TIME - (_MILLIS_TIME % Time.DAY);

		_midnightDateEntryId = _addDateEntry(_COMPANY_ID, _midnightTime, 0);
		_midnightNanos = _toNanos(_midnightTime, 0);

		_millisDateEntryId = _addDateEntry(_COMPANY_ID, _MILLIS_TIME, 0);
		_millisNanos = _toNanos(_MILLIS_TIME, 0);

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

			Timestamp timestamp = _toTimestamp(_MILLIS_TIME, _MICROSECONDS);

			dateEntry.setSnapshotDate(timestamp);

			dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

			_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

			dateEntry = _dateEntryLocalService.createDateEntry(dateEntryId2);

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry.setSnapshotDate(new Date(_MILLIS_TIME));

			dateEntry = _dateEntryLocalService.addDateEntry(dateEntry);

			_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());
		}
		finally {
			_dateEntryLocalService.deleteDateEntry(dateEntryId1);
			_dateEntryLocalService.deleteDateEntry(dateEntryId2);
		}
	}

	@Test
	public void testDSLQuery() {
		List<Object> results = _dateEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				DSLFunctionFactoryUtil.max(
					DateEntryTable.INSTANCE.snapshotDate
				).as(
					"maxSnapshotDate"
				)
			).from(
				DateEntryTable.INSTANCE
			).where(
				DateEntryTable.INSTANCE.companyId.eq(_COMPANY_ID)
			));

		Assert.assertEquals(results.toString(), 1, results.size());

		_assertSQLDate(_midnightTime, results.get(0));

		_assertSQLDateRows(
			_dateEntryLocalService.dslQuery(
				DSLQueryFactoryUtil.select(
					DateEntryTable.INSTANCE.dateEntryId,
					DateEntryTable.INSTANCE.snapshotDate
				).from(
					DateEntryTable.INSTANCE
				).where(
					DateEntryTable.INSTANCE.companyId.eq(_COMPANY_ID)
				).orderBy(
					DateEntryTable.INSTANCE.snapshotDate.ascending()
				)));
	}

	@Test
	public void testDynamicQuery() {
		DynamicQuery dynamicQuery = _createDynamicQuery();

		dynamicQuery.setProjection(ProjectionFactoryUtil.max("snapshotDate"));

		List<Object> results = _dateEntryLocalService.dynamicQuery(
			dynamicQuery);

		Assert.assertEquals(results.toString(), 1, results.size());

		_assertTimestamp(_microsNanos, results.get(0));

		dynamicQuery = _createDynamicQuery();

		dynamicQuery.addOrder(OrderFactoryUtil.asc("snapshotDate"));

		ProjectionList projectionList = ProjectionFactoryUtil.projectionList();

		projectionList.add(ProjectionFactoryUtil.property("dateEntryId"));
		projectionList.add(ProjectionFactoryUtil.property("snapshotDate"));

		dynamicQuery.setProjection(projectionList);

		_assertTimestampRows(_dateEntryLocalService.dynamicQuery(dynamicQuery));
	}

	@Test
	public void testFetchDateEntry() {
		DateEntry dateEntry = _dateEntryLocalService.fetchDateEntry(
			_COMPANY_ID, new Date(_MILLIS_TIME));

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_millisNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_COMPANY_ID, new Date(_MILLIS_TIME));

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_COMPANY_ID, _toTimestamp(_MILLIS_TIME, _MICROSECONDS));

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(
			_COMPANY_ID, _toTimestamp(_MILLIS_TIME, _MICROSECONDS));

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				BasePersistenceImpl.class.getName(), LoggerTestUtil.OFF)) {

			_dateEntryLocalService.fetchDateEntry(
				_COMPANY_ID, new java.sql.Date(_midnightTime));

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

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntry = _dateEntryLocalService.fetchDateEntry(_microsDateEntryId);

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntry = _dateEntryLocalService.fetchDateEntry(_microsDateEntryId);

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());
	}

	@Test
	public void testGetDateEntries() {
		List<DateEntry> dateEntries = _dateEntryLocalService.getDateEntries(
			new Date(_MILLIS_TIME));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		DateEntry dateEntry = dateEntries.get(0);

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_millisNanos, dateEntry.getSnapshotDate());

		dateEntries = _dateEntryLocalService.getDateEntries(
			new Date(_MILLIS_TIME));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_millisDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());

		_dateEntryPersistence.clearCache();

		dateEntries = _dateEntryLocalService.getDateEntries(
			_toTimestamp(_MILLIS_TIME, _MICROSECONDS));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertTimestamp(_microsNanos, dateEntry.getSnapshotDate());

		dateEntries = _dateEntryLocalService.getDateEntries(
			_toTimestamp(_MILLIS_TIME, _MICROSECONDS));

		Assert.assertEquals(dateEntries.toString(), 1, dateEntries.size());

		dateEntry = dateEntries.get(0);

		Assert.assertEquals(_microsDateEntryId, dateEntry.getDateEntryId());

		_assertDate(_MILLIS_TIME, dateEntry.getSnapshotDate());
	}

	@Test
	public void testJDBCQuery() throws Exception {
		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select max(snapshotDate) as maxSnapshotDate from DateEntry " +
					"where companyId = ?")) {

			preparedStatement.setLong(1, _COMPANY_ID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				Assert.assertTrue(resultSet.next());

				_assertTimestamp(
					_microsNanos, resultSet.getTimestamp("maxSnapshotDate"));
			}
		}

		try (Connection connection = DataAccess.getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(
				"select dateEntryId, snapshotDate from DateEntry where " +
					"companyId = ? order by snapshotDate")) {

			preparedStatement.setLong(1, _COMPANY_ID);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				List<Object[]> rows = new ArrayList<>();

				while (resultSet.next()) {
					rows.add(
						new Object[] {
							resultSet.getLong("dateEntryId"),
							resultSet.getTimestamp("snapshotDate")
						});
				}

				_assertTimestampRows(rows);
			}
		}
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
	public void testSQLQuery() {
		_assertTimestamp(_microsNanos, _getMaxSnapshotDateBySQLQuery(null));
		_assertSQLDate(_midnightTime, _getMaxSnapshotDateBySQLQuery(Type.DATE));
		_assertTimestamp(
			_microsNanos, _getMaxSnapshotDateBySQLQuery(Type.TIMESTAMP));

		_assertTimestampRows(
			_dateEntryLocalService.getDateEntriesBySQLQuery(_COMPANY_ID, null));
		_assertSQLDateRows(
			_dateEntryLocalService.getDateEntriesBySQLQuery(
				_COMPANY_ID, Type.DATE));
		_assertTimestampRows(
			_dateEntryLocalService.getDateEntriesBySQLQuery(
				_COMPANY_ID, Type.TIMESTAMP));
	}

	@Test
	public void testUpdateDateEntry() throws Exception {
		long dateEntryId = _addDateEntry(
			RandomTestUtil.nextLong(), _MILLIS_TIME, _MICROSECONDS);

		try {
			_dateEntryPersistence.clearCache();

			DateEntry dateEntry = _dateEntryLocalService.fetchDateEntry(
				dateEntryId);

			Timestamp timestamp = _toTimestamp(_MILLIS_TIME + 1, _MICROSECONDS);

			dateEntry.setSnapshotDate(timestamp);

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertTimestamp(timestamp.getNanos(), dateEntry.getSnapshotDate());

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertTimestamp(timestamp.getNanos(), dateEntry.getSnapshotDate());

			dateEntry.setSnapshotDate(new Date(_MILLIS_TIME + 2));

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertDate(_MILLIS_TIME + 2, dateEntry.getSnapshotDate());

			dateEntry.setCompanyId(RandomTestUtil.nextLong());

			dateEntry = _dateEntryLocalService.updateDateEntry(dateEntry);

			_assertDate(_MILLIS_TIME + 2, dateEntry.getSnapshotDate());
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

	private void _assertDate(long expectedTime, Object object) {
		Assert.assertEquals(Date.class, object.getClass());

		Date date = (Date)object;

		Assert.assertEquals(expectedTime, date.getTime());
	}

	private void _assertSQLDate(long expectedTime, Object object) {
		Assert.assertEquals(java.sql.Date.class, object.getClass());

		Date date = (Date)object;

		Assert.assertEquals(expectedTime, date.getTime());
	}

	private void _assertSQLDateRow(long expectedDateEntryId, Object[] row) {
		Number number = (Number)row[0];

		Assert.assertEquals(expectedDateEntryId, number.longValue());

		_assertSQLDate(_midnightTime, row[1]);
	}

	private void _assertSQLDateRows(List<Object[]> rows) {
		Assert.assertEquals(rows.toString(), 3, rows.size());

		_assertSQLDateRow(_midnightDateEntryId, rows.get(0));
		_assertSQLDateRow(_millisDateEntryId, rows.get(1));
		_assertSQLDateRow(_microsDateEntryId, rows.get(2));
	}

	private void _assertTimestamp(long expectedNanos, Object object) {
		Assert.assertEquals(Timestamp.class, object.getClass());

		Timestamp timestamp = (Timestamp)object;

		Assert.assertEquals(expectedNanos, timestamp.getNanos());
	}

	private void _assertTimestampRow(
		long expectedDateEntryId, long expectedNanos, Object[] row) {

		Number number = (Number)row[0];

		Assert.assertEquals(expectedDateEntryId, number.longValue());

		_assertTimestamp(expectedNanos, row[1]);
	}

	private void _assertTimestampRows(List<Object[]> rows) {
		Assert.assertEquals(rows.toString(), 3, rows.size());

		_assertTimestampRow(_midnightDateEntryId, _midnightNanos, rows.get(0));
		_assertTimestampRow(_millisDateEntryId, _millisNanos, rows.get(1));
		_assertTimestampRow(_microsDateEntryId, _microsNanos, rows.get(2));
	}

	private DynamicQuery _createDynamicQuery() {
		Class<?> clazz = _dateEntryLocalService.getClass();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DateEntry.class, clazz.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("companyId", _COMPANY_ID));

		return dynamicQuery;
	}

	private Object _getMaxSnapshotDateBySQLQuery(Type type) {
		List<Object> results =
			_dateEntryLocalService.getMaxSnapshotDatesBySQLQuery(
				_COMPANY_ID, type);

		Assert.assertEquals(results.toString(), 1, results.size());

		return results.get(0);
	}

	private static final long _COMPANY_ID = RandomTestUtil.nextLong();

	private static final int _MICROSECONDS = RandomTestUtil.randomInt(500, 999);

	private static final long _MILLIS_TIME = System.currentTimeMillis();

	@Inject
	private static DateEntryLocalService _dateEntryLocalService;

	@Inject
	private static DateEntryPersistence _dateEntryPersistence;

	private static long _microsDateEntryId;
	private static int _microsNanos;
	private static long _midnightDateEntryId;
	private static int _midnightNanos;
	private static long _midnightTime;
	private static long _millisDateEntryId;
	private static int _millisNanos;

}