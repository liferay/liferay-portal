/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ScriptManagementPage} from '../pages/portal-security-script-management-web/ScriptManagementPage';
import {ActionPage} from '../pages/portal-workflow-kaleo-designer-web/ActionPage';
import {ActionReassignmentPage} from '../pages/portal-workflow-kaleo-designer-web/ActionReassignmentPage';
import {ConditionNode} from '../pages/portal-workflow-kaleo-designer-web/ConditionNodePage';
import {ConfigurationTabPage} from '../pages/portal-workflow-kaleo-designer-web/ConfigurationTabPage';
import {DefinitionInfoPage} from '../pages/portal-workflow-kaleo-designer-web/DefinitionInfoPage';
import {DiagramViewPage} from '../pages/portal-workflow-kaleo-designer-web/DiagramViewPage';
import {NodePropertiesSidebarPage} from '../pages/portal-workflow-kaleo-designer-web/NodePropertiesSidebarPage';
import {NotificationSectionPage} from '../pages/portal-workflow-kaleo-designer-web/NotificationSectionPage';
import {ProcessBuilderPage} from '../pages/portal-workflow-kaleo-designer-web/ProcessBuilderPage';
import {SourceViewPage} from '../pages/portal-workflow-kaleo-designer-web/SourceViewPage';
import {TimerPage} from '../pages/portal-workflow-kaleo-designer-web/TimerPage';
import {TransitionInfoPage} from '../pages/portal-workflow-kaleo-designer-web/TransitionInfoPage';
import {AllItemsPage} from '../pages/portal-workflow-metrics-web/AllItemsPage';
import {MetricsPage} from '../pages/portal-workflow-metrics-web/MetricsPage';
import {ProcessMetricsPage} from '../pages/portal-workflow-metrics-web/ProcessMetricsPage';
import {WorkflowTaskDetailsPage} from '../pages/portal-workflow-task-web/WorkflowTaskDetailsPage';
import {WorkflowTasksPage} from '../pages/portal-workflow-task-web/WorkflowTasksPage';
import {WorkflowPage} from '../pages/portal-workflow-web/WorkflowPage';

const workflowPagesTest = test.extend<{
	actionPage: ActionPage;
	actionReassignmentPage: ActionReassignmentPage;
	allItemsPage: AllItemsPage;
	conditionNode: ConditionNode;
	configurationTabPage: ConfigurationTabPage;
	definitionInfoPage: DefinitionInfoPage;
	diagramViewPage: DiagramViewPage;
	metricsPage: MetricsPage;
	nodePropertiesSidebarPage: NodePropertiesSidebarPage;
	notificationSectionPage: NotificationSectionPage;
	processBuilderPage: ProcessBuilderPage;
	processMetricsPage: ProcessMetricsPage;
	scriptManagementPage: ScriptManagementPage;
	sourceViewPage: SourceViewPage;
	timerPage: TimerPage;
	transitionInfoPage: TransitionInfoPage;
	workflowPage: WorkflowPage;
	workflowTaskDetailsPage: WorkflowTaskDetailsPage;
	workflowTasksPage: WorkflowTasksPage;
}>({
	actionPage: async ({page}, use) => {
		await use(new ActionPage(page));
	},
	actionReassignmentPage: async ({page}, use) => {
		await use(new ActionReassignmentPage(page));
	},
	allItemsPage: async ({page}, use) => {
		await use(new AllItemsPage(page));
	},
	conditionNode: async ({page}, use) => {
		await use(new ConditionNode(page));
	},
	configurationTabPage: async ({page}, use) => {
		await use(new ConfigurationTabPage(page));
	},
	definitionInfoPage: async ({page}, use) => {
		await use(new DefinitionInfoPage(page));
	},
	diagramViewPage: async ({page}, use) => {
		await use(new DiagramViewPage(page));
	},
	metricsPage: async ({page}, use) => {
		await use(new MetricsPage(page));
	},
	nodePropertiesSidebarPage: async ({page}, use) => {
		await use(new NodePropertiesSidebarPage(page));
	},
	notificationSectionPage: async ({page}, use) => {
		await use(new NotificationSectionPage(page));
	},
	processBuilderPage: async ({page}, use) => {
		await use(new ProcessBuilderPage(page));
	},
	processMetricsPage: async ({page}, use) => {
		await use(new ProcessMetricsPage(page));
	},
	scriptManagementPage: async ({page}, use) => {
		await use(new ScriptManagementPage(page));
	},
	sourceViewPage: async ({page}, use) => {
		await use(new SourceViewPage(page));
	},
	timerPage: async ({page}, use) => {
		await use(new TimerPage(page));
	},
	transitionInfoPage: async ({page}, use) => {
		await use(new TransitionInfoPage(page));
	},
	workflowPage: async ({page}, use) => {
		await use(new WorkflowPage(page));
	},
	workflowTaskDetailsPage: async ({page}, use) => {
		await use(new WorkflowTaskDetailsPage(page));
	},
	workflowTasksPage: async ({page}, use) => {
		await use(new WorkflowTasksPage(page));
	},
});

export {workflowPagesTest};
