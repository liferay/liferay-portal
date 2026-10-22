/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinitionAPI} from '@liferay/object-admin-rest-client-js/v1_0';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {pageManagementSiteTest} from '../../../fixtures/pageManagementSiteTest';
import {checkAccessibility} from '../../../utils/checkAccessibility';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import dragAndDropElement from '../../../utils/dragAndDropElement';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';
import {getObjectERC} from '../../setup/page-management-site/main/utils/getObjectERC';
import getContainerDefinition from './utils/getContainerDefinition';
import getFormContainerDefinition from './utils/getFormContainerDefinition';
import getFragmentDefinition from './utils/getFragmentDefinition';
import getPageDefinition from './utils/getPageDefinition';

const test = mergeTests(
	apiHelpersTest,
	featureFlagsTest({
		'LPD-11235': {enabled: false},
		'LPS-169837': {enabled: true},
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest,
	pageManagementSiteTest
);

test(
	'Add, edit and delete page rule',
	{
		tag: ['@LPS-196461', '@LPS-196462', '@LPS-200349', '@LPD-9500'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create content page with a button fragment and go to edit mode

		const buttonId = getRandomString();

		const buttonDefinition = getFragmentDefinition({
			id: buttonId,
			key: 'BASIC_COMPONENT-button',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([buttonDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Assert info message

		await pageEditorPage.goToSidebarTab('Page Rules');

		await expect(
			page.getByText('Fortunately, it is very easy to add new ones.')
		).toBeVisible();

		// Open new rule modal

		const modal = await pageEditorPage.openRulesModal();

		// Create new rule

		const ruleName = getRandomString();

		await modal.getByLabel('Rule Name').fill(ruleName);

		// Check empty rules are not allowed

		await modal.getByRole('button', {exact: true, name: 'Save'}).click();

		await expect(
			modal.getByText('Please review the following fields before saving.')
		).toBeVisible();

		// Start adding a condition

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'User'}),
			trigger: page.getByLabel('Select Item for the Condition'),
		});

		// Check we can delete the condition

		await page.getByLabel('Delete Condition').click();

		await expect(
			page.getByLabel('Select Item for the Condition')
		).not.toHaveText('User');

		// Continue adding the condition

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'User'}),
			trigger: page.getByLabel('Select Item for the Condition'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Is the User'}),
			trigger: page.getByLabel('Select Condition'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'test'}),
			trigger: page.getByLabel('Select User'),
		});

		// Action

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Hide'}),
			trigger: page.getByLabel('Select Action'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Button'}),
			trigger: page.getByLabel('Select Fragment'),
		});

		await modal.getByRole('button', {exact: true, name: 'Save'}).click();

		await waitForAlert(page, 'Success:The rule was created successfully.');

		// Assert rule is created

		await expect(
			page.getByText('IfUserIs the UsertestHideButton')
		).toBeVisible();

		// Rename rule inline, put empty name and check it's not saved

		await expect(async () => {
			const name = page.locator('.page-editor__rule').getByText(ruleName);

			const input = page.locator('.page-editor__rule input');

			await name.dblclick({timeout: 1000});

			await expect(input).toBeVisible({timeout: 1000});

			await input.fill('', {timeout: 1000});

			await input.press('Enter', {timeout: 1000});

			await page
				.locator('.alert-info', {
					hasText: 'Rule name cannot be empty.',
				})
				.first()
				.waitFor({timeout: 4000});
		}).toPass();

		await expect(
			page.getByText('Rule name cannot be empty').first()
		).toBeVisible();

		// Rename rule properly

		const nextRuleName = getRandomString();

		await pageEditorPage.renameRuleInline({
			currentName: ruleName,
			newName: nextRuleName,
		});

		// Edit rule

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.getByLabel(`View ${nextRuleName} Options`),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Has the Role Of'}),
			trigger: page.getByLabel('Select Condition'),
		});

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {exact: true, name: 'Guest'}),
			trigger: page.getByLabel('Select Role'),
		});

		await modal.getByRole('button', {exact: true, name: 'Save'}).click();

		await waitForAlert(page, 'Success:The rule was updated successfully.');

		// Assert rule was updated

		await expect(
			page.getByText('IfUserHas the Role OfGuestHideButton')
		).toBeVisible();

		// Delete rule

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Delete'}),
			trigger: page.getByLabel(`View ${nextRuleName} Options`),
		});

		// Assert rule was deleted

		await expect(
			page.getByText('Fortunately, it is very easy to add new ones.')
		).toBeVisible();
	}
);

