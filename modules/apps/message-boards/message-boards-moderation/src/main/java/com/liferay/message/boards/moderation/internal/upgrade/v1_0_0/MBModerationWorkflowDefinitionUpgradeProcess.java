/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.moderation.internal.upgrade.v1_0_0;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

/**
 * @author Eduardo García
 */
public class MBModerationWorkflowDefinitionUpgradeProcess
	extends UpgradeProcess {

	public MBModerationWorkflowDefinitionUpgradeProcess(
		CompanyLocalService companyLocalService,
		WorkflowDefinitionManager workflowDefinitionManager) {

		_companyLocalService = companyLocalService;
		_workflowDefinitionManager = workflowDefinitionManager;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_companyLocalService.forEachCompanyId(
			companyId -> updateMBModerationWorkflowDefinition(companyId));
	}

	protected void updateMBModerationWorkflowDefinition(long companyId)
		throws Exception {

		int workflowDefinitionsCount =
			_workflowDefinitionManager.getWorkflowDefinitionsCount(
				companyId,
				WorkflowDefinitionConstants.
					NAME_MESSAGE_BOARDS_USER_STATS_MODERATION);

		if (workflowDefinitionsCount == 0) {
			return;
		}

		WorkflowDefinition latestWorkflowDefinition =
			_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
				companyId,
				WorkflowDefinitionConstants.
					NAME_MESSAGE_BOARDS_USER_STATS_MODERATION);

		String content = StringUtil.read(
			MBModerationWorkflowDefinitionUpgradeProcess.class,
			"dependencies/message-boards-moderation-workflow-definition.xml");

		try {
			_workflowDefinitionManager.deployWorkflowDefinition(
				null, companyId, latestWorkflowDefinition.getUserId(),
				latestWorkflowDefinition.getTitle(),
				latestWorkflowDefinition.getName(),
				latestWorkflowDefinition.getScope(), content.getBytes());
		}
		catch (WorkflowException workflowException) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Unable to upgrade workflow definition with name " +
						latestWorkflowDefinition.getName(),
					workflowException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBModerationWorkflowDefinitionUpgradeProcess.class);

	private final CompanyLocalService _companyLocalService;
	private final WorkflowDefinitionManager _workflowDefinitionManager;

}