/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {launchPagesTest} from '../../../fixtures/launchPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {LaunchEntry, LaunchSet} from '../../../helpers/LaunchApiHelper';
import {LaunchPreviewPage} from '../../../pages/launch-web/LaunchPreviewPage';
import {LaunchesPage} from '../../../pages/launch-web/LaunchesPage';
import getRandomString from '../../../utils/getRandomString';
import getBasicWebContentStructureId from '../../../utils/structured-content/getBasicWebContentStructureId';
import getFragmentDefinition from '../../layout-content-page-editor-web/main/utils/getFragmentDefinition';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';

const JOURNAL_ARTICLE_CLASS_NAME = 'com.liferay.journal.model.JournalArticle';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-72278': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	launchPagesTest,
	loginTest()
).extend<{launchSetIds: number[]}>({
	launchSetIds: async ({apiHelpers}, use) => {
		const launchSetIds: number[] = [];

		await use(launchSetIds);

		for (const launchSetId of launchSetIds) {
			await apiHelpers.launch.deleteLaunchSet(launchSetId);
		}
	},
});

async function addWebContentPage(
	apiHelpers: ApiHelpers,
	pageTitle: string,
	site: Site,
	title: string
) {
	let webContent = await apiHelpers.jsonWebServicesJournal.addWebContent({
		ddmStructureId: await getBasicWebContentStructureId(apiHelpers),
		descriptionMap: {en_US: ''},
		groupId: site.id,
		rawContent: '<p>Version 1.0</p>',
		titleMap: {en_US: title},
	});

	for (const version of ['1.1', '1.2']) {
		webContent = await apiHelpers.jsonWebServicesJournal.editWebContent(
			{description: '', rawContent: `<p>Version ${version}</p>`, title},
			site.id,
			webContent
		);
	}

	const journalArticleClassName =
		await apiHelpers.jsonWebServicesClassName.fetchClassName(
			JOURNAL_ARTICLE_CLASS_NAME
		);

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				fragmentConfig: {
					itemSelector: {
						className: JOURNAL_ARTICLE_CLASS_NAME,
						classNameId: journalArticleClassName.classNameId,
						classPK: Number(webContent.resourcePrimKey),
						title,
					},
				},
				id: getRandomString(),
				key: 'com.liferay.fragment.internal.renderer.ContentObjectFragmentRenderer',
			}),
		]),
		siteId: site.id,
		title: pageTitle,
	});

	return {layout, webContent};
}

function getURLParameter(url: string, name: string) {
	const {searchParams} = new URL(url);

	for (const [key, value] of searchParams) {
		if (key.endsWith(name)) {
			return value;
		}
	}

	return null;
}

async function expectEntryRow(
	launchesPage: LaunchesPage,
	{
		baseVersion,
		published = false,
		status,
		title,
		version,
	}: {
		baseVersion: string;
		published?: boolean;
		status: string;
		title: string;
		version: string;
	}
) {
	const row = launchesPage.getEntryRow(title);

	if (published) {
		await expect(row.getByRole('link')).toHaveCount(0);

		await expect(row).toContainText(title);
		await expect(row).toContainText(baseVersion);
	}
	else {
		await expect(
			row.getByRole('link', {exact: true, name: title})
		).toBeVisible();
		await expect(
			row.getByRole('link', {exact: true, name: baseVersion})
		).toBeVisible();
	}

	await expect(row.getByRole('cell').nth(1)).toHaveText(version);
	await expect(row).toContainText('Basic Web Content');
	await expect(row).toContainText(status);
}

async function expectPreview(
	previewPage: Page,
	{
		hidden,
		launchName,
		shown,
	}: {hidden: string[]; launchName: string; shown: string}
) {
	const launchPreviewPage = new LaunchPreviewPage(previewPage);

	await expect(launchPreviewPage.topbar).toContainText('Previewing');
	await expect(launchPreviewPage.getLaunchPicker(launchName)).toBeVisible();

	await expect(previewPage.getByText(shown, {exact: true})).toBeVisible();

	for (const text of hidden) {
		await expect(previewPage.getByText(text, {exact: true})).toBeHidden();
	}
}

