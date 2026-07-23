/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {addCMSAdministrator} from '../../../utils/addCMSAdministrator';
import {applyFDSSelectionFilter} from '../../../utils/applyFDSSelectionFilter';
import {clickAndExpectToBeHidden} from '../../../utils/clickAndExpectToBeHidden';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {performUserSwitch, userData} from '../../../utils/performLogin';
import {getTempDir} from '../../../utils/temp';
import {waitForAlert} from '../../../utils/waitForAlert';
import postSingleApproverCopy from '../../portal-workflow-kaleo-designer-web/main/utils/postSingleApproverCopy';
import {structureBuilderPagesTest} from '../structure-builder/fixtures/structureBuilderPagesTest';
import {cmsPagesTest} from './fixtures/cmsPagesTest';

const test = mergeTests(
	cmsPagesTest,
	structureBuilderPagesTest,
	dataApiHelpersTest,
	loginTest()
);

const testWithModalExportImport = mergeTests(
	cmsPagesTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-57655': {enabled: false},
	}),
	loginTest(),
	structureBuilderPagesTest
);

test(
	'Structure can be deleted without confirmation if it does not have an approved status',
	{tag: '@LPD-51516'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				scope: 'depot',
				status: {code: 2},
			})) as ObjectDefinition;
		const structureName = objectDefinition.name;

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: structureName,
		});
		await waitForAlert(page, `${structureName} was deleted successfully`, {
			type: 'success',
		});

		await expect(structuresPage.getItem(structureName)).toBeHidden();
	}
);

test(
	'Structure can be deleted after manual confirmation if it has an approved status',
	{tag: '@LPD-51516'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				scope: 'depot',
				status: {code: 0},
			})) as ObjectDefinition;
		const structureName = objectDefinition.name;

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: structureName,
		});

		await expect(page.locator('.liferay-modal .modal-dialog')).toHaveClass(
			/modal-dialog-centered/
		);

		await page
			.getByPlaceholder('Confirm Content Structure Name')
			.fill(structureName);
		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page, `${structureName} was deleted successfully`, {
			type: 'success',
		});

		await expect(structuresPage.getItem(structureName)).toBeHidden();
	}
);

test(
	'Structures cannot be deleted if they have a relation',
	{tag: '@LPD-51516'},
	async ({page, structureBuilderPage, structuresPage}) => {

		// Create structures and relate them

		const structureLabel = getRandomString();

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: `StructureName${getRandomInt()}`,
			page: structureBuilderPage,
		});

		const structure2Label = getRandomString();

		await structureBuilderPage.createStructureFromData({
			label: structure2Label,
			name: `StructureName2${getRandomInt()}`,
			page: structureBuilderPage,
		});

		await structureBuilderPage.addReferencedStructures([structureLabel]);

		await structureBuilderPage.publishStructure();

		// Try to delete them

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: structureLabel,
		});

		await expect(
			page.getByRole('heading', {name: 'Deletion Not Allowed'})
		).toBeVisible();

		await expect(page.locator('.liferay-modal .modal-dialog')).toHaveClass(
			/modal-dialog-centered/
		);

		await clickAndExpectToBeHidden({
			target: page.getByRole('button', {name: 'OK'}),
			trigger: page.getByRole('button', {name: 'OK'}),
		});

		await structuresPage.execItemAction({
			action: 'Delete',
			filter: structure2Label,
		});

		await expect(
			page.getByRole('heading', {name: 'Deletion Not Allowed'})
		).toBeVisible();
	}
);

test(
	'Some actions are only allowed to non-system structures',
	{tag: '@LPD-51405'},
	async ({apiHelpers, page, structuresPage}) => {
		await structuresPage.goto();

		await page
			.getByRole('row', {name: 'Basic Document'})
			.locator('.dropdown-toggle')
			.click();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'Edit'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'View Usages'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Export as JSON'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import and Override',
			})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Permissions'})
		).toBeVisible();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'Delete'})
		).toBeHidden();

		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				scope: 'depot',
				status: {code: 0},
			})) as ObjectDefinition;

		await structuresPage.goto();

		await page
			.getByRole('row', {name: objectDefinition.name})
			.locator('.dropdown-toggle')
			.click();

		expect(
			page.getByRole('menuitem', {exact: true, name: 'Edit'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'View Usages'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Export as JSON'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {
				exact: true,
				name: 'Import and Override',
			})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Permissions'})
		).toBeVisible();
		expect(
			page.getByRole('menuitem', {exact: true, name: 'Delete'})
		).toBeVisible();
	}
);

