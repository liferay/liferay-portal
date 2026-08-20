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
import {isolatedSiteTest} from '../../../fixtures/isolatedSiteTest';
import {loginTest} from '../../../fixtures/loginTest';
import {objectPagesTest} from '../../../fixtures/objectPagesTest';
import {rolesPagesTest} from '../../../fixtures/rolesPagesTest';
import {scriptManagementPagesTest} from '../../../fixtures/scriptManagementPagesTest';
import {liferayConfig} from '../../../liferay.config';
import {getRandomInt} from '../../../utils/getRandomInt';
import getRandomString from '../../../utils/getRandomString';
import {
	performLoginViaApi,
	performLogout,
	userData,
} from '../../../utils/performLogin';
import {waitForAlert} from '../../../utils/waitForAlert';
import {miniumSetUp} from '../../commerce/utils/commerce';
import {mockedObjectFields} from '../dependencies/objectMockedFields';
import {generateFormulaObjectFields} from '../utils/generateFormulaObjectFields';
import {generateObjectFields} from '../utils/generateObjectFields';

export const test = mergeTests(
	dataApiHelpersTest,
	editObjectDefinitionPagesTest,
	isolatedSiteTest,
	loginTest(),
	objectPagesTest,
	rolesPagesTest,
	scriptManagementPagesTest
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

		await viewObjectActionsPage.gotoByObjectDefinitionId(
			createdObjectDefinition.id
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
			await editObjectActionPage.addNewAction({
				thenOption: 'Split Order by Catalog',
				whenOption: objectAction,
			});
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
			await expect(
				page.getByRole('link', {name: objectAction})
			).toBeVisible();
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

		await viewObjectActionsPage.gotoByObjectDefinitionId(
			createdObjectDefinition.id
		);

		await editObjectActionPage.addNewAction({
			notificationTemplateName,
			thenOption: 'Notification',
			whenOption: 'On After Add',
		});

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

	test('can create and update condition with expression builder', async ({
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

		await viewObjectActionsPage.gotoByObjectDefinitionId(
			createdObjectDefinition.id
		);

		await editObjectActionPage.addNewAction({
			expressionBuilderValue: 'Expression',
			notificationTemplateName,
			thenOption: 'Notification',
			whenOption: 'On After Add',
		});

		await page.waitForLoadState('networkidle');

		await page.getByRole('link', {name: 'On After Add'}).click();

		await editObjectActionPage.openActionBuilderTab();

		await expect(editObjectActionPage.expressionInput).toHaveValue(
			'Expression'
		);

		await editObjectActionPage.fillExpression('newExpression');

		await editObjectActionPage.saveButton.click();

		await page.waitForLoadState('networkidle');

		await page.getByRole('link', {name: 'On After Add'}).click();

		await editObjectActionPage.openActionBuilderTab();

		await expect(editObjectActionPage.expressionInput).toHaveValue(
			'newExpression'
		);
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
		path.join(__dirname, '../dependencies', 'sampleFile.txt')
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

	const downloadPromise = page.waitForEvent('download');

	await viewObjectEntriesPage.page.getByText('sampleFile.txt').click();

	const download = await downloadPromise;

	await expect(download.failure()).resolves.toBeNull();

	// Verify if the email was sent

	const notificationQueueEntries =
		await apiHelpers.notification.getNotificationQueueEntriesPage(
			senderEmail
		);

	expect(notificationQueueEntries.items.length).toBeTruthy();

	const notificationQueueEntriesId = notificationQueueEntries.items.map(
		(item: any) => item.id
	);

	for (const notificationQueueEntryId of notificationQueueEntriesId) {
		apiHelpers.data.push({
			id: notificationQueueEntryId,
			type: 'notificationQueueEntry',
		});
	}
});

test(
	'Can add user notification actions to system objects that have a user notification handler only',
	{tag: ['@LPD-77313']},
	async ({apiHelpers, editObjectActionPage, page, viewObjectActionsPage}) => {
		let notificationTemplate;

		await test.step('Create an user notification template', async () => {
			notificationTemplate =
				await apiHelpers.notification.postNotificationTemplate({
					editorType: 'richText',
					name: 'Commerce Order Note Template',
					recipientType: 'term',
					recipients: [
						{
							term: '[%COMMERCEORDERNOTE_RECIPIENT_IDS%]',
						},
					],
					subject: {
						en_US: '[%COMMERCEORDERNOTE_ORDERID%]',
					},
					type: 'userNotification',
				});

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});
		});

		await test.step('Verify that the notification template is shown for Commerce Order Note system object', async () => {
			await viewObjectActionsPage.goto('Commerce Order Note');
			await viewObjectActionsPage.openObjectActionSidePanel();

			await editObjectActionPage.openActionBuilderTab();
			await editObjectActionPage.chooseNotificationOption();
			await editObjectActionPage.clickInputNotificationsCombo();

			await expect(
				page.frameLocator('iframe').getByRole('option', {
					name: `${notificationTemplate?.name} User Notification`,
				})
			).toBeVisible();
		});

		await test.step('Verify that the notification template is not shown for Commerce Order system object', async () => {
			await viewObjectActionsPage.goto('Commerce Order');
			await viewObjectActionsPage.openObjectActionSidePanel();

			await editObjectActionPage.openActionBuilderTab();
			await editObjectActionPage.chooseNotificationOption();
			await editObjectActionPage.clickInputNotificationsCombo();

			await expect(
				page.frameLocator('iframe').getByRole('option', {
					name: `${notificationTemplate?.name} User Notification`,
				})
			).toHaveCount(0);
		});
	}
);

test.describe('Object Action CRUD', () => {
	test(
		'Can cancel the creation of an action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = page.frameLocator('iframe');

			await iframe.getByText('Cancel').click();

			await page.reload();

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);

	test(
		'Can cancel the update of an action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await page.getByRole('link', {name: 'Action Label'}).click();

			const iframe = page.frameLocator('iframe');

			await iframe.getByPlaceholder('Text to translate').clear();
			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Update Action Label');

			await iframe.getByText('Cancel').click();

			await expect(
				page.getByRole('link', {name: 'Action Label'})
			).toBeVisible();

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();
		}
	);

	test(
		'Can create an action with webhook',
		{tag: '@LPD-78504'},
		async ({
			apiHelpers,
			editObjectActionPage,
			page,
			viewObjectActionsPage,
		}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe
				.getByLabel('URL')
				.fill(liferayConfig.environment.baseUrl);

			await iframe.getByRole('button', {name: 'Save'}).click();

			await page.waitForLoadState('networkidle');

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(
				page.getByRole('link', {name: 'Action Label'})
			).toBeVisible();

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();
		}
	);

	test(
		'Can create an action on a system object that adds a custom object entry',
		{tag: '@LPD-102828'},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectEntryValue = getRandomString();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_ACCOUNT',
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'textField'},
								name: 'textField',
								value: objectEntryValue,
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.headlessAdminUser.postAccount();

			await viewObjectEntriesPage.goto(objectDefinition.className);

			await expect(page.getByText(objectEntryValue)).toBeVisible();
		}
	);

	test(
		'Can delete an action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName = 'action' + getRandomInt();

			await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await expect(
				page.getByRole('link', {name: 'Action Label'})
			).toBeVisible();

			await page
				.getByRole('row', {name: 'Action Label'})
				.getByRole('button', {name: 'Actions'})
				.click();

			await page.getByRole('menuitem', {name: 'Delete'}).click();

			await expect(
				page.getByRole('link', {name: 'Action Label'})
			).toBeHidden();
		}
	);

	test(
		'Can activate an object action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					active: false,
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await expect(page.getByRole('cell', {name: 'No'})).toBeVisible();

			await page.getByRole('link', {name: 'Action Label'}).click();

			const iframe = page.frameLocator('iframe');

			await iframe.getByLabel('Active', {exact: true}).check();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await page.waitForLoadState('networkidle');

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();
		}
	);

	test(
		'Can inactivate an object action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					active: true,
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();

			await page.getByRole('link', {name: 'Action Label'}).click();

			const iframe = page.frameLocator('iframe');

			await iframe.getByLabel('Active', {exact: true}).uncheck();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await page.waitForLoadState('networkidle');

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(page.getByRole('cell', {name: 'No'})).toBeVisible();
		}
	);

	test(
		'Can search for an action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName1 = 'actionOne' + getRandomInt();
			const actionName2 = 'actionTwo' + getRandomInt();

			const objectAction1 =
				await apiHelpers.objectAction.postRandomAction(
					objectDefinition.externalReferenceCode!,
					{
						label: {en_US: 'Action Label 1'},
						name: actionName1,
						objectActionExecutorKey: 'add-object-entry',
						objectActionTriggerKey: 'onAfterAdd',
						parameters: {
							objectDefinitionExternalReferenceCode:
								objectDefinition.externalReferenceCode,
						},
					}
				);

			apiHelpers.data.push({id: objectAction1.id, type: 'objectAction'});

			const objectAction2 =
				await apiHelpers.objectAction.postRandomAction(
					objectDefinition.externalReferenceCode!,
					{
						label: {en_US: 'Action Label 2'},
						name: actionName2,
						objectActionExecutorKey: 'add-object-entry',
						objectActionTriggerKey: 'onAfterAdd',
						parameters: {
							objectDefinitionExternalReferenceCode:
								objectDefinition.externalReferenceCode,
						},
					}
				);

			apiHelpers.data.push({id: objectAction2.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await expect(
				page.getByRole('link', {name: 'Action Label 1'})
			).toBeVisible();

			await expect(
				page.getByRole('link', {name: 'Action Label 2'})
			).toBeVisible();

			await page
				.getByTestId('managementToolbar')
				.getByPlaceholder('Search', {exact: true})
				.fill('2');
			await page.keyboard.press('Enter');

			await expect(
				page.getByRole('link', {name: 'Action Label 1'})
			).toBeHidden();

			await expect(
				page.getByRole('link', {name: 'Action Label 2'})
			).toBeVisible();
		}
	);

	test(
		'Can update an action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await test.step('Update the action name', async () => {
				await page.getByRole('link', {name: 'Action Label'}).click();

				const iframe = page.frameLocator('iframe');

				await iframe.getByPlaceholder('Text to translate').clear();
				await iframe
					.getByPlaceholder('Text to translate')
					.fill('Update Action Label');

				await iframe.getByRole('button', {name: 'Save'}).click();

				await page.waitForLoadState('networkidle');

				await viewObjectActionsPage.actionsTabItem.click();

				await expect(
					page.getByRole('link', {name: 'Update Action Label'})
				).toBeVisible();

				await expect(
					page.getByRole('cell', {name: 'Yes'})
				).toBeVisible();
			});
		}
	);
});

