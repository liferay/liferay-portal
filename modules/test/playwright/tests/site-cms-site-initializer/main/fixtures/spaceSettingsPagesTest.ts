/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {backendPageTest} from '../../../../fixtures/backendPageTest';
import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {SpaceSettingsPage} from '../pages/SpaceSettingsPage';

const test = mergeTests(backendPageTest);

interface Space {
	externalReferenceCode: string;
	settings?: {trashEnabled?: boolean; [key: string]: unknown};
}

function isTrashEnabled(space: Space) {
	return space.settings?.trashEnabled ?? true;
}

async function getSpaces(apiHelpers: ApiHelpers): Promise<Space[]> {
	return (
		(await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
			"type eq 'Space'",
			-1
		)) ?? []
	);
}

async function setTrashEnabled(
	apiHelpers: ApiHelpers,
	space: Space,
	trashEnabled: boolean
) {
	await apiHelpers.headlessAssetLibrary.patchAssetLibrary(
		space.externalReferenceCode,
		{
			settings: {
				...space.settings,
				trashEnabled,
			},
		}
	);
}

const spaceSettingsPagesTest = test.extend<{
	disableOtherSpacesRecycleBin: (
		externalReferenceCode: string
	) => Promise<void>;
	spaceSettingsPage: SpaceSettingsPage;
}>({
	disableOtherSpacesRecycleBin: async ({backendPage}, use) => {
		const apiHelpers = new ApiHelpers(backendPage);

		const originalTrashEnabledValues = new Map<string, boolean>();

		for (const space of await getSpaces(apiHelpers)) {
			originalTrashEnabledValues.set(
				space.externalReferenceCode,
				isTrashEnabled(space)
			);
		}

		try {
			await use(async (externalReferenceCode: string) => {
				for (const space of await getSpaces(apiHelpers)) {
					if (
						space.externalReferenceCode === externalReferenceCode ||
						!isTrashEnabled(space)
					) {
						continue;
					}

					await setTrashEnabled(apiHelpers, space, false);
				}
			});
		}
		finally {
			for (const space of await getSpaces(apiHelpers)) {
				const trashEnabled =
					originalTrashEnabledValues.get(
						space.externalReferenceCode
					) ?? true;

				if (isTrashEnabled(space) !== trashEnabled) {
					await setTrashEnabled(apiHelpers, space, trashEnabled);
				}
			}
		}
	},
	spaceSettingsPage: async ({page}, use) => {
		await use(new SpaceSettingsPage(page));
	},
});

export {spaceSettingsPagesTest};
