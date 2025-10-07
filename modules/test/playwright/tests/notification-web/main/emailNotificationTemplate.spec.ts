/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {apiHelpersTest} from '../../../fixtures/apiHelpersTest';
import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {loginTest} from '../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../fixtures/notificationPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {waitForAlert} from '../../../utils/waitForAlert';

export const test = mergeTests(
	apiHelpersTest,
	dataApiHelpersTest,
	featureFlagsTest({
		'LPD-50091': {enabled: true},
	}),
	loginTest(),
	notificationPagesTest
);

let objectDefinition: ObjectDefinition;

let userGroups;

const notificationTemplateInfo = {
	bcc: 'test3@liferay.com',
	cc: 'test2@liferay.com',
	description: 'This is a description',
	recipients: 'test@liferay.com, test4@liferay.com',
	senderAddress: 'test1@liferay.com',
	senderName: 'Test Test',
	subject: 'This is a subject',
	term: '[%CURRENT_USER_FIRST_NAME%]',
};

test.beforeEach(async ({apiHelpers}) => {
	objectDefinition = await apiHelpers.objectAdmin.postRandomObjectDefinition({
		status: {code: 0},
	});
});

test.afterEach(async ({apiHelpers, notificationTemplatesPage, page}) => {
	const objectDefinitionAPIClient =
		await apiHelpers.buildRestClient(ObjectDefinitionAPI);

	await objectDefinitionAPIClient.deleteObjectDefinition(objectDefinition.id);

	await notificationTemplatesPage.goto();

	const frontEndDatasetItemActions =
		await notificationTemplatesPage.frontEndDatasetItemAction.all();

	for (let i = 0; i < frontEndDatasetItemActions.length; i++) {
		try {
			const actionButton = page
				.getByRole('button', {name: 'Actions'})
				.first();
			if (actionButton) {
				await actionButton.click();
				const deleteButton =
					notificationTemplatesPage.frontEndDatasetItemActionDelete;

				if (deleteButton) {
					await deleteButton.click();
					await waitForAlert(page);
				}
			}
		}
		catch (error) {
			throw new Error(error);
		}
	}

	if (userGroups !== undefined) {
		for (const userGroup of userGroups) {
			await apiHelpers.headlessAdminUser.deleteUserGroup(userGroup.id);
		}

		userGroups = [];
	}
});