test(
	'Bulk assign default workflow modal',
	{tag: '@LPD-76635'},
	async ({apiHelpers, page, structuresPage}) => {
		let workflowDefinition: WorkflowDefinition;

		await test.step('Create a custom workflow definition', async () => {
			workflowDefinition = await postSingleApproverCopy(apiHelpers);

			apiHelpers.data.push({
				id: workflowDefinition.id,
				type: 'workflowDefinition',
			});
		});

		await test.step('Log in as an CMS Administrator', async () => {
			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			const cmsAdminRole =
				await apiHelpers.headlessAdminUser.getRoleByName(
					'CMS Administrator'
				);

			await apiHelpers.headlessAdminUser.postRoleUserAccountAssociation(
				cmsAdminRole.id,
				Number(user.id)
			);

			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Check if the modal show the correct value default value', async () => {
			await structuresPage.goto();

			await page.getByRole('link', {name: 'Basic Document'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await page
				.getByLabel('Select Workflow')
				.first()
				.selectOption(workflowDefinition.name);

			await page.getByRole('button', {name: 'Publish'}).click();

			await waitForAlert(page, `Success:`, {
				type: 'success',
			});

			await structuresPage.goto();

			await page
				.getByRole('checkbox', {name: 'Select Basic Document'})
				.click();

			await page
				.getByRole('button', {name: 'Assign Default Workflow'})
				.click();

			await expect(
				page.getByLabel('Default Workflow', {exact: true})
			).toBeVisible();

			await expect(
				page.getByLabel('Default Workflow', {exact: true})
			).toHaveValue('');
		});

		await test.step('Check if the scoped workflow settings persist', async () => {
			await page
				.getByLabel('Default Workflow', {exact: true})
				.selectOption('Single Approver');

			await page
				.getByLabel('Assign Default Workflow to')
				.getByRole('button', {name: 'Assign Workflow'})
				.click();

			await waitForAlert(page, `Success:`, {
				type: 'success',
			});

			await page.getByRole('link', {name: 'Basic Document'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await expect(page.getByLabel('Default Workflow')).toHaveValue(
				'Single Approver'
			);

			await expect(
				page.getByLabel('Select Workflow').first()
			).toHaveValue(workflowDefinition.name);

			await page.getByLabel('Select Workflow').first().selectOption('');

			await page.getByRole('button', {name: 'Publish'}).click();

			await waitForAlert(page, `Success:`, {
				type: 'success',
			});
		});

		await test.step('Check if the modal can detect mixed workflow options', async () => {
			await structuresPage.goto();

			await page
				.getByRole('checkbox', {name: 'Select Basic Document'})
				.click();

			await page
				.getByRole('checkbox', {name: 'Select Basic Web Content'})
				.click();

			await page
				.getByRole('button', {name: 'Assign Default Workflow'})
				.click();

			await expect(
				page.getByLabel('Default Workflow', {exact: true})
			).toBeVisible();

			await expect(
				page.getByLabel('Default Workflow', {exact: true})
			).toHaveValue('Mixed Workflows');

			await expect(
				page
					.getByLabel('Assign Workflow')
					.getByRole('button', {name: 'Assign Workflow'})
			).toBeDisabled();
		});

		await test.step('Check if the Bulk action can update multiple structures', async () => {
			await page
				.getByLabel('Default Workflow', {exact: true})
				.selectOption(workflowDefinition.name);

			await expect(
				page.getByRole('button', {name: 'Assign Workflow'})
			).toBeEnabled();

			await page.getByRole('button', {name: 'Assign Workflow'}).click();

			await expect(
				page
					.getByRole('alert')
					.locator('div')
					.filter({hasText: 'Success:'})
					.first()
			).toBeVisible();

			await page.getByRole('link', {name: 'Basic Document'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await expect(page.getByLabel('Default Workflow')).toBeVisible();

			await expect(page.getByLabel('Default Workflow')).toHaveValue(
				workflowDefinition.name
			);

			await structuresPage.goto();

			await page.getByRole('link', {name: 'Basic Web Content'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await expect(page.getByLabel('Default Workflow')).toBeVisible();

			await expect(page.getByLabel('Default Workflow')).toHaveValue(
				workflowDefinition.name
			);
		});

		await test.step('Check if the Bulk action can clear multiple structures', async () => {
			await structuresPage.goto();

			await page
				.getByRole('checkbox', {name: 'Select Basic Document'})
				.click();

			await page
				.getByRole('checkbox', {name: 'Select Basic Web Content'})
				.click();

			await page
				.getByRole('button', {name: 'Assign Default Workflow'})
				.click();

			await expect(
				page.getByLabel('Default Workflow', {exact: true})
			).toBeVisible();

			await page
				.getByLabel('Default Workflow', {exact: true})
				.selectOption('');

			await expect(
				page.getByRole('button', {name: 'Assign Workflow'})
			).toBeEnabled();

			await page.getByRole('button', {name: 'Assign Workflow'}).click();

			await waitForAlert(page, `Success:`, {
				type: 'success',
			});

			await page.getByRole('link', {name: 'Basic Document'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await expect(page.getByLabel('Default Workflow')).toBeVisible();

			await expect(page.getByLabel('Default Workflow')).toHaveValue('');

			await structuresPage.goto();

			await page.getByRole('link', {name: 'Basic Web Content'}).click();

			await page.getByRole('tab', {name: 'Workflow'}).click();

			await expect(page.getByLabel('Default Workflow')).toBeVisible();

			await expect(page.getByLabel('Default Workflow')).toHaveValue('');
		});
	}
);

testWithModalExportImport(
	'Export and Import Content Structures actions open the export modal from the breadcrumb',
	{tag: '@LPD-78381'},
	async ({page, structuresPage}) => {
		await structuresPage.openMenuItem('Export');

		await expect(page.locator('.modal-title')).toHaveText(
			'Export Content Structures'
		);

		await structuresPage.openMenuItem('Import');

		await expect(page.locator('.modal-title')).toHaveText(
			'Import Content Structures'
		);
	}
);

testWithModalExportImport(
	'CMS Administrator can export and import content structures',
	{tag: '@LPD-87533'},
	async ({apiHelpers, page, structuresPage}) => {
		let larFilePath: string;

		await test.step('Log in as a CMS Administrator', async () => {
			const user = await addCMSAdministrator(apiHelpers);

			await performUserSwitch(page, user.alternateName);
		});

		await test.step('Export content structures and download the LAR', async () => {
			await structuresPage.openMenuItem('Export');

			await expect(page.locator('.modal-title')).toHaveText(
				'Export Content Structures'
			);

			const exportDialog = page
				.getByRole('dialog', {name: 'Export Content Structures'})
				.frameLocator('iframe');

			await exportDialog
				.getByRole('button', {exact: true, name: 'Export'})
				.click();

			await expect(
				exportDialog.getByRole('cell', {name: 'In Progress'}).first()
			).toBeVisible();

			await expect(
				exportDialog.getByRole('cell', {name: 'Successful'}).first()
			).toBeVisible({timeout: 30000});

			const downloadPromise = page.waitForEvent('download');

			await exportDialog
				.getByRole('link', {name: /\.lar/i})
				.first()
				.click();

			const download = await downloadPromise;

			larFilePath = `${getTempDir()}/${download.suggestedFilename()}`;

			await download.saveAs(larFilePath);
		});

		await test.step('Import the downloaded LAR back', async () => {
			await structuresPage.openMenuItem('Import');

			await expect(page.locator('.modal-title')).toHaveText(
				'Import Content Structures'
			);

			const importDialog = page
				.getByRole('dialog', {name: 'Import Content Structures'})
				.frameLocator('iframe');

			await importDialog
				.locator('input[type="file"]')
				.setInputFiles(larFilePath);

			await importDialog.getByRole('button', {name: 'Continue'}).click();

			await importDialog.getByRole('button', {name: 'Import'}).click();

			await expect(
				importDialog.getByRole('cell', {name: 'Successful'}).first()
			).toBeVisible({timeout: 30000});
		});
	}
);

testWithModalExportImport(
	'Export Content Structures list includes only object definitions from CMS folders',
	{tag: '@LPD-78381'},
	async ({apiHelpers, page, structuresPage}) => {
		const contentCountBadge = page
			.getByRole('dialog', {name: 'Export Content Structures'})
			.frameLocator('iframe')
			.locator(
				'label[for="_com_liferay_exportimport_web_portlet_ExportImportPortlet_PORTLET_DATA_com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet"] .badge-info'
			);

		await structuresPage.openMenuItem('Export');

		await contentCountBadge.waitFor({state: 'visible'});

		const initialCount = parseInt(
			(await contentCountBadge.textContent()) ?? '0',
			10
		);

		const objectDefinition1 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_FILE_TYPES',
				scope: 'depot',
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition1.id,
			type: 'objectDefinition',
		});

		const objectDefinition2 =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition2.id,
			type: 'objectDefinition',
		});

		await structuresPage.openMenuItem('Export');

		await expect(contentCountBadge).toHaveText(String(initialCount + 1));
	}
);

test(
	'Content Structure can be exported as JSON and imported back to override changes',
	{tag: '@LPD-89302'},
	async ({page, structureBuilderPage, structuresPage}) => {
		const structureLabel = `Structure${getRandomInt()}`;

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
		});

		await structuresPage.goto();

		const downloadPromise = page.waitForEvent('download');

		await structuresPage.execItemAction({
			action: 'Export as JSON',
			filter: structureLabel,
		});

		const download = await downloadPromise;

		const jsonFilePath = `${getTempDir()}/${download.suggestedFilename()}`;

		await download.saveAs(jsonFilePath);

		await page.getByRole('link', {name: structureLabel}).click();

		await structureBuilderPage.addField('Long Text');

		await expect(
			page.locator('.treeview-link', {hasText: 'Long Text'})
		).toBeVisible();

		await structureBuilderPage.publishStructure();

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Import and Override',
			filter: structureLabel,
		});

		const importDialog = page.getByRole('dialog', {
			name: 'Import and Override Content Structure',
		});

		const fileChooserPromise = page.waitForEvent('filechooser');

		await importDialog.getByRole('button', {name: 'Add'}).click();

		const fileChooser = await fileChooserPromise;

		await fileChooser.setFiles(jsonFilePath);

		const importButton = importDialog.getByRole('button', {
			name: 'Import and Override',
		});

		await expect(importButton).toBeEnabled();

		await importButton.click();

		await expect(importDialog).not.toBeAttached();

		await page.getByRole('link', {name: structureLabel}).click();

		await expect(
			page.getByRole('heading', {name: structureLabel})
		).toBeVisible();

		await expect(
			page.locator('.treeview-link', {hasText: 'Long Text'})
		).not.toBeVisible();
	}
);

test(
	'New fields can be added and removed on Basic Web Content but existing fields are locked',
	{tag: '@LPD-89302'},
	async ({page, structureBuilderPage, structuresPage}) => {
		await structuresPage.goto();

		await page.getByRole('link', {name: 'Basic Web Content'}).click();

		const contentTreeItem = page.locator('.treeview-link', {
			hasText: 'Content',
		});
		const titleTreeItem = page.locator('.treeview-link', {
			hasText: 'Title',
		});

		await structureBuilderPage.selectFields([{label: 'Content'}]);

		await expect(
			contentTreeItem.getByLabel('Field Options')
		).not.toBeVisible();

		await structureBuilderPage.selectFields([{label: 'Title'}]);

		await expect(
			titleTreeItem.getByLabel('Field Options')
		).not.toBeVisible();

		await structureBuilderPage.addField('Long Text');

		const longTextTreeItem = page.locator('.treeview-link', {
			hasText: 'Long Text',
		});

		await expect(longTextTreeItem).toBeVisible();

		await structureBuilderPage.deleteFields([{label: 'Long Text'}]);

		await expect(longTextTreeItem).not.toBeVisible();
	}
);

test(
	'View Usages lists the assets using the structure and deletes them with the CMS confirmation modal',
	{tag: ['@LPD-89302', '@LPD-89560']},
	async ({apiHelpers, page, structuresPage}) => {
		const applicationName = 'cms/basic-web-contents';
		const title = `Content ${getRandomString()}`;

		await apiHelpers.objectEntry.postObjectEntry(
			{
				objectEntryFolderExternalReferenceCode: 'L_CONTENTS',
				title,
			},
			applicationName,
			'Default'
		);

		// Open the usages list of the structure

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'View Usages',
			filter: 'Basic Web Content',
		});

		await test.step('Usages list includes the asset', async () => {
			await page.locator('.fds').waitFor();

			await expect(page.getByRole('row', {name: title})).toBeVisible();
		});

		await test.step('Delete shows the CMS confirmation modal', async () => {

			// Open the delete action of the usage entry

			await structuresPage.dataSetFragmentPage.execItemAction({
				action: 'Delete',
				filter: title,
			});

			// The CMS confirmation modal must appear instead of a native
			// confirm dialog

			await expect(
				page.getByText('Are you sure you want to delete this entry?')
			).toBeVisible();

			// Confirm the deletion

			await page
				.locator('.liferay-modal')
				.getByRole('button', {exact: true, name: 'Delete'})
				.click();

			await waitForAlert(page, `${title} was successfully deleted`, {
				type: 'success',
			});
		});
	}
);

