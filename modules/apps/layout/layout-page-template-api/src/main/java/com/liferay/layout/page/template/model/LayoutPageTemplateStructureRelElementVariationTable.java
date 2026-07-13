/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;LPTSRelElementVariation&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateStructureRelElementVariation
 * @generated
 */
public class LayoutPageTemplateStructureRelElementVariationTable
	extends BaseTable<LayoutPageTemplateStructureRelElementVariationTable> {

	public static final LayoutPageTemplateStructureRelElementVariationTable
		INSTANCE = new LayoutPageTemplateStructureRelElementVariationTable();

	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long>
			mvccVersion = createColumn(
				"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long>
			ctCollectionId = createColumn(
				"ctCollectionId", Long.class, Types.BIGINT,
				Column.FLAG_PRIMARY);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String> uuid =
			createColumn(
				"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String>
			externalReferenceCode = createColumn(
				"externalReferenceCode", String.class, Types.VARCHAR,
				Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long>
			layoutPageTemplateStructureRelElementVariationId = createColumn(
				"lptsRelElementVariationId", Long.class, Types.BIGINT,
				Column.FLAG_PRIMARY);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long> groupId =
			createColumn(
				"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long> companyId =
			createColumn(
				"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long> userId =
			createColumn(
				"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String> userName =
			createColumn(
				"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Date> createDate =
			createColumn(
				"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Date>
			modifiedDate = createColumn(
				"modifiedDate", Date.class, Types.TIMESTAMP,
				Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Boolean> active =
			createColumn(
				"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Clob> hide =
			createColumn("hide", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String> html =
			createColumn(
				"html", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String> js =
			createColumn(
				"js", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String> name =
			createColumn(
				"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, Long> plid =
			createColumn("plid", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String>
			segmentsExperienceERC = createColumn(
				"segmentsExperienceERC", String.class, Types.VARCHAR,
				Column.FLAG_DEFAULT);
	public final Column
		<LayoutPageTemplateStructureRelElementVariationTable, String>
			targetElement = createColumn(
				"targetElement", String.class, Types.VARCHAR,
				Column.FLAG_DEFAULT);

	private LayoutPageTemplateStructureRelElementVariationTable() {
		super(
			"LPTSRelElementVariation",
			LayoutPageTemplateStructureRelElementVariationTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-1922794387