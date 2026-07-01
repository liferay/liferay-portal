/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {AgentDefinition} from './types/AgentDefinition';

// Mirrors com.liferay.portal.kernel.workflow.WorkflowConstants.STATUS_DRAFT.
// Sending it as the object entry status drives ACTION_SAVE_DRAFT, which keeps
// the entry a draft and skips required-field validation.

export const WORKFLOW_STATUS_DRAFT = 2;

export const DEFAULT_AGENT_DEFINITION: Omit<
	AgentDefinition,
	'r_accountToAIHubAgentDefinitions_accountEntryERC'
> = {
	active: false,
	description: '',
	externalReferenceCode: '',
	inputVariables: '',
	outputVariable: '',
	title_i18n: {},
	workflowDefinitionName: '',
};