test(
	'Apply a page rule with Has the Role Of condition',
	{
		tag: ['@LPS-200332'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create content page with a heading and a button fragment and go to edit mode

		const buttonDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-button',
		});

		const headingDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				buttonDefinition,
				headingDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new rule to hide button for Guest Users

		const condition = [
			{label: 'Select Item for the Condition', option: 'User'},
			{label: 'Select Condition', option: 'Has the Role Of'},
			{label: 'Select Role', option: 'User'},
		];

		const action = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Button'},
		];

		await pageEditorPage.addRule({
			actions: [action],
			conditions: [condition],
			name: getRandomString(),
		});

		// Publish the page

		await pageEditorPage.publishPage();

		// Assert rule works

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await expect(page.getByText('Go Somewhere')).not.toBeVisible();
	}
);

test(
	'Apply a page rule with Form input condition',
	{
		tag: '@LPD-44720',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('All Fields')
			)
		).body;

		const checkboxId = getRandomString();

		const checkboxDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_boolean',
			},
			id: checkboxId,
			key: 'INPUTS-checkbox',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const headingFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [checkboxDefinition, submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				formDefinition,
				headingFragmentDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Create new rule with a condition when the checkbox is checked, and actions
		// to disable the submit button and hide the header

		const firstAction = [
			{label: 'Select Action', option: 'Disable'},
			{
				label: 'Select Fragment for the Action',
				option: 'Form Button',
			},
		];

		const secondAction = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Heading'},
		];

		const condition = [
			{
				label: 'Select Item for the Condition',
				option: 'Form Fragment',
			},
			{
				label: 'Select Fragment for the Condition',
				option: 'Checkbox',
			},
			{label: 'Select Type', option: 'Is Equal To'},
			{label: 'Select Value', option: 'True'},
		];

		await pageEditorPage.addRule({
			actions: [firstAction, secondAction],
			conditions: [condition],
			name: getRandomString(),
		});

		// Publish the page

		await pageEditorPage.publishPage();

		// Assert rule works

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Wait for rules to be loaded

		await page.waitForTimeout(1000);

		// Check the checkbox and assert the submit button is disabled and the heading is hidden

		await page.getByLabel('Boolean (Read Only)', {exact: true}).check();

		await expect(page.getByRole('button', {name: 'Submit'})).toBeDisabled();

		await expect(
			page.getByText('Heading Example', {exact: true})
		).not.toBeVisible();

		// Uncheck the checkbox and assert the submit button is enabled and the heading is visible

		await page.getByLabel('Boolean (Read Only)', {exact: true}).uncheck();

		await expect(page.getByRole('button', {name: 'Submit'})).toBeEnabled();

		await expect(
			page.getByText('Heading Example', {exact: true})
		).toBeVisible();
	}
);

test('Checks the accessibility of the rule modal by filling out a condition and an action', async ({
	apiHelpers,
	page,
	pageEditorPage,
	site,
}) => {

	// Create content page with a Heading fragment and go to edit mode

	const layout = await apiHelpers.headlessDelivery.createSitePage({
		pageDefinition: getPageDefinition([
			getFragmentDefinition({
				id: getRandomString(),
				key: 'BASIC_COMPONENT-heading',
			}),
		]),
		siteId: site.id,
		title: getRandomString(),
	});

	await pageEditorPage.goto(layout, site.friendlyUrlPath);

	// Add rule and check accessibility of modal

	await pageEditorPage.goToSidebarTab('Page Rules');

	await pageEditorPage.openRulesModal();

	await pageEditorPage.addRandomRuleCondition();

	await pageEditorPage.addRandomRuleAction();

	await checkAccessibility({
		page,
		selectors: ['.modal-body'],
	});
});

