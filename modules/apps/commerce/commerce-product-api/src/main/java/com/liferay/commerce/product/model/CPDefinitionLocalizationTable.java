/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

/**
 * The table class for the &quot;CPDefinitionLocalization&quot; database table.
 *
 * @author Marco Leo
 * @see CPDefinitionLocalization
 * @generated
 */
public class CPDefinitionLocalizationTable
	extends BaseTable<CPDefinitionLocalizationTable> {

	public static final CPDefinitionLocalizationTable INSTANCE =
		new CPDefinitionLocalizationTable();

	public final Column<CPDefinitionLocalizationTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CPDefinitionLocalizationTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CPDefinitionLocalizationTable, Long>
		cpDefinitionLocalizationId = createColumn(
			"cpDefinitionLocalizationId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<CPDefinitionLocalizationTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, Long> CPDefinitionId =
		createColumn(
			"CPDefinitionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String> languageId =
		createColumn(
			"languageId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, Long> CProductId =
		createColumn(
			"CProductId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String>
		shortDescription = createColumn(
			"shortDescription", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, Clob> description =
		createColumn(
			"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String> metaTitle =
		createColumn(
			"metaTitle", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String> metaDescription =
		createColumn(
			"metaDescription", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CPDefinitionLocalizationTable, String> metaKeywords =
		createColumn(
			"metaKeywords", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CPDefinitionLocalizationTable() {
		super("CPDefinitionLocalization", CPDefinitionLocalizationTable::new);
	}

}