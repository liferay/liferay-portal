/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import moment from 'moment';
import path from 'path';

import {accountSettingsPagesTest} from '../../../fixtures/accountSettingsPagesTest';
import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import fillAndClickOutside from '../../../utils/fillAndClickOutside';
import getRandomString from '../../../utils/getRandomString';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import {PORTLET_URLS} from '../../../utils/portletUrls';
import {waitForAlert} from '../../../utils/waitForAlert';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import getDataStructureDefinition from '../../journal-web/main/utils/getDataStructureDefinition';

export const test = mergeTests(
	accountSettingsPagesTest,
	apiHelpersTest,
	changeTrackingPagesTest,
	dataApiHelpersTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPD-35013': {enabled: true},
		'LPD-84028': {enabled: true},
		'LPS-164563': {enabled: true},
	}),
	isolatedSiteTest,
	journalPagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	workflowPagesTest
);

test('LPD-61649 Assert structure content fields are shown in the data tab', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
	site,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	const basicTextFieldName = 'Text1234';
	const imageFieldName = 'Image345';
	const nonLocalizableFieldName = 'TextNonLocalizable';
	const selectFieldName = 'Select123';
	const structureName = 'Structure 1';

	const dataDefinition = getDataStructureDefinition({
		defaultLanguageId: 'en_US',
		fields: [
			{name: basicTextFieldName},
			{
				localizable: false,
				name: nonLocalizableFieldName,
				required: true,
			},
			{fieldType: 'image', name: imageFieldName},
			{
				fieldType: 'select',
				name: selectFieldName,
				options: {
					en_US: [
						{
							label: 'option1',
							reference: 'option1',
							value: 'option1',
						},
						{
							label: 'option2',
							reference: 'option2',
							value: 'option2',
						},
					],
				},
			},
		],
		name: structureName,
	});

	await apiHelpers.dataEngine.createStructure(site.id, dataDefinition);

	await journalEditArticlePage.goto({
		siteUrl: site.friendlyUrlPath,
		structureName,
	});

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	const content = getRandomString();

	await fillAndClickOutside(
		page,
		page.getByLabel(basicTextFieldName),
		content
	);

	await fillAndClickOutside(
		page,
		page.getByLabel(nonLocalizableFieldName),
		content
	);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title} was created successfully.`);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.viewChanges({
		click: true,
		title,
		type: 'Web Content Article',
	});

	await changeTrackingPage.selectTab('Data');

	await page.waitForTimeout(3000);

	await expect(
		page.locator(
			'//td[contains(@class,"publications-section-header") and text()="FIELDS"]'
		)
	).toBeVisible();
	await expect(
		page.getByText(basicTextFieldName, {exact: true})
	).toBeVisible();
	await expect(
		page.getByText(nonLocalizableFieldName, {exact: true})
	).toBeVisible();
	await expect(page.getByText(selectFieldName, {exact: true})).toBeVisible();
	await expect(page.getByText(structureName, {exact: true})).toBeVisible();
	await expect(page.getByText(content, {exact: true}).first()).toBeVisible();
});

test('LPD-28276 Assert tag data persists in parent tab', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await page.goto(`/group/guest${PORTLET_URLS.tagsAdmin}`);

	await page.getByRole('link', {name: 'Add Tag'}).click();

	const tagName = getRandomString();

	await page.getByPlaceholder('Name').fill(tagName);

	await page.getByRole('button', {name: 'Save'}).click();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(tagName);

	await changeTrackingPage.selectTab('Parents');

	await page.waitForTimeout(3000);

	await expect(page.getByText('Guest', {exact: true})).toBeVisible();
});

test('LPD-29088 Assert Publication Overview panel empty', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const publicationOverviewPanel = page.getByRole('button', {
		name: 'Publication Overview',
	});

	await expect(publicationOverviewPanel).toBeVisible();
	await expect(page.getByText('No changes were found.')).toBeVisible();

	await publicationOverviewPanel.click();
	await expect(page.getByText('No changes were found.')).not.toBeVisible();
});

test('LPD-29088 Assert Publication Overview panel is visible', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.workOnProduction();

	const site1 = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	const site2 = await apiHelpers.headlessAdminSite.postSite({
		name: getRandomString(),
	});

	await changeTrackingPage.workOnPublication(ctCollection);

	await page.goto(`/group/guest${PORTLET_URLS.tagsAdmin}`);
	await page.getByRole('link', {name: 'Add Tag'}).click();
	await page.getByPlaceholder('Name').fill(getRandomString());

	await Promise.all([
		page.waitForLoadState('load'),
		page.getByRole('button', {name: 'Save'}).click(),
	]);

	await apiHelpers.headlessDelivery.postMessageBoardThread({
		articleBody: getRandomString(),
		headline: getRandomString(),
		siteId: site1.id,
	});

	for (let i = 0; i < 3; i++) {
		await apiHelpers.headlessDelivery.postDocument(
			site1.id,
			createReadStream(
				path.join(__dirname, '/dependencies/attachment.txt')
			)
		);

		await apiHelpers.headlessDelivery.postBlog(site2.id);
	}

	await expect(async () => {
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await expect(
			page.getByText(site2.name + ' (3):  Blogs Entry (3)')
		).toBeVisible({timeout: 10000});
	}).toPass();

	await expect(page.getByText('Liferay DXP Site (1): Tag (1)')).toBeVisible();
	await expect(
		page.getByText(
			site1.name +
				' (5): Document (3) Message Boards Message (1) Message Boards Thread (1)'
		)
	).toBeVisible();
	await expect(
		page.getByText(site2.name + ' (2):  Blogs Entry (2)')
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await expect(async () => {
		await changeTrackingPage.goToReviewChangesHistory(
			ctCollection.body.name
		);

		await expect(
			page.getByText(site2.name + ' (3):   Blogs Entry (3)')
		).toBeVisible({timeout: 10000});
	}).toPass();

	await expect(page.getByText('Liferay DXP Site (1): Tag (1)')).toBeVisible();
	await expect(
		page.getByText(
			site1.name +
				' (5): Document (3) Message Boards Message (1) Message Boards Thread (1)'
		)
	).toBeVisible();
	await expect(
		page.getByText(site2.name + ' (2):   Blogs Entry (2)')
	).toBeVisible();
});

test('LPD-29089 Assert Publication Overview filter', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await changeTrackingPage.workOnPublication(ctCollection);

	await apiHelpers.headlessDelivery.postBlog(site.id);

	await apiHelpers.headlessDelivery.postWikiNode(site.id);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page.getByRole('link', {name: 'Blogs Entry (1)'}).click();

	await expect(
		changeTrackingPage.frontendDataSetEntries
			.getByText('Blogs Entry')
			.first()
	).toBeVisible();

	await expect(
		changeTrackingPage.frontendDataSetEntries.getByText('Wiki Node').first()
	).toBeHidden();
});

test.describe('Publication Score tests', () => {
	test.beforeEach(
		'Add documents to generate change entries in the Publication',
		async ({apiHelpers, changeTrackingPage, ctCollection}) => {
			await changeTrackingPage.workOnPublication(ctCollection);

			const site =
				await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath(
					'guest'
				);

			for (let i = 0; i < 5; i++) {
				await apiHelpers.headlessDelivery.postDocument(
					site.id,
					createReadStream(
						path.join(__dirname, '/dependencies/attachment.txt')
					)
				);
			}
		}
	);

	test('LPD-47743 Assert Publication Score is visible', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await expect(page.getByText('Publication Size:')).toBeVisible();
	});

	test('LPD-45769 Assert Publication Score description is visible', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		const publicationSize = page.getByText('Publication Size:');
		await expect(publicationSize).toBeVisible();

		await publicationSize.hover();

		const publicationSizeDescription = page.getByText(
			'The size classification considers both the number of changes and the database size. Please allocate time for the publishing process accordingly.'
		);
		await expect(publicationSizeDescription).toBeVisible();
	});

	test('LPD-52951 Assert Publication Score is localized', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.goToReviewChanges(
			ctCollection.body.name,
			'es'
		);

		await expect(
			page.getByText('Tamaño de la publicación: Pequeño')
		).toBeVisible();
	});
});

test('LPD-52950 Assert publications user cannot see publications they do not have permission to view when moving changes', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const user = await changeTrackingPage.addUserWithPublicationsUserRole();

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection.body.id
	);

	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt'))
	);

	await apiHelpers.headlessChangeTracking.createCTCollection(
		getRandomString()
	);

	const ctCollection3 =
		await apiHelpers.headlessChangeTracking.createCTCollection(
			getRandomString()
		);

	await changeTrackingPage.addUserToPublication(
		ctCollection.body.name,
		'Editor',
		user
	);

	await changeTrackingPage.addUserToPublication(
		ctCollection3.body.name,
		'Editor',
		user
	);

	await performLogout(page);

	await performLoginViaApi({page, screenName: user.alternateName});

	await changeTrackingPage.workOnPublication(ctCollection);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const firstDropdown = page
		.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
		.first();
	await firstDropdown.waitFor();
	await firstDropdown.click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Move Changes'}),
		trigger: firstDropdown,
	});

	await expect(
		page.getByRole('heading', {name: 'Moved Changes'})
	).toBeVisible();

	const publicationSelector = page.locator(
		'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_toPublication'
	);

	await expect(publicationSelector).toBeVisible();

	const publicationsOptions = await page.locator(
		'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_toPublication > option'
	);

	await expect(publicationsOptions).toHaveText([
		'None',
		ctCollection3.body.name,
	]);

	await performLogout(page);

	await performLoginViaApi({page, screenName: 'test'});
});

test('LPD-61747 Discarding changes in a Publication containing a deletion change throws NPE', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await page
		.getByTestId('creationMenuNewButton')
		.locator('visible=true')
		.click();

	const pageTitle = getRandomString();

	await pagesAdminPage.addPage({
		name: pageTitle,
	});

	await pageEditorPage.addFragment('Basic Components', 'Heading');

	await pageEditorPage.publishPage();

	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.deleteFragment(headingId);

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const firstDropdown = page
		.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
		.first();
	await firstDropdown.waitFor();
	await firstDropdown.click();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Discard'}),
		trigger: firstDropdown,
	});

	await page.getByRole('button', {name: 'Discard'}).click();

	await waitForAlert(page, 'Success:Your request completed successfully.');
});

test('User time zone from theme display is applied to publication FDS', async ({
	accountSettingsPage,
	changeTrackingPage,
	page,
}) => {
	await test.step('Check date in different time zone', async () => {
		await accountSettingsPage.goToDisplaySettings();

		await accountSettingsPage.setTimeZone('Asia/Shanghai');

		await changeTrackingPage.goto();

		const utcTime = moment.utc();

		// Add 8 hour offset to the UTC time

		const timeZoneTime = utcTime.add(8, 'hours');

		await expect(
			page
				.locator('[data-id*="dateCreated"]')
				.getByText(timeZoneTime.format('MMM D, YYYY, h'))
				.first()
		).toBeVisible();
	});

	await test.step('Revert to default UTC time zone', async () => {
		await accountSettingsPage.goToDisplaySettings();

		await accountSettingsPage.setTimeZone('UTC');
	});
});

test('LPD-62112 Cannot Preview Pending Version of Page in a Publication', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	workflowPage,
}) => {

	// Enable Single Approver workflow for Content Pages

	await changeTrackingPage.workOnProduction();

	await workflowPage.goto();

	await workflowPage.changeWorkflow('Content Page', 'Single Approver');

	await changeTrackingPage.workOnPublication(ctCollection);

	await test.step('Go to home edit page', async () => {
		await page.goto(`/web/guest/home?p_l_mode=edit`);
	});

	const headingId = await pageEditorPage.getFragmentId('Paragraph');

	await pageEditorPage.editTextEditable(headingId, 'element-text', 'Edited');

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const filtersDropdown = page.locator('.filters-dropdown-button');

	await filtersDropdown.waitFor();
	await filtersDropdown.click();

	await page.getByRole('menuitem', {name: 'Status'}).click();

	const pendingCheckbox = page.getByLabel('Pending');

	await pendingCheckbox.check();

	await page.getByRole('button', {exact: true, name: 'Add Filter'}).click();

	await changeTrackingPage.reviewChange('Home');

	await page.locator('.dropdown-toggle.btn-outline-secondary').click();

	await page.getByRole('menuitem', {name: ctCollection.body.name}).click();

	const previewContent = page.locator('.publications-render-view-content');

	await expect(previewContent.getByText('Edited')).toBeVisible();

	// Disable workflow for Content Pages

	await changeTrackingPage.workOnProduction();

	await workflowPage.goto();

	await workflowPage.changeWorkflow('Content Page', 'No Workflow', {
		disable: true,
	});
});

test.describe('Publications with incomplete status tests', () => {
	const journalName = getRandomString();

	test.beforeEach(
		async ({
			apiHelpers,
			ctCollection,
			journalEditArticlePage,
			workflowPage,
		}) => {
			await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);

			await workflowPage.goto();
			await workflowPage.changeWorkflow(
				'Web Content Article',
				'Single Approver'
			);

			await apiHelpers.headlessChangeTracking.checkoutCTCollection(
				ctCollection.body.id
			);

			await journalEditArticlePage.goto();
			await journalEditArticlePage.submitArticleForWorkflow(journalName);
		}
	);

	test.afterEach(async ({apiHelpers, workflowPage}) => {
		await apiHelpers.headlessChangeTracking.checkoutCTCollection(0);

		await workflowPage.goto();

		await workflowPage.changeWorkflow(
			'Web Content Article',
			'No Workflow',
			{
				disable: true,
			}
		);
	});

	test('LPD-73271 Can view CTEntry actions in review changes page', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await expect(
			page.locator('.publication-name', {hasText: 'Pending Approval'})
		).toBeVisible();

		const firstDropdown = page
			.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
			.first();

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Discard'}),
			trigger: firstDropdown,
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Move Changes'}),
			trigger: firstDropdown,
		});
	});

	test('LPD-73271 Can view CTEntry and workflow actions in review change page', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await expect(
			page.locator('.publication-name', {hasText: 'Pending Approval'})
		).toBeVisible();

		await changeTrackingPage.reviewChange(journalName);

		const moreActionsButton = page.getByLabel('more-actions');

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {
				name: `Edit in ${ctCollection.body.name}`,
			}),
			trigger: moreActionsButton,
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Discard'}),
			trigger: moreActionsButton,
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Move Changes'}),
			trigger: moreActionsButton,
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Assign to Me'}),
			trigger: moreActionsButton,
		});

		await clickAndExpectToBeVisible({
			autoClick: false,
			target: page.getByRole('menuitem', {name: 'Assign to...'}),
			trigger: moreActionsButton,
		});
	});

	test('LPD-73272 Can use toolbar actions in review changes page', async ({
		changeTrackingPage,
		ctCollection,
		page,
	}) => {
		await changeTrackingPage.workOnProduction();
		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		await expect(
			page.locator('.publication-name', {hasText: 'Pending Approval'})
		).toBeVisible();

		await page.getByLabel('More Actions').click();

		const dropdownMenu = page.getByRole('menu');

		await expect(dropdownMenu).toBeVisible();

		const dropdownMenuItems = await dropdownMenu
			.locator('li')
			.allTextContents();

		const expectedItems = [
			'Show System Changes',
			'Work on Publication',
			'Edit',
			'Reindex',
			'Permissions',
			'Delete',
		];

		expect(dropdownMenuItems.filter(Boolean)).toEqual(expectedItems);

		const scheduleButton = page.locator('.btn', {hasText: 'Schedule'});

		await scheduleButton.waitFor();
		await scheduleButton.click();

		await expect(
			page
				.getByTestId('headerTitle')
				.getByText(
					`Schedule to Publish Later: ${ctCollection.body.name}`
				)
		).toBeVisible();

		await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

		const publishButton = page.locator('.btn', {hasText: 'Publish'});

		await publishButton.waitFor();
		await publishButton.click();

		await expect(
			page
				.getByTestId('headerTitle')
				.getByText(`Publish: ${ctCollection.body.name}`)
		).toBeVisible();
	});

	test('LPD-73282 Assert can move changes to publications with incomplete status', async ({
		apiHelpers,
		changeTrackingPage,
		ctCollection,
		journalEditArticlePage,
		page,
		workflowPage,
	}) => {
		const ctCollection2 =
			await apiHelpers.headlessChangeTracking.createCTCollection(
				getRandomString()
			);

		await apiHelpers.headlessChangeTracking.checkoutCTCollection(
			ctCollection2.body.id
		);

		await workflowPage.goto();

		await workflowPage.changeWorkflow(
			'Web Content Article',
			'No Workflow',
			{
				disable: true,
			}
		);

		const journalArticleTitle = getRandomString();

		await journalEditArticlePage.goto();

		await journalEditArticlePage.fillTitle(journalArticleTitle);

		await journalEditArticlePage.publishArticle();

		await waitForAlert(
			page,
			`Success:${journalArticleTitle} was created successfully.`
		);

		await changeTrackingPage.goToReviewChanges(ctCollection2.body.name);

		const firstDropdown = page
			.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
			.first();
		await firstDropdown.waitFor();
		await firstDropdown.click();

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Move Changes'}),
			trigger: firstDropdown,
		});

		await expect(
			page.getByRole('heading', {name: 'Moved Changes'})
		).toBeVisible();

		const publicationSelector = page.locator(
			'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_toPublication'
		);

		await expect(publicationSelector).toBeVisible();

		await expect(publicationSelector).toContainText('None');
		await expect(publicationSelector).toContainText(ctCollection.body.name);

		await apiHelpers.headlessChangeTracking.deleteCTCollection(
			ctCollection2.body.id
		);
	});
});

test('LPD-76512 User custom view is enabled for review changes', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const viewsSelectorButton = page.getByLabel('Views');

	await expect(viewsSelectorButton).toBeVisible();

	await expect(viewsSelectorButton).toHaveText('Default View');
});

test('LPD-62940 Assert download button is visible and functional in the data tab', async ({
	changeTrackingPage,
	ctCollection,
	documentLibraryEditFilePage,
	documentLibraryPage,
	page,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await documentLibraryPage.goto();

	await page.getByTitle('Provided by Liferay').click();

	await documentLibraryPage.goToFileEntryAction('Edit', 'astronaut.png');

	await page
		.locator(
			'#_com_liferay_document_library_web_portlet_DLAdminPortlet_title'
		)
		.fill('astronaut2');

	await documentLibraryEditFilePage.publishButton.click();

	await waitForAlert(page, 'Success:Your request completed successfully.');

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);
	await changeTrackingPage.reviewChange('astronaut2');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.locator('img[src*="astronaut"]')})
			.first()
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Production');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.locator('img[src*="astronaut"]')})
	).toBeVisible();

	await changeTrackingPage.selectRenderView(ctCollection.body.name);

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.locator('img[src*="astronaut"]')})
	).toBeVisible();

	await changeTrackingPage.selectTab('Data');

	await changeTrackingPage.selectRenderView('Unified View');

	await expect(
		page.locator(
			'td.publications-key-td:has-text("Title") + td .diff-html-added'
		)
	).toHaveText('astronaut2');

	await changeTrackingPage.selectRenderView('Split View');

	await expect(
		page
			.locator('td.publications-key-td:has-text("Title") + td')
			.filter({has: page.getByText('astronaut.png', {exact: true})})
	).toBeVisible();

	await expect(
		page
			.locator('td.publications-key-td:has-text("Title") + td')
			.filter({has: page.getByText('astronaut2', {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Production');

	await expect(
		page
			.locator('td.publications-key-td:has-text("Title") + td')
			.filter({has: page.getByText('astronaut.png', {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView(ctCollection.body.name);

	await expect(
		page
			.locator('td.publications-key-td:has-text("Title") + td')
			.filter({has: page.getByText('astronaut2', {exact: true})})
	).toBeVisible();

	const downloadPromise = page.waitForEvent('download');

	const downloadButton = page
		.locator('.btn-primary', {
			hasText: 'Download',
		})
		.first();

	await downloadButton.scrollIntoViewIfNeeded();
	await downloadButton.click();

	const download = await downloadPromise;
	expect(download.suggestedFilename()).toEqual('astronaut.png');
});

test('LPD-78919 Unified view in FragmentEntryLink review page is shown', async ({
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
}) => {
	await changeTrackingPage.workOnPublication(ctCollection);

	await test.step('Go to home edit page', async () => {
		await page.goto(`/web/guest/home?p_l_mode=edit`);
	});

	const headingId = await pageEditorPage.getFragmentId('Paragraph');

	await pageEditorPage.editTextEditable(headingId, 'element-text', 'Edited');

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await page
		.getByRole('row')
		.filter({hasText: 'Fragment Entry Link'})
		.getByRole('link')
		.first()
		.click();

	const renderViewDropdown = page.locator(
		'.publications-render-view-divider .dropdown'
	);

	await renderViewDropdown.waitFor({state: 'visible', timeout: 15000});

	await clickAndExpectToBeVisible({
		autoClick: false,
		target: page.getByRole('menuitem', {name: 'Unified View'}),
		trigger: renderViewDropdown,
	});
});

test('LPD-79249 Test XSS vulnerability when moving a change to a ctCollection with malicious name', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await apiHelpers.headlessChangeTracking.checkoutCTCollection(
		ctCollection.body.id
	);

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await apiHelpers.headlessDelivery.postDocument(
		site.id,
		createReadStream(path.join(__dirname, '/dependencies/attachment.txt'))
	);

	await apiHelpers.headlessChangeTracking.createCTCollection(
		`AnyName<script>alert('test');</script>`
	);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	const firstDropdown = page
		.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
		.first();
	await firstDropdown.waitFor();
	await firstDropdown.click();

	page.on('dialog', async (dialog) => {
		if (dialog.type() === 'alert') {
			throw new Error('XSS');
		}
	});

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: page.getByRole('menuitem', {name: 'Move Changes'}),
		trigger: firstDropdown,
	});

	await expect(
		page.getByRole('heading', {name: 'Moved Changes'})
	).toBeVisible();
});

test('LPD-82268 FragmentEntryLink change displays the fragment related to the published page', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
	pageEditorPage,
	pagesAdminPage,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	await pagesAdminPage.goto(site.friendlyUrlPath);

	await page
		.getByTestId('creationMenuNewButton')
		.locator('visible=true')
		.click();

	const pageTitle = getRandomString();

	await pagesAdminPage.addPage({
		name: pageTitle,
	});

	await pageEditorPage.addFragment('Basic Components', 'Heading');

	await pageEditorPage.publishPage();

	await changeTrackingPage.workOnPublication(ctCollection);

	await pagesAdminPage.goto(site.friendlyUrlPath);
	await pagesAdminPage.clickOnAction('Edit', pageTitle);

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.editTextEditable(headingId, 'element-text', 'Edited');

	await pageEditorPage.publishPage();

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await expect(
		page.getByRole('link', {name: `Heading for ${pageTitle}`})
	).toBeVisible();
});

test('LPD-86809 Web content change details display diff preview', async ({
	changeTrackingPage,
	ctCollection,
	journalEditArticlePage,
	page,
}) => {
	await changeTrackingPage.workOnProduction();

	await journalEditArticlePage.goto();

	const title = getRandomString();

	await journalEditArticlePage.fillTitle(title);

	const content = getRandomString();

	await journalEditArticlePage.journalPage.fillArticleContent(content);

	await journalEditArticlePage.publishArticle();

	await waitForAlert(page, `Success:${title} was created successfully.`);

	await changeTrackingPage.workOnPublication(ctCollection);

	await journalEditArticlePage.editArticle(title);

	const editedTitle = title + ' Edited';

	await journalEditArticlePage.fillTitle(editedTitle);

	const editedContent = content + ' Edited';

	await journalEditArticlePage.journalPage.articleContentTextBox.click();
	await journalEditArticlePage.journalPage.articleContentTextBox.selectText();
	await journalEditArticlePage.journalPage.articleContentTextBox.press(
		'Backspace'
	);
	await journalEditArticlePage.journalPage.articleContentTextBox.pressSequentially(
		editedContent
	);

	await journalEditArticlePage.publishArticle(true);

	await waitForAlert(
		page,
		`Success:${editedTitle} was updated successfully.`
	);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(editedTitle);

	await changeTrackingPage.selectRenderView('Unified View');

	await expect(
		page.locator('.diff-html-added').filter({hasText: 'Edited'})
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Split View');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(content, {exact: true})})
	).toBeVisible();

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(editedContent, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Version: 1.0 (Production)');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(content, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView(
		`Version: 1.1 (${ctCollection.body.name})`
	);

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(editedContent, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectTab('Data');

	await changeTrackingPage.selectRenderView('Unified View');

	await expect(
		page.locator(
			'td.publications-key-td:has-text("Name") + td .diff-html-added'
		)
	).toHaveText('Edited');

	await changeTrackingPage.selectRenderView('Split View');

	await expect(
		page
			.locator('td.publications-key-td:has-text("Name") + td')
			.filter({has: page.getByText(title, {exact: true})})
	).toBeVisible();

	await expect(
		page
			.locator('td.publications-key-td:has-text("Name") + td')
			.filter({has: page.getByText(editedTitle, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Version: 1.0 (Production)');

	await expect(
		page
			.locator('td.publications-key-td:has-text("Name") + td')
			.filter({has: page.getByText(title, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView(
		`Version: 1.1 (${ctCollection.body.name})`
	);

	await expect(
		page
			.locator('td.publications-key-td:has-text("Name") + td')
			.filter({has: page.getByText(editedTitle, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectTab('Parents');

	await expect(
		page.locator('tr').filter({hasText: 'Site'}).locator('+ tr')
	).toHaveText('Guest');

	await changeTrackingPage.selectTab('Children');

	await expect(
		page
			.locator('tr')
			.filter({hasText: 'Web Content Translation'})
			.locator('+ tr')
			.getByText(editedTitle, {exact: true})
	).toBeVisible();
});

test('LPS-179026 Can preview changes for WikiPages', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');

	const wikiNode = await apiHelpers.headlessDelivery.postWikiNode(site.id);

	const wikiPageContent = 'Wiki Page Content';
	const wikiPageTitle = 'Wiki Page Title';

	const wikiPage = await apiHelpers.headlessDelivery.postWikiPage(
		wikiNode.id,
		{
			content: wikiPageContent,
			headline: wikiPageTitle,
		}
	);

	await changeTrackingPage.workOnPublication(ctCollection);

	const wikiPageContentEdited = wikiPageContent + ' Edited';

	await apiHelpers.put(
		`${apiHelpers.baseUrl}headless-delivery/v1.0/wiki-pages/${wikiPage.id}`,
		{
			data: {
				content: wikiPageContentEdited,
				encodingFormat: 'plain_text',
				headline: wikiPageTitle,
			},
			failOnStatusCode: true,
		}
	);

	await apiHelpers.post(
		`${apiHelpers.baseUrl}headless-delivery/v1.0/wiki-pages/${wikiPage.id}/wiki-page-attachments`,
		{
			failOnStatusCode: true,
			headers: {
				...(await apiHelpers.getCSRFTokenHeader()),
			},
			multipart: {
				file: createReadStream(
					path.join(__dirname, '/dependencies/attachment.txt')
				),
			},
		}
	);

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await changeTrackingPage.reviewChange(wikiPageTitle + ' (1.1)');

	await changeTrackingPage.selectRenderView('Unified View');

	await expect(
		page.locator('.diff-html-added').filter({hasText: 'Edited'})
	).toBeVisible();

	await expect(
		page.locator('.diff-html-added').filter({hasText: 'Attachments'})
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Split View');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(wikiPageContent, {exact: true})})
	).toBeVisible();

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(wikiPageContentEdited, {exact: true})})
	).toBeVisible();

	await expect(
		page.locator('td.publications-render-view-content .page-attachments')
	).toBeVisible();

	await changeTrackingPage.selectRenderView('Version: 1.0 (Production)');

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(wikiPageContent, {exact: true})})
	).toBeVisible();

	await changeTrackingPage.selectRenderView(
		`Version: 1.1 (${ctCollection.body.name})`
	);

	await expect(
		page
			.locator('td.publications-render-view-content')
			.filter({has: page.getByText(wikiPageContentEdited, {exact: true})})
	).toBeVisible();

	await expect(
		page.locator('td.publications-render-view-content .page-attachments')
	).toBeVisible();
});
