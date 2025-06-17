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
 * The table class for the &quot;PProjectVersion&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherProjectVersion
 * @generated
 */
public class PatcherProjectVersionTable
	extends BaseTable<PatcherProjectVersionTable> {

	public static final PatcherProjectVersionTable INSTANCE =
		new PatcherProjectVersionTable();

	public final Column<PatcherProjectVersionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<PatcherProjectVersionTable, Long>
		patcherProjectVersionId = createColumn(
			"patcherProjectVersionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<PatcherProjectVersionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Long>
		patcherProductVersionId = createColumn(
			"patcherProductVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Long>
		rootPatcherProjectVersionId = createColumn(
			"rootPatcherProjectVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Boolean> combinedBranch =
		createColumn(
			"combinedBranch", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, String> committish =
		createColumn(
			"committish", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Clob> fixedIssues =
		createColumn(
			"fixedIssues", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Boolean> hide =
		createColumn("hide", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, Integer> productVersion =
		createColumn(
			"productVersion", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<PatcherProjectVersionTable, String> repositoryName =
		createColumn(
			"repositoryName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private PatcherProjectVersionTable() {
		super("PProjectVersion", PatcherProjectVersionTable::new);
	}

}