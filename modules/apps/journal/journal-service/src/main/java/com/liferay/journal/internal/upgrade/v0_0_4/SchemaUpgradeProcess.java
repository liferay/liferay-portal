/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.upgrade.v0_0_4;

import com.liferay.portal.kernel.upgrade.MVCCVersionUpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Eduardo García
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		String template = StringUtil.read(
			SchemaUpgradeProcess.class.getResourceAsStream(
				"dependencies/update.sql"));

		runSQLTemplate(template, false);

		upgrade(new MVCCVersionUpgradeProcess());
	}

	@Override
	protected UpgradeStep[] getPostUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.alterColumnName(
				"JournalArticle", "structureId", "DDMStructureKey"),
			UpgradeProcessFactory.alterColumnName(
				"JournalArticle", "templateId", "DDMTemplateKey"),
			UpgradeProcessFactory.alterColumnType(
				"JournalArticle", "description", "TEXT null"),
			UpgradeProcessFactory.alterColumnName(
				"JournalFeed", "structureId", "DDMStructureKey"),
			UpgradeProcessFactory.alterColumnName(
				"JournalFeed", "templateId", "DDMTemplateKey"),
			UpgradeProcessFactory.alterColumnName(
				"JournalFeed", "rendererTemplateId", "DDMRendererTemplateKey"),
			UpgradeProcessFactory.alterColumnType(
				"JournalFeed", "targetPortletId", "VARCHAR(200) null")
		};
	}

}