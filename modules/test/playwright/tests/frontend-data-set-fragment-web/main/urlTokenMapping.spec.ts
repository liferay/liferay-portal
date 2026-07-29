/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataSetManagerApiHelpersTest} from '../../../fixtures/dataSetManagerApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedLayoutTest} from '../../../fixtures/isolatedLayoutTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {waitForFDS} from '../../../utils/waitFor';
import {dataSetFragmentPageTest} from './fixtures/dataSetFragmentPageTest';

export const test = mergeTests(
	apiHelpersTest,
	dataSetManagerApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPD-38564': {enabled: true},
		'LPS-164563': {enabled: true},
	}),
	isolatedLayoutTest({publish: false}),
	isolatedSiteTest,
	loginTest(),
	dataSetFragmentPageTest,
	pageEditorPagesTest
);

const MAPPING_MODE = {
	AUTO_RESOLVED: 'Resolve Automatically',
	CONTENT: 'Map to Selected Entity',
	CONTEXT: 'Map to Page Context Entity',
	LITERAL: 'Input Token Value',
};

const UNMAPPED_URL_HELP =
	'The URL is incomplete. Please map all unmapped tokens in the fragment panel to display the data set.';

const UNRESOLVED_CONTEXT_URL_HELP =
	'The URL is incomplete. Tokens mapped to the page context entity are resolved when the page displays an entity. Use the "Preview With" selector to pick one and display the data set.';

const dataSetERCs: string[] = [];
let article: any;
let articleSiteId: string;

test.afterEach(async ({apiHelpers, dataSetManagerApiHelpers}) => {
	for (const erc of dataSetERCs) {
		await dataSetManagerApiHelpers.deleteDataSet({
			erc,
		});
	}

	dataSetERCs.length = 0;

	if (article) {
		await apiHelpers.jsonWebServicesJournal.moveArticleToTrash(
			articleSiteId,
			article.articleId
		);

		article = null;
	}
});

/**
 * The data set points to an endpoint with two tokens:
 *
 * - `{siteId}` is automatically resolved: the backend provides its value.
 * - `{path}` comes from the additional API URL parameters (a `filter`
 *   expression on `friendlyUrlPath`) and must be mapped manually, which also
 *   makes the resolved value observable in the displayed rows.
 */
async function createTokenizedDataSet({
	additionalAPIURLParameters = "filter=contains(friendlyUrlPath,'{path}')",
	dataSetManagerApiHelpers,
}: any) {
	const dataSet = {
		erc: getRandomString(),
		label: getRandomString(),
	};

	dataSetERCs.push(dataSet.erc);

	await dataSetManagerApiHelpers.createDataSet({
		additionalAPIURLParameters,
		erc: dataSet.erc,
		label: dataSet.label,
		restApplication: '/headless-delivery/v1.0',
		restEndpoint: '/v1.0/sites/{siteId}/structured-contents',
		restSchema: 'StructuredContent',
	});

	await dataSetManagerApiHelpers.createDataSetTableSection({
		dataSetERC: dataSet.erc,
		fieldName: 'title',
		label_i18n: {en_US: 'Title'},
		sortable: false,
		type: 'string',
	});

	return dataSet;
}

/**
 * The article external reference code is its title, which is also its friendly
 * URL path. A token filtering on `friendlyUrlPath` therefore matches this
 * article whichever identifier the mapping resolves to, which is what makes a
 * token resolved from the external reference code observable in the rows.
 */
async function createArticle({apiHelpers, siteId}: any) {
	const title = getRandomString().toLowerCase();

	articleSiteId = siteId;

	article = await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		externalReferenceCode: title,
		groupId: siteId,
		titleMap: {en_US: title},
	});

	return title;
}

async function editBasicWebContentDisplayPageTemplate({
	apiHelpers,
	displayPageTemplatesPage,
	site,
}: any) {
	const className = await apiHelpers.jsonWebServicesClassName.fetchClassName(
		'com.liferay.journal.model.JournalArticle'
	);

	const displayPageTemplateName = getRandomString();

	await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
		{
			classNameId: className.classNameId,
			classTypeKey: 'BASIC-WEB-CONTENT',
			groupId: site.id,
			name: displayPageTemplateName,
		}
	);

	await displayPageTemplatesPage.goto(site.friendlyUrlPath);

	await displayPageTemplatesPage.editTemplate(displayPageTemplateName);
}

