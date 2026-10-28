/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect} from '@playwright/test';

import {faroConfig} from '../faro.config';
import {waitForLoading} from './loading';

export enum ACPage {
	apisTokensPage = 'apis/tokens',
	assetPage = 'assets',
	dataSourcePage = 'data-source?field=name&page=1&sortOrder=DESC',
	dataPrivacyRequestLogPage = 'data-privacy/request-log',
	dataPrivacySupressedUsersPage = 'data-privacy/suppressed-users',
	definitionsIndividualAttributesPage = 'definitions/individual-attributes',
	definitionsEventAttributesGlobalPage = 'definitions/event-attributes/global',
	definitionsEventsCustomPage = 'definitions/events/custom',
	definitionsEventsBlockListPage = 'definitions/events/block-list',
	definitionsEventsDefaultPage = 'definitions/events/default',
	definitionsSearchPage = 'definitions/search',
	eventAnalysisPage = 'event-analysis',
	eventAttributesPage = 'definitions/event-attributes/global',
	individualPage = 'contacts/individuals',
	segmentPage = 'contacts/segments',
	sitePage = 'sites',
	propertiesPage = 'properties',
	testPage = 'tests',
	userManagementPage = 'users',
}

export async function navigateTo({
	page,
	pageName,
}: {
	page: Page;
	pageName: string;
}) {
	await page
		.getByRole('link', {name: pageName})
		.or(page.getByRole('menuitem', {name: pageName}))
		.first()
		.click();

	await waitForLoading(page);
}

export async function navigateToACAdmin({page}: {page: Page}) {
	await page.goto(`${faroConfig.environment.baseUrl}/admin`);
}

export async function navigateToACWorkspace({
	page,
	workspaceName = 'FARO-DEV-liferay Liferay Demo Liferay Data Platform Enterprise',
}: {
	page: Page;
	workspaceName?: string;
}) {
	await page.goto(faroConfig.environment.baseUrl);

	await page
		.getByRole('link', {
			name: workspaceName,
		})
		.click();
}

export async function navigateToACPageViaURL({
	acPage,
	channelID,
	page,
	projectID,
}: {
	acPage: ACPage;
	channelID: string;
	page: Page;
	projectID: string;
}) {
	await page.goto(
		`${faroConfig.environment.baseUrl}/workspace/${projectID}/${channelID}/${acPage}`
	);
}

export async function navigateToACIndividualProfileViaURL({
	channelID,
	individualId,
	page,
	projectID,
}: {
	channelID: string;
	individualId: string;
	page: Page;
	projectID: string;
}) {
	await page.goto(
		`${faroConfig.environment.baseUrl}/workspace/${projectID}/${channelID}/contacts/individuals/known-individuals/${individualId}`
	);
}

export async function openIndividualProfileViaURL({
	channelID,
	individualId,
	page,
	projectID,
}: {
	channelID: string;
	individualId: string;
	page: Page;
	projectID: string;
}) {
	await navigateToACIndividualProfileViaURL({
		channelID,
		individualId,
		page,
		projectID,
	});

	await waitForLoading(page);

	await expect(page.getByText('Individual Events')).toBeVisible();
}

export async function navigateToACSettingsViaURL({
	acPage,
	page,
	projectID,
}: {
	acPage: ACPage;
	page: Page;
	projectID: string;
}) {
	await page.goto(
		`${faroConfig.environment.baseUrl}/workspace/${projectID}/settings/${acPage}`
	);
}
