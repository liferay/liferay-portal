/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.timebased.otp.model;

import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.base.BaseTable;

import java.sql.Types;

import java.util.Date;

/**
 * The table class for the &quot;MFATimeBasedOTPEntry&quot; database table.
 *
 * @author Arthur Chan
 * @see MFATimeBasedOTPEntry
 * @generated
 */
public class MFATimeBasedOTPEntryTable
	extends BaseTable<MFATimeBasedOTPEntryTable> {

	public static final MFATimeBasedOTPEntryTable INSTANCE =
		new MFATimeBasedOTPEntryTable();

	public final Column<MFATimeBasedOTPEntryTable, Long> mvccVersion =
		createColumn(
			"mvccVersion", Long.class, Types.BIGINT, Column.FLAG_NULLITY);
	public final Column<MFATimeBasedOTPEntryTable, Long>
		mfaTimeBasedOTPEntryId = createColumn(
			"mfaTimeBasedOTPEntryId", Long.class, Types.BIGINT,
			Column.FLAG_PRIMARY);
	public final Column<MFATimeBasedOTPEntryTable, Long> companyId =
		createColumn(
			"companyId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Long> userId = createColumn(
		"userId", Long.class, Types.BIGINT, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> userName =
		createColumn(
			"userName", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Date> createDate =
		createColumn(
			"createDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Date> modifiedDate =
		createColumn(
			"modifiedDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> algorithm =
		createColumn(
			"algorithm", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Integer> failedAttempts =
		createColumn(
			"failedAttempts", Integer.class, Types.INTEGER,
			Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Date> lastFailDate =
		createColumn(
			"lastFailDate", Date.class, Types.TIMESTAMP, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> lastFailIP =
		createColumn(
			"lastFailIP", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, Date> lastSuccessDate =
		createColumn(
			"lastSuccessDate", Date.class, Types.TIMESTAMP,
			Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> lastSuccessIP =
		createColumn(
			"lastSuccessIP", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> lastValidTOTP =
		createColumn(
			"lastValidTOTP", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);
	public final Column<MFATimeBasedOTPEntryTable, String> sharedSecret =
		createColumn(
			"sharedSecret", String.class, Types.VARCHAR, Column.FLAG_DEFAULT);

	private MFATimeBasedOTPEntryTable() {
		super("MFATimeBasedOTPEntry", MFATimeBasedOTPEntryTable::new);
	}

}
// LIFERAY-SERVICE-BUILDER-HASH:-105448565