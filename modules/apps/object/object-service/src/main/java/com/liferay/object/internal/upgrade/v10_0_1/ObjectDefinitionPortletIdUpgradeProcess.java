/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_0_1;

import com.liferay.object.constants.ObjectPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.upgrade.BasePortletIdUpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Nathaly Gomes
 */
public class ObjectDefinitionPortletIdUpgradeProcess
	extends BasePortletIdUpgradeProcess {

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					"select objectDefinitionId, className from " +
						"ObjectDefinition where modifiable = [$TRUE$]"));
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				String className = resultSet.getString("className");

				long objectDefinitionId = resultSet.getLong(
					"objectDefinitionId");

				if (className.endsWith(StringPool.POUND + objectDefinitionId)) {
					continue;
				}

				_oldPortletId =
					ObjectPortletKeys.OBJECT_DEFINITIONS +
						StringPool.UNDERLINE + objectDefinitionId;
				_newPortletId =
					ObjectPortletKeys.OBJECT_DEFINITIONS +
						StringPool.UNDERLINE +
							StringUtil.split(className, StringPool.POUND)[1];

				super.doUpgrade();
			}
		}
	}

	@Override
	protected String[][] getRenamePortletIdsArray() {
		return new String[][] {{_oldPortletId, _newPortletId}};
	}

	private String _newPortletId;
	private String _oldPortletId;

}