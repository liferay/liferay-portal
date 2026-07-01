/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;AssetCategory&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see AssetCategory
 * @generated
 */
public class AssetCategoryTable extends BaseTable<AssetCategoryTable> {

	public static final AssetCategoryTable INSTANCE = new AssetCategoryTable();

	public final Column<AssetCategoryTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<AssetCategoryTable, Long> ctCollectionId = createColumn(
		"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<AssetCategoryTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Long> categoryId = createColumn(
		"categoryId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<AssetCategoryTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Date> modifiedDate = createColumn(
		"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Long> parentCategoryId =
		createColumn(
			"parentCategoryId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, String> treePath = createColumn(
		"treePath", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Clob> title = createColumn(
		"title", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Clob> description = createColumn(
		"description", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Long> vocabularyId = createColumn(
		"vocabularyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Boolean> system = createColumn(
		"system_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<AssetCategoryTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private AssetCategoryTable() {
		super("AssetCategory", AssetCategoryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:1574476507