/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.script.management.web.internal.upgrade.v1_2_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.dao.orm.common.SQLTransformer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.script.management.configuration.ScriptManagementConfiguration;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.definition.groovy.script.use.WorkflowDefinitionGroovyScriptUseDetector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Alberto Sousa
 */
public class ScriptManagementConfigurationUpgradeProcess
	extends UpgradeProcess {

	public ScriptManagementConfigurationUpgradeProcess(
		ConfigurationProvider configurationProvider, JSONFactory jsonFactory) {

		_configurationProvider = configurationProvider;
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		ScriptManagementConfiguration scriptManagementConfiguration =
			_configurationProvider.getSystemConfiguration(
				ScriptManagementConfiguration.class);

		if (scriptManagementConfiguration.
				allowScriptContentToBeExecutedOrIncluded() ||
			!_hasWorkflowDefinitionScriptUses()) {

			return;
		}

		_configurationProvider.saveSystemConfiguration(
			ScriptManagementConfiguration.class,
			HashMapDictionaryBuilder.<String, Object>put(
				"allowScriptContentToBeExecutedOrIncluded", true
			).build());
	}

	private boolean _hasWorkflowDefinitionScriptUses() throws Exception {
		try (PreparedStatement preparedStatement = connection.prepareStatement(
				SQLTransformer.transform(
					StringBundler.concat(
						"select KaleoDefinition.content from KaleoDefinition ",
						"where KaleoDefinition.externalReferenceCode != ? and ",
						"KaleoDefinition.active_ = [$TRUE$]")))) {

			preparedStatement.setString(
				1,
				WorkflowDefinitionConstants.
					EXTERNAL_REFERENCE_CODE_MESSAGE_BOARDS_USER_STATS_MODERATION);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					if (WorkflowDefinitionGroovyScriptUseDetector.detect(
							resultSet.getString("content"), _jsonFactory)) {

						return true;
					}
				}
			}
		}

		return false;
	}

	private final ConfigurationProvider _configurationProvider;
	private final JSONFactory _jsonFactory;

}