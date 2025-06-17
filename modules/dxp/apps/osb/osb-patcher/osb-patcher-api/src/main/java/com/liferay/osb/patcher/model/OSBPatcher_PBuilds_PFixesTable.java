/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;OSBPatcher_PBuilds_PFixes&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see PatcherBuild
 * @see PatcherFix
 * @generated
 */
public class OSBPatcher_PBuilds_PFixesTable
	extends BaseTable<OSBPatcher_PBuilds_PFixesTable> {

	public static final OSBPatcher_PBuilds_PFixesTable INSTANCE =
		new OSBPatcher_PBuilds_PFixesTable();

	public final Column<OSBPatcher_PBuilds_PFixesTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PBuilds_PFixesTable, Long> patcherBuildId =
		createColumn(
			"patcherBuildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<OSBPatcher_PBuilds_PFixesTable, Long> patcherFixId =
		createColumn(
			"patcherFixId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);

	private OSBPatcher_PBuilds_PFixesTable() {
		super("OSBPatcher_PBuilds_PFixes", OSBPatcher_PBuilds_PFixesTable::new);
	}

}