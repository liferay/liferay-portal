/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Page, expect, mergeTests} from '@playwright/test';
import {createReadStream} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {documentLibraryPagesTest} from '../../../fixtures/documentLibraryPages.fixtures';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {CommentsPage} from '../../../pages/comment/CommentsPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import getRandomString from '../../../utils/getRandomString';
import {openFieldset} from '../../../utils/openFieldset';
import {waitForAlert} from '../../../utils/waitForAlert';
import getPageDefinition from '../../layout-content-page-editor-web/main/utils/getPageDefinition';
import getWidgetDefinition from '../../layout-content-page-editor-web/main/utils/getWidgetDefinition';

const test = mergeTests(
	dataApiHelpersTest,
	documentLibraryPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest()
);

const IMAGE_PATH = path.join(__dirname, 'dependencies/image1.jpeg');

function getDocFile() {
	return {
		buffer: Buffer.from(getRandomString()),
		mimeType: 'application/msword',
		name: 'Document_1.doc',
	};
}

async function addDMWidgetPage({
	apiHelpers,
	site,
}: {
	apiHelpers: DataApiHelpers;
	site: Site;
}) {
	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getWidgetDefinition({
				id: getRandomString(),
				widgetName:
					'com_liferay_document_library_web_portlet_DLPortlet',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	return `/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`;
}

async function openInfoPanel(page: Page) {
	const infoPanel = page.locator('[id*="infoPanelId_"]');

	await clickAndExpectToBeVisible({
		target: infoPanel.getByRole('tab', {name: 'Details'}),
		timeout: 2000,
		trigger: page.locator('a[href*="infoPanelId_"]'),
	});

	return infoPanel;
}

test(
	'Entries with slashes and special characters in the title can be published, downloaded, moved and deleted from the widget',
	{tag: ['@LPS-51134', '@LPS-122738']},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const pageURL = await addDMWidgetPage({apiHelpers, site});

		await test.step('CanPublishTitleWithSpecialCharacters', async () => {
			const title = '/\\<>:"?|*';

			await page.goto(pageURL);

			await documentLibraryPage.goToCreateNewFile();

			await page.locator('input[type="file"]').setInputFiles(IMAGE_PATH);

			await documentLibraryEditFilePage.titleSelector.fill(title);

			await documentLibraryEditFilePage.publishFileEntry();

			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		});

		const title = `/D/M/@!# ${getRandomString()}`;

		await apiHelpers.headlessDelivery.postDocument(site.id, getDocFile(), {
			fileName: 'Document_1.doc',
			title,
		});

		await test.step('CanDownloadWithSpecialCharactersInTitle', async () => {
			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await page.getByRole('link', {exact: true, name: title}).click();

			const infoPanel = await openInfoPanel(page);

			const downloadPromise = page.waitForEvent('download');

			await infoPanel
				.getByRole('link', {exact: true, name: 'Download'})
				.click();

			const download = await downloadPromise;

			expect(download.suggestedFilename()).toBe('Document_1.doc');
		});

		await test.step('CanMoveWithSlashesInTitle', async () => {
			const folder = await apiHelpers.headlessDelivery.postDocumentFolder(
				site.id
			);

			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {exact: true, name: 'Move'}),
				trigger: page
					.locator('.list-group-item', {hasText: title})
					.getByRole('button', {name: 'Show Actions'}),
			});

			await page
				.getByRole('dialog')
				.frameLocator('iframe')
				.getByRole('row', {name: folder.name})
				.getByRole('button', {exact: true, name: 'Select'})
				.click();

			await waitForAlert(page);

			await page
				.getByRole('link', {exact: true, name: folder.name})
				.click();

			await expect(
				page.getByRole('link', {exact: true, name: title})
			).toBeVisible();
		});

		await test.step('CanDeleteDocumentInSitePage', async () => {
			await page.getByRole('link', {exact: true, name: title}).click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {
					exact: true,
					name: 'Delete',
				}),
				trigger: page.getByRole('button', {name: 'Show Actions'}),
			});

			await expect(
				page.getByText(
					'There are no documents or media files in this folder.'
				)
			).toBeVisible();
		});
	}
);

