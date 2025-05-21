/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;PatcherBuilds_PatcherFixes&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuild
 * @see PatcherFix
 * @generated
 */
public class PatcherBuilds_PatcherFixesTable
	extends BaseTable<PatcherBuilds_PatcherFixesTable> {

	public static final PatcherBuilds_PatcherFixesTable INSTANCE =
		new PatcherBuilds_PatcherFixesTable();

	public final Column<PatcherBuilds_PatcherFixesTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherBuilds_PatcherFixesTable, Long> patcherBuildId =
		createColumn(
			"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherBuilds_PatcherFixesTable, Long> patcherFixId =
		createColumn(
			"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private PatcherBuilds_PatcherFixesTable() {
		super(
			"PatcherBuilds_PatcherFixes", PatcherBuilds_PatcherFixesTable::new);
	}

}