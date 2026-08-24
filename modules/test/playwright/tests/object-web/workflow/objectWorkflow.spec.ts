/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {globalMenuPagesTest} from '../../../fixtures/globalMenuPagesTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {workflowPagesTest} from '../../../fixtures/workflowPagesTest';
import {FormBuilderPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderPage';
import {FormBuilderSidePanelPage} from '../../../pages/dynamic-data-mapping-form-web/FormBuilderSidePanelPage';
import {FormSettingsModalPage} from '../../../pages/dynamic-data-mapping-form-web/FormSettingsModalPage';
import {MetricsPage} from '../../../pages/portal-workflow-metrics-web/MetricsPage';
import {WorkflowTasksPage} from '../../../pages/portal-workflow-task-web/WorkflowTasksPage';
import {waitForAlert} from '../../../utils/waitForAlert';
import {generateObjectFields} from '../utils/generateObjectFields';

const test = mergeTests(
	dataApiHelpersTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	globalMenuPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	workflowPagesTest
);

test('Can preview entry information on My Workflow Tasks', async ({
	apiHelpers,
	configurationTabPage,
	globalMenuPage,
	page,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await globalMenuPage.goToApplications('Process Builder');

	await configurationTabPage.configurationTabLink.click();

	await configurationTabPage.assignWorkflowToAssetType(
		'Single Approver',
		objectDefinition.label['en_US']
	);

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const textFieldName = objectFields[0].name;

	await apiHelpers.objectEntry.postObjectEntry(
		{[textFieldName]: 'Entry Test'},
		applicationName
	);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTaskDetailsPage.selectAsset(objectDefinition.label['en_US']);

	await expect(page.getByText(objectFields[0].label['en_US'])).toHaveValue(
		'Entry Test'
	);
});

test('Can view entry information through View button on My Workflow Tasks', async ({
	apiHelpers,
	configurationTabPage,
	globalMenuPage,
	page,
	workflowTaskDetailsPage,
	workflowTasksPage,
}) => {
	const objectFields = generateObjectFields({
		objectFieldBusinessTypes: ['Text'],
	});

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields,
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: objectDefinition.id,
		type: 'objectDefinition',
	});

	await globalMenuPage.goToApplications('Process Builder');

	await configurationTabPage.configurationTabLink.click();

	await configurationTabPage.assignWorkflowToAssetType(
		'Single Approver',
		objectDefinition.label['en_US']
	);

	const applicationName = 'c/' + objectDefinition.name.toLowerCase() + 's';
	const textFieldName = objectFields[0].name;

	await apiHelpers.objectEntry.postObjectEntry(
		{[textFieldName]: 'Entry Test'},
		applicationName
	);

	await workflowTasksPage.goToAssignedToMyRoles();

	await workflowTaskDetailsPage.selectAsset(objectDefinition.label['en_US']);

	await workflowTaskDetailsPage.viewButton.click();

	await expect(page.getByText(objectFields[0].label['en_US'])).toHaveValue(
		'Entry Test'
	);
});

test(
	'Verify that the Object is not displayed on Process Builder settings before Published',
	{tag: '@LPS-135649'},
	async ({apiHelpers, configurationTabPage, globalMenuPage, page}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();
	}
);

test(
	'Verify that the Object is not displayed on Workflow settings from Site Menu before Published',
	{tag: '@LPS-135649'},
	async ({apiHelpers, page, site, workflowPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 2},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await workflowPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();
	}
);

test(
	'Verify that the Object is displayed on the Workflow Process Builder page',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		configurationTabPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const label = objectDefinition.label['en_US'];

		await configurationTabPage.searchAssetType(label);

		await expect(page.getByRole('row', {name: label})).toBeVisible();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(label);

		await configurationTabPage.searchAssetType(label);

		await expect(page.getByRole('row', {name: label})).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(label);

		await configurationTabPage.searchAssetType(label);

		await expect(page.getByRole('row', {name: label})).toBeVisible();
	}
);

test(
	'Verify that the Object disappears from Workflow Site Menu when inactivated and reappears when reactivated',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		page,
		site,
		viewObjectDefinitionsPage,
		workflowPage,
	}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				panelCategoryKey: 'site_administration.content',
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		await workflowPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('row', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		await workflowPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('row', {
				name: objectDefinition.label['en_US'],
			})
		).toBeVisible();
	}
);

test(
	'Verify that pending and completed Object entries disappear from Workflow Metrics and Workflow pages when they are deleted',
	{tag: '@LPS-135649'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry Test'},
			applicationName
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.frontendDatasetActions.first().click();
		await viewObjectEntriesPage.frontendDatasetDeleteAction.click();

		await page.getByRole('button', {name: 'Delete'}).click();

		await waitForAlert(page);

		const metricsPage = new MetricsPage(page);

		await metricsPage.goTo();

		await metricsPage.chooseProcess('Single Approver');

		await expect(page.getByText('0', {exact: true}).first()).toBeVisible();

		const workflowTasksPage = new WorkflowTasksPage(page);

		await workflowTasksPage.goToAssignedToMyRoles();

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();
	}
);

