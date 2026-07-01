/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import EdgeInformation from './EdgeInformation';
import NodeInformation from './NodeInformation';
import Actions from './actions/Actions';
import ActionsSummary from './actions/ActionsSummary';
import AIHubAgentDefinition from './ai-hub-agent/AIHubAgentDefinition';
import Assignments from './assignments/Assignments';
import AssignmentsSummary from './assignments/AssignmentsSummary';
import SourceCode from './assignments/SourceCode';
import Authentication from './http-request/Authentication';
import ConnectionTimeout from './http-request/ConnectionTimeout';
import HTTPEndpoint from './http-request/HTTPEndpoint';
import Payload from './http-request/Payload';
import Variables from './http-request/Variables';
import Notifications from './notifications/Notifications';
import NotificationsSummary from './notifications/NotificationsSummary';
import PromptSummary from './prompt/PromptSummary';
import RAGSummary from './rag/RAGSummary';
import ServiceConfiguration from './service/ServiceConfiguration';
import TimerSourceCode from './timers/TimerSourceCode';
import Timers from './timers/Timers';
import TimersSummary from './timers/TimersSummary';
import ToolsSummary from './tools/ToolsSummary';

const sectionComponents = {
	actions: Actions,
	actionsSummary: ActionsSummary,
	aiHubAgentDefinition: AIHubAgentDefinition,
	assignments: Assignments,
	assignmentsSummary: AssignmentsSummary,
	authentication: Authentication,
	connectionTimeout: ConnectionTimeout,
	edgeInformation: EdgeInformation,
	httpEndpoint: HTTPEndpoint,
	nodeInformation: NodeInformation,
	notifications: Notifications,
	notificationsSummary: NotificationsSummary,
	payload: Payload,
	promptSummary: PromptSummary,
	ragSummary: RAGSummary,
	serviceConfiguration: ServiceConfiguration,
	sourceCode: SourceCode,
	timerSourceCode: TimerSourceCode,
	timers: Timers,
	timersSummary: TimersSummary,
	toolsSummary: ToolsSummary,
	variables: Variables,
};

export default sectionComponents;