test(
	'Highlight and scroll to the fragments when the rule is clicked',
	{
		tag: '@LPD-70720',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('All Fields')
			)
		).body;

		const checkboxId = getRandomString();

		const checkboxDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_boolean',
			},
			id: checkboxId,
			key: 'INPUTS-checkbox',
		});

		const submitButtonId = getRandomString();

		const submitFragmentDefinition = getFragmentDefinition({
			id: submitButtonId,
			key: 'INPUTS-submit-button',
		});

		const headingFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [checkboxDefinition, submitFragmentDefinition],
		});

		// Give height to the container so that we have some scroll

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getContainerDefinition({
					fragmentStyle: {minHeight: '2000px'},
					id: getRandomString(),
				}),
				formDefinition,
				headingFragmentDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Add a rule with a condition when the checkbox is checked and an action to hide the heading

		const ruleName = getRandomString();

		const action = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Heading'},
		];

		const condition = [
			{
				label: 'Select Item for the Condition',
				option: 'Form Fragment',
			},
			{
				label: 'Select Fragment for the Condition',
				option: 'Checkbox',
			},
			{label: 'Select Type', option: 'Is Equal To'},
			{label: 'Select Value', option: 'True'},
		];

		await pageEditorPage.addRule({
			actions: [action],
			conditions: [condition],
			name: ruleName,
		});

		// Click the rule and check the highlighted fragments

		const rule = page.getByRole('menuitem').filter({hasText: ruleName});

		const buttonFragment = page.locator('[data-name="Form Button"]');
		const checkboxFragment = page.locator('[data-name="Checkbox"]');
		const headingFragment = page.locator('[data-name="Heading"]');

		const highlightedClass = /highlighted-from-rule/;

		await expect(checkboxFragment).not.toBeInViewport();

		await rule.click();

		await expect(checkboxFragment).toBeInViewport();

		await expect(checkboxFragment).toHaveClass(highlightedClass);
		await expect(headingFragment).toHaveClass(highlightedClass);
		await expect(buttonFragment).not.toHaveClass(highlightedClass);

		// Click in other part of the page and check that the fragments are not highlighted

		await page.locator('body').click();

		await expect(checkboxFragment).not.toHaveClass(highlightedClass);
		await expect(headingFragment).not.toHaveClass(highlightedClass);
		await expect(buttonFragment).not.toHaveClass(highlightedClass);

		// Press the rule by keyboard and check the highlighted fragments

		await rule.focus();
		await page.keyboard.press('Enter');

		await expect(checkboxFragment).toHaveClass(highlightedClass);
		await expect(headingFragment).toHaveClass(highlightedClass);
		await expect(buttonFragment).not.toHaveClass(highlightedClass);

		await page.keyboard.press('Tab');
		await page.keyboard.press('Tab');
		await page.keyboard.press('Enter');

		// Unhighlight the fragments when another fragment is selected

		await pageEditorPage.selectFragment(submitButtonId);

		await expect(checkboxFragment).not.toHaveClass(highlightedClass);
		await expect(headingFragment).not.toHaveClass(highlightedClass);
	}
);

