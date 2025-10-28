/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import Translation from '../../../src/main/resources/META-INF/resources/js/components/Translation';
import {ChartStateContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/ChartStateContext';

import '@testing-library/jest-dom';

const mockViewURLs = [
	{
		default: true,
		languageId: 'en-US',
		languageLabel: 'English (United States)',
		selected: true,
		viewURL: 'http://localhost:8080/en/web/guest/-/basic-web-content',
	},
	{
		default: false,
		languageId: 'es-ES',
		languageLabel: 'Spanish (Spain)',
		selected: false,
		viewURL: 'http://localhost:8080/es/web/guest/-/contenido-web-basico',
	},
];

const noop = () => {};

describe('Translation', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('renders', () => {
		const testProps = {
			defaultLanguage: 'en-US',
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2020',
			timeRange: {endDate: '2020-01-27', startDate: '2020-02-02'},
			timeSpanKey: 'last-7-days',
		};

		const {asFragment} = render(
			<ChartStateContextProvider
				publishDate={testProps.pagePublishDate}
				timeRange={testProps.timeRange}
				timeSpanKey={testProps.timeSpanKey}
			>
				<Translation
					defaultLanguage={testProps.defaultLanguage}
					onSelectedLanguageClick={noop}
					viewURLs={mockViewURLs}
				/>
			</ChartStateContextProvider>
		);

		expect(asFragment()).toMatchSnapshot();
	});

	it('renders languages translated into', () => {
		const testProps = {
			defaultLanguage: 'en-US',
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2020',
			timeRange: {endDate: '2020-01-27', startDate: '2020-02-02'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText} = render(
			<ChartStateContextProvider
				publishDate={testProps.pagePublishDate}
				timeRange={testProps.timeRange}
				timeSpanKey={testProps.timeSpanKey}
			>
				<Translation
					defaultLanguage={testProps.defaultLanguage}
					onSelectedLanguageClick={noop}
					viewURLs={mockViewURLs}
				/>
			</ChartStateContextProvider>
		);

		expect(getByText('en-US')).toBeInTheDocument();
		expect(getByText('English (United States)')).toBeInTheDocument();
		expect(getByText('default')).toBeInTheDocument();
		expect(getByText('Spanish (Spain)')).toBeInTheDocument();
	});
});
