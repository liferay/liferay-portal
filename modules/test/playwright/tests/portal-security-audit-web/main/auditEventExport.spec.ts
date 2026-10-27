/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {readFileSync, statSync} from 'fs';

import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getTempDir} from '../../../utils/temp';

export const test = mergeTests(loginTest(), globalMenuPagesTest);

const AUDIT_PORTLET_NAMESPACE =
	'_com_liferay_portal_security_audit_web_portlet_AuditPortlet_';

const dateFields = [
	'endDateAmPm',
	'endDateDay',
	'endDateHour',
	'endDateMinute',
	'endDateMonth',
	'endDateYear',
	'startDateAmPm',
	'startDateDay',
	'startDateHour',
	'startDateMinute',
	'startDateMonth',
	'startDateYear',
];

const fields = [
	'className',
	'classPK',
	'clientHost',
	'clientIP',
	'eventType',
	'serverName',
	'userName',
	'groupId',
	'serverPort',
	'userId',
];

async function openAdvancedSearch(page: Page) {
	await expect(async () => {
		await page
			.locator('#toggle_id_audit_event_searchtoggleAdvanced')
			.click({timeout: 1000});

		await expect(
			page.locator('#toggle_id_audit_event_searchadvancedSearch')
		).toHaveValue('true', {timeout: 1000});
	}).toPass();
}

function parseCSV(content: string) {
	const rows: string[][] = [];

	let quoted = false;
	let row: string[] = [];
	let value = '';

	for (let index = 0; index < content.length; index++) {
		const character = content[index];

		if (quoted) {
			if (character !== '"') {
				value += character;
			}
			else if (content[index + 1] === '"') {
				value += character;

				index++;
			}
			else {
				quoted = false;
			}
		}
		else if (character === '"') {
			quoted = true;
		}
		else if (character === ',') {
			row.push(value);

			value = '';
		}
		else if (character === '\n') {
			row.push(value);
			rows.push(row);

			row = [];
			value = '';
		}
		else {
			value += character;
		}
	}

	if (row.length || value) {
		row.push(value);
		rows.push(row);
	}

	return rows;
}

test('LPD-55895: Check if the additional information field is exported correctly in the .csv', async ({
	globalMenuPage,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	await globalMenuPage.goToControlPanel('Audit');

	await page.locator('#toggle_id_audit_event_searchtoggleAdvanced').click();

	await page
		.locator(`#${AUDIT_PORTLET_NAMESPACE}eventType:visible`)
		.fill('UPDATE');

	await expect(
		page.locator('#main-content .lexicon-icon-search')
	).toBeVisible();

	await page.locator('#main-content .lexicon-icon-search').click();

	await page.waitForTimeout(500);

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			name: 'Export Audit Events',
		}),
		trigger: page.getByLabel('Options'),
	});

	const downloadPromise = await page.waitForEvent('download');

	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();

	await download.saveAs(filePath);

	const [columns, ...rows] = parseCSV(readFileSync(filePath, 'utf8'));

	const additionalInfos = rows
		.map((row) => row[columns.indexOf('additionalInfo')])
		.filter(Boolean);

	expect(additionalInfos).not.toHaveLength(0);

	for (const additionalInfo of additionalInfos) {
		expect(() => JSON.parse(additionalInfo)).not.toThrow();
	}
});

test('LPD-40224: Check if the export audit events .csv is being filtered by the search fields', async ({
	globalMenuPage,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	await globalMenuPage.goToControlPanel('Audit');

	await openAdvancedSearch(page);

	// Filter the events by the first element, use the information to make assertions in the .csv

	const firstEventResourceId = await page
		.locator('td.lfr-resource-id-column')
		.first();

	const resourceId = (await firstEventResourceId.textContent()).trim();

	const firstEventResourceAction = await page
		.locator('td.lfr-resource-action-column')
		.first();

	const resourceAction = (
		await firstEventResourceAction.textContent()
	).trim();

	await page
		.locator(`#${AUDIT_PORTLET_NAMESPACE}classPK:visible`)
		.fill(resourceId);

	await page
		.locator(`#${AUDIT_PORTLET_NAMESPACE}eventType:visible`)
		.fill(resourceAction);

	await expect(
		page.locator('#main-content .lexicon-icon-search')
	).toBeVisible();

	await page.locator('#main-content .lexicon-icon-search').click();

	await page.waitForTimeout(500);

	// Populate map with all the date parameters

	const dateValues = {};

	for (const field of dateFields) {
		const inputElement = await page.locator(`#${field}`);
		const inputValue = await inputElement.inputValue();

		dateValues[field] = inputValue;
	}

	// On the export request, check if the body has all parameters

	await page.on('request', async (request) => {
		if (request.url().includes('export_audit_events')) {
			const requestBody = request.postData();

			for (const field of dateFields) {
				expect(requestBody).toContain(
					`${AUDIT_PORTLET_NAMESPACE + field}=${dateValues[field]}`
				);
			}

			for (const field of fields) {
				expect(requestBody).toContain(AUDIT_PORTLET_NAMESPACE + field);
			}
		}
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			name: 'Export Audit Events',
		}),
		trigger: page.getByLabel('Options'),
	});

	const downloadPromise = await page.waitForEvent('download');

	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();

	await download.saveAs(filePath);

	const [columns, ...rows] = parseCSV(readFileSync(filePath, 'utf8'));

	const eventCount = await page
		.locator('table.table > tbody tr:not(.d-none)')
		.count();

	expect(rows.length).toBeGreaterThanOrEqual(eventCount);

	for (const row of rows) {
		expect(row[columns.indexOf('classPK')]).toBe(resourceId);
		expect(row[columns.indexOf('eventType')]).toBe(resourceAction);
	}
});

test('LPD-40224: Check if the audit events filtered by date are being exported', async ({
	globalMenuPage,
	page,
}) => {
	page.on('dialog', (dialog) => dialog.accept());

	await globalMenuPage.goToControlPanel('Audit');

	await openAdvancedSearch(page);

	await page.locator('#startDate').fill('01/01/2001');

	await page.locator('#endDate').fill('01/01/2001');

	await page.locator('#endDate').press('Tab');

	await page.waitForTimeout(500);

	await clickAndExpectToBeVisible({
		target: page.getByText('There are no events.'),
		trigger: page.locator('#main-content .lexicon-icon-search'),
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {
			name: 'Export Audit Events',
		}),
		trigger: page.getByLabel('Options'),
	});

	const downloadPromise = await page.waitForEvent('download');

	const download = await downloadPromise;

	const filePath = getTempDir() + download.suggestedFilename();

	await download.saveAs(filePath);

	expect(statSync(filePath).size).toBe(0);
});

test("LPS-192555: Assert that the page's URL with advanced search doesn't get over 2048 characters", async ({
	globalMenuPage,
	page,
}) => {
	await globalMenuPage.goToControlPanel('Audit');

	await openAdvancedSearch(page);

	await page
		.locator(`#${AUDIT_PORTLET_NAMESPACE}userName:visible`)
		.fill('test test');

	await page
		.locator(`#${AUDIT_PORTLET_NAMESPACE}eventType:visible`)
		.fill('LOGIN');

	await page.locator('#main-content .lexicon-icon-search').click();

	await page.waitForTimeout(500);

	const pageURL = page.url();

	expect(pageURL.length).toBeLessThan(2048);
});