test.describe('Object Action Conditions and Triggers', () => {
	test(
		'Can create an action with expression builder condition',
		{tag: '@LPD-78504'},
		async ({
			apiHelpers,
			editObjectActionPage,
			page,
			viewObjectActionsPage,
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

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Custom Action');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await editObjectActionPage.fillExpression(
				objectFields[0].name + " == 'Entry Test'"
			);

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe
				.getByLabel('URL')
				.fill(liferayConfig.environment.baseUrl);

			await iframe.getByRole('button', {name: 'Save'}).click();

			await page.waitForLoadState('networkidle');

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(
				page.getByRole('link', {name: 'Custom Action'})
			).toBeVisible();

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();
		}
	);

	test(
		'Can disable condition on an action',
		{tag: '@LPD-78504'},
		async ({
			apiHelpers,
			editObjectActionPage,
			page,
			viewObjectActionsPage,
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

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						objectFields[0].name + " == 'Entry with condition'",
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await page.getByRole('link', {name: 'Custom Action'}).click();

			const iframe = editObjectActionPage.iframeLocator;

			await editObjectActionPage.openActionBuilderTab();

			await expect(iframe.getByLabel('Enable Condition')).toBeChecked();

			await iframe.getByLabel('Enable Condition').uncheck();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await page.waitForLoadState('networkidle');

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(
				page
					.getByRole('row', {name: 'Custom Action'})
					.getByRole('cell', {name: 'Yes'})
			).toBeVisible();
		}
	);

	test(
		'Can trigger action after disabling expression condition',
		{tag: ['@LPD-78504', '@LPS-156343']},
		async ({apiHelpers}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const predefinedValue = `New object entry value ${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression: `${fieldName} == 'Entry Test'`,
					label: {en_US: 'Custom Action Label'},
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: predefinedValue,
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const objectActionAPIClient =
				await apiHelpers.buildRestClient(ObjectActionAPI);

			await objectActionAPIClient.patchObjectAction(objectAction.id!, {
				conditionExpression: '',
			});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Different Value'},
				applicationName
			);

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const autoCreatedEntry = entries.items.find(
				(item: any) => item[fieldName] === predefinedValue
			);

			expect(autoCreatedEntry).toBeTruthy();
		}
	);

	test(
		'Can trigger action with expression by adding an entry',
		{tag: ['@LPD-78504', '@LPS-156320']},
		async ({apiHelpers}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const predefinedValue = `Add Entry Test ${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression: `${fieldName} == 'Entry Test'`,
					label: {en_US: 'Action Label'},
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: predefinedValue,
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry Test'},
				applicationName
			);

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const autoCreatedEntry = entries.items.find(
				(item: any) => item[fieldName] === predefinedValue
			);

			expect(autoCreatedEntry).toBeTruthy();
		}
	);

	test(
		'Can use expression with webhook action',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
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

			const actionName = 'action' + getRandomInt();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						objectFields[0].name + " == 'Entry Test'",
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'webhook',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						url: liferayConfig.environment.baseUrl,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';
			const fieldName = objectFields[0].name!;

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry Test'},
				applicationName
			);

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await expect(
				page.getByRole('link', {name: 'Action Label'})
			).toBeVisible();

			await expect(page.getByRole('cell', {name: 'Yes'})).toBeVisible();
		}
	);

	test(
		'Can verify condition card is hidden when using on subscription status update trigger',
		{tag: '@LPD-78504'},
		async ({editObjectActionPage, viewObjectActionsPage}) => {
			await viewObjectActionsPage.goto('Commerce Order');

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await expect(iframe.getByLabel('Enable Condition')).toBeVisible();

			await editObjectActionPage.inputWhenCombo.click();

			await iframe
				.getByRole('option', {name: 'On Subscription Status Update'})
				.click();

			await expect(iframe.getByLabel('Enable Condition')).toBeHidden();
		}
	);
});

