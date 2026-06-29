/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../../fixtures/pageEditorPagesTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../../pages/layout-content-page-editor-web/PageEditorPage';
import getRandomString from '../../../../utils/getRandomString';
import {performUserSwitchViaApi} from '../../../../utils/performLogin';
import {PORTLET_URLS} from '../../../../utils/portletUrls';
import {waitForAlert} from '../../../../utils/waitForAlert';
import getFragmentDefinition from '../../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';
import {RecycleBinPage} from '../../../site-cms-site-initializer/main/pages/RecycleBinPage';
import {registerUserCredentials} from '../../../site-cms-site-initializer/main/spaces/helpers/roleMembership';
import {cmsPagesTest} from '../../../site-cms-site-initializer/permissions/fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	isolatedSiteTest,
	pageEditorPagesTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPD-17564': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	loginTest()
);

async function grantContentViewToGuestAndUser(
	apiHelpers: DataApiHelpers,
	entryId: number
) {
	await apiHelpers.objectEntry.putObjectEntryPermissions(
		'cms/basic-web-contents',
		entryId,
		[
			{actionIds: ['VIEW'], roleName: 'Guest'},
			{actionIds: ['VIEW'], roleName: 'User'},
		]
	);
}

async function connectSpaceToSite(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	siteExternalReferenceCode: string
) {
	await apiHelpers.headlessAssetLibrary.connectSite(
		spaceExternalReferenceCode,
		siteExternalReferenceCode,
		{searchable: true}
	);
}

async function disconnectSpaceFromSite(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	siteExternalReferenceCode: string
) {
	await apiHelpers.delete(
		`${apiHelpers.baseUrl}headless-asset-library/v1.0/asset-libraries/${spaceExternalReferenceCode}/connected-sites/${siteExternalReferenceCode}`
	);
}

async function mapTitleToHeading(
	page: Page,
	pageEditorPage: PageEditorPage,
	fragmentId: string,
	entryTitle: string
) {
	const iframe = page.frameLocator('iframe[title="Select"]');

	await pageEditorPage.selectEditable(fragmentId, 'element-text');

	await page.getByLabel('Select Item').click();

	const selectItemMenuItem = page.getByRole('menuitem', {
		name: 'Select Item...',
	});

	if (await selectItemMenuItem.isVisible()) {
		await selectItemMenuItem.click();
	}

	await iframe.getByRole('main').waitFor();

	await iframe.getByText('Basic Web Contents (CMS)', {exact: true}).click();

	await iframe.getByText(entryTitle, {exact: true}).first().click();

	await expect(
		page.locator('.page-editor__item-selector__content-input')
	).toHaveValue(entryTitle, {timeout: 10000});

	await page.getByLabel('Field', {exact: true}).selectOption('Title');

	await pageEditorPage.waitForChangesSaved();
}

async function isContentAvailableInPicker(
	page: Page,
	pageEditorPage: PageEditorPage,
	fragmentId: string,
	entryTitle: string
) {
	const iframe = page.frameLocator('iframe[title="Select"]');

	await pageEditorPage.selectEditable(fragmentId, 'element-text');

	await page.getByLabel('Select Item').click();

	const selectItemMenuItem = page.getByRole('menuitem', {
		name: 'Select Item...',
	});

	if (await selectItemMenuItem.isVisible()) {
		await selectItemMenuItem.click();
	}

	await iframe.getByRole('main').waitFor();

	const entity = iframe.getByText('Basic Web Contents (CMS)', {exact: true});

	const entityVisible = await entity
		.waitFor({state: 'visible', timeout: 8000})
		.then(() => true)
		.catch(() => false);

	if (!entityVisible) {
		return false;
	}

	await entity.click();

	return iframe
		.getByText(entryTitle, {exact: true})
		.first()
		.waitFor({state: 'visible', timeout: 8000})
		.then(() => true)
		.catch(() => false);
}

async function prepareUser(apiHelpers: DataApiHelpers) {
	const user = await apiHelpers.headlessAdminUser.postUserAccount();

	registerUserCredentials(user);

	await apiHelpers.jsonWebServicesUser.agreeToTermsOfUse(user.id);
	await apiHelpers.jsonWebServicesUser.answerReminderQuery(user.id);

	return user;
}

async function createSpace(apiHelpers: DataApiHelpers) {
	return apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: getRandomString(),
		settings: {},
		type: 'Space',
	});
}

