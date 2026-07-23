/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {expectToPass} from '../../../utils/expectToPass';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import performLogin, {
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {SITE_CMS_SPACE_NAME} from '../../setup/site-cms-site/constants/space';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	fragmentsPagesTest,
	structureBuilderPagesTest,
	loginTest()
);

test(
	'Can find and replace with basic contents',
	{tag: '@LPD-78865'},
	async ({apiHelpers, assetsPage, dataSetPage, findAndReplacePage, page}) => {
		const ids = [];

		// Create three contents

		for (const i of ['1', '2', '3']) {
			const {id} = await apiHelpers.objectEntry.postObjectEntry(
				{
					content_i18n: {
						en_US: `<p>Blue content ${i}</p>`,
						es_ES: `<p>Blue content ${i}</p>`,
						fr_FR: `<p>Blue content ${i}</p>`,
					},
					objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
					title_i18n: {
						en_US: `Blue ${i}`,
						es_ES: `Blue ${i}`,
						fr_FR: `Blue ${i}`,
					},
				},
				'cms/basic-web-contents',
				SITE_CMS_SPACE_NAME
			);

			ids.push(id);
		}

		// Go to All section and search

		await assetsPage.gotoAll();

		await dataSetPage.search('Blue');

		await dataSetPage.selectAll();

		// Open find and replace modal

		await findAndReplacePage.open();

		// Check we can't advance without typing search replacement

		await expectToPass(
			async () => {
				await page
					.locator('.modal-footer')
					.getByText('Review Changes')
					.click();

				await expect(
					page
						.locator('.modal-body')
						.getByText('This field is required')
				).toHaveCount(2);
			},
			{timeout: 5000}
		);

		// Go with all languages and check proper number of changes

		await page.getByRole('textbox', {name: 'Find'}).fill('Blue');

		await page.getByLabel('Replace with').fill('Azul');

		await findAndReplacePage.goToReviewChanges();

		await expect(page.getByText('6 Changes')).toHaveCount(3);

		// Go back, select spanish and check proper number of changes

		await findAndReplacePage.goBack();

		await findAndReplacePage.switchLanguage('es-ES');

		await findAndReplacePage.goToReviewChanges();

		await expect(page.getByText('2 Changes')).toHaveCount(3);

		// Apply replace for all items individually

		await findAndReplacePage.applyChangesToItem('Blue 1');
		await findAndReplacePage.applyChangesToItem('Blue 2');
		await findAndReplacePage.applyChangesToItem('Blue 3');

		// Check only spanish value has ben translated

		for (const [index, id] of ids.entries()) {
			const itemNumber = index + 1;

			const {content_i18n, title_i18n} =
				await apiHelpers.objectEntry.getObjectEntryById(
					'cms/basic-web-contents',
					String(id)
				);

			expect(content_i18n.en_US).toBe(
				`<p>Blue content ${itemNumber}</p>`
			);
			expect(content_i18n.fr_FR).toBe(
				`<p>Blue content ${itemNumber}</p>`
			);
			expect(content_i18n.es_ES).toBe(
				`<p>Azul content ${itemNumber}</p>`
			);

			expect(title_i18n.en_US).toBe(`Blue ${itemNumber}`);
			expect(title_i18n.fr_FR).toBe(`Blue ${itemNumber}`);
			expect(title_i18n.es_ES).toBe(`Azul ${itemNumber}`);
		}

		// Apply replace to the other languages

		await assetsPage.gotoAll();

		await dataSetPage.search('Blue');

		await dataSetPage.selectAll();

		await findAndReplacePage.open();

		await page.getByRole('textbox', {name: 'Find'}).fill('Blue');

		await page.getByLabel('Replace with').fill('Azul');

		await findAndReplacePage.goToReviewChanges();

		await findAndReplacePage.applyChangesToAllItems();

		await assetsPage.gotoAll();

		await expect(page.getByText('Azul').nth(0)).toBeVisible();
		await expect(page.getByText('Blue').nth(0)).not.toBeVisible();

		for (const [index, id] of ids.entries()) {
			const itemNumber = index + 1;

			const {content_i18n, title_i18n} =
				await apiHelpers.objectEntry.getObjectEntryById(
					'cms/basic-web-contents',
					String(id)
				);

			expect(content_i18n.en_US).toBe(
				`<p>Azul content ${itemNumber}</p>`
			);
			expect(content_i18n.fr_FR).toBe(
				`<p>Azul content ${itemNumber}</p>`
			);
			expect(content_i18n.es_ES).toBe(
				`<p>Azul content ${itemNumber}</p>`
			);

			expect(title_i18n.en_US).toBe(`Azul ${itemNumber}`);
			expect(title_i18n.fr_FR).toBe(`Azul ${itemNumber}`);
			expect(title_i18n.es_ES).toBe(`Azul ${itemNumber}`);
		}
	}
);

