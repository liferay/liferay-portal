/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OSBPatcher_PatcherBuild&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuild
 * @generated
 */
public class PatcherBuildTable extends BaseTable<PatcherBuildTable> {

	public static final PatcherBuildTable INSTANCE = new PatcherBuildTable();

	public final Column<PatcherBuildTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherBuildTable, Long> patcherBuildId = createColumn(
		"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherBuildTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> hotfixId = createColumn(
		"hotfixId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> patcherAccountId =
		createColumn(
			"patcherAccountId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> patcherFixId = createColumn(
		"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> patcherProductVersionId =
		createColumn(
			"patcherProductVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> patcherProjectVersionId =
		createColumn(
			"patcherProjectVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> ticketEntryId = createColumn(
		"ticketEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> accountEntryCode =
		createColumn(
			"accountEntryCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> childBuild = createColumn(
		"childBuild", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Clob> comments = createColumn(
		"comments", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> fileName = createColumn(
		"fileName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> initialName = createColumn(
		"initialName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Double> keyVersion = createColumn(
		"keyVersion", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> latestBuild = createColumn(
		"latestBuild", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> latestKeyBuild =
		createColumn(
			"latestKeyBuild", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> latestLESATicketBuild =
		createColumn(
			"latestLESATicketBuild", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> latestSupportTicketBuild =
		createColumn(
			"latestSupportTicketBuild", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> lesaTicket = createColumn(
		"lesaTicket", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Double> lesaTicketVersion =
		createColumn(
			"lesaTicketVersion", Double.class, Types.DOUBLE,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Clob> name = createColumn(
		"name", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Boolean> notified = createColumn(
		"notified", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Integer> productVersion =
		createColumn(
			"productVersion", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Clob> qaComments = createColumn(
		"qaComments", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Integer> qaStatus = createColumn(
		"qaStatus", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> requestKey = createColumn(
		"requestKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> sourceName = createColumn(
		"sourceName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> supportTicket = createColumn(
		"supportTicket", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Double> supportTicketVersion =
		createColumn(
			"supportTicketVersion", Double.class, Types.DOUBLE,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<PatcherBuildTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private PatcherBuildTable() {
		super("OSBPatcher_PatcherBuild", PatcherBuildTable::new);
	}

}