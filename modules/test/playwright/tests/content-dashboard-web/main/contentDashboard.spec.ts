/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import createUserWithPermissions from '../../../utils/createUserWithPermissions';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {
	gotoPage,
	nextPage,
	previousPage,
	setItemsPerPage,
} from '../../../utils/pagination';
import {performLoginViaApi, performLogout} from '../../../utils/performLogin';
import addApprovedStructuredContent from '../../../utils/structured-content/addApprovedStructuredContent';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {contentDashboardPagesTest} from './fixtures/contentDashboardPagesTest';
import getContentDashboardRolePermissions from './utils/getContentDashboardRolePermissions';

export const test = mergeTests(
	contentDashboardPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	journalPagesTest,
	loginTest(),
	usersAndOrganizationsPagesTest
);

test(
	'Review date in Content Dashboard matches the review date set in Web Content',
	{
		tag: '@LPD-74997',
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		page,
		site,
		usersAndOrganizationsPage,
	}) => {
		await usersAndOrganizationsPage.goToUsersWithLimitedAccess();

		await (
			await usersAndOrganizationsPage.usersTableRowLink('test')
		).click();

		await usersAndOrganizationsPage.userPreferencesButton.click();

		await usersAndOrganizationsPage.displaySettingsButton.click();

		await usersAndOrganizationsPage.timeZoneSelect.selectOption(
			'Europe/Lisbon'
		);

		await usersAndOrganizationsPage.saveTimeZoneButton.click();

		const title = getRandomString();

		const reviewDate = new Date(
			2026,
			Math.floor(Math.random() * 12),
			Math.floor(Math.random() * 28) + 1,
			Math.floor(Math.random() * 12) + 1,
			0
		);

		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		await apiHelpers.jsonWebServicesJournal.addWebContentDetailed({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			neverReview: false,
			reviewDateDay: reviewDate.getDate(),
			reviewDateHour: reviewDate.getHours(),
			reviewDateMinute: reviewDate.getMinutes(),
			reviewDateMonth: reviewDate.getMonth(),
			reviewDateYear: reviewDate.getFullYear(),
			titleMap: {en_US: title},
		});

		await contentDashboardPage.goto(site.friendlyUrlPath);

		const row = page.getByRole('row', {name: title});

		const expectedDateText = reviewDate.toLocaleString('en-US', {
			day: 'numeric',
			hour: 'numeric',
			hour12: true,
			minute: '2-digit',
			month: 'numeric',
			year: '2-digit',
		});

		await expect(row).toBeVisible();
		await expect(row.getByText(expectedDateText)).toBeVisible();
	}
);

