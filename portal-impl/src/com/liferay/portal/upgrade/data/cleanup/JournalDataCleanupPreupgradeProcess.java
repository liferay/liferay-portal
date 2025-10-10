/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.data.cleanup.DataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.TableOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;

/**
 * @author Luis Ortiz
 */
public class JournalDataCleanupPreupgradeProcess
	extends DataCleanupPreupgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		upgrade(
			new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					OrphanReferencesDataCleanupUtil.SOURCE_TABLE_ALIAS,
					".classNameId = (select classNameId from ClassName_ where ",
					"value = 'com.liferay.journal.model.JournalArticle')"),
				new String[] {"classNameId"}, "classPK",
				new String[] {"resourcePrimKey", "id_"}, "JournalArticle"));
		upgrade(
			new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
				StringBundler.concat(
					OrphanReferencesDataCleanupUtil.SOURCE_TABLE_ALIAS,
					".classNameId = (select classNameId from ClassName_ where ",
					"value = 'com.liferay.journal.model.JournalFeed')"),
				new String[] {"classNameId"}, "classPK", new String[] {"id_"},
				"JournalFeed"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, "articlePK", "JournalArticleLocalization", "id_",
				"JournalArticle"));
		upgrade(
			new TableOrphanReferencesDataCleanupPreupgradeProcess(
				null, "resourcePrimKey", "JournalArticleResource",
				"resourcePrimKey", "JournalArticle"));
	}

}