/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {test} from '@playwright/test';

import {ManageCollaboratorsPage} from '../pages/sharing-web/ManageCollaboratorsPage';
import {SharePage} from '../pages/sharing-web/SharePage';
import {SharedContentPage} from '../pages/sharing-web/SharedContentPage';
import {SharedContentViewerPage} from '../pages/sharing-web/SharedContentViewerPage';
import {SharingNotificationPage} from '../pages/sharing-web/SharingNotificationPage';

export const sharingPagesTest = test.extend<{
	manageCollaboratorsPage: ManageCollaboratorsPage;
	sharePage: SharePage;
	sharedContentPage: SharedContentPage;
	sharedContentViewerPage: SharedContentViewerPage;
	sharingNotificationPage: SharingNotificationPage;
}>({
	manageCollaboratorsPage: async ({page}, use) => {
		await use(new ManageCollaboratorsPage(page));
	},
	sharePage: async ({page}, use) => {
		await use(new SharePage(page));
	},
	sharedContentPage: async ({page}, use) => {
		await use(new SharedContentPage(page));
	},
	sharedContentViewerPage: async ({page}, use) => {
		await use(new SharedContentViewerPage(page));
	},
	sharingNotificationPage: async ({page}, use) => {
		await use(new SharingNotificationPage(page));
	},
});
