/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.moderation.internal.configuration.persistence.listener;

import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.moderation.configuration.MBModerationGroupConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = "model.class.name=com.liferay.message.boards.moderation.configuration.MBModerationGroupConfiguration",
	service = ConfigurationModelListener.class
)
public class MBModerationGroupConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onAfterSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		MBModerationGroupConfiguration mbModerationGroupConfiguration =
			ConfigurableUtil.createConfigurable(
				MBModerationGroupConfiguration.class,
				new HashMapDictionary<>());

		long companyId = GetterUtil.getLong(properties.get("companyId"));
		boolean enableMessageBoardsModeration = GetterUtil.getBoolean(
			properties.get("enableMessageBoardsModeration"),
			mbModerationGroupConfiguration.enableMessageBoardsModeration());

		try {
			if (companyId == 0) {
				_companyLocalService.forEachCompanyId(
					curCompanyId -> _updateMBModerationWorkflow(
						curCompanyId, enableMessageBoardsModeration));
			}
			else {
				_updateMBModerationWorkflow(
					companyId, enableMessageBoardsModeration);
			}
		}
		catch (Exception exception) {
			throw new ConfigurationModelListenerException(
				exception.getMessage(), MBModerationGroupConfiguration.class,
				getClass(), properties);
		}
	}

	private void _updateMBModerationWorkflow(
			long companyId, boolean enableMessageBoardsModeration)
		throws Exception {

		if (!enableMessageBoardsModeration) {
			WorkflowDefinitionLink workflowDefinitionLink =
				_workflowDefinitionLinkLocalService.fetchWorkflowDefinitionLink(
					companyId, 0, MBMessage.class.getName(), 0, 0);

			if (workflowDefinitionLink != null) {
				_workflowDefinitionLinkLocalService.
					deleteWorkflowDefinitionLink(workflowDefinitionLink);
			}

			return;
		}

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
				companyId,
				WorkflowDefinitionConstants.
					NAME_MESSAGE_BOARDS_USER_STATS_MODERATION);

		_workflowDefinitionLinkLocalService.addWorkflowDefinitionLink(
			null, workflowDefinition.getUserId(), companyId, 0,
			MBMessage.class.getName(), 0, 0,
			WorkflowDefinitionConstants.
				NAME_MESSAGE_BOARDS_USER_STATS_MODERATION,
			workflowDefinition.getVersion());
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Reference
	private WorkflowDefinitionManager _workflowDefinitionManager;

}