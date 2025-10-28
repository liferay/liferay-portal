/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import TotalCount from '../../../src/main/resources/META-INF/resources/js/components/TotalCount';
import {StoreContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/StoreContext';

import '@testing-library/jest-dom';

const mockLanguageTag = 'en-US';

describe('TotalCount', () => {
	it('renders text, help text and total count number', async () => {
		const mockDataProvider = jest.fn(() => {
			return Promise.resolve(9999);
		});

		const testProps = {
			dataProvider: mockDataProvider,
			label: 'Total Views',
			popoverHeader: 'Total Views',
			popoverMessage:
				'This number refers to the total number of views since the content was published.',
		};

		const {getByRole, getByText} = render(
			<StoreContextProvider value={{languageTag: mockLanguageTag}}>
				<TotalCount
					dataProvider={testProps.dataProvider}
					label={testProps.label}
					popoverHeader={testProps.popoverHeader}
					popoverMessage={testProps.popoverMessage}
				/>
			</StoreContextProvider>
		);

		await waitFor(() => expect(mockDataProvider).toHaveBeenCalled());

		const formatter = new Intl.NumberFormat(mockLanguageTag);
		expect(getByText(formatter.format(9999))).toBeInTheDocument();

		const label = getByText(testProps.label);
		expect(label).toBeInTheDocument();

		const helpTextIcon = getByRole('presentation');

		fireEvent.mouseEnter(helpTextIcon);

		expect(
			getByText(
				'This number refers to the total number of views since the content was published.'
			)
		).toBeInTheDocument();

		expect(mockDataProvider).toHaveBeenCalledTimes(1);
	});

	it('renders a dash instead of total count number when there is an error', async () => {
		const mockDataProvider = jest.fn(() => {
			return Promise.reject('-');
		});

		const testProps = {
			dataProvider: mockDataProvider,
			label: 'Total Views',
			popoverHeader: 'Total Views',
			popoverMessage:
				'This number refers to the total number of views since the content was published.',
		};

		const {getByText} = render(
			<TotalCount
				dataProvider={testProps.dataProvider}
				label={testProps.label}
				popoverHeader={testProps.popoverHeader}
				popoverMessage={testProps.popoverMessage}
			/>
		);

		await waitFor(() => expect(mockDataProvider).toHaveBeenCalled());

		expect(getByText('-')).toBeInTheDocument();

		expect(mockDataProvider).toHaveBeenCalledTimes(1);
	});
});
