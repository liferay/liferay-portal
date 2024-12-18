/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;WorkflowDefinitionLink&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see WorkflowDefinitionLink
 * @generated
 */
public class WorkflowDefinitionLinkTable
	extends BaseTable<WorkflowDefinitionLinkTable> {

	public static final WorkflowDefinitionLinkTable INSTANCE =
		new WorkflowDefinitionLinkTable();

	public final Column<WorkflowDefinitionLinkTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<WorkflowDefinitionLinkTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<WorkflowDefinitionLinkTable, String> uuid =
		createColumn("uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, String>
		externalReferenceCode = createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long>
		workflowDefinitionLinkId = createColumn(
			"workflowDefinitionLinkId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<WorkflowDefinitionLinkTable, Long> groupId =
		createColumn("groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long> userId =
		createColumn("userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long> classNameId =
		createColumn(
			"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long> classPK =
		createColumn("classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Long> typePK =
		createColumn("typePK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, String>
		workflowDefinitionName = createColumn(
			"workflowDefinitionName", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<WorkflowDefinitionLinkTable, Integer>
		workflowDefinitionVersion = createColumn(
			"workflowDefinitionVersion", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);

	private WorkflowDefinitionLinkTable() {
		super("WorkflowDefinitionLink", WorkflowDefinitionLinkTable::new);
	}

}