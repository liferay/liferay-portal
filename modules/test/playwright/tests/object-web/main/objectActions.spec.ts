/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectAction,
	ObjectActionAPI,
	ObjectDefinition,
} from '@liferay/object-admin-rest-client-js';
import {expect, mergeTests} from '@playwright/test';
import path from 'node:path';

import {dataApiHelpersTest} from '../../../fixtures/dataApiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../../fixtures/editObjectDefinitionPagesTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {getRandomInt} from '../../../utils/getRandomInt';
import {waitForAlert} from '../../../utils/waitForAlert';
import {mockedObjectFields} from './dependencies/objectMockedFields';

export const test = mergeTests(
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	loginTest(),
	objectPagesTest
);

let createdObjectDefinition: ObjectDefinition;

test.beforeEach(async ({apiHelpers}) => {
	const newObjectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	apiHelpers.data.push({
		id: newObjectDefinition.id,
		type: 'objectDefinition',
	});

	createdObjectDefinition = newObjectDefinition;
});

test.describe('Manage object actions through object actions tab', () => {
	test('notification action section must display all persisted notifications', async ({
		apiHelpers,
		editObjectActionPage,
		page,
		viewObjectActionsPage,
	}) => {
		const names: string[] = [];

		for (let index = 1; index <= 21; index++) {
			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					'notification template test ' + getRandomInt()
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			names.push(
				notificationTemplate.name + ' ' + notificationTemplate.type
			);
		}

		await viewObjectActionsPage.goto(
			createdObjectDefinition.label['en_US']
		);

		await viewObjectActionsPage.openObjectActionSidePanel();

		await editObjectActionPage.openActionBuilderTab();

		await editObjectActionPage.chooseNotificationOption();

		await editObjectActionPage.clickInputNotificationsCombo();

		for (let index = 0; index < names.length; index++) {
			await expect(
				page
					.frameLocator('iframe')
					.getByRole('option', {name: names[index]})
			).toBeVisible();
		}
	});

	test('can create actions related to commerce order object', async ({
		apiHelpers,
		editObjectActionPage,
		page,
		viewObjectActionsPage,
	}) => {
		await viewObjectActionsPage.goto('Commerce Order');

		const objectActionsMock = [
			{
				objectAction: 'On Order Status Update',
			},
			{
				objectAction: 'On Payment Status Update',
			},
			{
				objectAction: 'On Subscription Status Update',
			},
		] as {objectAction: string}[];

		for (const {objectAction} of objectActionsMock) {
			await editObjectActionPage.addNewAction(
				'Split Order by Catalog',
				objectAction
			);
		}

		const objectActionAPIClient =
			await apiHelpers.buildRestClient(ObjectActionAPI);

		const {body: objectActions} =
			await objectActionAPIClient.getObjectDefinitionByExternalReferenceCodeObjectActionsPage(
				'L_COMMERCE_ORDER'
			);

		objectActions.items.forEach((objectAction: ObjectAction) =>
			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'})
		);

		for (const {objectAction} of objectActionsMock) {
			await expect(page.getByText(objectAction)).toBeVisible();
		}
	});

	test('can create an email notification object action using user preferred language', async ({
		apiHelpers,
		editObjectActionPage,
		page,
		viewObjectActionsPage,
	}) => {
		const notificationTemplateName =
			'notification template test ' + getRandomInt();

		const notificationTemplate =
			await apiHelpers.notification.postRandomNotificationTemplate(
				notificationTemplateName,
				'test' + getRandomInt() + '@liferay.com'
			);

		apiHelpers.data.push({
			id: notificationTemplate.id,
			type: 'notificationTemplate',
		});

		await viewObjectActionsPage.goto(
			createdObjectDefinition.label['en_US']
		);

		await editObjectActionPage.addNewAction(
			'Notification',
			'On After Add',
			notificationTemplateName
		);

		await page.waitForLoadState('networkidle');

		await viewObjectActionsPage.frontendDataSetItems
			.filter({
				hasText: 'On After Add',
			})
			.click();

		await editObjectActionPage.openActionBuilderTab();

		await expect(editObjectActionPage.userPreferredLanguage).toBeChecked();

		await editObjectActionPage.checkbox.uncheck();

		await editObjectActionPage.saveButton.click();

		await page.waitForLoadState('networkidle');

		await viewObjectActionsPage.frontendDataSetItems
			.filter({
				hasText: 'On After Add',
			})
			.click();

		await editObjectActionPage.openActionBuilderTab();

		await expect(
			editObjectActionPage.userPreferredLanguage
		).not.toBeChecked();
	});
});

test('can send notification email via download action', async ({
	apiHelpers,
	page,
	viewObjectEntriesPage,
}) => {

	// Create email notification template

	const senderEmail: string = 'test' + getRandomInt() + '@liferay.com';

	const notificationTemplate =
		await apiHelpers.notification.postRandomNotificationTemplate(
			'notification template test ' + getRandomInt(),
			senderEmail
		);

	apiHelpers.data.push({
		id: notificationTemplate.id,
		type: 'notificationTemplate',
	});

	// Create object definition with an attachment field

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [mockedObjectFields.attachmentFieldUserComputer],
			status: {code: 0},
		});

	apiHelpers.data.push({id: objectDefinition.id, type: 'objectDefinition'});

	// Create an action to send notification after attachment download

	const objectActionAPIClient =
		await apiHelpers.buildRestClient(ObjectActionAPI);

	await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
		objectDefinition.externalReferenceCode,
		{
			active: true,
			label: {
				en_US: 'downloadAttachmentArchive',
			},
			name: 'downloadAttachmentArchive',
			objectActionExecutorKey: 'notification',
			objectActionTriggerKey: 'onAfterAttachmentDownload',
			parameters: {
				notificationTemplateId: notificationTemplate.id,
				type: 'email',
			},
		}
	);

	// Create an object entry

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await viewObjectEntriesPage.clickAddObjectEntry(objectDefinition.name);

	const fileChooserPromise = page.waitForEvent('filechooser');

	await viewObjectEntriesPage.selectFileButton.click();

	const fileChooser = await fileChooserPromise;

	await fileChooser.setFiles(
		path.join(__dirname, 'dependencies', 'sampleFile.txt')
	);

	await viewObjectEntriesPage.page
		.getByText('sampleFile.txt')
		.waitFor({state: 'visible'});

	await viewObjectEntriesPage.saveObjectEntryButton.click();

	await waitForAlert(page);

	// Download attachment from object entry

	await viewObjectEntriesPage.goto(objectDefinition.className);

	await page
		.getByRole('button', {name: 'Search'})
		.waitFor({state: 'visible'});

	await viewObjectEntriesPage.page.getByText('sampleFile.txt').click();

	// Verify if the email was sent

	const notificationQueueEntries =
		await apiHelpers.notification.getNotificationQueueEntriesPage(
			senderEmail
		);

	const notificationQueueEntriesId = notificationQueueEntries.items.map(
		(item: any) => item.id
	);

	for (const notificationQueueEntryId of notificationQueueEntriesId) {
		apiHelpers.data.push({
			id: notificationQueueEntryId,
			type: 'notificationQueueEntry',
		});
	}

	expect(notificationQueueEntries.items.length).toBeTruthy();
});