test.describe('Object Action Cross-Object Behavior', () => {
	test(
		'Can add account entry after creating account entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const targetAccountName = `Account Name ${getRandomInt()}`;

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_ACCOUNT',
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Name'},
								name: 'name',
								value: targetAccountName,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Type'},
								name: 'type',
								value: 'person',
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.headlessAdminUser.postAccount({
				name: `Business Account ${getRandomInt()}`,
				type: 'business',
			});

			const createdAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					targetAccountName
				);

			if (createdAccount) {
				apiHelpers.data.push({id: createdAccount.id!, type: 'account'});
			}

			expect(createdAccount).toBeTruthy();
		}
	);

	test(
		'Can add account entry after creating custom object entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const targetAccountName = `Account Name ${getRandomInt()}`;

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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Name'},
								name: 'name',
								value: targetAccountName,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Type'},
								name: 'type',
								value: 'guest',
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[objectFields[0].name!]: 'Entry Test'},
				applicationName
			);

			const createdAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					targetAccountName
				);

			if (createdAccount) {
				apiHelpers.data.push({id: createdAccount.id!, type: 'account'});
			}

			expect(createdAccount).toBeTruthy();
		}
	);

	test(
		'Can add account entry after deleting custom object entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const targetAccountName = `Account Name ${getRandomInt()}`;

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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterDelete',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Name'},
								name: 'name',
								value: targetAccountName,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Type'},
								name: 'type',
								value: 'guest',
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{[objectFields[0].name!]: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(entry.id)
			);

			const createdAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					targetAccountName
				);

			if (createdAccount) {
				apiHelpers.data.push({id: createdAccount.id!, type: 'account'});
			}

			expect(createdAccount).toBeTruthy();
		}
	);

	test(
		'Can add account entry after updating custom object entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const targetAccountName = `Account Name ${getRandomInt()}`;

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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Name'},
								name: 'name',
								value: targetAccountName,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Type'},
								name: 'type',
								value: 'guest',
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const fieldName = objectFields[0].name!;

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{[fieldName]: 'Entry Test Edited'},
				applicationName,
				Number(entry.id)
			);

			const createdAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					targetAccountName
				);

			if (createdAccount) {
				apiHelpers.data.push({id: createdAccount.id!, type: 'account'});
			}

			expect(createdAccount).toBeTruthy();
		}
	);

	test(
		'Can add commerce product group entry after deleting commerce product entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const targetGroupName = `Value Test ${getRandomInt()}`;

			const {catalog} = await miniumSetUp(apiHelpers);

			const product =
				await apiHelpers.headlessCommerceAdminCatalog.postProduct({
					catalogId: catalog.id,
					name: {en_US: `Simple T-Shirt ${getRandomInt()}`},
					productType: 'simple',
				});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_COMMERCE_PRODUCT_DEFINITION',
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterDelete',
					parameters: {
						objectDefinitionExternalReferenceCode:
							'L_COMMERCE_PRODUCT_GROUP',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Title'},
								name: 'title',
								value: targetGroupName,
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.headlessCommerceAdminCatalog.deleteProduct(
				product.productId!
			);

			const productGroups =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					'headless-commerce-admin-catalog/v1.0/product-groups'
				);

			const createdGroup = productGroups.items.find(
				(item: any) => item.title?.en_US === targetGroupName
			);

			expect(createdGroup).toBeTruthy();

			if (createdGroup) {
				apiHelpers.data.push({
					id: createdGroup.id,
					type: 'objectEntry',
				});
			}
		}
	);

	test(
		'Can add user after creating commerce product entry via action',
		{tag: ['@LPD-78504', '@LPS-180070']},
		async ({apiHelpers}) => {
			const newUserAlternateName = `newusertest${getRandomInt()}`;
			const newUserEmail = `${newUserAlternateName}@test.com`;

			const {catalog} = await miniumSetUp(apiHelpers);

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_COMMERCE_PRODUCT_DEFINITION',
				{
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_USER',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Screen Name'},
								name: 'alternateName',
								value: newUserAlternateName,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Email Address'},
								name: 'emailAddress',
								value: newUserEmail,
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'First Name'},
								name: 'givenName',
								value: 'newUserTest',
							},
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Last Name'},
								name: 'familyName',
								value: 'newUserTest',
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.headlessCommerceAdminCatalog.postProduct({
				catalogId: catalog.id,
				name: {en_US: `Simple T-Shirt ${getRandomInt()}`},
				productType: 'simple',
			});

			const createdUser =
				await apiHelpers.headlessAdminUser.getUserAccountByEmailAddress(
					newUserEmail
				);

			expect(createdUser).toBeTruthy();

			if (createdUser?.id) {
				apiHelpers.data.push({id: createdUser.id, type: 'userAccount'});
			}
		}
	);

	test(
		'Can update account entry after creating account entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const initialAccountName = `Account Name ${getRandomInt()}`;
			const updatedAccountName = `Updated Accounts Name ${getRandomInt()}`;

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_ACCOUNT',
				{
					objectActionExecutorKey: 'update-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode: 'L_ACCOUNT',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Name'},
								name: 'name',
								value: updatedAccountName,
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.headlessAdminUser.postAccount({
				name: initialAccountName,
				type: 'person',
			});

			const updatedAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					updatedAccountName
				);

			expect(updatedAccount).toBeTruthy();

			const oldAccount =
				await apiHelpers.headlessAdminUser.getAccountByName(
					initialAccountName
				);

			expect(oldAccount).toBeFalsy();
		}
	);

	test(
		'Can update commerce product group entry after creating commerce product entry via action',
		{tag: ['@LPD-78504', '@LPS-173537']},
		async ({apiHelpers}) => {
			const updatedTitle = `Product Group Updated ${getRandomInt()}`;

			await miniumSetUp(apiHelpers);

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_COMMERCE_PRODUCT_GROUP',
				{
					objectActionExecutorKey: 'update-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							'L_COMMERCE_PRODUCT_GROUP',
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Title'},
								name: 'title',
								value: updatedTitle,
							},
						],
						system: true,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const productGroup = await apiHelpers.objectEntry.postObjectEntry(
				{
					title: {en_US: 'Original Title'},
				},
				'headless-commerce-admin-catalog/v1.0/product-groups'
			);

			apiHelpers.data.push({id: productGroup.id, type: 'objectEntry'});

			await expect(async () => {
				const updatedGroup =
					await apiHelpers.objectEntry.getObjectEntryById(
						'headless-commerce-admin-catalog/v1.0/product-groups',
						String(productGroup.id)
					);

				expect(updatedGroup.title?.en_US).toBe(updatedTitle);
			}).toPass();
		}
	);
});

