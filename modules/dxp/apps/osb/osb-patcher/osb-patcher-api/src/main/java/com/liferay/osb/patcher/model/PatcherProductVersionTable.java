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
 * The table class for the &quot;PProductVersion&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProductVersion
 * @generated
 */
public class PatcherProductVersionTable
	extends BaseTable<PatcherProductVersionTable> {

	public static final PatcherProductVersionTable INSTANCE =
		new PatcherProductVersionTable();

	public final Column<PatcherProductVersionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherProductVersionTable, Long>
		patcherProductVersionId = createColumn(
			"patcherProductVersionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<PatcherProductVersionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, Integer> fixDeliveryMethod =
		createColumn(
			"fixDeliveryMethod", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, String> moduleFolderName =
		createColumn(
			"moduleFolderName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProductVersionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private PatcherProductVersionTable() {
		super("PProductVersion", PatcherProductVersionTable::new);
	}

}