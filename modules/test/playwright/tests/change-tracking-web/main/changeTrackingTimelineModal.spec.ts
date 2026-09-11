/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import moment from 'moment';

import {changeTrackingPagesTest} from '../../../fixtures/changeTrackingPagesTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import {journalPagesTest} from '../../journal-web/main/fixtures/journalPagesTest';
import {JournalPage} from '../../journal-web/main/pages/JournalPage';

export const test = mergeTests(
	dataApiHelpersTest,
	changeTrackingPagesTest,
	journalPagesTest
);

const publicationCount = 6;
let ctCollections = [];
let date;

test.beforeEach(async ({apiHelpers, changeTrackingPage}) => {
	const ctCollectionNamePrefix = getRandomString();
	ctCollections = [];

	const site =
		await apiHelpers.headlessAdminUser.getSiteByFriendlyUrlPath('guest');
	const contentStructureId = await getBasicWebContentStructureId(apiHelpers);

	for (let i = 0; i <= publicationCount; i++) {
		const ctCollectionName = ctCollectionNamePrefix + ' ' + i;

		const newCTCollection =
			await apiHelpers.headlessChangeTracking.createCTCollection(
				ctCollectionName
			);

		ctCollections.push(newCTCollection);

		await changeTrackingPage.workOnPublication(newCTCollection);

		if (i !== publicationCount) {
			await apiHelpers.jsonWebServicesJournal.addWebContent({
				ddmStructureId: contentStructureId,
				groupId: site.id,
			});
		}
	}
});

test.afterEach(async ({apiHelpers}) => {
	for (let i = 0; i < ctCollections.length; i++) {
		await apiHelpers.headlessChangeTracking.deleteCTCollection(
			ctCollections[i].body.id
		);
	}
});

const getEntityHistoryTableLocator = (page: Page) => {
	return page.frameLocator(
		'iframe[title="View Entity Modification History"]'
	);
};

const getPublicationTimelineLocator = (page: Page) => {
	return page.locator('.publication-timeline');
};

const getPublicationTimelineButton = (page: Page) => {
	return page.locator('.change-tracking-timeline-button');
};

const goToPublicationTimelineModal = async (
	page: Page,
	journalPage: JournalPage
) => {
	await journalPage.goto();
	await page.getByRole('heading', {name: 'Web Content'}).waitFor();

	const timelineButton = getPublicationTimelineButton(page);
	await timelineButton.waitFor();
	await timelineButton.click();

	const publicationTimelineLocator = getPublicationTimelineLocator(page);
	await publicationTimelineLocator
		.locator('li .dropdown-item')
		.first()
		.waitFor();

	const timelineViewMoreButton = publicationTimelineLocator.getByRole(
		'button',
		{name: 'View More'}
	);
	await timelineViewMoreButton.waitFor();
	await timelineViewMoreButton.click();

	await page
		.locator(
			'#_com_liferay_change_tracking_web_portlet_PublicationsPortlet_publication-timeline-history-modal'
		)
		.getByRole('heading', {name: 'View Entity Modification History'})
		.waitFor();
};

test('LPD-22759 Allow users to view the entire history of an entity in a popup modal', async ({
	journalPage,
	page,
}) => {
	await goToPublicationTimelineModal(page, journalPage);

	const entityHistoryModalLocator = getEntityHistoryTableLocator(page);
	await entityHistoryModalLocator
		.getByText(ctCollections[0].body.name)
		.waitFor();

	for (let i = 0; i < ctCollections.length; i++) {
		if (i !== ctCollections.length - 1) {
			await expect(
				entityHistoryModalLocator.getByText(ctCollections[i].body.name)
			).toBeVisible();
		}
	}
});

test('LPD-22768 Add options to interact with the same entity in other publications via a popup modal', async ({
	journalPage,
	page,
}) => {
	await goToPublicationTimelineModal(page, journalPage);

	const entityHistoryModalLocator = getEntityHistoryTableLocator(page);
	await entityHistoryModalLocator
		.getByText(ctCollections[0].body.name)
		.waitFor();

	const firstDropdown = entityHistoryModalLocator
		.locator('.cell-item-actions .dropdown svg.lexicon-icon-ellipsis-v')
		.first();
	await firstDropdown.waitFor();
	await firstDropdown.click();

	await entityHistoryModalLocator
		.locator('div.dropdown-menu.show li')
		.first()
		.waitFor();

	await expect(
		entityHistoryModalLocator.getByRole('menuitem', {name: 'Discard'})
	).toBeVisible();

	await expect(
		entityHistoryModalLocator.getByRole('menuitem', {
			name: 'Edit in Publication',
		})
	).toBeVisible();

	await expect(
		entityHistoryModalLocator.getByRole('menuitem', {name: 'Move Changes'})
	).toBeVisible();

	await expect(
		entityHistoryModalLocator.getByRole('menuitem', {name: 'Review Change'})
	).toBeVisible();
});

test('LPD-38392 Assert View Entity Modification History sorting', async ({
	apiHelpers,
	journalPage,
	page,
}) => {
	await apiHelpers.headlessChangeTracking.publishCTCollection(
		ctCollections[0].body.id
	);

	date = moment().format('ll');

	await goToPublicationTimelineModal(page, journalPage);
	const entityHistoryModalLocator = getEntityHistoryTableLocator(page);

	const statusColumnHeader = entityHistoryModalLocator
		.getByRole('columnheader', {name: 'Status'})
		.getByRole('button');
	await statusColumnHeader.click();
	await expect(statusColumnHeader).toBeVisible();

	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(4)')
			.filter({hasText: 'Approved'})
	).toBeVisible();
	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(5)')
			.filter({hasText: 'Test Test'})
	).toBeVisible();
	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(6)')
			.filter({hasText: date})
	).toBeVisible();

	await clickAndExpectToBeVisible({
		autoClick: true,
		target: entityHistoryModalLocator
			.locator('td:nth-child(4)')
			.filter({hasText: 'Approved'}),
		trigger: statusColumnHeader,
	});

	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(4)')
			.filter({hasText: 'Approved'})
	).toBeVisible();
	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(5)')
			.filter({hasText: 'Test Test'})
	).toBeVisible();
	await expect(
		entityHistoryModalLocator
			.locator('td:nth-child(6)')
			.filter({hasText: date})
	).toBeVisible();

	await journalPage.goto();
	await page.locator('label > .custom-checkbox').setChecked(true);
	await page.getByRole('button', {name: 'Delete'}).click();
});
