/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../fixtures/notificationPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';

const notificationTemplateInfo = {
	description: 'This is a description',
	subject: 'Subject',
	term: '[%CURRENT_USER_FIRST_NAME%]',
};

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPD-50091': {enabled: true},
	}),
	loginTest(),
	notificationPagesTest,
	objectPagesTest,
	usersAndOrganizationsPagesTest
);

test.describe('User notification template', () => {
	test('can be created and saved correctly', async ({
		page,
		userNotificationTemplatePage,
	}) => {
		await userNotificationTemplatePage.goto();

		const notificationTemplateName =
			'Notification Template Name' + getRandomInt();

		await userNotificationTemplatePage.basicInfoName.fill(
			notificationTemplateName
		);

		await userNotificationTemplatePage.descriptionInput.fill(
			notificationTemplateInfo.description
		);

		await userNotificationTemplatePage.toInput.fill(
			notificationTemplateInfo.term
		);

		await userNotificationTemplatePage.contentSubject.fill(
			notificationTemplateInfo.subject
		);

		await userNotificationTemplatePage.saveButton.click();

		await page.getByText(notificationTemplateName).click();

		await expect(userNotificationTemplatePage.basicInfoName).toHaveValue(
			notificationTemplateName
		);

		await expect(userNotificationTemplatePage.descriptionInput).toHaveValue(
			notificationTemplateInfo.description
		);

		await expect(userNotificationTemplatePage.toInput).toHaveValue(
			notificationTemplateInfo.term
		);

		await expect(userNotificationTemplatePage.contentSubject).toHaveValue(
			notificationTemplateInfo.subject
		);
	});

	test('can be sent to a regular role', async ({
		apiHelpers,
		editObjectActionPage,
		notificationsPage,
		page,
		userNotificationTemplatePage,
		viewObjectActionsPage,
	}) => {
		const roleName = getRandomString();

		const role = await apiHelpers.headlessAdminUser.postRole({
			externalReferenceCode: getRandomString(),
			name: roleName,
			name_i18n: {en_US: getRandomString()},
			roleType: 'regular',
		});

		apiHelpers.data.push({
			id: role.id,
			type: 'role',
		});

		const user =
			await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
				'test@liferay.com'
			);

		await apiHelpers.headlessAdminUser.assignUserToRole(
			role.externalReferenceCode,
			user.id
		);

		await userNotificationTemplatePage.goto();

		const notificationTemplateName = getRandomString();

		await userNotificationTemplatePage.basicInfoName.fill(
			notificationTemplateName
		);

		const contentSubject = getRandomString();

		await userNotificationTemplatePage.contentSubject.fill(contentSubject);

		await userNotificationTemplatePage.selectNotificationRecipient('Role');

		await userNotificationTemplatePage.selectRole(roleName);

		await userNotificationTemplatePage.saveButton.click();

		await page.getByText(notificationTemplateName).click();

		const notificationTemplateId = await page
			.locator('span:has-text("ID:") + strong')
			.textContent();

		apiHelpers.data.push({
			id: notificationTemplateId,
			type: 'notificationTemplate',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		await viewObjectActionsPage.goto(objectDefinition.label['en_US']);

		await editObjectActionPage.addNewAction(
			'Notification',
			'On After Add',
			notificationTemplateName
		);

		const applicationName =
			'c/' + objectDefinition.name.toLowerCase() + 's';

		const objectFieldValue = getRandomString();

		await apiHelpers.objectEntry.postObjectEntry(
			{textField: objectFieldValue},
			applicationName
		);

		await notificationsPage.goto();

		await page.getByText(contentSubject).click();

		await expect(page.getByLabel('textField', {exact: true})).toHaveValue(
			objectFieldValue
		);
	});

	test(
		'Support for User Groups in User Notification template',
		{tag: '@LPD-57578'},
		async ({apiHelpers, userNotificationTemplatePage}) => {
			const userGroup1 =
				await apiHelpers.headlessAdminUser.postUserGroup();
			const userGroup2 =
				await apiHelpers.headlessAdminUser.postUserGroup();

			await test.step('AC1: Display "User Group" option in the Recipient field.', async () => {
				await userNotificationTemplatePage.goto();
				await userNotificationTemplatePage.selectNotificationRecipient(
					'User Group'
				);
			});

			await test.step('AC2: Display existing user groups in the "User Group" field', async () => {
				await userNotificationTemplatePage.page
					.getByRole('combobox', {name: 'Select User Group'})
					.click();
				await expect(
					userNotificationTemplatePage.page.getByText(userGroup1.name)
				).toBeVisible();
				await expect(
					userNotificationTemplatePage.page.getByText(userGroup2.name)
				).toBeVisible();
			});

			await test.step('AC3: Multi-Selection Support', async () => {
				for (const userGroupName of [
					userGroup1.name,
					userGroup2.name,
				]) {
					await userNotificationTemplatePage.page
						.getByLabel(userGroupName)
						.click();
					await expect(
						userNotificationTemplatePage.page.getByLabel(
							userGroupName,
							{exact: true}
						)
					).toBeVisible();
				}
			});

			await test.step('AC4: Support Search and Filtering', async () => {
				userNotificationTemplatePage.page
					.getByRole('textbox', {name: 'Search for a User Group.'})
					.fill(userGroup1.name);
				await expect(
					userNotificationTemplatePage.page.getByLabel(
						userGroup1.name,
						{
							exact: true,
						}
					)
				).toBeVisible();
				await expect(
					userNotificationTemplatePage.page.getByLabel(
						userGroup2.name,
						{
							exact: true,
						}
					)
				).not.toBeVisible();
			});

			const notificationTemplateName = getRandomString();

			await userNotificationTemplatePage.basicInfoName.fill(
				notificationTemplateName
			);

			await userNotificationTemplatePage.contentSubject.fill(
				getRandomString()
			);

			await userNotificationTemplatePage.saveButton.click();

			await userNotificationTemplatePage.page
				.getByText(notificationTemplateName)
				.click();

			await test.step('AC5: Save User Group Selection', async () => {
				for (const userGroupName of [
					userGroup1.name,
					userGroup2.name,
				]) {
					await expect(
						userNotificationTemplatePage.page.getByRole('row', {
							name: `${userGroupName} Remove`,
						})
					).toBeVisible();
				}
			});
		}
	);
});
