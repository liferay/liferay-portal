/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../utils/clickAndExpectToBeVisible';
import {PORTLET_URLS} from '../../utils/portletUrls';
import {waitForAlert} from '../../utils/waitForAlert';

export class WorkflowPage {
	readonly page: Page;

	constructor(page: Page) {
		this.page = page;
	}

	async goto(siteUrl?: Site['friendlyUrlPath']) {
		await this.page.goto(
			`/group${siteUrl || '/guest'}${PORTLET_URLS.workflow}`
		);
	}

	async changeWorkflow(
		asset: string,
		value: 'Single Approver' | 'No Workflow',
		{disable} = {disable: false}
	) {
		const row = await this.page.getByRole('row').filter({hasText: asset});

		await clickAndExpectToBeVisible({
			target: row.getByRole('button', {name: 'Save'}),
			trigger: row.getByRole('button', {name: 'Edit'}),
		});

		await row
			.getByTitle('Workflow Definition')
			.selectOption({label: value});

		await this.page.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			this.page,
			disable
				? `Success:Workflow unassigned from ${asset}.`
				: `Success:Workflow assigned to ${asset}.`
		);
	}
}
