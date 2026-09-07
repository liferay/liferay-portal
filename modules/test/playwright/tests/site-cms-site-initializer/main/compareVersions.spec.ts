/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FrameLocator,
	Locator,
	Page,
	expect,
	mergeTests,
} from '@playwright/test';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-56634': {enabled: true},
	}),
	loginTest(),
	structureBuilderPagesTest
);

const PICKLIST = 'CMS Bulk Action Statuses';

function getDiffBox(frame: FrameLocator, fieldName: string): Locator {
	return frame.locator(
		`[data-field-name="ObjectField_${fieldName}"] .cms-compare-versions-diff`
	);
}

async function expectDiffBoxToShow(
	frame: FrameLocator,
	fieldName: string,
	value: string
) {
	await expect(async () => {
		const text = await getDiffBox(frame, fieldName).innerText({
			timeout: 5000,
		});

		expect(text.replace(/\s+/g, ' ')).toContain(value);
	}).toPass({timeout: 30000});
}

async function selectLanguageOption(
	page: Page,
	trigger: Locator,
	languageId: string
) {
	const option = page.getByRole('option').filter({hasText: languageId});

	await expect(async () => {
		if ((await trigger.getAttribute('aria-expanded')) === 'true') {
			await page.keyboard.press('Escape');
		}

		await trigger.click({timeout: 2000});

		await expect(option).toBeVisible({timeout: 3000});
	}).toPass({timeout: 30000});

	await option.click();
}

async function selectPicklistOption(
	page: Page,
	fieldLabel: string,
	option: string
) {
	await page.getByRole('combobox', {exact: true, name: fieldLabel}).click();

	await page.getByRole('option', {name: option}).click();
}

async function uploadAttachment(page: Page, fileName: string) {
	const fileChooserPromise = page.waitForEvent('filechooser');

	await page.getByRole('button', {exact: true, name: 'Select File'}).click();

	const fileChooser = await fileChooserPromise;

	await fileChooser.setFiles(path.join(__dirname, 'dependencies', fileName));

	await expect(page.getByText(fileName)).toBeVisible();
}

