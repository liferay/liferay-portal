/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectActionAPI,
	ObjectDefinition,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {featureFlagsTest} from '../../../fixtures/featureFlagsTest';
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {notificationPagesTest} from '../../../fixtures/notificationPagesTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {smtpPagesTest} from '../../../fixtures/smtpPagesTest';
import {usersAndOrganizationsPagesTest} from '../../../fixtures/usersAndOrganizationsPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';

const test = mergeTests(
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	featureFlagsTest({
		'LPS-178052': {enabled: true},
	}),
	isolatedSiteTest,
	loginTest(),
	notificationPagesTest,
	objectPagesTest,
	smtpPagesTest,
	usersAndOrganizationsPagesTest
);

test(
	'Can send email via on add object action using terms from related custom object',
	{tag: '@LPD-78504'},
	async ({apiHelpers, mockMockPage, page, site: _site}) => {
		const objectDefinitions: ObjectDefinition[] = [];

		for (const letter of ['A', 'B']) {
			const objectDefinitionExternalReferenceCode = `${letter}${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: `customField${letter}`,
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: `Text Field ${letter}`},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customField',
							required: false,
							system: false,
							type: 'String',
						},
					],
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			objectDefinitions.push(objectDefinition);
		}

		const objectDefinitionA = objectDefinitions[0];
		const objectDefinitionB = objectDefinitions[1];

		const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
			ObjectRelationshipAPI
		);

		const {body: objectRelationship} =
			await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
				objectDefinitionA.externalReferenceCode,
				{
					label: {en_US: 'Relationship'},
					name: 'relationship',
					objectDefinitionExternalReferenceCode1:
						objectDefinitionA.externalReferenceCode,
					objectDefinitionExternalReferenceCode2:
						objectDefinitionB.externalReferenceCode,
					type: 'oneToMany',
				}
			);

		const externalReferenceCode = (objectDefinition: ObjectDefinition) =>
			objectDefinition.externalReferenceCode.toUpperCase();

		const subjectTerm = `[%${objectRelationship.name.toUpperCase()}_${externalReferenceCode(objectDefinitionA)}_CUSTOMFIELD%] | [%${externalReferenceCode(objectDefinitionB)}_CUSTOMFIELD%]`;

		const objectRelationshipFieldName = `r_${objectRelationship.name}_c_${objectDefinitionA.name[0].toLowerCase() + objectDefinitionA.name.substring(1)}Id`;

		const notificationTemplate =
			await apiHelpers.notification.postNotificationTemplate({
				body: {en_US: 'Email Body'},
				editorType: 'richText',
				name: 'Notification Template ' + getRandomInt(),
				recipientType: 'email',
				recipients: [
					{
						from: '[%CURRENT_USER_EMAIL_ADDRESS%]',
						fromName: {en_US: '[%CURRENT_USER_FIRST_NAME%]'},
						to: {en_US: '[%CURRENT_USER_EMAIL_ADDRESS%]'},
						toType: 'email',
					},
				],
				subject: {en_US: subjectTerm},
				type: 'email',
			});

		apiHelpers.data.push({
			id: notificationTemplate.id,
			type: 'notificationTemplate',
		});

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		const objectAction =
			await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
				objectDefinitionB.externalReferenceCode,
				{
					active: true,
					label: {en_US: 'Send email on add'},
					name: 'sendEmailOnAdd',
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

		apiHelpers.data.push({
			id: objectAction.body.id,
			type: 'objectAction',
		});

		const objectEntryA = await apiHelpers.objectEntry.postObjectEntry(
			{customField: 'Entry Test A'},
			'c/' + objectDefinitionA.name.toLowerCase() + 's'
		);

		await performLogout(page);

		await performLoginViaApi({page, screenName: 'demo.company.admin'});

		await apiHelpers.objectEntry.postObjectEntry(
			{
				customField: 'Entry Test B',
				[objectRelationshipFieldName]: objectEntryA.id,
			},
			'c/' + objectDefinitionB.name.toLowerCase() + 's'
		);

		const demoAdmin = userData['demo.company.admin'];

		await mockMockPage.assertEmail({
			body: 'Email Body',
			from: demoAdmin.name,
			subject: 'Entry Test A | Entry Test B',
			to: 'demo.company.admin@liferay.com',
		});
	}
);

test(
	'Can send user notification to a regular role',
	{tag: '@LPD-78504'},
	async ({
		apiHelpers,
		editObjectActionPage,
		notificationsPage,
		page,
		site: _site,
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

		await page.getByRole('link', {name: notificationTemplateName}).click();

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

		await viewObjectActionsPage.gotoByObjectDefinitionId(
			objectDefinition.id
		);

		await editObjectActionPage.addNewAction({
			notificationTemplateName,
			thenOption: 'Notification',
			whenOption: 'On After Add',
		});

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
	}
);
