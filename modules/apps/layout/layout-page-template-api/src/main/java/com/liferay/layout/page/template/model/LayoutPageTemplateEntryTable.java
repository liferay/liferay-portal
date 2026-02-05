/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;LayoutPageTemplateEntry&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntry
 * @generated
 */
public class LayoutPageTemplateEntryTable
	extends BaseTable<LayoutPageTemplateEntryTable> {

	public static final LayoutPageTemplateEntryTable INSTANCE =
		new LayoutPageTemplateEntryTable();

	public final Column<LayoutPageTemplateEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<LayoutPageTemplateEntryTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<LayoutPageTemplateEntryTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long>
		layoutPageTemplateEntryId = createColumn(
			"layoutPageTemplateEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<LayoutPageTemplateEntryTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long>
		layoutPageTemplateCollectionId = createColumn(
			"layoutPageTemplateCollectionId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String>
		layoutPageTemplateEntryKey = createColumn(
			"layoutPageTemplateEntryKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> classNameId =
		createColumn(
			"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> classTypeId =
		createColumn(
			"classTypeId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String> classTypeKey =
		createColumn(
			"classTypeKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String> name =
		createColumn("name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Integer> type =
		createColumn(
			"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> previewFileEntryId =
		createColumn(
			"previewFileEntryId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Boolean> defaultTemplate =
		createColumn(
			"defaultTemplate", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> layoutPrototypeId =
		createColumn(
			"layoutPrototypeId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> plid = createColumn(
		"plid", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Integer> status =
		createColumn(
			"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Long> statusByUserId =
		createColumn(
			"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, String> statusByUserName =
		createColumn(
			"statusByUserName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<LayoutPageTemplateEntryTable, Date> statusDate =
		createColumn(
			"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private LayoutPageTemplateEntryTable() {
		super("LayoutPageTemplateEntry", LayoutPageTemplateEntryTable::new);
	}

}