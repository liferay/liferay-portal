/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {cmsPagesTest} from '../../site-cms-site-initializer/main/fixtures/cmsPagesTest';
import {cmpPagesTest} from './fixtures/cmpPagesTest';

const test = mergeTests(
	cmpPagesTest,
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-58677': {enabled: true},
	}),
	loginTest()
);

test(
	'Project view tab navigation',
	{tag: ['@LPD-77908']},
	async ({
		editProjectPage,
		editTaskPage,
		page,
		projectPage,
		projectsPage,
	}) => {
		const projectTitle = getRandomString();

		try {
			await test.step('Access details tab and create a task using the "new task" button', async () => {
				await projectsPage.goto();

				await projectsPage.newProjectButton.click();

				await editProjectPage.titleInput.fill(projectTitle);

				await editProjectPage.saveButton.click();

				await projectPage.newTaskButton.click();

				await editTaskPage.titleInput.fill(getRandomString());

				await editTaskPage.saveButton.click();

				await expect(projectPage.detailsTab).toHaveAttribute(
					'aria-selected',
					'true'
				);
			});

			await test.step('Access tasks tab and create a task using the "new" button', async () => {
				await projectPage.tasksTab.click();

				await projectPage.newButton.click();

				await editTaskPage.titleInput.fill(getRandomString());

				await editTaskPage.saveButton.click();

				await expect(projectPage.tasksTab).toHaveAttribute(
					'aria-selected',
					'true'
				);
			});

			await test.step('Navigate to projects page using sidebar button', async () => {
				await page.getByRole('menuitem', {name: 'Projects'}).click();

				await projectsPage.getProject(projectTitle).click();

				await expect(projectPage.detailsTab).toHaveAttribute(
					'aria-selected',
					'true'
				);
			});
		}
		finally {
			await projectsPage.goto();

			await projectsPage.dataSetFragmentPage.execItemAction({
				action: 'Delete',
				filter: projectTitle,
			});

			await projectPage.deleteButton.click();
		}
	}
);