test(
	'Permissions of a Content Structure can be modified',
	{tag: '@LPD-89302'},
	async ({apiHelpers, page, structuresPage}) => {
		const objectDefinition =
			(await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
				scope: 'depot',
				status: {code: 0},
			})) as ObjectDefinition;

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const structureName = objectDefinition.name;

		await structuresPage.goto();

		await structuresPage.execItemAction({
			action: 'Permissions',
			filter: structureName,
		});

		const permissionsDialog = page.getByRole('dialog', {
			name: 'Permissions',
		});
		const permissionsFrame = permissionsDialog.frameLocator('iframe');

		const siteMemberDeleteCheckbox = permissionsFrame.getByLabel(
			'Give Delete permission to users with the Site Member role.'
		);
		const siteMemberPermissionsCheckbox = permissionsFrame.getByLabel(
			'Give Permissions permission to users with the Site Member role.'
		);
		const siteMemberUpdateCheckbox = permissionsFrame.getByLabel(
			'Give Update permission to users with the Site Member role.'
		);
		const siteMemberViewCheckbox = permissionsFrame.getByLabel(
			'Give View permission to users with the Site Member role.'
		);

		await siteMemberDeleteCheckbox.check();
		await siteMemberPermissionsCheckbox.check();
		await siteMemberUpdateCheckbox.check();
		await siteMemberViewCheckbox.check();

		await permissionsFrame.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(permissionsFrame);

		await permissionsDialog.getByLabel('Close', {exact: true}).click();

		await expect(permissionsDialog).not.toBeAttached();

		await structuresPage.execItemAction({
			action: 'Permissions',
			filter: structureName,
		});

		await expect(siteMemberDeleteCheckbox).toBeChecked();
		await expect(siteMemberPermissionsCheckbox).toBeChecked();
		await expect(siteMemberUpdateCheckbox).toBeChecked();
		await expect(siteMemberViewCheckbox).toBeChecked();
	}
);

