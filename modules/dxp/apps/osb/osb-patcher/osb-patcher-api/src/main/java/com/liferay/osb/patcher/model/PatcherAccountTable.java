/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;OSBPatcher_PatcherAccount&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccount
 * @generated
 */
public class PatcherAccountTable extends BaseTable<PatcherAccountTable> {

	public static final PatcherAccountTable INSTANCE =
		new PatcherAccountTable();

	public final Column<PatcherAccountTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherAccountTable, Long> patcherAccountId =
		createColumn(
			"patcherAccountId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherAccountTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, Long> accountEntryId =
		createColumn(
			"accountEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherAccountTable, String> accountEntryCode =
		createColumn(
			"accountEntryCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private PatcherAccountTable() {
		super("OSBPatcher_PatcherAccount", PatcherAccountTable::new);
	}

}