test(
	'URL token mapping panel shows the initial state for a data set with unmapped tokens',
	{tag: '@LPD-93809'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout, page}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		await test.step('Add the fragment and assign the data set', async () => {
			await dataSetFragmentPage.addDataSetFragment(layout);

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Assert that the panel starts incomplete with no token preselected', async () => {
			await expect(dataSetFragmentPage.tokenMappingPanel).toBeVisible();

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Incomplete');

			await expect(
				dataSetFragmentPage.tokenMappingTokenSelectorTrigger
			).toContainText('Select an Option');

			await expect(
				dataSetFragmentPage.tokenMappingMappingSelect
			).toBeHidden();
		});

		await test.step('Assert that the token dropdown reflects per-token mapping state', async () => {
			await dataSetFragmentPage.tokenMappingTokenSelectorTrigger.click();

			await expect(
				page.getByRole('menuitem', {name: '{siteId} Mapped'})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: '{path} Unmapped'})
			).toBeVisible();

			await dataSetFragmentPage.tokenMappingTokenSelectorTrigger.click();
		});

		await test.step('Assert that the fragment renders the help alert, the URL and the skeleton', async () => {

			// Unmapped tokens are the only reason the URL is incomplete, so the
			// page context help must stay out.

			await expect(
				dataSetFragmentPage.unresolvedPreview.alerts
			).toContainText([UNMAPPED_URL_HELP]);

			await expect(
				dataSetFragmentPage.unresolvedPreview.urlBox
			).toContainText('{path}');

			await expect(
				dataSetFragmentPage.unresolvedPreview.skeletonBars.first()
			).toBeVisible();

			await expect(dataSetFragmentPage.table.container).toBeHidden();
		});

		await test.step('Assert that the page context mapping mode is not offered on a content page', async () => {
			await dataSetFragmentPage.selectToken('path');

			await expect(
				dataSetFragmentPage.tokenMappingMappingSelect.getByRole(
					'option',
					{
						name: MAPPING_MODE.CONTEXT,
					}
				)
			).toHaveCount(0);
		});
	}
);

test(
	'URL token mapping panel auto-selects the only token instead of showing a placeholder',
	{tag: '@LPD-93809'},
	async ({dataSetFragmentPage, dataSetManagerApiHelpers, layout}) => {
		const dataSet =
			await test.step('Create a data set with a single token in its API URL', async () =>
				createTokenizedDataSet({
					additionalAPIURLParameters: '',
					dataSetManagerApiHelpers,
				}));

		await test.step('Add the fragment and assign the data set', async () => {
			await dataSetFragmentPage.addDataSetFragment(layout);

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Assert that the only token is auto-selected with no placeholder', async () => {
			await expect(dataSetFragmentPage.tokenMappingPanel).toBeVisible();

			await expect(
				dataSetFragmentPage.tokenMappingTokenSelectorTrigger
			).toContainText('{siteId}');

			await expect(
				dataSetFragmentPage.tokenMappingTokenSelectorTrigger
			).not.toContainText('Select an Option');

			await expect(
				dataSetFragmentPage.tokenMappingMappingSelect
			).toBeVisible();
		});
	}
);

test(
	'Inputting a token value completes the mapping and renders the data set',
	{tag: '@LPD-93809'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		await dataSetFragmentPage.addDataSetFragment(layout);

		const articleTitle =
			await test.step('Create an article matching the token filter', async () =>
				createArticle({
					apiHelpers,
					siteId: await page.evaluate(() =>
						String(Liferay.ThemeDisplay.getSiteGroupId())
					),
				}));

		await test.step('Assign the data set', async () => {
			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Map {path} with a literal value matching the article friendly URL', async () => {
			await dataSetFragmentPage.selectToken('path');

			await expect(
				dataSetFragmentPage.tokenMappingMappingSelect
			).toHaveValue('literal');

			await dataSetFragmentPage.fillTokenValue(articleTitle);
		});

		await test.step('Assert that the token and the whole mapping become complete', async () => {
			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Mapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');
		});

		await test.step('Assert that the data set replaces the preview, showing the filtered article', async () => {
			await expect(
				dataSetFragmentPage.unresolvedPreview.container
			).toBeHidden();

			await waitForFDS({page});

			await expect(
				dataSetFragmentPage.table.bodyRows.getByText(articleTitle)
			).toBeVisible();
		});
	}
);