test.describe('Object Action Required Field Validation', () => {
	test(
		'Can verify action name is required',
		{tag: '@LPD-78504'},
		async ({apiHelpers, page, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = page.frameLocator('iframe');

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(
				iframe.locator('#actionNameInputfieldFeedback')
			).toContainText('Required');
		}
	);

	test(
		'Cannot leave action name blank',
		{tag: '@LPD-78504'},
		async ({apiHelpers, editObjectActionPage, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe
				.getByLabel('URL')
				.fill(liferayConfig.environment.baseUrl);

			await iframe.getByRole('tab', {name: 'Basic Info'}).click();

			await iframe.getByPlaceholder('Text to translate').clear();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(
				iframe.locator('#actionNameInputfieldFeedback')
			).toContainText('Required');
		}
	);

	test(
		'Cannot leave action then field blank',
		{tag: '@LPD-78504'},
		async ({apiHelpers, editObjectActionPage, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(iframe.getByText('Required')).toBeVisible();
		}
	);

	test(
		'Cannot leave action when field blank',
		{tag: '@LPD-78504'},
		async ({apiHelpers, editObjectActionPage, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe
				.getByLabel('URL')
				.fill(liferayConfig.environment.baseUrl);

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(iframe.getByText('Required')).toBeVisible();
		}
	);

	test(
		'Cannot leave URL blank when webhook is selected',
		{tag: '@LPD-78504'},
		async ({apiHelpers, editObjectActionPage, viewObjectActionsPage}) => {
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

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Action Label');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(
				iframe.locator('#urlInputfieldFeedback')
			).toContainText('Required');
		}
	);

	test(
		'Cannot save action without expression builder value',
		{tag: '@LPD-78504'},
		async ({
			apiHelpers,
			editObjectActionPage,
			page,
			viewObjectActionsPage,
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

			await viewObjectActionsPage.gotoByObjectDefinitionId(
				objectDefinition.id
			);

			await viewObjectActionsPage.openObjectActionSidePanel();

			const iframe = editObjectActionPage.iframeLocator;

			await iframe
				.getByPlaceholder('Text to translate')
				.fill('Custom Action');

			await editObjectActionPage.openActionBuilderTab();

			await editObjectActionPage.inputWhenCombo.click();
			await iframe.getByRole('option', {name: 'On After Add'}).click();

			await iframe.getByLabel('Enable Condition').check();

			await editObjectActionPage.inputThenCombo.click();
			await iframe.getByRole('option', {name: 'Webhook'}).click();

			await iframe
				.getByLabel('URL')
				.fill(liferayConfig.environment.baseUrl);

			await iframe.getByRole('button', {name: 'Save'}).click();

			await expect(iframe.getByText('Required')).toBeVisible();

			await page.reload();

			await viewObjectActionsPage.actionsTabItem.click();

			await expect(page.getByText('No Results Found')).toBeVisible();
		}
	);
});

test.describe('Object Action Standalone Permissions', () => {
	test(
		'Can manage standalone permissions in roles',
		{tag: ['@LPD-78504', '@LPS-173723']},
		async ({
			apiHelpers,
			roleDefinePermissionsPage,
			rolePage,
			rolesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const actionName = `ActionName${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					errorMessage: {en_US: 'Error Message'},
					label: {en_US: 'Action Label'},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'standalone',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: 'New Entry Test',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const roleName = `Regular Role ${getRandomInt()}`;

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: roleName,
			});

			apiHelpers.data.push({id: role.id, type: 'role'});

			await rolesPage.goto();

			await rolesPage.selectRole(roleName);

			await rolePage.definePermissionsLink.click();

			const objectName = objectDefinition.name!;
			const actionPermissionName = `action.${actionName}`;

			await roleDefinePermissionsPage.changePermission(
				objectName,
				actionPermissionName,
				true
			);

			await expect(
				roleDefinePermissionsPage.page.getByText(actionPermissionName)
			).toBeVisible();

			await roleDefinePermissionsPage.changePermission(
				objectName,
				actionPermissionName,
				false
			);

			await expect(
				roleDefinePermissionsPage.page.getByText(actionPermissionName)
			).toBeHidden();
		}
	);

	test(
		'Can trigger standalone action for site scoped object',
		{tag: ['@LPD-78504', '@LPS-172918']},
		async ({apiHelpers, page, site, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const actionLabel = `Action ${getRandomInt()}`;
			const predefinedValue = `New Entry Test ${getRandomInt()}`;

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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					errorMessage: {en_US: 'Error Message'},
					label: {en_US: actionLabel},
					name: `ActionName${getRandomInt()}`,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'standalone',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: predefinedValue,
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry'},
				applicationName,
				String(site.id)
			);

			await viewObjectEntriesPage.goto(
				objectDefinition.className!,
				'en',
				site.friendlyUrlPath
			);

			await viewObjectEntriesPage.frontendDatasetActions.click();

			const executeResponsePromise = page.waitForResponse(
				(response) =>
					response
						.url()
						.includes(`/object-actions/${objectAction.name}`) &&
					response.request().method() === 'PUT' &&
					response.ok()
			);

			await page.getByRole('menuitem', {name: actionLabel}).click();

			await executeResponsePromise;

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntriesByScope(
					applicationName,
					String(site.id)
				);

			const autoCreatedEntry = entries.items.find(
				(item: any) => item[fieldName] === predefinedValue
			);

			expect(autoCreatedEntry).toBeTruthy();
		}
	);

	test(
		'Can trigger standalone action with permission',
		{tag: ['@LPD-78504', '@LPS-169994']},
		async ({
			apiHelpers,
			page,
			roleDefinePermissionsPage,
			rolePage,
			rolesPage,
			viewObjectEntriesPage,
		}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const actionLabel = `Action Label ${getRandomInt()}`;
			const actionName = `ActionName${getRandomInt()}`;
			const predefinedValue = `New Entry Test ${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					errorMessage: {en_US: 'Error Message'},
					label: {en_US: actionLabel},
					name: actionName,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'standalone',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: predefinedValue,
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry Test'},
				applicationName
			);

			const company =
				await apiHelpers.jsonWebServicesCompany.getCompanyByWebId(
					'liferay.com'
				);

			const [, classNameSuffix] = objectDefinition.className!.split('#');

			const objectPortletId = `com_liferay_object_web_internal_object_definitions_portlet_ObjectDefinitionsPortlet_${classNameSuffix}`;

			const roleName = `Regular Role ${getRandomInt()}`;

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: roleName,
				rolePermissions: [
					{
						actionIds: ['VIEW_CONTROL_PANEL'],
						primaryKey: String(company.companyId),
						resourceName: '90',
						scope: 1,
					},
					{
						actionIds: ['ACCESS_IN_CONTROL_PANEL', 'VIEW'],
						primaryKey: String(company.companyId),
						resourceName: objectPortletId,
						scope: 1,
					},
					{
						actionIds: ['VIEW'],
						primaryKey: String(company.companyId),
						resourceName: `com.liferay.object.model.ObjectDefinition#${classNameSuffix}`,
						scope: 1,
					},
				],
			});

			apiHelpers.data.push({id: role.id, type: 'role'});

			await rolesPage.goto();

			await rolesPage.selectRole(roleName);

			await rolePage.definePermissionsLink.click();

			await roleDefinePermissionsPage.changePermission(
				objectDefinition.name!,
				`action.${actionName}`,
				true
			);

			const user = await apiHelpers.headlessAdminUser.postUserAccount();

			userData[user.alternateName] = {
				name: user.givenName,
				password: 'test',
				surname: user.familyName,
			};

			await apiHelpers.headlessAdminUser.assignUserToRole(
				role.externalReferenceCode,
				user.id
			);

			await performLogout(page);

			await performLoginViaApi({page, screenName: user.alternateName});

			await viewObjectEntriesPage.goto(objectDefinition.className!);

			await viewObjectEntriesPage.frontendDatasetActions.click();

			const executeResponsePromise = page.waitForResponse(
				(response) =>
					response.url().includes(`/object-actions/${actionName}`) &&
					response.request().method() === 'PUT' &&
					response.ok()
			);

			await page.getByRole('menuitem', {name: actionLabel}).click();

			await executeResponsePromise;

			await performLogout(page);

			await performLoginViaApi({page, screenName: 'test'});

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const autoCreatedEntry = entries.items.find(
				(item: any) => item[fieldName] === predefinedValue
			);

			expect(autoCreatedEntry).toBeTruthy();
		}
	);

	test(
		'Can verify unpublished object with standalone action does not show in permissions',
		{tag: ['@LPD-78504', '@LPS-173774']},
		async ({
			apiHelpers,
			roleDefinePermissionsPage,
			rolePage,
			rolesPage,
		}) => {
			const publishedObject =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: generateObjectFields({
						objectFieldBusinessTypes: ['Text'],
					}),
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: publishedObject.id,
				type: 'objectDefinition',
			});

			const unpublishedObjectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const unpublishedObject =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: unpublishedObjectFields,
					status: {code: 2},
				});

			apiHelpers.data.push({
				id: unpublishedObject.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				unpublishedObject.externalReferenceCode!,
				{
					errorMessage: {en_US: 'Error'},
					label: {en_US: 'Action Label'},
					name: `ActionName${getRandomInt()}`,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'standalone',
					parameters: {
						objectDefinitionExternalReferenceCode:
							unpublishedObject.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: unpublishedObjectFields[0].label!,
								name: unpublishedObjectFields[0].name!,
								value: 'Test',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const roleName = `Regular Role ${getRandomInt()}`;

			const role = await apiHelpers.headlessAdminUser.postRole({
				name: roleName,
			});

			apiHelpers.data.push({id: role.id, type: 'role'});

			await rolesPage.goto();

			await rolesPage.selectRole(roleName);

			await rolePage.definePermissionsLink.click();

			await roleDefinePermissionsPage.searchInput.click();
			await roleDefinePermissionsPage.searchInput.fill(
				publishedObject.name!
			);

			await expect(
				roleDefinePermissionsPage.menuItem(publishedObject.name!)
			).toBeVisible();

			await roleDefinePermissionsPage.searchInput.fill(
				unpublishedObject.name!
			);

			await expect(
				roleDefinePermissionsPage.menuItem(unpublishedObject.name!)
			).not.toBeVisible();
		}
	);

	test(
		'Cannot see deactivated standalone action in dropdown menu',
		{tag: ['@LPD-78504', '@LPS-173773']},
		async ({apiHelpers, page, viewObjectEntriesPage}) => {
			const objectFields = generateObjectFields({
				objectFieldBusinessTypes: ['Text'],
			});

			const fieldName = objectFields[0].name!;
			const fieldLabel = objectFields[0].label!;
			const actionLabel = `Action Label ${getRandomInt()}`;

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields,
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					active: false,
					errorMessage: {en_US: 'Error message'},
					label: {en_US: actionLabel},
					name: `ActionName${getRandomInt()}`,
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'standalone',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: fieldLabel,
								name: fieldName,
								value: 'New Entry Test',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{[fieldName]: 'Entry Test'},
				applicationName
			);

			await viewObjectEntriesPage.goto(objectDefinition.className!);

			await viewObjectEntriesPage.frontendDatasetActions.click();

			await expect(
				page.getByRole('menuitem', {name: actionLabel})
			).toBeHidden();
		}
	);
});

test(
	'Can trigger email notification action on entry add',
	{tag: ['@LPD-78504', '@LPS-176850']},
	async ({apiHelpers}) => {
		const senderEmail = `sender${getRandomInt()}@liferay.com`;

		const notificationTemplate =
			await apiHelpers.notification.postRandomNotificationTemplate(
				`Notification Template ${getRandomInt()}`,
				senderEmail
			);

		apiHelpers.data.push({
			id: notificationTemplate.id,
			type: 'notificationTemplate',
		});

		const objectDefinition =
			await apiHelpers.objectAdmin.postRandomObjectDefinition({
				objectFields: [
					{
						DBType: 'Integer',
						businessType: 'Integer',
						externalReferenceCode: 'customInteger',
						indexed: true,
						indexedAsKeyword: false,
						indexedLanguageId: '',
						label: {en_US: 'Custom Integer'},
						listTypeDefinitionId: 0,
						localized: false,
						name: 'customInteger',
						required: false,
						system: false,
						type: 'Integer',
					},
				],
				status: {code: 0},
			});

		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});

		const objectAction = await apiHelpers.objectAction.postRandomAction(
			objectDefinition.externalReferenceCode!,
			{
				label: {
					en_US: 'Notification action when entry is added',
				},
				name: `notifyOnAdd${getRandomInt()}`,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterAdd',
				parameters: {
					notificationTemplateId: notificationTemplate.id,
					type: 'email',
				},
			}
		);

		apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

		const applicationName =
			'c/' + objectDefinition.name!.toLowerCase() + 's';

		await apiHelpers.objectEntry.postObjectEntry(
			{customInteger: 1111},
			applicationName
		);

		// The notification action runs asynchronously after the entry is
		// committed, so poll until the queue is populated.

		let notificationQueueEntries: {items: {id: number}[]};

		await expect(async () => {
			notificationQueueEntries =
				await apiHelpers.notification.getNotificationQueueEntriesPage(
					senderEmail
				);

			expect(notificationQueueEntries.items.length).toBeGreaterThan(0);
		}).toPass();

		// Register the queue entries for teardown cleanup.

		for (const item of notificationQueueEntries!.items) {
			apiHelpers.data.push({
				id: item.id,
				type: 'notificationQueueEntry',
			});
		}
	}
);

