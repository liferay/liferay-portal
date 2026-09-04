/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator, Page, expect, mergeTests} from '@playwright/test';
import {readFileSync} from 'fs';
import path from 'path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {fragmentsPagesTest} from '../../../fixtures/fragmentPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {ApiHelpers} from '../../../helpers/ApiHelpers';
import {PageEditorPage} from '../../../pages/layout-content-page-editor-web/PageEditorPage';
import {DisplayPageTemplatesPage} from '../../../pages/layout-page-template-admin-web/DisplayPageTemplatesPage';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {
	FIELD_TYPES,
	StructureBuilderPage,
} from '../structure-builder/pages/StructureBuilderPage';
import {cmsPagesTest} from './fixtures/cmsPagesTest';
import {ContentsPage} from './pages/ContentsPage';
import {PicklistBuilderPage} from './pages/PicklistBuilderPage';
import {SpaceSummaryPage} from './pages/SpaceSummaryPage';

const test = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	fragmentsPagesTest,
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	structureBuilderPagesTest
);

const createAndEditContent = async ({
	contentsPage,
	page,
	spaceName,
	title,
}: {
	contentsPage: ContentsPage;
	page: Page;
	spaceName?: string;
	title: string;
}) => {
	await contentsPage.goto();

	await contentsPage.createContent('Basic Web Content', spaceName);

	await page.getByLabel('Title').fill(title);

	await contentsPage.saveContent();

	await contentsPage.editContent(title);
};

const createDisplayPageTemplate = async ({
	contentType = 'Basic Web Content',
	displayPageTemplateName,
	displayPageTemplatesPage,
	field = 'Friendly URL',
	linkURL,
	page,
	pageEditorPage,
	site,
}: {
	contentType?: string;
	displayPageTemplateName: string;
	displayPageTemplatesPage: DisplayPageTemplatesPage;
	field?: string;
	linkURL?: string;
	page: Page;
	pageEditorPage: PageEditorPage;
	site: Site;
}) => {
	await displayPageTemplatesPage.goto(site.friendlyUrlPath);

	await displayPageTemplatesPage.createTemplate({
		contentType,
		name: displayPageTemplateName,
	});

	await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

	await pageEditorPage.addFragment('Basic Components', 'Heading');

	const headingId = await pageEditorPage.getFragmentId('Heading');

	await pageEditorPage.selectEditable(headingId, 'element-text');

	await page
		.getByRole('combobox', {exact: true, name: 'Field'})
		.selectOption(field);

	await pageEditorPage.waitForChangesSaved();

	if (linkURL) {
		await pageEditorPage.addFragment('Basic Components', 'Button');

		await pageEditorPage.mapEditableLink({
			editableId: 'link',
			fragmentName: 'Button',
			linkConfiguration: {
				type: 'URL',
				url: linkURL,
			},
		});
	}

	await pageEditorPage.publishPage();
};

const createStructureWithAllFields = async ({
	apiHelpers,
	picklistBuilderPage,
	structureBuilderPage,
}: {
	apiHelpers: ApiHelpers;
	picklistBuilderPage: PicklistBuilderPage;
	structureBuilderPage: StructureBuilderPage;
}) => {
	const structureLabel = `Structure${getRandomInt()}`;

	const picklist = await picklistBuilderPage.createPicklist();

	await apiHelpers.listTypeAdmin.postListTypeEntry({
		key: 'option',
		listTypeDefinitionExternalReferenceCode: picklist.externalReferenceCode,
		name_i18n: {en_US: 'Option'},
	});

	await structureBuilderPage.createStructureFromData({
		label: structureLabel,
		name: structureLabel,
		page: structureBuilderPage,
		publish: false,
	});

	for (const type of FIELD_TYPES) {
		await structureBuilderPage.addField(type);

		if (type === 'Select from List') {
			await structureBuilderPage.changeFieldSettings({
				picklist: picklist.name,
			});
		}
		else if (type === 'Select Related Content') {
			await structureBuilderPage.changeFieldSettings({
				relatedContent: 'Basic Document',
			});
		}
		else if (type === 'Upload') {
			await structureBuilderPage.changeFieldSettings({
				requestFile: 'computer',
			});
		}
	}

	await structureBuilderPage.publishStructure();

	return structureLabel;
};

