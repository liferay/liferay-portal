/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v0_0_2;

import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.LoggingTimer;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Marcellus Tavares
 */
public class SchemaUpgradeProcess extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_updateSQL();

		_alterTables();
	}

	private void _alterTables() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			alterColumnName("DDMContent", "xml", "data_");

			alterColumnName("DDMStructure", "xsd", "definition");
			alterColumnType("DDMStructure", "description", "TEXT null");

			alterColumnType("DDMTemplate", "description", "TEXT null");
		}
	}

	private void _updateSQL() throws Exception {
		try (LoggingTimer loggingTimer = new LoggingTimer()) {
			String template = StringUtil.read(
				SchemaUpgradeProcess.class.getResourceAsStream(
					"dependencies/update.sql"));

			runSQLTemplate(template, false);
		}
	}

}