test(
	'Can find and replace with custom structure with a repeatable group',
	{tag: '@LPD-78865'},
	async ({
		assetsPage,
		contentsPage,
		dataSetPage,
		findAndReplacePage,
		page,
		structureBuilderPage,
	}) => {

		// Create structure with a repeatable group

		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
			publish: false,
			spaces: [SITE_CMS_SPACE_NAME],
		});

		await structureBuilderPage.addField('Text');

		await structureBuilderPage.createRepeatableGroup({
			fields: [{label: 'Text'}],
		});

		await structureBuilderPage.publishStructure();

		// Create a content for that structure

		await assetsPage.gotoAll();

		await contentsPage.createContent(structureLabel);

		await expectToPass(
			async () => {
				await contentsPage.fillData([
					{label: 'Title', value: 'Red'},
					{label: 'Text', value: 'This is Red'},
				]);

				await contentsPage.saveContent();
			},
			{timeout: 5000}
		);

		// Open find and replace

		await dataSetPage.search('"Red"');

		await dataSetPage.selectAll();

		await findAndReplacePage.open();

		await page.getByRole('textbox', {name: 'Find'}).fill('Red');

		await page.getByLabel('Replace with').fill('Orange');

		await findAndReplacePage.goToReviewChanges();

		// Open preview for item and check related entry field also appears

		await findAndReplacePage.goToPreviewChanges('Red');

		await expect(
			page
				.locator('p')
				.filter({
					has: page.locator(
						'.cms-find-and-replace-preview__search.text-danger',
						{hasText: 'Red'}
					),
				})
				.filter({
					has: page.locator(
						'.cms-find-and-replace-preview__replacement.font-weight-bold.text-success',
						{hasText: 'Orange'}
					),
				})
		).toHaveCount(2);

		// Apply changes and check it worked also for related entry

		await clickAndExpectToBeHidden({
			target: page.locator('.modal-title'),
			trigger: page.locator('.modal-footer').getByText('Apply Changes'),
		});

		await assetsPage.gotoAll();

		await contentsPage.editContent('Orange');

		await expect(page.getByLabel('Text')).toHaveValue('This is Orange');
	}
);

test(
	'Find and Replace bulk action is hidden when user cannot update selected items',
	{tag: '@LPD-78865'},
	async ({apiHelpers, assetsPage, dataSetPage, findAndReplacePage, page}) => {

		// Create content

		const contentTitle = `Green ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title: contentTitle,
			},
			'cms/basic-web-contents',
			SITE_CMS_SPACE_NAME
		);

		// Add user without update permission

		const [space] =
			await apiHelpers.headlessAssetLibrary.getAssetLibrariesPage(
				`name eq '${SITE_CMS_SPACE_NAME}'`
			);

		const user = await apiHelpers.headlessAdminUser.postUserAccount();

		userData[user.alternateName] = {
			name: user.givenName,
			password: 'test',
			surname: user.familyName,
		};

		await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
		await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

		await apiHelpers.jsonWebServicesUser.addGroupUsers(space.siteId, [
			user.id,
		]);

		await page.context().clearCookies();

		await performLogin(page, user.alternateName);

		// Check that the option is not displayed

		await assetsPage.gotoAll();

		await dataSetPage.selectAll();

		await expect(findAndReplacePage.openButton).not.toBeVisible();

		await performLogout(page);
	}
);