test(
	'Delete fragments from the conditions/actions of a rule and check the options in the Delete Referenced Fragments modal',
	{
		tag: '@LPD-71462',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page with a form and a heading

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('All Fields')
			)
		).body;

		const checkboxId = getRandomString();

		const checkboxDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_boolean',
			},
			id: checkboxId,
			key: 'INPUTS-checkbox',
		});

		const headingId = getRandomString();

		const headingFragmentDefinition = getFragmentDefinition({
			id: headingId,
			key: 'BASIC_COMPONENT-heading',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [checkboxDefinition, submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				formDefinition,
				headingFragmentDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Add a rule with a condition when the checkbox is checked and an action to hide the heading

		const ruleName = getRandomString();

		const action = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Heading'},
		];

		const condition = [
			{
				label: 'Select Item for the Condition',
				option: 'Form Fragment',
			},
			{
				label: 'Select Fragment for the Condition',
				option: 'Checkbox',
			},
			{label: 'Select Type', option: 'Is Equal To'},
			{label: 'Select Value', option: 'True'},
		];

		await pageEditorPage.addRule({
			actions: [action],
			conditions: [condition],
			name: ruleName,
		});

		// Try to delete the checkbox and cancel the action

		await pageEditorPage.deleteFragment(checkboxId);

		await page.getByText('Cancel', {exact: true}).click();

		await expect(page.locator('[data-name="Heading"]')).toBeAttached();

		// Delete the heading (fragment in the action)

		await pageEditorPage.deleteFragment(headingId);

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(page.locator('[data-name="Heading"]')).not.toBeAttached();

		// Check that rule has been disabled

		const rule = page.getByRole('menuitem').filter({hasText: ruleName});

		await expect(
			rule.locator('[data-title="Disabled Rule"]')
		).toBeAttached();

		await expect(rule.locator('.text-muted')).toHaveAttribute(
			'aria-disabled',
			'true'
		);

		// Delete the checkbox (fragment in the condition) and check the labels of the rule

		await pageEditorPage.deleteFragment(checkboxId);

		await page.getByRole('button', {name: 'Delete'}).click();

		await expect(rule.getByText('Heading')).not.toBeAttached();
		await expect(rule.getByText('Checkbox')).not.toBeAttached();

		// Edit the rule and check that the deleted fragments are not in the selectors

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('menuitem', {name: 'Edit'}),
			trigger: page.getByLabel(`View ${ruleName} Options`),
		});

		const actionFragmentSelector = page.getByLabel(
			'Select Fragment for the Action'
		);

		const conditionFragmentSelect = page.locator(
			'input[value="No Options Available"]'
		);

		await expect(conditionFragmentSelect).toBeAttached();
		await expect(actionFragmentSelector).toHaveText('Select');

		await actionFragmentSelector.click();

		await expect(
			page.getByRole('option', {name: 'Heading'})
		).not.toBeAttached();

		await expect(
			page.getByRole('option', {name: 'Checkbox'})
		).not.toBeAttached();

		// Check the errors when trying to save the rule

		await page.getByText('Save', {exact: true}).click();

		await expect(conditionFragmentSelect.locator('..')).toHaveText(
			/Select Fragment for the Condition/
		);
		await expect(actionFragmentSelector.locator('..')).toHaveText(
			/Select Fragment for the Action/
		);

		// Change the action to Hide and check the form fragments

		await clickAndExpectToBeVisible({
			autoClick: true,
			target: page.getByRole('option', {name: 'Disable'}),
			trigger: page.getByLabel('Select Action'),
		});

		await actionFragmentSelector.click();

		await expect(
			page.getByRole('option', {name: 'Checkbox'})
		).not.toBeAttached();

		// Refresh the page and check that the rule is still disabled

		await page.reload();

		await pageEditorPage.goToSidebarTab('Page Rules');

		await expect(
			rule.locator('[data-title="Disabled Rule"]')
		).toBeAttached();
	}
);

