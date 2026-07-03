/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

/**
 * The table class for the &quot;CompanyInfo&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see CompanyInfo
 * @generated
 */
public class CompanyInfoTable extends BaseTable<CompanyInfoTable> {

	public static final CompanyInfoTable INSTANCE = new CompanyInfoTable();

	public final Column<CompanyInfoTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CompanyInfoTable, Long> companyInfoId = createColumn(
		"companyInfoId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CompanyInfoTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> homeURL = createColumn(
		"homeURL", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> indexNameCurrent =
		createColumn(
			"indexNameCurrent", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> indexNameNext = createColumn(
		"indexNameNext", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> industry = createColumn(
		"industry", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, Clob> key = createColumn(
		"key_", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> legalId = createColumn(
		"legalId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> legalName = createColumn(
		"legalName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> legalType = createColumn(
		"legalType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, Long> logoId = createColumn(
		"logoId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> sicCode = createColumn(
		"sicCode", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> size = createColumn(
		"size_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> tickerSymbol = createColumn(
		"tickerSymbol", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CompanyInfoTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CompanyInfoTable() {
		super("CompanyInfo", CompanyInfoTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1059117305