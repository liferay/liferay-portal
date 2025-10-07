/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;Layout&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see Layout
 * @generated
 */
public class LayoutTable extends BaseTable<LayoutTable> {

	public static final LayoutTable INSTANCE = new LayoutTable();

	public final Column<LayoutTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<LayoutTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<LayoutTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> plid = createColumn(
		"plid", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<LayoutTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> parentPlid = createColumn(
		"parentPlid", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Boolean> privateLayout = createColumn(
		"privateLayout", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> layoutId = createColumn(
		"layoutId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> parentLayoutId = createColumn(
		"parentLayoutId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> classNameId = createColumn(
		"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> classPK = createColumn(
		"classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Clob> title = createColumn(
		"title", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Clob> description = createColumn(
		"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> keywords = createColumn(
		"keywords", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> robots = createColumn(
		"robots", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Clob> typeSettings = createColumn(
		"typeSettings", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Boolean> hidden = createColumn(
		"hidden_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Boolean> system = createColumn(
		"system_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> friendlyURL = createColumn(
		"friendlyURL", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> iconImageId = createColumn(
		"iconImageId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> themeId = createColumn(
		"themeId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> colorSchemeId = createColumn(
		"colorSchemeId", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> styleBookEntryId = createColumn(
		"styleBookEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Clob> css = createColumn(
		"css", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Integer> priority = createColumn(
		"priority", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> faviconFileEntryId = createColumn(
		"faviconFileEntryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> masterLayoutPlid = createColumn(
		"masterLayoutPlid", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> layoutPrototypeUuid = createColumn(
		"layoutPrototypeUuid", String.class, Types.VARCHAR,
		Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Boolean> layoutPrototypeLinkEnabled =
		createColumn(
			"layoutPrototypeLinkEnabled", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> layoutSetPrototypeLayoutERC =
		createColumn(
			"layoutSetPrototypeLayoutERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Date> publishDate = createColumn(
		"publishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Date> lastPublishDate = createColumn(
		"lastPublishDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Long> statusByUserId = createColumn(
		"statusByUserId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, String> statusByUserName = createColumn(
		"statusByUserName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<LayoutTable, Date> statusDate = createColumn(
		"statusDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);

	private LayoutTable() {
		super("Layout", LayoutTable::new);
	}

}