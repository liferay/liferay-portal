/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.internal.upgrade.v1_3_2;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;

import java.sql.PreparedStatement;

/**
 * @author Rafael Praxedes
 */
public class KaleoClassNameAndKaleoClassPKUpgradeProcess
	extends UpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeKaleoClassNameAndKaleoClassPK(
			"KaleoAction", "kaleoNodeId", KaleoNode.class.getName());
		_upgradeKaleoClassNameAndKaleoClassPK(
			"KaleoLog", "kaleoNodeId", KaleoNode.class.getName());
		_upgradeKaleoClassNameAndKaleoClassPK(
			"KaleoNotification", "kaleoNodeId", KaleoNode.class.getName());
		_upgradeKaleoClassNameAndKaleoClassPK(
			"KaleoTaskAssignment", "kaleoTaskId", KaleoTask.class.getName());
	}

	private void _upgradeKaleoClassNameAndKaleoClassPK(
			String tableName, String columnName, String kaleoClassName)
		throws Exception {

		if (!hasColumn(tableName, columnName)) {
			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				StringBundler.concat(
					"update ", tableName,
					" set kaleoClassName = ?, kaleoClassPK = ? where ",
					"kaleoClassName is null and kaleoClassPK is null and ",
					columnName, " is not null and ", columnName, " > 0 "))) {

			preparedStatement.setString(1, kaleoClassName);
			preparedStatement.setString(2, columnName);

			preparedStatement.executeUpdate();
		}
	}

}