/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js/v1_0';
import {mergeTests} from '@playwright/test';

import {backendPageTest} from '../../../../fixtures/backendPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {OBJECT_ENTITIES} from '../main/constants/objects';
import {PAGE_MANAGEMENT_SITE_ERC} from '../main/constants/site';

export const test = mergeTests(backendPageTest);

test('Teardown: Delete site and data for Page Management tests', async ({
	backendPage,
}) => {
	const apiHelpers = new ApiHelpers(backendPage);

	const {id: siteId} = await apiHelpers.headlessAdminSite.getSite(
		PAGE_MANAGEMENT_SITE_ERC
	);

	// Return if site does not exist, this is for cases in which this test is ran independently

	if (!siteId) {
		return;
	}

	// Delete object definitions

	const ERCs = Object.values(OBJECT_ENTITIES).map((entity) => entity.ERC);

	for (const ERC of ERCs) {
		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {id: objectDefinitionId} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				ERC
			)
		).body;

		if (objectDefinitionId) {
			await objectDefinitionAPIClient.deleteObjectDefinition(
				objectDefinitionId
			);
		}
	}

	// Delete site

	await apiHelpers.headlessAdminSite.deleteSite(PAGE_MANAGEMENT_SITE_ERC);
});
