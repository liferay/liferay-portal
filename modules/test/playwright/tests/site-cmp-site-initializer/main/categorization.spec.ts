/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	loginTest()
);

test(
	'Can create, read, update, and delete personas and funnel stages from a project',
	{tag: ['@LPD-93348']},
	async ({apiHelpers, editProjectPage, page, projectPage, projectsPage}) => {
		const projectTitle = getRandomString();

		const project = await apiHelpers.objectEntry.postObjectEntry(
			{title: projectTitle},
			'cmp/projects'
		);

		try {
			await test.step('Open the project', async () => {
				await projectsPage.goto();

				await projectsPage.getProject(projectTitle).click();
			});

			await test.step('Assign a persona and a funnel stage', async () => {
				await projectPage.editProject();

				await editProjectPage.selectCategory(
					'Personas',
					'Decision Maker'
				);

				await editProjectPage.selectCategory(
					'Funnel Stages',
					'Awareness'
				);

				await editProjectPage.saveButton.click();

				await waitForAlert(
					page,
					`Success:${projectTitle} was updated successfully.`
				);
			});

			await test.step('Read the assigned categories', async () => {
				await expect(
					projectPage.getInfoCategory('Decision Maker')
				).toBeVisible();

				await expect(
					projectPage.getInfoCategory('Awareness')
				).toBeVisible();
			});

			await test.step('Update the assigned categories', async () => {
				await projectPage.editProject();

				await editProjectPage.selectCategory('Personas', 'Champion');

				await editProjectPage.removeCategory(
					'Funnel Stages',
					'Awareness'
				);

				await editProjectPage.saveButton.click();

				await waitForAlert(
					page,
					`Success:${projectTitle} was updated successfully.`
				);

				await expect(
					projectPage.getInfoCategory('Champion')
				).toBeVisible();

				await expect(
					projectPage.getInfoCategory('Awareness')
				).toBeHidden();
			});

			await test.step('Remove the remaining categories', async () => {
				await projectPage.editProject();

				await editProjectPage.removeCategory(
					'Personas',
					'Decision Maker'
				);

				await editProjectPage.removeCategory('Personas', 'Champion');

				await editProjectPage.saveButton.click();

				await waitForAlert(
					page,
					`Success:${projectTitle} was updated successfully.`
				);

				await expect(
					projectPage.getInfoCategory('Decision Maker')
				).toBeHidden();

				await expect(
					projectPage.getInfoCategory('Champion')
				).toBeHidden();
			});
		}
		finally {
			await apiHelpers.objectEntry.deleteObjectEntry(
				'cmp/projects',
				String(project.id)
			);
		}
	}
);
