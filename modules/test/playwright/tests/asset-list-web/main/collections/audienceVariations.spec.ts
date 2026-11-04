/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {collectionsPagesTest} from '../../../../fixtures/collectionsPagesTest';
import {dataApiHelpersTest} from '../../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../../helpers/ApiHelpers';
import {liferayConfig} from '../../../../liferay.config';
import {CollectionsPage} from '../../../../pages/asset-list-web/CollectionsPage';
import {getRandomInt} from '../../../../utils/getRandomInt';
import getRandomString from '../../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../../utils/structured-content/getBasicWebContentStructureId';
import getCollectionDefinition from '../../../layout-content-page-editor-web/main/utils/getCollectionDefinition';
import getPageDefinition from '../../../layout-content-page-editor-web/main/utils/getPageDefinition';

const test = mergeTests(
	loginTest(),
	isolatedSiteTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-85746': {enabled: true}, // Audiences
		'LPD-109242': {enabled: true}, // Collections Audiences
	}),
	collectionsPagesTest
);

test(
	'Offers the audiences in the site scope that have no variation yet',
	{tag: '@LPD-113735'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const audienceName = getRandomString() + ' Audience';
		const otherSiteAudienceName = getRandomString() + ' Audience';
		const siteAudienceName = getRandomString() + ' Audience';

		// Create an audience for every site, one scoped to the site and one
		// scoped to another site

		const otherSite = await apiHelpers.headlessAdminSite.postSite({
			name: getRandomString(),
		});

		await apiHelpers.jsonWebServicesAudiencesEntry.addAudiencesEntry({
			name: audienceName,
		});

		await apiHelpers.jsonWebServicesAudiencesEntry.addAudiencesEntry({
			groupERCs: [otherSite.externalReferenceCode],
			name: otherSiteAudienceName,
		});

		await apiHelpers.jsonWebServicesAudiencesEntry.addAudiencesEntry({
			groupERCs: [site.externalReferenceCode],
			name: siteAudienceName,
		});

		// Create a manual collection of Web Content Articles

		await collectionsPage.goto(site.friendlyUrlPath);

		await collectionsPage.addNewManualCollection(getRandomString());

		await collectionsPage.configureSourceItemType({
			itemSubtype: 'Basic Web Content',
			itemType: 'Web Content Article',
		});

		await collectionsPage.save();

		// Only the audiences in the site scope are offered

		await collectionsPage.openAudiencePicker();

		await expect(
			page.getByRole('option', {exact: true, name: audienceName})
		).toBeVisible();

		await expect(
			page.getByRole('option', {exact: true, name: siteAudienceName})
		).toBeVisible();

		await expect(
			page.getByRole('option', {exact: true, name: otherSiteAudienceName})
		).not.toBeVisible();

		// Add a variation for the audience

		await page.reload();

		await collectionsPage.addAudienceVariation(audienceName);

		await expect(
			page.locator('.sortable-list-item').filter({hasText: audienceName})
		).toBeVisible();

		await expect(
			page.getByText('No Personalized Variations Yet')
		).not.toBeVisible();

		// The audience with a variation is no longer offered

		await collectionsPage.openAudiencePicker();

		await expect(
			page.getByRole('option', {exact: true, name: siteAudienceName})
		).toBeVisible();

		await expect(
			page.getByRole('option', {exact: true, name: audienceName})
		).not.toBeVisible();
	}
);

