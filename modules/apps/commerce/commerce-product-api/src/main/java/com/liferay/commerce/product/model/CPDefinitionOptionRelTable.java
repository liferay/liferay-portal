/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CPDefinitionOptionRel&quot; database table.
 *
 * @author Marco Leo
 * @see CPDefinitionOptionRel
 * @generated
 */
public class CPDefinitionOptionRelTable
	extends BaseTable<CPDefinitionOptionRelTable> {

	public static final CPDefinitionOptionRelTable INSTANCE =
		new CPDefinitionOptionRelTable();

	public final Column<CPDefinitionOptionRelTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPDefinitionOptionRelTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPDefinitionOptionRelTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Long>
		CPDefinitionOptionRelId = createColumn(
			"CPDefinitionOptionRelId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPDefinitionOptionRelTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Long> CPDefinitionId =
		createColumn(
			"CPDefinitionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Long> CPOptionId =
		createColumn(
			"CPOptionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String>
		commerceOptionTypeKey = createColumn(
			"commerceOptionTypeKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> infoItemServiceKey =
		createColumn(
			"infoItemServiceKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Double> priority =
		createColumn(
			"priority", Double.class, Types.DOUBLE, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Boolean> definedExternally =
		createColumn(
			"definedExternally", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Boolean> facetable =
		createColumn(
			"facetable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Boolean> required =
		createColumn(
			"required", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Boolean> skuContributor =
		createColumn(
			"skuContributor", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> key = createColumn(
		"key_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, String> priceType =
		createColumn(
			"priceType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionOptionRelTable, Clob> typeSettings =
		createColumn(
			"typeSettings", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);

	private CPDefinitionOptionRelTable() {
		super("CPDefinitionOptionRel", CPDefinitionOptionRelTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-155373855