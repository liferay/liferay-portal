/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests, test} from '@playwright/test';

import {loginTest} from '../../../../fixtures/loginTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {EditProjectPage} from '../pages/EditProjectPage';
import {EditTaskPage} from '../pages/EditTaskPage';
import {ProjectPage} from '../pages/ProjectPage';
import {ProjectsPage} from '../pages/ProjectsPage';
import {TasksPage} from '../pages/TasksPage';

const cmpPages = test.extend<{
	cmpSetup;
	editProjectPage: EditProjectPage;
	editTaskPage: EditTaskPage;
	projectPage: ProjectPage;
	projectsPage: ProjectsPage;
	tasksPage: TasksPage;
}>({
	cmpSetup: [
		async ({page}, use) => {
			const apiHelpers = new ApiHelpers(page);

			await apiHelpers.objectAdmin.waitForObjectDefinition('CMPProject');
			await apiHelpers.objectAdmin.waitForObjectDefinition('CMPTask');

			await use();
		},
		{auto: true},
	],
	editProjectPage: async ({page}, use) => {
		await use(new EditProjectPage(page));
	},
	editTaskPage: async ({page}, use) => {
		await use(new EditTaskPage(page));
	},
	projectPage: async ({page}, use) => {
		await use(new ProjectPage(page));
	},
	projectsPage: async ({page}, use) => {
		await use(new ProjectsPage(page));
	},
	tasksPage: async ({page}, use) => {
		await use(new TasksPage(page));
	},
});

const cmpPagesTest = mergeTests(loginTest(), cmpPages);

export {cmpPagesTest};