test(
	'Compares every field type against the previous version',
	{tag: '@LPD-101811'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const structureLabel = `Zoo${getRandomInt()}`;
		const contentTitle = `zoo content ${getRandomString()}`;
		const revisedTitle = `${contentTitle} revised`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});
		});

		await test.step('Create a structure with every field type', async () => {
			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				name: structureLabel,
				page: structureBuilderPage,
			});

			const fields: [
				Parameters<typeof structureBuilderPage.addField>[0],
				string,
				{multiselection?: boolean; picklist?: string},
			][] = [
				['Text', 'Words', {}],
				['Long Text', 'Essay', {}],
				['Rich Text', 'Story', {}],
				['Numeric', 'Amount', {}],
				['Decimal', 'Ratio', {}],
				['Date', 'Day', {}],
				['Date and Time', 'Moment', {}],
				['Boolean', 'Flag', {}],
				['Select from List', 'State', {picklist: PICKLIST}],
				[
					'Select from List',
					'Tags',
					{multiselection: true, picklist: PICKLIST},
				],
				['Upload', 'Attachment', {}],
			];

			for (const [type, label, settings] of fields) {
				await structureBuilderPage.addField(type);

				await structureBuilderPage.changeFieldSettings({
					label,
					...settings,
				});
			}

			await structureBuilderPage.publishStructure();
		});

		await test.step('Publish the first version', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel, spaceName);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
				{label: 'Words', value: 'Blue'},
				{label: 'Essay', value: 'First long text value.'},
				{label: 'Amount', value: '10'},
				{label: 'Ratio', value: '1.5'},
				{label: 'Day', value: '2026-08-28'},
				{label: 'Moment', value: '2026-08-28T10:30'},
			]);

			await selectPicklistOption(page, 'State', 'Completed');
			await selectPicklistOption(page, 'Tags', 'Initial');

			await page.getByRole('textbox', {name: /^Story/}).click();
			await page.keyboard.type('Rich text version one.');

			await uploadAttachment(page, 'file_upload_image_1.jpg');

			await contentsPage.saveContent();
		});

		await test.step('Publish a second version changing every field', async () => {
			await contentsPage.editContent(contentTitle);

			await contentsPage.fillData([
				{label: 'Title', value: revisedTitle},
				{label: 'Words', value: 'Green'},
				{label: 'Essay', value: 'Second long text value.'},
				{label: 'Amount', value: '25'},
				{label: 'Ratio', value: '3.75'},
				{label: 'Day', value: '2026-09-15'},
				{label: 'Moment', value: '2026-09-15T16:45'},
				{label: 'Flag', type: 'Checkbox', value: true},
			]);

			await selectPicklistOption(page, 'State', 'Failed');
			await selectPicklistOption(page, 'Tags', 'Started');

			await page.getByRole('textbox', {name: /^Story/}).click();
			await page.keyboard.press('ControlOrMeta+a');
			await page.keyboard.type('Rich text version two.');

			await uploadAttachment(page, 'sample_small_wide_400x300.jpg');

			await contentsPage.saveContent();
		});

		await test.step('Open the comparison of both versions', async () => {
			await assetsPage.execItemAction({
				action: 'View History',
				filter: revisedTitle,
			});

			await expect(
				page.getByRole('heading', {name: `"${revisedTitle}" History`})
			).toBeVisible();

			await page
				.getByRole('button', {name: `${revisedTitle} Actions`})
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Compare to...'}).click();

			await expect(
				page.getByText('Select a Version for Comparison')
			).toBeVisible();

			await page
				.getByRole('button', {name: 'Compare Versions Key Help'})
				.click();

			for (const key of ['Added', 'Deleted', 'Format Changes']) {
				await expect(page.getByText(key, {exact: true})).toBeVisible();
			}

			await page.keyboard.press('Escape');

			await page
				.getByRole('combobox', {
					name: 'Select a Version for Comparison',
				})
				.click();

			await expect(
				page.getByRole('option', {name: 'Version 2'})
			).toHaveCount(0);

			await page.getByRole('option', {name: 'Version 1'}).click();
		});

		const leftFrame = page.frameLocator('iframe[title="Version 2"]');
		const rightFrame = page.frameLocator('iframe[title="Version 1"]');

		await test.step('Wait for both panes to render their version', async () => {
			for (const frame of [leftFrame, rightFrame]) {
				await expect(
					frame.locator('[data-field-name="ObjectField_title"]')
				).toBeVisible({timeout: 90000});
			}
		});

		await test.step('Each pane marks its own value of every changed field', async () => {
			const cases: [string, string, string][] = [
				['title', revisedTitle, contentTitle],
				['words', 'Green', 'Blue'],
				['essay', 'Second long text value.', 'First long text value.'],
				['story', 'Rich text version two.', 'Rich text version one.'],
				['amount', '25', '10'],
				['ratio', '3.75', '1.5'],
				['flag', 'Yes', 'No'],
				['state', 'Failed', 'Completed'],
				['tags', ', Started', 'Initial'],
			];

			for (const [fieldName, leftValue, rightValue] of cases) {
				await expectDiffBoxToShow(leftFrame, fieldName, leftValue);
				await expectDiffBoxToShow(rightFrame, fieldName, rightValue);
			}
		});

		await test.step('A changed date is marked as a single value', async () => {
			const leftDay = getDiffBox(leftFrame, 'day').locator(
				'.diff-html-added'
			);

			await expect(leftDay).toHaveCount(1);
			await expect(leftDay).toHaveText('09/15/2026');

			await expect(
				getDiffBox(rightFrame, 'day').locator('.diff-html-added')
			).toHaveText('08/28/2026');

			await expect(
				getDiffBox(leftFrame, 'moment').locator('.diff-html-added')
			).toHaveText('09/15/2026, 04:45 PM');
		});

		await test.step('A replaced attachment shows each version thumbnail and file name', async () => {
			await expectDiffBoxToShow(
				leftFrame,
				'attachment',
				'sample_small_wide_400x300.jpg'
			);
			await expectDiffBoxToShow(
				rightFrame,
				'attachment',
				'file_upload_image_1.jpg'
			);

			const paneImages: [FrameLocator, string][] = [
				[leftFrame, 'sample_small_wide_400x300.jpg'],
				[rightFrame, 'file_upload_image_1.jpg'],
			];

			for (const [frame, fileName] of paneImages) {
				const image = frame.locator(
					'.cms-compare-versions-attachment:visible'
				);

				await expect(image).toBeVisible();
				await expect(image).toHaveAttribute(
					'src',
					new RegExp(`/documents/.*${fileName}`)
				);
			}
		});
	}
);

