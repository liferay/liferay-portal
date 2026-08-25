/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.content.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;LayoutContentVersionPreview&quot; database table.
 *
 * @author Lourdes Fernández Besada
 * @see LayoutContentVersionPreview
 * @generated
 */
public class LayoutContentVersionPreviewTable
	extends BaseTable<LayoutContentVersionPreviewTable> {

	public static final LayoutContentVersionPreviewTable INSTANCE =
		new LayoutContentVersionPreviewTable();

	public final Column<LayoutContentVersionPreviewTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<LayoutContentVersionPreviewTable, Long>
		layoutContentVersionPreviewId = createColumn(
			"layoutContentVersionPreviewId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<LayoutContentVersionPreviewTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Long>
		layoutContentVersionId = createColumn(
			"layoutContentVersionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, Clob> html =
		createColumn("html", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, String> languageId =
		createColumn(
			"languageId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutContentVersionPreviewTable, String>
		segmentsExperienceERC = createColumn(
			"segmentsExperienceERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);

	private LayoutContentVersionPreviewTable() {
		super(
			"LayoutContentVersionPreview",
			LayoutContentVersionPreviewTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:584115037