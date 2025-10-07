/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

/**
 * The table class for the &quot;CTSChild&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CTSChild
 * @generated
 */
public class CTSChildTable extends BaseTable<CTSChildTable> {

	public static final CTSChildTable INSTANCE = new CTSChildTable();

	public final Column<CTSChildTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CTSChildTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTSChildTable, Long> ctsChildId = createColumn(
		"ctsChildId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CTSChildTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSChildTable, Long> ctsGrandParentId = createColumn(
		"ctsGrandParentId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSChildTable, Long> parentCTSChildId = createColumn(
		"parentCTSChildId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CTSChildTable, String> ctsParentName = createColumn(
		"ctsParentName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CTSChildTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CTSChildTable() {
		super("CTSChild", CTSChildTable::new);
	}

}