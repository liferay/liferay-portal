/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shop.by.diagram.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;CSDiagramEntry&quot; database table.
 *
 * @author Alessio Antonio Rendina
 * @see CSDiagramEntry
 * @generated
 */
public class CSDiagramEntryTable extends BaseTable<CSDiagramEntryTable> {

	public static final CSDiagramEntryTable INSTANCE =
		new CSDiagramEntryTable();

	public final Column<CSDiagramEntryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<CSDiagramEntryTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CSDiagramEntryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Long> CSDiagramEntryId =
		createColumn(
			"CSDiagramEntryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<CSDiagramEntryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Long> CPDefinitionId =
		createColumn(
			"CPDefinitionId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Long> CPInstanceId = createColumn(
		"CPInstanceId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Long> CProductId = createColumn(
		"CProductId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Boolean> diagram = createColumn(
		"diagram", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, Integer> quantity = createColumn(
		"quantity", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, String> sequence = createColumn(
		"sequence", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<CSDiagramEntryTable, String> sku = createColumn(
		"sku", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private CSDiagramEntryTable() {
		super("CSDiagramEntry", CSDiagramEntryTable::new);
	}

}