test(
	'A manual collection display shows the variation of the highest-priority audience the visitor matches',
	{tag: '@LPD-113735'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const collectionName = getRandomString();

		// Create two audiences matched by a cookie each

		const firstAudience = await addCookieAudience(apiHelpers);
		const secondAudience = await addCookieAudience(apiHelpers);

		// Create a web content article for each variation

		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const defaultWebContentTitle = getRandomString();
		const firstAudienceWebContentTitle = getRandomString();
		const secondAudienceWebContentTitle = getRandomString();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: defaultWebContentTitle},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: firstAudienceWebContentTitle},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: secondAudienceWebContentTitle},
		});

		// Create a manual collection with a variation for each audience, the
		// first audience having the highest priority

		await collectionsPage.goto(site.friendlyUrlPath);

		await collectionsPage.addNewManualCollection(collectionName);

		await collectionsPage.configureSourceItemType({
			itemSubtype: 'Basic Web Content',
			itemType: 'Web Content Article',
		});

		await collectionsPage.save();

		await collectionsPage.selectAssets([defaultWebContentTitle]);

		await collectionsPage.addAudienceVariation(
			firstAudience.audiencesEntry.name
		);

		await collectionsPage.selectAssets([firstAudienceWebContentTitle]);

		await collectionsPage.addAudienceVariation(
			secondAudience.audiencesEntry.name
		);

		await collectionsPage.selectAssets([secondAudienceWebContentTitle]);

		// Display the collection and visit it matching each set of audiences

		const layoutURL = await addCollectionDisplayPage({
			apiHelpers,
			collectionName,
			collectionsPage,
			site,
		});

		await expectCollectionDisplayVariations({
			defaultWebContentTitle,
			firstAudience,
			firstAudienceWebContentTitle,
			layoutURL,
			page,
			secondAudience,
			secondAudienceWebContentTitle,
		});
	}
);

test(
	'A dynamic collection display shows the variation of the highest-priority audience the visitor matches',
	{tag: '@LPD-113735'},
	async ({apiHelpers, collectionsPage, page, site}) => {
		const collectionName = getRandomString();

		// Create two audiences matched by a cookie each

		const firstAudience = await addCookieAudience(apiHelpers);
		const secondAudience = await addCookieAudience(apiHelpers);

		// Create a web content article for each variation

		const ddmStructureId = await getBasicWebContentStructureId(apiHelpers);

		const defaultWebContentTitle = 'Default' + getRandomInt();
		const firstAudienceWebContentTitle = 'First' + getRandomInt();
		const secondAudienceWebContentTitle = 'Second' + getRandomInt();

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: defaultWebContentTitle},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: firstAudienceWebContentTitle},
		});

		await apiHelpers.jsonWebServicesJournal.addWebContent({
			ddmStructureId,
			groupId: site.id,
			titleMap: {en_US: secondAudienceWebContentTitle},
		});

		// Create a dynamic collection with a variation for each audience,
		// each variation filtering its own web content by title and the first
		// audience having the highest priority

		await collectionsPage.goto(site.friendlyUrlPath);

		await collectionsPage.addNewDynamicCollection(collectionName);

		await configureTitleFilteredWebContent(
			collectionsPage,
			defaultWebContentTitle
		);

		await collectionsPage.addAudienceVariation(
			firstAudience.audiencesEntry.name
		);

		await configureTitleFilteredWebContent(
			collectionsPage,
			firstAudienceWebContentTitle
		);

		await collectionsPage.addAudienceVariation(
			secondAudience.audiencesEntry.name
		);

		await configureTitleFilteredWebContent(
			collectionsPage,
			secondAudienceWebContentTitle
		);

		// Display the collection and visit it matching each set of audiences

		const layoutURL = await addCollectionDisplayPage({
			apiHelpers,
			collectionName,
			collectionsPage,
			site,
		});

		await expectCollectionDisplayVariations({
			defaultWebContentTitle,
			firstAudience,
			firstAudienceWebContentTitle,
			layoutURL,
			page,
			secondAudience,
			secondAudienceWebContentTitle,
		});
	}
);

type CookieAudience = {
	audiencesEntry: {externalReferenceCode: string; name: string};
	cookieName: string;
};

