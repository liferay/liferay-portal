/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi} from '../../../utils/performLogin';
import {localizationPagesTest} from '../../site-admin-web/main/fixtures/localizationPagesTest';

const DEFAULT_VIRTUAL_INSTANCE_NAME = 'www.able.com';
const VIRTUAL_INSTANCE_DOMAIN = 'able.com';
const VIRTUAL_INSTANCE_FULL_URL = `http://${DEFAULT_VIRTUAL_INSTANCE_NAME}:8080`;

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-39304': {enabled: true},
	}),
	loginTest(),
	localizationPagesTest
);

test(
	'Change localization after Site Template is added in virtual instance',
	{tag: ['@LPS-180299']},
	async ({apiHelpers, localizationInstanceSettingsPage, page}) => {
		const virtualInstance =
			await apiHelpers.headlessPortalInstance.addVirtualInstance({
				domain: VIRTUAL_INSTANCE_DOMAIN,
				portalInstanceId: DEFAULT_VIRTUAL_INSTANCE_NAME,
				virtualHost: DEFAULT_VIRTUAL_INSTANCE_NAME,
			});
		apiHelpers.data.push({
			id: virtualInstance.portalInstanceId,
			type: 'virtual-instance',
		});

		await performLoginViaApi({
			domain: `@${VIRTUAL_INSTANCE_DOMAIN}`,
			loginUrl: VIRTUAL_INSTANCE_FULL_URL,
			page,
			screenName: 'test',
		});

		await localizationInstanceSettingsPage.goto('Language', false);
		await localizationInstanceSettingsPage.setLanguage(['en_US']);

		await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
			{
				layoutsUpdateable: false,
				name: getRandomString(),
				url: VIRTUAL_INSTANCE_FULL_URL,
			}
		);

		await localizationInstanceSettingsPage.goto('Language', false);
		await localizationInstanceSettingsPage.setLanguage(['en_US', 'es_ES']);
	}
);
