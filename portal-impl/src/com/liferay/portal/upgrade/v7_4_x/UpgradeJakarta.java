/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.v7_4_x;

import com.liferay.portal.kernel.upgrade.BaseJakartaUpgradeProcess;

/**
 * @author Luis Ortiz
 */
public class UpgradeJakarta extends BaseJakartaUpgradeProcess {

	@Override
	protected String[][] getTableAndColumnNames() {
		return new String[][] {
			{"Configuration_", "configurationId"},
			{"Configuration_", "dictionary"},
			{"DDMFieldAttribute", "largeAttributeValue"},
			{"DDMTemplate", "script"}, {"DDMTemplateVersion", "script"},
			{"DispatchTrigger", "dispatchTaskSettings"},
			{"ExportImportConfiguration", "settings_"},
			{"FragmentEntry", "configuration"}, {"FragmentEntry", "html"},
			{"FragmentEntryLink", "configuration"},
			{"FragmentEntryLink", "editableValues"},
			{"FragmentEntryLink", "html"},
			{"FragmentEntryVersion", "configuration"},
			{"FragmentEntryVersion", "html"}, {"KaleoAction", "script"},
			{"JournalArticle", "content"}, {"KaleoCondition", "script"},
			{"KaleoDefinition", "content"},
			{"KaleoDefinitionVersion", "content"},
			{"KaleoInstance", "workflowContext"},
			{"KaleoLog", "workflowContext"}, {"KaleoNotification", "template"},
			{"KaleoTaskAssignment", "assigneeScript"},
			{"KaleoTaskInstanceToken", "workflowContext"},
			{"ObjectAction", "parameters"}, {"ObjectValidationRule", "script"}
		};
	}

}