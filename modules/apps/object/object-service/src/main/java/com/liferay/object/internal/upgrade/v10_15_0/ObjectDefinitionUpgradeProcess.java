/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.upgrade.v10_15_0;

import com.liferay.friendly.url.configuration.manager.FriendlyURLSeparatorConfigurationManager;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.model.ObjectEntry;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.constants.FriendlyURLResolverConstants;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.UpgradeProcessFactory;
import com.liferay.portal.kernel.upgrade.UpgradeStep;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Carolina Barbosa
 */
public class ObjectDefinitionUpgradeProcess extends UpgradeProcess {

	public ObjectDefinitionUpgradeProcess(
		FriendlyURLSeparatorConfigurationManager
			friendlyURLSeparatorConfigurationManager) {

		_friendlyURLSeparatorConfigurationManager =
			friendlyURLSeparatorConfigurationManager;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement preparedStatement1 = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select companyId, objectDefinitionId from ",
						"ObjectDefinition where friendlyURLSeparator is null ",
						"and modifiable = [$TRUE$] and storageType ='",
						ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT, "'")));
			PreparedStatement preparedStatement2 = connection.prepareStatement(
				"update ObjectDefinition set friendlyURLSeparator = ? where " +
					"objectDefinitionId = ?");
			ResultSet resultSet = preparedStatement1.executeQuery()) {

			while (resultSet.next()) {
				JSONObject friendlyURLSeparatorsJSONObject =
					_friendlyURLSeparatorConfigurationManager.
						getFriendlyURLSeparatorsJSONObject(
							resultSet.getLong("companyId"));

				String friendlyURLSeparator =
					friendlyURLSeparatorsJSONObject.getString(
						ObjectEntry.class.getName());

				if (Validator.isNull(friendlyURLSeparator)) {
					friendlyURLSeparator =
						FriendlyURLResolverConstants.URL_SEPARATOR_OBJECT_ENTRY;
				}

				preparedStatement2.setString(
					1,
					StringUtil.removeSubstring(
						friendlyURLSeparator, StringPool.SLASH));

				preparedStatement2.setLong(
					2, resultSet.getLong("objectDefinitionId"));

				preparedStatement2.addBatch();
			}

			preparedStatement2.executeBatch();
		}
	}

	@Override
	protected UpgradeStep[] getPreUpgradeSteps() {
		return new UpgradeStep[] {
			UpgradeProcessFactory.addColumns(
				"ObjectDefinition", "friendlyURLSeparator VARCHAR(75) null")
		};
	}

	private final FriendlyURLSeparatorConfigurationManager
		_friendlyURLSeparatorConfigurationManager;

}