test.describe('Object Action with Groovy Script', () => {
	const groovyScript = `
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;

import java.io.Serializable;
import java.util.Map;

ObjectEntry objectEntry = ObjectEntryLocalServiceUtil.getObjectEntry(id);
Map<String, Serializable> values = objectEntry.getValues();
values.put("customObjectFieldActionTest", "Action Test Works")
ObjectEntryLocalServiceUtil.updateObjectEntry(objectEntry.getUserId(), id, 0L, values, new ServiceContext());
`;

	test(
		'Can create an action with Groovy Script',
		{tag: ['@LPD-78504', '@LPS-156569']},
		async ({apiHelpers, scriptManagementPage}) => {
			await scriptManagementPage.enableScriptManagementConfiguration();

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customObjectField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectField',
							required: true,
							system: false,
							type: 'String',
						},
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode:
								'customObjectFieldActionTest',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field Action Test'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectFieldActionTest',
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					errorMessage: {en_US: ''},
					objectActionExecutorKey: 'groovy',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						lineCount: groovyScript.split('\n').length,
						script: groovyScript,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customObjectField: 'Entry Test'},
				applicationName
			);

			// The Groovy executor runs asynchronously after the entry is
			// committed, so poll until the action's effect (updated
			// customObjectFieldActionTest) is visible.

			await expect(async () => {
				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						applicationName,
						String(entry.id)
					);

				expect(updatedEntry.customObjectFieldActionTest).toBe(
					'Action Test Works'
				);
			}).toPass();
		}
	);

	test(
		'Can edit an action with Groovy Script',
		{tag: ['@LPD-78504', '@LPS-156560']},
		async ({apiHelpers, scriptManagementPage}) => {
			await scriptManagementPage.enableScriptManagementConfiguration();

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customObjectField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectField',
							required: true,
							system: false,
							type: 'String',
						},
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode:
								'customObjectFieldActionTest',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field Action Test'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectFieldActionTest',
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					errorMessage: {en_US: ''},
					objectActionExecutorKey: 'groovy',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						lineCount: groovyScript.split('\n').length,
						script: groovyScript,
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const objectActionAPIClient =
				await apiHelpers.buildRestClient(ObjectActionAPI);

			await objectActionAPIClient.patchObjectAction(objectAction.id!, {
				conditionExpression: "customObjectField == 'Entry Update'",
			});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customObjectField: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{customObjectField: 'Entry Update'},
				applicationName,
				Number(entry.id)
			);

			// The Groovy executor runs asynchronously after the entry is committed,
			// so poll until the action's effect (updated customObjectFieldActionTest)
			// is visible.

			await expect(async () => {
				const updatedEntry =
					await apiHelpers.objectEntry.getObjectEntryById(
						applicationName,
						String(entry.id)
					);

				expect(updatedEntry.customObjectFieldActionTest).toBe(
					'Action Test Works'
				);
			}).toPass();
		}
	);

	test(
		'Can run a Groovy Script action on an object with a formula field',
		{tag: '@LPD-102828'},
		async ({
			apiHelpers,
			page,
			scriptManagementPage,
			viewObjectActionsPage,
		}) => {
			await scriptManagementPage.enableScriptManagementConfiguration();

			const {firstObjectField, objectFields, secondObjectField} =
				generateFormulaObjectFields({
					objectFieldBusinessType: 'Integer',
					operator: '-',
					output: 'Integer',
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

			const objectActionLabel = getRandomString();

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					label: {en_US: objectActionLabel},
					objectActionExecutorKey: 'groovy',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						lineCount: 1,
						script: "println 'Success'",
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			await apiHelpers.objectEntry.postObjectEntry(
				{
					[firstObjectField.name as string]: 8,
					[secondObjectField.name as string]: 4,
				},
				'c/' + objectDefinition.name.toLowerCase() + 's'
			);

			// The Groovy executor runs asynchronously after the entry is
			// committed, so poll the actions page until the action's last
			// execution status reflects 'Success'.

			await expect(async () => {
				await viewObjectActionsPage.goto(
					objectDefinition.label['en_US']
				);

				await expect(
					page.getByRole('row').filter({hasText: objectActionLabel})
				).toContainText('Success');
			}).toPass();
		}
	);

	test(
		'Can use expression with Groovy Script action',
		{tag: ['@LPD-78504', '@LPS-156346']},
		async ({apiHelpers, scriptManagementPage, viewObjectActionsPage}) => {
			await scriptManagementPage.enableScriptManagementConfiguration();

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'Integer',
							businessType: 'Integer',
							externalReferenceCode: 'customInteger',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Integer'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customInteger',
							required: false,
							system: false,
							type: 'Integer',
						},
					],
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinition.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression: 'customInteger == 5',
					errorMessage: {en_US: ''},
					label: {en_US: 'Action Label'},
					objectActionExecutorKey: 'groovy',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						lineCount: 1,
						script: "println 'Success'",
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{customInteger: 5},
				applicationName
			);

			// The Groovy executor runs asynchronously after the entry is
			// committed, so poll the actions page until the action's last
			// execution status reflects 'Success'.

			await expect(async () => {
				await viewObjectActionsPage.gotoByObjectDefinitionId(
					objectDefinition.id
				);

				await expect(
					viewObjectActionsPage.lastExecutionCell.nth(1)
				).toContainText('Success');
			}).toPass();
		}
	);
});

