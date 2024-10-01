/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {changeTrackingPagesTest} from '../../fixtures/changeTrackingPagesTest';
import {isolatedSiteTest} from '../../fixtures/isolatedSiteTest';
import getRandomString from '../../utils/getRandomString';
import performLogin, {performLogout} from '../../utils/performLogin';
import {waitForAlert} from '../../utils/waitForAlert';
import {journalPagesTest} from '../journal-web/fixtures/journalPagesTest';

export const test = mergeTests(
	changeTrackingPagesTest,
	apiHelpersTest,
	isolatedSiteTest,
	journalPagesTest
);

const dataFields = [
	'Name',
	'Description',
	'Created By',
	'Create Date',
	'Last Modified',
	'Version',
	'Template',
];

const hiddenFields = [
	'statusDate',
	'smallImageId',
	'smallImage',
	'groupId',
	'indexable',
	'uuid',
	'smallImageSource',
	'externalReferenceCode',
	'classPK',
	'lastPublishDate',
	'reviewDate',
	'layoutUuid',
	'ctCollectionId',
	'id',
	'createDate',
	'expirationDate',
	'statusByUserId',
	'smallImageURL',
	'urlTitle',
	'articleId',
	'defaultLanguageId',
	'userName',
	'userId',
	'version',
	'folderId',
	'DDMTemplateKey',
	'companyId',
	'DDMStructureId	',
	'displayDate',
	'modifiedDate',
	'statusByUserName',
	'mvccVersion',
	'resourcePrimKey',
	'treePath',
	'status',
];

const journalArticleTitle = getRandomString();

test.afterEach(async ({changeTrackingPage}) => {
	await changeTrackingPage.toggleShowAllDataConfiguration(false);
});

test.beforeEach(async ({journalEditArticlePage, page, site}) => {
	await journalEditArticlePage.goto({siteUrl: site.friendlyUrlPath});

	await journalEditArticlePage.fillTitle(journalArticleTitle);

	await page.getByRole('button', {name: 'Publish'}).click();

	await waitForAlert(
		page,
		`Success:${journalArticleTitle} was created successfully.`
	);
});

test('LPD-29282 Assert administrator can not see the hidden fields if show all data configuration is disabled', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.toggleShowAllDataConfiguration(false);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalArticleTitle);

	await changeTrackingPage.selectTab('Data');

	for (const data of dataFields) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}

	for (const data of hiddenFields) {
		await expect(page.getByText(data, {exact: true})).toBeHidden();
	}
});

test('LPD-29282 Assert administrator can see the hidden fields if show all data configuration is enabled', async ({
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.toggleShowAllDataConfiguration(true);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalArticleTitle);

	await changeTrackingPage.selectTab('Data');

	for (const data of dataFields) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}

	for (const data of hiddenFields) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}
});

test('LPD-29282 Assert publications user can not see the hidden fields if show all data configuration is enabled', async ({
	apiHelpers,
	changeTrackingPage,
	ctCollection,
	page,
}) => {
	await changeTrackingPage.toggleShowAllDataConfiguration(true);

	const user = await changeTrackingPage.addUserWithPublicationsUserRole();

	await changeTrackingPage.addUserToPublication(
		ctCollection.name,
		'Viewer',
		user
	);

	await performLogout(page);

	await performLogin(page, user.alternateName);

	await changeTrackingPage.goToReviewChanges(ctCollection.name);

	await changeTrackingPage.reviewChange(journalArticleTitle);

	await changeTrackingPage.selectTab('Data');

	for (const data of dataFields) {
		await expect(page.getByText(data, {exact: true})).toBeVisible();
	}

	for (const data of hiddenFields) {
		await expect(page.getByText(data, {exact: true})).toBeHidden();
	}

	await performLogout(page);

	await performLogin(page, 'test');

	await apiHelpers.headlessAdminUser.deleteUserAccount(Number(user.id));
});
