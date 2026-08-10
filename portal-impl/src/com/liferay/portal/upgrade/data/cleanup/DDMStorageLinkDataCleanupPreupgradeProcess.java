/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.db.DBInspector;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;

/**
 * @author Luis Ortiz
 */
public class DDMStorageLinkDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, null, "contentId", "DDMContent", "classPK",
				"DDMStorageLink"));

		String sourceAdditionalWhereClause = null;

		DBInspector dbInspector = new DBInspector(connection);

		String journalArticleTableName = dbInspector.normalizeName(
			"JournalArticle");

		if (dbInspector.hasTable(journalArticleTableName)) {
			sourceAdditionalWhereClause = StringBundler.concat(
				"not exists (select 1 from ", journalArticleTableName,
				" where ", journalArticleTableName, ".",
				dbInspector.normalizeName("id_"),
				" = [$SOURCE_TABLE_ALIAS$].storageId)");
		}

		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, sourceAdditionalWhereClause, "storageId", "DDMField",
				"classPK", "DDMStorageLink"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, sourceAdditionalWhereClause, "storageId",
				"DDMFieldAttribute", "classPK", "DDMStorageLink"));
	}

}