/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBFaro_FaroProject&quot; database table.
 *
 * @author Matthew Kong
 * @see FaroProject
 * @generated
 */
public class FaroProjectTable extends BaseTable<FaroProjectTable> {

	public static final FaroProjectTable INSTANCE = new FaroProjectTable();

	public final Column<FaroProjectTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<FaroProjectTable, Long> faroProjectId = createColumn(
		"faroProjectId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<FaroProjectTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> createTime = createColumn(
		"createTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> modifiedTime = createColumn(
		"modifiedTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> accountKey = createColumn(
		"accountKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> accountName = createColumn(
		"accountName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> corpProjectName =
		createColumn(
			"corpProjectName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> corpProjectUuid =
		createColumn(
			"corpProjectUuid", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Boolean> dataSourceConnected =
		createColumn(
			"dataSourceConnected", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> ipAddresses = createColumn(
		"ipAddresses", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> incidentReportEmailAddresses =
		createColumn(
			"incidentReportEmailAddresses", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> lastAccessTime = createColumn(
		"lastAccessTime", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Boolean> recommendationsEnabled =
		createColumn(
			"recommendationsEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> salesforceAccountId =
		createColumn(
			"salesforceAccountId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> serverLocation = createColumn(
		"serverLocation", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> services = createColumn(
		"services", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> state = createColumn(
		"state_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> subscription = createColumn(
		"subscription", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, Long> subscriptionModifiedTime =
		createColumn(
			"subscriptionModifiedTime", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> timeZoneId = createColumn(
		"timeZoneId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FaroProjectTable, String> weDeployKey = createColumn(
		"weDeployKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private FaroProjectTable() {
		super("OSBFaro_FaroProject", FaroProjectTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1093186720