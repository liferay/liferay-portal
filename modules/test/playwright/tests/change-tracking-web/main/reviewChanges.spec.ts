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
	isolatedSiteTest,
	journalPagesTest,
	pagesAdminPagesTest,
	pageEditorPagesTest,
	workflowPagesTest,
	featureFlagsTest({
		'LPD-20131': {enabled: true},
	})
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

	const site1 = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});
	apiHelpers.data.push({id: site1.id, type: 'site'});

	const site2 = await apiHelpers.headlessSite.createSite({
		name: getRandomString(),
	});
	apiHelpers.data.push({id: site2.id, type: 'site'});

	await changeTrackingPage.workOnPublication(ctCollection);

	await page.goto(`/group/guest${PORTLET_URLS.tagsAdmin}`);
	await page.getByRole('link', {name: 'Add Tag'}).click();
	await page.getByPlaceholder('Name').fill(getRandomString());
	await page.getByRole('button', {name: 'Save'}).click();

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

	await changeTrackingPage.goToReviewChanges(ctCollection.body.name);

	await expect(page.getByText('Liferay DXP (1): Tag (1)')).toBeVisible();
	await expect(
		page.getByText(
			site1.name +
				' (5): Document (3) Message Boards Message (1) Message Boards Thread (1)'
		)
	).toBeVisible();
	await expect(
		page.getByText(site2.name + ' (3):  Blogs Entry (3)')
	).toBeVisible();

	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollection.body.id
	);

	await changeTrackingPage.goToReviewChangesHistory(ctCollection.body.name);

	await expect(page.getByText('Liferay DXP (1): Tag (1)')).toBeVisible();
	await expect(
		page.getByText(
			site1.name +
				' (5): Document (3) Message Boards Message (1) Message Boards Thread (1)'
		)
	).toBeVisible();
	await expect(
		page.getByText(site2.name + ' (3):   Blogs Entry (3)')
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

		await accountSettingsPage.setTimeZone('Europe/Lisbon');

		await changeTrackingPage.goto();

		const utcTime = moment.utc();

		// Add 1 hour offset to the UTC time

		const timeZoneTime = utcTime.add(1, 'hours');

		await expect(
			page
				.locator('[data-id*="dateCreated"]')
				.getByText(timeZoneTime.format('MMM D, YYYY, h:mm'))
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

	await page.locator('.btn-outline-secondary').click();

	await page.getByRole('menuitem', {name: ctCollection.body.name}).click();

	const publicationIFrame = page.frameLocator('iframe[src*="preview"]');

	const newHeading = publicationIFrame.getByText('Edited');

	await expect(newHeading).toBeVisible();
});
