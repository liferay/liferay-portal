/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.service.access.policy.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;SAPEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see SAPEntry
 * @generated
 */
public class SAPEntryTable extends BaseTable<SAPEntryTable> {

	public static final SAPEntryTable INSTANCE = new SAPEntryTable();

	public final Column<SAPEntryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Long> sapEntryId = createColumn(
		"sapEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<SAPEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, String> allowedServiceSignatures =
		createColumn(
			"allowedServiceSignatures", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Boolean> defaultSAPEntry = createColumn(
		"defaultSAPEntry", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Boolean> enabled = createColumn(
		"enabled", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<SAPEntryTable, Clob> title = createColumn(
		"title", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);

	private SAPEntryTable() {
		super("SAPEntry", SAPEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1025927835