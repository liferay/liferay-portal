/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;PatcherAccounts_PatcherBuilds&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccount
 * @see PatcherBuild
 * @generated
 */
public class PatcherAccounts_PatcherBuildsTable
	extends BaseTable<PatcherAccounts_PatcherBuildsTable> {

	public static final PatcherAccounts_PatcherBuildsTable INSTANCE =
		new PatcherAccounts_PatcherBuildsTable();

	public final Column<PatcherAccounts_PatcherBuildsTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherAccounts_PatcherBuildsTable, Long>
		patcherAccountId = createColumn(
			"patcherAccountId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<PatcherAccounts_PatcherBuildsTable, Long>
		patcherBuildId = createColumn(
			"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private PatcherAccounts_PatcherBuildsTable() {
		super(
			"PatcherAccounts_PatcherBuilds",
			PatcherAccounts_PatcherBuildsTable::new);
	}

}