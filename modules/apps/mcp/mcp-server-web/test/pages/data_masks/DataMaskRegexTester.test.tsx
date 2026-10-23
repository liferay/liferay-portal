/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {DataMaskRegexTester} from '../../../src/main/resources/META-INF/resources/js/pages/data_masks/DataMaskRegexTester';

describe('DataMaskRegexTester', () => {
	it('shows the error of an invalid regex instead of the output', async () => {
		fetch.mockResponseOnce(
			JSON.stringify({
				error: 'Unclosed group near index 4',
				output: 'abc',
			})
		);

		render(
			<DataMaskRegexTester
				detectionRegex="(abc"
				replacementRegex=""
				replacementValue="***"
			/>
		);

		await userEvent.type(screen.getByLabelText('sample'), 'abc');
		await userEvent.click(screen.getByRole('button', {name: 'test'}));

		expect(
			await screen.findByText('Unclosed group near index 4')
		).toBeInTheDocument();
		expect(screen.getByLabelText('output')).toHaveValue('');
	});
});