async function addCollectionDisplayPage({
	apiHelpers,
	collectionName,
	collectionsPage,
	site,
}: {
	apiHelpers: DataApiHelpers;
	collectionName: string;
	collectionsPage: CollectionsPage;
	site: Site;
}) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getCollectionDefinition({
				classPK: await collectionsPage.getCollectionClassPK(
					collectionName,
					site.friendlyUrlPath
				),
				id: getRandomString(),
				listStyle: 'Bulleted List (Journal)',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	return `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

async function addCookieAudience(
	apiHelpers: DataApiHelpers
): Promise<CookieAudience> {
	const cookieName = getRandomString();

	const audiencesEntry =
		await apiHelpers.jsonWebServicesAudiencesEntry.addAudiencesEntry({
			name: getRandomString() + ' Audience',
			rules: [
				{
					attribute: 'cookies',
					operator: 'includes',
					value: `${cookieName}=true`,
				},
			],
		});

	return {audiencesEntry, cookieName};
}

async function configureTitleFilteredWebContent(
	collectionsPage: CollectionsPage,
	title: string
) {
	await collectionsPage.configureSourceItemType({
		itemSubtype: 'Basic Web Content',
		itemType: 'Web Content Article',
	});

	await collectionsPage.addFilterConditions([
		{
			field: 'Title',
			fieldGroup: 'Common Fields',
			operator: 'Contains',
			quantifier: 'Any of the Following',
			value: title,
		},
	]);

	await collectionsPage.save();
}

async function expectCollectionDisplayVariations({
	defaultWebContentTitle,
	firstAudience,
	firstAudienceWebContentTitle,
	layoutURL,
	page,
	secondAudience,
	secondAudienceWebContentTitle,
}: {
	defaultWebContentTitle: string;
	firstAudience: CookieAudience;
	firstAudienceWebContentTitle: string;
	layoutURL: string;
	page: Page;
	secondAudience: CookieAudience;
	secondAudienceWebContentTitle: string;
}) {

	// Accept functional cookies

	await page.context().addCookies([
		{
			name: 'CONSENT_TYPE_FUNCTIONAL',
			url: liferayConfig.environment.baseUrl,
			value: 'true',
		},
	]);

	// Matching only the second audience shows its variation once the browser
	// has detected it

	await page.context().addCookies([
		{
			name: secondAudience.cookieName,
			url: liferayConfig.environment.baseUrl,
			value: 'true',
		},
	]);

	await page.goto(layoutURL);

	await expectDetectedAudiences(page, {
		detected: [secondAudience.audiencesEntry.externalReferenceCode],
		notDetected: [firstAudience.audiencesEntry.externalReferenceCode],
	});

	await page.reload();

	await expect(page.getByText(secondAudienceWebContentTitle)).toBeVisible();

	await expect(page.getByText(defaultWebContentTitle)).not.toBeVisible();

	// Matching both audiences shows the variation with the highest priority

	await page.context().addCookies([
		{
			name: firstAudience.cookieName,
			url: liferayConfig.environment.baseUrl,
			value: 'true',
		},
	]);

	await page.goto(layoutURL);

	await expectDetectedAudiences(page, {
		detected: [
			firstAudience.audiencesEntry.externalReferenceCode,
			secondAudience.audiencesEntry.externalReferenceCode,
		],
		notDetected: [],
	});

	await page.reload();

	await expect(page.getByText(firstAudienceWebContentTitle)).toBeVisible();

	await expect(
		page.getByText(secondAudienceWebContentTitle)
	).not.toBeVisible();

	// Matching no audience shows the default variation

	await page.context().clearCookies({
		name: new RegExp(
			`^(${firstAudience.cookieName}|${secondAudience.cookieName})$`
		),
	});

	await page.goto(layoutURL);

	await expectDetectedAudiences(page, {
		detected: [],
		notDetected: [
			firstAudience.audiencesEntry.externalReferenceCode,
			secondAudience.audiencesEntry.externalReferenceCode,
		],
	});

	await page.reload();

	await expect(page.getByText(defaultWebContentTitle)).toBeVisible();

	await expect(
		page.getByText(firstAudienceWebContentTitle)
	).not.toBeVisible();
}

async function expectDetectedAudiences(
	page: Page,
	{detected, notDetected}: {detected: string[]; notDetected: string[]}
) {
	await expect(async () => {
		const cookies = await page.context().cookies();

		const audiencesCookie = cookies.find(
			(cookie) => cookie.name === 'com.liferay.frontend.js.audiences'
		);

		const audienceEntryERCs: string[] = JSON.parse(
			decodeURIComponent(audiencesCookie?.value ?? '[]')
		);

		expect(audienceEntryERCs).toEqual(expect.arrayContaining(detected));
		expect(
			audienceEntryERCs.filter((audienceEntryERC) =>
				notDetected.includes(audienceEntryERC)
			)
		).toEqual([]);
	}).toPass();
}