test.describe('Email notification template', () => {
	test('can be created and saved correctly', async ({
		emailNotificationTemplatePage,
		notificationTemplatesPage,
	}) => {
		await emailNotificationTemplatePage.goto();

		const notificationTemplateName =
			'Notification Template Name' + getRandomInt();

		await emailNotificationTemplatePage.fillNotificationTemplateInfo(
			notificationTemplateName,
			notificationTemplateInfo
		);

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplateName)
			.click();

		await expect(emailNotificationTemplatePage.basicInfoName).toHaveValue(
			notificationTemplateName
		);

		await expect(
			emailNotificationTemplatePage.descriptionInput
		).toHaveValue(notificationTemplateInfo.description);

		await expect(
			emailNotificationTemplatePage.senderEmailAddress
		).toHaveValue(notificationTemplateInfo.senderAddress);

		await expect(emailNotificationTemplatePage.senderName).toHaveValue(
			notificationTemplateInfo.senderName
		);

		await expect(
			emailNotificationTemplatePage.primaryRecipients
		).toHaveValue(notificationTemplateInfo.recipients);

		await expect(
			emailNotificationTemplatePage.secondaryRecipientsCC
		).toHaveValue(notificationTemplateInfo.cc);

		await expect(
			emailNotificationTemplatePage.secondaryRecipientsBCC
		).toHaveValue(notificationTemplateInfo.bcc);

		await expect(emailNotificationTemplatePage.contentSubject).toHaveValue(
			notificationTemplateInfo.subject
		);
	});

	test('can be edited correctly', async ({
		apiHelpers,
		emailNotificationTemplatePage,
		notificationTemplatesPage,
	}) => {
		const notificationTemplate =
			await apiHelpers.notification.postRandomNotificationTemplate(
				'notification template test ' + getRandomInt()
			);

		apiHelpers.data.push({
			id: notificationTemplate.id,
			type: 'notificationTemplate',
		});

		await notificationTemplatesPage.goto();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplate.name)
			.click();

		await emailNotificationTemplatePage.page
			.getByRole('heading', {name: notificationTemplate.name})
			.waitFor();

		const editedNotificationTemplateInfo = {
			bcc: getRandomString(),
			cc: getRandomString(),
			description: getRandomString(),
			recipients: getRandomString(),
			senderAddress: getRandomString(),
			senderName: getRandomString(),
			subject: getRandomString(),
			term: getRandomString(),
		};

		const editedNotificationTemplateName = getRandomString();

		await emailNotificationTemplatePage.fillNotificationTemplateInfo(
			editedNotificationTemplateName,
			editedNotificationTemplateInfo
		);

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(editedNotificationTemplateName)
			.click();

		await expect
			.soft(emailNotificationTemplatePage.basicInfoName)
			.toHaveValue(editedNotificationTemplateName);

		await expect
			.soft(emailNotificationTemplatePage.descriptionInput)
			.toHaveValue(editedNotificationTemplateInfo.description);

		await expect
			.soft(emailNotificationTemplatePage.senderEmailAddress)
			.toHaveValue(editedNotificationTemplateInfo.senderAddress);

		await expect
			.soft(emailNotificationTemplatePage.senderName)
			.toHaveValue(editedNotificationTemplateInfo.senderName);

		await expect
			.soft(emailNotificationTemplatePage.primaryRecipients)
			.toHaveValue(editedNotificationTemplateInfo.recipients);

		await expect
			.soft(emailNotificationTemplatePage.secondaryRecipientsCC)
			.toHaveValue(editedNotificationTemplateInfo.cc);

		await expect
			.soft(emailNotificationTemplatePage.secondaryRecipientsBCC)
			.toHaveValue(editedNotificationTemplateInfo.bcc);

		await expect
			.soft(emailNotificationTemplatePage.contentSubject)
			.toHaveValue(editedNotificationTemplateInfo.subject);

		await expect
			.soft(emailNotificationTemplatePage.contentSubject)
			.toHaveValue(editedNotificationTemplateInfo.subject);

		await expect(test.info().errors).toHaveLength(0);
	});

	test('can have rich text source code verifying that the source code is persisted', async ({
		emailNotificationTemplatePage,
		notificationTemplatesPage,
		page,
	}) => {
		await emailNotificationTemplatePage.goto();

		const notificationTemplateName =
			'Notification Template Name' + getRandomInt();

		await emailNotificationTemplatePage.basicInfoName.fill(
			notificationTemplateName
		);

		await emailNotificationTemplatePage.senderEmailAddress.fill(
			'test@liferay.com'
		);

		await emailNotificationTemplatePage.senderName.fill('test user');

		await emailNotificationTemplatePage.primaryRecipients.fill(
			'test@liferay.com'
		);

		await emailNotificationTemplatePage.contentSubject.fill(
			'Content subject'
		);

		await emailNotificationTemplatePage.richTextSourceButton.click();

		await emailNotificationTemplatePage.richTextSourceField.fill(
			'<h1>Hello World</h1>'
		);

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplateName)
			.click();

		await emailNotificationTemplatePage.richTextSourceButton.click();

		await expect(page.getByText('<h1>Hello World</h1>')).toBeVisible();
	});

	test('can save recipients roles', async ({
		emailNotificationTemplatePage,
		notificationTemplatesPage,
		page,
	}) => {
		const primaryRecipientsRoles = [
			'Account Administrator',
			'Account Member',
			'Administrator',
			'Analytics Administrator',
			'Account Manager',
			'Organization Administrator',
		];

		const secondaryRecipientsRolesCC = [
			'Account Supplier',
			'Buyer',
			'Owner',
			'Portal Content Reviewer',
			'Organization Content Reviewer',
			'Organization Owner',
		];

		const secondaryRecipientsRolesBCC = [
			'Order Manager',
			'Power User',
			'Publications User',
			'Organization User',
		];

		await emailNotificationTemplatePage.goto();

		const notificationTemplateName =
			'Notification Template Name' + getRandomInt();

		await emailNotificationTemplatePage.basicInfoName.fill(
			notificationTemplateName
		);

		await emailNotificationTemplatePage.senderEmailAddress.fill(
			'test@liferay.com'
		);

		await emailNotificationTemplatePage.senderName.fill('test user');

		await emailNotificationTemplatePage.primaryRecipientType.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.primaryRecipients.click();

		for (const role of primaryRecipientsRoles) {
			await page
				.getByLabel(role, {exact: true})
				.locator('visible=true')
				.check();
		}

		await emailNotificationTemplatePage.secondaryRecipientTypeCC.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.secondaryRecipientsCC.click();

		for (const role of secondaryRecipientsRolesCC) {
			await page
				.getByLabel(role, {exact: true})
				.locator('visible=true')
				.check();
		}

		await emailNotificationTemplatePage.secondaryRecipientTypeBCC.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.secondaryRecipientsBCC.click();

		for (const role of secondaryRecipientsRolesBCC) {
			await page
				.getByLabel(role, {exact: true})
				.locator('visible=true')
				.check();
		}

		await emailNotificationTemplatePage.contentSubject.fill(
			'Content subject'
		);

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplateName)
			.click();

		await emailNotificationTemplatePage.primaryRecipients.click();

		for (const role of primaryRecipientsRoles) {
			await expect(
				page.getByLabel(role, {exact: true}).locator('visible=true')
			).toBeChecked();
		}

		await emailNotificationTemplatePage.secondaryRecipientsCC.click();

		for (const role of secondaryRecipientsRolesCC) {
			await expect(
				page.getByLabel(role, {exact: true}).locator('visible=true')
			).toBeChecked();
		}

		await emailNotificationTemplatePage.secondaryRecipientsBCC.click();

		for (const role of secondaryRecipientsRolesBCC) {
			await expect(
				page.getByLabel(role, {exact: true}).locator('visible=true')
			).toBeChecked();
		}
	});

	test('can have all roles groups in recipients', async ({
		emailNotificationTemplatePage,
		page,
	}) => {
		await emailNotificationTemplatePage.goto();

		await emailNotificationTemplatePage.primaryRecipientType.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.primaryRecipients.click();

		await expect(
			emailNotificationTemplatePage.accountRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.regularRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.organizationRolesGroupTitle
		).toBeVisible();

		await page.keyboard.press('Escape');

		await emailNotificationTemplatePage.secondaryRecipientTypeCC.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.secondaryRecipientsCC.click();

		await expect(
			emailNotificationTemplatePage.accountRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.regularRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.organizationRolesGroupTitle
		).toBeVisible();

		await page.keyboard.press('Escape');

		await emailNotificationTemplatePage.secondaryRecipientTypeBCC.click();

		await page.getByRole('option', {name: 'Roles'}).click();

		await emailNotificationTemplatePage.secondaryRecipientsBCC.click();

		await expect(
			emailNotificationTemplatePage.accountRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.regularRolesGroupTitle
		).toBeVisible();

		await expect(
			emailNotificationTemplatePage.organizationRolesGroupTitle
		).toBeVisible();
	});

	test('can see cc/bcc fields in UI when creating notification via API without passing them', async ({
		apiHelpers,
		emailNotificationTemplatePage,
		notificationTemplatesPage,
	}) => {
		const notificationTemplate =
			await apiHelpers.notification.postNotificationTemplate({
				editorType: 'richText',
				name: 'Test Email',
				recipientType: 'email',
				recipients: [
					{
						from: 'test@liferay.com',
						fromName: {
							en_US: 'Test',
						},
						to: [
							{
								roleName: 'Account Administrator',
							},
						],
						toType: 'role',
					},
				],
				subject: {
					en_US: 'Subject',
				},
				type: 'email',
			});

		await notificationTemplatesPage.goto();

		await notificationTemplatesPage.openNotificationTemplate(
			notificationTemplate.name
		);

		await expect(
			emailNotificationTemplatePage.secondaryRecipientsBCC
		).toBeVisible();
		await expect(
			emailNotificationTemplatePage.secondaryRecipientsCC
		).toBeVisible();
	});

	test('can use notification terms and freeMarker variables', async ({
		emailNotificationTemplatePage,
		notificationTemplatesPage,
		page,
	}) => {
		await emailNotificationTemplatePage.goto();

		const notificationTemplateName =
			'Notification Template Name' + getRandomInt();

		await emailNotificationTemplatePage.basicInfoName.fill(
			notificationTemplateName
		);

		await emailNotificationTemplatePage.senderEmailAddress.fill(
			'test@liferay.com'
		);

		await emailNotificationTemplatePage.senderName.fill('test user');

		await emailNotificationTemplatePage.primaryRecipients.fill(
			'[%CURRENT_USER_EMAIL_ADDRESS%]'
		);

		await emailNotificationTemplatePage.contentSubject.fill(
			'Content subject'
		);

		await emailNotificationTemplatePage.definitionOfTermsEntity.click();

		await page
			.getByRole('option', {name: objectDefinition.externalReferenceCode})
			.click();

		const objectDefinitionTerm =
			objectDefinition.externalReferenceCode.toUpperCase();

		const objectFieldName = objectDefinition.objectFields.find(
			(objectField) => !objectField.system
		).name;

		const terms = [
			'[%CURRENT_USER_FIRST_NAME%]',
			'[%CURRENT_USER_PREFIX%]',
			'[%CURRENT_DATE%]',
			'[%CURRENT_USER_LAST_NAME%]',
			'[%CURRENT_USER_MIDDLE_NAME%]',
			'[%CURRENT_USER_EMAIL_ADDRESS%]',
			'[%CURRENT_USER_ID%]',
			'[%CURRENT_USER_SUFFIX%]',
			`[%${objectDefinitionTerm}_CREATEDATE%]`,
			`[%${objectDefinitionTerm}_AUTHOR_EMAIL_ADDRESS%]`,
			`[%${objectDefinitionTerm}_AUTHOR_SUFFIX%]`,
			`[%${objectDefinitionTerm}_AUTHOR_PREFIX%]`,
			`[%${objectDefinitionTerm}_AUTHOR_FIRST_NAME%]`,
			`[%${objectDefinitionTerm}_AUTHOR_LAST_NAME%]`,
			`[%${objectDefinitionTerm}_AUTHOR_MIDDLE_NAME%]`,
			`[%${objectDefinitionTerm}_AUTHOR_ID%]`,
			`[%${objectDefinitionTerm}_EXTERNALREFERENCECODE%]`,
			`[%${objectDefinitionTerm}_ID%]`,
			`[%${objectDefinitionTerm}_MODIFIEDDATE%]`,
			`[%${objectDefinitionTerm}_STATUS%]`,
			`[%${objectDefinitionTerm}_${objectFieldName.toUpperCase()}%]`,
		];

		for (const term of terms) {
			await expect(page.locator('.fds td').getByText(term)).toBeVisible();
		}

		const copyButtons = [
			emailNotificationTemplatePage.copyButton.first(),
			emailNotificationTemplatePage.copyButton.last(),
		];

		for (const copyButton of copyButtons) {
			await copyButton.click();

			await emailNotificationTemplatePage.richTextField.click();

			await page.keyboard.press('PageDown');

			await page.keyboard.press('Control+V');
		}

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplateName)
			.click();

		await expect(
			emailNotificationTemplatePage.primaryRecipients
		).toHaveValue('[%CURRENT_USER_EMAIL_ADDRESS%]');

		await expect(
			emailNotificationTemplatePage.richTextField.getByText(
				'[%CURRENT_USER_FIRST_NAME%]' +
					`[%${objectDefinitionTerm}_${objectFieldName.toUpperCase()}%]`
			)
		).toBeVisible();

		await emailNotificationTemplatePage.editorType.click();

		await page.getByRole('option', {name: 'FreeMarker Template'}).click();

		await expect(page.getByText('Elements')).toBeVisible();

		await emailNotificationTemplatePage.freeMarkerEntity.click();

		await page
			.getByRole('option', {name: objectDefinition.label['en_US']})
			.click();

		const freeMarkerVariables = [
			'Author',
			'Create Date',
			'Default',
			'External Reference Code',
			'ID',
			'Locale',
			'Modified Date',
			'Portal URL',
			'Publish Date',
			'Status',
			'User Profile Image',
			objectFieldName,
		];

		for (const freeMarkerVariable of freeMarkerVariables) {
			await expect(
				page.getByRole('button', {
					exact: true,
					name: freeMarkerVariable,
				})
			).toBeVisible();
		}

		await page.getByRole('button', {name: objectFieldName}).click();

		await emailNotificationTemplatePage.saveButton.click();

		await notificationTemplatesPage
			.getFrontEndDatasetItemLocator(notificationTemplateName)
			.click();

		await expect(
			page
				.locator('.CodeMirror-lines')
				.getByText(`{ObjectField_${objectFieldName}.getData()}`)
		).toBeVisible();
	});

	test(
		'Verify User Groups are now supported for Email Notification Templates',
		{tag: '@LPD-57577'},
		async ({
			apiHelpers,
			emailNotificationTemplatePage,
			notificationTemplatesPage,
			page,
		}) => {
			const recipientUserGroupFields = [
				emailNotificationTemplatePage.primaryRecipients,
				emailNotificationTemplatePage.secondaryRecipientsBCC,
				emailNotificationTemplatePage.secondaryRecipientsCC,
			];

			userGroups = [
				await apiHelpers.headlessAdminUser.postUserGroup(),
				await apiHelpers.headlessAdminUser.postUserGroup(),
			];

			await test.step('AC1: User Group option is available', async () => {
				await emailNotificationTemplatePage.goto();

				await emailNotificationTemplatePage.primaryRecipientType.click();

				expect(
					await page.getByRole('option', {name: 'User Groups'})
				).toBeVisible();

				await page.getByRole('option', {name: 'User Groups'}).click();

				await emailNotificationTemplatePage.secondaryRecipientTypeCC.click();

				expect(
					await page.getByRole('option', {name: 'User Groups'})
				).toBeVisible();

				await page.getByRole('option', {name: 'User Groups'}).click();

				await emailNotificationTemplatePage.secondaryRecipientTypeBCC.click();

				expect(
					await page.getByRole('option', {name: 'User Groups'})
				).toBeVisible();

				await page.getByRole('option', {name: 'User Groups'}).click();
			});

			await test.step('AC2: Existing User Groups are displayed', async () => {
				for (const recipientUserGroupField of recipientUserGroupFields) {
					await recipientUserGroupField.click();

					for (const userGroup of userGroups) {
						await expect(
							page.getByRole('menuitem', {name: userGroup.name})
						).toBeVisible();
					}

					await page.keyboard.press('Escape');
				}
			});

			await test.step('AC3: All selected User Groups will be displayed', async () => {
				for (const recipientUserGroupField of recipientUserGroupFields) {
					await recipientUserGroupField.click();

					for (const userGroup of userGroups) {
						await page
							.getByRole('checkbox', {name: userGroup.name})
							.check();
					}

					await page.keyboard.press('Escape');
				}

				for (const userGroup of userGroups) {
					await expect(
						await page
							.getByRole('row', {name: userGroup.name})
							.count()
					).toBe(recipientUserGroupFields.length);
				}
			});

			await test.step('AC4: Typing in the User Group selection field will filter results', async () => {
				for (const recipientUserGroupField of recipientUserGroupFields) {
					await recipientUserGroupField.click();

					await page
						.getByRole('textbox', {
							name: 'Search for a user group.',
						})
						.fill('UserGroup');

					for (const userGroup of userGroups) {
						await expect(
							await page.getByRole('checkbox', {
								name: userGroup.name,
							})
						).toBeVisible();
					}

					await page
						.getByRole('textbox', {
							name: 'Search for a user group.',
						})
						.fill('foobar');

					for (const userGroup of userGroups) {
						await expect(
							await page.getByRole('checkbox', {
								name: userGroup.name,
							})
						).not.toBeVisible();
					}

					await page.keyboard.press('Escape');
				}
			});

			await test.step('AC5: Verify User Group selections are saved', async () => {
				const notificationTemplateName =
					'Notification Template Name' + getRandomInt();

				await emailNotificationTemplatePage.basicInfoName.fill(
					notificationTemplateName
				);

				await emailNotificationTemplatePage.senderEmailAddress.fill(
					'test@liferay.com'
				);

				await emailNotificationTemplatePage.senderName.fill(
					'test user'
				);

				await emailNotificationTemplatePage.contentSubject.fill(
					'Content subject'
				);

				await emailNotificationTemplatePage.saveButton.click();

				await notificationTemplatesPage
					.getFrontEndDatasetItemLocator(notificationTemplateName)
					.click();

				// We waitFor() a previously selected user group since the page can
				// take 2+ seconds to render the entries, causing a race condition

				await emailNotificationTemplatePage.page
					.getByRole('row', {name: userGroups[0].name})
					.first()
					.waitFor();

				for (const userGroup of userGroups) {
					await expect(
						await emailNotificationTemplatePage.page
							.getByRole('row', {name: userGroup.name})
							.count()
					).toBe(recipientUserGroupFields.length);
				}
			});
		}
	);
});