test(
	'Content Structure can be scoped to specific spaces',
	{tag: '@LPD-89302'},
	async ({apiHelpers, page, structureBuilderPage, structuresPage}) => {
		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;
		const structureLabel = `Structure${getRandomInt()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName1,
			settings: {},
			type: 'Space',
		});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName2,
			settings: {},
			type: 'Space',
		});

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			name: structureLabel,
			page: structureBuilderPage,
			spaces: [spaceName1, spaceName2],
		});

		await structuresPage.goto();

		const row = page.getByRole('row', {name: structureLabel});

		await expect(row).toBeVisible();
		await expect(row.getByText('All Spaces')).not.toBeVisible();

		await page.getByRole('link', {name: structureLabel}).click();

		await expect(
			page.locator('.label-secondary', {hasText: spaceName1})
		).toBeVisible();
		await expect(
			page.locator('.label-secondary', {hasText: spaceName2})
		).toBeVisible();
	}
);

test(
	'Content Structures list can be filtered by space with include and exclude',
	{tag: ['@LPD-89342', '@LPD-91933']},
	async ({apiHelpers, page, structureBuilderPage, structuresPage}) => {
		const spaceName1 = `Space ${getRandomString()}`;
		const spaceName2 = `Space ${getRandomString()}`;
		const structureLabel = `Structure${getRandomString()}`;

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName1,
			settings: {},
		});

		await apiHelpers.headlessAssetLibrary.createAssetLibrary({
			name: spaceName2,
			settings: {},
		});

		await structureBuilderPage.createStructureFromData({
			label: structureLabel,
			page: structureBuilderPage,
			spaces: [spaceName1],
		});

		const row = structuresPage.getItem(structureLabel);

		await structuresPage.goto();
		await applyFDSSelectionFilter(page, {
			filter: 'Space',
			value: spaceName1,
		});
		await expect(row).toBeVisible();

		await structuresPage.goto();
		await applyFDSSelectionFilter(page, {
			filter: 'Space',
			value: spaceName2,
		});
		await expect(row).toBeHidden();

		await structuresPage.goto();
		await applyFDSSelectionFilter(page, {
			exclude: true,
			filter: 'Space',
			value: spaceName1,
		});
		await expect(row).toBeHidden();

		await structuresPage.goto();
		await applyFDSSelectionFilter(page, {
			exclude: true,
			filter: 'Space',
			value: spaceName2,
		});
		await expect(row).toBeVisible();
	}
);
