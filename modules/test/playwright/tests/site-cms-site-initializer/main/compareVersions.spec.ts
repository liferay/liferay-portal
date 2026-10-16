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
import fs from 'fs';
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

const RELATED_CONTENT_LABEL = 'Reference';

function getDiffBox(frame: FrameLocator, fieldName: string): Locator {
	return frame.locator(
		`[data-field-name="ObjectField_${fieldName}"] .cms-compare-versions-diff`
	);
}

function getRelatedContentDiffBox(frame: FrameLocator): Locator {
	return frame.locator(
		'[data-field-name^="ObjectField_r_"] .cms-compare-versions-diff'
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

async function selectRelatedContent(page: Page, title: string) {
	const combobox = page.getByRole('combobox', {
		exact: true,
		name: RELATED_CONTENT_LABEL,
	});

	const option = page.getByRole('option', {exact: true, name: title});

	await expect(async () => {
		await combobox.fill(title, {timeout: 2000});

		await expect(option).toBeVisible({timeout: 3000});
	}).toPass({timeout: 30000});

	await option.click();
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
	{tag: ['@LPD-101811', '@LPD-106606']},
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
		const firstRelatedTitle = `first reference ${getRandomString()}`;
		const secondRelatedTitle = `second reference ${getRandomString()}`;

		await test.step('Create a space with two contents to reference', async () => {
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
			});

			for (const title of [firstRelatedTitle, secondRelatedTitle]) {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
						title,
					},
					'cms/basic-web-contents',
					spaceName
				);
			}
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
				['Email', 'Contact', {}],
				['Phone Number', 'Line', {}],
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

			await structureBuilderPage.addRelatedContent(
				RELATED_CONTENT_LABEL,
				'Basic Web Content'
			);

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
				{label: 'Contact', value: 'first@liferay.com'},
				{label: 'Line', value: '600111222'},
				{label: 'Day', type: 'Date', value: '08/28/2026'},
				{label: 'Moment', type: 'Date', value: '08/28/2026 10:30 AM'},
			]);

			await selectRelatedContent(page, firstRelatedTitle);

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
				{label: 'Contact', value: 'second@liferay.com'},
				{label: 'Line', value: '600999888'},
				{label: 'Day', type: 'Date', value: '09/15/2026'},
				{label: 'Moment', type: 'Date', value: '09/15/2026 04:45 PM'},
				{label: 'Flag', type: 'Checkbox', value: true},
			]);

			await selectRelatedContent(page, secondRelatedTitle);

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
		});

		await test.step('A single version shows its boolean, phone number and rich text as text', async () => {
			const frame = page.frameLocator('iframe[title="Version 2"]');

			await expectDiffBoxToShow(frame, 'flag', 'Yes');
			await expectDiffBoxToShow(frame, 'line', '600999888');
			await expectDiffBoxToShow(frame, 'story', 'Rich text version two.');

			await expect(getDiffBox(frame, 'line')).not.toHaveClass(
				/form-control-select/
			);

			const textareaHeight = await frame
				.locator('[data-field-name="ObjectField_essay"] textarea')
				.evaluate((textarea) => getComputedStyle(textarea).height);

			await expect(getDiffBox(frame, 'story')).toHaveCSS(
				'min-height',
				textareaHeight
			);
		});

		await test.step('Select the previous version as the target', async () => {
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

		await test.step('Clicking a field scrolls the other version to the same field', async () => {
			const leftField = leftFrame.locator(
				'[data-field-name="ObjectField_flag"]'
			);
			const rightField = rightFrame.locator(
				'[data-field-name="ObjectField_flag"]'
			);

			await leftField.evaluate((field) => field.scrollIntoView());

			await getDiffBox(leftFrame, 'flag').click();

			await expect(async () => {
				const leftBox = await leftField.boundingBox();
				const rightBox = await rightField.boundingBox();

				expect(Math.abs(leftBox!.y - rightBox!.y)).toBeLessThan(1);
			}).toPass({timeout: 10000});
		});

		await test.step('Moving the focus with the keyboard also aligns the other version', async () => {
			await getDiffBox(rightFrame, 'essay').click();

			await rightFrame
				.locator('[data-field-name="ObjectField_attachment"]')
				.evaluate((field) => field.scrollIntoView());

			await leftFrame
				.locator('[data-field-name="ObjectField_title"]')
				.evaluate((field) => field.scrollIntoView());

			await page.keyboard.press('Tab');

			const fieldName = await rightFrame
				.locator(':focus')
				.evaluate(
					(element) =>
						element.closest<HTMLElement>('[data-field-name]')
							?.dataset.fieldName
				);

			expect(fieldName).toBeTruthy();
			expect(fieldName).not.toBe('ObjectField_essay');

			await expect(async () => {
				const leftBox = await leftFrame
					.locator(`[data-field-name="${fieldName}"]`)
					.boundingBox();
				const rightBox = await rightFrame
					.locator(`[data-field-name="${fieldName}"]`)
					.boundingBox();

				expect(Math.abs(leftBox!.y - rightBox!.y)).toBeLessThan(1);
			}).toPass({timeout: 10000});
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

		await test.step('Each diff box is a read only text box named after its field', async () => {
			for (const label of ['Day', 'Flag', 'Story', 'Words']) {
				const textbox = leftFrame.getByRole('textbox', {name: label});

				await expect(textbox).toHaveAttribute('aria-readonly', 'true');
				await expect(textbox).toHaveClass(/cms-compare-versions-diff/);
			}
		});

		await test.step('A changed atomic value is marked as one unit', async () => {
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

			const atomicCases: [string, string, string][] = [
				['contact', 'second@liferay.com', 'first@liferay.com'],
				['line', '+1600999888', '+1600111222'],
				['ratio', '3.75', '1.5'],
			];

			for (const [fieldName, leftValue, rightValue] of atomicCases) {
				await expect(
					getDiffBox(leftFrame, fieldName).locator('.diff-html-added')
				).toHaveText(leftValue);
				await expect(
					getDiffBox(rightFrame, fieldName).locator(
						'.diff-html-added'
					)
				).toHaveText(rightValue);
			}
		});

		await test.step('A changed reference is marked by its title', async () => {
			await expect(
				getRelatedContentDiffBox(leftFrame).locator('.diff-html-added')
			).toHaveText(secondRelatedTitle);

			await expect(
				getRelatedContentDiffBox(rightFrame).locator('.diff-html-added')
			).toHaveText(firstRelatedTitle);
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
	{tag: ['@LPD-104103', '@LPD-106618']},
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
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
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

		await test.step('The version can still be changed from the empty state', async () => {
			await page
				.getByRole('combobox', {
					name: /Select a version. Current version: 1/,
				})
				.click();

			await page.getByRole('option', {name: 'Version 2'}).click();

			await expect(
				page.getByText('No Translation Available')
			).toBeHidden();

			await expectDiffBoxToShow(rightFrame, 'words', 'Green');
		});
	}
);

test(
	'Keeps both versions read only',
	{tag: '@LPD-106618'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const referencedStructureLabel = `Nested${getRandomInt()}`;
		const structureLabel = `ReadOnly${getRandomInt()}`;
		const contentTitle = `read only content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space and a structure with a repeatable group', async () => {
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
			});

			await structureBuilderPage.createStructureFromData({
				label: referencedStructureLabel,
				name: referencedStructureLabel,
				page: structureBuilderPage,
				publish: true,
			});

			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				name: structureLabel,
				page: structureBuilderPage,
				publish: false,
			});

			await structureBuilderPage.addField('Decimal');

			await structureBuilderPage.changeFieldSettings({label: 'Ratio'});

			await structureBuilderPage.addField('Date');

			await structureBuilderPage.changeFieldSettings({label: 'Day'});

			await structureBuilderPage.addReferencedStructures([
				referencedStructureLabel,
			]);

			await structureBuilderPage.publishStructure();
		});

		await test.step('Publish two versions', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel, spaceName);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
				{label: 'Ratio', value: '1.5'},
				{label: 'Day', type: 'Date', value: '08/28/2026'},
			]);

			await page
				.locator('.lfr-layout-structure-item-form-relationship')
				.getByRole('textbox', {exact: true, name: 'Title'})
				.fill('Nested title');

			await contentsPage.saveContent();

			await contentsPage.editContent(contentTitle);

			await contentsPage.fillData([{label: 'Ratio', value: '3.75'}]);

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

		const leftFrame = page.frameLocator('iframe[title="Version 2"]');
		const rightFrame = page.frameLocator('iframe[title="Version 1"]');

		await test.step('The key help icon shows a tooltip with its label', async () => {
			await page
				.getByRole('button', {name: 'Compare Versions Key Help'})
				.hover();

			await expect(
				page
					.getByRole('tooltip')
					.filter({hasText: 'Compare Versions Key Help'})
			).toBeVisible();

			await page.mouse.move(0, 0);
		});

		await test.step('Each pane shows its own value of the changed field', async () => {
			await expectDiffBoxToShow(leftFrame, 'ratio', '3.75');
			await expectDiffBoxToShow(rightFrame, 'ratio', '1.5');
		});

		await test.step('The repeatable group is read only', async () => {
			for (const frame of [leftFrame, rightFrame]) {
				await expect(
					frame.locator(
						'.lfr-layout-structure-item-form-relationship'
					)
				).toBeVisible();

				await expect(frame.getByText('Add New')).toHaveCount(0);
			}
		});

		await test.step('An unchanged date keeps the read only focus style', async () => {
			const field = leftFrame.locator(
				'[data-field-name="ObjectField_day"]'
			);

			const dateInput = field.locator('input.form-control');
			const inputGroupItem = field.locator('.input-group-item-focusable');

			const getStyle = () =>
				inputGroupItem.evaluate(async (element) => {
					await Promise.all(
						element
							.getAnimations()
							.map((animation) => animation.finished)
					);

					const {backgroundColor, boxShadow} =
						getComputedStyle(element);

					return {backgroundColor, boxShadow};
				});

			const style = await getStyle();

			await dateInput.click();

			await expect(dateInput).toBeFocused();

			expect(await getStyle()).toEqual(style);
		});

		await test.step('A changed field keeps the read-only input style', async () => {
			for (const frame of [leftFrame, rightFrame]) {
				const field = frame.locator(
					'[data-field-name="ObjectField_ratio"]'
				);

				const getBackgroundColor = (locator: Locator) =>
					locator.evaluate(
						(element) => getComputedStyle(element).backgroundColor
					);

				expect(
					await getBackgroundColor(
						field.locator('.cms-compare-versions-diff')
					)
				).toBe(
					await getBackgroundColor(field.locator('input[readonly]'))
				);
			}
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
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
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

test(
	'Compares the two selected versions from the management toolbar',
	{tag: '@LPD-101810'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const structureLabel = `Bulk${getRandomInt()}`;
		const contentTitle = `bulk content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space and a structure with a text field', async () => {
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
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

		await test.step('Publish three versions', async () => {
			await contentsPage.goto();

			await contentsPage.createContent(structureLabel, spaceName);

			await contentsPage.fillData([
				{label: 'Title', value: contentTitle},
				{label: 'Words', value: 'First'},
			]);

			await contentsPage.saveContent();

			for (const value of ['Second', 'Third']) {
				await contentsPage.editContent(contentTitle);

				await contentsPage.fillData([{label: 'Words', value}]);

				await contentsPage.saveContent();
			}
		});

		await test.step('Open the version history', async () => {
			await assetsPage.execItemAction({
				action: 'View History',
				filter: contentTitle,
			});

			await expect(
				page.getByRole('heading', {name: `"${contentTitle}" History`})
			).toBeVisible();
		});

		const compareButton = page.getByRole('button', {
			exact: true,
			name: 'Compare',
		});
		const rowCheckboxes = page.locator(
			'tbody tr input[title="Select Item"]'
		);

		await test.step('The action is disabled with one version selected', async () => {
			await rowCheckboxes.nth(0).check();

			await expect(compareButton).toBeVisible();
			await expect(compareButton).toBeDisabled();
		});

		await test.step('The action is disabled with three versions selected', async () => {
			await rowCheckboxes.nth(1).check();
			await rowCheckboxes.nth(2).check();

			await expect(compareButton).toBeDisabled();
		});

		await test.step('Two selected versions open the comparison preloaded', async () => {
			await rowCheckboxes.nth(2).uncheck();

			await expect(compareButton).toBeEnabled();

			await compareButton.click();

			await expectDiffBoxToShow(
				page.frameLocator('iframe[title="Version 3"]'),
				'words',
				'Third'
			);
			await expectDiffBoxToShow(
				page.frameLocator('iframe[title="Version 2"]'),
				'words',
				'Second'
			);
		});
	}
);

test(
	'Compares two versions of a file entry',
	{tag: '@LPD-104533'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `file compare ${getRandomString()}`;

		const readImageBase64 = (fileName: string) =>
			fs
				.readFileSync(path.join(__dirname, 'dependencies', fileName))
				.toString('base64');

		const firstFileName = `compare_v1_${getRandomString()}.jpg`;
		const secondFileName = `compare_v2_${getRandomString()}.jpg`;

		await test.step('Publish two file versions', async () => {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: readImageBase64('file_upload_image_1.jpg'),
						name: firstFileName,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				'cms/basic-documents',
				'Default'
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{
					file: {
						fileBase64: readImageBase64(
							'sample_small_wide_400x300.jpg'
						),
						name: secondFileName,
					},
				},
				'cms/basic-documents',
				entry.id
			);
		});

		await test.step('Open the file version history', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.execCardItemAction({
				action: 'View History',
				filter: title,
			});

			await expect(
				page.getByRole('heading', {name: `"${title}" History`})
			).toBeVisible();
		});

		await test.step('Compare the two versions from the row action', async () => {
			await page
				.getByRole('button', {name: `${title} Actions`})
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Compare to...'}).click();

			await page
				.getByRole('combobox', {
					name: 'Select a Version for Comparison',
				})
				.click();

			await page.getByRole('option', {name: 'Version 1'}).click();

			for (const [version, fileName] of [
				[2, secondFileName],
				[1, firstFileName],
			] as const) {
				const image = page
					.frameLocator(`iframe[title="Version ${version}"]`)
					.locator('.cms-compare-versions-attachment:visible');

				await expect(image).toHaveAttribute(
					'src',
					new RegExp(`/documents/.*${fileName}`),
					{timeout: 90000}
				);
			}
		});
	}
);

test(
	'Shows an unchanged file entry image in both versions',
	{tag: '@LPD-106618'},
	async ({apiHelpers, assetsPage, page}) => {
		const title = `file compare ${getRandomString()}`;

		const fileName = `compare_${getRandomString()}.jpg`;

		await test.step('Publish a second version keeping the same file', async () => {
			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{
					file: {
						fileBase64: fs
							.readFileSync(
								path.join(
									__dirname,
									'dependencies',
									'file_upload_image_1.jpg'
								)
							)
							.toString('base64'),
						name: fileName,
					},
					objectEntryFolderExternalReferenceCode: 'L_FILES',
					title,
				},
				'cms/basic-documents',
				'Default'
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{title: `${title} revised`},
				'cms/basic-documents',
				entry.id
			);
		});

		await test.step('Compare both versions from the version history', async () => {
			await assetsPage.gotoFiles();

			await assetsPage.execCardItemAction({
				action: 'View History',
				filter: title,
			});

			await page
				.getByRole('button', {name: `${title} Actions`})
				.first()
				.click();

			await page.getByRole('menuitem', {name: 'Compare to...'}).click();

			await expect(
				page
					.frameLocator('iframe[title="Version 2"]')
					.locator('.cms-compare-versions-attachment:visible')
			).toHaveAttribute('src', new RegExp(`/documents/.*${fileName}`), {
				timeout: 90000,
			});

			await page
				.getByRole('combobox', {
					name: 'Select a Version for Comparison',
				})
				.click();

			await page.getByRole('option', {name: 'Version 1'}).click();
		});

		await test.step('Both versions show the image without a diff border', async () => {
			for (const version of [1, 2]) {
				const image = page
					.frameLocator(`iframe[title="Version ${version}"]`)
					.locator('.cms-compare-versions-attachment:visible');

				await expect(image).toHaveAttribute(
					'src',
					new RegExp(`/documents/.*${fileName}`),
					{timeout: 90000}
				);
				await expect(image).not.toHaveClass(/border-(danger|success)/);
			}
		});
	}
);

test(
	'Aligns the same repeatable item in the other version',
	{tag: '@LPD-106618'},
	async ({
		apiHelpers,
		assetsPage,
		contentsPage,
		page,
		structureBuilderPage,
	}) => {
		const structureLabel = `Repeatable${getRandomInt()}`;
		const contentTitle = `repeatable content ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		await test.step('Create a space and a structure with a repeatable group', async () => {
			await apiHelpers.headlessAssetLibrary.createSpace({
				name: spaceName,
			});

			await structureBuilderPage.createStructureFromData({
				label: structureLabel,
				name: structureLabel,
				page: structureBuilderPage,
				publish: false,
			});

			await structureBuilderPage.addField('Long Text');

			await structureBuilderPage.changeFieldSettings({label: 'Intro'});

			await structureBuilderPage.addField('Text');

			await structureBuilderPage.changeFieldSettings({label: 'Note'});

			await structureBuilderPage.createRepeatableGroup({
				fields: [{label: 'Note'}],
				label: 'Items',
			});

			await structureBuilderPage.publishStructure();
		});

		await test.step('Publish a second version with a shorter intro', async () => {
			const objectDefinition =
				await apiHelpers.objectAdmin.getObjectDefinitionByName(
					structureLabel
				);

			const applicationName = objectDefinition.restContextPath.replace(
				'/o/',
				''
			);
			const items = Array.from({length: 6}, (_, index) => ({
				externalReferenceCode: `note-${index}`,
				note: `Note ${index}`,
			}));
			const relationshipName =
				objectDefinition.objectRelationships[0].name;

			const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
				{
					intro: Array.from(
						{length: 30},
						(_, index) => `Intro line ${index}.`
					).join('\n'),
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					[relationshipName]: items,
					title: contentTitle,
				},
				applicationName,
				spaceName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{intro: 'Short intro.', [relationshipName]: items},
				applicationName,
				objectEntry.id
			);
		});

		await test.step('Open the comparison of both versions', async () => {
			await contentsPage.goto();

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

		await test.step('Clicking a repeatable item scrolls the other version to the same item', async () => {
			const getLastNote = (version: number) =>
				page
					.frameLocator(`iframe[title="Version ${version}"]`)
					.locator('[data-field-name$="_note"]')
					.nth(5);

			await expect(getLastNote(1)).toBeAttached({timeout: 90000});

			await getLastNote(2).locator('.form-control').first().click();

			await expect(async () => {
				const leftBox = await getLastNote(2).boundingBox();
				const rightBox = await getLastNote(1).boundingBox();

				expect(
					Math.abs((leftBox?.y ?? 0) - (rightBox?.y ?? Infinity))
				).toBeLessThan(2);
			}).toPass({timeout: 10000});
		});
	}
);