test(
	'Content Dashboard table exports, filters and orders by review date, orders by column and paginates',
	{
		tag: ['@LPS-111323', '@LPS-124236', '@LPS-132254', '@LPS-182032'],
	},
	async ({
		apiHelpers,
		contentDashboardPage,
		journalEditArticlePage,
		journalPage,
		page,
		site,
	}) => {
		const basicWebContentStructureId =
			await getBasicWebContentStructureId(apiHelpers);

		const reviewKeyword = getRandomString().replace(/-/g, '');

		const reviewWebContentTitles = [];

		for (const reviewDateYear of [2100, 2200]) {
			const title = `${reviewKeyword} ${getRandomString()}`;

			await apiHelpers.jsonWebServicesJournal.addWebContentDetailed({
				ddmStructureId: basicWebContentStructureId,
				groupId: site.id,
				neverReview: false,
				reviewDateDay: 1,
				reviewDateHour: 12,
				reviewDateMinute: 0,
				reviewDateMonth: 0,
				reviewDateYear,
				titleMap: {en_US: title},
			});

			reviewWebContentTitles.push(title);
		}

		const neverReviewWebContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContentDetailed({
			ddmStructureId: basicWebContentStructureId,
			groupId: site.id,
			titleMap: {en_US: neverReviewWebContentTitle},
		});

		const orderKeyword = getRandomString().replace(/-/g, '');

		const orderWebContentTitles = [];

		for (let i = 1; i <= 18; i++) {
			const title = `${orderKeyword} ${String(i).padStart(2, '0')}`;

			await addApprovedStructuredContent({
				apiHelpers,
				contentStructureId: basicWebContentStructureId,
				siteId: site.id,
				title,
			});

			orderWebContentTitles.push(title);
		}

		const emptySite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		const company =
			await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
				'liferay.com'
			);

		const user = await createUserWithPermissions({
			apiHelpers,
			rolePermissions: getContentDashboardRolePermissions(
				company.companyId
			),
		});

		const administratorRole = await apiHelpers.jsonWebServicesRole.getRole(
			String(company.companyId),
			'Administrator'
		);

		await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
			Number(administratorRole.roleId),
			Number(user.id)
		);

		await performLogout(page);

		await performLoginViaApi({page, screenName: user.alternateName});

		await test.step('ExportXLSDisabled', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: emptySite.id});

			await expect(contentDashboardPage.exportXLSButton).toBeDisabled();
		});

		await test.step('ViewExportXLSButtonWithReviewDate', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await expect(contentDashboardPage.exportXLSButton).toBeEnabled();
		});

		await test.step('ViewNeverReviewChecked', async () => {
			await expect(
				contentDashboardPage.getCell(
					neverReviewWebContentTitle,
					'review-date'
				)
			).toHaveText('-');

			await journalPage.goto(site.friendlyUrlPath);

			await journalEditArticlePage.editArticle(
				neverReviewWebContentTitle
			);

			const scheduleFieldset = await openFieldset(page, 'Schedule');

			await expect(
				scheduleFieldset.getByLabel('Never Review', {exact: true})
			).toBeChecked();
		});

		await test.step('FilterByToBeReviewed', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.filterBy('To Be Reviewed');

			await expect(
				contentDashboardPage.getCell(
					reviewWebContentTitles[0],
					'review-date'
				)
			).toContainText('1/1/00');

			await expect(
				contentDashboardPage.getRow(neverReviewWebContentTitle)
			).toHaveCount(0);
		});

		await test.step('OrderByReviewDate', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.search(reviewKeyword);

			await contentDashboardPage.orderBy('Review Date');

			await contentDashboardPage.orderBy('Descending');

			await expect(contentDashboardPage.getRowTitles()).toHaveText([
				reviewWebContentTitles[1],
				reviewWebContentTitles[0],
			]);

			await contentDashboardPage.orderBy('Ascending');

			await expect(contentDashboardPage.getRowTitles()).toHaveText([
				reviewWebContentTitles[0],
				reviewWebContentTitles[1],
			]);
		});

		await test.step('OrderByColumn', async () => {
			for (const orderByColumn of ['Modified Date', 'Title']) {
				await contentDashboardPage.goto(undefined, {scopeId: site.id});

				await contentDashboardPage.search(orderKeyword);

				await contentDashboardPage.orderBy(orderByColumn);

				await contentDashboardPage.orderBy('Ascending');

				await expect(contentDashboardPage.getRowTitles()).toHaveText(
					orderWebContentTitles
				);

				await contentDashboardPage.orderBy('Descending');

				await expect(contentDashboardPage.getRowTitles()).toHaveText(
					[...orderWebContentTitles].reverse()
				);
			}
		});

		await test.step('Pagination', async () => {
			await contentDashboardPage.goto(undefined, {scopeId: site.id});

			await contentDashboardPage.orderBy('Modified Date');

			await setItemsPerPage(page, 20);

			const firstPageResults = page.getByText(
				'Showing 1 to 20 of 21 entries.',
				{exact: true}
			);
			await expect(firstPageResults).toBeVisible();

			await nextPage(page);

			const secondPageResults = page.getByText(
				'Showing 21 to 21 of 21 entries.',
				{exact: true}
			);

			await expect(secondPageResults).toBeVisible();

			await previousPage(page);

			await expect(firstPageResults).toBeVisible();

			await gotoPage(page, 2);

			await expect(secondPageResults).toBeVisible();
		});
	}
);
