/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page} from '@playwright/test';

import {PORTLET_URLS} from '../../utils/portletUrls';
import {DiagramViewPage} from './DiagramViewPage';
import {SourceViewPage} from './SourceViewPage';

export class ProcessBuilderPage {
	readonly configurationTab: Locator;
	readonly diagramViewPage: DiagramViewPage;
	readonly page: Page;
	readonly sourceViewPage: SourceViewPage;

	constructor(page: Page) {
		this.configurationTab = page.getByRole('link', {name: 'Configuration'});
		this.diagramViewPage = new DiagramViewPage(page);
		this.page = page;
		this.sourceViewPage = new SourceViewPage(page);
	}

	async clickWorkflowDefinitionName(name: string) {
		await this.page
			.getByRole('link', {
				exact: true,
				name,
			})
			.click();
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.processBuilder}`
		);
		await this.page.waitForLoadState('networkidle');
	}

	async switchToSourceViewAndBackToDiagram() {
		await this.diagramViewPage.clickSourceViewButton();

		await this.sourceViewPage.xmlFirstLine.waitFor({state: 'visible'});

		await this.sourceViewPage.clickDiagramViewButton();
	}

	async updateAccountWorkflow(currentWorkflow: string, newWorkflow: string) {
		await this.configurationTab.click();

		await this.page
			.getByRole('row', {name: `Account ${currentWorkflow} Edit`})
			.getByRole('button')
			.click();

		await this.page
			.locator(
				'[id="_com_liferay_portal_workflow_web_portlet_ControlPanelWorkflowPortlet_workflowDefinitionName-com-liferay-account-model-AccountEntry"]'
			)
			.selectOption(newWorkflow);

		await this.page.getByRole('button', {name: 'Save'}).click();
	}
}
