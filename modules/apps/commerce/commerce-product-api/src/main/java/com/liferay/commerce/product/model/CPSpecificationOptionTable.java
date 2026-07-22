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
 * The table class for the &quot;CPSpecificationOption&quot; database table.
 *
 * @author Marco Leo
 * @see CPSpecificationOption
 * @generated
 */
public class CPSpecificationOptionTable
	extends BaseTable<CPSpecificationOptionTable> {

	public static final CPSpecificationOptionTable INSTANCE =
		new CPSpecificationOptionTable();

	public final Column<CPSpecificationOptionTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPSpecificationOptionTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPSpecificationOptionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Long>
		CPSpecificationOptionId = createColumn(
			"CPSpecificationOptionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPSpecificationOptionTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Long> CPOptionCategoryId =
		createColumn(
			"CPOptionCategoryId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, String> title =
		createColumn("title", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Boolean> facetable =
		createColumn(
			"facetable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Double> priority =
		createColumn(
			"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Boolean> visible =
		createColumn(
			"visible", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<CPSpecificationOptionTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private CPSpecificationOptionTable() {
		super("CPSpecificationOption", CPSpecificationOptionTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-388105418