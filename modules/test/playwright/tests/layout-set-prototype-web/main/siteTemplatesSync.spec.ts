/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {productMenuPageTest} from '../../../fixtures/productMenuPageTest';
import {sitesPageTest} from '../../../fixtures/sitesPageTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {ChangeTrackingPage} from '../../../pages/change-tracking-web/ChangeTrackingPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {sitesAdminPagesTest} from '../../site-admin-web/main/fixtures/sitesAdminPagesTest';
import {layoutSetPrototypePageTest} from './fixtures/layoutSetPrototypePageTest';
import createSiteTemplate from './utils/createSiteTemplate';

export const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-82107': {enabled: true},
	}),
	globalMenuPagesTest,
	layoutSetPrototypePageTest,
	loginTest(),
	productMenuPageTest,
	sitesAdminPagesTest,
	sitesPageTest
);

const testWithPublications = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-35443': {enabled: true},
		'LPD-82107': {enabled: true},
		'LPD-104837': {enabled: true},
	}),
	globalMenuPagesTest,
	layoutSetPrototypePageTest,
	loginTest(),
	productMenuPageTest,
	sitesAdminPagesTest,
	sitesPageTest
);

/**
 * Checks the layouts of the site through the layout service instead of the
 * site pages endpoint, which is search based and does not see the pages
 * imported into a Publication until it is published.
 */
async function hasSitePage(
	apiHelpers: ApiHelpers,
	pageName: string,
	siteExternalReferenceCode: string
) {
	const site = await apiHelpers.headlessAdminSite.getSite(
		siteExternalReferenceCode
	);

	const layouts = await apiHelpers.jsonWebServicesLayout.getLayouts(
		Number(site.id),
		false
	);

	return layouts.some((layout) => layout.nameCurrentValue === pageName);
}

test(
	'Execute Site Template Sync action is hidden for inactive Site Templates',
	{tag: '@LPD-87027'},
	async ({apiHelpers, globalMenuPage, layoutSetPrototypePage}) => {

		// Create an inactive Site Template

		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{
					active: false,
					name: siteTemplateName,
				}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await globalMenuPage.goToControlPanel('Site Templates');

		// Inactive: Activate is shown and the sync action is hidden

		await clickAndExpectToBeVisible({
			target: layoutSetPrototypePage.activateMenuItem,
			trigger: layoutSetPrototypePage.rowActions(siteTemplateName),
		});

		await expect(layoutSetPrototypePage.executeSyncMenuItem).toBeHidden();

		// Activate the Site Template and verify the sync action is now visible

		await layoutSetPrototypePage.activateMenuItem.click();

		await clickAndExpectToBeVisible({
			target: layoutSetPrototypePage.executeSyncMenuItem,
			trigger: layoutSetPrototypePage.rowActions(siteTemplateName),
		});
	}
);

test(
	'Execute Site Template Sync is blocked when Publications is enabled',
	{tag: '@LPD-87027'},
	async ({apiHelpers, globalMenuPage, layoutSetPrototypePage, page}) => {
		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype =
			await apiHelpers.jsonWebServicesLayoutSetPrototype.addLayoutSetPrototypes(
				{
					name: siteTemplateName,
				}
			);

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		const changeTrackingPage = new ChangeTrackingPage(page);

		try {
			await changeTrackingPage.enablePublications(true);

			await globalMenuPage.goToControlPanel('Site Templates');

			await clickAndExpectToBeVisible({
				target: layoutSetPrototypePage.executeSyncMenuItem,
				trigger: layoutSetPrototypePage.rowActions(siteTemplateName),
			});

			await layoutSetPrototypePage.executeSyncMenuItem.click();

			await expect(
				page.getByText(
					'The site template sync cannot be run with publications enabled.'
				)
			).toBeVisible();

			await expect(
				page.getByText(
					'This will apply changes from your site template to linked sites'
				)
			).toBeHidden();

			// Close the dialog so it does not block the navigation that
			// disables publications during cleanup

			const dialog = page.getByRole('alertdialog');

			await dialog
				.locator('.modal-footer')
				.getByRole('button', {name: 'Close'})
				.click();

			await expect(dialog).toBeHidden();
		}
		finally {
			await changeTrackingPage.enablePublications(false);
		}
	}
);