test(
	'Previews two launches holding their own draft of a web content over the live site',
	{tag: ['@LPD-72339', '@LPD-109351']},
	async ({apiHelpers, launchSetIds, launchesPage, page, site}) => {
		const pageTitle = getRandomString();
		const title = getRandomString();

		const {layout, webContent} = await addWebContentPage(
			apiHelpers,
			pageTitle,
			site,
			title
		);

		const liveText = 'Version 1.2';

		const firstLaunchName = getRandomString();
		const firstLaunchText = getRandomString();
		const secondLaunchName = getRandomString();
		const secondLaunchText = getRandomString();

		async function updateDraft(text: string) {
			await apiHelpers.jsonWebServicesJournal.editWebContent(
				{description: '', rawContent: `<p>${text}</p>`, title},
				site.id,
				{
					...webContent,
					articleId: getURLParameter(page.url(), 'articleId'),
					serviceContext: {workflowAction: 2},
					version: getURLParameter(page.url(), 'version'),
				}
			);
		}

		const backToLaunches = page
			.locator('.control-menu-nav-item')
			.getByTitle('Launches');

		let firstLaunch: LaunchSet;
		let firstLaunchEntriesURL: string;
		let firstLaunchEntry: LaunchEntry;
		let secondLaunch: LaunchSet;

		await test.step('Create the first launch', async () => {
			await launchesPage.goto(site.friendlyUrlPath);

			const description = getRandomString();

			await launchesPage.createLaunch(firstLaunchName, description);

			firstLaunch =
				await apiHelpers.launch.getLaunchSetByName(firstLaunchName);

			launchSetIds.push(firstLaunch.id);

			await launchesPage.goToDetails();

			await expect(launchesPage.nameInput).toHaveValue(firstLaunchName);
			await expect(launchesPage.descriptionInput).toHaveValue(
				description
			);
		});

		await test.step('Add the web content, which makes a draft from the live version', async () => {
			await launchesPage.goToEntries();

			firstLaunchEntriesURL = page.url();

			await launchesPage.addWebContentEntry(site.name, title);

			firstLaunchEntry = await apiHelpers.launch.getLaunchEntry(
				firstLaunch.id,
				JOURNAL_ARTICLE_CLASS_NAME
			);

			expect(firstLaunchEntry.classPK).toBe(
				Number(webContent.resourcePrimKey)
			);
			expect(firstLaunchEntry.baseVersionKey).toBe('1.2');
			expect(firstLaunchEntry.draftId).toBeGreaterThan(0);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.2',
				status: 'Draft',
				title,
				version: 'Draft',
			});
		});

		await test.step('Edit the draft of the first launch', async () => {
			await launchesPage
				.getEntryRow(title)
				.getByRole('link', {exact: true, name: title})
				.click();

			await expect(backToLaunches).toBeVisible();

			expect(getURLParameter(page.url(), 'articleId')).not.toBe(
				webContent.articleId
			);

			await updateDraft(firstLaunchText);

			await backToLaunches.click();

			await expect(launchesPage.newEntryButton).toBeVisible();
		});

		await test.step('Create a second launch with the same web content', async () => {
			await launchesPage.goto(site.friendlyUrlPath);

			await launchesPage.createLaunch(
				secondLaunchName,
				getRandomString()
			);

			secondLaunch =
				await apiHelpers.launch.getLaunchSetByName(secondLaunchName);

			launchSetIds.push(secondLaunch.id);

			await launchesPage.goToEntries();

			await launchesPage.addWebContentEntry(site.name, title);

			const secondLaunchEntry = await apiHelpers.launch.getLaunchEntry(
				secondLaunch.id,
				JOURNAL_ARTICLE_CLASS_NAME
			);

			expect(secondLaunchEntry.draftId).toBeGreaterThan(0);
			expect(secondLaunchEntry.draftId).not.toBe(
				firstLaunchEntry.draftId
			);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.2',
				status: 'Draft',
				title,
				version: 'Draft',
			});
		});

		await test.step('Pick an older version in the second launch, which replaces its draft', async () => {
			const secondLaunchEntry = await apiHelpers.launch.getLaunchEntry(
				secondLaunch.id,
				JOURNAL_ARTICLE_CLASS_NAME
			);

			await launchesPage.selectEntryVersion(title, '1.2', '1.1');

			const rebasedSecondLaunchEntry =
				await apiHelpers.launch.getLaunchEntry(
					secondLaunch.id,
					JOURNAL_ARTICLE_CLASS_NAME
				);

			expect(rebasedSecondLaunchEntry.baseVersionKey).toBe('1.1');
			expect(rebasedSecondLaunchEntry.draftId).toBeGreaterThan(0);
			expect(rebasedSecondLaunchEntry.draftId).not.toBe(
				secondLaunchEntry.draftId
			);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.1',
				status: 'Draft',
				title,
				version: 'Draft',
			});
		});

		await test.step('Edit the draft of the second launch', async () => {
			await launchesPage
				.getEntryRow(title)
				.getByRole('link', {exact: true, name: title})
				.click();

			await expect(backToLaunches).toBeVisible();

			await updateDraft(secondLaunchText);

			await backToLaunches.click();

			await expect(launchesPage.newEntryButton).toBeVisible();
		});

		await test.step('The first launch kept its own draft', async () => {
			await page.goto(firstLaunchEntriesURL);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.2',
				status: 'Draft',
				title,
				version: 'Draft',
			});
		});

		let previewPage: Page;

		await test.step('Preview the first launch on the page', async () => {
			previewPage = await launchesPage.previewOnPage(pageTitle);

			expect(previewPage.url()).toContain(
				`previewLaunchSetId=${firstLaunch.id}`
			);
			expect(previewPage.url()).toContain('p_l_mode=preview');

			await expectPreview(previewPage, {
				hidden: [liveText, secondLaunchText],
				launchName: firstLaunchName,
				shown: firstLaunchText,
			});
		});

		await test.step('Switch the preview to the second launch', async () => {
			const launchPreviewPage = new LaunchPreviewPage(previewPage);

			await launchPreviewPage.switchLaunch(
				firstLaunchName,
				secondLaunchName
			);

			expect(previewPage.url()).toContain(
				`previewLaunchSetId=${secondLaunch.id}`
			);

			await expectPreview(previewPage, {
				hidden: [firstLaunchText, liveText],
				launchName: secondLaunchName,
				shown: secondLaunchText,
			});
		});

		await test.step('Switch the preview to the live site without leaving preview mode', async () => {
			const launchPreviewPage = new LaunchPreviewPage(previewPage);

			await launchPreviewPage.switchLaunch(secondLaunchName, 'No Launch');

			expect(previewPage.url()).toContain('previewLaunchSetId=0');

			await expectPreview(previewPage, {
				hidden: [firstLaunchText, secondLaunchText],
				launchName: 'No Launch',
				shown: liveText,
			});

			await previewPage.close();
		});

		await test.step('Visitors of the live page see none of it', async () => {
			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.getByRole('navigation', {name: 'Preview'})
			).toBeHidden();

			await expect(page.getByText(liveText, {exact: true})).toBeVisible();
			await expect(
				page.getByText(firstLaunchText, {exact: true})
			).toBeHidden();
			await expect(
				page.getByText(secondLaunchText, {exact: true})
			).toBeHidden();
		});

		await test.step('Publish the first launch, which writes its draft as the next version', async () => {
			await page.goto(firstLaunchEntriesURL);

			await launchesPage.publishLaunch();

			const publishedLaunchEntry = await apiHelpers.launch.getLaunchEntry(
				firstLaunch.id,
				JOURNAL_ARTICLE_CLASS_NAME
			);

			expect(publishedLaunchEntry.versionKey).toBe('1.3');
			expect(publishedLaunchEntry.draftId).toBe(0);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.2',
				published: true,
				status: 'Approved',
				title,
				version: '1.3',
			});

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.getByText(firstLaunchText, {exact: true})
			).toBeVisible();
			await expect(page.getByText(liveText, {exact: true})).toBeHidden();
		});

		await test.step('The published launch is frozen, and offers nothing to change', async () => {
			await page.goto(firstLaunchEntriesURL);

			await expect(launchesPage.publishedButton).toBeDisabled();
			await expect(launchesPage.newEntryButton).toBeHidden();

			const row = launchesPage.getEntryRow(title);

			await expect(
				row.getByRole('link', {exact: true, name: title})
			).toBeHidden();
			await expect(
				row.getByRole('link', {exact: true, name: '1.2'})
			).toBeHidden();
			await expect(
				row.getByRole('button', {name: 'Actions'})
			).toBeHidden();

			await launchesPage.goto(site.friendlyUrlPath);

			await expect(
				launchesPage.getEntryRow(firstLaunchName)
			).toContainText('Published');
			await expect(
				launchesPage.getEntryRow(secondLaunchName)
			).toContainText('In Progress');
		});

		await test.step('Publishing the second launch leaves the first one holding its own version', async () => {
			await launchesPage.openLaunch(secondLaunchName);

			await launchesPage.goToEntries();

			await launchesPage.publishLaunch();

			const publishedLaunchEntry = await apiHelpers.launch.getLaunchEntry(
				secondLaunch.id,
				JOURNAL_ARTICLE_CLASS_NAME
			);

			expect(publishedLaunchEntry.versionKey).toBe('1.4');

			await page.goto(
				`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`
			);

			await expect(
				page.getByText(secondLaunchText, {exact: true})
			).toBeVisible();
			await expect(
				page.getByText(firstLaunchText, {exact: true})
			).toBeHidden();

			await page.goto(firstLaunchEntriesURL);

			await expectEntryRow(launchesPage, {
				baseVersion: '1.2',
				published: true,
				status: 'Approved',
				title,
				version: '1.3',
			});
		});

		await test.step('The first launch is previewed as the version it wrote', async () => {
			previewPage = await launchesPage.previewOnPage(pageTitle);

			await expectPreview(previewPage, {
				hidden: [liveText, secondLaunchText],
				launchName: firstLaunchName,
				shown: firstLaunchText,
			});

			await previewPage.close();
		});
	}
);
