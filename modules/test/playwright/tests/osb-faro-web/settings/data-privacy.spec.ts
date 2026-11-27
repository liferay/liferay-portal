/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {isolatedChannelTest} from '../../../fixtures/isolatedChannelTest';
import {loginAnalyticsCloudTest} from '../../../fixtures/loginAnalyticsCloudTest';
import {loginTest} from '../../../fixtures/loginTest';
import {faroConfig} from '../main/faro.config';
import {createIndividuals} from '../main/utils/individuals';

const test = mergeTests(
	apiHelpersTest,
	isolatedChannelTest,
	loginAnalyticsCloudTest(),
	loginTest()
);

test(
	'Access, delete, and suppress data control requests can be created by email',
	{
		tag: ['@LRAC-8119', '@LRAC-8117', '@LRAC-8109'],
	},
	async ({apiHelpers, page, project}) => {

		// Checking Delete also flags Suppress, so the two requests below cover
		// all three request types.

		const requests = [
			{jobType: 'Access', name: `dcpaccess${Date.now()}`},
			{jobType: 'Delete', name: `dcpdelete${Date.now()}`},
		];

		await createIndividuals({
			apiHelpers,
			individuals: requests.map(({name}) => ({id: name, name})),
		});

		await page.goto(
			`${faroConfig.environment.baseUrl}/workspace/${project.groupId}/settings/data-privacy/request-log`
		);

		for (const {jobType, name} of requests) {
			const email = `${name}@liferay.com`;

			await page.getByRole('button', {name: 'Create Request'}).click();

			await page.getByRole('checkbox', {name: jobType}).check();

			const emailInput = page.getByPlaceholder('example@email.com');

			const emailOption = page.getByRole('option', {name: email});

			await expect(async () => {
				await emailInput.fill('');
				await emailInput.fill(email);

				await expect(emailOption).toBeVisible({timeout: 5000});
			}).toPass();

			await emailOption.click();

			await page.getByRole('button', {name: 'Save'}).click();

			await expect(
				page.getByRole('cell', {name: email}).first()
			).toBeVisible();
		}
	}
);
