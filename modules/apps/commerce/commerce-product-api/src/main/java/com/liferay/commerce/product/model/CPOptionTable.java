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
 * The table class for the &quot;CPOption&quot; database table.
 *
 * @author Marco Leo
 * @see CPOption
 * @generated
 */
public class CPOptionTable extends BaseTable<CPOptionTable> {

	public static final CPOptionTable INSTANCE = new CPOptionTable();

	public final Column<CPOptionTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPOptionTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPOptionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Long> CPOptionId = createColumn(
		"CPOptionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPOptionTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> description = createColumn(
		"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> commerceOptionTypeKey =
		createColumn(
			"commerceOptionTypeKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Boolean> facetable = createColumn(
		"facetable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Boolean> required = createColumn(
		"required", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Boolean> skuContributor = createColumn(
		"skuContributor", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Date> lastPublishDate = createColumn(
		"lastPublishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPOptionTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CPOptionTable() {
		super("CPOption", CPOptionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:311621381