test(
	'Changing the mapping mode of a backend resolved token unmaps it and restoring it renders the data set',
	{tag: '@LPD-93809'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		await dataSetFragmentPage.addDataSetFragment(layout);

		const articleTitle =
			await test.step('Create an article matching the token filter', async () =>
				createArticle({
					apiHelpers,
					siteId: await page.evaluate(() =>
						String(Liferay.ThemeDisplay.getSiteGroupId())
					),
				}));

		await test.step('Assign the data set', async () => {
			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Complete the mapping: {path} literal, {siteId} resolves automatically', async () => {
			await dataSetFragmentPage.selectToken('path');

			await dataSetFragmentPage.fillTokenValue(articleTitle);

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');

			await waitForFDS({page});
		});

		await test.step('Assert that {siteId} is preselected as automatically resolved', async () => {
			await dataSetFragmentPage.selectToken('siteId');

			await expect(
				dataSetFragmentPage.tokenMappingMappingSelect
			).toHaveValue('autoResolved');
		});

		await test.step('Assert that changing the mapping mode unmaps the token and brings the skeleton back', async () => {
			await dataSetFragmentPage.selectMappingMode(MAPPING_MODE.LITERAL);

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Unmapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Incomplete');

			await expect(
				dataSetFragmentPage.unresolvedPreview.skeletonBars.first()
			).toBeVisible();

			await expect(dataSetFragmentPage.table.container).toBeHidden();
		});

		await test.step('Assert that restoring automatic resolution renders the data set again', async () => {
			await dataSetFragmentPage.selectMappingMode(
				MAPPING_MODE.AUTO_RESOLVED
			);

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Mapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');

			await waitForFDS({page});

			await expect(
				dataSetFragmentPage.table.bodyRows.getByText(articleTitle)
			).toBeVisible();
		});
	}
);

test(
	'Mapping a token to a selected entity requires an entity and an identifier field',
	{tag: '@LPD-93809'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		layout,
		page,
	}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		await dataSetFragmentPage.addDataSetFragment(layout);

		const articleTitle =
			await test.step('Create an article to select as entity', async () =>
				createArticle({
					apiHelpers,
					siteId: await page.evaluate(() =>
						String(Liferay.ThemeDisplay.getSiteGroupId())
					),
				}));

		await test.step('Assign the data set and choose the entity mapping mode for {path}', async () => {
			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);

			await dataSetFragmentPage.selectToken('path');

			await dataSetFragmentPage.selectMappingMode(MAPPING_MODE.CONTENT);
		});

		await test.step('Assert that the field selector stays disabled until an entity is selected', async () => {
			await expect(
				dataSetFragmentPage.tokenMappingFieldSelect
			).toBeDisabled();

			await dataSetFragmentPage.selectEntity(articleTitle);
		});

		await test.step('Assert that the entity alone does not map the token: a field must be chosen', async () => {
			await expect(
				dataSetFragmentPage.tokenMappingFieldSelect
			).toBeEnabled();

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Unmapped');

			await dataSetFragmentPage.tokenMappingFieldSelect.selectOption({
				label: 'ID',
			});

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Mapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');
		});

		await test.step('Assert that the data set renders: the entity ID matches no friendly URL path', async () => {
			await waitForFDS({empty: true, page});
		});

		await test.step('Assert that removing the entity unmaps the token again', async () => {
			await dataSetFragmentPage.tokenMappingRemoveEntityButton.click();

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Unmapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Incomplete');

			await expect(
				dataSetFragmentPage.unresolvedPreview.skeletonBars.first()
			).toBeVisible();
		});
	}
);

test(
	'Mapping a token to the page context entity is offered on display page templates',
	{tag: ['@LPD-93809', '@LPD-99359']},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		displayPageTemplatesPage,
		pageEditorPage,
		site,
	}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		await test.step('Create a display page template for basic web content', async () =>
			editBasicWebContentDisplayPageTemplate({
				apiHelpers,
				displayPageTemplatesPage,
				site,
			}));

		await test.step('Add the fragment and assign the data set', async () => {
			await pageEditorPage.addFragment('Content Display', 'Data Set');

			await dataSetFragmentPage.fragmentSelectionArea.click();

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Map {path} to the page context entity through an identifier field', async () => {
			await dataSetFragmentPage.selectToken('path');

			await dataSetFragmentPage.selectMappingMode(MAPPING_MODE.CONTEXT);

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Unmapped');

			await dataSetFragmentPage.tokenMappingFieldSelect.selectOption({
				label: 'External Reference Code',
			});

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Mapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Completed');
		});

		await test.step('Assert that the fragment explains why the completed mapping cannot be resolved yet: the editor has no page context entity', async () => {
			await expect(
				dataSetFragmentPage.unresolvedPreview.skeletonBars.first()
			).toBeVisible();

			// Every token is either resolved or mapped to the page context
			// entity, so the unmapped token help must stay out.

			await expect(
				dataSetFragmentPage.unresolvedPreview.alerts
			).toContainText([UNRESOLVED_CONTEXT_URL_HELP]);
		});
	}
);