test(
	'Reorder rules',
	{
		tag: ['@LPD-73194', '@LPD-74326'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create content page with a button fragment and go to edit mode

		const buttonDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-button',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([buttonDefinition]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create three rules

		for (const name of ['Banana Rule', 'Orange Rule', 'Strawberry Rule']) {
			const condition = [
				{label: 'Select Item for the Condition', option: 'User'},
				{label: 'Select Condition', option: 'Has the Role Of'},
				{label: 'Select Role', option: 'User'},
			];

			const action = [
				{label: 'Select Action', option: 'Hide'},
				{label: 'Select Fragment for the Action', option: 'Button'},
			];

			await pageEditorPage.addRule({
				actions: [action],
				conditions: [condition],
				name,
			});
		}

		// Reorder rules by mouse
		// Check that moving the same element does not reorder it

		const rule = page.locator('.page-editor__rule');

		await dragAndDropElement({
			dragTarget: page.getByRole('button', {name: 'Move Banana Rule'}),
			dropTarget: page.getByRole('menuitem', {name: /Banana Rule/}),
			offset: {y: 20},
		});

		await expect(rule.nth(0)).toContainText('Banana Rule');
		await expect(rule.nth(1)).toContainText('Orange Rule');
		await expect(rule.nth(2)).toContainText('Strawberry Rule');

		await dragAndDropElement({
			dragTarget: page.getByRole('button', {name: 'Move Banana Rule'}),
			dropTarget: page.getByRole('menuitem', {name: /Banana Rule/}),
			offset: {y: 120},
		});

		await expect(rule.nth(0)).toContainText('Banana Rule');
		await expect(rule.nth(1)).toContainText('Orange Rule');
		await expect(rule.nth(2)).toContainText('Strawberry Rule');

		// Check that it is reordered to a top position

		await dragAndDropElement({
			dragTarget: page.getByRole('button', {name: 'Move Banana Rule'}),
			dropTarget: page.getByRole('menuitem', {name: /Strawberry Rule/}),
			offset: {y: 20},
		});

		await expect(rule.nth(0)).toContainText('Orange Rule');
		await expect(rule.nth(1)).toContainText('Banana Rule');
		await expect(rule.nth(2)).toContainText('Strawberry Rule');

		// Check that it is reordered to a bottom position

		await dragAndDropElement({
			dragTarget: page.getByRole('button', {name: 'Move Orange Rule'}),
			dropTarget: page.getByRole('menuitem', {name: /Strawberry Rule/}),
			offset: {y: 120},
		});

		await expect(rule.nth(0)).toContainText('Banana Rule');
		await expect(rule.nth(1)).toContainText('Strawberry Rule');
		await expect(rule.nth(2)).toContainText('Orange Rule');

		// Reorder rules by keyboard
		// Check that moving the same element does not reorder it

		await page.getByTitle('Move Banana Rule').press('Enter');

		await expect(
			page.getByText(
				'Use Arrows to move it and press Enter to select the new position. Press Esc to cancel.'
			)
		).toBeAttached();

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('Banana Rule moved to the bottom of Banana Rule.')
		).not.toBeAttached();

		await expect(rule.nth(0)).toContainText('Banana Rule');
		await expect(rule.nth(1)).toContainText('Strawberry Rule');
		await expect(rule.nth(2)).toContainText('Orange Rule');

		// Check that it is reordered to a bottom position

		await page.keyboard.press('Enter');
		await page.keyboard.press('ArrowDown');

		await expect(
			page.getByText('Move Banana Rule at the bottom of Strawberry Rule.')
		).toBeAttached();

		await page.keyboard.press('ArrowDown');

		await expect(
			page.getByText('Move Banana Rule at the bottom of Orange Rule.')
		).toBeAttached();

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('Banana Rule moved to the bottom of Orange Rule.')
		).toBeAttached();

		await expect(rule.nth(0)).toContainText('Strawberry Rule');
		await expect(rule.nth(1)).toContainText('Orange Rule');
		await expect(rule.nth(2)).toContainText('Banana Rule');

		// Check that it is reordered to a top position

		await page.keyboard.press('Enter');
		await page.keyboard.press('ArrowUp');

		await expect(
			page.getByText('Move Banana Rule at the top of Orange Rule.')
		).toBeAttached();

		await page.keyboard.press('Enter');

		await expect(
			page.getByText('Banana Rule moved to the top of Orange Rule.')
		).toBeAttached();

		await expect(rule.nth(0)).toContainText('Strawberry Rule');
		await expect(rule.nth(1)).toContainText('Banana Rule');
		await expect(rule.nth(2)).toContainText('Orange Rule');
	}
);

test(
	'Apply an advanced page rule checking the role of the user',
	{
		tag: ['@LPD-23884'],
	},
	async ({apiHelpers, page, pageEditorPage, site}) => {

		// Create content page with a heading and a button fragment and go to edit mode

		const buttonDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-button',
		});

		const headingDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				buttonDefinition,
				headingDefinition,
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, site.friendlyUrlPath);

		// Create new rule to hide button for Guest Users

		const condition = [
			{label: 'Select Item for the Condition', option: 'User'},
			{label: 'Select Condition', option: 'Has the Role Of'},
			{label: 'Select Role', option: 'User'},
		];

		const action = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Button'},
		];

		await pageEditorPage.addRule({
			actions: [action],
			conditions: [condition],
			name: getRandomString(),
			saveRule: false,
		});

		// Convert the rule to advanced

		const rulesModal = page.locator('.modal-dialog');

		await rulesModal.getByRole('button', {name: 'Advanced'}).click();

		await expect(page.getByText('contains(roleIds,')).toBeVisible();

		// Save the rule

		await rulesModal
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(page, 'Success:The rule was created successfully.');

		// Publish the page

		await pageEditorPage.publishPage();

		// Assert rule works

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		await expect(page.getByText('Heading Example')).toBeVisible();

		await expect(page.getByText('Go Somewhere')).not.toBeVisible();
	}
);