const createSpaceAndConnectSites = async ({
	apiHelpers,
	site,
	spaceName,
	spaceSummaryPage,
}: {
	apiHelpers: ApiHelpers;
	site: Site;
	spaceName: string;
	spaceSummaryPage: SpaceSummaryPage;
}) => {
	await apiHelpers.headlessAssetLibrary.createAssetLibrary({
		name: spaceName,
		settings: {},
		type: 'Space',
	});

	await spaceSummaryPage.goto(spaceName);
	await spaceSummaryPage.connectSite('Global');
	await spaceSummaryPage.connectSite(site.name);
};

test(
	'The content preview opens, resizes and closes correctly, taking focus into account',
	{tag: '@LPD-84613'},
	async ({contentsPage, page}) => {
		const title = getRandomString();
		const previewTitle = `${title} Preview`;

		try {
			await test.step('Create a new basic web content and edit it', async () => {
				await createAndEditContent({contentsPage, page, title});
			});

			const preview = page.getByLabel(previewTitle);

			await test.step('Open the content preview', async () => {
				await page.getByRole('button', {name: 'Open Preview'}).click();

				await expect(page.getByText(previewTitle)).toBeVisible();
				await expect(preview).toBeFocused();
			});

			await test.step('Resize the content preview', async () => {
				await page.keyboard.press('Tab');

				await expect(
					preview.getByRole('button', {name: 'Close Preview'})
				).toBeFocused();

				await page.keyboard.press('Tab');
				await page.keyboard.press('Tab');
				await page.keyboard.press('Tab');

				const resizeHandle = preview.getByRole('separator');

				await expect(resizeHandle).toBeFocused();

				const initWidth = (await preview.boundingBox())?.width ?? 0;
				const resizeHandleBox = await resizeHandle.boundingBox();

				if (resizeHandleBox) {
					await page.mouse.move(
						resizeHandleBox.x + resizeHandleBox.width / 2,
						100
					);
					await page.mouse.down();
					await page.mouse.move(
						resizeHandleBox.x + resizeHandleBox.width / 2 + 200,
						100,
						{steps: 20}
					);
					await page.mouse.up();
				}

				expect((await preview.boundingBox())?.width).toBeLessThan(
					initWidth
				);
			});

			await test.step('Close the content preview from the close button', async () => {
				await preview
					.getByRole('button', {name: 'Close Preview'})
					.click();

				await expect(contentsPage.previewButton).toBeFocused();
				await expect(page.getByText(previewTitle)).not.toBeVisible();
			});

			await test.step('Close the content preview from the preview button', async () => {
				await page.getByRole('button', {name: 'Open Preview'}).click();

				await expect(page.getByText(previewTitle)).toBeVisible();

				await page
					.locator('.content-editor__toolbar')
					.getByRole('button', {name: 'Close Preview'})
					.click();

				await expect(contentsPage.previewButton).toBeFocused();
				await expect(page.getByText(previewTitle)).not.toBeVisible();
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);

test(
	'Preview a content',
	{tag: '@LPD-85584'},
	async ({
		apiHelpers,
		contentsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		spaceSummaryPage,
	}) => {
		const displayPageTemplateName = getRandomString();
		const linkURL = `/web${site.friendlyUrlPath}`;
		const title = getRandomString();
		const previewTitle = `${title} Preview`;
		const spaceName = `Space ${getRandomString()}`;

		try {
			await test.step('Create a display page template', async () => {
				await createDisplayPageTemplate({
					displayPageTemplateName,
					displayPageTemplatesPage,
					linkURL,
					page,
					pageEditorPage,
					site,
				});
			});

			await test.step('Create a space and sites to the space', async () => {
				await createSpaceAndConnectSites({
					apiHelpers,
					site,
					spaceName,
					spaceSummaryPage,
				});
			});

			await test.step('Create a new basic web content and edit it', async () => {
				await createAndEditContent({
					contentsPage,
					page,
					spaceName,
					title,
				});
			});

			await test.step('Go to the content preview and select a channel that does not have a display page template', async () => {
				await contentsPage.previewButton.click();

				await expect(page.getByText(previewTitle)).toBeVisible();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'Global',
					}),
					trigger: page.getByLabel('Select Channel'),
				});

				await expect(
					page.getByText(
						'No display page templates are available for preview in this channel'
					)
				).toBeVisible();
			});

			await test.step('Select a channel and a display page template', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: site.name,
					}),
					trigger: page.getByLabel('Select Channel'),
				});

				await expect(
					page.getByText(
						'No display page templates are available for preview in this channel'
					)
				).not.toBeVisible();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: displayPageTemplateName,
					}),
					trigger: page.getByLabel('Select Display Page'),
				});
			});

			await test.step('Check that the display page appears in the preview and is interactive but does not navigate', async () => {
				const friendlyURL = await page
					.getByLabel('Friendly URL')
					.inputValue();

				const iframe = page.frameLocator('iframe');

				await expect(iframe.getByText(friendlyURL)).toBeVisible();

				await iframe.getByRole('link', {name: 'Go Somewhere'}).click();

				// Give Senna time to navigate if the iframe is not prevented.

				await page.waitForTimeout(2000);

				await expect(iframe.getByText(friendlyURL)).toBeVisible();
			});

			await test.step('Check the button to open the preview in another tab', async () => {
				const openPreviewNewTabButton = page.getByTitle(
					'Open Preview in a new tab.'
				);

				await expect(openPreviewNewTabButton).toBeVisible();
				await expect(openPreviewNewTabButton).toHaveRole('link');
				await expect(openPreviewNewTabButton).toHaveAttribute(
					'target',
					'_blank'
				);
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);

test(
	'Preview a content from the preview modal in mobile view',
	{tag: '@LPD-85583'},
	async ({
		apiHelpers,
		contentsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
		spaceSummaryPage,
	}) => {
		const displayPageTemplateName = getRandomString();
		const title = getRandomString();
		const modalTitle = `Preview ${title}`;
		const spaceName = `Space ${getRandomString()}`;

		try {
			await test.step('Create a display page template', async () => {
				await createDisplayPageTemplate({
					displayPageTemplateName,
					displayPageTemplatesPage,
					page,
					pageEditorPage,
					site,
				});
			});

			await test.step('Create a space and sites to the space', async () => {
				await createSpaceAndConnectSites({
					apiHelpers,
					site,
					spaceName,
					spaceSummaryPage,
				});
			});

			await test.step('Create a new basic web content and edit it', async () => {
				await createAndEditContent({
					contentsPage,
					page,
					spaceName,
					title,
				});
			});

			await test.step('Set viewport to mobile size', async () => {
				await page.setViewportSize({height: 932, width: 430});
			});

			await test.step('Open the preview modal from the mobile preview button', async () => {
				await page
					.locator('.content-editor__toolbar')
					.getByTitle('Preview')
					.click();

				await expect(page.getByText(modalTitle)).toBeVisible();
			});

			await test.step('Select a channel without display page templates and check that the alert appears', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: 'Global',
					}),
					trigger: page.getByLabel('Select Channel'),
				});

				await expect(
					page.getByText(
						'No display page templates are available for preview in this channel'
					)
				).toBeVisible();

				const previewInNewTabButton = page.getByRole('button', {
					name: 'Preview in a new tab',
				});

				await expect(previewInNewTabButton).toBeDisabled();
			});

			await test.step('Select a channel with display page templates and check that the preview button is a link', async () => {
				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: site.name,
					}),
					trigger: page.getByLabel('Select Channel'),
				});

				await expect(
					page.getByText(
						'No display page templates are available for preview in this channel'
					)
				).not.toBeVisible();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: displayPageTemplateName,
					}),
					trigger: page.getByLabel('Select Display Page'),
				});

				const previewInNewTabLink = page.getByRole('link', {
					name: 'Preview in a new tab',
				});

				await expect(previewInNewTabLink).toHaveAttribute('href', /.+/);
				await expect(previewInNewTabLink).toHaveAttribute(
					'target',
					'_blank'
				);
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);

