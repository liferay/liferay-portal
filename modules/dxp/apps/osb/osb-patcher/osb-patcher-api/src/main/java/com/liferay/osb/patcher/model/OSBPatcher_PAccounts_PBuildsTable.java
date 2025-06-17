/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBPatcher_PAccounts_PBuilds&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherAccount
 * @see PatcherBuild
 * @generated
 */
public class OSBPatcher_PAccounts_PBuildsTable
	extends BaseTable<OSBPatcher_PAccounts_PBuildsTable> {

	public static final OSBPatcher_PAccounts_PBuildsTable INSTANCE =
		new OSBPatcher_PAccounts_PBuildsTable();

	public final Column<OSBPatcher_PAccounts_PBuildsTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PAccounts_PBuildsTable, Long>
		patcherAccountId = createColumn(
			"patcherAccountId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PAccounts_PBuildsTable, Long>
		patcherBuildId = createColumn(
			"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private OSBPatcher_PAccounts_PBuildsTable() {
		super(
			"OSBPatcher_PAccounts_PBuilds",
			OSBPatcher_PAccounts_PBuildsTable::new);
	}

}