test.describe('Object Action with oldValue Function', () => {
	test(
		'Can create action Add an Object Entry using oldValue with On After Delete trigger',
		{tag: '@LPD-78504'},
		async ({apiHelpers}) => {
			const suffix = getRandomString().substring(0, 8);

			const objectDefinitionA =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `ObjA${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customObjectField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectField',
							required: false,
							system: false,
							type: 'String',
						},
					],
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinitionA.id,
				type: 'objectDefinition',
			});

			const objectDefinitionB =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `ObjB${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customObjectField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customObjectField',
							required: false,
							system: false,
							type: 'String',
						},
					],
					status: {code: 0},
				});

			apiHelpers.data.push({
				id: objectDefinitionB.id,
				type: 'objectDefinition',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinitionA.externalReferenceCode!,
				{
					conditionExpression:
						"oldValue('customObjectField') == 'Entry Test'",
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterDelete',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinitionB.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Custom Field'},
								name: 'customObjectField',
								value: 'Works!',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationNameA =
				'c/' + objectDefinitionA.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customObjectField: 'Entry Test'},
				applicationNameA
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationNameA,
				String(entry.id)
			);

			const applicationNameB =
				'c/' + objectDefinitionB.name!.toLowerCase() + 's';

			const entriesB =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationNameB
				);

			expect(entriesB.items.length).toBeGreaterThanOrEqual(1);

			const addedEntry = entriesB.items.find(
				(item: ObjectEntry) => item.customObjectField === 'Works!'
			);

			expect(addedEntry).toBeTruthy();
		}
	);

	test(
		'Can create action Add an Object Entry using oldValue with On After Update trigger and Picklist field',
		{tag: '@LPD-78504'},
		async ({apiHelpers}) => {
			const suffix = getRandomString().substring(0, 8);

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'open',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Open'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'inprogress',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'In Progress'},
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `Obj${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customTextField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Text Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customTextField',
							required: true,
							system: false,
							type: 'String',
						},
						{
							DBType: 'String',
							businessType: 'Picklist',
							externalReferenceCode: 'customPicklistField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Picklist Field'},
							listTypeDefinitionExternalReferenceCode:
								listTypeDefinition.externalReferenceCode,
							listTypeDefinitionId: listTypeDefinition.id,
							localized: false,
							name: 'customPicklistField',
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						"oldValue('customPicklistField') == 'open'",
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Custom Text Field'},
								name: 'customTextField',
								value: 'Object entry added',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customTextField: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{customPicklistField: {key: 'open', name: 'Open'}},
				applicationName,
				entry.id
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{customPicklistField: {key: 'inprogress', name: 'In Progress'}},
				applicationName,
				entry.id
			);

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const addedEntry = entries.items.find(
				(item: ObjectEntry) =>
					item.customTextField === 'Object entry added'
			);

			expect(addedEntry).toBeTruthy();
		}
	);

	test(
		'Can create action Notification using oldValue with On After Add trigger',
		{tag: ['@LPD-78504', '@LPS-175197']},
		async ({apiHelpers}) => {
			const senderEmail = `sender${getRandomInt()}@liferay.com`;

			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					`Email Template ${getRandomInt()}`,
					senderEmail
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression: 'isEmpty(oldValue("customField"))',
					label: {
						en_US: 'Notification action when entry is added',
					},
					name: 'notifyOnAdd',
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{customField: 'Entry Test'},
				applicationName
			);

			// The notification action runs asynchronously after the entry is
			// committed, so poll until the queue is populated.

			let notificationQueueEntries: {items: {id: number}[]};

			await expect(async () => {
				notificationQueueEntries =
					await apiHelpers.notification.getNotificationQueueEntriesPage(
						senderEmail
					);

				expect(notificationQueueEntries.items.length).toBeGreaterThan(
					0
				);
			}).toPass();

			// Register the queue entries for teardown cleanup.

			for (const item of notificationQueueEntries!.items) {
				apiHelpers.data.push({
					id: item.id,
					type: 'notificationQueueEntry',
				});
			}
		}
	);

	test(
		'Can create action Notification using oldValue with On After Delete trigger',
		{tag: ['@LPD-78504', '@LPS-175192']},
		async ({apiHelpers}) => {
			const senderEmail = `sender${getRandomInt()}@liferay.com`;

			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					`Email Template ${getRandomInt()}`,
					senderEmail
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						'oldValue("customField") == "Entry Test"',
					label: {
						en_US: 'Notification action when entry is deleted',
					},
					name: 'notifyOnDelete',
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterDelete',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customField: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.deleteObjectEntry(
				applicationName,
				String(entry.id)
			);

			// The notification action runs asynchronously after the entry is
			// committed, so poll until the queue is populated.

			let notificationQueueEntries: {items: {id: number}[]};

			await expect(async () => {
				notificationQueueEntries =
					await apiHelpers.notification.getNotificationQueueEntriesPage(
						senderEmail
					);

				expect(notificationQueueEntries.items.length).toBeGreaterThan(
					0
				);
			}).toPass();

			// Register the queue entries for teardown cleanup.

			for (const item of notificationQueueEntries!.items) {
				apiHelpers.data.push({
					id: item.id,
					type: 'notificationQueueEntry',
				});
			}
		}
	);

	test(
		'Can create action Notification using oldValue with On After Update trigger',
		{tag: ['@LPD-78504', '@LPS-175191']},
		async ({apiHelpers}) => {
			const senderEmail = `sender${getRandomInt()}@liferay.com`;

			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					`Email Template ${getRandomInt()}`,
					senderEmail
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						'oldValue("customField") == "Entry Test"',
					label: {
						en_US: 'Notification action when entry is updated',
					},
					name: 'notifyOnUpdate',
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customField: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{customField: 'Entry Test Edited'},
				applicationName,
				Number(entry.id)
			);

			// The notification action runs asynchronously after the entry is
			// committed, so poll until the queue is populated.

			let notificationQueueEntries: {items: {id: number}[]};

			await expect(async () => {
				notificationQueueEntries =
					await apiHelpers.notification.getNotificationQueueEntriesPage(
						senderEmail
					);

				expect(notificationQueueEntries.items.length).toBeGreaterThan(
					0
				);
			}).toPass();

			// Register the queue entries for teardown cleanup.

			for (const item of notificationQueueEntries!.items) {
				apiHelpers.data.push({
					id: item.id,
					type: 'notificationQueueEntry',
				});
			}
		}
	);

	test(
		'Can create action Notification using oldValue with On After Update trigger on Account Object',
		{tag: ['@LPD-78504', '@LPS-178410']},
		async ({apiHelpers}) => {
			const senderEmail = `sender${getRandomInt()}@liferay.com`;
			const accountName = `Accounts Name Test ${getRandomInt()}`;

			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					`Email Template ${getRandomInt()}`,
					senderEmail
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_ACCOUNT',
				{
					conditionExpression: `oldValue("name") == "${accountName}"`,
					label: {
						en_US: 'Notification action when account is updated',
					},
					name: `notifyOnAccountUpdate${getRandomInt()}`,
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const account = await apiHelpers.headlessAdminUser.postAccount({
				name: accountName,
				type: 'person',
			});

			await apiHelpers.patch(
				`${apiHelpers.baseUrl}headless-admin-user/v1.0/accounts/${account.id}`,
				{name: `${accountName} Edited`}
			);

			// The notification action runs asynchronously after the entry is
			// committed, so poll until the queue is populated.

			let notificationQueueEntries: {items: {id: number}[]};

			await expect(async () => {
				notificationQueueEntries =
					await apiHelpers.notification.getNotificationQueueEntriesPage(
						senderEmail
					);

				expect(notificationQueueEntries.items.length).toBeGreaterThan(
					0
				);
			}).toPass();

			// Register the queue entries for teardown cleanup.

			for (const item of notificationQueueEntries!.items) {
				apiHelpers.data.push({
					id: item.id,
					type: 'notificationQueueEntry',
				});
			}
		}
	);

	test(
		'Can create action Notification using oldValue with On After Update trigger on User Object',
		{tag: ['@LPD-78504', '@LPS-178415']},
		async ({apiHelpers}) => {
			const senderEmail = `sender${getRandomInt()}@liferay.com`;
			const givenName = `userfn${getRandomInt()}`;

			const notificationTemplate =
				await apiHelpers.notification.postRandomNotificationTemplate(
					`Email Template ${getRandomInt()}`,
					senderEmail
				);

			apiHelpers.data.push({
				id: notificationTemplate.id,
				type: 'notificationTemplate',
			});

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				'L_USER',
				{
					conditionExpression: `oldValue("givenName") == "${givenName}"`,
					label: {
						en_US: 'Notification action when first name is edited',
					},
					name: `notifyOnUserUpdate${getRandomInt()}`,
					objectActionExecutorKey: 'notification',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						notificationTemplateId: notificationTemplate.id,
						type: 'email',
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const userAccount =
				await apiHelpers.headlessAdminUser.postUserAccount({
					givenName,
				});

			await apiHelpers.headlessAdminUser.patchUserAccount(userAccount, {
				givenName: `edit${givenName}`,
			});

			// The notification action runs asynchronously after the entry is
			// committed, so poll until the queue is populated.

			let notificationQueueEntries: {items: {id: number}[]};

			await expect(async () => {
				notificationQueueEntries =
					await apiHelpers.notification.getNotificationQueueEntriesPage(
						senderEmail
					);

				expect(notificationQueueEntries.items.length).toBeGreaterThan(
					0
				);
			}).toPass();

			// Register the queue entries for teardown cleanup.

			for (const item of notificationQueueEntries!.items) {
				apiHelpers.data.push({
					id: item.id,
					type: 'notificationQueueEntry',
				});
			}
		}
	);

	test(
		'Can create action Update an Object Entry using oldValue with On After Update trigger',
		{tag: '@LPD-78504'},
		async ({apiHelpers}) => {
			const suffix = getRandomString().substring(0, 8);

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `Obj${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Field'},
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						'oldValue("customField") == "Entry Test"',
					objectActionExecutorKey: 'update-object-entry',
					objectActionTriggerKey: 'onAfterUpdate',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Custom Field'},
								name: 'customField',
								value: 'Object entry updated',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			const entry = await apiHelpers.objectEntry.postObjectEntry(
				{customField: 'Entry Test'},
				applicationName
			);

			await apiHelpers.objectEntry.patchObjectEntry(
				{customField: 'Entry Test Edited'},
				applicationName,
				entry.id
			);

			const updatedEntry =
				await apiHelpers.objectEntry.getObjectEntryById(
					applicationName,
					String(entry.id)
				);

			expect(updatedEntry.customField).toBe('Object entry updated');
		}
	);

	test(
		'Can create action using oldValue with On After Add trigger and Picklist field',
		{tag: '@LPD-78504'},
		async ({apiHelpers}) => {
			const suffix = getRandomString().substring(0, 8);

			const listTypeDefinition =
				await apiHelpers.listTypeAdmin.postRandomListTypeDefinition();

			apiHelpers.data.push({
				id: listTypeDefinition.id,
				type: 'listTypeDefinition',
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'open',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Open'},
			});

			await apiHelpers.listTypeAdmin.postListTypeEntry({
				key: 'closed',
				listTypeDefinitionExternalReferenceCode:
					listTypeDefinition.externalReferenceCode,
				name_i18n: {en_US: 'Closed'},
			});

			const objectDefinition =
				await apiHelpers.objectAdmin.postRandomObjectDefinition({
					objectDefinitionExternalReferenceCode: `Obj${suffix}`,
					objectFields: [
						{
							DBType: 'String',
							businessType: 'Text',
							externalReferenceCode: 'customTextField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Text Field'},
							listTypeDefinitionId: 0,
							localized: false,
							name: 'customTextField',
							required: true,
							system: false,
							type: 'String',
						},
						{
							DBType: 'String',
							businessType: 'Picklist',
							externalReferenceCode: 'customPicklistField',
							indexed: true,
							indexedAsKeyword: false,
							indexedLanguageId: '',
							label: {en_US: 'Custom Picklist Field'},
							listTypeDefinitionExternalReferenceCode:
								listTypeDefinition.externalReferenceCode,
							listTypeDefinitionId: listTypeDefinition.id,
							localized: false,
							name: 'customPicklistField',
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

			const objectAction = await apiHelpers.objectAction.postRandomAction(
				objectDefinition.externalReferenceCode!,
				{
					conditionExpression:
						'isEmpty(oldValue("customPicklistField"))',
					objectActionExecutorKey: 'add-object-entry',
					objectActionTriggerKey: 'onAfterAdd',
					parameters: {
						objectDefinitionExternalReferenceCode:
							objectDefinition.externalReferenceCode,
						predefinedValues: [
							{
								businessType: 'Text',
								inputAsValue: true,
								label: {en_US: 'Custom Text Field'},
								name: 'customTextField',
								value: 'Object entry added',
							},
						],
					},
				}
			);

			apiHelpers.data.push({id: objectAction.id, type: 'objectAction'});

			const applicationName =
				'c/' + objectDefinition.name!.toLowerCase() + 's';

			await apiHelpers.objectEntry.postObjectEntry(
				{customTextField: 'Entry Test'},
				applicationName
			);

			const entries =
				await apiHelpers.objectEntry.getObjectDefinitionObjectEntries(
					applicationName
				);

			const addedEntry = entries.items.find(
				(item: ObjectEntry) =>
					item.customTextField === 'Object entry added'
			);

			expect(addedEntry).toBeTruthy();
		}
	);
});