test(
	'Entry details in the widget show the image info, comments and related documents',
	{tag: ['@LPS-126086', '@LPS-126088', '@LPS-44249']},
	async ({
		apiHelpers,
		documentLibraryEditFilePage,
		documentLibraryPage,
		page,
		site,
	}) => {
		const pageURL = await addDMWidgetPage({apiHelpers, site});

		const description = getRandomString();
		const imageTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(IMAGE_PATH),
			{description, fileName: 'image1.jpeg', title: imageTitle}
		);

		await test.step('CanViewImageFromInfoPanel', async () => {
			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await page
				.getByRole('link', {exact: true, name: imageTitle})
				.click();

			const infoPanel = await openInfoPanel(page);

			await expect(infoPanel.getByText(description)).toBeVisible();
			await expect(infoPanel.getByText('Basic Document')).toBeVisible();
			await expect(
				infoPanel.getByText('jpeg', {exact: true})
			).toBeVisible();
			await expect(
				infoPanel.getByRole('img', {name: 'Thumbnail'})
			).toBeVisible();
		});

		const documentTitle = getRandomString();

		await apiHelpers.headlessDelivery.postDocument(site.id, getDocFile(), {
			title: documentTitle,
		});

		await test.step('CanAddCommentToDocument', async () => {
			const comment = getRandomString();
			const commentsPage = new CommentsPage(page);

			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await page
				.getByRole('link', {exact: true, name: documentTitle})
				.click();

			await commentsPage.addComment(comment);

			await page.goto(pageURL);

			await page
				.getByRole('link', {exact: true, name: documentTitle})
				.click();

			await expect(
				page.locator('.comment-container').filter({hasText: comment})
			).toBeVisible();
		});

		await test.step('CanViewRelatedDocument', async () => {
			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await page
				.getByRole('link', {exact: true, name: imageTitle})
				.click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('menuitem', {exact: true, name: 'Edit'}),
				trigger: page.getByRole('button', {name: 'Show Actions'}),
			});

			const relatedAssetsFieldset = await openFieldset(
				page,
				'Related Assets'
			);

			const selectButton = page
				.getByRole('dialog')
				.getByRole('button', {exact: true, name: 'Select'});

			await clickAndExpectToBeVisible({
				target: selectButton,
				trigger: relatedAssetsFieldset.getByLabel('Select Items'),
			});

			await page
				.getByRole('checkbox', {name: `Select ${documentTitle}`})
				.check();

			await selectButton.click();

			await expect(page.getByRole('dialog')).toBeHidden();

			await documentLibraryEditFilePage.publishButton.click();

			await waitForAlert(page, undefined, {first: true});

			await page.goto(pageURL);

			await page
				.getByRole('link', {exact: true, name: documentTitle})
				.click();

			const infoPanel = await openInfoPanel(page);

			await infoPanel
				.getByRole('link', {exact: true, name: imageTitle})
				.click();

			const imagePreview = page.locator('.preview-file-container img');

			await expect(imagePreview).toBeVisible();
			await expect(imagePreview).toHaveAttribute('src', /image1\.jpeg/);
		});
	}
);

test(
	'Versions tab in the widget downloads, views and reverts older versions',
	{tag: ['@LPS-39248', '@LPS-52101', '@LPS-175916']},
	async ({apiHelpers, documentLibraryPage, page, site}) => {
		const pageURL = await addDMWidgetPage({apiHelpers, site});

		const description = getRandomString();
		const title = getRandomString();

		const document = await apiHelpers.headlessDelivery.postDocument(
			site.id,
			createReadStream(IMAGE_PATH),
			{description, fileName: 'FileName.jpeg', title}
		);

		let versionActionsTrigger = page.locator('');

		const openVersionActions = async (
			documentTitle: string,
			version: string
		) => {
			await page.goto(pageURL);

			await documentLibraryPage.changeView('List');

			await page
				.getByRole('link', {exact: true, name: documentTitle})
				.click();

			const infoPanel = await openInfoPanel(page);

			await infoPanel.getByRole('tab', {name: 'Versions'}).click();

			versionActionsTrigger = infoPanel
				.getByRole('listitem')
				.filter({hasText: `Version ${version}`})
				.getByRole('button');
		};

		const clickVersionAction = async (actionName: string) => {
			const menuItem = page.getByRole('menuitem', {name: actionName});

			await expect(async () => {
				if (!(await menuItem.isVisible())) {
					await versionActionsTrigger.click({timeout: 1000});
				}

				await menuItem.click({timeout: 1000});
			}).toPass();
		};

		const assertVersionActions = async (
			actionNames: string[],
			missingActionNames: string[]
		) => {
			for (const actionName of actionNames) {
				await clickAndExpectToBeVisible({
					target: page.getByRole('menuitem', {name: actionName}),
					trigger: versionActionsTrigger,
				});
			}

			for (const actionName of missingActionNames) {
				await expect(
					page.getByRole('menuitem', {name: actionName})
				).toHaveCount(0);
			}
		};

		const downloadVersion = async () => {
			const downloadPromise = page.waitForEvent('download');

			await clickVersionAction('Download');

			return (await downloadPromise).suggestedFilename();
		};

		const editedTitle = getRandomString();

		await test.step('CanDownloadDifferentVersions', async () => {
			await openVersionActions(title, '1.0');

			await assertVersionActions(
				['Download', 'View'],
				['Delete', 'Revert']
			);

			await apiHelpers.headlessDelivery.patchDocument({
				document: {
					description: getRandomString(),
					fileName: 'FileNameEdit.jpeg',
					title: editedTitle,
				},
				documentId: document.id,
			});

			await openVersionActions(editedTitle, '1.1');

			await assertVersionActions(
				['Delete', 'Download', 'View'],
				['Revert']
			);

			expect(await downloadVersion()).toBe('FileNameEdit.jpeg');

			await openVersionActions(editedTitle, '1.0');

			await assertVersionActions(
				['Delete', 'Download', 'Revert', 'View'],
				[]
			);

			expect(await downloadVersion()).toBe('FileName.jpeg');
		});

		await test.step('CanViewOlderVersion', async () => {
			await openVersionActions(editedTitle, '1.0');

			await clickVersionAction('View');

			await expect(
				page.getByText(`${title} (Version 1.0)`, {exact: true})
			).toBeVisible();

			const infoPanel = await openInfoPanel(page);

			await expect(infoPanel.getByText(description)).toBeVisible();

			await page.getByRole('link', {name: 'Back'}).click();

			await expect(
				page.getByRole('heading', {exact: true, name: editedTitle})
			).toBeVisible();
		});

		await test.step('CanRevertToOriginal', async () => {
			await openVersionActions(editedTitle, '1.0');

			await clickVersionAction('Revert');

			await waitForAlert(page);

			await page.goto(pageURL);

			await page.getByRole('link', {exact: true, name: title}).click();

			const infoPanel = await openInfoPanel(page);

			await expect(
				infoPanel.getByText('Version 2.0', {exact: true}).first()
			).toBeVisible();
			await expect(infoPanel.getByText(description)).toBeVisible();
		});
	}
);