async function addSpaceUser(
	apiHelpers: DataApiHelpers,
	spaceExternalReferenceCode: string,
	spaceRoleNames: string[] = []
) {
	const user = await prepareUser(apiHelpers);

	await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccount(
		spaceExternalReferenceCode,
		user.externalReferenceCode
	);

	if (spaceRoleNames.length) {
		await apiHelpers.headlessAssetLibrary.putAssetLibraryUserAccountRoles(
			spaceExternalReferenceCode,
			user.externalReferenceCode,
			spaceRoleNames
		);
	}

	return user;
}

async function createBasicWebContent(
	apiHelpers: DataApiHelpers,
	spaceName: string,
	title: string
) {
	return apiHelpers.objectEntry.postObjectEntry(
		{objectEntryFolderExternalReferenceCode: 'L_CONTENTS', title},
		'cms/basic-web-contents',
		spaceName
	);
}

async function startSessionAs(page: Page, alternateName: string) {
	await performUserSwitchViaApi(page, alternateName);

	await page.goto(PORTLET_URLS.cmsHome, {waitUntil: 'domcontentloaded'});
}

test(
	'Content from a connected Space is available for mapping in the page editor',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.f']},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		test.setTimeout(120000);

		const title = `Content ${getRandomString()}`;

		const space = await createSpace(apiHelpers);

		const entry = await createBasicWebContent(
			apiHelpers,
			space.name,
			title
		);

		await grantContentViewToGuestAndUser(apiHelpers, entry.id);

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const headingId = getRandomString();

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: headingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		expect(
			await isContentAvailableInPicker(
				page,
				pageEditorPage,
				headingId,
				title
			)
		).toBe(true);
	}
);

test(
	'Content from a Space connected to two sites is available for mapping on both',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.g']},
	async ({apiHelpers, page, pageEditorPage, site}) => {
		test.setTimeout(150000);

		const title = `Content ${getRandomString()}`;

		const space = await createSpace(apiHelpers);

		const entry = await createBasicWebContent(
			apiHelpers,
			space.name,
			title
		);

		await grantContentViewToGuestAndUser(apiHelpers, entry.id);

		const secondSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);
		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			secondSite.externalReferenceCode
		);

		const isAvailableOnSite = async (targetSite: Site) => {
			const headingId = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: headingId,
						key: 'BASIC_COMPONENT-heading',
					}),
				]),
				siteId: targetSite.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, targetSite.friendlyUrlPath);

			return isContentAvailableInPicker(
				page,
				pageEditorPage,
				headingId,
				title
			);
		};

		expect(await isAvailableOnSite(site)).toBe(true);
		expect(await isAvailableOnSite(secondSite)).toBe(true);
	}
);

