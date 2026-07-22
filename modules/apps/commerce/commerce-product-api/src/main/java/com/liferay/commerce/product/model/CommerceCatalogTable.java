/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CommerceCatalog&quot; database table.
 *
 * @author Marco Leo
 * @see CommerceCatalog
 * @generated
 */
public class CommerceCatalogTable extends BaseTable<CommerceCatalogTable> {

	public static final CommerceCatalogTable INSTANCE =
		new CommerceCatalogTable();

	public final Column<CommerceCatalogTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CommerceCatalogTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommerceCatalogTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Long> commerceCatalogId =
		createColumn(
			"commerceCatalogId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CommerceCatalogTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Long> accountEntryId =
		createColumn(
			"accountEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, String> commerceCurrencyCode =
		createColumn(
			"commerceCurrencyCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, String> catalogDefaultLanguageId =
		createColumn(
			"catalogDefaultLanguageId", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Boolean> system = createColumn(
		"system_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CommerceCatalogTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CommerceCatalogTable() {
		super("CommerceCatalog", CommerceCatalogTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-683698202