test(
	'Apply an advanced page rule checking a form input',
	{
		tag: '@LPD-23884',
	},
	async ({apiHelpers, page, pageEditorPage, pageManagementSite}) => {

		// Create a page

		const objectDefinitionAPIClient =
			await apiHelpers.buildRestClient(ObjectDefinitionAPI);

		const {className: objectDefinitionClassName} = (
			await objectDefinitionAPIClient.getObjectDefinitionByExternalReferenceCode(
				getObjectERC('All Fields')
			)
		).body;

		const checkboxId = getRandomString();

		const checkboxDefinition = getFragmentDefinition({
			fragmentConfig: {
				inputFieldId: 'ObjectField_boolean',
			},
			id: checkboxId,
			key: 'INPUTS-checkbox',
		});

		const submitFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'INPUTS-submit-button',
		});

		const headingFragmentDefinition = getFragmentDefinition({
			id: getRandomString(),
			key: 'BASIC_COMPONENT-heading',
		});

		const formDefinition = getFormContainerDefinition({
			id: getRandomString(),
			objectDefinitionClassName,
			pageElements: [checkboxDefinition, submitFragmentDefinition],
		});

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				formDefinition,
				headingFragmentDefinition,
			]),
			siteId: pageManagementSite.id,
			title: getRandomString(),
		});

		await pageEditorPage.goto(layout, pageManagementSite.friendlyUrlPath);

		// Create new rule with a condition when the checkbox is checked, and actions
		// to disable the submit button and hide the header

		const firstAction = [
			{label: 'Select Action', option: 'Disable'},
			{
				label: 'Select Fragment for the Action',
				option: 'Form Button',
			},
		];

		const secondAction = [
			{label: 'Select Action', option: 'Hide'},
			{label: 'Select Fragment for the Action', option: 'Heading'},
		];

		const condition = [
			{
				label: 'Select Item for the Condition',
				option: 'Form Fragment',
			},
			{
				label: 'Select Fragment for the Condition',
				option: 'Checkbox',
			},
			{label: 'Select Type', option: 'Is Equal To'},
			{label: 'Select Value', option: 'True'},
		];

		await pageEditorPage.addRule({
			actions: [firstAction, secondAction],
			conditions: [condition],
			name: getRandomString(),
			saveRule: false,
		});

		// Convert the rule to advanced

		const rulesModal = page.locator('.modal-dialog');

		await rulesModal.getByRole('button', {name: 'Advanced'}).click();

		await expect(page.getByText('input__')).toBeVisible();

		// Save the rule

		await rulesModal
			.getByRole('button', {exact: true, name: 'Save'})
			.click();

		await waitForAlert(page, 'Success:The rule was created successfully.');

		// Publish the page

		await pageEditorPage.publishPage();

		// Assert rule works

		await page.goto(
			`/web${pageManagementSite.friendlyUrlPath}${layout.friendlyUrlPath}`
		);

		// Wait for rules to be loaded

		await page.waitForTimeout(1000);

		// Check the checkbox and assert the submit button is disabled and the heading is hidden

		await page.getByLabel('Boolean (Read Only)', {exact: true}).check();

		await expect(page.getByRole('button', {name: 'Submit'})).toBeDisabled();

		await expect(
			page.getByText('Heading Example', {exact: true})
		).not.toBeVisible();

		// Uncheck the checkbox and assert the submit button is enabled and the heading is visible

		await page.getByLabel('Boolean (Read Only)', {exact: true}).uncheck();

		await expect(page.getByRole('button', {name: 'Submit'})).toBeEnabled();

		await expect(
			page.getByText('Heading Example', {exact: true})
		).toBeVisible();
	}
);
