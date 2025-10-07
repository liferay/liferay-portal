/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CalendarBooking&quot; database table.
 *
 * @author Eduardo Lundgren
 * @see CalendarBooking
 * @generated
 */
public class CalendarBookingTable extends BaseTable<CalendarBookingTable> {

	public static final CalendarBookingTable INSTANCE =
		new CalendarBookingTable();

	public final Column<CalendarBookingTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CalendarBookingTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CalendarBookingTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> calendarBookingId =
		createColumn(
			"calendarBookingId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CalendarBookingTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> calendarId = createColumn(
		"calendarId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> calendarResourceId =
		createColumn(
			"calendarResourceId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> parentCalendarBookingId =
		createColumn(
			"parentCalendarBookingId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> recurringCalendarBookingId =
		createColumn(
			"recurringCalendarBookingId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> vEventUid = createColumn(
		"vEventUid", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> title = createColumn(
		"title", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Clob> description = createColumn(
		"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> location = createColumn(
		"location", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> startTime = createColumn(
		"startTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> endTime = createColumn(
		"endTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Boolean> allDay = createColumn(
		"allDay", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> recurrence = createColumn(
		"recurrence", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> firstReminder =
		createColumn(
			"firstReminder", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> firstReminderType =
		createColumn(
			"firstReminderType", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> secondReminder =
		createColumn(
			"secondReminder", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> secondReminderType =
		createColumn(
			"secondReminderType", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Long> statusByUserId =
		createColumn(
			"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CalendarBookingTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private CalendarBookingTable() {
		super("CalendarBooking", CalendarBookingTable::new);
	}

}