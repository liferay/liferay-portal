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
 * The table class for the &quot;ObjectRelationship&quot; database table.
 *
 * @author Marco Leo
 * @see ObjectRelationship
 * @generated
 */
public class ObjectRelationshipTable
	extends BaseTable<ObjectRelationshipTable> {

	public static final ObjectRelationshipTable INSTANCE =
		new ObjectRelationshipTable();

	public final Column<ObjectRelationshipTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<ObjectRelationshipTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> objectRelationshipId =
		createColumn(
			"objectRelationshipId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<ObjectRelationshipTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> objectDefinitionId1 =
		createColumn(
			"objectDefinitionId1", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> objectDefinitionId2 =
		createColumn(
			"objectDefinitionId2", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> objectFieldId2 =
		createColumn(
			"objectFieldId2", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Long> parameterObjectFieldId =
		createColumn(
			"parameterObjectFieldId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> deletionType =
		createColumn(
			"deletionType", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> dbTableName =
		createColumn(
			"dbTableName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> description =
		createColumn(
			"description", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Boolean> edge = createColumn(
		"edge", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> label = createColumn(
		"label", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> name = createColumn(
		"name", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Boolean> reverse =
		createColumn(
			"reverse", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, Boolean> system = createColumn(
		"system_", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<ObjectRelationshipTable, String> type = createColumn(
		"type_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private ObjectRelationshipTable() {
		super("ObjectRelationship", ObjectRelationshipTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:2024517687