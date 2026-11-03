/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {changeManagementToolbarView} from '../../../utils/changeManagementToolbarView';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from './fixtures/journalPagesTest';

const test = mergeTests(
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest()
);

test(
	'Web content shows its status, version and icon in every view',
	{tag: ['@LPS-155805', '@LPS-197978', '@LPS-202596']},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const folderName = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addFolder({
			groupId: site.id,
			name: folderName,
		});

		const titles = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{title},
				site.id,
				webContent
			);
		}

		const draftTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			serviceContext: {workflowAction: 2},
			titleMap: {en_US: draftTitle},
		});

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		const rows = page.locator('[data-qa-id="row"]');

		await expect(rows.filter({hasText: folderName})).toBeVisible();
		await expect(rows.filter({hasText: titles[0]})).toBeHidden();

		await expect(journalPage.getArticleStatus(titles[2])).toHaveText(
			'Approved'
		);
		await expect(journalPage.getArticleStatus(draftTitle)).toHaveText(
			'Draft'
		);

		await expect(page.getByLabel(`Actions for ${titles[2]}`)).toHaveCSS(
			'color',
			'rgb(107, 108, 126)'
		);

		for (const view of ['Cards', 'Table', 'List']) {
			await changeManagementToolbarView(page, view);

			await expect(
				rows
					.filter({hasText: titles[2]})
					.locator('.lexicon-icon-web-content')
					.first()
			).toBeVisible();
		}

		// Filter by versions

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Versions'}),
			trigger: page.getByRole('button', {exact: true, name: 'Filter'}),
		});

		await expect(
			page.getByText('4 Results Found With Filters')
		).toBeVisible();

		for (const view of ['Cards', 'Table', 'List']) {
			await changeManagementToolbarView(page, view);

			await expect(rows.filter({hasText: folderName})).toBeHidden();

			for (const [index, title] of titles.entries()) {
				await expect(rows.filter({hasText: title})).toContainText(
					`Version 1.${index}`
				);
			}
		}
	}
);

test(
	'Info panel shows the status and the last ten versions of web content',
	{tag: '@LPS-191927'},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const titles = Array.from({length: 11}, () => getRandomString());

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{title},
				site.id,
				webContent
			);
		}

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await page
			.getByRole('checkbox', {name: `Select ${titles[10]}`})
			.check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		const infoPanel = page.locator('.sidebar');

		await expect(
			infoPanel.getByRole('heading', {name: titles[10]})
		).toBeVisible();
		await expect(
			infoPanel.getByText('Version 2.0', {exact: true}).first()
		).toBeVisible();
		await expect(
			infoPanel.getByText('Approved', {exact: true}).first()
		).toBeVisible();

		// Check the versions tab

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		const versionsPanel = infoPanel.getByRole('tabpanel');

		for (const version of [
			'1.1',
			'1.2',
			'1.3',
			'1.4',
			'1.5',
			'1.6',
			'1.7',
			'1.8',
			'1.9',
			'2.0',
		]) {
			await expect(
				versionsPanel.getByText(`Version ${version}`, {exact: true})
			).toBeVisible();
		}

		await expect(
			versionsPanel.getByText('Version 1.0', {exact: true})
		).toBeHidden();

		await versionsPanel.getByText('View More', {exact: true}).click();

		for (const title of titles) {
			await expect(
				page.getByRole('cell', {exact: true, name: title})
			).toBeVisible();
		}
	}
);

test(
	'A version of web content can be previewed and expired from the info panel',
	{tag: '@LPS-191927'},
	async ({apiHelpers, journalPage, page, site}) => {
		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const content = getRandomString();
		const titles = [
			getRandomString(),
			getRandomString(),
			getRandomString(),
		];

		let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
			content,
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: titles[0]},
		});

		for (const title of titles.slice(1)) {
			webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
				{content: getRandomString(), title},
				site.id,
				webContent
			);
		}

		await journalPage.goto(site.friendlyUrlPath);

		await changeManagementToolbarView(page, 'List');

		await page.getByRole('checkbox', {name: `Select ${titles[2]}`}).check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		const infoPanel = page.locator('.sidebar');

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		// Preview version 1.0

		const versionRow = (version: string) =>
			infoPanel.locator('li').filter({
				has: page.getByText(`Version ${version}`, {exact: true}),
			});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Preview'}),
			trigger: versionRow('1.0').getByRole('button'),
		});

		const modal = page.locator('.modal-dialog');

		await expect(modal.getByText(titles[0])).toBeVisible();
		await expect(
			modal.frameLocator('iframe').getByText(content)
		).toBeVisible();

		await modal.getByLabel('Close').click();

		// Expire version 1.1

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Expire'}),
			trigger: versionRow('1.1').getByRole('button'),
		});

		await waitForAlert(page);

		await page.getByRole('checkbox', {name: `Select ${titles[2]}`}).check();

		await page.getByRole('button', {name: 'Toggle Info Panel'}).click();

		await infoPanel.getByRole('tab', {name: 'Versions'}).click();

		await expect(versionRow('1.1')).toContainText('Expired');
	}
);
