/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {updateUsePreferredLanguageForGuestsParameter} from '../../components/ObjectAction/tabs/ActionContainer/updateUsePreferredLanguageForGuestsParameter';

const values: Partial<ObjectAction> = {
	label: {
		en_US: "Send email notifications in a guest user's",
	},
	name: 'sendEmailNotificationsInAGuestUsers',
	parameters: {
		notificationTemplateExternalReferenceCode:
			'28bb2e48-07ac-ac6b-85f7-da3329ebfdde',
	},
};

describe('updateUsePreferredLanguageForGuestsParameter should return true for UsePreferredLanguageForGuests', () => {
	it('when ussing onAfterAdd trigger', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterAdd',
			},
			'email'
		);

		expect(parameters).toStrictEqual({
			...values.parameters,
			usePreferredLanguageForGuests: true,
		});
	});

	it('when ussing onAfterUpdate trigger', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterUpdate',
			},
			'email'
		);

		expect(parameters).toStrictEqual({
			...values.parameters,
			usePreferredLanguageForGuests: true,
		});
	});
});

describe('updateUsePreferredLanguageForGuestsParameter should not return UsePreferredLanguageForGuests', () => {
	it('when ussing onAfterAttachmentDownload trigger', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterAttachmentDownload',
			},
			'email'
		);

		expect(parameters).toStrictEqual(values.parameters);
	});

	it('when ussing onAfterDelete trigger', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterDelete',
			},
			'email'
		);

		expect(parameters).toStrictEqual(values.parameters);
	});

	it('when ussing standalone trigger', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'standalone',
			},
			'email'
		);

		expect(parameters).toStrictEqual(values.parameters);
	});

	it('when ussing user notification template', () => {
		const parameters = updateUsePreferredLanguageForGuestsParameter(
			{
				...values,
				objectActionExecutorKey: 'notification',
				objectActionTriggerKey: 'onAfterAdd',
			},
			'userNotification'
		);

		expect(parameters).toStrictEqual(values.parameters);
	});
});
