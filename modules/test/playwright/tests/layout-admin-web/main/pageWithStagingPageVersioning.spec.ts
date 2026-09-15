/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pagesAdminPagesTest} from '../../../fixtures/pagesAdminPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {stagingConfigurationPageTest} from '../../staging-configuration-web/main/fixtures/stagingConfigurationPageTest';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-105778': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pagesAdminPagesTest,
	pageEditorPagesTest,
	stagingConfigurationPageTest
);

test(
	'Assert private indicator is not added to page variations',
	{
		tag: '@LPD-47052',
	},
	async ({
		apiHelpers,
		page,
		pagesAdminPage,
		site,
		stagingConfigurationPage,
	}) => {

		// Create a widget page

		const layoutTitle = getRandomString();

		await apiHelpers.jsonWebServicesLayout.addLayout({
			groupId: site.id,
			options: {
				type: 'portlet',
			},
			title: layoutTitle,
		});

		// Enable staging with page versioning

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		await stagingConfigurationPage.enableLocalStaging({versioning: true});

		// Go to page administration and assert private indicator doesnot appear

		await pagesAdminPage.goto(`${site.friendlyUrlPath}-staging`);

		await expect(page.getByLabel('Main Variation')).toBeVisible();

		await expect(
			page
				.getByRole('menuitem', {exact: true, name: 'Main Variation'})
				.locator('.lexicon-icon-password-policies')
		).not.toBeVisible();
	}
);

test(
	'Site Pages Variation in content pages',
	{
		tag: '@LPS-125755',
	},
	async ({
		apiHelpers,
		page,
		pageEditorPage,
		site,
		stagingConfigurationPage,
	}) => {

		// Create a content page

		const layoutTitle = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition(),
			siteId: site.id,
			title: layoutTitle,
		});

		// Enable staging with page versioning

		await stagingConfigurationPage.gotoStagingConfiguration(
			site.friendlyUrlPath
		);

		await stagingConfigurationPage.enableLocalStaging({versioning: true});

		// Assert site variation selector is not visible in edit mode

		await pageEditorPage.goto(layout, `${site.friendlyUrlPath}-staging`);

		await page
			.getByText('Drag and drop fragments or widgets here.')
			.waitFor();

		await expect(
			page.getByRole('button', {name: 'Main Variation'})
		).not.toBeVisible();

		// Add new heading fragment

		await pageEditorPage.addFragment('Basic Components', 'Heading');

		await pageEditorPage.publishPage();

		// Assert site variation selector is visible in view mode

		await page.goto(
			`/web${site.friendlyUrlPath}-staging${layout.friendlyUrlPath}`
		);

		await expect(
			page.getByRole('button', {name: 'Main Variation'})
		).toBeVisible();

		// Add page variation

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Site Pages Variation'}),
			trigger: page.getByLabel('Show Staging Version Options'),
		});

		const iframe = page.frameLocator(
			'iframe[title="Site Pages Variation"]'
		);

		await iframe
			.getByRole('button', {name: 'Add Site Pages Variation'})
			.click();

		const sitePagesVariationName = getRandomString();

		await iframe.getByLabel('Name').fill(sitePagesVariationName);

		await iframe.getByRole('button', {exact: true, name: 'Add'}).click();

		await waitForAlert(iframe, 'Success:Site page variation was added.');

		// Assert fragment in new page variation

		await page.goto(
			`/web${site.friendlyUrlPath}-staging${layout.friendlyUrlPath}`
		);

		await expect(
			page.getByRole('button', {name: sitePagesVariationName})
		).toBeVisible();

		await expect(
			page.getByRole('heading', {name: 'Heading Example'})
		).toBeVisible();

		// Edit new page variation and add a fragment

		await pageEditorPage.goto(layout, `${site.friendlyUrlPath}-staging`);

		await pageEditorPage.addFragment('Basic Components', 'Button');

		await pageEditorPage.publishPage();

		// Publish to live

		await page.goto(
			`/web${site.friendlyUrlPath}-staging${layout.friendlyUrlPath}`
		);

		await page.getByRole('button', {name: 'Publish to Live'}).click();

		const publishToLiveIframe = page.frameLocator(
			`iframe[title="Publish ${sitePagesVariationName} to Live."]`
		);

		await publishToLiveIframe
			.getByRole('button', {name: 'Publish to Live'})
			.click();

		await expect(publishToLiveIframe.getByText('Successful')).toBeVisible();

		// Assert fragments in live

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(
			page.getByText(`Page ${layoutTitle} was last published from`)
		).toBeVisible();

		await expect(
			page.getByRole('heading', {name: 'Heading Example'})
		).toBeVisible();

		await expect(
			page.getByRole('link', {name: 'Go Somewhere'})
		).toBeVisible();
	}
);
