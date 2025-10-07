/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;Release_&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see Release
 * @generated
 */
public class ReleaseTable extends BaseTable<ReleaseTable> {

	public static final ReleaseTable INSTANCE = new ReleaseTable();

	public final Column<ReleaseTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ReleaseTable, Long> releaseId = createColumn(
		"releaseId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<ReleaseTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, String> servletContextName = createColumn(
		"servletContextName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, String> schemaVersion = createColumn(
		"schemaVersion", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, Integer> buildNumber = createColumn(
		"buildNumber", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, Date> buildDate = createColumn(
		"buildDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, String> versionDisplayName = createColumn(
		"versionDisplayName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, Boolean> verified = createColumn(
		"verified", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, Integer> state = createColumn(
		"state_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ReleaseTable, String> testString = createColumn(
		"testString", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private ReleaseTable() {
		super("Release_", ReleaseTable::new);
	}

}