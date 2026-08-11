/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

/**
 * The table class for the &quot;ClobEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see ClobEntry
 * @generated
 */
public class ClobEntryTable extends BaseTable<ClobEntryTable> {

	public static final ClobEntryTable INSTANCE = new ClobEntryTable();

	public final Column<ClobEntryTable, Long> clobEntryId = createColumn(
		"clobEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<ClobEntryTable, Clob> content = createColumn(
		"content", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);

	private ClobEntryTable() {
		super("ClobEntry", ClobEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-509051128