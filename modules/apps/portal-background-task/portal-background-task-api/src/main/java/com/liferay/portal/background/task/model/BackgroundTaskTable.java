/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;BackgroundTask&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see BackgroundTask
 * @generated
 */
public class BackgroundTaskTable extends BaseTable<BackgroundTaskTable> {

	public static final BackgroundTaskTable INSTANCE =
		new BackgroundTaskTable();

	public final Column<BackgroundTaskTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<BackgroundTaskTable, Long> backgroundTaskId =
		createColumn(
			"backgroundTaskId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<BackgroundTaskTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, String> servletContextNames =
		createColumn(
			"servletContextNames", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, String> taskExecutorClassName =
		createColumn(
			"taskExecutorClassName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Clob> taskContextMap =
		createColumn(
			"taskContextMap", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Boolean> completed = createColumn(
		"completed", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Date> completionDate =
		createColumn(
			"completionDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Clob> errorStackTrace =
		createColumn(
			"errorStackTrace", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<BackgroundTaskTable, Clob> statusMessage = createColumn(
		"statusMessage", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);

	private BackgroundTaskTable() {
		super("BackgroundTask", BackgroundTaskTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:453417703