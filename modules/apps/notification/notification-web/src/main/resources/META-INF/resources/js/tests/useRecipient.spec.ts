/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {useForm} from '@liferay/object-js-components-web';
import {renderHook} from '@testing-library/react-hooks';

import {validate} from '../components/EditNotificationTemplate';
import {useRecipient} from '../hooks/useRecipient';

const initialValues: NotificationTemplate = {
	attachmentObjectFieldIds: [],
	body: {
		['en_US']: '',
	},
	description: '',
	editorType: 'richText' as EditorTypeOptions,
	externalReferenceCode: '',
	name: '',
	objectDefinitionExternalReferenceCode: '',
	objectDefinitionId: 0,
	recipientType: 'email',
	recipients: [],
	subject: {
		['en_US']: '',
	},
	system: false,
	type: 'email',
};

describe('useRecipient handleTypeChange', () => {
	it('returns empty array to role recipient type', () => {
		const {result} = renderHook(() =>
			useForm({
				initialValues,
				onSubmit: () => {},
				validate,
			})
		);

		const {handleTypeChange} = useRecipient(
			result.current.setValues,
			result.current.values
		);

		handleTypeChange('to', 'roles');

		expect(
			(result.current.values.recipients[0] as EmailRecipients).to
		).toStrictEqual([]);
	});

	it('returns empty string to email recipient type', () => {
		const {result} = renderHook(() =>
			useForm({
				initialValues,
				onSubmit: () => {},
				validate,
			})
		);

		const {handleTypeChange} = useRecipient(
			result.current.setValues,
			result.current.values
		);

		handleTypeChange('to', 'email');

		expect(
			(result.current.values.recipients[0] as EmailRecipients).to
		).toStrictEqual('');
	});

	it('returns empty string to term recipient type', () => {
		const {result} = renderHook(() =>
			useForm({
				initialValues,
				onSubmit: () => {},
				validate,
			})
		);

		const {handleTypeChange} = useRecipient(
			result.current.setValues,
			result.current.values
		);

		handleTypeChange('to', 'term');

		expect(
			(result.current.values.recipients[0] as EmailRecipients).to
		).toStrictEqual('');
	});

	it('returns [%EMAIL_RECIPIENT_ADDRESS%] to subscribers recipient type', () => {
		const {result} = renderHook(() =>
			useForm({
				initialValues,
				onSubmit: () => {},
				validate,
			})
		);

		const {handleTypeChange} = useRecipient(
			result.current.setValues,
			result.current.values
		);

		handleTypeChange('to', 'subscribers');

		expect(
			(result.current.values.recipients[0] as EmailRecipients).to
		).toStrictEqual('[%EMAIL_RECIPIENT_ADDRESS%]');
	});
});
