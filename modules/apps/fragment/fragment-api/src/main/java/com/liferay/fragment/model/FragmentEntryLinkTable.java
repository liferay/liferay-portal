/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Clob;
import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;FragmentEntryLink&quot; database table.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentEntryLink
 * @generated
 */
public class FragmentEntryLinkTable extends BaseTable<FragmentEntryLinkTable> {

	public static final FragmentEntryLinkTable INSTANCE =
		new FragmentEntryLinkTable();

	public final Column<FragmentEntryLinkTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<FragmentEntryLinkTable, Long> ctCollectionId =
		createColumn(
			"ctCollectionId", Long.class, Types.BIGINT, Column.FLAG_PRIMARY);
	public final Column<FragmentEntryLinkTable, String> uuid = createColumn(
		"uuid_", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> externalReferenceCode =
		createColumn(
			"externalReferenceCode", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> fragmentEntryLinkId =
		createColumn(
			"fragmentEntryLinkId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<FragmentEntryLinkTable, Long> groupId = createColumn(
		"groupId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> companyId = createColumn(
		"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> userName = createColumn(
		"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Date> createDate = createColumn(
		"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String>
		originalFragmentEntryLinkERC = createColumn(
			"originalFragmentEntryLinkERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> fragmentEntryERC =
		createColumn(
			"fragmentEntryERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> fragmentEntryScopeERC =
		createColumn(
			"fragmentEntryScopeERC", String.class, Types.VARCHAR,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> segmentsExperienceId =
		createColumn(
			"segmentsExperienceId", Long.class, Types.BIGINT,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> classNameId =
		createColumn(
			"classNameId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> classPK = createColumn(
		"classPK", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Long> plid = createColumn(
		"plid", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Clob> css = createColumn(
		"css", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Clob> html = createColumn(
		"html", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Clob> js = createColumn(
		"js", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Clob> configuration =
		createColumn(
			"configuration", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Boolean> deleted = createColumn(
		"deleted", Boolean.class, Types.BOOLEAN, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Clob> editableValues =
		createColumn(
			"editableValues", Clob.class, Types.CLOB, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> namespace =
		createColumn(
			"namespace", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Integer> position =
		createColumn(
			"position", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, String> rendererKey =
		createColumn(
			"rendererKey", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Integer> type = createColumn(
		"type_", Integer.class, Types.INTEGER, Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Date> lastPropagationDate =
		createColumn(
			"lastPropagationDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<FragmentEntryLinkTable, Date> lastPublishDate =
		createColumn(
			"lastPublishDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);

	private FragmentEntryLinkTable() {
		super("FragmentEntryLink", FragmentEntryLinkTable::new);
	}

}