test(
	'Verify that Object entries with workflow disappear from workflow pages when inactivated and reappear when reactivated',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
				titleObjectFieldName: objectFields[0].name,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry A'},
			applicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry B'},
			applicationName
		);

		const workflowTasksPage = new WorkflowTasksPage(page);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.assignToMe('Entry A');

		await workflowTasksPage.approve('Entry A');

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		await workflowTasksPage.goto();

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();

		await workflowTasksPage.assignedToMyRolesLink.click();

		await expect(
			page.getByRole('heading', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		await workflowTasksPage.goto();

		await expect(
			page.getByText(objectDefinition.label['en_US']).first()
		).toBeVisible();

		await workflowTasksPage.assignedToMyRolesLink.click();

		await expect(
			page.getByText(objectDefinition.label['en_US']).first()
		).toBeVisible();
	}
);

test(
	'Verify that the Object entries with workflow are displayed again on the Workflow Metrics page when reactivated',
	{tag: '@LPS-139005'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectDefinitionsPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
				titleObjectFieldName: objectFields[0].name,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry A'},
			applicationName
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry B'},
			applicationName
		);

		const workflowTasksPage = new WorkflowTasksPage(page);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.assignToMe('Entry A');

		await workflowTasksPage.approve('Entry A');

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		await viewObjectDefinitionsPage.goto();

		await viewObjectDefinitionsPage.changeObjectActivateStatus(
			objectDefinition.label['en_US']
		);

		const metricsPage = new MetricsPage(page);

		await metricsPage.goTo();

		await metricsPage.chooseProcess('Single Approver');

		await expect(page.getByText('1', {exact: true}).first()).toBeVisible();
	}
);

test(
	'Verify that a completed entry is displayed with an Approved status',
	{tag: '@LPS-135649'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
				titleObjectFieldName: objectFields[0].name,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{[objectFields[0].name!]: 'Entry A'},
			'c/' + objectDefinition.name!.toLowerCase() + 's'
		);

		const workflowTasksPage = new WorkflowTasksPage(page);

		await workflowTasksPage.goToAssignedToMyRoles();

		await workflowTasksPage.assignToMe('Entry A');

		await workflowTasksPage.approve('Entry A');

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Entry A'})
				.getByText('Approved')
		).toBeVisible();
	}
);

test(
	'Verify that a withdrawn pending entry is displayed with a Draft status',
	{tag: '@LPS-135649'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		personalMenuPage,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';
		const fieldName = objectFields[0].name!;

		await apiHelpers.objectEntry.postObjectEntry(
			{[fieldName!]: 'Entry Test'},
			applicationName
		);

		await personalMenuPage.userPersonalMenuButton.click();

		await personalMenuPage.menuItem('My Submissions').click();

		await page.waitForTimeout(1000);

		const entryRow = page
			.getByRole('row')
			.filter({hasText: objectDefinition.label['en_US']});

		await entryRow.locator('.dropdown-toggle').click();

		await page
			.locator('.dropdown-menu:visible')
			.getByText('Withdraw Submission', {exact: true})
			.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Entry Test'})
				.getByText('Draft')
		).toBeVisible();
	}
);

test(
	'Verify that the workflow is triggered when submitting an entry through Forms',
	{tag: '@LPS-135649'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectEntriesPage,
	}) => {
		await page.setViewportSize({height: 1080, width: 1920});

		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		const formBuilderPage = new FormBuilderPage(page);
		const formSettingsModalPage = new FormSettingsModalPage(page);
		const formBuilderSidePanelPage = new FormBuilderSidePanelPage(page);

		await formBuilderPage.goToNew();

		await formBuilderPage.fillFormTitle('Form Object');

		await formBuilderPage.formSettingsButton.click();

		await formSettingsModalPage.selectStorageType('Object');

		await formSettingsModalPage.selectObject(
			objectDefinition.label['en_US']
		);

		await formSettingsModalPage.clickDoneButton();

		await formBuilderSidePanelPage.addFieldByDoubleClick('Text');

		await formBuilderSidePanelPage.clickAdvancedTab();

		await formBuilderSidePanelPage.selectObjectField(objectFields[0].name!);

		await page.waitForTimeout(1000);

		await formBuilderPage.clickSaveButton();

		await waitForAlert(page);

		await formBuilderPage.publishButton.click();

		await waitForAlert(page, undefined, {autoClose: false});

		await page.waitForLoadState('networkidle');

		await formBuilderPage.shareButton.waitFor({state: 'visible'});

		const formSubmissionURL = await formBuilderPage.getFormSubmissionURL();

		await page.goto(formSubmissionURL);

		await page.getByLabel('Text', {exact: true}).fill('Entry Test');

		await page.getByRole('button', {name: 'Submit'}).click();

		await expect(
			page.getByText('Your information was successfully received')
		).toBeVisible();

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Entry Test'})
				.getByText('Pending')
		).toBeVisible();
	}
);