test(
	'After disconnecting a Space, its content is unavailable for mapping and already-mapped content degrades gracefully',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.h']},
	async ({apiHelpers, browser, page, pageEditorPage, site}) => {
		test.setTimeout(180000);

		const title = `Content ${getRandomString()}`;

		const space = await createSpace(apiHelpers);

		const entry = await createBasicWebContent(
			apiHelpers,
			space.name,
			title
		);

		await grantContentViewToGuestAndUser(apiHelpers, entry.id);

		await connectSpaceToSite(
			apiHelpers,
			space.externalReferenceCode,
			site.externalReferenceCode
		);

		const mappedHeadingId = getRandomString();

		const mappedLayout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFragmentDefinition({
					id: mappedHeadingId,
					key: 'BASIC_COMPONENT-heading',
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		const mappedUrl = `/web${site.friendlyUrlPath}${mappedLayout.friendlyUrlPath}`;

		await test.step('SiteA maps the content and it renders while connected', async () => {
			await pageEditorPage.goto(mappedLayout, site.friendlyUrlPath);

			await mapTitleToHeading(
				page,
				pageEditorPage,
				mappedHeadingId,
				title
			);

			await pageEditorPage.publishPage();

			await page.goto(mappedUrl, {waitUntil: 'domcontentloaded'});

			await expect(page.getByText(title).first()).toBeVisible();
		});

		await test.step('SPA disconnects the Space from the site', async () => {
			await disconnectSpaceFromSite(
				apiHelpers,
				space.externalReferenceCode,
				site.externalReferenceCode
			);
		});

		await test.step('The content is no longer available for mapping', async () => {
			const headingId = getRandomString();

			const layout = await apiHelpers.headlessDelivery.createSitePage({
				pageDefinition: getPageDefinition([
					getFragmentDefinition({
						id: headingId,
						key: 'BASIC_COMPONENT-heading',
					}),
				]),
				siteId: site.id,
				title: getRandomString(),
			});

			await pageEditorPage.goto(layout, site.friendlyUrlPath);

			expect(
				await isContentAvailableInPicker(
					page,
					pageEditorPage,
					headingId,
					title
				)
			).toBe(false);
		});

		await test.step('The already-mapped page still loads gracefully', async () => {
			const guestContext = await browser.newContext();

			try {
				const guestPage = await guestContext.newPage();

				const response = await guestPage.goto(mappedUrl, {
					waitUntil: 'domcontentloaded',
				});

				expect(response?.status()).toBeLessThan(500);

				await expect(guestPage.getByText('Powered by')).toBeVisible();
			}
			finally {
				await guestContext.close();
			}
		});
	}
);

test(
	'A Space Administrator can rename a Space and the new name is reflected in the navigation and header',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.d']},
	async ({apiHelpers, page, spaceSummaryPage}) => {
		const space = await createSpace(apiHelpers);
		const newName = `Renamed ${getRandomString()}`;

		const spaceAdministrator = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode,
			['Asset Library Administrator']
		);

		await startSessionAs(page, spaceAdministrator.alternateName);

		await test.step('Space Administrator updates the Space name', async () => {
			await spaceSummaryPage.goto(space.name);

			await page.getByRole('button', {name: 'More Actions'}).click();
			await page.getByRole('menuitem', {name: 'Settings'}).click();

			await page.getByRole('textbox', {name: 'Space Name'}).fill(newName);

			await page.getByRole('button', {name: 'Save'}).click();

			await waitForAlert(page, 'Success');
		});

		await test.step('The new name is reflected in the Spaces navigation', async () => {
			await page.goto(PORTLET_URLS.cms);

			await expect(
				page.getByRole('menuitem', {name: newName})
			).toBeVisible();

			await expect(
				page.getByRole('menuitem', {name: space.name})
			).toBeHidden();
		});

		await test.step('The new name is reflected in the Space header', async () => {
			await spaceSummaryPage.goto(newName);

			await expect(
				page.getByRole('heading', {exact: true, name: newName})
			).toBeVisible();
		});
	}
);

test(
	'A removed Space member can no longer access the Space',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.e']},
	async ({apiHelpers, page}) => {
		const space = await createSpace(apiHelpers);

		const member = await addSpaceUser(
			apiHelpers,
			space.externalReferenceCode
		);

		await test.step('The member can access the Space', async () => {
			await startSessionAs(page, member.alternateName);

			await page.goto(PORTLET_URLS.cms);

			await expect(
				page.getByRole('menuitem', {name: space.name})
			).toBeVisible();
		});

		await test.step('The administrator removes the member', async () => {
			await performUserSwitchViaApi(page, 'test');

			await apiHelpers.delete(
				`${apiHelpers.baseUrl}headless-asset-library/v1.0/asset-libraries/${space.externalReferenceCode}/user-accounts/${member.externalReferenceCode}`
			);
		});

		await test.step('The removed member can no longer access the Space', async () => {
			await startSessionAs(page, member.alternateName);

			await page.goto(PORTLET_URLS.cms);

			await expect(
				page.getByRole('menuitem', {name: space.name})
			).toBeHidden();
		});
	}
);

test(
	'Deleting a Space permanently deletes its content instead of moving it to the Recycle Bin',
	{tag: ['@LPD-95534', '@LPD-95534/TC-11.i']},
	async ({apiHelpers, page}) => {
		const recycleBinPage = new RecycleBinPage(page);

		const space = await createSpace(apiHelpers);

		const contentTitle = `Content ${getRandomString()}`;

		await createBasicWebContent(apiHelpers, space.name, contentTitle);

		await test.step('CA deletes the Space from the All Spaces view', async () => {
			await page.goto(PORTLET_URLS.cmsAllSpaces);

			await page
				.getByRole('button', {name: `${space.name} Actions`})
				.click();
			await page.getByRole('menuitem', {name: 'Delete'}).click();
			await page.getByRole('button', {name: 'Delete'}).click();

			await waitForAlert(
				page,
				`Success:${space.name} was successfully deleted.`
			);
		});

		await test.step('The content is not moved to the Recycle Bin', async () => {
			await recycleBinPage.goto();

			await expect(page.getByText(contentTitle)).toHaveCount(0);
		});
	}
);
