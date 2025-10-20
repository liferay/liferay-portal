/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import {render} from '@testing-library/react';

import {PrimaryRecipient} from '../components/SettingsContainer/PrimaryRecipients';

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

const renderPrimaryRecipient = (recipient?: Partial<EmailRecipients>) => {
	return render(
		<PrimaryRecipient
			errors={{}}
			learnResources={{}}
			recipientOptions={[]}
			roles={[]}
			selectedLocale="en_US"
			setValues={() => {}}
			userGroups={[]}
			values={{
				...notificationTemplateMock,
				recipients: [
					{
						...notificationTemplateMock.recipients[0],
						...recipient,
					},
				],
			}}
		/>
	);
};

describe('send emails separately: checkbox state', () => {
	it('is unchecked when single recipient is false', () => {
		const {getByRole} = renderPrimaryRecipient({singleRecipient: false});

		expect(
			getByRole('checkbox', {name: 'send-emails-separately'})
		).not.toBeChecked();
	});

	it('is checked when single recipient is true', () => {
		const {getByRole} = renderPrimaryRecipient({singleRecipient: true});

		expect(
			getByRole('checkbox', {name: 'send-emails-separately'})
		).toBeChecked();
	});

	it('is checked when single recipient is undefined', () => {
		const {getByRole} = renderPrimaryRecipient({
			singleRecipient: undefined,
		});

		expect(
			getByRole('checkbox', {name: 'send-emails-separately'})
		).toBeChecked();
	});
});