test(
	'Falls back to the default language and shows an empty state for missing translations',
	{tag: '@LPD-104103'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const structureLabel = `Lang${getRandomInt()}`;
		const contentTitle = `lang content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space and a structure with a text field', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				name: structureLabel,
				page: structureBuilderPage,
			});

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({label: 'Words'});

			await structureBuilderPage.publishStructure();
		});

		await test.step('Publish the first version in the default language', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel, spaceName);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
				{label: 'Words', value: 'Green'},
			]);

			await contentsPage.saveContent();
		});

		const translate = async (fields: [string, string][]) => {
			await contentsPage.translateContent(contentTitle);

			await selectLanguageOption(
				page,
				page.getByRole('combobox', {name: 'Select a language'}).last(),
				'es-ES'
			);

			for (const [label, value] of fields) {
				await page
					.getByRole('textbox', {exact: true, name: label})
					.first()
					.fill(value);
			}

			await page
				.locator('button[type="submit"]', {hasText: 'Publish'})
				.click();

			await expect(page).toHaveURL(/contents/);
		};

		await test.step('Publish a partial and a fuller Spanish translation', async () => {
			await translate([['Title', 'Contenido de idiomas']]);
			await translate([['Words', 'Verde']]);
		});

		await test.step('Open the comparison in Spanish', async () => {
			await assetsPage.execItemAction({
				action: 'View History',
				filter: contentTitle,
			});

			await page
				.getByRole('button', {name: `${contentTitle} Actions`})
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Compare to...'}).click();

			await selectLanguageOption(
				page,
				page.locator('.modal-header').getByText('en-US'),
				'es-ES'
			);

			await page
				.getByRole('combobox', {
					name: 'Select a Version for Comparison',
				})
				.click();

			await page.getByRole('option', {name: 'Version 2'}).click();
		});

		const leftFrame = page.frameLocator('iframe[title="Version 3"]');
		const rightFrame = page.frameLocator('iframe[title="Version 2"]');

		await test.step('An untranslated field compares its default language value', async () => {
			await expectDiffBoxToShow(leftFrame, 'words', 'Verde');
			await expectDiffBoxToShow(rightFrame, 'words', 'Green');
		});

		await test.step('A version without the language shows an empty state', async () => {
			await page
				.getByRole('combobox', {
					name: /Select a version. Current version: 2/,
				})
				.click();

			await page.getByRole('option', {name: 'Version 1'}).click();

			await expect(
				page.getByText('No Translation Available')
			).toBeVisible();
		});
	}
);

test(
	'Marks a format-only change in both panes',
	{tag: '@LPD-101811'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const structureLabel = `Format${getRandomInt()}`;
		const contentTitle = `format content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space and a structure with a rich text field', async () => {
			await apiHelpers.headlessAssetLibrary.createAssetLibrary({
				name: spaceName,
				settings: {},
				type: 'Space',
			});

			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				name: structureLabel,
				page: structureBuilderPage,
			});

			await structureBuilderPage.addField('Rich Text');

			await structureBuilderPage.changeFieldSettings({label: 'Story'});

			await structureBuilderPage.publishStructure();
		});

		await test.step('Publish a version and bold its text in a second one', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel, spaceName);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
			]);

			await page.getByRole('textbox', {name: /^Story/}).click();
			await page.keyboard.type('Emphasis pending.');

			await contentsPage.saveContent();

			await contentsPage.editContent(contentTitle);

			const story = page.getByRole('textbox', {name: /^Story/});

			await story.click();
			await page.keyboard.press('ControlOrMeta+a');
			await page.getByRole('button', {exact: true, name: 'Bold'}).click();

			await expect(story.locator('strong')).toBeVisible();

			await contentsPage.saveContent();
		});

		await test.step('Open the comparison of both versions', async () => {
			await assetsPage.execItemAction({
				action: 'View History',
				filter: contentTitle,
			});

			await page
				.getByRole('button', {name: `${contentTitle} Actions`})
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Compare to...'}).click();

			await page
				.getByRole('combobox', {
					name: 'Select a Version for Comparison',
				})
				.click();

			await page.getByRole('option', {name: 'Version 1'}).click();
		});

		await test.step('Both panes mark the text as a formatted change', async () => {
			for (const title of ['Version 2', 'Version 1']) {
				const frame = page.frameLocator(`iframe[title="${title}"]`);

				const mark = getDiffBox(frame, 'story').locator(
					'.diff-html-changed'
				);

				await expect(mark.first()).toBeVisible();
				await expect(mark.first()).toContainText('Emphasis pending.');
			}
		});
	}
);
