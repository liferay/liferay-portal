/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {validate} from '../components/EditNotificationTemplate';

const notificationTemplateMock: NotificationTemplate = {
	attachmentObjectFieldIds: [],
	body: {},
	description: '',
	editorType: 'richText',
	externalReferenceCode: '',
	name: 'name',
	objectDefinitionExternalReferenceCode: '',
	objectDefinitionId: null,
	recipientType: 'email',
	recipients: [
		{
			bcc: '',
			bccType: 'email',
			cc: '',
			ccType: 'email',
			from: 'user@liferay.com',
			fromName: {
				en_US: 'senderName',
			},
			singleRecipient: false,
			to: {
				en_US: 'user@liferay.com',
			},
			toType: 'email',
		},
	],
	subject: {
		['en_US']: 'subject',
	},
	system: false,
	type: 'email',
};

function generateModifiedNotificationTemplateMock(
	modifiedNotificationTemplateStructure: Partial<NotificationTemplate>
) {
	return {
		...notificationTemplateMock,
		...modifiedNotificationTemplateStructure,
	} as NotificationTemplate;
}

describe('Notification template: Validate required fields', () => {
	it('validates that name field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({name: ''});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			name: 'required',
		});
	});

	it('validates that email address field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						from: '',
					},
				],
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			from: 'required',
		});
	});

	it('validates that sender name field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						fromName: {
							['en_US']: '',
						},
					},
				],
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			fromName: 'required',
		});
	});

	it('validates that primary recipients field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						to: {
							['en_US']: '',
						},
					},
				],
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			to: 'required',
		});
	});

	it('validates that primary recipient roles field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						to: [],
					},
				],
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			to: 'required',
		});
	});

	it('validates that subject field is required', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				subject: {
					['en_US']: '',
				},
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			subject: 'required',
		});
	});

	it('validates that all required fields return an error', () => {
		const modifiedNotificationTemplateMock =
			generateModifiedNotificationTemplateMock({
				name: '',
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						from: '',
						fromName: {
							['en_US']: '',
						},
						to: {
							['en_US']: '',
						},
					},
				],
				subject: {
					['en_US']: '',
				},
			});

		const error = validate(modifiedNotificationTemplateMock);

		expect(error).toStrictEqual({
			from: 'required',
			fromName: 'required',
			name: 'required',
			subject: 'required',
			to: 'required',
		});
	});
});
