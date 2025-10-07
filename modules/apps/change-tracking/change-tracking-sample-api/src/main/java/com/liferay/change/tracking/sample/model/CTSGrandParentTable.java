/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;CTSGrandParent&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CTSGrandParent
 * @generated
 */
public class CTSGrandParentTable extends BaseTable<CTSGrandParentTable> {

	public static final CTSGrandParentTable INSTANCE =
		new CTSGrandParentTable();

	public final Column<CTSGrandParentTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CTSGrandParentTable, Long> ctsGrandParentId =
		createColumn(
			"ctsGrandParentId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTSGrandParentTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSGrandParentTable, Long> parentCTSGrandParentId =
		createColumn(
			"parentCTSGrandParentId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CTSGrandParentTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CTSGrandParentTable() {
		super("CTSGrandParent", CTSGrandParentTable::new);
	}

}