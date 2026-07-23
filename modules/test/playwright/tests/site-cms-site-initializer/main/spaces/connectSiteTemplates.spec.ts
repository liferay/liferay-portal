/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {loginTest} from '../../../../fixtures/loginTest';
import getRandomString from '../../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../../utils/performLogin';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {cmsPagesTest} from '../fixtures/cmsPagesTest';

const test = mergeTests(cmsPagesTest, dataApiHelpersTest, loginTest());

const templateLabel = (name: string) => `${name} (Site Template)`;

test(
	'Can connect and disconnect a site template alongside a connected site',
	{tag: '@LPD-88037'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const siteTemplateName = `Site Template ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			'L_GLOBAL'
		);

		const connectedSites = page.getByTestId(
			'space-summary-connected-sites'
		);

		const globalRowLocator = connectedSites.getByText('Global', {
			exact: true,
		});

		const templateRowLocator = connectedSites.getByText(
			templateLabel(siteTemplateName),
			{exact: true}
		);

		await spaceSummaryPage.goto(spaceName);

		await expect(globalRowLocator).toBeVisible();
		await expect(templateRowLocator).not.toBeVisible();

		await spaceSummaryPage.connectSiteTemplate(siteTemplateName);

		await expect(
			page.getByRole('heading', {name: 'Sites (2)'})
		).toBeVisible();
		await expect(globalRowLocator).toBeVisible();
		await expect(templateRowLocator).toBeVisible();

		await spaceSummaryPage.openConnectedSitesModal();
		await spaceSummaryPage.disconnectSiteFromModal({
			isSiteTemplate: true,
			siteName: siteTemplateName,
		});

		await spaceSummaryPage.closeButton.click();

		await expect(templateRowLocator).not.toBeVisible();
		await expect(globalRowLocator).toBeVisible();
	}
);

test(
	'Can toggle searchable content on a connected site template',
	{tag: '@LPD-88037'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const siteTemplateName = `Site Template ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.connectSiteTemplate(siteTemplateName);

		await spaceSummaryPage.openConnectedSitesModal();

		const templateRow = page
			.getByLabel('Connected Sites')
			.getByRole('listitem')
			.filter({
				has: page.getByText(templateLabel(siteTemplateName), {
					exact: true,
				}),
			});

		await expect(templateRow.getByText('Content: Yes')).toBeVisible();

		await templateRow.getByRole('button', {name: /Actions/i}).click();
		await page.getByRole('menuitem', {name: 'Make Unsearchable'}).click();

		await expect(templateRow.getByText('Content: No')).toBeVisible();

		await page.reload();

		await spaceSummaryPage.openConnectedSitesModal();

		await expect(templateRow.getByText('Content: No')).toBeVisible();
	}
);

test(
	'Read-only space member cannot connect or disconnect site templates',
	{tag: ['@LPD-88037', '@LPD-92500']},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const siteTemplateName = `Site Template ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.connectSiteTemplate(siteTemplateName);

		await spaceSummaryPage.addUserOrUserGroup(user.name, 'users');

		await performLogout(page);
		await performLoginViaApi({page, screenName: user.alternateName});

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.openConnectedSitesModal();

		const dialog = page.getByRole('dialog');

		await expect(
			dialog.getByLabel('Sites', {exact: true})
		).not.toBeVisible();
		await expect(
			dialog.getByRole('button', {exact: true, name: 'Connect'})
		).not.toBeVisible();

		await expect(
			dialog.getByText(templateLabel(siteTemplateName), {exact: true})
		).toBeVisible();
		await expect(
			dialog.getByRole('button', {name: /Actions/i})
		).not.toBeVisible();
	}
);

test(
	'Summary panel surfaces Make Unsearchable and Disconnect on a site template row',
	{tag: '@LPD-88037'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const siteTemplateName = `Site Template ${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{name: siteTemplateName}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.connectSiteTemplate(siteTemplateName);

		await page
			.getByTestId('space-summary-connected-sites')
			.getByRole('row')
			.filter({
				has: page.getByText(templateLabel(siteTemplateName), {
					exact: true,
				}),
			})
			.getByRole('button', {name: /Actions/i})
			.click();

		await expect(
			page.getByRole('menuitem', {name: 'Make Unsearchable'})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {name: 'Disconnect'})
		).toBeVisible();
	}
);

test(
	'Excludes already-connected sites and site templates from the autocomplete and clears the input',
	{tag: '@LPD-91266'},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const spaceName = `Space ${getRandomString()}`;
		const connectedTemplateName = `Site Template ${getRandomString()}`;
		const otherTemplateName = `Site Template ${getRandomString()}`;

		const space = await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName,
			settings: {logoColor: 'outline-3'},
			type: 'Space',
		});

		for (const name of [connectedTemplateName, otherTemplateName]) {
			const layoutSetPrototype =
				await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
					{name}
				);

			apiHelpers.data.push({
				id: layoutSetPrototype.layoutSetPrototypeId,
				type: 'layoutSetPrototype',
			});
		}

		await apiHelpers.headlessAssetLibrary.connectSite(
			space.externalReferenceCode,
			'L_GLOBAL'
		);

		await spaceSummaryPage.goto(spaceName);

		await spaceSummaryPage.openConnectedSitesModal();

		await page.getByLabel('Sites', {exact: true}).click();

		await page
			.getByRole('option', {exact: true, name: 'Site Templates'})
			.click();

		const siteTemplateAutocomplete = page.getByPlaceholder(
			'Select a Site Template',
			{exact: true}
		);

		await siteTemplateAutocomplete.click();
		await page
			.getByRole('option', {exact: true, name: connectedTemplateName})
			.click();
		await page.getByRole('button', {exact: true, name: 'Connect'}).click();

		await waitForAlert(
			page,
			`Success:Site template ${connectedTemplateName} was successfully connected to the space.`,
			{autoClose: false}
		);

		await page
			.getByLabel('Connected Sites')
			.getByText(templateLabel(connectedTemplateName), {exact: true})
			.waitFor();

		await expect(siteTemplateAutocomplete).toHaveValue('');

		await siteTemplateAutocomplete.click();

		await expect(
			page.getByRole('option', {exact: true, name: otherTemplateName})
		).toBeVisible();
		await expect(
			page.getByRole('option', {exact: true, name: connectedTemplateName})
		).toHaveCount(0);

		await page.getByLabel('Sites', {exact: true}).click();

		await page.getByRole('option', {exact: true, name: 'Sites'}).click();

		await page.getByPlaceholder('Select a Site', {exact: true}).click();

		await page.getByRole('option').first().waitFor();

		await expect(
			page.getByRole('option', {exact: true, name: 'Global'})
		).toHaveCount(0);
	}
);