test(
	'A token mapped to the external reference code of the page context entity resolves and renders the data set',
	{tag: '@LPD-99359'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {
		const dataSet =
			await test.step('Create a data set with tokens in its API URL', async () =>
				createTokenizedDataSet({
					dataSetManagerApiHelpers,
				}));

		const articleTitle =
			await test.step('Create an article to preview the display page template with', async () =>
				createArticle({apiHelpers, siteId: site.id}));

		await test.step('Create a display page template for basic web content', async () =>
			editBasicWebContentDisplayPageTemplate({
				apiHelpers,
				displayPageTemplatesPage,
				site,
			}));

		await test.step('Add the fragment and assign the data set', async () => {
			await pageEditorPage.addFragment('Content Display', 'Data Set');

			await dataSetFragmentPage.fragmentSelectionArea.click();

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Map {path} to the external reference code of the page context entity', async () => {
			await dataSetFragmentPage.selectToken('path');

			await dataSetFragmentPage.selectMappingMode(MAPPING_MODE.CONTEXT);

			await dataSetFragmentPage.tokenMappingFieldSelect.selectOption({
				label: 'External Reference Code',
			});

			await expect(
				dataSetFragmentPage.unresolvedPreview.alerts
			).toContainText([UNRESOLVED_CONTEXT_URL_HELP]);
		});

		await test.step('Preview the display page template with the article', async () =>
			pageEditorPage.selectDisplayPagePreviewItem(articleTitle));

		await test.step('Assert that the data set replaces the preview, showing the article the token resolved to', async () => {
			await expect(
				dataSetFragmentPage.unresolvedPreview.container
			).toBeHidden();

			await waitForFDS({page});

			await expect(
				dataSetFragmentPage.table.bodyRows.getByText(articleTitle)
			).toBeVisible();
		});
	}
);

test(
	'Both the unmapped token and the page context entity help are shown when each applies to a different token',
	{tag: '@LPD-99359'},
	async ({
		apiHelpers,
		dataSetFragmentPage,
		dataSetManagerApiHelpers,
		displayPageTemplatesPage,
		pageEditorPage,
		site,
	}) => {
		const dataSet =
			await test.step('Create a data set with two manually mapped tokens in its API URL', async () =>
				createTokenizedDataSet({
					additionalAPIURLParameters:
						"filter=contains(friendlyUrlPath,'{path}') and contains(title,'{title}')",
					dataSetManagerApiHelpers,
				}));

		await test.step('Create a display page template for basic web content', async () =>
			editBasicWebContentDisplayPageTemplate({
				apiHelpers,
				displayPageTemplatesPage,
				site,
			}));

		await test.step('Add the fragment and assign the data set', async () => {
			await pageEditorPage.addFragment('Content Display', 'Data Set');

			await dataSetFragmentPage.fragmentSelectionArea.click();

			await dataSetFragmentPage.selectDataSetButton.click();

			await dataSetFragmentPage.selectDataSet(dataSet.label);
		});

		await test.step('Map {path} to the page context entity and leave {title} unmapped', async () => {
			await dataSetFragmentPage.selectToken('path');

			await dataSetFragmentPage.selectMappingMode(MAPPING_MODE.CONTEXT);

			await dataSetFragmentPage.tokenMappingFieldSelect.selectOption({
				label: 'External Reference Code',
			});

			await expect(
				dataSetFragmentPage.tokenMappingTokenStatusLabel
			).toHaveText('Mapped');

			await expect(
				dataSetFragmentPage.tokenMappingStatusLabel
			).toHaveText('Incomplete');
		});

		await test.step('Assert that the fragment explains both reasons the URL is incomplete', async () => {
			await expect(
				dataSetFragmentPage.unresolvedPreview.skeletonBars.first()
			).toBeVisible();

			await expect(
				dataSetFragmentPage.unresolvedPreview.alerts
			).toContainText([UNRESOLVED_CONTEXT_URL_HELP, UNMAPPED_URL_HELP]);
		});

		await test.step('Assert that each help alert can be dismissed on its own', async () => {

			// The page editor marks the fragment content as aria-hidden, so
			// nothing inside it is reachable by role.

			await dataSetFragmentPage.unresolvedPreview.alerts
				.first()
				.locator('.close')
				.click();

			await expect(
				dataSetFragmentPage.unresolvedPreview.alerts
			).toContainText([UNMAPPED_URL_HELP]);
		});
	}
);
