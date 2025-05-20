/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.osb.patcher.hook.upgrade.v7_0_0;

import com.liferay.compat.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.compat.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * @author Kiana Suetani
 */
public class UpgradePatcherAccount extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		if (!tableHasColumn("OSB_PatcherAccount", "accountEntryId")) {
			return;
		}

		try {
			runSQL(
				"update OSB_PatcherAccount set accountEntryId = " +
					_ACCOUNT_ENTRY_ID_NO_MATCH + " where accountEntryCode <> " +
						"'" + _ACCOUNT_ENTRY_CODE_PATCHERTEST + "'");

			Class<?> clazz = getClass();

			String csvText = StringUtil.read(
				clazz.getClassLoader(),
				"com/liferay/osb/patcher/hook/" +
					"upgrade/v7_0_0/dependencies/AccountEntryIds_20181113.csv");

			CSVParser csvParser = CSVParser.parse(
				csvText, _CSV_FORMAT.withFirstRecordAsHeader());

			for (CSVRecord csvRecord : csvParser) {
				String accountEntryCode = csvRecord.get("accountEntryCode");
				String accountEntryId = csvRecord.get("accountEntryId");

				runSQL(
					"update OSB_PatcherAccount set accountEntryId = " +
						accountEntryId + " where accountEntryCode = '" +
							accountEntryCode + "'");
			}
		}
		catch (Exception e) {
			_log.error(e);
		}
	}

	private static final String _ACCOUNT_ENTRY_CODE_PATCHERTEST = "PATCHERTEST";

	private static final long _ACCOUNT_ENTRY_ID_NO_MATCH = -1;

	private static final CSVFormat _CSV_FORMAT = CSVFormat.DEFAULT;

	private static final Log _log = LogFactoryUtil.getLog(
		UpgradePatcherAccount.class);

}