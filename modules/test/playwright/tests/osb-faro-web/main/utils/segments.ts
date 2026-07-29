/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect} from '@playwright/test';

import {clickAndExpectToBeVisible} from '../../../../utils/clickAndExpectToBeVisible';
import {waitForAlert} from '../../../../utils/waitForAlert';
import {SegmentConditions} from './selectors';
import {searchByTerm} from './utils';

export async function addNestedSegmentField({
	criterionName,
	criterionType,
	nestedSegmentField,
	page,
}: {
	criterionName: string;
	criterionType: string;
	nestedSegmentField: string;
	page: Page;
}) {
	await page.locator('button.dropdown-toggle.btn-outline-secondary').click();
	await page.getByRole('menuitem', {name: criterionType}).click();

	await dragAndDropCriteriaItem({
		nestedSegmentField,
		page,
		segmentField: criterionName,
	});
}

export async function addSegmentField({
	criterionName,
	criterionType,
	page,
}: {
	criterionName: string;
	criterionType: string;
	page: Page;
}) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('option', {name: criterionType}),
		trigger: page.getByRole('combobox'),
	});

	await dragAndDropCriteriaItem({
		page,
		segmentField: criterionName,
	});
}

export async function addStaticMember({
	memberNames,
	page,
}: {
	memberNames: string[] | string;
	page: Page;
}) {
	await page.getByRole('button', {name: 'Add Members'}).click();

	const memberNamesArray = Array.isArray(memberNames)
		? memberNames
		: [memberNames];

	for (const memberName of memberNamesArray) {
		await searchByTerm({
			page,
			searchTerm: memberName,
		});

		await page.locator('.clickable').getByText(memberName).first().click();
	}

	await page.getByRole('button', {exact: true, name: 'Add'}).click();
}

export async function createBatchSegment(page: Page) {
	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByTestId('batch-segment-dropdown-item'),
		trigger: page
			.getByRole('button', {name: 'Menu'})
			.filter({hasText: 'New Segment'}),
	});
}

export async function createDynamicSegment(page: Page) {
	await page.getByRole('banner').getByLabel('Menu').click();
	await page.getByRole('menuitem', {name: 'Dynamic Segment'}).click();
}

export async function createStaticSegment(page: Page) {
	await page.getByRole('banner').getByLabel('Menu').click();
	await page.getByRole('menuitem', {name: 'Static Segment'}).click();
}

export async function deleteSegment({
	page,
	segmentName,
}: {
	page: Page;
	segmentName: string;
}) {
	await searchByTerm({
		page,
		searchTerm: segmentName,
	});

	await page.locator('.dropdown-action').click();
	await page.locator('button.dropdown-item:has-text("Delete")').click();
	await page.getByRole('button', {name: 'Delete'}).click();
}

export async function dragAndDropCriteriaItem({
	nestedSegmentField,
	page,
	segmentField,
}: {
	nestedSegmentField?: string;
	page: Page;
	segmentField: string;
}) {
	const source = page
		.locator(`[data-testid*="criteria-item-"]`)
		.getByText(segmentField, {exact: true});

	let target: Locator;
	if (nestedSegmentField) {
		target = page.locator('.display-value').getByText(nestedSegmentField);
	}
	else {
		target = page
			.locator('.empty-drop-zone-target, div.drop-zone-target')
			.last();
	}

	return await source.dragTo(target);
}

export async function editCriteriaAttributeValue({
	attributeValue,
	index = 0,
	page,
}: {
	attributeValue: string;
	index?: number;
	page: Page;
}) {
	await page
		.locator('input[data-testid="attribute-value-string-input"]')
		.nth(index)
		.click();

	await page
		.locator('input[data-testid="attribute-value-string-input"]')
		.nth(index)
		.fill(attributeValue);
}

export async function editCriteriaConjunction({
	index = 0,
	page,
}: {
	index?: number;
	page: Page;
}) {
	await page.locator('.conjunction-button').nth(index).click();
}

export async function editSegment(page: Page) {
	await page.getByRole('link', {name: 'Edit Segment'}).click();
	await page.waitForSelector('text=Edit Individuals Segment');
}

export async function includeAnonymousToggle({
	enable,
	page,
}: {
	enable: boolean;
	page: Page;
}) {
	const toggle = page.getByTestId('toggle-switch-input');

	if (enable) {
		await toggle.check();
	}
	else {
		await toggle.uncheck();
	}
}

export async function saveSegment(page: Page) {
	await page.locator('button[type="submit"]').click();

	await waitForAlert(page, 'Success:Changes to segment saved.', {
		autoClose: false,
	});
}

export async function selectAsset({
	assetName,
	index = 0,
	page,
}: {
	assetName: string;
	index?: number;
	page: Page;
}) {
	await page.getByRole('button', {name: 'Select'}).nth(index).click();

	await page.getByPlaceholder('Search').first().click();
	await page.getByPlaceholder('Search').first().fill(assetName);
	await page.getByPlaceholder('Search').first().press('Enter');

	await page.locator('.table-title').getByText(assetName).click();

	await page.getByRole('button', {name: 'Add'}).click();
}

export async function selectOperator({
	index = 0,
	operator,
	operatorField,
	page,
}: {
	index?: number;
	operator: string;
	operatorField: SegmentConditions;
	page: Page;
}) {
	await page.locator(operatorField).nth(index).click();
	await page.getByRole('option', {name: operator}).click();
}

export async function setSegmentName({
	page,
	segmentName,
}: {
	page: Page;
	segmentName: string;
}) {
	const input = page.getByPlaceholder('Unnamed Segment');

	await expect(async () => {
		await page.getByLabel('Edit').click();

		await input.fill(segmentName, {timeout: 2000});

		await expect(input).toHaveValue(segmentName);
	}).toPass();

	await page.keyboard.press('Tab');
}

export async function viewSegmentCriteriaCard({
	criteriaRowIndex,
	criteriaRowValue,
	page,
	parent,
}: {
	criteriaRowIndex: number;
	criteriaRowValue: string;
	page: Page;
	parent?: Locator;
}) {
	let criteriaRowText;

	if (parent) {
		criteriaRowText = parent.locator('.criteria-row').nth(criteriaRowIndex);
	}
	else {
		criteriaRowText = page.locator('.criteria-row').nth(criteriaRowIndex);
	}

	criteriaRowText = await criteriaRowText.textContent();
	criteriaRowText = criteriaRowText.replace(/\s/g, '');

	criteriaRowValue = criteriaRowValue.replace(/\s/g, '');

	expect(criteriaRowText).toEqual(criteriaRowValue);
}

export async function viewSegmentMembershipCount({
	anonymousMemberCount,
	knownMemberCount,
	page,
	totalMemberCount,
}: {
	anonymousMemberCount: string;
	knownMemberCount: string;
	page: Page;
	totalMemberCount: string;
}) {
	await expect(
		page.locator('li').filter({hasText: 'Known Members:'}).locator('b')
	).toHaveText(knownMemberCount);

	await expect(
		page.locator('li').filter({hasText: 'Anonymous Members:'}).locator('b')
	).toHaveText(anonymousMemberCount);

	await expect(
		page.locator('li').filter({hasText: 'Total Members:'}).locator('b')
	).toHaveText(totalMemberCount);
}
