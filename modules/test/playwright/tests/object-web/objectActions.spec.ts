/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {expect, mergeTests} from '@playwright/test';
import path from 'node:path';

import {apiHelpersTest} from '../../fixtures/apiHelpersTest';
import {editObjectDefinitionPagesTest} from '../../fixtures/editObjectDefinitionPagesTest';
import {loginTest} from '../../fixtures/loginTest';
import {objectPagesTest} from '../../fixtures/objectPagesTest';
import {getRandomInt} from '../../utils/getRandomInt';
import {waitForAlert} from '../../utils/waitForAlert';
import {mockedObjectFields} from './dependencies/objectMockedFields';

export const test = mergeTests(
	apiHelpersTest,
	editObjectDefinitionPagesTest,
	loginTest(),
	objectPagesTest
);

const createdEntities = {
	notificationQueueEntriesId: [],
	notificationTemplatesId: [],
	objectActionsId: [],
	objectDefinition: {},
} as {
	notificationQueueEntriesId: number[];
	notificationTemplatesId: number[];
	objectActionsId: number[];
	objectDefinition: ObjectDefinition;
};

test.beforeEach(async ({apiHelpers}) => {
	const newObjectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFolderExternalReferenceCode: 'default',
			status: {code: 0},
		});

	createdEntities.objectDefinition = newObjectDefinition;
});

test.afterEach(async ({apiHelpers}) => {
	await apiHelpers.objectAdmin.deleteObjectDefinition(
		createdEntities.objectDefinition.id
	);

	for (const queueEntryId of createdEntities.notificationQueueEntriesId) {
		await apiHelpers.notification.deleteNotificationQueueEntry(
			queueEntryId
		);
	}

	createdEntities.notificationQueueEntriesId = [];

	for (const templateId of createdEntities.notificationTemplatesId) {
		await apiHelpers.notification.deleteNotificationTemplate(templateId);
	}

	createdEntities.notificationTemplatesId = [];

	for (const actionId of createdEntities.objectActionsId) {
		await apiHelpers.objectAdmin.deleteObjectAction(actionId);
	}

	createdEntities.objectActionsId = [];
});

test.describe('Manage object actions through object actions tab', () => {
	test('notification action section must display all persisted notifications', async ({
		apiHelpers,
		editObjectActionPage,
		page,
		viewObjectActionsPage,
	}) => {
		const names: string[] = [];

		const {notificationTemplatesId, objectDefinition} = createdEntities;

		for (let index = 1; index <= 21; index++) {
			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					'notification template test ' + getRandomInt()
				);
			notificationTemplatesId.push(notificationTemplate.id);
			names.push(
				notificationTemplate.name + ' ' + notificationTemplate.type
			);
		}

		await viewObjectActionsPage.goto(objectDefinition.label['en_US']);

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
		const {objectActionsId} = createdEntities;

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

		const objectActions =
			await apiHelpers.objectAdmin.getObjectActionsByExternalReferenceCode(
				'L_COMMERCE_ORDER'
			);

		objectActions.items.forEach((objectAction: ObjectAction) =>
			objectActionsId.push(objectAction.id)
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
		const {objectDefinition} = createdEntities;

		const notificationTemplateName =
			'notification template test ' + getRandomInt();

		const notificationTemplate =
			await apiHelpers.notification.postRandomNotificationTemplate(
				notificationTemplateName,
				'test' + getRandomInt() + '@liferay.com'
			);

		createdEntities.notificationTemplatesId = [notificationTemplate.id];

		await viewObjectActionsPage.goto(objectDefinition.label['en_US']);

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

	createdEntities.notificationTemplatesId = [notificationTemplate.id];

	// Create object definition with an attachment field

	const objectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [mockedObjectFields.attachmentFieldUserComputer],
			objectFolderExternalReferenceCode: 'default',
			status: {code: 0},
		});

	createdEntities.objectDefinition = objectDefinition;

	// Create an action to send notification after attachment download

	await apiHelpers.objectAdmin.postObjectActionByExternalReferenceCode(
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

	await viewObjectEntriesPage.goto(objectDefinition.id);

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

	await viewObjectEntriesPage.goto(objectDefinition.id);

	await page
		.getByRole('button', {name: 'Search'})
		.waitFor({state: 'visible'});

	await viewObjectEntriesPage.page.getByText('sampleFile.txt').click();

	// Verify if the email was sent

	const notificationQueueEntries =
		await apiHelpers.notification.getNotificationQueueEntriesPage(
			senderEmail
		);

	createdEntities.notificationQueueEntriesId =
		notificationQueueEntries.items.map((item: any) => item.id);

	expect(notificationQueueEntries.items.length).toBeTruthy();
});
