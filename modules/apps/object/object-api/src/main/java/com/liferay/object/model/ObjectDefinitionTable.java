/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;ObjectDefinition&quot; database table.
 *
 * @author Marco Leo
 * @see ObjectDefinition
 * @generated
 */
public class ObjectDefinitionTable extends BaseTable<ObjectDefinitionTable> {

	public static final ObjectDefinitionTable INSTANCE =
		new ObjectDefinitionTable();

	public final Column<ObjectDefinitionTable, Long> mvccVersion = createColumn(
		"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ObjectDefinitionTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long> objectDefinitionId =
		createColumn(
			"objectDefinitionId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ObjectDefinitionTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long>
		accountEntryRestrictedObjectFieldId = createColumn(
			"accountERObjectFieldId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long> descriptionObjectFieldId =
		createColumn(
			"descriptionObjectFieldId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long> objectFolderId =
		createColumn(
			"objectFolderId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Long> titleObjectFieldId =
		createColumn(
			"titleObjectFieldId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> accountEntryRestricted =
		createColumn(
			"accountEntryRestricted", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> active = createColumn(
		"active_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> className = createColumn(
		"className", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> dbTableName =
		createColumn(
			"dbTableName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableCategorization =
		createColumn(
			"enableCategorization", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableComments =
		createColumn(
			"enableComments", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableFormContainer =
		createColumn(
			"enableFormContainer", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean>
		enableFriendlyURLCustomization = createColumn(
			"enableFriendlyURLCustomization", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableIndexSearch =
		createColumn(
			"enableIndexSearch", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableLocalization =
		createColumn(
			"enableLocalization", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> enableObjectEntryDraft =
		createColumn(
			"enableObjectEntryDraft", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean>
		enableObjectEntryHistory = createColumn(
			"enableObjectEntryHistory", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean>
		enableObjectEntrySchedule = createColumn(
			"enableObjectEntrySchedule", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean>
		enableObjectEntrySubscription = createColumn(
			"enableObjectEntrySubscription", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean>
		enableObjectEntryVersioning = createColumn(
			"enableObjectEntryVersioning", Boolean.class, Types.BOOLEAN,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> friendlyURLSeparator =
		createColumn(
			"friendlyURLSeparator", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> label = createColumn(
		"label", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> modifiable =
		createColumn(
			"modifiable", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> panelAppOrder =
		createColumn(
			"panelAppOrder", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> panelCategoryKey =
		createColumn(
			"panelCategoryKey", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String>
		pkObjectFieldDBColumnName = createColumn(
			"pkObjectFieldDBColumnName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> pkObjectFieldName =
		createColumn(
			"pkObjectFieldName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> pluralLabel =
		createColumn(
			"pluralLabel", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> portlet = createColumn(
		"portlet", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> scope = createColumn(
		"scope", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, String> storageType =
		createColumn(
			"storageType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Boolean> system = createColumn(
		"system_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Integer> version = createColumn(
		"version", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<ObjectDefinitionTable, Integer> status = createColumn(
		"status", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);

	private ObjectDefinitionTable() {
		super("ObjectDefinition", ObjectDefinitionTable::new);
	}

}