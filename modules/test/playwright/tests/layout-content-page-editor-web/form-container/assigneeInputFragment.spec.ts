/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {displayPageTemplatesPagesTest} from '../../../fixtures/displayPageTemplatesPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {pageEditorPagesTest} from '../../../fixtures/pageEditorPagesTest';
import {clickAndExpectToBeVisible} from '../../../utils/clickAndExpectToBeVisible';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {normalizeRestPath} from '../../../utils/normalizeRestPath';
import getFormContainerDefinition from '../main/utils/getFormContainerDefinition';
import getFragmentDefinition from '../main/utils/getFragmentDefinition';
import getPageDefinition from '../main/utils/getPageDefinition';

const ASSIGNEE_FIELD_LABEL = 'Task Owner';

const test = mergeTests(
	dataApiHelpersTest,
	displayPageTemplatesPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	pageEditorPagesTest
);

test(
	'Can search for a user, select it, and submit the form',
	{tag: '@LPD-102916'},
	async ({apiHelpers, page, site}) => {

		// Create a user and a role sharing the same search term

		const searchTerm = `Assignee${getRandomInt()}`;

		const userAccount = await apiHelpers.headlessAdminUser.postUserAccount({
			alternateName: searchTerm.toLowerCase(),
			emailAddress: `${searchTerm.toLowerCase()}@liferay.com`,
			familyName: 'User',
			givenName: searchTerm,
			password: 'test',
		});

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `${searchTerm} Role`,
		});

		// Create an object with an assignee field

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionExternalReferenceCode: `AssigneeTask${getRandomInt()}`,
				objectFields: [
					{
						businessType: 'Assignee',
						externalReferenceCode: getRandomString(),
						label: {en_US: ASSIGNEE_FIELD_LABEL},
						name: 'assignee',
						required: false,
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a page with a form container mapped to the object

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFormContainerDefinition({
					id: getRandomString(),
					objectDefinitionClassName: objectDefinition.className,
					pageElements: [
						getFragmentDefinition({
							fragmentConfig: {
								inputFieldId: 'ObjectField_assignee',
							},
							id: getRandomString(),
							key: 'INPUTS-assignee-input',
						}),
						getFragmentDefinition({
							id: getRandomString(),
							key: 'INPUTS-submit-button',
						}),
					],
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Search for the assignees and check both the user and the role are listed

		const assigneeInput = page.getByPlaceholder('Search for an Assignee');
		const option = page.getByRole('option');

		await expect(async () => {
			await assigneeInput.fill(searchTerm, {timeout: 1000});

			await expect(option).toHaveCount(2, {timeout: 5000});
		}).toPass();

		await expect(option.filter({hasText: userAccount.name})).toBeVisible();
		await expect(option.filter({hasText: role.name})).toBeVisible();

		// Select the user and submit the form

		await option.filter({hasText: userAccount.name}).click();

		await expect(assigneeInput).toHaveValue(userAccount.name);

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry was created with the selected user

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				normalizeRestPath(objectDefinition.restContextPath)
			);

		expect(items).toHaveLength(1);

		expect(items[0].assignee).toMatchObject({
			externalReferenceCode: userAccount.externalReferenceCode,
			name: userAccount.name,
			type: 'User',
		});
	}
);

test(
	'Can select a role with the keyboard and submit the form',
	{tag: '@LPD-102916'},
	async ({apiHelpers, page, site}) => {

		// Create a role

		const searchTerm = `Assignee${getRandomInt()}`;

		const role = await apiHelpers.headlessAdminUser.postRole({
			name: `${searchTerm} Role`,
		});

		// Create an object with an assignee field

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionExternalReferenceCode: `AssigneeTask${getRandomInt()}`,
				objectFields: [
					{
						businessType: 'Assignee',
						externalReferenceCode: getRandomString(),
						label: {en_US: ASSIGNEE_FIELD_LABEL},
						name: 'assignee',
						required: false,
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create a page with a form container mapped to the object

		const layout = await apiHelpers.headlessDelivery.createSitePage({
			pageDefinition: getPageDefinition([
				getFormContainerDefinition({
					id: getRandomString(),
					objectDefinitionClassName: objectDefinition.className,
					pageElements: [
						getFragmentDefinition({
							fragmentConfig: {
								inputFieldId: 'ObjectField_assignee',
							},
							id: getRandomString(),
							key: 'INPUTS-assignee-input',
						}),
						getFragmentDefinition({
							id: getRandomString(),
							key: 'INPUTS-submit-button',
						}),
					],
				}),
			]),
			siteId: site.id,
			title: getRandomString(),
		});

		await page.goto(`/web${site.friendlyUrlPath}${layout.friendlyUrlPath}`);

		// Open the dropdown menu and check the Guest role is not offered

		const assigneeInput = page.getByPlaceholder('Search for an Assignee');
		const option = page.getByRole('option');

		await clickAndExpectToBeVisible({
			target: option.first(),
			trigger: page.getByLabel('Open Options Menu'),
		});

		await expect(option.filter({hasText: 'Guest'})).not.toBeVisible();

		// Search for the role and select it with the keyboard

		await expect(async () => {
			await assigneeInput.fill(searchTerm, {timeout: 1000});

			await expect(option).toHaveCount(1, {timeout: 5000});
		}).toPass();

		await assigneeInput.press('ArrowDown');

		await expect(option.filter({hasText: role.name})).toHaveAttribute(
			'aria-selected',
			'true'
		);

		await assigneeInput.press('Enter');

		await expect(assigneeInput).toHaveValue(role.name);

		// Submit the form

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the object entry was created with the selected role

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				normalizeRestPath(objectDefinition.restContextPath)
			);

		expect(items).toHaveLength(1);

		expect(items[0].assignee).toMatchObject({
			externalReferenceCode: role.externalReferenceCode,
			name: role.name,
			type: 'Role',
		});
	}
);

test(
	'Loads the assigned user when editing an entry in a display page',
	{tag: '@LPD-102916'},
	async ({
		apiHelpers,
		displayPageTemplatesPage,
		page,
		pageEditorPage,
		site,
	}) => {

		// Create the user the entry is assigned to and the user it is reassigned to

		const searchTerm = `Assignee${getRandomInt()}`;

		const assignedUserAccount =
			await apiHelpers.headlessAdminUser.postUserAccount({
				alternateName: `assigned${searchTerm.toLowerCase()}`,
				emailAddress: `assigned${searchTerm.toLowerCase()}@liferay.com`,
				familyName: 'User',
				givenName: `Assigned${searchTerm}`,
				password: 'test',
			});

		const reassignedUserAccount =
			await apiHelpers.headlessAdminUser.postUserAccount({
				alternateName: `reassigned${searchTerm.toLowerCase()}`,
				emailAddress: `reassigned${searchTerm.toLowerCase()}@liferay.com`,
				familyName: 'User',
				givenName: `Reassigned${searchTerm}`,
				password: 'test',
			});

		// Create an object with an assignee field

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectDefinitionExternalReferenceCode: `AssigneeTask${getRandomInt()}`,
				objectFields: [
					{
						businessType: 'Assignee',
						externalReferenceCode: getRandomString(),
						label: {en_US: ASSIGNEE_FIELD_LABEL},
						name: 'assignee',
						required: false,
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		// Create an entry assigned to the first user

		const restPath = normalizeRestPath(objectDefinition.restContextPath);

		const objectEntry = await apiHelpers.objectEntry.postObjectEntry(
			{
				assignee: {
					externalReferenceCode:
						assignedUserAccount.externalReferenceCode,
					name: assignedUserAccount.name,
					type: 'User',
				},
			},
			restPath
		);

		// Create a display page template for the object

		const displayPageTemplateName = getRandomString();

		const className =
			await apiHelpers.jsonWebServicesClassName.fetchClassName(
				objectDefinition.className
			);

		await apiHelpers.jsonWebServicesLayoutPageTemplateEntry.addDisplayPageLayoutPageTemplateEntry(
			{
				classNameId: className.classNameId,
				groupId: site.id,
				name: displayPageTemplateName,
			}
		);

		// Add a form container mapped to the object and publish the template

		await displayPageTemplatesPage.goto(site.friendlyUrlPath);

		await displayPageTemplatesPage.editTemplate(displayPageTemplateName);

		await pageEditorPage.addFragment('Form Components', 'Form Container');

		await pageEditorPage.mapFormFragment(
			await pageEditorPage.getFragmentId('Form Container'),
			`${objectDefinition.label['en_US']} (Default)`,
			[ASSIGNEE_FIELD_LABEL]
		);

		await displayPageTemplatesPage.publishTemplate();

		// Go to the entry display page and check the assigned user is loaded

		await page.goto(
			`/web${site.friendlyUrlPath}/e/${displayPageTemplateName}/${className.classNameId}/${objectEntry.id}`
		);

		const assigneeInput = page.getByPlaceholder('Search for an Assignee');
		const option = page.getByRole('option');

		await expect(assigneeInput).toHaveValue(assignedUserAccount.name);

		// Reassign the entry and submit

		await expect(async () => {
			await assigneeInput.fill(`Reassigned${searchTerm}`, {
				timeout: 1000,
			});

			await expect(option).toHaveCount(1, {timeout: 5000});
		}).toPass();

		await option.filter({hasText: reassignedUserAccount.name}).click();

		await expect(assigneeInput).toHaveValue(reassignedUserAccount.name);

		await page.getByText('Submit', {exact: true}).click();

		await expect(
			page.getByText(
				'Thank you. Your information was successfully received.'
			)
		).toBeVisible();

		// Check the entry was updated instead of a new one being created

		const {items} =
			await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
				restPath
			);

		expect(items).toHaveLength(1);

		expect(items[0].id).toBe(objectEntry.id);

		expect(items[0].assignee).toMatchObject({
			externalReferenceCode: reassignedUserAccount.externalReferenceCode,
			name: reassignedUserAccount.name,
			type: 'User',
		});
	}
);