test(
	'Verify that the workflow is triggered when submitting an entry through Custom Object portlet',
	{tag: '@LPS-135649'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		viewObjectEntriesPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page
			.getByLabel(objectFields[0].label!['en_US'])
			.fill('Entry Test');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(objectDefinition.className);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Entry Test'})
				.getByText('Pending')
		).toBeVisible();
	}
);

test(
	'Verify that the workflow is triggered when submitting an entry when Object is scoped by Site and the workflow was assigned on the Workflow settings from the Site Menu',
	{tag: '@LPS-135649'},
	async ({apiHelpers, page, site, viewObjectEntriesPage, workflowPage}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				panelCategoryKey: 'site_administration.content',
				scope: 'site',
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await workflowPage.goto(site.friendlyUrlPath);

		const assetTypeRow = page
			.getByRole('row')
			.filter({hasText: objectDefinition.label['en_US']});

		await assetTypeRow.getByRole('button', {name: 'Edit'}).click();

		await assetTypeRow
			.getByRole('combobox')
			.selectOption({label: 'Single Approver'});

		await assetTypeRow.getByRole('button', {name: 'Save'}).click();

		await waitForAlert(
			page,
			`Success:Workflow assigned to ${objectDefinition.label['en_US']}.`
		);

		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			'en',
			site.friendlyUrlPath
		);

		await viewObjectEntriesPage.clickAddObjectEntry(
			objectDefinition.label['en_US']
		);

		await page
			.getByLabel(objectFields[0].label!['en_US'])
			.fill('Entry Test');

		await viewObjectEntriesPage.saveObjectEntryButton.click();

		await waitForAlert(page);

		await viewObjectEntriesPage.goto(
			objectDefinition.className,
			'en',
			site.friendlyUrlPath
		);

		await expect(
			page
				.getByRole('row')
				.filter({hasText: 'Entry Test'})
				.getByText('Pending')
		).toBeVisible();
	}
);

test(
	'Verify that when Objects are not scoped by Site it should not be displayed on the Workflow settings from the Site Menu',
	{tag: '@LPS-135649'},
	async ({apiHelpers, page, site, workflowPage}) => {
		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await workflowPage.goto(site.friendlyUrlPath);

		await expect(
			page.getByRole('row', {
				name: objectDefinition.label['en_US'],
			})
		).toBeHidden();
	}
);

test(
	'Verify the Object Entry Title is displayed for Object entries on workflow pages',
	{tag: '@LPS-139803'},
	async ({
		apiHelpers,
		configurationTabPage,
		globalMenuPage,
		page,
		personalMenuPage,
	}) => {
		const objectFields = generateObjectFields({
			objectFieldBusinessTypes: ['Text'],
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields,
				status: {code: 0},
				titleObjectFieldName: objectFields[0].name,
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await globalMenuPage.goToApplications('Process Builder');

		await configurationTabPage.configurationTabLink.click();

		await configurationTabPage.assignWorkflowToAssetType(
			'Single Approver',
			objectDefinition.label['en_US']
		);

		await apiHelpers.objectEntry.postObjectEntry(
			{[objectFields[0].name!]: 'WorkflowEntryTitle'},
			'c/' + objectDefinition.name!.toLowerCase() + 's'
		);

		const workflowTasksPage = new WorkflowTasksPage(page);

		await workflowTasksPage.goToAssignedToMyRoles();

		await expect(
			page.getByText('WorkflowEntryTitle').first()
		).toBeVisible();

		await personalMenuPage.userPersonalMenuButton.click();

		await personalMenuPage.menuItem('My Submissions').click();

		await page.waitForTimeout(1000);

		await expect(
			page.getByText('WorkflowEntryTitle').first()
		).toBeVisible();

		await globalMenuPage.goToApplications('Submissions');

		await expect(
			page.getByText('WorkflowEntryTitle').first()
		).toBeVisible();
	}
);

test.skip('Workflow is not triggered for draft entry', async () => {

	// This test requires:
	// 1. A site-scoped custom object with "Allow Users to Save Entries as Draft" enabled
	// 2. A content page with a Form Container mapped to the custom object
	//    and the Form Button configured with "Submitted Entry Status" set to "Draft"
	// 3. Single Approver workflow assigned to the custom object
	// 4. Submitting an entry through the form on the content page
	// 5. Verifying the entry status is "Draft" (workflow not triggered)
	//
	// Cannot be implemented because it requires content page Form Container
	// mapping to custom objects, which depends on page builder fragment
	// configuration infrastructure not available in the current test fixtures.

});
