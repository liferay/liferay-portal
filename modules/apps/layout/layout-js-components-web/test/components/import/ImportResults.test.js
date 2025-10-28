/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render} from '@testing-library/react';
import React from 'react';

import ImportResults from '../../../src/main/resources/META-INF/resources/js/components/import/ImportResults';
import checkAccessibility from '../../__lib__/checkAccessibility';

const SUCCESS_RESULT = {
	success: [
		{
			name: 'fragment 1',
		},
	],
};

const SUCCESS_WARNING_AND_INVALID_RESULT = {
	error: [
		{
			messages: ['This is an invalid message'],
			name: 'fragment 4',
		},
	],
	success: [
		{
			name: 'fragment 1',
		},
	],
	warning: [
		{
			messages: ['This is a warning message'],
			name: 'fragment 2',
		},
		{
			messages: ['This is another warning message'],
			name: 'fragment 3',
		},
	],
};

describe('ImportResults', () => {
	it('renders success imported results expanded when there are not imported draft or invalid', async () => {
		const {container, getByRole, getByText} = render(
			<ImportResults
				fileName="example.zip"
				importResults={SUCCESS_RESULT}
			/>
		);

		expect(getByText('fragment 1')).toBeInTheDocument();
		expect(getByText('x-item-was-imported')).toBeInTheDocument();
		expect(getByRole('button').classList.contains('collapsed')).toBe(false);

		await checkAccessibility({context: container});
	});

	it('renders success imported results collapsed when there are nt imported draft or invalid', () => {
		const {getByRole, getByText} = render(
			<ImportResults
				fileName="example.zip"
				importResults={SUCCESS_WARNING_AND_INVALID_RESULT}
			/>
		);

		expect(getByText('fragment 1')).toBeInTheDocument();
		expect(getByText('x-item-was-imported')).toBeInTheDocument();
		expect(getByRole('button').classList.contains('collapsed')).toBe(true);
	});

	it('renders warning and invalid results', async () => {
		const {container, getByText} = render(
			<ImportResults
				fileName="example.zip"
				importResults={SUCCESS_WARNING_AND_INVALID_RESULT}
			/>
		);

		expect(getByText('fragment 2')).toBeInTheDocument();
		expect(getByText('fragment 3')).toBeInTheDocument();
		expect(getByText('fragment 4')).toBeInTheDocument();

		expect(
			getByText('x-items-were-imported-with-warnings')
		).toBeInTheDocument();
		expect(getByText('x-item-could-not-be-imported')).toBeInTheDocument();
		expect(getByText('This is a warning message')).toBeInTheDocument();
		expect(
			getByText('This is another warning message')
		).toBeInTheDocument();
		expect(getByText('This is an invalid message')).toBeInTheDocument();

		await checkAccessibility({context: container});
	});
});
