/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page} from '@playwright/test';

import {ApiHelpers} from '../../../../helpers/ApiHelpers';
import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {getDefaultProject} from './project';

export async function createChannel({
	apiHelpers,
	channelName,
}: {
	apiHelpers: ApiHelpers;
	channelName: string;
}): Promise<{
	channel: any;
	project: any;
}> {
	const project = await getDefaultProject(apiHelpers);

	const channel = await apiHelpers.jsonWebServicesOSBFaro.createChannel(
		channelName,
		project.groupId
	);

	return {
		channel,
		project,
	};
}

export async function switchChannel({
	channelName,
	page,
}: {
	channelName: string;
	page: Page;
}) {
	const searchInput = page.getByPlaceholder('Search');

	await clickAndExpectToBeVisible({
		target: searchInput,
		trigger: page.getByRole('combobox', {name: 'Property'}),
	});

	await searchInput.fill(channelName);

	await page.getByRole('option').click();
}