test(
	'The unsaved changes alert appears when any field type is edited in the preview',
	{tag: '@LPD-86440'},
	async ({
		apiHelpers,
		contentsPage,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		picklistBuilderPage,
		site,
		spaceSummaryPage,
		structureBuilderPage,
	}) => {
		const displayPageTemplateName = getRandomString();
		const fileTitle = `File ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;
		const title = getRandomString();
		const unsavedChangesAlert = page.getByText(
			'You have unsaved changes. Save as draft or publish to update this preview.'
		);

		let structureLabel = '';

		try {
			await test.step('Create a structure with all field types', async () => {
				structureLabel = await createStructureWithAllFields({
					apiHelpers,
					picklistBuilderPage,
					structureBuilderPage,
				});
			});

			await test.step('Create a display page template for the new content type', async () => {
				await createDisplayPageTemplate({
					contentType: structureLabel,
					displayPageTemplateName,
					displayPageTemplatesPage,
					page,
					pageEditorPage,
					site,
				});
			});

			await test.step('Create a space and connect sites', async () => {
				await createSpaceAndConnectSites({
					apiHelpers,
					site,
					spaceName,
					spaceSummaryPage,
				});
			});

			await test.step('Create a basic document to relate content to', async () => {
				await apiHelpers.objectEntry.postObjectEntry(
					{
						file: {
							fileBase64: readFileSync(
								path.join(
									__dirname,
									'/dependencies/file_upload_image_1.jpg'
								)
							).toString('base64'),
							name: `${fileTitle}.jpg`,
						},
						objectEntryFolderExternalReferenceCode: 'L_FILES',
						title: fileTitle,
					},
					'cms/basic-documents',
					spaceName
				);
			});

			await test.step('Create a content from the new structure', async () => {
				await contentsPage.goto();

				await contentsPage.createContent(structureLabel, spaceName);

				await page.getByLabel('Title').fill(title);

				await contentsPage.saveContent();

				await contentsPage.editContent(title);
			});

			const form = page.locator('.lfr-layout-structure-item-form');

			const fill = async (input: Locator, value: string) => {
				await input.waitFor({state: 'visible', timeout: 15000});
				await input.scrollIntoViewIfNeeded();
				await input.fill(value);
			};

			const fieldInteractions: Array<{
				action: () => Promise<void>;
				label: string;
			}> = [
				{
					action: () =>
						fill(
							form.getByRole('textbox', {
								exact: true,
								name: 'Text',
							}),
							'text value'
						),
					label: 'Title',
				},
				{
					action: () =>
						fill(form.getByLabel('Long Text'), 'long text value'),
					label: 'Long Text',
				},
				{
					action: async () => {
						const editable = form.locator('.ck-editor__editable');

						await editable.scrollIntoViewIfNeeded();
						await editable.click();
						await page.keyboard.type('rich text value');
					},
					label: 'Rich Text',
				},
				{
					action: () => fill(form.getByLabel('Decimal'), '1.5'),
					label: 'Decimal',
				},
				{
					action: async () => {
						const trigger = form
							.getByLabel('Open Options Menu')
							.nth(1);

						await trigger.scrollIntoViewIfNeeded();

						await trigger.click();

						await clickAndExpectToBeVisible({
							autoClick: true,
							target: page
								.getByRole('listbox')
								.getByRole('option')
								.first(),
							trigger,
						});
					},
					label: 'Select from List',
				},
				{
					action: () => fill(form.getByLabel('Numeric'), '42'),
					label: 'Numeric',
				},
				{
					action: () =>
						fill(form.getByLabel('Date').first(), '2026-05-01'),
					label: 'Date',
				},
				{
					action: () =>
						fill(
							form.getByLabel('Date').nth(1),
							'2026-05-01T13:30'
						),
					label: 'Date and Time',
				},
				{
					action: async () => {
						const checkbox = form.getByLabel('Boolean');

						await checkbox.scrollIntoViewIfNeeded();
						await checkbox.click();
					},
					label: 'Boolean',
				},

				{
					action: async () => {
						const uploadButton = form.getByRole('button', {
							exact: true,
							name: 'Select File',
						});

						await uploadButton.scrollIntoViewIfNeeded();

						const fileChooserPromise =
							page.waitForEvent('filechooser');

						await uploadButton.click();

						const fileChooser = await fileChooserPromise;

						await fileChooser.setFiles(
							path.join(
								__dirname,
								'/dependencies/file_upload_image_1.jpg'
							)
						);

						await expect(
							form.getByText('file_upload_image_1.jpg')
						).toBeVisible();
					},
					label: 'Upload',
				},
				{
					action: () =>
						fill(form.locator('input[type="tel"]'), '2125551234'),
					label: 'Phone Number',
				},
				{
					action: async () => {
						const trigger = form.getByRole('combobox', {
							name: 'Select Related Content',
						});

						await trigger.scrollIntoViewIfNeeded();

						await clickAndExpectToBeVisible({
							autoClick: true,
							target: page.getByRole('option', {
								name: fileTitle,
							}),
							trigger,
						});
					},
					label: 'Select Related Content',
				},
			];

			await contentsPage.previewButton.click();

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {name: site.name}),
				trigger: page.getByLabel('Select Channel'),
			});

			await clickAndExpectToBeVisible({
				autoClick: true,
				target: page.getByRole('option', {
					name: displayPageTemplateName,
				}),
				trigger: page.getByLabel('Select Display Page'),
			});

			for (const {action, label} of fieldInteractions) {
				await test.step(`Editing the ${label} field shows the unsaved changes alert`, async () => {
					await expect(unsavedChangesAlert).not.toBeVisible();

					await action();

					await expect(unsavedChangesAlert).toBeVisible();

					await page.reload();
				});
			}
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);

test(
	'The external URL preview shows an error for a blocked URL and renders the iframe when a valid one is entered',
	{tag: '@LPD-89124'},
	async ({contentsPage, page}) => {
		const title = getRandomString();
		const previewTitle = `${title} Preview`;

		try {
			await test.step('Create a basic web content and edit it', async () => {
				await createAndEditContent({contentsPage, page, title});
			});

			await test.step('Open the preview and select the External URL channel', async () => {
				await contentsPage.previewButton.click();

				await expect(page.getByText(previewTitle)).toBeVisible();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {name: 'External URL'}),
					trigger: page.getByLabel('Select Channel'),
				});
			});

			const externalURLInput = page.getByLabel('External URL', {
				exact: true,
			});

			const errorAlert = page
				.locator('.alert')
				.filter({hasText: 'We could not load the preview'});

			await page.route('https://blocked.test/**', (route) =>
				route.fulfill({
					body: '<html><body>Blocked</body></html>',
					headers: {'X-Frame-Options': 'DENY'},
					status: 200,
				})
			);

			await page.route('https://valid.test/**', async (route) => {
				await new Promise((resolve) => setTimeout(resolve, 800));

				await route.fulfill({
					body: '<html><body>Valid Preview</body></html>',
					status: 200,
				});
			});

			await test.step('Enter a URL that blocks iframe embedding and check the error alert appears', async () => {
				await externalURLInput.fill('blocked.test');
				await externalURLInput.blur();

				await expect(externalURLInput).toHaveValue(
					'https://blocked.test'
				);

				await expect(errorAlert).toBeVisible();

				await page.reload();

				await expect(errorAlert).toBeVisible();
			});

			await test.step('Click refresh in the error alert and verify a new request is issued', async () => {
				const requestPromise = page.waitForRequest(
					'https://blocked.test/'
				);

				await errorAlert.getByRole('button', {name: 'Refresh'}).click();

				await requestPromise;
			});

			await test.step('Enter a valid URL and check that its content renders in the iframe', async () => {
				await externalURLInput.fill('https://valid.test');
				await externalURLInput.press('Enter');

				const iframe = page.frameLocator('iframe[title="Preview"]');

				await expect(iframe.getByText('Valid Preview')).toBeVisible();

				await page.reload();

				await expect(iframe.getByText('Valid Preview')).toBeVisible();
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(title);
			});
		}
	}
);

test(
	'Switching the editing language updates the content preview',
	{tag: '@LPD-87037'},
	async ({
		apiHelpers,
		contentsPage,
		displayPageTemplatesPage,
		localizationSelectPage,
		page,
		pageEditorPage,
		site,
		spaceSummaryPage,
	}) => {
		const catalanTitle = `Catalan ${getRandomString()}`;
		const displayPageTemplateName = getRandomString();
		const englishTitle = `English ${getRandomString()}`;
		const spanishTitle = `Spanish ${getRandomString()}`;
		const spaceName = `Space ${getRandomString()}`;

		try {
			await test.step('Create a display page template that maps the Title field', async () => {
				await createDisplayPageTemplate({
					displayPageTemplateName,
					displayPageTemplatesPage,
					field: 'Title',
					page,
					pageEditorPage,
					site,
				});
			});

			await test.step('Create a space and connect sites', async () => {
				await createSpaceAndConnectSites({
					apiHelpers,
					site,
					spaceName,
					spaceSummaryPage,
				});
			});

			await test.step('Create a content with the English, Spanish and Catalan title', async () => {
				await contentsPage.goto();

				await contentsPage.createContent(
					'Basic Web Content',
					spaceName
				);

				await page.getByLabel('Title').fill(englishTitle);

				await localizationSelectPage.switchLanguage('es-ES');

				await page.getByLabel('Title').fill(spanishTitle);

				await localizationSelectPage.switchLanguage('ca-ES');

				await page.getByLabel('Title').fill(catalanTitle);

				await localizationSelectPage.switchLanguage('en-US');

				await contentsPage.saveContent();
			});

			await test.step('Open the preview and verify the English title is shown', async () => {
				await contentsPage.editContent(englishTitle);

				await contentsPage.previewButton.click();

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {name: site.name}),
					trigger: page.getByLabel('Select Channel'),
				});

				await clickAndExpectToBeVisible({
					autoClick: true,
					target: page.getByRole('option', {
						name: displayPageTemplateName,
					}),
					trigger: page.getByLabel('Select Display Page'),
				});

				const iframe = page.frameLocator('iframe[title="Preview"]');

				await expect(iframe.getByText(englishTitle)).toBeVisible();
			});

			await test.step('Switch to Spanish and verify the preview updates', async () => {
				await localizationSelectPage.switchLanguage('es-ES');

				const iframe = page.frameLocator('iframe[title="Preview"]');

				await expect(iframe.getByText(spanishTitle)).toBeVisible();
				await expect(iframe.getByText(englishTitle)).not.toBeVisible();
			});

			await test.step('Close the preview, switch to Catalan and reopen the preview and verify the selected language persists', async () => {
				await contentsPage.previewButton.click();

				await expect(
					page
						.locator('.content-editor__preview')
						.getByText('Preview', {exact: true})
				).not.toBeVisible();

				await localizationSelectPage.switchLanguage('ca-ES');

				await contentsPage.previewButton.click();

				const iframe = page.frameLocator('iframe[title="Preview"]');

				await expect(iframe.getByText(catalanTitle)).toBeVisible();
				await expect(iframe.getByText(spanishTitle)).not.toBeVisible();
			});
		}
		finally {
			await test.step('Delete content', async () => {
				await contentsPage.goto();

				await contentsPage.deleteContent(englishTitle);
			});
		}
	}
);
