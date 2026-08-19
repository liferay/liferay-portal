/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {loginTest} from '../../../fixtures/loginTest';
import {virtualInstancesPagesTest} from '../../../fixtures/virtualInstancesPagesTest';
import getRandomString from '../../../utils/getRandomString';

const test = mergeTests(apiHelpersTest, loginTest(), virtualInstancesPagesTest);

test(
	'LPD-92620 Copying an instance to an existing company ID shows an error',
	{tag: '@LPD-92620'},
	async ({apiHelpers, virtualInstancesPage}) => {
		test.setTimeout(360000);

		const name = getRandomString();

		let created = false;

		try {
			await virtualInstancesPage.addNewVirtualInstance(name);

			created = true;

			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(name);

			await virtualInstancesPage.openCopyVirtualInstanceModal(name);

			// Reusing the source company ID as the destination collides with an
			// instance that already exists

			await virtualInstancesPage.submitCopyVirtualInstance({
				destinationCompanyId: String(company.companyId),
				name: getRandomString(),
				virtualHost: getRandomString(),
				webId: getRandomString(),
			});

			await expect(
				virtualInstancesPage.copyInstanceErrorMessage
			).toBeVisible();
		}
		finally {

			// A failed copy leaves the modal open, which blocks navigation

			if (
				await virtualInstancesPage.copyInstanceCancelButton.isVisible()
			) {
				await virtualInstancesPage.copyInstanceCancelButton.click();
			}

			if (created) {
				await virtualInstancesPage.deleteVirtualInstance(name);
			}
		}
	}
);

test(
	'LPD-92620 Copying an instance shows the copy success message',
	{tag: '@LPD-92620'},
	async ({virtualInstancesPage}) => {
		test.setTimeout(360000);

		const name = getRandomString();
		const copyWebId = getRandomString();

		let copied = false;
		let created = false;

		try {
			await virtualInstancesPage.addNewVirtualInstance(name);

			created = true;

			await virtualInstancesPage.openCopyVirtualInstanceModal(name);

			// A blank destination company ID copies into a new instance

			await virtualInstancesPage.submitCopyVirtualInstance({
				destinationCompanyId: '',
				name: copyWebId,
				timeout: 180 * 1000,
				virtualHost: copyWebId,
				webId: copyWebId,
			});

			await expect(
				virtualInstancesPage.copyInstanceSuccessMessage(copyWebId)
			).toBeVisible();

			copied = true;
		}
		finally {
			if (copied) {
				await virtualInstancesPage.deleteVirtualInstance(copyWebId);
			}

			if (created) {
				await virtualInstancesPage.deleteVirtualInstance(name);
			}
		}
	}
);