test(
	'Execute Site Template Sync propagates changes to a linked Site',
	{tag: '@LPD-87027'},
	async ({
		apiHelpers,
		globalMenuPage,
		layoutSetPrototypePage,
		page,
		productMenuPage,
		sitesAdminPage,
		sitesPage,
	}) => {
		test.slow();

		// Create the Site Template and a linked Site

		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await sitesAdminPage.goto();

		const siteName = 'Site-' + getRandomString();

		const {externalReferenceCode} = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: externalReferenceCode, type: 'site'});

		// Add a new page to the Site Template

		const layoutSetPrototypeGroup =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				layoutSetPrototype.companyId,
				layoutSetPrototype.layoutSetPrototypeId
			);

		const newPageName = 'NewPage-' + getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: layoutSetPrototypeGroup.groupId,
			privateLayout: 'true',
			title: newPageName,
		});

		// Trigger the manual sync from the Site Templates list and wait for the
		// asynchronous completion notification

		await globalMenuPage.goToControlPanel('Site Templates');

		await layoutSetPrototypePage.executeSyncAndWaitForSuccess(
			siteTemplateName
		);

		// The new page propagates to the linked Site

		await expect(async () => {
			expect(
				await hasSitePage(
					apiHelpers,
					newPageName,
					externalReferenceCode
				)
			).toBeTruthy();
		}).toPass();
	}
);

testWithPublications(
	'Execute Site Template Sync applies changes to the active Publication and publishes them to production',
	{tag: '@LPD-105505'},
	async ({
		apiHelpers,
		globalMenuPage,
		layoutSetPrototypePage,
		page,
		productMenuPage,
		sitesAdminPage,
		sitesPage,
	}) => {
		test.slow();

		// Create the Site Template and a linked Site

		const siteTemplateName = 'SiteTemplate-' + getRandomString();

		const layoutSetPrototype = await createSiteTemplate({
			apiHelpers,
			page,
			productMenuPage,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({
			id: layoutSetPrototype.layoutSetPrototypeId,
			type: 'layoutSetPrototype',
		});

		await sitesAdminPage.goto();

		const siteName = 'Site-' + getRandomString();

		const {externalReferenceCode} = await sitesPage.createSite({
			isCustom: true,
			siteName,
			templateName: siteTemplateName,
		});

		apiHelpers.data.push({id: externalReferenceCode, type: 'site'});

		// Add a new page to the Site Template

		const layoutSetPrototypeGroup =
			await apiHelpers.jsonWebServicesGroup.getGroupByKey(
				layoutSetPrototype.companyId,
				layoutSetPrototype.layoutSetPrototypeId
			);

		const newPageName = 'NewPage-' + getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: layoutSetPrototypeGroup.groupId,
			privateLayout: 'true',
			title: newPageName,
		});

		const changeTrackingPage = new ChangeTrackingPage(page);

		try {
			await changeTrackingPage.enablePublications(true);

			// Trigger the manual sync while working on a Publication

			const ctCollection =
				await apiHelpers.headlessChangeTracking.createCTCollection(
					'Publication-' + getRandomString()
				);

			await changeTrackingPage.workOnPublication(ctCollection);

			await globalMenuPage.goToControlPanel('Site Templates');

			await layoutSetPrototypePage.executeSyncAndWaitForSuccess(
				siteTemplateName
			);

			// The new page is in the Publication but not in production

			expect(
				await hasSitePage(
					apiHelpers,
					newPageName,
					externalReferenceCode
				)
			).toBeTruthy();

			await changeTrackingPage.workOnProduction();

			expect(
				await hasSitePage(
					apiHelpers,
					newPageName,
					externalReferenceCode
				)
			).toBeFalsy();

			// Publishing the Publication propagates the new page to production

			await apiHelpers.headlessChangeTracking.publishCTCollection(
				ctCollection.body.id
			);

			await expect(async () => {
				expect(
					await hasSitePage(
						apiHelpers,
						newPageName,
						externalReferenceCode
					)
				).toBeTruthy();
			}).toPass();
		}
		finally {
			await changeTrackingPage.workOnProduction();

			await changeTrackingPage.enablePublications(false);
		}
	}
);
